package com.volvo.gloria.procurematerial.d.entities.status.requestline;

import java.util.List;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.dto.MaterialDTO;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Operations for ADDED status.
 */
public class Added extends MaterialStatusDefaultOperations {
    @Override
    public void remove(ChangeId changeId, boolean procureLineExists, Material material, ProcureLineRepository procureLineRepository,
            TraceabilityRepository traceabilityRepository) {
        material.setRemove(changeId);
        changeId.getRemoveMaterials().add(material);
        if (procureLineExists) {
            ProcureLine procureLine = material.getProcureLine();
            if (procureLine != null) {
                procureLine.setNeedIsChanged(true);
                procureLineRepository.save(procureLine);
            }
            material.setStatus(MaterialStatus.REMOVE_MARKED);
        } else {
            MaterialStatusHelper.removeMaterial(changeId.getMtrlRequestVersion(), material, null, traceabilityRepository);
        }
    }

    @Override
    public boolean isAvailableForGrouping(Material material) {
        return true;
    }
    
    @Override
    public void needIsRemoved(ChangeId changeId, Material material, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        if (material.getProcureLine() != null) {
            material.setStatus(MaterialStatus.REMOVE_MARKED);
        } else {
            MaterialStatusHelper.removeMaterial(changeId.getMtrlRequestVersion(), material, null, traceabilityRepository);
        }
        requestHeaderRepository.updateMaterial(material);
    }
    

    @Override
    public void accept(MaterialHeaderRepository requestHeaderRepo, Material addMaterial, ChangeId changeId, ProcurementServices procurementServices,
            ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs, OrderRepository orderRepository, UserServices userServices,
            String userId, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
    }
}
