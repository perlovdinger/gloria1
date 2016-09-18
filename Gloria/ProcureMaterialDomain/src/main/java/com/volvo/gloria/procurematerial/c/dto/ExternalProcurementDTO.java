package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;
/**
 * Data class for external procurement details.
 */
public class ExternalProcurementDTO implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 4908974559763283248L;
    
    private long id;
    private long version;
    private String buildSeriesName;
    private String projectId;
    private String idQualifier;
    private String partNumber;
    private String partVersion;
    private String partName;
    private String functionGroup;
    private String functionGroupSuffix;
    private String dfuObjectNumber;
    private String inspectionLevel;
    private String criticality;
    private String securityLevel;
    private String scrappingAlert;
    private String warehouseComment;
    private Double estimatedPrice;
    private Double maxPrice;
    private String currency;
    private String wbsCode;
    private String costCenter;
    private String glAccount;
    private String internalOrder;
    private String issuerId;
    private String issuerName;
    private String purchaseInfo;
    
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
    public String getBuildSeriesName() {
        return buildSeriesName;
    }
    public void setBuildSeriesName(String buildSeriesName) {
        this.buildSeriesName = buildSeriesName;
    }
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    public String getIdQualifier() {
        return idQualifier;
    }
    public void setIdQualifier(String idQualifier) {
        this.idQualifier = idQualifier;
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
    public String getFunctionGroup() {
        return functionGroup;
    }
    public void setFunctionGroup(String functionGroup) {
        this.functionGroup = functionGroup;
    }
    public String getFunctionGroupSuffix() {
        return functionGroupSuffix;
    }
    public void setFunctionGroupSuffix(String functionGroupSuffix) {
        this.functionGroupSuffix = functionGroupSuffix;
    }
    public String getDfuObjectNumber() {
        return dfuObjectNumber;
    }
    public void setDfuObjectNumber(String dfuObjectNumber) {
        this.dfuObjectNumber = dfuObjectNumber;
    }
    public String getInspectionLevel() {
        return inspectionLevel;
    }
    public void setInspectionLevel(String inspectionLevel) {
        this.inspectionLevel = inspectionLevel;
    }
    public String getCriticality() {
        return criticality;
    }
    public void setCriticality(String criticality) {
        this.criticality = criticality;
    }
    public String getSecurityLevel() {
        return securityLevel;
    }
    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }
    public String getScrappingAlert() {
        return scrappingAlert;
    }
    public void setScrappingAlert(String scrappingAlert) {
        this.scrappingAlert = scrappingAlert;
    }
    public String getWarehouseComment() {
        return warehouseComment;
    }
    public void setWarehouseComment(String warehouseComment) {
        this.warehouseComment = warehouseComment;
    }
    public Double getEstimatedPrice() {
        return estimatedPrice;
    }
    public void setEstimatedPrice(Double estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }
    public Double getMaxPrice() {
        return maxPrice;
    }
    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public String getWbsCode() {
        return wbsCode;
    }
    public void setWbsCode(String wbsCode) {
        this.wbsCode = wbsCode;
    }
    public String getCostCenter() {
        return costCenter;
    }
    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }
    public String getGlAccount() {
        return glAccount;
    }
    public void setGlAccount(String glAccount) {
        this.glAccount = glAccount;
    }
    public String getInternalOrder() {
        return internalOrder;
    }
    public void setInternalOrder(String internalOrder) {
        this.internalOrder = internalOrder;
    }
    public String getIssuerId() {
        return issuerId;
    }
    public void setIssuerId(String issuerId) {
        this.issuerId = issuerId;
    }
    public String getIssuerName() {
        return issuerName;
    }
    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }
    public String getPurchaseInfo() {
        return purchaseInfo;
    }
    public void setPurchaseInfo(String purchaseInfo) {
        this.purchaseInfo = purchaseInfo;
    }
   
    
}
