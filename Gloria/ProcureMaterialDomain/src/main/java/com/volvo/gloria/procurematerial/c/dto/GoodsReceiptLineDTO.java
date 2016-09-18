package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;
import java.util.Date;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * DTO for GoodsReceiptLine.
 * 
 */
public class GoodsReceiptLineDTO implements Serializable, PageResults {

    private static final long serialVersionUID = 8772082598980715277L;

    private long id;

    private long version;

    private String status;

    private String plant;

    private String movementType;

    private String vendor;

    private String vendorMaterialNumber;

    private String orderReference;

    private long quantity;

    private long quantityCancelled;

    private String isoUnitOfMeasure;

    private boolean isCancelable;

    private String deliveryNoteNo;

    private Date deliveryNoteDate;

    private String projectId;

    private String referenceId;

    private String partNumber;

    private String partVersion;

    private String partName;

    private String partModification;
    
    private Date receivalDate;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVendorMaterialNumber() {
        return vendorMaterialNumber;
    }

    public void setVendorMaterialNumber(String vendorMaterialNumber) {
        this.vendorMaterialNumber = vendorMaterialNumber;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getQuantityCancelled() {
        return quantityCancelled;
    }

    public void setQuantityCancelled(long quantityCancelled) {
        this.quantityCancelled = quantityCancelled;
    }

    public String getIsoUnitOfMeasure() {
        return isoUnitOfMeasure;
    }

    public void setIsoUnitOfMeasure(String isoUnitOfMeasure) {
        this.isoUnitOfMeasure = isoUnitOfMeasure;
    }

    public boolean isCancelable() {
        return isCancelable;
    }

    public void setCancelable(boolean isCancelable) {
        this.isCancelable = isCancelable;
    }

    public String getDeliveryNoteNo() {
        return deliveryNoteNo;
    }

    public void setDeliveryNoteNo(String deliveryNoteNo) {
        this.deliveryNoteNo = deliveryNoteNo;
    }

    public Date getDeliveryNoteDate() {
        return deliveryNoteDate;
    }

    public void setDeliveryNoteDate(Date deliveryNoteDate) {
        this.deliveryNoteDate = deliveryNoteDate;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartVersion() {
        return partVersion;
    }

    public void setPartVersion(String partVersion) {
        this.partVersion = partVersion;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getPartModification() {
        return partModification;
    }

    public void setPartModification(String partModification) {
        this.partModification = partModification;
    }

    public Date getReceivalDate() {
        return receivalDate;
    }

    public void setReceivalDate(Date receivalDate) {
        this.receivalDate = receivalDate;
    }
}
