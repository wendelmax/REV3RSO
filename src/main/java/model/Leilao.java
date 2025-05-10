package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "leiloes")
@Getter
@Setter
public class Leilao extends PanacheEntity {

    public Date dataCancelamento;
    @ManyToOne
    @JoinColumn(name = "lance_vencedor_id")
    public Lance lanceVencedor;

    public enum TipoLeilao {
        ABERTO,  // Qualquer fornecedor cadastrado pode participar
        FECHADO  // Apenas fornecedores convidados podem participar
    }
    
    public enum Status {
        RASCUNHO,    // Criado mas não publicado
        AGENDADO,    // Agendado para iniciar no futuro
        ABERTO,      // Em andamento, aceitando lances
        ENCERRADO,   // Período de lances encerrado
        CANCELADO,   // Cancelado pelo criador
        CONCLUIDO    // Leilão finalizado com sucesso
    }
    
    @NotBlank(message = "O título é obrigatório")
    @Column(nullable = false)
    public String titulo;
    
    @NotBlank(message = "A descrição é obrigatória")
    @Column(columnDefinition = "TEXT", nullable = false)
    public String descricao;
    
    @Column(columnDefinition = "TEXT")
    public String especificacoesTecnicas;
    
    @Column(name = "valor_inicial")
    public BigDecimal valorInicial;
    
    @Column(name = "data_inicio", nullable = false)
    @NotNull(message = "A data de início é obrigatória")
    public Date dataInicio;
    
    @Column(name = "data_fim", nullable = false)
    @NotNull(message = "A data de término é obrigatória")
    public Date dataFim;
    
    @Column(name = "valor_referencia")
    public BigDecimal valorReferencia;
    
    @Column(name = "valor_vencedor")
    public BigDecimal valorVencedor;
    
    @Column(name = "melhor_oferta")
    public BigDecimal melhorOferta;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "O tipo de leilão é obrigatório")
    public TipoLeilao tipoLeilao;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public Status status;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "criador_id", nullable = false)
    @NotNull(message = "O criador é obrigatório")
    public Usuario criador;
    
    @Column(name = "tipo_requisicao", nullable = false)
    @NotBlank(message = "O tipo de requisição é obrigatório")
    public String tipoRequisicao;
    
    @ManyToOne
    @JoinColumn(name = "forma_pagamento_id")
    @NotNull(message = "A forma de pagamento é obrigatória")
    public FormaPagamento formaPagamento;
    
    public Integer quantidade;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    public Categoria categoria;

    @Column(name = "unidade_medida")
    public String unidadeMedida;
    
    @Column(name = "motivo_cancelamento")
    public String motivoCancelamento;
    
    @Column(name = "data_criacao", nullable = false)
    public Date dataCriacao;
    
    @Column(name = "data_atualizacao")
    public Date dataAtualizacao;
    
    @OneToMany(mappedBy = "leilao")
    public List<Lance> lances;
    
    @OneToMany(mappedBy = "leilao")
    public List<Convite> convites;
    
    @OneToMany(mappedBy = "leilao")
    public List<Mensagem> mensagens;
    
    @OneToMany(mappedBy = "leilao")
    public List<Anexo> anexos;
    
    // Construtor
    public Leilao() {
        this.dataCriacao = new Date();
        this.status = Status.RASCUNHO;
        this.lances = new ArrayList<>();
        this.convites = new ArrayList<>();
        this.mensagens = new ArrayList<>();
        this.anexos = new ArrayList<>();
    }
    
    // Métodos estáticos para consultas frequentes
    public static List<Leilao> listarLeiloesAbertos() {
        return list("status = ?1", Status.ABERTO);
    }
    
    public static List<Leilao> listarLeiloesPorCriador(Usuario criador) {
        return list("criador = ?1 ORDER BY dataCriacao DESC", criador);
    }
    
    public static List<Leilao> listarLeiloesPorFornecedor(Usuario fornecedor) {
        // Busca leilões abertos onde o fornecedor foi convidado ou leilões abertos públicos
        return find("(status = ?1 AND tipoLeilao = ?2) OR " +
                   "(status = ?1 AND tipoLeilao = ?3 AND id IN " +
                   "(SELECT c.leilao.id FROM Convite c WHERE c.fornecedor = ?4))",
                   Status.ABERTO, TipoLeilao.ABERTO, TipoLeilao.FECHADO, fornecedor)
                   .list();
    }
    
    // Métodos de instância para ações no leilão
    public void publicar() {
        if (this.status == Status.RASCUNHO) {
            this.status = new Date().after(this.dataInicio) ? Status.ABERTO : Status.AGENDADO;
            this.dataAtualizacao = new Date();
            this.persist();
        }
    }
    
    public void cancelar(String motivo) {
        this.status = Status.CANCELADO;
        this.motivoCancelamento = motivo;
        this.dataAtualizacao = new Date();
        this.persist();
    }
    
    public void concluir() {
        if (this.status == Status.ENCERRADO) {
            // Definir o lance vencedor
            Lance menorLance = getMenorLance();
            if (menorLance != null) {
                menorLance.definirComoVencedor();
                this.lanceVencedor = menorLance;
                this.valorVencedor = menorLance.valor;
            }
            
            this.status = Status.CONCLUIDO;
            this.dataAtualizacao = new Date();
            this.persist();
        }
    }
    
    public Lance getMenorLance() {
        if (lances == null || lances.isEmpty()) {
            return null;
        }
        
        Lance menorLance = null;
        for (Lance lance : lances) {
            if (menorLance == null || lance.valor.compareTo(menorLance.valor) < 0) {
                menorLance = lance;
            }
        }
        return menorLance;
    }
    
    public boolean isConvidado(Usuario fornecedor) {
        if (tipoLeilao == TipoLeilao.ABERTO) {
            return true;
        }
        
        for (Convite convite : convites) {
            if (convite.fornecedor.equals(fornecedor)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Leilao leilao = (Leilao) o;
        return Objects.equals(id, leilao.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    /**
     * Busca estatísticas de leilões por mês nos últimos N meses
     * @param meses quantidade de meses a buscar retroativamente
     * @return lista de mapas com dados estatísticos por mês
     */
    public static List<Map<String, Object>> buscarLeiloesPorMes(int meses) {
        List<Map<String, Object>> resultado = new ArrayList<>();
        
        // Obtém a data atual
        Calendar cal = Calendar.getInstance();
        // Ajusta para o primeiro dia do mês
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        // Para cada um dos N meses anteriores
        for (int i = 0; i < meses; i++) {
            // Calcula o início e fim do mês
            Date inicioMes = cal.getTime();
            
            // Move para o próximo mês para obter a data final
            cal.add(Calendar.MONTH, 1);
            Date fimMes = cal.getTime();
            
            // Volta um mês para a próxima iteração
            cal.add(Calendar.MONTH, -2);
            
            // Conta os leilões criados no mês
            long total = Leilao.count("dataCriacao >= ?1 and dataCriacao < ?2", inicioMes, fimMes);
            
            // Conta por status
            long abertos = Leilao.count("dataCriacao >= ?1 and dataCriacao < ?2 and status = ?3", 
                                      inicioMes, fimMes, Status.ABERTO);
            long concluidos = Leilao.count("dataCriacao >= ?1 and dataCriacao < ?2 and status = ?3", 
                                          inicioMes, fimMes, Status.CONCLUIDO);
            long cancelados = Leilao.count("dataCriacao >= ?1 and dataCriacao < ?2 and status = ?3", 
                                         inicioMes, fimMes, Status.CANCELADO);
            
            // Formata o mês e ano para exibição
            SimpleDateFormat sdf = new SimpleDateFormat("MMM/yyyy");
            String mesAno = sdf.format(inicioMes);
            
            // Cria o mapa de resultados para este mês
            Map<String, Object> mesResultado = new HashMap<>();
            mesResultado.put("mesAno", mesAno);
            mesResultado.put("total", total);
            mesResultado.put("abertos", abertos);
            mesResultado.put("concluidos", concluidos);
            mesResultado.put("cancelados", cancelados);
            
            resultado.add(mesResultado);
        }
        
        // Inverte a lista para ficar em ordem cronológica (mais antigo primeiro)
        Collections.reverse(resultado);
        
        return resultado;
    }
    
    /**
     * Busca estatísticas de lances por mês nos últimos N meses
     * @param meses quantidade de meses a buscar retroativamente
     * @return lista de mapas com dados estatísticos por mês
     */
    public static List<Map<String, Object>> buscarLancesPorMes(int meses) {
        List<Map<String, Object>> resultado = new ArrayList<>();
        
        // Obtém a data atual
        Calendar cal = Calendar.getInstance();
        // Ajusta para o primeiro dia do mês
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        // Para cada um dos N meses anteriores
        for (int i = 0; i < meses; i++) {
            // Calcula o início e fim do mês
            Date inicioMes = cal.getTime();
            
            // Move para o próximo mês para obter a data final
            cal.add(Calendar.MONTH, 1);
            Date fimMes = cal.getTime();
            
            // Volta um mês para a próxima iteração
            cal.add(Calendar.MONTH, -2);
            
            // Conta os lances registrados no mês através de query nativa
            long totalLances = Lance.count("dataCriacao >= ?1 and dataCriacao < ?2", inicioMes, fimMes);
            
            // Calcula o valor médio dos lances no mês (poderia ser feito com query mais complexa)
            List<Lance> lances = Lance.list("dataCriacao >= ?1 and dataCriacao < ?2 order by valor", inicioMes, fimMes);
            
            BigDecimal valorMedio = BigDecimal.ZERO;
            BigDecimal valorMenor = null;
            BigDecimal valorMaior = null;
            
            if (!lances.isEmpty()) {
                BigDecimal soma = BigDecimal.ZERO;
                for (Lance lance : lances) {
                    soma = soma.add(lance.valor);
                    
                    if (valorMenor == null || lance.valor.compareTo(valorMenor) < 0) {
                        valorMenor = lance.valor;
                    }
                    
                    if (valorMaior == null || lance.valor.compareTo(valorMaior) > 0) {
                        valorMaior = lance.valor;
                    }
                }
                
                valorMedio = soma.divide(BigDecimal.valueOf(lances.size()), 2, RoundingMode.HALF_UP);
            }
            
            // Formata o mês e ano para exibição
            SimpleDateFormat sdf = new SimpleDateFormat("MMM/yyyy");
            String mesAno = sdf.format(inicioMes);
            
            // Cria o mapa de resultados para este mês
            Map<String, Object> mesResultado = new HashMap<>();
            mesResultado.put("mesAno", mesAno);
            mesResultado.put("totalLances", totalLances);
            mesResultado.put("valorMedio", valorMedio);
            mesResultado.put("valorMenor", valorMenor);
            mesResultado.put("valorMaior", valorMaior);
            
            resultado.add(mesResultado);
        }
        
        // Inverte a lista para ficar em ordem cronológica (mais antigo primeiro)
        Collections.reverse(resultado);
        
        return resultado;
    }
    
    /**
     * Retorna a lista de todos os participantes do leilão,
     * incluindo o criador e todos os fornecedores que deram lances ou foram convidados.
     * 
     * @return Lista de usuários participantes
     */
    public List<Usuario> getParticipantes() {
        // Usar um Map para evitar duplicatas de usuários
        Map<Long, Usuario> participantesMap = new HashMap<>();
        
        // Adiciona o criador
        participantesMap.put(criador.id, criador);
        
        // Adiciona fornecedores que deram lances
        if (lances != null) {
            for (Lance lance : lances) {
                if (lance.fornecedor != null) {
                    participantesMap.put(lance.fornecedor.id, lance.fornecedor);
                }
            }
        }
        
        // Adiciona fornecedores convidados
        if (convites != null) {
            for (Convite convite : convites) {
                if (convite.fornecedor != null) {
                    participantesMap.put(convite.fornecedor.id, convite.fornecedor);
                }
            }
        }
        
        // Converte para lista
        return new ArrayList<>(participantesMap.values());
    }
    
    /**
     * Verifica se um usuário participou do leilão, seja como fornecedor que deu lance
     * ou como fornecedor convidado em leilões fechados.
     * 
     * @param usuario Usuário a verificar
     * @return true se o usuário participou, false caso contrário
     */
    public boolean temParticipacao(Usuario usuario) {
        // Verifica se o usuário deu algum lance
        if (lances != null) {
            for (Lance lance : lances) {
                if (lance.fornecedor != null && lance.fornecedor.equals(usuario)) {
                    return true;
                }
            }
        }
        
        // Em leilões fechados, verifica se foi convidado
        if (tipoLeilao == TipoLeilao.FECHADO) {
            return isConvidado(usuario);
        }
        
        // Em leilões abertos, qualquer fornecedor pode participar
        return tipoLeilao == TipoLeilao.ABERTO && 
               usuario.tipoUsuario == Usuario.TipoUsuario.FORNECEDOR;
    }
}
