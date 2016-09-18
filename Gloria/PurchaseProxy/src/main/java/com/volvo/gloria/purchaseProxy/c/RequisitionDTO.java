package com.volvo.gloria.purchaseProxy.c;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO class for Requisition.
 */
public class RequisitionDTO implements Serializable {

    private static final long serialVersionUID = 6328922046987871474L;

    private long id;
    private String requisitionId;
    private String originatorUserId;
    private String originatorName;
    private String issuerOrganisation;
    private String issuerName;
    private String issuerPhoneNo;
    private String issuerDepartment;
    private String issuerUserId;
    private String materialUserId;
    private String contactPersonName;
    private String ppSuffix;
    private String reference;
    private Long quantity;
    private Double maximumPrice;
    private String maximumcurrency;
    private String priceType;
    private String requiredStaWeek;
    private Date requiredStaDate;
    private String purchaseInfo1;
    private String purchaseInfo2;
    private String partNumber;
    private String partQualifier;
    private String partVersion;
    private String partName;
    private String partFunctionGroup;
    private String unitOfMeasure;
    private String projectId;
    private String wbsCode;
    private String glAccount;
    private String costCenter;
    private boolean cancelled;
    private String buyerId;
    private String purchaseOrganizationCode;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRequisitionId() {
        return requisitionId;
    }

    public void setRequisitionId(String requisitionId) {
        this.requisitionId = requisitionId;
    }

    public String getIssuerOrganisation() {
        return issuerOrganisation;
    }

    public void setIssuerOrganisation(String issuerOrganisation) {
        this.issuerOrganisation = issuerOrganisation;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public String getIssuerPhoneNo() {
        return issuerPhoneNo;
    }

    public void setIssuerPhoneNo(String issuerPhoneNo) {
        this.issuerPhoneNo = issuerPhoneNo;
    }

    public String getIssuerDepartment() {
        return issuerDepartment;
    }

    public void setIssuerDepartment(String issuerDepartment) {
        this.issuerDepartment = issuerDepartment;
    }

    public String getIssuerUserId() {
        return issuerUserId;
    }

    public void setIssuerUserId(String issuerUserId) {
        this.issuerUserId = issuerUserId;
    }

    public String getMaterialUserId() {
        return materialUserId;
    }

    public void setMaterialUserId(String materialUserId) {
        this.materialUserId = materialUserId;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getPpSuffix() {
        return ppSuffix;
    }

    public void setPpSuffix(String ppSuffix) {
        this.ppSuffix = ppSuffix;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Double getMaximumPrice() {
        return maximumPrice;
    }

    public void setMaximumPrice(Double maximumPrice) {
        this.maximumPrice = maximumPrice;
    }

    public String getMaximumcurrency() {
        return maximumcurrency;
    }

    public void setMaximumcurrency(String maximumcurrency) {
        this.maximumcurrency = maximumcurrency;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public String getRequiredStaWeek() {
        return requiredStaWeek;
    }

    public void setRequiredStaWeek(String requiredStaWeek) {
        this.requiredStaWeek = requiredStaWeek;
    }

    public Date getRequiredStaDate() {
        return requiredStaDate;
    }

    public void setRequiredStaDate(Date requiredStaDate) {
        this.requiredStaDate = requiredStaDate;
    }

    public String getPurchaseInfo1() {
        return purchaseInfo1;
    }

    public void setPurchaseInfo1(String purchaseInfo1) {
        this.purchaseInfo1 = purchaseInfo1;
    }

    public String getPurchaseInfo2() {
        return purchaseInfo2;
    }

    public void setPurchaseInfo2(String purchaseInfo2) {
        this.purchaseInfo2 = purchaseInfo2;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartQualifier() {
        return partQualifier;
    }

    public void setPartQualifier(String partQualifier) {
        this.partQualifier = partQualifier;
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

    public String getPartFunctionGroup() {
        return partFunctionGroup;
    }

    public void setPartFunctionGroup(String partFunctionGroup) {
        this.partFunctionGroup = partFunctionGroup;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getWbsCode() {
        return wbsCode;
    }

    public void setWbsCode(String wbsCode) {
        this.wbsCode = wbsCode;
    }

    public String getGlAccount() {
        return glAccount;
    }

    public void setGlAccount(String glAccount) {
        this.glAccount = glAccount;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getPurchaseOrganizationCode() {
        return purchaseOrganizationCode;
    }

    public void setPurchaseOrganizationCode(String purchaseOrganizationCode) {
        this.purchaseOrganizationCode = purchaseOrganizationCode;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getOriginatorName() {
        return originatorName;
    }

    public void setOriginatorName(String originatorName) {
        this.originatorName = originatorName;
    }

    public String getOriginatorUserId() {
        return originatorUserId;
    }

    public void setOriginatorUserId(String originatorUserId) {
        this.originatorUserId = originatorUserId;
    }

}
