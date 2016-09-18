package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;
import java.util.Date;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * Data class for Procurement line details.
 */
public class ProcureLineDTO implements Serializable, PageResults {

    private static final long serialVersionUID = -6055920219249903540L;

    private long id;
    private long version;
    private long financeHeaderId;
    private long financeHeaderVersion;
    private String projectId;
    private String changeRequestIds;
    private String materialRequestId;
    private String mailFormIds;
    private String functionGroupId;
    private String functionGroupSuffix;
    private String dfuObjectNumber;
    private String originatorId;
    private String originatorName;
    private String originatorDepartment;
    private String criticality;
    private String issuerId;
    private String issuerName;
    private String companyCode;
    private String wbsCode;
    private String costCenter;
    private String glAccount;
    private String internalOrderNoSAP;
    private String procureResponsibility;
    private String status;
    private String warehouseComment;
    private Double maxPrice;
    private String pPartAffiliation;
    private String pPartNumber;
    private String pPartName;
    private String pPartModification;
    private String pPartVersion;
    private String requisitionId;
    private String orderNo;
    private String referenceGroups;
    private long dangerousGoods;
    private String procureType;
    private String buyerCode;
    private String buyerName;
    private Long supplierCounterPartID;
    private Date requiredStaDate;
    private Long supplierId;
    private String supplierName;
    private double unitPrice;
    private String currency;
    private String procureInfo;
    private long additionalQuantity;
    private String referenceIds;
    private boolean needIsChanged;
    private String partAlias;
    private Long qualityDocumentId;
    private long usageQty;
    private String procureForwardedId;
    private String procureForwardedName;
    private String procureForwardedTeam;
    private boolean forwardedForDC;
    private String modularHarness;
    private String purchaseOrganisationCode;
    private boolean forStock;
    private String referenceGps;
    private long procureQty;
    private Date orderStaDate;
    private boolean hasUnread;
    private Date procureFailureDate;
    private boolean procureCommmentExist;
    private long unitOfPrice;
    private String outboundLocationId;
    private String outboundLocationName;
    private long fromStockQty;
    private long fromStockProjectQty;
    private String statusFlag;
    private boolean edited;

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

    public long getFinanceHeaderId() {
        return financeHeaderId;
    }

    public void setFinanceHeaderId(long financeHeaderId) {
        this.financeHeaderId = financeHeaderId;
    }

    public long getFinanceHeaderVersion() {
        return financeHeaderVersion;
    }

    public void setFinanceHeaderVersion(long financeHeaderVersion) {
        this.financeHeaderVersion = financeHeaderVersion;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getChangeRequestIds() {
        return changeRequestIds;
    }

    public void setChangeRequestIds(String changeRequestIds) {
        this.changeRequestIds = changeRequestIds;
    }

    public String getMaterialRequestId() {
        return materialRequestId;
    }

    public void setMaterialRequestId(String materialRequestId) {
        this.materialRequestId = materialRequestId;
    }

    public String getMailFormIds() {
        return mailFormIds;
    }

    public void setMailFormIds(String mailFormIds) {
        this.mailFormIds = mailFormIds;
    }

    public String getFunctionGroupId() {
        return functionGroupId;
    }

    public void setFunctionGroupId(String functionGroupId) {
        this.functionGroupId = functionGroupId;
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

    public String getOriginatorId() {
        return originatorId;
    }

    public void setOriginatorId(String originatorId) {
        this.originatorId = originatorId;
    }

    public String getOriginatorName() {
        return originatorName;
    }

    public void setOriginatorName(String originatorName) {
        this.originatorName = originatorName;
    }

    public String getOriginatorDepartment() {
        return originatorDepartment;
    }

    public void setOriginatorDepartment(String originatorDepartment) {
        this.originatorDepartment = originatorDepartment;
    }

    public String getCriticality() {
        return criticality;
    }

    public void setCriticality(String criticality) {
        this.criticality = criticality;
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

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
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

    public String getInternalOrderNoSAP() {
        return internalOrderNoSAP;
    }

    public void setInternalOrderNoSAP(String internalOrderNoSAP) {
        this.internalOrderNoSAP = internalOrderNoSAP;
    }

    public String getProcureResponsibility() {
        return procureResponsibility;
    }

    public void setProcureResponsibility(String procureResponsibility) {
        this.procureResponsibility = procureResponsibility;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWarehouseComment() {
        return warehouseComment;
    }

    public void setWarehouseComment(String warehouseComment) {
        this.warehouseComment = warehouseComment;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getpPartAffiliation() {
        return pPartAffiliation;
    }

    public void setpPartAffiliation(String pPartAffiliation) {
        this.pPartAffiliation = pPartAffiliation;
    }

    public String getpPartNumber() {
        return pPartNumber;
    }

    public void setpPartNumber(String pPartNumber) {
        this.pPartNumber = pPartNumber;
    }

    public String getpPartName() {
        return pPartName;
    }

    public void setpPartName(String pPartName) {
        this.pPartName = pPartName;
    }

    public String getpPartModification() {
        return pPartModification;
    }

    public void setpPartModification(String pPartModification) {
        this.pPartModification = pPartModification;
    }

    public String getpPartVersion() {
        return pPartVersion;
    }

    public void setpPartVersion(String pPartVersion) {
        this.pPartVersion = pPartVersion;
    }

    public String getRequisitionId() {
        return requisitionId;
    }

    public void setRequisitionId(String requisitionId) {
        this.requisitionId = requisitionId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getReferenceGroups() {
        return referenceGroups;
    }

    public void setReferenceGroups(String referenceGroups) {
        this.referenceGroups = referenceGroups;
    }

    public long getDangerousGoods() {
        return dangerousGoods;
    }

    public void setDangerousGoods(long dangerousGoods) {
        this.dangerousGoods = dangerousGoods;
    }

    public String getProcureType() {
        return procureType;
    }

    public void setProcureType(String procureType) {
        this.procureType = procureType;
    }

    public String getBuyerCode() {
        return buyerCode;
    }

    public void setBuyerCode(String buyerCode) {
        this.buyerCode = buyerCode;
    }
    
    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public Long getSupplierCounterPartID() {
        return supplierCounterPartID;
    }

    public void setSupplierCounterPartID(Long supplierCounterPartID) {
        this.supplierCounterPartID = supplierCounterPartID;
    }

    public Date getRequiredStaDate() {
        return requiredStaDate;
    }

    public void setRequiredStaDate(Date requiredStaDate) {
        this.requiredStaDate = requiredStaDate;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getProcureInfo() {
        return procureInfo;
    }

    public void setProcureInfo(String procureInfo) {
        this.procureInfo = procureInfo;
    }

    public String getReferenceIds() {
        return referenceIds;
    }

    public void setReferenceIds(String referenceIds) {
        this.referenceIds = referenceIds;
    }

    public long getAdditionalQuantity() {
        return additionalQuantity;
    }

    public void setAdditionalQuantity(long additionalQuantity) {
        this.additionalQuantity = additionalQuantity;
    }

    public boolean isNeedIsChanged() {
        return needIsChanged;
    }

    public void setNeedIsChanged(boolean needIsChanged) {
        this.needIsChanged = needIsChanged;
    }

    public String getPartAlias() {
        return partAlias;
    }

    public void setPartAlias(String partAlias) {
        this.partAlias = partAlias;
    }

    public Long getQualityDocumentId() {
        return qualityDocumentId;
    }

    public void setQualityDocumentId(Long qualityDocumentId) {
        this.qualityDocumentId = qualityDocumentId;
    }

    public long getUsageQty() {
        return usageQty;
    }

    public void setUsageQty(long usageQty) {
        this.usageQty = usageQty;
    }

    public String getProcureForwardedId() {
        return procureForwardedId;
    }

    public void setProcureForwardedId(String procureForwardedId) {
        this.procureForwardedId = procureForwardedId;
    }

    public String getProcureForwardedName() {
        return procureForwardedName;
    }

    public void setProcureForwardedName(String procureForwardedName) {
        this.procureForwardedName = procureForwardedName;
    }

    public boolean isForwardedForDC() {
        return forwardedForDC;
    }

    public void setForwardedForDC(boolean forwardedForDC) {
        this.forwardedForDC = forwardedForDC;
    }

    public String getModularHarness() {
        return modularHarness;
    }

    public void setModularHarness(String modularHarness) {
        this.modularHarness = modularHarness;
    }

    public String getPurchaseOrganisationCode() {
        return purchaseOrganisationCode;
    }

    public void setPurchaseOrganisationCode(String purchaseOrganisationCode) {
        this.purchaseOrganisationCode = purchaseOrganisationCode;
    }
    
    public String getProcureForwardedTeam() {
        return procureForwardedTeam;
    }
    
    public void setProcureForwardedTeam(String procureForwardedTeam) {
        this.procureForwardedTeam = procureForwardedTeam;
    }

    public boolean isForStock() {
        return forStock;
    }

    public void setForStock(boolean forStock) {
        this.forStock = forStock;
    }

    public String getReferenceGps() {
        return referenceGps;
    }
    
    public void setReferenceGps(String referenceGps) {
        this.referenceGps = referenceGps;
    }

    public long getProcureQty() {
        return procureQty;
    }

    public void setProcureQty(long procureQty) {
        this.procureQty = procureQty;
    }
    
    public Date getOrderStaDate() {
        return orderStaDate;
    }
    
    public void setOrderStaDate(Date orderStaDate) {
        this.orderStaDate = orderStaDate;
    }

    public boolean isHasUnread() {
        return hasUnread;
    }
    
    public void setHasUnread(boolean unRead) {
        this.hasUnread = unRead;
    }

    public Date getProcureFailureDate() {
        return procureFailureDate;
    }

    public void setProcureFailureDate(Date procureFailureDate) {
        this.procureFailureDate = procureFailureDate;
    }

    public boolean isProcureCommmentExist() {
        return procureCommmentExist;
    }

    public void setProcureCommmentExist(boolean procureCommmentExist) {
        this.procureCommmentExist = procureCommmentExist;
    }

    public long getUnitOfPrice() {
        return unitOfPrice;
    }

    public void setUnitOfPrice(long unitOfPrice) {
        this.unitOfPrice = unitOfPrice;
    }

    public String getOutboundLocationId() {
        return outboundLocationId;
    }
    
    public void setOutboundLocationId(String outboundLocationId) {
        this.outboundLocationId = outboundLocationId;
    }

    public String getOutboundLocationName() {
        return outboundLocationName;
    }
    
    public void setOutboundLocationName(String outboundLocationName) {
        this.outboundLocationName = outboundLocationName;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public long getFromStockQty() {
        return fromStockQty;
    }

    public void setFromStockQty(long fromStockQty) {
        this.fromStockQty = fromStockQty;
    }

    public long getFromStockProjectQty() {
        return fromStockProjectQty;
    }

    public void setFromStockProjectQty(long fromStockProjectQty) {
        this.fromStockProjectQty = fromStockProjectQty;
    }
    
    public String getStatusFlag() {
        return statusFlag;
    }

    public void setStatusFlag(String statusFlag) {
        this.statusFlag = statusFlag;
    }
    
    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

}
