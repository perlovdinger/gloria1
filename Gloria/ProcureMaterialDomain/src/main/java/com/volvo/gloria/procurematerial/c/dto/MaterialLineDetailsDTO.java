package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;
import java.util.Date;

import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.util.paging.c.PageResults;

/**
 * Data transfer class for MaterialLine Details.
 */
public class MaterialLineDetailsDTO implements Serializable, PageResults {

    private static final long serialVersionUID = 1L;

    private MaterialLineStatus status;
    private long quantity;
    private String assignTo;
    private Date receivedDate;
    private String barcode;

    public MaterialLineStatus getStatus() {
        return status;
    }

    public void setStatus(MaterialLineStatus status) {
        this.status = status;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public String getAssignTo() {
        return assignTo;
    }

    public void setAssignTo(String assignTo) {
        this.assignTo = assignTo;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

}
