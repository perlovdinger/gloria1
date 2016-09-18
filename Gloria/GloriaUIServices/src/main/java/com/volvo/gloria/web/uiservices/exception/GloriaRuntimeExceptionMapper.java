package com.volvo.gloria.web.uiservices.exception;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.transaction.UnexpectedRollbackException;

import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaExceptionLogger;
import com.volvo.gloria.util.GloriaSystemException;

/**
 * Jersey exception mapper implementation for <code>RuntimeSystemException</code>, registered with the JAX-RS runtime. Sets HTTP 500 (Internal Server Error)
 * status code for unrecovareble exceptions.
 */
@Provider
public class GloriaRuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {
    @Override
    public Response toResponse(RuntimeException exception) {
        if (exception instanceof org.springframework.security.access.AccessDeniedException) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else if (exception instanceof PersistenceException) {
            String exceptionMessage = exception.getMessage();
            if (exceptionMessage == null) {
                // This happens when the user is not logged in, thrown by PreAuthorize
                return Response.status(Response.Status.UNAUTHORIZED).build();
            } else {
                GloriaExceptionLogger.log(exception, GloriaRuntimeExceptionMapper.class);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        } else if (exception instanceof ConstraintViolationException) {
            GloriaApplicationException applicationException = getGloriaApplicationException((ConstraintViolationException) exception);
            return Response.status(Status.BAD_REQUEST).entity(applicationException.toString()).type(MediaType.APPLICATION_JSON).build();
        } else if (exception instanceof UnexpectedRollbackException && exception.getCause() != null
                && exception.getCause().getCause() instanceof ConstraintViolationException) {
            // For merge operation or some cases ConstraintViolationException is wrapped under rollback exception!
            GloriaApplicationException applicationException = getGloriaApplicationException((ConstraintViolationException) exception.getCause().getCause());
            return Response.status(Status.BAD_REQUEST).entity(applicationException.toString()).type(MediaType.APPLICATION_JSON).build();
        } else {
            GloriaSystemException gloriaSystemException = new GloriaSystemException(exception, exception.getMessage());
            GloriaExceptionLogger.log(exception, GloriaRuntimeExceptionMapper.class);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(gloriaSystemException.toJson()).type(MediaType.APPLICATION_JSON).build();
        }
    }

    private GloriaApplicationException getGloriaApplicationException(ConstraintViolationException exception) {
        ConstraintViolation<?> constraintViolation = exception.getConstraintViolations().iterator().next();
        String errorCode = constraintViolation.getMessage();
        String attribute = constraintViolation.getPropertyPath().toString();
        Map<String, Object> attributeMap = constraintViolation.getConstraintDescriptor().getAttributes();
        Map<String, Object> msgParam = new HashMap<String, Object>();

        for (String key : attributeMap.keySet()) {
            if (!"message".equals(key) && !"payload".equals(key) && !"groups".equals(key)) {
                msgParam.put(key, attributeMap.get(key));
            }
        }
        return new GloriaApplicationException(attribute, errorCode, "", msgParam);
    }

}
