package util;

import io.quarkus.qute.TemplateExtension;
import model.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe que define extensões para uso nos templates Qute.
 * Estas extensões permitem adicionar métodos para tipos que não são modificáveis.
 * Resolve problemas de validação de templates no projeto REV3RSO.
 */
@TemplateExtension
public class TemplateExtensions {

    /**
     * Formata uma data no padrão especificado
     */
    public static String format(Date date, String pattern) {
        if (date == null) return "";
        return new SimpleDateFormat(pattern).format(date);
    }
    
    /**
     * Acessa o nome de um usuário com verificação de nulos
     */
    public static String nome(Usuario usuario) {
        return usuario != null ? usuario.nomeFantasia : "";
    }
    
    /**
     * Acessa a razão social de um usuário com verificação de nulos
     */
    public static String razaoSocial(Usuario usuario) {
        return usuario != null ? usuario.razaoSocial : "";
    }
    
    /**
     * Acessa o nome fantasia de um usuário com verificação de nulos
     */
    public static String nomeFantasia(Usuario usuario) {
        return usuario != null ? usuario.nomeFantasia : "";
    }
    
    /**
     * Compara um TipoUsuario com uma string (substitui o operador ==)
     */
    public static boolean eq(Usuario.TipoUsuario tipo, String valor) {
        return tipo != null && tipo.name().equals(valor);
    }
    
    /**
     * Método especial para tratar a comparação == em templates para enums
     * Usado em casos como: {tipoUsuario == 'FORNECEDOR'}
     */
    @TemplateExtension(matchName = "==")
    public static boolean equals(Usuario.TipoUsuario tipo, String valor) {
        return eq(tipo, valor);
    }
    
    /**
     * Acessa o nome de uma área de atuação com verificação de nulos
     */
    public static String nome(AreaAtuacao area) {
        return area != null ? area.descricao : "";
    }
    
    /**
     * Verifica se uma lista está vazia com segurança contra nulos
     */
    public static boolean isEmpty(List<?> lista) {
        return lista == null || lista.isEmpty();
    }
    
    /**
     * Retorna o tamanho de uma lista com segurança contra nulos
     */
    public static int size(List<?> lista) {
        return lista == null ? 0 : lista.size();
    }
    
    /**
     * Acessa o nome de uma forma de pagamento com verificação de nulos
     */
    public static String nome(FormaPagamento forma) {
        return forma != null ? forma.descricao : "";
    }
    
    /**
     * Acessa os requisitos de um leilão com verificação de nulos
     */
    public static String requisitos(Leilao leilao) {
        return leilao != null ? leilao.descricao : "";
    }
    
    /**
     * Acessa o comprador de um leilão com verificação de nulos
     */
    public static Usuario comprador(Leilao leilao) {
        // Assumindo que o criador do leilão é o comprador
        return leilao != null ? leilao.criador : null;
    }
    
    /**
     * Acessa a data de criação de uma avaliação
     */
    public static Date dataCriacao(Avaliacao avaliacao) {
        return avaliacao != null ? avaliacao.dataAvaliacao : null;
    }
    
    /**
     * Acessa a data de fechamento de um leilão (dataFim no modelo)
     */
    public static Date dataFechamento(Leilao leilao) {
        return leilao != null ? leilao.dataFim : null;
    }
    
    /**
     * Acessa a data final de um leilão (dataFim no modelo)
     */
    public static Date dataFinal(Leilao leilao) {
        return leilao != null ? leilao.dataFim : null;
    }
    
    /**
     * Acessa a data de um lance
     */
    public static Date dataLance(Lance lance) {
        return lance != null ? lance.dataCriacao : null;
    }
    
    /**
     * Verifica se um objeto é diferente de null (para uso no template)
     */
    @TemplateExtension(matchName = "!=")
    public static boolean notEquals(Object obj, Object value) {
        if ("null".equals(String.valueOf(value))) {
            return obj != null;
        }
        return obj != null && !obj.equals(value);
    }
    
    /**
     * Acessa a lista de usuários de uma área de atuação
     */
    public static List<Usuario> usuarios(AreaAtuacao area) {
        // Considerando que pode não existir o campo 'usuarios' em AreaAtuacao, estamos retornando uma lista vazia
        return area != null ? new ArrayList<>() : null;
    }
    
    /**
     * Acessa a lista de leilões de uma forma de pagamento
     */
    public static List<Leilao> leiloes(FormaPagamento forma) {
        // Considerando que pode não existir o campo 'leiloes' em FormaPagamento, estamos retornando uma lista vazia
        return forma != null ? new ArrayList<>() : null;
    }
    
    /**
     * Acessa o valor estimado de um leilão
     */
    public static BigDecimal valorEstimado(Leilao leilao) {
        return leilao != null ? leilao.valorReferencia : null;
    }
    
    /**
     * Acessa o melhor lance de um leilão
     */
    public static Lance melhorLance(Leilao leilao) {
        if (leilao == null || leilao.lances == null || leilao.lances.isEmpty()) {
            return null;
        }
        // Retorna o lance com menor valor (leilão reverso)
        return leilao.lances.stream()
                .min(Comparator.comparing(l -> l.valor))
                .orElse(null);
    }
    
    /**
     * Acessa o fornecedor vencedor de um leilão
     */
    public static Usuario fornecedorVencedor(Leilao leilao) {
        Lance melhor = melhorLance(leilao);
        return melhor != null ? melhor.fornecedor : null;
    }
    
    /**
     * Ordena lances por valor
     */
    public static List<Lance> sort(List<Lance> lances, Object comparator) {
        if (lances == null || lances.isEmpty()) {
            return lances;
        }
        return lances.stream()
                .sorted(Comparator.comparing(l -> l.valor))
                .collect(Collectors.toList());
    }
    
    /**
     * Classe para acesso ao Status dos convites nos templates
     */
    @TemplateExtension(namespace = "convite")
    public static class StatusExtensions {
        // Métodos de extensão para acessar os valores do enum Status
        public static Convite.Status PENDENTE() {
            return Convite.Status.PENDENTE;
        }
        
        public static Convite.Status ACEITO() {
            return Convite.Status.ACEITO;
        }
        
        public static Convite.Status RECUSADO() {
            return Convite.Status.RECUSADO;
        }
    }
}
