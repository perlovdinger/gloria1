package com.volvo.gloria.procurematerial.b.beans;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.DeliveryLogDTO;
import com.volvo.gloria.financeProxy.b.GoodsReceiptSender;
import com.volvo.gloria.financeProxy.b.ProcessPurchaseOrderSender;
import com.volvo.gloria.financeProxy.b.beans.GoodsReceiptTransformerBean;
import com.volvo.gloria.financeProxy.b.beans.ProcessPurchaseOrderTransformerBean;
import com.volvo.gloria.financeProxy.c.GoodsReceiptHeaderTransformerDTO;
import com.volvo.gloria.financeProxy.c.GoodsReceiptLineTransformerDTO;
import com.volvo.gloria.financeProxy.c.OrderSapAccountsDTO;
import com.volvo.gloria.financeProxy.c.OrderSapLineDTO;
import com.volvo.gloria.financeProxy.c.OrderSapScheduleDTO;
import com.volvo.gloria.financeProxy.c.ProcessPurchaseOrderDTO;
import com.volvo.gloria.procurematerial.b.DeliveryServices;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.ProcurementDtoTransformer;
import com.volvo.gloria.procurematerial.c.CompleteType;
import com.volvo.gloria.procurematerial.c.DeliveryNoteLineStatus;
import com.volvo.gloria.procurematerial.c.EventType;
import com.volvo.gloria.procurematerial.c.QiMarking;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteDTO;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteLineDTO;
import com.volvo.gloria.procurematerial.c.dto.DeliveryScheduleDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderLineDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderLineLogDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderLogDTO;
import com.volvo.gloria.procurematerial.d.entities.AttachedDoc;
import com.volvo.gloria.procurematerial.d.entities.DeliveryLog;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNote;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliverySchedule;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.ReceiveDoc;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLastModified;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLog;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.OrderLog;
import com.volvo.gloria.procurematerial.d.entities.OrderSap;
import com.volvo.gloria.procurematerial.d.entities.OrderSapAccounts;
import com.volvo.gloria.procurematerial.d.entities.OrderSapLine;
import com.volvo.gloria.procurematerial.d.entities.OrderSapSchedule;
import com.volvo.gloria.procurematerial.d.entities.ProblemDoc;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.QiDoc;
import com.volvo.gloria.procurematerial.d.entities.TransportLabel;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.d.type.internalexternal.InternalExternal;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.procurematerial.d.type.receive.ReceiveType;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderSapRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.util.DeliveryHelper;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.DocumentDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.d.entities.BinLocation;
import com.volvo.gloria.warehouse.d.entities.Printer;
import com.volvo.gloria.warehouse.repositories.b.WarehouseRepository;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class DeliveryServiceBeanTest extends AbstractTransactionalTestCase {
    @Inject
    private ProcurementDtoTransformer procurementDtoTransformer;
    
    public DeliveryServiceBeanTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    private static final String INITDATA_USER_XML = "globaldataTest/UserOrganisationDetails.xml";
    private static final String INITDATA_WAREHOUSE_XML = "globaldataTest/Warehouse.xml";
    private static final String INITDATA_DELIVERY_NOTE_XML = "globaldataTest/DeliveryNote.xml";
    private static final String INITDATA_SITE_XML = "globaldataTest/Site.xml";

    @Inject
    private DeliveryServices deliveryService;
    @Inject
    private UserServices userServices;

    @Inject
    private WarehouseServices warehouseServices;

    @Inject
    MaterialHeaderRepository requestHeaderRepository;
    
    @Inject
    private GoodsReceiptSender goodsReceiptSenderBean; 
    
    @Inject
    private GoodsReceiptTransformerBean goodsReceiptTransformer;
    
    @Inject
    private ProcessPurchaseOrderSender processPurchaseOrderSender;
    
    @Inject
    private OrderSapRepository orderSapRepository;
    
    @Inject
    private ProcureLineRepository procureLineRepository;
    
    @Inject
    private WarehouseRepository warehouseRepository;
    
    @Inject
    private ProcessPurchaseOrderTransformerBean processOrderTransformerBean;
    
    @Inject
    private DeliveryNoteRepository deliveryNoteRepository;
    
    @Inject
    private OrderRepository orderRepository;
    
    @Inject
    private MaterialServices materialServices;
    
    @Inject
    private CommonServices commonServices;
    

    @Before
    public void setUpTestData() throws Exception {
        preparePurchaseOrder();
        deliveryService.createDeliveryNoteData(IOUtil.getStringFromClasspath(INITDATA_DELIVERY_NOTE_XML));
        userServices.createUserData(IOUtil.getStringFromClasspath(INITDATA_USER_XML));
        commonServices.createSitesData(IOUtil.getStringFromClasspath(INITDATA_SITE_XML));
        warehouseServices.createWarehouseData(IOUtil.getStringFromClasspath(INITDATA_WAREHOUSE_XML),null);
    }

    private void preparePurchaseOrder() throws GloriaApplicationException {

        List<Order> orders = new ArrayList<Order>();
        Order order = new Order();
        orders.add(order);
        order.setInternalExternal(InternalExternal.EXTERNAL);
        order.setOrderNo("ON1234");
        order.setMaterialUserCategory("xxxyy");
        order.setMaterialUserId("23434");
        order.setMaterialUserName("xyz");
        order.setSupplierCategory("xxxys");
        order.setSupplierName("abc");
        order.setDeliveryControllerTeam("DEL-FOLLOW-UP-GOT-PT");
        order.setSuffix("519");
        order.setShipToId("1722");

        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        order.setOrderLines(orderLines);

        OrderLine orderLine11 = new OrderLine();
        orderLines.add(orderLine11);
        orderLine11.setDeliveryControllerUserId("delControllerUser");
        orderLine11.setOrder(order);
        orderLine11.setRequisitionId("1234567");
        orderLine11.setPartNumber("P001");
        orderLine11.setSupplierPartNo("SP001");
        orderLine11.setStatus(OrderLineStatus.COMPLETED);
        orderLine11.setCompleteType(CompleteType.RECEIVED);
        
        OrderLineLastModified orderLineLastModified = new OrderLineLastModified();
        orderLine11.setOrderLineLastModified(orderLineLastModified);
        
        List<OrderLineVersion> orderLineVersions = new ArrayList<OrderLineVersion>();
        OrderLineVersion orderLineVersion = new OrderLineVersion();
        orderLineVersion.setPartVersion("V001");
        orderLineVersion.setQuantity(10L);
        orderLineVersion.setOrderLine(orderLine11);
        orderLineVersions.add(orderLineVersion);
        orderLine11.setOrderLineVersions(orderLineVersions);
        orderLine11.setCurrent(orderLineVersion);

        List<DeliverySchedule> deliverySchedules11 = new ArrayList<DeliverySchedule>();
        DeliverySchedule deliverySchedule11 = new DeliverySchedule();
        deliverySchedule11.setExpectedQuantity(10L);
        deliverySchedule11.setExpectedDate(DateUtil.getSqlDate());
        deliverySchedule11.setOrderLine(orderLine11);
        deliverySchedules11.add(deliverySchedule11);
        orderLine11.setDeliverySchedule(deliverySchedules11);

        OrderLine orderLine12 = new OrderLine();
        orderLines.add(orderLine12);
        orderLine12.setOrder(order);
        orderLine12.setRequisitionId("123456730");
        orderLine12.setPartNumber("P002");

        orderLine12.setSupplierPartNo("SP002");
        orderLine12.setShipToPartyId("8410");
        orderLine12.setStatus(OrderLineStatus.PLACED);
        
        OrderLineLastModified orderLineLastModified12 = new OrderLineLastModified();
        orderLine12.setOrderLineLastModified(orderLineLastModified12);

        List<OrderLineVersion> orderLineVersions12 = new ArrayList<OrderLineVersion>();
        OrderLineVersion orderLineVersion12 = new OrderLineVersion();
        orderLineVersion12.setPartVersion("V002");
        orderLineVersion12.setQuantity(12L);
        orderLineVersion12.setOrderLine(orderLine12);
        orderLineVersions12.add(orderLineVersion12);
        orderLine12.setOrderLineVersions(orderLineVersions12);
        orderLine12.setCurrent(orderLineVersion12);

        List<DeliverySchedule> deliverySchedules12 = new ArrayList<DeliverySchedule>();
        DeliverySchedule deliverySchedule12 = new DeliverySchedule();
        deliverySchedule12.setExpectedQuantity(12L);
        deliverySchedule12.setExpectedDate(DateUtil.getSqlDate());
        deliverySchedule12.setOrderLine(orderLine12);
        deliverySchedules12.add(deliverySchedule12);
        orderLine12.setDeliverySchedule(deliverySchedules12);

        ProcureLine procureLine = new ProcureLine();
        procureLine.setRequisitionId("123456730");
        procureLine.setWhSiteId("1622");
        ProcureLine savedProcureLine1 = procureLineRepository.save(procureLine);

        List<Material> materials = new ArrayList<Material>();
        Material material = new Material();
        material.setMaterialType(MaterialType.USAGE);
        material.setOrderLine(orderLine12);
        material.setProcureLine(savedProcureLine1);
        
        MaterialLine materialLine = new MaterialLine();
        materialLine.setDirectSend(DirectSendType.YES_TRANSFER);
        materialLine.setWhSiteId("1622");
        materialLine.setMaterial(material);
        materialLine.setQuantity(12L);
        materialLine.setStatus(MaterialLineStatus.ORDER_PLACED_INTERNAL);
        material.getMaterialLine().add(materialLine);

        materials.add(material);

        orderLine12.setMaterials(materials);

        Order order1 = new Order();
        order1.setInternalExternal(InternalExternal.INTERNAL);
        orders.add(order1);
        order1.setOrderNo("ON245");
        order1.setMaterialUserCategory("xxxyy");
        order1.setMaterialUserId("23434");
        order1.setMaterialUserName("xyz");
        order1.setSupplierId("104");
        order1.setSupplierCategory("xxxys");
        order1.setSupplierName("abc");
        order1.setDeliveryControllerTeam("DEL-FOLLOW-UP-CUR");
        order1.setSuffix("519");
        order1.setShipToId("8410");

        List<OrderLine> orderLines1 = new ArrayList<OrderLine>();
        order1.setOrderLines(orderLines1);

        OrderLine orderLine21 = new OrderLine();
        orderLines1.add(orderLine21);
        orderLine21.setOrder(order1);
        orderLine21.setRequisitionId("2374741");
        orderLine21.setPartNumber("P003");
        orderLine21.setSupplierPartNo("SP003");
        orderLine21.setStatus(OrderLineStatus.PLACED);
        orderLine21.setDeliveryControllerUserId("delControllerUser");

        OrderLineLastModified orderLineLastModified21 = new OrderLineLastModified();
        orderLine21.setOrderLineLastModified(orderLineLastModified21);
        
        List<OrderLineVersion> orderLineVersions21 = new ArrayList<OrderLineVersion>();
        OrderLineVersion orderLineVersion21 = new OrderLineVersion();
        orderLineVersion21.setPartVersion("V003");
        orderLineVersion21.setQuantity(10L);
        orderLineVersion21.setOrderLine(orderLine21);
        orderLineVersions21.add(orderLineVersion21);
        orderLine21.setOrderLineVersions(orderLineVersions21);
        orderLine21.setCurrent(orderLineVersion21);


        List<DeliverySchedule> deliverySchedules21 = new ArrayList<DeliverySchedule>();
        DeliverySchedule deliverySchedule21 = new DeliverySchedule();
        deliverySchedule21.setExpectedQuantity(10L);
        deliverySchedule21.setExpectedDate(DateUtil.getSqlDate());
        deliverySchedule21.setOrderLine(orderLine21);
        deliverySchedules21.add(deliverySchedule21);
        orderLine21.setDeliverySchedule(deliverySchedules21);

        OrderLine orderLine22 = new OrderLine();
        orderLines1.add(orderLine22);
        orderLine22.setOrder(order1);
        orderLine22.setRequisitionId("2374741");
        orderLine22.setPartNumber("P004");
        orderLine22.setSupplierPartNo("SP006");
        orderLine22.setStatus(OrderLineStatus.PLACED);
        
        OrderLineLastModified orderLineLastModified22 = new OrderLineLastModified();
        orderLine22.setOrderLineLastModified(orderLineLastModified22);

        List<OrderLineVersion> orderLineVersions22 = new ArrayList<OrderLineVersion>();
        OrderLineVersion orderLineVersion22 = new OrderLineVersion();
        orderLineVersion22.setPartVersion("V005");
        orderLineVersion22.setQuantity(20L);
        orderLineVersion22.setOrderLine(orderLine22);
        orderLineVersions22.add(orderLineVersion22);
        orderLine22.setOrderLineVersions(orderLineVersions22);
        orderLine22.setCurrent(orderLineVersion22);
        
        List<DeliverySchedule> deliverySchedules22 = new ArrayList<DeliverySchedule>();
        DeliverySchedule deliverySchedule22 = new DeliverySchedule();
        deliverySchedule22.setExpectedQuantity(10L);
        deliverySchedule22.setExpectedDate(DateUtil.getSqlDate());
        deliverySchedule22.setOrderLine(orderLine22);
        deliverySchedules22.add(deliverySchedule22);
        orderLine22.setDeliverySchedule(deliverySchedules22);

        ProcureLine procureLine2 = new ProcureLine();
      
        procureLine2.setRequisitionId("2374741");
        ProcureLine savedProcureLine2 = procureLineRepository.save(procureLine2);

        List<Material> materials2 = new ArrayList<Material>();
        Material material2 = new Material();
        material2.setMaterialType(MaterialType.USAGE);
        material2.setOrderLine(orderLine21);
        material2.setProcureLine(savedProcureLine2);
        
        MaterialLine materialLine2 = new MaterialLine();
        materialLine2.setDirectSend(DirectSendType.YES_TRANSFER);
        materialLine2.setWhSiteId("8410");
        materialLine2.setMaterial(material2);
        materialLine2.setQuantity(10L);
        materialLine2.setStatus(MaterialLineStatus.ORDER_PLACED_INTERNAL);
        material2.getMaterialLine().add(materialLine2);

        materials2.add(material2);

        orderLine21.setMaterials(materials2);
        for(Order iterateOrder : orders){
            deliveryService.addOrder(iterateOrder);
        }
    }
    
    
    private String setUpDeliveryDataForQI(List<MaterialLine> materialLines, long approveQty, boolean isStore) throws GloriaApplicationException {
        List<DeliveryNote> deliveryNotes = deliveryService.getAllDeliveryNotes();
        DeliveryNote deliveryNote = deliveryNotes.get(0);

        List<DeliveryNoteLine> noteLines = deliveryService.getDeliveryNoteLines(deliveryNote.getDeliveryNoteOID(), null , DeliveryNoteLineStatus.IN_WORK, ReceiveType.REGULAR);
        OrderLine orderLine = prepareOrder();
        
        DeliveryNoteLine deliveryNoteLine = noteLines.get(0);
        deliveryNoteLine.getDeliveryNote().setWhSiteId("1722");
        deliveryNoteLine.getDeliveryNoteSubLines().get(0).setToApproveQty(approveQty);
        if (isStore) {
            BinLocation binLocation = warehouseServices.findBinLocationByCode("STO-01-01-01", "1722");
            deliveryNoteLine.getDeliveryNoteSubLines().get(0).setBinLocation(binLocation.getBinLocationOid());
        }
        
        deliveryNoteLine.setMaterialLine(materialLines);
        for (MaterialLine materialLine : materialLines) {
            materialLine.setDeliveryNoteLine(deliveryNoteLine);
        }
        deliveryNoteLine.setOrderLine(orderLine);
        
        deliveryNoteRepository.save(deliveryNoteLine);
        String deliveryNoteLineIDs = String.valueOf(deliveryNoteLine.getDeliveryNoteLineOID());
        return deliveryNoteLineIDs;
    }

    public OrderLine prepareOrder() {
        Order order = new Order();
        order.setOrderNo("1");
        OrderLine orderLine = new OrderLine();
        orderLine.setQiMarking(QiMarking.MANDATORY);
        orderLine.setOrder(order);
        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        orderLines.add(orderLine); 
        
        order.setOrderLines(orderLines);
        orderRepository.save(order);
        return orderLine;
    }

    public MaterialLine prepareMaterialLine() throws GloriaApplicationException {
        MaterialLine materialLine = new MaterialLine();
        materialLine.setWhSiteId("1722");
        materialLine.setDirectSend(DirectSendType.YES_TRANSFER);
        materialLine.setStatus(MaterialLineStatus.QI_READY);
        materialLine.setQuantity(10L);
        
        Material material = new Material();
        
        ProcureLine procureLine = new ProcureLine();
        procureLine.getMaterials().add(material);
        procureLine.setProcureType(ProcureType.EXTERNAL);
        procureLineRepository.save(procureLine);
        
        FinanceHeader financeHeader = new FinanceHeader();
        material.setFinanceHeader(financeHeader);
        
        material.setProcureLine(procureLine);
        materialLine.setMaterial(material);
        MaterialHeader materialHeader = new MaterialHeader();
        MaterialHeaderVersion accepted = new MaterialHeaderVersion();
        materialHeader.setAccepted(accepted);
        
        material.setMaterialHeader(materialHeader);
        materialHeader.getMaterials().add(material);
        requestHeaderRepository.save(materialHeader);
        return materialLine;
    }

    @Test
    public void testFindOrderByDeliveryControllerUserId() throws GloriaApplicationException {
        // Arrange
        String deliveryControllerUserId = "delControllerUser";
        PageObject pageObject = new PageObject();
        HashMap<String, String> predicates = new HashMap<String, String>();
        predicates.put("deliveryControllerUserId", deliveryControllerUserId);
        pageObject.setPredicates(predicates);

        // Act
        PageObject ordersPageObject = deliveryService.findOrders(pageObject, null, "all", true);

        // Assert
        Assert.assertNotNull(ordersPageObject);
        Assert.assertEquals(1, ordersPageObject.getGridContents().size());
    }

    @Test
    public void testUpdateOrder() throws GloriaApplicationException {
        // Arrange
        String deliveryControllerUserId = "delControllerUser";
        PageObject pageObject = new PageObject();
        HashMap<String, String> predicates = new HashMap<String, String>();
        predicates.put("deliveryControllerUserId", deliveryControllerUserId);
        pageObject.setPredicates(predicates);

        PageObject ordersPageObject = deliveryService.findOrders(pageObject, null, "all", true);
        OrderDTO orderDTO = (OrderDTO) ordersPageObject.getGridContents().get(0);
        orderDTO.setDeliveryControllerUserId("delControllerUser-updated");
        // Act
        Order orderAfterUpdate = deliveryService.updateOrder(orderDTO);

        // Assert
        Assert.assertNotNull(orderAfterUpdate);
    }

    /*@Test
    public void testAssignMaterialToOrderLine() throws IOException,GloriaApplicationException {
        // Arrange
        materialService.createMaterialData(IOUtil.getStringFromClasspath(INITDATA_MATERIAL_XML));
        long requisitionId = 123456732;

//        OrderId orderId = new OrderId();
//        orderId.setOrderId("PBS001");
//        List<Order> orders = new ArrayList<Order>();
//        orders.add(order);

        Order order = new Order();
        order.setInternalExternal(InternalExternal.EXTERNAL);
        order.setOrderNo("ON1234");
        order.setDeliveryControllerUserId("delControllerUser");
        order.setDeliveryControllerTeam("DEL-FOLLOW-UP-CUR");
        order.setSuffix("519");
        order.setShipToId("1622");

        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        order.setOrderLines(orderLines);

        OrderLine orderLine11 = new OrderLine();
        orderLines.add(orderLine11);
        orderLine11.setOrder(order);
        orderLine11.setRequisitionId(requisitionId);
        orderLine11.setStatus(OrderLineStatus.PLACED);

        deliveryService.addOrder(order);

        // Act
        deliveryService.assignMaterialToOrderLine(requisitionId);

        // Assert
        List<Material> materials = materialService.getMaterialByRequisitionId(requisitionId);
        Assert.assertNotNull(materials);
        Assert.assertTrue(materials.size() > 0);
        OrderLine orderLine = null;
        for (Material material : materials) {
            Assert.assertNotNull(material.getOrderLine());
            orderLine = material.getOrderLine();
        }
        Assert.assertNotNull(orderLine);
        Assert.assertNotNull(orderLine.getMaterialProcure());
        Assert.assertEquals(requisitionId, orderLine.getRequisitionId().longValue());

    }*/

    @Test
    public void testAddDeliveryNote() throws GloriaApplicationException {
        // Arrange
        List<DeliveryNote> deliveryNotesBeforeAdd = deliveryService.getAllDeliveryNotes();

        DeliveryNoteDTO deliveryNoteDTO = new DeliveryNoteDTO();
        deliveryNoteDTO.setCarrier("DHL");
        deliveryNoteDTO.setDeliveryNoteNo("DN1234");
        deliveryNoteDTO.setSupplierId("SUP1234");
        deliveryNoteDTO.setTransportationNo("TN1234");
        deliveryNoteDTO.setOrderNo("ON1234");
        deliveryNoteDTO.setReceiveType(ReceiveType.REGULAR.name());

        // Act
        DeliveryNote deliveryNote = deliveryService.createOrUpdateDeliveryNote(deliveryNoteDTO);

        // Assert
        Assert.assertNotNull(deliveryNote);
        List<DeliveryNote> deliveryNotesAfterAdd = deliveryService.getAllDeliveryNotes();
        Assert.assertNotNull(deliveryNotesAfterAdd);
        Assert.assertTrue(deliveryNotesAfterAdd.size() > deliveryNotesBeforeAdd.size());
    }

    @Test
    public void testUpdateDeliveryNote() throws GloriaApplicationException {
        // Arrange
        List<DeliveryNote> deliveryNotes = deliveryService.getAllDeliveryNotes();
        DeliveryNote deliveryNote = deliveryNotes.get(0);

        DeliveryNoteDTO deliveryNoteDTO = DeliveryHelper.transformAsDTO(deliveryNote);
        deliveryNoteDTO.setCarrier("BLUEDART");
        deliveryNoteDTO.setTransportationNo("BPL8756");
        deliveryNoteDTO.setReceiveType(ReceiveType.REGULAR.name());

        // Act
        DeliveryNote updatedDeliveryNote = deliveryService.createOrUpdateDeliveryNote(deliveryNoteDTO);

        // Assert
        Assert.assertNotNull(updatedDeliveryNote);
        Assert.assertEquals("BLUEDART", updatedDeliveryNote.getCarrier());
    }

    @Test
    public void testGetDeliveryNoteLines() throws GloriaApplicationException {
        // Arrange
        List<DeliveryNote> deliveryNotes = deliveryService.getAllDeliveryNotes();
        DeliveryNote deliveryNote = deliveryNotes.get(0);

        // Act
        List<DeliveryNoteLine> noteLines = deliveryService.getDeliveryNoteLines(deliveryNote.getDeliveryNoteOID(), "1722", DeliveryNoteLineStatus.IN_WORK, ReceiveType.REGULAR);
        // Assert
        Assert.assertNotNull(noteLines);
    }

    @Test
    public void testUpdateDeliveryNoteLine() throws GloriaApplicationException {
        // Arrange
        List<DeliveryNote> deliveryNotes = deliveryService.getAllDeliveryNotes();
        DeliveryNote deliveryNote = deliveryNotes.get(0);

        List<DeliveryNoteLine> noteLines = deliveryService.getDeliveryNoteLines(deliveryNote.getDeliveryNoteOID(), null, DeliveryNoteLineStatus.IN_WORK, ReceiveType.REGULAR);
        noteLines.get(0).getDeliveryNoteSubLines().get(0).setToReceiveQty(2);        
        DeliveryNoteLineDTO noteLineDTO = DeliveryHelper.transformAsDTO(noteLines.get(0), null);        
        noteLineDTO.setDeliveryNoteQuantity(12L);
        String userId = "all";

        // Act
        DeliveryNoteLine updatedDeliveryNoteLine = deliveryService.updateDeliveryNoteLine(noteLineDTO, userId, false);

        // Assert
        Assert.assertNotNull(updatedDeliveryNoteLine);
        Assert.assertTrue(2L == updatedDeliveryNoteLine.getDeliveryNoteQuantity());
    }

    @Test
    public void testGetDeliveryNoteLineById() throws GloriaApplicationException {
        // Arrange
        List<DeliveryNote> deliveryNotes = deliveryService.getAllDeliveryNotes();
        DeliveryNote deliveryNote = deliveryNotes.get(0);

        List<DeliveryNoteLine> noteLines = deliveryService.getDeliveryNoteLines(deliveryNote.getDeliveryNoteOID(), null , DeliveryNoteLineStatus.IN_WORK, ReceiveType.REGULAR);
        DeliveryNoteLine deliveryNoteLine = noteLines.get(0);

        // Act
        DeliveryNoteLine fetcedDeliveryNoteLine = deliveryService.getDeliveryNoteLineById(deliveryNoteLine.getDeliveryNoteLineOID());

        // Assert
        Assert.assertNotNull(fetcedDeliveryNoteLine);
    }

    @Test
    public void testGetOrdersByDeliveryControllerId() throws ParseException, GloriaApplicationException, IOException {
        // Arrange
        String deliveryControllerId = "delControllerUser";
        String internalExternal = ProcureType.EXTERNAL.name();
        PageObject pageObject = new PageObject();
        pageObject.setCount(100);
        pageObject.setResultsPerPage(10);
        // Act
        pageObject = deliveryService.findOrderLinesForDeliveryControl(null, null, null,null, null, null, null, null, null,
                                                    null, deliveryControllerId, internalExternal, null,
                                                    Boolean.FALSE, null, "all", false, pageObject, null, false, null, null, false);
        // Assert
        Assert.assertNotNull(pageObject);
        Assert.assertNotNull(pageObject.getGridContents());
    }

    @Test
    @Ignore
    public void testFindOrderLinesByPartNumber() throws GloriaApplicationException, IOException, ParseException {
        // Arrange
        String partNumber = "P003"; // like lookup
        PageObject pageObject = new PageObject();
        pageObject.setCount(100);
        pageObject.setResultsPerPage(10);

        // Act
        pageObject = deliveryService.findOrderLinesForDeliveryControl(null, null, null, null, null, null,null, null, null,null, null, null, partNumber, null, "PLACED,RECEIVED_PARTLY", null,
                                                                      false, pageObject, null, false, null, null, false);

        // Assert
        Assert.assertNotNull(pageObject);
        Assert.assertNotNull(pageObject.getGridContents());
        Assert.assertEquals(1, pageObject.getGridContents().size());
        List<PageResults> pageResults = (List<PageResults>) pageObject.getGridContents();
        OrderLineDTO orderLineDTO = (OrderLineDTO) pageResults.get(0);
        Assert.assertEquals("ON245", orderLineDTO.getOrderNo());
        Assert.assertEquals(OrderLineStatus.PLACED.name(), orderLineDTO.getStatus());
    }

    /**
     * Create delivery note and delivery note line based on direct send value.
     * 
     * expected once delivery note and 2 delivery note line for each direct send status
     * 
     * 
     * @throws GloriaApplicationException
     */
   /* @Test
    public void testCreateUpdateDeliveryNote() throws GloriaApplicationException {
        // Arrange

        Order order = new Order();
        order.setOrderNo("ON4444");
        order.setMaterialUserCategory("xxxyy");
        order.setMaterialUserId("23434");
        order.setMaterialUserName("xyz");
        order.setSupplierId("101");
        order.setSupplierCategory("xxxys");
        order.setSupplierName("abc");
        order.setDeliveryControllerUserId("dcUser");

        MaterialProcure materialProcure = new MaterialProcure();
        materialProcure.setDirectSend(true);
        MaterialProcure materialProcureSaved = requestHeaderRepository.save(materialProcure);

        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        OrderLine orderLine11 = new OrderLine();
        orderLine11.setOrder(order);
        orderLine11.setMaterialProcure(materialProcureSaved);
        orderLine11.setRequisitionId(2374741L);
        orderLine11.setPartNumber("P001");
        //orderLine11.setPartVersion("V001");
        orderLine11.setSupplierPartNo("SP001");
        //orderLine11.setQuantity(200L);
        orderLine11.setStatus(OrderLineStatus.PLACED);
        orderLines.add(orderLine11);

        order.setOrderLines(orderLines);

        List<Material> materials = new ArrayList<Material>();
        Material material = new Material();
        material.setMaterialProcure(materialProcureSaved);
        material.setQuantity(100L);

        MaterialLine materialLine = new MaterialLine();
        materialLine.setMaterial(material);
        materialLine.setQuantity(100L);
        materialLine.setStatus(MaterialLineStatus.PROCURED);
        material.getMaterialLine().add(materialLine);

        materials.add(material);

        MaterialProcure materialProcure2 = new MaterialProcure();
        materialProcure2.setDirectSend(false);
        MaterialProcure materialProcureSaved2 = requestHeaderRepository.save(materialProcure2);

        Material material2 = new Material();
        material2.setMaterialProcure(materialProcureSaved2);
        material2.setQuantity(100L);

        MaterialLine materialLine2 = new MaterialLine();
        materialLine2.setMaterial(material2);
        materialLine2.setQuantity(100L);
        materialLine2.setStatus(MaterialLineStatus.PROCURED);
        material.getMaterialLine().add(materialLine2);

        materials.add(material2);

        materialService.addMaterials(materials);

        orderLine11.setMaterials(materials);
        deliveryService.addOrder(order);

        DeliveryNoteDTO deliveryNoteDTO = new DeliveryNoteDTO();
        deliveryNoteDTO.setCarrier("DHL");
        deliveryNoteDTO.setDeliveryNoteNo("DN1234");
        deliveryNoteDTO.setSupplierId("SUP1234");
        deliveryNoteDTO.setTransportationNo("TN1234");
        deliveryNoteDTO.setOrderNo("ON4444");

        // Act
        deliveryService.createOrUpdateDeliveryNote(deliveryNoteDTO);

        // Assert
        List<DeliveryNote> deliveryNotes = deliveryService.getAllDeliveryNotes();

        DeliveryNote deliveryNote = null;
        for (DeliveryNote note : deliveryNotes) {
            if (note.getOrderNo().equals("ON4444")) {
                deliveryNote = note;
                break;
            }
        }

        List<DeliveryNoteLine> deliveryNoteLines = deliveryService.getDeliveryNoteLines(deliveryNote.getId(), DeliveryNoteLineStatus.IN_WORK);

        Assert.assertNotNull(deliveryNoteLines);
        Assert.assertEquals(2, deliveryNoteLines.size());
    }*/

    /**
     * Received Qty is EQUAL TO total Expected Qty
     * 
     * OrderLine qty = 200 MaterialLine1 qty = 100 MaterialLine2 qty = 100
     * 
     * Received Qty = 200
     * 
     * Assertion : 1. MaterialLine1 and MaterialLine2 status to be set to RECEIVED. 2. DeliveryNoteLines to be updated with received qty 100 each 3.
     * DeliveryNoteLine status to be set to 'RECEIVED' 4. OrderLine status to be set to 'RECEIVED'
     * 
     * 
     * 
     * NOTE: THE STATUS 'RECEIVED' is changed to 'READY_TO_STORE' as the QI is not in scope yet.
     * NOTE : QI implementation added, and set to OPTIONAL in this scenario. Hence MaterialLine status would be READY_TO-STORE as direct send is false.
     * 
     * @throws GloriaApplicationException
     */
    /*@Test
    public void testReceiveOrdeLineScenario1() throws GloriaApplicationException {
        // Arrange
//        OrderId orderId = new OrderId();
//        orderId.setOrderId("OID1234");

//        List<Order> orders = new ArrayList<Order>();
//        order.setOrderId(orderId);
        Order order = new Order();
        order.setOrderNo("ON4444");
        order.setMaterialUserCategory("xxxyy");
        order.setMaterialUserId("23434");
        order.setMaterialUserName("xyz");
        order.setSupplierId("101");
        order.setSupplierCategory("xxxys");
        order.setSupplierName("abc");
        order.setDeliveryControllerUserId("dcUser");

        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        OrderLine orderLine11 = new OrderLine();
        orderLine11.setOrder(order);
        orderLine11.setRequisitionId(2374741L);
        orderLine11.setPartNumber("P001");
        //orderLine11.setPartVersion("V001");
        orderLine11.setSupplierPartNo("SP001");
        //orderLine11.setQuantity(200L);
        orderLine11.setStatus(OrderLineStatus.PLACED);
        orderLines.add(orderLine11);

        order.setOrderLines(orderLines);
//        orders.add(order);
//        orderId.setOrders(orders);

        MaterialProcure materialProcure = new MaterialProcure();
        materialProcure.setDirectSend(false);
        materialProcure.setQiMarking(QiMarking.OPTIONAL);
        materialProcure.setRequisitionId(123456730);
        MaterialProcure savedMaterialProcure1 = requestHeaderRepository.save(materialProcure);

        List<Material> materials = new ArrayList<Material>();
        Material material = new Material();
        material.setMaterialProcure(savedMaterialProcure1);
        material.setOrderLine(orderLine11);
        material.setQuantity(100L);

        MaterialLine materialLine = new MaterialLine();
        materialLine.setMaterial(material);
        materialLine.setQuantity(100L);
        materialLine.setStatus(MaterialLineStatus.PROCURED);
        material.getMaterialLine().add(materialLine);

        materials.add(material);

        Material material2 = new Material();
        material2.setMaterialProcure(savedMaterialProcure1);
        material2.setOrderLine(orderLine11);
        material2.setQuantity(100L);

        MaterialLine materialLine2 = new MaterialLine();
        materialLine2.setMaterial(material2);
        materialLine2.setQuantity(100L);
        materialLine2.setStatus(MaterialLineStatus.PROCURED);
        material2.getMaterialLine().add(materialLine2);
        materials.add(material2);

        orderLine11.setMaterials(materials);

        deliveryService.addOrder(order);

        DeliveryNoteDTO deliveryNoteDTO = new DeliveryNoteDTO();
        deliveryNoteDTO.setCarrier("DHL");
        deliveryNoteDTO.setDeliveryNoteNo("DN1234");
        deliveryNoteDTO.setSupplierId("SUP1234");
        deliveryNoteDTO.setTransportationNo("TN1234");
        deliveryNoteDTO.setOrderNo("ON4444");

        DeliveryNote deliveryNote = deliveryService.createOrUpdateDeliveryNote(deliveryNoteDTO);

        List<DeliveryNoteLineDTO> lineDTOsForUpdate = new ArrayList<DeliveryNoteLineDTO>();
        List<DeliveryNoteLine> deliveryNoteLines = deliveryService.getDeliveryNoteLines(deliveryNote.getDeliveryNoteOID(), DeliveryNoteLineStatus.IN_WORK);
        DeliveryNoteLineDTO lineDTO1 = DeliveryHelper.transformAsDTO(deliveryNoteLines.get(0));
        lineDTO1.setReceivedQuantity(100L);
        lineDTOsForUpdate.add(lineDTO1);

        // Act
        deliveryService.updateDeliveryNoteLines(lineDTOsForUpdate, true);

        // Assert
        List<DeliveryNoteLine> noteLines = deliveryService.getDeliveryNoteLines(deliveryNote.getDeliveryNoteOID(), DeliveryNoteLineStatus.RECEIVED);
        Assert.assertNotNull(noteLines);
        Assert.assertEquals(1, noteLines.size());
        List<MaterialLine> noteLine1MaterialLines = noteLines.get(0).getMaterialLine();
        Assert.assertEquals(1, noteLine1MaterialLines.size());
        Assert.assertEquals(MaterialLineStatus.READY_TO_STORE, noteLine1MaterialLines.get(0).getStatus());
        Assert.assertTrue(100L == noteLine1MaterialLines.get(0).getQuantity());
    }*/

    /**
     * Received Qty is LESS THAN total Expected Qty
     * 
     * OrderLine qty = 200 MaterialLine1 qty = 100 MaterialLine2 qty = 100
     * 
     * Received Qty = 50 Damaged Qty = 0
     * 
     * Assertion : 1. MaterialLine1 to be set to RECEIVED and MaterialLine1X to be created with EXPECTED 50 and Material2 with status EXPECTED. 2.
     * DeliveryNoteLine1 to be updated with received qty 50 each 3. DeliveryNoteLine1 status to be set to 'RECEIVED' and DeliveryNoteLine2 to be deleted 4.
     * OrderLine status to be set to 'RECEIVED_PARTLY'
     * 
     * 
     * NOTE: THE STATUS 'RECEIVED' is changed to 'READY_TO_STORE' as the QI is not in scope yet.
     * NOTE : QI implementation added, and set to MANDATORY in this scenario. Hence MaterialLine status would be QI_READY.
     * 
     * @throws GloriaApplicationException
     */
   /* @Test
    public void testReceiveOrdeLineScenario2() throws GloriaApplicationException {
        // Arrange
//        OrderId orderId = new OrderId();
//        orderId.setOrderId("OID1234");
//        List<Order> orders = new ArrayList<Order>();
//        order.setOrderId(orderId);

        Order order = new Order();
        order.setOrderNo("ON4444");
        order.setMaterialUserCategory("xxxyy");
        order.setMaterialUserId("23434");
        order.setMaterialUserName("xyz");
        order.setSupplierId("101");
        order.setSupplierCategory("xxxys");
        order.setSupplierName("abc");
        order.setDeliveryControllerUserId("dcUser");

        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        OrderLine orderLine11 = new OrderLine();
        orderLine11.setOrder(order);
        orderLine11.setRequisitionId(2374741L);
        orderLine11.setPartNumber("P001");
        //orderLine11.setPartVersion("V001");
        orderLine11.setSupplierPartNo("SP001");
        //orderLine11.setQuantity(200L);
        orderLine11.setStatus(OrderLineStatus.PLACED);
        orderLines.add(orderLine11);

        order.setOrderLines(orderLines);
//        orders.add(order);
//        orderId.setOrders(orders);

        MaterialProcure materialProcure = new MaterialProcure();
        materialProcure.setDirectSend(false);
        materialProcure.setQiMarking(QiMarking.MANDATORY);
        materialProcure.setRequisitionId(123456730);
        MaterialProcure savedMaterialProcure1 = requestHeaderRepository.save(materialProcure);

        List<Material> materials = new ArrayList<Material>();
        Material material = new Material();
        material.setMaterialProcure(savedMaterialProcure1);
        material.setOrderLine(orderLine11);
        material.setQuantity(100L);

        MaterialLine materialLine = new MaterialLine();
        materialLine.setMaterial(material);
        materialLine.setQuantity(100L);
        materialLine.setStatus(MaterialLineStatus.PROCURED);
        material.getMaterialLine().add(materialLine);

        materials.add(material);

        Material material2 = new Material();
        material2.setMaterialProcure(savedMaterialProcure1);
        material2.setOrderLine(orderLine11);
        material2.setQuantity(100L);

        MaterialLine materialLine2 = new MaterialLine();
        materialLine2.setMaterial(material2);
        materialLine2.setQuantity(100L);
        materialLine2.setStatus(MaterialLineStatus.PROCURED);
        material2.getMaterialLine().add(materialLine2);
        materials.add(material2);

        orderLine11.setMaterials(materials);

        deliveryService.addOrder(order);

        DeliveryNoteDTO deliveryNoteDTO = new DeliveryNoteDTO();
        deliveryNoteDTO.setCarrier("DHL");
        deliveryNoteDTO.setDeliveryNoteNo("DN1234");
        deliveryNoteDTO.setSupplierId("SUP1234");
        deliveryNoteDTO.setTransportationNo("TN1234");
        deliveryNoteDTO.setOrderNo("ON4444");

        DeliveryNote deliveryNote = deliveryService.createOrUpdateDeliveryNote(deliveryNoteDTO);

        List<DeliveryNoteLineDTO> lineDTOsForUpdate = new ArrayList<DeliveryNoteLineDTO>();
        List<DeliveryNoteLine> deliveryNoteLines = deliveryService.getDeliveryNoteLines(deliveryNote.getDeliveryNoteOID(), DeliveryNoteLineStatus.IN_WORK);
        DeliveryNoteLineDTO lineDTO1 = DeliveryHelper.transformAsDTO(deliveryNoteLines.get(0));
        lineDTO1.setReceivedQuantity(50L);
        lineDTOsForUpdate.add(lineDTO1);

        // Act
        List<DeliveryNoteLine> noteLines = deliveryService.updateDeliveryNoteLines(lineDTOsForUpdate, true);

        // Assert
        Assert.assertNotNull(noteLines);
        Assert.assertEquals(1, noteLines.size());
        List<MaterialLine> noteLine1MaterialLines = noteLines.get(0).getMaterialLine();
        Assert.assertEquals(1, noteLine1MaterialLines.size());
        Assert.assertEquals(MaterialLineStatus.QI_READY, noteLine1MaterialLines.get(0).getStatus());
        Assert.assertTrue(50L == noteLine1MaterialLines.get(0).getQuantity());

        Material updatedMaterial = materialService.getMaterialWithMaterialLinesById(noteLine1MaterialLines.get(0).getMaterial().getMaterialOID());
        Assert.assertEquals(2, updatedMaterial.getMaterialLine().size());
        Assert.assertTrue(50L == updatedMaterial.getMaterialLine().get(0).getQuantity());
        Assert.assertEquals(MaterialLineStatus.QI_READY, updatedMaterial.getMaterialLine().get(0).getStatus());
        Assert.assertTrue(50L == updatedMaterial.getMaterialLine().get(1).getQuantity());
        Assert.assertEquals(MaterialLineStatus.PROCURED, updatedMaterial.getMaterialLine().get(1).getStatus());

        Assert.assertEquals(OrderLineStatus.PLACED, updatedMaterial.getOrderLine().getStatus());
    }*/

    /**
     * Received Qty is EQUAL TO total Expected Qty WITH SOME DAMAGED QTY
     * 
     * OrderLine qty = 200 MaterialLine1 qty = 100 MaterialLine2 qty = 100
     * 
     * Received Qty = 200 Damaged Qty = 50
     * 
     * Assertion : 1. MaterialLine1 a d MaterialLine2 to be set to RECEIVED and MaterialLine2x to be created with RECEIVED_DAMAGED 50. 2. DeliveryNoteLine to be
     * updated with received qty 200 3. DeliveryNoteLine status to be set to 'RECEIVED' 4. OrderLine status to be set to 'RECEIVED'
     * 
     * 
     * NOTE: THE STATUS 'RECEIVED' is changed to 'READY_TO_STORE' as the QI is not in scope yet.
     * 
     *  NOTE : QI implementation added, and set to OPTIONAL in this scenario. Hence MaterialLine status would be READY_TO_SHIP, as direct send is true.
     * @throws GloriaApplicationException
     */
   /* @Test
    public void testReceiveOrdeLineScenario3() throws GloriaApplicationException {
        // Arrange

        Order order = new Order();
        order.setOrderNo("ON4444");
        order.setMaterialUserCategory("xxxyy");
        order.setMaterialUserId("23434");
        order.setMaterialUserName("xyz");
        order.setSupplierId("101");
        order.setSupplierCategory("xxxys");
        order.setSupplierName("abc");
        order.setDeliveryControllerUserId("dcUser");

        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        OrderLine orderLine11 = new OrderLine();
        orderLine11.setOrder(order);
        orderLine11.setRequisitionId(2374741L);
        orderLine11.setPartNumber("P001");
        orderLine11.setSupplierPartNo("SP001");
        orderLine11.setStatus(OrderLineStatus.PLACED);
        orderLines.add(orderLine11);

        order.setOrderLines(orderLines);

        MaterialProcure materialProcure = new MaterialProcure();
        materialProcure.setDirectSend(true);
        materialProcure.setQiMarking(QiMarking.OPTIONAL);
        materialProcure.setRequisitionId(123456730);
        MaterialProcure savedMaterialProcure1 = requestHeaderRepository.save(materialProcure);

        List<Material> materials = new ArrayList<Material>();
        Material material = new Material();
        material.setMaterialProcure(savedMaterialProcure1);
        material.setOrderLine(orderLine11);
        material.setQuantity(100L);

        MaterialLine materialLine = new MaterialLine();
        materialLine.setMaterial(material);
        materialLine.setQuantity(100L);
        materialLine.setStatus(MaterialLineStatus.PROCURED);
        material.getMaterialLine().add(materialLine);

        materials.add(material);

        Material material2 = new Material();
        material2.setMaterialProcure(savedMaterialProcure1);
        material2.setOrderLine(orderLine11);
        material2.setQuantity(100L);

        MaterialLine materialLine2 = new MaterialLine();
        materialLine2.setMaterial(material2);
        materialLine2.setQuantity(100L);
        materialLine2.setStatus(MaterialLineStatus.PROCURED);
        material2.getMaterialLine().add(materialLine2);
        materials.add(material2);

        orderLine11.setMaterials(materials);

        deliveryService.addOrder(order);

        DeliveryNoteDTO deliveryNoteDTO = new DeliveryNoteDTO();
        deliveryNoteDTO.setCarrier("DHL");
        deliveryNoteDTO.setDeliveryNoteNo("DN1234");
        deliveryNoteDTO.setSupplierId("SUP1234");
        deliveryNoteDTO.setTransportationNo("TN1234");
        deliveryNoteDTO.setOrderNo("ON4444");

        DeliveryNote deliveryNote = deliveryService.createOrUpdateDeliveryNote(deliveryNoteDTO);

        List<DeliveryNoteLineDTO> lineDTOsForUpdate = new ArrayList<DeliveryNoteLineDTO>();
        List<DeliveryNoteLine> deliveryNoteLines = deliveryService.getDeliveryNoteLines(deliveryNote.getDeliveryNoteOID(), DeliveryNoteLineStatus.IN_WORK);
        DeliveryNoteLineDTO lineDTO1 = DeliveryHelper.transformAsDTO(deliveryNoteLines.get(0));
        lineDTO1.setReceivedQuantity(200L);
        lineDTO1.setDamagedQuantity(50L);
        lineDTOsForUpdate.add(lineDTO1);

        // Act
        List<DeliveryNoteLine> noteLines = deliveryService.updateDeliveryNoteLines(lineDTOsForUpdate, true);

        // Assert
        Assert.assertNotNull(noteLines);
        Assert.assertEquals(1, noteLines.size());

        List<MaterialLine> noteLine1MaterialLines = noteLines.get(0).getMaterialLine();

        Assert.assertEquals(3, noteLine1MaterialLines.size());
        Assert.assertEquals(MaterialLineStatus.READY_TO_SHIP, noteLine1MaterialLines.get(0).getStatus());
        Assert.assertTrue(100L == noteLine1MaterialLines.get(0).getQuantity());
        Assert.assertEquals(MaterialLineStatus.BLOCKED, noteLine1MaterialLines.get(1).getStatus());
        Assert.assertTrue(50L == noteLine1MaterialLines.get(1).getQuantity());
        Assert.assertEquals(MaterialLineStatus.READY_TO_SHIP, noteLine1MaterialLines.get(2).getStatus());
        Assert.assertTrue(50L == noteLine1MaterialLines.get(2).getQuantity());

        Assert.assertEquals(OrderLineStatus.PLACED, noteLine1MaterialLines.get(0).getMaterial().getOrderLine().getStatus());
    }
*/
    @Test
    public void testGetDeliveryNoteById() {
        // Arrange
        List<DeliveryNote> deliveryNotes = deliveryService.getAllDeliveryNotes();
        DeliveryNote deliveryNote = deliveryNotes.get(0);
        deliveryNote.setWhSiteId("1722");
        // Act
        DeliveryNote fetchedDeliveryNote = deliveryService.getDeliveryNoteById(deliveryNote.getDeliveryNoteOID(), "1722");
        // Assert
        Assert.assertNotNull(fetchedDeliveryNote);
        Assert.assertEquals(deliveryNote.getDeliveryNoteNo(), fetchedDeliveryNote.getDeliveryNoteNo());
    }

    /*@Test
    public void testSuggestBinLocation() throws IOException, GloriaApplicationException {
        // Arrange
        materialService.createMaterialData(IOUtil.getStringFromClasspath(INITDATA_MATERIAL_XML));
        warehouseServices.createWarehouseData(IOUtil.getStringFromClasspath(INITDATA_WAREHOUSE_XML));
        createAisleRackRowSetup();

        List<Material> materials = materialService.getMaterialByRequisitionId(123456732);
        Material material = materials.get(0);

        MaterialLine materialLine = null;
        for (MaterialLine someMaterialLine : material.getMaterialLine()) {
            if (someMaterialLine.getStatus().name().equals("STORED")) {
                materialLine = someMaterialLine;
                break;
            }
        }
        MaterialProcure materialProcure = material.getMaterialProcure();

        // Act
        BinLocation suggestedBinLocation = requestHeaderRepository.suggestBinLocation(materialProcure.getpPartNumber(), materialProcure.getpPartVersion(), "1622");
        // Assert
        Assert.assertNull(suggestedBinLocation);

        List<Zone> zones = warehouseServices.findZonesByZoneTypeAndWhSiteId(ZoneType.STORAGE.name(), "all");
        List<BinLocation> binLocations = zones.get(0).getBinLocations();

        BinLocation binLocation = binLocations.get(0);
        Placement placement = new Placement();
        placement.setMaterialLineOID(materialLine.getMaterialLineOID());
        placement.setBinLocation(binLocation);
        warehouseServices.addPlacement(placement);

        suggestedBinLocation = requestHeaderRepository.suggestBinLocation(materialProcure.getpPartNumber(), materialProcure.getpPartVersion(), "1622");
        Assert.assertNotNull(suggestedBinLocation);
        Assert.assertEquals(binLocation.getCode(), suggestedBinLocation.getCode());

    }*/

    @Test
    public void testAddOrderLog() throws GloriaApplicationException, IOException {
        // Arrange
        String userId = "all";
        List<OrderLine> orderLines = deliveryService.getAllOrderLines();
        OrderLine orderLine = orderLines.get(0);

        OrderLogDTO orderLogDTO = new OrderLogDTO();
        orderLogDTO.setEventValue("Test order log");
        // Act
        OrderLog orderLog = deliveryService.addOrderLog(orderLine.getOrderLineOID(), orderLogDTO, userId);
        // Assert
        Assert.assertNotNull(orderLog);
        Assert.assertNotNull(orderLog.getEventOriginatorId());
    }

    @Test
    public void testAddOrderLogs() throws GloriaApplicationException, IOException {
        // Arrange
        String userId = "all";
        List<OrderLine> orderLines = deliveryService.getAllOrderLines();
        OrderLine orderLine = orderLines.get(0);

        List<OrderLogDTO> orderLogDTOs = new ArrayList<OrderLogDTO>();
        OrderLogDTO orderLogDTO = new OrderLogDTO();
        orderLogDTO.setEventValue("Test order log1");
        orderLogDTOs.add(orderLogDTO);

        OrderLogDTO orderLogDTO2 = new OrderLogDTO();
        orderLogDTO2.setEventValue("Test order log2");
        orderLogDTOs.add(orderLogDTO2);

        // Act
        List<OrderLog> orderLogs = deliveryService.addOrderLogs(orderLine.getOrderLineOID(), orderLogDTOs, userId);
        // Assert
        Assert.assertNotNull(orderLogs);
        Assert.assertEquals(orderLogDTOs.size(), orderLogs.size());
        Assert.assertNotNull(orderLogs.get(0).getEventOriginatorId());
    }

    @Test
    public void testGetOrderLogsByOrderLineId() throws GloriaApplicationException, IOException {
        // Arrange
        String userId = "all";
        List<OrderLine> orderLines = deliveryService.getAllOrderLines();
        OrderLine orderLine = orderLines.get(0);

        OrderLogDTO orderLogDTO = new OrderLogDTO();
        orderLogDTO.setEventValue("Test order log1");
        deliveryService.addOrderLog(orderLine.getOrderLineOID(), orderLogDTO, userId);

        OrderLogDTO orderLogDTO2 = new OrderLogDTO();
        orderLogDTO2.setEventValue("Test order log2");
        deliveryService.addOrderLog(orderLine.getOrderLineOID(), orderLogDTO2, userId);
        // Act
        List<OrderLog> orderLogs = deliveryService.getOrderLogsByOrderLineId(orderLine.getOrderLineOID());
        // Assert
        Assert.assertNotNull(orderLogs);
        Assert.assertNotNull(orderLogDTO.getEventValue(), orderLogs.get(0).getEventValue());
    }

    @Test
    public void testAddOrderLineLog() throws GloriaApplicationException, IOException {
        // Arrange
        String userId = "all";
        List<OrderLine> orderLines = deliveryService.getAllOrderLines();
        OrderLine orderLine = orderLines.get(0);

        OrderLineLogDTO orderLineLogDTO = new OrderLineLogDTO();
        orderLineLogDTO.setEventValue("Test order log");
        // Act
        OrderLineLog orderLineLog = deliveryService.addOrderLineLog(orderLine.getOrderLineOID(), orderLineLogDTO, userId);
        // Assert
        Assert.assertNotNull(orderLineLog);
        Assert.assertNotNull(orderLineLog.getEventOriginatorId());
    }

    @Test
    public void testAddOrderLineLogs() throws GloriaApplicationException, IOException {
        // Arrange
        String userId = "all";
        List<OrderLine> orderLines = deliveryService.getAllOrderLines();
        OrderLine orderLine = orderLines.get(0);

        List<OrderLineLogDTO> orderLineLogDTOs = new ArrayList<OrderLineLogDTO>();
        OrderLineLogDTO orderLineLogDTO = new OrderLineLogDTO();
        orderLineLogDTO.setEventValue("Test order log1");
        orderLineLogDTOs.add(orderLineLogDTO);

        OrderLineLogDTO orderLineLogDTO2 = new OrderLineLogDTO();
        orderLineLogDTO2.setEventValue("Test order log2");
        orderLineLogDTOs.add(orderLineLogDTO2);

        // Act
        List<OrderLineLog> orderLineLogs = deliveryService.addOrderLineLogs(orderLine.getOrderLineOID(), orderLineLogDTOs, userId);
        // Assert
        Assert.assertNotNull(orderLineLogs);
        Assert.assertEquals(orderLineLogDTOs.size(), orderLineLogs.size());
        Assert.assertNotNull(orderLineLogs.get(0).getEventOriginatorId());
    }

    @Test
    public void testGetOrderLineLogs() throws GloriaApplicationException, IOException {
        // Arrange
        String userId = "all";
        List<OrderLine> orderLines = deliveryService.getAllOrderLines();
        OrderLine orderLine = orderLines.get(0);

        OrderLineLogDTO orderLineLogDTO = new OrderLineLogDTO();
        orderLineLogDTO.setEventValue("Test order log1");
        deliveryService.addOrderLineLog(orderLine.getOrderLineOID(), orderLineLogDTO, userId);

        OrderLineLogDTO orderLineLogDTO2 = new OrderLineLogDTO();
        orderLineLogDTO2.setEventValue("Test order log2");
        deliveryService.addOrderLineLog(orderLine.getOrderLineOID(), orderLineLogDTO2, userId);
        // Act
        List<OrderLineLog> orderLineLogs = deliveryService.getOrderLineLogs(orderLine.getOrderLineOID());
        // Assert
        Assert.assertNotNull(orderLineLogs);
        Assert.assertNotNull(orderLineLogDTO.getEventValue(), orderLineLogs.get(0).getEventValue());
    }

    @Test
    public void testGetDeliveryNoteLinesByOrderLineId() throws GloriaApplicationException {
        // Arrange
        List<DeliveryNote> deliveryNotes = deliveryService.getAllDeliveryNotes();
        DeliveryNote deliveryNote = deliveryNotes.get(0);

        List<DeliveryNoteLine> noteLines = deliveryService.getDeliveryNoteLines(deliveryNote.getDeliveryNoteOID(), null , DeliveryNoteLineStatus.IN_WORK, ReceiveType.REGULAR);
        DeliveryNoteLine deliveryNoteLine = noteLines.get(0);
        // Act
        List<DeliveryNoteLine> deliveryNoteLines = deliveryService.getDeliveryNoteLinesByOrderLineId(deliveryNoteLine.getOrderLine().getOrderLineOID(),
                                                                                                     DeliveryNoteLineStatus.IN_WORK.name());
        // Assert
        Assert.assertNotNull(deliveryNoteLines);
        Assert.assertTrue(deliveryNoteLines.size() > 0);
    }

    @Test
    public void testUpdateDeliverySchedule() throws GloriaApplicationException {
        // Arrange
        List<OrderLine> orderLines = deliveryService.getAllOrderLines();
        OrderLine orderLine = orderLines.get(0);
        List<DeliverySchedule> deliverySchedules = orderLine.getDeliverySchedule();
        List<DeliveryScheduleDTO> deliveryScheduleDTOs = DeliveryHelper.transformAsDeliveryScheduleDTOs(deliverySchedules);
        DeliveryScheduleDTO deliveryScheduleDTO = deliveryScheduleDTOs.get(0);
        deliveryScheduleDTO.setExpectedQuantity(4L);
        // Act
        DeliverySchedule deliverySchedule = deliveryService.updateDeliverySchedule(orderLine.getOrderLineOID(), deliveryScheduleDTO, "all");
        // Assert
        Assert.assertNotNull(deliverySchedule);
        Assert.assertEquals(new Long(4L), deliverySchedule.getExpectedQuantity());
    }

    @Test
    public void testUpdateDeliverySchedules() throws GloriaApplicationException {
        // Arrange
        List<OrderLine> orderLines = deliveryService.getAllOrderLines();
        OrderLine orderLine = orderLines.get(0);
        List<DeliverySchedule> deliverySchedules = orderLine.getDeliverySchedule();
        List<DeliveryScheduleDTO> deliveryScheduleDTOs = DeliveryHelper.transformAsDeliveryScheduleDTOs(deliverySchedules);
        DeliveryScheduleDTO deliveryScheduleDTO = deliveryScheduleDTOs.get(0);
        deliveryScheduleDTO.setExpectedQuantity(4L);

        DeliveryScheduleDTO newDeliveryScheduleDTO = new DeliveryScheduleDTO();
        deliveryScheduleDTO.setExpectedQuantity(10L);
        deliveryScheduleDTO.setExpectedDate(DateUtil.getSqlDate());
        deliveryScheduleDTOs.add(newDeliveryScheduleDTO);
        // Act
        List<DeliverySchedule> updatedDeliverySchedules = deliveryService.updateDeliverySchedules(orderLine.getOrderLineOID(), deliveryScheduleDTOs, "ALL");
        // Assert
        Assert.assertNotNull(updatedDeliverySchedules);
        Assert.assertEquals(2, updatedDeliverySchedules.size());
    }

    @Test
    public void testGetTransportLabels() throws GloriaApplicationException, IOException {
        // Arrange
        TransportLabel transportLabel = new TransportLabel();
        transportLabel.setCode("1622-01");
        transportLabel.setCreateTime(DateUtil.getSqlDate());
        transportLabel.setWhSiteId("1722");
        deliveryService.addTransportlabel(transportLabel);

        TransportLabel transportLabel2 = new TransportLabel();
        transportLabel2.setCode("1622-01");
        transportLabel2.setCreateTime(DateUtil.getSqlDate());
        transportLabel2.setWhSiteId("1722");
        deliveryService.addTransportlabel(transportLabel2);

        // Act
        List<TransportLabel> transportLabels = deliveryService.getTransportLabels("all", "1722", null, 0);
        // Assert
        Assert.assertNotNull(transportLabels);
        Assert.assertEquals(2, transportLabels.size());
    }

    /**
     * the printer tires to connect to 'test.localhost.print:9100' and end up with system exception
     * 
     * @throws GloriaApplicationException
     * @throws IOException
     */
    @Ignore
    @Test(expected = GloriaSystemException.class)
    public void testGetTransportLabel() throws GloriaApplicationException, IOException {
        // Arrange
        String userId = "all";
        
        warehouseServices.createWarehouseData(IOUtil.getStringFromClasspath(INITDATA_WAREHOUSE_XML),null);

        TransportLabel transportLabel = new TransportLabel();
        transportLabel.setCode("1622-01");
        transportLabel.setCreateTime(DateUtil.getSqlDate());
        transportLabel.setWhSiteId("1722");
        deliveryService.addTransportlabel(transportLabel);

        Printer printer = new Printer();
        printer.setHostAddress("test.localhost.print:9100");
        printer.setName("testPrinter");
        printer.setWarehouse(warehouseServices.findWarehouseBySiteId("1722"));
        warehouseRepository.save(printer);
        
        List<TransportLabel> transportLabels = deliveryService.getTransportLabels("all", "1722", null, 0);

        TransportLabel savedTransportLabel = transportLabels.get(0);
        // Act
        TransportLabel updatedTransportLabel = deliveryService.getTransportLabel(userId, "1722", "printLabel", savedTransportLabel.getTransportLabelOid());
        // Assert
        Assert.assertNotNull(updatedTransportLabel);
    }
    
    
    /**
     * create transport label
     * 
     * @throws GloriaApplicationException
     * @throws IOException
     */
    @Test
    public void testGetTransportLabelActionCreate() throws GloriaApplicationException, IOException {
        // Arrange
        String userId = "all";

        // Act
        TransportLabel updatedTransportLabel = deliveryService.getTransportLabel(userId, "1722", "create", 0);
        // Assert
        Assert.assertNotNull(updatedTransportLabel);
    }

    @Test
    public void testGetOrderLines() throws IOException {
        // Arrange
        List<OrderLine> orderLines = deliveryService.getAllOrderLines();
        OrderLine orderLineToFind = orderLines.get(0);
        // Act
        OrderLine fetchedOrderLine = deliveryService.getOrderLine(orderLineToFind.getOrderLineOID());

        // Assert
        Assert.assertNotNull(fetchedOrderLine);
        Assert.assertEquals(orderLineToFind.getOrderLineOID(), fetchedOrderLine.getOrderLineOID());
    }

    @Test
    public void testUploadQualityDocuments() throws IOException, GloriaApplicationException {
        // Arrange
        String testContent = "MOCK TEST FILE UPLOAD INVOCATION FOR QUALITY DOCS";
        DeliveryNoteDTO deliveryNoteDTO = new DeliveryNoteDTO();
        deliveryNoteDTO.setCarrier("DHL");
        deliveryNoteDTO.setDeliveryNoteNo("DN1234");
        deliveryNoteDTO.setSupplierId("SUP1234");
        deliveryNoteDTO.setTransportationNo("TN1234");
        deliveryNoteDTO.setOrderNo("ON1234");
        deliveryNoteDTO.setReceiveType(ReceiveType.REGULAR.name());

        DeliveryNote deliveryNote = deliveryService.createOrUpdateDeliveryNote(deliveryNoteDTO);

        DeliveryNoteLine deliveryNoteLine = deliveryNote.getDeliveryNoteLine().get(0);

        DocumentDTO documentToUpload = doUploadDocument(testContent);

        // Act
        QiDoc docucmentUploaded = deliveryService.uploadQiDocs(deliveryNoteLine.getDeliveryNoteLineOID(), documentToUpload);

        // Assert
        Assert.assertNotNull(docucmentUploaded);

        if (docucmentUploaded.getDocumentName().equals("testFile.txt")) {
            QiDoc documentToDownload = deliveryService.getQiDoc(docucmentUploaded.getQiDocOID());
            Assert.assertTrue(ObjectUtils.nullSafeEquals(testContent.getBytes(), documentToDownload.getFileContent()));
        }
    }

    @Test
    public void testUploadProblemDocuments() throws IOException, GloriaApplicationException {
        // Arrange
        String testContent = "MOCK TEST FILE UPLOAD INVOCATION FOR PROBLEM DOCS";
        DeliveryNoteDTO deliveryNoteDTO = new DeliveryNoteDTO();
        deliveryNoteDTO.setCarrier("DHL");
        deliveryNoteDTO.setDeliveryNoteNo("DN1234");
        deliveryNoteDTO.setSupplierId("SUP1234");
        deliveryNoteDTO.setTransportationNo("TN1234");
        deliveryNoteDTO.setOrderNo("ON1234");
        deliveryNoteDTO.setReceiveType(ReceiveType.REGULAR.name());

        DeliveryNote deliveryNote = deliveryService.createOrUpdateDeliveryNote(deliveryNoteDTO);

        DeliveryNoteLine deliveryNoteLine = deliveryNote.getDeliveryNoteLine().get(0);

        DocumentDTO documentToUpload = doUploadDocument(testContent);

        // Act
        ProblemDoc docucmentUploaded = deliveryService.uploadProblemDocuments(deliveryNoteLine.getDeliveryNoteLineOID(), documentToUpload);

        // Assert
        Assert.assertNotNull(docucmentUploaded);

        if (docucmentUploaded.getDocumentName().equals("testFile.txt")) {
            ProblemDoc documentToDownload = deliveryService.getProblemDocument(docucmentUploaded.getProblemDocOID());
            Assert.assertTrue(ObjectUtils.nullSafeEquals(testContent.getBytes(), documentToDownload.getFileContent()));
        }
    }

    @Test
    public void testUploadAttachedDocs() throws IOException, GloriaApplicationException {
        // Arrange
        String testContent = "MOCK TEST FILE UPLOAD INVOCATION FOR ATTACHED DOCS";

        OrderLine orderLine = deliveryService.getAllOrderLines().get(0);

        List<DeliverySchedule> deliverySchedules = orderLine.getDeliverySchedule();

        DeliverySchedule deliverySchedule = deliverySchedules.get(0);

        DocumentDTO documentToUpload = doUploadDocument(testContent);

        // Act
        AttachedDoc docucmentUploaded = deliveryService.uploadAttachedDocuments(deliverySchedule.getDeliveryScheduleOID(), documentToUpload);

        // Assert
        Assert.assertNotNull(docucmentUploaded);

        if (docucmentUploaded.getDocumentName().equals("testFile.txt")) {
            AttachedDoc documentToDownload = deliveryService.getAttachedDocument(docucmentUploaded.getAttachedDocOID());
            Assert.assertTrue(ObjectUtils.nullSafeEquals(testContent.getBytes(), documentToDownload.getFileContent()));
        }
    }

    @Test
    public void testDeleteQualityDocument() throws IOException, GloriaApplicationException {
        // Arrange
        String testContent = "MOCK TEST FILE UPLOAD INVOCATION FOR QUALITY DOCS";
        DeliveryNoteDTO deliveryNoteDTO = new DeliveryNoteDTO();
        deliveryNoteDTO.setCarrier("DHL");
        deliveryNoteDTO.setDeliveryNoteNo("DN1234");
        deliveryNoteDTO.setSupplierId("SUP1234");
        deliveryNoteDTO.setTransportationNo("TN1234");
        deliveryNoteDTO.setOrderNo("ON1234");
        deliveryNoteDTO.setReceiveType(ReceiveType.REGULAR.name());

        DeliveryNote deliveryNote = deliveryService.createOrUpdateDeliveryNote(deliveryNoteDTO);

        DeliveryNoteLine deliveryNoteLine = deliveryNote.getDeliveryNoteLine().get(0);

        DocumentDTO documentToUpload = doUploadDocument(testContent);

        QiDoc docucmentUploaded = deliveryService.uploadQiDocs(deliveryNoteLine.getDeliveryNoteLineOID(), documentToUpload);

        // Act
        deliveryService.deleteQiDoc(docucmentUploaded.getQiDocOID());

        // Assert
        QiDoc qualityDoc = deliveryService.getQiDoc(docucmentUploaded.getQiDocOID());
        Assert.assertNull(qualityDoc);
    }

    @Test
    public void testDeleteProblemDocument() throws IOException, GloriaApplicationException {
        // Arrange
        String testContent = "MOCK TEST FILE UPLOAD INVOCATION FOR PROBLEM DOCS";
        DeliveryNoteDTO deliveryNoteDTO = new DeliveryNoteDTO();
        deliveryNoteDTO.setCarrier("DHL");
        deliveryNoteDTO.setDeliveryNoteNo("DN1234");
        deliveryNoteDTO.setSupplierId("SUP1234");
        deliveryNoteDTO.setTransportationNo("TN1234");
        deliveryNoteDTO.setOrderNo("ON1234");
        deliveryNoteDTO.setReceiveType(ReceiveType.REGULAR.name());

        DeliveryNote deliveryNote = deliveryService.createOrUpdateDeliveryNote(deliveryNoteDTO);

        DeliveryNoteLine deliveryNoteLine = deliveryNote.getDeliveryNoteLine().get(0);

        DocumentDTO documentToUpload = doUploadDocument(testContent);

        ProblemDoc docucmentUploaded = deliveryService.uploadProblemDocuments(deliveryNoteLine.getDeliveryNoteLineOID(), documentToUpload);

        // Act
        deliveryService.deleteProblemDoc(docucmentUploaded.getProblemDocOID());

        // Assert
        ProblemDoc document =deliveryService.getProblemDocument(docucmentUploaded.getProblemDocOID());
        Assert.assertNull(document);
    }

    @Test
    public void testDeleteAttachedDoc() throws IOException, GloriaApplicationException {
        // Arrange
        String testContent = "MOCK TEST FILE UPLOAD INVOCATION FOR ATTACHED DOCS";
        OrderLine orderLine = deliveryService.getAllOrderLines().get(0);

        List<DeliverySchedule> deliverySchedules = orderLine.getDeliverySchedule();

        DeliverySchedule deliverySchedule = deliverySchedules.get(0);

        DocumentDTO documentToUpload = doUploadDocument(testContent);

        AttachedDoc attachedDoc = deliveryService.uploadAttachedDocuments(deliverySchedule.getDeliveryScheduleOID(), documentToUpload);

        // Act
        deliveryService.deleteAttachedDoc(attachedDoc.getAttachedDocOID());

        // Assert
        AttachedDoc doc =deliveryService.getAttachedDocument(attachedDoc.getAttachedDocOID());
        Assert.assertNull(doc);
    }

    @Test(expected = GloriaApplicationException.class)
    public void testQualityDocsUploadLimit() throws GloriaApplicationException, IOException {
        // Arrange
        String testContent = "MOCK TEST FILE UPLOAD INVOCATION FOR QUALITY DOCS";
        DeliveryNoteDTO deliveryNoteDTO = new DeliveryNoteDTO();
        deliveryNoteDTO.setCarrier("DHL");
        deliveryNoteDTO.setDeliveryNoteNo("DN1234");
        deliveryNoteDTO.setSupplierId("SUP1234");
        deliveryNoteDTO.setTransportationNo("TN1234");
        deliveryNoteDTO.setOrderNo("ON1234");
        deliveryNoteDTO.setReceiveType(ReceiveType.REGULAR.name());

        DeliveryNote deliveryNote = deliveryService.createOrUpdateDeliveryNote(deliveryNoteDTO);

        DeliveryNoteLine deliveryNoteLine = deliveryNote.getDeliveryNoteLine().get(0);

        List<DocumentDTO> documentsToUpload = new ArrayList<DocumentDTO>();
        documentsToUpload.add(doUploadDocument(testContent));
        documentsToUpload.add(doUploadDocument(testContent));
        documentsToUpload.add(doUploadDocument(testContent));
        documentsToUpload.add(doUploadDocument(testContent));
        documentsToUpload.add(doUploadDocument(testContent));
        documentsToUpload.add(doUploadDocument(testContent));

        // Act
        for (DocumentDTO documentToUpload : documentsToUpload) {
            deliveryService.uploadQiDocs(deliveryNoteLine.getDeliveryNoteLineOID(), documentToUpload);
        }
        // Assert
    }

    @Test(expected = GloriaApplicationException.class)
    public void testProblemNoteDocsUploadLimit() throws GloriaApplicationException, IOException {
        // Arrange
        String testContent = "MOCK TEST FILE UPLOAD INVOCATION FOR PROBLEM DOCS";
        DeliveryNoteDTO deliveryNoteDTO = new DeliveryNoteDTO();
        deliveryNoteDTO.setCarrier("DHL");
        deliveryNoteDTO.setDeliveryNoteNo("DN1234");
        deliveryNoteDTO.setSupplierId("SUP1234");
        deliveryNoteDTO.setTransportationNo("TN1234");
        deliveryNoteDTO.setOrderNo("ON1234");
        deliveryNoteDTO.setReceiveType(ReceiveType.REGULAR.name());

        DeliveryNote deliveryNote = deliveryService.createOrUpdateDeliveryNote(deliveryNoteDTO);

        DeliveryNoteLine deliveryNoteLine = deliveryNote.getDeliveryNoteLine().get(0);

        List<DocumentDTO> documentsToUpload = new ArrayList<DocumentDTO>();
        documentsToUpload.add(doUploadDocument(testContent));
        documentsToUpload.add(doUploadDocument(testContent));
        documentsToUpload.add(doUploadDocument(testContent));
        documentsToUpload.add(doUploadDocument(testContent));
        documentsToUpload.add(doUploadDocument(testContent));
        documentsToUpload.add(doUploadDocument(testContent));

        // Act
        for (DocumentDTO documentToUpload : documentsToUpload) {
            deliveryService.uploadProblemDocuments(deliveryNoteLine.getDeliveryNoteLineOID(), documentToUpload);
        }
        // Assert
    }

    @Test(expected = GloriaApplicationException.class)
    public void testAttachedDocsUploadLimit() throws GloriaApplicationException, IOException {
        // Arrange
        String testContent = "MOCK TEST FILE UPLOAD INVOCATION FOR ATTACHED DOCS";

        OrderLine orderLine = deliveryService.getAllOrderLines().get(0);

        List<DeliverySchedule> deliverySchedules = orderLine.getDeliverySchedule();

        DeliverySchedule deliverySchedule = deliverySchedules.get(0);

        List<DocumentDTO> documentsToUpload = new ArrayList<DocumentDTO>();
        documentsToUpload.add(doUploadDocument(testContent));
        documentsToUpload.add(doUploadDocument(testContent));
        documentsToUpload.add(doUploadDocument(testContent));
        documentsToUpload.add(doUploadDocument(testContent));
        documentsToUpload.add(doUploadDocument(testContent));
        documentsToUpload.add(doUploadDocument(testContent));

        // Act
        for (DocumentDTO documentToUpload : documentsToUpload) {
            deliveryService.uploadAttachedDocuments(deliverySchedule.getDeliveryScheduleOID(), documentToUpload);
        }
        // Assert
    }

    private DocumentDTO doUploadDocument(String testContent) throws IOException {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        request.addFile(new MockMultipartFile("testFileKey", "testFile.txt", "text/plain", testContent.getBytes()));

        Set<String> fileNames = new HashSet<String>();
        Iterator<String> fileIter = request.getFileNames();
        while (fileIter.hasNext()) {
            fileNames.add(fileIter.next());
        }
        Assert.assertEquals(1, fileNames.size());
        Assert.assertTrue(fileNames.contains("testFileKey"));

        MultipartFile mockFile = request.getFile("testFileKey");

        Assert.assertEquals("testFile.txt", mockFile.getOriginalFilename());
        Assert.assertTrue(ObjectUtils.nullSafeEquals(testContent.getBytes(), mockFile.getBytes()));

        DocumentDTO documentToUpload = new DocumentDTO();
        documentToUpload.setName(mockFile.getName());
        documentToUpload.setContent(FileCopyUtils.copyToByteArray(mockFile.getInputStream()));
        return documentToUpload;
    }
    
    @Test
    public void testAddDeliveryLog() throws IOException, GloriaApplicationException {
        // Arrange
        String userId = "all";
        List<OrderLine> orderLines = deliveryService.getAllOrderLines();
        OrderLine orderLine = orderLines.get(0);
        List<DeliverySchedule> deliverySchedules = orderLine.getDeliverySchedule();

        DeliveryLogDTO deliveryLogDTO = new DeliveryLogDTO();
        deliveryLogDTO.setEventValue("test delivery log");
        // Act
        DeliveryLog createdDeliveryLog = deliveryService.addDeliveryLog(deliverySchedules.get(0).getDeliveryScheduleOID(), EventType.DELIVERY_LOG.name(),
                                                                        deliveryLogDTO, userId);
        // Assert
        Assert.assertNotNull(createdDeliveryLog);
        Assert.assertEquals(deliveryLogDTO.getEventValue(), createdDeliveryLog.getEventValue());
    }
    
    @Test
    public void testUpdateOrderLine() throws GloriaApplicationException {
        // Arrange
        List<OrderLine> orderLines = deliveryService.getAllOrderLines();
        OrderLineDTO orderLineDTO = procurementDtoTransformer.transformAsDTO(orderLines.get(0), false);
        orderLineDTO.setFreeText("updated text");
        orderLineDTO.setStaAcceptedDate(DateUtil.getSqlDate());
        orderLineDTO.setStaAgreedDate(DateUtil.getSqlDate());
        orderLineDTO.setAllowedQuantity(orderLineDTO.getQuantity()-orderLineDTO.getReceivedQuantity());
        String userId = "all";
        // Act
        OrderLine orderLine = deliveryService.updateOrderLine(orderLineDTO, userId);
        // Assert
        Assert.assertNotNull(orderLine);
        OrderLineDTO updatedOrderLineDTO = procurementDtoTransformer.transformAsDTO(orderLine, false);
        Assert.assertEquals(orderLineDTO.getFreeText(), updatedOrderLineDTO.getFreeText());
        Assert.assertEquals(orderLineDTO.getStaAcceptedDate(), updatedOrderLineDTO.getStaAcceptedDate());
        Assert.assertEquals(orderLineDTO.getStaAgreedDate(), updatedOrderLineDTO.getStaAgreedDate());
    }
    

    @Test
    @Ignore
    public void testGoodsReceiptSenderBeanXMLMessage() throws JAXBException, ParseException{
        
        // Arrange
        
        GoodsReceiptHeaderTransformerDTO goodsReceiptHeaderDTO = new GoodsReceiptHeaderTransformerDTO();
        goodsReceiptHeaderDTO.setAssignCodeGM("GM");
        goodsReceiptHeaderDTO.setCompanyCode("Com");
        goodsReceiptHeaderDTO.setDocumentDate(DateUtil.getCurrentUTCDate());
        goodsReceiptHeaderDTO.setHeaderText("text header");
        goodsReceiptHeaderDTO.setPostingDateTime(DateUtil.getCurrentUTCDate());
        goodsReceiptHeaderDTO.setReferenceDocument("enceDocument");
        
        GoodsReceiptLineTransformerDTO goodsReceiptLineDTO = new GoodsReceiptLineTransformerDTO();
        goodsReceiptLineDTO.setIsoUnitOfMeasure("PCE");
        goodsReceiptLineDTO.setMovementType("mov");
        goodsReceiptLineDTO.setOrderReference("9090-900-009");
        goodsReceiptLineDTO.setPlant("BLR");
        goodsReceiptLineDTO.setQuantity(10);
        goodsReceiptLineDTO.setVendor("SPID");
        goodsReceiptLineDTO.setVendorMaterialNumber("ML009");
        goodsReceiptHeaderDTO.getGoodsReceiptLineTransformerDTOs().add(goodsReceiptLineDTO);
        
        // Act
        String xmlMessage = goodsReceiptSenderBean.sendGoodsReceipt(goodsReceiptHeaderDTO);
        
        Assert.assertNotNull(xmlMessage);
        goodsReceiptTransformer.transformXmlToJaxb(xmlMessage);
    }
    
    @Test
    public void findOrderSapByUniqueExtOrderTest() throws JAXBException, ParseException {

        // ARRANGE
        OrderSap  orderSap = new OrderSap();

        boolean yesOrNo = true;

        orderSap.setCompanyCode("A022");
        orderSap.setOrderType("A");
        orderSap.setVendor("vendor");
        orderSap.setPurchaseOrganization("p");
        orderSap.setPurchaseGroup("G");
        orderSap.setDocumentDate(DateUtil.getCurrentUTCDate());
        orderSap.setCurrency("currency");
        orderSap.setPurchaseType("T");
        orderSap.setUniqueExtOrder("123");

        ArrayList<OrderSapLine> orderSapLineList = new ArrayList<OrderSapLine>();
        OrderSapLine orderSapLine = new OrderSapLine();
        orderSapLine.setRequisitionId(10);
        orderSapLine.setPurchaseOrderitem(Long.valueOf(2));
        orderSapLine.setAction("action");
        orderSapLine.setOrderReference("orderReference");
        orderSapLine.setPartNumber("partNumber");
        orderSapLine.setShortText("ST");
        orderSapLine.setCancelDate(DateUtil.getCurrentUTCDate());
        orderSapLine.setPlant("p");
        orderSapLine.setCurrentBuyer("CB");
        orderSapLine.setMaterialGroup("MG");
        orderSapLine.setQuantity(2L);
        orderSapLine.setIsoOrderPriceUnit("1");
        orderSapLine.setIsoPurchaseOrderUnit("IS");
        orderSapLine.setNetPrice(10.00);
        orderSapLine.setPriceUnit("1");
        orderSapLine.setTaxCode("TC");
        orderSapLine.setAccountAssignmentCategory("A");
        orderSapLine.setUnlimitedDeliveryIndicator(String.valueOf(yesOrNo));
        orderSapLine.setGrIndicator(String.valueOf(yesOrNo));
        orderSapLine.setNonValuedGrIndicator(String.valueOf(yesOrNo));
        orderSapLine.setIrIndicator(String.valueOf(yesOrNo));
        orderSapLine.setAcknowledgementNumber("B1");
        orderSapLine.setPurchaseRequisitionNumber("2L");
        orderSapLineList.add(orderSapLine);

        ArrayList<OrderSapSchedule> orderSapScheduleList = new ArrayList<OrderSapSchedule>();
        OrderSapSchedule orderSapSchedule = new OrderSapSchedule();
        orderSapSchedule.setCategoryOfDeliveryDate("T");
        orderSapSchedule.setDeliveryDate(DateUtil.getCurrentUTCDate());
        orderSapScheduleList.add(orderSapSchedule);

        ArrayList<OrderSapAccounts> orderSapAccountsList = new ArrayList<OrderSapAccounts>();
        OrderSapAccounts accounts = new OrderSapAccounts();
        accounts.setSequence(4L);
        accounts.setGeneralLedgerAccount("GA");
        accounts.setCostCenter("costCenter");
        accounts.setWbsElement("WB");

        orderSapAccountsList.add(accounts);

        orderSapLine.setOrderSapAccounts(orderSapAccountsList);
        orderSapLine.setOrderSapSchedules(orderSapScheduleList);
        orderSap.setOrderSapLines(orderSapLineList);
        orderSapRepository.save(orderSap);
        
        // ACT
       OrderSap  savedOrderSap = orderSapRepository.findOrderSapByUniqueExtOrder(orderSap.getUniqueExtOrder());
       
       // ASSERT
       Assert.assertNotNull(savedOrderSap);
       Assert.assertEquals(savedOrderSap.getCompanyCode(), "A022");
       
    }
    
    @Test
    public void testUploadInspectionDocuments() throws IOException, GloriaApplicationException {
        // Arrange
        String testContent = "MOCK TEST FILE UPLOAD INVOCATION FOR INSPECTION DOCS";
        DeliveryNoteDTO deliveryNoteDTO = new DeliveryNoteDTO();
        deliveryNoteDTO.setCarrier("DHL");
        deliveryNoteDTO.setDeliveryNoteNo("DN1234");
        deliveryNoteDTO.setSupplierId("SUP1234");
        deliveryNoteDTO.setTransportationNo("TN1234");
        deliveryNoteDTO.setOrderNo("ON1234");
        deliveryNoteDTO.setReceiveType(ReceiveType.REGULAR.name());

        DeliveryNote deliveryNote = deliveryService.createOrUpdateDeliveryNote(deliveryNoteDTO);

        DeliveryNoteLine deliveryNoteLine = deliveryNote.getDeliveryNoteLine().get(0);

        DocumentDTO documentToUpload = doUploadDocument(testContent);

        // Act
        ReceiveDoc docucmentUploaded = deliveryService.uploadReceiveDocuments(deliveryNoteLine.getDeliveryNoteLineOID(), documentToUpload);

        // Assert
        Assert.assertNotNull(docucmentUploaded);

        if (docucmentUploaded.getDocumentName().equals("testFile.txt")) {
            ReceiveDoc documentToDownload = deliveryService.getReceiveDocument(docucmentUploaded.getRecieveDocOID());
            Assert.assertTrue(ObjectUtils.nullSafeEquals(testContent.getBytes(), documentToDownload.getFileContent()));
        }
    }
   
    @Test
    public void testDeleteInspectionDocument() throws IOException, GloriaApplicationException {
        // Arrange
        String testContent = "MOCK TEST FILE UPLOAD INVOCATION FOR INSPECTION DOCS";
        DeliveryNoteDTO deliveryNoteDTO = new DeliveryNoteDTO();
        deliveryNoteDTO.setCarrier("DHL");
        deliveryNoteDTO.setDeliveryNoteNo("DN1234");
        deliveryNoteDTO.setSupplierId("SUP1234");
        deliveryNoteDTO.setTransportationNo("TN1234");
        deliveryNoteDTO.setOrderNo("ON1234");
        deliveryNoteDTO.setReceiveType(ReceiveType.REGULAR.name());

        DeliveryNote deliveryNote = deliveryService.createOrUpdateDeliveryNote(deliveryNoteDTO);

        DeliveryNoteLine deliveryNoteLine = deliveryNote.getDeliveryNoteLine().get(0);

        DocumentDTO documentToUpload = doUploadDocument(testContent);

        ReceiveDoc docucmentUploaded = deliveryService.uploadReceiveDocuments(deliveryNoteLine.getDeliveryNoteLineOID(), documentToUpload);

        // Act
        deliveryService.deleteReceiveDocument(docucmentUploaded.getRecieveDocOID());

        // Assert
        ReceiveDoc receiveDoc = deliveryService.getReceiveDocument(docucmentUploaded.getRecieveDocOID());
        Assert.assertNull(receiveDoc);
    }

    @Test
    public void testFindAllOrderLineVersions() throws IOException, GloriaApplicationException {
        List<OrderLine> orderLines = deliveryService.getAllOrderLines();
        List<OrderLineVersion> listofOrderLineVerion = deliveryService.findAllOrderLineVersions(orderLines.get(0).getOrderLineOID());
        Assert.assertNotNull(listofOrderLineVerion);
        Assert.assertEquals(1, listofOrderLineVerion.size());
    }
    
    @Test
    public void testSendProcessPurchaseOrderCreate() throws JAXBException, ParseException {

        // ARRANGE
        ProcessPurchaseOrderDTO processPurchaseOrderDTO = new ProcessPurchaseOrderDTO();

        boolean yesOrNo = true;

        processPurchaseOrderDTO.setAction("Create");
        processPurchaseOrderDTO.setCompanyCode("A022");
        processPurchaseOrderDTO.setOrderType("A");
        processPurchaseOrderDTO.setVendor("vendor");
        processPurchaseOrderDTO.setPurchaseOrganisation("p");
        processPurchaseOrderDTO.setPurhchaseGroup("G");
        processPurchaseOrderDTO.setDocumentDate(DateUtil.getCurrentUTCDate());
        processPurchaseOrderDTO.setCurrency("currency");
        processPurchaseOrderDTO.setPurchaseType("T");
        processPurchaseOrderDTO.setUniqueExtOrder("uniqueExtOrder");

        ArrayList<OrderSapLineDTO> orderSapLineDTOList = new ArrayList<OrderSapLineDTO>();
        OrderSapLineDTO orderSapLineDTO = new OrderSapLineDTO();
        orderSapLineDTO.setRequisitionId(10);
        orderSapLineDTO.setPurchaseOrderItem("O");
        orderSapLineDTO.setAction("Create");
        orderSapLineDTO.setOrderReference("orderReference");
        orderSapLineDTO.setPartNumber("partNumber");
        orderSapLineDTO.setShortText("ST");        
        orderSapLineDTO.setPlant("p");
        orderSapLineDTO.setCurrentBuyer("CB");
        orderSapLineDTO.setMaterialGroup("MG");
        orderSapLineDTO.setQuantity(BigDecimal.valueOf(2));
        orderSapLineDTO.setIsoOrderPriceUnit("100");
        orderSapLineDTO.setIsoPurchaseOrderUnit("IS");
        orderSapLineDTO.setNetPrice(BigDecimal.valueOf(10L));
        orderSapLineDTO.setPriceUnit(BigDecimal.valueOf(100L));
        orderSapLineDTO.setTaxCode("TC");
        orderSapLineDTO.setAccountAssignmentCategory("A");
        orderSapLineDTO.setUnlimitedDeliveryIndicator(yesOrNo);
        orderSapLineDTO.setGrIndicator(yesOrNo);
        orderSapLineDTO.setNonValuedGrIndicator(yesOrNo);
        orderSapLineDTO.setIrIndicator(yesOrNo);
        orderSapLineDTO.setAcknowledgementNumber("B1");
        orderSapLineDTO.setPurchaseRequisitionNumber("A1");
        orderSapLineDTOList.add(orderSapLineDTO);

        ArrayList<OrderSapScheduleDTO> orderSapScheduleDTOList = new ArrayList<OrderSapScheduleDTO>();
        OrderSapScheduleDTO orderSapScheduleDTO = new OrderSapScheduleDTO();
        orderSapScheduleDTO.setCategoryOfDeliveryDate("c");
        orderSapScheduleDTO.setDeliveryDate(DateUtil.getCurrentUTCDate());
        orderSapScheduleDTOList.add(orderSapScheduleDTO);

        ArrayList<OrderSapAccountsDTO> orderSapAccountsDTOList = new ArrayList<OrderSapAccountsDTO>();
        OrderSapAccountsDTO accountsDTO = new OrderSapAccountsDTO();
        accountsDTO.setSequence("SE");
        accountsDTO.setGeneralLedgerAccount("GA");
        accountsDTO.setCostCenter("costCenter");
        accountsDTO.setWbsElement("WB");

        orderSapAccountsDTOList.add(accountsDTO);

        orderSapLineDTO.setOrderSapAccounts(orderSapAccountsDTOList);
        orderSapLineDTO.setOrderSapSchedule(orderSapScheduleDTOList);
        processPurchaseOrderDTO.setOrderSapLines(orderSapLineDTOList);

        // ACT
        String marshalledXMLMessage = processPurchaseOrderSender.sendProcessPurchaseOrder(processPurchaseOrderDTO);

        // Assert
        System.out.println(marshalledXMLMessage);
        Assert.assertNotNull(marshalledXMLMessage);
        processOrderTransformerBean.transformXmlToJaxb(marshalledXMLMessage);
    }
    
    @Test
    public void testSendProcessPurchaseOrderCancel() throws JAXBException, ParseException {

        // ARRANGE
        ProcessPurchaseOrderDTO processPurchaseOrderDTO = new ProcessPurchaseOrderDTO();

        boolean yesOrNo = true;

        processPurchaseOrderDTO.setAction("Change");
        processPurchaseOrderDTO.setCompanyCode("A022");
        processPurchaseOrderDTO.setOrderType("A");
        processPurchaseOrderDTO.setVendor("vendor");
        processPurchaseOrderDTO.setPurchaseOrganisation("p");
        processPurchaseOrderDTO.setPurhchaseGroup("G");
        processPurchaseOrderDTO.setDocumentDate(DateUtil.getCurrentUTCDate());
        processPurchaseOrderDTO.setCurrency("currency");
        processPurchaseOrderDTO.setPurchaseType("T");
        processPurchaseOrderDTO.setUniqueExtOrder("uniqueExtOrder");

        ArrayList<OrderSapLineDTO> orderSapLineDTOList = new ArrayList<OrderSapLineDTO>();
        OrderSapLineDTO orderSapLineDTO = new OrderSapLineDTO();
        orderSapLineDTO.setRequisitionId(10);
        orderSapLineDTO.setPurchaseOrderItem("O");
        orderSapLineDTO.setAction("Change");
        orderSapLineDTO.setOrderReference("orderReference");
        orderSapLineDTO.setCancelDate(new Date());
        orderSapLineDTO.setPartNumber("partNumber");
        orderSapLineDTO.setShortText("ST");        
        orderSapLineDTO.setPlant("p");
        orderSapLineDTO.setCurrentBuyer("CB");
        orderSapLineDTO.setMaterialGroup("MG");
        orderSapLineDTO.setQuantity(BigDecimal.valueOf(2));
        orderSapLineDTO.setIsoOrderPriceUnit("100");
        orderSapLineDTO.setIsoPurchaseOrderUnit("IS");
        orderSapLineDTO.setNetPrice(BigDecimal.valueOf(10L));
        orderSapLineDTO.setPriceUnit(BigDecimal.valueOf(100L));
        orderSapLineDTO.setTaxCode("TC");
        orderSapLineDTO.setAccountAssignmentCategory("A");
        orderSapLineDTO.setUnlimitedDeliveryIndicator(yesOrNo);
        orderSapLineDTO.setGrIndicator(yesOrNo);
        orderSapLineDTO.setNonValuedGrIndicator(yesOrNo);
        orderSapLineDTO.setIrIndicator(yesOrNo);
        orderSapLineDTO.setAcknowledgementNumber("B1");
        orderSapLineDTO.setPurchaseRequisitionNumber("A1");
        orderSapLineDTOList.add(orderSapLineDTO);

        ArrayList<OrderSapScheduleDTO> orderSapScheduleDTOList = new ArrayList<OrderSapScheduleDTO>();
        OrderSapScheduleDTO orderSapScheduleDTO = new OrderSapScheduleDTO();
        orderSapScheduleDTO.setCategoryOfDeliveryDate("c");
        orderSapScheduleDTO.setDeliveryDate(DateUtil.getCurrentUTCDate());
        orderSapScheduleDTOList.add(orderSapScheduleDTO);

        ArrayList<OrderSapAccountsDTO> orderSapAccountsDTOList = new ArrayList<OrderSapAccountsDTO>();
        OrderSapAccountsDTO accountsDTO = new OrderSapAccountsDTO();
        accountsDTO.setSequence("SE");
        accountsDTO.setGeneralLedgerAccount("GA");
        accountsDTO.setCostCenter("costCenter");
        accountsDTO.setWbsElement("WB");

        orderSapAccountsDTOList.add(accountsDTO);

        orderSapLineDTO.setOrderSapAccounts(orderSapAccountsDTOList);
        orderSapLineDTO.setOrderSapSchedule(orderSapScheduleDTOList);
        processPurchaseOrderDTO.setOrderSapLines(orderSapLineDTOList);

        // ACT
        String marshalledXMLMessage = processPurchaseOrderSender.sendProcessPurchaseOrder(processPurchaseOrderDTO);

        // Assert
        System.out.println(marshalledXMLMessage);
        Assert.assertNotNull(marshalledXMLMessage);
        processOrderTransformerBean.transformXmlToJaxb(marshalledXMLMessage);
    }
    
    @Test
    public void testGetDeliveryNoteLinesForQI() throws GloriaApplicationException {
        //Arrange
        List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
        MaterialLine materialLine = prepareMaterialLine();
        materialLines.add(materialLine);
        setUpDeliveryDataForQI(materialLines, 0L, false);
        
        PageObject pageObject = new PageObject();
        pageObject.setCount(100);
        pageObject.setResultsPerPage(10);
        
        //Act
        pageObject = deliveryService.getDeliveryNoteLinesForQI(pageObject, "QI_READY", "MANDATORY", "1722", true);
        
        //Assert
        Assert.assertNotNull(pageObject);
        Assert.assertNotNull(pageObject.getGridContents());
        Assert.assertEquals(1, pageObject.getGridContents().size());
    }

    @Test
    public void testQiApproveDeliveryNoteLine() throws GloriaApplicationException{
        //Arrange
        List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
        MaterialLine materialLine = prepareMaterialLine();
        materialLines.add(materialLine);
        String deliveryNoteLineIds = setUpDeliveryDataForQI(materialLines, 0L, false);
        
        //Act
        List<DeliveryNoteLine> deliveryNoteLines = materialServices.qiApproveDeliveryNoteLine(deliveryNoteLineIds, "A029682");
        
        //Assert
        Assert.assertNotNull(deliveryNoteLines);
        Assert.assertEquals(1, deliveryNoteLines.size());
        List<MaterialLine> materialLinesAfterApprove = deliveryNoteLines.get(0).getMaterialLine();
        for (MaterialLine materialLineAfterApprove : materialLinesAfterApprove) {
            Assert.assertEquals(MaterialLineStatus.BLOCKED, materialLineAfterApprove.getStatus());
        }
    }
   
    /**
     * Test case scenario : The approveMaterialLines() method encounters two materialLines in the loop with First materialLine having the qty > toApproveQty.
     * In such a case the first materialLine(Qty = 99) should be spilt - 10 as approved from QI and the remaining 89 as Blocked.
     * Also the second materialLine(Qty = 1L) should also be blocked .
     * Test case Forces the materialLines order. Adding the larger qty to the first index.
     * @throws GloriaApplicationException
     */
    @Test
    public void testGLO5247QiApprove() throws GloriaApplicationException {
        //Arrange
        List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
        MaterialLine firstMaterialLine = prepareMaterialLine();
        firstMaterialLine.setQuantity(1L);
        MaterialLine secondMaterialLine = prepareMaterialLine();
        secondMaterialLine.setQuantity(99L);
        
        materialLines.add(0, secondMaterialLine);
        materialLines.add(1, firstMaterialLine);
        
        String deliveryNoteLineIds = setUpDeliveryDataForQI(materialLines, 10L, false);
        
        //Act
        List<DeliveryNoteLine> deliveryNoteLines = materialServices.qiApproveDeliveryNoteLine(deliveryNoteLineIds, "A029682");
        
        //Assert
        Assert.assertNotNull(deliveryNoteLines);
        Assert.assertEquals(1, deliveryNoteLines.size());
        List<MaterialLine> materialLinesAfterApprove = deliveryNoteLines.get(0).getMaterialLine();
        int countOfBlockedLines = 0;
        int countOfApprovedLines = 0;
        int sumOfBlockedQty = 0;
        for (MaterialLine materialLineAfterApprove : materialLinesAfterApprove) {
            if (materialLineAfterApprove.getStatus().equals(MaterialLineStatus.BLOCKED)) {
                countOfBlockedLines++;
                sumOfBlockedQty += materialLineAfterApprove.getQuantity();
            } else {
                countOfApprovedLines++;
                Assert.assertEquals(10L, materialLineAfterApprove.getQuantity().longValue());
            }
        }
        
        Assert.assertEquals(2, countOfBlockedLines);
        Assert.assertEquals(1, countOfApprovedLines);
        Assert.assertEquals(90, sumOfBlockedQty);
    }
    
    @Test
    public void testQiApproveAndStoreDeliveryNoteLine() throws GloriaApplicationException{
        //Arrange
        List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
        MaterialLine materialLine = prepareMaterialLine();
        materialLines.add(materialLine);
        String deliveryNoteLineIds = setUpDeliveryDataForQI(materialLines, 2L, true);
        
        //Act
        List<DeliveryNoteLine> deliveryNoteLines = materialServices.qiApproveDeliveryNoteLine(deliveryNoteLineIds, "A029682");
        
        //Assert
        Assert.assertNotNull(deliveryNoteLines);
        Assert.assertEquals(1, deliveryNoteLines.size());
        List<MaterialLine> materialLinesAfterApprove = deliveryNoteLines.get(0).getMaterialLine();
        int sumOfBlockedQty = 0;
        int sumOfApprovedQty = 0;
        for (MaterialLine materialLineAfterApprove : materialLinesAfterApprove) {
            if (materialLineAfterApprove.getStatus().equals(MaterialLineStatus.BLOCKED)) {
                sumOfBlockedQty += materialLineAfterApprove.getQuantity();
            } else {
                sumOfApprovedQty += materialLineAfterApprove.getQuantity();
                Assert.assertEquals(MaterialLineStatus.STORED, materialLineAfterApprove.getStatus());
            }
        }
        Assert.assertEquals(8, sumOfBlockedQty);
        Assert.assertEquals(2, sumOfApprovedQty);
    }
    
    @Test
    public void testQiApproveDirectStoreIfDirectSendTrue() throws GloriaApplicationException{
        //Arrange
        List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
        MaterialLine materialLine = prepareMaterialLine();
        materialLines.add(materialLine);
        String deliveryNoteLineIds = setUpDeliveryDataForQI(materialLines, 2L, true);
        
        //Act
        List<DeliveryNoteLine> deliveryNoteLines = materialServices.qiApproveDeliveryNoteLine(deliveryNoteLineIds, "A029682");
        
        //Assert
        Assert.assertNotNull(deliveryNoteLines);
        Assert.assertEquals(1, deliveryNoteLines.size());
        List<MaterialLine> materialLinesAfterApprove = deliveryNoteLines.get(0).getMaterialLine();
        int sumOfBlockedQty = 0;
        int sumOfApprovedQty = 0;
        for (MaterialLine materialLineAfterApprove : materialLinesAfterApprove) {
            if (materialLineAfterApprove.getStatus().equals(MaterialLineStatus.BLOCKED)) {
                sumOfBlockedQty += materialLineAfterApprove.getQuantity();
            } else {
                sumOfApprovedQty += materialLineAfterApprove.getQuantity();
                Assert.assertEquals(MaterialLineStatus.STORED, materialLineAfterApprove.getStatus());
            }
        }
        Assert.assertEquals(8, sumOfBlockedQty);
        Assert.assertEquals(2, sumOfApprovedQty);
    }
    
    @Test
    public void testQiApproveNoDirectStoreIfDirectSendTrue() throws GloriaApplicationException{
        //Arrange
        List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
        MaterialLine materialLine = prepareMaterialLine();
        materialLines.add(materialLine);
        String deliveryNoteLineIds = setUpDeliveryDataForQI(materialLines, 2L, false);
        
        //Act
        List<DeliveryNoteLine> deliveryNoteLines = materialServices.qiApproveDeliveryNoteLine(deliveryNoteLineIds, "A029682");
        
        //Assert
        Assert.assertNotNull(deliveryNoteLines);
        Assert.assertEquals(1, deliveryNoteLines.size());
        List<MaterialLine> materialLinesAfterApprove = deliveryNoteLines.get(0).getMaterialLine();
        int sumOfBlockedQty = 0;
        int sumOfApprovedQty = 0;
        for (MaterialLine materialLineAfterApprove : materialLinesAfterApprove) {
            if (materialLineAfterApprove.getStatus().equals(MaterialLineStatus.BLOCKED)) {
                sumOfBlockedQty += materialLineAfterApprove.getQuantity();
            } else {
                sumOfApprovedQty += materialLineAfterApprove.getQuantity();
                Assert.assertEquals(MaterialLineStatus.READY_TO_SHIP, materialLineAfterApprove.getStatus());
            }
        }
        Assert.assertEquals(8, sumOfBlockedQty);
        Assert.assertEquals(2, sumOfApprovedQty);
    }
    
    // GLO-5985
    @Test
    public void testDeliveryNoteLineStatusOnQIApproval() throws GloriaApplicationException {
        // arrange
        List<DeliveryNote> deliveryNotes = deliveryService.getAllDeliveryNotes();
        DeliveryNote deliveryNote = deliveryNotes.get(0);

        List<DeliveryNoteLine> noteLines = deliveryService.getDeliveryNoteLines(deliveryNote.getDeliveryNoteOID(), null, DeliveryNoteLineStatus.IN_WORK, ReceiveType.REGULAR);
        DeliveryNoteLine deliveryNoteLine = noteLines.get(0);
        deliveryNoteLine.setStatus(DeliveryNoteLineStatus.RECEIVED);
        deliveryNoteRepository.save(deliveryNoteLine);
        deliveryNoteLine = deliveryNoteRepository.findDeliveryNoteLineById(deliveryNoteLine.getDeliveryNoteLineOID());
        
        // act
        deliveryNoteLine = deliveryService.updateDeliveryNoteLine(DeliveryHelper.transformAsDTO(deliveryNoteLine, null), "ALL", false);
        
        // assert
        Assert.assertEquals(DeliveryNoteLineStatus.RECEIVED, deliveryNoteLine.getStatus());
    }   
    
    @Ignore
    @Test(expected = GloriaApplicationException.class)
    public void testValidateDeliveryNoteDateForFutureDate() throws GloriaApplicationException {
        // Arrange
        DeliveryNoteDTO deliveryNoteDTO = new DeliveryNoteDTO();
        deliveryNoteDTO.setDeliveryNoteNo("DN1234");
        deliveryNoteDTO.setOrderNo("ON1234");
        deliveryNoteDTO.setReceiveType(ReceiveType.REGULAR.name());
        deliveryNoteDTO.setDeliveryNoteDate(DateUtil.getNextDate(DateUtil.getSqlDate()));

        // Act
        deliveryService.createOrUpdateDeliveryNote(deliveryNoteDTO);

        // Assert
    }
}