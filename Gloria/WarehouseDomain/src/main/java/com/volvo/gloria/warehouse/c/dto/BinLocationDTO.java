package com.volvo.gloria.warehouse.c.dto;

import java.io.Serializable;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * holds binLocations.
 */
public class BinLocationDTO implements Serializable, PageResults {

    private static final long serialVersionUID = -5918255923647083452L;

    private Long id;
    private long version;
    private String code;
    private long rackRow;
    private String aisleRackRowCode;
    private String bay;
    private long level;
    private long position;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getRackRow() {
        return rackRow;
    }

    public void setRackRow(long rackRow) {
        this.rackRow = rackRow;
    }

    public String getBay() {
        return bay;
    }

    public void setBay(String bay) {
        this.bay = bay;
    }

    public long getLevel() {
        return level;
    }

    public void setLevel(long level) {
        this.level = level;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAisleRackRowCode() {
        return aisleRackRowCode;
    }

    public void setAisleRackRowCode(String aisleRackRowCode) {
        this.aisleRackRowCode = aisleRackRowCode;
    }

}
