/*
 * Copyright 2009 Volvo Information Technology AB 
 * 
 * Licensed under the Volvo IT Corporate Source License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *      http://java.volvo.com/licenses/CORPORATE-SOURCE-LICENSE 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.volvo.gloria.web.uiservices;

import java.util.List;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.DangerousGoodsDTO;
import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamDTO;
import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamFilterDTO;
import com.volvo.gloria.common.c.dto.MaterialLineTracebilityDTO;
import com.volvo.gloria.common.c.dto.ProjectDTO;
import com.volvo.gloria.common.c.dto.QualityDocumentDTO;
import com.volvo.gloria.common.c.dto.SiteDTO;
import com.volvo.gloria.common.c.dto.SupplierCounterPartDTO;
import com.volvo.gloria.common.companycode.c.dto.CompanyCodeDTO;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeamFilter;
import com.volvo.gloria.common.util.CommonHelper;
import com.volvo.gloria.config.b.beans.ApplicationUtils;
import com.volvo.gloria.config.b.beans.PublicConfigurationDTO;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteLineDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderlineTracebilityDTO;
import com.volvo.gloria.procurematerial.util.DeliveryHelper;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.paging.c.PageUtil;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.c.dto.BinlocationBalanceDTO;
import com.volvo.gloria.web.uiservices.mapper.RsGridParameters;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Restful service for handling Common Modules.
 */
@Path("/common")
public class CommonUIService {

    private static final String RS_QUERY_PARAMETERS = "rsQueryParameters";
    private static final String COMPANY_CODE = "companyCode";

    @Context
    private HttpServletResponse response;

    private CommonServices commonServices = ServiceLocator.getService(CommonServices.class);
    private UserServices userServices = ServiceLocator.getService(UserServices.class);
    private MaterialServices materialServices = ServiceLocator.getService(MaterialServices.class);
    private WarehouseServices warehouseServices = ServiceLocator.getService(WarehouseServices.class);

    @GET
    @Path("v1/deliveryfollowupteams/{deliveryFollowUpTeam}")
    @Produces(MediaType.APPLICATION_JSON)
    public DeliveryFollowUpTeamDTO getDeliveryFollowupTeamV1(@PathParam("deliveryFollowUpTeam") String deliveryFollowUpTeam) throws GloriaApplicationException {
        return commonServices.getDeliveryFollowupTeam(deliveryFollowUpTeam);
    }

    @PUT
    @Path("v1/deliveryfollowupteams/{deliveryFollowUpTeamId}")
    @Produces(MediaType.APPLICATION_JSON)
    public DeliveryFollowUpTeamDTO updateDeliveryFollowUpTeamV1(DeliveryFollowUpTeamDTO deliveryFollowUpTeamDTO) throws GloriaApplicationException {
        return CommonHelper.transformAsDeliveryFollowUpTeamDTO(commonServices.updateDeliveryFollowupTeam(deliveryFollowUpTeamDTO));
    }

    @GET
    @Path("v1/deliveryfollowupteams/{deliveryFollowUpTeamId}/deliveryfollowupteamfilters")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getDeliveryFollowupTeamFiltersV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters,
            @PathParam("deliveryFollowUpTeamId") Long deliveryFollowUpTeamId) {
        return PageUtil.updatePage(commonServices.getDeliveryFollowUpTeamFilters(rsGridParameters.getPageObject(), deliveryFollowUpTeamId), response);
    }

    @GET
    @Path("v1/deliveryfollowupteams/{deliveryFollowUpTeamId}/deliveryfollowupteamfilters/{deliveryFollowUpTeamFilterId}")
    @Produces(MediaType.APPLICATION_JSON)
    public DeliveryFollowUpTeamFilterDTO getDeliveryFollowupTeamFiltersByIdV1(@PathParam("deliveryFollowUpTeamFilterId") String deliveryFollowUpTeamFilterId)
            throws GloriaApplicationException {
        long deliveryFollowUpTeamFilterOId = Long.valueOf(deliveryFollowUpTeamFilterId);
        return commonServices.getDeliveryFollowUpTeamFilter(deliveryFollowUpTeamFilterOId);
    }

    @POST
    @Path("v1/deliveryfollowupteams/{deliveryFollowUpTeamId}/deliveryfollowupteamfilters")
    @Produces(MediaType.APPLICATION_JSON)
    public DeliveryFollowUpTeamFilterDTO createDeliveryFollowupTeamFiltersV1(DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO,
            @PathParam("deliveryFollowUpTeamId") String deliveryFollowUpTeamId) throws GloriaApplicationException {
        long deliveryFollowUpTeamOid = Long.parseLong(deliveryFollowUpTeamId);
        return commonServices.addDeliveryFollowUpTeamFilter(deliveryFollowUpTeamFilterDTO, deliveryFollowUpTeamOid);
    }

    @PUT
    @Path("v1/deliveryfollowupteams/{deliveryFollowUpTeamId}/deliveryfollowupteamfilters/{deliveryFollowUpTeamFilterId}")
    @Produces(MediaType.APPLICATION_JSON)
    public DeliveryFollowUpTeamFilterDTO updateDeliveryFollowupTeamFiltersV1(DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO)
            throws GloriaApplicationException {
        DeliveryFollowUpTeamFilter deliveryFollowUpTeamFilter = commonServices.updateDeliveryFollowupTeamFilter(deliveryFollowUpTeamFilterDTO);
        return CommonHelper.transformAsDeliveryFollowUpTeamFilterDTO(deliveryFollowUpTeamFilter);
    }

    @DELETE
    @Path("v1/deliveryfollowupteams/{deliveryFollowUpTeamId}/deliveryfollowupteamfilters/{deliveryFollowUpTeamFilterId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteDeliveryFollowupTeamFiltersV1(@PathParam("deliveryFollowUpTeamFilterId") String deliveryFollowUpTeamFilterId) {
        long deliveryFollowUpTeamFilterOid = Long.parseLong(deliveryFollowUpTeamFilterId);
        commonServices.deleteDeliveryFollowupTeamFilter(deliveryFollowUpTeamFilterOid);
    }

    @GET
    @Path("v1/deliveryfollowupteams/{deliveryFollowUpTeamId}/suppliercounterparts")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SupplierCounterPartDTO> getSupplierCounterParts(@PathParam("deliveryFollowUpTeamId") String deliveryFollowUpTeamId)
            throws GloriaApplicationException {
        return CommonHelper.transformAsDTO(commonServices.getAllSupplierCounterParts(deliveryFollowUpTeamId));
    }

    @GET
    @Path("v1/buildsites")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SiteDTO> getBuildSites(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters, @QueryParam("siteId") String siteId)
            throws GloriaApplicationException {
        return CommonHelper.transforToSiteDTOs(commonServices.getAllBuildSites(rsGridParameters.getPageObject(), siteId));
    }

    @GET
    @Path("v1/dangerousgoods")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DangerousGoodsDTO> getdangerousGoods() throws GloriaApplicationException {
        return CommonHelper.transformToDangerousGoodsDTOs(commonServices.getAllDangerousGoods());
    }

    @GET
    @Path("v1/wbselements")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getWbsElement(@QueryParam(COMPANY_CODE) String companyCode, @QueryParam("projectId") String projectId,
            @QueryParam("wbs") String wbs, @QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters) throws GloriaApplicationException {
        return PageUtil.updatePage(commonServices.findWbsElementsByCompanyCodeAndProjectId(companyCode, projectId, wbs, rsGridParameters.getPageObject()),
                                   response);
    }

    @GET
    @Path("v1/costcenters")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getCostCenter(@QueryParam(COMPANY_CODE) String companyCode, @QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters,
            @QueryParam("costCenter") String costCenter) throws GloriaApplicationException {
        return PageUtil.updatePage(commonServices.findCostCentersByCompanyCode(companyCode, costCenter, rsGridParameters.getPageObject()), response);
    }

    @GET
    @Path("v1/glaccounts")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getGlAccount(@QueryParam(COMPANY_CODE) String companyCode, @QueryParam("glAccountStr") String glAccountStr,
            @QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters) throws GloriaApplicationException {
        return PageUtil.updatePage(commonServices.findGlAccountsByCompanyCode(companyCode, glAccountStr, rsGridParameters.getPageObject()), response);
    }

    @GET
    @Path("v1/companycodes/{companyCode}/projects")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ProjectDTO> getProjectsByCompanyCodeV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters,
            @PathParam(COMPANY_CODE) String companyCode, @QueryParam("projectId") String projectId) throws GloriaApplicationException {
        return commonServices.getProjectsByCompanyCode(rsGridParameters.getPageObject(), companyCode, projectId);
    }

    @GET
    @Path("v1/projects")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ProjectDTO> getProjects(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters, @QueryParam("projectId") String projectId
                                        , @QueryParam("teamName") String team,  @QueryParam("teamType") String teamType)
            throws GloriaApplicationException {
        return userServices.getTeamProjects(rsGridParameters.getPageObject(), team, teamType, projectId);
    }

    @GET
    @Path("v1/companycodes")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CompanyCodeDTO> getCompanyCodes(@QueryParam("userId") String userId, @QueryParam("code") String code, @QueryParam("type") String teamType)
            throws GloriaApplicationException {
        return CommonHelper.transformCompanyCodeToDTO(userServices.getUserCompanyCodes(userId, null, teamType, code));
    }

    @GET
    @Path("/v1/qualitydocuments")
    @Produces(MediaType.APPLICATION_JSON)
    public List<QualityDocumentDTO> getQualityDocuments() {
        return CommonHelper.transformToQualityDocumentDTOs(commonServices.findAllQualityDocuments());
    }

    @GET
    @Path("/v1/materiallines/{materialLineId}/traceabilitys")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MaterialLineTracebilityDTO> getMaterialLineTraceabilitys(@PathParam("materialLineId") long materialLineId) {
        return CommonHelper.transformAsMaterialLineTraceabilityDTOs(commonServices.getMaterialLineTraceabilitys(materialLineId));
    }

    @GET
    @Path("/v1/orderlines/{orderLineId}/traceabilitys")
    @Produces(MediaType.APPLICATION_JSON)
    public List<OrderlineTracebilityDTO> getOrderLineTraceabilitys(@PathParam("orderLineId") long orderLineId) {
        return DeliveryHelper.transformAsOrderlineTraceabilityDTOs(commonServices.getOrderLineTraceabilitys(orderLineId));
    }

    @PUT
    @Path("v1/deliverynotelines/partlabels")
    @Produces(MediaType.APPLICATION_JSON)
    public Response printPartLabel(List<DeliveryNoteLineDTO> deliveryNoteLineDTOs, @QueryParam("quantity") long quantity,
            @QueryParam("whSiteId") String whSiteId) throws GloriaApplicationException {
        materialServices.printPartLabels(deliveryNoteLineDTOs, quantity, whSiteId);
        return Response.ok().build();
    }

    @PUT
    @Path("v1/partbalance/partlabels")
    @Produces(MediaType.APPLICATION_JSON)
    public void printBalancePartLabel(List<BinlocationBalanceDTO> binlocationBalanceDTOs, @QueryParam("quantity") long quantity,
            @QueryParam("whSiteId") String whSiteId) throws GloriaApplicationException {
        warehouseServices.printPartLabels(binlocationBalanceDTOs, quantity, whSiteId);
    }

    @GET
    @Path("v1/suppliercounterparts")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SupplierCounterPartDTO> getSupplierCounterPartsByCompanyCode(@QueryParam("companyCode") String companyCode) throws GloriaApplicationException {
        return CommonHelper.transformAsDTO(commonServices.getSupplierCounterPartsByCompanyCode(companyCode));
    }

    @PUT
    @Path("v1/picklists/{picklistId}/pullabels")
    @Produces(MediaType.APPLICATION_JSON)
    public void printPullLabelsForPicklist(@PathParam("picklistId") long picklistId, @QueryParam("quantity") long quantity,
            @QueryParam("whSiteId") String whSiteId) throws GloriaApplicationException {
        materialServices.printPullLabels(picklistId, quantity, whSiteId, 0L);
    }

    @PUT
    @Path("v1/picklists/{picklistId}/materiallines/{materiallineId}/pullabels")
    @Produces(MediaType.APPLICATION_JSON)
    public void printPullLabelsForMaterialLine(@PathParam("picklistId") long picklistId, @PathParam("materiallineId") long materialLineId,
            @QueryParam("quantity") long quantity, @QueryParam("whSiteId") String whSiteId) throws GloriaApplicationException {
        materialServices.printPullLabels(picklistId, quantity, whSiteId, materialLineId);
    }

    @GET
    @Path("v1/publicconfiguration")
    @Produces(MediaType.APPLICATION_JSON)
    public PublicConfigurationDTO getPublicConfiguration() throws GloriaApplicationException {
        return ApplicationUtils.getPublicConfiguration();
    }
    
    @GET
    @Path("v1/app/status")
    @Produces(MediaType.TEXT_PLAIN)
    public String getGloriaApplicationStatusV1(@PathParam("deliveryFollowUpTeam") String deliveryFollowUpTeam) throws GloriaApplicationException {
        boolean status = commonServices.verifyApplicationStatus();
        return (status) ? "OK" : "NOT-OK";
    }
}
