package util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe utilitária para tratamento de exceções.
 */
public class ExceptionUtil {
    
    private static final Logger LOGGER = Logger.getLogger(ExceptionUtil.class.getName());
    
    /**
     * Registra uma exceção no log e retorna uma mensagem amigável.
     * 
     * @param e Exceção a ser registrada
     * @param contextMessage Mensagem de contexto
     * @return Mensagem amigável para o usuário
     */
    public static String handleException(Exception e, String contextMessage) {
        LOGGER.log(Level.SEVERE, contextMessage, e);
        
        // Retorna uma mensagem amigável, sem detalhes técnicos
        return "Ocorreu um erro ao processar sua solicitação. Por favor, tente novamente mais tarde.";
    }
    
    /**
     * Registra uma exceção no log com informações detalhadas e retorna uma mensagem amigável.
     * 
     * @param e Exceção a ser registrada
     * @param contextMessage Mensagem de contexto
     * @param detailedInfo Informações detalhadas sobre o contexto
     * @return Mensagem amigável para o usuário
     */
    public static String handleExceptionWithDetails(Exception e, String contextMessage, String detailedInfo) {
        LOGGER.log(Level.SEVERE, contextMessage + " - Detalhes: " + detailedInfo, e);
        
        // Retorna uma mensagem amigável, sem detalhes técnicos
        return "Ocorreu um erro ao processar sua solicitação. Por favor, tente novamente mais tarde.";
    }
    
    /**
     * Registra uma exceção de validação no log e retorna uma mensagem amigável.
     * 
     * @param e Exceção a ser registrada
     * @param fieldName Nome do campo com erro
     * @return Mensagem amigável para o usuário
     */
    public static String handleValidationException(Exception e, String fieldName) {
        LOGGER.log(Level.WARNING, "Erro de validação no campo: " + fieldName, e);
        
        // Retorna uma mensagem amigável sobre o erro de validação
        return "O campo " + fieldName + " está com um valor inválido. Por favor, verifique e tente novamente.";
    }
    
    /**
     * Registra um erro de autorização no log.
     * 
     * @param userName Nome do usuário que tentou acessar o recurso
     * @param resourceName Nome do recurso que foi tentado acessar
     */
    public static void logAuthorizationError(String userName, String resourceName) {
        LOGGER.log(Level.WARNING, "Tentativa de acesso não autorizado: Usuário [" + 
                userName + "] tentou acessar [" + resourceName + "]");
    }
}
