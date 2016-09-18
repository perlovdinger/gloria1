/**
 * 
 */
package com.volvo.gloria.common.d.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entity class for Currency Rate.
 */
@Entity
@Table(name = "CURRENCY_RATE")
public class CurrencyRate implements GenericEntity<Long>, Serializable {

    private static final long serialVersionUID = 6482182190148385645L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CURRENCY_RATE_OID")
    private long currencyRateOID;

    @Version
    private long version;
    private String baseCurrencyCode;
    private double rate;
    private long displaySeq;
    private Date startDate;
    private Date endDate;

    @ManyToOne
    private Currency currency;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getCurrencyRateOID() {
        return currencyRateOID;
    }

    public void setCurrencyRateOID(long currencyRateOID) {
        this.currencyRateOID = currencyRateOID;
    }

    public String getBaseCurrencyCode() {
        return baseCurrencyCode;
    }

    public void setBaseCurrencyCode(String baseCurrencyCode) {
        this.baseCurrencyCode = baseCurrencyCode;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public long getDisplaySeq() {
        return displaySeq;
    }

    public void setDisplaySeq(long displaySeq) {
        this.displaySeq = displaySeq;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public Long getId() {
        return currencyRateOID;
    }
}
