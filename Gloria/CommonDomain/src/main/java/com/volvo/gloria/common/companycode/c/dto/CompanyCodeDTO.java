package com.volvo.gloria.common.companycode.c.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.volvo.gloria.common.d.entities.Currency;
import com.volvo.gloria.util.paging.c.PageResults;

/**
 * A DTO implementation for Company Codes.
 */
public class CompanyCodeDTO implements Serializable, PageResults {

    private static final long serialVersionUID = 6532788482160497541L;

    private long id;
    private long version;
    private String code;
    private String name;
    private boolean receivingGoods;
    private boolean sendPOtoSAP;
    private boolean sendGRtoSAP;
    private String sapPurchaseOrg;
    private String sapQuantityBlockReceiverId;
    private String defaultCurrency;
    private String companyGroupCode;
    private List<Currency> currencies = new ArrayList<Currency>();

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSendPOtoSAP() {
        return sendPOtoSAP;
    }

    public void setSendPOtoSAP(boolean sendPOtoSAP) {
        this.sendPOtoSAP = sendPOtoSAP;
    }

    public boolean isSendGRtoSAP() {
        return sendGRtoSAP;
    }

    public void setSendGRtoSAP(boolean sendGRtoSAP) {
        this.sendGRtoSAP = sendGRtoSAP;
    }

    public String getSapPurchaseOrg() {
        return sapPurchaseOrg;
    }

    public void setSapPurchaseOrg(String sapPurchaseOrg) {
        this.sapPurchaseOrg = sapPurchaseOrg;
    }

    public String getSapQuantityBlockReceiverId() {
        return sapQuantityBlockReceiverId;
    }

    public void setSapQuantityBlockReceiverId(String sapQuantityBlockReceiverId) {
        this.sapQuantityBlockReceiverId = sapQuantityBlockReceiverId;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public boolean isReceivingGoods() {
        return receivingGoods;
    }

    public void setReceivingGoods(boolean receivingGoods) {
        this.receivingGoods = receivingGoods;
    }

    public String getCompanyGroupCode() {
        return companyGroupCode;
    }

    public void setCompanyGroupCode(String companyGroupCode) {
        this.companyGroupCode = companyGroupCode;
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
    }
}
