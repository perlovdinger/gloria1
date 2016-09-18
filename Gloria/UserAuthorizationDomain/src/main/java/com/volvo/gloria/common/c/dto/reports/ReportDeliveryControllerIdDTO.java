package com.volvo.gloria.common.c.dto.reports;

import java.io.Serializable;

import com.volvo.gloria.util.c.dto.reports.ReportFilterDTO;
import com.volvo.gloria.util.paging.c.PageResults;

/**
 * DTO class for Delivery Controller Id report filter.
 * 
 */
public class ReportDeliveryControllerIdDTO extends ReportFilterDTO implements Serializable, PageResults {

    private static final long serialVersionUID = 4396231968663646023L;

    private String deliveryControllerName;

    public String getDeliveryControllerName() {
        return deliveryControllerName;
    }

    public void setDeliveryControllerName(String deliveryControllerName) {
        this.deliveryControllerName = deliveryControllerName;
    }
}
