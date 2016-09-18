package com.volvo.gloria.common.repositories.b;

import java.util.Collection;
import java.util.List;

import com.volvo.gloria.common.d.entities.Site;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * Repository for Site.
 */
public interface SiteRepository extends GenericRepository<Site, Long> {
    
    List<Site> getSiteBySiteIds(List<String> siteIds);
    
    Site getSiteBySiteId(String siteId);

    List<Site> getAllBuildSites(PageObject pageObject, String siteId);

    List<Site> getAllsparePartSites();

    Site getSiteBySiteCode(String siteCode);

    List<String> getSiteIdsByCompanyCodes(Collection<String> companyCodeCodes);

}
