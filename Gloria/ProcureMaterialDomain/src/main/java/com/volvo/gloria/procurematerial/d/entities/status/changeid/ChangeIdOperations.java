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
 * Possible operations for all statuses.
 */
public interface ChangeIdOperations {
    void init(boolean procureLineExists, MaterialHeaderRepository requestHeaderRepo, MaterialProcureResponse materialProcureResponse, ChangeId changeId,
            String userId);

    void accept(ChangeId changeId, MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices,
            ProcureLineRepository procureLineRepository, MaterialProcureResponse materialProcureResponse, String userId, List<MaterialDTO> materialDTOs,
            OrderRepository orderRepository, UserServices userServices, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException;

    void reject(ChangeId changeId, MaterialHeaderRepository requestHeaderRepo, MaterialProcureResponse materialProcureResponse, String userId,
            ProcureLineRepository procureLineRepository, TraceabilityRepository traceabilityRepository, UserDTO userDTO) throws GloriaApplicationException;

    void cancel(ChangeId changeId, MaterialHeaderRepository requestHeaderRepository, MaterialProcureResponse materialProcureResponse, String userId,
            ProcureLineRepository procureLineRepository, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException;

    void setAcceptedRelationToMaterialHeader(ChangeId changeId, MaterialHeaderRepository requestHeaderRepo);

    boolean isCancelRejected();

    boolean isChangeInWait();

}
