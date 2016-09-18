package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;

public class MaterialHeaderGroupingDTO implements Serializable {

    private static final long serialVersionUID = 6087565403595865030L;
    private long id;
    private long version;
    private long procurementQty;
    private String materialIds;
    
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
    public long getProcurementQty() {
        return procurementQty;
    }
    public void setProcurementQty(long procurementQty) {
        this.procurementQty = procurementQty;
    }
    public String getMaterialIds() {
        return materialIds;
    }
    public void setMaterialIds(String materialIds) {
        this.materialIds = materialIds;
    }
}
