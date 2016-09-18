package com.volvo.gloria.procurematerial.c.dto;

import java.util.Date;

/**
 * DTO class for MaterialBatch.
 */
public class MaterialBatchDTO {

    private String batchNumber;
    private String serialNumber;
    private Date expirationDate;
    
    public String getBatchNumber() {
        return batchNumber;
    }
    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }
    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    public Date getExpirationDate() {
        return expirationDate;
    }
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
    
    
}
