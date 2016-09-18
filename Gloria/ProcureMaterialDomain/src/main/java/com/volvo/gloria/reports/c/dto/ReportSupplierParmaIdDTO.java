package com.volvo.gloria.reports.c.dto;

import java.io.Serializable;

import com.volvo.gloria.util.c.dto.reports.ReportFilterDTO;
import com.volvo.gloria.util.paging.c.PageResults;

/**
 * DTO class for SupplierparmaId report filter.
 * 
 */
public class ReportSupplierParmaIdDTO extends ReportFilterDTO implements Serializable, PageResults {

    private static final long serialVersionUID = 2697159553179212157L;

    private String supplierName;
    
    public ReportSupplierParmaIdDTO() {
     
    }
    
    public ReportSupplierParmaIdDTO(String id, String text, String supplierName) {     
        super(id, text);
        this.supplierName = supplierName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
}
