package com.volvo.gloria.warehouse.c.dto;

import java.io.Serializable;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * holds Aisles.
 */
public class AisleRackRowDTO implements Serializable, PageResults  {

    private static final long serialVersionUID = -5918255923647083452L;

    private Long id;
    private String code;
    private String baySides;
    private long numberOfBays;
    private String setUp;
    private long version;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBaySides() {
        return baySides;
    }

    public void setBaySides(String baySides) {
        this.baySides = baySides;
    }

    public long getNumberOfBays() {
        return numberOfBays;
    }

    public void setNumberOfBays(long numberOfBays) {
        this.numberOfBays = numberOfBays;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getSetUp() {
        return setUp;
    }

    public void setSetUp(String setUp) {
        this.setUp = setUp;
    }

}

