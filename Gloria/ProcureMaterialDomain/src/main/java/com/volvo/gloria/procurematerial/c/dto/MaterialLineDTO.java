package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;
import java.util.Date;

import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.paging.c.PageResults;

/**
 * Data class for Material line details.
 * 
 */
public class MaterialLineDTO implements Serializable, PageResults {

    private static final long serialVersionUID = 5434897385806534982L;
    private long version;
    private long id;
    private String projectId;
    private String referenceGroup;
    private String referenceId;
    private String pPartAffiliation;
    private String pPartVersion;
    private String pPartName;
    private String buildId;
    private String buildType;
    private String changeRequestIds;
    private String outBoundLocationId;
    private String outBoundLocationName;
    private Long quantity;
    private Long additionalQuantity;
    private String status;
    private Long suggestedBinLocationId;
    private String suggestedBinLocation;
    private String pPartNumber;
    private Long placementID;
    private Double stockBalance;
    private String whSiteId;
    private MaterialType materialType;
    private long requestListID;
    private long requestListVersion;
    private Long priority;
    private String note;
    private Date requiredDeliveryDate;
    private Date outboundStartDate;
    private String pPartModification;
    private String unitOfMeasure;
    private String procureUserId;
    private String procureTeam;
    private long materialId;
    private long materialOwnerId;
    private String binLocationCode;
    private long storedQuantity;
    private String transportLabel;
    private long binlocation;
    private long procureLineId;
    private String mtrlRequestVersion;
    private String mtrlRequestType;
    private String deliveryNoteNo;
    private String supplierId;
    private String supplierName;
    private String partNumber;
    private String partAlias;
    private String partVersion;
    private String orderNo;
    private Long possiblePickQuantity;
    private long pickedQuantity;
    private boolean balanceExceeded;
    private Date deliveryNoteDate;
    private Date statusDate;
    private String dispatchNoteNo;
    private String pickListCode;
    private String deliveryAddressName;
    private String assignedMaterialControllerId;
    private String assignedMaterialControllerName;
    private String parmaName;
    private String parmaID;
    private String transportationNo;
    private String carrier;
    private Double binlocationBalance;
    private String buildName;
    private Date expirationDate;    
    private boolean markPassedDate;
    private String materialPartModification;
    private String partAffiliation;
    private String mailFormId;
    private String functionGroup;
    private String designResponsible;
    private String modularHarness;
    private String financeHeaderCompanyCode;
    private String glAccount;
    private String wbsCode;
    private String costCenter;
    private String orderSuffix;
    private String materialControllerTeam;
    private String materialControllerName;
    private String materialControllerId;
    private String deliveryControllerId;
    private String deliveryControllerName;
    private Date requiredStaDate;
    private String referenceGps;
    private Date procureDate;
    private String requisitionId;
    private String requesterName;
    private String requesterUserId;
    private String contactPersonId;
    private String contactPersonName;
    private Date expirationDate2;
    private String deliveryAddressId;
    private String storageRoomName;
    private String internalOrderNoSAP;
    private boolean alertPartVersion;
    private long deliveryNoteLineId;
    private Date expectedDate;
    private String finalWhSiteId;

    public String getFinalWhSiteId() {
        return finalWhSiteId;
    }

    public void setFinalWhSiteId(String finalWhSiteId) {
        this.finalWhSiteId = finalWhSiteId;
    }

    public MaterialLineDTO() {
    }
    
    public String getDeliveryControllerId() {
        return deliveryControllerId;
    }

    public void setDeliveryControllerId(String deliveryControllerId) {
        this.deliveryControllerId = deliveryControllerId;
    }

    public String getDeliveryControllerName() {
        return deliveryControllerName;
    }

    public void setDeliveryControllerName(String deliveryControllerName) {
        this.deliveryControllerName = deliveryControllerName;
    }

    /**
     * This constructor is used only in service getMaterialLinesForWarehouse() in {@link MaterialHeaderRepository}.
     */
    public MaterialLineDTO(long id, long materialId, String pPartNumber, String pPartVersion, String pPartName, String pPartAffiliation,
            String pPartModification, Long quantity, String transportLabel, String whSiteId) {
        this.id = id;
        this.materialId = materialId;
        this.pPartNumber = pPartNumber;
        this.pPartVersion = pPartVersion;
        this.pPartName = pPartName;
        this.pPartAffiliation = pPartAffiliation;
        this.pPartModification = pPartModification;
        this.quantity = quantity;
        this.transportLabel = transportLabel;
        this.whSiteId = whSiteId;
    }
    
    public boolean isMarkPassedDate() {
        return markPassedDate;
    }

    public void setMarkPassedDate(boolean markPassedDate) {
        this.markPassedDate = markPassedDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getReferenceGroup() {
        return referenceGroup;
    }

    public void setReferenceGroup(String referenceGroup) {
        this.referenceGroup = referenceGroup;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
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

    public String getOutBoundLocationId() {
        return outBoundLocationId;
    }

    public void setOutBoundLocationId(String outBoundLocationId) {
        this.outBoundLocationId = outBoundLocationId;
    }

    public String getOutBoundLocationName() {
        return outBoundLocationName;
    }

    public void setOutBoundLocationName(String outBoundLocationName) {
        this.outBoundLocationName = outBoundLocationName;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getBinlocation() {
        return binlocation;
    }

    public void setBinlocation(long binlocation) {
        this.binlocation = binlocation;
    }

    public String getSuggestedBinLocation() {
        return suggestedBinLocation;
    }

    public void setSuggestedBinLocation(String suggestedBinLocation) {
        this.suggestedBinLocation = suggestedBinLocation;
    }
    
    public Long getSuggestedBinLocationId() {
        return suggestedBinLocationId;
    }

    public void setSuggestedBinLocationId(Long suggestedBinLocationId) {
        this.suggestedBinLocationId = suggestedBinLocationId;
    }

    public String getpPartAffiliation() {
        return pPartAffiliation;
    }

    public void setpPartAffiliation(String pPartAffiliation) {
        this.pPartAffiliation = pPartAffiliation;
    }

    public Long getPlacementID() {
        return placementID;
    }

    public void setPlacementID(Long placementID) {
        this.placementID = placementID;
    }

    public Double getStockBalance() {
        return stockBalance;
    }

    public void setStockBalance(Double stockBalance) {
        this.stockBalance = stockBalance;
    }

    public String getWhSiteId() {
        return whSiteId;
    }

    public void setWhSiteId(String whSiteId) {
        this.whSiteId = whSiteId;
    }

    public MaterialType getMaterialType() {
        return materialType;
    }

    public void setMaterialType(MaterialType materialType) {
        this.materialType = materialType;
    }

    public String getProcureUserId() {
        return procureUserId;
    }

    public void setProcureUserId(String procureUserId) {
        this.procureUserId = procureUserId;
    }

    public String getProcureTeam() {
        return procureTeam;
    }

    public void setProcureTeam(String procureTeam) {
        this.procureTeam = procureTeam;
    }

    public long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(long materialId) {
        this.materialId = materialId;
    }

    public String getTransportLabel() {
        return transportLabel;
    }

    public void setTransportLabel(String transportLabel) {
        this.transportLabel = transportLabel;
    }

    public void setpPartModification(String pPartModification) {
        this.pPartModification = pPartModification;
    }

    public String getpPartModification() {
        return pPartModification;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public long getRequestListID() {
        return requestListID;
    }

    public void setRequestListID(long requestListID) {
        this.requestListID = requestListID;
    }

    public long getRequestListVersion() {
        return requestListVersion;
    }

    public void setRequestListVersion(long requestListVersion) {
        this.requestListVersion = requestListVersion;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getRequiredDeliveryDate() {
        return requiredDeliveryDate;
    }

    public void setRequiredDeliveryDate(Date requiredDeliveryDate) {
        this.requiredDeliveryDate = requiredDeliveryDate;
    }

    public String getBinLocationCode() {
        return binLocationCode;
    }

    public void setBinLocationCode(String binLocationCode) {
        this.binLocationCode = binLocationCode;
    }

    public String getChangeRequestIds() {
        return changeRequestIds;
    }

    public void setChangeRequestIds(String changeRequestIds) {
        this.changeRequestIds = changeRequestIds;
    }

    public Date getOutboundStartDate() {
        return outboundStartDate;
    }

    public void setOutboundStartDate(Date outboundStartDate) {
        this.outboundStartDate = outboundStartDate;
    }

    public long getStoredQuantity() {
        return storedQuantity;
    }

    public void setStoredQuantity(long storedQuantity) {
        this.storedQuantity = storedQuantity;
    }

    public long getProcureLineId() {
        return procureLineId;
    }

    public void setProcureLineId(long procureLineId) {
        this.procureLineId = procureLineId;
    }

    public Long getAdditionalQuantity() {
        return additionalQuantity;
    }

    public void setAdditionalQuantity(Long additionalQuantity) {
        this.additionalQuantity = additionalQuantity;
    }

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    public String getBuildType() {
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    public String getMtrlRequestVersion() {
        return mtrlRequestVersion;
    }

    public void setMtrlRequestVersion(String mtrlRequestVersion) {
        this.mtrlRequestVersion = mtrlRequestVersion;
    }

    public String getMtrlRequestType() {
        return mtrlRequestType;
    }

    public void setMtrlRequestType(String mtrlRequestType) {
        this.mtrlRequestType = mtrlRequestType;
    }

    public String getDeliveryNoteNo() {
        return deliveryNoteNo;
    }

    public void setDeliveryNoteNo(String deliveryNoteNo) {
        this.deliveryNoteNo = deliveryNoteNo;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartAlias() {
        return partAlias;
    }

    public void setPartAlias(String partAlias) {
        this.partAlias = partAlias;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getPossiblePickQuantity() {
        return possiblePickQuantity;
    }

    public void setPossiblePickQuantity(Long possiblePickQuantity) {
        this.possiblePickQuantity = possiblePickQuantity;
    }

    public long getPickedQuantity() {
        return pickedQuantity;
    }

    public void setPickedQuantity(long pickedQuantity) {
        this.pickedQuantity = pickedQuantity;
    }

    public boolean isBalanceExceeded() {
        return balanceExceeded;
    }

    public Date getDeliveryNoteDate() {
        return deliveryNoteDate;
    }

    public void setDeliveryNoteDate(Date deliveryNoteDate) {
        this.deliveryNoteDate = deliveryNoteDate;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public String getDispatchNoteNo() {
        return dispatchNoteNo;
    }

    public void setDispatchNoteNo(String dispatchNoteNo) {
        this.dispatchNoteNo = dispatchNoteNo;
    }

    public void setBalanceExceeded(boolean balanceExceeded) {
        this.balanceExceeded = balanceExceeded;
    }

    public String getPickListCode() {
        return pickListCode;
    }

    public void setPickListCode(String pickListCode) {
        this.pickListCode = pickListCode;
    }

    public String getDeliveryAddressName() {
        return deliveryAddressName;
    }

    public void setDeliveryAddressName(String deliveryAddressName) {
        this.deliveryAddressName = deliveryAddressName;
    }

    public String getAssignedMaterialControllerId() {
        return assignedMaterialControllerId;
    }

    public void setAssignedMaterialControllerId(String assignedMaterialControllerId) {
        this.assignedMaterialControllerId = assignedMaterialControllerId;
    }

    public String getAssignedMaterialControllerName() {
        return assignedMaterialControllerName;
    }

    public void setAssignedMaterialControllerName(String assignedMaterialControllerName) {
        this.assignedMaterialControllerName = assignedMaterialControllerName;
    }

    public String getPartVersion() {
        return partVersion;
    }

    public void setPartVersion(String partVersion) {
        this.partVersion = partVersion;
    }

    public String getParmaName() {
        return parmaName;
    }

    public void setParmaName(String parmaName) {
        this.parmaName = parmaName;
    }

    public String getParmaID() {
        return parmaID;
    }

    public void setParmaID(String parmaID) {
        this.parmaID = parmaID;
    }

    public String getTransportationNo() {
        return transportationNo;
    }

    public void setTransportationNo(String transportationNo) {
        this.transportationNo = transportationNo;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public Double getBinlocationBalance() {
        return binlocationBalance;
    }

    public void setBinlocationBalance(Double binlocationBalance) {
        this.binlocationBalance = binlocationBalance;
    }

    public String getBuildName() {
        return buildName;
    }

    public void setBuildName(String buildName) {
        this.buildName = buildName;
    }

    public void setMaterialPartModification(String partModification) {
        this.materialPartModification = partModification;
    }
    
    public String getMaterialPartModification() {
        return materialPartModification;    
    }

    public void setMaterialPartAffiliation(String partAffiliation) {
        this.partAffiliation =  partAffiliation;        
    }

    public void setMaterialMailFormId(String mailFormId) {
        this.mailFormId = mailFormId;        
    }

    public void setMaterialFunctionGroup(String functionGroup) {
        this.functionGroup = functionGroup;        
    }

    public void setMaterialDesignResponsible(String designResponsible) {
        this.designResponsible = designResponsible;        
    }

    public void setMaterialModularHarness(String modularHarness) {
        this.modularHarness = modularHarness;        
    }

    public String getMaterialPartAffiliation() {
        return this.partAffiliation;
    }

    public String getMaterialMailFormId() {
        return this.mailFormId;
    }

    public String getMaterialFunctionGroup() {
        return this.functionGroup;
    }

    public String getMaterialDesignResponsible() {
        return this.designResponsible;
    }

    public String getMaterialModularHarness() {
        return this.modularHarness;
    }

    public void setFinanceHeaderCompanyCode(String companyCode) {
        this.financeHeaderCompanyCode = companyCode;
    }
    
    public String getFinanceHeaderCompanyCode() {
        return this.financeHeaderCompanyCode;
    }

    public void setFinanceHeaderGlAccount(String glAccount) {
        this.glAccount = glAccount;
    }

    public void setFinanceHeaderWbsCode(String wbsCode) {
        this.wbsCode = wbsCode;
    }

    public void setFinanceHeaderCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getFinanceHeaderGlAccount() {
        return this.glAccount;
    }

    public String getFinanceHeaderWbsCode() {
        return this.wbsCode;
    }

    public String getFinanceHeaderCostCenter() {
        return this.costCenter;
    }

    public void setOrderSuffix(String suffix) {
        this.orderSuffix = suffix;        
    }
    
    public String getOrderSuffix() {
        return orderSuffix;        
    }

    public void setProcureLineMaterialControllerTeam(String materialControllerTeam) {
        this.materialControllerTeam = materialControllerTeam;        
    }
    public String getProcureLineMaterialControllerTeam() {
        return this.materialControllerTeam;
        
    }

    public void setProcureLineRequisitionId(String requisitionId) {
        this.requisitionId = requisitionId;
    }

    public void setProcureLineProcureDate(Date procureDate) {
        this.procureDate  = procureDate;
    }

    public void setProcureLineReferenceGps(String referenceGps) {
        this.referenceGps = referenceGps;
    }
    
    public String getProcureLineRequisitionId() {
        return this.requisitionId;
    }

    public Date getProcureLineProcureDate() {
        return this.procureDate;
    }

    public String getProcureLineReferenceGps() {
        return this.referenceGps;
    }

    public void setProcureLineRequiredStaDate(Date requiredStaDate) {
        this.requiredStaDate = requiredStaDate;
    }
    public Date getProcureLineRequiredStaDate() {
       return this.requiredStaDate;
    }

    public void setProcureLineMaterialControllerId(String materialControllerId) {
        this.materialControllerId = materialControllerId;
    }
    
    public String getProcureLineMaterialControllerId() {
        return this.materialControllerId;
    }

    public void setProcureLineMaterialControllerName(String materialControllerName) {
        this.materialControllerName = materialControllerName;
    }
    
    public String getProcureLineMaterialControllerName() {
        return this.materialControllerName;
    }

    public void setMaterialHeaderVersionRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public void setMaterialHeaderVersionRequesterUserId(String requesterUserId) {
        this.requesterUserId = requesterUserId;
    }

    public void setMaterialHeaderVersionContactPersonId(String contactPersonId) {
        this.contactPersonId = contactPersonId;
    }

    public void setMaterialHeaderVersionContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public void setMaterialLineExpirationDate(Date expirationDate2) {
        this.expirationDate2 = expirationDate2;
    }

    public String getMaterialHeaderVersionRequesterName() {
        return this.requesterName;
    }

    public String getMaterialHeaderVersionRequesterUserId() {
        return this.requesterUserId;
    }

    public String getMaterialHeaderVersionContactPersonId() {
        return this.contactPersonId;
    }

    public String getMaterialHeaderVersionContactPersonName() {
        return this.contactPersonName;
    }

    public Date getMaterialLineExpirationDate() {
        return this.expirationDate2;
    }

    public void setRequestListDeliveryAddressId(String deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }
    public String getRequestListDeliveryAddressId() {
        return this.deliveryAddressId;
    }

    public void setStorageRoomName(String storageRoomName) {
        this.storageRoomName = storageRoomName;
    }

    public String getStorageRoomName() {
        return this.storageRoomName;
    }

    public void setFinanceHeaderInternalOrderNoSAP(String internalOrderNoSAP) {
        this.internalOrderNoSAP = internalOrderNoSAP;
        
    }
    public String getFinanceHeaderInternalOrderNoSAP() {
        return internalOrderNoSAP;        
    }

    public boolean isAlertPartVersion() {
        return alertPartVersion;
    }

    public void setAlertPartVersion(boolean alertPartVersion) {
        this.alertPartVersion = alertPartVersion;
    }

    public long getMaterialOwnerId() {
        return materialOwnerId;
    }

    public void setMaterialOwnerId(long materialOwnerId) {
        this.materialOwnerId = materialOwnerId;
    }

    public long getDeliveryNoteLineId() {
        return deliveryNoteLineId;
    }

    public void setDeliveryNoteLineId(long deliveryNoteLineId) {
        this.deliveryNoteLineId = deliveryNoteLineId;
    }
    
    public Date getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(Date expectedDate) {
        this.expectedDate = expectedDate;
    }
}

