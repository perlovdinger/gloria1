package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.volvo.gloria.procurematerial.d.entities.status.materialLine.InspectionStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.Utils;
import com.volvo.gloria.util.validators.GloriaLongValue;
import com.volvo.gloria.warehouse.c.ZoneType;

/**
 * Entity class of materialline.
 */
@Entity
@Table(name = "MATERIAL_LINE")
public class MaterialLine implements Serializable {

    private static final long serialVersionUID = 3248208902209612300L;
    private static final int MAX_QUANTITY = 99999;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MATERIAL_LINE_OID")
    private long materialLineOID;

    @Version
    private long version;

    @ManyToOne
    @JoinColumn(name = "MATERIAL_OID")
    private Material material;

    @ManyToOne
    @JoinColumn(name = "DELIVERY_NOTE_LINE_OID")
    private DeliveryNoteLine deliveryNoteLine;

    @ManyToOne
    @JoinColumn(name = "REQUEST_GROUP_OID")
    private RequestGroup requestGroup;

    @ManyToOne
    @JoinColumn(name = "PICK_LIST_OID")
    private PickList pickList;
    
    @OneToOne(cascade = { CascadeType.ALL} , orphanRemoval = true)
    @JoinColumn(name = "MATERIAL_LINE_STATUS_TIME_OID")
    private MaterialLineStatusTime materialLineStatusTime = new MaterialLineStatusTime();

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
    @JoinColumn(name = "MATERIAL_OWNER_OID")
    private Material materialOwner;

    @Enumerated(EnumType.STRING)
    private MaterialLineStatus status;

    private Date statusDate;
    private Date expirationDate;
    @GloriaLongValue(max = MAX_QUANTITY)
    private Long quantity = 0L;
    private Date receivedDate;
    private Long placementOID;
    private String storageRoomCode;
    private String storageRoomName;
    private String zoneCode;
    private String zoneName;
    private ZoneType zoneType;
    private String binLocationCode;
    private String barCode;
    private boolean requestedExcluded;
    private ProcureType procureType;
    private String reservedUserId;
    @Temporal(TemporalType.TIMESTAMP)
    private Date reservedTimeStamp;
    private String finalWhSiteId;
    private String whSiteId;
    @Enumerated(EnumType.STRING)
    private DirectSendType directSend;
    private boolean isOrderCancelled;
    private boolean borrowed;
    private boolean released;
    private String statusUserId;
    private String statusUserName;
    @Enumerated(EnumType.STRING)
    private InspectionStatus inspectionStatus;
    @Enumerated(EnumType.STRING)
    private MaterialLineStatus previousStatus;
   
    // field added to reduce the complexity of TO_STORE data query
    private String transportLabelCode;

    private String orderNo;

    @Transient
    private String suggestedBinLocation;

    @Transient
    private Double stockBalance;
    
    //field for actionDetail
    @Transient 
    private String confirmationText;

    public long getMaterialLineOID() {
        return materialLineOID;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setMaterialLineOID(long materialLineOID) {
        this.materialLineOID = materialLineOID;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public MaterialLineStatus getStatus() {
        return status;
    }

    public void setStatus(MaterialLineStatus status) {
        if (this.status != null && !this.status.equals(status)) {
            this.previousStatus = this.status;
        }
        this.status = status;
        if (this.status != null) {
            this.status.updateWithStatusTime(this);
        }
    }
    
    public Material getMaterialOwner() {
        return materialOwner;
    }

    public void setMaterialOwner(Material materialOwner) {
        this.materialOwner = materialOwner;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) throws GloriaApplicationException {
        Utils.validatePostiveNumberLong(quantity);
        this.quantity = quantity;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Long getPlacementOID() {
        return placementOID;
    }

    public void setPlacementOID(Long placementOID) {
        this.placementOID = placementOID;
    }

    public DeliveryNoteLine getDeliveryNoteLine() {
        return deliveryNoteLine;
    }

    public void setDeliveryNoteLine(DeliveryNoteLine deliveryNoteLine) {
        this.deliveryNoteLine = deliveryNoteLine;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public RequestGroup getRequestGroup() {
        return requestGroup;
    }

    public void setRequestGroup(RequestGroup requestGroup) {
        this.requestGroup = requestGroup;
    }

    public PickList getPickList() {
        return pickList;
    }

    public void setPickList(PickList pickList) {
        this.pickList = pickList;
    }
    
    public MaterialLineStatusTime getMaterialLineStatusTime() {
        return materialLineStatusTime;
    }
    
    public void setMaterialLineStatusTime(MaterialLineStatusTime materialLineStatusTime) {
        this.materialLineStatusTime = materialLineStatusTime;
    }

    public String getSuggestedBinLocation() {
        return suggestedBinLocation;
    }

    public void setSuggestedBinLocation(String suggestedBinLocation) {
        this.suggestedBinLocation = suggestedBinLocation;
    }

    public Double getStockBalance() {
        return stockBalance;
    }

    public void setStockBalance(Double stockBalance) {
        this.stockBalance = stockBalance;
    }

    public String getBinLocationCode() {
        return binLocationCode;
    }

    public void setBinLocationCode(String binLocationCode) {
        this.binLocationCode = binLocationCode;
    }

    public String getStorageRoomCode() {
        return storageRoomCode;
    }

    public void setStorageRoomCode(String storageRoomCode) {
        this.storageRoomCode = storageRoomCode;
    }

    public String getStorageRoomName() {
        return storageRoomName;
    }

    public void setStorageRoomName(String storageRoomName) {
        this.storageRoomName = storageRoomName;
    }

    public String getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public ZoneType getZoneType() {
        return zoneType;
    }

    public void setZoneType(ZoneType zoneType) {
        this.zoneType = zoneType;
    }

    public boolean isRequestedExcluded() {
        return requestedExcluded;
    }

    public void setRequestedExcluded(boolean requestedExcluded) {
        this.requestedExcluded = requestedExcluded;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public ProcureType getProcureType() {
        return procureType;
    }

    public void setProcureType(ProcureType procureType) {
        this.procureType = procureType;
    }

    public String getReservedUserId() {
        return reservedUserId;
    }

    public void setReservedUserId(String reservedUserId) {
        this.reservedUserId = reservedUserId;
    }

    public Date getReservedTimeStamp() {
        return reservedTimeStamp;
    }

    public void setReservedTimeStamp(Date reservedTimeStamp) {
        this.reservedTimeStamp = reservedTimeStamp;
    }

    public String getFinalWhSiteId() {
        return finalWhSiteId;
    }

    public void setFinalWhSiteId(String finalWhSiteId) {
        this.finalWhSiteId = finalWhSiteId;
    }

    public String getWhSiteId() {
        return whSiteId;
    }

    public void setWhSiteId(String whSiteId) {
        this.whSiteId = whSiteId;
    }

    public DirectSendType getDirectSend() {
        return directSend;
    }

    public void setDirectSend(DirectSendType directSend) {
        this.directSend = directSend;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public boolean isOrderCancelled() {
        return isOrderCancelled;
    }

    public void setOrderCancelled(boolean isOrderCancelled) {
        this.isOrderCancelled = isOrderCancelled;
    }
    
    public String getTransportLabelCode() {
        return transportLabelCode;
    }
    
    public void setTransportLabelCode(String transportLabelCode) {
        this.transportLabelCode = transportLabelCode;
    }
    
    public String getGroupIdentifierKey() {

        String key = "projectId=" + material.getFinanceHeader().getProjectId();

        MaterialHeader materialHeader = material.getMaterialHeader();
        if (materialHeader != null) {
            key += ",referenceGroup=" + materialHeader.getAccepted().getReferenceGroup() + ",referenceId=" + materialHeader.getReferenceId() + ",phase="
                    + material.getMaterialHeader().getBuildId() + /*",changeRequestId=" + material.getMtrlRequestVersionAccepted() +*/ ",Zone=" + getZoneCode();
        }
        return key;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    public boolean isReleased() {
        return released;
    }

    public void setReleased(boolean released) {
        this.released = released;
    }

    public String getConfirmationText() {
        return confirmationText;
    }

    public void setConfirmationText(String confirmationText) {
        this.confirmationText = confirmationText;
    }

    public String getStatusUserName() {
        return statusUserName;
    }

    public void setStatusUserName(String statusUserName) {
        this.statusUserName = statusUserName;
    }

    public String getStatusUserId() {
        return statusUserId;
    }

    public void setStatusUserId(String statusUserId) {
        this.statusUserId = statusUserId;
    }
    
    public InspectionStatus getInspectionStatus() {
        return inspectionStatus;
    }

    public void setInspectionStatus(InspectionStatus inspectionStatus) {
        this.inspectionStatus = inspectionStatus;
    }

    public MaterialLineStatus getPreviousStatus() {
        return previousStatus;
    }
    
    public void setPreviousStatus(MaterialLineStatus previousStatus) {
        this.previousStatus = previousStatus;
    }
}
