package com.volvo.gloria.common.repositories.b;


import java.util.Date;
import java.util.Set;

import com.volvo.gloria.common.d.entities.CostCenter;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * repository for root CostCenter.
 * 
 */
public interface CostCenterRepository extends GenericRepository<CostCenter, Long> {

    PageObject findCostCentersByCompanyCode(String companyCode, String searchString, PageObject pageObject);

    CostCenter findCostCenterByCompanyCodeAndStartDate(String companyCode, String costCenter, Date effictiveStartDate);

    void deleteCostCentersByCompanyCode(String companyCode);

    Set<String> filterInvalidCostCenters(Set<String> costCenterList, String companyCode);

}
