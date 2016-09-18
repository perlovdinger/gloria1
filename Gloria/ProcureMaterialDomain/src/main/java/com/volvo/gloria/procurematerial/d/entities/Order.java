package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.procurematerial.d.type.internalexternal.InternalExternal;
import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entity Class for Order.
 */
@Entity
@Table(name = "ORDERS")
public class Order implements GenericEntity<Long>, Serializable{

    private static final long serialVersionUID = 1424590574479479667L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_OID")
    private long orderOID;

    @Version
    private long version;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "order")
    private List<OrderLine> orderLines = new ArrayList<OrderLine>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "orders")
    private List<OrderLog> orderLog = new ArrayList<OrderLog>();

    @Enumerated(EnumType.STRING)
    private InternalExternal internalExternal;
    private String orderNo;
    private String orderIdGps;
    private Date orderDateTime;
    private String orderMode;
    private String materialUserId;
    private String materialUserName;
    private String materialUserCategory;
    private String supplierId;
    private String supplierName;
    private String supplierCategory;
    private String deliveryControllerTeam;
    private String suffix;
    private String shipToId;
    private boolean migrated;
    private String companyCode;

    public long getOrderOID() {
        return orderOID;
    }

    public void setOrderOID(long orderOID) {
        this.orderOID = orderOID;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public List<OrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<OrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    public List<OrderLog> getOrderLog() {
        return orderLog;
    }

    public void setOrderLog(List<OrderLog> orderLog) {
        this.orderLog = orderLog;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Date getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(Date orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public String getOrderIdGps() {
        return orderIdGps;
    }

    public void setOrderIdGps(String orderIdGps) {
        this.orderIdGps = orderIdGps;
    }

    public String getOrderMode() {
        return orderMode;
    }

    public void setOrderMode(String orderMode) {
        this.orderMode = orderMode;
    }

    public String getMaterialUserId() {
        return materialUserId;
    }

    public void setMaterialUserId(String materialUserId) {
        this.materialUserId = materialUserId;
    }

    public String getMaterialUserName() {
        return materialUserName;
    }

    public void setMaterialUserName(String materialUserName) {
        this.materialUserName = materialUserName;
    }

    public String getMaterialUserCategory() {
        return materialUserCategory;
    }

    public void setMaterialUserCategory(String materialUserCategory) {
        this.materialUserCategory = materialUserCategory;
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

    public String getSupplierCategory() {
        return supplierCategory;
    }

    public void setSupplierCategory(String supplierCategory) {
        this.supplierCategory = supplierCategory;
    }

    public String getDeliveryControllerTeam() {
        return deliveryControllerTeam;
    }

    public void setDeliveryControllerTeam(String deliveryControllerTeam) {
        this.deliveryControllerTeam = deliveryControllerTeam;
    }

    public InternalExternal getInternalExternal() {
        return internalExternal;
    }

    public void setInternalExternal(InternalExternal internalExternal) {
        this.internalExternal = internalExternal;
    }
    
    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getShipToId() {
        return shipToId;
    }

    public void setShipToId(String shipToId) {
        this.shipToId = shipToId;
    }
    
    public boolean isMigrated() {
        return migrated;
    }

    public void setMigrated(boolean migrated) {
        this.migrated = migrated;
    }
    
    public String getCompanyCode() {
        return companyCode;
    }
    
    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    @Override
    public Long getId() {
        return orderOID;
    }
}
