package com.volvo.gloria.util.c.dto.reports;

import java.io.Serializable;

public class ReportColumn implements Serializable {

    private static final long serialVersionUID = -6644308284491136932L;
        
    private String name;
    
    private Object value;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }    
}
