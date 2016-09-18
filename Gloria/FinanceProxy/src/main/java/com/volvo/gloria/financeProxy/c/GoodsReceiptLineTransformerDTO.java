package com.volvo.gloria.financeProxy.c;

import java.io.Serializable;

public class GoodsReceiptLineTransformerDTO implements Serializable {

    private static final long serialVersionUID = 1690313129347876967L;

    private String plant;
    private String movementType;
    private String vendor;
    private String vendorMaterialNumber;
    private String orderReference;
    private long quantity;
    private String isoUnitOfMeasure;
    private String movementIndicator;

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

    public String getIsoUnitOfMeasure() {
        return isoUnitOfMeasure;
    }

    public void setIsoUnitOfMeasure(String isoUnitOfMeasure) {
        this.isoUnitOfMeasure = isoUnitOfMeasure;
    }

    public String getMovementIndicator() {
        return movementIndicator;
    }

    public void setMovementIndicator(String movementIndicator) {
        this.movementIndicator = movementIndicator;
    }

}
