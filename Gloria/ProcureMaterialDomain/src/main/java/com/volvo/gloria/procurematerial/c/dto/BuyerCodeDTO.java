package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * BuyerCode DTO class.
 */

public class BuyerCodeDTO implements Serializable, PageResults {

    private static final long serialVersionUID = 8322690024811379104L;

    private long id;
    private long version;
    private String buyerId;
    private String buyerName;
    
    public BuyerCodeDTO() {
    }
    
    public BuyerCodeDTO(long id, String buyerId, String buyerName) {
        this.id = id;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
    }

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

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

}
