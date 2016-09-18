package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusCounterType;
import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entity class for MaterialLineStatusCounter.
 * 
 */
@Entity
@Table(name = "MATERIAL_LINE_STATUS_COUNTER")
public class MaterialLineStatusCounter implements GenericEntity<Long>, Serializable {

    private static final long serialVersionUID = -4343537924331047638L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MATERIAL_LINE_STATUS_COUNTER_OID")
    private long materialLineStatusCounterOid;
    
    @Enumerated(EnumType.STRING)
    private MaterialLineStatusCounterType type;


    private Timestamp  date;
    private String whSite;
    private String projectId;

    @Version
    private long version;
    
    public Timestamp getDate() {
        return date;
    }
    
    public void setDate(Timestamp date) {
        this.date = date;
    }
    
    public String getWhSite() {
        return whSite;
    }
    
    public void setWhSite(String whSite) {
        this.whSite = whSite;
    }
    
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Override
    public Long getId() {
        return materialLineStatusCounterOid;
    }

    @Override
    public long getVersion() {
        return version;
    }  
    
    public MaterialLineStatusCounterType getType() {
        return type;
    }

    public void setType(MaterialLineStatusCounterType type) {
        this.type = type;
    }
}
