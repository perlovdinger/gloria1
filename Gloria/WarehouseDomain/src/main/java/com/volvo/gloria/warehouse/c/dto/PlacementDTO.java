package com.volvo.gloria.warehouse.c.dto;

import java.io.Serializable;

/**
 * DTO class for Placement.
 */
public class PlacementDTO implements Serializable {

    private static final long serialVersionUID = -368254234305557482L;
    private Long materialLineOid;

    public Long getMaterialLineOid() {
        return materialLineOid;
    }

    public void setMaterialLineOid(Long materialLineOid) {
        this.materialLineOid = materialLineOid;
    }
}
