package interceptor;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;

import annotation.Pageable;
import service.PaginationService;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Interceptor para adicionar suporte a paginação automática nos controllers.
 * Examina os headers da requisição e injeta parâmetros de paginação quando solicitado.
 */
@Interceptor
@Pageable
@Priority(Interceptor.Priority.APPLICATION)
public class PaginationInterceptor {
    
    private static final Logger LOGGER = Logger.getLogger(PaginationInterceptor.class.getName());
    
    @Inject
    PaginationService paginationService;
    
    @Context
    HttpHeaders headers;
    
    @Context
    UriInfo uriInfo;
    
    /**
     * Método executado ao redor dos métodos anotados com @Pageable.
     * Analisa os headers de paginação e modifica os parâmetros do método original.
     */
    @AroundInvoke
    public Object addPagination(InvocationContext context) throws Exception {
        // Verifica se a paginação via header está habilitada
        if (!paginationService.isHeaderPaginationEnabled()) {
            return context.proceed();
        }
        
        // Verifica se a requisição tem headers de paginação
        Map<String, String> paginationParams = extractPaginationHeaders();
        if (paginationParams.isEmpty()) {
            return context.proceed();
        }
        
        LOGGER.fine("Detectados parâmetros de paginação nos headers: " + paginationParams);
        
        // Substitui parâmetros de paginação nos argumentos do método
        injectPaginationParams(context, paginationParams);
        
        // Chama o método original com os parâmetros já modificados no contexto
        return context.proceed();
    }
    
    /**
     * Extrai parâmetros de paginação dos headers HTTP.
     * 
     * @return Mapa com os parâmetros de paginação
     */
    private Map<String, String> extractPaginationHeaders() {
        Map<String, String> paginationParams = new HashMap<>();
        
        String pageHeader = paginationService.getPageHeaderName();
        String sizeHeader = paginationService.getSizeHeaderName();
        
        if (headers.getRequestHeaders().containsKey(pageHeader)) {
            paginationParams.put("page", headers.getRequestHeader(pageHeader).get(0));
        }
        
        if (headers.getRequestHeaders().containsKey(sizeHeader)) {
            paginationParams.put("size", headers.getRequestHeader(sizeHeader).get(0));
        }
        
        return paginationParams;
    }
    
    /**
     * Injeta parâmetros de paginação nos argumentos do método.
     * 
     * @param context Contexto da invocação
     * @param paginationParams Parâmetros de paginação extraídos
     * @return Array de parâmetros modificados
     */
    private Object[] injectPaginationParams(InvocationContext context, Map<String, String> paginationParams) {
        Method method = context.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] originalParams = context.getParameters();
        Object[] modifiedParams = originalParams.clone();
        
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            String paramName = param.getName();
            
            // Verifica se o parâmetro é um parâmetro de paginação
            if (paramName.equals("page") && paginationParams.containsKey("page")) {
                int page = Integer.parseInt(paginationParams.get("page"));
                modifiedParams[i] = page;
            }
            else if (paramName.equals("size") && paginationParams.containsKey("size")) {
                int size = Integer.parseInt(paginationParams.get("size"));
                // Garantir que o tamanho não exceda o máximo permitido
                size = Math.min(size, paginationService.getMaxPageSize());
                modifiedParams[i] = size;
            }
        }
        
        context.setParameters(modifiedParams);
        return modifiedParams;
    }
}
