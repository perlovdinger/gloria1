package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Entity Class for Order Line Last Modified.
 */
@Entity
@Table(name = "ORDER_LINE_LAST_MODIFIED")
public class OrderLineLastModified implements Serializable {

    private static final long serialVersionUID = -7102968317644298017L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_LINE_LAST_MODIFIED_OID")
    private long orderLineLastModifiedOID;

    @Version
    private long version;

    private String modifiedByUserId;
    private Date modifiedTime;
    private boolean alertQuantity;
    private boolean alertOrderStaDate;
    private boolean alertPartVersion;

    public long getOrderLineLastModifiedOID() {
        return orderLineLastModifiedOID;
    }

    public void setOrderLineLastModifiedOID(long orderLineLastModifiedOID) {
        this.orderLineLastModifiedOID = orderLineLastModifiedOID;
    }

    public String getModifiedByUserId() {
        return modifiedByUserId;
    }

    public void setModifiedByUserId(String modifiedByUserId) {
        this.modifiedByUserId = modifiedByUserId;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public boolean isAlertQuantity() {
        return alertQuantity;
    }

    public void setAlertQuantity(boolean alertQuantity) {
        this.alertQuantity = alertQuantity;
    }

    public boolean isAlertOrderStaDate() {
        return alertOrderStaDate;
    }

    public void setAlertOrderStaDate(boolean alertOrderStaDate) {
        this.alertOrderStaDate = alertOrderStaDate;
    }

    public boolean isAlertPartVersion() {
        return alertPartVersion;
    }

    public void setAlertPartVersion(boolean alertPartVersion) {
        this.alertPartVersion = alertPartVersion;
    }

    public long getVersion() {
        return version;
    }
}
