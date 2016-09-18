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
 * Default operations.
 */
public class ChangeIdDefaultOperations implements ChangeIdOperations {

    @Override
    public void init(boolean procureLineExists, MaterialHeaderRepository requestHeaderRepo, MaterialProcureResponse materialProcureResponse, ChangeId changeId,
            String userId) {
    }

    @Override
    public void accept(ChangeId changeId, MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices,
            ProcureLineRepository procureLineRepository, MaterialProcureResponse materialProcureResponse, String userId, List<MaterialDTO> materialDTOs,
            OrderRepository orderRepository, UserServices userServices, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
    }

    @Override
    public void reject(ChangeId changeId, MaterialHeaderRepository requestHeaderRepo, MaterialProcureResponse materialProcureResponse, String userId,
            ProcureLineRepository procureLineRepository, TraceabilityRepository traceabilityRepository, UserDTO userDTO) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public void cancel(ChangeId changeId, MaterialHeaderRepository requestHeaderRepository, MaterialProcureResponse materialProcureResponse, String userId,
            ProcureLineRepository procureLineRepository, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public void setAcceptedRelationToMaterialHeader(ChangeId changeId, MaterialHeaderRepository requestHeaderRepo) {
    }
    
    @Override
    public boolean isCancelRejected() {
        return false;
    }
    
    @Override
    public boolean isChangeInWait() {
        return false;
    }
}
