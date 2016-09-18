package com.volvo.gloria.warehouse.c.dto;

import java.io.Serializable;

public class QualityInspectionPartDTO implements Serializable {

    private static final long serialVersionUID = 208786154829474059L;

    private Long id;
    private Long version;
    private String partNumber;
    private String partName;
    private boolean mandatory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public boolean isMandatory() {
        return mandatory;
    }
    
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
}
