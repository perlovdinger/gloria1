package com.volvo.gloria.procurematerial.d.type.receive;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.DangerousGoodsRepository;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.DeliveryServices;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteDTO;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNote;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.b.WarehouseServices;

/**
 * operation for type RETURN.
 * 
 */
public class Return extends ReceiveTypeDefaultOperations {
    @Override
    public String nextLocation(DeliveryNoteLine deliveryNoteLine, boolean directSend, WarehouseServices warehouseServices,
            DeliveryServices deliveryServices) throws GloriaApplicationException {
        return deliveryServices.suggestNextLocation(deliveryNoteLine.getDeliveryNote().getWhSiteId(), deliveryNoteLine.getPartNumber(),
                                                    deliveryNoteLine.getPartVersion(), directSend, 
                                                    deliveryNoteLine.getPartModification(), deliveryNoteLine.getPartAffiliation());
    }

    @Override
    public DeliveryNote createDeliveryInformation(DeliveryNoteDTO deliveryNoteDTO, DeliveryNoteRepository deliveryNoteRepository,
            OrderRepository orderRepository, DangerousGoodsRepository dangerousGoodsRepository, MaterialHeaderRepository requestHeaderRepository)
            throws GloriaApplicationException {
        DeliveryNote deliveryNote = ReceiveTypeHelper.createDeliveryNoteForTransferReturn(deliveryNoteDTO, deliveryNoteRepository, requestHeaderRepository);
        return ReceiveTypeHelper.createDeliveryNoteLinesForTransferReturn(deliveryNote, deliveryNoteRepository, requestHeaderRepository);
    }

    @Override
    public void receive(long deliveryNoteId, UserDTO user, DeliveryNoteRepository deliveryNoteRepository, OrderRepository orderRepository,
            MaterialHeaderRepository requestHeaderRepository, MaterialServices materialServices, CommonServices commonServices,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices) throws GloriaApplicationException {

        ReceiveTypeHelper.receiveTransferReturn(deliveryNoteId, user, deliveryNoteRepository, orderRepository, requestHeaderRepository, materialServices,
                                                traceabilityRepository, false, warehouseServices);
    }

    @Override
    public String whSite(RequestGroup requestGroup) throws GloriaApplicationException {
        if (requestGroup != null) {
            return requestGroup.getRequestList().getWhSiteId();
        }
        return null;
    }
}
