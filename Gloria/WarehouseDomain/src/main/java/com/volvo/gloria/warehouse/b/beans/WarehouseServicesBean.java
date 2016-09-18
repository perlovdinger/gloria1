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
package com.volvo.gloria.warehouse.b.beans;

import static com.volvo.gloria.util.c.GloriaParams.PROJECT_ID_LENGTH;

import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.Site;
import com.volvo.gloria.config.b.beans.ApplicationUtils;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaFormateUtil;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.ObjectJSON;
import com.volvo.gloria.util.b.PrintLabelServices;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.c.PrintLabelTemplate;
import com.volvo.gloria.util.c.dto.reports.ReportWarehouseDTO;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.warehouse.b.PrinterTransformer;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.b.WarehouseServicesHelper;
import com.volvo.gloria.warehouse.b.WarehouseTransformer;
import com.volvo.gloria.warehouse.c.BaySides;
import com.volvo.gloria.warehouse.c.Setup;
import com.volvo.gloria.warehouse.c.ZoneType;
import com.volvo.gloria.warehouse.c.dto.AisleRackRowDTO;
import com.volvo.gloria.warehouse.c.dto.AisleRackRowTransformerDTO;
import com.volvo.gloria.warehouse.c.dto.BaySettingDTO;
import com.volvo.gloria.warehouse.c.dto.BaySettingTransformerDTO;
import com.volvo.gloria.warehouse.c.dto.BinLocationTransformerDTO;
import com.volvo.gloria.warehouse.c.dto.BinlocationBalanceDTO;
import com.volvo.gloria.warehouse.c.dto.PrinterTransformerDTO;
import com.volvo.gloria.warehouse.c.dto.QualityInspectionPartDTO;
import com.volvo.gloria.warehouse.c.dto.QualityInspectionProjectDTO;
import com.volvo.gloria.warehouse.c.dto.QualityInspectionSupplierDTO;
import com.volvo.gloria.warehouse.c.dto.StorageRoomDTO;
import com.volvo.gloria.warehouse.c.dto.StorageRoomTransformerDTO;
import com.volvo.gloria.warehouse.c.dto.WarehouseDTO;
import com.volvo.gloria.warehouse.c.dto.WarehouseTransformerDTO;
import com.volvo.gloria.warehouse.c.dto.ZoneDTO;
import com.volvo.gloria.warehouse.c.dto.ZoneTransformerDTO;
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
import com.volvo.gloria.warehouse.repositories.b.AisleRepository;
import com.volvo.gloria.warehouse.repositories.b.WarehouseRepository;
import com.volvo.gloria.warehouse.util.BinLocationGenerator;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * This class implements Warehouse services.
 */
@ContainerManaged(name = "warehouseService")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class WarehouseServicesBean implements WarehouseServices {

    private static final String COMMA = ",";
    private static final int PARTNUMBER_LENGTH = 20;
    private static final int PARTNAME_LENGTH = 255;
    private static final Logger LOGGER = LoggerFactory.getLogger(WarehouseServicesBean.class);
    private static final int LABEL_COPIES = 1;
    private static final int LBL_MAX_PARTMOD_LENGTH = 60;
    private static final int LBL_MAX_PARTVER_LENGTH = 10;
    private static final int LBL_MAX_PARTNO_LENGTH = 20;
    private static final int LBL_MAX_PARTNAME_LENGTH = 40;
    private static final String ERROR_MSG_OPERATION_NOT_VALID = "This operation cannot be performed since the information "
            + "seen in the page has already been updated.";

    @Inject
    private WarehouseRepository warehouseRepository;
    @Inject
    private AisleRepository aisleRepository;

    @Inject
    private CommonServices commonServices;
    @Inject
    private WarehouseTransformer warehouseTransformer;

    @Inject
    private PrinterTransformer printerTransformer;

    @Inject
    private UserServices userServices;

    @Inject
    private PrintLabelServices printLabelServices;

    @Override
    public Warehouse findWarehouseById(Long id) {
        return warehouseRepository.findById(id);
    }

    @Override
    public List<Warehouse> getWarehouseList() {
        return warehouseRepository.getWarehouseList();
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','WH_SETUP','PROCURE', 'DELIVERY', 'PROCURE-INTERNAL', 'REQUESTER_FOR_PULL', 'VIEWER_PRICE', 'VIEWER', 'WH_QI','IT_SUPPORT')")
    public List<WarehouseDTO> getWarehouses() {
        List<Warehouse> warehouses = warehouseRepository.getWarehouseList();
        return WarehouseServicesHelper.transformToDTO(warehouses, commonServices);
    }

    @Override
    public void addWarehouse(Warehouse warehouse) {
        warehouseRepository.save(warehouse);
    }

    @Override
    public void addWarehouses(List<Warehouse> warehouses) {
        for (Warehouse warehouse : warehouses) {
            addWarehouse(warehouse);
        }
    }

    @Override
    public void addStorageRoom(StorageRoom storageRoom) {
        warehouseRepository.addStorageRoom(storageRoom);
    }

    @Override
    public void addBinLocation(BinLocation binLocation) {
        warehouseRepository.addBinLocation(binLocation);
    }

    @Override
    public void addPlacement(Placement placement) {
        warehouseRepository.addPlacement(placement);
    }

    @Override
    public void updatePlacement(Placement placement) {
        warehouseRepository.updatePlacement(placement);
    }

    @Override
    public void addPlacements(List<Placement> placements) {
        for (Placement placement : placements) {
            addPlacement(placement);
        }
    }

    public void addPlacement(Long binLocationId, Long materialLineDetailsId) {
        if (binLocationId != null && materialLineDetailsId != null) {
            BinLocation binLocation = warehouseRepository.findBinLocationByID(binLocationId);
            if (binLocation != null) {
                Placement placement = new Placement();
                placement.setBinLocation(binLocation);
                placement.setMaterialLineOID(materialLineDetailsId);
                addPlacement(placement);
            }
        }
    }

    public Placement getPlacementByMaterialLine(Long id) {
        List<Placement> placementList = warehouseRepository.getPlacementByMaterialLine(id);
        if (placementList != null && !placementList.isEmpty()) {
            return placementList.get(0);
        }
        return null;
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT')")
    public List<StorageRoom> getStorageRooms() {
        return warehouseRepository.getStorageRooms();
    }

    @Override
    public void addZone(Zone zone) {
        warehouseRepository.addZone(zone);
    }

    @Override
    public void updateZone(Zone zone) {
        warehouseRepository.updateZone(zone);
    }

    @Override
    public void addAisleRackRow(AisleRackRow aisleRackRow) {
        aisleRepository.save(aisleRackRow);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','WH_SETUP','IT_SUPPORT')")
    public PageObject getStorageRooms(PageObject pageObject, long warehouseOid) throws GloriaApplicationException {
        return warehouseRepository.getStorageRoomsByWarehouseId(pageObject, warehouseOid);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','WH_SETUP','PROCURE', 'DELIVERY', 'PROCURE-INTERNAL', 'REQUESTER_FOR_PULL', 'VIEWER_PRICE', 'VIEWER','IT_SUPPORT')")
    public WarehouseDTO findWarehouseByWarehouseId(long warehouseId) {
        Warehouse warehouse = findWarehouseById(warehouseId);
        if (warehouse == null) {
            return null;
        }
        Site site = commonServices.getSiteBySiteId(warehouse.getSiteId());
        if (site == null) {
            // Happens for erroneously setup test data
            return null; 
        }
        return WarehouseServicesHelper.transformAsDTO(warehouse, site);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_SETUP')")
    public void deleteStorageRoomById(long storageRoomOid) throws GloriaApplicationException {
        warehouseRepository.deleteStorageRoomById(storageRoomOid);
    }

    @Override
    public PageObject getZones(PageObject pageObject, long storageRoomOid) throws GloriaApplicationException {
        return warehouseRepository.getZones(pageObject, storageRoomOid);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','WH_SETUP','IT_SUPPORT','WH_QI')")
    public List<AisleRackRow> getAisleRackRow(long zoneID) throws GloriaApplicationException {
        return aisleRepository.getAisleRackRowByZoneID(zoneID);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_SETUP')")
    public AisleRackRow addAisleRackRow(long zoneId, AisleRackRowDTO aisleRackRowDTO) {
        AisleRackRow aisleRackRow = new AisleRackRow();
        aisleRackRow.setCode(aisleRackRowDTO.getCode());
        if (aisleRackRowDTO.getBaySides() != null) {
            aisleRackRow.setBaySides(BaySides.valueOf(aisleRackRowDTO.getBaySides()));
        }
        aisleRackRow.setNumberOfBay(aisleRackRowDTO.getNumberOfBays());
        WarehouseServicesHelper.updateBaySettingsForAisleRackRow(aisleRackRow, aisleRackRowDTO, aisleRepository);
        return aisleRepository.addAisleRackRow(zoneId, aisleRackRow);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_SETUP')")
    public AisleRackRow updateAisleRackRow(AisleRackRowDTO aisleRackRowDTO) {
        AisleRackRow aisleRackRow = aisleRepository.findById(aisleRackRowDTO.getId());
        aisleRackRow.setCode(aisleRackRowDTO.getCode());
        if (aisleRackRowDTO.getBaySides() != null) {
            aisleRackRow.setBaySides(BaySides.valueOf(aisleRackRowDTO.getBaySides()));
        }
        aisleRackRow.setNumberOfBay(aisleRackRowDTO.getNumberOfBays());
        WarehouseServicesHelper.updateBaySettingsForAisleRackRow(aisleRackRow, aisleRackRowDTO, aisleRepository);
        return aisleRepository.updateAisleRackRow(aisleRackRow);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','WH_SETUP')")
    public void deleteAisleRackRow(long aisleRackRowOid) {
        aisleRepository.deleteAisleRackRow(aisleRackRowOid);
    }

    @Override
    public void deleteBinLocation(long binLocationOid) {
        warehouseRepository.deleteBinLocation(binLocationOid);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_SETUP')")
    public StorageRoom addStorageRoom(StorageRoomDTO storageRoomDto, Long warehouseOid) throws GloriaApplicationException {
        StorageRoom storageRoom = null;
        if (storageRoomDto != null) {
            storageRoom = new StorageRoom();
            storageRoom.setCode(storageRoomDto.getCode());
            storageRoom.setName(storageRoomDto.getName());
            storageRoom.setDescription(storageRoomDto.getDescription());

            Warehouse warehouse = warehouseRepository.findById(warehouseOid);
            storageRoom.setWarehouse(warehouse);

            warehouseRepository.addStorageRoom(storageRoom);
            storageRoom = warehouseRepository.findStorageRoomById(storageRoom.getStorageRoomOid());
        }
        return storageRoom;
    }

    @Override
    public StorageRoom findStorageRoomById(Long storageRoomOid) {
        return warehouseRepository.findStorageRoomById(storageRoomOid);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_SETUP')")
    public StorageRoom updateStorageRoom(StorageRoomDTO storageRoomDto) throws GloriaApplicationException {
        Long storageRoomId = storageRoomDto.getId();
        StorageRoom storageRoom = warehouseRepository.findStorageRoomById(storageRoomId);

        if (storageRoomDto.getVersion() != storageRoom.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                 ERROR_MSG_OPERATION_NOT_VALID);
        }

        storageRoom.setCode(storageRoomDto.getCode());
        storageRoom.setName(storageRoomDto.getName());
        storageRoom.setDescription(storageRoomDto.getDescription());

        storageRoom = warehouseRepository.updateStorageRoom(storageRoom);
        return storageRoom;
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_SETUP')")
    public Zone addZone(Long storageRoomOid, ZoneDTO zoneDto) throws GloriaApplicationException {
        Zone zone = null;
        if (zoneDto != null) {
            StorageRoom storageRoom = warehouseRepository.findStorageRoomById(storageRoomOid);

            String type = zoneDto.getType();
            if (StringUtils.isEmpty(type)) {
                throw new GloriaApplicationException(GloriaExceptionConstants.ZONE_TYPE_REQUIRED, "Zone type is required");
            }
            if (type != null && !type.equalsIgnoreCase(ZoneType.STORAGE.name())) {
                Warehouse warehouse = storageRoom.getWarehouse();
                Zone zoneWithType = warehouseRepository.findZoneByWarehouseIdAndType(type, warehouse.getWarehouseOid());
                if (zoneWithType != null) {
                    throw new GloriaApplicationException(GloriaExceptionConstants.ZONE_TYPE_EXISTS,
                                                         "Zone with the specified type is existed. Please choose another zone type.");
                }
            }
            zone = new Zone();
            zone.setCode(zoneDto.getCode());
            zone.setName(zoneDto.getName());
            zone.setDescription(zoneDto.getDescription());
            if (type != null && type.length() > 0) {
                zone.setType(ZoneType.valueOf(type));
            }

            zone.setStorageRoom(storageRoom);
            warehouseRepository.addZone(zone);
            zone = warehouseRepository.findZoneById(zone.getZoneOid());
        }
        return zone;
    }

    @Override
    public Zone getZone(long zoneId) {
        return warehouseRepository.findZoneById(zoneId);
    }
    
    @Override
    public Placement getPlacement(long placementId) {
        return warehouseRepository.findPlacementByID(placementId);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_SETUP')")
    public Zone updateZone(ZoneDTO zoneDTO) throws GloriaApplicationException {
        Long zoneOid = zoneDTO.getId();

        Zone zone = warehouseRepository.findZoneWithAilesById(zoneOid);

        if (zoneDTO.getVersion() != zone.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET, ERROR_MSG_OPERATION_NOT_VALID);
        }

        zone.setCode(zoneDTO.getCode());
        zone.setName(zoneDTO.getName());
        zone.setDescription(zoneDTO.getDescription());
        zone = warehouseRepository.updateZone(zone);
        return zone;
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','WH_SETUP')")
    public void deleteZone(Long zoneOid) {
        warehouseRepository.deleteZone(zoneOid);
    }

    @Override
    public BinLocation getBinlocationById(long binlocationId) {
        return warehouseRepository.findBinLocationById(binlocationId);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','WH_SETUP','IT_SUPPORT')")
    public StorageRoom getStorageRoomById(long storageRoomOid) {
        return warehouseRepository.findStorageRoomById(storageRoomOid);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','WH_SETUP','IT_SUPPORT')")
    public Zone getZoneById(long zoneId) {
        return warehouseRepository.findZoneById(zoneId);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','WH_SETUP','IT_SUPPORT')")
    public FileToExportDTO exportWarehouseAsXML(long warehouseId) throws GloriaApplicationException {
        Warehouse warehouse = warehouseRepository.findById(warehouseId);
        Site site = null;
        if (warehouse.getSiteId() != null) {
            site = commonServices.getSiteBySiteId(warehouse.getSiteId());
        }
        FileToExportDTO warehouseXML = new FileToExportDTO();
        warehouseXML.setName(site.getSiteName());
        String xmlMessage = gererateXML(warehouse, site);
        warehouseXML.setContent(xmlMessage.getBytes(Charset.forName("UTF-8")));
        return warehouseXML;
    }

    private String gererateXML(Warehouse warehouse, Site site) throws GloriaApplicationException {
        try {
            return marshallXMLMessage(warehouse, site);
        } catch (JAXBException je) {
            LOGGER.error("Unable to marshall the warehouse information to XML. Exception is:", je);
            throw new GloriaSystemException(je, "Unable to export XML.", GloriaExceptionConstants.FAILED_XML_EXPORT);
        }
    }

    private String marshallXMLMessage(Warehouse wareHouse, Site site) throws JAXBException {
        QName qName = null;
        JAXBElement<com.volvo.group.init.warehouse._1_0.Warehouses> process = null;

        JAXBContext jaxbContext = JAXBContext.newInstance(com.volvo.group.init.warehouse._1_0.Warehouse.class.getPackage().getName());
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        com.volvo.group.init.warehouse._1_0.Warehouses wareHouseJaxbObject = transformWarehouse(wareHouse, site);

        qName = new QName("Warehouses");
        process = new JAXBElement<com.volvo.group.init.warehouse._1_0.Warehouses>(qName, com.volvo.group.init.warehouse._1_0.Warehouses.class,
                                                                                  wareHouseJaxbObject);
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(process, stringWriter);
        return stringWriter.toString();
    }

    public com.volvo.group.init.warehouse._1_0.Warehouses transformWarehouse(Warehouse warehouseEty, Site site) {
        com.volvo.group.init.warehouse._1_0.Warehouses listOfWarehouse = new com.volvo.group.init.warehouse._1_0.Warehouses();
        com.volvo.group.init.warehouse._1_0.Warehouse warehouse = new com.volvo.group.init.warehouse._1_0.Warehouse();
        warehouse.setSetUp(warehouseEty.getSetUp().toString());
        if (site != null) {
            warehouse.setSiteCode(site.getSiteCode());
            warehouse.setSiteId(site.getSiteId());
            warehouse.setSiteName(site.getSiteName());
        }
        List<StorageRoom> storageRooms = warehouseEty.getStorageRooms();
        if (storageRooms != null && !storageRooms.isEmpty()) {
            for (StorageRoom storageRoomEntity : storageRooms) {
                com.volvo.group.init.warehouse._1_0.StorageRoom storageRoom = createStorageRoom(storageRoomEntity);
                warehouse.getStorageRoom().add(storageRoom);
            }
        }
        listOfWarehouse.getWarehouse().add(warehouse);
        return listOfWarehouse;
    }

    private com.volvo.group.init.warehouse._1_0.StorageRoom createStorageRoom(StorageRoom storageRoomEntity) {
        com.volvo.group.init.warehouse._1_0.StorageRoom storageRoom = new com.volvo.group.init.warehouse._1_0.StorageRoom();
        storageRoom.setCode(storageRoomEntity.getCode());
        storageRoom.setDescription(storageRoomEntity.getDescription());
        storageRoom.setName(storageRoomEntity.getName());
        List<Zone> zones = storageRoomEntity.getZones();
        if (zones != null && !zones.isEmpty()) {
            for (Zone zonEntity : zones) {
                com.volvo.group.init.warehouse._1_0.Zon zon = createZone(zonEntity);
                storageRoom.getZon().add(zon);
            }
        }
        return storageRoom;
    }

    private com.volvo.group.init.warehouse._1_0.Zon createZone(Zone zonEntity) {
        com.volvo.group.init.warehouse._1_0.Zon zon = new com.volvo.group.init.warehouse._1_0.Zon();
        zon.setCode(zonEntity.getCode());
        zon.setDescription(zonEntity.getDescription());
        zon.setName(zonEntity.getName());
        zon.setType(zonEntity.getType().toString());
        List<BinLocation> binLocations = zonEntity.getBinLocations();
        if (binLocations != null && !binLocations.isEmpty()) {
            for (BinLocation binLocationEntity : binLocations) {
                com.volvo.group.init.warehouse._1_0.BinLocation binLocation = createBinLocation(binLocationEntity);
                zon.getBinLocation().add(binLocation);
            }
        }
        List<AisleRackRow> aisleRackRows = zonEntity.getAisleRackRows();
        if (aisleRackRows != null && !aisleRackRows.isEmpty()) {
            for (AisleRackRow aisleRackRowEntity : aisleRackRows) {
                com.volvo.group.init.warehouse._1_0.AisleRackRow aisleRackRow = createAisleRackRow(aisleRackRowEntity);
                zon.getAisleRackRow().add(aisleRackRow);
            }
        }

        return zon;
    }

    private com.volvo.group.init.warehouse._1_0.AisleRackRow createAisleRackRow(AisleRackRow aisleRackRowEntity) {
        com.volvo.group.init.warehouse._1_0.AisleRackRow aisleRackRow = new com.volvo.group.init.warehouse._1_0.AisleRackRow();
        aisleRackRow.setCode(aisleRackRowEntity.getCode());
        aisleRackRow.setNumberOfBay(aisleRackRowEntity.getNumberOfBay());
        aisleRackRow.setSetUp(aisleRackRowEntity.getSetUp().name());
        if (aisleRackRowEntity.getSetUp() == Setup.AISLE && aisleRackRowEntity.getBaySides() != null) {
            aisleRackRow.setBaySides(aisleRackRowEntity.getBaySides().name());
        }
        List<BaySetting> baySettings = aisleRackRowEntity.getBaySettings();
        if (baySettings != null && !baySettings.isEmpty()) {
            for (BaySetting baySettingEntity : baySettings) {
                com.volvo.group.init.warehouse._1_0.BaySetting baySetting = createBaySetting(baySettingEntity);
                aisleRackRow.getBaySetting().add(baySetting);
            }
        }
        return aisleRackRow;
    }

    private com.volvo.group.init.warehouse._1_0.BaySetting createBaySetting(BaySetting baySettingEntity) {
        com.volvo.group.init.warehouse._1_0.BaySetting baySetting = new com.volvo.group.init.warehouse._1_0.BaySetting();
        baySetting.setBayCode(baySettingEntity.getBayCode());
        baySetting.setNumberOfLevels(baySettingEntity.getNumberOfLevels());
        baySetting.setNumberOfPositions(baySettingEntity.getNumberOfPositions());
        return baySetting;
    }

    private com.volvo.group.init.warehouse._1_0.BinLocation createBinLocation(BinLocation binLocationEntity) {
        com.volvo.group.init.warehouse._1_0.BinLocation binLocation = new com.volvo.group.init.warehouse._1_0.BinLocation();
        if (binLocationEntity.getAllocation() != null) {
            binLocation.setAllocation(binLocationEntity.getAllocation().name());
        }
        binLocation.setBayCode(binLocationEntity.getBayCode());
        binLocation.setCode(binLocationEntity.getCode());
        binLocation.setLevel(binLocationEntity.getLevel());
        binLocation.setPosition(binLocationEntity.getPosition());
     
        return binLocation;
    }
   

    @Override
    public void createWarehouseData(String xmlContent, String env) {

        List<WarehouseTransformerDTO> warehouseTransformerDTOs = warehouseTransformer.transformWarehouse(xmlContent);

        List<Warehouse> wareHouseList = new ArrayList<Warehouse>();
        for (WarehouseTransformerDTO warehouseTransformerDTO : warehouseTransformerDTOs) {
           
            // Site need needs to be whSite in order to add
            Site site = commonServices.getSiteBySiteId(warehouseTransformerDTO.getSiteId());
            //&& site.isWhSite()) ?? how to check site as WAREHOUSE ??
            boolean whShouldBeAdded = false;
            if (env == null) {
                whShouldBeAdded = true;
            } else {
                List<String> companyCodesEnv = ApplicationUtils.getCompanyCodesForEnv(env);
                String companyCode = site.getCompanyCode();
                if (companyCodesEnv.contains(companyCode)) {
                    whShouldBeAdded = true;
                }
            }           
           
           
            if (site != null && whShouldBeAdded) {  
                Warehouse warehouse = new Warehouse();
                warehouse.setSiteId(warehouseTransformerDTO.getSiteId());

                warehouse.setQiSupported(warehouseTransformerDTO.isQiSupported());
                warehouse.setSetUp(warehouseTransformerDTO.getSetUp());
                addWarehouse(warehouse);

                List<StorageRoom> storageRooms = new ArrayList<StorageRoom>();
                for (StorageRoomTransformerDTO storageRoomDTO : warehouseTransformerDTO.getStorageRoomDTOs()) {
                    StorageRoom storageRoom = createStorageRoomData(warehouse, storageRoomDTO);
                    addStorageRoom(storageRoom);
                    List<Zone> zones = new ArrayList<Zone>();
                    for (ZoneTransformerDTO zoneDTO : storageRoomDTO.getZoneDTOs()) {
                        Zone zone = createZoneData(storageRoom, zoneDTO);
                        if (zoneDTO.getAisleTransformerDTOs() != null && !zoneDTO.getAisleTransformerDTOs().isEmpty()) {
                            List<AisleRackRow> aisleRackRows = new ArrayList<AisleRackRow>();
                            for (AisleRackRowTransformerDTO aisleRackRowTransformerDTO : zoneDTO.getAisleTransformerDTOs()) {
                                AisleRackRow aisleRackRow = createAisleRackRowData(zone, aisleRackRowTransformerDTO);
                                addAisleRackRow(aisleRackRow);
                                aisleRackRows.add(aisleRackRow);
                                createAilseRackRowAndBaySetting(aisleRackRowTransformerDTO, aisleRackRow);
                            }
                            zone.setAisleRackRows(aisleRackRows);
                        }
                        if (zoneDTO.getBinLocationTransformerDTOs() != null && !zoneDTO.getBinLocationTransformerDTOs().isEmpty()) {
                            List<BinLocation> binLocations = new ArrayList<BinLocation>();
                            for (BinLocationTransformerDTO binLocationTransformerDTO : zoneDTO.getBinLocationTransformerDTOs()) {
                                BinLocation binLocation = createBinLocationData(zone, binLocationTransformerDTO);
                                addBinLocation(binLocation);
                                binLocations.add(binLocation);
                            }

                            zone.setBinLocations(binLocations);
                        }
                        addZone(zone);
                        zones.add(zone);
                    }
                    storageRoom.setZones(zones);
                    storageRooms.add(storageRoom);
                }
                warehouse.setStorageRooms(storageRooms);
                wareHouseList.add(warehouse);
            }
        }
    }

    private void createAilseRackRowAndBaySetting(AisleRackRowTransformerDTO aisleRackRowTransformerDTO, AisleRackRow aisleRackRow) {
        if (aisleRackRowTransformerDTO.getBaySettingTransformerDTOs() != null && !aisleRackRowTransformerDTO.getBaySettingTransformerDTOs().isEmpty()) {
            List<BaySetting> baySettings = new ArrayList<BaySetting>();
            for (BaySettingTransformerDTO baySettingTransformerDTO : aisleRackRowTransformerDTO.getBaySettingTransformerDTOs()) {
                BaySetting baySetting = createBaySettingData(aisleRackRow, baySettingTransformerDTO);
                baySettings.add(baySetting);
            }
            aisleRackRow.setBaySettings(baySettings);
        }
    }

    private BinLocation createBinLocationData(Zone zone, BinLocationTransformerDTO binLocationTransformerDTO) {
        BinLocation binLocation = new BinLocation();
        if (binLocationTransformerDTO.getAllocation() != null) {
            binLocation.setAllocation(binLocationTransformerDTO.getAllocation());
        }
        binLocation.setAisleRackRowCode(binLocationTransformerDTO.getAisleRackRowCode());
        binLocation.setBayCode(binLocationTransformerDTO.getBay());
        binLocation.setCode(binLocationTransformerDTO.getCode());
        binLocation.setLevel(binLocationTransformerDTO.getLevel());
        binLocation.setPosition(binLocationTransformerDTO.getPosition());
        binLocation.setZone(zone);
        return binLocation;
    }

    private BaySetting createBaySettingData(AisleRackRow aisleRackRow, BaySettingTransformerDTO baySettingTransformerDTO) {
        BaySetting baySetting = new BaySetting();
        baySetting.setAisleRackRow(aisleRackRow);
        baySetting.setBayCode(baySettingTransformerDTO.getBayCode());
        baySetting.setNumberOfLevels(baySettingTransformerDTO.getNumberOfLevels());
        baySetting.setNumberOfPositions(baySettingTransformerDTO.getNumberOfPositions());
        addBaySetting(baySetting);
        return baySetting;
    }

    private AisleRackRow createAisleRackRowData(Zone zone, AisleRackRowTransformerDTO aisleRackRowTransformerDTO) {
        AisleRackRow aisleRackRow = new AisleRackRow();
        if (aisleRackRowTransformerDTO.getSetUp() == Setup.AISLE) {
            aisleRackRow.setBaySides(aisleRackRowTransformerDTO.getBaySides());
        }
        aisleRackRow.setCode(aisleRackRowTransformerDTO.getCode());
        aisleRackRow.setNumberOfBay(aisleRackRowTransformerDTO.getNumberOfBay());
        aisleRackRow.setSetUp(aisleRackRowTransformerDTO.getSetUp());
        aisleRackRow.setZone(zone);
        return aisleRackRow;
    }

    private Zone createZoneData(StorageRoom storageRoom, ZoneTransformerDTO zoneDTO) {
        Zone zone = new Zone();
        zone.setCode(zoneDTO.getCode());
        zone.setName(zoneDTO.getName());
        zone.setDescription(zoneDTO.getDescription());
        zone.setType(zoneDTO.getType());
        zone.setStorageRoom(storageRoom);
        return zone;
    }

    private StorageRoom createStorageRoomData(Warehouse warehouse, StorageRoomTransformerDTO storageRoomDTO) {
        StorageRoom storageRoom = new StorageRoom();
        storageRoom.setCode(storageRoomDTO.getCode());
        storageRoom.setName(storageRoomDTO.getName());
        storageRoom.setDescription(storageRoomDTO.getDescription());
        storageRoom.setWarehouse(warehouse);
        return storageRoom;
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','WH_SETUP',GR_ONLY','IT_SUPPORT')")
    public Warehouse findWarehouseBySiteId(String siteId) {
        return warehouseRepository.findWarehouseBySiteId(siteId);
    }

    @Override
    public void addBaySetting(BaySetting baySetting) {
        aisleRepository.addBaySetting(baySetting);
    }

    @Override
    public void updateBaySetting(BaySetting baySetting) {
        aisleRepository.updateBaySetting(baySetting);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_SETUP')")
    public BaySetting addBaySetting(long aisleRackRowOID, BaySettingDTO baySettingDTO) {
        BaySetting baySetting = new BaySetting();
        AisleRackRow aisleRackRow = aisleRepository.findById(aisleRackRowOID);
        aisleRackRow.setNumberOfBay(aisleRackRow.getNumberOfBay() + 1);
        baySetting.setAisleRackRow(aisleRackRow);
        baySetting.setBayCode(baySettingDTO.getBayCode());
        baySetting.setNumberOfLevels(baySettingDTO.getNumberOfLevels());
        baySetting.setNumberOfPositions(baySettingDTO.getNumberOfPositions());
        return aisleRepository.addBaySetting(baySetting);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_SETUP')")
    public BaySetting updateBaySetting(BaySettingDTO baySettingDTO) {
        BaySetting baySetting = aisleRepository.findBaySetting(baySettingDTO.getId());
        baySetting.setBayCode(baySettingDTO.getBayCode());
        baySetting.setNumberOfLevels(baySettingDTO.getNumberOfLevels());
        baySetting.setNumberOfPositions(baySettingDTO.getNumberOfPositions());
        return aisleRepository.updateBaySetting(baySetting);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_SETUP')")
    public void generateBinlocations(long warehouseOid, boolean printBarcodeLabels, String userId, String whSiteId) throws GloriaApplicationException {
        userServices.validateAccess(userId, whSiteId);

        if (warehouseOid > 0) {
            Warehouse warehouse = warehouseRepository.findById(warehouseOid);
            List<StorageRoom> storageRooms = warehouse.getStorageRooms();
            List<BinLocation> generatedBinLocations = new ArrayList<BinLocation>();
            generateBinLocations(storageRooms, generatedBinLocations);

            if (printBarcodeLabels && generatedBinLocations != null && !generatedBinLocations.isEmpty()) {
                for (BinLocation binLocation : generatedBinLocations) {
                    print(binLocation.getCode(), LABEL_COPIES, whSiteId);
                }
            }
        }
    }

    private void generateBinLocations(List<StorageRoom> storageRooms, List<BinLocation> generatedBinLocations) throws GloriaApplicationException {
        if (storageRooms != null && !storageRooms.isEmpty()) {
            for (StorageRoom storageRoom : storageRooms) {
                List<Zone> zones = storageRoom.getZones();
                if (zones != null && !zones.isEmpty()) {
                    for (Zone zone : zones) {
                        if (zone.getType().equals(ZoneType.STORAGE)) {
                            generateBinLocationForAisleRackRow(zone, generatedBinLocations);
                        } else {
                            generateBinLocationForNonStorageZones(zone, generatedBinLocations);
                        }
                    }
                }
            }
        }
    }

    private void generateBinLocationForNonStorageZones(Zone zone, List<BinLocation> generatedBinLocations) throws GloriaApplicationException {
        if (StringUtils.isEmpty(zone.getStorageRoom().getCode())) {
            throw new GloriaApplicationException(GloriaExceptionConstants.STORAGE_ROOM_CODE_REQUIRED, "StorageRoom code is missing.");
        } else if (StringUtils.isEmpty(zone.getCode())) {
            throw new GloriaApplicationException(GloriaExceptionConstants.ZONE_CODE_REQUIRED, "Zone code is missing.");
        }
        for (BinLocation binLocation : zone.getBinLocations()) {
            warehouseRepository.deleteBinLocation(binLocation.getBinLocationOid());
        }
        zone.getBinLocations().clear();
        zone.setBinLocations(BinLocationGenerator.generate(zone));
        generatedBinLocations.addAll(zone.getBinLocations());
        warehouseRepository.updateZone(zone);
    }

    private void generateBinLocationForAisleRackRow(Zone zone, List<BinLocation> generatedBinLocations) throws GloriaApplicationException {

        List<BinLocation> binLocations = zone.getBinLocations();
        if (binLocations != null && !binLocations.isEmpty()) {
            for (BinLocation binLocation : binLocations) {
                warehouseRepository.deleteBinLocation(binLocation.getBinLocationOid());
            }
            binLocations.clear();
        }

        List<AisleRackRow> aisleRackRows = zone.getAisleRackRows();
        if (aisleRackRows != null && !aisleRackRows.isEmpty()) {
            for (AisleRackRow aisleRackRow : aisleRackRows) {
                List<BaySetting> baySettings = aisleRackRow.getBaySettings();
                if (baySettings != null && !baySettings.isEmpty()) {
                    for (BaySetting baySetting : baySettings) {
                        StorageRoom storageRoom = zone.getStorageRoom();
                        validate(zone, aisleRackRow, baySetting, storageRoom);
                        binLocations.addAll(BinLocationGenerator.generate(baySetting));
                    }
                }
            }
            zone.setBinLocations(binLocations);
            generatedBinLocations.addAll(binLocations);
            warehouseRepository.updateZone(zone);
        }
    }

    private void validate(Zone zone, AisleRackRow aisleRackRow, BaySetting baySetting, StorageRoom storageRoom) throws GloriaApplicationException {
        if (StringUtils.isEmpty(storageRoom.getCode())) {
            throw new GloriaApplicationException(GloriaExceptionConstants.STORAGE_ROOM_CODE_REQUIRED, "StorageRoom code is missing.");
        } else if (StringUtils.isEmpty(zone.getCode())) {
            throw new GloriaApplicationException(GloriaExceptionConstants.ZONE_CODE_REQUIRED, "Zone code is missing.");
        } else if (StringUtils.isEmpty(aisleRackRow.getCode())) {
            throw new GloriaApplicationException(GloriaExceptionConstants.AISLE_RACK_ROW_CODE_REQUIRED, "AisleRackRow code is missing.");
        } else if (StringUtils.isEmpty(baySetting.getBayCode())) {
            throw new GloriaApplicationException(GloriaExceptionConstants.BAY_CODE_REQUIRED, "Bay code is missing.");
        } else if (baySetting.getNumberOfLevels() < 1) {
            throw new GloriaApplicationException(GloriaExceptionConstants.NO_OF_LEVEL_REQUIRED, "No Of Levels should be greater than zero.");
        } else if (baySetting.getNumberOfPositions() < 1) {
            throw new GloriaApplicationException(GloriaExceptionConstants.NO_OF_POSITION_REQUIRED, "No Of Positions should be greater than zero.");
        }
    }

    @Override
    public BinLocation findBinLocationById(long binlocationID) {
        return warehouseRepository.findBinLocationById(binlocationID);
    }

    @Override
    public List<BinLocation> findPlacementBinLocations(long materialLineOID) {
        return warehouseRepository.findPlacementBinLocations(materialLineOID);
    }

    @Override
    public BinLocation findBinLocationByCode(String code, String whSiteId) {
        return warehouseRepository.findBinLocationByCode(code, whSiteId);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','WH_SETUP','IT_SUPPORT')")
    public List<Zone> findZonesByZoneTypeAndWhSiteId(String zoneType, String userId, String whSiteId) throws GloriaApplicationException {
        userServices.validateAccess(userId, whSiteId);
        return warehouseRepository.findZonesByZoneTypeAndWhSiteId(zoneType, whSiteId);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_SETUP')")
    public void deleteBaySetting(Long baySettingOid) {
        aisleRepository.deleteBaySetting(baySettingOid);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','WH_SETUP','IT_SUPPORT', 'WH_QI')")
    public BaySetting findBaySetting(Long baySettingOid) {
        return aisleRepository.findBaySetting(baySettingOid);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','WH_SETUP', 'WH_QI','IT_SUPPORT')")
    public PageObject getBinLocations(String userId, String whSiteId, PageObject pageObject) throws GloriaApplicationException {
        userServices.validateAccess(userId, whSiteId);
        return warehouseRepository.getAllBinLocationByWhSiteIdAndCode(whSiteId, pageObject);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','WH_SETUP','IT_SUPPORT')")
    public List<Zone> getZones(long storageroomId, String zoneType) {
        return warehouseRepository.getZones(storageroomId, zoneType);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','WH_SETUP','IT_SUPPORT','WH_QI')")
    public List<BaySetting> getBaySettings(long aislerackRowId) {
        return warehouseRepository.getBaySettings(aislerackRowId);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_SETUP')")
    public void print(String binlocationCode, int labelCopies, String siteId) throws GloriaApplicationException {
        List<Printer> printers = warehouseRepository.findListOfPrinters(siteId);
        if (printers != null && !printers.isEmpty()) {
            String printerInfo = printers.get(0).getHostAddress();
            printLabelServices.doPrint(String.format(PrintLabelTemplate.ZPL_LABEL_TEMPLATE_BINLOCATION_CODE,
                                                         binlocationCode, labelCopies), printerInfo);
        } else {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_PRINTER_CONFIGURATION, "Printers are not configured correctly");
        }
    }

    @Override
    public void printPartLabels(List<BinlocationBalanceDTO> binlocationBalanceDTOs, long labelCopies, String whSiteId) throws GloriaApplicationException {
        List<Printer> printers = warehouseRepository.findListOfPrinters(whSiteId);
        if (printers != null && !printers.isEmpty()) {
            String printerInfo = printers.get(0).getHostAddress();
            for (BinlocationBalanceDTO binlocationBalanceDTO : binlocationBalanceDTOs) {
                long copies = labelCopies;
                if (copies == 0) {
                    copies = binlocationBalanceDTO.getQuantity();
                }

                ObjectJSON objectJSON = new ObjectJSON();

                String truncatedPartMod = GloriaFormateUtil.truncate(binlocationBalanceDTO.getPartModification(), LBL_MAX_PARTMOD_LENGTH);
                String truncatedPartVersion = GloriaFormateUtil.truncate(binlocationBalanceDTO.getPartVersion(), LBL_MAX_PARTVER_LENGTH);
                String truncatedPartNumber = GloriaFormateUtil.truncate(binlocationBalanceDTO.getPartNumber(), LBL_MAX_PARTNO_LENGTH);
                String truncatedPartName = GloriaFormateUtil.truncate(binlocationBalanceDTO.getPartName(), LBL_MAX_PARTNAME_LENGTH);

                if (!StringUtils.isEmpty(binlocationBalanceDTO.getPartAffiliation())) {
                    objectJSON.addItem("a", binlocationBalanceDTO.getPartAffiliation());
                }
                objectJSON.addItem("n", truncatedPartName);
                objectJSON.addItem("no", truncatedPartNumber);
                objectJSON.addItem("v", truncatedPartVersion);
                objectJSON.addItem("m", truncatedPartMod);

                // padding to maintain the 2D bar code size
                if ((PrintLabelTemplate.MAX2D_LABEL_DATA_LENGTH - objectJSON.jsonValue().length()) > 0) {
                    objectJSON.addItem("em", StringUtils.rightPad("", PrintLabelTemplate.MAX2D_LABEL_DATA_LENGTH - objectJSON.jsonValue().length()));
                }

                printLabelServices.doPrint(String.format(PrintLabelTemplate.ZPL_2D_LABEL_TEMPLATE_PART, objectJSON.jsonValueWithHexaDecimal(),
                                                         GloriaFormateUtil.hexaDecimalValue(truncatedPartMod),
                                                         GloriaFormateUtil.hexaDecimalValue(truncatedPartVersion),
                                                         GloriaFormateUtil.hexaDecimalValue(truncatedPartNumber),
                                                         GloriaFormateUtil.hexaDecimalValue(truncatedPartName), copies), printerInfo);
            }
        } else {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_PRINTER_CONFIGURATION, "Printers are not configured correctly");
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','WH_SETUP','IT_SUPPORT')")
    public AisleRackRow getAisleRackRowById(long aislerackRowId) {
        return aisleRepository.findById(aislerackRowId);
    }

    @Override
    public QualityInspectionPart findQualityinspectionPartById(long qualityinspectionPartOId) {
        return warehouseRepository.findQualityInspectionPart(qualityinspectionPartOId);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_QI')")
    public QualityInspectionPart createQualityinspectionPart(QualityInspectionPartDTO qualityinspectionPartDTO, String whSiteId)
            throws GloriaApplicationException {

        QualityInspectionPart createQualityInspectionPart = null;
        String partName = qualityinspectionPartDTO.getPartName();
        String partNumber = qualityinspectionPartDTO.getPartNumber();

        List<QualityInspectionPart> validateQualityInspectionParts = warehouseRepository.findQualityInspectionPartByPartNameOrNumber(partName, partNumber,
                                                                                                                                     whSiteId);
        validatePart(partName, partNumber);

        if (validateQualityInspectionParts != null && !validateQualityInspectionParts.isEmpty()) {
            LOGGER.error("Same part number or part name already exists");
            throw new GloriaApplicationException(GloriaExceptionConstants.PARTNUMBER_OR_PARTNAME_EXISTS,
                                                 "QI settings for this  Part Number or Part Name already exists");
        }

        QualityInspectionPart qualityInspectionPart = new QualityInspectionPart();
        qualityInspectionPart.setPartName(setNullIfEmpty(qualityinspectionPartDTO.getPartName()));
        qualityInspectionPart.setPartNumber(setNullIfEmpty(qualityinspectionPartDTO.getPartNumber()));
        qualityInspectionPart.setMandatory(qualityinspectionPartDTO.isMandatory());

        Warehouse warehouse = warehouseRepository.findWarehouseBySiteId(whSiteId);
        qualityInspectionPart.setWarehouse(warehouse);

        createQualityInspectionPart = warehouseRepository.save(qualityInspectionPart);

        return createQualityInspectionPart;
    }

    @Override
    public void validatePart(String partName, String partNumber) throws GloriaApplicationException {
        if ((!StringUtils.isEmpty(partNumber) && partNumber.length() > PARTNUMBER_LENGTH)
                || (!StringUtils.isEmpty(partName) && partName.length() > PARTNAME_LENGTH)) {

            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_STRING_LENGTH,
                                                 "This operation cannot be performed since the length of the partNumber and partName is exceeds the length.");
        }
    }

    private String setNullIfEmpty(String value) {
        if (!StringUtils.isEmpty(value)) {
            return value;
        }
        return null;
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_QI')")
    public QualityInspectionPart updateQualityinspectionPart(QualityInspectionPartDTO qualityinspectionPartDTO, String whSiteId)
            throws GloriaApplicationException {

        QualityInspectionPart qualityInspectionPart = warehouseRepository.findQualityInspectionPart(qualityinspectionPartDTO.getId());

        String partName = qualityinspectionPartDTO.getPartName();
        String partNumber = qualityinspectionPartDTO.getPartNumber();
        List<QualityInspectionPart> validateQualityInspectionParts = warehouseRepository.findQualityInspectionPartByPartNameOrNumber(partName, partNumber,
                                                                                                                                     whSiteId);

        if (qualityinspectionPartDTO.getVersion() != qualityInspectionPart.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET, ERROR_MSG_OPERATION_NOT_VALID);
        }

        validatePart(partName, partNumber);
        
        if (validateQualityInspectionParts != null && !validateQualityInspectionParts.isEmpty()) {
            boolean isValid = true;
            if (validateQualityInspectionParts.size() > 1) {
                isValid = false;
            } else if (qualityInspectionPart.getQualityInspectionPartOId() != validateQualityInspectionParts.get(0).getQualityInspectionPartOId()) {
                isValid = false;
            }
            if (!isValid) {
                LOGGER.error(" Update cannot be done. Same record may exist.");
                throw new GloriaApplicationException("QI settings for this  Part Number or Part Name already exists",
                                                     GloriaExceptionConstants.PARTNUMBER_OR_PARTNAME_EXISTS);
            }
        }

        qualityInspectionPart.setPartName(setNullIfEmpty(qualityinspectionPartDTO.getPartName()));
        qualityInspectionPart.setPartNumber(setNullIfEmpty(qualityinspectionPartDTO.getPartNumber()));
        qualityInspectionPart.setMandatory(qualityinspectionPartDTO.isMandatory());
        return warehouseRepository.save(qualityInspectionPart);
    }

    @Override
    public QualityInspectionProject findQualityinspectionProjectById(long qualityinspectionProjectOId) {
        return warehouseRepository.findQualityInspectionProject(qualityinspectionProjectOId);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_QI')")
    public QualityInspectionProject createQualityinspectionProject(QualityInspectionProjectDTO qualityinspectionProjectDTO, String whSiteId)
            throws GloriaApplicationException {
        QualityInspectionProject existingQualityInspectionProject 
                                    = warehouseRepository.findQualityInspectionProjectByProject(qualityinspectionProjectDTO.getProject(), whSiteId);
        WarehouseServicesHelper.validateQISupplierOrProject(qualityinspectionProjectDTO);
        WarehouseServicesHelper.validateUniqueQISupplierOrProject(qualityinspectionProjectDTO, existingQualityInspectionProject);

        QualityInspectionProject qualityInspectionProject = new QualityInspectionProject();

        if (qualityinspectionProjectDTO.getProject().length() > PROJECT_ID_LENGTH) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_STRING_LENGTH,
                                                 "This operation cannot be performed since the length of the Project is exceeds the length "
                                                         + PROJECT_ID_LENGTH);
        }
        qualityInspectionProject.setProject(qualityinspectionProjectDTO.getProject());
        qualityInspectionProject.setMandatory(qualityinspectionProjectDTO.isMandatory());

        Warehouse warehouse = warehouseRepository.findWarehouseBySiteId(whSiteId);
        qualityInspectionProject.setWarehouse(warehouse);

        return warehouseRepository.save(qualityInspectionProject);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_QI')")
    public QualityInspectionProject updateQualityinspectionProject(QualityInspectionProjectDTO qualityinspectionProjectDTO, String whSiteId)
            throws GloriaApplicationException {
        long qualityInspectionProjectOId = qualityinspectionProjectDTO.getId();
        QualityInspectionProject qualityInspectionProject = warehouseRepository.findQualityInspectionProject(qualityInspectionProjectOId);

        QualityInspectionProject qIProjectForAProjName = warehouseRepository.findQualityInspectionProjectByProject(qualityinspectionProjectDTO.getProject(),
                                                                                                                   whSiteId);

        if (qualityinspectionProjectDTO.getVersion() != qualityInspectionProject.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                 "This operation cannot be performed since the information seen in the page has already been updated.");
        }

        if (qIProjectForAProjName != null && qualityInspectionProjectOId != qIProjectForAProjName.getQualityInspectionProjectOId()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.PROJECT_EXISTS,
                                                 "This operation cannot be performed since the information has already been exit.");
        }

        if (qualityinspectionProjectDTO.getProject().length() > PROJECT_ID_LENGTH) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_STRING_LENGTH,
                                                 "This operation cannot be performed since the length of the Project is exceeds the length "
                                                         + PROJECT_ID_LENGTH);
        }
        qualityInspectionProject.setProject(qualityinspectionProjectDTO.getProject());
        qualityInspectionProject.setMandatory(qualityinspectionProjectDTO.isMandatory());
        return warehouseRepository.save(qualityInspectionProject);
    }

    @Override
    public QualityInspectionSupplier findQualityinspectionSupplierById(long qualityinspectionSupplierOId) {
        return warehouseRepository.findQualityInspectionSupplier(qualityinspectionSupplierOId);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_QI')")
    public QualityInspectionSupplier createQualityinspectionSupplier(QualityInspectionSupplierDTO qualityinspectionSupplierDTO, String whSiteId)
            throws GloriaApplicationException {

        QualityInspectionSupplier existingQualityInspectionSupplier 
                    = warehouseRepository.findQualityInspectionSupplierBySupplier(qualityinspectionSupplierDTO.getSupplier(), whSiteId);
        
        WarehouseServicesHelper.validateQISupplierOrProject(qualityinspectionSupplierDTO);
        WarehouseServicesHelper.validateUniqueQISupplierOrProject(qualityinspectionSupplierDTO, existingQualityInspectionSupplier);

        QualityInspectionSupplier qualityInspectionSupplier = new QualityInspectionSupplier();
        qualityInspectionSupplier.setSupplier(qualityinspectionSupplierDTO.getSupplier());
        qualityInspectionSupplier.setMandatory(qualityinspectionSupplierDTO.isMandatory());

        Warehouse warehouse = warehouseRepository.findWarehouseBySiteId(whSiteId);
        qualityInspectionSupplier.setWarehouse(warehouse);

        return warehouseRepository.save(qualityInspectionSupplier);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_QI')")
    public QualityInspectionSupplier updateQualityinspectionSupplier(QualityInspectionSupplierDTO qualityinspectionSupplierDTO, String whSiteId)
            throws GloriaApplicationException {
        long qualityInspectionSupplierOId = qualityinspectionSupplierDTO.getId();
        QualityInspectionSupplier qualityInspectionSupplier = warehouseRepository.findQualityInspectionSupplier(qualityInspectionSupplierOId);

        QualityInspectionSupplier qISupplierForASupplierName 
                    = warehouseRepository.findQualityInspectionSupplierBySupplier(qualityinspectionSupplierDTO.getSupplier(), whSiteId);

        if (qualityInspectionSupplier == null) {
            LOGGER.error("No quality inspection supplier objects exists for id : " + qualityInspectionSupplierOId);
            throw new GloriaSystemException("This operation cannot be performed. No quality inspection supplier objects exists for id : "
                    + qualityInspectionSupplierOId, GloriaExceptionConstants.INVALID_DATASET_OID);
        }

        if (qualityinspectionSupplierDTO.getVersion() != qualityInspectionSupplier.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                 "This operation cannot be performed since the information seen in the page has already been updated.");
        }

        if (qISupplierForASupplierName != null && (qualityInspectionSupplierOId != qISupplierForASupplierName.getQualityInspectionSupplierOId())) {
            throw new GloriaApplicationException(GloriaExceptionConstants.SUPPLIER_EXISTS,
                                                 "This operation cannot be performed since the information seen in the page has already been exit.");
        }

        qualityInspectionSupplier.setSupplier(qualityinspectionSupplierDTO.getSupplier());
        qualityInspectionSupplier.setMandatory(qualityinspectionSupplierDTO.isMandatory());

        return warehouseRepository.save(qualityInspectionSupplier);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_QI')")
    public void deleteQualityinspectionProject(long qualityinspectionProjectOID) throws GloriaApplicationException {
        warehouseRepository.deleteQualityInspectionProjectById(qualityinspectionProjectOID);
    }

    @Override
    public List<QualityInspectionProject> findQualityinspectionProjects(String whSiteId) {
        return warehouseRepository.findQualityInspectionProjects(whSiteId);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_QI')")
    public void deleteQualityinspectionPart(long qualityinspectionPartOID) throws GloriaApplicationException {
        warehouseRepository.deleteQualityInspectionPartById(qualityinspectionPartOID);
    }

    @Override    
    public List<QualityInspectionPart> findQualityinspectionPart(String whSiteId) {
        return warehouseRepository.findQualityInspectionParts(whSiteId);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_QI')")
    public void deleteQualityinspectionSupplier(long qualityinspectionSupplierOID) throws GloriaApplicationException {
        warehouseRepository.deleteQualityInspectionSupplierById(qualityinspectionSupplierOID);
    }

    @Override
    public List<QualityInspectionSupplier> findQualityinspectionSupplier(String whSiteId) {
        return warehouseRepository.findQualityInspectionSuppliers(whSiteId);
    }

    @Override
    public void createPrinterData(String xmlContent) {
        List<PrinterTransformerDTO> printerTransformerDTOs = printerTransformer.transformPrinters(xmlContent);

        for (PrinterTransformerDTO printerTransformerDTO : printerTransformerDTOs) {
            Warehouse warehouse = warehouseRepository.findWarehouseBySiteId(printerTransformerDTO.getSiteId());
            if (warehouse != null) {
                Printer printer = new Printer();
                printer.setWarehouse(warehouse);
                printer.setHostAddress(printerTransformerDTO.getHostAddress());
                printer.setName(printerTransformerDTO.getName());
                warehouseRepository.save(printer);
            }
        }
    }

    @Override
    public List<Printer> findListOfPrinters(String whSiteId) {
        return warehouseRepository.findListOfPrinters(whSiteId);
    }

    @Override
    public Printer findPrinterBySiteIdAndName(String whSiteId, String name) {
        return warehouseRepository.findPrinterBySiteIdAndName(whSiteId, name);
    }

    @Override
    public Zone findZoneCodes(ZoneType type, String whSiteId) {
        return warehouseRepository.findZoneCodes(type, whSiteId);

    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','WH_SETUP','PROCURE', 'DELIVERY', 'PROCURE-INTERNAL', 'REQUESTER_FOR_PULL', 'VIEWER_PRICE', 'VIEWER', 'WH_QI', 'IT_SUPPORT')")
    public List<WarehouseDTO> findWarehousesByUserId(String userId) throws GloriaApplicationException {
        UserDTO userDTO = userServices.getUser(userId);
        if (!StringUtils.isEmpty(userDTO.getWhSite())) {
            List<String> whSiteIds = new ArrayList<String>();
            for (String siteId : Arrays.asList(userDTO.getWhSite().split(COMMA))) {
                Site site = commonServices.getSiteBySiteId(siteId);
                if (site != null && StringUtils.isNotBlank(site.getShipToType()) && "WH".equalsIgnoreCase(site.getShipToType())) {
                    whSiteIds.add(siteId);
                }
            }

            if (!whSiteIds.isEmpty()) {
                List<Warehouse> warehouses = warehouseRepository.findWarehousesByWhsiteIds(whSiteIds);
                return WarehouseServicesHelper.transformToDTO(warehouses, commonServices);
            }
        }
        return new ArrayList<WarehouseDTO>();
    }

    @Override
    public BinlocationBalance getBinlocationBalance(String partAffiliation, String partNumber, String partVersion, String partModification,
            String binLocationCode, String siteId) {
        return warehouseRepository.getBinlocationBalance(partAffiliation, partNumber, partVersion, partModification, binLocationCode, siteId);
    }

    @Override
    public BinlocationBalance saveOrUpdateBinlocationBalance(BinlocationBalance binlocationBalance) {
        return warehouseRepository.save(binlocationBalance);
    }

    @Override
    public void removeBinlocationBalance(BinlocationBalance binlocationBalance) {
        warehouseRepository.removeBinlocationBalance(binlocationBalance);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT', 'WH_QI','IT_SUPPORT')")
    public PageObject getBinlocationBalancesByWhSite(PageObject pageObject, String userId, String whSiteId, String pPartNumber, String binLocationCode)
            throws GloriaApplicationException {
        return warehouseRepository.getBinlocationBalancesByWhSite(pageObject, whSiteId, pPartNumber, binLocationCode);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT', 'WH_QI','IT_SUPPORT')")
    public PageObject getBinlocationBalancesByBinlocation(PageObject pageObject, String whSiteId, String binlocationCode, String partNumber, String partName,
            String partVersion, String partAffiliation, String partModification) throws GloriaApplicationException {
        return warehouseRepository.getBinlocationBalancesByBinlocation(pageObject, whSiteId, binlocationCode, partNumber, partName, partVersion,
                                                                       partAffiliation, partModification);
    }

    
    @Override
    public BinlocationBalance findBinlocationBalanceById(long binlocationBalanceOid) {
        return warehouseRepository.findBinlocationBalanceById(binlocationBalanceOid);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','WH_SETUP','IT_SUPPORT', 'WH_QI')")
    public List<Zone> getZones(String whSiteId) {
        return warehouseRepository.findZones(whSiteId);
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','WH_SETUP','IT_SUPPORT','WH_QI')")
    public void printBinlocations(String binlocationCode, String zone, String aisle, String bay, String level, int copies, String whSiteId)
            throws GloriaApplicationException {
        if (!StringUtils.isEmpty(binlocationCode)) {
            print(binlocationCode, copies, whSiteId);
        } else {
            List<BinLocation> binLocations = warehouseRepository.findBinlocations(zone, aisle, bay, level, whSiteId);
            if (binLocations != null && !binLocations.isEmpty()) {
                for (BinLocation binLocation : binLocations) {
                    print(binLocation.getCode(), copies, whSiteId);
                }
            }
        }
    }

    @Override
    public void deletePlacement(long placementOid) {
        warehouseRepository.deletePlacement(placementOid);
    }

    @Override
    public PageObject getReportWarehouses(PageObject pageObject) {
        List<WarehouseDTO> warehouses = this.getWarehouses();
        List<ReportWarehouseDTO> reportWarehouseDTOs = new ArrayList<ReportWarehouseDTO>();
        for (WarehouseDTO warehouse : warehouses) {
            reportWarehouseDTOs.add(new ReportWarehouseDTO(warehouse.getSiteId(), warehouse.getSiteName()));
        }
        if (reportWarehouseDTOs != null && !reportWarehouseDTOs.isEmpty()) {
            pageObject.setCount(warehouses.size());
            pageObject.setGridContents(new ArrayList<PageResults>(reportWarehouseDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }

    @Override
    public List<BinlocationBalance> getBinLocationBalances(String whSite) {
        return warehouseRepository.getBinLocationBalances(whSite);
    }
}
