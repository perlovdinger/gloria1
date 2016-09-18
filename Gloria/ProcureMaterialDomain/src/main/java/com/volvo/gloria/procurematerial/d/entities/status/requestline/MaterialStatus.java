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
 * ChangeIdStatus possible statuses supporting operations in behaviour classes..
 */
public enum MaterialStatus implements MaterialStatusOperations {
    START(new Start()), 
    ADD_NOT_ACCEPTED(new AddNotAccepted()), 
    ADDED(new Added()), 
    REMOVE_MARKED(new RemoveMarked()), 
    REMOVED(new Removed());

    private final MaterialStatusOperations materialOperations;
    
    MaterialStatus(MaterialStatusOperations changeIdOperations) {
        this.materialOperations = changeIdOperations;
    }

    @Override
    public void init(boolean procureLineExists, Material material) throws GloriaApplicationException {
        materialOperations.init(procureLineExists, material);
    }

    @Override
    public void accept(MaterialHeaderRepository requestHeaderRepo, Material material, ChangeId changeId, ProcurementServices procurementServices,
            ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs, OrderRepository orderRepository, UserServices userServices,
            String userId, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        materialOperations.accept(requestHeaderRepo, material, changeId, procurementServices, procureLineRepository, materialDTOs, orderRepository,
                                  userServices, userId, traceabilityRepository);
    }

    @Override
    public void reject(Material material, MaterialHeaderRepository requestHeaderRepo, TraceabilityRepository traceabilityRepository, UserDTO userDTO)
            throws GloriaApplicationException {
        materialOperations.reject(material, requestHeaderRepo, traceabilityRepository, userDTO);
    }

    @Override
    public void remove(ChangeId changeId, boolean procureLineExists, Material material, ProcureLineRepository procureLineRepository,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        materialOperations.remove(changeId, procureLineExists, material, procureLineRepository, traceabilityRepository);
    }

    @Override
    public boolean isAvailableForGrouping(Material material) {
        return materialOperations.isAvailableForGrouping(material);
    }

    @Override
    public void needIsRemoved(ChangeId changeId, Material material, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        materialOperations.needIsRemoved(changeId, material, requestHeaderRepository, traceabilityRepository);
    }

    @Override
    public String getChangeAction(Material material) {
        return materialOperations.getChangeAction(material);
    }

    public void acceptAfterCancelOrder(Material material, TraceabilityRepository traceabilityRepository) {
        materialOperations.acceptAfterCancelOrder(material, traceabilityRepository);        
    }
}
