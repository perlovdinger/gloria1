package com.volvo.gloria.materialrequest.d.type.materialrequest;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.materialrequest.c.dto.MaterialRequestLineDTO;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestLine;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.Utils;

/**
 * helper class for operations on Material Types.
 * 
 */
public final class MaterialRequestTypeHelper {

    private static final String ADDED = "ADDED";

    private MaterialRequestTypeHelper() {
    }

    public static void setAttributesOnCreateTypeSingle(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestLine materialRequestLine)
            throws GloriaApplicationException {
        materialRequestLine.setPartAffiliation(materialRequestLineDTO.getPartAffiliation());
        materialRequestLine.setPartNumber(materialRequestLineDTO.getPartNumber().trim());
        materialRequestLine.setPartVersion(materialRequestLineDTO.getPartVersion().trim());
        materialRequestLine.setPartName(materialRequestLineDTO.getPartName().trim());
        materialRequestLine.setQuantity(materialRequestLineDTO.getQuantity());
        materialRequestLine.setUnitOfMeasure(materialRequestLineDTO.getUnitOfMeasure());
        materialRequestLine.setRequestLinkId(Utils.createRandomNumber());
        materialRequestLine.setOperation(ADDED);
        String partModification = materialRequestLineDTO.getPartModification();
        if (!StringUtils.isEmpty(partModification)) {
            partModification = partModification.trim();
        }
        materialRequestLine.setPartModification(partModification);
        materialRequestLine.setFunctionGroup(materialRequestLineDTO.getFunctionGroup());
    }

    public static void setAttributesOnCreateTypeMultiple(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestLine materialRequestLine)
            throws GloriaApplicationException {
        materialRequestLine.setPartAffiliation(materialRequestLineDTO.getPartAffiliation());
        materialRequestLine.setPartNumber(materialRequestLineDTO.getPartNumber().trim());
        materialRequestLine.setPartVersion(materialRequestLineDTO.getPartVersion().trim());
        materialRequestLine.setPartName(materialRequestLineDTO.getPartName().trim());
        materialRequestLine.setQuantity(materialRequestLineDTO.getQuantity());
        materialRequestLine.setUnitOfMeasure(materialRequestLineDTO.getUnitOfMeasure());
        materialRequestLine.setRequestLinkId(Utils.createRandomNumber());
        materialRequestLine.setOperation(ADDED);
    }

    public static void setAttributesOnCreateTypeForStock(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestLine materialRequestLine)
            throws GloriaApplicationException {
        materialRequestLine.setPartAffiliation(materialRequestLineDTO.getPartAffiliation());
        materialRequestLine.setPartNumber(materialRequestLineDTO.getPartNumber().trim());
        materialRequestLine.setPartVersion(materialRequestLineDTO.getPartVersion().trim());
        materialRequestLine.setPartName(materialRequestLineDTO.getPartName().trim());
        materialRequestLine.setQuantity(materialRequestLineDTO.getQuantity());
        materialRequestLine.setUnitOfMeasure(materialRequestLineDTO.getUnitOfMeasure());
        materialRequestLine.setRequestLinkId(Utils.createRandomNumber());
        materialRequestLine.setOperation(ADDED);
        String partModification = materialRequestLineDTO.getPartModification();
        if (!StringUtils.isEmpty(partModification)) {
            partModification = partModification.trim();
        }
        materialRequestLine.setPartModification(partModification);
        materialRequestLine.setFunctionGroup(materialRequestLineDTO.getFunctionGroup());
    }

    public static void setAttributesOnUpdateTypeSingle(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestLine materialRequestLine)
            throws GloriaApplicationException {
        materialRequestLine.setRemoveMarked(materialRequestLineDTO.isRemoveMarked());
        materialRequestLine.setPartAffiliation(materialRequestLineDTO.getPartAffiliation());
        materialRequestLine.setPartNumber(materialRequestLineDTO.getPartNumber().trim());
        materialRequestLine.setPartVersion(materialRequestLineDTO.getPartVersion().trim());
        materialRequestLine.setPartName(materialRequestLineDTO.getPartName().trim());
        materialRequestLine.setQuantity(materialRequestLineDTO.getQuantity());
        materialRequestLine.setUnitOfMeasure(materialRequestLineDTO.getUnitOfMeasure());
        materialRequestLine.setFunctionGroup(materialRequestLineDTO.getFunctionGroup());
        String partModification = materialRequestLineDTO.getPartModification();
        if (!StringUtils.isEmpty(partModification)) {
            partModification = partModification.trim();
        }
        materialRequestLine.setPartModification(partModification);
    }

    public static void setAttributesOnUpdateTypeMultiple(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestLine materialRequestLine)
            throws GloriaApplicationException {
        materialRequestLine.setRemoveMarked(materialRequestLineDTO.isRemoveMarked());
        materialRequestLine.setPartAffiliation(materialRequestLineDTO.getPartAffiliation());
        materialRequestLine.setPartNumber(materialRequestLineDTO.getPartNumber().trim());
        materialRequestLine.setPartVersion(materialRequestLineDTO.getPartVersion().trim());
        materialRequestLine.setPartName(materialRequestLineDTO.getPartName().trim());
        materialRequestLine.setQuantity(materialRequestLineDTO.getQuantity());
        materialRequestLine.setUnitOfMeasure(materialRequestLineDTO.getUnitOfMeasure());
    }

    public static void setAttributesOnUpdateTypeForStock(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestLine materialRequestLine)
            throws GloriaApplicationException {
        materialRequestLine.setRemoveMarked(materialRequestLineDTO.isRemoveMarked());
        materialRequestLine.setPartAffiliation(materialRequestLineDTO.getPartAffiliation());
        materialRequestLine.setPartNumber(materialRequestLineDTO.getPartNumber().trim());
        materialRequestLine.setPartVersion(materialRequestLineDTO.getPartVersion().trim());
        materialRequestLine.setPartName(materialRequestLineDTO.getPartName().trim());
        materialRequestLine.setQuantity(materialRequestLineDTO.getQuantity());
        materialRequestLine.setUnitOfMeasure(materialRequestLineDTO.getUnitOfMeasure());
        materialRequestLine.setFunctionGroup(materialRequestLineDTO.getFunctionGroup());
        String partModification = materialRequestLineDTO.getPartModification();
        if (!StringUtils.isEmpty(partModification)) {
            partModification = partModification.trim();
        }
        materialRequestLine.setPartModification(partModification);
    }

}
