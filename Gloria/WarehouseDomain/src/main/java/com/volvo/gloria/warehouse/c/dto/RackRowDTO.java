package com.volvo.gloria.warehouse.c.dto;

import java.io.Serializable;

/**
 * holds RackRows.
 */
public class RackRowDTO implements Serializable {

    private static final long serialVersionUID = 5590736312834399521L;

    private Long id;
    private String code;
    private long version;
    private long numberOfBay;
    private long numberOfLevels;
    private long numberOfPositions;

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

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getNumberOfBay() {
        return numberOfBay;
    }

    public void setNumberOfBay(long numberOfBay) {
        this.numberOfBay = numberOfBay;
    }

    public long getNumberOfLevels() {
        return numberOfLevels;
    }

    public void setNumberOfLevels(long numberOfLevels) {
        this.numberOfLevels = numberOfLevels;
    }

    public long getNumberOfPositions() {
        return numberOfPositions;
    }

    public void setNumberOfPositions(long numberOfPositions) {
        this.numberOfPositions = numberOfPositions;
    }

}
