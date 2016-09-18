package com.volvo.gloria.warehouse.c.dto;

import java.io.Serializable;

public class PrinterTransformerDTO implements Serializable {

    private static final long serialVersionUID = 378425228317812997L;

    private String siteId;
    private String name;
    private String hostAddress;

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

}
