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

/**
 * Entity for QualityInspectionPart.
 */

@Entity
@Table(name = "QUALITY_INSPECTION_PART")
public class QualityInspectionPart implements Serializable {

    private static final long serialVersionUID = -1437585263905139622L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUALITY_INSPECTION_PART_OID")
    private long qualityInspectionPartOId;
    
    private long version;

    private String partNumber;

    private String partName;
    
    private boolean mandatory;

    @ManyToOne
    @JoinColumn(name = "WAREHOUSE_OID")
    private Warehouse warehouse;

    public long getQualityInspectionPartOId() {
        return qualityInspectionPartOId;
    }

    public void setQualityInspectionPartOId(long qualityInspectionPartOId) {
        this.qualityInspectionPartOId = qualityInspectionPartOId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
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

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }
}
