package util;

import lombok.Getter;

import java.util.List;

/**
 * Classe utilitária para paginação de listas.
 * Fornece métodos para paginar resultados de consultas.
 */
public class PaginationUtil {
    
    /**
     * Calcula o índice inicial para a paginação.
     * 
     * @param page Número da página (começando em 0)
     * @param size Tamanho da página
     * @return Índice inicial
     */
    public static int calculateStartIndex(int page, int size) {
        return page * size;
    }
    
    /**
     * Calcula o índice final para a paginação.
     * 
     * @param page Número da página (começando em 0)
     * @param size Tamanho da página
     * @param totalItems Total de itens
     * @return Índice final
     */
    public static int calculateEndIndex(int page, int size, int totalItems) {
        int endIndex = (page + 1) * size;
        return Math.min(endIndex, totalItems);
    }
    
    /**
     * Pagina uma lista de itens.
     * 
     * @param <T> Tipo dos itens
     * @param items Lista completa
     * @param page Número da página (começando em 0)
     * @param size Tamanho da página
     * @return Lista paginada
     */
    public static <T> List<T> paginate(List<T> items, int page, int size) {
        int totalItems = items.size();
        int startIndex = calculateStartIndex(page, size);
        int endIndex = calculateEndIndex(page, size, totalItems);
        
        if (startIndex >= totalItems) {
            return List.of(); // Página vazia
        }
        
        return items.subList(startIndex, endIndex);
    }
    
    /**
     * Calcula o número total de páginas.
     * 
     * @param totalItems Total de itens
     * @param size Tamanho da página
     * @return Número total de páginas
     */
    public static int calculateTotalPages(int totalItems, int size) {
        return (totalItems + size - 1) / size;
    }
    
    /**
     * Gera uma lista de números de página para exibição na paginação.
     * 
     * @param currentPage Página atual (começando em 0)
     * @param totalPages Total de páginas
     * @param maxPageLinks Número máximo de links de página a serem exibidos
     * @return Lista de números de página
     */
    public static List<Integer> generatePageNumbers(int currentPage, int totalPages, int maxPageLinks) {
        int startPage = Math.max(0, currentPage - (maxPageLinks / 2));
        int endPage = Math.min(totalPages - 1, startPage + maxPageLinks - 1);
        
        // Ajusta o início se não tivermos páginas suficientes no final
        startPage = Math.max(0, endPage - maxPageLinks + 1);
        
        return startPage == endPage 
            ? List.of(startPage)
            : java.util.stream.IntStream.rangeClosed(startPage, endPage)
                .boxed().toList();
    }
    
    /**
     * Classe que representa um resultado paginado.
     * 
     * @param <T> Tipo dos itens
     */
    @Getter
    public static class PagedResult<T> {
        private final List<T> content;
        private final int page;
        private final int size;
        private final int totalPages;
        private final long totalElements;
        private final List<Integer> pageNumbers;
        
        /**
         * Construtor para um resultado paginado.
         * 
         * @param content Conteúdo da página
         * @param page Número da página atual (começando em 0)
         * @param size Tamanho da página
         * @param totalElements Total de elementos
         * @param maxPageLinks Número máximo de links de página
         */
        public PagedResult(List<T> content, int page, int size, long totalElements, int maxPageLinks) {
            this.content = content;
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = calculateTotalPages((int) totalElements, size);
            this.pageNumbers = generatePageNumbers(page, totalPages, maxPageLinks);
        }
        
        /**
         * Factory method para criar um resultado paginado a partir de uma lista.
         * 
         * @param <T> Tipo dos itens
         * @param items Lista completa
         * @param page Número da página (começando em 0)
         * @param size Tamanho da página
         * @param maxPageLinks Número máximo de links de página
         * @return Resultado paginado
         */
        public static <T> PagedResult<T> of(List<T> items, int page, int size, int maxPageLinks) {
            List<T> content = paginate(items, page, size);
            return new PagedResult<>(content, page, size, items.size(), maxPageLinks);
        }
        
        // Getters

        public boolean isFirst() {
            return page == 0;
        }
        
        public boolean isLast() {
            return page >= totalPages - 1;
        }
        
        public boolean hasPrevious() {
            return page > 0;
        }
        
        public boolean hasNext() {
            return page < totalPages - 1;
        }
        
        public int getPreviousPage() {
            return Math.max(0, page - 1);
        }
        
        public int getNextPage() {
            return Math.min(totalPages - 1, page + 1);
        }
    }
}
