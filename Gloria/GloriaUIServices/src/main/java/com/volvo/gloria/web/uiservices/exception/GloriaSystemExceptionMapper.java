package com.volvo.gloria.web.uiservices.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.volvo.gloria.util.GloriaExceptionLogger;
import com.volvo.gloria.util.GloriaSystemException;

/**
 * Jersey exception mapper implementation for <code>GloriaSystemException</code>, registered with the JAX-RS runtime. Sets HTTP 500 (Internal Server Error)
 * status code for unrecovareble exceptions.
 */
@Provider
public class GloriaSystemExceptionMapper implements ExceptionMapper<GloriaSystemException> {

    @Override
    public Response toResponse(GloriaSystemException gloriaSystemException) {
        GloriaExceptionLogger.log(gloriaSystemException, GloriaSystemExceptionMapper.class);
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(gloriaSystemException.toJson()).type(MediaType.APPLICATION_JSON).build();
    }

}
