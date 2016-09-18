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

/**
 * ChangeIdStatus possible statuses supporting operations in behaviour classes..
 */
public enum ChangeIdStatus implements ChangeIdOperations {

    START(new Start()), 
    WAIT_CONFIRM(new WaitConfirm()), 
    ACCEPTED(new Accepted()), 
    REJECTED(new Rejected()), 
    CANCEL_WAIT(new CancelWait()), 
    CANCELLED(new Cancelled()),
    CANCEL_REJECTED(new CancelRejected());

    private final ChangeIdOperations changeIdOperations;

    ChangeIdStatus(ChangeIdOperations changeIdOperations) {
        this.changeIdOperations = changeIdOperations;
    }

    @Override
    public void init(boolean procureLineExists, MaterialHeaderRepository requestHeaderRepo, MaterialProcureResponse materialProcureResponse, ChangeId changeId,
            String userId) {
        changeIdOperations.init(procureLineExists, requestHeaderRepo, materialProcureResponse, changeId, userId);
    }

    @Override
    public void accept(ChangeId changeId, MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices,
            ProcureLineRepository procureLineRepository, MaterialProcureResponse materialProcureResponse, String userId, 
            List<MaterialDTO> materialDTOs, OrderRepository orderRepository, UserServices userServices, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        changeIdOperations.accept(changeId, requestHeaderRepo, procurementServices, procureLineRepository, materialProcureResponse, 
                                  userId, materialDTOs, orderRepository, userServices, traceabilityRepository);
    }

    @Override
    public void reject(ChangeId changeId, MaterialHeaderRepository requestHeaderRepo, MaterialProcureResponse materialProcureResponse, String userId, 
            ProcureLineRepository procureLineRepository, TraceabilityRepository traceabilityRepository, UserDTO userDTO) throws GloriaApplicationException {
        changeIdOperations.reject(changeId, requestHeaderRepo, materialProcureResponse, userId, procureLineRepository, traceabilityRepository, userDTO);
    }

    @Override
    public void cancel(ChangeId changeId, MaterialHeaderRepository requestHeaderRepository, MaterialProcureResponse materialProcureResponse, String userId,
            ProcureLineRepository procureLineRepository, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        changeIdOperations.cancel(changeId, requestHeaderRepository, materialProcureResponse, userId, procureLineRepository, traceabilityRepository);

    }

    @Override
    public void setAcceptedRelationToMaterialHeader(ChangeId changeId, MaterialHeaderRepository requestHeaderRepo) {
        changeIdOperations.setAcceptedRelationToMaterialHeader(changeId, requestHeaderRepo);
    }
    
    @Override
    public boolean isCancelRejected() {
        return changeIdOperations.isCancelRejected();
    }
    
    @Override
    public boolean isChangeInWait() {
        return changeIdOperations.isChangeInWait();
    }
}
