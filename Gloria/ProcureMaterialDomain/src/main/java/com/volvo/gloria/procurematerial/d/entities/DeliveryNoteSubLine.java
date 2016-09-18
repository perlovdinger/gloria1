package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

@Entity
@Table(name = "DELIVERY_NOTE_SUB_LINE")
public class DeliveryNoteSubLine implements Serializable {

    private static final long serialVersionUID = 7620316925364321762L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DELIVERY_NOTE_SUB_LINE_OID")
    private long deliveryNoteSubLineOID;
    
    @Version
    private long version;

    @ManyToOne
    @JoinColumn(name = "DELIVERY_NOTE_LINE_OID")
    private DeliveryNoteLine deliveryNoteLine;
    
    @ManyToOne
    @JoinColumn(name = "TRANSPORT_LABEL")
    private TransportLabel transportLabel;
    
    private boolean directSend;

    private long toReceiveQty;
    
    private Long toApproveQty;
    
    private long binLocation;
    
    private String binLocationCode;
    
    @Transient
    private String nextZoneCode;
    
    

    public long getDeliveryNoteSubLineOID() {
        return deliveryNoteSubLineOID;
    }

    public void setDeliveryNoteSubLineOID(long deliveryNoteSubLineOID) {
        this.deliveryNoteSubLineOID = deliveryNoteSubLineOID;
    }
    
    public long getVersion() {
        return version;
    }
    
    public void setVersion(long version) {
        this.version = version;
    }
    
    public DeliveryNoteLine getDeliveryNoteLine() {
        return deliveryNoteLine;
    }
    
    public void setDeliveryNoteLine(DeliveryNoteLine deliveryNoteLine) {
        this.deliveryNoteLine = deliveryNoteLine;
    }

    public boolean isDirectSend() {
        return directSend;
    }

    public void setDirectSend(boolean directSend) {
        this.directSend = directSend;
    }

    public long getToReceiveQty() {
        return toReceiveQty;
    }

    public void setToReceiveQty(long toReceiveQty) {
        this.toReceiveQty = toReceiveQty;
    }
    
    public TransportLabel getTransportLabel() {
        return transportLabel;
    }
    
    public void setTransportLabel(TransportLabel transportLabel) {
        this.transportLabel = transportLabel;
    }
    
    public String getNextZoneCode() {
        return nextZoneCode;
    }
    
    public void setNextZoneCode(String nextZoneCode) {
        this.nextZoneCode = nextZoneCode;
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

    public boolean isStore() {
        if (this.binLocation > 0) {
            return true;
        }
        return false;
    }

    public String getBinLocationCode() {
        return binLocationCode;
    }

    public void setBinLocationCode(String binLocationCode) {
        this.binLocationCode = binLocationCode;
    }
}
