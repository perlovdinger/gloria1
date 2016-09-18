package com.volvo.gloria.procurematerial.d.entities.status.requestline;

import java.util.List;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.dto.MaterialDTO;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Possible operations for all statuses.
 */
public interface MaterialStatusOperations {
    
    void init(boolean procureLineExists, Material requestLine) throws GloriaApplicationException;
    
    void accept(MaterialHeaderRepository requestHeaderRepo, Material material, ChangeId changeId, ProcurementServices procurementServices,
            ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs, OrderRepository orderRepository, UserServices userServices,
            String userId, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException;

    void reject(Material requestLine, MaterialHeaderRepository requestHeaderRepo, TraceabilityRepository traceabilityRepository, UserDTO userDTO)
            throws GloriaApplicationException;

    void remove(ChangeId changeId, boolean procureLineExists, Material requestLine, ProcureLineRepository procureLineRepository,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException;
    
    boolean isAvailableForGrouping(Material material);

    void needIsRemoved(ChangeId changeId, Material material, MaterialHeaderRepository requestHeaderRepository, 
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException;

    String getChangeAction(Material material);

    void acceptAfterCancelOrder(Material material, TraceabilityRepository traceabilityRepository);
}
