package com.volvo.gloria.procure.c.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for Procure.xml message.
 * 
 */
public class ProcureDTO implements Serializable {

    private static final long serialVersionUID = 2890597062646535224L;

    private String assignedMaterialController;

    private String shipToId;

    private String finalWarehouseId;

    private Date requiredSTADate;

    private double unitPrice;

    private String currency;

    private String supplierId;

    private String buyerCode;

    private Double maxPrice;

    private String materialUserCategory;

    private String materialUserName;

    private String unitOfMeasure;

    private long additionalQuantity;

    public String getAssignedMaterialController() {
        return assignedMaterialController;
    }

    public void setAssignedMaterialController(String assignedMaterialController) {
        this.assignedMaterialController = assignedMaterialController;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getBuyerCode() {
        return buyerCode;
    }

    public void setBuyerCode(String buyerCode) {
        this.buyerCode = buyerCode;
    }

    public String getMaterialUserCategory() {
        return materialUserCategory;
    }

    public void setMaterialUserCategory(String materialUserCategory) {
        this.materialUserCategory = materialUserCategory;
    }

    public String getMaterialUserName() {
        return materialUserName;
    }

    public void setMaterialUserName(String materialUserName) {
        this.materialUserName = materialUserName;
    }

    public String getShipToId() {
        return shipToId;
    }

    public void setShipToId(String shipToId) {
        this.shipToId = shipToId;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Date getRequiredSTADate() {
        return requiredSTADate;
    }

    public void setRequiredSTADate(Date requiredSTADate) {
        this.requiredSTADate = requiredSTADate;
    }

    public String getFinalWarehouseId() {
        return finalWarehouseId;
    }

    public void setFinalWarehouseId(String finalWarehouseId) {
        this.finalWarehouseId = finalWarehouseId;
    }


    public long getAdditionalQuantity() {
        return additionalQuantity;
    }

    public void setAdditionalQuantity(long additionalQuantity) {
        this.additionalQuantity = additionalQuantity;
    }
}
