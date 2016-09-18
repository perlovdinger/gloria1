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
import com.volvo.gloria.util.c.GloriaExceptionConstants;

/**
 * Operations for WAIT_CONFIRM status.
 */
public class WaitConfirm extends ChangeIdDefaultOperations {

    @Override
    public void accept(ChangeId changeId, MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices,
            ProcureLineRepository procureLineRepository, MaterialProcureResponse materialProcureResponse, 
            String userId, List<MaterialDTO> materialDTOs, OrderRepository orderRepository, 
            UserServices userServices, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        for (MaterialDTO materialDTO : materialDTOs) {
            if (materialDTO.getChangeAction() == null) {
                throw new GloriaApplicationException(GloriaExceptionConstants.CHANGE_ACTION_REQUIRED, "Action is mandatory");
            }
        }
        
        ChangeIdStatusHelper.setChangeIdStatus(changeId, requestHeaderRepo, materialProcureResponse, userId, ChangeIdStatus.ACCEPTED);
        ChangeIdStatusHelper.updateVersionAsAccepted(changeId, requestHeaderRepo);
        ChangeIdStatusHelper.acceptMaterials(requestHeaderRepo, changeId, procurementServices, procureLineRepository, 
                                             materialDTOs, orderRepository, userServices, userId, traceabilityRepository);
    }

    @Override
    public void reject(ChangeId changeId, MaterialHeaderRepository requestHeaderRepo, MaterialProcureResponse materialProcureResponse, String userId, 
            ProcureLineRepository procureLineRepository, TraceabilityRepository traceabilityRepository, UserDTO userDTO) throws GloriaApplicationException {
        ChangeIdStatusHelper.setChangeIdStatus(changeId, requestHeaderRepo, materialProcureResponse, userId, ChangeIdStatus.REJECTED);

        ChangeIdStatusHelper.rejectMaterials(changeId, requestHeaderRepo, traceabilityRepository, userDTO);
    }

    @Override
    public void cancel(ChangeId changeId, MaterialHeaderRepository requestHeaderRepository, MaterialProcureResponse materialProcureResponse, String userId,
            ProcureLineRepository procureLineRepository, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        ChangeIdStatusHelper.setChangeIdStatus(changeId, requestHeaderRepository, materialProcureResponse, userId, ChangeIdStatus.CANCEL_WAIT);
        ChangeIdStatusHelper.cancelMaterials(changeId, requestHeaderRepository, procureLineRepository, traceabilityRepository);
    }
    
    @Override
    public boolean isChangeInWait() {
        return true;
    }
}
