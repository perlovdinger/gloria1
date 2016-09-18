package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;

public class PartAliasDTO implements Serializable {

    private static final long serialVersionUID = 655462483054457583L;

    private long id;
    private String partNumber;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

}
