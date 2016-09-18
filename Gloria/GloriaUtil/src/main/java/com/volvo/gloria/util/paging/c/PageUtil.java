package com.volvo.gloria.util.paging.c;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * Pagination helper utility.
 * 
 */
public class PageUtil {

    protected PageUtil() {
    }

    public static List<PageResults> updatePage(PageObject pageContent, HttpServletResponse response) {
        response.setHeader("X-Result-Counter", String.valueOf(pageContent.getCount()));
        response.setHeader("X-Page-No", String.valueOf(pageContent.getCurrentPage()));
        return pageContent.getGridContents();
    }
}
