package com.volvo.gloria.procurematerial.d.entities.status.materialLine;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.b.beans.MaterialServicesHelper;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;

public class QiOk extends MaterialLineDefaultOperation {        
    @Override
    public boolean isRevertable() throws GloriaApplicationException {
        return true;
    }
    
    @Override
    public MaterialLine release(MaterialLineDTO materialLineDTO, ProcurementServices procurementServices, MaterialHeaderRepository requestHeaderRepository)
            throws GloriaApplicationException {
        return MaterialServicesHelper.releaseGoods(materialLineDTO, procurementServices, requestHeaderRepository);
    }
    
    @Override
    public MaterialLine scrap(MaterialLine materialLine, MaterialLineStatus scrapped2, long scrapQuantity,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, MaterialServices materialServices, UserDTO user)
            throws GloriaApplicationException {
        return MaterialLineStatusHelper.split(materialLine, scrapped2, scrapQuantity, requestHeaderRepository,
                                              traceabilityRepository, materialServices, user, null);
    }
    
    @Override
    public MaterialLine setInspectionStatus(MaterialLine materialLine) {
        InspectionStatus currentInspectionStatus = materialLine.getInspectionStatus();
        if (currentInspectionStatus != null) {
            materialLine.setInspectionStatus(InspectionStatus.OK);
        }
        return materialLine;
    }

}
