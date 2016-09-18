package com.volvo.gloria.common.repositories.b;

import java.util.List;

import com.volvo.gloria.authorization.d.entities.ReportFilter;
import com.volvo.gloria.authorization.d.entities.ReportFilterMaterial;
import com.volvo.gloria.authorization.d.entities.ReportFilterOrder;

/**
 * Repository class ReportFilter.
 * 
 */
public interface ReportFilterRepository {

    ReportFilterMaterial save(ReportFilterMaterial reportFilterMaterial);

    ReportFilterOrder save(ReportFilterOrder reportFilterOrder);
    
    void remove(long reportFilterId);

    ReportFilter findReportFilterById(long id);

    List<ReportFilter> getReportFilters(String reportType, String userId);
}
