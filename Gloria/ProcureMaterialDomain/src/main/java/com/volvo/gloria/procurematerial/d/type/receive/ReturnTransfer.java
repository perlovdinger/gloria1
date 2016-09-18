package com.volvo.gloria.procurematerial.d.type.receive;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.b.WarehouseServices;

public class ReturnTransfer extends Return {
    @Override
    public void receive(long deliveryNoteId, UserDTO user, DeliveryNoteRepository deliveryNoteRepository, OrderRepository orderRepository,
            MaterialHeaderRepository requestHeaderRepository, MaterialServices materialServices, CommonServices commonServices,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices) throws GloriaApplicationException {

        ReceiveTypeHelper.receiveTransferReturn(deliveryNoteId, user, deliveryNoteRepository, orderRepository, requestHeaderRepository, materialServices,
                                                traceabilityRepository, true, warehouseServices);
    }

}
