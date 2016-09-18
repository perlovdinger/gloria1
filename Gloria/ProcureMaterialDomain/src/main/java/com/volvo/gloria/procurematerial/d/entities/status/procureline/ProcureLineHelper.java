package com.volvo.gloria.procurematerial.d.entities.status.procureline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.SupplierCounterPart;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.ProcureLineProgressStatusFlag;
import com.volvo.gloria.procurematerial.c.ProcureResponsibility;
import com.volvo.gloria.procurematerial.c.dto.MaterialDTO;
import com.volvo.gloria.procurematerial.c.dto.ProcureLineDTO;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.PartAlias;
import com.volvo.gloria.procurematerial.d.entities.PartNumber;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatusHelper;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.material.MaterialTypeHelper;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureTypeHelper;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.PurchaseOrganisationRepository;
import com.volvo.gloria.procurematerial.util.ProcureGroupHelper;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.GloriaExceptionConstants;

public final class ProcureLineHelper {
    
    private ProcureLineHelper() {
    }

    public static void updateProcureLine(ProcureLineDTO procureLineDTO, ProcureLine procureLine, 
            String action, ProcureLineRepository procureLineRepository, CommonServices commonServices,
            ProcurementServices procurementServices, PurchaseOrganisationRepository purchaseOrganisationRepo, 
            UserDTO userDto, String finalWarehouseId) throws GloriaApplicationException {
        if (!StringUtils.isEmpty(procureLineDTO.getProcureType())) {
            procureLine.setProcureType(ProcureType.valueOf(procureLineDTO.getProcureType()));
        }
        procureLine.setpPartAffiliation(procureLineDTO.getpPartAffiliation());
        procureLine.setpPartNumber(procureLineDTO.getpPartNumber());
        procureLine.setpPartVersion(procureLineDTO.getpPartVersion());
        procureLine.setpPartName(procureLineDTO.getpPartName());
        procureLine.setpPartModification(procureLineDTO.getpPartModification());
        
        procureLine.setOrderStaDate(procureLineDTO.getOrderStaDate());

        procureLine.setDangerousGoodsOID(procureLineDTO.getDangerousGoods());
        procureLine.setWarehouseComment(procureLineDTO.getWarehouseComment());
        if (!StringUtils.isEmpty(procureLineDTO.getStatusFlag())) {
            procureLine.setStatusFlag(ProcureLineProgressStatusFlag.valueOf(procureLineDTO.getStatusFlag()));
        } else {
            procureLine.setStatusFlag(null);
        }
        
        procureLine.setCurrency(procureLineDTO.getCurrency());
        procureLine.setForwardedForDC(procureLineDTO.isForwardedForDC());

        procureLine.setRequiredStaDate(procureLineDTO.getRequiredStaDate());
        procureLine.setSupplierCounterPartOID(procureLineDTO.getSupplierCounterPartID());
        
        if (!procureLine.isContentEdited()) {
            procureLine.setContentEdited(procureLineDTO.isEdited());
        }
        
        updateShipToId(procureLine, commonServices);
        
        updateProcureLineWithAliasInfo(procureLineDTO, procureLine, procurementServices);
        
        updateAttributesByProcureType(procureLineDTO, procureLine, purchaseOrganisationRepo, userDto);

        if (procureLineDTO.getAdditionalQuantity() >= 0) {
            procureLine.setAdditionalQuantity(procureLineDTO.getAdditionalQuantity());
        } else {
            throw new GloriaApplicationException(GloriaExceptionConstants.NEGATIVE_NUMBER,
                                                 "This operation cannot be performed. Additional quantity is a negative number");
        }

        if (!StringUtils.isEmpty(action) && "toprocure".equalsIgnoreCase(action)) {
            procureLine.setResponsibility(ProcureResponsibility.PROCURER);
        } else if (!StringUtils.isEmpty(action) && "onbuildsite".equalsIgnoreCase(action)) {
            procureLine.setResponsibility(ProcureResponsibility.BUILDSITE);
        } else if (!StringUtils.isEmpty(action) && "delegate".equalsIgnoreCase(action)) {
            procureLine.setStatus(ProcureLineStatus.PROCURED);
        }
        
        if (!StringUtils.isEmpty(finalWarehouseId)) {
            List<Material> materials = procureLine.getMaterials();
            for (Material material : materials) {
                for (MaterialLine materialLine : material.getMaterialLine()) {
                    materialLine.setFinalWhSiteId(finalWarehouseId);
                }
            }
        }
                       
        
    }
    
    private static void updateProcureLineWithAliasInfo(ProcureLineDTO procureLineDTO, ProcureLine procureLine, ProcurementServices procurementServices) {
        if (!StringUtils.isEmpty(procureLineDTO.getPartAlias())) {
            PartNumber partNumber = procurementServices.findVolvoPartWithAliasByPartNumber(procureLineDTO.getpPartNumber());
            if (partNumber == null) {
                partNumber = new PartNumber();
                partNumber.setVolvoPartNumber(procureLineDTO.getpPartNumber());
                updatePartAlias(partNumber, procureLineDTO, procureLine);
            } else {
                updatePartAlias(partNumber, procureLineDTO, procureLine);
            }
        } else {
            procureLine.setPartAlias(null);
        }
    }

    private static void updatePartAlias(PartNumber partNumber, ProcureLineDTO procureLineDTO, ProcureLine procureLine) {
        List<PartAlias> partAliases = partNumber.getPartAlias();
        boolean isNew = true;
        if (partAliases != null && !partAliases.isEmpty()) {
            for (PartAlias alias : partAliases) {
                if (alias.getAliasPartNumber().equals(procureLineDTO.getPartAlias())) {
                    procureLine.setPartAlias(alias);
                    isNew = false;
                }
            }
        }

        if (isNew) {
            PartAlias alias = procureLine.getPartAlias();
            if (alias == null || alias.getPartNumber() != null) {
                 alias = new PartAlias();       
            }            
            alias.setAliasPartNumber(procureLineDTO.getPartAlias());
            procureLine.setPartAlias(alias);
        }
    }

    public static void updateAttributesByProcureType(ProcureLineDTO procureLineDTO, ProcureLine procureLine,
            PurchaseOrganisationRepository purchaseOrganisationRepo, UserDTO userDto) throws GloriaApplicationException {
        if (procureLine.getProcureType() != null) {
            procureLine.getProcureType().updateProcureLine(procureLineDTO, procureLine, purchaseOrganisationRepo, userDto);
        }
    }

    private static void updateShipToId(ProcureLine procureLine, CommonServices commonServices) {
        SupplierCounterPart supplierCounterPart = null; 
        
        if (procureLine.getSupplierCounterPartOID() != null) {
            supplierCounterPart = commonServices.getSupplierCounterPartById(procureLine.getSupplierCounterPartOID());
        }

        if (supplierCounterPart != null) {
            procureLine.setShipToId(supplierCounterPart.getShipToId());
        } else {
            procureLine.setShipToId(null);
        }
    }

    public static void updateFinancialInfo(List<Material> materials, ProcureLineDTO procureLineDTO) {
        if (materials != null && !materials.isEmpty()) {
            for (Material material : materials) {
                FinanceHeader financeHeader = material.getFinanceHeader();
                financeHeader.setCostCenter(procureLineDTO.getCostCenter());
                financeHeader.setGlAccount(procureLineDTO.getGlAccount());
                financeHeader.setInternalOrderNoSAP(procureLineDTO.getInternalOrderNoSAP());
                financeHeader.setWbsCode(procureLineDTO.getWbsCode());
            }
        }
    }
    
    public static void updateChangeToProcureLine(ProcureLine procureLine, ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs,
            Material removeMaterial, MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, OrderRepository orderRepository,
            boolean isPlaced, UserDTO userDto, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        procureLine.setNeedIsChanged(false);
        for (MaterialDTO materialDTO : materialDTOs) {
            if (removeMaterial.getMaterialOID() == materialDTO.getId()) {
                boolean removeProcureline = procureLine.getProcureType().update(removeMaterial, materialDTO.getChangeAction(), requestHeaderRepo, procureLine,
                                                                                procureLineRepository, procurementServices, orderRepository, isPlaced, userDto, traceabilityRepository);
                if (removeProcureline) {
                    removeAllUnderLyingMaterials(procureLine, removeMaterial, requestHeaderRepo, userDto, traceabilityRepository);
                    procureLineRepository.delete(procureLine);
                } else {
                    ProcureGroupHelper.updateIdSetsOnProcureLine(procureLine, procureLine.getMaterials());
                    procureLineRepository.save(procureLine);
                }
                break;
            }
        }
    }

    private static void removeAllUnderLyingMaterials(ProcureLine procureLine, Material removeMaterial, MaterialHeaderRepository requestHeaderRepo,
            UserDTO userDto, TraceabilityRepository traceabilityRepository) {
        List<Material> materials = procureLine.getMaterials();
        for (Material material : materials) {
            MaterialStatusHelper.removeMaterial(removeMaterial.getMtrlRequestVersionAccepted(), material, userDto, traceabilityRepository);
            requestHeaderRepo.updateMaterial(material);
        }
    }

    public static void associateMaterialWithProcureLine(Material material, ProcureLine procureLine) {
        procureLine.getMaterials().add(material);
        material.setProcureLine(procureLine);
        ProcureGroupHelper.updateProcureLineUsageQty(procureLine);
        ProcureGroupHelper.updateProcureLineAdditionalQty(procureLine);
        ProcureGroupHelper.updateIdSetsOnProcureLine(procureLine, procureLine.getMaterials());
    }
    
    public static List<Long> associateMaterialWithProcureLinePerformance(List<Material> existingMaterial, List<Material> materialsTobeAdded,
            ProcureLine procureLine, MaterialHeaderRepository repository) {
        if (materialsTobeAdded != null) {
            for (Material m : materialsTobeAdded) {
                m.setProcureLine(procureLine);
                procureLine.getMaterials().add(m);
                m.setAddedAfter(true);
            }
            existingMaterial.addAll(materialsTobeAdded);
        }

        List<Long> materialIds = new ArrayList<Long>();
        for (Material m : existingMaterial) {
            materialIds.add(m.getMaterialOID());
        }

        Map<MaterialType, Long> mapOfQuantities = repository.findMaterialLineQuantities(materialIds);
        ProcureGroupHelper.updateProcureLineUsageQtyFromMap(procureLine, mapOfQuantities);
        ProcureGroupHelper.updateProcureLineAdditionalQtyFromMap(procureLine, mapOfQuantities);
        ProcureGroupHelper.updateIdSetsOnProcureLine(procureLine, procureLine.getMaterials());

        return materialIds;
    }
    
    public static void deAssociateMaterialFromProcureLine(Material material) {
        ProcureLine procureLine = material.getProcureLine();
        if (procureLine != null) {
            procureLine.getMaterials().remove(material);
            material.setProcureLine(null);
            ProcureGroupHelper.updateProcureLineUsageQty(procureLine);
            ProcureGroupHelper.updateIdSetsOnProcureLine(procureLine, procureLine.getMaterials());
        }
    }
    /*
     * the method below is more performance centric since it does not use the MaterialLines from Materials
     */
    public static void deAssociateMaterialFromProcureLinePerformance(ProcureLine procureLine, Long materialHeaderOid, MaterialHeaderRepository repository, List<Material> materials, ProcureLineRepository procureLineRepository ) throws GloriaApplicationException {
        if (procureLine != null) {
            List<Material>  materialToRemove = new ArrayList<Material>();
            if (materials != null && !materials.isEmpty()) {
                for (Material material : materials) {
                    if (!material.getStatus().equals(MaterialStatus.REMOVED)) {
                        if (material.getMaterialHeader().getMaterialHeaderOid() == materialHeaderOid) {
                            materialToRemove.add(material);
                            material.setProcureLine(null);
                            material.setAddedAfter(false);
                        }
                    }
                }
                
            }
            if (materialToRemove != null) {
                materials.removeAll(materialToRemove);
                procureLine.setMaterials(materials);
            }// delete procure line if no materials are there
            if(materials.isEmpty()){
                procureLine.getStatus().remove(procureLine, procureLineRepository);
            }else{
                if (materials.size() == 1) {
                    materials.get(0).setAddedAfter(false);
                }
                List<Long> materialIds = new ArrayList<Long>();
                for(Material m: materials){
                    materialIds.add(m.getMaterialOID());
                }
                Map<MaterialType, Long> mapOfQuantities = repository.findMaterialLineQuantities(materialIds);
                ProcureGroupHelper.updateProcureLineUsageQtyFromMap(procureLine, mapOfQuantities);
                ProcureGroupHelper.updateIdSetsOnProcureLine(procureLine, materials);
            }
        }
    }
    
    public static void handleReceivedProcureLines(ProcureLine procureLine, ProcureLineRepository procureLineRepository, Material removeMaterial,
            MaterialHeaderRepository requestHeaderRepo,  UserDTO userDto, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        procureLine.setNeedIsChanged(false);
        ProcureTypeHelper.createNewReleasedOrAdditionalMaterial(removeMaterial, removeMaterial.getMaterialLine(), requestHeaderRepo, procureLine,
                                                    null, MaterialType.ADDITIONAL, userDto, traceabilityRepository);
        ProcureLineHelper.deAssociateMaterialFromProcureLine(removeMaterial);
        procureLineRepository.save(procureLine);
    }

    public static void cancelModification(List<Material> materials, MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices,
            ProcureLineRepository procureLineRepository, ProcureLine procureLine, UserDTO userDTO) throws GloriaApplicationException {
        for (int idx = materials.size() - 1; idx >= 0; idx--) {
            Material material = materials.get(idx);
            material.getMaterialType().cancelModification(material, requestHeaderRepository, procureLine);
        }

        List<Material> revertedMaterialsToUsage = procureLine.getMaterials();
        procureLineRepository.delete(procureLine);

        List<Material> materialsNotGrouped = new ArrayList<Material>();
        for (int idx = revertedMaterialsToUsage.size() - 1; idx >= 0; idx--) {
            Material material = revertedMaterialsToUsage.get(idx);
            materialsNotGrouped.addAll(procurementServices.groupIfMaterialsExist(userDTO.getId(), material));
        }
        procurementServices.groupMaterials(materialsNotGrouped);
        
    }
    
    public static void performRemoveOnAllRemoveMarked(ProcureLine procureLine, ChangeId changeId, MaterialHeaderRepository requestHeaderRepo, UserDTO userDTO,
            TraceabilityRepository traceabilityRepository) {
        for (int idx = procureLine.getMaterials().size() - 1; idx >= 0; idx--) {
            Material material = procureLine.getMaterials().get(idx);
            if (material.getStatus().equals(MaterialStatus.REMOVE_MARKED)) {
                MaterialStatusHelper.removeMaterial(changeId.getMtrlRequestVersion(), material, userDTO, traceabilityRepository);
                MaterialTypeHelper.resetModifiedAttributes(material);
                ProcureTypeHelper.disconnectMaterialFromProcureline(material);
                requestHeaderRepo.updateMaterial(material);
            }
        }
    }

    public static void acceptUsageReplaced(ProcureLine procureLine, ProcureLineRepository procureLineRepository, Material removeMaterial,
            MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, OrderRepository orderRepository, UserServices userServices,
            ChangeId changeId, String userID, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        UserDTO userDTO = userServices.getUser(userID);
        MaterialStatusHelper.removeMaterial(changeId.getMtrlRequestVersion(), removeMaterial, userDTO, traceabilityRepository);
        UserDTO userDTOMC = userServices.getUser(procureLine.getMaterialControllerId());
        MaterialTypeHelper.resetModifiedAttributes(removeMaterial);
        procureLine.getProcureType().updateForUsageReplaced(procureLine, procureLineRepository, removeMaterial, requestHeaderRepo, procurementServices,
                                                            orderRepository, false, userDTO, traceabilityRepository);
        performRemoveOnAllRemoveMarked(procureLine, changeId, requestHeaderRepo, userDTO, traceabilityRepository);
        ProcureLineHelper.cancelModification(procureLine.getMaterials(), requestHeaderRepo, procurementServices, procureLineRepository, procureLine, userDTOMC);
    }
}
