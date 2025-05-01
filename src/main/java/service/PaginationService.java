package service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Serviço para gerenciar configurações de paginação.
 * Centraliza as configurações e lógica relacionada à paginação.
 */
@ApplicationScoped
public class PaginationService {
    
    @Inject
    @ConfigProperty(name = "pagination.default.size", defaultValue = "10")
    int defaultPageSize;
    
    @Inject
    @ConfigProperty(name = "pagination.max.size", defaultValue = "100")
    int maxPageSize;
    
    @Inject
    @ConfigProperty(name = "pagination.header.enabled", defaultValue = "true")
    boolean headerPaginationEnabled;
    
    @Inject
    @ConfigProperty(name = "pagination.header.page", defaultValue = "X-Page")
    String pageHeaderName;
    
    @Inject
    @ConfigProperty(name = "pagination.header.size", defaultValue = "X-Size")
    String sizeHeaderName;
    
    /**
     * Retorna o tamanho padrão de página.
     * 
     * @return Tamanho padrão de página
     */
    public int getDefaultPageSize() {
        return defaultPageSize;
    }
    
    /**
     * Retorna o tamanho máximo permitido para uma página.
     * 
     * @return Tamanho máximo de página
     */
    public int getMaxPageSize() {
        return maxPageSize;
    }
    
    /**
     * Verifica se a paginação via header está habilitada.
     * 
     * @return true se a paginação via header estiver habilitada
     */
    public boolean isHeaderPaginationEnabled() {
        return headerPaginationEnabled;
    }
    
    /**
     * Retorna o nome do header para o número da página.
     * 
     * @return Nome do header para o número da página
     */
    public String getPageHeaderName() {
        return pageHeaderName;
    }
    
    /**
     * Retorna o nome do header para o tamanho da página.
     * 
     * @return Nome do header para o tamanho da página
     */
    public String getSizeHeaderName() {
        return sizeHeaderName;
    }
    
    /**
     * Calcula o tamanho efetivo da página com base no tamanho solicitado.
     * Garante que o tamanho esteja dentro dos limites permitidos.
     * 
     * @param requestedSize Tamanho solicitado
     * @return Tamanho efetivo da página
     */
    public int getEffectivePageSize(int requestedSize) {
        if (requestedSize <= 0) {
            return defaultPageSize;
        }
        
        return Math.min(requestedSize, maxPageSize);
    }
}
