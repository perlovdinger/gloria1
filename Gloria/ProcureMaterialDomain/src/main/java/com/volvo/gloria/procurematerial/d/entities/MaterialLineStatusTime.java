package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity class of materiallinestatustime.
 */
@Entity
@Table(name = "MATERIAL_LINE_STATUS_TIME")
public class MaterialLineStatusTime implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MATERIAL_LINE_STATUS_TIME_OID")
    private long materialLineStatusTimeOID;

    private Timestamp receivedTime;

    private Timestamp storedTime;

    private Timestamp requestTime;

    private Timestamp shippedTime;
    
    private Timestamp pickedTime;
    
    private long requestedQty;
    
    private long pickedQty;
    
    private String pickedStorageRoom;
    
    private String pickedBinLocation;
    
    private long storedQty;
    
    private String storedStorageRoom;
    
    private String storedBinLocation;

    public long getMaterialLineStatusTimeOID() {
        return materialLineStatusTimeOID;
    }

    public void setMaterialLineStatusTimeOID(long materialLineStatusTimeOID) {
        this.materialLineStatusTimeOID = materialLineStatusTimeOID;
    }

    public Timestamp getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(Timestamp receivedTime) {
        this.receivedTime = receivedTime;
    }

    public Timestamp getStoredTime() {
        return storedTime;
    }

    public void setStoredTime(Timestamp storedTime) {
        this.storedTime = storedTime;
    }

    public Timestamp getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Timestamp requestTime) {
        this.requestTime = requestTime;
    }

    public Timestamp getShippedTime() {
        return shippedTime;
    }

    public void setShippedTime(Timestamp shippedTime) {
        this.shippedTime = shippedTime;
    }

    public Timestamp getPickedTime() {
        return pickedTime;
    }

    public void setPickedTime(Timestamp pickedTime) {
        this.pickedTime = pickedTime;
    }

    public long getRequestedQty() {
        return requestedQty;
    }

    public void setRequestedQty(long requestedQty) {
        this.requestedQty = requestedQty;
    }

    public long getPickedQty() {
        return pickedQty;
    }

    public void setPickedQty(long pickedQty) {
        this.pickedQty = pickedQty;
    }

    public String getPickedStorageRoom() {
        return pickedStorageRoom;
    }

    public void setPickedStorageRoom(String pickedStorageRoom) {
        this.pickedStorageRoom = pickedStorageRoom;
    }

    public String getPickedBinLocation() {
        return pickedBinLocation;
    }

    public void setPickedBinLocation(String pickedBinLocation) {
        this.pickedBinLocation = pickedBinLocation;
    }

    public long getStoredQty() {
        return storedQty;
    }

    public void setStoredQty(long storedQty) {
        this.storedQty = storedQty;
    }

    public String getStoredStorageRoom() {
        return storedStorageRoom;
    }

    public void setStoredStorageRoom(String storedStorageRoom) {
        this.storedStorageRoom = storedStorageRoom;
    }

    public String getStoredBinLocation() {
        return storedBinLocation;
    }

    public void setStoredBinLocation(String storedBinLocation) {
        this.storedBinLocation = storedBinLocation;
    }
}
