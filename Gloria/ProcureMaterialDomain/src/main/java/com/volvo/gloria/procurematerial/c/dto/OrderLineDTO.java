package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * DTO class to manage Order Line details.
 * 
 */
public class OrderLineDTO implements Serializable, PageResults {

    private static final long serialVersionUID = -8807690823656016960L;

    private Long id;
    private long version;
    private String partNumber;
    private String partVersion;
    private Long perQuantity;
    private Double amount;
    private String currency;
    private String freightTermCode;
    private Long orderId;
    private String orderNo;
    private String supplierId;
    private String supplierName;
    private Long quantity;
    private Long receivedQuantity;
    private String partName;
    private String referenceGroups;
    private Date orderStaDate;
    private Date staAcceptedDate;
    private Date staAgreedDate;
    private String freeText;
    private String procureInfo;
    private Date orderDateTime;
    private String buyerId;
    private String buyerName;
    private String projectId;
    private long allowedQuantity;
    private String unitOfMeasure;
    private Date buildStartDate;
    private String dfuObjectNumber;
    private boolean markedAsComplete;
    private String wbsCode;
    private String costCenter;
    private String glAccount;
    private String internalOrderNo;
    private boolean directSend;
    private String consignorId;
    private String consignorName;
    private boolean inStock;
    private String qiMarking;
    private String referenceId;
    private String partAlias;

    private String projectNumber;
    private boolean orderStaChanged;
    private String partAffiliation;
    private String status;
    private Date expectedDate;
    private Long expectedQty;
    private String statusFlag;
    private String internalExternal;
    private long additionalQuantity;
    private long previousQuantity;
    private String lastModifiedByUserId;
    private Date lastModifiedDate;
    private String mailFormIds;
    private String internalOrderNoSAP;
    private double unitPrice;
    private String reference;
    private String requisitionId;
    private Date procureDate;
    private boolean deliveryDeviation;
    private String partModification;
    private String warehouseComment;
    private String completeType;

    private String deliveryControllerUserId;
    private String deliveryControllerUserName;
    private String deliveryControllerTeam;
    
    private String materialControllerUserId;
    private String materialControllerUserName;

    private String suffix;
    private Date requiredStaDate;
    private boolean alertQuantiy;
    private boolean alertPartVersion;
    private Date versionDate;

    private Date plannedDispatchDate;
    private String materialPartModification;
    private Date eventTime;
    private long possibleToReceiveQty;
    private long unitOfPrice;
    
    private String qualityDocumentName;
    private boolean contentEdited;

    public String getQualityDocumentName() {
        return qualityDocumentName;
    }

    public void setQualityDocumentName(String qualityDocumentName) {
        this.qualityDocumentName = qualityDocumentName;
    }

    private List<DeliveryScheduleDTO> deliveryScheduleDTOs = new ArrayList<DeliveryScheduleDTO>();
    
    public String getMaterialPartModification() {
        return materialPartModification;
    }

    public void setMaterialPartModification(String materialPartModification) {
        this.materialPartModification = materialPartModification;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartVersion() {
        return partVersion;
    }

    public void setPartVersion(String partVersion) {
        this.partVersion = partVersion;
    }

    public Long getPerQuantity() {
        return perQuantity;
    }

    public void setPerQuantity(Long perQuantity) {
        this.perQuantity = perQuantity;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    public String getFreightTermCode() {
        return freightTermCode;
    }

    public void setFreightTermCode(String freightTermCode) {
        this.freightTermCode = freightTermCode;
    }

    public boolean isOrderStaChanged() {
        return orderStaChanged;
    }

    public void setOrderStaChanged(boolean orderStaChanged) {
        this.orderStaChanged = orderStaChanged;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Date getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(Date orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public Date getStaAcceptedDate() {
        return staAcceptedDate;
    }

    public void setStaAcceptedDate(Date staAcceptedDate) {
        this.staAcceptedDate = staAcceptedDate;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getPartAffiliation() {
        return partAffiliation;
    }

    public void setPartAffiliation(String partAffiliation) {
        this.partAffiliation = partAffiliation;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(Long receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public Date getStaAgreedDate() {
        return staAgreedDate;
    }

    public void setStaAgreedDate(Date staAgreedDate) {
        this.staAgreedDate = staAgreedDate;
    }

    public String getReferenceGroups() {
        return referenceGroups;
    }

    public void setReferenceGroups(String referenceGroups) {
        this.referenceGroups = referenceGroups;
    }

    public Date getBuildStartDate() {
        return buildStartDate;
    }

    public void setBuildStartDate(Date buildStartDate) {
        this.buildStartDate = buildStartDate;
    }

    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    public String getProcureInfo() {
        return procureInfo;
    }

    public void setProcureInfo(String procureInfo) {
        this.procureInfo = procureInfo;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public long getAllowedQuantity() {
        return allowedQuantity;
    }

    public void setAllowedQuantity(long allowedQuantity) {
        this.allowedQuantity = allowedQuantity;
    }

    public String getDfuObjectNumber() {
        return dfuObjectNumber;
    }

    public void setDfuObjectNumber(String dfuObjectNumber) {
        this.dfuObjectNumber = dfuObjectNumber;
    }

    public boolean isMarkedAsComplete() {
        return markedAsComplete;
    }

    public void setMarkedAsComplete(boolean markedAsComplete) {
        this.markedAsComplete = markedAsComplete;
    }

    public String getWbsCode() {
        return wbsCode;
    }

    public void setWbsCode(String wbsCode) {
        this.wbsCode = wbsCode;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getGlAccount() {
        return glAccount;
    }

    public void setGlAccount(String glAccount) {
        this.glAccount = glAccount;
    }

    public String getInternalOrderNo() {
        return internalOrderNo;
    }

    public void setInternalOrderNo(String internalOrderNo) {
        this.internalOrderNo = internalOrderNo;
    }

    public boolean isDirectSend() {
        return directSend;
    }

    public void setDirectSend(boolean directSend) {
        this.directSend = directSend;
    }

    public Date getOrderStaDate() {
        return orderStaDate;
    }

    public void setOrderStaDate(Date orderStaDate) {
        this.orderStaDate = orderStaDate;
    }

    public Long getExpectedQty() {
        return expectedQty;
    }

    public void setExpectedQty(Long expectedQty) {
        this.expectedQty = expectedQty;
    }

    public Date getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(Date expectedDate) {
        this.expectedDate = expectedDate;
    }

    public String getStatusFlag() {
        return statusFlag;
    }

    public void setStatusFlag(String statusFlag) {
        this.statusFlag = statusFlag;
    }

    public String getConsignorId() {
        return consignorId;
    }

    public void setConsignorId(String consignorId) {
        this.consignorId = consignorId;
    }

    public String getConsignorName() {
        return consignorName;
    }

    public void setConsignorName(String consignorName) {
        this.consignorName = consignorName;
    }

    public String getInternalExternal() {
        return internalExternal;
    }

    public void setInternalExternal(String internalExternal) {
        this.internalExternal = internalExternal;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public String getQiMarking() {
        return qiMarking;
    }

    public void setQiMarking(String qiMarking) {
        this.qiMarking = qiMarking;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getPartAlias() {
        return partAlias;
    }

    public void setPartAlias(String partAlias) {
        this.partAlias = partAlias;
    }

    public long getAdditionalQuantity() {
        return additionalQuantity;
    }

    public void setAdditionalQuantity(long additionalQuantity) {
        this.additionalQuantity = additionalQuantity;
    }

    public long getPreviousQuantity() {
        return previousQuantity;
    }

    public void setPreviousQuantity(long previousQuantity) {
        this.previousQuantity = previousQuantity;
    }

    public String getLastModifiedByUserId() {
        return lastModifiedByUserId;
    }

    public void setLastModifiedByUserId(String lastModifiedByUserId) {
        this.lastModifiedByUserId = lastModifiedByUserId;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getMailFormIds() {
        return mailFormIds;
    }

    public void setMailFormIds(String mailFormIds) {
        this.mailFormIds = mailFormIds;
    }

    public String getInternalOrderNoSAP() {
        return internalOrderNoSAP;
    }

    public void setInternalOrderNoSAP(String internalOrderNoSAP) {
        this.internalOrderNoSAP = internalOrderNoSAP;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getRequisitionId() {
        return requisitionId;
    }

    public void setRequisitionId(String requisitionId) {
        this.requisitionId = requisitionId;
    }

    public Date getProcureDate() {
        return procureDate;
    }

    public void setProcureDate(Date procureDate) {
        this.procureDate = procureDate;
    }

    public boolean isDeliveryDeviation() {
        return deliveryDeviation;
    }

    public String getDeliveryControllerUserId() {
        return deliveryControllerUserId;
    }

    public void setDeliveryControllerUserId(String deliveryControllerUserId) {
        this.deliveryControllerUserId = deliveryControllerUserId;
    }

    public String getDeliveryControllerUserName() {
        return deliveryControllerUserName;
    }

    public void setDeliveryControllerUserName(String deliveryControllerUserName) {
        this.deliveryControllerUserName = deliveryControllerUserName;
    }

    public String getDeliveryControllerTeam() {
        return deliveryControllerTeam;
    }

    public void setDeliveryControllerTeam(String deliveryControllerTeam) {
        this.deliveryControllerTeam = deliveryControllerTeam;
    }
    
    public String getMaterialControllerUserId() {
        return materialControllerUserId;
    }

    public void setMaterialControllerUserId(String materialControllerUserId) {
        this.materialControllerUserId = materialControllerUserId;
    }

    public String getMaterialControllerUserName() {
        return materialControllerUserName;
    }

    public void setMaterialControllerUserName(String materialControllerUserName) {
        this.materialControllerUserName = materialControllerUserName;
    }

    public void setDeliveryDeviation(boolean deliveryDeviation) {
        this.deliveryDeviation = deliveryDeviation;
    }

    public String getPartModification() {
        return partModification;
    }

    public void setPartModification(String partModification) {
        this.partModification = partModification;
    }

    public String getWarehouseComment() {
        return warehouseComment;
    }

    public void setWarehouseComment(String warehouseComment) {
        this.warehouseComment = warehouseComment;
    }

    public String getCompleteType() {
        return completeType;
    }

    public void setCompleteType(String completeType) {
        this.completeType = completeType;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Date getRequiredStaDate() {
        return requiredStaDate;
    }

    public void setRequiredStaDate(Date requiredStaDate) {
        this.requiredStaDate = requiredStaDate;
    }

    public boolean isAlertQuantiy() {
        return alertQuantiy;
    }

    public void setAlertQuantiy(boolean alertQuantiy) {
        this.alertQuantiy = alertQuantiy;
    }

    public boolean isAlertPartVersion() {
        return alertPartVersion;
    }

    public void setAlertPartVersion(boolean alertPartVersion) {
        this.alertPartVersion = alertPartVersion;
    }

    public Date getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(Date versionDate) {
        this.versionDate = versionDate;
    }

    public Date getPlannedDispatchDate() {
        return plannedDispatchDate;
    }

    public void setPlannedDispatchDate(Date plannedDispatchDate) {
        this.plannedDispatchDate = plannedDispatchDate;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public Date getEventTime() {
        return this.eventTime;
    }

    public long getPossibleToReceiveQty() {
        return possibleToReceiveQty;
    }

    public void setPossibleToReceiveQty(long possibleToReceiveQty) {
        this.possibleToReceiveQty = possibleToReceiveQty;
    }

    public long getUnitOfPrice() {
        return unitOfPrice;
    }

    public void setUnitOfPrice(long unitOfPrice) {
        this.unitOfPrice = unitOfPrice;
    }
    
    public List<DeliveryScheduleDTO> getDeliveryScheduleDTOs() {
        return deliveryScheduleDTOs;
    }
    
    public void setDeliveryScheduleDTOs(List<DeliveryScheduleDTO> deliveryScheduleDTOs) {
        this.deliveryScheduleDTOs = deliveryScheduleDTOs;
    }
    
    public boolean isContentEdited() {
        return contentEdited;
    }

    public void setContentEdited(boolean contentEdited) {
        this.contentEdited = contentEdited;
    }
}
