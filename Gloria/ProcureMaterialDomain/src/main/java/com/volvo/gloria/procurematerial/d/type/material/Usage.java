package com.volvo.gloria.procurematerial.d.type.material;

import java.util.List;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.dto.MaterialDTO;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatusHelper;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Operations for USAGE material type.
 */
public class Usage extends MaterialTypeDefaultOperations {

    @Override
    public void revert(Material material, ProcureLine procureLine, MaterialHeaderRepository requestHeaderRepository,
            ProcureLineRepository procureLineRepository, List<Material> materialsToBeRemoved, UserDTO userDto, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        MaterialTypeHelper.revertUsageMaterial(material, procureLine, requestHeaderRepository, procureLineRepository, materialsToBeRemoved, userDto,
                                               traceabilityRepository);
    }
    
    @Override
    public boolean isBorrowable() {
        return true;
    }
    
    @Override
    public void release(Material material, ProcureLine procureLine) throws GloriaApplicationException {
        MaterialTypeHelper.release(material);
    }
    
    @Override
    public boolean isModifiable() {
        return true;
    }
    
    @Override
    public boolean isReceiveble() {
        return true;
    }
    
    @Override
    public List<Material> cancelMaterial(Material material, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository,
            OrderLine orderLine, UserDTO userDTO) throws GloriaApplicationException {
        return MaterialTypeHelper.cancelUsageMaterial(material, requestHeaderRepository, traceabilityRepository, orderLine, userDTO);
    }
    
    @Override
    public String getChangeAction(Material material) {
        if (material != null && material.getProcureLine() != null) {
            return material.getProcureLine().getStatus().getUsageChangeAction(material);
        } else {
            return null;
        }
    }
    
    @Override
    public void acceptRemove(ProcureLine procureLine, ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs, Material removeMaterial,
            MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, OrderRepository orderRepository, UserServices userServices,
            ChangeId changeId, String userId, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        UserDTO userDto = userServices.getUser(userId);
        MaterialStatusHelper.removeMaterial(changeId.getMtrlRequestVersion(), removeMaterial, userDto, traceabilityRepository);
        if (procureLine != null) {
            procureLine.getStatus().acceptRemoveMaterial(procureLine, procureLineRepository, materialDTOs, removeMaterial, requestHeaderRepo,
                                                         procurementServices, orderRepository, userDto, traceabilityRepository);
        }
    }
}
