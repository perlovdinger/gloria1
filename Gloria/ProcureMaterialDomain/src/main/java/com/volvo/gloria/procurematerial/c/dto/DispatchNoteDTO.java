package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 */
public class DispatchNoteDTO implements Serializable {

    private static final long serialVersionUID = 3148989737015757700L;

    private long id;
    private long version;
    private String dispatchNoteNo;
    private Date dispatchNoteDate;
    private Date deliveryDate;
    private String transportMode;
    private String weight;
    private String height;
    private String carrier;
    private String trackingNo;
    private String note;
    private String requestListStatus;
    private long requestListOId;
    private String shipVia;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDispatchNoteNo() {
        return dispatchNoteNo;
    }

    public void setDispatchNoteNo(String dispatchNoteNo) {
        this.dispatchNoteNo = dispatchNoteNo;
    }

    public Date getDispatchNoteDate() {
        return dispatchNoteDate;
    }

    public void setDispatchNoteDate(Date dispatchNoteDate) {
        this.dispatchNoteDate = dispatchNoteDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getRequestListStatus() {
        return requestListStatus;
    }

    public void setRequestListStatus(String requestListStatus) {
        this.requestListStatus = requestListStatus;
    }

    public long getRequestListOId() {
        return requestListOId;
    }

    public void setRequestListOId(long requestListOId) {
        this.requestListOId = requestListOId;
    }

    public String getShipVia() {
        return shipVia;
    }

    public void setShipVia(String shipVia) {
        this.shipVia = shipVia;
    }

}
