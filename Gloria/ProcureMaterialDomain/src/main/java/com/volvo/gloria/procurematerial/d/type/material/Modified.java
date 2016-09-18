package com.volvo.gloria.procurematerial.d.type.material;

import java.util.List;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineHelper;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Operations for MODIFIED material type.
 */
public class Modified extends MaterialTypeDefaultOperations {
    @Override
    public void revert(Material material, ProcureLine procureLine, MaterialHeaderRepository requestHeaderRepository,
            ProcureLineRepository procureLineRepository, List<Material> materialsToBeRemoved, UserDTO userDto, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        MaterialTypeHelper.revertModifiedMaterial(material, procureLine, requestHeaderRepository, procureLineRepository, materialsToBeRemoved);
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
    public void cancelModification(Material material, MaterialHeaderRepository requestHeaderRepository,
            ProcureLine procureLine) throws GloriaApplicationException {
        procureLine.getMaterials().remove(material);
        requestHeaderRepository.deleteMaterial(material);
    }
    
    @Override
    public boolean isReceiveble() {
        return true;
    }
    
    @Override
    public List<Material> cancelMaterial(Material material, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository,
            OrderLine orderLine, UserDTO userDTO) throws GloriaApplicationException {
        if (orderLine.getStatus().isReceivedPartly(orderLine)) {
            for (MaterialLine materialLine : material.getMaterialLine()) {
                materialLine.getStatus().cancel(materialLine, requestHeaderRepository, traceabilityRepository, userDTO);
            }
        } else {
            ProcureLineHelper.deAssociateMaterialFromProcureLine(material);
            orderLine.getMaterials().remove(material);
            requestHeaderRepository.deleteMaterial(material);
        }
        return null;
    }
}
