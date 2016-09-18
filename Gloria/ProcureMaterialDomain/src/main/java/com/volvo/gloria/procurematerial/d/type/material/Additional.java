package com.volvo.gloria.procurematerial.d.type.material;

import java.util.List;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Operations for ADDITIONAL material type.
 */
public class Additional extends MaterialTypeDefaultOperations {

    @Override
    public void revert(Material material, ProcureLine procureLine, MaterialHeaderRepository requestHeaderRepository,
            ProcureLineRepository procureLineRepository, List<Material> materialsToBeRemoved, UserDTO userDto, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        MaterialTypeHelper.revertAdditionalMaterial(material, procureLine, requestHeaderRepository, procureLineRepository, materialsToBeRemoved, userDto,
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
    public boolean isAdditional() {
        return true;
    }    
    
    @Override
    public boolean isReceiveble() {
        return true;
    }

    @Override
    public List<Material> cancelMaterial(Material material, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository,
            OrderLine orderLine, UserDTO userDTO) throws GloriaApplicationException {
        MaterialTypeHelper.handleMaterialOnOrderCancel(material, requestHeaderRepository, traceabilityRepository, orderLine, userDTO);
        return null;
    }
    
    @Override
    public void cancelModification(Material material, MaterialHeaderRepository requestHeaderRepository, ProcureLine procureLine)
            throws GloriaApplicationException {
        procureLine.getMaterials().remove(material);
        requestHeaderRepository.deleteMaterial(material);
    }
}
