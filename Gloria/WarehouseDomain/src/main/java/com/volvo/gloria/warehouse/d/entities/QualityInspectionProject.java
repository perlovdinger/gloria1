package com.volvo.gloria.warehouse.d.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "QUALITY_INSPECTION_PROJECT")
public class QualityInspectionProject implements Serializable {

    private static final long serialVersionUID = 1416285476428675782L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUALITY_INSPECTION_PROJECT_OID")
    private long qualityInspectionProjectOId;
    private long version;

   
    private String project;
    private boolean mandatory;

    @ManyToOne
    @JoinColumn(name = "WAREHOUSE_OID")
    private Warehouse warehouse;

    public long getQualityInspectionProjectOId() {
        return qualityInspectionProjectOId;
    }

    public void setQualityInspectionProjectOId(long qualityInspectionProjectOId) {
        this.qualityInspectionProjectOId = qualityInspectionProjectOId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }
}
