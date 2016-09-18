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

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang3.time.DateUtils;

import com.volvo.gloria.authorization.b.ReportFilterServices;
import com.volvo.gloria.common.c.dto.reports.ReportFilterMaterialDTO;
import com.volvo.gloria.common.c.dto.reports.ReportFilterOrderDTO;
import com.volvo.gloria.common.c.dto.reports.ReportFilters;
import com.volvo.gloria.common.c.dto.reports.ReportGeneralDTO;
import com.volvo.gloria.common.c.dto.reports.ReportPartDeliveryPrecisionDTO;
import com.volvo.gloria.common.c.dto.reports.ReportPerformanceDTO;
import com.volvo.gloria.common.c.dto.reports.ReportWarehouseActionDTO;
import com.volvo.gloria.common.c.dto.reports.ReportWarehouseCostDTO;
import com.volvo.gloria.common.c.dto.reports.ReportWarehouseTransactionDTO;
import com.volvo.gloria.reports.ReportHelper;
import com.volvo.gloria.reports.b.ReportServices;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.paging.c.PageUtil;
import com.volvo.gloria.web.uiservices.mapper.DateTimeParam;
import com.volvo.gloria.web.uiservices.mapper.RsGridParameters;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Restful service for handling Report Modules.
 */
@Path("/report")
public class ReportUIService {

    private static final String RS_QUERY_PARAMETERS = "rsQueryParameters";

    @Context
    private HttpServletRequest request;

    @Context
    private HttpServletResponse response;

    private ReportFilterServices reportFilterServices = ServiceLocator.getService(ReportFilterServices.class);

    private ReportServices reportServices = ServiceLocator.getService(ReportServices.class);

    @GET
    @Path("v1/reportFilters")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ReportFilters> getReportFiltersV1(@QueryParam("reportType") String reportType, @QueryParam("userId") String userId)
            throws GloriaApplicationException {
        return ReportHelper.transformAsReportFilterDTOs(reportFilterServices.getReportFilters(reportType, userId));
    }

    @GET
    @Path("v1/reportFilters/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ReportFilters getReportFilterV1(@PathParam("id") long reportFilterId) throws GloriaApplicationException {
        return ReportHelper.transformAsReportFilterDTO(reportFilterServices.getReportFilterById(reportFilterId));
    }

    @GET
    @Path("v1/companyCodes")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getCompanyCodesV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters,
            @QueryParam("userId") String userId) throws GloriaApplicationException {
        return PageUtil.updatePage(reportServices.getCompanyCodeFilters(rsGridParameters.getPageObject(), userId), response);
    }

    @GET
    @Path("v1/suffix")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getSuffixV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters)
            throws GloriaApplicationException {
        return PageUtil.updatePage(reportServices.getSuffixFilters(rsGridParameters.getPageObject()), response);
    }

    @GET
    @Path("v1/projects")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getProjectsV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters, @QueryParam("companyCode") String companyCode,
            @QueryParam("userId") String userId, @QueryParam("id") String projectId) throws GloriaApplicationException {
        return PageUtil.updatePage(reportServices.getProjectFilters(rsGridParameters.getPageObject(), companyCode, userId, projectId), response);
    }

    @GET
    @Path("v1/warehouses")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getWarehousesV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters)
            throws GloriaApplicationException {
        return PageUtil.updatePage(reportServices.getWarehouseFilters(rsGridParameters.getPageObject()), response);
    }

    @GET
    @Path("v1/buildSeries")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getBuildSeriesV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters,
            @QueryParam("companyCode") String companyCode, @QueryParam("userId") String userId)
            throws GloriaApplicationException {
        return PageUtil.updatePage(reportServices.getBuildSerieFilters(rsGridParameters.getPageObject(), companyCode, userId), response);
    }

    @GET
    @Path("v1/testObjects")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getTestObjectsV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters) throws GloriaApplicationException {
        return PageUtil.updatePage(reportServices.getTestObjectFilters(rsGridParameters.getPageObject()), response);
    }

    @GET
    @Path("v1/supplierParmaIds")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getSupplierParmaIdsV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters,
            @QueryParam("companyCode") String companyCode, @QueryParam("suffix") String suffix, @QueryParam("userId") String userId)
            throws GloriaApplicationException {
        return PageUtil.updatePage(reportServices.getSupplierParmaIdFilters(rsGridParameters.getPageObject(), companyCode, suffix, userId), response);
    }

    @GET
    @Path("v1/supplierNames")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getSupplierNamesV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters,
            @QueryParam("companyCode") String companyCode, @QueryParam("suffix") String suffix, @QueryParam("userId") String userId)
            throws GloriaApplicationException {
        return PageUtil.updatePage(reportServices.getSupplierParmaNameFilters(rsGridParameters.getPageObject(), companyCode, suffix, userId), response);
    }

    @GET
    @Path("v1/mtrIds")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getMtrIdsV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters) throws GloriaApplicationException {
        return PageUtil.updatePage(reportServices.getMtrIdFilters(rsGridParameters.getPageObject()), response);
    }

    @GET
    @Path("v1/deliveryControllerIds")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getDeliveryControllerIdsV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters,
            @QueryParam("companyCode") String companyCode, @QueryParam("suffix") String suffix, @QueryParam("userId") String userId)
            throws GloriaApplicationException {
        return PageUtil.updatePage(reportServices.getDeliveryControllerIdFilters(rsGridParameters.getPageObject(), companyCode, suffix, userId), response);
    }

    @GET
    @Path("v1/deliveryControllerNames")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getDeliveryControllerNamesV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters,
            @QueryParam("companyCode") String companyCode, @QueryParam("suffix") String suffix, @QueryParam("userId") String userId)
            throws GloriaApplicationException {
        return PageUtil.updatePage(reportServices.getDeliveryControllerNameFilters(rsGridParameters.getPageObject(), companyCode, suffix, userId), response);
    }

    @GET
    @Path("v1/wbs")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getWbsV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters, @QueryParam("id") String wbsCode)
            throws GloriaApplicationException {
        return PageUtil.updatePage(reportServices.getWbsFilters(rsGridParameters.getPageObject(), wbsCode), response);
    }

    @GET
    @Path("v1/materialControllerTeams")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getMaterialControllerTeamsV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters) 
            throws GloriaApplicationException {
        return PageUtil.updatePage(reportServices.getMaterialControllerTeamFilters(rsGridParameters.getPageObject()), response);
    }

    @GET
    @Path("v1/references")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getReferencesV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters) throws GloriaApplicationException {
        return PageUtil.updatePage(reportServices.getReferenceFilters(rsGridParameters.getPageObject()), response);
    }

    @GET
    @Path("v1/phases")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getBuildNamesV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters) throws GloriaApplicationException {
        return PageUtil.updatePage(reportServices.getBuildNameFilters(rsGridParameters.getPageObject()), response);
    }

    @GET
    @Path("v1/storageRooms")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getStorageRoomssV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters) throws GloriaApplicationException {
        return PageUtil.updatePage(reportServices.getStorageRooms(rsGridParameters.getPageObject()), response);
    }

    @POST
    @Path("/v1/reportFilterOrders")
    public ReportFilters createOrderReportFilter(@QueryParam("userId") String userId, ReportFilterOrderDTO reportFilterOrderDTO)
            throws GloriaApplicationException {
        return ReportHelper.transformAsOrderFilterDTO(reportFilterServices.createOrderReportFilter(reportFilterOrderDTO, userId));
    }

    @DELETE
    @Path("/v1/reportFilters/{reportFilterId}")
    public void removeReportFilter(@PathParam("reportFilterId") long reportFilterId) throws GloriaApplicationException {
        reportFilterServices.removeReportFilter(reportFilterId);
    }

    @POST
    @Path("/v1/reportFilterMaterials")
    public ReportFilters createMaterialReportFilter(@QueryParam("userId") String userId, ReportFilterMaterialDTO reportFilterMaterialDTO)
            throws GloriaApplicationException {
        return ReportHelper.transformAsMaterialFilterDTO(reportFilterServices.createMaterialReportFilter(reportFilterMaterialDTO, userId));
    }

    @POST
    @Path("v1/reportFilterOrders/excel")
    @Produces({ "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" })
    public Response generateOrderReport(ReportFilterOrderDTO reportFilterOrderDTO, @QueryParam("fromDate") DateTimeParam fromDateParam,
            @QueryParam("toDate") DateTimeParam toDateParam, @QueryParam("userId") String userId) throws GloriaApplicationException {
        Date fromDate = null;
        Date toDate = null;
        if (fromDateParam != null) {
            fromDate = fromDateParam.getDateTime();
        }
        if (toDateParam != null) {
            toDate = toDateParam.getDateTime();
        }

        FileToExportDTO excelDTO = reportServices.generateOrderReport(reportFilterOrderDTO, fromDate, toDate, userId);
        ResponseBuilder responseBuilder = Response.ok(excelDTO.getContent(), MediaType.APPLICATION_OCTET_STREAM);
        responseBuilder.header("content-disposition", "attachment; filename=" + excelDTO.getName() + ".xlsx");
        responseBuilder.header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return responseBuilder.build();
    }

    @POST
    @Path("v1/reportFilterMaterials/excel")
    @Produces({ "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" })
    public Response generateMaterialReport(ReportFilterMaterialDTO reportFilterMaterialDTO, @QueryParam("userId") String userId)
            throws GloriaApplicationException {
        FileToExportDTO excelDTO = reportServices.generateMaterialReport(reportFilterMaterialDTO, userId);
        ResponseBuilder responseBuilder = Response.ok(excelDTO.getContent(), MediaType.APPLICATION_OCTET_STREAM);
        responseBuilder.header("content-disposition", "attachment; filename=" + excelDTO.getName() + ".xlsx");
        responseBuilder.header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return responseBuilder.build();
    }

    @POST
    @Path("v1/reportWarehouseAction/excel")
    @Produces({ "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" })
    public Response generateWarehouseActionReport(ReportWarehouseActionDTO reportWarehouseActionDTO, @QueryParam("fromDate") DateTimeParam fromDateParam,
            @QueryParam("toDate") DateTimeParam toDateParam) throws GloriaApplicationException {
        Date fromDate = getDefaultFromDate(fromDateParam);
        Date toDate = getDefaultToDate(toDateParam);
        FileToExportDTO excelDTO = reportServices.generateWarehouseActionReport(fromDate, toDate, reportWarehouseActionDTO);
        ResponseBuilder responseBuilder = Response.ok(excelDTO.getContent(), MediaType.APPLICATION_OCTET_STREAM);
        responseBuilder.header("content-disposition", "attachment; filename=" + excelDTO.getName() + ".xlsx");
        responseBuilder.header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return responseBuilder.build();
    }

    @POST
    @Path("v1/reportGeneralWarehouse/excel")
    @Produces({ "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" })
    public Response generateGeneralWarehouseReport(ReportGeneralDTO reportGeneralDTO) throws GloriaApplicationException {
        FileToExportDTO excelDTO = reportServices.generateGeneralWarehouseReport(reportGeneralDTO);
        ResponseBuilder responseBuilder = Response.ok(excelDTO.getContent(), MediaType.APPLICATION_OCTET_STREAM);
        responseBuilder.header("content-disposition", "attachment; filename=" + excelDTO.getName() + ".xlsx");
        responseBuilder.header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return responseBuilder.build();
    }

    @POST
    @Path("v1/reportWarehouseCost/excel")
    @Produces({ "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" })
    public Response generateWarehouseCostReport(ReportWarehouseCostDTO reportWarehouseCostDTO, @QueryParam("fromDate") DateTimeParam fromDateParam,
            @QueryParam("toDate") DateTimeParam toDateParam, @QueryParam("userId") String userId) throws GloriaApplicationException {
        Date fromDate = null;
        Date toDate = null;
        if (fromDateParam != null) {
            fromDate = fromDateParam.getDateTime();
        }
        if (toDateParam != null) {
            toDate = toDateParam.getDateTime();
        }
        FileToExportDTO excelDTO = reportServices.generateWarehouseCostReport(reportWarehouseCostDTO, fromDate, toDate, userId);
        ResponseBuilder responseBuilder = Response.ok(excelDTO.getContent(), MediaType.APPLICATION_OCTET_STREAM);
        responseBuilder.header("content-disposition", "attachment; filename=" + excelDTO.getName() + ".xlsx");
        responseBuilder.header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return responseBuilder.build();
    }

    @POST
    @Path("v1/reportPerformance/excel")
    @Produces({ "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" })
    public Response generatePerformanceReport(ReportPerformanceDTO reportPerformanceDTO, @QueryParam("dateType") String dateType,
            @QueryParam("fromDate") DateTimeParam fromDateParam, @QueryParam("toDate") DateTimeParam toDateParam) throws GloriaApplicationException {
        Date fromDate = getDefaultFromDate(fromDateParam);
        Date toDate = getDefaultToDate(toDateParam);
        FileToExportDTO excelDTO = reportServices.generatePeformanceReport(fromDate, toDate, dateType, reportPerformanceDTO);
        ResponseBuilder responseBuilder = Response.ok(excelDTO.getContent(), MediaType.APPLICATION_OCTET_STREAM);
        responseBuilder.header("content-disposition", "attachment; filename=" + excelDTO.getName() + ".xlsx");
        responseBuilder.header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return responseBuilder.build();
    }

    @POST
    @Path("v1/reportPartDeliveryPrecision/excel")
    @Produces({ "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" })
    public Response generatePartDeliveryPrecisionReport(ReportPartDeliveryPrecisionDTO reportPartDeliveryPrecisionDTO,
            @QueryParam("fromDate") DateTimeParam fromDateParam, @QueryParam("toDate") DateTimeParam toDateParam, @QueryParam("userId") String userId)
            throws GloriaApplicationException {
        Date fromDate = getDefaultFromDate(fromDateParam);
        Date toDate = getDefaultToDate(toDateParam);
        FileToExportDTO excelDTO = reportServices.generatePartDeliveryPrecisionReport(fromDate, toDate, userId, reportPartDeliveryPrecisionDTO);
        ResponseBuilder responseBuilder = Response.ok(excelDTO.getContent(), MediaType.APPLICATION_OCTET_STREAM);
        responseBuilder.header("content-disposition", "attachment; filename=" + excelDTO.getName() + ".xlsx");
        responseBuilder.header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return responseBuilder.build();
    }

    private Date getDefaultToDate(DateTimeParam toDateParam) {
        Date toDate = new Date();
        if (toDateParam != null) {
            Date date = toDateParam.getDateTime();
            if (date != null) {
                toDate = date;
            }
        }
        return DateUtil.getDateWithEndTime(toDate);
    }

    private Date getDefaultFromDate(DateTimeParam fromDateParam) {
        Date fromDate = DateUtils.addYears(new Date(), -1);
        if (fromDateParam != null) {
            Date date = fromDateParam.getDateTime();
            if (date != null) {
                fromDate = date;
            }
        }
        return DateUtil.getDateWithZeroTime(fromDate);
    }

    @GET
    @Path("v1/buyers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getBuyersV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters)
            throws GloriaApplicationException {
        return PageUtil.updatePage(reportServices.getBuyerFilters(rsGridParameters.getPageObject()), response);
    }

    @GET
    @Path("v1/parts")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getDistinctPartsFromOrderLineV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters)
            throws GloriaApplicationException {
        return PageUtil.updatePage(reportServices.getPartsFromOrderLine(rsGridParameters.getPageObject()), response);
    }

    @POST
    @Path("v1/reportWarehouseTransaction/excel")
    @Produces({ "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" })
    public Response generateGeneralWarehouseReport(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO,
            @QueryParam("fromDate") DateTimeParam fromDateParam, @QueryParam("toDate") DateTimeParam toDateParam) throws GloriaApplicationException {
        Date fromDate = getDefaultFromDate(fromDateParam);
        Date toDate = getDefaultToDate(toDateParam);
        FileToExportDTO excelDTO = reportServices.generateWarehouseTransactionReport(reportWarehouseTransactionDTO, fromDate, toDate);
        ResponseBuilder responseBuilder = Response.ok(excelDTO.getContent(), MediaType.APPLICATION_OCTET_STREAM);
        responseBuilder.header("content-disposition", "attachment; filename=" + excelDTO.getName() + ".xlsx");
        responseBuilder.header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return responseBuilder.build();
    }
}
