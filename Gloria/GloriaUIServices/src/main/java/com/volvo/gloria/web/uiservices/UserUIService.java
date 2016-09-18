package com.volvo.gloria.web.uiservices;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.authorization.util.UserHelper;
import com.volvo.gloria.common.c.dto.RoleDTO;
import com.volvo.gloria.common.c.dto.TeamDTO;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.b.SecurityServices;
import com.volvo.gloria.util.b.UtilServices;
import com.volvo.gloria.util.c.dto.LDAPUserDTO;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.paging.c.PageUtil;
import com.volvo.gloria.web.uiservices.mapper.RsGridParameters;
import com.volvo.gloria.web.w.CheckEnvironment;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * 
 * Restful service for handling User.
 */
@Path("/user")
public class UserUIService {
    
    private static final String RS_QUERY_PARAMETERS = "rsQueryParameters";
    
    @Context
    private HttpServletRequest request;
    
    @Context
    private HttpServletResponse response;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserUIService.class);

    private UtilServices utilServices = ServiceLocator.getService(UtilServices.class);
    private UserServices userServices = ServiceLocator.getService(UserServices.class);
    private SecurityServices securityServices = ServiceLocator.getService(SecurityServices.class);
    private MaterialServices materialServices = ServiceLocator.getService(MaterialServices.class);
    
 

    @GET
    @Path("/v1/ldapusers/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public LDAPUserDTO getLdapUser(@PathParam("userId") String userId) throws GloriaApplicationException {
        return utilServices.getLDAPUserData(userId);
    }

    @GET
    @Path("/v1/teams")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TeamDTO> getAllInternalProcureTeams(@QueryParam("type") String type) {
        return UserHelper.transformToTeamDTOs(userServices.getTeams(type));
    }

    @GET
    @Path("/v1/teams/{team}/users")
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserDTO> getAllUsersForTeam(@PathParam("team") String team, @QueryParam("type") String type, 
                                            @QueryParam("userCategory") String userCategory) throws GloriaApplicationException {
        return userServices.findUserByTeam(team, type, userCategory);
    }  
    
    @GET
    @Path("/v1/users/{userId}/teams")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TeamDTO> getUserTeams(@PathParam("userId") String userId, @QueryParam("type") String type, @QueryParam("companyCode") String companyCode)
            throws GloriaApplicationException {
        return UserHelper.transformToTeamDTOs(userServices.getUserTeams(userId, type, companyCode));
    }
    
    @PUT
    @Path("/v1/users/{userId}/teams")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TeamDTO> updateUserTeams(@PathParam("userId") String userId, @QueryParam("type") String type, List<TeamDTO> teamDTOs)
            throws GloriaApplicationException {
        return UserHelper.transformToTeamDTOs(userServices.updateUserTeams(userId, type, teamDTOs));
    }
    
    @GET
    @Path("/v1/users/current")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLoggedinUser() throws GloriaApplicationException {
        List<UserDTO> users = new ArrayList<UserDTO>();
        String userId = securityServices.getLoggedInUserId(request);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        UserDTO userDTO = userServices.getUser(userId);
        users.add(userDTO);
        return Response.ok(users).build();
    }

    @GET
    @Path("/v1/users")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getUsers(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters, @QueryParam("userCategoryID") String userCategoryID,
            @QueryParam("teamType") String type) {
        return PageUtil.updatePage(userServices.getTeamUsers(rsGridParameters.getPageObject(), type, userCategoryID), response);
    }

    @GET
    @Path("/v1/users/{userId}/roles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserRoles(@PathParam("userId") String userId) throws GloriaApplicationException {
        if (!isValidLoggedinUserId(userId)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        List<RoleDTO> roles =   userServices.getUserRoles(userId);
        return Response.ok(roles).build();
    }

    @GET
    @Path("/v1/users/{userId}/sites")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserSites(@PathParam("userId") String userId) throws GloriaApplicationException {
        if (!isValidLoggedinUserId(userId)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        return Response.ok(userServices.getUserSites(userId)).build();
    }

    @GET
    @Path("/v1/users/{userId}/deliveryfollowupteammembers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDeliveryFollowupTeamMembers(@PathParam("userId") String userId) throws GloriaApplicationException {
        if (!isValidLoggedinUserId(userId)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        UserDTO user = userServices.getUser(userId);
        return Response.ok(userServices.findUserByDeliveryFollowUpTeam(user.getDelFollowUpTeam())).build();
    }

    @GET
    @Path("/v1/users/{userId}/procureteammembers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcureTeamMembers(@PathParam("userId") String userId) throws GloriaApplicationException {
        if (!isValidLoggedinUserId(userId)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        UserDTO user = userServices.getUser(userId);
        return Response.ok(userServices.findUserByProcureTeam(user.getProcureTeam(), userId)).build();
    }
    
    @GET
    @Path("/v1/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@QueryParam("userId") String userId, @QueryParam("password") String password) throws GloriaApplicationException {
       
        LOGGER.info("login called with userId=" + userId);    
        try {
            if (!userServices.authenticateUser(userId, password)) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            securityServices.setAuthentication(userId, userServices.getUserRoleIds(userId), request);
        } catch (GloriaApplicationException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        return Response.ok(userServices.getUser(userId)).build();
    }
    
    @GET
    @Path("/v1/logout")
    public Response logout() throws GloriaApplicationException {
        materialServices.unlockMaterialLine(securityServices.getLoggedInUserId(request));
        materialServices.releasePickList(securityServices.getLoggedInUserId(request));
        securityServices.logout(request);
        return Response.ok().build();
    }
    @GET
    @Path("/v1/checkEnvironment")
    public Response checkEnvironment() throws GloriaApplicationException {
        new CheckEnvironment().checkAll();        
        return Response.ok().build();
    }

    private Boolean isValidLoggedinUserId(String userId) throws GloriaApplicationException {
        String loggedInUser = securityServices.getLoggedInUserId(request);
        if (userId == null || loggedInUser == null || "null".equals(userId) || !loggedInUser.equals(userId)) {
            return false;
        }
        return true;
    }
    
}
