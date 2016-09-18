package com.volvo.gloria.common.purchaseorder.c.dto;

import java.io.Serializable;

/**
 * data class for purchase order line.
 */
public class PurchaseOrderLineDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String partNumber;
    private String partVersion;
    private String supplierPartNumber;
    private String supplierPartVersion;
    private double amount;
    private String currency;
    private String priceUnit;
    private String unitCode;

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

    public String getSupplierPartNumber() {
        return supplierPartNumber;
    }

    public void setSupplierPartNumber(String supplierPartNumber) {
        this.supplierPartNumber = supplierPartNumber;
    }

    public String getSupplierPartVersion() {
        return supplierPartVersion;
    }

    public void setSupplierPartVersion(String supplierPartVersion) {
        this.supplierPartVersion = supplierPartVersion;
    }

    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getPriceUnit() {
        return priceUnit;
    }
    
    public void setPriceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
    }
    
    public String getUnitCode() {
        return unitCode;
    }
    
    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
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
        PurchaseOrderLineDTO other = (PurchaseOrderLineDTO) obj;
        if (!this.getPartVersion().equals(other.getPartVersion())) {
            return false;
        }
        return true;
    }

    /**
     * Base hashCode on releaseID. {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return (int) this.amount;
    }

    @Override
    public String toString() {
        return "PurchaseOrderLineDTO [partNumber=" + partNumber + ", partVersion=" + partVersion + ", supplierPartNumber=" + supplierPartNumber
                + ", supplierPartVersion=" + supplierPartVersion + ", amount=" + amount + ", currency=" + currency
                + ", priceUnit=" + priceUnit + ", unitCode=" + unitCode + "]";
  }
}
