package com.volvo.gloria.common.c.dto;

import java.io.Serializable;

/**
 * DTO class for Team.
 */
public class TeamDTO implements Serializable {

    private static final long serialVersionUID = 7723217389491703059L;

    private long id;

    private long version;

    private String name;

    private String type;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
