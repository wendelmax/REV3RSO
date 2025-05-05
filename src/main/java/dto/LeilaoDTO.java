package dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import model.Leilao;
import model.Lance;

/**
 * DTO (Data Transfer Object) para a entidade Leilão.
 * Utilizado para transferência de dados entre camadas, sem expor a entidade diretamente.
 * Implementado como record para garantir imutabilidade e reduzir código boilerplate.
 */
public record LeilaoDTO(
    Long id,
    String titulo,
    String descricao,
    String especificacoesTecnicas,
    BigDecimal valorInicial,
    Date dataInicio,
    Date dataFim,
    String status,
    String tipoLeilao,
    Long criadorId,
    String nomeCriador,
    int quantidadeLances,
    BigDecimal menorLance,
    Long categoriaId,
    String nomeCategoria,
    List<String> anexos
) {
    
    /**
     * Construtor que converte uma entidade Leilao em DTO.
     * 
     * @param leilao A entidade a ser convertida.
     */
    public static LeilaoDTO fromEntity(Leilao leilao) {
        // Obter o número de lances e o menor lance
        int quantidadeLances = 0;
        BigDecimal menorLanceValor = null;
        
        if (leilao.lances != null) {
            quantidadeLances = leilao.lances.size();
            
            // Obter o menor lance
            Lance menorLance = leilao.getMenorLance();
            if (menorLance != null) {
                menorLanceValor = menorLance.valor;
            }
        }
        
        // Obter lista de anexos
        List<String> anexos = null;
        if (leilao.anexos != null) {
            anexos = leilao.anexos.stream()
                .map(anexo -> anexo.nome)
                .collect(Collectors.toList());
        }
        
        return new LeilaoDTO(
            leilao.id,
            leilao.titulo,
            leilao.descricao,
            leilao.especificacoesTecnicas,
            leilao.valorInicial,
            leilao.dataInicio,
            leilao.dataFim,
            leilao.status != null ? leilao.status.name() : null,
            leilao.tipoLeilao != null ? leilao.tipoLeilao.name() : null,
            leilao.criador != null ? leilao.criador.id : null,
            leilao.criador != null ? leilao.criador.nomeFantasia : null,
            quantidadeLances,
            menorLanceValor,
            leilao.categoria != null ? leilao.categoria.id : null,
            leilao.categoria != null ? leilao.categoria.nome : null,
            anexos
        );
    }
    
    /**
     * Converte este DTO em uma entidade Leilao.
     * Observação: Este método deve ser usado apenas para novos leilões,
     * pois não preenche todos os campos da entidade.
     * 
     * @return Uma nova instância de Leilao com os dados deste DTO.
     */
    public Leilao paraEntidade() {
        Leilao leilao = new Leilao();
        leilao.titulo = this.titulo;
        leilao.descricao = this.descricao;
        leilao.especificacoesTecnicas = this.especificacoesTecnicas;
        leilao.valorInicial = this.valorInicial;
        leilao.dataInicio = this.dataInicio;
        leilao.dataFim = this.dataFim;
        
        // Outros campos como criador, tipo, status, devem ser preenchidos pelo serviço
        return leilao;
    }
    
    /**
     * Cria uma lista de DTOs a partir de uma lista de entidades.
     * 
     * @param leiloes Lista de entidades Leilao
     * @return Lista de LeilaoDTO
     */
    public static List<LeilaoDTO> converterLista(List<Leilao> leiloes) {
        return leiloes.stream()
            .map(LeilaoDTO::fromEntity)
            .collect(Collectors.toList());
    }
}
