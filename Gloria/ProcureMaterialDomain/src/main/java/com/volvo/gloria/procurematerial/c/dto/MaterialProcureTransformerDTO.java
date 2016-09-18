package com.volvo.gloria.procurematerial.c.dto;

import java.util.Date;
import java.util.List;

/**
 * DTO class for MaterialProcureTransformer.
 */
public class MaterialProcureTransformerDTO {

    private String procureType;
    private Date procureDate;
    private long procureLineOid;
    private String procureUserId;
    private long requisitionId;
    private String pPartAffiliation;
    private String pPartNumber;
    private String pPartVersion;
    private String pPartName;
    private String pPartModification;
    private String dfuObjectNumber;
    private String referenceGroups;
    private String inspectionLevel;
    private boolean sendToQI;
    private boolean dangerousGoodsFlag;
    private String dangerousGoodsName;
    private String scrappingAlert;
    private Date scrapExpirationDate;
    private String warehouseComment;
    private String procureInfo;
    private double unitPrice;
    private int totalPrice;
    private String currency;
    private String projectId;
    private String wbsCode;
    private String glAccount;
    private String costCenter;
    private String internalOrderNo;
    private String mailFormId;
    private String consignorId;
    private boolean directSend;
    private String whSiteId;
    private String shipToId;
    private String finalWarehouseId;
    private Date requiredStaDate;
    private List<MaterialTransformerDTO> materialDTOs;

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getWbsCode() {
        return wbsCode;
    }

    public void setWbsCode(String wbsCode) {
        this.wbsCode = wbsCode;
    }

    public String getGlAccount() {
        return glAccount;
    }

    public void setGlAccount(String glAccount) {
        this.glAccount = glAccount;
    }

    public String getpPartAffiliation() {
        return pPartAffiliation;
    }

    public void setpPartAffiliation(String pPartAffiliation) {
        this.pPartAffiliation = pPartAffiliation;
    }

    public String getpPartNumber() {
        return pPartNumber;
    }

    public void setpPartNumber(String pPartNumber) {
        this.pPartNumber = pPartNumber;
    }

    public String getpPartVersion() {
        return pPartVersion;
    }

    public void setpPartVersion(String pPartVersion) {
        this.pPartVersion = pPartVersion;
    }

    public String getpPartName() {
        return pPartName;
    }

    public void setpPartName(String pPartName) {
        this.pPartName = pPartName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public List<MaterialTransformerDTO> getMaterialDTOs() {
        return materialDTOs;
    }

    public void setMaterialDTOs(List<MaterialTransformerDTO> materialDTOs) {
        this.materialDTOs = materialDTOs;
    }

    public String getInspectionLevel() {
        return inspectionLevel;
    }

    public void setInspectionLevel(String inspectionLevel) {
        this.inspectionLevel = inspectionLevel;
    }

    public String getDfuObjectNumber() {
        return dfuObjectNumber;
    }

    public void setDfuObjectNumber(String dfuObjectNumber) {
        this.dfuObjectNumber = dfuObjectNumber;
    }

    public String getProcureType() {
        return procureType;
    }

    public void setProcureType(String procureType) {
        this.procureType = procureType;
    }

    public Date getProcureDate() {
        return procureDate;
    }

    public void setProcureDate(Date procureDate) {
        this.procureDate = procureDate;
    }

    public long getProcureLineOid() {
        return procureLineOid;
    }

    public void setProcureLineOid(long procureLineOid) {
        this.procureLineOid = procureLineOid;
    }

    public String getProcureUserId() {
        return procureUserId;
    }

    public void setProcureUserId(String procureUserId) {
        this.procureUserId = procureUserId;
    }

    public long getRequisitionId() {
        return requisitionId;
    }

    public void setRequisitionId(long requisitionId) {
        this.requisitionId = requisitionId;
    }

    public String getpPartModification() {
        return pPartModification;
    }

    public void setpPartModification(String pPartModification) {
        this.pPartModification = pPartModification;
    }

    public String getReferenceGroups() {
        return referenceGroups;
    }

    public void setReferenceGroups(String referenceGroups) {
        this.referenceGroups = referenceGroups;
    }

    public boolean isSendToQI() {
        return sendToQI;
    }

    public void setSendToQI(boolean sendToQI) {
        this.sendToQI = sendToQI;
    }

    public boolean isDangerousGoodsFlag() {
        return dangerousGoodsFlag;
    }

    public void setDangerousGoodsFlag(boolean dangerousGoodsFlag) {
        this.dangerousGoodsFlag = dangerousGoodsFlag;
    }

    public String getDangerousGoodsName() {
        return dangerousGoodsName;
    }

    public void setDangerousGoodsName(String dangerousGoodsName) {
        this.dangerousGoodsName = dangerousGoodsName;
    }

    public String getScrappingAlert() {
        return scrappingAlert;
    }

    public void setScrappingAlert(String scrappingAlert) {
        this.scrappingAlert = scrappingAlert;
    }

    public Date getScrapExpirationDate() {
        return scrapExpirationDate;
    }

    public void setScrapExpirationDate(Date scrapExpirationDate) {
        this.scrapExpirationDate = scrapExpirationDate;
    }

    public String getWarehouseComment() {
        return warehouseComment;
    }

    public void setWarehouseComment(String warehouseComment) {
        this.warehouseComment = warehouseComment;
    }

    public String getProcureInfo() {
        return procureInfo;
    }

    public void setProcureInfo(String procureInfo) {
        this.procureInfo = procureInfo;
    }


    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getInternalOrderNo() {
        return internalOrderNo;
    }

    public void setInternalOrderNo(String internalOrderNo) {
        this.internalOrderNo = internalOrderNo;
    }

    public String getMailFormId() {
        return mailFormId;
    }

    public void setMailFormId(String mailFormId) {
        this.mailFormId = mailFormId;
    }

    public String getConsignorId() {
        return consignorId;
    }

    public void setConsignorId(String consignorId) {
        this.consignorId = consignorId;
    }

    public boolean isDirectSend() {
        return directSend;
    }

    public void setDirectSend(boolean directSend) {
        this.directSend = directSend;
    }

    public String getWhSiteId() {
        return whSiteId;
    }

    public void setWhSiteId(String whSiteId) {
        this.whSiteId = whSiteId;
    }

    public String getShipToId() {
        return shipToId;
    }

    public void setShipToId(String shipToId) {
        this.shipToId = shipToId;
    }

    public String getFinalWarehouseId() {
        return finalWarehouseId;
    }

    public void setFinalWarehouseId(String finalWarehouseId) {
        this.finalWarehouseId = finalWarehouseId;
    }

    public Date getRequiredStaDate() {
        return requiredStaDate;
    }

    public void setRequiredStaDate(Date requiredStaDate) {
        this.requiredStaDate = requiredStaDate;
    }

}
