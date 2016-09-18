package com.volvo.gloria.common.c.dto.reports;

import java.io.Serializable;

/**
 * 
 * DTO class for Warehouse Cost report.
 */
public class ReportWarehouseCostDTO implements Serializable {

    private static final long serialVersionUID = 9100305307173776765L;

    private String[] project;
    private String[] warehouse;
    private boolean scrappedPartValue = true;
    private boolean inventoryAdjustmentValue = true;

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

    public boolean isScrappedPartValue() {
        return scrappedPartValue;
    }

    public void setScrappedPartValue(boolean scrappedPartValue) {
        this.scrappedPartValue = scrappedPartValue;
    }

    public boolean isInventoryAdjustmentValue() {
        return inventoryAdjustmentValue;
    }

    public void setInventoryAdjustmentValue(boolean inventoryAdjustmentValue) {
        this.inventoryAdjustmentValue = inventoryAdjustmentValue;
    }
}
