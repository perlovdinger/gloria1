package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;
import java.util.Date;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * 
 * DTO class to manager Material.
 * 
 */
public class MaterialDTO implements Serializable, PageResults {

    private static final long serialVersionUID = 7505621806140439328L;

    private long id;
    private long version;
    private String referenceId;
    // quantity where its sum of materiallines which are not part of cancelled order.
    private long quantity; 
    // quantity where its just sum of all the underlying materiallines.
    private long quantityInclusiveCancelled; 
    private Long additionalQuantity;
    private String unitOfMeasure;
    private String receiver;
    private String partNumber;
    private String partVersion;
    private String partName;
    private String partModification;
    private String orderNo;
    private Date requiredStaDate;
    private String buildId;
    private String buildType;
    private String mtrlRequestVersion;
    private String mtrlRequestType;
    private Date outboundStartDate;
    private String outboundLocationId;
    private String contactPersonId;
    private String contactPersonName;
    private String requesterUserId;
    private String requesterName;

    private String partAffiliation;
    private String functionGroup;
    private String designResponsible;
    private long itemToVariantLinkId;
    private String modularHarness;
    private double unitPrice;
    private String currency;
    private String pPartAffiliation;
    private String pPartNumber;
    private String pPartVersion;
    private String pPartName;
    private String dfuObjectNumber;
    private long procureLineId;
    private String procureLineStatus;
    private String mark;
    private String mailFormId;
    private String changeAction;

    private String finalWhSiteId;
    
    private String materialType;
    private long modificationId;
    private String modificationType;
    
    private long procurementQty;
    
    private String projectId;
    private String referenceGroup;
    private String buildName;
    private String mtrlRequestId;
    private String companyCode;
    private String wbsCode;
    private String costCenter;
    private String glAccount;
    private String internalOrderNoSAP;
    private String materialControllerUserId;
    private String materialControllerName;
    private String deliveryControllerUserId;
    private String deliveryControllerUserName;
    private String[] finalWhSiteNames;
    private boolean isIsRead;
    private boolean isAlertPartVersion;
    private String procureType;
    private String procureComment;
    private String requestType;
    private long orderId;
    private boolean migrated;
    
    public boolean isIsRead() {
        return isIsRead;
    }

    public void setIsRead(boolean isRead) {
        this.isIsRead = isRead;
    }

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

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getQuantityInclusiveCancelled() {
        return quantityInclusiveCancelled;
    }

    public void setQuantityInclusiveCancelled(long quantityInclusiveCancelled) {
        this.quantityInclusiveCancelled = quantityInclusiveCancelled;
    }

    public Long getAdditionalQuantity() {
        return additionalQuantity;
    }

    public void setAdditionalQuantity(Long additionalQuantity) {
        this.additionalQuantity = additionalQuantity;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }
    
    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContactPersonId() {
        return contactPersonId;
    }

    public void setContactPersonId(String contactPersonId) {
        this.contactPersonId = contactPersonId;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getRequesterUserId() {
        return requesterUserId;
    }

    public void setRequesterUserId(String requesterUserId) {
        this.requesterUserId = requesterUserId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
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

    public String getPartModification() {
        return partModification;
    }

    public void setPartModification(String partModification) {
        this.partModification = partModification;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Date getRequiredStaDate() {
        return requiredStaDate;
    }

    public void setRequiredStaDate(Date requiredStaDate) {
        this.requiredStaDate = requiredStaDate;
    }

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    public String getBuildType() {
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    public String getMtrlRequestVersion() {
        return mtrlRequestVersion;
    }

    public void setMtrlRequestVersion(String mtrlRequestVersion) {
        this.mtrlRequestVersion = mtrlRequestVersion;
    }

    public String getMtrlRequestType() {
        return mtrlRequestType;
    }

    public void setMtrlRequestType(String mtrlRequestType) {
        this.mtrlRequestType = mtrlRequestType;
    }

    public Date getOutboundStartDate() {
        return outboundStartDate;
    }

    public void setOutboundStartDate(Date outboundStartDate) {
        this.outboundStartDate = outboundStartDate;
    }

    public String getOutboundLocationId() {
        return outboundLocationId;
    }

    public void setOutboundLocationId(String outboundLocationId) {
        this.outboundLocationId = outboundLocationId;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getPartAffiliation() {
        return partAffiliation;
    }

    public void setPartAffiliation(String partAffiliation) {
        this.partAffiliation = partAffiliation;
    }

    public String getFunctionGroup() {
        return functionGroup;
    }

    public void setFunctionGroup(String functionGroup) {
        this.functionGroup = functionGroup;
    }

    public String getDesignResponsible() {
        return designResponsible;
    }

    public void setDesignResponsible(String designResponsible) {
        this.designResponsible = designResponsible;
    }

    public long getItemToVariantLinkId() {
        return itemToVariantLinkId;
    }

    public void setItemToVariantLinkId(long itemToVariantLinkId) {
        this.itemToVariantLinkId = itemToVariantLinkId;
    }

    public String getModularHarness() {
        return modularHarness;
    }

    public void setModularHarness(String modularHarness) {
        this.modularHarness = modularHarness;
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

    public String getpPartVersion() {
        return pPartVersion;
    }

    public void setpPartVersion(String pPartVersion) {
        this.pPartVersion = pPartVersion;
    }

    public String getpPartName() {
        return pPartName;
    }

    public void setpPartName(String pPartName) {
        this.pPartName = pPartName;
    }

    public String getDfuObjectNumber() {
        return dfuObjectNumber;
    }

    public void setDfuObjectNumber(String dfuObjectNumber) {
        this.dfuObjectNumber = dfuObjectNumber;
    }

    public long getProcureLineId() {
        return procureLineId;
    }

    public void setProcureLineId(long procureLineId) {
        this.procureLineId = procureLineId;
    }

    public String getProcureLineStatus() {
        return procureLineStatus;
    }

    public void setProcureLineStatus(String procureLineStatus) {
        this.procureLineStatus = procureLineStatus;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getMailFormId() {
        return mailFormId;
    }

    public void setMailFormId(String mailFormId) {
        this.mailFormId = mailFormId;
    }

    public String getChangeAction() {
        return changeAction;
    }

    public void setChangeAction(String changeAction) {
        this.changeAction = changeAction;
    }

    public String getFinalWhSiteId() {
        return finalWhSiteId;
    }

    public void setFinalWhSiteId(String finalWhSiteId) {
        this.finalWhSiteId = finalWhSiteId;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getMaterialType() {
        return materialType;
    }

    public long getModificationId() {
        return modificationId;
    }

    public void setModificationId(long modificationId) {
        this.modificationId = modificationId;
    }

    public String getModificationType() {
        return modificationType;
    }

    public void setModificationType(String modificationType) {
        this.modificationType = modificationType;
    }

    public long getProcurementQty() {
        return procurementQty;
    }

    public void setProcurementQty(long procurementQty) {
        this.procurementQty = procurementQty;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getReferenceGroup() {
        return referenceGroup;
    }

    public void setReferenceGroup(String referenceGroup) {
        this.referenceGroup = referenceGroup;
    }

    public String getBuildName() {
        return buildName;
    }

    public void setBuildName(String buildName) {
        this.buildName = buildName;
    }

    public String getMtrlRequestId() {
        return mtrlRequestId;
    }

    public void setMtrlRequestId(String mtrlRequestId) {
        this.mtrlRequestId = mtrlRequestId;
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

    public String getMaterialControllerUserId() {
        return materialControllerUserId;
    }

    public void setMaterialControllerUserId(String materialControllerUserId) {
        this.materialControllerUserId = materialControllerUserId;
    }

    public String getMaterialControllerName() {
        return materialControllerName;
    }

    public void setMaterialControllerName(String materialControllerName) {
        this.materialControllerName = materialControllerName;
    }

    public String getDeliveryControllerUserId() {
        return deliveryControllerUserId;
    }

    public void setDeliveryControllerUserId(String deliveryControllerUserId) {
        this.deliveryControllerUserId = deliveryControllerUserId;
    }

    public String getDeliveryControllerUserName() {
        return deliveryControllerUserName;
    }

    public void setDeliveryControllerUserName(String deliveryControllerUserName) {
        this.deliveryControllerUserName = deliveryControllerUserName;
    }
    
    public String[] getFinalWhSiteNames() {
        return finalWhSiteNames;
    }
    
    public void setFinalWhSiteNames(String[] finalWhSiteNames) {
        this.finalWhSiteNames = finalWhSiteNames;
    }

    public boolean isAlertPartVersion() {
        return isAlertPartVersion;
    }

    public void setAlertPartVersion(boolean isAlertPartVersion) {
        this.isAlertPartVersion = isAlertPartVersion;
    }

    public String getProcureType() {
        return procureType;
    }

    public void setProcureType(String procureType) {
        this.procureType = procureType;
    }

    public String getProcureComment() {
        return procureComment;
    }

    public void setProcureComment(String procureComment) {
        this.procureComment = procureComment;
    }
    
    public String getRequestType() {
        return requestType;
    }
    
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
    
    public long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
    
    public boolean isMigrated() {
        return migrated;
    }
    
    public void setMigrated(boolean migrated) {
        this.migrated = migrated;
    }
}
