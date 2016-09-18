package com.volvo.gloria.procurematerial.d.entities.status.orderLine;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.b.WarehouseServices;

/**
 * behaviour for status placed.
 * 
 */
public class Placed extends OrderLineStatusDefaultOperations {

    @Override
    public OrderLine receive(OrderLine orderLine, DeliveryNoteLine deliveryNoteLine, long receivedQty) throws GloriaApplicationException {
        orderLine.setFirst(deliveryNoteLine);
        return OrderLineStatusHelper.receive(orderLine, receivedQty);
    }
    
    @Override
    public OrderLine markAsComplete(OrderLine orderLine, UserDTO userDTO, MaterialServices materialServices, WarehouseServices warehouseServices,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        return OrderLineStatusHelper.markAsComplete(orderLine, userDTO, materialServices, warehouseServices, requestHeaderRepository, traceabilityRepository);
    }
    
    @Override
    public OrderLine cancel(OrderLine orderLine, ProcurementServices procurementServices, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, UserDTO userDTO) throws GloriaApplicationException {
        return OrderLineStatusHelper.cancel(orderLine, procurementServices, requestHeaderRepository, traceabilityRepository, userDTO);
    }
    
    @Override
    public long getAdditionalQty(OrderMigrationDTO orderMigrationDTO) throws GloriaApplicationException {
       return OrderLineStatusHelper.findOverDeliveredQtyForPlacedReceivedPartly(orderMigrationDTO);
    }
}
