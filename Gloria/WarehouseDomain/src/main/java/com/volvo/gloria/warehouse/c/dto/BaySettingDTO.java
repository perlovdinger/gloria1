package com.volvo.gloria.warehouse.c.dto;

import java.io.Serializable;

/**
 * data class for BaySetting.
 * 
 */
public class BaySettingDTO implements Serializable {
    private static final long serialVersionUID = 2333810328172356605L;

    private Long id;
    private String bayCode;
    private long version;
    private long numberOfLevels;
    private long numberOfPositions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBayCode() {
        return bayCode;
    }

    public void setBayCode(String bayCode) {
        this.bayCode = bayCode;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
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
