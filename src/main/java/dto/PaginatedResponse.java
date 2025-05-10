package dto;

import java.util.List;

public class PaginatedResponse<T> {
    private List<T> items;
    private int currentPage;
    private int totalPages;
    private long totalItems;
    private int pageSize;
    
    public PaginatedResponse(List<T> items, int currentPage, int totalPages, long totalItems, int pageSize) {
        this.items = items;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
        this.pageSize = pageSize;
    }
    
    public List<T> getItems() {
        return items;
    }
    
    public int getCurrentPage() {
        return currentPage;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public long getTotalItems() {
        return totalItems;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public boolean hasNext() {
        return currentPage < totalPages;
    }
    
    public boolean hasPrevious() {
        return currentPage > 1;
    }
} 