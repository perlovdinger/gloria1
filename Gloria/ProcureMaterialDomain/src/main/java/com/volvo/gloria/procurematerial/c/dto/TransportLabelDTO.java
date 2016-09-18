package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;

/**
 * DTO class for maintaning Transport label information.
 */
public class TransportLabelDTO implements Serializable {

    private static final long serialVersionUID = 2502844505869453784L;

    private long id;
    private long version;
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
