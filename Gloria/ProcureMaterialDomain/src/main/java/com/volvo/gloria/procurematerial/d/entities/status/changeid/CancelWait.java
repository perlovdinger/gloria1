package com.volvo.gloria.procurematerial.d.entities.status.changeid;

import java.util.List;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.materialRequestProxy.b.MaterialProcureResponse;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.dto.MaterialDTO;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;

public class CancelWait extends ChangeIdDefaultOperations {
    @Override
    public void accept(ChangeId changeId, MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices,
            ProcureLineRepository procureLineRepository, MaterialProcureResponse materialProcureResponse, String userId, List<MaterialDTO> materialDTOs,
            OrderRepository orderRepository, UserServices userServices, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        ChangeIdStatusHelper.setChangeIdStatus(changeId, requestHeaderRepository, materialProcureResponse, userId, ChangeIdStatus.CANCELLED);
        ChangeIdStatusHelper.acceptMaterials(requestHeaderRepository, changeId, procurementServices, 
                                             procureLineRepository, materialDTOs, orderRepository, userServices, userId, traceabilityRepository);
        ChangeIdStatusHelper.inactivateHeader(changeId);
    }

    @Override
    public void reject(ChangeId changeId, MaterialHeaderRepository requestHeaderRepository, MaterialProcureResponse materialProcureResponse, String userId,
            ProcureLineRepository procureLineRepository, TraceabilityRepository traceabilityRepository, UserDTO userDTO) throws GloriaApplicationException {
        ChangeIdStatusHelper.setChangeIdStatus(changeId, requestHeaderRepository, materialProcureResponse, userId, ChangeIdStatus.CANCEL_REJECTED);
        ChangeIdStatusHelper.rejectMaterials(changeId, requestHeaderRepository, traceabilityRepository, userDTO);
        ChangeIdStatusHelper.updateNeedIsChanged(changeId, procureLineRepository, false);
    }
    
    @Override
    public boolean isChangeInWait() {
        return true;
    }
}
