package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.procurematerial.c.PriceType;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.validators.GloriaLongValue;

/**
 * Entity Class for Order Line version.
 */
@Entity
@Table(name = "ORDER_LINE_VERSION")
public class OrderLineVersion implements Serializable {

    private static final long serialVersionUID = -414380542735036138L;
    private static final int MAX_QUANTITY = 99999;

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_LINE_VERSION_OID")
    private long orderLineVersionOid;

    @Version
    private long version;

    private Date versionDate;    
    private String revisionId;
    private String partVersion;
    private Date orderStaDate;
    
    @GloriaLongValue(max = MAX_QUANTITY)
    private long quantity;
    private double unitPrice;
    private String currency;
    private PriceType priceType;
    private Date staAcceptedDate;
    private Date staAgreedDate;
    private long versionNo;
    private String buyerId;
    private String buyerSecurityId;
    private String buyerName;
    private String buyerEmail;
    private long perQuantity;
    private Boolean orderStaDateOnTime;
    private Boolean staAgreedDateOnTime;
    private Date staAgreedLastUpdated;
    
    // currency converted value in EURO, added for report scalability while querying
    private double unitPriceInEuro;

    @ManyToOne
    @JoinColumn(name = "ORDER_LINE_OID")
    private OrderLine orderLine;

    public long getOrderLineVersionOid() {
        return orderLineVersionOid;
    }

    public void setOrderLineVersionOid(long orderLineVersionOid) {
        this.orderLineVersionOid = orderLineVersionOid;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getPartVersion() {
        return partVersion;
    }

    public void setPartVersion(String partVersion) {
        this.partVersion = partVersion;
    }

    public String getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
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

    public OrderLine getOrderLine() {
        return orderLine;
    }

    public void setOrderLine(OrderLine orderLine) {
        this.orderLine = orderLine;
    }
    
    public Date getVersionDate() {
        return versionDate;
    }
    
    public void setVersionDate(Date versionDate) {
        this.versionDate = versionDate;
    }
    
    public double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public PriceType getPriceType() {
        return priceType;
    }
    
    
    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    @PrePersist
    protected void updateVersionDate() {
        this.setVersionDate(DateUtil.getCurrentUTCDateTime());
    }

    public long getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(long versionNo) {
        this.versionNo = versionNo;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerSecurityId() {
        return buyerSecurityId;
    }

    public void setBuyerSecurityId(String buyerSecurityId) {
        this.buyerSecurityId = buyerSecurityId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public long getPerQuantity() {
        return perQuantity;
    }

    public void setPerQuantity(long perQuantity) {
        this.perQuantity = perQuantity;
    }

    public Boolean isOrderStaDateOnTime() {
        return orderStaDateOnTime;
    }

    public void setOrderStaDateOnTime(Boolean orderStaDateOnTime) {
        this.orderStaDateOnTime = orderStaDateOnTime;
    }

    public Boolean isStaAgreedDateOnTime() {
        return staAgreedDateOnTime;
    }

    public void setStaAgreedDateOnTime(Boolean staAgreedDateOnTime) {
        this.staAgreedDateOnTime = staAgreedDateOnTime;
    }

    public double getUnitPriceInEuro() {
        return unitPriceInEuro;
    }
    
    public void setUnitPriceInEuro(double unitPriceInEuro) {
        this.unitPriceInEuro = unitPriceInEuro;
    }

    public Date getStaAgreedLastUpdated() {
        return staAgreedLastUpdated;
    }

    public void setStaAgreedLastUpdated(Date staAgreedLastUpdated) {
        this.staAgreedLastUpdated = staAgreedLastUpdated;
    }
}
