package com.volvo.gloria.procurematerial.d.entities.status.materialLine;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteSubLine;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.c.ZoneType;


public class Received extends MaterialLineDefaultOperation {    
    @Override
    public void qualityInspectMaterial(MaterialLine materialLine, MaterialServices materialServices,
            UserDTO user, TraceabilityRepository traceabilityRepository, DeliveryNoteSubLine deliveryNoteSubLine) throws GloriaApplicationException {
        MaterialLineStatusHelper.qualityInspectMaterial(materialLine, materialServices, user, traceabilityRepository, deliveryNoteSubLine);
    }
    
    @Override
    public boolean isRevertable() throws GloriaApplicationException {
        return true;
    }
    
    @Override
    public void placeIntoZone(MaterialLine materialLine, MaterialServices materialServices) throws GloriaApplicationException {
        materialServices.placeIntoZone(materialLine, ZoneType.TO_STORE);
    }
    
    @Override
    public MaterialLine scrap(MaterialLine materialLine, MaterialLineStatus scrapped2, long scrapQuantity,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, MaterialServices materialServices, UserDTO user)
            throws GloriaApplicationException {
        return MaterialLineStatusHelper.split(materialLine, scrapped2, scrapQuantity, requestHeaderRepository,
                                              traceabilityRepository, materialServices, user, null);
    }
    
    @Override
    public MaterialLine updateWithStatusTime(MaterialLine materialLine) {
        materialLine.getMaterialLineStatusTime().setReceivedTime(DateUtil.getUTCTimeStamp());
        return materialLine;
    }
}
