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

import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.gloria.util.validators.GloriaStringSize;

/**
 * Entity for Quality inspection supplier.
 */
@Entity
@Table(name = "QUALITY_INSPECTION_SUPPLIER")
public class QualityInspectionSupplier implements Serializable {

    private static final long serialVersionUID = -3359352494155790174L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUALITY_INSPECTION_SUPPLIER_OID")
    private long qualityInspectionSupplierOId;
    private long version;

    @GloriaStringSize(max = GloriaParams.SUPPLIER_ID_LENGTH) 
    private String supplier;
    private boolean mandatory;
    
    @ManyToOne
    @JoinColumn(name = "WAREHOUSE_OID")
    private Warehouse warehouse;

    public long getQualityInspectionSupplierOId() {
        return qualityInspectionSupplierOId;
    }

    public void setQualityInspectionSupplierOId(long qualityInspectionSupplierOId) {
        this.qualityInspectionSupplierOId = qualityInspectionSupplierOId;
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
    
    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }
}
