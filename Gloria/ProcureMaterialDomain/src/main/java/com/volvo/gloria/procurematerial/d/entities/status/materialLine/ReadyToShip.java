package com.volvo.gloria.procurematerial.d.entities.status.materialLine;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.c.dto.DispatchNoteDTO;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptLine;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.b.WarehouseServices;



public class ReadyToShip extends MaterialLineDefaultOperation {    
    @Override
    public boolean isShippable() {
        return true;
    }
    
    @Override
    public boolean isCancelGoodsReceiptAllowed(MaterialLine materialLine) {
        if (materialLine.getDirectSend().equals(DirectSendType.YES_TRANSFER)) {
            return true;
        }
        return false;
    }
    
    @Override
    public void cancelReceivedMaterialLine(long qtyCancelled, DeliveryNoteLine deliveryNoteLine, GoodsReceiptLine goodsReceiptLine, MaterialLine materialLine,
            MaterialServices materialServices, WarehouseServices warehouseServices, DeliveryNoteRepository deliveryNoteRepository,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, UserDTO userDTO) throws GloriaApplicationException {
        if (this.isCancelGoodsReceiptAllowed(materialLine)) {

            MaterialLineStatusHelper.revertMaterialLineToOrderPlaced(qtyCancelled, materialLine, deliveryNoteLine, goodsReceiptLine, userDTO, materialServices,
                                                                     warehouseServices, requestHeaderRepository, traceabilityRepository);
        } else {
            super.cancelReceivedMaterialLine(qtyCancelled, deliveryNoteLine, goodsReceiptLine, materialLine, materialServices, warehouseServices,
                                             deliveryNoteRepository, requestHeaderRepository, traceabilityRepository, userDTO);
        }
    }
    @Override
    public void shipOrTransfer(MaterialLine materialLine, MaterialServices materialServices, String loggedInUserId) throws GloriaApplicationException {
        DispatchNoteDTO dispatchNoteDTO = new DispatchNoteDTO();
        RequestList requestList = materialLine.getRequestGroup().getRequestList();
        materialServices.createDispatchNoteforRequestList(requestList.getRequestListOid(), dispatchNoteDTO, "markAsShipped", loggedInUserId);
    }
    
    @Override
    public boolean isPicked() {
        return true;
    }

}
