package com.volvo.gloria.common.c.dto.reports;

import java.io.Serializable;

public class ReportWarehouseActionDTO implements ReportFilters, Serializable , Cloneable {

    private static final long serialVersionUID = 8922383571772624260L;
    private String[] warehouse;
    private String[] project;
    private boolean numberOfReceivals = true;
    private boolean numberOfPulls = true;
    private boolean numberOfReturns = true;
    private boolean numberOfShipments = true;
    
    public String[] getProject() {
        return project;
    }
    public void setProject(String[] project) {
        this.project = project;
    }
    public String[] getWarehouse() {
        return warehouse;
    }
    public void setWarehouse(String[] warehouse) {
        this.warehouse = warehouse;
    }
    public boolean isNumberOfReceivals() {
        return numberOfReceivals;
    }
    public void setNumberOfReceivals(boolean numberOfReceivals) {
        this.numberOfReceivals = numberOfReceivals;
    }
    public boolean isNumberOfReturns() {
        return numberOfReturns;
    }
    public void setNumberOfReturns(boolean numberOfReturns) {
        this.numberOfReturns = numberOfReturns;
    }
    public boolean isNumberOfShipments() {
        return numberOfShipments;
    }
    public void setNumberOfShipments(boolean numberOfShipments) {
        this.numberOfShipments = numberOfShipments;
    }
    public boolean isNumberOfPulls() {
        return numberOfPulls;
    }
    public void setNumberOfPulls(boolean numberOfPulls) {
        this.numberOfPulls = numberOfPulls;
    }
    
    public ReportWarehouseActionDTO clone() {
        try {
            return (ReportWarehouseActionDTO) super.clone();
        } catch (CloneNotSupportedException e) {        
            throw new RuntimeException();
        }
    }
}
