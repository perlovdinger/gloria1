package com.volvo.gloria.material.d.status.orderLine;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.CompleteType;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.Requisition;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.d.type.internalexternal.InternalExternal;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class OrderLineStatusTest extends AbstractTransactionalTestCase {

    private Order order;

    private OrderLine orderLine;

    private ProcureLine procureLine;

    private Requisition requisition;

    private DeliveryNoteLine deliveryNoteLine;

    private OrderRepository orderRepository;

    private MaterialHeaderRepository requestHeaderRepository;

    private TraceabilityRepository traceabilityRepository;

    private MaterialServices materialServices;

    private WarehouseServices warehouseServices;

    private ProcurementServices procurementServices;

    private UserDTO userDTO;

    public OrderLineStatusTest() {
    }

    @Before
    public void setUp() {
        userDTO = Mockito.mock(UserDTO.class);
        order = Mockito.mock(Order.class);
        orderLine = Mockito.mock(OrderLine.class);
        procureLine = Mockito.mock(ProcureLine.class);
        requisition = Mockito.mock(Requisition.class);
        orderRepository = Mockito.mock(OrderRepository.class);
        traceabilityRepository = Mockito.mock(TraceabilityRepository.class);
        requestHeaderRepository = Mockito.mock(MaterialHeaderRepository.class);
        warehouseServices = Mockito.mock(WarehouseServices.class);
        materialServices = Mockito.mock(MaterialServices.class);
        procurementServices = Mockito.mock(ProcurementServices.class);
        deliveryNoteLine = Mockito.mock(DeliveryNoteLine.class);
    }

    @Test
    public void testRevoke_from_COMPLETED_to_PLACED() throws GloriaApplicationException {
        // arrange
        long quantityCancelled = 10L;

        Mockito.when(orderLine.getReceivedQuantity()).thenReturn(10L);
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.COMPLETED);
        Mockito.when(orderLine.getProcureLine()).thenReturn(procureLine);
        // act
        orderLine.getStatus().revoke(orderLine, quantityCancelled, orderRepository);

        // assert
        Mockito.verify(orderLine).setStatus(Mockito.eq(OrderLineStatus.PLACED));
    }

    @Test
    public void testRevoke_from_COMPLETED_to_RECEIVED_PARTLY() throws GloriaApplicationException {
        // arrange
        long quantityCancelled = 5L;

        Mockito.when(orderLine.getReceivedQuantity()).thenReturn(10L);
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.COMPLETED);
        Mockito.when(orderLine.getProcureLine()).thenReturn(procureLine);

        // act
        orderLine = orderLine.getStatus().revoke(orderLine, quantityCancelled, orderRepository);

        // assert
        Mockito.verify(orderLine).setStatus(Mockito.eq(OrderLineStatus.RECEIVED_PARTLY));
    }

    @Test
    public void testRevoke_from_RECEIVED_PARTLY_to_PLACED() throws GloriaApplicationException {
        // arrange
        long quantityCancelled = 5L;

        Mockito.when(orderLine.getReceivedQuantity()).thenReturn(5L);
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.RECEIVED_PARTLY);
        Mockito.when(orderLine.getProcureLine()).thenReturn(procureLine);

        // act
        orderLine = orderLine.getStatus().revoke(orderLine, quantityCancelled, orderRepository);

        // assert
        Mockito.verify(orderLine).setStatus(Mockito.eq(OrderLineStatus.PLACED));
    }

    @Test
    public void testRevoke_from_RECEIVED_PARTLY_to_RECEIVED_PARTLY() throws GloriaApplicationException {
        // arrange
        long quantityCancelled = 5L;

        Mockito.when(orderLine.getReceivedQuantity()).thenReturn(10L);
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.RECEIVED_PARTLY);
        Mockito.when(orderLine.getProcureLine()).thenReturn(procureLine);
        // act
        orderLine = orderLine.getStatus().revoke(orderLine, quantityCancelled, orderRepository);

        // assert
        Mockito.verify(orderLine).setStatus(Mockito.eq(OrderLineStatus.RECEIVED_PARTLY));
    }

    @Test
    public void testReceive_from_PLACED_to_COMPLETED() throws GloriaApplicationException {
        // arrange
        long receivedQty = 10L;

        Mockito.when(orderLine.getPossibleToReceiveQuantity()).thenReturn(20L);
        Mockito.when(orderLine.getReceivedQuantity()).thenReturn(10L);
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.PLACED);
        Mockito.when(orderLine.getProcureLine()).thenReturn(procureLine);

        // act
        orderLine = orderLine.getStatus().receive(orderLine, deliveryNoteLine, receivedQty);

        // assert
        Mockito.verify(orderLine).setStatus(Mockito.eq(OrderLineStatus.COMPLETED));
        Mockito.verify(orderLine).setCompleteType(Mockito.eq(CompleteType.RECEIVED));
    }

    @Test
    public void testReceive_from_PLACED_to_RECEIVED_PARTLY() throws GloriaApplicationException {
        // arrange
        long receivedQty = 5L;

        Mockito.when(orderLine.getPossibleToReceiveQuantity()).thenReturn(20L);
        Mockito.when(orderLine.getReceivedQuantity()).thenReturn(10L);
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.PLACED);
        Mockito.when(orderLine.getProcureLine()).thenReturn(procureLine);

        // act
        orderLine = orderLine.getStatus().receive(orderLine, deliveryNoteLine, receivedQty);

        // assert
        Mockito.verify(orderLine).setStatus(Mockito.eq(OrderLineStatus.RECEIVED_PARTLY));
    }

    @Test
    public void testReceive_from_RECEIVED_PARTLY_to_COMPLETED() throws GloriaApplicationException {
        // arrange
        long receivedQty = 5L;

        Mockito.when(orderLine.getPossibleToReceiveQuantity()).thenReturn(20L);
        Mockito.when(orderLine.getReceivedQuantity()).thenReturn(15L);
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.RECEIVED_PARTLY);
        Mockito.when(orderLine.getProcureLine()).thenReturn(procureLine);

        // act
        orderLine = orderLine.getStatus().receive(orderLine, deliveryNoteLine, receivedQty);

        // assert
        Mockito.verify(orderLine).setStatus(Mockito.eq(OrderLineStatus.COMPLETED));
        Mockito.verify(orderLine).setCompleteType(Mockito.eq(CompleteType.RECEIVED));
    }

    @Test(expected = GloriaApplicationException.class)
    public void testReceive_from_COMPLETED() throws GloriaApplicationException {
        // arrange
        long receivedQty = 5L;
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.COMPLETED);

        // act
        orderLine = orderLine.getStatus().receive(orderLine, deliveryNoteLine, receivedQty);

        // assert
    }

    @Test
    public void testMarkAsComplete_from_PLACED_to_COMPLETE() throws GloriaApplicationException {
        // arrange
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.PLACED);
        Mockito.when(orderLine.getCurrent()).thenReturn(Mockito.mock(OrderLineVersion.class));
        Mockito.when(orderLine.getCurrent().getQuantity()).thenReturn(10L);
        Mockito.when(orderLine.getReceivedQuantity()).thenReturn(0L);
        Mockito.when(orderLine.getPossibleToReceiveQuantity()).thenReturn(10L);
        Mockito.when(orderLine.getOrder()).thenReturn(order);
        Mockito.when(order.getInternalExternal()).thenReturn(InternalExternal.INTERNAL);

        // act
        orderLine = orderLine.getStatus().markAsComplete(orderLine, userDTO, materialServices, warehouseServices, requestHeaderRepository,
                                                         traceabilityRepository);

        // assert
        Mockito.verify(orderLine).setStatus(Mockito.eq(OrderLineStatus.COMPLETED));
        Mockito.verify(orderLine).setCompleteType(Mockito.eq(CompleteType.COMPLETE));
    }

    @Test
    public void testMarkAsComplete_from_RECEIVED_PARTLY_to_COMPLETE() throws GloriaApplicationException {
        // arrange
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.RECEIVED_PARTLY);
        Mockito.when(orderLine.getCurrent()).thenReturn(Mockito.mock(OrderLineVersion.class));
        Mockito.when(orderLine.getCurrent().getQuantity()).thenReturn(10L);
        Mockito.when(orderLine.getReceivedQuantity()).thenReturn(5L);
        Mockito.when(orderLine.getPossibleToReceiveQuantity()).thenReturn(5L);
        Mockito.when(orderLine.getOrder()).thenReturn(order);
        Mockito.when(order.getInternalExternal()).thenReturn(InternalExternal.INTERNAL);

        // act
        orderLine = orderLine.getStatus().markAsComplete(orderLine, userDTO, materialServices, warehouseServices, requestHeaderRepository,
                                                         traceabilityRepository);

        // assert
        Mockito.verify(orderLine).setStatus(Mockito.eq(OrderLineStatus.COMPLETED));
        Mockito.verify(orderLine).setCompleteType(Mockito.eq(CompleteType.COMPLETE));
    }

    @Test
    public void testCancel_from_PLACED() throws GloriaApplicationException {
        // arrange
        Mockito.when(orderLine.getStatus()).thenReturn(OrderLineStatus.PLACED);
        Mockito.when(orderLine.getProcureLine()).thenReturn(procureLine);
        Mockito.when(procureLine.getRequisition()).thenReturn(requisition);
        Mockito.when(orderLine.getRequisitionId()).thenReturn("G1");
        Mockito.when(procurementServices.findProcureLineByRequisitionId("G1")).thenReturn(procureLine);

        // act
        orderLine.getStatus().cancel(orderLine, procurementServices, requestHeaderRepository, traceabilityRepository, null);
        // assert
        Mockito.verify(orderLine).setStatus(Mockito.eq(OrderLineStatus.COMPLETED));
        Mockito.verify(orderLine).setCompleteType(Mockito.eq(CompleteType.CANCELLED));
        Mockito.verify(procureLine).setStatus(ProcureLineStatus.CANCELLED);
    }
}
