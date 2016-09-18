package com.volvo.gloria.web.uiservices;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.materialrequest.b.MaterialRequestHelper;
import com.volvo.gloria.materialrequest.b.MaterialRequestServices;
import com.volvo.gloria.materialrequest.c.dto.MaterialRequestDTO;
import com.volvo.gloria.materialrequest.c.dto.MaterialRequestLineDTO;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.b.SecurityServices;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.paging.c.PageUtil;
import com.volvo.gloria.web.uiservices.mapper.RsGridParameters;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Restful service for handling Material Requests.
 */
@Path("/materialrequest")
public class MaterialRequestUIService {

    @Context
    private HttpServletResponse response;

    @Context
    private HttpServletRequest request;

    private SecurityServices securityServices = ServiceLocator.getService(SecurityServices.class);
    private MaterialRequestServices materialRequestServices = ServiceLocator.getService(MaterialRequestServices.class);
    private UserServices userServices = ServiceLocator.getService(UserServices.class);

    @GET
    @Path("/v1/materialrequests/current")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getMaterialRequests(@QueryParam("rsQueryParameters") RsGridParameters rsGridParameters, @QueryParam("userId") String userId)
            throws GloriaApplicationException {
        return PageUtil.updatePage(materialRequestServices.getMaterialRequests(rsGridParameters.getPageObject(), userId), response);
    }

    @GET
    @Path("/v1/materialrequests/{materialrequestId}/current/materialrequestlines/excel")
    @Produces({ "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" })
    public Response exportMaterialRequestParts(@PathParam("materialrequestId") long materialrequestId) {
        FileToExportDTO excelDTO = materialRequestServices.exportMaterialRequestParts(materialrequestId);
        ResponseBuilder responseBuilder = Response.ok(excelDTO.getContent(), MediaType.APPLICATION_OCTET_STREAM);
        responseBuilder.header("content-disposition", "attachment; filename=" + excelDTO.getName() + ".xlsx");
        responseBuilder.header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return responseBuilder.build();
    }

    @GET
    @Path("/v1/materialrequests/{materialrequestId}/current")
    @Produces(MediaType.APPLICATION_JSON)
    public MaterialRequestDTO getMaterialRequest(@PathParam("materialrequestId") long materialrequestId, @QueryParam("action") String action)
            throws GloriaApplicationException {
        return MaterialRequestHelper.transformAsDTO(materialRequestServices.findMaterialRequestById(materialrequestId, action,
                                                                                                    securityServices.getLoggedInUserId(request)));
    }

    @GET
    @Path("/v1/materialrequests/{materialrequestId}/current/materialrequestlines")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getMaterialRequestlines(@PathParam("materialrequestId") long materialrequestId,
            @QueryParam("rsQueryParameters") RsGridParameters rsGridParameters) throws GloriaApplicationException {
        return PageUtil.updatePage(materialRequestServices.getMaterialRequestlines(materialrequestId, rsGridParameters.getPageObject()), response);
    }

    @POST
    @Path("/v1/materialrequests")
    @Produces(MediaType.APPLICATION_JSON)
    public MaterialRequestDTO createMaterialRequests(MaterialRequestDTO materialRequestDTO) throws GloriaApplicationException {
        return MaterialRequestHelper.transformAsDTO(materialRequestServices.createMaterialRequest(materialRequestDTO,
                                                                                                  securityServices.getLoggedInUserId(request)));
    }

    @PUT
    @Path("/v1/materialrequests/{materialrequestId}")
    @Produces(MediaType.APPLICATION_JSON)
    public MaterialRequestDTO updateMaterialRequests(MaterialRequestDTO materialRequestDTO) throws GloriaApplicationException {
        return MaterialRequestHelper.transformAsDTO(materialRequestServices.updateMaterialRequest(materialRequestDTO));
    }

    @PUT
    @Path("/v1/materialrequests/{materialrequestId}/materialrequestlines")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MaterialRequestLineDTO> updateMaterialRequestLines(@PathParam("materialrequestId") long materialrequestId,
            List<MaterialRequestLineDTO> materialRequestLines) throws GloriaApplicationException {
        return MaterialRequestHelper.transformListOfMaterialRequestLineDTOs(materialRequestServices.updateMaterialRequestLines(materialRequestLines,
                                                                                                                               materialrequestId));
    }

    @DELETE
    @Path("/v1/materialrequests/{materialrequestId}")
    public void deleteMaterialRequest(@PathParam("materialrequestId") long materialRequestId) throws GloriaApplicationException {
        materialRequestServices.deleteMaterialRequest(materialRequestId);
    }

    @PUT
    @Path("/v1/materialrequests/{materialrequestId}/current")
    @Produces(MediaType.APPLICATION_JSON)
    public MaterialRequestDTO updateCurrentMaterialRequest(@QueryParam("action") String action, MaterialRequestDTO materialRequestDTO)
            throws GloriaApplicationException {
        return MaterialRequestHelper.transformAsDTO(materialRequestServices.updateMaterialRequest(materialRequestDTO, action,
                                                                                                  securityServices.getLoggedInUserId(request)));
    }
    
    @GET
    @Path("/v1/materialrequests/{companyCode}/isValidMC/{mcId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response isValidMCForCompanyCode(@PathParam("mcId") String mcId, @PathParam("companyCode") String companyCode) throws GloriaApplicationException {
        return Response.ok().entity(String.valueOf(MaterialRequestHelper.validateMCAndCompanyCodes(mcId, companyCode, userServices))).build();
    }

    @POST
    @Path("/v1/materialrequests/{materialrequestId}/current/materialrequestlines")
    @Produces(MediaType.APPLICATION_JSON)
    public MaterialRequestLineDTO createMaterialRequestLine(@PathParam("materialrequestId") long materialRequestOid,
            MaterialRequestLineDTO materialRequestLineDTO) throws GloriaApplicationException {
        return MaterialRequestHelper.transformAsDTO(materialRequestServices.createMaterialRequestLine(materialRequestLineDTO, materialRequestOid));
    }

    @PUT
    @Path("/v1/materialrequests/{materialrequestId}/current/materialrequestlines")
    @Produces(MediaType.APPLICATION_JSON)
    public MaterialRequestLineDTO updateMaterialRequestLine(@PathParam("materialrequestId") long materialRequestOid,
            MaterialRequestLineDTO materialRequestLineDTO) throws GloriaApplicationException {
        return MaterialRequestHelper.transformAsDTO(materialRequestServices.updateMaterialRequestLine(materialRequestLineDTO, materialRequestOid));
    }

    @DELETE
    @Path("/v1/materialrequests/{materialrequestId}/current/materialrequestlines/{materialrequestlineIds}")
    public void deleteMaterialRequestLine(@PathParam("materialrequestlineIds") String materialRequestLineOids,
            @PathParam("materialrequestId") long materialRequestOid) throws GloriaApplicationException {
        materialRequestServices.deleteMaterialRequestLine(materialRequestOid, materialRequestLineOids);
    }

    @PUT
    @Path("/v1/materialrequests/{materialrequestId}/current/materialrequestlines/{materialrequestlineId}/undoRemove")
    public MaterialRequestLineDTO undoRemoveMaterialRequestLine(@PathParam("materialrequestId") long materialRequestOid,
            @PathParam("materialrequestlineId") long materialRequestlineOid, MaterialRequestLineDTO materialRequestLineDTO) throws GloriaApplicationException {
        return MaterialRequestHelper.transformAsDTO(materialRequestServices.undoRemoveMaterialRequestLine(materialRequestLineDTO, materialRequestOid, materialRequestlineOid));
    }
}
