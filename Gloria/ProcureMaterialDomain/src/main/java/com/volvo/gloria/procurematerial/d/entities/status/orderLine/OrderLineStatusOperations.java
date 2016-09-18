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
 * possible operation for all statuses.
 * 
 */
public interface OrderLineStatusOperations {

    OrderLine revoke(OrderLine orderLine, long quantityCancelled, OrderRepository orderRepository) throws GloriaApplicationException;

    OrderLine receive(OrderLine orderLine, DeliveryNoteLine deliveryNoteLine, long receivedQty) throws GloriaApplicationException;
    
    OrderLine markAsComplete(OrderLine orderLine, UserDTO userDTO, MaterialServices materialServices, WarehouseServices warehouseServices,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException;

    OrderLine cancel(OrderLine orderLine, ProcurementServices procurementServices, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, UserDTO userDTO) throws GloriaApplicationException;

    boolean isMarkedAsComplete(OrderLine orderLine);

    boolean isCancelled(OrderLine orderLine);

    boolean isReceived(OrderLine orderLine);
    
    boolean isReceivedPartly(OrderLine orderLine);

    long getAdditionalQty(OrderMigrationDTO orderMigrationDTO) throws GloriaApplicationException;    
}
