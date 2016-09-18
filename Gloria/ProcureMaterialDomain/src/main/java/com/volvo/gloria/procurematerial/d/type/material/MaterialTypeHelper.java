package com.volvo.gloria.procurematerial.d.type.material;

import java.util.Arrays;
import java.util.List;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.SupplierCounterPart;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusHelper;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineHelper;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;

public final class MaterialTypeHelper {

    private MaterialTypeHelper() {
    }

    public static void revertUsageMaterial(Material material, ProcureLine procureLine, MaterialHeaderRepository requestHeaderRepository,
            ProcureLineRepository procureLineRepository, List<Material> materialsToBeRemoved, UserDTO userDto, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        if (isMaterialInStateProcured(material)) {
            List<MaterialLine> materialLines = material.getMaterialLine();
            for (int idx = materialLines.size() - 1; idx >= 0; idx--) {
                MaterialLine materialLine = materialLines.get(idx);
                if (materialLine.getProcureType() != null && materialLine.getProcureType().isFromStock()) {
                    materialLine.getProcureType().revertMaterialLine(materialLine, requestHeaderRepository, userDto, traceabilityRepository);
                }
            }
            // After reverting the from stock material lines only one MaterialLine is referenced to the material always,
            // hence changing the quantity of this material line. If not create one new materialLine.
            if (material.getMaterialLine() != null && !material.getMaterialLine().isEmpty()) {
                MaterialLine materialLine = material.getMaterialLine().get(0);
                materialLine.setQuantity(material.getQuantity());
                materialLine.setOrderNo(null);
                materialLine.setStatus(MaterialLineStatus.WAIT_TO_PROCURE);
                materialLine.setStatusDate(DateUtil.getCurrentUTCDateTime());
                materialLine.setProcureType(null);
            } else {
                createMaterialLine(material, requestHeaderRepository);
            }
            requestHeaderRepository.updateMaterial(material);
        }
    }

    private static void createMaterialLine(Material material, MaterialHeaderRepository requestHeaderRepository) throws GloriaApplicationException {
        MaterialLine materialLine = new MaterialLine();
        materialLine.setQuantity(material.getQuantity());
        materialLine.setStatus(MaterialLineStatus.WAIT_TO_PROCURE);
        materialLine.setStatusDate(DateUtil.getCurrentUTCDateTime());
        MaterialLineStatusHelper.assignMaterialLineToMaterial(material, materialLine);
        requestHeaderRepository.addMaterialLine(materialLine);
    }

    public static void revertAdditionalMaterial(Material material, ProcureLine procureLine, MaterialHeaderRepository requestHeaderRepository,
            ProcureLineRepository procureLineRepository, List<Material> materialsToBeRemoved, UserDTO userDto, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        procureLine.setAdditionalQuantity(0L);
        if (isMaterialInStateProcured(material)) {
            List<MaterialLine> materialLines = material.getMaterialLine();
            for (int idx = materialLines.size() - 1; idx >= 0; idx--) {
                MaterialLine materialLine = materialLines.get(idx);
                if (materialLine.getProcureType().isFromStock()) {
                    materialLine.getProcureType().revertMaterialLine(materialLine, requestHeaderRepository, userDto, traceabilityRepository);
                }
            }
            materialsToBeRemoved.add(material);
        }
    }
    
    public static void revertReleasedMaterial(Material material, MaterialHeaderRepository requestHeaderRepository,
            List<Material> materialsToBeRemoved) throws GloriaApplicationException {
        if (isMaterialInStateProcured(material)) {
            for (MaterialLine materialLine : material.getMaterialLine()) {
                materialLine.setStatus(MaterialLineStatus.WAIT_TO_PROCURE);
                materialLine.setOrderNo(null);
                requestHeaderRepository.updateMaterialLine(materialLine);
            }
        }
    }

    public static void revertModifiedMaterial(Material material, ProcureLine procureLine, MaterialHeaderRepository requestHeaderRepository,
            ProcureLineRepository procureLineRepository, List<Material> materialsToBeRemoved) throws GloriaApplicationException {
        
        // remove IP related information for MODIFIED
        procureLine.setForwardedUserId(null);
        procureLine.setForwardedUserName(null);
        procureLine.setForwardedTeam(null);
        procureLine.setForwardedForDC(false);

        if (isMaterialInStateProcured(material)) {
            materialsToBeRemoved.add(material);
        }
    }

    private static boolean isMaterialInStateProcured(Material material) throws GloriaApplicationException {
        List<MaterialLine> materialLines = material.getMaterialLine();
        if (materialLines != null && !materialLines.isEmpty()) {
            for (MaterialLine materialLine : materialLines) {
                if (!materialLine.getStatus().isRevertable()) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void release(Material material) throws GloriaApplicationException {
        material.setMaterialType(MaterialType.RELEASED);
    }

    public static void revertUsageReplacedMaterial(Material material, MaterialHeaderRepository requestHeaderRepository,
            List<Material> materialsToBeRemoved) throws GloriaApplicationException {
        if (isMaterialInStateProcured(material)) {
            material.setMaterialType(MaterialType.USAGE);
            material.setModificationId(0);
            material.setModificationType(null);
            material.setReplacedByOid(0);
            requestHeaderRepository.updateMaterial(material);
        }
    }

    public static void setSupplierCounterPart(List<MaterialLine> materialLines, SupplierCounterPart supplierCounterPart) {
        if (materialLines != null && !materialLines.isEmpty()) {
            for (MaterialLine materialLine : materialLines) {
                if (supplierCounterPart != null) {
                    materialLine.setWhSiteId(supplierCounterPart.getShipToId());
                    if (!supplierCounterPart.getShipToId().equals(materialLine.getFinalWhSiteId())
                            && !materialLine.getMaterial().getMaterialType().isAdditional()) {
                        materialLine.setDirectSend(DirectSendType.YES_TRANSFER);
                    } else {
                        materialLine.setDirectSend(DirectSendType.NO);
                    }
                }
            }
        }
    }
    
    /**
     *  to handle material on Order Cancellation. 
     *  
     *  for ADDITIONAL and RELEASED.
     * 
     * @param material
     * @param requestHeaderRepository
     * @return
     * @throws GloriaApplicationException 
     */
    public static void handleMaterialOnOrderCancel(Material material, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, OrderLine orderLine,  UserDTO userDTO) throws GloriaApplicationException {
        List<MaterialLine> materialLines = material.getMaterialLine();
        if (materialLines != null && !materialLines.isEmpty()) {
            for (int idx = materialLines.size() - 1; idx >= 0; idx--) {
                MaterialLine materialLine = materialLines.get(idx);
                materialLine.getStatus().cancel(materialLine, requestHeaderRepository, traceabilityRepository, userDTO);
            }
        }

        if (material.getMaterialLine().isEmpty()) {
            material.getProcureLine().getMaterials().remove(material);
            orderLine.getMaterials().remove(material);
            requestHeaderRepository.deleteMaterial(material);
        }
    }
    
    public static List<Material> cancelUsageMaterial(Material material, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, OrderLine orderLine,  UserDTO userDTO) throws GloriaApplicationException {
        boolean hasMaterialsToReceive = false;
        List<MaterialLine> materialLines = material.getMaterialLine();
        for (MaterialLine materialLine : materialLines) {
            if (materialLine.getStatus().isReceiveble()) {
                hasMaterialsToReceive = true;
                break;
            }
        }

        if (hasMaterialsToReceive) {
            orderLine.getMaterials().remove(material);
            material.getProcureLine().getMaterials().remove(material);
            material.setOrderLine(null);
            material.setOrderNo(null);
            material.setOrderCancelled(true);
            ProcureLineHelper.deAssociateMaterialFromProcureLine(material);
            for (int i = materialLines.size() - 1; i >= 0; i--) {
                MaterialLine usageMaterialLine = materialLines.get(i);
                usageMaterialLine.getStatus().cancel(usageMaterialLine, requestHeaderRepository, traceabilityRepository, userDTO);
            }
            return Arrays.asList(material);
        }
        return null;
    }
    
    public static void resetModifiedAttributes(Material removeMaterial) {
        removeMaterial.setMaterialType(MaterialType.USAGE);
        removeMaterial.setModificationId(0);
        removeMaterial.setModificationType(null);
        removeMaterial.setReplacedByOid(0);
    }
}
