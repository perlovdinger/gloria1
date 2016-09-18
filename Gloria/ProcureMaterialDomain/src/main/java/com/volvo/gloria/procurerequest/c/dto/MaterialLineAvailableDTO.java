package com.volvo.gloria.procurerequest.c.dto;

import java.io.Serializable;

import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.util.paging.c.PageResults;

public class MaterialLineAvailableDTO implements Serializable, PageResults {

    private static final long serialVersionUID = 3304527055020564600L;

    private long id;

    private long version;

    private String materialLineKeys;

    private String countryCode;

    private String siteId;

    private String projectId;

    private long quantity;

    private String assignedMaterialController;

    private MaterialType materialType;

    private String mtrlRequestVersion;

    private String referenceId;

    public MaterialLineAvailableDTO(long id, String countryCode, String siteId, String projectId, Long quantity, String assignedMaterialController,
            MaterialType materialType, String mtrlRequestVersion, String referenceId) {
        this.id = id;
        this.countryCode = countryCode;
        this.siteId = siteId;
        this.projectId = projectId;
        this.quantity = quantity;
        this.assignedMaterialController = assignedMaterialController;
        this.materialLineKeys = this.countryCode + "-" + this.siteId + "-" + this.assignedMaterialController + "-" + materialType.name();
        this.materialType = materialType;
        this.mtrlRequestVersion = mtrlRequestVersion;
        this.referenceId = referenceId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getMaterialLineKeys() {
        return materialLineKeys;
    }

    public void setMaterialLineKeys(String materialLineKeys) {
        this.materialLineKeys = materialLineKeys;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public String getAssignedMaterialController() {
        return assignedMaterialController;
    }

    public void setAssignedMaterialController(String assignedMaterialController) {
        this.assignedMaterialController = assignedMaterialController;
    }

    public MaterialType getMaterialType() {
        return materialType;
    }

    public void setMaterialType(MaterialType materialType) {
        this.materialType = materialType;
    }

    public String getMtrlRequestVersion() {
        return mtrlRequestVersion;
    }

    public void setMtrlRequestVersion(String mtrlRequestVersion) {
        this.mtrlRequestVersion = mtrlRequestVersion;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
}
