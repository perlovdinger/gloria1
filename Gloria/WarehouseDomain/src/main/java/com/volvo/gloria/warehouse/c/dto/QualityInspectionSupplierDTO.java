package com.volvo.gloria.warehouse.c.dto;

import java.io.Serializable;

public class QualityInspectionSupplierDTO implements Serializable {

    private static final long serialVersionUID = 5520388166888734423L;
    private long id;
    private long version;
    private String supplier;
    private boolean mandatory;

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

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

}
