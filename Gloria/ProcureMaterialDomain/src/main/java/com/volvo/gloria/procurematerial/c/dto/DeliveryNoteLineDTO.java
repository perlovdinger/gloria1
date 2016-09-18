package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * 
 * DTO for delivery line note.
 * 
 */
public class DeliveryNoteLineDTO implements Serializable, PageResults {

    private static final long serialVersionUID = 5109198311613415884L;

    private long id;
    private long version;
    private String receiveType;
    private String partNumber;
    private String partName;
    private String partVersion;
    private String partAffiliation;
    private String partModification;
    private String partAlias;
    private String status;
    private Date expirationDate;    
    private String supplierId;
    private String supplierName;
    private String orderNo;
    private String freeText;
    private String inspectionLevel;
    private boolean hasMeasuringDoc;
    private boolean hasControlCertificateDoc;
    private boolean hasDamagedParts;
    private boolean hasMissingInfo;
    private boolean hasOverDelivery;
    private String problemDescription;
    private Date deliveryNoteDate;
    private String carrier;
    private String transportationNo;
    private long transportLabelId;
    private boolean hasNotes;
    private boolean receivedDetailsUpdated;
    private boolean qiDetailsUpdated;
    private String procureType;
    private String procureInfo;
    private String unitOfMeasure;
    private boolean dangerousGoodsFlag;
    private String projectId;
    private boolean sendToQI;
    private String qiMarking;
    private String qualityInspectionComment;
    private String referenceId;
    
    private long receivedQuantity;
    private long deliveryNoteQuantity;
    private long damagedQuantity;
    private long possibleToReceiveQuantity;
    private long directSendQuantity;
    
    private long deliveryNoteId;
    
    private String batchNumber;
    private String serialNumber;
    private long orderLineQuantity;
    private long orderLineReceivedQuantity;
    private long orderLineId;
    private long orderLineVersion;
    private long blockedQuantity;
    
    private String qualityDocumentName;
    
    public String getQualityDocumentName() {
        return qualityDocumentName;
    }

    public void setQualityDocumentName(String qualityDocumentName) {
        this.qualityDocumentName = qualityDocumentName;
    }

    private List<DeliveryNoteSubLineDTO> deliveryNoteSubLineDTOs = new ArrayList<DeliveryNoteSubLineDTO>();
    
    public boolean isDangerousGoodsFlag() {
        return dangerousGoodsFlag;
    }

    public void setDangerousGoodsFlag(boolean dangerousGoodsFlag) {
        this.dangerousGoodsFlag = dangerousGoodsFlag;
    }

    public long getTransportLabelId() {
        return transportLabelId;
    }

    public void setTransportLabelId(long transportLabelId) {
        this.transportLabelId = transportLabelId;
    }

    public String getPartModification() {
        return partModification;
    }

    public void setPartModification(String partModification) {
        this.partModification = partModification;
    }

    public String getProcureType() {
        return procureType;
    }

    public void setProcureType(String procureType) {
        this.procureType = procureType;
    }

    public String getProcureInfo() {
        return procureInfo;
    }

    public void setProcureInfo(String procureInfo) {
        this.procureInfo = procureInfo;
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

    public String getPartAffiliation() {
        return partAffiliation;
    }

    public void setPartAffiliation(String partAffiliation) {
        this.partAffiliation = partAffiliation;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getOrderLineId() {
        return orderLineId;
    }

    public void setOrderLineId(long orderLineId) {
        this.orderLineId = orderLineId;
    }

    public long getDeliveryNoteId() {
        return deliveryNoteId;
    }

    public void setDeliveryNoteId(long deliveryNoteId) {
        this.deliveryNoteId = deliveryNoteId;
    }

    public String getPartVersion() {
        return partVersion;
    }

    public void setPartVersion(String partVersion) {
        this.partVersion = partVersion;
    }

    public long getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(long receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public long getDeliveryNoteQuantity() {
        return deliveryNoteQuantity;
    }

    public void setDeliveryNoteQuantity(long deliveryNoteQuantity) {
        this.deliveryNoteQuantity = deliveryNoteQuantity;
    }

    public long getDamagedQuantity() {
        return damagedQuantity;
    }

    public void setDamagedQuantity(long damagedQuantity) {
        this.damagedQuantity = damagedQuantity;
    }

    public long getOrderLineQuantity() {
        return orderLineQuantity;
    }

    public void setOrderLineQuantity(long orderLineQuantity) {
        this.orderLineQuantity = orderLineQuantity;
    }

    public long getOrderLineReceivedQuantity() {
        return orderLineReceivedQuantity;
    }

    public void setOrderLineReceivedQuantity(long orderLineReceivedQuantity) {
        this.orderLineReceivedQuantity = orderLineReceivedQuantity;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    public long getOrderLineVersion() {
        return orderLineVersion;
    }

    public void setOrderLineVersion(long orderLineVersion) {
        this.orderLineVersion = orderLineVersion;
    }

    public String getInspectionLevel() {
        return inspectionLevel;
    }

    public void setInspectionLevel(String inspectionLevel) {
        this.inspectionLevel = inspectionLevel;
    }

    public boolean isHasMeasuringDoc() {
        return hasMeasuringDoc;
    }

    public void setHasMeasuringDoc(boolean hasMeasuringDoc) {
        this.hasMeasuringDoc = hasMeasuringDoc;
    }

    public boolean isHasControlCertificateDoc() {
        return hasControlCertificateDoc;
    }

    public void setHasControlCertificateDoc(boolean hasControlCertificateDoc) {
        this.hasControlCertificateDoc = hasControlCertificateDoc;
    }

    public boolean isHasDamagedParts() {
        return hasDamagedParts;
    }

    public void setHasDamagedParts(boolean hasDamagedParts) {
        this.hasDamagedParts = hasDamagedParts;
    }

    public boolean isHasMissingInfo() {
        return hasMissingInfo;
    }

    public void setHasMissingInfo(boolean hasMissingInfo) {
        this.hasMissingInfo = hasMissingInfo;
    }

    public boolean isHasOverDelivery() {
        return hasOverDelivery;
    }

    public void setHasOverDelivery(boolean hasOverDelivery) {
        this.hasOverDelivery = hasOverDelivery;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }
    
    public Date getDeliveryNoteDate() {
        return deliveryNoteDate;
    }

    public void setDeliveryNoteDate(Date deliveryNoteDate) {
        this.deliveryNoteDate = deliveryNoteDate;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getTransportationNo() {
        return transportationNo;
    }

    public void setTransportationNo(String transportationNo) {
        this.transportationNo = transportationNo;
    }
    
    public boolean isReceivedDetailsUpdated() {
        return receivedDetailsUpdated;
    }

    public void setReceivedDetailsUpdated(boolean receivedDetailsUpdated) {
        this.receivedDetailsUpdated = receivedDetailsUpdated;
    }

    public boolean isQiDetailsUpdated() {
        return qiDetailsUpdated;
    }

    public void setQiDetailsUpdated(boolean qiDetailsUpdated) {
        this.qiDetailsUpdated = qiDetailsUpdated;
    }

    public boolean isHasNotes() {
        return hasNotes;
    }

    public void setHasNotes(boolean hasNotes) {
        this.hasNotes = hasNotes;
    }

    public boolean isSendToQI() {
        return sendToQI;
    }

    public void setSendToQI(boolean sendToQI) {
        this.sendToQI = sendToQI;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getQiMarking() {
        return qiMarking;
    }

    public void setQiMarking(String qiMarking) {
        this.qiMarking = qiMarking;
    }

    public String getPartAlias() {
        return partAlias;
    }

    public void setPartAlias(String partAlias) {
        this.partAlias = partAlias;
    }

    public String getQualityInspectionComment() {
        return qualityInspectionComment;
    }

    public void setQualityInspectionComment(String qualityInspectionComment) {
        this.qualityInspectionComment = qualityInspectionComment;
    }
    
    public String getReceiveType() {
        return receiveType;
    }
    
    public void setReceiveType(String receiveType) {
        this.receiveType = receiveType;
    }

    public long getPossibleToReceiveQuantity() {
        return possibleToReceiveQuantity;
    }

    public void setPossibleToReceiveQuantity(long possibleToReceiveQuantity) {
        this.possibleToReceiveQuantity = possibleToReceiveQuantity;
    }

    public long getDirectSendQuantity() {
        return directSendQuantity;
    }

    public void setDirectSendQuantity(long directSendQuantity) {
        this.directSendQuantity = directSendQuantity;
    }
   
    public String getReferenceId() {
        return referenceId;
    }
    
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public long getBlockedQuantity() {
        return blockedQuantity;
    }

    public void setBlockedQuantity(long blockedQuantity) {
        this.blockedQuantity = blockedQuantity;
    }
    
    public List<DeliveryNoteSubLineDTO> getDeliveryNoteSubLineDTOs() {
        return deliveryNoteSubLineDTOs;
    }
    
    public void setDeliveryNoteSubLineDTOs(List<DeliveryNoteSubLineDTO> deliveryNoteSubLineDTOs) {
        this.deliveryNoteSubLineDTOs = deliveryNoteSubLineDTOs;
    }
}
