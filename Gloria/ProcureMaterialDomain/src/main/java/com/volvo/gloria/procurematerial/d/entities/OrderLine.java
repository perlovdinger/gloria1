package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.volvo.gloria.procurematerial.c.CompleteType;
import com.volvo.gloria.procurematerial.c.QiMarking;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;
import com.volvo.gloria.util.validators.GloriaLongValue;

/**
 * Entity Class for Order Line.
 */
@Entity
@Table(name = "ORDER_LINE")
public class OrderLine implements Serializable {

    private static final long serialVersionUID = 1277402837254268881L;
    private static final int MAX_QUANTITY = 99999;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_LINE_OID")
    private long orderLineOID;

    @Version
    private long version;

    @ManyToOne
    @JoinColumn(name = "ORDER_OID")
    private Order order;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH }, mappedBy = "orderLine")
    private List<Material> materials = new ArrayList<Material>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "orderLine")
    private List<OrderLineLog> orderLineLog = new ArrayList<OrderLineLog>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "orderLine")
    private List<DeliveryNoteLine> deliveryNoteLines = new ArrayList<DeliveryNoteLine>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "orderLine")
    @OrderBy("expectedDate ASC")
    private List<DeliverySchedule> deliverySchedule = new ArrayList<DeliverySchedule>();

    /**
     * Fetch type must be lazy - otherwise we get an exception in Not Received.
     * This is due to a bug in OpenJPA. Fixed in OpenJPA 2.2.3 and 2.4.0. /Daniel
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "orderLine")
    private List<OrderLineVersion> orderLineVersions = new ArrayList<OrderLineVersion>();
    
    @OneToOne(cascade = CascadeType.MERGE)
    private OrderLineVersion current;

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH })
    @JoinColumn(name = "PROCURE_LINE_OID")
    private ProcureLine procureLine;
    
    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH })
    @JoinColumn(name = "ORDER_LINE_LAST_MODIFIED_OID")
    private OrderLineLastModified orderLineLastModified;

    @Enumerated(EnumType.STRING)
    private OrderLineStatus status;
    @Enumerated(EnumType.STRING)
    private CompleteType completeType;
    private String partAffiliation;
    private String partNumber; 
    private String partName;
    private String supplierPartNo;
    private String countryOfOrigin;
    private Date priceStartDateTime;
    private String currency;
    private String unitOfMeasure;
    private String shipToPartyId;
    private String shipToPartyName;
    private String freightTermCode;
    private String paymentTerm;
    private String buyerPartyId;
    private String requisitionId;
    private String projectId;    
    private String domesticInvoicesTo;
    private String internationalInvoicesTo;
    private String freeText;    
    @GloriaLongValue(max = MAX_QUANTITY)
    private long possibleToReceiveQuantity;
    @GloriaLongValue(max = MAX_QUANTITY)
    private long receivedQuantity;   
    @Enumerated(EnumType.STRING)
    private QiMarking qiMarking;
    private Date earliestExpectedDate;
    private boolean deliveryDeviation;    
    private String deliveryControllerUserId;
    private String deliveryControllerUserName;
  //  @Comment: New column for assitance of migrated orders, to identify the already paid qty.
    private long paidQuantity;
    
    @OneToOne(cascade = CascadeType.MERGE)
    private DeliveryNoteLine first;

    @Transient
    private long alreadyDecreasedQty;    
    private boolean staAgreedDateUpdated;
    private boolean contentEdited;
    private boolean orderlinePaidOnMigration;
   

    public long getOrderLineOID() {
        return orderLineOID;
    }

    public void setOrderLineOID(long orderLineOID) {
        this.orderLineOID = orderLineOID;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderLineLog> getOrderLineLog() {
        return orderLineLog;
    }

    public void setOrderLineLog(List<OrderLineLog> orderLineLog) {
        this.orderLineLog = orderLineLog;
    }

    public List<DeliveryNoteLine> getDeliveryNoteLines() {
        return deliveryNoteLines;
    }

    public void setDeliveryNoteLines(List<DeliveryNoteLine> deliveryNoteLines) {
        this.deliveryNoteLines = deliveryNoteLines;
    }

    public List<DeliverySchedule> getDeliverySchedule() {
        return deliverySchedule;
    }

    public void setDeliverySchedule(List<DeliverySchedule> deliverySchedule) {
        this.deliverySchedule = deliverySchedule;
    }

    public String getPartAffiliation() {
        return partAffiliation;
    }

    public void setPartAffiliation(String partAffiliation) {
        this.partAffiliation = partAffiliation;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getRequisitionId() {
        return requisitionId;
    }

    public void setRequisitionId(String requisitionId) {
        this.requisitionId = requisitionId;
    }

    public String getSupplierPartNo() {
        return supplierPartNo;
    }

    public void setSupplierPartNo(String supplierPartNo) {
        this.supplierPartNo = supplierPartNo;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public Date getPriceStartDateTime() {
        return priceStartDateTime;
    }

    public void setPriceStartDateTime(Date priceStartDateTime) {
        this.priceStartDateTime = priceStartDateTime;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getShipToPartyId() {
        return shipToPartyId;
    }

    public void setShipToPartyId(String shipToPartyId) {
        this.shipToPartyId = shipToPartyId;
    }

    public String getShipToPartyName() {
        return shipToPartyName;
    }

    public void setShipToPartyName(String shipToPartyName) {
        this.shipToPartyName = shipToPartyName;
    }

    public String getFreightTermCode() {
        return freightTermCode;
    }

    public void setFreightTermCode(String freightTermCode) {
        this.freightTermCode = freightTermCode;
    }

    public String getPaymentTerm() {
        return paymentTerm;
    }

    public void setPaymentTerm(String paymentTerm) {
        this.paymentTerm = paymentTerm;
    }

    public String getBuyerPartyId() {
        return buyerPartyId;
    }

    public void setBuyerPartyId(String buyerPartyId) {
        this.buyerPartyId = buyerPartyId;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDomesticInvoicesTo() {
        return domesticInvoicesTo;
    }

    public void setDomesticInvoicesTo(String domesticInvoicesTo) {
        this.domesticInvoicesTo = domesticInvoicesTo;
    }

    public String getInternationalInvoicesTo() {
        return internationalInvoicesTo;
    }

    public void setInternationalInvoicesTo(String internationalInvoicesTo) {
        this.internationalInvoicesTo = internationalInvoicesTo;
    }

    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    public long getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(long receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public long getPossibleToReceiveQuantity() {
        return possibleToReceiveQuantity;
    }

    public void setPossibleToReceiveQuantity(long possibleToReceiveQuantity) {
        this.possibleToReceiveQuantity = possibleToReceiveQuantity;
    }

    public OrderLineStatus getStatus() {
        return status;
    }

    public void setStatus(OrderLineStatus status) {
        this.status = status;
    }
    
    public CompleteType getCompleteType() {
        return completeType;
    }
    
    public void setCompleteType(CompleteType completeType) {
        this.completeType = completeType;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public List<OrderLineVersion> getOrderLineVersions() {
        return orderLineVersions;
    }

    public void setOrderLineVersions(List<OrderLineVersion> orderLineVersions) {
        this.orderLineVersions = orderLineVersions;
    }
    
    public QiMarking getQiMarking() {
        return qiMarking;
    }
    
    public void setQiMarking(QiMarking qiMarking) {
        this.qiMarking = qiMarking;
    }
    
    public ProcureLine getProcureLine() {
        return procureLine;
    }
    
    public void setProcureLine(ProcureLine procureLine) {
        this.procureLine = procureLine;
    }

    public Date getEarliestExpectedDate() {
        return earliestExpectedDate;
    }

    public void setEarliestExpectedDate(Date earliestExpectedDate) {
        this.earliestExpectedDate = earliestExpectedDate;
    }
    
    public boolean isDeliveryDeviation() {
        return deliveryDeviation;
    }
    
    public void setDeliveryDeviation(boolean deliveryDeviation) {
        this.deliveryDeviation = deliveryDeviation;
    }
    
    public OrderLineVersion getCurrent() {
        return current;
    }

    public void setCurrent(OrderLineVersion current) {
        this.current = current;
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

    public OrderLineLastModified getOrderLineLastModified() {
        return orderLineLastModified;
    }

    public void setOrderLineLastModified(OrderLineLastModified orderLineLastModified) {
        this.orderLineLastModified = orderLineLastModified;
    }

    public long getAlreadyDecreasedQty() {
        return alreadyDecreasedQty;
    }

    public void setAlreadyDecreasedQty(long alreadyDecreasedQty) {
        this.alreadyDecreasedQty = alreadyDecreasedQty;
    }

    public long getPaidQuantity() {
        return paidQuantity;
    }

    public void setPaidQuantity(long paidQuantity) {
        this.paidQuantity = paidQuantity;
    }

    public boolean isStaAgreedDateUpdated() {
        return staAgreedDateUpdated;
    }

    public void setStaAgreedDateUpdated(boolean staAgreedDateUpdated) {
        this.staAgreedDateUpdated = staAgreedDateUpdated;        
    }
    
    public DeliveryNoteLine getFirst() {
        return first;
    }
    
    public void setFirst(DeliveryNoteLine first) {
        this.first = first;
    }
    
    public boolean isContentEdited() {
        return contentEdited;
    }

    public void setContentEdited(boolean contentEdited) {
        this.contentEdited = contentEdited;
    }

    public boolean isOrderlinePaidOnMigration() {
        return orderlinePaidOnMigration;
    }

    public void setOrderlinePaidOnMigration(boolean orderlinePaidOnMigration) {
        this.orderlinePaidOnMigration = orderlinePaidOnMigration;
    }
    
}
