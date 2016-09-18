package com.volvo.gloria.util.c.dto.reports;

import java.io.Serializable;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * DTO class for report filter.
 * 
 */
public class ReportFilterDTO implements Serializable, PageResults {

    private static final long serialVersionUID = 3657545986277127346L;

    private String id;

    private String text;

    public ReportFilterDTO() {
        
    }

    public ReportFilterDTO(String id, String text) {
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
