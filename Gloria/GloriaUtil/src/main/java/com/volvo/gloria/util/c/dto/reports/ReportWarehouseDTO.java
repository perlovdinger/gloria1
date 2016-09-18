package com.volvo.gloria.util.c.dto.reports;

import java.io.Serializable;

import com.volvo.gloria.util.paging.c.PageResults;

public class ReportWarehouseDTO implements Serializable, PageResults {

    private static final long serialVersionUID = 2287872007152794858L;

    private String id;

    private String text;

    public ReportWarehouseDTO() {
        
    }

    public ReportWarehouseDTO(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}