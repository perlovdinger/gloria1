package com.volvo.gloria.common.c.dto.reports;

import java.io.Serializable;

public class ReportPerformanceDTO implements Serializable, Cloneable  {
    
    private static final long serialVersionUID = 2426424545520754626L;

    private String[] warehouse;
    private String[] suffix;
    private String[] project;
    private String[] buildSeries;
    private String[] buyerId;
    private String[] partNumber;
    private boolean orderNumber;
    
    public ReportPerformanceDTO clone() {
        try {
            return (ReportPerformanceDTO) super.clone();
        } catch (CloneNotSupportedException e) {        
            throw new RuntimeException();
        }
    }

    public String[] getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String[] warehouse) {
        this.warehouse = warehouse;
    }

    public String[] getSuffix() {
        return suffix;
    }

    public void setSuffix(String[] suffix) {
        this.suffix = suffix;
    }

    public String[] getBuildSeries() {
        return buildSeries;
    }

    public void setBuildSeries(String[] buildSeries) {
        this.buildSeries = buildSeries;
    }

    public boolean isOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(boolean orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String[] getProject() {
        return project;
    }

    public void setProject(String[] project) {
        this.project = project;
    }

    public String[] getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String[] partNumber) {
        this.partNumber = partNumber;
    }

    public String[] getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String[] buyerId) {
        this.buyerId = buyerId;
    }
}
