package com.volvo.gloria.procurematerial.d.entities.status.materialLine;

import java.util.ArrayList;
import java.util.List;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.b.WarehouseServices;

public class WaitToProcure extends MaterialLineDefaultOperation {
    
    @Override
    public void procure(MaterialLine materialLine, TraceabilityRepository traceabilityRepository, MaterialHeaderRepository requestHeaderRepository, UserDTO user)
            throws GloriaApplicationException {
        MaterialLineStatus procureMaterialLineStatus = MaterialLineStatusHelper.getProcureState(materialLine);
        List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
        if (!procureMaterialLineStatus.equals(MaterialLineStatus.ORDER_PLACED_INTERNAL)) {
            avoidTraceForMLStatus.add(procureMaterialLineStatus);
        }
        MaterialLineStatusHelper.merge(materialLine, procureMaterialLineStatus, requestHeaderRepository, traceabilityRepository, user, avoidTraceForMLStatus);
    }

    @Override
    public boolean isRevertable() {
        return true;
    }

    @Override
    public void borrow(MaterialLine borrowerMaterialLine, List<MaterialLine> borrowFromMaterialLines, boolean noReturn,
            MaterialHeaderRepository requestHeaderRepository, MaterialServices materialServices, TraceabilityRepository traceabilityRepository,
            WarehouseServices warehouseServices, OrderRepository orderRepository, UserDTO userDTO) throws GloriaApplicationException {
        MaterialLineStatusHelper.borrow(borrowerMaterialLine, borrowFromMaterialLines, requestHeaderRepository, noReturn, materialServices,
                                        traceabilityRepository, warehouseServices, orderRepository, userDTO);
    }
}
