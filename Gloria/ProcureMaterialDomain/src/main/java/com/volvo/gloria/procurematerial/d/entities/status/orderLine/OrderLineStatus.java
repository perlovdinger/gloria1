package com.volvo.gloria.procurematerial.d.entities.status.orderLine;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.b.WarehouseServices;

/**
 * Enum class for Order Status.
 */
public enum OrderLineStatus implements OrderLineStatusOperations {

    PLACED(new Placed()), 
    RECEIVED_PARTLY(new ReceivedPartly()), 
    COMPLETED(new Completed());

    private final OrderLineStatusDefaultOperations orderLineStatusDefaultOperations;

    private OrderLineStatus(OrderLineStatusDefaultOperations orderLineStatusDefaultOperations) {
        this.orderLineStatusDefaultOperations = orderLineStatusDefaultOperations;
    }

    @Override
    public OrderLine revoke(OrderLine orderLine, long quantityCancelled, OrderRepository orderRepository) throws GloriaApplicationException {
        return this.orderLineStatusDefaultOperations.revoke(orderLine, quantityCancelled, orderRepository);
    }

    @Override
    public OrderLine receive(OrderLine orderLine, DeliveryNoteLine deliveryNoteLine, long receivedQty) throws GloriaApplicationException {
        return this.orderLineStatusDefaultOperations.receive(orderLine, deliveryNoteLine, receivedQty);
    }

    @Override
    public OrderLine markAsComplete(OrderLine orderLine, UserDTO userDTO, MaterialServices materialServices, WarehouseServices warehouseServices,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        return this.orderLineStatusDefaultOperations.markAsComplete(orderLine, userDTO, materialServices, warehouseServices, requestHeaderRepository,
                                                                    traceabilityRepository);
    }

    @Override
    public OrderLine cancel(OrderLine orderLine, ProcurementServices procurementServices, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, UserDTO userDTO) throws GloriaApplicationException {
        return this.orderLineStatusDefaultOperations.cancel(orderLine, procurementServices, requestHeaderRepository, traceabilityRepository, userDTO);
    }

    @Override
    public boolean isCancelled(OrderLine orderLine) {
        return this.orderLineStatusDefaultOperations.isCancelled(orderLine);
    }

    @Override
    public boolean isMarkedAsComplete(OrderLine orderLine) {
        return this.orderLineStatusDefaultOperations.isMarkedAsComplete(orderLine);
    }

    @Override
    public boolean isReceived(OrderLine orderLine) {
        return this.orderLineStatusDefaultOperations.isReceived(orderLine);
    }

    @Override
    public long getAdditionalQty(OrderMigrationDTO orderMigrationDTO) throws GloriaApplicationException {
        return this.orderLineStatusDefaultOperations.getAdditionalQty(orderMigrationDTO);
    }
    
    @Override
    public boolean isReceivedPartly(OrderLine orderLine) {
        return this.orderLineStatusDefaultOperations.isReceivedPartly(orderLine);
    }
}
