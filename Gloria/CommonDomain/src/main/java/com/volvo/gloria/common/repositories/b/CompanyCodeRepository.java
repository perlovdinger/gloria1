package com.volvo.gloria.common.repositories.b;

import java.util.List;

import com.volvo.gloria.common.d.entities.CompanyCode;
import com.volvo.gloria.common.d.entities.CompanyGroup;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * repository for root CompanyCode.
 * 
 */
public interface CompanyCodeRepository extends GenericRepository<CompanyCode, Long>  {

    CompanyCode findCompanyCodeByCode(String code);

    List<CompanyCode> getCompanyCodes(List<String> companyCodes, String filterStr);

    CompanyGroup getCompanyGroupByCode(String companyCodeGroup);

    CompanyGroup save(CompanyGroup companyGroup);

    PageObject getCompanyCodeFilters(PageObject pageObject, List<String> companyCodes);

    List<CompanyGroup> getCompanyGroupsByCompanyCodes(List<String> companyCodes);
}
