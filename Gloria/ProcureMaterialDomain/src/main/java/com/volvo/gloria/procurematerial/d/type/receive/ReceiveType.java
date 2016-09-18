package com.volvo.gloria.procurematerial.d.type.receive;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.DangerousGoodsRepository;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.DeliveryServices;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteDTO;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteLineDTO;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNote;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.b.WarehouseServices;

/**
 * ReceiveType possible types, supporting operations in behaviour classes.
 */
public enum ReceiveType implements ReceiveTypeOperations {

    REGULAR(new Regular()), 
    TRANSFER(new Transfer()), 
    RETURN(new Return()),
    RETURN_TRANSFER(new ReturnTransfer());


    private final ReceiveTypeOperations receiveTypeOperations;

    ReceiveType(ReceiveTypeOperations receiveTypeOperations) {
        this.receiveTypeOperations = receiveTypeOperations;
    }

    @Override
    public String nextLocation(DeliveryNoteLine deliveryNoteLine, boolean directSend, WarehouseServices warehouseServices,
            DeliveryServices deliveryServices) throws GloriaApplicationException {
        return receiveTypeOperations.nextLocation(deliveryNoteLine, directSend, warehouseServices, deliveryServices);
    }

    @Override
    public DeliveryNote createDeliveryInformation(DeliveryNoteDTO deliveryNote, DeliveryNoteRepository deliveryNoteRepository, OrderRepository orderRepository,
            DangerousGoodsRepository dangerousGoodsRepository, MaterialHeaderRepository requestHeaderRepository) throws GloriaApplicationException {
        return receiveTypeOperations.createDeliveryInformation(deliveryNote, deliveryNoteRepository, orderRepository, dangerousGoodsRepository,
                                                               requestHeaderRepository);
    }
    
    @Override
    public void receive(long deliveryNoteId, UserDTO user, DeliveryNoteRepository deliveryNoteRepository, OrderRepository orderRepository,
            MaterialHeaderRepository requestHeaderRepository, MaterialServices materialServices, CommonServices commonServices,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices) throws GloriaApplicationException {
        receiveTypeOperations.receive(deliveryNoteId, user, deliveryNoteRepository, orderRepository, requestHeaderRepository, materialServices, commonServices,
                                      traceabilityRepository, warehouseServices);
    }
    
    @Override
    public String whSite(RequestGroup requestGroup) throws GloriaApplicationException {
        return receiveTypeOperations.whSite(requestGroup);
    }

    public void setOrderOrMaterialInfo(DeliveryNoteLine deliveryNoteLine, DeliveryNoteLineDTO deliveryNoteLineDTO) {
        receiveTypeOperations.setOrderOrMaterialInfo(deliveryNoteLine, deliveryNoteLineDTO);
    }
}
