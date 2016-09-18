package com.volvo.gloria.procurematerial.util;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.procurematerial.b.beans.ProcurementServicesBean;
import com.volvo.gloria.procurematerial.c.ChangeType;
import com.volvo.gloria.procurematerial.c.ProcureResponsibility;
import com.volvo.gloria.procurematerial.c.dto.MaterialGroupDTO;
import com.volvo.gloria.procurematerial.c.dto.ProcureLineDTO;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatus;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.gloria.util.c.UniqueItems;

/**
 * Helper class to group ProcureRequestLines on assigning procure requests to material controller.
 * 
 * grouping based on
 * 
 * DefaultGrouping - following attributes have to match : partAffiliation , partNumber,partVersion,partModification, projectId,glAccount,costCenter,wbsCode,
 * internalOrderNoSAP,mailFormId, referenceGroup,changeRequestType, changeRequestId
 * 
 * Manual Grouping - following attributes have to match: projectId,glAccount,costCenter,wbsCode, internalOrderNoSAP,mailFormId,referenceGroup,changeRequestType,
 * changeRequestId
 * 
 * 
 * A grouping key is evaluated on ProcureRequestLine entity and the same will be used for grouping
 * 
 */
public class ProcureGroupHelper {

    private String groupingType;

    private Map<String, PreparedMaterialGroup> preparedGroups;
    
    private List<String> financeCriteria;
    
    private static final int START_RANGE = 100000000;
    private static final int END_RANGE = 900000000;
    
    public Map<String, PreparedMaterialGroup> getPreparedGroups(){
        return this.preparedGroups;
    }

    public ProcureGroupHelper(List<MaterialGroupDTO> materialGroupDTOs, String groupingType) {
        this.groupingType = groupingType;
        financeCriteria = new ArrayList<String>();
        preparedGroups = new HashMap<String, PreparedMaterialGroup>();
        for (MaterialGroupDTO materialGroupDTO : materialGroupDTOs) {
            for (Material material : materialGroupDTO.getMaterials()) {
                if (material.getStatus().isAvailableForGrouping(material)) {
                    String groupingKey = ((Material) material).getGroupIdentifierKey(groupingType);
                    PreparedMaterialGroup preparedMaterialGroup = null;
                    if (preparedGroups.containsKey(groupingKey)) {
                        preparedMaterialGroup = preparedGroups.get(groupingKey);
                    } else {
                        preparedMaterialGroup = new PreparedMaterialGroup(groupingKey, material.getProcureLine());
                    }
                    preparedGroups.put(groupingKey, preparedMaterialGroup);
                    preparedMaterialGroup.addItem(material);
                    
                    String financeCriteriaKey = ((Material) material).getFinanceCriteriaKey();
                    if (!financeCriteria.contains(financeCriteriaKey)) {
                        financeCriteria.add(financeCriteriaKey);
                    }
                }
            }
            
        }
    }
    
    public static void updateProcureLineUsageQtyFromMap(ProcureLine procureLine, Map<MaterialType, Long> materialQuantities) {
        List<MaterialType> materialTypes = new ArrayList<MaterialType>();
        materialTypes.add(MaterialType.USAGE);
        materialTypes.add(MaterialType.MODIFIED);
        materialTypes.add(MaterialType.RELEASED);
        materialTypes.add(MaterialType.ADDITIONAL_USAGE);
        Long quantityToSet = 0L;
        for (MaterialType materialType : materialTypes) {
            Long tempLong = (Long) materialQuantities.get(materialType);
            if (tempLong != null) {
                quantityToSet = quantityToSet + tempLong;
            }
        }
        if (quantityToSet != null) {
            procureLine.setUsageQuantity(quantityToSet);
        }
    }
    
    /**
     * If false, throw err for manual grouping.
     */
    public boolean isValidForManualGrouping() {
        if (groupingType.equalsIgnoreCase(GloriaParams.GROUP_TYPE_DIFF_TOBJ_SAME_PART) && financeCriteria != null && financeCriteria.size() > 1) {
            return false;
        } 
        return true;
    }

    public List<ProcureLine> getProcureLines(CommonServices commonServices, UserDTO userDTO, MaterialHeaderRepository repository) 
            throws GloriaApplicationException {

        if (!groupingType.equalsIgnoreCase(GloriaParams.GROUP_TYPE_DEFAULT)) {
            return new ArrayList<ProcureLine>();
        }
        List<ProcureLine> procureLines = new ArrayList<ProcureLine>();
        for (Object groupingKey : preparedGroups.keySet()) {
            PreparedMaterialGroup group = preparedGroups.get(groupingKey);
            List<Material> items = (List<Material>) group.getItems();
            ProcureLine procureLine = null;
            if (items != null && !items.isEmpty()) {
                procureLine = prepareProcureLine(items, commonServices, userDTO, repository);
                procureLines.add(procureLine);
            }
            group.setCurrentProcureLine(procureLine);
        }
        return procureLines;
    }

    private ProcureLine prepareProcureLine(List<Material> materialList, CommonServices commonServices, UserDTO userDTO, MaterialHeaderRepository repository) 
            throws GloriaApplicationException {
        Material material = materialList.get(0);
        MaterialHeader requestHeader = material.getMaterialHeader();
        ProcureLine procureLine = new ProcureLine();
        try {
            procureLine.setGroupingKeyMd5(material.getGroupingKeyMd5());
        } catch (NoSuchAlgorithmException no) {
            throw new GloriaApplicationException(GloriaExceptionConstants.DEFAULT_ERROR_CODE, "NoSuchAlgorithm Exception");
        }
        procureLine.setStatus(ProcureLineStatus.START);
        procureLine.setFinanceHeader(material.getFinanceHeader());
        procureLine.getStatus().init(procureLine);
        procureLine.setMaterials(materialList);
        procureLine.setResponsibility(ProcureResponsibility.PROCURER);
        procureLine.setDfuObjectNumber(material.getObjectNumber());
        procureLine.setMaterials(materialList);
        
        List<Long> materialIds = new ArrayList<Long>();
        for (Material m : materialList) {
            materialIds.add(m.getMaterialOID());
        }

        Map<MaterialType, Long> mapOfQuantities = repository.findMaterialLineQuantities(materialIds);
        updateProcureLineUsageQtyFromMap(procureLine, mapOfQuantities);
        sortMaterialsByEarliestRequiredSTADate(materialList);
        procureLine.setRequiredStaDate(materialList.get(0).getRequiredStaDate());
        procureLine.setMaterialControllerId(requestHeader.getMaterialControllerUserId());
        procureLine.setMaterialControllerName(requestHeader.getMaterialControllerName());
        procureLine.setMaterialControllerTeam(requestHeader.getMaterialControllerTeam());        
        procureLine.setpPartAffiliation(material.getPartAffiliation());
        procureLine.setpPartNumber(material.getPartNumber());
        procureLine.setpPartVersion(material.getPartVersion());
        procureLine.setpPartName(material.getPartName());
        procureLine.setpPartModification(material.getPartModification());
        procureLine.setFunctionGroupId(material.getFunctionGroup());
        if (material.getAdd() != null && !material.getStatus().equals(MaterialStatus.REMOVE_MARKED) && !material.getStatus().equals(MaterialStatus.REMOVED)
                && !material.getAdd().getType().equals(ChangeType.FOR_STOCK)) {
            procureLine.setFromStockAllowed(true);
        }
        updateIdSetsOnProcureLine(procureLine, materialList);        
        return procureLine;
    }

    public static void updateProcureLineUsageQty(ProcureLine procureLine) {
        long usageQty = 0;
        List<Material> materials = procureLine.getMaterials();
        if (materials != null && !materials.isEmpty()) {
            for (Material material : materials) {
                MaterialType materialType = material.getMaterialType();
                if (materialType.equals(MaterialType.USAGE) || materialType.equals(MaterialType.MODIFIED) || materialType.equals(MaterialType.RELEASED)
                        || materialType.equals(MaterialType.ADDITIONAL_USAGE)) {
                    usageQty += sumUpMaterialLines(procureLine, material);
                }
            }
        }
        procureLine.setUsageQuantity(usageQty);
    }

    private static long sumUpMaterialLines(ProcureLine procureLine, Material material) {
        long sumOfQuantities = 0L;
        for (MaterialLine materialLine : material.getMaterialLine()) {
            if (!materialLine.isOrderCancelled()) {
                sumOfQuantities += materialLine.getQuantity();
            }
        }
        return sumOfQuantities;
    }

    private void sortMaterialsByEarliestRequiredSTADate(List<Material> materialList) {
        Collections.sort(materialList, new Comparator<Material>() {
            public int compare(Material m1, Material m2) {
                if (m1.getRequiredStaDate() != null && m2.getRequiredStaDate() != null) {
                    return m1.getRequiredStaDate().compareTo(m2.getRequiredStaDate());
                }
                return 1;
            }
        });
        
    }

    public static ProcureLine updateIdSetsOnProcureLine(ProcureLine procureLine, List<Material> materialList) {
        UniqueItems idSets = new UniqueItems();
        UniqueItems groupSets = new UniqueItems();
        UniqueItems mtrlRequestVersions = new UniqueItems();

        for (Material material : materialList) {
            if (material.getStatus() == MaterialStatus.ADDED || material.getStatus() == MaterialStatus.REMOVE_MARKED) {
                MaterialHeader header = material.getMaterialHeader();
                if (header != null) {
                    if (!material.getMaterialType().isAdditional()) {
                        idSets.add(header.getReferenceId());
                    }
                    MaterialHeaderVersion acceptedVersion = header.getAccepted();
                    if (acceptedVersion != null) {
                        groupSets.add(acceptedVersion.getReferenceGroup());
                    }
                }
                ChangeId changeId = material.getAdd();
                if (changeId != null) {
                    mtrlRequestVersions.add(changeId.getMtrlRequestVersion());
                }
            }
        }

        procureLine.setReferenceGroups(groupSets.createCommaSeparatedKey());
        procureLine.setReferenceIds(idSets.createCommaSeparatedKey());
        procureLine.setChangeRequestIds(mtrlRequestVersions.createCommaSeparatedKey());
        return procureLine;
    }

    public List<Material> groupLines(String pPartNo, String pPartVersion, String pPartName, String pPartModification,
                                     ProcurementServicesBean procurementServicesBean, ProcureLineRepository procureLineRepository, 
                                     List<MaterialGroupDTO> materialGroupDTOs, String userId) throws GloriaApplicationException {
        List<Material> updatedMaterials = new ArrayList<Material>();
        if (!groupingType.equalsIgnoreCase(GloriaParams.GROUP_TYPE_DEFAULT)) {
            long modificationId = (long) (new Random().nextInt(END_RANGE) + START_RANGE);
            if (preparedGroups != null && !preparedGroups.isEmpty()) {
                for (PreparedMaterialGroup preparedMaterialGroup : preparedGroups.values()) {
                    ProcureLine procureLine = new ProcureLine();
                    procureLine.setStatus(ProcureLineStatus.START);
                    procureLine.setpPartAffiliation("X"); 
                    procureLine.setpPartNumber(pPartNo);
                    procureLine.setpPartVersion(pPartVersion);
                    procureLine.setpPartName(pPartName);
                    procureLine.setpPartModification(pPartModification);
                    procureLine.setResponsibility(ProcureResponsibility.PROCURER);
                    procureLine.getStatus().init(procureLine);
                    procureLine.setMaterialControllerId(preparedMaterialGroup.getCurrentProcureLine().getMaterialControllerId());
                    procureLine.setMaterialControllerName(preparedMaterialGroup.getCurrentProcureLine().getMaterialControllerName());
                    procureLine.setMaterialControllerTeam(preparedMaterialGroup.getCurrentProcureLine().getMaterialControllerTeam());
                    sortMaterialsByEarliestRequiredSTADate(preparedMaterialGroup.getItems());
                    procureLine.setRequiredStaDate(preparedMaterialGroup.getItems().get(0).getRequiredStaDate());
                   
                    procureLine = procureLineRepository.save(procureLine);
                    updatedMaterials.addAll(procurementServicesBean.associateProcureLines(preparedMaterialGroup.getItems(), 
                                            procureLine, groupingType, materialGroupDTOs, modificationId, userId));                 
                   
                }
            }
        }
        return updatedMaterials;
    }

    public static void updateMaterialAddedAfter(ProcureLineDTO procureLineDTO, ProcureLine procureLine) {
        List<Material> materials = procureLine.getMaterials();
        if (materials != null) {
            for (Material material : materials) {
                if (!procureLineDTO.isHasUnread()) {
                    // all material addedAfter is reset if DTO says so
                    material.setAddedAfter(false);
                }
            }
        } 
    }
    
    public static void updateProcureLineAdditionalQty(ProcureLine procureLine) {
        long additionalQty = 0;
        List<Material> materials = procureLine.getMaterials();
        if (materials != null && !materials.isEmpty()) {
            for (Material material : materials) {
                MaterialType materialType = material.getMaterialType();
                if (materialType.equals(MaterialType.ADDITIONAL)) {
                    // this is a performance problem 
                    additionalQty += sumUpMaterialLines(procureLine, material);
                }
            }
        }
        procureLine.setAdditionalQuantity(additionalQty);
    }
    
    public static void updateProcureLineAdditionalQtyFromMap(ProcureLine procureLine, Map<MaterialType, Long> materialQuantities) {
        Long additionalQuantity = (Long) materialQuantities.get(MaterialType.ADDITIONAL);
        if (additionalQuantity != null) {
            procureLine.setUsageQuantity(additionalQuantity);    
        }
    }
}

