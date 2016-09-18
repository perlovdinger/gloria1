package com.volvo.gloria.procurematerial.d.entities.status.orderLine;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.CompleteType;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * behaviour for status Completed.
 * 
 */
public class Completed extends OrderLineStatusDefaultOperations {

    @Override
    public boolean isMarkedAsComplete(OrderLine orderLine) {
        return orderLine.getCompleteType() != null && orderLine.getCompleteType() == CompleteType.COMPLETE;
    }

    @Override
    public boolean isCancelled(OrderLine orderLine) {
        return orderLine.getCompleteType() != null && orderLine.getCompleteType() == CompleteType.CANCELLED;
    }

    @Override
    public boolean isReceived(OrderLine orderLine) {
        return orderLine.getCompleteType() != null && orderLine.getCompleteType() == CompleteType.RECEIVED;
    }

    @Override
    public OrderLine revoke(OrderLine orderLine, long quantityCancelled, OrderRepository orderRepository) throws GloriaApplicationException {
        return OrderLineStatusHelper.revoke(orderLine, quantityCancelled, orderRepository);
    }
    
    @Override
    public long getAdditionalQty(OrderMigrationDTO orderMigrationDTO) throws GloriaApplicationException {
        if (orderMigrationDTO.getReceivedQuantity() > orderMigrationDTO.getTotalTestobjectsQty()) {
            return orderMigrationDTO.getReceivedQuantity() - orderMigrationDTO.getTotalTestobjectsQty();
        }
       return 0;
    }
    
    @Override
    public OrderLine cancel(OrderLine orderLine, ProcurementServices procurementServices, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, UserDTO userDTO) throws GloriaApplicationException {
        orderLine.setCompleteType(CompleteType.CANCELLED);
        orderLine.setStatus(OrderLineStatus.COMPLETED);
        return orderLine;
    }
}
