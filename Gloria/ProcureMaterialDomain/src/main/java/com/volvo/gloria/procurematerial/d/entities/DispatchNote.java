package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

/**
 * Entity class of dispatchNote.
 */
@Entity
@Table(name = "DISPATCH_NOTE")
public class DispatchNote implements Serializable {

    private static final long serialVersionUID = 2212425031466215813L;

    private static final int INITIAL_VALUE_1000 = 1000;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISPATCH_NOTE_OID")
    private long dispatchNoteOID;

    @Version
    private long version;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "REQUEST_LIST_OID")
    private RequestList requestList;
    
    @TableGenerator(name = "SEQUENCE_DISPATCHNOTE", table = "SEQUENCE", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", 
            pkColumnValue = "DISPATCHNOTE_SEQUENCE", allocationSize = 1, initialValue = INITIAL_VALUE_1000)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENCE_DISPATCHNOTE")
    private Long dispatchNoteNoSequence;
    
    private String dispatchNoteNo;
    private Date dispatchNoteDate;
    private Date deliveryDate;
    private String transportMode;
    private String weight;
    private String height;
    private String carrier;
    private String trackingNo;
    private String note;
    
    public long getDispatchNoteOID() {
        return dispatchNoteOID;
    }

    public void setDispatchNoteOID(long dispatchNoteOID) {
        this.dispatchNoteOID = dispatchNoteOID;
    }
    
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public RequestList getRequestList() {
        return requestList;
    }

    public void setRequestList(RequestList requestList) {
        this.requestList = requestList;
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
    
    public Long getDispatchNoteNoSequence() {
        return dispatchNoteNoSequence;
    }
    
    public void setDispatchNoteNoSequence(Long dispatchNoteNoSequence) {
        this.dispatchNoteNoSequence = dispatchNoteNoSequence;
    }
}
