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
package com.volvo.gloria.warehouse.b;

import java.util.List;

import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.warehouse.c.ZoneType;
import com.volvo.gloria.warehouse.c.dto.AisleRackRowDTO;
import com.volvo.gloria.warehouse.c.dto.BaySettingDTO;
import com.volvo.gloria.warehouse.c.dto.BinlocationBalanceDTO;
import com.volvo.gloria.warehouse.c.dto.QualityInspectionPartDTO;
import com.volvo.gloria.warehouse.c.dto.QualityInspectionProjectDTO;
import com.volvo.gloria.warehouse.c.dto.QualityInspectionSupplierDTO;
import com.volvo.gloria.warehouse.c.dto.StorageRoomDTO;
import com.volvo.gloria.warehouse.c.dto.WarehouseDTO;
import com.volvo.gloria.warehouse.c.dto.ZoneDTO;
import com.volvo.gloria.warehouse.d.entities.AisleRackRow;
import com.volvo.gloria.warehouse.d.entities.BaySetting;
import com.volvo.gloria.warehouse.d.entities.BinLocation;
import com.volvo.gloria.warehouse.d.entities.BinlocationBalance;
import com.volvo.gloria.warehouse.d.entities.Placement;
import com.volvo.gloria.warehouse.d.entities.Printer;
import com.volvo.gloria.warehouse.d.entities.QualityInspectionPart;
import com.volvo.gloria.warehouse.d.entities.QualityInspectionProject;
import com.volvo.gloria.warehouse.d.entities.QualityInspectionSupplier;
import com.volvo.gloria.warehouse.d.entities.StorageRoom;
import com.volvo.gloria.warehouse.d.entities.Warehouse;
import com.volvo.gloria.warehouse.d.entities.Zone;

/**
 * WarehouseService interface. This is where the contract of this DOMAIN COMPONENT is defined.
 */
public interface WarehouseServices {

    void addWarehouse(Warehouse warehouse);

    void addWarehouses(List<Warehouse> warehouse);

    void addStorageRoom(StorageRoom storageRoom);

    void addBinLocation(BinLocation binLocation);

    void addPlacement(Placement place);

    void addPlacements(List<Placement> places);

    void addPlacement(Long binLocationId, Long materialLineDetailsId);

    Warehouse findWarehouseById(Long id);

    List<Warehouse> getWarehouseList();

    List<WarehouseDTO> getWarehouses();

    List<StorageRoom> getStorageRooms();

    Placement getPlacementByMaterialLine(Long id);

    void addZone(Zone zone);

    void updateZone(Zone zone);

    void addAisleRackRow(AisleRackRow aisleRackRow);

    PageObject getStorageRooms(PageObject pageObject, long warehouseOid) throws GloriaApplicationException;

    WarehouseDTO findWarehouseByWarehouseId(long warehouseId);

    void deleteStorageRoomById(long storageRoomOid) throws GloriaApplicationException;

    PageObject getZones(PageObject pageObject, long storageRoomOid) throws GloriaApplicationException;

    List<AisleRackRow> getAisleRackRow(long zoneID) throws GloriaApplicationException;

    AisleRackRow addAisleRackRow(long zoneId, AisleRackRowDTO aisleRackRowDTO);

    AisleRackRow updateAisleRackRow(AisleRackRowDTO aisleRackRowDTO);

    void deleteAisleRackRow(long aisleRackRowOid);

    void deleteBinLocation(long binLocationOid);

    StorageRoom addStorageRoom(StorageRoomDTO storageRoomDto, Long warehouseOid) throws GloriaApplicationException;

    StorageRoom findStorageRoomById(Long storageRoomOid);

    StorageRoom updateStorageRoom(StorageRoomDTO sRoomDto) throws GloriaApplicationException;

    Zone addZone(Long storageRoomOid, ZoneDTO zoneDto) throws GloriaApplicationException;

    Zone getZone(long zoneId);

    Zone updateZone(ZoneDTO zoneDTO) throws GloriaApplicationException;

    void deleteZone(Long zoneOid);

    StorageRoom getStorageRoomById(long storageRoomOid);

    Zone getZoneById(long zoneId);

    BinLocation getBinlocationById(long binlocationId);

    FileToExportDTO exportWarehouseAsXML(long warehouseId) throws GloriaApplicationException;

    void createWarehouseData(String xmlContent, String env);

    Warehouse findWarehouseBySiteId(String siteId);

    void addBaySetting(BaySetting baySetting);

    void updateBaySetting(BaySetting baySetting);

    BaySetting addBaySetting(long aisleRackRowOID, BaySettingDTO baySettingDTO);

    BaySetting updateBaySetting(BaySettingDTO baySettingDTO);

    void generateBinlocations(long warehouseOid, boolean printBarcodeLabels, String userId, String whSiteId) throws GloriaApplicationException;

    BinLocation findBinLocationById(long binlocationID);

    List<BinLocation> findPlacementBinLocations(long materialLineOID);

    BinLocation findBinLocationByCode(String code, String whSiteId);

    List<Zone> findZonesByZoneTypeAndWhSiteId(String zoneType, String userId, String whSiteId) throws GloriaApplicationException;

    void deleteBaySetting(Long baySettingOid);

    BaySetting findBaySetting(Long baySettingOid);

    PageObject getBinLocations(String userId, String whSiteId, PageObject pageObject) throws GloriaApplicationException;

    void print(String binlocationCode, int labelCopies, String siteId) throws GloriaApplicationException;

    List<Zone> getZones(long storageroomId, String zoneType);

    List<BaySetting> getBaySettings(long aislerackRowId);

    AisleRackRow getAisleRackRowById(long aislerackRowId);

    void updatePlacement(Placement placement);

    QualityInspectionPart findQualityinspectionPartById(long qualityinspectionPartOId);

    QualityInspectionPart createQualityinspectionPart(QualityInspectionPartDTO qualityinspectionPartDTO, String whSiteId) throws GloriaApplicationException;

    QualityInspectionPart updateQualityinspectionPart(QualityInspectionPartDTO qualityinspectionPartDTO, String whSiteId) throws GloriaApplicationException;

    QualityInspectionProject findQualityinspectionProjectById(long qualityinspectionProjectOId);

    QualityInspectionProject createQualityinspectionProject(QualityInspectionProjectDTO qualityinspectionProjectDTO, String whSiteId)
            throws GloriaApplicationException;

    QualityInspectionProject updateQualityinspectionProject(QualityInspectionProjectDTO qualityinspectionProjectDTO, String whSiteId)
            throws GloriaApplicationException;

    QualityInspectionSupplier findQualityinspectionSupplierById(long qualityinspectionSupplierOId);

    QualityInspectionSupplier createQualityinspectionSupplier(QualityInspectionSupplierDTO qualityinspectionSupplierDTO, String whSiteId)
            throws GloriaApplicationException;

    QualityInspectionSupplier updateQualityinspectionSupplier(QualityInspectionSupplierDTO qualityinspectionSupplierDTO, String whSiteId)
            throws GloriaApplicationException;
    
    void deleteQualityinspectionProject(long qualityinspectionProjectOID) throws GloriaApplicationException;

    List<QualityInspectionProject> findQualityinspectionProjects(String whSiteId);
    
    void deleteQualityinspectionPart(long qualityinspectionPartOID) throws GloriaApplicationException;

    List<QualityInspectionPart> findQualityinspectionPart(String whSiteId);
    
    void deleteQualityinspectionSupplier(long qualityinspectionSupplierOID) throws GloriaApplicationException;

    List<QualityInspectionSupplier> findQualityinspectionSupplier(String whSiteId);

    void validatePart(String partName, String partNumber) throws GloriaApplicationException;
    
    void createPrinterData(String xmlContent);
    
    List<Printer> findListOfPrinters(String whSiteId);

    Printer findPrinterBySiteIdAndName(String whSiteId, String name);

    Zone findZoneCodes(ZoneType type, String whSiteId);

    List<WarehouseDTO> findWarehousesByUserId(String userId) throws GloriaApplicationException;
    
    BinlocationBalance getBinlocationBalance(String partAffiliation, String partNumber, String partVersion, String partModification, String binLocationCode,
            String siteId);

    BinlocationBalance saveOrUpdateBinlocationBalance(BinlocationBalance binlocationBalance);

    void removeBinlocationBalance(BinlocationBalance binlocationBalance);

    PageObject getBinlocationBalancesByWhSite(PageObject pageObject, String userId, String whSiteId, String pPartNumber, String binLocationCode)
            throws GloriaApplicationException;

    BinlocationBalance findBinlocationBalanceById(long binlocationBalanceOid);

    void printPartLabels(List<BinlocationBalanceDTO> binlocationBalanceDTOs, long labelCopies, String whSiteId) throws GloriaApplicationException;
    
    List<Zone> getZones(String whSiteId);

    void printBinlocations(String binlocationCode, String zone, String aisle, String bay, String level, int copies, String whSiteId)
            throws GloriaApplicationException;

    void deletePlacement(long placementOid);

    Placement getPlacement(long placementId);

    PageObject getReportWarehouses(PageObject pageObject);

    PageObject getBinlocationBalancesByBinlocation(PageObject pageObject, String whSiteId, String binlocationCode, String partNumber, String partName,
            String partVersion, String partAffiliation, String partModification) throws GloriaApplicationException;

    List<BinlocationBalance> getBinLocationBalances(String whSite);
}
