package com.volvo.gloria.procurematerial.d.entities.status.requestline;

import java.util.List;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.dto.MaterialDTO;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureTypeHelper;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Operations for REMOVE_MARKED status.
 */
public class RemoveMarked extends MaterialStatusDefaultOperations {

    @Override
    public void accept(MaterialHeaderRepository requestHeaderRepo, Material removeMaterial, ChangeId changeId, ProcurementServices procurementServices,
            ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs, OrderRepository orderRepository, UserServices userServices,
            String userId, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        ProcureLine procureLine = removeMaterial.getProcureLine();
        removeMaterial.getMaterialType().acceptRemove(procureLine, procureLineRepository, materialDTOs, removeMaterial, requestHeaderRepo, procurementServices,
                                                      orderRepository, userServices, changeId, userId, traceabilityRepository);
        requestHeaderRepo.updateMaterial(removeMaterial);
    }

    @Override
    public void reject(Material material, MaterialHeaderRepository requestHeaderRepo, TraceabilityRepository traceabilityRepository, UserDTO userDTO)
            throws GloriaApplicationException {
        material.setRejectChangeStatus(material.getStatus());
        material.setStatus(MaterialStatus.ADDED);
        
        ProcureLine procureLine = material.getProcureLine();
        if (procureLine != null) {
            procureLine.setNeedIsChanged(false);
        }
        requestHeaderRepo.updateMaterial(material);
    }
    
    @Override
    public void needIsRemoved(ChangeId changeId, Material material, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        MaterialStatusHelper.removeMaterial(changeId.getMtrlRequestVersion(), material, null, traceabilityRepository);
        requestHeaderRepository.updateMaterial(material);
    }
    
    @Override
    public void remove(ChangeId changeId, boolean procureLineExists, Material material, ProcureLineRepository procureLineRepository,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
    }
    
    @Override
    public String getChangeAction(Material material) {
        return material.getMaterialType().getChangeAction(material);
    }
    
   @Override
    public void acceptAfterCancelOrder(Material material, TraceabilityRepository traceabilityRepository) {
       MaterialStatusHelper.removeMaterial(material.getRemove().getMtrlRequestVersion(), material, null, traceabilityRepository);
       ProcureTypeHelper.disconnectMaterialFromProcureline(material);
    } 
   
    @Override
    public boolean isAvailableForGrouping(Material material) {
        if (material.isOrderCancelled()) {
            return true;
        }
        return false;
    }
}
