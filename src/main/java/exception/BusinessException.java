package exception;

/**
 * Exceção de negócio que representa erros nas regras de negócio da aplicação.
 * Utilizada para diferenciar erros de negócio de erros técnicos.
 */
public class BusinessException extends RuntimeException {
    
    private final String errorCode;
    
    /**
     * Construtor com mensagem.
     * 
     * @param message Mensagem da exceção
     */
    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
    }
    
    /**
     * Construtor com mensagem e código de erro.
     * 
     * @param message Mensagem da exceção
     * @param errorCode Código do erro
     */
    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * Construtor com mensagem e causa.
     * 
     * @param message Mensagem da exceção
     * @param cause Causa da exceção
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BUSINESS_ERROR";
    }
    
    /**
     * Construtor com mensagem, código de erro e causa.
     * 
     * @param message Mensagem da exceção
     * @param errorCode Código do erro
     * @param cause Causa da exceção
     */
    public BusinessException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    /**
     * Obtém o código de erro.
     * 
     * @return Código de erro
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Cria uma exceção de negócio para entidade não encontrada.
     * 
     * @param entityName Nome da entidade
     * @param id Identificador da entidade
     * @return Exceção de negócio
     */
    public static BusinessException entityNotFound(String entityName, Object id) {
        return new BusinessException(
                entityName + " não encontrado com o ID: " + id,
                "ENTITY_NOT_FOUND");
    }
    
    /**
     * Cria uma exceção de negócio para acesso negado.
     * 
     * @param resourceName Nome do recurso
     * @return Exceção de negócio
     */
    public static BusinessException accessDenied(String resourceName) {
        return new BusinessException(
                "Acesso negado ao recurso: " + resourceName,
                "ACCESS_DENIED");
    }
    
    /**
     * Cria uma exceção de negócio para operação não permitida.
     * 
     * @param operation Nome da operação
     * @param reason Motivo da não permissão
     * @return Exceção de negócio
     */
    public static BusinessException operationNotAllowed(String operation, String reason) {
        return new BusinessException(
                "Operação não permitida: " + operation + ". Motivo: " + reason,
                "OPERATION_NOT_ALLOWED");
    }
    
    /**
     * Cria uma exceção de negócio para validação.
     * 
     * @param field Campo com erro
     * @param message Mensagem de erro
     * @return Exceção de negócio
     */
    public static BusinessException validationError(String field, String message) {
        return new BusinessException(
                "Erro de validação no campo '" + field + "': " + message,
                "VALIDATION_ERROR");
    }
}
