package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.procurematerial.d.type.receive.ReceiveType;
import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entity class for delivery note.
 */
@Entity
@Table(name = "DELIVERY_NOTE")
public class DeliveryNote implements GenericEntity<Long>, Serializable {

    private static final long serialVersionUID = 6987424355011515933L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DELIVERY_NOTE_OID")
    private long deliveryNoteOID;

    @Version
    private long version;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "deliveryNote")
    private List<DeliveryNoteLine> deliveryNoteLine = new ArrayList<DeliveryNoteLine>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "deliveryNote")
    private List<GoodsReceiptHeader> goodsReceiptHeader = new ArrayList<GoodsReceiptHeader>();

    private ReceiveType receiveType;
    private String deliveryNoteNo;
    private Date deliveryNoteDate;
    private String supplierId;
    private String supplierName;
    private String transportationNo;
    private String carrier;
    private String whSiteId;
    private String orderNo;
    private String materialUserId;
    private String returnDeliveryAddressId;
    private String returnDeliveryAddressName;

    public long getDeliveryNoteOID() {
        return deliveryNoteOID;
    }

    public void setDeliveryNoteOID(long deliveryNoteOID) {
        this.deliveryNoteOID = deliveryNoteOID;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public List<DeliveryNoteLine> getDeliveryNoteLine() {
        return deliveryNoteLine;
    }

    public void setDeliveryNoteLine(List<DeliveryNoteLine> deliveryNoteLine) {
        this.deliveryNoteLine = deliveryNoteLine;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getWhSiteId() {
        return whSiteId;
    }

    public void setWhSiteId(String whSiteId) {
        this.whSiteId = whSiteId;
    }

    public String getDeliveryNoteNo() {
        return deliveryNoteNo;
    }

    public void setDeliveryNoteNo(String deliveryNoteNo) {
        this.deliveryNoteNo = deliveryNoteNo;
    }

    public Date getDeliveryNoteDate() {
        return deliveryNoteDate;
    }

    public void setDeliveryNoteDate(Date deliveryNoteDate) {
        this.deliveryNoteDate = deliveryNoteDate;
    }

    public String getTransportationNo() {
        return transportationNo;
    }

    public void setTransportationNo(String transportationNo) {
        this.transportationNo = transportationNo;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    @Override
    public Long getId() {
        return deliveryNoteOID;
    }

    public List<GoodsReceiptHeader> getGoodsReceiptHeader() {
        return goodsReceiptHeader;
    }

    public void setGoodsReceiptHeader(List<GoodsReceiptHeader> goodsReceiptHeader) {
        this.goodsReceiptHeader = goodsReceiptHeader;
    }

    public String getMaterialUserId() {
        return materialUserId;
    }

    public void setMaterialUserId(String materialUserId) {
        this.materialUserId = materialUserId;
    }

    public ReceiveType getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(ReceiveType receiveType) {
        this.receiveType = receiveType;
    }

    public String getReturnDeliveryAddressId() {
        return returnDeliveryAddressId;
    }

    public void setReturnDeliveryAddressId(String returnDeliveryAddressId) {
        this.returnDeliveryAddressId = returnDeliveryAddressId;
    }

    public String getReturnDeliveryAddressName() {
        return returnDeliveryAddressName;
    }

    public void setReturnDeliveryAddressName(String returnDeliveryAddressName) {
        this.returnDeliveryAddressName = returnDeliveryAddressName;
    }
}
