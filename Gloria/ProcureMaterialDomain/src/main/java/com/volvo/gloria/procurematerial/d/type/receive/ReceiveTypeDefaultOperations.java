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
 * Default behaviour for receive types.
 * 
 */
public class ReceiveTypeDefaultOperations implements ReceiveTypeOperations {
    
    @Override
    public String nextLocation(DeliveryNoteLine deliveryNoteLine, boolean directSend, WarehouseServices warehouseServices,
            DeliveryServices deliveryServices)
            throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }
    
    @Override
    public DeliveryNote createDeliveryInformation(DeliveryNoteDTO deliveryNote, DeliveryNoteRepository deliveryNoteRepository,
            OrderRepository orderRepository, DangerousGoodsRepository dangerousGoodsRepository, MaterialHeaderRepository requestHeaderRepository)
            throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }
    
    @Override
    public void receive(long deliveryNoteId, UserDTO user, DeliveryNoteRepository deliveryNoteRepository, OrderRepository orderRepository,
            MaterialHeaderRepository requestHeaderRepository, MaterialServices materialServices, CommonServices commonservices,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public String whSite(RequestGroup requestGroup) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public void setOrderOrMaterialInfo(DeliveryNoteLine deliveryNoteLine, DeliveryNoteLineDTO deliveryNoteLineDTO) {
        deliveryNoteLineDTO.setPartAffiliation(deliveryNoteLine.getPartAffiliation());
        deliveryNoteLineDTO.setPartNumber(deliveryNoteLine.getPartNumber());
        deliveryNoteLineDTO.setPartName(deliveryNoteLine.getPartName());
        deliveryNoteLineDTO.setPartVersion(deliveryNoteLine.getPartVersion());
        deliveryNoteLineDTO.setPartAlias(deliveryNoteLine.getPartAlias());
        deliveryNoteLineDTO.setReferenceId(deliveryNoteLine.getReferenceIds());
        deliveryNoteLineDTO.setPossibleToReceiveQuantity(deliveryNoteLine.getPossibleToReceiveQty());
    }
}
