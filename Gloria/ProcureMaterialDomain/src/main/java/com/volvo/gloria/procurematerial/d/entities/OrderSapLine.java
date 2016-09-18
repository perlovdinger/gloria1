package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Entity Class for ORDER_SAP_LINE.
 */
@Entity
@Table(name = "ORDER_SAP_LINE")
public class OrderSapLine implements Serializable {

    private static final long serialVersionUID = -381025946505560316L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_SAP_LINE_OID")
    private long orderSapLineOID;

    @Version
    private long version;

    @ManyToOne
    @JoinColumn(name = "ORDER_SAP_OID")
    private OrderSap orderSap;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "orderSapLine")
    private List<OrderSapAccounts> orderSapAccounts = new ArrayList<OrderSapAccounts>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "orderSapLine")
    private List<OrderSapSchedule> orderSapSchedules = new ArrayList<OrderSapSchedule>();

    @OneToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "ORDER_LINE_VERSION_OID")
    private OrderLineVersion orderLineVersion;

    private long requisitionId;

    private long purchaseOrderitem;
    private String action;
    private String orderReference;
    private String partNumber;
    private String shortText;
    private Date cancelDate;
    private String plant;
    private String currentBuyer;
    private String materialGroup;
    private long quantity;
    private String isoPurchaseOrderUnit;
    private String isoOrderPriceUnit;
    private double netPrice;
    private String priceUnit;
    private boolean estimatedPriceIndicator;
    private String taxCode;
    private String accountAssignmentCategory;
    private String unlimitedDeliveryIndicator;
    private String grIndicator;
    private String nonValuedGrIndicator;
    private String irIndicator;
    private String acknowledgementNumber;
    private String purchaseRequisitionNumber;

    public long getOrderSapLineOID() {
        return orderSapLineOID;
    }

    public void setOrderSapLineOID(long orderSapLineOID) {
        this.orderSapLineOID = orderSapLineOID;
    }

    public long getRequisitionId() {
        return requisitionId;
    }

    public void setRequisitionId(long requisitionId) {
        this.requisitionId = requisitionId;
    }

    public long getPurchaseOrderitem() {
        return purchaseOrderitem;
    }

    public void setPurchaseOrderitem(long purchaseOrderitem) {
        this.purchaseOrderitem = purchaseOrderitem;
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

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
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

    public double getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(double netPrice) {
        this.netPrice = netPrice;
    }

    public String getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
    }

    public boolean isEstimatedPriceIndicator() {
        return estimatedPriceIndicator;
    }

    public void setEstimatedPriceIndicator(boolean estimatedPriceIndicator) {
        this.estimatedPriceIndicator = estimatedPriceIndicator;
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

    public String getUnlimitedDeliveryIndicator() {
        return unlimitedDeliveryIndicator;
    }

    public void setUnlimitedDeliveryIndicator(String unlimitedDeliveryIndicator) {
        this.unlimitedDeliveryIndicator = unlimitedDeliveryIndicator;
    }

    public String getGrIndicator() {
        return grIndicator;
    }

    public void setGrIndicator(String grIndicator) {
        this.grIndicator = grIndicator;
    }

    public String getNonValuedGrIndicator() {
        return nonValuedGrIndicator;
    }

    public void setNonValuedGrIndicator(String nonValuedGrIndicator) {
        this.nonValuedGrIndicator = nonValuedGrIndicator;
    }

    public String getIrIndicator() {
        return irIndicator;
    }

    public void setIrIndicator(String irIndicator) {
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

    public OrderSap getOrderSap() {
        return orderSap;
    }

    public void setOrderSap(OrderSap orderSap) {
        this.orderSap = orderSap;
    }

    public List<OrderSapAccounts> getOrderSapAccounts() {
        return orderSapAccounts;
    }

    public void setOrderSapAccounts(List<OrderSapAccounts> orderSapAccounts) {
        this.orderSapAccounts = orderSapAccounts;
    }

    public List<OrderSapSchedule> getOrderSapSchedules() {
        return orderSapSchedules;
    }

    public void setOrderSapSchedules(List<OrderSapSchedule> orderSapSchedules) {
        this.orderSapSchedules = orderSapSchedules;
    }

    public long getVersion() {
        return version;
    }

    public OrderLineVersion getOrderLineVersion() {
        return orderLineVersion;
    }

    public void setOrderLineVersion(OrderLineVersion orderLineVersion) {
        this.orderLineVersion = orderLineVersion;
    }
}
