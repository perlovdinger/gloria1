package com.volvo.gloria.materialrequest.d.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Entity MaterialRequestObject.
 * 
 */
@Entity
@Table(name = "MATERIAL_REQUEST_OBJECT")
public class MaterialRequestObject implements Serializable {
    private static final long serialVersionUID = -7366601948553296046L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MATERIAL_REQUEST_OBJECT_OID")
    private long materialRequestObjectOid;

    @Version
    private long version;

    private String name;
    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "materialRequestObject")
    private List<MaterialRequestLine> materialRequestLines = new ArrayList<MaterialRequestLine>();
    
    public long getMaterialRequestObjectOid() {
        return materialRequestObjectOid;
    }
    
    public void setMaterialRequestObjectOid(long materialRequestObjectOid) {
        this.materialRequestObjectOid = materialRequestObjectOid;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public List<MaterialRequestLine> getMaterialRequestLines() {
        return materialRequestLines;
    }
    
    public void setMaterialRequestLines(List<MaterialRequestLine> materialRequestLines) {
        this.materialRequestLines = materialRequestLines;
    }
}
