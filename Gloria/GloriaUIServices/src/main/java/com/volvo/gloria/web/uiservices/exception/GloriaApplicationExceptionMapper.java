package com.volvo.gloria.web.uiservices.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Jersey exception mapper implementation for <code>GloriaApplicationException</code>,
 *  registered with the JAX-RS runtime. Sets HTTP 400 (Bad Request) status code for.
 * recovareble exceptions.
 */
@Provider
public class GloriaApplicationExceptionMapper implements ExceptionMapper<GloriaApplicationException> {

    @Override
    public Response toResponse(GloriaApplicationException exception) {
        return Response.status(Status.BAD_REQUEST).entity(exception.toString()).type(MediaType.APPLICATION_JSON).build();
    }

}
