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

import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entity Class for ORDER_SAP.
 */
@Entity
@Table(name = "ORDER_SAP")
public class OrderSap implements GenericEntity<Long>, Serializable{

    private static final long serialVersionUID = -3643152088703632626L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_SAP_OID")
    private long orderSapOID;
    
    @Version
    private long version;
    
    private String companyCode;
    private String orderType;
    private String vendor;
    private String purchaseOrganization;
    private String purchaseGroup;
    private Date documentDate;
    private String currency;
    private String purchaseType;
    private String uniqueExtOrder;
    private boolean suppressDecimal;   
    @Enumerated(EnumType.STRING)
    private MessageStatus messageStatus = MessageStatus.NO_SEND;
    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "orderSap")
    private List<OrderSapLine> orderSapLines = new ArrayList<OrderSapLine>();
    
    public long getOrderSapOID() {
        return orderSapOID;
    }

    public void setOrderSapOID(long orderSapOID) {
        this.orderSapOID = orderSapOID;
    }

    public long getVersion() {
        return version;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getPurchaseOrganization() {
        return purchaseOrganization;
    }

    public void setPurchaseOrganization(String purchaseOrganization) {
        this.purchaseOrganization = purchaseOrganization;
    }

    public String getPurchaseGroup() {
        return purchaseGroup;
    }

    public void setPurchaseGroup(String purchaseGroup) {
        this.purchaseGroup = purchaseGroup;
    }

    public Date getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(Date documentDate) {
        this.documentDate = documentDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

    public String getUniqueExtOrder() {
        return uniqueExtOrder;
    }

    public void setUniqueExtOrder(String uniqueExtOrder) {
        this.uniqueExtOrder = uniqueExtOrder;
    }

    public List<OrderSapLine> getOrderSapLines() {
        return orderSapLines;
    }

    public void setOrderSapLines(List<OrderSapLine> orderSapLines) {
        this.orderSapLines = orderSapLines;
    }
    
    public boolean isSuppressDecimal() {
        return suppressDecimal;
    }

    public void setSuppressDecimal(boolean suppressDecimal) {
        this.suppressDecimal = suppressDecimal;
    }

    @Override
    public Long getId() {
        return orderSapOID;
    }
    
    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

}
