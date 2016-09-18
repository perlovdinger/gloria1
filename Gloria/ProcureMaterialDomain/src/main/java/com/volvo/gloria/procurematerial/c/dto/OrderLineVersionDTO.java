/**
 * 
 */
package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;
import java.util.Date;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * 
 */
public class OrderLineVersionDTO implements Serializable, PageResults {

    /**
     * 
     */
    private static final long serialVersionUID = -5211464910139709351L;

    private long id;
    private long version;
    private Date versionDate;
    private String revisionId;
    private String partVersion;
    private Date orderStaDate;
    private long quantity;
    private double unitPrice;
    private String currency;
    private Date staAcceptedDate;
    private Date staAgreedDate;
    private String buyerId;
    private String priceType;
    

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

    public Date getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(Date versionDate) {
        this.versionDate = versionDate;
    }

    public String getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }

    public String getPartVersion() {
        return partVersion;
    }

    public void setPartVersion(String partVersion) {
        this.partVersion = partVersion;
    }

    public Date getOrderStaDate() {
        return orderStaDate;
    }

    public void setOrderStaDate(Date orderStaDate) {
        this.orderStaDate = orderStaDate;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getStaAcceptedDate() {
        return staAcceptedDate;
    }

    public void setStaAcceptedDate(Date staAcceptedDate) {
        this.staAcceptedDate = staAcceptedDate;
    }

    public Date getStaAgreedDate() {
        return staAgreedDate;
    }

    public void setStaAgreedDate(Date staAgreedDate) {
        this.staAgreedDate = staAgreedDate;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

}
