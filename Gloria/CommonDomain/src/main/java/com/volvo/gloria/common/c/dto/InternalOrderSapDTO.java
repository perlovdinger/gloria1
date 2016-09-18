package com.volvo.gloria.common.c.dto;

import java.io.Serializable;

/**
 * DTO class to handle internal order values.
 * 
 */
public class InternalOrderSapDTO implements Serializable {

    private static final long serialVersionUID = -2768010349434037607L;

    private long version;
    
    private long id;

    private String code;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
