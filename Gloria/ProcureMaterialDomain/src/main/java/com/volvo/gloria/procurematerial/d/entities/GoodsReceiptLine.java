package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.volvo.gloria.procurematerial.d.entities.status.goodsReceiptLine.GoodsReceiptLineStatus;

/**
 * 
 */

@Entity
@Table(name = "GOODS_RECEIPT_LINE")
public class GoodsReceiptLine implements Serializable {

    private static final long serialVersionUID = 165065908140468237L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GOODS_RECEIPT_LINE_OID")
    private long goodsReceiptLineOId;

    @Version
    private long version;
    @Enumerated(EnumType.STRING)
    private GoodsReceiptLineStatus status;
    private String plant;
    private String movementType;
    private String vendor;
    private String vendorMaterialNumber;
    private String orderReference;
    private long quantity;
    private long quantityCancelled;
    private String isoUnitOfMeasure;
    private String movementIndicator;
    
    @ManyToOne
    @JoinColumn(name = "GOODS_RECEIPT_HEADER_OID")
    private GoodsReceiptHeader goodsReceiptHeader;

    @ManyToOne
    @JoinColumn(name = "DELIVERY_NOTE_LINE_OID")
    private DeliveryNoteLine deliveryNoteLine;

    @Transient
    private boolean isCancelable;
    
    public long getGoodsReceiptLineOId() {
        return goodsReceiptLineOId;
    }

    public void setGoodsReceiptLineOId(long goodsReceiptLineOId) {
        this.goodsReceiptLineOId = goodsReceiptLineOId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
    
    public GoodsReceiptLineStatus getStatus() {
        return status;
    }
    
    public void setStatus(GoodsReceiptLineStatus status) {
        this.status = status;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public String getVendorMaterialNumber() {
        return vendorMaterialNumber;
    }

    public void setVendorMaterialNumber(String vendorMaterialNumber) {
        this.vendorMaterialNumber = vendorMaterialNumber;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
    
    public long getQuantityCancelled() {
        return quantityCancelled;
    }
    
    public void setQuantityCancelled(long quantityCancelled) {
        this.quantityCancelled = quantityCancelled;
    }

    public String getIsoUnitOfMeasure() {
        return isoUnitOfMeasure;
    }

    public void setIsoUnitOfMeasure(String isoUnitOfMeasure) {
        this.isoUnitOfMeasure = isoUnitOfMeasure;
    }

    public DeliveryNoteLine getDeliveryNoteLine() {
        return deliveryNoteLine;
    }

    public void setDeliveryNoteLine(DeliveryNoteLine deliveryNoteLine) {
        this.deliveryNoteLine = deliveryNoteLine;
    }

    public GoodsReceiptHeader getGoodsReceiptHeader() {
        return goodsReceiptHeader;
    }

    public void setGoodsReceiptHeader(GoodsReceiptHeader goodsReceiptHeader) {
        this.goodsReceiptHeader = goodsReceiptHeader;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getMovementIndicator() {
        return movementIndicator;
    }

    public void setMovementIndicator(String movementIndicator) {
        this.movementIndicator = movementIndicator;
    }
    
    public boolean isCancelable() {
        return isCancelable;
    }
    
    public void setCancelable(boolean isCancelable) {
        this.isCancelable = isCancelable;
    }
}
