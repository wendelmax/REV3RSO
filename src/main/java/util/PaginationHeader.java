package util;

import jakarta.ws.rs.core.MultivaluedMap;

/**
 * Classe para representar os headers de paginação nas requisições e respostas.
 * Encapsula a lógica para lidar com os headers de paginação.
 */
public class PaginationHeader {
    private boolean paginate;
    private int page;
    private int size;
    private int total;
    private int pages;
    private boolean hasNext;
    private boolean hasPrev;
    
    /**
     * Construtor padrão
     */
    public PaginationHeader() {
        this.paginate = false;
        this.page = 0;
        this.size = 25;
        this.total = 0;
        this.pages = 0;
        this.hasNext = false;
        this.hasPrev = false;
    }
    
    /**
     * Construtor com parâmetros básicos
     * 
     * @param paginate Indica se a paginação está ativa
     * @param page Número da página atual
     * @param size Tamanho da página
     */
    public PaginationHeader(boolean paginate, int page, int size) {
        this.paginate = paginate;
        this.page = page;
        this.size = size;
        this.total = 0;
        this.pages = 0;
        this.hasNext = false;
        this.hasPrev = false;
    }
    
    /**
     * Construtor completo
     * 
     * @param paginate Indica se a paginação está ativa
     * @param page Número da página atual
     * @param size Tamanho da página
     * @param total Número total de itens
     * @param pages Número total de páginas
     */
    public PaginationHeader(boolean paginate, int page, int size, int total, int pages) {
        this.paginate = paginate;
        this.page = page;
        this.size = size;
        this.total = total;
        this.pages = pages;
        this.hasNext = page < pages - 1;
        this.hasPrev = page > 0;
    }
    
    /**
     * Verifica se a paginação está ativa a partir dos headers da requisição
     * 
     * @param headers Headers da requisição
     * @param paginationHeaderName Nome do header que ativa a paginação
     * @return true se a paginação estiver ativa
     */
    public static boolean isPaginationEnabled(MultivaluedMap<String, String> headers, String paginationHeaderName) {
        if (headers == null || paginationHeaderName == null) {
            return false;
        }
        
        String paginationValue = headers.getFirst(paginationHeaderName);
        return "true".equalsIgnoreCase(paginationValue);
    }
    
    /**
     * Extrai os parâmetros de paginação dos headers da requisição
     * 
     * @param headers Headers da requisição
     * @param paginationHeaderName Nome do header que ativa a paginação
     * @param pageHeaderName Nome do header para o número da página
     * @param sizeHeaderName Nome do header para o tamanho da página
     * @param defaultSize Tamanho padrão da página
     * @param maxSize Tamanho máximo da página
     * @return Objeto PaginationHeader com os parâmetros extraídos
     */
    public static PaginationHeader fromHeaders(
            MultivaluedMap<String, String> headers,
            String paginationHeaderName,
            String pageHeaderName,
            String sizeHeaderName,
            int defaultSize,
            int maxSize) {
        
        if (headers == null) {
            return new PaginationHeader();
        }
        
        boolean paginate = isPaginationEnabled(headers, paginationHeaderName);
        int page = 0;
        int size = defaultSize;
        
        try {
            String pageValue = headers.getFirst(pageHeaderName);
            if (pageValue != null && !pageValue.isEmpty()) {
                page = Integer.parseInt(pageValue);
                if (page < 0) page = 0;
            }
        } catch (NumberFormatException e) {
            // Usa o valor padrão
        }
        
        try {
            String sizeValue = headers.getFirst(sizeHeaderName);
            if (sizeValue != null && !sizeValue.isEmpty()) {
                size = Integer.parseInt(sizeValue);
                if (size <= 0) size = defaultSize;
                if (size > maxSize) size = maxSize;
            }
        } catch (NumberFormatException e) {
            // Usa o valor padrão
        }
        
        return new PaginationHeader(paginate, page, size);
    }
    
    /**
     * Aplica os headers de paginação à resposta
     * 
     * @param response Resposta a ser modificada
     * @param headerNamePage Nome do header para o número da página
     * @param headerNameSize Nome do header para o tamanho da página
     * @param headerNameTotal Nome do header para o total de itens
     * @param headerNamePages Nome do header para o total de páginas
     * @param headerNameHasNext Nome do header para indicar se existe próxima página
     * @param headerNameHasPrev Nome do header para indicar se existe página anterior
     * @return A resposta com os headers adicionados
     */
    public void addHeaders(
            MultivaluedMap<String, Object> headers,
            String headerNamePage,
            String headerNameSize,
            String headerNameTotal,
            String headerNamePages,
            String headerNameHasNext,
            String headerNameHasPrev) {
        
        if (headers == null || !paginate) {
            return;
        }
        
        headers.add(headerNamePage, String.valueOf(page));
        headers.add(headerNameSize, String.valueOf(size));
        headers.add(headerNameTotal, String.valueOf(total));
        headers.add(headerNamePages, String.valueOf(pages));
        headers.add(headerNameHasNext, String.valueOf(hasNext));
        headers.add(headerNameHasPrev, String.valueOf(hasPrev));
    }
    
    // Getters e Setters
    
    public boolean isPaginate() {
        return paginate;
    }
    
    public void setPaginate(boolean paginate) {
        this.paginate = paginate;
    }
    
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public int getTotal() {
        return total;
    }
    
    public void setTotal(int total) {
        this.total = total;
        calculatePages();
    }
    
    public int getPages() {
        return pages;
    }
    
    public void setPages(int pages) {
        this.pages = pages;
        this.hasNext = page < pages - 1;
        this.hasPrev = page > 0;
    }
    
    public boolean isHasNext() {
        return hasNext;
    }
    
    public boolean isHasPrev() {
        return hasPrev;
    }
    
    /**
     * Calcula o número de páginas com base no total de itens e no tamanho da página
     */
    private void calculatePages() {
        if (size <= 0) {
            this.pages = 0;
        } else {
            this.pages = (int) Math.ceil((double) total / size);
        }
        
        this.hasNext = page < pages - 1;
        this.hasPrev = page > 0;
    }
}
