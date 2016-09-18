package com.volvo.gloria.common.c.dto;

/**
 * DTO class for part affiliation.
 */
public class PartAffiliationDTO {
    
    private long id;
    private String name;
    private String code;
    private String displaySeq;
    private boolean requestable; 
    
    public boolean isRequestable() {
        return requestable;
    }
    public void setRequestable(boolean requestable) {
        this.requestable = requestable;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getDisplaySeq() {
        return displaySeq;
    }
    public void setDisplaySeq(String displaySeq) {
        this.displaySeq = displaySeq;
    }    
}
