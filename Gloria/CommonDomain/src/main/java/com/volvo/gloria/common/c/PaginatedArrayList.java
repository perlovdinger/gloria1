package com.volvo.gloria.common.c;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PaginatedArrayList<T> extends ArrayList<T> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_PAGE_SIZE = 100;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private int currentEndIndex = 0;

    public PaginatedArrayList(Collection<T> collection) {
        super(collection);
    }

    public PaginatedArrayList() {
        super();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> nextPage() {
        int size = this.size();

        if (currentEndIndex >= size) {
            return null;
        }
        int endIndex;
        int startIndex = currentEndIndex;

        if (startIndex + pageSize >= size) {
            endIndex = size;
        } else {
            endIndex = startIndex + pageSize;
        }
        currentEndIndex = endIndex;

        return (List<T>) this.subList(startIndex, endIndex);
    }
}
