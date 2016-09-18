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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.b.WarehouseServicesHelper;
import com.volvo.gloria.warehouse.c.Allocation;
import com.volvo.gloria.warehouse.c.BaySides;
import com.volvo.gloria.warehouse.c.Setup;
import com.volvo.gloria.warehouse.c.ZoneType;
import com.volvo.gloria.warehouse.c.dto.AisleRackRowDTO;
import com.volvo.gloria.warehouse.c.dto.BaySettingDTO;
import com.volvo.gloria.warehouse.c.dto.QualityInspectionPartDTO;
import com.volvo.gloria.warehouse.c.dto.QualityInspectionProjectDTO;
import com.volvo.gloria.warehouse.c.dto.QualityInspectionSupplierDTO;
import com.volvo.gloria.warehouse.c.dto.StorageRoomDTO;
import com.volvo.gloria.warehouse.c.dto.WarehouseDTO;
import com.volvo.gloria.warehouse.c.dto.ZoneDTO;
import com.volvo.gloria.warehouse.d.entities.AisleRackRow;
import com.volvo.gloria.warehouse.d.entities.BaySetting;
import com.volvo.gloria.warehouse.d.entities.BinLocation;
import com.volvo.gloria.warehouse.d.entities.QualityInspectionPart;
import com.volvo.gloria.warehouse.d.entities.QualityInspectionProject;
import com.volvo.gloria.warehouse.d.entities.QualityInspectionSupplier;
import com.volvo.gloria.warehouse.d.entities.StorageRoom;
import com.volvo.gloria.warehouse.d.entities.Warehouse;
import com.volvo.gloria.warehouse.d.entities.Zone;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class WarehouseServicesBeanTest extends AbstractTransactionalTestCase {
    public WarehouseServicesBeanTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }  
    private static final String INITDATA_WAREHOUSE_XML = "globaldataTest/Warehouse.xml";
    private static final String INITDATA_USER_XML = "globaldataTest/UserOrganisationDetails.xml";
    private static final String INITDATA_SITE_XML = "globaldataTest/Site.xml";
    private static final String USER = "ALL";

    @Inject
    private WarehouseServices warehouseService;

    @Inject
    private CommonServices commonServices;

    @Inject
    UserServices userServices;

    @Before
    public void setUpTestData() throws Exception {
        commonServices.createSitesData(IOUtil.getStringFromClasspath(INITDATA_SITE_XML));
        warehouseService.createWarehouseData(IOUtil.getStringFromClasspath(INITDATA_WAREHOUSE_XML),null);
        userServices.createUserData(IOUtil.getStringFromClasspath(INITDATA_USER_XML));
        createAisleRackRowSetup();
    }

    private void createAisleRackRowSetup() throws GloriaApplicationException {
        List<Warehouse> storedWarehouses = warehouseService.getWarehouseList();
        Warehouse warehouseWithAisleSetUp = null;
        for (Warehouse warehouse : storedWarehouses) {
            if (warehouse.getSetUp().equals(Setup.AISLE) && warehouse.getSiteId().equals("2800")) {
                warehouseWithAisleSetUp = warehouse;
                break;
            }
        }

        StorageRoom storageRoom = warehouseWithAisleSetUp.getStorageRooms().get(0);
        for (Zone zone : storageRoom.getZones()) {
            if (zone.getType().equals(ZoneType.STORAGE)) {
                List<AisleRackRow> aisleRackRows = new ArrayList<AisleRackRow>();
                AisleRackRow aisleRackRow = new AisleRackRow();
                aisleRackRow.setSetUp(warehouseWithAisleSetUp.getSetUp());
                aisleRackRow.setCode("12");
                aisleRackRow.setNumberOfBay(2);
                aisleRackRow.setBaySides(BaySides.LEFT);
                aisleRackRow.setZone(zone);
                warehouseService.addAisleRackRow(aisleRackRow);
                aisleRackRow.setBaySettings(createBaySettings(aisleRackRow));
                aisleRackRows.add(aisleRackRow);
                zone.setAisleRackRows(aisleRackRows);
                warehouseService.updateZone(zone);
            }
        }
        warehouseWithAisleSetUp = warehouseService.findWarehouseById(warehouseWithAisleSetUp.getWarehouseOid());
        warehouseService.generateBinlocations(warehouseWithAisleSetUp.getWarehouseOid(), false, USER, "1722");

    }

    private List<BaySetting> createBaySettings(AisleRackRow aisleRackRow) {
        List<BaySetting> baySettings = new ArrayList<BaySetting>();
        BaySetting baySetting = new BaySetting();
        baySetting.setAisleRackRow(aisleRackRow);
        baySetting.setBayCode("1");
        baySetting.setNumberOfLevels(2);
        baySetting.setNumberOfPositions(2);
        warehouseService.addBaySetting(baySetting);
        baySettings.add(baySetting);

        BaySetting baySetting2 = new BaySetting();
        baySetting2.setAisleRackRow(aisleRackRow);
        baySetting2.setBayCode("2");
        baySetting2.setNumberOfLevels(2);
        baySetting2.setNumberOfPositions(1);
        warehouseService.addBaySetting(baySetting2);
        baySettings.add(baySetting2);
        return baySettings;
    }

    @Test
    public void testAddWarehouse() {
        // Arrange
        Warehouse warehouse_WH = new Warehouse();
        warehouse_WH.setSiteId("1001");
        warehouse_WH.setSetUp(Setup.AISLE);

        List<StorageRoom> storageRooms = new ArrayList<StorageRoom>();
        StorageRoom storageRoom_ST_A = new StorageRoom();
        storageRoom_ST_A.setCode("WH1_ST1");
        storageRoom_ST_A.setName("WH1_ST1_STORE");
        storageRoom_ST_A.setDescription("WH1_ST1_DESC");
        storageRooms.add(storageRoom_ST_A);

        StorageRoom storageRoom_ST_B = new StorageRoom();
        storageRoom_ST_B.setCode("WH1_ST2");
        storageRoom_ST_B.setName("WH1_ST2_STORE");
        storageRoom_ST_B.setDescription("WH2_ST2_DESC");
        storageRooms.add(storageRoom_ST_B);

        warehouse_WH.setStorageRooms(storageRooms);

        // Act
        warehouseService.addWarehouse(warehouse_WH);

        // Assert
        Warehouse warehouse = warehouseService.findWarehouseById(warehouse_WH.getWarehouseOid());
        Assert.assertNotNull(warehouse);
    }

    @Test
    public void testAddWarehouses() {
        // Arrange
        Warehouse warehouse_WHA = new Warehouse();
        warehouse_WHA.setSiteId("664");
        warehouse_WHA.setSetUp(Setup.AISLE);

        List<StorageRoom> storageRooms = new ArrayList<StorageRoom>();
        StorageRoom storageRoom_ST_A = new StorageRoom();
        storageRoom_ST_A.setCode("WH1_ST1");
        storageRoom_ST_A.setName("WH1_ST1_STORE");
        storageRoom_ST_A.setDescription("WH1_ST1_DESC");
        storageRooms.add(storageRoom_ST_A);

        StorageRoom storageRoom_ST_B = new StorageRoom();
        storageRoom_ST_B.setCode("WH1_ST2");
        storageRoom_ST_B.setName("WH1_ST2_STORE");
        storageRoom_ST_B.setDescription("WH2_ST2_DESC");
        storageRooms.add(storageRoom_ST_B);

        warehouse_WHA.setStorageRooms(storageRooms);

        Warehouse warehouse_WHB = new Warehouse();
        warehouse_WHB.setSiteId("1540");
        warehouse_WHB.setSetUp(Setup.ROW);

        List<StorageRoom> storageRooms_A = new ArrayList<StorageRoom>();
        StorageRoom storageRoom_ST_AA = new StorageRoom();
        storageRoom_ST_AA.setCode("WH2_ST3");
        storageRoom_ST_AA.setName("WH2_ST3_STORE");
        storageRoom_ST_AA.setDescription("WH2_ST3_DESC");
        storageRooms_A.add(storageRoom_ST_AA);

        StorageRoom storageRoom_ST_BB = new StorageRoom();
        storageRoom_ST_BB.setCode("WH2_ST3");
        storageRoom_ST_BB.setName("WH2_ST3_STORE");
        storageRoom_ST_BB.setDescription("WH2_ST3_DESC");
        storageRooms_A.add(storageRoom_ST_BB);

        warehouse_WHB.setStorageRooms(storageRooms_A);

        List<Warehouse> warehouses = new ArrayList<Warehouse>();
        warehouses.add(warehouse_WHA);
        warehouses.add(warehouse_WHB);

        // Act
        warehouseService.addWarehouses(warehouses);

        // Assert
        List<Warehouse> storedWarehouses = warehouseService.getWarehouseList();
        Assert.assertEquals(4, storedWarehouses.size());
    }

    @Test
    @Ignore
    public void testGetWarehouses() {
        // Arrange
        List<WarehouseDTO> warehouses = warehouseService.getWarehouses();
        Assert.assertNotNull(warehouses);
        Assert.assertTrue(warehouses.size() > 0); // Erroneous test data - all warehouses must have a site
        WarehouseDTO warehouse = warehouses.get(0);
        Assert.assertNotNull(warehouse);
        // Assert
        for (WarehouseDTO wHouse : warehouses) {
            if (wHouse.getSiteId() == warehouse.getSiteId()) {
                warehouse = wHouse;
                break;
            }
        }
    }

    @Test
    public void testGetWarehouseList() {
        // Arrange
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Assert.assertNotNull(warehouses);
        Assert.assertTrue(warehouses.size() > 0);
        Warehouse warehouse = warehouses.get(0);
        Assert.assertNotNull(warehouse);
        // Assert
        for (Warehouse wHouse : warehouses) {
            if (wHouse.getWarehouseOid() == warehouse.getWarehouseOid()) {
                warehouse = wHouse;
                break;
            }
        }
    }

    @Test
    public void testAddStorageRoom() {
        // Arrange
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Warehouse warehouse = warehouses.get(0);

        StorageRoom storageRoom_ST_AA = new StorageRoom();
        storageRoom_ST_AA.setWarehouse(warehouse);
        storageRoom_ST_AA.setCode("WH2_ST3");
        storageRoom_ST_AA.setName("WH2_ST3_STORE");
        storageRoom_ST_AA.setDescription("WH2_ST3_DESC");
        warehouse.getStorageRooms().add(storageRoom_ST_AA);

        // Act
        warehouseService.addStorageRoom(storageRoom_ST_AA);

        // Assert
        for (Warehouse wHouse : warehouses) {
            if (wHouse.getWarehouseOid() == warehouse.getWarehouseOid()) {
                warehouse = wHouse;
                break;
            }
        }
        List<StorageRoom> storageRooms = warehouse.getStorageRooms();
        Assert.assertNotNull(storageRooms);
        StorageRoom updatedStorageRoom = null;
        for (StorageRoom storageRoom : storageRooms) {
            if (storageRoom.getStorageRoomOid() == storageRoom_ST_AA.getStorageRoomOid()) {
                updatedStorageRoom = storageRoom;
                break;
            }
        }
        Assert.assertNotNull(updatedStorageRoom);
        Assert.assertEquals("WH2_ST3", updatedStorageRoom.getCode());
        Assert.assertEquals("WH2_ST3_DESC", updatedStorageRoom.getDescription());
    }

    @Test
    public void testAddBinLocations() {
        // Arrange
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Warehouse warehouse = warehouses.get(0);

        List<StorageRoom> storageRooms = warehouse.getStorageRooms();
        StorageRoom storageRoom = storageRooms.get(0);

        List<Zone> zones = storageRoom.getZones();
        Zone zone = zones.get(0);

        BinLocation binlocation_BL_AA = new BinLocation();
        binlocation_BL_AA.setCode("WH2_ST3_ZN3_BL1");
        binlocation_BL_AA.setBayCode("1");
        binlocation_BL_AA.setLevel(1);
        binlocation_BL_AA.setPosition(1);
        binlocation_BL_AA.setAllocation(Allocation.FIXED);
        zone.getBinLocations().add(binlocation_BL_AA);

        // Act
        warehouseService.addBinLocation(binlocation_BL_AA);

        // Assert
        List<StorageRoom> sRooms = warehouse.getStorageRooms();
        Assert.assertNotNull(sRooms);
        StorageRoom updatedStorageRoom = null;
        for (StorageRoom sRoom : sRooms) {
            if (sRoom.getStorageRoomOid() == storageRoom.getStorageRoomOid()) {
                updatedStorageRoom = sRoom;
                break;
            }
        }

        List<Zone> zNs = updatedStorageRoom.getZones();
        Assert.assertNotNull(zNs);
        Zone updatedZon = null;
        for (Zone zn : zNs) {
            if (zn.getZoneOid() == zone.getZoneOid()) {
                updatedZon = zn;
                break;
            }
        }

        if (updatedZon != null) {
            List<BinLocation> bLocations = updatedZon.getBinLocations();
            Assert.assertNotNull(bLocations);
            BinLocation updatedBinLocation = null;
            for (BinLocation bLocation : bLocations) {
                if (bLocation.getBinLocationOid() == binlocation_BL_AA.getBinLocationOid()) {
                    updatedBinLocation = bLocation;
                    break;
                }
            }
            Assert.assertEquals("WH2_ST3_ZN3_BL1", updatedBinLocation.getCode());
            Assert.assertEquals("1", updatedBinLocation.getBayCode());
            Assert.assertEquals("FIXED", updatedBinLocation.getAllocation().toString());
        }
    }

    @Test
    public void testFindWarehouseById() {
        // Arrange
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Warehouse warehouse = warehouses.get(0);
        // Act
        Warehouse fetchedWarehouse = warehouseService.findWarehouseById(warehouse.getWarehouseOid());
        // Assert
        Assert.assertNotNull(fetchedWarehouse);
        Assert.assertEquals(warehouse.getSiteId(), fetchedWarehouse.getSiteId());
    }

    @Test
    public void testGetStorageRooms() {
        // Arrange

        // Act
        List<StorageRoom> storageRooms = warehouseService.getStorageRooms();
        // Assert
        Assert.assertNotNull(storageRooms);
        Assert.assertEquals(2, storageRooms.size());
    }

    @Test
    public void testAddZon() {
        // Arrange
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Warehouse warehouse = warehouses.get(0);

        List<StorageRoom> storageRooms = warehouse.getStorageRooms();
        StorageRoom storageRoom = storageRooms.get(0);

        Zone zon_AA = new Zone();
        zon_AA.setStorageRoom(storageRoom);
        zon_AA.setCode("WH2_ZN3");
        zon_AA.setName("WH2_ZN3_STORE");
        zon_AA.setDescription("WH2_ZN3_DESC");
        storageRoom.getZones().add(zon_AA);

        // Act
        warehouseService.addZone(zon_AA);

        // Assert
        for (StorageRoom sRoom : storageRooms) {
            if (sRoom.getStorageRoomOid() == storageRoom.getStorageRoomOid()) {
                storageRoom = sRoom;
                break;
            }
        }
        List<Zone> zons = storageRoom.getZones();
        Assert.assertNotNull(zons);
        Zone updatedZon = null;
        for (Zone zon : zons) {
            if (zon.getZoneOid() == zon_AA.getZoneOid()) {
                updatedZon = zon;
                break;
            }
        }
        Assert.assertNotNull(updatedZon);
        Assert.assertEquals("WH2_ZN3", updatedZon.getCode());
        Assert.assertEquals("WH2_ZN3_DESC", updatedZon.getDescription());
    }

    @Test
    public void testAddAisleRackRow() {
        // Arrange
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Warehouse warehouse = warehouses.get(0);

        List<StorageRoom> storageRooms = warehouse.getStorageRooms();
        StorageRoom storageRoom = storageRooms.get(0);

        List<Zone> zones = storageRoom.getZones();
        Zone zone = zones.get(0);
        AisleRackRow aisle_rack_row_AA = new AisleRackRow();
        aisle_rack_row_AA.setCode("WH2_ST2_ZN2_AI2");
        aisle_rack_row_AA.setSetUp(warehouse.getSetUp());
        aisle_rack_row_AA.setNumberOfBay(2);
        aisle_rack_row_AA.setBaySides(BaySides.BOTH);
        aisle_rack_row_AA.setZone(zone);
        zone.getAisleRackRows().add(aisle_rack_row_AA);

        // Act
        warehouseService.addAisleRackRow(aisle_rack_row_AA);

        // Assert
        List<StorageRoom> sRooms = warehouse.getStorageRooms();
        Assert.assertNotNull(sRooms);
        StorageRoom updatedStorageRoom = null;
        for (StorageRoom sRoom : sRooms) {
            if (sRoom.getStorageRoomOid() == storageRoom.getStorageRoomOid()) {
                updatedStorageRoom = storageRoom;
                break;
            }
        }

        List<Zone> zNs = updatedStorageRoom.getZones();
        Assert.assertNotNull(zNs);
        Zone updatedZon = null;
        for (Zone zn : zNs) {
            if (zn.getZoneOid() == zone.getZoneOid()) {
                updatedZon = zone;
                break;
            }
        }

        List<AisleRackRow> aisleRackRows = updatedZon.getAisleRackRows();
        Assert.assertNotNull(aisleRackRows);
        AisleRackRow updatedAisle = null;
        for (AisleRackRow aisleRackRow : aisleRackRows) {
            if (aisleRackRow.getAisleRackRowOid() == aisle_rack_row_AA.getAisleRackRowOid()) {
                updatedAisle = aisleRackRow;
                break;
            }
        }
        Assert.assertNotNull(updatedAisle);
        Assert.assertEquals("WH2_ST2_ZN2_AI2", updatedAisle.getCode());
        Assert.assertEquals(warehouse.getSetUp(), updatedAisle.getSetUp());
    }

    @Test
    public void testGetStorageRoomsByWarehouseId() throws GloriaApplicationException {
        // Arrage
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Long warehouseOid = warehouses.get(0).getWarehouseOid();

        // Act
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);
        pageObject = warehouseService.getStorageRooms(pageObject, warehouseOid);

        // Assert
        Assert.assertNotNull(pageObject.getGridContents());
    }

    @Test
    public void testDeleteStorageRoom() throws GloriaApplicationException {
        // Arrage
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Long warehouseOid = warehouses.get(0).getWarehouseOid();

        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);
        pageObject = warehouseService.getStorageRooms(pageObject, warehouseOid);
        StorageRoomDTO storageRoomDTO = (StorageRoomDTO) pageObject.getGridContents().get(0);

        // Act
        warehouseService.deleteStorageRoomById(storageRoomDTO.getId());

        // Assert
        PageObject pageObjectNew = new PageObject();
        pageObjectNew.setResultsPerPage(100);
        pageObjectNew = warehouseService.getStorageRooms(pageObjectNew, warehouseOid);
        Assert.assertNull(pageObjectNew.getGridContents());
    }

    @Test
    public void testGetZones() throws GloriaApplicationException {
        // Arrage
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Long warehouseOid = warehouses.get(0).getWarehouseOid();

        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);
        pageObject = warehouseService.getStorageRooms(pageObject, warehouseOid);
        StorageRoomDTO storageRoomDTO = (StorageRoomDTO) pageObject.getGridContents().get(0);

        PageObject pageObjectNew = new PageObject();
        pageObjectNew.setResultsPerPage(100);

        // Act
        pageObjectNew = warehouseService.getZones(pageObjectNew, storageRoomDTO.getId());

        // Assert
        Assert.assertNotNull(pageObjectNew.getGridContents());
    }

    @Test
    public void testGetAisleRackRow() throws GloriaApplicationException {
        // Arrage
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Long warehouseOid = null;
        for (Warehouse warehouse : warehouses) {
            if (warehouse.getSetUp().equals(Setup.AISLE) && warehouse.getSiteId().equals("2800")) {
                Warehouse warehouseWithAisleSetUp = warehouse;
                warehouseOid = warehouseWithAisleSetUp.getWarehouseOid();
                break;
            }
        }

        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);
        pageObject = warehouseService.getStorageRooms(pageObject, warehouseOid);
        StorageRoomDTO storageRoomDTO = (StorageRoomDTO) pageObject.getGridContents().get(0);

        PageObject pageObjectNew = new PageObject();
        pageObjectNew.setResultsPerPage(100);
        pageObjectNew = warehouseService.getZones(pageObjectNew, storageRoomDTO.getId());
        ZoneDTO zoneWithStorageType = null;
        for (PageResults pageResults : pageObjectNew.getGridContents()) {
            ZoneDTO zoneDTO = (ZoneDTO) pageResults;
            if (zoneDTO.getType().equals(ZoneType.STORAGE.toString())) {
                zoneWithStorageType = zoneDTO;
                break;
            }
        }

        // Act
        List<AisleRackRow> aisleRackRows = warehouseService.getAisleRackRow(zoneWithStorageType.getId());

        // Assert
        Assert.assertNotNull(aisleRackRows);
    }

    @Test
    public void testAddAisleRackRowWithZone() throws GloriaApplicationException {
        // Arrage
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Warehouse warehouseWithAisleSetUp = null;
        for (Warehouse warehouse : warehouses) {
            if (warehouse.getSetUp().equals(Setup.AISLE) && warehouse.getSiteId().equals("2800")) {
                warehouseWithAisleSetUp = warehouse;
                break;
            }
        }

        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);
        pageObject = warehouseService.getStorageRooms(pageObject, warehouseWithAisleSetUp.getWarehouseOid());

        StorageRoomDTO storageRoomDTO = (StorageRoomDTO) pageObject.getGridContents().get(0);

        PageObject pageObjectNew = new PageObject();
        pageObjectNew.setResultsPerPage(100);
        pageObjectNew = warehouseService.getZones(pageObjectNew, storageRoomDTO.getId());

        ZoneDTO zoneWithStorageType = null;
        for (PageResults pageResults : pageObjectNew.getGridContents()) {
            ZoneDTO zoneDTO = (ZoneDTO) pageResults;
            if (zoneDTO.getType().equals(ZoneType.STORAGE.toString())) {
                zoneWithStorageType = zoneDTO;
                break;
            }
        }

        AisleRackRowDTO aisleRackRowDTO = new AisleRackRowDTO();
        aisleRackRowDTO.setCode("Test");
        aisleRackRowDTO.setSetUp(warehouseWithAisleSetUp.getSetUp().name());
        aisleRackRowDTO.setBaySides(BaySides.BOTH.name());
        aisleRackRowDTO.setNumberOfBays(2);

        // Act
        AisleRackRow aisleRackRow = warehouseService.addAisleRackRow(zoneWithStorageType.getId(), aisleRackRowDTO);

        // Assert
        Assert.assertNotNull(aisleRackRow.getAisleRackRowOid());
        Assert.assertEquals(2, aisleRackRow.getBaySettings().size());

    }

    @Test
    public void testUpdateAisleRackRow() throws GloriaApplicationException {
        // Arrage
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Long warehouseOid = null;
        for (Warehouse warehouse : warehouses) {
            if (warehouse.getSetUp().equals(Setup.AISLE) && warehouse.getSiteId().equals("2800")) {
                Warehouse warehouseWithAisleSetUp = warehouse;
                warehouseOid = warehouseWithAisleSetUp.getWarehouseOid();
                break;
            }
        }
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);
        pageObject = warehouseService.getStorageRooms(pageObject, warehouseOid);
        StorageRoomDTO storageRoomDTO = (StorageRoomDTO) pageObject.getGridContents().get(0);

        PageObject pageObjectNew = new PageObject();
        pageObjectNew.setResultsPerPage(100);
        pageObjectNew = warehouseService.getZones(pageObjectNew, storageRoomDTO.getId());
        ZoneDTO zoneWithStorageType = null;
        for (PageResults pageResults : pageObjectNew.getGridContents()) {
            ZoneDTO zoneDTO = (ZoneDTO) pageResults;
            if (zoneDTO.getType().equals(ZoneType.STORAGE.toString())) {
                zoneWithStorageType = zoneDTO;
                break;
            }
        }

        AisleRackRowDTO aisleRackRowDTO = new AisleRackRowDTO();
        aisleRackRowDTO.setCode("Test");
        aisleRackRowDTO.setSetUp(Setup.AISLE.toString());
        aisleRackRowDTO.setBaySides(BaySides.BOTH.name());
        aisleRackRowDTO.setNumberOfBays(2);

        AisleRackRow aisleRackRow = warehouseService.addAisleRackRow(zoneWithStorageType.getId(), aisleRackRowDTO);
        Assert.assertNotNull(aisleRackRow.getAisleRackRowOid());
        // Act
        aisleRackRowDTO.setId(aisleRackRow.getAisleRackRowOid());
        aisleRackRowDTO.setCode("Update");
        aisleRackRowDTO.setBaySides(BaySides.LEFT.name());
        aisleRackRowDTO.setNumberOfBays(3);

        AisleRackRow updatedAisle = warehouseService.updateAisleRackRow(aisleRackRowDTO);
        // Assert
        Assert.assertEquals("Update", updatedAisle.getCode());
        Assert.assertEquals(3, updatedAisle.getBaySettings().size());
        Assert.assertEquals(BaySides.LEFT.name(), updatedAisle.getBaySides().name());
    }

    @Test
    public void testDeleteAisleRackRow() throws GloriaApplicationException {
        // Arrage
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Long warehouseOid = null;
        for (Warehouse warehouse : warehouses) {
            if (warehouse.getSetUp().equals(Setup.AISLE) && warehouse.getSiteId().equals("2800")) {
                Warehouse warehouseWithAisleSetUp = warehouse;
                warehouseOid = warehouseWithAisleSetUp.getWarehouseOid();
                break;
            }
        }

        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);
        pageObject = warehouseService.getStorageRooms(pageObject, warehouseOid);
        StorageRoomDTO storageRoomDTO = (StorageRoomDTO) pageObject.getGridContents().get(0);

        PageObject pageObjectNew = new PageObject();
        pageObjectNew.setResultsPerPage(100);
        pageObjectNew = warehouseService.getZones(pageObjectNew, storageRoomDTO.getId());
        ZoneDTO zoneWithStorageType = null;
        for (PageResults pageResults : pageObjectNew.getGridContents()) {
            ZoneDTO zoneDTO = (ZoneDTO) pageResults;
            if (zoneDTO.getType().equals(ZoneType.STORAGE.toString())) {
                zoneWithStorageType = zoneDTO;
                break;
            }
        }

        List<AisleRackRow> aisleRackRows = warehouseService.getAisleRackRow(zoneWithStorageType.getId());
        Assert.assertTrue(aisleRackRows.size() > 0);
        int expectedSize = aisleRackRows.size() - 1;

        // Act
        warehouseService.deleteAisleRackRow(aisleRackRows.get(0).getId());

        // Assert
        List<AisleRackRow> aisleRackRowsList = warehouseService.getAisleRackRow(zoneWithStorageType.getId());
        Assert.assertEquals(expectedSize, aisleRackRowsList.size());

    }

    @Test
    public void testAddStorageRoomWithDTO() throws GloriaApplicationException {
        // Arrange
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Warehouse warehouse = warehouses.get(0);

        StorageRoomDTO storageRoomDto = new StorageRoomDTO();
        storageRoomDto.setCode("WH2_ST3");
        storageRoomDto.setName("WH2_ST3_STORE");
        storageRoomDto.setDescription("WH2_ST3_DESC");

        // Act
        StorageRoom storageRoom = warehouseService.addStorageRoom(storageRoomDto, warehouse.getWarehouseOid());

        // Assert
        Assert.assertNotNull(storageRoom);
        Assert.assertNotNull(storageRoom.getStorageRoomOid());
        Assert.assertEquals("WH2_ST3", storageRoom.getCode());
        Assert.assertEquals("WH2_ST3_STORE", storageRoom.getName());
        Assert.assertEquals("WH2_ST3_DESC", storageRoom.getDescription());
    }

    @Test
    public void testDeleteBinLocation() throws GloriaApplicationException {
        // Arrage
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Long warehouseOid = null;
        for (Warehouse warehouse : warehouses) {
            if (warehouse.getSetUp().equals(Setup.AISLE) && warehouse.getSiteId().equals("2800")) {
                Warehouse warehouseWithAisleSetUp = warehouse;
                warehouseOid = warehouseWithAisleSetUp.getWarehouseOid();
                break;
            }
        }

        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);
        pageObject = warehouseService.getStorageRooms(pageObject, warehouseOid);
        StorageRoomDTO storageRoomDTO = (StorageRoomDTO) pageObject.getGridContents().get(0);

        PageObject pageObjectNew = new PageObject();
        pageObjectNew.setResultsPerPage(100);
        pageObjectNew = warehouseService.getZones(pageObjectNew, storageRoomDTO.getId());
        ZoneDTO zoneWithStorageType = null;
        for (PageResults pageResults : pageObjectNew.getGridContents()) {
            ZoneDTO zoneDTO = (ZoneDTO) pageResults;
            if (zoneDTO.getType().equals(ZoneType.STORAGE.toString())) {
                zoneWithStorageType = zoneDTO;
                break;
            }
        }

        Zone zone = warehouseService.getZoneById(zoneWithStorageType.getId());
        Assert.assertNotNull(zone);

        List<BinLocation> binLocations = zone.getBinLocations();
        Assert.assertNotNull(binLocations);
        BinLocation binLocation = binLocations.get(0);

        warehouseService.deleteBinLocation(binLocation.getBinLocationOid());
    }

    @Test
    public void testFindStorageRoomById() {
        // Arrange
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Warehouse warehouse = warehouses.get(0);

        List<StorageRoom> storageRooms = warehouse.getStorageRooms();
        StorageRoom sRoom = storageRooms.get(0);
        // Act
        StorageRoom storageRoom = warehouseService.findStorageRoomById(sRoom.getStorageRoomOid());

        // Assert
        Assert.assertNotNull(storageRoom);

    }

    @Test
    public void testUpdateStorageRoom() throws GloriaApplicationException {
        // Arrange
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Warehouse warehouse = warehouses.get(0);

        List<StorageRoom> storageRooms = warehouse.getStorageRooms();
        StorageRoom sRoom = storageRooms.get(0);

        StorageRoomDTO sRoomDto = new StorageRoomDTO();
        sRoomDto.setId(sRoom.getStorageRoomOid());
        sRoomDto.setCode("ST3_Updated");
        sRoomDto.setDescription("ST3_DESC_Updated");
        sRoomDto.setName("ST3_NAME_Updated");
        sRoomDto.setVersion(sRoom.getVersion());
        // Act
        StorageRoom storageRoom = warehouseService.updateStorageRoom(sRoomDto);

        // Assert
        Assert.assertNotNull(storageRoom);
        Assert.assertEquals("ST3_Updated", storageRoom.getCode());
        Assert.assertEquals("ST3_DESC_Updated", storageRoom.getDescription());
        Assert.assertEquals("ST3_NAME_Updated", storageRoom.getName());
    }

    @Test(expected = GloriaApplicationException.class)
    public void testSaveZoneWithDTO() throws GloriaApplicationException {
        // Arrange
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Warehouse warehouseWithRowSetUp = null;
        for (Warehouse warehouse : warehouses) {
            if (warehouse.getSetUp().equals(Setup.ROW) && warehouse.getSiteId().equals("1722")) {
                warehouseWithRowSetUp = warehouse;
                break;
            }
        }

        List<StorageRoom> storageRooms = warehouseWithRowSetUp.getStorageRooms();
        StorageRoom storageRoom = storageRooms.get(0);

        ZoneDTO zoneDto = new ZoneDTO();
        zoneDto.setCode("WH1_ST1_ZN1");
        zoneDto.setDescription("WH1_ST1_ZN1_Desc");
        zoneDto.setName("WH1_ST1_Zone1");
        String type = ZoneType.QI.toString();
        zoneDto.setType(type);

        // Act
        Zone zone = warehouseService.addZone(storageRoom.getStorageRoomOid(), zoneDto);

        // Assert
        Assert.assertNotNull(zone);
        Assert.assertNotNull(zone.getZoneOid());
        Assert.assertEquals("WH1_ST1_ZN1", zone.getCode());
        Assert.assertEquals("WH1_ST1_Zone1", zone.getName());
        Assert.assertEquals("WH1_ST1_ZN1_Desc", zone.getDescription());

        // Act
        StorageRoom storageRoom2 = storageRooms.get(1);
        ZoneDTO zoneDto2 = new ZoneDTO();
        zoneDto2.setCode("WH1_ST1_ZN1");
        zoneDto2.setDescription("WH1_ST1_ZN1_Desc");
        zoneDto2.setName("WH1_ST1_Zone1");
        zoneDto2.setType(type);
        warehouseService.addZone(storageRoom2.getStorageRoomOid(), zoneDto2);
    }

    @Test
    public void testGetZone() {
        // Arrange
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Warehouse warehouse = warehouses.get(0);

        List<StorageRoom> storageRooms = warehouse.getStorageRooms();
        StorageRoom storageRoom = storageRooms.get(0);

        List<Zone> zons = storageRoom.getZones();
        Zone zon = zons.get(0);
        Long zonOid = zon.getZoneOid();
        // Act
        zon = warehouseService.getZone(zonOid);

        // Assert
        Assert.assertNotNull(zon);
        Assert.assertEquals(zon.getZoneOid(), zonOid);
    }

    @Test
    public void testUpdateZone() throws GloriaApplicationException {
        // Arrange
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Warehouse warehouse = warehouses.get(0);

        List<StorageRoom> storageRooms = warehouse.getStorageRooms();
        StorageRoom storageRoom = storageRooms.get(0);

        List<Zone> zons = storageRoom.getZones();
        Zone zon = zons.get(0);

        ZoneDTO zoneDTO = new ZoneDTO();
        zoneDTO.setId(zon.getZoneOid());
        zoneDTO.setCode("ZN_Updated");
        zoneDTO.setDescription("ZN_DESC_Updated");
        zoneDTO.setName("ZN_NAME_Updated");
        zoneDTO.setVersion(zon.getVersion());

        // Act
        Zone zonAfterUpdate = warehouseService.updateZone(zoneDTO);

        // Assert
        Assert.assertNotNull(storageRoom);
        Assert.assertEquals("ZN_Updated", zonAfterUpdate.getCode());
        Assert.assertEquals("ZN_DESC_Updated", zonAfterUpdate.getDescription());
        Assert.assertEquals("ZN_NAME_Updated", zonAfterUpdate.getName());
    }

    @Test
    public void testDeleteZone() {
        // Arrange
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Warehouse warehouse = warehouses.get(0);

        List<StorageRoom> storageRooms = warehouse.getStorageRooms();
        StorageRoom storageRoom = storageRooms.get(0);

        List<Zone> zons = storageRoom.getZones();
        Zone zon = zons.get(0);
        Long zonOid = zon.getZoneOid();
        // Act
        warehouseService.deleteZone(zonOid);

        // Assert
        zon = warehouseService.getZone(zonOid);
        Assert.assertNull(zon);
    }

    @Test
    public void testExportXML() throws GloriaApplicationException {
        // Arrange
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Warehouse warehouse = warehouses.get(0);

        // Act
        FileToExportDTO xmldto = warehouseService.exportWarehouseAsXML(warehouse.getId());

        // Assert
        Assert.assertNotNull(xmldto);
        Assert.assertNotNull(xmldto.getName());
        Assert.assertNotNull(xmldto.getContent());
    }

    @Test
    public void testFindWarehouseBySiteId() {
        // Arrange
        String siteId = "1722";

        // Act
        Warehouse warehouse = warehouseService.findWarehouseBySiteId(siteId);

        // Assert
        Assert.assertNotNull(warehouse);
        Assert.assertEquals("1722", warehouse.getSiteId());
    }

    @Test
    public void testAddBaySetting() throws GloriaApplicationException {
        // Arrange
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Warehouse warehouseWithAisleSetUp = null;
        for (Warehouse warehouse : warehouses) {
            if (warehouse.getSetUp().equals(Setup.AISLE) && warehouse.getSiteId().equals("2800")) {
                warehouseWithAisleSetUp = warehouse;
                break;
            }
        }

        List<StorageRoom> storageRooms = warehouseWithAisleSetUp.getStorageRooms();
        StorageRoom storageRoom = storageRooms.get(0);

        List<Zone> zons = storageRoom.getZones();
        Zone zoneWithStorageType = null;
        for (Zone zone : zons) {
            if (zone.getType().equals(ZoneType.STORAGE)) {
                zoneWithStorageType = zone;
                break;
            }
        }

        List<AisleRackRow> aisleRackRows = zoneWithStorageType.getAisleRackRows();
        AisleRackRow aisleRackRow = aisleRackRows.get(0);
        Assert.assertNotNull(aisleRackRow);

        BaySetting baySetting = new BaySetting();
        baySetting.setBayCode("111");
        baySetting.setNumberOfLevels(1);
        baySetting.setNumberOfPositions(2);
        baySetting.setAisleRackRow(aisleRackRow);
        aisleRackRow.getBaySettings().add(baySetting);

        // Act
        warehouseService.addBaySetting(baySetting);

        // Assert
        List<StorageRoom> sRooms = warehouseWithAisleSetUp.getStorageRooms();
        Assert.assertNotNull(sRooms);
        StorageRoom updatedStorageRoom = null;
        for (StorageRoom sRoom : sRooms) {
            if (sRoom.getStorageRoomOid() == storageRoom.getStorageRoomOid()) {
                updatedStorageRoom = storageRoom;
                break;
            }
        }

        List<Zone> zNs = updatedStorageRoom.getZones();
        Assert.assertNotNull(zNs);
        Zone updatedZon = null;
        for (Zone zn : zNs) {
            if (zn.getZoneOid() == zoneWithStorageType.getZoneOid()) {
                updatedZon = zn;
                break;
            }
        }

        List<AisleRackRow> ais = updatedZon.getAisleRackRows();
        Assert.assertNotNull(ais);
        AisleRackRow updatedAisle = null;
        for (AisleRackRow aisl : ais) {
            if (aisl.getAisleRackRowOid() == aisleRackRow.getAisleRackRowOid()) {
                updatedAisle = aisl;
                break;
            }
        }
        Assert.assertNotNull(updatedAisle.getBaySettings());
        Assert.assertTrue(updatedAisle.getBaySettings().size() > 0);
        boolean added = false;
        for (BaySetting aisleBaySettingAfterAdd : updatedAisle.getBaySettings()) {
            if (aisleBaySettingAfterAdd.getBayCode() == baySetting.getBayCode()) {
                added = true;
            }
        }
        Assert.assertTrue(added);
    }

    @Test
    public void testFindZonesByZoneTypeAndWhSiteId() throws GloriaApplicationException {
        // Arrange
        String zoneType = "QI";
        String userId = "all";

        // Act
        List<Zone> zones = warehouseService.findZonesByZoneTypeAndWhSiteId(zoneType, userId, "1722");

        // Assert
        Assert.assertNotNull(zones);
        Assert.assertEquals(1, zones.size());

    }

    @Test
    public void testDeleteBaySetting() throws GloriaApplicationException {
        // Arrange
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Warehouse warehouseWithAisleSetUp = null;
        for (Warehouse warehouse : warehouses) {
            if (warehouse.getSetUp().equals(Setup.AISLE) && warehouse.getSiteId().equals("2800")) {
                warehouseWithAisleSetUp = warehouse;
                break;
            }
        }

        List<StorageRoom> storageRooms = warehouseWithAisleSetUp.getStorageRooms();
        StorageRoom storageRoom = storageRooms.get(0);

        List<Zone> zons = storageRoom.getZones();
        Zone zoneWithStorageType = null;
        for (Zone zone : zons) {
            if (zone.getType().equals(ZoneType.STORAGE)) {
                zoneWithStorageType = zone;
                break;
            }
        }

        List<AisleRackRow> aisleRackRows = zoneWithStorageType.getAisleRackRows();
        AisleRackRow aisleRackRow = aisleRackRows.get(0);
        Assert.assertNotNull(aisleRackRow);

        List<BaySetting> baySettings = aisleRackRow.getBaySettings();
        BaySetting baySetting = baySettings.get(0);

        // Act
        warehouseService.deleteBaySetting(baySetting.getBaySettingOid());

        // Assert
        BaySetting updatedBaySetting = warehouseService.findBaySetting(baySetting.getBaySettingOid());
        Assert.assertNull(updatedBaySetting);

    }

    @Test
    public void testAddBaySettingWithAisleRackRow() {
        // Arrange
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Warehouse warehouseWithAisleSetUp = null;
        for (Warehouse warehouse : warehouses) {
            if (warehouse.getSetUp().equals(Setup.AISLE) && warehouse.getSiteId().equals("2800")) {
                warehouseWithAisleSetUp = warehouse;
                break;
            }
        }

        List<StorageRoom> storageRooms = warehouseWithAisleSetUp.getStorageRooms();
        StorageRoom storageRoom = storageRooms.get(0);

        List<Zone> zons = storageRoom.getZones();
        Zone zoneWithStorageType = null;
        for (Zone zone : zons) {
            if (zone.getType().equals(ZoneType.STORAGE)) {
                zoneWithStorageType = zone;
                break;
            }
        }

        List<AisleRackRow> aisleRackRows = zoneWithStorageType.getAisleRackRows();
        AisleRackRow aisleRackRow = aisleRackRows.get(0);
        Assert.assertNotNull(aisleRackRow);

        BaySettingDTO baySettingDTO = new BaySettingDTO();
        baySettingDTO.setBayCode("1");
        baySettingDTO.setNumberOfLevels(1);
        baySettingDTO.setNumberOfPositions(2);

        // Act
        BaySetting baySetting = warehouseService.addBaySetting(aisleRackRow.getAisleRackRowOid(), baySettingDTO);

        // Assert
        Assert.assertNotNull(baySetting);
        Assert.assertEquals(baySettingDTO.getBayCode(), baySetting.getBayCode());
        Assert.assertEquals(baySettingDTO.getNumberOfLevels(), baySetting.getNumberOfLevels());
        Assert.assertEquals(baySettingDTO.getNumberOfPositions(), baySetting.getNumberOfPositions());

    }

    @Test
    public void testGetZonesByTypeAndStorageRoomId() {
        // Arrange
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Warehouse warehouseWithAisleSetUp = null;
        for (Warehouse warehouse : warehouses) {
            if (warehouse.getSetUp().equals(Setup.AISLE) && warehouse.getSiteId().equals("2800")) {
                warehouseWithAisleSetUp = warehouse;
                break;
            }
        }

        List<StorageRoom> storageRooms = warehouseWithAisleSetUp.getStorageRooms();
        StorageRoom storageRoom = storageRooms.get(0);

        // Act
        List<Zone> zones = warehouseService.getZones(storageRoom.getStorageRoomOid(), "QUARANTINE");

        // Assert
        Assert.assertNotNull(zones);
        Assert.assertEquals(1, zones.size());
    }

    @Test
    public void testGetBaySettings() {
        // Arrange
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Warehouse warehouseWithAisleSetUp = null;
        for (Warehouse warehouse : warehouses) {
            if (warehouse.getSetUp().equals(Setup.AISLE) && warehouse.getSiteId().equals("2800")) {
                warehouseWithAisleSetUp = warehouse;
                break;
            }
        }

        List<StorageRoom> storageRooms = warehouseWithAisleSetUp.getStorageRooms();
        StorageRoom storageRoom = storageRooms.get(0);

        List<Zone> zons = storageRoom.getZones();
        Zone zoneWithStorageType = null;
        for (Zone zone : zons) {
            if (zone.getType().equals(ZoneType.STORAGE)) {
                zoneWithStorageType = zone;
                break;
            }
        }

        List<AisleRackRow> aisleRackRows = zoneWithStorageType.getAisleRackRows();
        AisleRackRow aisleRackRow = aisleRackRows.get(0);
        Assert.assertNotNull(aisleRackRow);

        // Act
        List<BaySetting> baySettings = warehouseService.getBaySettings(aisleRackRow.getAisleRackRowOid());

        // Assert
        Assert.assertNotNull(baySettings);
        Assert.assertEquals(2, baySettings.size());
    }

    @Test
    public void testFindBaySettings() {
        // Arrange
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Warehouse warehouseWithAisleSetUp = null;
        for (Warehouse warehouse : warehouses) {
            if (warehouse.getSetUp().equals(Setup.AISLE) && warehouse.getSiteId().equals("2800")) {
                warehouseWithAisleSetUp = warehouse;
                break;
            }
        }

        List<StorageRoom> storageRooms = warehouseWithAisleSetUp.getStorageRooms();
        StorageRoom storageRoom = storageRooms.get(0);

        List<Zone> zons = storageRoom.getZones();
        Zone zoneWithStorageType = null;
        for (Zone zone : zons) {
            if (zone.getType().equals(ZoneType.STORAGE)) {
                zoneWithStorageType = zone;
                break;
            }
        }

        List<AisleRackRow> aisleRackRows = zoneWithStorageType.getAisleRackRows();
        AisleRackRow aisleRackRow = aisleRackRows.get(0);
        Assert.assertNotNull(aisleRackRow);

        List<BaySetting> baySettings = warehouseService.getBaySettings(aisleRackRow.getAisleRackRowOid());
        BaySetting baySetting = baySettings.get(0);

        // Act
        BaySetting fetchedBaySetting = warehouseService.findBaySetting(baySetting.getBaySettingOid());

        // Assert
        Assert.assertNotNull(fetchedBaySetting);
        Assert.assertEquals(baySetting.getBaySettingOid(), fetchedBaySetting.getBaySettingOid());
    }

    @Test
    public void testUpdateBaySettings() {
        // Arrange
        List<Warehouse> warehouses = warehouseService.getWarehouseList();
        Warehouse warehouseWithAisleSetUp = null;
        for (Warehouse warehouse : warehouses) {
            if (warehouse.getSetUp().equals(Setup.AISLE) && warehouse.getSiteId().equals("2800")) {
                warehouseWithAisleSetUp = warehouse;
                break;
            }
        }

        List<StorageRoom> storageRooms = warehouseWithAisleSetUp.getStorageRooms();
        StorageRoom storageRoom = storageRooms.get(0);

        List<Zone> zons = storageRoom.getZones();
        Zone zoneWithStorageType = null;
        for (Zone zone : zons) {
            if (zone.getType().equals(ZoneType.STORAGE)) {
                zoneWithStorageType = zone;
                break;
            }
        }

        List<AisleRackRow> aisleRackRows = zoneWithStorageType.getAisleRackRows();
        AisleRackRow aisleRackRow = aisleRackRows.get(0);
        Assert.assertNotNull(aisleRackRow);

        List<BaySetting> baySettings = warehouseService.getBaySettings(aisleRackRow.getAisleRackRowOid());
        BaySetting baySetting = baySettings.get(0);

        BaySettingDTO baySettingDTO = new BaySettingDTO();
        baySettingDTO.setId(baySetting.getBaySettingOid());
        baySettingDTO.setBayCode("updated Code");

        // Act
        BaySetting baySettingAfterUpdate = warehouseService.updateBaySetting(baySettingDTO);

        // Assert
        Assert.assertNotNull(baySettingAfterUpdate);
        Assert.assertEquals("updated Code", baySettingAfterUpdate.getBayCode());
    }

    @Test
    public void testCreateUpdateAndFindQIProject() throws GloriaApplicationException {

        // Arrange
        QualityInspectionProjectDTO qualityInspectionProjectDTO = new QualityInspectionProjectDTO();
        String whSiteId = "1722";

        qualityInspectionProjectDTO.setProject("21967906");
        qualityInspectionProjectDTO.setMandatory(true);

        // Act - Create
        QualityInspectionProject qualityInspectionProject = warehouseService.createQualityinspectionProject(qualityInspectionProjectDTO, whSiteId);

        // Assert - Create
        Assert.assertNotNull(qualityInspectionProject);
        Assert.assertEquals("21967906", qualityInspectionProject.getProject());

        QualityInspectionProjectDTO updateQualityInspectionProjectDTO = WarehouseServicesHelper.transformAsDTO(qualityInspectionProject);

        updateQualityInspectionProjectDTO.setProject("21967909");

        // Act - update
        QualityInspectionProject updateQualityInspectionProject = warehouseService.updateQualityinspectionProject(updateQualityInspectionProjectDTO, whSiteId);

        // Assert - update
        Assert.assertNotNull(updateQualityInspectionProject);
        Assert.assertEquals("21967909", updateQualityInspectionProject.getProject());

    }

    @Test(expected = GloriaApplicationException.class)
    public void testCreateQIPart() throws GloriaApplicationException {

        // Arrange
        QualityInspectionPartDTO qualityInspectionPartDTO = new QualityInspectionPartDTO();
        String whSiteId = "1722";

        qualityInspectionPartDTO.setPartNumber("21967906");
        qualityInspectionPartDTO.setPartName("ENGINE BRACKET FRONT RIGHT");
        qualityInspectionPartDTO.setMandatory(true);

     
        QualityInspectionPart qualityInspectionPart = warehouseService.createQualityinspectionPart(qualityInspectionPartDTO, whSiteId);
        
      
        Assert.assertNotNull(qualityInspectionPart);
        Assert.assertEquals("ENGINE BRACKET FRONT RIGHT", qualityInspectionPart.getPartName());
        
        QualityInspectionPartDTO qualityInspectionPartDTO2 = new QualityInspectionPartDTO();
        String whSiteId2 = "1722";

        qualityInspectionPartDTO2.setPartNumber("21967906");
        qualityInspectionPartDTO2.setMandatory(true);
        
        //Act 
        warehouseService.createQualityinspectionPart(qualityInspectionPartDTO2, whSiteId2);

        //Assert Exception

    }
    
    @Test
    public void testUpdateQIPart() throws GloriaApplicationException {

        // Arrange
        QualityInspectionPartDTO qualityInspectionPartDTO = new QualityInspectionPartDTO();
        String whSiteId = "1722";

        qualityInspectionPartDTO.setPartNumber("21967906");
        qualityInspectionPartDTO.setPartName("ENGINE BRACKET FRONT RIGHT");
        qualityInspectionPartDTO.setMandatory(true);
        

        QualityInspectionPart qualityInspectionPart = warehouseService.createQualityinspectionPart(qualityInspectionPartDTO, whSiteId);

        QualityInspectionPartDTO updateQualityInspectionPartDTO = WarehouseServicesHelper.transformAsDTO(qualityInspectionPart);

        updateQualityInspectionPartDTO.setPartName("ENGINE BRACKET FRONT LEFT");
        updateQualityInspectionPartDTO.setPartNumber("21967909");

        // Act
        QualityInspectionPart updateQualityInspectionPart = warehouseService.updateQualityinspectionPart(updateQualityInspectionPartDTO, whSiteId);

        // Assert
        Assert.assertNotNull(updateQualityInspectionPart);
        Assert.assertEquals("ENGINE BRACKET FRONT LEFT", updateQualityInspectionPart.getPartName());

    }

    @Test
    public void testCreateQISupplier() throws GloriaApplicationException {
        // Arrange
        QualityInspectionSupplierDTO qualityInspectionSupplierDTO = new QualityInspectionSupplierDTO();
        String whSiteId = "1722";

        qualityInspectionSupplierDTO.setSupplier("219679");
        qualityInspectionSupplierDTO.setMandatory(true);

        // Act
        QualityInspectionSupplier qualityInspectionSupplier = warehouseService.createQualityinspectionSupplier(qualityInspectionSupplierDTO, whSiteId);

        // Assert
        Assert.assertNotNull(qualityInspectionSupplier);
        Assert.assertEquals("219679", qualityInspectionSupplier.getSupplier());
    }
    
    @Test (expected=ConstraintViolationException.class)
    public void testCreateQISupplierInvalid() throws GloriaApplicationException {
        // Arrange
        QualityInspectionSupplierDTO qualityInspectionSupplierDTO = new QualityInspectionSupplierDTO();
        String whSiteId = "1722";

        qualityInspectionSupplierDTO.setSupplier("21967922");
        qualityInspectionSupplierDTO.setMandatory(true);

        // Act
        QualityInspectionSupplier qualityInspectionSupplier = warehouseService.createQualityinspectionSupplier(qualityInspectionSupplierDTO, whSiteId);

        // Assert
        Assert.assertNotNull(qualityInspectionSupplier);
        Assert.assertEquals("21967906", qualityInspectionSupplier.getSupplier());
    }

    @Test
    public void testUpdateQISupplier() throws GloriaApplicationException {

        // Arrange
        QualityInspectionSupplierDTO qualityInspectionSupplierDTO = new QualityInspectionSupplierDTO();
        String whSiteId = "1722";

        qualityInspectionSupplierDTO.setSupplier("219679");
        qualityInspectionSupplierDTO.setMandatory(true);

        QualityInspectionSupplier qualityInspectionSupplier = warehouseService.createQualityinspectionSupplier(qualityInspectionSupplierDTO, whSiteId);

        QualityInspectionSupplierDTO updateQualityInspectionSupplierDTO = WarehouseServicesHelper.transformAsDTO(qualityInspectionSupplier);
        updateQualityInspectionSupplierDTO.setSupplier("219679");

        // Act
        QualityInspectionSupplier updateQualityInspectionSupplier = warehouseService.updateQualityinspectionSupplier(updateQualityInspectionSupplierDTO, whSiteId);

        // Assert
        Assert.assertNotNull(updateQualityInspectionSupplier);
        Assert.assertEquals("219679", updateQualityInspectionSupplier.getSupplier());
    }

    @Test(expected = GloriaApplicationException.class)
    public void testUpdateQISupplierException() throws GloriaApplicationException {

        // Arrange
        QualityInspectionSupplierDTO qualityInspectionSupplierDTO = new QualityInspectionSupplierDTO();
        String whSiteId = "1722";

        qualityInspectionSupplierDTO.setSupplier("219679");
        qualityInspectionSupplierDTO.setMandatory(true);

        QualityInspectionSupplier qualityInspectionSupplier = warehouseService.createQualityinspectionSupplier(qualityInspectionSupplierDTO, whSiteId);

        QualityInspectionSupplierDTO qualityInspectionSupplierDTO2 = new QualityInspectionSupplierDTO();

        qualityInspectionSupplierDTO2.setSupplier("219679");
        qualityInspectionSupplierDTO2.setMandatory(true);

        warehouseService.createQualityinspectionSupplier(qualityInspectionSupplierDTO2, whSiteId);

        QualityInspectionSupplierDTO updateQualityInspectionSupplierDTO = WarehouseServicesHelper.transformAsDTO(qualityInspectionSupplier);
        updateQualityInspectionSupplierDTO.setSupplier("219679");

        // Act
        warehouseService.updateQualityinspectionSupplier(updateQualityInspectionSupplierDTO, whSiteId);

    }

}
