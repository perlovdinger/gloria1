package com.volvo.gloria.common.purchaseorder.c.dto;

import java.io.Serializable;

/**
 * data class for purchase order schedule.
 */
public class PurchaseOrderScheduleDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long quantity;
    private String shipToArrive;

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getShipToArrive() {
        return shipToArrive;
    }

    public void setShipToArrive(String shipToArrive) {
        this.shipToArrive = shipToArrive;
    }
}
