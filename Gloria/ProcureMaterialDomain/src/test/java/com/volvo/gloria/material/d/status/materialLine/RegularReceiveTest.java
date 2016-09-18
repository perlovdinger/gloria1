package com.volvo.gloria.material.d.status.materialLine;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.c.QiMarking;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNote;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteSubLine;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLastModified;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusHelper;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.procurematerial.d.type.receive.ReceiveType;
import com.volvo.gloria.procurematerial.repositories.b.MaterialLineStatusCounterRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.c.ZoneType;
import com.volvo.gloria.warehouse.d.entities.BinLocation;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class RegularReceiveTest extends AbstractTransactionalTestCase {
    public RegularReceiveTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    private DeliveryNoteLine deliveryNoteLine;
    private DeliveryNoteSubLine deliveryNoteSubLine;
    private Order order;
    private OrderLine orderLine;
    private ProcureLine procureLine;
    private MaterialLine materialLine;
    private Material material;

    private MaterialHeaderRepository requestHeaderRepository;
    private TraceabilityRepository traceabilityRepository;

    private MaterialServices materialServices;

    private UserDTO user;
    List<MaterialLine> materialLines;
    private WarehouseServices warehouseServices;
    private MaterialLineStatusCounterRepository materialLineStatusCounterRepository;
    private DeliveryNote deliveryNote;

    @Before
    public void setUp() {

        deliveryNoteLine = Mockito.mock(DeliveryNoteLine.class);
        deliveryNote = Mockito.mock(DeliveryNote.class);
        deliveryNoteSubLine = Mockito.mock(DeliveryNoteSubLine.class);
        order = Mockito.mock(Order.class);
        orderLine = Mockito.mock(OrderLine.class);
        procureLine = Mockito.mock(ProcureLine.class);
        materialLine = Mockito.mock(MaterialLine.class);
        material = Mockito.mock(Material.class);
        
        requestHeaderRepository = Mockito.mock(MaterialHeaderRepository.class);
        traceabilityRepository = Mockito.mock(TraceabilityRepository.class);

        materialServices = Mockito.mock(MaterialServices.class);

        user = Mockito.mock(UserDTO.class);

        warehouseServices = Mockito.mock(WarehouseServices.class);
        materialLineStatusCounterRepository = Mockito.mock(MaterialLineStatusCounterRepository.class);
        
        Mockito.when(user.getId()).thenReturn("ALL");
        Mockito.when(user.getUserName()).thenReturn("ALL");

        materialLines = new ArrayList<MaterialLine>();
        Mockito.when(deliveryNoteLine.getMaterialLine()).thenReturn(materialLines);
        Mockito.when(materialLine.getMaterial()).thenReturn(material);
        Mockito.when(material.getProcureLine()).thenReturn(procureLine);
        Mockito.when(material.getOrderLine()).thenReturn(orderLine);
        Mockito.when(orderLine.getOrder()).thenReturn(order);
        Mockito.when(deliveryNoteLine.getAlreadyBlockedQty()).thenReturn(0L);
        Mockito.when(deliveryNoteLine.getAlreadyReceivedQty()).thenReturn(0L);
        List<DeliveryNoteSubLine> deliveryNoteSubLines =new ArrayList<DeliveryNoteSubLine>();
        deliveryNoteSubLines.add(deliveryNoteSubLine);
        Mockito.when(deliveryNoteLine.getDeliveryNoteSubLines()).thenReturn(deliveryNoteSubLines);
        Mockito.when(materialServices.getMaterialLineStatusCounterRepository()).thenReturn(materialLineStatusCounterRepository);
    }

    @Test
    public void receiveMaterials_FULLQTY_NODAMAGEQTY() throws GloriaApplicationException {
        // arrange
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.PLACED);
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.ORDER_PLACED_INTERNAL);
        Mockito.when(materialLine.getQuantity()).thenReturn(4L);

        Mockito.when(deliveryNoteSubLine.getToReceiveQty()).thenReturn(4L);

        Mockito.when(deliveryNoteLine.getDamagedQuantity()).thenReturn(0L);
        Mockito.when(procureLine.getProcureType()).thenReturn(ProcureType.INTERNAL);

        Mockito.when(deliveryNoteLine.getOrderLine()).thenReturn(orderLine);
        Mockito.when(orderLine.getOrder()).thenReturn(order);
        
        MaterialLine materialLineReceived = new MaterialLine();
        materialLineReceived.setStatus(MaterialLineStatus.RECEIVED);
        materialLineReceived.setQuantity(4L);
        Material materialForReceived= new Material();
        materialForReceived.setPartVersion("A03");
        materialLineReceived.setMaterial(materialForReceived);

        List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
        avoidTraceForMLStatus.add(materialLine.getStatus());
        avoidTraceForMLStatus.add(MaterialLineStatus.RECEIVED);
        Mockito.when(requestHeaderRepository.split(materialLine, MaterialLineStatus.RECEIVED, 4, null, requestHeaderRepository, traceabilityRepository, user, avoidTraceForMLStatus))
               .thenReturn(materialLineReceived);
        Mockito.when(deliveryNoteLine.getDeliveryNote()).thenReturn(deliveryNote);
        Mockito.when(deliveryNote.getReceiveType()).thenReturn(ReceiveType.REGULAR);
        // act
        MaterialLine receivedMaterialLine = MaterialLineStatusHelper.receiveRegular(deliveryNoteLine, deliveryNoteSubLine, materialLine, requestHeaderRepository, user, traceabilityRepository,
                                         materialServices);
        // assert
        Assert.assertEquals(receivedMaterialLine.getStatus(), MaterialLineStatus.RECEIVED);
        Assert.assertEquals(receivedMaterialLine.getDeliveryNoteLine(), deliveryNoteLine);
    }

    @Test
    public void receiveMaterials_PARTIALQTY_NODAMAGEQTY() throws GloriaApplicationException {
        // arrange
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.PLACED);
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.ORDER_PLACED_INTERNAL);
        Mockito.when(materialLine.getQuantity()).thenReturn(4L);
        Mockito.when(procureLine.getProcureType()).thenReturn(ProcureType.INTERNAL);
        Mockito.when(deliveryNoteSubLine.getToReceiveQty()).thenReturn(1L);
        Mockito.when(deliveryNoteLine.getDamagedQuantity()).thenReturn(0L);
        Mockito.when(deliveryNoteLine.getOrderLine()).thenReturn(orderLine);
        Mockito.when(orderLine.getOrder()).thenReturn(order);
        
        MaterialLine materialLineReceived = new MaterialLine();
        materialLineReceived.setStatus(MaterialLineStatus.RECEIVED);
        materialLineReceived.setQuantity(1L);
        Material materialForReceived= new Material();
        materialForReceived.setPartVersion("A03");
        materialLineReceived.setMaterial(materialForReceived);
        List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
        avoidTraceForMLStatus.add(materialLine.getStatus());
        avoidTraceForMLStatus.add(MaterialLineStatus.RECEIVED);
        Mockito.when(requestHeaderRepository.split(materialLine, MaterialLineStatus.RECEIVED, 1, null, requestHeaderRepository, traceabilityRepository, user, avoidTraceForMLStatus))
               .thenReturn(materialLineReceived);
        Mockito.when(deliveryNoteLine.getDeliveryNote()).thenReturn(deliveryNote);
        Mockito.when(deliveryNote.getReceiveType()).thenReturn(ReceiveType.REGULAR);
        // act
        MaterialLine receivedMaterialLine = MaterialLineStatusHelper.receiveRegular(deliveryNoteLine, deliveryNoteSubLine, materialLine, requestHeaderRepository, user, traceabilityRepository,
                                         materialServices);
        // assert
        Assert.assertEquals(receivedMaterialLine.getStatus(), MaterialLineStatus.RECEIVED);
        Assert.assertEquals(receivedMaterialLine.getDeliveryNoteLine(), deliveryNoteLine);
        Assert.assertEquals(receivedMaterialLine.getQuantity(), new Long(1L));
    }

    @Test
    public void receiveMaterials_WITH_FULL_DAMAGEQTY() throws GloriaApplicationException {
        // arrange
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.PLACED);

        Mockito.when(deliveryNoteSubLine.getToReceiveQty()).thenReturn(4L);

        Mockito.when(deliveryNoteLine.getDamagedQuantity()).thenReturn(4L);

        Mockito.when(procureLine.getProcureType()).thenReturn(ProcureType.INTERNAL);


        MaterialLine materialLineOrderPlacedInternal = new MaterialLine();
        materialLineOrderPlacedInternal.setStatus(MaterialLineStatus.ORDER_PLACED_INTERNAL);
        materialLineOrderPlacedInternal.setQuantity(4L);
        Mockito.when(requestHeaderRepository.split(materialLine, MaterialLineStatus.RECEIVED, 3, null, requestHeaderRepository, traceabilityRepository, user, null))
               .thenReturn(materialLineOrderPlacedInternal);

        // act
        MaterialLine receivedMaterialLine = MaterialLineStatusHelper.receiveRegular(deliveryNoteLine, deliveryNoteSubLine, materialLine, requestHeaderRepository, user, traceabilityRepository,
                                         materialServices);

        // assert
        Assert.assertEquals(receivedMaterialLine, null);
    }

    @Test
    public void receiveMaterials_RECEIVEWITH_PARTIALDAMAGEQTY() throws GloriaApplicationException {
        // arrange
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.PLACED);
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.ORDER_PLACED_INTERNAL);
        Mockito.when(materialLine.getQuantity()).thenReturn(4L);

        Mockito.when(deliveryNoteSubLine.getToReceiveQty()).thenReturn(4L);

        Mockito.when(deliveryNoteLine.getDamagedQuantity()).thenReturn(1L);
        Mockito.when(procureLine.getProcureType()).thenReturn(ProcureType.INTERNAL);
        
        Mockito.when(deliveryNoteLine.getOrderLine()).thenReturn(orderLine);
        Mockito.when(orderLine.getOrder()).thenReturn(order);
        
        MaterialLine blockedMaterialLine = new MaterialLine();
        blockedMaterialLine.setStatus(MaterialLineStatus.BLOCKED);
        blockedMaterialLine.setQuantity(1L);
        Material materialForBlocked = new Material();
        materialForBlocked.setPartVersion("P02");
        blockedMaterialLine.setMaterial(materialForBlocked);
        List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
        avoidTraceForMLStatus.add(materialLine.getStatus());
        avoidTraceForMLStatus.add(MaterialLineStatus.RECEIVED);

        Mockito.when(requestHeaderRepository.split(materialLine, MaterialLineStatus.BLOCKED, 1, null, requestHeaderRepository, traceabilityRepository, user, null))
               .thenReturn(blockedMaterialLine);

        MaterialLine receivedMaterialLine = new MaterialLine();
        receivedMaterialLine.setStatus(MaterialLineStatus.RECEIVED);
        receivedMaterialLine.setQuantity(3L);
        Material materialForReceived= new Material();
        materialForReceived.setPartVersion("A03");
        receivedMaterialLine.setMaterial(materialForReceived);
        Mockito.when(requestHeaderRepository.split(materialLine, MaterialLineStatus.RECEIVED, 3, null, requestHeaderRepository, traceabilityRepository, user, avoidTraceForMLStatus))
       
        .thenReturn(receivedMaterialLine);
        MaterialLastModified materialLastModified = Mockito.mock(MaterialLastModified.class);
        Mockito.when(material.getMaterialLastModified()).thenReturn(materialLastModified);   
        Mockito.when(deliveryNoteLine.getDeliveryNote()).thenReturn(deliveryNote);
        Mockito.when(deliveryNote.getReceiveType()).thenReturn(ReceiveType.REGULAR);

        // act
        MaterialLine materialLineReceived = MaterialLineStatusHelper.receiveRegular(deliveryNoteLine, deliveryNoteSubLine, materialLine, requestHeaderRepository, user, traceabilityRepository,
                                         materialServices);

        // assert
        Assert.assertEquals(materialLineReceived.getStatus(),MaterialLineStatus.RECEIVED);
        Assert.assertEquals(materialLineReceived.getDeliveryNoteLine(),deliveryNoteLine);
        Assert.assertEquals(materialLineReceived.getQuantity(),new Long(3L));
    }
        
    @Test
    public void testQualityInspectMaterial_MARKING_MANDATORY_DIRECTSEND_TRUE() throws GloriaApplicationException {
        //arrange
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.PLACED);
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.RECEIVED);
        Mockito.when(orderLine.getQiMarking()).thenReturn(QiMarking.MANDATORY);
        Mockito.when(materialLine.getDirectSend()).thenReturn(DirectSendType.YES_REQUESTED);
        Mockito.when(deliveryNoteSubLine.isStore()).thenReturn(false);
        Mockito.when(deliveryNoteSubLine.getDeliveryNoteLine()).thenReturn(deliveryNoteLine);
        Mockito.when(deliveryNoteLine.getOrderLine()).thenReturn(orderLine);
        
        //act
        MaterialLineStatusHelper.qualityInspectMaterial(materialLine, materialServices, user, traceabilityRepository, deliveryNoteSubLine);
        
        //assert
        Mockito.verify(materialLine).setStatus(Mockito.eq(MaterialLineStatus.QI_READY));
        Mockito.verify(materialServices).placeIntoZone(Mockito.eq(materialLine), Mockito.eq(ZoneType.QI));
    }
    
    @Test
    public void testQualityInspectMaterial_MARKING_MANDATORY_DIRECTSEND_FALSE() throws GloriaApplicationException {
        //arrange
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.PLACED);
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.RECEIVED);
        Mockito.when(orderLine.getQiMarking()).thenReturn(QiMarking.MANDATORY);
        Mockito.when(materialLine.getDirectSend()).thenReturn(DirectSendType.NO);
        Mockito.when(deliveryNoteSubLine.isStore()).thenReturn(false);
        Mockito.when(deliveryNoteSubLine.getDeliveryNoteLine()).thenReturn(deliveryNoteLine);
        Mockito.when(deliveryNoteLine.getOrderLine()).thenReturn(orderLine);
        
        //act
        MaterialLineStatusHelper.qualityInspectMaterial(materialLine, materialServices, user, traceabilityRepository, deliveryNoteSubLine);
        
        //assert
        Mockito.verify(materialLine).setStatus(Mockito.eq(MaterialLineStatus.QI_READY));
        Mockito.verify(materialServices).placeIntoZone(Mockito.eq(materialLine), Mockito.eq(ZoneType.QI));
    }
    
    @Test
    public void testQualityInspectMaterial_MARKING_VISUAL_DIRECTSEND_TRUE() throws GloriaApplicationException {
        //arrange
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.PLACED);
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.RECEIVED);
        Mockito.when(orderLine.getQiMarking()).thenReturn(QiMarking.VISUAL);
        Mockito.when(materialLine.getDirectSend()).thenReturn(DirectSendType.YES_REQUESTED);
        Mockito.when(material.getMaterialType()).thenReturn(MaterialType.USAGE);
        Mockito.when(deliveryNoteSubLine.isStore()).thenReturn(false);
        Mockito.when(deliveryNoteSubLine.getDeliveryNoteLine()).thenReturn(deliveryNoteLine);
        Mockito.when(deliveryNoteLine.getOrderLine()).thenReturn(orderLine);
        
        //act
        MaterialLineStatusHelper.qualityInspectMaterial(materialLine, materialServices, user, traceabilityRepository, deliveryNoteSubLine);
        
        //assert
        Mockito.verify(materialLine).setStatus(Mockito.eq(MaterialLineStatus.READY_TO_SHIP));
        Mockito.verify(materialServices).placeIntoZone(Mockito.eq(materialLine), Mockito.eq(ZoneType.SHIPPING));
    }
    
    @Test
    public void testQualityInspectMaterial_MARKING_VISUAL_DIRECTSEND_FALSE() throws GloriaApplicationException {
        //arrange
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.PLACED);
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.RECEIVED);
        Mockito.when(orderLine.getQiMarking()).thenReturn(QiMarking.VISUAL);
        Mockito.when(orderLine.getQiMarking()).thenReturn(QiMarking.VISUAL);
        Mockito.when(materialLine.getDirectSend()).thenReturn(DirectSendType.NO);
        Mockito.when(deliveryNoteSubLine.isStore()).thenReturn(false);
        Mockito.when(deliveryNoteSubLine.getDeliveryNoteLine()).thenReturn(deliveryNoteLine);
        Mockito.when(deliveryNoteLine.getOrderLine()).thenReturn(orderLine);
        
        //act
        MaterialLineStatusHelper.qualityInspectMaterial(materialLine, materialServices, user, traceabilityRepository, deliveryNoteSubLine);
        
        //assert
        Mockito.verify(materialLine).setStatus(Mockito.eq(MaterialLineStatus.READY_TO_STORE));
        Mockito.verify(materialServices).placeIntoZone(Mockito.eq(materialLine), Mockito.eq(ZoneType.TO_STORE));
    }
    
    
    /**
     * TEST cases for GLO-5100
     * @throws GloriaApplicationException
     */
    @Test
    public void receiveMaterials_WITH_ALREADY_RECEIVEDQTY() throws GloriaApplicationException {
        // arrange
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.PLACED);
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.ORDER_PLACED_INTERNAL);
        Mockito.when(materialLine.getQuantity()).thenReturn(10L);

        Mockito.when(deliveryNoteSubLine.getToReceiveQty()).thenReturn(5L);

        Mockito.when(deliveryNoteLine.getAlreadyReceivedQty()).thenReturn(2L);
        Mockito.when(procureLine.getProcureType()).thenReturn(ProcureType.INTERNAL);
        
        Mockito.when(deliveryNoteLine.getOrderLine()).thenReturn(orderLine);
        Mockito.when(orderLine.getOrder()).thenReturn(order);
        
        MaterialLine receivedMaterialLine = new MaterialLine();
        receivedMaterialLine.setStatus(MaterialLineStatus.RECEIVED);
        receivedMaterialLine.setQuantity(3L);
        Material materialForReceived= new Material();
        materialForReceived.setPartVersion("A03");
        receivedMaterialLine.setMaterial(materialForReceived);
        List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
        avoidTraceForMLStatus.add(materialLine.getStatus());
        avoidTraceForMLStatus.add(MaterialLineStatus.RECEIVED);
        Mockito.when(requestHeaderRepository.split(materialLine, MaterialLineStatus.RECEIVED, 3, null, requestHeaderRepository, traceabilityRepository, user, avoidTraceForMLStatus))
               .thenReturn(receivedMaterialLine);
        Mockito.when(deliveryNoteLine.getDeliveryNote()).thenReturn(deliveryNote);
        Mockito.when(deliveryNote.getReceiveType()).thenReturn(ReceiveType.REGULAR);
        // act
        MaterialLine materialLineReceived = MaterialLineStatusHelper.receiveRegular(deliveryNoteLine, deliveryNoteSubLine, materialLine, requestHeaderRepository, user, traceabilityRepository,
                                         materialServices);

        // assert
        Assert.assertEquals(materialLineReceived.getStatus(),MaterialLineStatus.RECEIVED);
        Assert.assertEquals(materialLineReceived.getDeliveryNoteLine(),deliveryNoteLine);
        Assert.assertEquals(materialLineReceived.getQuantity(),new Long(3L));
    }
    
    /**
     * TEST cases for GLO-5100
     * @throws GloriaApplicationException
     */
    @Test
    public void receiveMaterials_WITH_ALREADY_BLOCKEDQTY() throws GloriaApplicationException {
        // arrange
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.PLACED);
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.ORDER_PLACED_INTERNAL);
        Mockito.when(materialLine.getQuantity()).thenReturn(10L);

        Mockito.when(deliveryNoteSubLine.getToReceiveQty()).thenReturn(10L);

        Mockito.when(deliveryNoteLine.getDamagedQuantity()).thenReturn(5L);
        Mockito.when(deliveryNoteLine.getAlreadyBlockedQty()).thenReturn(1L);
        Mockito.when(deliveryNoteLine.getPreviouslyBlockedQty()).thenReturn(3L);
        Mockito.when(procureLine.getProcureType()).thenReturn(ProcureType.INTERNAL);
       
        Mockito.when(deliveryNoteLine.getOrderLine()).thenReturn(orderLine);
        Mockito.when(orderLine.getOrder()).thenReturn(order);
                
        MaterialLine blockedMaterialLine = new MaterialLine();
        blockedMaterialLine.setStatus(MaterialLineStatus.BLOCKED);
        blockedMaterialLine.setQuantity(1L);
        Material materialForBlocked = new Material();
        materialForBlocked.setPartVersion("P02");
        blockedMaterialLine.setMaterial(materialForBlocked);

        Mockito.when(requestHeaderRepository.split(materialLine, MaterialLineStatus.BLOCKED, 1, null, requestHeaderRepository, traceabilityRepository, user, null))
               .thenReturn(blockedMaterialLine); 
        
        MaterialLine receivedMaterialLine = new MaterialLine();
        receivedMaterialLine.setStatus(MaterialLineStatus.RECEIVED);
        receivedMaterialLine.setQuantity(8L);
        Material materialForReceived= new Material();
        materialForReceived.setPartVersion("A03");
        receivedMaterialLine.setMaterial(materialForReceived);

        List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
        avoidTraceForMLStatus.add(materialLine.getStatus());
        avoidTraceForMLStatus.add(MaterialLineStatus.RECEIVED);
        Mockito.when(requestHeaderRepository.split(materialLine, MaterialLineStatus.RECEIVED, 8, null, requestHeaderRepository, traceabilityRepository, user, avoidTraceForMLStatus))
               .thenReturn(receivedMaterialLine);
        Mockito.when(deliveryNoteLine.getDeliveryNote()).thenReturn(deliveryNote);
        Mockito.when(deliveryNote.getReceiveType()).thenReturn(ReceiveType.REGULAR);

        // act
        MaterialLine materialLineReceived = MaterialLineStatusHelper.receiveRegular(deliveryNoteLine, deliveryNoteSubLine, materialLine, requestHeaderRepository, user, traceabilityRepository,
                                         materialServices);

        // assert
        Assert.assertEquals(materialLineReceived.getStatus(),MaterialLineStatus.RECEIVED);
        Assert.assertEquals(materialLineReceived.getDeliveryNoteLine(),deliveryNoteLine);
        Assert.assertEquals(materialLineReceived.getQuantity(),new Long(8L));
    }
    
    @Test
    public void testRegularReceiveAndStore() throws GloriaApplicationException {
        //arrange
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.PLACED);
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.READY_TO_STORE);
        Mockito.when(deliveryNoteSubLine.getBinLocation()).thenReturn(1L);
        BinLocation binLocation = Mockito.mock(BinLocation.class);
        Mockito.when(warehouseServices.findBinLocationById(1L)).thenReturn(binLocation);
        
        MaterialLine mergedMaterialLine = new MaterialLine();
        mergedMaterialLine.setStatus(MaterialLineStatus.STORED);
        mergedMaterialLine.setQuantity(10L);
        Mockito.when(MaterialLineStatusHelper.merge(materialLine, MaterialLineStatus.STORED, null, requestHeaderRepository, traceabilityRepository, user, null))
               .thenReturn(mergedMaterialLine);

        //act
        materialLine.getStatus().storeReceiveAndQi(materialLine, materialServices, user, traceabilityRepository, deliveryNoteSubLine, warehouseServices, requestHeaderRepository);
        
        //assert
        Mockito.verify(materialLine, Mockito.times(2)).setStatus(Mockito.eq(MaterialLineStatus.STORED));
    }
    
    
}
