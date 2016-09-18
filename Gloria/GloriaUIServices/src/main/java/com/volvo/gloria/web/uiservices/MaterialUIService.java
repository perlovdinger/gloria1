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
import javax.ws.rs.core.UriInfo;

import com.volvo.gloria.common.c.dto.DeliveryLogDTO;
import com.volvo.gloria.procurematerial.b.DeliveryServices;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.ProcurementDtoTransformer;
import com.volvo.gloria.procurematerial.c.DeliveryNoteLineStatus;
import com.volvo.gloria.procurematerial.c.EventType;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteDTO;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteLineDTO;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteSubLineDTO;
import com.volvo.gloria.procurematerial.c.dto.DeliveryScheduleDTO;
import com.volvo.gloria.procurematerial.c.dto.DispatchNoteDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderLineDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderLineLogDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderLineVersionDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderLogDTO;
import com.volvo.gloria.procurematerial.c.dto.RequestGroupDTO;
import com.volvo.gloria.procurematerial.c.dto.RequestListDTO;
import com.volvo.gloria.procurematerial.c.dto.TransportLabelDTO;
import com.volvo.gloria.procurematerial.d.type.receive.ReceiveType;
import com.volvo.gloria.procurematerial.util.DeliveryHelper;
import com.volvo.gloria.procurematerial.util.MaterialTransformHelper;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaFormateUtil;
import com.volvo.gloria.util.b.SecurityServices;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.paging.c.PageUtil;
import com.volvo.gloria.web.uiservices.mapper.DateTimeParam;
import com.volvo.gloria.web.uiservices.mapper.RsGridParameters;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Restful service for handling Delivery Control.
 */
@Path("/material")
public class MaterialUIService {

    private static final String RS_QUERY_PARAMETERS = "rsQueryParameters";

    @Context
    private HttpServletResponse response;

    @Context
    private HttpServletRequest request;

    private SecurityServices securityServices = ServiceLocator.getService(SecurityServices.class);
    private DeliveryServices deliveryServices = ServiceLocator.getService(DeliveryServices.class);
    private MaterialServices materialServices = ServiceLocator.getService(MaterialServices.class);
    private ProcurementDtoTransformer procurementDtoTransformer = ServiceLocator.getService(ProcurementDtoTransformer.class);

    @GET
    @Path("/v1/orders/warehouse")
    public List<OrderDTO> getWarehouseOrdersV1(@QueryParam("whSiteId") String whSiteId,
                                                  @QueryParam("orderNo") String orderNo) throws GloriaApplicationException {
        // When reimplementing this: The only search parameter used is orderNo.
        return DeliveryHelper.transformAsDTO(deliveryServices.findOrdersForWarehouse(securityServices.getLoggedInUserId(request),
                                                                           whSiteId, orderNo));
    }

    @GET
    @Path("/v1/orders/deliverycontrol")
    public List<PageResults> getDeliveryControlOrdersV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters,
            @QueryParam("deliveryManagerId") String deliveryManagerId) throws GloriaApplicationException {
        return PageUtil.updatePage(deliveryServices.findOrders(rsGridParameters.getPageObject(), deliveryManagerId,
                                                               securityServices.getLoggedInUserId(request), true), response);
    }

    @GET
    @Path("/v1/orders/{orderId}")
    public OrderDTO getOrderV1(@PathParam("orderId") Long orderId) throws GloriaApplicationException {
        return DeliveryHelper.transformAsDTO(deliveryServices.findOrderById(orderId));
    }

    @PUT
    @Path("/v1/orders/{orderId}")
    public OrderDTO updateOrderV1(OrderDTO orderDTO) throws GloriaApplicationException {
        return DeliveryHelper.transformAsDTO(deliveryServices.updateOrder(orderDTO));
    }

    @GET
    @Path("/v1/orderlines/{orderLineId}/deliveryschedules")
    public List<DeliveryScheduleDTO> getDeliverySchedulesV1(@PathParam("orderLineId") Long orderLineId) throws GloriaApplicationException {
        // ?? do we need user check here
        return DeliveryHelper.transformAsDeliveryScheduleDTOs(deliveryServices.getDeliverySchedules(null, orderLineId));
    }

    @GET
    @Path("/v1/orderlines/{orderLineId}/deliveryschedules/{deliveryScheduleId}")
    public DeliveryScheduleDTO getDeliveryScheduleV1(@PathParam("orderLineId") Long orderLineId, @PathParam("deliveryScheduleId") Long deliveryScheduleId)
            throws GloriaApplicationException {
        return DeliveryHelper.transformAsDTO(deliveryServices.getDeliverySchedule(deliveryScheduleId));
    }

    @PUT
    @Path("/v1/orderlines/{orderLineId}/deliveryschedules/{deliveryScheduleId}")
    public DeliveryScheduleDTO updateDeliveryScheduleV1(@PathParam("orderLineId") Long orderLineId, 
            DeliveryScheduleDTO deliveryScheduleDTO) throws GloriaApplicationException {
        return DeliveryHelper.transformAsDTO(deliveryServices.updateDeliverySchedule(orderLineId, deliveryScheduleDTO,
                                                                                     securityServices.getLoggedInUserId(request)));
    }
    
    @PUT
    @Path("/v1/orderlines/{orderLineId}/deliveryschedules")
    public List<DeliveryScheduleDTO> updateDeliverySchedulesV1(@PathParam("orderLineId") Long orderLineId, List<DeliveryScheduleDTO> deliveryScheduleDTOs)
            throws GloriaApplicationException {
        return DeliveryHelper.transformAsDeliveryScheduleDTOs(deliveryServices.updateDeliverySchedules(orderLineId, deliveryScheduleDTOs,
                                                                                                       securityServices.getLoggedInUserId(request)));
    }

    @PUT
    @Path("/v1/deliverynotes")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public DeliveryNoteDTO createUpdateDeliveryNotesV1(DeliveryNoteDTO deliveryNoteDTO) throws GloriaApplicationException {
        return DeliveryHelper.transformAsDTO(deliveryServices.createOrUpdateDeliveryNote(deliveryNoteDTO));
    }

    @GET
    @Path("/v1/deliverynotes/{deliveryNoteId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public DeliveryNoteDTO getDeliveryNotesV1(@PathParam("deliveryNoteId") long deliveryNoteId, @QueryParam("whSiteId") String whSiteId)
            throws GloriaApplicationException {
        return DeliveryHelper.transformAsDTO(deliveryServices.getDeliveryNoteById(deliveryNoteId, whSiteId));
    }

    @GET
    @Path("/v1/deliverynotelines/{deliveryNoteLineId}")
    @Produces(MediaType.APPLICATION_JSON)
    public DeliveryNoteLineDTO getDeliveryNoteLineV1(@PathParam("deliveryNoteLineId") Long deliveryNoteLineId) throws GloriaApplicationException {
        return DeliveryHelper.transformAsDTO(deliveryServices.getDeliveryNoteLineByIdAndSetMaterialLine(deliveryNoteLineId), null);
    }

    @PUT
    @Path("/v1/deliverynotelines/{deliveryNoteLineId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public DeliveryNoteLineDTO updateDeliveryNoteLineV1(@QueryParam("receive") boolean receive,
            DeliveryNoteLineDTO deliveryNoteLineDTO) throws GloriaApplicationException {
        return DeliveryHelper.transformAsDTO(deliveryServices.updateDeliveryNoteLine(deliveryNoteLineDTO, securityServices.getLoggedInUserId(request), receive),
                                             null);
    }

    @GET
    @Path("/v1/deliverynotes/{deliveryNoteId}/deliverynotelines")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DeliveryNoteLineDTO> getDeliveryNoteLinesByDeliveryNoteIdV1(@PathParam("deliveryNoteId") Long deliveryNoteId,
            @QueryParam("whSiteId") String whSiteId, @QueryParam("receiveType") ReceiveType receiveType) throws GloriaApplicationException {
        return DeliveryHelper.transaformAsDeliveryNoteLineDTOs(deliveryServices.getDeliveryNoteLines(deliveryNoteId, whSiteId, DeliveryNoteLineStatus.IN_WORK,
                                                                                                     receiveType), null);
    }

    @DELETE
    @Path("/v1/deliverynotes/{deliveryNoteId}/deliverynotelines/{deliveryNoteLineIds}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteDeliveryNoteLinesV1(@PathParam("deliveryNoteLineIds") String deliveryNoteLineIds) throws GloriaApplicationException {
        deliveryServices.deleteDeliveryNoteLines(deliveryNoteLineIds);
    }

    @GET
    @Path("/v1/orderlines/{orderlineId}/deliverynotelines")
    public List<DeliveryNoteLineDTO> getDeliveryNoteLinesByOrderLineIdV1(@PathParam("orderlineId") Long orderlineId, @QueryParam("status") String status)
            throws GloriaApplicationException {
        return DeliveryHelper.transaformAsDeliveryNoteLineDTOs(deliveryServices.getDeliveryNoteLinesByOrderLineId(orderlineId, status), null);
    }

    @GET
    @Path("/v1/deliverynotelines")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getDeliveryNoteLinesV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters, @QueryParam("status") String status,
            @QueryParam("qiMarking") String qiMarking, @QueryParam("whSiteId") String whSiteId, @QueryParam("needSublines") boolean needSublines)
            throws GloriaApplicationException {
        return PageUtil.updatePage(deliveryServices.getDeliveryNoteLinesForQI(rsGridParameters.getPageObject(), status, qiMarking, whSiteId, needSublines),
                                   response);
    }

    @PUT
    @Path("/v1/deliverynotelines")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<DeliveryNoteLineDTO> updateDeliveryNoteLinesV1(List<DeliveryNoteLineDTO> deliveryNoteLineDTOs, @QueryParam("receive") boolean receive)
            throws GloriaApplicationException {
        return DeliveryHelper.transaformAsDeliveryNoteLineDTOs(deliveryServices.updateDeliveryNoteLines(deliveryNoteLineDTOs, receive,
                                                                                                        securityServices.getLoggedInUserId(request)), null);
    }

    @GET
    @Path("v1/orderlines/{orderlineId}/materials")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MaterialDTO> getMaterialsV1(@PathParam("orderlineId") Long orderlineId) throws GloriaApplicationException {
        return procurementDtoTransformer.transformAsMaterialDTOs(materialServices.getMaterials(orderlineId));
    }

    @GET
    @Path("v1/orderlines/{orderlineId}/orderlinelogs")
    @Produces(MediaType.APPLICATION_JSON)
    public List<OrderLineLogDTO> getOrderLineLogsV1(@PathParam("orderlineId") Long orderlineId) throws GloriaApplicationException {
        return DeliveryHelper.transaformAsOrderLineLogDTOs(deliveryServices.getOrderLineLogs(orderlineId));
    }

    @POST
    @Path("v1/orderlines/{orderlineId}/orderlinelog")
    @Produces(MediaType.APPLICATION_JSON)
    public OrderLineLogDTO createOrderLineLogV1(@PathParam("orderlineId") Long orderlineId, OrderLineLogDTO orderLineLogDTO) throws GloriaApplicationException {
        return DeliveryHelper.transformAsDTO(deliveryServices.addOrderLineLog(orderlineId, orderLineLogDTO, securityServices.getLoggedInUserId(request)));
    }

    @GET
    @Path("v1/orderlines/{orderlineId}/orderlogs")
    @Produces(MediaType.APPLICATION_JSON)
    public List<OrderLogDTO> getOrderLogsV1(@PathParam("orderlineId") Long orderlineId) throws GloriaApplicationException {
        return DeliveryHelper.transaformAsOrderLogDTOs(deliveryServices.getOrderLogsByOrderLineId(orderlineId));
    }

    @POST
    @Path("v1/orderlines/{orderlineId}/orderlog")
    @Produces(MediaType.APPLICATION_JSON)
    public OrderLogDTO createOrderLogV1(@PathParam("orderlineId") Long orderlineId, OrderLogDTO orderLogDTO) throws GloriaApplicationException {
        return DeliveryHelper.transformAsDTO(deliveryServices.addOrderLog(orderlineId, orderLogDTO, securityServices.getLoggedInUserId(request)));
    }

    @GET
    @Path("v1/transportlabels")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TransportLabelDTO> getTransportLabelsV1(@QueryParam("action") String action, @QueryParam("whSiteId") String whSiteId,
            @QueryParam("transportLabelCopies") long transportLabelCopies) throws GloriaApplicationException {
        return DeliveryHelper.transformAsTransportLabelDTOs(deliveryServices.getTransportLabels(securityServices.getLoggedInUserId(request), whSiteId, action,
                                                                                                transportLabelCopies));
    }

    @GET
    @Path("v1/transportlabels/{transportlabelID}")
    @Produces(MediaType.APPLICATION_JSON)
    public TransportLabelDTO getTransportLabelV1(@QueryParam("action") String action, @PathParam("transportlabelID") Long transportlabelID,
            @QueryParam("whSiteId") String whSiteId) throws GloriaApplicationException {
        return DeliveryHelper.transformAsDTO(deliveryServices.getTransportLabel(securityServices.getLoggedInUserId(request), 
                                                                                whSiteId, action, transportlabelID));
    }

    @GET
    @Path("v1/requestlists")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getRequestListsV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters, @QueryParam("statuses") String status,
            @QueryParam("whSiteId") String whSiteId, @QueryParam("outBoundLocationId") String outBoundLocationId, @QueryParam("requesterId") String requesterId)
            throws GloriaApplicationException {
        return PageUtil.updatePage(materialServices.getRequestLists(rsGridParameters.getPageObject(), status, whSiteId, outBoundLocationId, requesterId),
                                   response);
    }

    @POST
    @Path("v1/requestlists/{requestlistId}/dispatchnotes")
    @Produces(MediaType.APPLICATION_JSON)
    public DispatchNoteDTO createDispatchNoteV1(@PathParam("requestlistId") long requestlistId, DispatchNoteDTO dispatchNoteDTO,
            @QueryParam("action") String action) throws GloriaApplicationException {
        return MaterialTransformHelper.transformAsDTO(materialServices.createDispatchNoteforRequestList(requestlistId, dispatchNoteDTO, action,
                                                                                                        securityServices.getLoggedInUserId(request)));
    }

    @GET
    @Path("v1/dispatchnotes/{dispatchnoteId}")
    @Produces(MediaType.APPLICATION_JSON)
    public DispatchNoteDTO getDispatchNoteV1(@PathParam("dispatchnoteId") long dispatchNoteId) throws GloriaApplicationException {
        return MaterialTransformHelper.transformAsDTO(materialServices.findDispatchNoteById(dispatchNoteId));
    }

    @PUT
    @Path("v1/requestlists/{requestlistId}/dispatchnotes/{dispatchnoteId}")
    @Produces(MediaType.APPLICATION_JSON)
    public DispatchNoteDTO updateDispatchNoteV1(@PathParam("requestlistId") long requestlistId,
            DispatchNoteDTO dispatchNoteDTO, @QueryParam("action") String action) throws GloriaApplicationException {

        return MaterialTransformHelper.transformAsDTO(materialServices.createDispatchNoteforRequestList(requestlistId, dispatchNoteDTO, action,
                                                                                                        securityServices.getLoggedInUserId(request)));
    }

    @DELETE
    @Path("v1/dispatchnotes/{dispatchnoteId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteDispatchNoteV1(@PathParam("dispatchnoteId") long dispatchNoteId) throws GloriaApplicationException {
        materialServices.deleteDispatchNote(dispatchNoteId);
    }

    @GET
    @Path("v1/requestlists/{requestlistId}")
    @Produces(MediaType.APPLICATION_JSON)
    public RequestListDTO getRequestListsV1(@PathParam("requestlistId") long requestlistId) throws GloriaApplicationException {
        return MaterialTransformHelper.transformAsRequestListDTO(materialServices.findRequestListById(requestlistId));
    }
    
    @GET
    @Path("v1/requestlists/{requestlistId}/materiallines")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MaterialLineDTO> getMaterialLinesForReqestListV1(@PathParam("requestlistId") long requestlistId) throws GloriaApplicationException {
        return MaterialTransformHelper.transformAsMaterialLineDTOs(materialServices.getMaterialLinesForRequestList(requestlistId));
    }

    @GET
    @Path("v1/requestlists/{requestlistId}/requestgroups")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RequestGroupDTO> getRequestGroups(@PathParam("requestlistId") long requestlistId) {
        return MaterialTransformHelper.transformAsRequestGroupDTOs(materialServices.getRequestGroups(requestlistId));
    }

    @GET
    @Path("v1/requestlists/excel/{requestlistId}")
    @Produces({ "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" })
    public Response exportProforma(@PathParam("requestlistId") long requestlistId, @QueryParam("whSiteId") String whSiteId) throws GloriaApplicationException {
        FileToExportDTO excelDTO = materialServices.exportProforma(requestlistId, whSiteId);
        ResponseBuilder responseBuilder = Response.ok(excelDTO.getContent(), MediaType.APPLICATION_OCTET_STREAM);
        responseBuilder.header("content-disposition", "attachment; filename=" + excelDTO.getName() + ".xlsx");
        responseBuilder.header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return responseBuilder.build();
    }

    @PUT
    @Path("v1/requestlists/{requestlistId}")
    @Produces(MediaType.APPLICATION_JSON)
    public RequestListDTO updateRequestListsV1(@QueryParam("action") String action,
            RequestListDTO requestListDTO) throws GloriaApplicationException {
        return MaterialTransformHelper.transformAsRequestListDTO(materialServices.updateRequestList(requestListDTO, action,
                                                                                                    securityServices.getLoggedInUserId(request)));
    }

    @PUT
    @Path("v1/requestlists/materiallines/{materiallineids}")
    public RequestListDTO createSendRequestListsV1(List<MaterialLineDTO> materialLineDTOs, @QueryParam("action") String action,
            @QueryParam("requiredDeliveryDate") DateTimeParam requiredDeliveryDateStr, @QueryParam("priority") Long priority,
            @QueryParam("deliveryAddressType") String deliveryAddressType, @QueryParam("deliveryAddressId") String deliveryAddessId,
            @QueryParam("deliveryAddressName") String deliveryAddressName, @QueryParam("requestlistoid") Long requestListOid) 
                    throws GloriaApplicationException {
        return MaterialTransformHelper.transformAsRequestListDTO(materialServices.manageRequestList(materialLineDTOs, action,
                                                                                                           requiredDeliveryDateStr.getDateTime(), priority,
                                                                                                           deliveryAddressType, deliveryAddessId,
                                                                                                           deliveryAddressName,
                                                                                                           securityServices.getLoggedInUserId(request),
                                                                                                           requestListOid));
    }

    @GET
    @Path("/v1/requestgroups")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getRequestGroups(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters, @QueryParam("whSiteId") String whSiteId,
            @QueryParam("userId") String userId) throws GloriaApplicationException {
        return PageUtil.updatePage(materialServices.getRequestGroup(rsGridParameters.getPageObject(), userId, whSiteId), response);
    }

    @GET
    @Path("v1/dispatchnotes/{dispatchnoteId}/requestgroups")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RequestGroupDTO> getRequestGroupsForADispatchNoteIdV1(@PathParam("dispatchnoteId") long dispatchNoteId) throws GloriaApplicationException {
        return MaterialTransformHelper.transformAsRequestGroupDTOs(materialServices.findRequestGroupsByDispatchNoteId(dispatchNoteId));
    }

    @GET
    @Path("v1/requestgroups/{requestgroupId}/materials")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MaterialLineDTO> getMaterialLinesForARequestGroupIdV1(@PathParam("requestgroupId") long requestgroupId) throws GloriaApplicationException {
        return MaterialTransformHelper.transformAsMaterialLineDTOs(materialServices.findMaterialLinesByRequestGroupId(requestgroupId));
    }

    @GET
    @Path("/v1/orderlines/{orderLineId}/orders")
    public OrderDTO getOrders(@PathParam("orderLineId") Long orderLineId) {
        return DeliveryHelper.transformAsDTO(deliveryServices.getOrderForOrderLine(orderLineId));
    }

    @GET
    @Path("/v1/deliveryschedules/{deliveryId}/deliverylogs")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DeliveryLogDTO> getDeliveryLogs(@PathParam("deliveryId") Long deliveryScheduleId) {
        return DeliveryHelper.transaformAsDeliveryLogDTOs(deliveryServices.getDeliveryLogs(deliveryScheduleId, EventType.DELIVERY_LOG.name()));
    }

    @POST
    @Path("/v1/deliveryschedules/{deliveryScheduleId}/deliverylogs/{deliveryLogId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public DeliveryLogDTO createDeliveryLineLog(@PathParam("deliveryScheduleId") Long deliveryScheduleId, DeliveryLogDTO eventLog) 
            throws GloriaApplicationException {
        return DeliveryHelper.transformAsDTO(deliveryServices.addDeliveryLog(deliveryScheduleId, EventType.DELIVERY_LOG.name(), eventLog,
                                                                             securityServices.getLoggedInUserId(request)));
    }

    @GET
    @Path("/v1/orderlines/{orderLineId}/orderlineversions")
    public List<OrderLineVersionDTO> getOrderLineVersions(@PathParam("orderLineId") Long orderLineId) {
        return DeliveryHelper.transformAsDTOs(deliveryServices.findAllOrderLineVersions(orderLineId));
    }

    @GET
    @Path("/v1/orderlines/current/excel")
    @Produces({ "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" })
    public Response exportSupplier(@QueryParam("ids") String orderLineIds, @QueryParam("includeDeliveryController") boolean includeDeliveryController,
            @QueryParam("includeOrderLineLogs") boolean includeOrderLineLogs, @QueryParam("includeOrderLogs") boolean includeOrderLogs,
            @QueryParam("includeProject") boolean includeProject, @QueryParam("includePlannedDispatchDate") boolean includePlannedDispatchDate,
            @QueryParam("excludeAgreedSTA") boolean excludeAgreedSTA, @Context UriInfo ui, @QueryParam("expectedArrivalFrom") String expectedArrivalFrom,
            @QueryParam("expectedArrivalTo") String expectedArrivalTo, @QueryParam("expectedArrival") String expectedArrival,
            @QueryParam("statusFlag") String statusFlag, @QueryParam("partNumber") String partNumber, @QueryParam("markedAsComplete") Boolean markedAsComplete,
            @QueryParam("status") String status, @QueryParam("deliveryControllerId") String deliveryControllerId,
            @QueryParam("internalExternal") String internalExternal, @QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters,
            @QueryParam("deliveryControllerTeam") String deliveryControllerTeam, @QueryParam("unassigned") boolean unassigned,
            @QueryParam("completeType") String completeType, @QueryParam("needDeliverySchedules") boolean loadDeliverySchedules)
            throws GloriaApplicationException, ParseException {
        Map<String, List<String>> queryParams = ui.getQueryParameters();
        List<Long> orderLineOids = new ArrayList<Long>();
        if (orderLineIds == null || orderLineIds.equalsIgnoreCase("null")) {
            PageObject pageObject = rsGridParameters.getPageObject();
            pageObject.setCurrentPage(1);
            pageObject.setResultsPerPage(Integer.MAX_VALUE);
            List<PageResults> pageResults = PageUtil.updatePage(deliveryServices.findOrderLinesForDeliveryControl(expectedArrivalFrom, expectedArrivalTo,
                                                                                                                  expectedArrival, null, null, null, 
                                                                                                                  null, null, null,statusFlag,
                                                                                                                  deliveryControllerId, internalExternal,
                                                                                                                  partNumber, markedAsComplete, status,
                                                                                                                  securityServices.getLoggedInUserId(request),
                                                                                                                  false, pageObject,
                                                                                                                  deliveryControllerTeam, unassigned,
                                                                                                                  completeType, queryParams,
                                                                                                                  loadDeliverySchedules), response);
            for (PageResults orderLineDTO : pageResults) {
                orderLineOids.add(((OrderLineDTO) orderLineDTO).getId());
            }
        } else {
            orderLineOids = GloriaFormateUtil.getValuesAsLong(orderLineIds);
        }
        FileToExportDTO fileToExportDTO = materialServices.exportSupplier(orderLineOids, includeDeliveryController, includeOrderLineLogs, includeOrderLogs,
                                                                          includeProject, includePlannedDispatchDate, excludeAgreedSTA);
        ResponseBuilder responseBuilder = Response.ok(fileToExportDTO.getContent(), MediaType.APPLICATION_OCTET_STREAM);
        responseBuilder.header("content-disposition", "attachment; filename=" + fileToExportDTO.getName() + ".xlsx");
        responseBuilder.header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return responseBuilder.build();
    }
    
    @GET
    @Path("/v1/orderlines")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getOrderlinesV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters,
            @QueryParam("deliveryControllerTeamId") long deliveryControllerTeamId, @QueryParam("status") String status)
            throws GloriaApplicationException {
        return PageUtil.updatePage(deliveryServices.findOrderLines(rsGridParameters.getPageObject(), deliveryControllerTeamId,
                                                                   securityServices.getLoggedInUserId(request), status), response);
    }

    @GET
    @Path("/v1/deliverynotelines/{deliveryNoteLineId}/deliverynotesublines")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DeliveryNoteSubLineDTO> getDeliveryNoteSublinesV1(@PathParam("deliveryNoteLineId") Long deliveryNoteLineId, @QueryParam("status") String status)
            throws GloriaApplicationException {
        return DeliveryHelper.transaformAsDeliveryNoteSubLineDTOs(deliveryServices.getDeliveryNoteSubLinesByDeliveryNoteLineId(deliveryNoteLineId, status));
    }

    @PUT
    @Path("/v1/deliverynotelines/{deliveryNoteLineId}/deliverynotesublines/{deliverynotesublineId}")
    @Produces(MediaType.APPLICATION_JSON)
    public DeliveryNoteSubLineDTO updateDeliveryNoteSublineV1(DeliveryNoteSubLineDTO deliveryNoteSubLineDTO) 
            throws GloriaApplicationException {
        return DeliveryHelper.transformAsDTO(deliveryServices.updateDeliveryNoteSubLine(deliveryNoteSubLineDTO));
    }

    @PUT
    @Path("v1/deliverynotelines/qiapprove")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DeliveryNoteLineDTO> approveQiDeliveryNotelinesV1(@QueryParam("deliveryNoteLineIds") String deliveryNoteLineIds,
            @QueryParam("userId") String userId) throws GloriaApplicationException {
        return DeliveryHelper.transaformAsDeliveryNoteLineDTOs(materialServices.qiApproveDeliveryNoteLine(deliveryNoteLineIds, userId), null);
    }
    
    /*
     * 
     * Material Overview page Pull button uses this interface to undertake a Quick pull which means that all the steps are done here
     */
    @PUT
    @Path("/v1/materiallines/{materialLineId}/pull")
    @Produces(MediaType.APPLICATION_JSON)
    public void quickPull(@QueryParam("quantity") int quantity,
            MaterialLineDTO materialLineDTO, @QueryParam("requestDeliveryDate") DateTimeParam requiredDeliveryDateStr,
            @QueryParam("deliveryAddressType") String deliveryAddressType, @QueryParam("deliveryAddressId") String deliveryAddressId,
            @QueryParam("deliveryAddressName") String deliveryAddressName)
            throws GloriaApplicationException {
         materialServices.sendQuickPull(materialLineDTO, requiredDeliveryDateStr.getDateTime(), deliveryAddressType, deliveryAddressId,
                                       deliveryAddressName, securityServices.getLoggedInUserId(request), quantity);
        return;
    }
}
