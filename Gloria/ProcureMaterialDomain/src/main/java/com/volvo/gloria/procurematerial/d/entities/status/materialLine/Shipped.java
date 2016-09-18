package com.volvo.gloria.procurematerial.d.entities.status.materialLine;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteSubLine;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.b.WarehouseServices;

public class Shipped extends MaterialLineDefaultOperation {
  
    @Override
    public MaterialLine transferReturn(DeliveryNoteLine deliveryNoteLine, DeliveryNoteSubLine deliveryNoteSubLine, MaterialLine materialLine,
            MaterialHeaderRepository requestHeaderRepository, UserDTO user, TraceabilityRepository traceabilityRepository, MaterialServices materialServices,
            Material transferMaterial, WarehouseServices warehouseServices) throws GloriaApplicationException {
        return MaterialLineStatusHelper.receiveTransferReturn(deliveryNoteLine, deliveryNoteSubLine, materialLine, requestHeaderRepository, user,
                                                              traceabilityRepository, materialServices, transferMaterial, warehouseServices);
    }
    
    @Override
    public MaterialLine updateWithStatusTime(MaterialLine materialLine) {
        materialLine.getMaterialLineStatusTime().setShippedTime(DateUtil.getUTCTimeStamp());
        return materialLine;
    }
}
