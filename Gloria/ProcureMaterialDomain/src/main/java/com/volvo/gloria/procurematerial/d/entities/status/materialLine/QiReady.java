package com.volvo.gloria.procurematerial.d.entities.status.materialLine;

import java.util.ArrayList;
import java.util.List;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.tracing.c.GloriaTraceabilityConstants;
import com.volvo.gloria.warehouse.b.WarehouseServices;

public class QiReady extends MaterialLineDefaultOperation {    
    @Override
    public boolean isBorrowable() {
        return true;
    }
    
    @Override
    public MaterialLine approveQI(long approvedQuantity, UserDTO user, MaterialLine materialLine, MaterialServices materialServices,
            TraceabilityRepository traceabilityRepository, MaterialHeaderRepository requestHeaderRepository, CommonServices commonServices, boolean isStore,
            WarehouseServices warehouseServices) throws GloriaApplicationException {
        List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
        avoidTraceForMLStatus.add(MaterialLineStatus.QI_OK);
        MaterialLineStatusHelper.merge(materialLine, MaterialLineStatus.QI_OK, approvedQuantity, requestHeaderRepository, traceabilityRepository, user,
                                       avoidTraceForMLStatus);
        MaterialLine approvedMaterialLine = MaterialLineStatusHelper.doShipOrStoreMaterial(user, materialLine, materialServices, traceabilityRepository,
                                                                                           requestHeaderRepository, commonServices, isStore, warehouseServices);
        MaterialLineStatusHelper.createTraceabilityLog(approvedMaterialLine, traceabilityRepository, MaterialLineStatus.QI_OK.name(), null, user.getId(),
                                                       user.getUserName(), GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE);
        return approvedMaterialLine;
    }
     
    @Override
    public void quarantine(long blockedQuantity, UserDTO user, MaterialLine materialLine, MaterialServices materialServices,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices) throws GloriaApplicationException {
        MaterialLineStatusHelper.quarantine(blockedQuantity, user, materialLine, materialServices, traceabilityRepository, warehouseServices);
    }
    
    @Override
    public boolean isRevertable() throws GloriaApplicationException {
        return true;
    }
    
    @Override
    public boolean isReadyForQIApprove() {
        return true;
    }
    
    @Override
    public MaterialLine scrap(MaterialLine materialLine, MaterialLineStatus scrapped2, long scrapQuantity,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, MaterialServices materialServices, UserDTO user)
            throws GloriaApplicationException {
        return MaterialLineStatusHelper.split(materialLine, scrapped2, scrapQuantity, requestHeaderRepository,
                                              traceabilityRepository, materialServices, user, null);
    }    
}
