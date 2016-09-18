package com.volvo.gloria.common.c.dto;

import java.io.Serializable;

/**
 * data class for Dangerous goods.
 * 
 */
public class DangerousGoodsDTO implements Serializable {

    private static final long serialVersionUID = 7962511497407930490L;

    private long id;

    private long version;

    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
