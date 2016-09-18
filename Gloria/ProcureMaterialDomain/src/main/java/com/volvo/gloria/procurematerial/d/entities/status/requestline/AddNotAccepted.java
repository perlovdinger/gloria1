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
 * Operations for ADD_NOT_ACCEPTED status.
 */
public class AddNotAccepted extends MaterialStatusDefaultOperations {
    
    @Override
    public void accept(MaterialHeaderRepository requestHeaderRepo, Material addMaterial, ChangeId changeId, ProcurementServices procurementServices,
            ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs, OrderRepository orderRepository, UserServices userServices,
            String userId, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        addMaterial.setStatus(MaterialStatus.ADDED);
        addMaterial.setMtrlRequestVersionAccepted(changeId.getMtrlRequestVersion());
        MaterialStatusHelper.regroupMaterials(requestHeaderRepo, addMaterial, procurementServices, userServices, userId, traceabilityRepository);
    }

    @Override
    public void reject(Material material, MaterialHeaderRepository requestHeaderRepo, TraceabilityRepository traceabilityRepository, UserDTO userDTO)
            throws GloriaApplicationException {
        material.setRejectChangeStatus(material.getStatus());
        MaterialStatusHelper.removeMaterial(material.getMtrlRequestVersionAccepted(), material, userDTO, traceabilityRepository);
        requestHeaderRepo.updateMaterial(material);
    }
    
    @Override
    public void needIsRemoved(ChangeId changeId, Material material, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        MaterialStatusHelper.removeMaterial(changeId.getMtrlRequestVersion(), material, null, traceabilityRepository);
        requestHeaderRepository.updateMaterial(material);
    }
    
    @Override
    public String getChangeAction(Material material) {
        return "ADD";
    }
}
