package com.volvo.gloria.web.uiservices.mapper;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.volvo.gloria.common.c.dto.reports.ReportFilters;
import com.volvo.gloria.util.paging.c.PageResults;

public class GloriaResponseObjectTypeResolver extends DefaultTypeResolverBuilder {

    private static final long serialVersionUID = -4210567779557838258L;

    public GloriaResponseObjectTypeResolver() {
        super(DefaultTyping.OBJECT_AND_NON_CONCRETE);
    }

    @Override
    public boolean useForType(JavaType t) {
        if (t.getRawClass().isAssignableFrom(PageResults.class)) {
            return true;
        }
        if (t.getRawClass().isAssignableFrom(ReportFilters.class)) {
            return true;
        }
        return false;
    }
}
