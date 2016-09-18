package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;

/**
 * 
 * DTO for delivery sub line note.
 * 
 */
public class DeliveryNoteSubLineDTO implements Serializable {

    private static final long serialVersionUID = 7817098333881743270L;

    private long id;

    private long version;

    private long toReceiveQty;

    private boolean directSend;

    private long transportLabelId;
    
    private String transportLabelCode;

    private String nextZoneCode;

    private String nextZoneType;

    private Long toApproveQty;

    private long binLocation;
    
    private String binLocationCode;

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

    public long getToReceiveQty() {
        return toReceiveQty;
    }

    public void setToReceiveQty(long toReceiveQty) {
        this.toReceiveQty = toReceiveQty;
    }

    public boolean isDirectSend() {
        return directSend;
    }

    public void setDirectSend(boolean directSend) {
        this.directSend = directSend;
    }

    public long getTransportLabelId() {
        return transportLabelId;
    }

    public void setTransportLabelId(long transportLabelId) {
        this.transportLabelId = transportLabelId;
    }

    public String getTransportLabelCode() {
        return transportLabelCode;
    }
    
    public void setTransportLabelCode(String transportLabelCode) {
        this.transportLabelCode = transportLabelCode;
    }
    
    public String getNextZoneCode() {
        return nextZoneCode;
    }

    public void setNextZoneCode(String nextZoneCode) {
        this.nextZoneCode = nextZoneCode;
    }

    public String getNextZoneType() {
        return nextZoneType;
    }

    public void setNextZoneType(String nextZoneType) {
        this.nextZoneType = nextZoneType;
    }

    public Long getToApproveQty() {
        return toApproveQty;
    }

    public void setToApproveQty(Long toApproveQty) {
        this.toApproveQty = toApproveQty;
    }

    public long getBinLocation() {
        return binLocation;
    }

    public void setBinLocation(long binLocation) {
        this.binLocation = binLocation;
    }

    public String getBinLocationCode() {
        return binLocationCode;
    }

    public void setBinLocationCode(String binLocationCode) {
        this.binLocationCode = binLocationCode;
    }
}
