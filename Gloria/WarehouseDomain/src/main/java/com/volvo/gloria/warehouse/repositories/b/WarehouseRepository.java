package com.volvo.gloria.warehouse.repositories.b;

import java.util.List;
import java.util.Set;

import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.persistence.GenericRepository;
import com.volvo.gloria.warehouse.c.ZoneType;
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
 * Repository.
 * 
 * Specific to warehouse,storageroom,zone
 * 
 */
public interface WarehouseRepository extends GenericRepository<Warehouse, Long> {

    List<Warehouse> getWarehouseList();

    void addStorageRoom(StorageRoom storageRoom);

    void addBinLocation(BinLocation binLocation);

    void addPlacement(Placement placement);

    BinLocation findBinLocationByID(Long binLocationId);

    List<Placement> getPlacementByMaterialLine(Long id);

    List<StorageRoom> getStorageRooms();

    PageObject getStorageRoomsByWarehouseId(PageObject pageObject, long warehouseOid) throws GloriaApplicationException;

    void deleteStorageRoomById(long storageRoomOid) throws GloriaApplicationException;

    PageObject getZones(PageObject pageObject, long storageRoomOid) throws GloriaApplicationException;

    StorageRoom findStorageRoomById(Long storageRoomOid);

    StorageRoom updateStorageRoom(StorageRoom storageRoom);

    BinLocation findBinLocationById(long binLocationOid);

    void deleteBinLocation(long binLocationOid);

    Zone findZoneById(Long zoneOid);

    Zone updateZone(Zone zone);

    void deleteZone(Long zoneOid);

    Zone findZoneWithAilesById(long zoneOid);

    Warehouse findWarehouseBySiteId(String siteId);

    PageObject getAllBinLocationByWhSiteIdAndCode(String whSite, PageObject pageObject);

    List<BinLocation> findPlacementBinLocations(long materialLineOID);

    BinLocation findBinLocationByCode(String code, String whSiteId);

    List<Zone> findZonesByZoneTypeAndWhSiteId(String zoneType, String whSiteId);

    List<Zone> getZones(long storageroomId, String zoneType);

    List<BaySetting> getBaySettings(long aislerackRowId);

    Zone findZoneByWarehouseIdAndType(String type, Long warehouseOid);

    void addZone(Zone zone);

    Placement updatePlacement(Placement placement);

    QualityInspectionPart save(QualityInspectionPart instanceToSave);

    QualityInspectionSupplier save(QualityInspectionSupplier instanceToSave);

    QualityInspectionProject save(QualityInspectionProject instanceToSave);

    QualityInspectionSupplier findQualityInspectionSupplier(long qualityInspectionSupplierOId);

    QualityInspectionProject findQualityInspectionProject(long qualityInspectionProjectOId);

    QualityInspectionPart findQualityInspectionPart(Long qualityInspectionPartOId);

    QualityInspectionProject findQualityInspectionProjectByProject(String projectName, String whSiteId);

    QualityInspectionSupplier findQualityInspectionSupplierBySupplier(String supplierName, String whSiteId);

    List<QualityInspectionPart> findQualityInspectionPartByPartNameOrNumber(String partName, String partNumber, String whSiteId);

    List<QualityInspectionProject> findQualityInspectionProjects(String whSiteId);

    List<QualityInspectionPart> findQualityInspectionParts(String whSiteId);

    List<QualityInspectionSupplier> findQualityInspectionSuppliers(String whSiteId);
    
    void deleteQualityInspectionPartById(long qiPartOid) throws GloriaApplicationException;
    
    void deleteQualityInspectionProjectById(long qiProjectOid) throws GloriaApplicationException;
    
    void deleteQualityInspectionSupplierById(long qiSupplierOid) throws GloriaApplicationException;

    Printer save(Printer instanceToSave);
    
    List<Printer> findListOfPrinters(String whSiteId);

    Printer findPrinterBySiteIdAndName(String whSiteId, String name);

    Zone findZoneCodes(ZoneType type, String whSiteId);

    List<Warehouse>findWarehousesByWhsiteIds(List<String> whSiteIds);
    
    BinlocationBalance getBinlocationBalance(String partAffiliation, String partNumber, String partVersion, 
            String partModification, String binLocationCode, String siteId);

    BinlocationBalance save(BinlocationBalance binlocationBalance);

    void removeBinlocationBalance(BinlocationBalance binlocationBalance);

    PageObject getBinlocationBalancesByWhSite(PageObject pageObject, String whSite, String pPartNumber, String binLocationCode);

    BinlocationBalance findBinlocationBalanceById(long binlocationBalanceOid);

    List<Zone> findZones(String whSiteId);

    List<BinLocation> findBinlocations(String zone, String aisle, String bay, String level, String whSiteId);

    Placement findPlacementByID(Long placementId);

    void deletePlacement(Long placementOid);

    List<BinLocation> findBinLocations(String whSiteId, Set<String> binLocationCodes);

    PageObject getBinlocationBalancesByBinlocation(PageObject pageObject, String whSite, String binlocationCode, String partNumber, String partName,
            String partVersion, String partAffiliation, String partModification);

    PageObject getWarehouses(PageObject pageObject);

    List<BinlocationBalance> getBinLocationBalances(String whSite);
}
