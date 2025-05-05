package util;

import io.quarkus.qute.TemplateExtension;
import model.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Classe que define extensões para uso nos templates Qute.
 * Estas extensões permitem adicionar métodos para tipos que não são modificáveis.
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
}
