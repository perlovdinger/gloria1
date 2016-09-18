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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.InternalOrderSapDTO;
import com.volvo.gloria.common.c.dto.PartAffiliationDTO;
import com.volvo.gloria.common.c.dto.SiteDTO;
import com.volvo.gloria.common.c.dto.UnitOfMeasureDTO;
import com.volvo.gloria.common.util.CommonHelper;
import com.volvo.gloria.procurematerial.b.DeliveryServices;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.ProcurementDtoTransformer;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.ProcureResponsibility;
import com.volvo.gloria.procurematerial.c.dto.ChangeIdDTO;
import com.volvo.gloria.procurematerial.c.dto.GoodsReceiptLineDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialHeaderDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialHeaderGroupingDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineQiDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderLineDTO;
import com.volvo.gloria.procurematerial.c.dto.PartAliasDTO;
import com.volvo.gloria.procurematerial.c.dto.PickListDTO;
import com.volvo.gloria.procurematerial.c.dto.ProcureLineDTO;
import com.volvo.gloria.procurematerial.c.dto.PurchaseOrganisationDTO;
import com.volvo.gloria.procurematerial.c.dto.RequestGroupDTO;
import com.volvo.gloria.procurematerial.c.dto.ResponseDTO;
import com.volvo.gloria.procurematerial.c.dto.SupplierDTO;
import com.volvo.gloria.procurematerial.d.entities.status.changeid.ChangeIdStatus;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.util.DeliveryHelper;
import com.volvo.gloria.procurematerial.util.MaterialTransformHelper;
import com.volvo.gloria.procurematerial.util.ProcurementHelper;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaFormateUtil;
import com.volvo.gloria.util.b.SecurityServices;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.paging.c.PageUtil;
import com.volvo.gloria.web.uiservices.mapper.RsGridParameters;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Restful service for handling Procurement Process.
 */
@Path("/procurement")
public class ProcurementUIService {

    private static final String RS_QUERY_PARAMETERS = "rsQueryParameters";
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcurementUIService.class);
    @Context
    private HttpServletResponse response;

    @Context
    private HttpServletRequest request;

    private ProcurementServices procurementServices = ServiceLocator.getService(ProcurementServices.class);
    private SecurityServices securityServices = ServiceLocator.getService(SecurityServices.class);
    private CommonServices commonServices = ServiceLocator.getService(CommonServices.class);
    private MaterialServices materialServices = ServiceLocator.getService(MaterialServices.class);
    private DeliveryServices deliveryServices = ServiceLocator.getService(DeliveryServices.class);
    private ProcurementDtoTransformer procurementDtoTransformer = ServiceLocator.getService(ProcurementDtoTransformer.class);

    @GET
    @Path("/v1/materialheaders/current")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getRequestsV1(@QueryParam("rsQueryParameters") RsGridParameters rsGridParameters, @QueryParam("userId") String userId)
            throws GloriaApplicationException {
        return PageUtil.updatePage(procurementServices.getRequestHeaders(rsGridParameters.getPageObject(), null, userId), response);
    }

    @PUT
    @Path("/v1/materialheaders/current")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MaterialHeaderDTO> updateRequestsV1(List<MaterialHeaderDTO> requests, @QueryParam("action") String action,
            @QueryParam("materialControllerId") String materialControllerId, @QueryParam("materialControllerTeam") String materialControllerTeam,
            @QueryParam("type") String teamType) throws GloriaApplicationException {
        long startTime = System.currentTimeMillis();
        List<MaterialHeaderDTO> returnList = procurementDtoTransformer.transformAsDTO(procurementServices.assignOrUnassignMateriaHeaders(requests,
                                                                                                                                         action,
                                                                                                                                         materialControllerId,
                                                                                                                                         materialControllerTeam,
                                                                                                                                         teamType,
                                                                                                                                         securityServices.getLoggedInUserId(request)));
        long endTime = System.currentTimeMillis() - startTime;
        LOGGER.info("PERFORMANCE : updateRequestsV1 " + endTime);
        return returnList;
    }

    @GET
    @Path("/v1/orderlines/current")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * TODO: Convert arguments to rsQueryParameters
     */
    public List<PageResults> getDeliveryControlOrderLinesV1(@QueryParam("expectedArrivalFrom") String expectedArrivalFrom,
            @QueryParam("expectedArrivalTo") String expectedArrivalTo, @QueryParam("expectedArrival") String expectedArrival, 
            @QueryParam("buildStartDateFrom") String buildStartDateFrom, @QueryParam("buildStartDateTo") String buildStartDateTo, @QueryParam("buildStartDate") String buildStartDate,
            @QueryParam("orderStaDateFrom") String orderStaDateFrom, @QueryParam("orderStaDateTo") String orderStaDateTo, @QueryParam("orderStaDate") String orderStaDate,
            @QueryParam("statusFlag") String statusFlag, @QueryParam("partNumber") String partNumber, @QueryParam("markedAsComplete") Boolean markedAsComplete,
            @QueryParam("status") String status, @QueryParam("deliveryControllerId") String deliveryControllerId,
            @QueryParam("internalExternal") String internalExternal, @QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters,
            @QueryParam("deliveryControllerTeam") String deliveryControllerTeam, @QueryParam("unassigned") boolean unassigned,
            @QueryParam("completeType") String completeType, @Context UriInfo ui, @QueryParam("needDeliverySchedules") boolean loadDeliverySchedules)
            throws GloriaApplicationException, ParseException {
       
        Map<String, List<String>> queryParams = ui.getQueryParameters();
        List<PageResults> pageResults = PageUtil.updatePage(deliveryServices.findOrderLinesForDeliveryControl(expectedArrivalFrom, expectedArrivalTo,
                                                                                                              expectedArrival, buildStartDateFrom, buildStartDateTo, buildStartDate, 
                                                                                                              orderStaDateFrom, orderStaDateTo, orderStaDate, statusFlag,
                                                                                                              deliveryControllerId, internalExternal,
                                                                                                              partNumber, markedAsComplete, status,
                                                                                                              securityServices.getLoggedInUserId(request),
                                                                                                              false, rsGridParameters.getPageObject(),
                                                                                                              deliveryControllerTeam, unassigned, completeType,
                                                                                                              queryParams, loadDeliverySchedules), response);
        return pageResults;
    }

    @GET
    @Path("/v1/orderlines/{orderLineId}")
    public OrderLineDTO getOrderLineV1(@PathParam("orderLineId") Long orderLineId) {
        return procurementDtoTransformer.transformAsDTO(deliveryServices.getOrderLine(orderLineId), false);
    }

    @PUT
    @Path("/v1/orderlines/{orderLineId}")
    public OrderLineDTO updateOrderLineV1(OrderLineDTO orderLineDTO) throws GloriaApplicationException {
        return procurementDtoTransformer.transformAsDTO(deliveryServices.updateOrderLine(orderLineDTO, securityServices.getLoggedInUserId(request)), false);
    }

    @PUT
    @Path("/v1/orderlines/{orderLineId}/revoke")
    public OrderLineDTO revokeOrderLineV1(OrderLineDTO orderLineDTO) throws GloriaApplicationException {
        return procurementDtoTransformer.transformAsDTO(deliveryServices.revokeOrderline(orderLineDTO, securityServices.getLoggedInUserId(request)), false);
    }

    @PUT
    @Path("/v1/orderlines")
    public List<OrderLineDTO> updateOrderLinesV1(List<OrderLineDTO> orderLineDTOs) throws GloriaApplicationException {
        return procurementDtoTransformer.transformAsOrderLineDTOs(deliveryServices.updateOrderLines(orderLineDTOs, securityServices.getLoggedInUserId(request)),
                                                                  false);
    }

    @PUT
    @Path("/v1/orderlines/assign")
    public List<OrderLineDTO> assignOrderLinesV1(List<OrderLineDTO> orderLineDTOs) throws GloriaApplicationException {
        return procurementDtoTransformer.transformAsOrderLineDTOs(deliveryServices.assignOrderLines(orderLineDTOs), false);
    }

    @PUT
    @Path("/v1/orderlines/{orderLineId}/ignore")
    public OrderLineDTO ignoreDeliveryDeviationV1(OrderLineDTO orderLineDTO) throws GloriaApplicationException {
        return procurementDtoTransformer.transformAsDTO(deliveryServices.updateDeliveryDeviation(orderLineDTO, securityServices.getLoggedInUserId(request)),
                                                        false);
    }

    @GET
    @Path("/v1/procurelines/{procureLineId}")
    public ProcureLineDTO getProcureLinesV1(@PathParam("procureLineId") long procureLineId, @QueryParam("modificationType") String modificationType)
            throws GloriaApplicationException {
        procurementServices.updateProcureLineSuppliers(procureLineId);
        return procurementServices.findProcureLineById(procureLineId, modificationType);
    }

    @GET
    @Path("/v1/procurelines")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getProcureLinesV1(@QueryParam("procureResponsibility") String procureResponsibilities,
            @QueryParam("procureForwardedId") String procureForwardedId, @QueryParam("procureForwardedTeam") String procureForwardedTeam,
            @QueryParam("status") String status, @QueryParam("assignedMaterialControllerId") String assignedMaterialController,
            @QueryParam("assignedMaterialControllerTeam") String assignedMaterialControllerTeam, @QueryParam("type") String teamType,
            @QueryParam("showFilter") String showFilter, @QueryParam("rsQueryParameters") RsGridParameters rsGridParameters) throws GloriaApplicationException {
        return PageUtil.updatePage(procurementServices.getProcurements(GloriaFormateUtil.getValuesAsString(procureResponsibilities), procureForwardedId,
                                                                       procureForwardedTeam, status, assignedMaterialController,
                                                                       assignedMaterialControllerTeam, teamType, showFilter, rsGridParameters.getPageObject()),
                                   response);
    }

    @PUT
    @Path("/v1/procurelines/{procureLineId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ProcureLineDTO updateProcureLineV1(ProcureLineDTO procureLineDTO, @QueryParam("action") String action,
            @QueryParam("fromStockMaterials") String materialLineKeys, @QueryParam("teamId") String teamId, @QueryParam("edited") boolean edited) throws GloriaApplicationException {
        return ProcurementHelper.transformAsDTO(procurementServices.updateProcureLine(procureLineDTO, action, materialLineKeys, teamId,
                                                                                      securityServices.getLoggedInUserId(request), null, false, null));
    }

    @GET
    @Path("/v1/procurelines/modification")
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseDTO checkV1(@QueryParam("requestLineIds") String requestLineIds, @QueryParam("type") String type) throws GloriaApplicationException {
        return ProcurementHelper.transformAsDTO(procurementServices.isManualGroupingAllowed(GloriaFormateUtil.getValuesAsLong(requestLineIds), type));
    }

    @PUT
    @Path("/v1/procurelines")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ProcureLineDTO> updateProcureLinesV1(List<ProcureLineDTO> procureLineDTOs, @QueryParam("action") String action,
            @QueryParam("teamId") String teamId, @QueryParam("finalWarehouse") String finalWarehouseId, @QueryParam("userId") String forwardedUserId,
            @QueryParam("edited") boolean edited) throws GloriaApplicationException {

        return ProcurementHelper.transformEntityToDTO(procurementServices.updateProcureLines(procureLineDTOs, action, teamId,
                                                                                             securityServices.getLoggedInUserId(request), finalWarehouseId,
                                                                                             forwardedUserId));
    }

    @GET
    @Path("/v1/procurelines/{procureLineId}/materials/{materialId}")
    @Produces(MediaType.APPLICATION_JSON)
    public MaterialDTO getMaterialV1(@PathParam("materialId") Long materialId) throws GloriaApplicationException {
        return procurementServices.findMaterialById(materialId);
    }

    @GET
    @Path("/v1/procurelines/{procureLineId}/materials")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MaterialDTO> getProcureDetailsV1(@PathParam("procureLineId") Long procureLineId) throws GloriaApplicationException {
        return procurementDtoTransformer.transformAsMaterialDTOs(materialServices.findMaterialsByProcureLineIdForProcureDetails(procureLineId));
    }

    @PUT
    @Path("/v1/procurelines/{procureLineId}/materials/{materialId}")
    @Produces(MediaType.APPLICATION_JSON)
    public MaterialDTO updateProcureDetailV1(MaterialDTO materialDTO) throws GloriaApplicationException {
        return procurementDtoTransformer.transformAsDTO(procurementServices.updateMaterial(materialDTO, securityServices.getLoggedInUserId(request)));
    }

    @DELETE
    @Path("/v1/procurelines/{procureLineId}/materials/{materialIds}")
    public void procureLaterV1(@PathParam("materialIds") String materialIds) throws GloriaApplicationException {
        procurementServices.procureLater(GloriaFormateUtil.getValuesAsLong(materialIds));
    }

    @GET
    @Path("/v1/partaffiliations")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PartAffiliationDTO> getPartAffiliationsV1(@QueryParam("requestable") boolean requestable) throws GloriaApplicationException {
        return CommonHelper.transformAsPartAffiliationDTO(commonServices.getAllPartAffiliations(requestable));
    }

    @GET
    @Path("/v1/unitsofmeasure")
    @Produces(MediaType.APPLICATION_JSON)
    public List<UnitOfMeasureDTO> getUnitsOfMeasureV1() throws GloriaApplicationException {
        return CommonHelper.transformAsUnitOfMeasureDTO(commonServices.getAllUnitOfMeasures());
    }

    @GET
    @Path("/v1/materials")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getMaterials(@QueryParam("rsQueryParameters") RsGridParameters rsGridParameters,
            @QueryParam("procureLineId") String procureLineOids, @QueryParam("procureLineStatus") String procureLineStatus,
            @QueryParam("assignedMaterialControllerId") String assignedMaterialControllerId,
            @QueryParam("assignedMaterialControllerTeam") String assignedMaterialControllerTeam, @QueryParam("procureForwardedId") String procureForwardedId,
            @QueryParam("procureForwardedTeam") String procureForwardedTeam, @QueryParam("materialType") String materialType,
            @QueryParam("type") String teamType) throws GloriaApplicationException {
        if (StringUtils.isNotEmpty(procureLineOids)) {
            return PageUtil.updatePage(procurementServices.findMaterialsByProcureLineIds(procureLineOids, rsGridParameters.getPageObject()), response);
        } else {
            return PageUtil.updatePage(procurementServices.findMaterialsByStatusAndResponsability(ProcureLineStatus.getValue(procureLineStatus),
                                                                                                  ProcureResponsibility.PROCURER, assignedMaterialControllerId,
                                                                                                  assignedMaterialControllerTeam, procureForwardedId,
                                                                                                  procureForwardedTeam, rsGridParameters.getPageObject(),
                                                                                                  materialType, teamType), response);
        }
    }

    @GET
    @Path("/v1/materials/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public MaterialDTO getMaterialsById(@PathParam("id") Long id) throws GloriaApplicationException {
        return procurementServices.findMaterialById(id);
    }

    @PUT
    @Path("v1/materials/{materialId}")
    @Produces(MediaType.APPLICATION_JSON)
    public MaterialDTO updateMaterial(MaterialDTO materialDTO) throws GloriaApplicationException {
        return procurementDtoTransformer.transformAsDTO(procurementServices.updateMaterial(materialDTO, securityServices.getLoggedInUserId(request)));
    }

    @GET
    @Path("/v1/procurelines/{procureLineId}/carryovers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SupplierDTO> getCarryOver(@PathParam("procureLineId") Long procureLineId) throws GloriaApplicationException {
        return procurementServices.getProcureLineSuppliers(procureLineId);
    }

    @GET
    @Path("/v1/internalordernosap")
    @Produces(MediaType.APPLICATION_JSON)
    public List<InternalOrderSapDTO> getInternalOrderNoSAP(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters, @QueryParam("code") String code) {
        return ProcurementHelper.transformAsInternalSapDTOs(commonServices.getAllInternalOrderNoSAP(rsGridParameters.getPageObject(), code));
    }

    @GET
    @Path("/v1/buyercodes")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getBuyerCodes(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters,
            @QueryParam("organisationCode") String organisationCode) {
        return PageUtil.updatePage(procurementServices.findAllBuyerCodes(rsGridParameters.getPageObject(), organisationCode), response);
    }

    @GET
    @Path("v1/procurelines/{procurelineId}/changeids/waitconfirm")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ChangeIdDTO> getChangeIdsInWaitConfirmState(@PathParam("procurelineId") long procurelineId) throws GloriaApplicationException {
        return ProcurementHelper.transformChangeIdDTOs(procurementServices.findChangeIdByStateAndProcureLineId(procurelineId, ChangeIdStatus.WAIT_CONFIRM,
                                                                                                               ChangeIdStatus.CANCEL_WAIT));
    }

    @GET
    @Path("v1/changeids")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getAllChangeIds(@QueryParam("rsQueryParameters") RsGridParameters rsGridParameters,
            @QueryParam("assignedMaterialControllerId") String assignedMaterialController,
            @QueryParam("assignedMaterialControllerTeam") String assignedMaterialControllerTeam) throws GloriaApplicationException {
        return PageUtil.updatePage(procurementServices.findAllChangeIds(rsGridParameters.getPageObject(), assignedMaterialController,
                                                                        assignedMaterialControllerTeam), response);
    }

    @PUT
    @Path("v1/changeids/{changeIdId}/materials")
    @Produces(MediaType.APPLICATION_JSON)
    public void updateChangeId(List<MaterialDTO> materialDTOs, @PathParam("changeIdId") long changeIdId, @QueryParam("action") String action)
            throws GloriaApplicationException {
        procurementServices.acceptOrRejectChangeId(action, changeIdId, securityServices.getLoggedInUserId(request), materialDTOs);
    }

    @GET
    @Path("v1/changeids/{changeIdId}/materials")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MaterialDTO> getMaterialsForChangeId(@PathParam("changeIdId") long changeIdId) throws GloriaApplicationException {
        return procurementDtoTransformer.transformAsMaterialDTOs(procurementServices.getMaterialsByChangeId(changeIdId));
    }

    @GET
    @Path("v1/changeids/{changeIdId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ChangeIdDTO getChangeId(@PathParam("changeIdId") long changeIdId) throws GloriaApplicationException {
        return ProcurementHelper.transformChangeIdDTO(procurementServices.findChangeIdById(changeIdId));
    }

    @GET
    @Path("v1/procurelines/excel")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response exportOnBuildSite(@QueryParam("id") String selectedIds) throws GloriaApplicationException {
        FileToExportDTO excelDTO = procurementServices.exportOnBuildSite(GloriaFormateUtil.getValuesAsLong(selectedIds));
        ResponseBuilder responseBuilder = Response.ok(excelDTO.getContent(), MediaType.APPLICATION_OCTET_STREAM);
        responseBuilder.header("content-disposition", "attachment; filename=" + excelDTO.getName() + ".xlsx");
        responseBuilder.header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return responseBuilder.build();
    }

    @GET
    @Path("v1/partaliases")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PartAliasDTO> getPartAliases(@QueryParam("volvoPartNumber") String volvoPartNumber) throws GloriaApplicationException {
        return ProcurementHelper.transformAsPartAliasDTOs(procurementServices.getPartAliases(volvoPartNumber));
    }

    @GET
    @Path("/v1/materiallines")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getMaterialLinesV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters,
            @QueryParam("userId") String loggedInUserId, @QueryParam("procureUserId") String procureUserId, @QueryParam("procureTeam") String procureUserTeam,
            @QueryParam("type") String teamType, @QueryParam("expirationDate") String expirationDate, @QueryParam("expirationDateTo") String expirationTo,
            @QueryParam("expirationDateFrom") String expirationFrom, @QueryParam("allExpirationDate") String allExpired) throws GloriaApplicationException {
        List<PageResults> pageResultList = PageUtil.updatePage(materialServices.getMaterialLines(rsGridParameters.getPageObject(), loggedInUserId,
                                                                                                 procureUserId, procureUserTeam, teamType, expirationDate,
                                                                                                 expirationTo, expirationFrom, new Boolean(allExpired)),
                                                               response);
        return pageResultList;
    }

    @GET
    @Path("/v1/materiallines/qi")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getMaterialLinesQIV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters, @QueryParam("status") String status,
            @QueryParam("qiMarking") String qiMarking, @QueryParam("suggestBinLocation") boolean suggestBinLocation, @QueryParam("whSiteId") String whSiteId)
            throws GloriaApplicationException {
        return PageUtil.updatePage(materialServices.getMaterialLineQi(rsGridParameters.getPageObject(), status, qiMarking, suggestBinLocation, whSiteId),
                                   response);
    }

    @PUT
    @Path("/v1/materiallines/qi")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MaterialLineQiDTO> updateMaterialLinesQIV1(List<MaterialLineQiDTO> materialLineQiDTOs) throws GloriaApplicationException {
        return materialServices.updateMaterialLinesQi(materialLineQiDTOs, securityServices.getLoggedInUserId(request));
    }
    
    @PUT
    @Path("/v1/materiallines/qi/{materialLineQIId}/unmark")
    @Produces(MediaType.APPLICATION_JSON)
    public MaterialLineQiDTO unmarkMaterialLinesQIV1(MaterialLineQiDTO materialLineQiDTO, @PathParam("materialLineQIId") long materialLineQIId,
            @QueryParam("quantity") long quantity) throws GloriaApplicationException {
        return materialServices.unmarkMaterialLineByQi(materialLineQiDTO, quantity, securityServices.getLoggedInUserId(request));
    }

    @PUT
    @Path("/v1/materiallines/qi/{materialLineQIId}")
    @Produces(MediaType.APPLICATION_JSON)
    public MaterialLineQiDTO updateMaterialLineQI(MaterialLineQiDTO materialLineQiDTO) throws GloriaApplicationException {
        return materialServices.updateMaterialLineByQi(materialLineQiDTO, securityServices.getLoggedInUserId(request));
    }

    @PUT
    @Path("/v1/materiallines")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MaterialLineDTO> updateMaterialLinesV1(List<MaterialLineDTO> materialLineDTOs, @QueryParam("action") String action,
            @QueryParam("quantity") long scrapQty, @QueryParam("confirmationText") String confirmationText,
            @QueryParam("fromMaterialLineId") String fromMaterialLineIds, @QueryParam("whSiteId") String whSiteId) throws GloriaApplicationException {
        return MaterialTransformHelper.transformAsMaterialLineDTOs(materialServices.updateMaterialLines(materialLineDTOs, fromMaterialLineIds, action,
                                                                                                        scrapQty, confirmationText,
                                                                                                        securityServices.getLoggedInUserId(request), whSiteId,
                                                                                                        null, null, null));
    }

    @GET
    @Path("/v1/materiallines/{materialLineId}")
    @Produces(MediaType.APPLICATION_JSON)
    public MaterialLineDTO getMaterialLineV1(@PathParam("materialLineId") Long materialLineId,
            @QueryParam("calculateStockBalance") boolean calculateStockBalance) throws GloriaApplicationException {
        return MaterialTransformHelper.transformAsMaterialLineDTO(materialServices.findMaterialLineByIdProcurement(materialLineId, calculateStockBalance,
                                                                                                                   securityServices.getLoggedInUserId(request)),
                                                                  null);
    }

    @GET
    @Path("/v1/materiallines/borrow")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getBorrowMaterialLineV1(@QueryParam("rsQueryParameters") RsGridParameters rsGridParameters,
            @QueryParam("forMaterialLineId") long materialLineId) throws GloriaApplicationException {
        return PageUtil.updatePage(materialServices.getBorrowableMaterialLines(rsGridParameters.getPageObject(), materialLineId), response);
    }

    @PUT
    @Path("/v1/materiallines/{materialLineId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public MaterialLineDTO updateMaterialLineV1(MaterialLineDTO materialLineDTO, @QueryParam("action") String action,
            @QueryParam("fromMaterialLineId") String fromMaterialLineIds, @QueryParam("noReturn") boolean noReturn) throws GloriaApplicationException {
        return MaterialTransformHelper.transformAsMaterialLineDTO(materialServices.updateMaterialLine(materialLineDTO, fromMaterialLineIds, action, noReturn,
                                                                                                      securityServices.getLoggedInUserId(request), null, null,
                                                                                                      null, null), null);
    }

    @GET
    @Path("v1/requestlists/materiallines/{materiallineids}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MaterialLineDTO> getMaterialLinesV1(@PathParam("materiallineids") String materialLineIds) throws GloriaApplicationException {
        return MaterialTransformHelper.transformAsMaterialLineDTOs(materialServices.findMaterialLines(GloriaFormateUtil.getValuesAsLong(materialLineIds)));
    }

    @GET
    @Path("/v1/picklists")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getPicklist(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters, @QueryParam("whSiteId") String whSiteId,
            @QueryParam("status") String status) throws GloriaApplicationException {
        return PageUtil.updatePage(materialServices.findPickListByStatus(rsGridParameters.getPageObject(), whSiteId, status,
                                                                         securityServices.getLoggedInUserId(request)), response);
    }

    @POST
    @Path("/v1/picklists")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RequestGroupDTO> createPickListV1(List<RequestGroupDTO> requestGroupDTOs) throws GloriaApplicationException {
        return MaterialTransformHelper.transformAsRequestGroupDTOs(materialServices.updateRequestGroups(requestGroupDTOs, true,
                                                                                                        securityServices.getLoggedInUserId(request)));
    }
    
    @PUT
    @Path("/v1/picklists/cancel")
    @Produces(MediaType.APPLICATION_JSON)
    public void cancelPickListsV1(List<RequestGroupDTO> requestGroupDTOs) throws GloriaApplicationException {
        procurementServices.cancelPickLists(requestGroupDTOs, securityServices.getLoggedInUserId(request));
    }
    
    @PUT
    @Path("/v1/picklists/{picklistid}/cancel")
    @Produces(MediaType.APPLICATION_JSON)
    public void cancelPickListsV1(@PathParam("picklistid") long picklistOid, PickListDTO pickListDTO) throws GloriaApplicationException {
        procurementServices.cancelPickList(pickListDTO, securityServices.getLoggedInUserId(request));
    }

    @GET
    @Path("/v1/picklists/{pickListId}")
    @Produces(MediaType.APPLICATION_JSON)
    public PickListDTO getRequestGroup(@PathParam("pickListId") long picklistsId) throws GloriaApplicationException {
        return MaterialTransformHelper.transformAsPickListDTO(materialServices.findPickListById(picklistsId));
    }

    @PUT
    @Path("v1/picklists/{picklistID}")
    @Produces(MediaType.APPLICATION_JSON)
    public PickListDTO updatePickList(PickListDTO pickListDTO, @QueryParam("action") String action) throws GloriaApplicationException {
        return MaterialTransformHelper.transformAsPickListDTO(materialServices.updatePickListByAction(pickListDTO, action));
    }

    @GET
    @Path("/v1/picklists/{pickListId}/materiallines")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getMaterialLinesForAPickListID(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters,
            @QueryParam("suggestBinLocation") boolean suggestBinLocation, @PathParam("pickListId") Long pickListId, @QueryParam("status") String status)
            throws GloriaApplicationException {
        return PageUtil.updatePage(materialServices.getMaterialLines(rsGridParameters.getPageObject(), pickListId, status, suggestBinLocation), response);
    }

    @PUT
    @Path("/v1/picklists/{pickListId}/materiallines")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MaterialLineDTO> updateMaterialLineOnPick(@QueryParam("action") String action, List<MaterialLineDTO> materialLineDTOs)
            throws GloriaApplicationException {
        return MaterialTransformHelper.transformAsMaterialLineDTOs(materialServices.updateMaterialLines(materialLineDTOs, null, action, 0, null,
                                                                                                        securityServices.getLoggedInUserId(request), null,
                                                                                                        null, null, null));
    }

    @PUT
    @Path("/v1/materialheaders/grouping")
    @Produces(MediaType.APPLICATION_JSON)
    public Response groupMaterials(List<MaterialHeaderGroupingDTO> materialHeaderGroupDTOs, @QueryParam("pPartNo") String pPartNo,
            @QueryParam("pVersion") String pPartVersion, @QueryParam("pPartName") String pPartName, @QueryParam("pPartModification") String pPartModification)
            throws GloriaApplicationException {
        procurementServices.groupMaterials(materialHeaderGroupDTOs, pPartNo, pPartVersion, pPartName, pPartModification,
                                           securityServices.getLoggedInUserId(request));
        return Response.status(Status.CREATED).build();
    }

    @GET
    @Path("/v1/materiallines/available")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getMaterialsFromStock(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters,
            @QueryParam("partNumber") String partNumber, @QueryParam("partVersion") String partVersion, @QueryParam("partAffiliation") String partAffiliation,
            @QueryParam("project") String projectId, @QueryParam("partModification") String partModification, @QueryParam("companyCode") String companyCode)
            throws GloriaApplicationException {
        return PageUtil.updatePage(procurementServices.getAccumulatedMaterialLinesInStock(rsGridParameters.getPageObject(), partNumber, partVersion,
                                                                                          partAffiliation, projectId, partModification, companyCode), response);
    }

    @PUT
    @Path("/v1/materialheaders/grouping/{modificationId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelModification(@QueryParam("action") String action, @PathParam("modificationId") long modificationId) throws GloriaApplicationException {
        if ("cancel".equalsIgnoreCase(action)) {
            procurementServices.cancelModification(modificationId);
        }
        return Response.status(Status.OK).build();
    }

    @GET
    @Path("/v1/procurelines/modification/{modificationId}/materials")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MaterialDTO> getMaterialsByModificationId(@PathParam("modificationId") long modificationId) {
        return procurementServices.findMaterialsByModificationId(modificationId);
    }

    @GET
    @Path("/v1/materials/{materialId}/orderlines")
    @Produces(MediaType.APPLICATION_JSON)
    public List<OrderLineDTO> findOrderLineByMaterialId(@PathParam("materialId") long materialId) throws GloriaApplicationException {
        return materialServices.findOrderLineByMaterialId(materialId, securityServices.getLoggedInUserId(request));
    }

    @GET
    @Path("/v1/purchaseorganization")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PurchaseOrganisationDTO> findAllPurchaseOrganisation() {
        return ProcurementHelper.transformAsPurchaseOrganisationsDTOs(procurementServices.findAllPurchaseOrganizations());
    }

    @PUT
    @Path("/v1/picklists/{picklistId}/lock")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void lockMaterialLinesForAPickListV1(@PathParam("picklistId") Long picklistId, @QueryParam("userId") String userId)
            throws GloriaApplicationException {
        materialServices.reservePicklist(picklistId, userId);
    }

    @PUT
    @Path("/v1/picklists/{picklistId}/unlock")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void unlockMaterialLinesForAPickListV1(@PathParam("picklistId") Long picklistId, @QueryParam("userId") String userId)
            throws GloriaApplicationException {
        materialServices.releasePickList(picklistId, userId);
    }

    @GET
    @Path("/v1/procurelines/sparePartSites")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SiteDTO> getSparePartSites() {
        return procurementServices.getSparePartSites();
    }

    @PUT
    @Path("/v1/orderlines/{orderlineId}/alerts")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateOrderLineLastModifiedAlerts(@PathParam("orderlineId") Long orderlineId) throws GloriaApplicationException {
        deliveryServices.updateOrderLineLastModified(orderlineId);
    }

    @PUT
    @Path("/v1/materiallines/{materialLineId}/alerts")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateMaterialLineLastModifiedAlerts(@PathParam("materialLineId") Long materialLineId) throws GloriaApplicationException {
        deliveryServices.updateMaterialLastModified(materialLineId);
    }

    @GET
    @Path("/v1/goodsreceiptlines")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> findGoodsReceiptLines(@QueryParam("rsQueryParameters") RsGridParameters rsGridParameters, @QueryParam("whSiteId") String whSiteId,
            @QueryParam("status") String status) {
        return PageUtil.updatePage(deliveryServices.findGoodsReceiptLinesByPlant(rsGridParameters.getPageObject(), whSiteId, status), response);
    }

    @GET
    @Path("/v1/goodsreceiptlines/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public GoodsReceiptLineDTO findGoodsReceiptLineById(@PathParam("id") long id) {
        return DeliveryHelper.transformAsDTO(deliveryServices.findGoodsReceiptLineById(id));
    }

    @PUT
    @Path("/v1/goodsreceiptlines/{id}/cancel")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GoodsReceiptLineDTO updateGoodsReceiptLine(GoodsReceiptLineDTO goodsReceiptLineDTO) throws GloriaApplicationException {
        return DeliveryHelper.transformAsDTO(deliveryServices.cancelGoodsReceiptLine(goodsReceiptLineDTO, securityServices.getLoggedInUserId(request)));
    }
    
    @PUT
    @Path("v1/materiallines/{materiallineId}/referenceId")
    @Produces(MediaType.APPLICATION_JSON)
    public MaterialLineDTO updateReferenceIdOnMaterialline(MaterialLineDTO materialLineDTO) throws GloriaApplicationException {
        return procurementServices.updateReferenceIdByMaterialLine(materialLineDTO, securityServices.getLoggedInUserId(request));
    }
    
    @GET
    @Path("v1/procurelines/toprocure/excel")
    @Produces({ "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" })
    public Response exportToProcure(@QueryParam("ids") String procureLineIds, @QueryParam("procureResponsibility") String procureResponsibilities,
            @QueryParam("procureForwardedId") String procureForwardedId, @QueryParam("procureForwardedTeam") String procureForwardedTeam,
            @QueryParam("status") String status, @QueryParam("assignedMaterialControllerId") String assignedMaterialController,
            @QueryParam("assignedMaterialControllerTeam") String assignedMaterialControllerTeam, @QueryParam("type") String teamType,
            @QueryParam("rsQueryParameters") RsGridParameters rsGridParameters) throws GloriaApplicationException {

        List<Long> procureLineOids = new ArrayList<Long>();
        if (procureLineIds == null || procureLineIds.equalsIgnoreCase("null")) {
            PageObject pageObject = rsGridParameters.getPageObject();
            pageObject.setCurrentPage(1);
            pageObject.setResultsPerPage(Integer.MAX_VALUE);

            List<PageResults> pageResults = PageUtil.updatePage(procurementServices.getProcurements(GloriaFormateUtil.getValuesAsString(procureResponsibilities),
                                                                                                    procureForwardedId, procureForwardedTeam, status,
                                                                                                    assignedMaterialController, assignedMaterialControllerTeam,
                                                                                                    teamType, null, rsGridParameters.getPageObject()), response);

            for (PageResults procureLineDTO : pageResults) {
                procureLineOids.add(((ProcureLineDTO) procureLineDTO).getId());
            }
        } else {
            procureLineOids = GloriaFormateUtil.getValuesAsLong(procureLineIds);
        }
        FileToExportDTO fileToExportDTO = procurementServices.exportToProcure(procureLineOids);

        ResponseBuilder responseBuilder = Response.ok(fileToExportDTO.getContent(), MediaType.APPLICATION_OCTET_STREAM);
        responseBuilder.header("content-disposition", "attachment; filename=" + fileToExportDTO.getName() + ".xlsx");
        responseBuilder.header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return responseBuilder.build();
    }

}
