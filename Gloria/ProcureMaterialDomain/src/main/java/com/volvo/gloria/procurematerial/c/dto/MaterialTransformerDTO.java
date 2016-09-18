package com.volvo.gloria.procurematerial.c.dto;

import java.util.Date;
import java.util.List;

/**
 * DTO class for MaterialTransformer.
 */
public class MaterialTransformerDTO {

    private Long quantity;

    private String materialType;

    private String unitOfMeasure;
    private boolean migrated;
    private String referenceId;
    private String referenceGroup;
    private String outboundLocationId;
    private String outboundLocationName;
    private Date outBoundStartDate;
    private Date requiredStaDate;
    private Date buildStartDate;
    private String receiver;
    private String changeRequestType;
    private String changeRequestId;
    private String phaseType;
    private String phase;
    private Date phasePlannedDeliveryStartDate;
    private Date phasePlannedAssemblyStartDate;
    private Date phasePlannedAssemblyEndDAte;
    private String buildSeriesName;
    private Date buildSeriesStartDate;
    private Date buildSeriesEndDate;
    private String contactPersonId;
    private String contactPersonName;
    private String contactDepartment;
    private String requesterUserId;
    private String requesterName;

    private MaterialProcureTransformerDTO materialProcureTransformerDTO;
    private List<MaterialLineDetailsDTO> materialLineDTO;

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public boolean isMigrated() {
        return migrated;
    }

    public void setMigrated(boolean migrated) {
        this.migrated = migrated;
    }

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

    public String getOutboundLocationId() {
        return outboundLocationId;
    }

    public void setOutboundLocationId(String outboundLocationId) {
        this.outboundLocationId = outboundLocationId;
    }

    public String getOutboundLocationName() {
        return outboundLocationName;
    }

    public void setOutboundLocationName(String outboundLocationName) {
        this.outboundLocationName = outboundLocationName;
    }

    public Date getOutBoundStartDate() {
        return outBoundStartDate;
    }

    public void setOutBoundStartDate(Date outBoundStartDate) {
        this.outBoundStartDate = outBoundStartDate;
    }

    public Date getRequiredStaDate() {
        return requiredStaDate;
    }

    public void setRequiredStaDate(Date requiredStaDate) {
        this.requiredStaDate = requiredStaDate;
    }

    public Date getBuildStartDate() {
        return buildStartDate;
    }

    public void setBuildStartDate(Date buildStartDate) {
        this.buildStartDate = buildStartDate;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getChangeRequestType() {
        return changeRequestType;
    }

    public void setChangeRequestType(String changeRequestType) {
        this.changeRequestType = changeRequestType;
    }

    public String getChangeRequestId() {
        return changeRequestId;
    }

    public void setChangeRequestId(String changeRequestId) {
        this.changeRequestId = changeRequestId;
    }

    public String getPhaseType() {
        return phaseType;
    }

    public void setPhaseType(String phaseType) {
        this.phaseType = phaseType;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public Date getPhasePlannedDeliveryStartDate() {
        return phasePlannedDeliveryStartDate;
    }

    public void setPhasePlannedDeliveryStartDate(Date phasePlannedDeliveryStartDate) {
        this.phasePlannedDeliveryStartDate = phasePlannedDeliveryStartDate;
    }

    public Date getPhasePlannedAssemblyStartDate() {
        return phasePlannedAssemblyStartDate;
    }

    public void setPhasePlannedAssemblyStartDate(Date phasePlannedAssemblyStartDate) {
        this.phasePlannedAssemblyStartDate = phasePlannedAssemblyStartDate;
    }

    public Date getPhasePlannedAssemblyEndDAte() {
        return phasePlannedAssemblyEndDAte;
    }

    public void setPhasePlannedAssemblyEndDAte(Date phasePlannedAssemblyEndDAte) {
        this.phasePlannedAssemblyEndDAte = phasePlannedAssemblyEndDAte;
    }

    public String getBuildSeriesName() {
        return buildSeriesName;
    }

    public void setBuildSeriesName(String buildSeriesName) {
        this.buildSeriesName = buildSeriesName;
    }

    public Date getBuildSeriesStartDate() {
        return buildSeriesStartDate;
    }

    public void setBuildSeriesStartDate(Date buildSeriesStartDate) {
        this.buildSeriesStartDate = buildSeriesStartDate;
    }

    public Date getBuildSeriesEndDate() {
        return buildSeriesEndDate;
    }

    public void setBuildSeriesEndDate(Date buildSeriesEndDate) {
        this.buildSeriesEndDate = buildSeriesEndDate;
    }

    public String getContactPersonId() {
        return contactPersonId;
    }

    public void setContactPersonId(String contactPersonId) {
        this.contactPersonId = contactPersonId;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getContactDepartment() {
        return contactDepartment;
    }

    public void setContactDepartment(String contactDepartment) {
        this.contactDepartment = contactDepartment;
    }

    public String getRequesterUserId() {
        return requesterUserId;
    }

    public void setRequesterUserId(String requesterUserId) {
        this.requesterUserId = requesterUserId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public MaterialProcureTransformerDTO getMaterialProcureTransformerDTO() {
        return materialProcureTransformerDTO;
    }

    public void setMaterialProcureTransformerDTO(MaterialProcureTransformerDTO materialProcureTransformerDTO) {
        this.materialProcureTransformerDTO = materialProcureTransformerDTO;
    }

    public List<MaterialLineDetailsDTO> getMaterialLineDTO() {
        return materialLineDTO;
    }

    public void setMaterialLineDTO(List<MaterialLineDetailsDTO> materialLineDTO) {
        this.materialLineDTO = materialLineDTO;
    }

}
