package com.volvo.gloria.procurematerial.d.type.material;

import java.util.Arrays;
import java.util.List;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.SupplierCounterPart;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.dto.MaterialDTO;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;


/**
 * Operations for USAGE_REPLACED material type.
 */
public class UsageReplaced extends MaterialTypeDefaultOperations {
    @Override
    public void revert(Material material, ProcureLine procureLine, MaterialHeaderRepository requestHeaderRepository, 
            ProcureLineRepository procureLineRepository, List<Material> materialsToBeRemoved,  UserDTO userDto, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
       MaterialTypeHelper.revertUsageReplacedMaterial(material, requestHeaderRepository, materialsToBeRemoved);
    }
    
    @Override
    public void cancelModification(Material material, MaterialHeaderRepository requestHeaderRepository,
            ProcureLine procureLine) throws GloriaApplicationException {
        MaterialTypeHelper.resetModifiedAttributes(material);
        requestHeaderRepository.updateMaterial(material);
    }
    
    @Override
    public boolean isAllowedForRequisitionQty(ProcureLine procureLine) {
        return false;
    }
    
    @Override
    public boolean isScrappable() {
        return false;
    }
    
    @Override
    public void setSupplierCounterPart(List<MaterialLine> materialLines, SupplierCounterPart supplierCounterPart) {
     // do nothing
    }  
    
    @Override
    public List<Material> cancelMaterial(Material material, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository,
            OrderLine orderLine, UserDTO userDTO) throws GloriaApplicationException {
        if (!orderLine.getStatus().isReceivedPartly(orderLine)) {
            material.setMaterialType(MaterialType.USAGE);
            material.setModificationId(0);
            material.setModificationType(null);
            material.setReplacedByOid(0);
            material.setOrderCancelled(true);
            material.getStatus().acceptAfterCancelOrder(material, traceabilityRepository);
            requestHeaderRepository.updateMaterial(material);
            return Arrays.asList(material);
        }
        return null;
    }
    
    @Override
    public String getChangeAction(Material material) {
        if (material != null && material.getProcureLine() != null) {
            return material.getProcureLine().getStatus().getUsageReplacedChangeAction(material);
        } else {
            return null;
        }
    }
    
    @Override
    public void acceptRemove(ProcureLine procureLine, ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs, Material removeMaterial,
            MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, OrderRepository orderRepository, UserServices userServices,
            ChangeId changeId, String userID, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        if (procureLine != null) {
            procureLine.getStatus().acceptRemoveUsageReplacedMaterial(procureLine, procureLineRepository, materialDTOs, removeMaterial, requestHeaderRepo,
                                                                      procurementServices, orderRepository, userServices, changeId, userID,
                                                                      traceabilityRepository);
        }
    }
}
