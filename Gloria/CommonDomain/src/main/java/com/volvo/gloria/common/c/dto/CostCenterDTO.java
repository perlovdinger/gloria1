package com.volvo.gloria.common.c.dto;

import java.io.Serializable;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * Data class for cost center Details.
 */
public class CostCenterDTO implements Serializable, PageResults {

    private static final long serialVersionUID = -8393535565641046841L;
    
    private long id;
    private String costCenter;
    private String descriptionShort;
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getCostCenter() {
        return costCenter;
    }
    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }
    public String getDescriptionShort() {
        return descriptionShort;
    }
    public void setDescriptionShort(String descriptionShort) {
        this.descriptionShort = descriptionShort;
    }
    
}
