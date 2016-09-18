package com.volvo.gloria.common.c.dto;

import com.volvo.gloria.util.tracing.c.TraceabilityDTO;

public class MaterialLineTracebilityDTO extends TraceabilityDTO {

    private static final long serialVersionUID = -2260849390330047188L;

    private long materialOID;
    private String orderNo;
    private String mlStatus;
    private long mlQuantity;

    public long getMaterialOID() {
        return materialOID;
    }

    public void setMaterialOID(long materialOID) {
        this.materialOID = materialOID;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getMlStatus() {
        return mlStatus;
    }

    public void setMlStatus(String mlStatus) {
        this.mlStatus = mlStatus;
    }

    public long getMlQuantity() {
        return mlQuantity;
    }

    public void setMlQuantity(long mlQuantity) {
        this.mlQuantity = mlQuantity;
    }

}
