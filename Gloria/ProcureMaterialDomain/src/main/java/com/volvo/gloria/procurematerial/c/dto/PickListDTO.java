package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * DTO class for PickList.
 */
public class PickListDTO implements PageResults, Serializable {

    private static final long serialVersionUID = -3898186240148008661L;

    private long id;

    private long version;

    private String code;

    private String status;

    private String pulledByUserId;

    private String zoneId;

    private long items;

    private long totalQty;

    private long pickedQuantity;

    private boolean balanceExceeded;
    
    private boolean shipSkippable;

    public boolean isShipSkippable() {
        return shipSkippable;
    }

    public void setShipSkippable(boolean shipSkippable) {
        this.shipSkippable = shipSkippable;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPulledByUserId() {
        return pulledByUserId;
    }

    public void setPulledByUserId(String pulledByUserId) {
        this.pulledByUserId = pulledByUserId;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public long getItems() {
        return items;
    }

    public void setItems(long items) {
        this.items = items;
    }

    public long getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(long totalQty) {
        this.totalQty = totalQty;
    }

    public long getPickedQuantity() {
        return pickedQuantity;
    }

    public void setPickedQuantity(long pickedQuantity) {
        this.pickedQuantity = pickedQuantity;
    }

    public boolean isBalanceExceeded() {
        return balanceExceeded;
    }

    public void setBalanceExceeded(boolean balanceExceeded) {
        this.balanceExceeded = balanceExceeded;
    }
}
