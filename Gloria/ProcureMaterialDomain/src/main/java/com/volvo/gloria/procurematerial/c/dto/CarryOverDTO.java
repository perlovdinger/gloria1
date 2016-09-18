package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;

/**
 * 
 */
public class CarryOverDTO implements Serializable {

    private static final long serialVersionUID = -8126083941599721534L;

    private long carryOverOid;
    private long version;
    private String customerId;
    private String customerName;
    private String partNumber;
    private String partVersion;

    public long getCarryOverOid() {
        return carryOverOid;
    }

    public void setCarryOverOid(long carryOverOid) {
        this.carryOverOid = carryOverOid;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartVersion() {
        return partVersion;
    }

    public void setPartVersion(String partVersion) {
        this.partVersion = partVersion;
    }

}
