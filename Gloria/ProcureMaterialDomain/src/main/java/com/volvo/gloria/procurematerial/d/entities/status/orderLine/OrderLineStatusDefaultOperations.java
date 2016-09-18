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
 * 
 * possible operations for all statuses.
 */
public class OrderLineStatusDefaultOperations implements OrderLineStatusOperations {

    public static final String EXCEPTION_MESSAGE = "This operation is not supported in current state.";

    @Override
    public OrderLine revoke(OrderLine orderLine, long quantityCancelled, OrderRepository orderRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public OrderLine receive(OrderLine orderLine, DeliveryNoteLine deliveryNoteLine, long receivedQty) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public OrderLine markAsComplete(OrderLine orderLine, UserDTO userDTO, MaterialServices materialServices, WarehouseServices warehouseServices,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public OrderLine cancel(OrderLine orderLine, ProcurementServices procurementServices, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, UserDTO userDTO) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public boolean isCancelled(OrderLine orderLine) {
        return false;
    }

    @Override
    public boolean isMarkedAsComplete(OrderLine orderLine) {
        return false;
    }

    @Override
    public boolean isReceived(OrderLine orderLine) {
        return false;
    }

    @Override
    public boolean isReceivedPartly(OrderLine orderLine) {
        return false;
    }
    
    @Override
    public long getAdditionalQty(OrderMigrationDTO orderMigrationDTO) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());
    }
}
