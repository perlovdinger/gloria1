package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;
import java.util.Date;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * Data class for RequestListEntity.
 */
public class RequestListDTO implements Serializable, PageResults {

    private static final long serialVersionUID = -6975768894756837925L;

    private long id;
    private long version;
    
    private String deliveryAddressName;


    private String deliveryAddressId;
    private String whSiteId;
    private Date requiredDeliveryDate;
    private Long priority;
    private String requestUserId;
    private String requesterName;
    private long dispatchNoteId;
    private long dispatchNoteVersion;
    private String dispatchNoteNumber;
    private String carrier;
    private String trackingNo;
    private String status;
    private Date createdDate;
    private String deliveryAddressType;
    private String shipVia;
    private String whSiteName;
    private String deliveryAddress;
    private String whSiteAddress;
    private String shipmentType;
    private boolean cancelAllowed;
    private String materialDirectSendType;

    public String getWhSiteName() {
        return whSiteName;
    }

    public void setWhSiteName(String whSiteName) {
        this.whSiteName = whSiteName;
    }

    public String getShipVia() {
        return shipVia;
    }

    public void setShipVia(String shipVia) {
        this.shipVia = shipVia;
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
    
    public String getDeliveryAddressName() {
        return deliveryAddressName;
    }

    public void setDeliveryAddressName(String deliveryAddressName) {
        this.deliveryAddressName = deliveryAddressName;
    }

    public String getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(String deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }

    public String getWhSiteId() {
        return whSiteId;
    }

    public void setWhSiteId(String whSiteId) {
        this.whSiteId = whSiteId;
    }

    public Date getRequiredDeliveryDate() {
        return requiredDeliveryDate;
    }

    public void setRequiredDeliveryDate(Date requiredDeliveryDate) {
        this.requiredDeliveryDate = requiredDeliveryDate;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public String getRequestUserId() {
        return requestUserId;
    }

    public void setRequestUserId(String requestUserId) {
        this.requestUserId = requestUserId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public long getDispatchNoteId() {
        return dispatchNoteId;
    }

    public void setDispatchNoteId(long dispatchNoteId) {
        this.dispatchNoteId = dispatchNoteId;
    }

    public long getDispatchNoteVersion() {
        return dispatchNoteVersion;
    }

    public void setDispatchNoteVersion(long dispatchNoteVersion) {
        this.dispatchNoteVersion = dispatchNoteVersion;
    }

    public String getDispatchNoteNumber() {
        return dispatchNoteNumber;
    }

    public void setDispatchNoteNumber(String dispatchNoteNumber) {
        this.dispatchNoteNumber = dispatchNoteNumber;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getDeliveryAddressType() {
        return deliveryAddressType;
    }

    public void setDeliveryAddressType(String deliveryAddressType) {
        this.deliveryAddressType = deliveryAddressType;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getWhSiteAddress() {
        return whSiteAddress;
    }

    public void setWhSiteAddress(String whSiteAddress) {
        this.whSiteAddress = whSiteAddress;
    }
    
    public String getShipmentType() {
        return shipmentType;
    }
    
    public void setShipmentType(String shipmentType) {
        this.shipmentType = shipmentType;
    }
    
    public boolean isCancelAllowed() {
        return cancelAllowed;
    }
    
    public void setCancelAllowed(boolean cancelAllowed) {
        this.cancelAllowed = cancelAllowed;
    }
    
    public void setMaterialDirectSendType(String materialDirectSendType) {
        this.materialDirectSendType = materialDirectSendType;
    }
    
    public String getMaterialDirectSendType() {
        return materialDirectSendType;
    }
}
