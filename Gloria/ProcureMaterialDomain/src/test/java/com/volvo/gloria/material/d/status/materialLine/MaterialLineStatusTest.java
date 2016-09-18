package com.volvo.gloria.material.d.status.materialLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNote;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteSubLine;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusHelper;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatusHelper;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.procurematerial.d.type.receive.ReceiveType;
import com.volvo.gloria.procurematerial.repositories.b.MaterialLineStatusCounterRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.d.entities.BinLocation;
import com.volvo.gloria.warehouse.d.entities.BinlocationBalance;
import com.volvo.gloria.warehouse.d.entities.Placement;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class MaterialLineStatusTest extends AbstractTransactionalTestCase {
    public MaterialLineStatusTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    private MaterialLine materialLine;
    private Material material;
    private ProcureLine procureLine;
    private DeliveryNoteLine deliveryNoteLine;
    private DeliveryNoteSubLine deliveryNoteSubLine;
    private MaterialHeaderRepository requestHeaderRepository;
    private TraceabilityRepository traceabilityRepository;
    private MaterialServices materialServices;
    private CommonServices commonServices;
    private WarehouseServices warehouseServices;
    private OrderRepository orderRepository;
    private UserDTO user;
    private OrderLine orderLine;
    private Order order;
    private MaterialLineStatusCounterRepository materialLineStatusCounterRepository;
    private DeliveryNote deliveryNote;
    private RequestGroup requestGroup;
    
    @Before
    public void setUp() {
        materialLine = Mockito.mock(MaterialLine.class);
        material = Mockito.mock(Material.class);
        procureLine = Mockito.mock(ProcureLine.class);
        order = Mockito.mock(Order.class);
        orderLine = Mockito.mock(OrderLine.class);
        deliveryNoteLine = Mockito.mock(DeliveryNoteLine.class);
        deliveryNoteSubLine = Mockito.mock(DeliveryNoteSubLine.class);
        requestGroup = Mockito.mock(RequestGroup.class);
        
        requestHeaderRepository = Mockito.mock(MaterialHeaderRepository.class);
        traceabilityRepository = Mockito.mock(TraceabilityRepository.class);
        
        materialServices = Mockito.mock(MaterialServices.class);
        commonServices = Mockito.mock(CommonServices.class);
        warehouseServices = Mockito.mock(WarehouseServices.class);
        orderRepository = Mockito.mock(OrderRepository.class);
        materialLineStatusCounterRepository = Mockito.mock(MaterialLineStatusCounterRepository.class);
        
        user = Mockito.mock(UserDTO.class);
        Mockito.when(materialLine.getMaterial()).thenReturn(material);
        Mockito.when(material.getProcureLine()).thenReturn(procureLine);
        List<DeliveryNoteSubLine> deliveryNoteSubLines=new ArrayList<DeliveryNoteSubLine>();
        deliveryNoteSubLines.add(deliveryNoteSubLine);
        Mockito.when(deliveryNoteLine.getDeliveryNoteSubLines()).thenReturn(deliveryNoteSubLines);
        Mockito.when(order.getOrderNo()).thenReturn("ON1");
        Mockito.when(materialServices.getMaterialLineStatusCounterRepository()).thenReturn(materialLineStatusCounterRepository);
        deliveryNote = Mockito.mock(DeliveryNote.class);
        
    }

    @Test
    public void testProcureInternalForWAIT_TO_PROCUREDState() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.WAIT_TO_PROCURE);
        Mockito.when(procureLine.getProcureType()).thenReturn(ProcureType.INTERNAL);

        // Act
        materialLine.getStatus().procure(materialLine, traceabilityRepository, requestHeaderRepository, user);

        // Assert
        Mockito.verify(materialLine).setStatus(Mockito.eq(MaterialLineStatus.ORDER_PLACED_INTERNAL));
    }

    @Test
    public void testProcureExternalForWAIT_TO_PROCUREDState() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.WAIT_TO_PROCURE);
        Mockito.when(procureLine.getProcureType()).thenReturn(ProcureType.EXTERNAL);

        // Act
        materialLine.getStatus().procure(materialLine, traceabilityRepository, requestHeaderRepository, user);

        // Assert
        Mockito.verify(materialLine).setStatus(Mockito.eq(MaterialLineStatus.REQUISITION_SENT));
    }
    
    @Test
    public void testPlaceREQUISITION_SENTState() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.REQUISITION_SENT);

        // Act
        materialLine.getStatus().place(materialLine, traceabilityRepository, "", "");

        // Assert
        Mockito.verify(materialLine).setStatus(Mockito.eq(MaterialLineStatus.ORDER_PLACED_EXTERNAL));
    }

    @Test
    public void testReceiveForPROCUREDState() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.ORDER_PLACED_INTERNAL);
        Mockito.when(materialLine.getQuantity()).thenReturn(1l);
        Mockito.when(deliveryNoteSubLine.getToReceiveQty()).thenReturn(1L);
        Mockito.when(deliveryNoteLine.getOrderLine()).thenReturn(orderLine);
        Mockito.when(orderLine.getOrder()).thenReturn(order);
         
        MaterialLine materialLineReceived = new MaterialLine();
        materialLineReceived.setStatus(MaterialLineStatus.RECEIVED);
        materialLineReceived.setQuantity(1L);
        Material materialForReceived= new Material();
        materialForReceived.setPartVersion("A03");
        materialLineReceived.setMaterial(materialForReceived);

        List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
        avoidTraceForMLStatus.add(MaterialLineStatus.ORDER_PLACED_INTERNAL);
        avoidTraceForMLStatus.add(MaterialLineStatus.RECEIVED);

        Mockito.when(requestHeaderRepository.split(materialLine, MaterialLineStatus.RECEIVED, 1, null, requestHeaderRepository, traceabilityRepository, user,
                                                   avoidTraceForMLStatus)).thenReturn(materialLineReceived);
        Mockito.when(deliveryNoteLine.getDeliveryNote()).thenReturn(deliveryNote);
        Mockito.when(deliveryNote.getReceiveType()).thenReturn(ReceiveType.REGULAR);
        

        // Act
        MaterialLine receivedMaterialLine = materialLine.getStatus().receive(deliveryNoteLine, deliveryNoteSubLine, materialLine, requestHeaderRepository,
                                                                             user, traceabilityRepository, materialServices);

        // Assert
        Assert.assertEquals(receivedMaterialLine.getStatus(), MaterialLineStatus.RECEIVED);
    }
    
    @Test
    public void testApproveQIAndStoreForQI_READYState() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.QI_READY); 
        Mockito.when(materialLine.getDeliveryNoteLine()).thenReturn(deliveryNoteLine); 
        Mockito.when(materialLine.getDirectSend()).thenReturn(DirectSendType.NO);
        
        // Act
        materialLine.getStatus().approveQI(10, user, materialLine, materialServices, traceabilityRepository, 
                                           requestHeaderRepository, commonServices, false, warehouseServices);

        // Assert
        Mockito.verify(materialLine).setStatus(Mockito.eq(MaterialLineStatus.READY_TO_STORE));
    }
    
    @Test
    public void testApproveQIAndShipForQI_READYState() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.QI_READY);       
        Mockito.when(materialLine.getDeliveryNoteLine()).thenReturn(deliveryNoteLine);
        Mockito.when(materialLine.getDirectSend()).thenReturn(DirectSendType.YES_TRANSFER);
        
        // Act
        materialLine.getStatus().approveQI(10, user, materialLine, materialServices, traceabilityRepository, 
                                           requestHeaderRepository, commonServices, false, warehouseServices);

        // Assert
        Mockito.verify(materialLine).setStatus(Mockito.eq(MaterialLineStatus.READY_TO_SHIP));
    }
    
    @Test
    public void testApproveQIAndStoreForBLOCKEDState() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.BLOCKED); 
        Mockito.when(materialLine.getMaterial()).thenReturn(material);
        Mockito.when(materialLine.getDirectSend()).thenReturn(DirectSendType.NO);
       // Mockito.when(materialLine.getMaterial().getDirectSend().isDirectSend()).thenReturn(false);
        
        // Act
        materialLine.getStatus().approveQI(10, user, materialLine, materialServices, traceabilityRepository, 
                                           requestHeaderRepository, commonServices, false, warehouseServices);

        // Assert
        Mockito.verify(materialLine).setStatus(Mockito.eq(MaterialLineStatus.READY_TO_STORE));
    }
    
    @Test
    public void testApproveQIAndShipForBLOCKEDState() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.BLOCKED);       
        Mockito.when(materialLine.getDeliveryNoteLine()).thenReturn(deliveryNoteLine);
        Mockito.when(materialLine.getDirectSend()).thenReturn(DirectSendType.YES_TRANSFER);
        
        // Act
        materialLine.getStatus().approveQI(10, user, materialLine, materialServices, traceabilityRepository, 
                                           requestHeaderRepository, commonServices, false, warehouseServices);

        // Assert
        Mockito.verify(materialLine).setStatus(Mockito.eq(MaterialLineStatus.READY_TO_SHIP));
    }
    
    @Test
    public void testStoreFullQuantityNoSplit() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialLine.getQuantity()).thenReturn(10L);
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.READY_TO_STORE);

        MaterialLineDTO materialLineDTO = new MaterialLineDTO();
        materialLineDTO.setStoredQuantity(10L);

        List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
        materialLines.add(materialLine);

        String status = MaterialLineStatus.READY_TO_STORE.toString() + "," + MaterialLineStatus.REQUESTED.toString();
        Mockito.when(requestHeaderRepository.findMaterialsByPartStatusSiteAndTransportLabel(null, null, status, null, null, null, null)).thenReturn(materialLines);

        BinLocation binLocation = new BinLocation();
        binLocation.setCode("TEST");
        Mockito.when(warehouseServices.findBinLocationById(materialLineDTO.getBinlocation())).thenReturn(binLocation);
        
        MaterialLine mergedMaterialLine = new MaterialLine();
        mergedMaterialLine.setStatus(MaterialLineStatus.STORED);
        mergedMaterialLine.setQuantity(10L);
        Mockito.when(MaterialLineStatusHelper.merge(materialLine, MaterialLineStatus.STORED, null, requestHeaderRepository, traceabilityRepository, user, null))
               .thenReturn(mergedMaterialLine);

        // Act
        materialLine.getStatus().store(materialLineDTO, user, requestHeaderRepository, materialServices, traceabilityRepository, warehouseServices, null);

        // Assert
        Mockito.verify(materialLine, Mockito.times(2)).setStatus(Mockito.eq(MaterialLineStatus.STORED));
        Mockito.verify(materialLine, Mockito.times(2)).setQuantity(Mockito.eq(10L));
    }
    
    @Test
    public void testGLO5166() throws GloriaApplicationException {
        //Arrange
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.REQUESTED);
        Mockito.when(materialLine.getQuantity()).thenReturn(5L);
        MaterialLineDTO materialLineDTO = new MaterialLineDTO();
        
        Mockito.when(requestHeaderRepository.findMaterialLineById(0L)).thenReturn(materialLine);
        
        materialLineDTO.setPickedQuantity(0L);
        
        MaterialLine deviatedMaterialLine =  new MaterialLine();
        deviatedMaterialLine.setStatus(MaterialLineStatus.DEVIATED);
        deviatedMaterialLine.setQuantity(5L);

        List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
        avoidTraceForMLStatus.add(MaterialLineStatus.REQUESTED);
        
        Mockito.when(MaterialLineStatusHelper.split(materialLine, MaterialLineStatus.DEVIATED, 5L, requestHeaderRepository, traceabilityRepository,
                                                    materialServices, user, avoidTraceForMLStatus)).thenReturn(deviatedMaterialLine);

        Mockito.when(requestHeaderRepository.findAMaterialLineByAndStatusAndPlacement(materialLine, MaterialLineStatus.DEVIATED,
                                                                                                                  null)).thenReturn(null);
        
        Mockito.when(requestHeaderRepository.split(materialLine, MaterialLineStatus.DEVIATED, 5L, null, requestHeaderRepository, traceabilityRepository, user,
                                                   avoidTraceForMLStatus)).thenReturn(deviatedMaterialLine);
        
        Mockito.when(MaterialLineStatusHelper.merge(materialLine, MaterialLineStatus.DEVIATED, null, requestHeaderRepository, traceabilityRepository, user,
                                                    avoidTraceForMLStatus)).thenReturn(deviatedMaterialLine);
        //Act
        materialLine.getStatus().pick(materialLineDTO, user, requestHeaderRepository, traceabilityRepository, warehouseServices, materialServices);
        
        //Assert
        Mockito.verify(materialLine).setStatus(Mockito.eq(MaterialLineStatus.DEVIATED));
        Mockito.verify(materialLine).setQuantity(Mockito.eq(5L));
    }
    
    /**
     * Test for request goods for partial pull quantity
     * @throws GloriaApplicationException
     */
    @Test
    public void testGLO5174() throws GloriaApplicationException {
        //Arrange
        Mockito.when(materialLine.getRequestGroup()).thenReturn(requestGroup);
        Mockito.when(requestGroup.getMaterialLines()).thenReturn(new ArrayList<MaterialLine>(Arrays.asList(materialLine)));
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.READY_TO_STORE);
        Mockito.when(materialLine.getQuantity()).thenReturn(8L);
        MaterialLineDTO materialLineDTO = new MaterialLineDTO();
        materialLineDTO.setPossiblePickQuantity(5L);
        materialLineDTO.setQuantity(8L);
        Mockito.when(requestHeaderRepository.findMaterialLineById(0L)).thenReturn(materialLine);
        
        Placement placement = new Placement();
        placement.setPlacementOid(1L);
        BinLocation binLocation = new BinLocation();
        binLocation.setCode("TS-01-01-01");
        placement.setBinLocation(binLocation);
        
        BinlocationBalance binlocationBalance = new BinlocationBalance();
        binlocationBalance.setBinLocation(binLocation);
        binlocationBalance.setQuantity(8L);
        placement.setBinlocationBalance(binlocationBalance);
        Mockito.when(warehouseServices.getPlacement(materialLine.getPlacementOID())).thenReturn(placement);
        
        Mockito.when(warehouseServices.getPlacementByMaterialLine(materialLine.getMaterialLineOID())).thenReturn(placement);
        
        List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
        avoidTraceForMLStatus.add(MaterialLineStatus.READY_TO_STORE);

        MaterialLine requestedMaterialLine = new MaterialLine();
        requestedMaterialLine.setQuantity(5L);
        requestedMaterialLine.setStatus(MaterialLineStatus.REQUESTED);
        Mockito.when(MaterialLineStatusHelper.split(materialLine, MaterialLineStatus.REQUESTED, materialLineDTO.getPossiblePickQuantity(),
                                                    requestHeaderRepository, traceabilityRepository, materialServices, user, avoidTraceForMLStatus))
               .thenReturn(requestedMaterialLine);

        //Act
        requestedMaterialLine = materialLine.getStatus().request(materialLineDTO, requestHeaderRepository, traceabilityRepository, materialServices, user, warehouseServices, null, null, null, commonServices);
        
        //Assert
        Mockito.verify(materialServices).createPlacement(binLocation, requestedMaterialLine);
        Mockito.verify(materialServices).createPlacement(binLocation, materialLine);
        
    }
    
    @Test
    public void testBorrowReturnTo_WAIT_TO_PROCURE() throws GloriaApplicationException {
        MaterialLine borrowFromMaterialLine1 = Mockito.mock(MaterialLine.class);
        MaterialLine borrowFromMaterialLine2 = Mockito.mock(MaterialLine.class);
        MaterialLine borrowFromMaterialLine3 = Mockito.mock(MaterialLine.class);

        // Arrange
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.WAIT_TO_PROCURE);

        //Act
        prepareAndBorrow(borrowFromMaterialLine1, borrowFromMaterialLine2, borrowFromMaterialLine3, null, false);

        //Assert
        Mockito.verify(requestHeaderRepository, Mockito.times(3)).split(materialLine, MaterialLineStatus.WAIT_TO_PROCURE, 10, null, requestHeaderRepository,
                                                                        traceabilityRepository, user, null);
    }

    @Test
    public void testBorrowReturnTo_ORDER_PLACED_EXTERNAL() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.ORDER_PLACED_EXTERNAL);

        OrderLine orderLine = Mockito.mock(OrderLine.class);

        MaterialLine borrowFromMaterialLine1 = Mockito.mock(MaterialLine.class);
        MaterialLine borrowFromMaterialLine2 = Mockito.mock(MaterialLine.class);
        MaterialLine borrowFromMaterialLine3 = Mockito.mock(MaterialLine.class);

        //Act
        prepareAndBorrow(borrowFromMaterialLine1, borrowFromMaterialLine2, borrowFromMaterialLine3, orderLine, false);

        //Assert
        Mockito.verify(requestHeaderRepository, Mockito.times(3)).split(materialLine, MaterialLineStatus.ORDER_PLACED_EXTERNAL, 10, null,
                                                                        requestHeaderRepository, traceabilityRepository, user, null);
    }
    
    @Test
    public void testBorrowNoReturnTo_WAIT_TO_PROCURE() throws GloriaApplicationException {
        MaterialLine borrowFromMaterialLine1 = Mockito.mock(MaterialLine.class);
        MaterialLine borrowFromMaterialLine2 = Mockito.mock(MaterialLine.class);
        MaterialLine borrowFromMaterialLine3 = Mockito.mock(MaterialLine.class);

        // Arrange
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.WAIT_TO_PROCURE);

        //Act
        prepareAndBorrow(borrowFromMaterialLine1, borrowFromMaterialLine2, borrowFromMaterialLine3, null, true);

        //Assert
        Mockito.verify(requestHeaderRepository, Mockito.times(3)).split(materialLine, MaterialLineStatus.WAIT_TO_PROCURE, 10, null, requestHeaderRepository,
                                                                        traceabilityRepository, user, null);
        Mockito.verify(requestHeaderRepository, Mockito.times(1)).split(borrowFromMaterialLine1, MaterialLineStatus.STORED, 10, null, requestHeaderRepository,
                                                                        traceabilityRepository, user, null);
        Mockito.verify(requestHeaderRepository, Mockito.times(1)).split(borrowFromMaterialLine2, MaterialLineStatus.READY_TO_STORE, 10, null, requestHeaderRepository,
                                                                        traceabilityRepository, user, null);
        Mockito.verify(requestHeaderRepository, Mockito.times(1)).split(borrowFromMaterialLine3, MaterialLineStatus.READY_TO_STORE, 10, null, requestHeaderRepository,
                                                                        traceabilityRepository, user, null);
    }

    @Test
    public void testBorrowNoReturnTo_ORDER_PLACED_EXTERNAL() throws GloriaApplicationException {
        MaterialLine borrowFromMaterialLine1 = Mockito.mock(MaterialLine.class);
        MaterialLine borrowFromMaterialLine2 = Mockito.mock(MaterialLine.class);
        MaterialLine borrowFromMaterialLine3 = Mockito.mock(MaterialLine.class);

        // Arrange
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.ORDER_PLACED_EXTERNAL);

        //Act
        prepareAndBorrow(borrowFromMaterialLine1, borrowFromMaterialLine2, borrowFromMaterialLine3, null, true);
        
        //Assert
        Mockito.verify(requestHeaderRepository, Mockito.times(3)).split(materialLine, MaterialLineStatus.ORDER_PLACED_EXTERNAL, 10, null, requestHeaderRepository,
                                                                        traceabilityRepository, user, null);
        Mockito.verify(requestHeaderRepository, Mockito.times(1)).split(borrowFromMaterialLine1, MaterialLineStatus.STORED, 10, null, requestHeaderRepository,
                                                                        traceabilityRepository, user, null);
        Mockito.verify(requestHeaderRepository, Mockito.times(1)).split(borrowFromMaterialLine2, MaterialLineStatus.READY_TO_STORE, 10, null, requestHeaderRepository,
                                                                        traceabilityRepository, user, null);
        Mockito.verify(requestHeaderRepository, Mockito.times(1)).split(borrowFromMaterialLine3, MaterialLineStatus.READY_TO_STORE, 10, null, requestHeaderRepository,
                                                                        traceabilityRepository, user, null);
    }
    
    @Test
    public void testBorrowNoReturnTo_MARKED_INSPECTION() throws GloriaApplicationException {
        MaterialLine borrowFromMaterialLine1 = Mockito.mock(MaterialLine.class);
        MaterialLine borrowFromMaterialLine2 = Mockito.mock(MaterialLine.class);
        MaterialLine borrowFromMaterialLine3 = Mockito.mock(MaterialLine.class);

        // Arrange
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.MARKED_INSPECTION);

        //Act
        prepareAndBorrow(borrowFromMaterialLine1, borrowFromMaterialLine2, borrowFromMaterialLine3, null, true);

        //Assert
        Mockito.verify(requestHeaderRepository, Mockito.times(3)).split(materialLine, MaterialLineStatus.MARKED_INSPECTION, 10, null, requestHeaderRepository,
                                                                        traceabilityRepository, user, null);
        Mockito.verify(requestHeaderRepository, Mockito.times(1)).split(borrowFromMaterialLine1, MaterialLineStatus.STORED, 10, null, requestHeaderRepository,
                                                                        traceabilityRepository, user, null);
        Mockito.verify(requestHeaderRepository, Mockito.times(1)).split(borrowFromMaterialLine2, MaterialLineStatus.READY_TO_STORE, 10, null,
                                                                        requestHeaderRepository, traceabilityRepository, user, null);
        Mockito.verify(requestHeaderRepository, Mockito.times(1)).split(borrowFromMaterialLine3, MaterialLineStatus.READY_TO_STORE, 10, null,
                                                                        requestHeaderRepository, traceabilityRepository, user, null);
    }

    private void prepareAndBorrow(MaterialLine borrowFromMaterialLine1, MaterialLine borrowFromMaterialLine2, MaterialLine borrowFromMaterialLine3, OrderLine orderLine, boolean noReturn)
            throws GloriaApplicationException {
        Material borrowFromMaterial1 = Mockito.mock(Material.class);
        Material borrowFromMaterial2 = Mockito.mock(Material.class);
        Material borrowFromMaterial3 = Mockito.mock(Material.class);
        Mockito.when(materialLine.getQuantity()).thenReturn(30L);

        Mockito.when(material.getMaterialType()).thenReturn(MaterialType.USAGE);

        Mockito.when(material.getOrderLine()).thenReturn(orderLine);

        List<MaterialLine> borrowFromMaterialLines = new ArrayList<MaterialLine>();

        // MaterialLine 1
        Mockito.when(borrowFromMaterialLine1.getStatus()).thenReturn(MaterialLineStatus.STORED);
        Mockito.when(borrowFromMaterialLine1.getQuantity()).thenReturn(10L);

        OrderLine borrowFromOrderLine1 = Mockito.mock(OrderLine.class);
        ProcureLine borrowFromProcureLine1 = Mockito.mock(ProcureLine.class);
        Mockito.when(borrowFromProcureLine1.getProcureType()).thenReturn(ProcureType.INTERNAL);

        Mockito.when(borrowFromMaterialLine1.getMaterial()).thenReturn(borrowFromMaterial1);
        Mockito.when(borrowFromMaterial1.getProcureLine()).thenReturn(borrowFromProcureLine1);
        Mockito.when(borrowFromMaterial1.getOrderLine()).thenReturn(borrowFromOrderLine1);
        Mockito.when(borrowFromMaterial1.getOrderNo()).thenReturn("order1");

        borrowFromMaterialLines.add(borrowFromMaterialLine1);

        // MaterialLine 2
        Mockito.when(borrowFromMaterialLine2.getStatus()).thenReturn(MaterialLineStatus.READY_TO_STORE);
        Mockito.when(borrowFromMaterialLine2.getQuantity()).thenReturn(10L);

        OrderLine borrowFromOrderLine2 = Mockito.mock(OrderLine.class);
        ProcureLine borrowFromProcureLine2 = Mockito.mock(ProcureLine.class);
        Mockito.when(borrowFromProcureLine2.getProcureType()).thenReturn(ProcureType.INTERNAL);

        Mockito.when(borrowFromMaterialLine2.getMaterial()).thenReturn(borrowFromMaterial2);
        Mockito.when(borrowFromMaterial2.getProcureLine()).thenReturn(borrowFromProcureLine2);
        Mockito.when(borrowFromMaterial2.getOrderLine()).thenReturn(borrowFromOrderLine2);
        Mockito.when(borrowFromMaterial2.getOrderNo()).thenReturn("order2");

        borrowFromMaterialLines.add(borrowFromMaterialLine2);

        // MaterialLine 3
        Mockito.when(borrowFromMaterialLine3.getStatus()).thenReturn(MaterialLineStatus.READY_TO_STORE);
        Mockito.when(borrowFromMaterialLine3.getQuantity()).thenReturn(15L);

        OrderLine borrowFromOrderLine3 = Mockito.mock(OrderLine.class);
        ProcureLine borrowFromProcureLine3 = Mockito.mock(ProcureLine.class);
        Mockito.when(borrowFromProcureLine3.getProcureType()).thenReturn(ProcureType.INTERNAL);

        Mockito.when(borrowFromMaterialLine3.getMaterial()).thenReturn(borrowFromMaterial3);
        Mockito.when(borrowFromMaterial3.getProcureLine()).thenReturn(borrowFromProcureLine3);
        Mockito.when(borrowFromMaterial3.getOrderLine()).thenReturn(borrowFromOrderLine3);
        Mockito.when(borrowFromMaterial3.getOrderNo()).thenReturn("order3");

        borrowFromMaterialLines.add(borrowFromMaterialLine3);
        // Act
        materialLine.getStatus().borrow(materialLine, borrowFromMaterialLines, noReturn, requestHeaderRepository, materialServices, traceabilityRepository,
                                         warehouseServices,  orderRepository, user);
    }
    
    /**
     * Store with equal qty but with existing materials available for merge
     * @throws GloriaApplicationException
     */
    @Test
    public void testGLO5240_storeWithMergedMaterials() throws GloriaApplicationException {
        //Arrange
        Mockito.when(materialLine.getQuantity()).thenReturn(3L);
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.READY_TO_STORE);

        MaterialLineDTO materialLineDTO = new MaterialLineDTO();
        materialLineDTO.setStoredQuantity(3L);

        List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
        materialLines.add(materialLine);

        String status = MaterialLineStatus.READY_TO_STORE.toString() + "," + MaterialLineStatus.REQUESTED.toString();
        Mockito.when(requestHeaderRepository.findMaterialsByPartStatusSiteAndTransportLabel(null, null, status, null, null, null, null)).thenReturn(materialLines);

        BinLocation binLocation = new BinLocation();
        binLocation.setCode("TEST");
        Mockito.when(warehouseServices.findBinLocationById(materialLineDTO.getBinlocation())).thenReturn(binLocation);
        
        MaterialLine mergedMaterialLine = new MaterialLine();
        mergedMaterialLine.setStatus(MaterialLineStatus.STORED);
        mergedMaterialLine.setQuantity(10L);
        Mockito.when(MaterialLineStatusHelper.merge(materialLine, MaterialLineStatus.STORED, null, requestHeaderRepository, traceabilityRepository, user, null))
               .thenReturn(mergedMaterialLine);

        // Act
        materialLine.getStatus().store(materialLineDTO, user, requestHeaderRepository, materialServices, traceabilityRepository, warehouseServices, null);

        // Assert
        Mockito.verify(materialServices).createPlacement(binLocation, mergedMaterialLine);
    }
    
    /*
     *  test appropriate material line transitions 
     * 
     */
    @Test
    public void testShipOrTransferStatus(){
        try {
            MaterialLineStatus.BLOCKED.shipOrTransfer(null, materialServices, null);
            Assert.fail("Blocked should not be able to ShipOrTransfer");            
        } catch (GloriaApplicationException exception) {
            
        }
        try {
            MaterialLineStatus.CREATED.shipOrTransfer(null, materialServices, null);
            Assert.fail("created should not be able to ShipOrTransfer");            
        } catch (GloriaApplicationException exception) { 
        }
        try {
            MaterialLineStatus.DEVIATED.shipOrTransfer(null, materialServices, null);
            Assert.fail("deviated should not be able to ShipOrTransfer");            
        } catch (GloriaApplicationException exception) { 
        }
        try {
            MaterialLineStatus.IN_TRANSFER.shipOrTransfer(null, materialServices, null);
            Assert.fail("IN_TRANSFER should not be able to ShipOrTransfer");            
        } catch (GloriaApplicationException exception) { 
        }
        try {
            MaterialLineStatus.MARKED_INSPECTION.shipOrTransfer(null, materialServices, null);
            Assert.fail("MARKED_INSPECTION should not be able to ShipOrTransfer");            
        } catch (GloriaApplicationException exception) { 
        }
        try {
            MaterialLineStatus.MISSING.shipOrTransfer(null, materialServices, null);
            Assert.fail("MISSING should not be able to ShipOrTransfer");            
        } catch (GloriaApplicationException exception) { 
        }
        try {
            MaterialLineStatus.ORDER_PLACED_EXTERNAL.shipOrTransfer(null, materialServices, null);
            Assert.fail("ORDER_PLACED_EXTERNAL should not be able to ShipOrTransfer");            
        } catch (GloriaApplicationException exception) { 
        }
        try {
            MaterialLineStatus.ORDER_PLACED_INTERNAL.shipOrTransfer(null, materialServices, null);
            Assert.fail("ORDER_PLACED_INTERNAL should not be able to ShipOrTransfer");            
        } catch (GloriaApplicationException exception) { 
        }
        try {
            MaterialLineStatus.QI_OK.shipOrTransfer(null, materialServices, null);
            Assert.fail("QI_OK should not be able to ShipOrTransfer");            
        } catch (GloriaApplicationException exception) { 
        }
        try {
            MaterialLineStatus.QI_READY.shipOrTransfer(null, materialServices, null);
            Assert.fail("QI_READY should not be able to ShipOrTransfer");            
        } catch (GloriaApplicationException exception) { 
        }

        try {
            MaterialLineStatus.READY_TO_STORE.shipOrTransfer(null, materialServices, null);
            Assert.fail("READY_TO_STORE should not be able to ShipOrTransfer");            
        } catch (GloriaApplicationException exception) { 
        }
        try {
            MaterialLineStatus.REMOVED.shipOrTransfer(null, materialServices, null);
            Assert.fail("REMOVED should not be able to ShipOrTransfer");            
        } catch (GloriaApplicationException exception) { 
        }
        try {
            MaterialLineStatus.REMOVED_DB.shipOrTransfer(null, materialServices, null);
            Assert.fail("REMOVED_DB should not be able to ShipOrTransfer");            
        } catch (GloriaApplicationException exception) { 
        }
        try {
            MaterialLineStatus.REQUISITION_SENT.shipOrTransfer(null, materialServices, null);
            Assert.fail("REQUISITION_SENT should not be able to ShipOrTransfer");            
        } catch (GloriaApplicationException exception) { 
        }
        try {
            MaterialLineStatus.SCRAPPED.shipOrTransfer(null, materialServices, null);
            Assert.fail("SCRAPPED should not be able to ShipOrTransfer");            
        } catch (GloriaApplicationException exception) { 
        }
        
        try {
            MaterialLineStatus.SHIPPED.shipOrTransfer(null, materialServices, null);
            Assert.fail("SHIPPED should not be able to ShipOrTransfer");            
        } catch (GloriaApplicationException exception) { 
        }
        try {
            MaterialLineStatus.STORED.shipOrTransfer(null, materialServices, null);
            Assert.fail("STORED should not be able to ShipOrTransfer");            
        } catch (GloriaApplicationException exception) { 
        }
        try {
            MaterialLineStatus.WAIT_TO_PROCURE.shipOrTransfer(null, materialServices, null);
            Assert.fail("WAIT_TO_PROCURE should not be able to ShipOrTransfer");            
        } catch (GloriaApplicationException exception) { 
        }
        try {
            MaterialLineStatus.REQUESTED.shipOrTransfer(null, materialServices, null);
            Assert.fail("REQUESTED should not be able to ShipOrTransfer");            
        } catch (GloriaApplicationException exception) { 
        }

        //this is the only success case
        try {
            MaterialLine materialLine = new MaterialLine();
            RequestGroup requestGroup = new RequestGroup();
            RequestList requestList = new RequestList();
            requestList.setRequestListOid(1L);
            requestGroup.setRequestList(requestList);
            materialLine.setRequestGroup(requestGroup);
            MaterialLineStatus.READY_TO_SHIP.shipOrTransfer(materialLine, materialServices, null);
        } catch (GloriaApplicationException exception) { 
            Assert.fail("READY_TO_SHIP should not throw exception");            
        }
    }

    @Test
    public void testDecreaseQty_From_OrderPlaced_External_TO_QTY_DECREASED_State() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.ORDER_PLACED_EXTERNAL);
        Mockito.when(materialLine.getQuantity()).thenReturn(4L);
        Mockito.when(orderLine.getAlreadyDecreasedQty()).thenReturn(0L);
        MaterialLine qtyDecreasedMaterialLine = Mockito.mock(MaterialLine.class);
        Mockito.when(qtyDecreasedMaterialLine.getQuantity()).thenReturn(2L);
        Mockito.when(MaterialLineStatusHelper.split(materialLine, MaterialLineStatus.QTY_DECREASED, 2L, requestHeaderRepository, traceabilityRepository,
                                                    materialServices, user, null)).thenReturn(qtyDecreasedMaterialLine);

        // Act
        materialLine.getStatus().markAsDecreased(orderLine, materialLine, 2L, user, materialServices, warehouseServices, requestHeaderRepository,
                                                 traceabilityRepository);

        // Assert
        List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
        avoidTraceForMLStatus.add(MaterialLineStatus.REQUISITION_SENT);

        Mockito.verify(requestHeaderRepository).split(materialLine, MaterialLineStatus.QTY_DECREASED, 2L, null, requestHeaderRepository,
                                                      traceabilityRepository, user, avoidTraceForMLStatus);
    }

    @Test
    public void testDecreaseQty_From_OrderPlaced_Internal_TO_QTY_DECREASED_State() throws GloriaApplicationException {
     // Arrange
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.ORDER_PLACED_INTERNAL);
        Mockito.when(materialLine.getQuantity()).thenReturn(4L);
        Mockito.when(orderLine.getAlreadyDecreasedQty()).thenReturn(0L);
        MaterialLine qtyDecreasedMaterialLine = Mockito.mock(MaterialLine.class);
        Mockito.when(qtyDecreasedMaterialLine.getQuantity()).thenReturn(2L);
        Mockito.when(MaterialLineStatusHelper.split(materialLine, MaterialLineStatus.QTY_DECREASED, 2L, requestHeaderRepository, traceabilityRepository,
                                                    materialServices, user, null)).thenReturn(qtyDecreasedMaterialLine);

        // Act
        materialLine.getStatus().markAsDecreased(orderLine, materialLine, 2L, user, materialServices, warehouseServices, requestHeaderRepository,
                                                 traceabilityRepository);

        // Assert
        List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
        avoidTraceForMLStatus.add(MaterialLineStatus.REQUISITION_SENT);
        Mockito.verify(requestHeaderRepository).split(materialLine, MaterialLineStatus.QTY_DECREASED, 2L, null, requestHeaderRepository,
                                                      traceabilityRepository, user, avoidTraceForMLStatus);
    }
             
    @Test
    public void testCancel_FROM_ORDERPLACEDEXTERNAL_TO_WAITTOPROCURE_WHEN_MATERIALTYPE_USAGE() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialLine.getMaterial()).thenReturn(material);
        Mockito.when(material.getMaterialType()).thenReturn(MaterialType.USAGE);
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.ORDER_PLACED_EXTERNAL);
        
        // Act
        materialLine.getStatus().cancel(materialLine, requestHeaderRepository, traceabilityRepository, null);
        // Assert
        Mockito.verify(materialLine).setStatus(MaterialLineStatus.WAIT_TO_PROCURE);
    }
    
    @Test
    public void testCancel_FROM_ORDERPLACEDEXTERNAL_TO_WAITTOPROCURE_WHEN_MATERIALTYPE_MODIFIED() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialLine.getMaterial()).thenReturn(material);
        Mockito.when(material.getMaterialType()).thenReturn(MaterialType.MODIFIED);
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.ORDER_PLACED_EXTERNAL);
        
        // Act
        materialLine.getStatus().cancel(materialLine, requestHeaderRepository, traceabilityRepository, null);
        // Assert
        Mockito.verify(materialLine).setStatus(MaterialLineStatus.QTY_DECREASED);
    }
    
    @Test
    public void testCancel_FROM_ORDERPLACEDINTERNAL() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.ORDER_PLACED_INTERNAL);
        Mockito.when(materialLine.getProcureType()).thenReturn(ProcureType.INTERNAL);
        // Act
        materialLine.getStatus().cancel(materialLine, requestHeaderRepository, traceabilityRepository, null);
        // Assert
        Mockito.verify(materialLine, Mockito.times(0)).setStatus(MaterialLineStatus.WAIT_TO_PROCURE);
    }
    
    @Test
    public void testCancel_FROM_WAITTOPROCURE() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.WAIT_TO_PROCURE);
        Mockito.when(materialLine.getProcureType()).thenReturn(ProcureType.EXTERNAL);        
        // Act
        materialLine.getStatus().cancel(materialLine, requestHeaderRepository, traceabilityRepository, null);
        // Assert
        Mockito.verify(materialLine, Mockito.times(0)).setStatus(MaterialLineStatus.WAIT_TO_PROCURE);
    }
    
    @Test
    public void testGLO5947() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.ORDER_PLACED_EXTERNAL);
        Mockito.when(materialLine.getQuantity()).thenReturn(40L);
        Mockito.when(orderLine.getOrderLineOID()).thenReturn(1L);
        Mockito.when(orderLine.getAlreadyDecreasedQty()).thenReturn(0L);
        Mockito.when(orderLine.getReceivedQuantity()).thenReturn(20L);
        List<Material> orderLineMaterials = new ArrayList<Material>();
        orderLineMaterials.add(material);
        
        List<MaterialLine> orderLineMaterialLines = new ArrayList<MaterialLine>();
        orderLineMaterialLines.add(materialLine);
        Mockito.when(orderLine.getMaterials()).thenReturn(orderLineMaterials);

        Mockito.when(material.getMaterialLine()).thenReturn(orderLineMaterialLines);

        
        List<Material> materials = new ArrayList<Material>();
        materials.add(material);
        
        Mockito.when(materialServices.getMaterialLinesForOrderLine(1L)).thenReturn(materials);

        // Act
        OrderLineStatusHelper.handleMaterialsWhenPossibleToReceiveQuantityIsDecreased(8, orderLine, user, materialServices, warehouseServices,
                                                                                      requestHeaderRepository, traceabilityRepository);

        // Assert
        List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
        avoidTraceForMLStatus.add(MaterialLineStatus.REQUISITION_SENT);

        Mockito.verify(requestHeaderRepository).split(materialLine, MaterialLineStatus.QTY_DECREASED, 8L, null, requestHeaderRepository,
                                                      traceabilityRepository, user, avoidTraceForMLStatus);
    }

}