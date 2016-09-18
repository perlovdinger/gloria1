package com.volvo.gloria.web.uiservices.mapper;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.api.core.ExtendedUriInfo;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

/**
 * This provider class maps multiple query parameters to <code>QueryParameter</code>.
 */
@Provider
public final class QueryParameterBeanMapper implements InjectableProvider<QueryParam, Parameter> {

    @Context
    private final HttpContext httpContext;

    public QueryParameterBeanMapper(@Context HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable<RsGridParameters> getInjectable(ComponentContext componentContext, final QueryParam queryParam, final Parameter parameter) {
        if (RsGridParameters.class != parameter.getParameterClass()) {
            return null;
        }

        return new Injectable<RsGridParameters>() {
            public RsGridParameters getValue() {
                ExtendedUriInfo uriInfo = httpContext.getUriInfo();
                return new RsGridParameters(uriInfo.getQueryParameters());

            }
        };
    }
}
