package com.volvo.gloria.common.repositories.b;

import java.util.List;
import java.util.Set;

import com.volvo.gloria.common.c.dto.ProjectDTO;
import com.volvo.gloria.common.d.entities.WbsElement;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * repository for root WbsElement.
 * 
 */
public interface WbsElementRepository extends GenericRepository<WbsElement, Long> {

    PageObject findWbsElementsByCompanyCodeAndProjectId(String companyCode, String shortId, String code, PageObject pageObject);

    WbsElement findWbsElementByCompanyCodeAndProjectId(String companyCode, String projectId, String wbsCode);

    List<ProjectDTO> getProjectsByCompanyCode(PageObject pageObject, List<String> companyCodes, String projectID);

    List<WbsElement> findWbsElementByCompanyCode(String companyCode);

    Set<String> filterInvalidWbsCodes(Set<String> wbsList, String companyCode);

    PageObject getProjects(PageObject pageObject, List<String> companyCodes);

    PageObject getWbsElements(PageObject pageObject, List<String> companyCodes);
}
