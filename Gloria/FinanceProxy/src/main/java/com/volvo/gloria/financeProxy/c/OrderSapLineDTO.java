package com.volvo.gloria.financeProxy.c;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderSapLineDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6096129724997459673L;

    private long requisitionId;
    private String purchaseOrderItem;
    private String action;
    private String orderReference;
    private String partNumber;
    private String shortText;
    private Date cancelDate;
    private String plant;
    private String currentBuyer;
    private String materialGroup;
    private BigDecimal quantity;
    private String isoPurchaseOrderUnit;
    private String isoOrderPriceUnit;
    private BigDecimal netPrice;
    private BigDecimal priceUnit;
    private boolean isEstimatedPriceIndicator;
    private String taxCode;
    private String accountAssignmentCategory;
    private boolean unlimitedDeliveryIndicator;
    private boolean grIndicator;
    private boolean nonValuedGrIndicator;
    private boolean irIndicator;
    private String acknowledgementNumber;
    private String purchaseRequisitionNumber;

    private List<OrderSapAccountsDTO> orderSapAccounts = new ArrayList<OrderSapAccountsDTO>();

    private List<OrderSapScheduleDTO> orderSapSchedule = new ArrayList<OrderSapScheduleDTO>();

    public long getRequisitionId() {
        return requisitionId;
    }

    public void setRequisitionId(long requisitionId) {
        this.requisitionId = requisitionId;
    }

    public String getPurchaseOrderItem() {
        return purchaseOrderItem;
    }

    public void setPurchaseOrderItem(String purchaseOrderItem) {
        this.purchaseOrderItem = purchaseOrderItem;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getShortText() {
        return shortText;
    }

    public void setShortText(String shortText) {
        this.shortText = shortText;
    }

    public Date getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(Date cancelDate) {
        this.cancelDate = cancelDate;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getCurrentBuyer() {
        return currentBuyer;
    }

    public void setCurrentBuyer(String currentBuyer) {
        this.currentBuyer = currentBuyer;
    }

    public String getMaterialGroup() {
        return materialGroup;
    }

    public void setMaterialGroup(String materialGroup) {
        this.materialGroup = materialGroup;
    }

   

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getIsoPurchaseOrderUnit() {
        return isoPurchaseOrderUnit;
    }

    public void setIsoPurchaseOrderUnit(String isoPurchaseOrderUnit) {
        this.isoPurchaseOrderUnit = isoPurchaseOrderUnit;
    }

    public String getIsoOrderPriceUnit() {
        return isoOrderPriceUnit;
    }

    public void setIsoOrderPriceUnit(String isoOrderPriceUnit) {
        this.isoOrderPriceUnit = isoOrderPriceUnit;
    }

    public BigDecimal getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(BigDecimal priceUnit) {
        this.priceUnit = priceUnit;
    }

    public void setEstimatedPriceIndicator(boolean isEstimatedPriceIndicator) {
        this.isEstimatedPriceIndicator = isEstimatedPriceIndicator;
    }
    
    public boolean isEstimatedPriceIndicator() {
        return isEstimatedPriceIndicator;
    }
    
    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getAccountAssignmentCategory() {
        return accountAssignmentCategory;
    }

    public void setAccountAssignmentCategory(String accountAssignmentCategory) {
        this.accountAssignmentCategory = accountAssignmentCategory;
    }

    public boolean isUnlimitedDeliveryIndicator() {
        return unlimitedDeliveryIndicator;
    }

    public void setUnlimitedDeliveryIndicator(boolean unlimitedDeliveryIndicator) {
        this.unlimitedDeliveryIndicator = unlimitedDeliveryIndicator;
    }

    public boolean isGrIndicator() {
        return grIndicator;
    }

    public void setGrIndicator(boolean grIndicator) {
        this.grIndicator = grIndicator;
    }

    public boolean isNonValuedGrIndicator() {
        return nonValuedGrIndicator;
    }

    public void setNonValuedGrIndicator(boolean nonValuedGrIndicator) {
        this.nonValuedGrIndicator = nonValuedGrIndicator;
    }

    public boolean isIrIndicator() {
        return irIndicator;
    }

    public void setIrIndicator(boolean irIndicator) {
        this.irIndicator = irIndicator;
    }

    public String getAcknowledgementNumber() {
        return acknowledgementNumber;
    }

    public void setAcknowledgementNumber(String acknowledgementNumber) {
        this.acknowledgementNumber = acknowledgementNumber;
    }

    public String getPurchaseRequisitionNumber() {
        return purchaseRequisitionNumber;
    }

    public void setPurchaseRequisitionNumber(String purchaseRequisitionNumber) {
        this.purchaseRequisitionNumber = purchaseRequisitionNumber;
    }

    public List<OrderSapAccountsDTO> getOrderSapAccounts() {
        return orderSapAccounts;
    }

    public void setOrderSapAccounts(List<OrderSapAccountsDTO> orderSapAccounts) {
        this.orderSapAccounts = orderSapAccounts;
    }

    public List<OrderSapScheduleDTO> getOrderSapSchedule() {
        return orderSapSchedule;
    }

    public void setOrderSapSchedule(List<OrderSapScheduleDTO> orderSapSchedule) {
        this.orderSapSchedule = orderSapSchedule;
    }

    public BigDecimal getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(BigDecimal netPrice) {
        this.netPrice = netPrice;
    }

    
}
