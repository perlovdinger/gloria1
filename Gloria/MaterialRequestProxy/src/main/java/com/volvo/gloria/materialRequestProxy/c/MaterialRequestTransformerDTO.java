package com.volvo.gloria.materialRequestProxy.c;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A DTO to transform the procureMessage.
 */
public class MaterialRequestTransformerDTO implements Serializable {

    private static final long serialVersionUID = 5595531999568045550L;

    private String mtrlRequestId;

    private String referenceId;

    private String referenceGroup;

    private String contactPersonUserId;

    private String contactPersonName;

    private String requesterId;

    private String requesterName;
    
    private String materialControllerUserId;
    
    private String materialControllerName;

    private MaterialRequestVersionTransformerDTO mtrlRequestVersionDTO;

    private FinanceMaterialDTO financeMaterialDTO;

    private List<MaterialRequestLineTransformerDTO> mtrlRequestLineTransformerDTOs = new ArrayList<MaterialRequestLineTransformerDTO>();

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getReferenceGroup() {
        return referenceGroup;
    }

    public void setReferenceGroup(String referenceGroup) {
        this.referenceGroup = referenceGroup;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }
    
    public String getMaterialControllerName() {
        return materialControllerName;
    }
    
    public void setMaterialControllerName(String materialControllerName) {
        this.materialControllerName = materialControllerName;
    }
    
    public String getMaterialControllerUserId() {
        return materialControllerUserId;
    }
    
    public void setMaterialControllerUserId(String materialControllerUserId) {
        this.materialControllerUserId = materialControllerUserId;
    }

    public String getMtrlRequestId() {
        return mtrlRequestId;
    }

    public void setMtrlRequestId(String mtrlRequestId) {
        this.mtrlRequestId = mtrlRequestId;
    }

    public MaterialRequestVersionTransformerDTO getMtrlRequestVersionDTO() {
        return mtrlRequestVersionDTO;
    }

    public void setMtrlRequestVersionDTO(MaterialRequestVersionTransformerDTO mtrlRequestVersionDTO) {
        this.mtrlRequestVersionDTO = mtrlRequestVersionDTO;
    }

    public List<MaterialRequestLineTransformerDTO> getMtrlRequestLineTransformerDTOs() {
        return mtrlRequestLineTransformerDTOs;
    }

    public void setMtrlRequestLineTransformerDTOs(List<MaterialRequestLineTransformerDTO> mtrlRequestLineTransformerDTOs) {
        this.mtrlRequestLineTransformerDTOs = mtrlRequestLineTransformerDTOs;
    }

    public FinanceMaterialDTO getFinanceMaterialDTO() {
        return financeMaterialDTO;
    }

    public void setFinanceMaterialDTO(FinanceMaterialDTO financeMaterialDTO) {
        this.financeMaterialDTO = financeMaterialDTO;
    }

    public String getContactPersonUserId() {
        return contactPersonUserId;
    }

    public void setContactPersonUserId(String contactPersonUserId) {
        this.contactPersonUserId = contactPersonUserId;
    }
}
