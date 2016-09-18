package com.volvo.gloria.common.c.dto.reports;

import java.io.Serializable;

public class ReportGeneralDTO  implements Serializable, Cloneable  {
    
    private static final long serialVersionUID = -2483760891364697982L;
    
    private String[] warehouse;
    private String[] project;
    private boolean numberOfStoredParts;
    private boolean totalStock;
    private boolean numberOfParts;

    public ReportGeneralDTO clone() {
        try {
            return (ReportGeneralDTO) super.clone();
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

    public String[] getProject() {
        return project;
    }

    public void setProject(String[] project) {
        this.project = project;
    }

    public boolean isNumberOfStoredParts() {
        return numberOfStoredParts;
    }

    public void setNumberOfStoredParts(boolean numberOfStoredParts) {
        this.numberOfStoredParts = numberOfStoredParts;
    }

    public boolean isTotalStock() {
        return totalStock;
    }

    public void setTotalStock(boolean totalStock) {
        this.totalStock = totalStock;
    }

    public boolean isNumberOfParts() {
        return numberOfParts;
    }

    public void setNumberOfParts(boolean numberOfParts) {
        this.numberOfParts = numberOfParts;
    }
}
