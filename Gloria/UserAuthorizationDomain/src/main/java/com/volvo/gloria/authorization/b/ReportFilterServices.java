package com.volvo.gloria.authorization.b;

import java.util.List;

import com.volvo.gloria.authorization.d.entities.ReportFilter;
import com.volvo.gloria.authorization.d.entities.ReportFilterMaterial;
import com.volvo.gloria.authorization.d.entities.ReportFilterOrder;
import com.volvo.gloria.common.c.dto.reports.ReportFilterMaterialDTO;
import com.volvo.gloria.common.c.dto.reports.ReportFilterOrderDTO;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * 
 * services for Report support.
 * 
 */
public interface ReportFilterServices {

    List<ReportFilter> getReportFilters(String reportType, String userId);

    ReportFilter getReportFilterById(long id);

    void removeReportFilter(long reportFilterId) throws GloriaApplicationException;

    ReportFilterOrder createOrderReportFilter(ReportFilterOrderDTO reportFilterOrderDTO, String userId) throws GloriaApplicationException;

    ReportFilterMaterial createMaterialReportFilter(ReportFilterMaterialDTO reportFilterMaterialDTO, String userId) throws GloriaApplicationException;
}
