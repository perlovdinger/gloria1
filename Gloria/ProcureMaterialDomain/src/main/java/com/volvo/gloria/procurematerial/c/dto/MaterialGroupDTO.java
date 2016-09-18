package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.volvo.gloria.procurematerial.d.entities.Material;

public class MaterialGroupDTO implements Serializable {

    private static final long serialVersionUID = -2619563743741571498L;

    private long id;
    private long version;
    private long procurementQty;
    private List<Material> materials = new ArrayList<Material>();
    
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
    public List<Material> getMaterials() {
        return materials;
    }
    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }
}
