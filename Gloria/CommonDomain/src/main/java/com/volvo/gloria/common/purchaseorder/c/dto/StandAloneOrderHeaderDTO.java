package com.volvo.gloria.common.purchaseorder.c.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Class to hold header data from GPS.
 */
public class StandAloneOrderHeaderDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderNo;
    private String orderIdGps;
    private String orderMode;
    private String materialUserId;
    private String materialUserName;
    private String materialUserCategory;
    private String supplierId;
    private String supplierName;
    private String supplierCategory;
    private String orderDateTime;
    private String revisionId;
    private String partVersion;
    
    private List<StandAloneOrderLineDTO> standAloneOrderLineDTO;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderIdGps() {
        return orderIdGps;
    }

    public void setOrderIdGps(String orderIdGps) {
        this.orderIdGps = orderIdGps;
    }    
    
    public String getOrderMode() {
        return orderMode;
    }

    public void setOrderMode(String orderMode) {
        this.orderMode = orderMode;
    }

    public String getMaterialUserId() {
        return materialUserId;
    }

    public void setMaterialUserId(String materialUserId) {
        this.materialUserId = materialUserId;
    }

    public String getMaterialUserName() {
        return materialUserName;
    }

    public void setMaterialUserName(String materialUserName) {
        this.materialUserName = materialUserName;
    }

    public String getMaterialUserCategory() {
        return materialUserCategory;
    }

    public void setMaterialUserCategory(String materialUserCategory) {
        this.materialUserCategory = materialUserCategory;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierCategory() {
        return supplierCategory;
    }

    public void setSupplierCategory(String supplierCategory) {
        this.supplierCategory = supplierCategory;
    }

    public String getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(String orderDateTime) {
        this.orderDateTime = orderDateTime;
    }
    
    public String getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }

    public String getPartVersion() {
        return partVersion;
    }

    public void setPartVersion(String partVersion) {
        this.partVersion = partVersion;
    }

    public List<StandAloneOrderLineDTO> getStandAloneOrderLineDTO() {
        return standAloneOrderLineDTO;
    }

    public void setStandAloneOrderLineDTO(List<StandAloneOrderLineDTO> standAloneOrderLineDTO) {
        this.standAloneOrderLineDTO = standAloneOrderLineDTO;
    }

    /**
     * Base equals on releaseID. {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        StandAloneOrderHeaderDTO other = (StandAloneOrderHeaderDTO) obj;
        if (!this.getMaterialUserId().equals(other.getMaterialUserId())) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return this.orderNo.hashCode();
    }

    @Override
    public String toString() {
        return "StandAloneOrderHeaderDTO [orderNo=" + orderNo + ", orderIdGps=" + orderIdGps + ", materialUserId=" + materialUserId + ", materialUserName="
                + materialUserName + ", materialUserCategory=" + materialUserCategory + ", supplierId=" + supplierId + ", supplierName=" + supplierName
                + ", supplierCategory=" + supplierCategory + ", orderDateTime=" + orderDateTime + ", standAloneOrderLineDTO=" + standAloneOrderLineDTO + "]";
    }

}
