package exception.handler;

import exception.BusinessException;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.runtime.LaunchMode;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manipulador global de exceções que captura e trata exceções não tratadas.
 * Retorna páginas de erro amigáveis para o usuário.
 */
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Throwable> {
    
    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class.getName());
    
    @Inject
    Template errorTemplate;
    
    @Inject
    Template notFoundTemplate;
    
    @Inject
    Template forbiddenTemplate;
    
    /**
     * Trata todas as exceções não capturadas e retorna uma resposta HTTP apropriada.
     * 
     * @param exception Exceção lançada
     * @return Resposta HTTP com página de erro
     */
    @Override
    public Response toResponse(Throwable exception) {
        // Verifica se estamos em modo de desenvolvimento
        boolean isDevMode = LaunchMode.current() == LaunchMode.DEVELOPMENT;
        
        if (isDevMode) {
            // Em modo de desenvolvimento, loga a exceção completa com stack trace
            LOGGER.log(Level.SEVERE, "Erro não tratado na classe " + exception.getClass().getName() + ": " + exception.getMessage(), exception);
        } else {
            // Em produção, loga apenas a mensagem básica
            LOGGER.log(Level.SEVERE, "Erro não tratado: " + exception.getMessage());
        }
        
        if (exception instanceof BusinessException) {
            return handleBusinessException((BusinessException) exception);
        }
        
        // Para outras exceções, retorna um erro 500 genérico
        TemplateInstance content = errorTemplate.data("error", exception.getMessage())
                                              .data("status", 500)
                                              .data("title", "Erro Interno do Servidor");
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                     .entity(content)
                     .build();
    }
    
    /**
     * Trata exceções de negócio e retorna uma resposta HTTP apropriada.
     * 
     * @param exception Exceção de negócio
     * @return Resposta HTTP com página de erro
     */
    private Response handleBusinessException(BusinessException exception) {
        String errorCode = exception.getErrorCode();
        
        switch (errorCode) {
            case "ENTITY_NOT_FOUND":
                TemplateInstance notFoundContent = notFoundTemplate.data("error", exception.getMessage())
                                                               .data("status", 404)
                                                               .data("title", "Página Não Encontrada");
                
                return Response.status(Response.Status.NOT_FOUND)
                             .entity(notFoundContent)
                             .build();
                
            case "ACCESS_DENIED":
                TemplateInstance forbiddenContent = forbiddenTemplate.data("error", exception.getMessage())
                                                                 .data("status", 403)
                                                                 .data("title", "Acesso Negado");
                
                return Response.status(Response.Status.FORBIDDEN)
                             .entity(forbiddenContent)
                             .build();
                
            default:
                TemplateInstance errorContent = errorTemplate.data("error", exception.getMessage())
                                                        .data("status", 500)
                                                        .data("title", "Erro do Servidor");
                
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                             .entity(errorContent)
                             .build();
        }
    }
}
