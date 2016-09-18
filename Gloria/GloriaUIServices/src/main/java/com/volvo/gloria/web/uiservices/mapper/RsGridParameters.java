package com.volvo.gloria.web.uiservices.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import com.volvo.gloria.util.paging.c.PageObject;

/**
 * Contains query parameters.
 */
public class RsGridParameters {

    public static final String RESULTS_PER_PAGE = "per_page";
    public static final String RESULTS_SORT_BY = "sort_by";
    public static final String RESULTS_ORDER = "order";
    public static final String CURRENT_PAGE = "page";
    public static final String TOTAL_PAGES = "total_pages";
    public static final String TOTAL_ENTRIES = "total_entries";

    private MultivaluedMap<String, String> queryParameters;

    public RsGridParameters(MultivaluedMap<String, String> queryParameters) {
        this.queryParameters = queryParameters;
    }

    private String getParam(String key) {
        if (queryParameters.containsKey(key)) {
            List<String> params = queryParameters.get(key);
            if (params.size() == 1) {
                return queryParameters.get(key).get(0);
            }
        }
        return null;
    }

    private Map<String, String> getPredicatesAsMap() {
        Map<String, String> predicates = new HashMap<String, String>();
        Set<String> keys = queryParameters.keySet();
        for (String key : keys) {
            if (!key.equalsIgnoreCase(RESULTS_PER_PAGE) && !key.equalsIgnoreCase(CURRENT_PAGE) && !key.equalsIgnoreCase(RESULTS_SORT_BY)
                    && !key.equalsIgnoreCase(RESULTS_ORDER) && !key.equalsIgnoreCase(TOTAL_ENTRIES) && !key.equalsIgnoreCase(TOTAL_PAGES)) {
                predicates.put(key, getParam(key));
            }
        }
        return predicates;
    }

    public PageObject getPageObject() {
        PageObject pageObject = new PageObject();
        if (getParam(RESULTS_PER_PAGE) != null) {
            pageObject.setResultsPerPage(Integer.valueOf(getParam(RESULTS_PER_PAGE)));
        }
        if (getParam(CURRENT_PAGE) != null) {
            pageObject.setCurrentPage(Integer.valueOf(getParam(CURRENT_PAGE)));
        }
        pageObject.setSortBy(getParam(RESULTS_SORT_BY));
        pageObject.setSortOrder(getParam(RESULTS_ORDER));
        pageObject.setPredicates(getPredicatesAsMap());
        return pageObject;
    }
}
