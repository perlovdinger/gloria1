package com.volvo.gloria.procurematerial.b.beans;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.apache.openjpa.persistence.PersistenceException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.procurematerial.b.DeliveryServices;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.c.DeliveryNoteLineStatus;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteDTO;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteLineDTO;
import com.volvo.gloria.procurematerial.c.dto.DispatchNoteDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.c.dto.PickListDTO;
import com.volvo.gloria.procurematerial.c.dto.RequestGroupDTO;
import com.volvo.gloria.procurematerial.c.dto.RequestListDTO;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNote;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DispatchNote;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLastModified;
import com.volvo.gloria.procurematerial.d.entities.PickList;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.picklist.PickListStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestlist.RequestListStatus;
import com.volvo.gloria.procurematerial.d.type.internalexternal.InternalExternal;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.receive.ReceiveType;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.util.DeliveryHelper;
import com.volvo.gloria.procurematerial.util.MaterialTransformHelper;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.c.BaySides;
import com.volvo.gloria.warehouse.c.Setup;
import com.volvo.gloria.warehouse.c.ZoneType;
import com.volvo.gloria.warehouse.d.entities.AisleRackRow;
import com.volvo.gloria.warehouse.d.entities.BaySetting;
import com.volvo.gloria.warehouse.d.entities.BinLocation;
import com.volvo.gloria.warehouse.d.entities.StorageRoom;
import com.volvo.gloria.warehouse.d.entities.Warehouse;
import com.volvo.gloria.warehouse.d.entities.Zone;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

@Ignore
public class MaterialServiceBeanTest extends AbstractTransactionalTestCase {
    public MaterialServiceBeanTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    private static final String USER = "all";

    @Inject
    private MaterialServices materialServices;

    @Inject
    private UserServices userServices;

    @Inject
    private WarehouseServices warehouseService;

    @Inject
    private DeliveryServices deliveryServices;

    @Inject
    private OrderRepository orderIdRepository;

    @Inject
    private MaterialHeaderRepository requestHeaderRepository;

    private static final String INITDATA_USER_XML = "globaldataTest/UserOrganisationDetails.xml";
    private static final String INITDATA_WAREHOUSE_XML = "globaldataTest/Warehouse.xml";

    @Before
    public void setUpTestData() throws JAXBException, IOException, GloriaApplicationException, InterruptedException {
        userServices.createUserData(IOUtil.getStringFromClasspath(INITDATA_USER_XML));
        warehouseService.createWarehouseData(IOUtil.getStringFromClasspath(INITDATA_WAREHOUSE_XML),null);
        createAisleRackRowSetup();
        Thread.sleep(100);
    }

    private void createAisleRackRowSetup() throws GloriaApplicationException {
        List<Warehouse> storedWarehouses = warehouseService.getWarehouseList();
        Warehouse warehouseWithAisleSetUp = null;
        for (Warehouse warehouse : storedWarehouses) {
            if (warehouse.getSetUp().equals(Setup.AISLE) && warehouse.getSiteId().equals("1622")) {
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
        warehouseService.generateBinlocations(warehouseWithAisleSetUp.getWarehouseOid(), false, USER, "1622");

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
    public void testGetMaterialLines() throws GloriaApplicationException, PersistenceException {
        // Arrange
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);
        String userId = "ALL";

        // Act
        pageObject = materialServices.getMaterialLines(pageObject, userId, userId, null, null, null, null, null, null);

        // Assert
        Assert.assertNotNull(pageObject.getGridContents());
        Assert.assertTrue(pageObject.getGridContents().size() > 0);
        Assert.assertEquals(10, pageObject.getGridContents().size());
    }

    @Test
    public void testGetMaterialLinesWithOutProcureNWhSiteUserRole() throws GloriaApplicationException, PersistenceException {
        // Arrange
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);

        // Act
        pageObject = materialServices.getMaterialLinesForWarehouse(pageObject, null, false, "dc1", "1722", false, null, null,null,null,null,null);

        // Assert
        Assert.assertNull(pageObject);
    }

    @Test
    public void testGetMaterialLinesForStatus() throws GloriaApplicationException {
        // Arrange
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);

        // Act
        pageObject = materialServices.getMaterialLinesForWarehouse(pageObject, MaterialLineStatus.ORDER_PLACED_INTERNAL.name(), false, "all", "1722", false,
                                                                   null, null,null,null,null,null);

        // Assert
        Assert.assertNotNull(pageObject.getGridContents());
        Assert.assertEquals(6, pageObject.getGridContents().size());
    }

    @Test
    public void testUpdateMaterialLines() throws GloriaApplicationException {
        // Arrange
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);
        String userId = "ALL";
        pageObject = materialServices.getMaterialLines(pageObject, userId, userId, null, null, null, null, null, null);

        List<MaterialLineDTO> materialLineDTOs = new ArrayList<MaterialLineDTO>();
        for (PageResults pageResult : pageObject.getGridContents()) {
            MaterialLineDTO materialLineDTO = (MaterialLineDTO) pageResult;
            materialLineDTO.setQuantity(new Long(100));
            materialLineDTO.setStatus("PROCURED");
            materialLineDTOs.add(materialLineDTO);
        }

        // Act
        List<MaterialLine> materialLines = materialServices.updateMaterialLines(materialLineDTOs, null, (MaterialLineStatus.ORDER_PLACED_INTERNAL.name()), 0,
                                                                                null, null, null, null, null, null);

        // Assert
        Assert.assertNotNull(materialLines);
        Assert.assertEquals(10, materialLines.size());
        for (MaterialLine materialLine : materialLines) {
            Assert.assertEquals(new Long(100), materialLine.getQuantity());
            Assert.assertEquals("PROCURED", materialLine.getStatus().toString());
        }
    }

    @Test
    public void testUpdateStoreMaterialLinesWithEqualQuantity() throws GloriaApplicationException {
        // Arrange
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);
        pageObject = materialServices.getMaterialLinesForWarehouse(pageObject, MaterialLineStatus.READY_TO_STORE.name(), false, "all", "1722", false, null,
                                                                   null,null,null,null,null);

        List<Zone> zones = warehouseService.findZonesByZoneTypeAndWhSiteId(ZoneType.STORAGE.name(), "all", "1722");
        List<BinLocation> binLocations = zones.get(0).getBinLocations();
        long binlocationID = binLocations.get(0).getBinLocationOid();

        List<MaterialLineDTO> materialLineDTOsToStore = new ArrayList<MaterialLineDTO>();
        for (PageResults pageResult : pageObject.getGridContents()) {
            MaterialLineDTO materialLineDTO = (MaterialLineDTO) pageResult;
            if (materialLineDTO.getMaterialType().equals(MaterialType.RELEASED)) {
                materialLineDTO.setStoredQuantity(new Long(13));
                materialLineDTO.setBinlocation(binlocationID);
                materialLineDTOsToStore.add(materialLineDTO);
            }
        }

        // Act
        materialServices.updateMaterialLine(materialLineDTOsToStore.get(0), null, "store", false, "all", null, null, null, null);

        // Assert
        List<MaterialLine> materialLines = requestHeaderRepository.findMaterialsByPartStatusSiteAndTransportLabel("12345", "2", MaterialLineStatus.STORED.name(),
                                                                                                                "1722", null, null, null);
        Assert.assertNotNull(materialLines);
        for (MaterialLine materialLine : materialLines) {
            if (materialLine.getStatus() == MaterialLineStatus.STORED) {
                Assert.assertEquals("STORED", materialLine.getStatus().toString());
                Assert.assertTrue(materialLine.getPlacementOID() > 0);
            }
        }
    }

    @Test
    public void testUpdateStoreMaterialLinesWithLessQuantity() throws GloriaApplicationException {
        // Arrange
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);
        pageObject = materialServices.getMaterialLinesForWarehouse(pageObject, MaterialLineStatus.READY_TO_STORE.name(), false, "all", "1722", false, null,
                                                                   null,null,null,null,null);

        List<Zone> zones = warehouseService.findZonesByZoneTypeAndWhSiteId(ZoneType.STORAGE.name(), "all", "1722");
        List<BinLocation> binLocations = zones.get(0).getBinLocations();
        long binlocationID = binLocations.get(0).getBinLocationOid();

        List<MaterialLineDTO> materialLineDTOsToStore = new ArrayList<MaterialLineDTO>();
        for (PageResults pageResult : pageObject.getGridContents()) {
            MaterialLineDTO materialLineDTO = (MaterialLineDTO) pageResult;
            if (materialLineDTO.getMaterialType().equals(MaterialType.RELEASED)) {
                materialLineDTO.setStoredQuantity(new Long(13));
                materialLineDTO.setBinlocation(binlocationID);
                materialLineDTOsToStore.add(materialLineDTO);
            }
        }

        // Act
        materialServices.updateMaterialLine(materialLineDTOsToStore.get(0), null, "store", false, "all", null, null, null, null);

        // Assert
        List<MaterialLine> materialLines = requestHeaderRepository.findMaterialsByPartStatusSiteAndTransportLabel("12345", "2", MaterialLineStatus.STORED.name(),
                                                                                                                "1722", null, null, null);
        Assert.assertNotNull(materialLines);
        for (MaterialLine materialLine : materialLines) {
            Assert.assertEquals("STORED", materialLine.getStatus().toString());
            Assert.assertTrue(materialLine.getPlacementOID() > 0);
        }
    }

    @Test
    public void testFindMaterialByOrderLineId() throws GloriaApplicationException {
        // Arrange

        Order order = new Order();

        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        order.setOrderLines(orderLines);

        OrderLine orderLine = new OrderLine();
        orderLines.add(orderLine);
        orderLine.setOrder(order);
        orderLine.setStatus(OrderLineStatus.PLACED);
        OrderLineLastModified orderLineLastModified11 = new OrderLineLastModified();
        orderLine.setOrderLineLastModified(orderLineLastModified11);
        
        List<Material> materials = new ArrayList<Material>();
        Material material = new Material();
        // material.setOrderLine(orderLine);
        
        MaterialLine materialLine = new MaterialLine();
        materialLine.setMaterial(material);
        materialLine.setQuantity(12L);
        materialLine.setStatus(MaterialLineStatus.ORDER_PLACED_INTERNAL);
        material.getMaterialLine().add(materialLine);
        materials.add(material);
        orderLine.setMaterials(materials);
        deliveryServices.addOrder(order);

        List<OrderLine> fetchedOrderLines = deliveryServices.getAllOrderLines();
        OrderLine fetchedOrderLine = fetchedOrderLines.get(0);
        // Act
        List<Material> fetchedMaterials = materialServices.getMaterials(fetchedOrderLine.getOrderLineOID());
        // Assert
        Assert.assertNotNull(fetchedMaterials);
        // Assert.assertEquals(fetchedOrderLine.getOrderLineOID(), fetchedMaterials.get(0).getOrderLine().getOrderLineOID());
    }

    @Test
    public void testGetRequestGroup() throws GloriaApplicationException, ParseException {

        // Assign

        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);

        RequestList requestListToUpdate = createRequestListTestData();
        String startDateString = "06/27/2014";
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date requiredDeliveryDate = df.parse(startDateString);

        RequestListDTO requestListDTO = new RequestListDTO();
        requestListDTO.setId(requestListToUpdate.getRequestListOid());
        requestListDTO.setVersion(requestListToUpdate.getVersion());
        requestListDTO.setRequiredDeliveryDate(requiredDeliveryDate);
        requestListDTO.setPriority(1L);

        String action = "Send";
        String userId = "all";
        materialServices.updateRequestList(requestListDTO, action, userId);

        // Act
        PageObject pageObjectResult = materialServices.getRequestGroup(pageObject, userId, "1722");

        // Assert
        Assert.assertTrue(pageObjectResult.getGridContents().size() > 0);
        Assert.assertEquals(2, pageObjectResult.getGridContents().size());
    }

    @Test
    public void testupdateRequestGroups() throws GloriaApplicationException, ParseException {

        // Arrange

        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);

        RequestList requestListToUpdate = createRequestListTestData();
        String startDateString = "06/27/2014";
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date requiredDeliveryDate = df.parse(startDateString);

        RequestListDTO requestListDTO = new RequestListDTO();
        requestListDTO.setId(requestListToUpdate.getRequestListOid());
        requestListDTO.setVersion(requestListToUpdate.getVersion());
        requestListDTO.setRequiredDeliveryDate(requiredDeliveryDate);
        requestListDTO.setPriority(1L);

        String action = "Send";
        String userId = "all";
        materialServices.updateRequestList(requestListDTO, action, userId);

        PageObject pageObjectResult = materialServices.getRequestGroup(pageObject, userId, "1722");

        List<PageResults> listOfPageResults = pageObjectResult.getGridContents();

        List<RequestGroupDTO> listOfRequestGroupDTO = new ArrayList<RequestGroupDTO>();
        for (PageResults pageResult : listOfPageResults) {
            RequestGroupDTO requestGroupDto = (RequestGroupDTO) pageResult;
            listOfRequestGroupDTO.add(requestGroupDto);
        }

        // Act
        List<RequestGroup> fetchedRequestGroups = materialServices.updateRequestGroups(listOfRequestGroupDTO, true, null);

        // Assert
        Assert.assertNotNull(fetchedRequestGroups.get(0).getPickList().getCode());
        Assert.assertEquals(listOfRequestGroupDTO.size(), fetchedRequestGroups.size());
        Assert.assertEquals("REQUESTED", fetchedRequestGroups.get(0).getMaterialLines().get(0).getStatus().name());
        Assert.assertEquals("IN_WORK", fetchedRequestGroups.get(0).getPickList().getStatus().name());
    }

    @Test
    public void testcreateAndSendRequestList() throws GloriaApplicationException {
        // Arrange
        String loggerInUserId = "ALL";

        String requisitionId = "123456732";

        Order order = new Order();
        order.setInternalExternal(InternalExternal.EXTERNAL);
        // order.setOrderId(orderId);
        order.setOrderNo("ON1234");
        order.setDeliveryControllerTeam("DEL-FOLLOW-UP-CUR");
        order.setSuffix("519");
        order.setShipToId("1622");

        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        order.setOrderLines(orderLines);

        OrderLine orderLine11 = new OrderLine();
        orderLines.add(orderLine11);
        orderLine11.setOrder(order);
        // orderLine11.setQuantity(9L);
        orderLine11.setReceivedQuantity(0L);
        orderLine11.setRequisitionId(requisitionId);
        orderLine11.setStatus(OrderLineStatus.PLACED);
        orderLine11.setDeliveryControllerUserId("delControllerUser");
        
        OrderLineLastModified orderLineLastModified11 = new OrderLineLastModified();
        orderLine11.setOrderLineLastModified(orderLineLastModified11);

        deliveryServices.addOrder(order);
        deliveryServices.assignMaterialToOrderLine(requisitionId, orderLine11);

        List<Material> materialsToAssert = materialServices.getMaterialByRequisitionId(requisitionId);
        Assert.assertNotNull(materialsToAssert);
        Assert.assertTrue(materialsToAssert.size() > 0);
        OrderLine orderLine = null;
        for (Material material : materialsToAssert) {
            Assert.assertNotNull(material.getOrderLine());
            orderLine = material.getOrderLine();
        }
        Assert.assertNotNull(orderLine);
        // Assert.assertNotNull(orderLine.getMaterialProcure());
        Assert.assertEquals(requisitionId, orderLine.getRequisitionId());

        DeliveryNoteDTO deliveryNoteDTO = new DeliveryNoteDTO();
        deliveryNoteDTO.setCarrier("DHL");
        deliveryNoteDTO.setDeliveryNoteNo("DN1234");
        deliveryNoteDTO.setSupplierId("SUP1234");
        deliveryNoteDTO.setTransportationNo("TN1234");
        deliveryNoteDTO.setOrderNo("ON1234");

        DeliveryNote deliveryNote = deliveryServices.createOrUpdateDeliveryNote(deliveryNoteDTO);
        Assert.assertNotNull(deliveryNote);

        List<DeliveryNoteLineDTO> lineDTOsForUpdate = new ArrayList<DeliveryNoteLineDTO>();
        List<DeliveryNoteLine> deliveryNoteLines = deliveryServices.getDeliveryNoteLines(deliveryNote.getDeliveryNoteOID(), null,
                                                                                         DeliveryNoteLineStatus.IN_WORK, ReceiveType.REGULAR);
        DeliveryNoteLineDTO lineDTO1 = DeliveryHelper.transformAsDTO(deliveryNoteLines.get(0), null);
        lineDTO1.setReceivedQuantity(5L);
        lineDTOsForUpdate.add(lineDTO1);
        deliveryServices.updateDeliveryNoteLines(lineDTOsForUpdate, true, loggerInUserId);

        DeliveryNoteDTO deliveryNoteDTO2 = new DeliveryNoteDTO();
        deliveryNoteDTO2.setCarrier("DHL");
        deliveryNoteDTO2.setDeliveryNoteNo("DN1235");
        deliveryNoteDTO2.setSupplierId("SUP1234");
        deliveryNoteDTO2.setTransportationNo("TN1234");
        deliveryNoteDTO2.setOrderNo("ON1234");

        DeliveryNote deliveryNote2 = deliveryServices.createOrUpdateDeliveryNote(deliveryNoteDTO2);

        List<DeliveryNoteLineDTO> lineDTOsForUpdate2 = new ArrayList<DeliveryNoteLineDTO>();
        List<DeliveryNoteLine> deliveryNoteLines2 = deliveryServices.getDeliveryNoteLines(deliveryNote2.getDeliveryNoteOID(), null,
                                                                                          DeliveryNoteLineStatus.IN_WORK, ReceiveType.REGULAR);
        DeliveryNoteLineDTO lineDTO2 = DeliveryHelper.transformAsDTO(deliveryNoteLines2.get(0), null);
        lineDTO2.setReceivedQuantity(4L);
        lineDTOsForUpdate2.add(lineDTO2);
        deliveryServices.updateDeliveryNoteLines(lineDTOsForUpdate2, true, loggerInUserId);

        List<Material> materials = materialServices.getMaterialByRequisitionId(requisitionId);
        Material material = materials.get(0);

        List<MaterialLine> materialLines = material.getMaterialLine();

        Assert.assertNotNull(materialLines);
        Assert.assertEquals(4, materialLines.size());

        List<MaterialLineDTO> materialLinesToRequest = new ArrayList<MaterialLineDTO>();
        for (MaterialLine materialLine : materialLines) {
            if (materialLine.getStatus().equals(MaterialLineStatus.READY_TO_STORE) || materialLine.getStatus().equals(MaterialLineStatus.STORED)) {
                materialLinesToRequest.add(MaterialTransformHelper.transformAsMaterialLineDTO(materialLine, null));
            }
        }

        RequestListDTO requestListDTO = new RequestListDTO();
        requestListDTO.setDeliveryAddressId("1722");
        requestListDTO.setWhSiteId("1722");

        // Act
        RequestList requestList = materialServices.manageRequestList(materialLinesToRequest, "send", null, 1L , null, "1722", null, loggerInUserId, 0L);

        // Assert
        Assert.assertNotNull(requestList);
        Assert.assertNotNull(requestList.getRequestGroups());
        Assert.assertEquals(2, requestList.getRequestGroups().size());
        Assert.assertEquals(RequestListStatus.SENT, requestList.getStatus());
    }

    @Test
    public void testFindMaterialLinesById() {
        String requisitionId = "123456732";
        List<Material> materials = materialServices.getMaterialByRequisitionId(requisitionId);
        Material material = materials.get(0);

        List<MaterialLine> materialLines = material.getMaterialLine();
        List<Long> materialLineOids = new ArrayList<Long>();
        for (MaterialLine materialLine : materialLines) {
            materialLineOids.add(materialLine.getMaterialLineOID());
        }

        List<MaterialLine> materialLineEntities = materialServices.findMaterialLines(materialLineOids);

        Assert.assertNotNull(materialLineEntities);
        Assert.assertEquals(3, materialLineEntities.size());

    }

    @Test
    public void testGetMaterialsLinesByPickListIdAndStatus() throws GloriaApplicationException {
        // Arrange
        PickList pickList = new PickList();
        pickList.setCode("Code");

        List<Material> materials = materialServices.getMaterialByRequisitionId("123456732");
        Material material = materials.get(0);

        pickList.getMaterialLines().addAll(material.getMaterialLine());

        pickList.setPulledByUserId("A028512");
        List<RequestGroup> listOfRequestGroup = new ArrayList<RequestGroup>();

        RequestList requestList1 = new RequestList();
        requestList1.setWhSiteId("1622");

        RequestGroup requestGroup = new RequestGroup();
        requestGroup.setRequestList(requestList1);
        listOfRequestGroup.add(requestGroup);

        RequestGroup requestGroup1 = new RequestGroup();
        requestGroup1.setRequestList(requestList1);
        listOfRequestGroup.add(requestGroup1);

        RequestGroup requestGroup2 = new RequestGroup();
        requestGroup2.setRequestList(requestList1);
        listOfRequestGroup.add(requestGroup2);

        requestList1.setRequestGroups(listOfRequestGroup);
        pickList.setRequestGroups(listOfRequestGroup);
        materialServices.saveRequestList(requestList1);
        pickList.setStatus(PickListStatus.PICKED);

        PickList savedPickList = materialServices.savePickList(pickList);

        for (MaterialLine materialLine : material.getMaterialLine()) {
            materialLine.setPickList(savedPickList);
            materialServices.addMaterialLine(materialLine);
        }

        PageObject pageObject = new PageObject();
        pageObject.setCount(100);
        // Act
        pageObject = materialServices.getMaterialLines(pageObject, savedPickList.getPickListOid(), MaterialLineStatus.REQUESTED.name(), false);
        // Assert
        Assert.assertNotNull(pageObject);
        Assert.assertTrue(pageObject.getGridContents().size() > 0);
        MaterialLineDTO materialLineDTO = (MaterialLineDTO) pageObject.getGridContents().get(0);
        Assert.assertEquals(MaterialLineStatus.REQUESTED.name(), materialLineDTO.getStatus());

    }

    @Test
    public void testFindPickListById() throws GloriaApplicationException {
        // Arrange
        PickList pickList = new PickList();
        pickList.setCode("Code");

        List<Material> materials = materialServices.getMaterialByRequisitionId("123456732");
        Material material = materials.get(0);

        pickList.getMaterialLines().addAll(material.getMaterialLine());

        pickList.setPulledByUserId("A028512");
        List<RequestGroup> listOfRequestGroup = new ArrayList<RequestGroup>();

        RequestList requestList1 = new RequestList();
        requestList1.setWhSiteId("1622");

        RequestGroup requestGroup = new RequestGroup();
        requestGroup.setRequestList(requestList1);
        listOfRequestGroup.add(requestGroup);

        RequestGroup requestGroup1 = new RequestGroup();
        requestGroup1.setRequestList(requestList1);
        listOfRequestGroup.add(requestGroup1);

        RequestGroup requestGroup2 = new RequestGroup();
        requestGroup2.setRequestList(requestList1);
        listOfRequestGroup.add(requestGroup2);

        requestList1.setRequestGroups(listOfRequestGroup);
        pickList.setRequestGroups(listOfRequestGroup);
        materialServices.saveRequestList(requestList1);
        pickList.setStatus(PickListStatus.PICKED);

        PickList savedPickList = materialServices.savePickList(pickList);

        for (MaterialLine materialLine : material.getMaterialLine()) {
            materialLine.setPickList(savedPickList);
            materialServices.addMaterialLine(materialLine);
        }

        // Act
        PickList fetchedPickList = materialServices.findPickListById(savedPickList.getPickListOid());
        // Assert
        Assert.assertNotNull(fetchedPickList.getMaterialLines());
        Assert.assertEquals(3, fetchedPickList.getMaterialLines().size());
        Assert.assertEquals(savedPickList.getPickListOid(), fetchedPickList.getPickListOid());

    }

    @Test
    public void testFindRequestListById() throws GloriaApplicationException {
        // Arrange
        RequestList requestListToFind = createRequestListTestData();

        // Act
        RequestList requestList = materialServices.findRequestListById(requestListToFind.getRequestListOid());

        // Assert
        Assert.assertNotNull(requestList);
        Assert.assertEquals(requestListToFind.getRequestListOid(), requestList.getRequestListOid());
        RequestGroup requestGroup = requestListToFind.getRequestGroups().get(0);
        Assert.assertEquals(requestGroup.getMaterialLines().get(0).getMaterial().getMaterialHeader().getAccepted().getOutboundLocationId(), requestList.getDeliveryAddressId());
        Assert.assertNotNull(requestList.getRequestUserId());
    }

    private RequestList createRequestListTestData() throws GloriaApplicationException {
        String loggerInUserId = "all";

        String requisitionId = "123456732";

        // OrderId orderId = new OrderId();
        // orderId.setOrderId("PBS001");

        // List<Order> orders = new ArrayList<Order>();
        // orderId.setOrders(orders);

        Order order = new Order();
        // orders.add(order);
        order.setInternalExternal(InternalExternal.EXTERNAL);
        // order.setOrderId(orderId);
        order.setOrderNo("ON1234");
        order.setDeliveryControllerTeam("DEL-FOLLOW-UP-CUR");
        order.setSuffix("519");
        order.setShipToId("1622");

        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        order.setOrderLines(orderLines);

        OrderLine orderLine11 = new OrderLine();
        orderLines.add(orderLine11);
        orderLine11.setOrder(order);
        // orderLine11.setQuantity(9L);
        orderLine11.setReceivedQuantity(0L);
        orderLine11.setRequisitionId(requisitionId);
        orderLine11.setStatus(OrderLineStatus.PLACED);
        orderLine11.setDeliveryControllerUserId("delControllerUser");
        OrderLineLastModified orderLineLastModified11 = new OrderLineLastModified();
        orderLine11.setOrderLineLastModified(orderLineLastModified11);
        orderIdRepository.save(order);
        deliveryServices.assignMaterialToOrderLine(requisitionId, orderLine11);

        List<Material> materialsToAssert = materialServices.getMaterialByRequisitionId(requisitionId);
        Assert.assertNotNull(materialsToAssert);
        Assert.assertTrue(materialsToAssert.size() > 0);
        OrderLine orderLine = null;
        for (Material material : materialsToAssert) {
            Assert.assertNotNull(material.getOrderLine());
            orderLine = material.getOrderLine();
        }
        Assert.assertNotNull(orderLine);
        // Assert.assertNotNull(orderLine.getMaterialProcure());
        Assert.assertEquals(requisitionId, orderLine.getRequisitionId());

        DeliveryNoteDTO deliveryNoteDTO = new DeliveryNoteDTO();
        deliveryNoteDTO.setCarrier("DHL");
        deliveryNoteDTO.setDeliveryNoteNo("DN1234");
        deliveryNoteDTO.setSupplierId("SUP1234");
        deliveryNoteDTO.setTransportationNo("TN1234");
        deliveryNoteDTO.setOrderNo("ON1234");

        DeliveryNote deliveryNote = deliveryServices.createOrUpdateDeliveryNote(deliveryNoteDTO);
        Assert.assertNotNull(deliveryNote);

        List<DeliveryNoteLineDTO> lineDTOsForUpdate = new ArrayList<DeliveryNoteLineDTO>();
        List<DeliveryNoteLine> deliveryNoteLines = deliveryServices.getDeliveryNoteLines(deliveryNote.getDeliveryNoteOID(), null,
                                                                                         DeliveryNoteLineStatus.IN_WORK, ReceiveType.REGULAR);
        DeliveryNoteLineDTO lineDTO1 = DeliveryHelper.transformAsDTO(deliveryNoteLines.get(0), null);
        lineDTO1.setReceivedQuantity(5L);
        lineDTOsForUpdate.add(lineDTO1);
        deliveryServices.updateDeliveryNoteLines(lineDTOsForUpdate, true, loggerInUserId);

        DeliveryNoteDTO deliveryNoteDTO2 = new DeliveryNoteDTO();
        deliveryNoteDTO2.setCarrier("DHL");
        deliveryNoteDTO2.setDeliveryNoteNo("DN1235");
        deliveryNoteDTO2.setSupplierId("SUP1234");
        deliveryNoteDTO2.setTransportationNo("TN1234");
        deliveryNoteDTO2.setOrderNo("ON1234");

        DeliveryNote deliveryNote2 = deliveryServices.createOrUpdateDeliveryNote(deliveryNoteDTO2);

        List<DeliveryNoteLineDTO> lineDTOsForUpdate2 = new ArrayList<DeliveryNoteLineDTO>();
        List<DeliveryNoteLine> deliveryNoteLines2 = deliveryServices.getDeliveryNoteLines(deliveryNote2.getDeliveryNoteOID(), null,
                                                                                          DeliveryNoteLineStatus.IN_WORK, ReceiveType.REGULAR);
        DeliveryNoteLineDTO lineDTO2 = DeliveryHelper.transformAsDTO(deliveryNoteLines2.get(0), null);
        lineDTO2.setReceivedQuantity(4L);
        lineDTOsForUpdate2.add(lineDTO2);
        deliveryServices.updateDeliveryNoteLines(lineDTOsForUpdate2, true, loggerInUserId);

        List<Material> materials = materialServices.getMaterialByRequisitionId(requisitionId);
        Material material = materials.get(0);

        List<MaterialLine> materialLines = material.getMaterialLine();

        Assert.assertNotNull(materialLines);
        Assert.assertEquals(4, materialLines.size());

        List<MaterialLineDTO> materialLineToRequest = new ArrayList<MaterialLineDTO>();
        for (MaterialLine materialLine : materialLines) {
            if (materialLine.getStatus().equals(MaterialLineStatus.READY_TO_STORE) || materialLine.getStatus().equals(MaterialLineStatus.STORED)) {
                materialLineToRequest.add(MaterialTransformHelper.transformAsMaterialLineDTO(materialLine, null));
            }
        }

        RequestListDTO requestListDTO = new RequestListDTO();
        requestListDTO.setDeliveryAddressId("1722");
        requestListDTO.setWhSiteId("1722");

        return materialServices.manageRequestList(materialLineToRequest, "send", null, 1L , null, "1722", null, loggerInUserId, 0L);
    }

    @Test
    public void createRequestListTestDataSuffix() throws Exception {
        throw new RuntimeException("not yet implemented");
    }

    @Test
    public void testFindRequestListByUserId() throws GloriaApplicationException {
        // Arrange
        String requesterUserId = "all";
        String status = "CREATED";
        createRequestListTestData();

        // Act
        List<RequestList> requestLists = materialServices.findRequestListByUserId(requesterUserId.toUpperCase(), status);

        // Assert
        Assert.assertNotNull(requestLists);
        Assert.assertEquals(1, requestLists.size());

    }

    @Test
    public void testUpdateRequestList() throws GloriaApplicationException, ParseException {
        // Arrange
        RequestList requestListToUpdate = createRequestListTestData();
        String startDateString = "06/27/2014";
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date requiredDeliveryDate = df.parse(startDateString);

        RequestListDTO requestListDTO = new RequestListDTO();
        requestListDTO.setId(requestListToUpdate.getRequestListOid());
        requestListDTO.setVersion(requestListToUpdate.getVersion());
        requestListDTO.setRequiredDeliveryDate(requiredDeliveryDate);
        requestListDTO.setPriority(1L);
        requestListDTO.setDeliveryAddressType("NEW_DELIVERY_ADDRESS");
        requestListDTO.setDeliveryAddressName("Test data");

        String action = "Send";

        // Act
        RequestList updatedRequestList = materialServices.updateRequestList(requestListDTO, action, "all");

        // Assert
        Assert.assertNotNull(updatedRequestList);
        Assert.assertEquals(requestListDTO.getPriority(), updatedRequestList.getPriority());
        Assert.assertEquals(requestListDTO.getRequiredDeliveryDate(), updatedRequestList.getRequiredDeliveryDate());
        Assert.assertEquals(requestListDTO.getDeliveryAddressId(), updatedRequestList.getDeliveryAddressId());
        Assert.assertEquals(requestListDTO.getDeliveryAddressName(), updatedRequestList.getDeliveryAddressName());
        Assert.assertEquals(requestListDTO.getDeliveryAddressType(), updatedRequestList.getDeliveryAddressType().toString());
    }

    @Test
    public void testFindPickListByStatus() throws GloriaApplicationException, ParseException {
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);

        RequestList requestListToUpdate = createRequestListTestData();
        String startDateString = "06/27/2014";
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date requiredDeliveryDate = df.parse(startDateString);

        RequestListDTO requestListDTO = new RequestListDTO();
        requestListDTO.setId(requestListToUpdate.getRequestListOid());
        requestListDTO.setVersion(requestListToUpdate.getVersion());
        requestListDTO.setRequiredDeliveryDate(requiredDeliveryDate);
        requestListDTO.setPriority(1L);

        String action = "Send";

        materialServices.updateRequestList(requestListDTO, action, "all");

        String userId = "all";

        PageObject pageObjectResult = materialServices.getRequestGroup(pageObject, userId, "1722");

        List<PageResults> listOfPageResults = pageObjectResult.getGridContents();

        List<RequestGroupDTO> listOfRequestGroupDTO = new ArrayList<RequestGroupDTO>();
        for (PageResults pageResult : listOfPageResults) {
            RequestGroupDTO requestGroupDto = (RequestGroupDTO) pageResult;
            listOfRequestGroupDTO.add(requestGroupDto);
        }
        materialServices.updateRequestGroups(listOfRequestGroupDTO, true, null);

        // Act
        PageObject pageObjectForPickList = new PageObject();
        pageObjectForPickList.setResultsPerPage(100);
        pageObjectForPickList = materialServices.findPickListByStatus(pageObjectForPickList, "1722", MaterialLineStatus.REQUESTED.name(), "tin3000");

        // Assert
        Assert.assertNotNull(pageObjectForPickList.getGridContents());
        Assert.assertEquals(1, pageObjectForPickList.getGridContents().size());
    }

    @Test
    public void testCreateDispatchNote() throws GloriaApplicationException, ParseException {
        // arrange
        Date date = DateUtil.getCurrentUTCDate();
        RequestList requestList = createRequestListTestData();

        DispatchNoteDTO dispatchNoteDTO = new DispatchNoteDTO();
        dispatchNoteDTO.setDeliveryDate(date);
        dispatchNoteDTO.setDispatchNoteDate(date);
        dispatchNoteDTO.setDispatchNoteNo("1");
        dispatchNoteDTO.setDispatchNoteNo("2");
        dispatchNoteDTO.setTransportMode("transportmode");
        dispatchNoteDTO.setDispatchNoteNo("1234");
        dispatchNoteDTO.setWeight("12");
        dispatchNoteDTO.setHeight("13");
        dispatchNoteDTO.setCarrier("14");
        dispatchNoteDTO.setTrackingNo("123");
        dispatchNoteDTO.setNote(dispatchNoteDTO.getNote());
        dispatchNoteDTO.setShipVia(dispatchNoteDTO.getShipVia());
        dispatchNoteDTO.setRequestListStatus("CREATED");

        // act
        DispatchNote dispatchNote = materialServices.createDispatchNote(requestList.getRequestListOid(), dispatchNoteDTO);
        // Assert
        Assert.assertNotNull(dispatchNote);
        Assert.assertEquals("1234", dispatchNote.getDispatchNoteNo());
        Assert.assertEquals(date, dispatchNote.getDeliveryDate());
    }

    @Test
    public void testUpdateDispatchNote() throws GloriaApplicationException, ParseException {

        // arrange
        Date date = DateUtil.getCurrentUTCDate();
        RequestList requestList = createRequestListTestData();
        DispatchNoteDTO dispatchNoteDTO = new DispatchNoteDTO();
        dispatchNoteDTO.setDeliveryDate(date);
        dispatchNoteDTO.setDispatchNoteDate(date);
        dispatchNoteDTO.setDispatchNoteNo("1");
        dispatchNoteDTO.setDispatchNoteNo("2");
        dispatchNoteDTO.setTransportMode("transportmode");
        dispatchNoteDTO.setDispatchNoteNo("1234");
        dispatchNoteDTO.setWeight("12");
        dispatchNoteDTO.setHeight("13");
        dispatchNoteDTO.setCarrier("14");
        dispatchNoteDTO.setTrackingNo("123");
        dispatchNoteDTO.setNote(dispatchNoteDTO.getNote());
        dispatchNoteDTO.setShipVia(dispatchNoteDTO.getShipVia());
        dispatchNoteDTO.setRequestListStatus("CREATED");

        DispatchNote dispatchNote = materialServices.createDispatchNote(requestList.getRequestListOid(), dispatchNoteDTO);
        DispatchNoteDTO updateddispatchNoteDTO = new DispatchNoteDTO();
        updateddispatchNoteDTO.setId(dispatchNote.getDispatchNoteOID());
        updateddispatchNoteDTO.setVersion(dispatchNote.getVersion());
        updateddispatchNoteDTO.setTransportMode("new transport mode");
        updateddispatchNoteDTO.setWeight("10");
        updateddispatchNoteDTO.setHeight("15");
        updateddispatchNoteDTO.setCarrier("new carrier");
        updateddispatchNoteDTO.setTrackingNo("new tracking no");
        updateddispatchNoteDTO.setShipVia("newshipvia");
        updateddispatchNoteDTO.setNote("newnote");

        String startDateString = "06/27/2014";
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date requiredDeliveryDate = df.parse(startDateString);
        updateddispatchNoteDTO.setDeliveryDate(requiredDeliveryDate);
        // act
        DispatchNote dispatchNote2 = materialServices.updateDispatchNote(updateddispatchNoteDTO, "", "all");

        // assert
        Assert.assertNotNull(dispatchNote2);
    }

    @Test
    public void testGetRequestListsByStatus() throws GloriaApplicationException {
        // arrange
        createRequestListTestData();
        PageObject pageObject = new PageObject();
        pageObject.setCurrentPage(1);
        pageObject.setResultsPerPage(10);
        // act
      //  pageObject = materialServices.getRequestLists(pageObject, "CREATED,SENT,PICK_COMPLETED", "1722");
        // assert
        Assert.assertNotNull(pageObject.getGridContents());
        Assert.assertTrue(pageObject.getGridContents().size() > 0);
    }

    @Test
    public void testUpdateRequestListStatusToShipped() throws GloriaApplicationException, ParseException {
        // Arrange
        RequestList requestList = new RequestList();
        requestList.setRequestUserId("1622");
        requestList.setStatus(RequestListStatus.READY_TO_SHIP);
        materialServices.saveRequestList(requestList);

        List<RequestList> requestLists = materialServices.findRequestListByUserId("1622", RequestListStatus.READY_TO_SHIP.name());

        String action = "ship";

        RequestListDTO requestListDTO = MaterialTransformHelper.transformAsRequestListDTO(requestLists.get(0));
        requestListDTO.setDispatchNoteNumber("DNN1234");

        // Act
        RequestList updatedRequestList = materialServices.updateRequestList(requestListDTO, action, "all");

        // Assert
        Assert.assertNotNull(updatedRequestList);
        Assert.assertEquals(RequestListStatus.SHIPPED, updatedRequestList.getStatus());
    }

    @Test(expected = GloriaApplicationException.class)
    public void testUpdateRequestListStatusToShippedMissingDispatchNote() throws GloriaApplicationException, ParseException {
        // Arrange
        RequestList requestList = new RequestList();
        requestList.setRequestUserId("1622");
        requestList.setStatus(RequestListStatus.READY_TO_SHIP);
        materialServices.saveRequestList(requestList);

        List<RequestList> requestLists = materialServices.findRequestListByUserId("1622", RequestListStatus.READY_TO_SHIP.name());

        String action = "ship";

        RequestListDTO requestListDTO = MaterialTransformHelper.transformAsRequestListDTO(requestLists.get(0));

        // Act
        RequestList updatedRequestList = materialServices.updateRequestList(requestListDTO, action, "all");

        // Assert
        Assert.assertNotNull(updatedRequestList);
        Assert.assertEquals(requestListDTO.getStatus(), updatedRequestList.getStatus().name());
    }

    @Test(expected = GloriaApplicationException.class)
    public void testUpdateRequestListStatusToShippedMissingStatus() throws GloriaApplicationException, ParseException {
        // Arrange
        RequestList requestList = new RequestList();
        requestList.setRequestUserId("1622");
        requestList.setStatus(RequestListStatus.SENT);
        materialServices.saveRequestList(requestList);

        List<RequestList> requestLists = materialServices.findRequestListByUserId("1622", RequestListStatus.SENT.name());

        String action = "ship";

        RequestListDTO requestListDTO = MaterialTransformHelper.transformAsRequestListDTO(requestLists.get(0));
        requestListDTO.setDispatchNoteNumber("DNN1234");

        // Act
        RequestList updatedRequestList = materialServices.updateRequestList(requestListDTO, action, "all");

        // Assert
        Assert.assertNotNull(updatedRequestList);
        Assert.assertEquals(requestListDTO.getStatus(), updatedRequestList.getStatus().name());
    }

    @Test
    public void testUpdateRequestListStatusToPickCompleted() throws GloriaApplicationException {
        // Arrange
        PickList pickList = new PickList();
        pickList.setCode("Code");
        pickList.setStatus(PickListStatus.CREATED);

        List<Material> materials = materialServices.getMaterialByRequisitionId("123456732");
        Material material = materials.get(0);
        pickList.getMaterialLines().addAll(material.getMaterialLine());

        pickList.setPulledByUserId("A028512");

        List<RequestGroup> listOfRequestGroup = new ArrayList<RequestGroup>();

        RequestList requestList1 = new RequestList();
        requestList1.setWhSiteId("1622");
        requestList1.setStatus(RequestListStatus.SENT);

        RequestGroup requestGroup = new RequestGroup();
        requestGroup.setRequestList(requestList1);
        requestGroup.setPickList(pickList);
        listOfRequestGroup.add(requestGroup);

        RequestGroup requestGroup1 = new RequestGroup();
        requestGroup1.setRequestList(requestList1);
        requestGroup1.setPickList(pickList);
        listOfRequestGroup.add(requestGroup1);

        RequestGroup requestGroup2 = new RequestGroup();
        requestGroup2.setRequestList(requestList1);
        requestGroup2.setPickList(pickList);
        listOfRequestGroup.add(requestGroup2);

        requestList1.setRequestGroups(listOfRequestGroup);
        pickList.setRequestGroups(listOfRequestGroup);
        materialServices.saveRequestList(requestList1);

        PickList savedPickList = materialServices.savePickList(pickList);

        PickListDTO pickListDTO = new PickListDTO();
        pickListDTO.setId(savedPickList.getPickListOid());
        pickListDTO.setCode(savedPickList.getCode());
        pickListDTO.setPulledByUserId(savedPickList.getPulledByUserId());
        pickListDTO.setVersion(savedPickList.getVersion());
        pickListDTO.setStatus(savedPickList.getStatus().name());

        // Act
        materialServices.updatePickListByAction(pickListDTO, "pick");

        // Assert
        Assert.assertEquals(RequestListStatus.PICK_COMPLETED, requestList1.getStatus());
    }

    @Test
    public void testUpdateMaterialLine() throws GloriaApplicationException {
        // Arrange
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);
        pageObject.setSortBy("pPartNumber");
        pageObject.setSortOrder("asc");
        String userId = "ALL";
        pageObject = materialServices.getMaterialLines(pageObject, userId, userId, null, null, null, null, null, null);
        int materialLinesLength = pageObject.getGridContents().size();
        List<Zone> zones = warehouseService.findZonesByZoneTypeAndWhSiteId(ZoneType.STORAGE.name(), "all", "1722");
        List<BinLocation> binLocations = zones.get(0).getBinLocations();
        long binlocationID = binLocations.get(0).getBinLocationOid();
        MaterialLineDTO materialLineDTO = null;
        for (PageResults pageResult : pageObject.getGridContents()) {
            materialLineDTO = (MaterialLineDTO) pageResult;
            materialLineDTO.setStoredQuantity(1L);
            materialLineDTO.setStatus(MaterialLineStatus.READY_TO_STORE.name());
            materialLineDTO.setBinlocation(binlocationID);
            break;
        }

        // Act
        materialServices.updateMaterialLine(materialLineDTO, null, "store", false, "all", null, null, null, null);

        // Assert
        PageObject pageObj = new PageObject();
        pageObj.setResultsPerPage(100);
        pageObj = materialServices.getMaterialLines(pageObj, userId, userId, null, null, null, null, null, null);
        int materialLinesLengthAfterSplit = pageObj.getGridContents().size();

        Assert.assertEquals(materialLinesLength + 1, materialLinesLengthAfterSplit);
    }

    @Test
    public void testGetRequestGroupsByRequestlistId() throws ParseException, GloriaApplicationException {
        // Arrange
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);

        RequestList requestListToUpdate = createRequestListTestData();

        // Act
        List<RequestGroup> requestGroups = materialServices.getRequestGroups(requestListToUpdate.getRequestListOid());
        // Assert
        Assert.assertNotNull(requestGroups);
        Assert.assertTrue(requestGroups.size() > 0);
    }

    @Test
    public void testFindRequestGroupById() throws GloriaApplicationException {
        // arrange
        createRequestListTestData();
        List<RequestGroup> requestGroups = materialServices.getAllRequestGroups();
        RequestGroup requestGroup = requestGroups.get(0);
        // act
        RequestGroup fetchedRequestGroup = materialServices.findRequestGroupById(requestGroup.getRequestGroupOid());
        // assert
        Assert.assertNotNull(fetchedRequestGroup);
        Assert.assertEquals(requestGroup.getRequestGroupOid(), fetchedRequestGroup.getRequestGroupOid());
    }

    @Test
    public void testFindPicklistByCode() {
        // arrange
        PickList pickList = new PickList();
        pickList.setCode("Code");

        List<Material> materials = materialServices.getMaterialByRequisitionId("123456732");
        Material material = materials.get(0);

        pickList.getMaterialLines().addAll(material.getMaterialLine());

        pickList.setPulledByUserId("A028512");
        List<RequestGroup> listOfRequestGroup = new ArrayList<RequestGroup>();

        RequestList requestList1 = new RequestList();
        requestList1.setWhSiteId("1622");

        RequestGroup requestGroup = new RequestGroup();
        requestGroup.setRequestList(requestList1);
        listOfRequestGroup.add(requestGroup);

        RequestGroup requestGroup1 = new RequestGroup();
        requestGroup1.setRequestList(requestList1);
        listOfRequestGroup.add(requestGroup1);

        RequestGroup requestGroup2 = new RequestGroup();
        requestGroup2.setRequestList(requestList1);
        listOfRequestGroup.add(requestGroup2);

        requestList1.setRequestGroups(listOfRequestGroup);
        pickList.setRequestGroups(listOfRequestGroup);
        materialServices.saveRequestList(requestList1);
        pickList.setStatus(PickListStatus.PICKED);

        PickList savedPickList = materialServices.savePickList(pickList);

        for (MaterialLine materialLine : material.getMaterialLine()) {
            materialLine.setPickList(savedPickList);
            materialServices.addMaterialLine(materialLine);
        }
        // act
        PickList fetchedPickedList = materialServices.findPickListByCode(savedPickList.getCode());

        // assert
        Assert.assertNotNull(fetchedPickedList);
        Assert.assertEquals(savedPickList.getCode(), fetchedPickedList.getCode());
    }

    @Test
    public void testGetRequestGroupsByDispatchNoteId() throws ParseException, GloriaApplicationException {
        // Arrange
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);
        RequestList requestList = createRequestListTestData();
        requestList.setStatus(RequestListStatus.SENT);
        List<RequestGroup> updateRequestGroups = materialServices.updateRequestGroups(MaterialTransformHelper.transformAsRequestGroupDTOs(requestList.getRequestGroups()),
                                                                                      true, null);

        for (RequestGroup requestGroupFetched : updateRequestGroups) {
            PickList pickList = requestGroupFetched.getPickList();
            PickListDTO pickListDTO = new PickListDTO();
            pickListDTO.setId(pickList.getPickListOid());
            pickListDTO.setVersion(pickList.getVersion());
            pickListDTO.setStatus(PickListStatus.PICKED.name());
            materialServices.updatePickListByAction(pickListDTO, "pick");
        }

        DispatchNoteDTO dispatchNoteDTO = new DispatchNoteDTO();

        dispatchNoteDTO.setCarrier("carrier");
        dispatchNoteDTO.setHeight("3");

        DispatchNote dispatchNote = materialServices.createDispatchNoteforRequestList(requestList.getRequestListOid(), dispatchNoteDTO, null, "all");
        // Act
        List<RequestGroup> requestGroupList = materialServices.findRequestGroupsByDispatchNoteId(dispatchNote.getDispatchNoteOID());

        // Assert
        Assert.assertTrue(requestGroupList.size() > 0);
        Assert.assertNotNull(requestGroupList);
    }

    @Test
    public void testFindMaterialLinesByRequestGroupId() throws ParseException, GloriaApplicationException {
        // arrange
        RequestList requestList = createRequestListTestData();
        List<RequestGroup> requestGroups = requestList.getRequestGroups();
        RequestGroup requestGroup = requestGroups.get(0);
        // act
        List<MaterialLine> materialLine = materialServices.findMaterialLinesByRequestGroupId(requestGroup.getRequestGroupOid());
        // assert
        Assert.assertNotNull(materialLine);
        Assert.assertEquals(1, materialLine.size());
    }

    @Test
    public void testUpdateDispatchNoteByRequestListStatus() throws GloriaApplicationException, ParseException {

        // arrange
        Date date = DateUtil.getCurrentUTCDate();
        RequestList requestList = createRequestListTestData();
        DispatchNoteDTO dispatchNoteDTO = new DispatchNoteDTO();
        dispatchNoteDTO.setDeliveryDate(date);
        dispatchNoteDTO.setDispatchNoteDate(date);
        dispatchNoteDTO.setDispatchNoteNo("1");
        dispatchNoteDTO.setDispatchNoteNo("2");
        dispatchNoteDTO.setTransportMode("transportmode");
        dispatchNoteDTO.setDispatchNoteNo("1234");
        dispatchNoteDTO.setWeight("12");
        dispatchNoteDTO.setHeight("13");
        dispatchNoteDTO.setCarrier("14");
        dispatchNoteDTO.setTrackingNo("123");
        dispatchNoteDTO.setNote(dispatchNoteDTO.getNote());
        dispatchNoteDTO.setShipVia(dispatchNoteDTO.getShipVia());
        dispatchNoteDTO.setRequestListStatus("CREATED");

        DispatchNote dispatchNote = materialServices.createDispatchNote(requestList.getRequestListOid(), dispatchNoteDTO);
        DispatchNoteDTO updateddispatchNoteDTO = new DispatchNoteDTO();
        updateddispatchNoteDTO.setId(dispatchNote.getDispatchNoteOID());
        updateddispatchNoteDTO.setVersion(dispatchNote.getVersion());
        updateddispatchNoteDTO.setTransportMode("new transport mode");
        updateddispatchNoteDTO.setWeight("10");
        updateddispatchNoteDTO.setHeight("15");
        updateddispatchNoteDTO.setCarrier("new carrier");
        updateddispatchNoteDTO.setTrackingNo("new tracking no");
        updateddispatchNoteDTO.setShipVia("newshipvia");
        updateddispatchNoteDTO.setNote("newnote");

        String startDateString = "06/27/2014";
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date requiredDeliveryDate = df.parse(startDateString);
        updateddispatchNoteDTO.setDeliveryDate(requiredDeliveryDate);
        // act
        DispatchNote dispatchNote2 = materialServices.updateDispatchNote(updateddispatchNoteDTO, "markAsShipped", "all");

        // assert
        Assert.assertNotNull(dispatchNote2);
        Assert.assertEquals(RequestListStatus.SHIPPED, dispatchNote2.getRequestList().getStatus());
    }

    /*
     * @Test public void testScrapMaterialLines() throws GloriaApplicationException { // Arrange PageObject pageObject = new PageObject();
     * pageObject.setResultsPerPage(100); pageObject = materialServices.getMaterialLines(pageObject, MaterialLineStatus.READY_TO_STORE.name(), null, false,
     * "all");
     * 
     * List<Zone> zones = warehouseService.findZonesByZoneTypeAndWhSiteId(ZoneType.STORAGE.name(), "all"); List<BinLocation> binLocations =
     * zones.get(0).getBinLocations(); long binlocationID = binLocations.get(0).getBinLocationOid();
     * 
     * List<MaterialLineDTO> materialLineDTOsToStore = new ArrayList<MaterialLineDTO>(); for (PageResults pageResult : pageObject.getGridContents()) {
     * MaterialLineDTO materialLineDTO = (MaterialLineDTO) pageResult; if (materialLineDTO.getMaterialType().equals(MaterialType.RELEASED)) {
     * materialLineDTO.setStoredQuantity(new Long(13)); materialLineDTO.setBinlocation(binlocationID); materialLineDTOsToStore.add(materialLineDTO); } }
     * 
     * materialServices.updateMaterialLine(materialLineDTOsToStore.get(0), "store"); List<MaterialLine> materialLines =
     * materialProcureRepository.findMaterialLinesByPartNoAndVersionAndStatus("12345", "2", MaterialLineStatus.STORED);
     * 
     * List<MaterialLineDTO> materialLineDTOs = new ArrayList<MaterialLineDTO>(); for (MaterialLine ml : materialLines) {
     * materialLineDTOs.add(MaterialHelper.transformAsMateiralLineDTO(ml)); } Assert.assertEquals(2, materialLineDTOs.size()); long scrapQuantity = 8L; String
     * confirmationText = "Scrapping the materials which are released";
     * 
     * // Act
     * 
     * List<MaterialLine> scrappedMaterialLines = materialServices.scrapMaterialLines(materialLineDTOs, scrapQuantity, confirmationText, "all");
     * 
     * // Assert Assert.assertNotNull(scrappedMaterialLines); Assert.assertEquals(3, scrappedMaterialLines.size()); Assert.assertNotNull(userLogRepo.findAll());
     * Assert.assertEquals(1, userLogRepo.findAll().size()); }
     */

    /*
     * @Test public void testGetMaterialLinesByQi() throws GloriaApplicationException { // Arrange long requisitionId = 123456732;
     * 
     * Order order = new Order(); order.setInternalExternal(InternalExternal.EXTERNAL); order.setOrderNo("ON1234");
     * order.setDeliveryControllerUserId("delControllerUser"); order.setDeliveryControllerTeam("DEL-FOLLOW-UP-CUR"); order.setSuffix("519");
     * order.setShipToId("1622");
     * 
     * List<OrderLine> orderLines = new ArrayList<OrderLine>(); order.setOrderLines(orderLines);
     * 
     * OrderLine orderLine11 = new OrderLine(); orderLines.add(orderLine11); orderLine11.setOrder(order); orderLine11.setReceivedQuantity(0L);
     * orderLine11.setRequisitionId(requisitionId); orderLine11.setStatus(OrderLineStatus.PLACED);
     * 
     * deliveryServices.addOrder(order); deliveryServices.assignMaterialToOrderLine(requisitionId);
     * 
     * DeliveryNoteDTO deliveryNoteDTO = new DeliveryNoteDTO(); deliveryNoteDTO.setCarrier("DHL"); deliveryNoteDTO.setDeliveryNoteNo("DN1234");
     * deliveryNoteDTO.setSupplierId("SUP1234"); deliveryNoteDTO.setTransportationNo("TN1234"); deliveryNoteDTO.setOrderNo("ON1234");
     * 
     * DeliveryNote deliveryNote = deliveryServices.createOrUpdateDeliveryNote(deliveryNoteDTO);
     * 
     * List<DeliveryNoteLineDTO> lineDTOsForUpdate = new ArrayList<DeliveryNoteLineDTO>(); List<DeliveryNoteLine> deliveryNoteLines =
     * deliveryServices.getDeliveryNoteLines(deliveryNote.getDeliveryNoteOID(), DeliveryNoteLineStatus.IN_WORK); DeliveryNoteLineDTO lineDTO1 =
     * DeliveryHelper.transformAsDTO(deliveryNoteLines.get(0)); lineDTO1.setReceivedQuantity(5L); lineDTO1.setNextZoneCode("B");
     * lineDTOsForUpdate.add(lineDTO1); deliveryServices.updateDeliveryNoteLines(lineDTOsForUpdate, true);
     * 
     * List<Material> materials = materialServices.getMaterialByRequisitionId(requisitionId); List<MaterialLine> materialLines =
     * materials.get(0).getMaterialLine(); Assert.assertNotNull(materialLines);
     * 
     * for (MaterialLine line : materialLines) { if (line.getDeliveryNoteLine() != null) { line.setStatus(MaterialLineStatus.QI_READY);
     * line.getMaterial().getMaterialProcure().setQiMarking(QiMarking.MANDATORY); materialProcureRepository.updateMaterialLine(line); break; } }
     * 
     * PageObject pageObject = new PageObject(); pageObject.setCount(10); pageObject.setResultsPerPage(100);
     * 
     * // Act pageObject = materialServices.getMaterialLineQi(pageObject, MaterialLineStatus.QI_READY.name(), QiMarking.MANDATORY.name(), true, "1622");
     * 
     * // Assert Assert.assertNotNull(pageObject.getGridContents()); Assert.assertEquals(1, pageObject.getGridContents().size());
     * 
     * }
     */

    /*
     * @Test public void testupdateMaterialLineByQi() throws GloriaApplicationException {
     * 
     * long requisitionId = 123456732;
     * 
     * Site site = new Site(); site.setBuildSite(true); site.setBuildSiteType(BuildSiteType.PLANT.name()); site.setCompanyCode("CMP1"); site.setSiteId("1622");
     * site.setSiteName("GOT"); site.setWhSite(true);
     * 
     * commonServices.addSite(site);
     * 
     * Order order = new Order(); order.setInternalExternal(InternalExternal.EXTERNAL); order.setMaterialUserId("all"); order.setOrderNo("ON1234");
     * order.setDeliveryControllerUserId("delControllerUser"); order.setDeliveryControllerTeam("DEL-FOLLOW-UP-CUR"); order.setSuffix("519");
     * order.setShipToId("1622");
     * 
     * List<OrderLine> orderLines = new ArrayList<OrderLine>(); order.setOrderLines(orderLines);
     * 
     * OrderLine orderLine11 = new OrderLine(); orderLines.add(orderLine11); orderLine11.setOrder(order); orderLine11.setReceivedQuantity(0L);
     * orderLine11.setRequisitionId(requisitionId); orderLine11.setUnitOfMeasure("PCS"); orderLine11.setStatus(OrderLineStatus.PLACED);
     * 
     * deliveryServices.addOrder(order); deliveryServices.assignMaterialToOrderLine(requisitionId);
     * 
     * DeliveryNoteDTO deliveryNoteDTO = new DeliveryNoteDTO(); deliveryNoteDTO.setCarrier("DHL"); deliveryNoteDTO.setDeliveryNoteNo("DN1234");
     * deliveryNoteDTO.setSupplierId("SUP1234"); deliveryNoteDTO.setTransportationNo("TN1234"); deliveryNoteDTO.setOrderNo("ON1234");
     * 
     * DeliveryNote deliveryNote = deliveryServices.createOrUpdateDeliveryNote(deliveryNoteDTO);
     * 
     * List<DeliveryNoteLineDTO> lineDTOsForUpdate = new ArrayList<DeliveryNoteLineDTO>(); List<DeliveryNoteLine> deliveryNoteLines =
     * deliveryServices.getDeliveryNoteLines(deliveryNote.getDeliveryNoteOID(), DeliveryNoteLineStatus.IN_WORK); DeliveryNoteLineDTO lineDTO1 =
     * DeliveryHelper.transformAsDTO(deliveryNoteLines.get(0)); lineDTO1.setReceivedQuantity(5L); lineDTO1.setNextZoneCode("B");
     * lineDTOsForUpdate.add(lineDTO1); deliveryServices.updateDeliveryNoteLines(lineDTOsForUpdate, true);
     * 
     * List<Material> materials = materialServices.getMaterialByRequisitionId(requisitionId); List<MaterialLine> materialLines =
     * materials.get(0).getMaterialLine(); Assert.assertNotNull(materialLines);
     * 
     * for (MaterialLine line : materialLines) { if (line.getDeliveryNoteLine() != null) { line.setStatus(MaterialLineStatus.QI_READY);
     * line.getMaterial().getMaterialProcure().setQiMarking(QiMarking.MANDATORY); materialProcureRepository.updateMaterialLine(line); break; } }
     * 
     * PageObject pageObject = new PageObject(); pageObject.setCount(10); pageObject.setResultsPerPage(100);
     * 
     * pageObject = materialServices.getMaterialLineQi(pageObject, MaterialLineStatus.QI_READY.name(), QiMarking.MANDATORY.name(), true, "1622");
     * 
     * MaterialLineQiDTO materialLineQiDTO = (MaterialLineQiDTO) pageObject.getGridContents().get(0); materialLineQiDTO.setApprovedQty(2L);
     * 
     * // Act MaterialLineQiDTO lineQiDTO = materialServices.updateMaterialLineByQi(materialLineQiDTO);
     * 
     * Assert.assertNotNull(lineQiDTO); }
     */
}