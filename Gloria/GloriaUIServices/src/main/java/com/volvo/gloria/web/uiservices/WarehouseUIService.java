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

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.Site;
import com.volvo.gloria.procurematerial.b.DeliveryServices;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.ProcurementDtoTransformer;
import com.volvo.gloria.procurematerial.c.dto.OrderLineDTO;
import com.volvo.gloria.procurematerial.c.dto.PrintDTO;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.b.SecurityServices;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.paging.c.PageUtil;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.b.WarehouseServicesHelper;
import com.volvo.gloria.warehouse.c.dto.AisleRackRowDTO;
import com.volvo.gloria.warehouse.c.dto.BaySettingDTO;
import com.volvo.gloria.warehouse.c.dto.BinLocationDTO;
import com.volvo.gloria.warehouse.c.dto.BinlocationBalanceDTO;
import com.volvo.gloria.warehouse.c.dto.QualityInspectionPartDTO;
import com.volvo.gloria.warehouse.c.dto.QualityInspectionProjectDTO;
import com.volvo.gloria.warehouse.c.dto.QualityInspectionSupplierDTO;
import com.volvo.gloria.warehouse.c.dto.StorageRoomDTO;
import com.volvo.gloria.warehouse.c.dto.WarehouseDTO;
import com.volvo.gloria.warehouse.c.dto.ZoneDTO;
import com.volvo.gloria.warehouse.d.entities.Warehouse;
import com.volvo.gloria.web.uiservices.mapper.RsGridParameters;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Restful layer for managaing Warehouse Domain activities.
 * 
 */
@Path("/warehouse")
public class WarehouseUIService {

    private static final String RS_QUERY_PARAMETERS = "rsQueryParameters";

    @Context
    private HttpServletRequest request;

    @Context
    private HttpServletResponse response;

    private DeliveryServices deliveryServices = ServiceLocator.getService(DeliveryServices.class);
    private WarehouseServices warehouseServices = ServiceLocator.getService(WarehouseServices.class);
    private MaterialServices materialServices = ServiceLocator.getService(MaterialServices.class);
    private SecurityServices securityServices = ServiceLocator.getService(SecurityServices.class);
    private CommonServices commonServices = ServiceLocator.getService(CommonServices.class);
    private ProcurementDtoTransformer procurementDtoTransformer = ServiceLocator.getService(ProcurementDtoTransformer.class);

    @GET
    @Path("/v1/orderlines/current")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getWarehouseOrderLinesV1(@QueryParam("status") String status, @QueryParam("calculateInStock") boolean calculateInStock,
            @QueryParam("internalExternal") String internalExternal, @QueryParam("whSiteId") String whSiteId,
            @QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters) throws GloriaApplicationException, ParseException {

        return PageUtil.updatePage(deliveryServices.findOrderLinesForWarehouse(internalExternal, status, securityServices.getLoggedInUserId(request), whSiteId,
                                                                               calculateInStock, rsGridParameters.getPageObject()), response);
    }

    @PUT
    @Path("/v1/orderlines")
    public List<OrderLineDTO> updateOrderLinesV1(List<OrderLineDTO> orderLineDTOs) throws GloriaApplicationException {
        return procurementDtoTransformer.transformAsOrderLineDTOs(deliveryServices.updateOrderLines(orderLineDTOs, securityServices.getLoggedInUserId(request)),
                                                                  true);
    }

    @PUT
    @Path("/v1/orderlines/qimarking")
    public List<OrderLineDTO> updateQImarkingsOnOrderLinesV1(List<OrderLineDTO> orderLineDTOs) throws GloriaApplicationException {
        return procurementDtoTransformer.transformAsOrderLineDTOs(deliveryServices.updateOrderlinesQIMarking(orderLineDTOs,
                                                                                                             securityServices.getLoggedInUserId(request)), true);
    }

    @GET
    @Path("/v1/warehouses")
    @Produces(MediaType.APPLICATION_JSON)
    public List<WarehouseDTO> getWarehousesV1(@QueryParam("siteId") String siteId) {
        if (siteId != null && siteId.length() > 0) {
            List<WarehouseDTO> warehouseDTOs = new ArrayList<WarehouseDTO>();
            Warehouse warehouse = warehouseServices.findWarehouseBySiteId(siteId);
            if (warehouse != null) {
                Site site = commonServices.getSiteBySiteId(warehouse.getSiteId());
                // For erroneously setup data - site should always have a value
                if (site != null) {
                    warehouseDTOs.add(WarehouseServicesHelper.transformAsDTO(warehouse, site));
                }
            }
            return warehouseDTOs;
        } else {
            return warehouseServices.getWarehouses();
        }
    }

    @GET
    @Path("/v1/warehouses/{warehouseId}/export")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response exportWarehouseInfoV1(@PathParam("warehouseId") long warehouseId) throws GloriaApplicationException {
        FileToExportDTO xmlDTO = warehouseServices.exportWarehouseAsXML(warehouseId);
        return Response.ok(xmlDTO.getContent(), MediaType.APPLICATION_OCTET_STREAM)
                       .header("content-disposition", "attachment; filename = " + xmlDTO.getName() + ".xml").build();
    }

    @GET
    @Path("/v1/warehouses/{warehouseId}")
    @Produces(MediaType.APPLICATION_JSON)
    public WarehouseDTO getWarehouseByIdV1(@PathParam("warehouseId") long warehouseId) {
        return warehouseServices.findWarehouseByWarehouseId(warehouseId);
    }

    @GET
    @Path("/v1/warehouses/{warehouseId}/storagerooms/{storageRoomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public StorageRoomDTO getStorageRoomsV1(@PathParam("storageRoomId") long storageRoomId) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsDTO(warehouseServices.getStorageRoomById(storageRoomId));
    }

    @GET
    @Path("/v1/warehouses/{warehouseId}/storagerooms")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getStorageRoomsV1(@PathParam("warehouseId") long warehouseId, @QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters)
            throws GloriaApplicationException {
        return PageUtil.updatePage(warehouseServices.getStorageRooms(rsGridParameters.getPageObject(), warehouseId), response);
    }

    @POST
    @Path("/v1/warehouses/{warehouseId}/storagerooms")
    @Produces(MediaType.APPLICATION_JSON)
    public StorageRoomDTO createStorageRoomsV1(@PathParam("warehouseId") long warehouseId, StorageRoomDTO storageRoomDTO) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsDTO(warehouseServices.addStorageRoom(storageRoomDTO, warehouseId));
    }

    @PUT
    @Path("/v1/warehouses/{warehouseId}/storagerooms/{storageRoomId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public StorageRoomDTO updateStorageRoomV1(StorageRoomDTO storageRoomDTO) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsDTO(warehouseServices.updateStorageRoom(storageRoomDTO));
    }

    @DELETE
    @Path("/v1/warehouses/{warehouseId}/storagerooms/{storageRoomId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteStorageRoomV1(@PathParam("storageRoomId") long storageRoomId) throws GloriaApplicationException {
        warehouseServices.deleteStorageRoomById(storageRoomId);
    }

    @GET
    @Path("/v1/warehouses/{warehouseId}/storagerooms/{storageRoomId}/zones/{zoneId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ZoneDTO getZonesV1(@PathParam("zoneId") long zoneId) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsDTO(warehouseServices.getZoneById(zoneId));
    }

    @GET
    @Path("/v1/warehouses/{warehouseId}/storagerooms/{storageRoomId}/zones")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ZoneDTO> getZonesV1(@PathParam("storageRoomId") long storageroomId, @QueryParam("zoneType") String zoneType) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsZoneDTOs(warehouseServices.getZones(storageroomId, zoneType));
    }

    @POST
    @Path("/v1/warehouses/{warehouseId}/storagerooms/{storageRoomId}/zones")
    @Produces(MediaType.APPLICATION_JSON)
    public ZoneDTO createzonesV1(@PathParam("storageRoomId") long storageroomId, ZoneDTO zoneDTO) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsDTO(warehouseServices.addZone(storageroomId, zoneDTO));
    }

    @PUT
    @Path("/v1/warehouses/{warehouseId}/storagerooms/{storageRoomId}/zones/{zoneId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ZoneDTO updateZoneV1(ZoneDTO zoneDTO) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsDTO(warehouseServices.updateZone(zoneDTO));
    }

    @DELETE
    @Path("/v1/warehouses/{warehouseId}/storagerooms/{storageRoomId}/zones/{zoneId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteZoneV1(@PathParam("zoneId") long zoneId) throws GloriaApplicationException {
        warehouseServices.deleteZone(zoneId);
    }

    @GET
    @Path("/v1/warehouses/{warehouseId}/storagerooms/{storageRoomId}/zones/{zoneId}/aislerackrows")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AisleRackRowDTO> getAisleRackRowsV1(@PathParam("zoneId") long zoneId) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsAisleDTOs(warehouseServices.getAisleRackRow(zoneId));
    }

    @GET
    @Path("/v1/warehouses/{warehouseId}/storagerooms/{storageRoomId}/zones/{zoneId}/aislerackrows/{aislerackRowId}")
    @Produces(MediaType.APPLICATION_JSON)
    public AisleRackRowDTO getAisleRackRowV1(@PathParam("aislerackRowId") long aislerackRowId) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsDTO(warehouseServices.getAisleRackRowById(aislerackRowId));
    }

    @POST
    @Path("/v1/warehouses/{warehouseId}/storagerooms/{storageRoomId}/zones/{zoneId}/aislerackrows")
    @Produces(MediaType.APPLICATION_JSON)
    public AisleRackRowDTO createAisleRackRowsV1(@PathParam("zoneId") long zoneId, AisleRackRowDTO aisleRackRowDTO) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsDTO(warehouseServices.addAisleRackRow(zoneId, aisleRackRowDTO));
    }

    @PUT
    @Path("/v1/warehouses/{warehouseId}/storagerooms/{storageRoomId}/zones/{zoneId}/aislerackrows/{aislerackRowId}")
    @Produces(MediaType.APPLICATION_JSON)
    public AisleRackRowDTO updateAisleRackRowsV1(AisleRackRowDTO aisleRackRowDTO) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsDTO(warehouseServices.updateAisleRackRow(aisleRackRowDTO));
    }

    @DELETE
    @Path("/v1/warehouses/{warehouseId}/storagerooms/{storageRoomId}/zones/{zoneId}/aislerackrows/{aislerackRowId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteAisleRackRowsV1(@PathParam("aislerackRowId") long aisleRackRowId) throws GloriaApplicationException {
        warehouseServices.deleteAisleRackRow(aisleRackRowId);
    }

    @GET
    @Path("/v1/warehouses/{warehouseId}/storagerooms/{storageRoomId}/zones/{zoneId}/aislerackrows/{aislerackRowId}/baysettings")
    @Produces(MediaType.APPLICATION_JSON)
    public List<BaySettingDTO> getBaySettingsV1(@PathParam("aislerackRowId") long aislerackRowId) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsBaySettingDTOs(warehouseServices.getBaySettings(aislerackRowId));
    }

    @GET
    @Path("/v1/warehouses/{warehouseId}/storagerooms/{storageRoomId}/zones/{zoneId}/aislerackrows/{aislerackRowId}/baysettings/{baySettingId}")
    @Produces(MediaType.APPLICATION_JSON)
    public BaySettingDTO getBaySettingV1(@PathParam("baySettingId") long baySettingId) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsDTO(warehouseServices.findBaySetting(baySettingId));
    }

    @POST
    @Path("/v1/warehouses/{warehouseId}/storagerooms/{storageRoomId}/zones/{zoneId}/aislerackrows/{aislerackRowId}/baysettings")
    @Produces(MediaType.APPLICATION_JSON)
    public BaySettingDTO createBaySettingsV1(@PathParam("aislerackRowId") long aislerackRowId, BaySettingDTO baySettingDTO) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsDTO(warehouseServices.addBaySetting(aislerackRowId, baySettingDTO));
    }

    @PUT
    @Path("/v1/warehouses/{warehouseId}/storagerooms/{storageRoomId}/zones/{zoneId}/aislerackrows/{aislerackRowId}/baysettings/{baySettingId}")
    @Produces(MediaType.APPLICATION_JSON)
    public BaySettingDTO updateBaySettingsV1(BaySettingDTO baySettingDTO) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsDTO(warehouseServices.updateBaySetting(baySettingDTO));
    }

    @DELETE
    @Path("/v1/warehouses/{warehouseId}/storagerooms/{storageRoomId}/zones/{zoneId}/aislerackrows/{aislerackRowId}/baysettings/{baySettingId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteBaySettingsV1(@PathParam("baySettingId") long baySettingId) throws GloriaApplicationException {
        warehouseServices.deleteBaySetting(baySettingId);
    }

    @PUT
    @Path("/v1/warehouses/{warehouseId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void generateBinLocationsV1(@PathParam("warehouseId") long warehouseId, @QueryParam("action") String action,
            @QueryParam("printBarcodes") boolean printBarcodes, @QueryParam("whSiteId") String whSiteId) throws GloriaApplicationException {
        if ("generateBinLocations".equalsIgnoreCase(action)) {
            warehouseServices.generateBinlocations(warehouseId, printBarcodes, securityServices.getLoggedInUserId(request), whSiteId);
        }
    }

    @GET
    @Path("/v1/binlocations")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getBinlocationsV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters, @QueryParam("whSiteId") String whSiteId)
            throws GloriaApplicationException {
        PageObject pageObject = warehouseServices.getBinLocations(securityServices.getLoggedInUserId(request), whSiteId, rsGridParameters.getPageObject());
        return PageUtil.updatePage(pageObject, response);
    }

    @PUT
    @Path("/v1/print/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void doPrint(PrintDTO printDTO) {
        materialServices.print(printDTO.getId(), printDTO.getCopies());
    }

    @GET
    @Path("/v1/zones")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ZoneDTO> getZones(@QueryParam("type") String zoneType, @QueryParam("whSiteId") String whSiteId) throws GloriaApplicationException {
        String userId = securityServices.getLoggedInUserId(request);
        return WarehouseServicesHelper.transformAsZoneDTOs(warehouseServices.findZonesByZoneTypeAndWhSiteId(zoneType, userId, whSiteId));
    }

    @GET
    @Path("/v1/warehouses/{whSiteId}/qualityinspectionparts")
    @Produces(MediaType.APPLICATION_JSON)
    public List<QualityInspectionPartDTO> getQIPartsV1(@PathParam("whSiteId") String whSiteId) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsQualityInspectionPartDTOs(warehouseServices.findQualityinspectionPart(whSiteId));
    }

    @POST
    @Path("/v1/warehouses/{whSiteId}/qualityinspectionparts")
    @Produces(MediaType.APPLICATION_JSON)
    public QualityInspectionPartDTO createQualityinspectionPartV1(@PathParam("whSiteId") String whSiteId, QualityInspectionPartDTO qualityInspectionPartDTO)
            throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsDTO(warehouseServices.createQualityinspectionPart(qualityInspectionPartDTO, whSiteId));
    }

    @DELETE
    @Path("/v1/warehouses/{whSiteId}/qualityinspectionparts/{qiPartId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteQIPartV1(@PathParam("qiPartId") long qiPartId) throws GloriaApplicationException {
        warehouseServices.deleteQualityinspectionPart(qiPartId);
    }

    @PUT
    @Path("/v1/warehouses/{whSiteId}/qualityinspectionparts/{qiPartId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public QualityInspectionPartDTO updateQIPartV1(@PathParam("whSiteId") String whSiteId, QualityInspectionPartDTO qualityInspectionPartDTO)
            throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsDTO(warehouseServices.updateQualityinspectionPart(qualityInspectionPartDTO, whSiteId));
    }

    @GET
    @Path("/v1/warehouses/{whSiteId}/qualityinspectionprojects")
    @Produces(MediaType.APPLICATION_JSON)
    public List<QualityInspectionProjectDTO> getQIProjectsV1(@PathParam("whSiteId") String whSiteId) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsQualityInspectionProjectDTOs(warehouseServices.findQualityinspectionProjects(whSiteId));
    }

    @POST
    @Path("/v1/warehouses/{whSiteId}/qualityinspectionprojects")
    @Produces(MediaType.APPLICATION_JSON)
    public QualityInspectionProjectDTO createQIProjectV1(@PathParam("whSiteId") String whSiteId, QualityInspectionProjectDTO qualityInspectionProjectDTO)
            throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsDTO(warehouseServices.createQualityinspectionProject(qualityInspectionProjectDTO, whSiteId));
    }

    @PUT
    @Path("/v1/warehouses/{whSiteId}/qualityinspectionprojects/{qiProjectId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public QualityInspectionProjectDTO updateQIProjectV1(@PathParam("whSiteId") String whSiteId, QualityInspectionProjectDTO qualityInspectionProjectDTO)
            throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsDTO(warehouseServices.updateQualityinspectionProject(qualityInspectionProjectDTO, whSiteId));
    }

    @DELETE
    @Path("/v1/warehouses/{whSiteId}/qualityinspectionprojects/{qiProjectId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteQIProjectV1(@PathParam("qiProjectId") long qiProjectId) throws GloriaApplicationException {
        warehouseServices.deleteQualityinspectionProject(qiProjectId);
    }

    @GET
    @Path("/v1/warehouses/{whSiteId}/qualityinspectionsuppliers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<QualityInspectionSupplierDTO> getQISuppliersV1(@PathParam("whSiteId") String whSiteId) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsQualityInspectionSupplierDTOs(warehouseServices.findQualityinspectionSupplier(whSiteId));
    }

    @POST
    @Path("/v1/warehouses/{whSiteId}/qualityinspectionsuppliers")
    @Produces(MediaType.APPLICATION_JSON)
    public QualityInspectionSupplierDTO createQISupplierV1(@PathParam("whSiteId") String whSiteId, QualityInspectionSupplierDTO qualityInspectionSupplierDTO)
            throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsDTO(warehouseServices.createQualityinspectionSupplier(qualityInspectionSupplierDTO, whSiteId));
    }

    @PUT
    @Path("/v1/warehouses/{whSiteId}/qualityinspectionsuppliers/{qiSupplierId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public QualityInspectionSupplierDTO updateQISupplierV1(@PathParam("whSiteId") String whSiteId, QualityInspectionSupplierDTO qualityInspectionSupplierDTO)
            throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsDTO(warehouseServices.updateQualityinspectionSupplier(qualityInspectionSupplierDTO, whSiteId));
    }

    @DELETE
    @Path("/v1/warehouses/{whSiteId}/qualityinspectionsuppliers/{qiSupplierId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteQISupplierV1(@PathParam("qiSupplierId") long qiSupplierId) throws GloriaApplicationException {
        warehouseServices.deleteQualityinspectionSupplier(qiSupplierId);
    }

    @GET
    @Path("/v1/materiallines")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getMaterialLinesV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters, @QueryParam("status") String status,
            @QueryParam("calculateStockBalance") boolean calculateStockBalance, @QueryParam("suggestBinLocation") boolean suggestBinLocation,
            @QueryParam("whSiteId") String whSiteId, @QueryParam("partNo") String partNumber, @QueryParam("transportLabel") String transportLabel,
            @QueryParam("partAffiliation") String partAffiliation, @QueryParam("partVersion") String partVersion,
            @QueryParam("partModification") String partModification, @QueryParam("partName") String partName) throws GloriaApplicationException {
        return PageUtil.updatePage(materialServices.getMaterialLinesForWarehouse(rsGridParameters.getPageObject(), status, calculateStockBalance,
                                                                                 securityServices.getLoggedInUserId(request), whSiteId, suggestBinLocation,
                                                                                 StringUtils.upperCase(partNumber), transportLabel, partAffiliation,
                                                                                 partVersion, partModification, partName), response);
    }

    @GET
    @Path("/v1/materiallines/transferReturn")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getMaterialLinesV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters, @QueryParam("status") String status,
            @QueryParam("deliveryAddressId") String deliveryAddressId, @QueryParam("whSiteId") String whSiteId,
            @QueryParam("shipmentType") String shipmentType, @QueryParam("dispatchNoteNo") String dispatchNoteNo, @QueryParam("partNo") String partNo,
            @QueryParam("partVersion") String partVersion) throws GloriaApplicationException {
        return PageUtil.updatePage(materialServices.findMaterialLinesByDispatchNote(rsGridParameters.getPageObject(), null, whSiteId, deliveryAddressId,
                                                                                    shipmentType, dispatchNoteNo, partNo, partVersion, status), response);
    }

    @GET
    @Path("/v1/partbalance")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getBalanceForWhSiteV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters, @QueryParam("userId") String userId,
            @QueryParam("whSiteId") String whSiteId, @QueryParam("partNo") String partNumber, @QueryParam("binLocationCode") String binLocationCode)
            throws GloriaApplicationException {
        return PageUtil.updatePage(warehouseServices.getBinlocationBalancesByWhSite(rsGridParameters.getPageObject(), userId, whSiteId,
                                                                                    StringUtils.upperCase(partNumber), binLocationCode), response);
    }

    @GET
    @Path("/v1/binlocation/{binlocationcode}/partbalance")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PageResults> getBalanceForBinlocationV1(@QueryParam(RS_QUERY_PARAMETERS) RsGridParameters rsGridParameters,
            @PathParam("binlocationcode") String binlocationCode, @QueryParam("whSiteId") String whSiteId, @QueryParam("partNo") String partNumber,
            @QueryParam("partAffiliation") String partAffiliation, @QueryParam("partVersion") String partVersion,
            @QueryParam("partModification") String partModification, @QueryParam("partName") String partName) throws GloriaApplicationException {
        return PageUtil.updatePage(warehouseServices.getBinlocationBalancesByBinlocation(rsGridParameters.getPageObject(), whSiteId, binlocationCode,
                                                                                         partNumber, partName, partVersion, partAffiliation, partModification),
                                   response);
    }

    @PUT
    @Path("/v1/partbalance/{binlocationBalanceId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public BinlocationBalanceDTO updateBinlocationBalanceV1(@QueryParam("comments") String comments, BinlocationBalanceDTO binlocationBalanceDto,
            @QueryParam("userId") String userId, @QueryParam("action") String action, @QueryParam("quantity") long quantity,
            @QueryParam("binLocationCode") String binLocationCode) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsDTO(materialServices.updateBinlocationBalance(binlocationBalanceDto, comments, userId, action, quantity,
                                                                                                binLocationCode));
    }

    @GET
    @Path("/v1/users/{userId}/warehouses")
    @Produces(MediaType.APPLICATION_JSON)
    public List<WarehouseDTO> getUserWarehouses(@PathParam("userId") String userId) throws GloriaApplicationException {
        return warehouseServices.findWarehousesByUserId(userId);
    }

    @GET
    @Path("/v1/warehouses/{warehouseId}/zones")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ZoneDTO> getZonesV1(@PathParam("warehouseId") String warehouseId) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsZoneDTOs(warehouseServices.getZones(warehouseId));
    }

    @GET
    @Path("/v1/warehouses/{warehouseId}/zones/{zoneId}/aislerackrows")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AisleRackRowDTO> getAisleRackRowsV1(@PathParam("warehouseId") String warehouseId, @PathParam("zoneId") long zoneId)
            throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsAisleDTOs(warehouseServices.getAisleRackRow(zoneId));
    }

    @GET
    @Path("/v1/warehouses/{warehouseId}/aislerackrows/{aislerackRowId}/baysettings")
    @Produces(MediaType.APPLICATION_JSON)
    public List<BaySettingDTO> getBaySettingsV1(@PathParam("warehouseId") String warehouseId, @PathParam("aislerackRowId") long aislerackRowId)
            throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsBaySettingDTOs(warehouseServices.getBaySettings(aislerackRowId));
    }

    @GET
    @Path("/v1/warehouses/{warehouseId}/baysettings/{baySettingId}/levels")
    @Produces(MediaType.APPLICATION_JSON)
    public BaySettingDTO getLevelsV1(@PathParam("baySettingId") long baySettingId) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsDTO(warehouseServices.findBaySetting(baySettingId));
    }

    @GET
    @Path("v1/binlocations/printlabels")
    @Produces(MediaType.APPLICATION_JSON)
    public Response printBinLocations(@QueryParam("whSiteId") String whSiteId, @QueryParam("binlocation") String binlocation, @QueryParam("zone") String zone,
            @QueryParam("aisle") String aisleRackRow, @QueryParam("bay") String bay, @QueryParam("level") String level) throws GloriaApplicationException {
        warehouseServices.printBinlocations(binlocation, zone, aisleRackRow, bay, level, 1, whSiteId);
        return Response.ok().build();
    }

    @PUT
    @Path("/v1/materiallines/{materiallineIds}/lock")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void lockMaterialLinesV1(@PathParam("materiallineIds") String materiallineIds, @QueryParam("userId") String userId,
            @QueryParam("whSiteId") String whSiteId) throws GloriaApplicationException {
        materialServices.reserveMaterialLines(materiallineIds, userId, whSiteId);
    }

    @PUT
    @Path("/v1/materiallines/{materiallineIds}/unlock")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void unlockMaterialLinesV1(@PathParam("materiallineIds") String materiallineIds, @QueryParam("userId") String userId,
            @QueryParam("whSiteId") String whSiteId) throws GloriaApplicationException {
        materialServices.unlockMaterialLines(materiallineIds, userId, whSiteId);
    }

    @GET
    @Path("v1/binlocations/{code}")
    @Produces(MediaType.APPLICATION_JSON)
    public BinLocationDTO getBinLocationByCodeV1(@PathParam("code") String code, @QueryParam("whSiteId") String whSiteId) throws GloriaApplicationException {
        return WarehouseServicesHelper.transformAsDTO(warehouseServices.findBinLocationByCode(code, whSiteId));
    }

    @GET
    @Path("/v1/partbalance/excel")
    @Produces({ "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" })
    public Response exportInventory(@QueryParam("whSiteId") String whSiteId) throws GloriaApplicationException {
        FileToExportDTO excelDTO = WarehouseServicesHelper.exportPartBlanace(warehouseServices.getBinLocationBalances(whSiteId));
        ResponseBuilder responseBuilder = Response.ok(excelDTO.getContent(), MediaType.APPLICATION_OCTET_STREAM);
        responseBuilder.header("content-disposition", "attachment; filename=Inventory.xlsx");
        responseBuilder.header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return responseBuilder.build();
    }
}
