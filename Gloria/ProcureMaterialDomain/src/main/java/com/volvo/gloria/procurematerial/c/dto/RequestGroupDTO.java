package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;
import java.util.Date;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * Data class for RequestGroup details.
 */

public class RequestGroupDTO implements Serializable, PageResults {

    private static final long serialVersionUID = 3218450021683045961L;

    private long id;
    private long version;
    private String status;
    private String zoneId;
    private String items;
    private long totalQuantity;
    private long priority;
    private String projectId;
    private String referenceGroup;
    private String referenceId;
    private String changeRequestIds;
    private Date requiredDeliveryDate;
    private String deliveryAddressName;
    private String deliveryAddressId;
    private String requesterUserId;
    private String requesterUserName;
    private String pickListCode;
    private String pulledByUserId;
    private Date createdDate;
    private long pickListId;
    private long requestListID;
    private String reservedUserId;
    
    private String shipmentType;
    private String directSendPartNo;
    private String directSendPartVersion;
    private String directSendPartAffiliation;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
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

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getChangeRequestIds() {
        return changeRequestIds;
    }

    public void setChangeRequestIds(String changeRequestIds) {
        this.changeRequestIds = changeRequestIds;
    }

    public Date getRequiredDeliveryDate() {
        return requiredDeliveryDate;
    }

    public void setRequiredDeliveryDate(Date requiredDeliveryDate) {
        this.requiredDeliveryDate = requiredDeliveryDate;
    }

    public String getDeliveryAddressName() {
        return deliveryAddressName;
    }

    public void setDeliveryAddressName(String deliveryAddressName) {
        this.deliveryAddressName = deliveryAddressName;
    }

    public String getRequesterUserId() {
        return requesterUserId;
    }

    public void setRequesterUserId(String requesterUserId) {
        this.requesterUserId = requesterUserId;
    }

    public String getRequesterUserName() {
        return requesterUserName;
    }

    public void setRequesterUserName(String requesterUserName) {
        this.requesterUserName = requesterUserName;
    }

    public String getPickListCode() {
        return pickListCode;
    }

    public void setPickListCode(String pickListCode) {
        this.pickListCode = pickListCode;
    }

    public String getPulledByUserId() {
        return pulledByUserId;
    }

    public void setPulledByUserId(String pulledByUserId) {
        this.pulledByUserId = pulledByUserId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(String deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }

    public long getPickListId() {
        return pickListId;
    }

    public void setPickListId(long pickListId) {
        this.pickListId = pickListId;
    }

    public long getRequestListID() {
        return requestListID;
    }

    public void setRequestListID(long requestListID) {
        this.requestListID = requestListID;
    }

    public String getReservedUserId() {
        return reservedUserId;
    }

    public void setReservedUserId(String reservedUserId) {
        this.reservedUserId = reservedUserId;
    }
    
    public String getShipmentType() {
        return shipmentType;
    }
    
    public void setShipmentType(String shipmentType) {
        this.shipmentType = shipmentType;
    }

    public String getDirectSendPartNo() {
        return directSendPartNo;
    }

    public void setDirectSendPartNo(String directSendPartNo) {
        this.directSendPartNo = directSendPartNo;
    }

    public String getDirectSendPartVersion() {
        return directSendPartVersion;
    }

    public void setDirectSendPartVersion(String directSendPartVersion) {
        this.directSendPartVersion = directSendPartVersion;
    }

    public String getDirectSendPartAffiliation() {
        return directSendPartAffiliation;
    }

    public void setDirectSendPartAffiliation(String directSendPartAffiliation) {
        this.directSendPartAffiliation = directSendPartAffiliation;
    }
}
