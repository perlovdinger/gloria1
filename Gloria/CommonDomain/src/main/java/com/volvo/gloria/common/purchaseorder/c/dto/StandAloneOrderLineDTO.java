package com.volvo.gloria.common.purchaseorder.c.dto;

import java.io.Serializable;
import java.util.List;
/**
 * Data class for standalone orderLine.
 */
public class StandAloneOrderLineDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String partNumber;
    private String partVersion;
    private String partQualifier;
    private String partDescription;
    private String supplierPartNumber;
    private double amount;
    private Long perQuantity;
    private String currency;
    private String unitPriceTimePeriod;
    private String shipToPartyId;
    private String shipToPartyName;
    private String freightTermCode;
    private String paymentTerm;
    private String requisitionIds;
    private String buyerPartyId;
    private String buyerId;
    private String buyerName;
    private String buyerEmail;
    private String buyerSecurityId;
    private String projectNumber;
    private String wbsCode;
    private String account;
    private String costCenter;
    private String unitOfMeasure;
    private Long quantity;
    private String priceType;
    private String countryOfOrigin;
    
    private List<PurchaseOrderScheduleDTO> purchaseOrderSchedule;

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

    public String getPartQualifier() {
        return partQualifier;
    }

    public void setPartQualifier(String partQualifier) {
        this.partQualifier = partQualifier;
    }

    public String getPartDescription() {
        return partDescription;
    }

    public void setPartDescription(String partDescription) {
        this.partDescription = partDescription;
    }

    public String getSupplierPartNumber() {
        return supplierPartNumber;
    }

    public void setSupplierPartNumber(String supplierPartNumber) {
        this.supplierPartNumber = supplierPartNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Long getPerQuantity() {
        return perQuantity;
    }

    public void setPerQuantity(Long perQuantity) {
        this.perQuantity = perQuantity;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUnitPriceTimePeriod() {
        return unitPriceTimePeriod;
    }

    public void setUnitPriceTimePeriod(String unitPriceTimePeriod) {
        this.unitPriceTimePeriod = unitPriceTimePeriod;
    }

    public String getShipToPartyId() {
        return shipToPartyId;
    }

    public void setShipToPartyId(String shipToPartyId) {
        this.shipToPartyId = shipToPartyId;
    }

    public String getShipToPartyName() {
        return shipToPartyName;
    }

    public void setShipToPartyName(String shipToPartyName) {
        this.shipToPartyName = shipToPartyName;
    }

    public String getFreightTermCode() {
        return freightTermCode;
    }

    public void setFreightTermCode(String freightTermCode) {
        this.freightTermCode = freightTermCode;
    }

    public String getPaymentTerm() {
        return paymentTerm;
    }

    public void setPaymentTerm(String paymentTerm) {
        this.paymentTerm = paymentTerm;
    }

    public String getRequisitionIds() {
        return requisitionIds;
    }

    public void setRequisitionIds(String requisitionIds) {
        this.requisitionIds = requisitionIds;
    }

    public String getBuyerPartyId() {
        return buyerPartyId;
    }

    public void setBuyerPartyId(String buyerPartyId) {
        this.buyerPartyId = buyerPartyId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerSecurityId() {
        return buyerSecurityId;
    }

    public void setBuyerSecurityId(String buyerSecurityId) {
        this.buyerSecurityId = buyerSecurityId;
    }

    public String getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    public String getWbsCode() {
        return wbsCode;
    }

    public void setWbsCode(String wbsCode) {
        this.wbsCode = wbsCode;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public List<PurchaseOrderScheduleDTO> getPurchaseOrderSchedule() {
        return purchaseOrderSchedule;
    }

    public void setPurchaseOrderSchedule(List<PurchaseOrderScheduleDTO> purchaseOrderSchedule) {
        this.purchaseOrderSchedule = purchaseOrderSchedule;
    }
    
    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
    
    public String getPriceType() {
        return priceType;
    }
    
    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }
    
    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }
    
    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }
}
