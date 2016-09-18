package com.volvo.gloria.web.uiservices.filter;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * Filter to exclude the empty query parameters.
 * 
 */
public class QueryParameterFilter implements ContainerRequestFilter {

    @Override
    public ContainerRequest filter(ContainerRequest requestContext) {
        final MultivaluedMap<String, String> qParameters = requestContext.getQueryParameters();
        if (!qParameters.keySet().isEmpty()) {
            final URI filteredURI = rebuildURI(requestContext.getRequestUri(), qParameters);
            requestContext.setUris(requestContext.getBaseUri(), filteredURI);
        }
        return requestContext;
    }

    private static URI rebuildURI(final URI uri, final MultivaluedMap<String, String> qParameters) {
        UriBuilder uriBuilder = UriBuilder.fromUri(uri).replaceQuery("");
        for (Map.Entry<String, List<String>> qParam : qParameters.entrySet()) {
            final String key = qParam.getKey();
            for (String qParamValue : qParam.getValue()) {
                if (!StringUtils.isEmpty(qParamValue)) {
                    uriBuilder = uriBuilder.queryParam(key, qParamValue);
                }
            }
        }
        return uriBuilder.build();
    }
}
