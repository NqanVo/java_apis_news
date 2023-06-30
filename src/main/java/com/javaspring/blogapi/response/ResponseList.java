package com.javaspring.blogapi.response;

import java.util.ArrayList;
import java.util.List;

public class ResponseList<T> {
    private Long currentPage;
    private Long limit;
    private Long totalPages;
    private Long totalItems;
    private List<T> data = new ArrayList<>();

    public ResponseList() {
    }

    public ResponseList(Long currentPage, Long limit, Long totalPages, Long totalItems, List<T> data) {
        this.currentPage = currentPage;
        this.limit = limit;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
        this.data = data;
    }

    public Long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Long currentPage) {
        this.currentPage = currentPage;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }

    public Long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Long totalItems) {
        this.totalItems = totalItems;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

}
