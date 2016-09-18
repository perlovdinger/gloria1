package com.volvo.gloria.common.c.dto.reports;

import java.io.Serializable;

/**
 * DTO class to Order Reports.
 * 
 */
public class ReportFilterOrderDTO implements ReportFilters, Serializable {

    private static final long serialVersionUID = 4328205606133591198L;

    private long id;
    private String type;
    private String name;
    private String[] companyCode;
    private String[] suffix;
    private String[] project;
    private String[] buildSeries;
    private String[] testObject;
    private String[] supplierParmaId;
    private String[] supplierParmaName;
    private String[] reference;
    private String[] mtrId;
    private String[] deliveryControllerId;
    private String[] deliveryControllerName;
    private String[] orderStatus;
    private boolean requisitionId;
    private boolean requisitionDate;
    private boolean requisitionSite;
    private boolean orderType;
    private boolean orderNo;
    private boolean purchasingOrg;
    private boolean buyerId;
    private boolean buyerName;
    private boolean internalProcurerId;
    private boolean orderDate;
    private boolean orderQty;
    private boolean possibleToReceiveQty;
    private boolean unitOfMeasure;
    private boolean orderSta;
    private boolean agreedSta;
    private boolean receivedQty;
    private boolean expectedQty;
    private boolean expectedArrival;
    private boolean lastModifiedDate;
    private boolean blockedQty;
    private boolean damagedQty;
    private boolean plannedDispatchDate;
    private boolean orderLog;
    private boolean orderLineLog;
    private boolean price;
    private boolean unitPrice;
    private boolean unitOfPrice;
    private boolean currency;
    private boolean qtyTestObject;
    private boolean additionalQty;
    private boolean wbs;
    private boolean costCenter;
    private boolean glAccount;
    private boolean contactPersonId;
    private boolean contactPersonName;
    private boolean materialControllerId;
    private boolean materialControllerName;
    private boolean mailFormId;
    private boolean procureTeam;
    private boolean staAgreedLastUpdated;
    private boolean receivedDateTime;
    private boolean procureInfo;
    private boolean deliveryNoteDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String[] companyCode) {
        this.companyCode = companyCode;
    }

    public String[] getSuffix() {
        return suffix;
    }

    public void setSuffix(String[] suffix) {
        this.suffix = suffix;
    }

    public String[] getProject() {
        return project;
    }

    public void setProject(String[] project) {
        this.project = project;
    }

    public String[] getBuildSeries() {
        return buildSeries;
    }

    public void setBuildSeries(String[] buildSeries) {
        this.buildSeries = buildSeries;
    }

    public String[] getTestObject() {
        return testObject;
    }

    public void setTestObject(String[] testObject) {
        this.testObject = testObject;
    }

    public String[] getSupplierParmaId() {
        return supplierParmaId;
    }

    public void setSupplierParmaId(String[] supplierParmaId) {
        this.supplierParmaId = supplierParmaId;
    }

    public String[] getSupplierParmaName() {
        return supplierParmaName;
    }

    public void setSupplierParmaName(String[] supplierParmaName) {
        this.supplierParmaName = supplierParmaName;
    }
    
    public String[] getReference() {
        return reference;
    }
    
    public void setReference(String[] reference) {
        this.reference = reference;
    }

    public String[] getMtrId() {
        return mtrId;
    }

    public void setMtrId(String[] mtrId) {
        this.mtrId = mtrId;
    }

    public String[] getDeliveryControllerId() {
        return deliveryControllerId;
    }

    public void setDeliveryControllerId(String[] deliveryControllerId) {
        this.deliveryControllerId = deliveryControllerId;
    }

    public String[] getDeliveryControllerName() {
        return deliveryControllerName;
    }

    public void setDeliveryControllerName(String[] deliveryControllerName) {
        this.deliveryControllerName = deliveryControllerName;
    }

    public String[] getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String[] orderStatus) {
        this.orderStatus = orderStatus;
    }

    public boolean isRequisitionId() {
        return requisitionId;
    }

    public void setRequisitionId(boolean requisitionId) {
        this.requisitionId = requisitionId;
    }

    public boolean isRequisitionDate() {
        return requisitionDate;
    }

    public void setRequisitionDate(boolean requisitionDate) {
        this.requisitionDate = requisitionDate;
    }

    public boolean isRequisitionSite() {
        return requisitionSite;
    }

    public void setRequisitionSite(boolean requisitionSite) {
        this.requisitionSite = requisitionSite;
    }

    public boolean isOrderType() {
        return orderType;
    }

    public void setOrderType(boolean orderType) {
        this.orderType = orderType;
    }

    public boolean isOrderNo() {
        return orderNo;
    }

    public void setOrderNo(boolean orderNo) {
        this.orderNo = orderNo;
    }

    public boolean isPurchasingOrg() {
        return purchasingOrg;
    }

    public void setPurchasingOrg(boolean purchasingOrg) {
        this.purchasingOrg = purchasingOrg;
    }

    public boolean isBuyerId() {
        return buyerId;
    }

    public void setBuyerId(boolean buyerId) {
        this.buyerId = buyerId;
    }

    public boolean isInternalProcurerId() {
        return internalProcurerId;
    }

    public void setInternalProcurerId(boolean internalProcurerId) {
        this.internalProcurerId = internalProcurerId;
    }

    public boolean isOrderDate() {
        return orderDate;
    }

    public void setOrderDate(boolean orderDate) {
        this.orderDate = orderDate;
    }

    public boolean isOrderQty() {
        return orderQty;
    }

    public void setOrderQty(boolean orderQty) {
        this.orderQty = orderQty;
    }
    
    public boolean isPossibleToReceiveQty() {
        return possibleToReceiveQty;
    }
    
    public void setPossibleToReceiveQty(boolean possibleToReceiveQty) {
        this.possibleToReceiveQty = possibleToReceiveQty;
    }
    
    public boolean isUnitOfMeasure() {
        return unitOfMeasure;
    }
    
    public void setUnitOfMeasure(boolean unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public boolean isOrderSta() {
        return orderSta;
    }

    public void setOrderSta(boolean orderSta) {
        this.orderSta = orderSta;
    }

    public boolean isAgreedSta() {
        return agreedSta;
    }

    public void setAgreedSta(boolean agreedSta) {
        this.agreedSta = agreedSta;
    }

    public boolean isReceivedQty() {
        return receivedQty;
    }

    public void setReceivedQty(boolean receivedQty) {
        this.receivedQty = receivedQty;
    }

    public boolean isExpectedQty() {
        return expectedQty;
    }

    public void setExpectedQty(boolean expectedQty) {
        this.expectedQty = expectedQty;
    }

    public boolean isExpectedArrival() {
        return expectedArrival;
    }

    public void setExpectedArrival(boolean expectedArrival) {
        this.expectedArrival = expectedArrival;
    }

    public boolean isLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(boolean lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public boolean isBlockedQty() {
        return blockedQty;
    }

    public void setBlockedQty(boolean blockedQty) {
        this.blockedQty = blockedQty;
    }

    public boolean isDamagedQty() {
        return damagedQty;
    }

    public void setDamagedQty(boolean damagedQty) {
        this.damagedQty = damagedQty;
    }

    public boolean isPlannedDispatchDate() {
        return plannedDispatchDate;
    }

    public void setPlannedDispatchDate(boolean plannedDispatchDate) {
        this.plannedDispatchDate = plannedDispatchDate;
    }

    public boolean isOrderLog() {
        return orderLog;
    }

    public void setOrderLog(boolean orderLog) {
        this.orderLog = orderLog;
    }

    public boolean isOrderLineLog() {
        return orderLineLog;
    }

    public void setOrderLineLog(boolean orderLineLog) {
        this.orderLineLog = orderLineLog;
    }

    public boolean isPrice() {
        return price;
    }

    public void setPrice(boolean price) {
        this.price = price;
    }

    public boolean isUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(boolean unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public boolean isUnitOfPrice() {
        return unitOfPrice;
    }

    public void setUnitOfPrice(boolean unitOfPrice) {
        this.unitOfPrice = unitOfPrice;
    }

    public boolean isCurrency() {
        return currency;
    }

    public void setCurrency(boolean currency) {
        this.currency = currency;
    }

    public boolean isQtyTestObject() {
        return qtyTestObject;
    }

    public void setQtyTestObject(boolean qtyTestObject) {
        this.qtyTestObject = qtyTestObject;
    }

    public boolean isAdditionalQty() {
        return additionalQty;
    }

    public void setAdditionalQty(boolean additionalQty) {
        this.additionalQty = additionalQty;
    }

    public boolean isWbs() {
        return wbs;
    }

    public void setWbs(boolean wbs) {
        this.wbs = wbs;
    }

    public boolean isCostCenter() {
        return costCenter;
    }

    public void setCostCenter(boolean costCenter) {
        this.costCenter = costCenter;
    }

    public boolean isGlAccount() {
        return glAccount;
    }

    public void setGlAccount(boolean glAccount) {
        this.glAccount = glAccount;
    }

    public boolean isContactPersonId() {
        return contactPersonId;
    }

    public void setContactPersonId(boolean contactPersonId) {
        this.contactPersonId = contactPersonId;
    }

    public boolean isContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(boolean contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public boolean isMaterialControllerId() {
        return materialControllerId;
    }

    public void setMaterialControllerId(boolean materialControllerId) {
        this.materialControllerId = materialControllerId;
    }

    public boolean isMaterialControllerName() {
        return materialControllerName;
    }

    public void setMaterialControllerName(boolean materialControllerName) {
        this.materialControllerName = materialControllerName;
    }

    public boolean isMailFormId() {
        return mailFormId;
    }

    public void setMailFormId(boolean mailFormId) {
        this.mailFormId = mailFormId;
    }

    public boolean isProcureTeam() {
        return procureTeam;
    }

    public void setProcureTeam(boolean procureTeam) {
        this.procureTeam = procureTeam;
    }

    public boolean isBuyerName() {
        return buyerName;
    }

    public void setBuyerName(boolean buyerName) {
        this.buyerName = buyerName;
    }

    public boolean isStaAgreedLastUpdated() {
        return staAgreedLastUpdated;
    }

    public void setStaAgreedLastUpdated(boolean staAgreedLastUpdated) {
        this.staAgreedLastUpdated = staAgreedLastUpdated;
    }

    public boolean isReceivedDateTime() {
        return receivedDateTime;
    }

    public void setReceivedDateTime(boolean receivedDateTime) {
        this.receivedDateTime = receivedDateTime;
    }

    public boolean isProcureInfo() {
        return procureInfo;
    }

    public void setProcureInfo(boolean procureInfo) {
        this.procureInfo = procureInfo;
    }
    
    public boolean isDeliveryNoteDate() {
        return deliveryNoteDate;
    }

    public void setDeliveryNoteDate(boolean deliveryNoteDate) {
        this.deliveryNoteDate = deliveryNoteDate;
    }

}
