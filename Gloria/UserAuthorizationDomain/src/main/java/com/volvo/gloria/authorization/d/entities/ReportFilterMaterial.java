package com.volvo.gloria.authorization.d.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Entity for Material Report Filters.
 * 
 */
@Entity
@DiscriminatorValue("MATERIAL")
public class ReportFilterMaterial extends ReportFilter {

    private static final long serialVersionUID = 792627455283030548L;

    private boolean costCenter;
    private boolean glAccount;
    private boolean internalOrderSAP;
    private boolean mailFormId;
    private boolean functionGroup;
    private boolean designGroup;
    private boolean modularHarness;
    private boolean requisitionId;
    private boolean requisitionDate;
    private boolean reference;
    private boolean requiredSta;
    private boolean buildLocationId;
    private boolean buildLocationName;
    private boolean buildStart;
    private boolean materialControllerId;
    private boolean materialControllerName;
    private boolean requesterId;
    private boolean requesterName;
    private boolean contactPersonId;
    private boolean contactPersonName;
    private boolean warehouse;
    private boolean storageRoom;
    private boolean binLocation;
    private boolean qty;
    private boolean unitOfMeasure;
    private boolean receivedDate;
    private boolean deliveryNoteNo;
    private boolean dispatchNoteNo;
    private boolean deliveryAddressId;
    private boolean deliveryAddressName;
    private boolean source;
    private boolean orderNo;
    private boolean orderStatus;
    private boolean purchasingOrganisation;
    private boolean buyerId;
    private boolean internalProcurerId;
    private boolean orderDate;
    private boolean orderQty;
    private boolean possibleToReceiveQty;
    private boolean orderSTA;
    private boolean agreedSTA;
    private boolean staAccepted;
    private boolean expectedQty;
    private boolean expectedDate;
    private boolean plannedDispatchDate;
    private boolean price;
    private boolean currency;
    private boolean unitPrice;
    private boolean unitOfPrice;
    private boolean supplierParmaId;
    private boolean supplierName;
    private boolean deliveryControllerId;
    private boolean deliveryControllerName;
    private boolean flagOrderLine;
    private boolean flagProcureLine;
    private boolean procureInfo;
    private boolean receivedQty;
    private boolean blockedQty;
    private boolean buyerName;
    private boolean orderLineLogEventValue;
    private boolean orderLogEventValue;
    private boolean problemDescription;
    private boolean qualityInspectionComment;
    private boolean statusUserId;
    private boolean statusUserName;
    private boolean staAgreedLastUpdated;
    private boolean inspectionStatus;

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

    public boolean isInternalOrderSAP() {
        return internalOrderSAP;
    }

    public void setInternalOrderSAP(boolean internalOrderSAP) {
        this.internalOrderSAP = internalOrderSAP;
    }

    public boolean isMailFormId() {
        return mailFormId;
    }

    public void setMailFormId(boolean mailFormId) {
        this.mailFormId = mailFormId;
    }

    public boolean isFunctionGroup() {
        return functionGroup;
    }

    public void setFunctionGroup(boolean functionGroup) {
        this.functionGroup = functionGroup;
    }

    public boolean isDesignGroup() {
        return designGroup;
    }

    public void setDesignGroup(boolean designGroup) {
        this.designGroup = designGroup;
    }

    public boolean isModularHarness() {
        return modularHarness;
    }

    public void setModularHarness(boolean modularHarness) {
        this.modularHarness = modularHarness;
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

    public boolean isReference() {
        return reference;
    }

    public void setReference(boolean reference) {
        this.reference = reference;
    }

    public boolean isRequiredSta() {
        return requiredSta;
    }

    public void setRequiredSta(boolean requiredSta) {
        this.requiredSta = requiredSta;
    }

    public boolean isBuildLocationId() {
        return buildLocationId;
    }

    public void setBuildLocationId(boolean buildLocationId) {
        this.buildLocationId = buildLocationId;
    }

    public boolean isBuildLocationName() {
        return buildLocationName;
    }

    public void setBuildLocationName(boolean buildLocationName) {
        this.buildLocationName = buildLocationName;
    }

    public boolean isBuildStart() {
        return buildStart;
    }

    public void setBuildStart(boolean buildStart) {
        this.buildStart = buildStart;
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

    public boolean isRequesterId() {
        return requesterId;
    }

    public void setRequesterId(boolean requesterId) {
        this.requesterId = requesterId;
    }

    public boolean isRequesterName() {
        return requesterName;
    }

    public void setRequesterName(boolean requesterName) {
        this.requesterName = requesterName;
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

    public boolean isWarehouse() {
        return warehouse;
    }

    public void setWarehouse(boolean warehouse) {
        this.warehouse = warehouse;
    }

    public boolean isStorageRoom() {
        return storageRoom;
    }

    public void setStorageRoom(boolean storageRoom) {
        this.storageRoom = storageRoom;
    }

    public boolean isBinLocation() {
        return binLocation;
    }

    public void setBinLocation(boolean binLocation) {
        this.binLocation = binLocation;
    }

    public boolean isQty() {
        return qty;
    }

    public void setQty(boolean qty) {
        this.qty = qty;
    }

    public boolean isUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(boolean unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public boolean isReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(boolean receivedDate) {
        this.receivedDate = receivedDate;
    }

    public boolean isDeliveryNoteNo() {
        return deliveryNoteNo;
    }

    public void setDeliveryNoteNo(boolean deliveryNoteNo) {
        this.deliveryNoteNo = deliveryNoteNo;
    }

    public boolean isDispatchNoteNo() {
        return dispatchNoteNo;
    }

    public void setDispatchNoteNo(boolean dispatchNoteNo) {
        this.dispatchNoteNo = dispatchNoteNo;
    }

    public boolean isDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(boolean deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }

    public boolean isDeliveryAddressName() {
        return deliveryAddressName;
    }

    public void setDeliveryAddressName(boolean deliveryAddressName) {
        this.deliveryAddressName = deliveryAddressName;
    }

    public boolean isSource() {
        return source;
    }

    public void setSource(boolean source) {
        this.source = source;
    }

    public boolean isOrderNo() {
        return orderNo;
    }

    public void setOrderNo(boolean orderNo) {
        this.orderNo = orderNo;
    }

    public boolean isOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(boolean orderStatus) {
        this.orderStatus = orderStatus;
    }

    public boolean isPurchasingOrganisation() {
        return purchasingOrganisation;
    }

    public void setPurchasingOrganisation(boolean purchasingOrganisation) {
        this.purchasingOrganisation = purchasingOrganisation;
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
    
    public boolean isOrderSTA() {
        return orderSTA;
    }

    public void setOrderSTA(boolean orderSTA) {
        this.orderSTA = orderSTA;
    }

    public boolean isAgreedSTA() {
        return agreedSTA;
    }

    public void setAgreedSTA(boolean agreedSTA) {
        this.agreedSTA = agreedSTA;
    }

    public boolean isStaAccepted() {
        return staAccepted;
    }

    public void setStaAccepted(boolean staAccepted) {
        this.staAccepted = staAccepted;
    }

    public boolean isExpectedQty() {
        return expectedQty;
    }

    public void setExpectedQty(boolean expectedQty) {
        this.expectedQty = expectedQty;
    }

    public boolean isExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(boolean expectedDate) {
        this.expectedDate = expectedDate;
    }

    public boolean isPlannedDispatchDate() {
        return plannedDispatchDate;
    }

    public void setPlannedDispatchDate(boolean plannedDispatchDate) {
        this.plannedDispatchDate = plannedDispatchDate;
    }

    public boolean isPrice() {
        return price;
    }

    public void setPrice(boolean price) {
        this.price = price;
    }

    public boolean isCurrency() {
        return currency;
    }

    public void setCurrency(boolean currency) {
        this.currency = currency;
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

    public boolean isSupplierParmaId() {
        return supplierParmaId;
    }

    public void setSupplierParmaId(boolean supplierParmaId) {
        this.supplierParmaId = supplierParmaId;
    }

    public boolean isSupplierName() {
        return supplierName;
    }

    public void setSupplierName(boolean supplierName) {
        this.supplierName = supplierName;
    }

    public boolean isDeliveryControllerId() {
        return deliveryControllerId;
    }

    public void setDeliveryControllerId(boolean deliveryControllerId) {
        this.deliveryControllerId = deliveryControllerId;
    }

    public boolean isDeliveryControllerName() {
        return deliveryControllerName;
    }

    public void setDeliveryControllerName(boolean deliveryControllerName) {
        this.deliveryControllerName = deliveryControllerName;
    }

    public boolean isProcureInfo() {
        return procureInfo;
    }

    public void setProcureInfo(boolean procureInfo) {
        this.procureInfo = procureInfo;
    }

    public boolean isReceivedQty() {
        return receivedQty;
    }

    public void setReceivedQty(boolean receivedQty) {
        this.receivedQty = receivedQty;
    }

    public boolean isBlockedQty() {
        return blockedQty;
    }

    public void setBlockedQty(boolean blockedQty) {
        this.blockedQty = blockedQty;
    }

    public boolean isOrderLineLogEventValue() {
        return orderLineLogEventValue;
    }

    public void setOrderLineLogEventValue(boolean orderLineLogEventValue) {
        this.orderLineLogEventValue = orderLineLogEventValue;
    }

    public boolean isOrderLogEventValue() {
        return orderLogEventValue;
    }

    public void setOrderLogEventValue(boolean orderLogEventValue) {
        this.orderLogEventValue = orderLogEventValue;
    }

    public boolean isBuyerName() {
        return buyerName;
    }

    public void setBuyerName(boolean buyerName) {
        this.buyerName = buyerName;
    }

    public boolean isProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(boolean problemDescription) {
        this.problemDescription = problemDescription;
    }

    public boolean isStatusUserId() {
        return statusUserId;
    }

    public void setStatusUserId(boolean statusUserId) {
        this.statusUserId = statusUserId;
    }

    public boolean isQualityInspectionComment() {
        return qualityInspectionComment;
    }

    public void setQualityInspectionComment(boolean qualityInspectionComment) {
        this.qualityInspectionComment = qualityInspectionComment;
    }

    public boolean isStatusUserName() {
        return statusUserName;
    }

    public void setStatusUserName(boolean statusUserName) {
        this.statusUserName = statusUserName;
    }

    public boolean isStaAgreedLastUpdated() {
        return staAgreedLastUpdated;
    }

    public void setStaAgreedLastUpdated(boolean staAgreedLastUpdated) {
        this.staAgreedLastUpdated = staAgreedLastUpdated;
    }

    public boolean isInspectionStatus() {
        return inspectionStatus;
    }

    public void setInspectionStatus(boolean inspectionStatus) {
        this.inspectionStatus = inspectionStatus;
    }

    public boolean isFlagOrderLine() {
        return flagOrderLine;
    }

    public void setFlagOrderLine(boolean flagOrderLine) {
        this.flagOrderLine = flagOrderLine;
    }
    
    public boolean isFlagProcureLine() {
        return flagProcureLine;
    }

    public void setFlagProcureLine(boolean flagProcureLine) {
        this.flagProcureLine = flagProcureLine;
    }
}
