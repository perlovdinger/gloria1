package com.volvo.gloria.warehouse.c.dto;

import java.io.Serializable;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * holds Zones.
 */
public class ZoneDTO implements Serializable, PageResults {

    private static final long serialVersionUID = -5918255923647083452L;

    private Long id;
    private String code;
    private String name;
    private String description;
    private int numberOfAisleRows;
    private String type;
    private Long version;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumberOfAisleRows() {
        return numberOfAisleRows;
    }

    public void setNumberOfAisleRows(int numberOfAisleRows) {
        this.numberOfAisleRows = numberOfAisleRows;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

}
