package com.volvo.gloria.common.carryover.c.dto;

import java.sql.Date;

/**
 * Carry Over Item coming from GPS.
 */
public class CarryOverItemDTO {

    private String customerId;
    private String supplierId;
       private String partAffliation;
    private String partNumber;
    private String customerName;
    private String supplierName;
    private String partVersion;
    private String supplierPartNumber;
    private String supplierPartVersion;
    private String supplierCountryCode;
    private Date startDate;
    private double amount;
    private String currency;
    private String priceUnit;
    private String unitCode;

    private String orderMode;
    private String documentId;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    
    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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

    public String getSupplierCountryCode() {
        return supplierCountryCode;
    }

    public void setSupplierCountryCode(String supplierCountryCode) {
        this.supplierCountryCode = supplierCountryCode;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
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

    public String getPartAffliation() {
        return partAffliation;
    }

    public void setPartAffliation(String partAffliation) {
        this.partAffliation = partAffliation;
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

    public void setOrderMode(String orderMode) {
        this.orderMode = orderMode;
    }

    public String getOrderMode() {
        return orderMode;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentId() {
        return documentId;
    }
}
