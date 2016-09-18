package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * DTO class for material lines for Quality Inspection.
 * 
 */
public class MaterialLineQiDTO implements Serializable, PageResults {

    private static final long serialVersionUID = -3953730182840104720L;

    private long id;
    private long version;
    private String materialLineIds;
    private String pPartNumber;
    private String pPartVersion;
    private String pPartName;
    private String pPartModification;
    private long quantity;
    private long approvedQty;
    private String suggestedBinLocation;
    private String binLocation;
    private long transportLabelId;
    private long deliveryNoteLineId;
    private long deliveryNoteLineVersion;
    private boolean hasDetails;
    private boolean markedForInspection;
    private String qiMarking;
    private String materialOrderNo;
    private String projectId;
    private String storageRoomCode;
    private String referenceId;
    private String contactPersonId;
    private String contactPersonName;
    private String inspectionStatus;

    public MaterialLineQiDTO(long id, String pPartNumber, String pPartVersion, String pPartName, String pPartModification, Long quantity) {
        this.id = id;
        this.pPartNumber = pPartNumber;
        this.pPartVersion = pPartVersion;
        this.pPartName = pPartName;
        this.pPartModification = pPartModification;
        this.quantity = quantity;
    }

    public MaterialLineQiDTO() {
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMaterialLineIds() {
        return materialLineIds;
    }

    public void setMaterialLineIds(String materialLineIds) {
        this.materialLineIds = materialLineIds;
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

    public String getpPartModification() {
        return pPartModification;
    }

    public void setpPartModification(String pPartModification) {
        this.pPartModification = pPartModification;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getApprovedQty() {
        return approvedQty;
    }

    public void setApprovedQty(long approvedQty) {
        this.approvedQty = approvedQty;
    }

    public String getSuggestedBinLocation() {
        return suggestedBinLocation;
    }

    public void setSuggestedBinLocation(String suggestedBinLocation) {
        this.suggestedBinLocation = suggestedBinLocation;
    }

    public String getBinLocation() {
        return binLocation;
    }

    public void setBinLocation(String binLocation) {
        this.binLocation = binLocation;
    }

    public long getTransportLabelId() {
        return transportLabelId;
    }

    public void setTransportLabelId(long transportLabelId) {
        this.transportLabelId = transportLabelId;
    }

    public long getDeliveryNoteLineId() {
        return deliveryNoteLineId;
    }

    public void setDeliveryNoteLineId(long deliveryNoteLineId) {
        this.deliveryNoteLineId = deliveryNoteLineId;
    }

    public long getDeliveryNoteLineVersion() {
        return deliveryNoteLineVersion;
    }

    public void setDeliveryNoteLineVersion(long deliveryNoteLineVersion) {
        this.deliveryNoteLineVersion = deliveryNoteLineVersion;
    }

    public boolean isHasDetails() {
        return hasDetails;
    }

    public void setHasDetails(boolean hasDetails) {
        this.hasDetails = hasDetails;
    }

    public boolean isMarkedForInspection() {
        return markedForInspection;
    }

    public void setMarkedForInspection(boolean markedForInspection) {
        this.markedForInspection = markedForInspection;
    }

    public String getQiMarking() {
        return qiMarking;
    }
    
    public void setQiMarking(String qiMarking) {
        this.qiMarking = qiMarking;
    }

    public String getMaterialOrderNo() {
        return materialOrderNo;
    }

    public void setMaterialOrderNo(String materialOrderNo) {
        this.materialOrderNo = materialOrderNo;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getStorageRoomCode() {
        return storageRoomCode;
    }

    public void setStorageRoomCode(String storageRoomCode) {
        this.storageRoomCode = storageRoomCode;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
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

    public String getInspectionStatus() {
        return inspectionStatus;
    }

    public void setInspectionStatus(String inspectionStatus) {
        this.inspectionStatus = inspectionStatus;
    }    
}
