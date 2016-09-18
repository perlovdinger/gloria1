package com.volvo.gloria.web.uiservices;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.b.SecurityServices;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Restful service for handling manual user login.
 * 
 * This service should never be built for production environment!
 */
@Path("/usertest")
public class UserUITestService {
    @Context
    private HttpServletRequest request;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserUITestService.class);
    private UserServices userServices = ServiceLocator.getService(UserServices.class);
    private SecurityServices securityServices = ServiceLocator.getService(SecurityServices.class);
   
    @GET
    @Path("/v1/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@QueryParam("userId") String userId) throws GloriaApplicationException {
        try {
            LOGGER.info("login called with userId=" + userId);
            securityServices.setAuthentication(userId, userServices.getUserRoleIds(userId), request);
        } catch (GloriaApplicationException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        return Response.ok(userServices.getUser(userId)).build();
    }

}
