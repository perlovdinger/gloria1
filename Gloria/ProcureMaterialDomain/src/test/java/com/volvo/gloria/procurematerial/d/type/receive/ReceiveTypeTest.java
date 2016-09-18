package com.volvo.gloria.procurematerial.d.type.receive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.DangerousGoodsRepository;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.DeliveryServices;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.c.CompleteType;
import com.volvo.gloria.procurematerial.c.DeliveryNoteLineStatus;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNote;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteSubLine;
import com.volvo.gloria.procurematerial.d.entities.DispatchNote;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLastModified;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.c.ZoneType;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class ReceiveTypeTest extends AbstractTransactionalTestCase {
    public ReceiveTypeTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    private static final String INITDATA_SITE_XML = "globaldataTest/Site.xml";
    
    private static final String INITDATA_WAREHOUSE_XML = "globaldataTest/Warehouse.xml";
    
    @Inject
    private WarehouseServices warehouseServices;
    
    @Inject
    private OrderRepository orderRepository;
    
    @Inject
    private DeliveryNoteRepository deliveryNoteRepository;
    
    @Inject
    private DangerousGoodsRepository dangerousGoodsRepository;
    
    @Inject
    private MaterialHeaderRepository requestHeaderRepository;
        
    @Inject
    private CommonServices commonServices;
    
    @Inject
    private TraceabilityRepository traceabilityRepository;
    
    @Inject
    private MaterialServices materialServices;
    
    @Inject
    private DeliveryServices deliveryServices;
    
    @Inject
    private ProcureLineRepository procureLineRepository;
    
    @Before
    public void setUp() throws IOException{
        commonServices.createSitesData(IOUtil.getStringFromClasspath(INITDATA_SITE_XML));
        warehouseServices.createWarehouseData(IOUtil.getStringFromClasspath(INITDATA_WAREHOUSE_XML),null);
    }
    
    /**
     * sendToQI = true
     * directSend = true
     * 
     * @throws GloriaApplicationException
     */
    @Test
    public void testNextLocationForRegularScenario1() throws GloriaApplicationException {
        // arrange
        DeliveryNote deliveryNote = new DeliveryNote();
        deliveryNote.setReceiveType(ReceiveType.REGULAR);
        deliveryNote.setWhSiteId("1722");
        
        DeliveryNoteLine deliveryNoteLine = new DeliveryNoteLine();
        deliveryNoteLine.setDeliveryNote(deliveryNote);
        deliveryNoteLine.setStatus(DeliveryNoteLineStatus.IN_WORK);
        deliveryNoteLine.setSendToQI(true);
        
        DeliveryNoteSubLine deliveryNoteSubLine = new DeliveryNoteSubLine();
        deliveryNoteSubLine.setDirectSend(true);
        deliveryNoteSubLine.setDeliveryNoteLine(deliveryNoteLine);
        
        deliveryNoteLine.getDeliveryNoteSubLines().add(deliveryNoteSubLine);

        // act
        String nextLocation = deliveryNote.getReceiveType().nextLocation(deliveryNoteLine, deliveryNoteSubLine.isDirectSend(), warehouseServices, deliveryServices);
        // assert
        Assert.assertEquals("QI", nextLocation);
    }
    
    /**
     * sendToQI = true
     * directSend = false
     * 
     * @throws GloriaApplicationException
     */
    @Test
    public void testNextLocationForRegularScenario2() throws GloriaApplicationException {
        // arrange
        DeliveryNote deliveryNote = new DeliveryNote();
        deliveryNote.setWhSiteId("1722");
        deliveryNote.setReceiveType(ReceiveType.REGULAR);
        DeliveryNoteLine deliveryNoteLine = new DeliveryNoteLine();
        deliveryNoteLine.setDeliveryNote(deliveryNote);
        deliveryNoteLine.setSendToQI(true);
        
        DeliveryNoteSubLine deliveryNoteSubLine = new DeliveryNoteSubLine();
        deliveryNoteSubLine.setDirectSend(false);
        deliveryNoteLine.setStatus(DeliveryNoteLineStatus.IN_WORK);
        deliveryNoteSubLine.setDeliveryNoteLine(deliveryNoteLine);

        // act
        String nextLocation = deliveryNote.getReceiveType().nextLocation(deliveryNoteLine, deliveryNoteSubLine.isDirectSend(), warehouseServices, deliveryServices);
        // assert
        Assert.assertEquals("QI", nextLocation);
    }
    
    /**
     * sendToQI = false
     * directSend = false
     * 
     * @throws GloriaApplicationException
     */
    @Test
    public void testNextLocationForRegularScenario3() throws GloriaApplicationException {
        // arrange
        DeliveryNote deliveryNote = new DeliveryNote();
        deliveryNote.setWhSiteId("1722");
        deliveryNote.setReceiveType(ReceiveType.REGULAR);
        DeliveryNoteLine deliveryNoteLine = new DeliveryNoteLine();
        deliveryNoteLine.setDeliveryNote(deliveryNote);
        deliveryNoteLine.setSendToQI(false);

        DeliveryNoteSubLine deliveryNoteSubLine = new DeliveryNoteSubLine();
        deliveryNoteSubLine.setDirectSend(false);
        deliveryNoteSubLine.setDeliveryNoteLine(deliveryNoteLine);
        
        // act
        String nextLocation = deliveryNote.getReceiveType().nextLocation(deliveryNoteLine, deliveryNoteSubLine.isDirectSend(), warehouseServices, deliveryServices);
        // assert
        Assert.assertEquals("To Store", nextLocation);
    }
    
    /**
     * sendToQI = false
     * directSend = true
     * 
     * @throws GloriaApplicationException
     * Ignoring test case in GLO-4609 .
     */
    @Test
    public void testNextLocationForRegularScenario4() throws GloriaApplicationException {
        // arrange
        DeliveryNote deliveryNote = new DeliveryNote();
        deliveryNote.setReceiveType(ReceiveType.REGULAR);
        deliveryNote.setWhSiteId("1722");
        DeliveryNoteLine deliveryNoteLine = new DeliveryNoteLine();
        deliveryNoteLine.setDeliveryNote(deliveryNote);
        deliveryNoteLine.setSendToQI(false);

        DeliveryNoteSubLine deliveryNoteSubLine = new DeliveryNoteSubLine();
        deliveryNoteSubLine.setDirectSend(true);
        deliveryNoteSubLine.setDeliveryNoteLine(deliveryNoteLine);
        
        // act
        String nextLocation = deliveryNote.getReceiveType().nextLocation(deliveryNoteLine, deliveryNoteSubLine.isDirectSend(), warehouseServices, deliveryServices);
        // assert
        Assert.assertEquals("Shipping", nextLocation);
    }
    
    /**
     * sendToQI = false
     * directSend = false
     * 
     * @throws GloriaApplicationException
     */
    @Test
    public void testNextLocationForReturn() throws GloriaApplicationException {
        // arrange
        DeliveryNote deliveryNote = new DeliveryNote();
        deliveryNote.setReceiveType(ReceiveType.RETURN);
        deliveryNote.setWhSiteId("1722");
        DeliveryNoteLine deliveryNoteLine = new DeliveryNoteLine();
        deliveryNoteLine.setDeliveryNote(deliveryNote);
        deliveryNoteLine.setSendToQI(false);

        DeliveryNoteSubLine deliveryNoteSubLine = new DeliveryNoteSubLine();
        deliveryNoteSubLine.setDirectSend(false);
        deliveryNoteSubLine.setDeliveryNoteLine(deliveryNoteLine);
        
        // act
        String nextLocation = deliveryNote.getReceiveType().nextLocation(deliveryNoteLine, deliveryNoteSubLine.isDirectSend(), warehouseServices, deliveryServices);
        // assert
        Assert.assertEquals("To Store", nextLocation);
    }
    
    /**
     * sendToQI = false
     * directSend = false
     * 
     * @throws GloriaApplicationException
     */
    @Test
    public void testNextLocationForTransfer() throws GloriaApplicationException {
        // arrange
        DeliveryNote deliveryNote = new DeliveryNote();
        deliveryNote.setReceiveType(ReceiveType.TRANSFER);
        deliveryNote.setWhSiteId("1722");
        DeliveryNoteLine deliveryNoteLine = new DeliveryNoteLine();
        deliveryNoteLine.setDeliveryNote(deliveryNote);
        deliveryNoteLine.setSendToQI(false);

        DeliveryNoteSubLine deliveryNoteSubLine = new DeliveryNoteSubLine();
        deliveryNoteSubLine.setDirectSend(false);
        deliveryNoteSubLine.setDeliveryNoteLine(deliveryNoteLine);
        
        // act
        String nextLocation = deliveryNote.getReceiveType().nextLocation(deliveryNoteLine, deliveryNoteSubLine.isDirectSend(), warehouseServices, deliveryServices);
        // assert
        Assert.assertEquals("To Store", nextLocation);
    }
    
    /**
     * ReceiveType : Transfer
     * 
     * @throws GloriaApplicationException
     */
    @Test
    public void testWhSiteTransfer() throws GloriaApplicationException{
        //arrange
        RequestList requestList = new RequestList();
        requestList.setDeliveryAddressId("DA-1");
        requestList.setWhSiteId("wh-1002");
        RequestGroup requestGroup = new RequestGroup();
        requestGroup.setRequestList(requestList);
        //act
        
        //assert
        Assert.assertEquals("DA-1", ReceiveType.TRANSFER.whSite(requestGroup));
    }
    
    /**
     * ReceiveType : Return
     * 
     * @throws GloriaApplicationException
     */
    @Test
    public void testWhSiteReturn() throws GloriaApplicationException{
        //arrange
        RequestList requestList = new RequestList();
        requestList.setDeliveryAddressId("DA-1");
        requestList.setWhSiteId("wh-1002");
        RequestGroup requestGroup = new RequestGroup();
        requestGroup.setRequestList(requestList);
        //act
        
        //assert
        Assert.assertEquals("wh-1002", ReceiveType.RETURN.whSite(requestGroup));
    }
    
    /**
     * ReceiveType: Regular
     * 
     * DeliveryNoteLine == OrderLine
     * 
     * @throws GloriaApplicationException
     */
    @Test
    public void testCreateDeliveryNoteLinesTypeRegular() throws GloriaApplicationException{
        //arrange
        Order order = new Order();        
        order.setOrderNo("ON-1234");
        
        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        OrderLine orderLine = new OrderLine();
        orderLine.setStatus(OrderLineStatus.PLACED);
        orderLine.setPossibleToReceiveQuantity(10l);
        orderLine.setReceivedQuantity(0l);
        orderLine.setOrder(order);
        orderLines.add(orderLine);
        
        OrderLineLastModified orderLineLastModified11 = new OrderLineLastModified();
        orderLine.setOrderLineLastModified(orderLineLastModified11);
        
        List<OrderLineVersion> orderLineVersions = new ArrayList<OrderLineVersion>();
        OrderLineVersion orderLineVersion = new OrderLineVersion();
        orderLineVersion.setOrderLine(orderLine);
        orderLineVersion.setQuantity(10l);
        orderLineVersions.add(orderLineVersion);
        orderLine.setOrderLineVersions(orderLineVersions);
        
        ProcureLine procureLine = new ProcureLine();
        
        List<Material> materials = new ArrayList<Material>();
        Material material = new Material();
        material.setOrderLine(orderLine);
        material.setProcureLine(procureLine);
        material.setMaterialType(MaterialType.USAGE);        
        
        MaterialLine materialLine = new MaterialLine();
        materialLine.setMaterial(material);
        materialLine.setQuantity(10l);
        materialLine.setDirectSend(DirectSendType.NO);
        materialLine.setStatus(MaterialLineStatus.ORDER_PLACED_INTERNAL);
        material.getMaterialLine().add(materialLine);
        materials.add(material);
        
        
        orderLine.setProcureLine(procureLine);
        procureLine.setMaterials(materials);
        orderLine.setMaterials(materials);
        order.setOrderLines(orderLines);
        orderRepository.save(order);
        
        DeliveryNote deliveryNote = new DeliveryNote();
        deliveryNote.setOrderNo("ON-1234");        
        
        //act
        deliveryNote = ReceiveTypeHelper.createDeliveryNoteLinesForRegular(deliveryNote, deliveryNoteRepository, orderRepository, dangerousGoodsRepository);
        
        //assert
        Assert.assertNotNull(deliveryNote);
        Assert.assertNotNull(deliveryNote.getDeliveryNoteLine());
        Assert.assertEquals(1, deliveryNote.getDeliveryNoteLine().size());
    }
    
    /**
     * ReceiveType: Regular
     * 
     * directSend = true
     * @throws GloriaApplicationException 
     * 
     
    @Test
    public void testReceiveTypeRegular() throws GloriaApplicationException {
        //arrange
        Order order = new Order();        
        order.setOrderNo("ON-1234");
        
        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        OrderLine orderLine = new OrderLine();
        orderLine.setStatus(OrderLineStatus.PLACED);
        orderLine.setAllowedQuantity(10l);
        orderLine.setReceivedQuantity(0l);
        orderLine.setOrder(order);
        orderLines.add(orderLine);
        
        List<OrderLineVersion> orderLineVersions = new ArrayList<OrderLineVersion>();
        OrderLineVersion orderLineVersion = new OrderLineVersion();
        orderLineVersion.setOrderLine(orderLine);
        orderLineVersion.setQuantity(10l);
        orderLineVersions.add(orderLineVersion);
        orderLine.setOrderLineVersions(orderLineVersions);
        
        ProcureLine procureLine = new ProcureLine();
        procureLine.setProcureType(ProcureType.EXTERNAL);
        
        List<Material> materials = new ArrayList<Material>();
        FinanceHeader financeHeader = new FinanceHeader();
        MaterialHeader materialHeader = requestHeaderRepository.save(new MaterialHeader());        
        Material material = new Material();
        material.setFinanceHeader(financeHeader);
        material.setMaterialHeader(materialHeader);
        material.setOrderLine(orderLine);
        material.setProcureLine(procureLine);
        material.setMaterialType(MaterialType.USAGE);
        material.setQuantity(10l);
        material.setDirectSend(DirectSendType.YES_TRANSFER);
        
        MaterialLine materialLine = new MaterialLine();
        materialLine.setMaterial(material);
        materialLine.setQuantity(10l);
        materialLine.setStatus(MaterialLineStatus.ORDER_PLACED_INTERNAL);
        material.getMaterialLine().add(materialLine);
        materials.add(material);
        
        
        orderLine.setProcureLine(procureLine);
        procureLine.setMaterials(materials);
        orderLine.setMaterials(materials);
        order.setOrderLines(orderLines);
        orderRepository.save(order);
        
        DeliveryNote deliveryNote = new DeliveryNote();
        deliveryNote.setOrderNo("ON-1234");
        deliveryNote = ReceiveTypeHelper.createDeliveryNoteLinesForRegular(deliveryNote, deliveryNoteRepository, orderRepository, dangerousGoodsRepository);
        List<DeliveryNoteLine> deliveryNoteLines = deliveryNote.getDeliveryNoteLine();
        deliveryNoteLines.get(0).setReceivedQuantity(1l);
        deliveryNoteLines.get(0).getDeliveryNoteSubLines().get(0).setToReceiveQty(1l);
        
        UserDTO user = new UserDTO();
        user.setId("ALL");
        user.setFirstName("ALL-FN");
        user.setLastName("ALL-LN");
        //act
        ReceiveType.REGULAR.receive(deliveryNote.getDeliveryNoteOID(), user, deliveryNoteRepository, orderRepository, requestHeaderRepository, materialServices, commonServices, traceabilityRepository);
        
        //assert
        deliveryNote = deliveryNoteRepository.findById(deliveryNote.getDeliveryNoteOID());
        Assert.assertNotNull(deliveryNote);
        Assert.assertNotNull(deliveryNote.getDeliveryNoteLine());
        Assert.assertEquals(DeliveryNoteLineStatus.RECEIVED, deliveryNote.getDeliveryNoteLine().get(0).getStatus());
        Assert.assertEquals(1, deliveryNote.getDeliveryNoteLine().get(0).getOrderLine().getReceivedQuantity());
        Assert.assertEquals(OrderLineStatus.RECEIVED_PARTLY, deliveryNote.getDeliveryNoteLine().get(0).getOrderLine().getStatus());    
        
        Assert.assertNotNull(requestHeaderRepository.findRequestListByUserId(user.getId(), RequestListStatus.CREATED.name()));
        
    } 
*/
    /**
     * ReceiveType: Transfer
     * 
     * DeliveryNoteLine == MaterialLine
     * 
     * @throws GloriaApplicationException
     */
    @Test
    public void testCreateDeliveryNoteLinesTypeReturnTransfer() throws GloriaApplicationException{
        //arrange
        DispatchNote dispatchNote = new DispatchNote();
        dispatchNote.setDispatchNoteNo("DS-1");  
        
        RequestList requestList = new RequestList();
        requestList.setDispatchNote(requestHeaderRepository.saveDispatchNote(dispatchNote));
        dispatchNote.setRequestList(requestList);
        
        RequestGroup requestGroup = new RequestGroup();
        requestGroup.setRequestList(requestList);
        
        FinanceHeader financeHeader = new FinanceHeader();
        financeHeader.setProjectId("P100");
        
        Material material = new Material();
        material.setMaterialType(MaterialType.USAGE);
        material.setProcureLine(procureLineRepository.save(new ProcureLine()));
        material.setMaterialHeader(requestHeaderRepository.save(new MaterialHeader()));
        material.setFinanceHeader(financeHeader);
        
        MaterialLine materialLine = new MaterialLine();
        materialLine.setMaterial(material);
        materialLine.setRequestGroup(requestGroup);
        materialLine.setQuantity(10l);        
        materialLine.setStatus(MaterialLineStatus.IN_TRANSFER);
        material.getMaterialLine().add(materialLine);
        requestHeaderRepository.addMaterial(material);
        
        requestGroup.getMaterialLines().add(materialLine);        
        requestList.getRequestGroups().add(requestGroup);
        
        requestHeaderRepository.save(requestList);
        
        DeliveryNote deliveryNote = new DeliveryNote();
        deliveryNote.setDeliveryNoteNo("DS-1");        
        deliveryNote.setReceiveType(ReceiveType.TRANSFER);
        //act
        deliveryNote = ReceiveTypeHelper.createDeliveryNoteLinesForTransferReturn(deliveryNote, deliveryNoteRepository, requestHeaderRepository);
        
        //assert
        Assert.assertNotNull(deliveryNote);
        Assert.assertNotNull(deliveryNote.getDeliveryNoteLine());
        Assert.assertEquals(1, deliveryNote.getDeliveryNoteLine().size());
    }
    

    
    /**
     * ReceiveType: Transfer
     * 
     * @throws GloriaApplicationException 
     * 
     */
    @Test
    public void testReceiveTypeTransferReturn() throws GloriaApplicationException {
        //arrange
        DispatchNote dispatchNote = new DispatchNote();
        dispatchNote.setDispatchNoteNo("DS-1");  
        
        RequestList requestList = new RequestList();
        requestList.setDeliveryAddressId("1722");
        requestList.setDispatchNote(dispatchNote);
        dispatchNote.setRequestList(requestList);
        
        RequestGroup requestGroup = new RequestGroup();
        requestGroup.setRequestList(requestList);
        requestList.getRequestGroups().add(requestGroup);
        requestList = requestHeaderRepository.save(requestList);
       
        Order order = new Order();        
        order.setOrderNo("ON-1234");
        
        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        OrderLine orderLine = new OrderLine();
        orderLine.setStatus(OrderLineStatus.PLACED);
        orderLine.setPossibleToReceiveQuantity(10l);
        orderLine.setReceivedQuantity(0l);
        orderLine.setOrder(order);
        orderLines.add(orderLine);
       
        OrderLineLastModified orderLineLastModified11 = new OrderLineLastModified();
        orderLine.setOrderLineLastModified(orderLineLastModified11);
        
        List<OrderLineVersion> orderLineVersions = new ArrayList<OrderLineVersion>();
        OrderLineVersion orderLineVersion = new OrderLineVersion();
        orderLineVersion.setOrderLine(orderLine);
        orderLineVersion.setQuantity(10l);
        orderLineVersions.add(orderLineVersion);
        orderLine.setOrderLineVersions(orderLineVersions);
        
        ProcureLine procureLine = new ProcureLine();
        
        List<Material> materials = new ArrayList<Material>();
        FinanceHeader financeHeader = new FinanceHeader();
        MaterialHeader materialHeader = requestHeaderRepository.save(new MaterialHeader());        
        Material material = new Material();
        material.setFinanceHeader(financeHeader);
        material.setMaterialHeader(materialHeader);
        material.setOrderLine(orderLine);
        material.setProcureLine(procureLine);
        material.setMaterialType(MaterialType.USAGE);       
        
        MaterialLine materialLine = new MaterialLine();
        materialLine.setWhSiteId("1620");        
        materialLine.setMaterial(material);
        materialLine.setFinalWhSiteId("1722");
        materialLine.setDirectSend(DirectSendType.YES_TRANSFER);
        materialLine.setRequestGroup(requestList.getRequestGroups().get(0));
        materialLine.setQuantity(10l);
        materialLine.setStatus(MaterialLineStatus.SHIPPED);
        material.getMaterialLine().add(materialLine);
        materials.add(material);
        
        
        orderLine.setProcureLine(procureLine);
        procureLine.setMaterials(materials);
        orderLine.setMaterials(materials);
        order.setOrderLines(orderLines);
        orderRepository.save(order);
        
        DeliveryNote deliveryNote = new DeliveryNote();
        deliveryNote.setDeliveryNoteNo("DS-1");        
        deliveryNote.setReceiveType(ReceiveType.TRANSFER);
        
        deliveryNote = ReceiveTypeHelper.createDeliveryNoteLinesForTransferReturn(deliveryNote, deliveryNoteRepository, requestHeaderRepository);
        List<DeliveryNoteLine> deliveryNoteLines = deliveryNote.getDeliveryNoteLine();
        deliveryNoteLines.get(0).setReceivedQuantity(1l);
        
        UserDTO user = new UserDTO();
        user.setId("ALL");
        user.setFirstName("ALL-FN");
        user.setLastName("ALL-LN");
        //act
        ReceiveType.TRANSFER.receive(deliveryNote.getDeliveryNoteOID(), user, deliveryNoteRepository, orderRepository, requestHeaderRepository, materialServices, commonServices, traceabilityRepository, warehouseServices);
        
        //assert
        deliveryNote = deliveryNoteRepository.findById(deliveryNote.getDeliveryNoteOID());
        Assert.assertNotNull(deliveryNote);
        Assert.assertNotNull(deliveryNote.getDeliveryNoteLine());
        Assert.assertEquals(DeliveryNoteLineStatus.RECEIVED, deliveryNote.getDeliveryNoteLine().get(0).getStatus());
        /**
         * ?? no material reference returned ??
         * Assert.assertEquals(1, deliveryNote.getDeliveryNoteLine().get(0).getMaterialLine().size());        
         */
    } 

    @Test
    public void testGLO5181NullPointer() throws GloriaApplicationException {
        List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
        MaterialLine removedLine = new MaterialLine();
        materialLines.add(removedLine);
        ReceiveTypeHelper.evaluateMaterialLinesOnDirectSendType(materialLines, DirectSendType.NO);
    }

    /**
     * sendToQI = false
     * directSend = false
     * 
     * @throws GloriaApplicationException
     */
    @Test
    public void testNextLocationForRegularStorageScenario() throws GloriaApplicationException {
        // arrange
        DeliveryNote deliveryNote = new DeliveryNote();
        deliveryNote.setReceiveType(ReceiveType.REGULAR);
        deliveryNote.setWhSiteId("1722");
        DeliveryNoteLine deliveryNoteLine = new DeliveryNoteLine();
        deliveryNoteLine.setDeliveryNote(deliveryNote);
        deliveryNoteLine.setSendToQI(false);
        deliveryNoteLine.setPartAffiliation("V");
        deliveryNoteLine.setPartModification("pmod1");
        deliveryNoteLine.setPartNumber("1");
        deliveryNoteLine.setPartVersion("A01");
        

        DeliveryNoteSubLine deliveryNoteSubLine = new DeliveryNoteSubLine();
        deliveryNoteSubLine.setDirectSend(false);
        deliveryNoteSubLine.setDeliveryNoteLine(deliveryNoteLine);
        
        MaterialLine materialLine = new MaterialLine();
        Material material = new Material();
        ProcureLine procureLine = new ProcureLine();
        procureLine.setpPartAffiliation("V");
        procureLine.setpPartModification("pmod");
        procureLine.setpPartNumber("1");
        procureLine.setpPartVersion("A01");
        procureLineRepository.save(procureLine);        
        material.setProcureLine(procureLine);
        materialLine.setWhSiteId("1722");
        materialLine.setMaterial(material);
        materialLine.setBinLocationCode("STO-01-01-01");
        materialLine.setStatus(MaterialLineStatus.STORED);
        materialLine.setQuantity(10L);
        material.getMaterialLine().add(materialLine);
        requestHeaderRepository.addMaterial(material);
        
        materialServices.placeIntoZone(materialLine, ZoneType.STORAGE);
        
        // act
        String nextLocation = deliveryNote.getReceiveType().nextLocation(deliveryNoteLine, deliveryNoteSubLine.isDirectSend(), warehouseServices, deliveryServices);
        // assert
        Assert.assertEquals("To Store", nextLocation);
    }
    
    /**
     * ReceiveType: RETURNTRANSFER
     * Test if the deliveryNoteLine if already exist and the materialLine has a different quantity , 
     * then the quantity has to be updated in deliveryNoteLine in case of return transfer.
     * 
     * @throws GloriaApplicationException 
     * 
     */
    @Test
    public void testGLO5304() throws GloriaApplicationException {
        //arrange
        DispatchNote dispatchNote = new DispatchNote();
        dispatchNote.setDispatchNoteNo("DS-1");  
        
        RequestList requestList = new RequestList();
        requestList.setDeliveryAddressId("1722");
        requestList.setDispatchNote(dispatchNote);
        dispatchNote.setRequestList(requestList);
        
        RequestGroup requestGroup = new RequestGroup();
        requestGroup.setRequestList(requestList);
        requestList.getRequestGroups().add(requestGroup);
        requestList = requestHeaderRepository.save(requestList);
       
        Order order = new Order();        
        order.setOrderNo("ON-1234");
        
        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        OrderLine orderLine = new OrderLine();
        orderLine.setStatus(OrderLineStatus.PLACED);
        orderLine.setPossibleToReceiveQuantity(10l);
        orderLine.setReceivedQuantity(0l);
        orderLine.setOrder(order);
        orderLines.add(orderLine);
        
        OrderLineLastModified orderLineLastModified11 = new OrderLineLastModified();
        orderLine.setOrderLineLastModified(orderLineLastModified11);
        List<OrderLineVersion> orderLineVersions = new ArrayList<OrderLineVersion>();
        OrderLineVersion orderLineVersion = new OrderLineVersion();
        orderLineVersion.setOrderLine(orderLine);
        orderLineVersion.setQuantity(10l);
        orderLineVersions.add(orderLineVersion);
        orderLine.setOrderLineVersions(orderLineVersions);
        
        ProcureLine procureLine = new ProcureLine();
        
        List<Material> materials = new ArrayList<Material>();
        FinanceHeader financeHeader = new FinanceHeader();
        MaterialHeader materialHeader = requestHeaderRepository.save(new MaterialHeader());        
        Material material = new Material();
        material.setFinanceHeader(financeHeader);
        material.setMaterialHeader(materialHeader);
        material.setOrderLine(orderLine);
        material.setProcureLine(procureLine);
        material.setMaterialType(MaterialType.USAGE);       
        
        MaterialLine materialLine = new MaterialLine();
        materialLine.setWhSiteId("1620");        
        materialLine.setMaterial(material);
        materialLine.setFinalWhSiteId("1722");
        materialLine.setDirectSend(DirectSendType.YES_TRANSFER);
        materialLine.setRequestGroup(requestList.getRequestGroups().get(0));
        materialLine.setQuantity(9l);
        materialLine.setStatus(MaterialLineStatus.SHIPPED);
        material.getMaterialLine().add(materialLine);
        materials.add(material);
        
        
        orderLine.setProcureLine(procureLine);
        procureLine.setMaterials(materials);
        orderLine.setMaterials(materials);
        order.setOrderLines(orderLines);
        orderRepository.save(order);
        
        DeliveryNote deliveryNote = new DeliveryNote();
        deliveryNote.setDeliveryNoteNo("DS-1");        
        deliveryNote.setReceiveType(ReceiveType.RETURN_TRANSFER);
        
        deliveryNote = ReceiveTypeHelper.createDeliveryNoteLinesForTransferReturn(deliveryNote, deliveryNoteRepository, requestHeaderRepository);
        List<DeliveryNoteLine> deliveryNoteLines = deliveryNote.getDeliveryNoteLine();
        deliveryNoteLines.get(0).setDeliveryNoteQuantity(10L);
        deliveryNoteLines.get(0).setPossibleToReceiveQty(10L);
        
        UserDTO user = new UserDTO();
        user.setId("ALL");
        user.setFirstName("ALL-FN");
        user.setLastName("ALL-LN");
        //act
        deliveryNote = ReceiveTypeHelper.createDeliveryNoteLinesForTransferReturn(deliveryNote, deliveryNoteRepository, requestHeaderRepository);
        
        //assert
        deliveryNote = deliveryNoteRepository.findById(deliveryNote.getDeliveryNoteOID());
        Assert.assertNotNull(deliveryNote);
        Assert.assertNotNull(deliveryNote.getDeliveryNoteLine());
        Assert.assertEquals(9L, deliveryNote.getDeliveryNoteLine().get(0).getDeliveryNoteQuantity());
    } 
    
    @Test(expected = GloriaApplicationException.class)
    public void testGLO5245() throws GloriaApplicationException {
        // arrange
        Order order = new Order();
        order.setOrderNo("ON-1234");

        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        OrderLine orderLine = new OrderLine();
        orderLine.setStatus(OrderLineStatus.COMPLETED);
        orderLine.setCompleteType(CompleteType.COMPLETE);
        orderLines.add(orderLine);
        
        OrderLineLastModified orderLineLastModified1 = new OrderLineLastModified();
        orderLine.setOrderLineLastModified(orderLineLastModified1);
        
        OrderLine orderLine2 = new OrderLine();
        orderLine2.setStatus(OrderLineStatus.COMPLETED);
        orderLine2.setCompleteType(CompleteType.COMPLETE);
        orderLines.add(orderLine2);
        
        order.setOrderLines(orderLines);
        OrderLineLastModified orderLineLastModified2 = new OrderLineLastModified();
        orderLine2.setOrderLineLastModified(orderLineLastModified2);
        
        orderRepository.save(order);

        DeliveryNote deliveryNote = new DeliveryNote();
        deliveryNote.setOrderNo(order.getOrderNo());

        // act
        deliveryNote = ReceiveTypeHelper.createDeliveryNoteLinesForRegular(deliveryNote, deliveryNoteRepository, orderRepository, dangerousGoodsRepository);

        // assert
    }
}

