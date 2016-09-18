package com.volvo.gloria.procurematerial.util.migration.c.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;

/**
 * DTO for Order migration.
 */
public class OrderMigrationDTO {
    private static final String SEMICOLON = ";";
    private static final String COMMA = ",";
    private int id;
    private String itemId;
    private String lineType;
    private String orderNumber;
    private String orderDateOriginal;
    private Date orderDate;
    private String suffix;
    private String partQualifier;
    private String partNumber;
    private String description;
    private String partVersion;
    private long orderedQuantity;
    private String unitOfMeasure;
    private String priceOriginal;
    private double price;
    private String unitOfPrice;
    private String amountOriginal;
    private int amount;
    private String currency;
    private String materialUseridOriginal;
    private String materialUserid;
    private String buyerCode;
    private String buyerName;
    private String purchasingOrganization;
    private String project;
    private String costCenter;
    private String glAccount;
    private String wbsElement;
    private String supplierCode;
    private String supplierName;
    private String inspection;
    private String internalFlow;
    private String issuerId;
    private String ordererId;
    private String note;
    private long requisitionNumber;
    private String shipArriveDateOriginal;
    private Date shipArriveDate;
    private String acceptedStaDateOriginal;
    private Date acceptedStaDate;
    private String agreedStaDateOriginal;
    private Date agreedStaDate;
    private String expectedStaDateOriginal;
    private Date expectedStaDate;
    private String orderLineStatusOriginal;
    private String intExtOrder;
    private String orderFromGps;
    private String orderLinePaid;
    private String possibleToReceiveQuantityOriginal;
    private long possibleToReceiveQuantity;
    private String testObjectsQtyOriginal;
    private boolean isExternal;
    private boolean migrated = false;
    private boolean isSendPPO = false;
    private boolean isSendGM = false;
    private String companyCode;
    private String shipToId;
    private String deliveryControllerTeam;
    private Material material;
    private String materialControllerTeam;
    private long totalTestobjectsQty;
    private String testObjectstotalQty;
    private String requisitionNumberOriginal;
    private String orderedQtyoriginal;
    private String contactPersonName;
    private String contactPersonEmail;
    private String paidQuantityOriginal;
    private long paidQuantity;
    private String ppaId;
    private String reference;
    private String sparePartQuantityOriginal;
    private long sparePartQuantity;
    private String issuerName;
    private String issuerEmail;
    private String orderSiteId;
    private String orderSiteOriginal;
    private String referenceIds;
    private String deliveryControllerUserId;
    private String deliveryControllerUserName;
    private String orderIdGps;
    private String supplierPartNo;
    private String agreedSTALastUpdatedOriginal;
    private Date agreedSTALastUpdated;
    private String modularHarness;
    private String procureDateOriginal;
    private Date procureDate;
    private String objectInformation;
    private String orderInformation;
    private String warehouseSite;
    private String testObjectsPulledOriginal;
    private String orderState;
    private String orderTypeOriginal;
    private String orderType;

    private List<OrderReceivedDTO> orderReceivedList = new ArrayList<OrderReceivedDTO>();
    private List<String> testObjectsQty = new ArrayList<String>();
    private List<String> testObjectsPulled = new ArrayList<String>();

    private StringBuilder reason = new StringBuilder();
    private StringBuilder reasonIT = new StringBuilder();

    // flag to identify valid orders
    private boolean valid = true;

    private Set<String> packingSlipNumbersForReceivals = new HashSet<String>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getLineType() {
        return lineType;
    }

    public void setLineType(String lineType) {
        this.lineType = lineType;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderDateOriginal() {
        return orderDateOriginal;
    }

    public void setOrderDateOriginal(String orderDateOriginal) {
        this.orderDateOriginal = orderDateOriginal;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPartVersion() {
        return partVersion;
    }

    public void setPartVersion(String partVersion) {
        this.partVersion = partVersion;
    }

    public long getOrderedQuantity() {
        return orderedQuantity;
    }

    public void setOrderedQuantity(long orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }

    public long getReceivedQuantity() {
        long receivedQuantity = 0L;
        for (OrderReceivedDTO openOrderReceivedDTO : orderReceivedList) {
            if (openOrderReceivedDTO.getReceivedQuantity() != null) {
                receivedQuantity += openOrderReceivedDTO.getReceivedQuantity();
            }
        }

        return receivedQuantity;
    }

    public Date getMaxReceivedDate() {
        Date maxDate = null;
        for (OrderReceivedDTO openOrderReceivedDTO : orderReceivedList) {
            if (openOrderReceivedDTO.getReceivalDate() != null) {
                if (maxDate == null || openOrderReceivedDTO.getReceivalDate().after(maxDate)) {
                    maxDate = openOrderReceivedDTO.getReceivalDate();
                }
            }
        }

        return maxDate;
    }

    public Date getMinReceivedDate() {
        Date minDate = null;
        for (OrderReceivedDTO openOrderReceivedDTO : orderReceivedList) {
            if (openOrderReceivedDTO.getReceivalDate() != null) {
                if (minDate == null || openOrderReceivedDTO.getReceivalDate().before(minDate)) {
                    minDate = openOrderReceivedDTO.getReceivalDate();
                }
            }
        }

        return minDate;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getUnitOfPrice() {
        return unitOfPrice;
    }

    public void setUnitOfPrice(String unitOfPrice) {
        this.unitOfPrice = unitOfPrice;
    }

    public String getAmountOriginal() {
        return amountOriginal;
    }

    public void setAmountOriginal(String amountOriginal) {
        this.amountOriginal = amountOriginal;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMaterialUseridOriginal() {
        return materialUseridOriginal;
    }

    public void setMaterialUseridOriginal(String materialUseridOriginal) {
        this.materialUseridOriginal = materialUseridOriginal;
    }

    public String getMaterialUserid() {
        return materialUserid;
    }

    public void setMaterialUserid(String materialUserid) {
        this.materialUserid = materialUserid;
    }

    public String getBuyerCode() {
        return buyerCode;
    }

    public void setBuyerCode(String buyerCode) {
        this.buyerCode = buyerCode;
    }

    public String getPurchasingOrganization() {
        return purchasingOrganization;
    }

    public void setPurchasingOrganization(String purchasingOrganization) {
        this.purchasingOrganization = purchasingOrganization;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
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

    public String getWbsElement() {
        return wbsElement;
    }

    public void setWbsElement(String wbsElement) {
        this.wbsElement = wbsElement;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getInspection() {
        return inspection;
    }

    public void setInspection(String inspection) {
        this.inspection = inspection;
    }

    public String getInternalFlow() {
        return internalFlow;
    }

    public void setInternalFlow(String internalFlow) {
        this.internalFlow = internalFlow;
    }

    public String getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(String issuerId) {
        this.issuerId = issuerId;
    }

    public String getOrdererId() {
        return ordererId;
    }

    public void setOrdererId(String ordererId) {
        this.ordererId = ordererId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getRequisitionNumber() {
        return requisitionNumber;
    }

    public void setRequisitionNumber(long requisitionNumber) {
        this.requisitionNumber = requisitionNumber;
    }

    public Date getShipArriveDate() {
        return shipArriveDate;
    }

    public void setShipArriveDate(Date shipArriveDate) {
        this.shipArriveDate = shipArriveDate;
    }

    public String getExpectedStaDateOriginal() {
        return expectedStaDateOriginal;
    }

    public void setExpectedStaDateOriginal(String expectedStaDateOriginal) {
        this.expectedStaDateOriginal = expectedStaDateOriginal;
    }

    public Date getExpectedStaDate() {
        return expectedStaDate;
    }

    public void setExpectedStaDate(Date expectedStaDate) {
        this.expectedStaDate = expectedStaDate;
    }

    public String getOrderLineStatusOriginal() {
        return orderLineStatusOriginal;
    }

    public void setOrderLineStatusOriginal(String orderLineStatusOriginal) {
        this.orderLineStatusOriginal = orderLineStatusOriginal;
    }

    public OrderLineStatus getOrderLineStatus() {
        if ("placed".equalsIgnoreCase(orderLineStatusOriginal)) {
            return OrderLineStatus.PLACED;
        }
        if ("received_partly".equalsIgnoreCase(orderLineStatusOriginal)) {
            return OrderLineStatus.RECEIVED_PARTLY;
        }
        if ("received".equalsIgnoreCase(orderLineStatusOriginal)) {
            return OrderLineStatus.COMPLETED;
        }
        return null;
    }

    public String getIntExtOrder() {
        return intExtOrder;
    }

    public void setIntExtOrder(String intExtOrder) {
        this.intExtOrder = intExtOrder;
    }

    public String getOrderFromGps() {
        return orderFromGps;
    }

    public void setOrderFromGps(String orderFromGps) {
        this.orderFromGps = orderFromGps;
    }

    public String getOrderLinePaid() {
        return orderLinePaid;
    }

    public void setOrderLinePaid(String orderLinePaid) {
        this.orderLinePaid = orderLinePaid;
    }

    public String getPossibleToReceiveQuantityOriginal() {
        return possibleToReceiveQuantityOriginal;
    }

    public void setPossibleToReceiveQuantityOriginal(String possibleToReceiveQuantityOriginal) {
        this.possibleToReceiveQuantityOriginal = possibleToReceiveQuantityOriginal;
    }

    public long getPossibleToReceiveQuantity() {
        return possibleToReceiveQuantity;
    }

    public void setPossibleToReceiveQuantity(long possibleToReceiveQuantity) {
        this.possibleToReceiveQuantity = possibleToReceiveQuantity;
    }

    public String getTestObjectsQtyOriginal() {
        return testObjectsQtyOriginal;
    }

    public void setTestObjectsQtyOriginal(String testObjectsQtyOriginal) {
        this.testObjectsQtyOriginal = testObjectsQtyOriginal;
    }

    public boolean isPaid() {
        return "yes".equalsIgnoreCase(this.orderLinePaid);
    }

    public boolean isMigrated() {
        return migrated;
    }

    public void setMigrated(boolean migrated) {
        this.migrated = migrated;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getShipArriveDateOriginal() {
        return shipArriveDateOriginal;
    }

    public void setShipArriveDateOriginal(String shipArriveDateOriginal) {
        this.shipArriveDateOriginal = shipArriveDateOriginal;
    }

    public String getPriceOriginal() {
        return priceOriginal;
    }

    public void setPriceOriginal(String priceOriginal) {
        this.priceOriginal = priceOriginal;
    }

    public String getAcceptedStaDateOriginal() {
        return acceptedStaDateOriginal;
    }

    public void setAcceptedStaDateOriginal(String acceptedStaDateOriginal) {
        this.acceptedStaDateOriginal = acceptedStaDateOriginal;
    }

    public Date getAcceptedStaDate() {
        return acceptedStaDate;
    }

    public void setAcceptedStaDate(Date acceptedStaDate) {
        this.acceptedStaDate = acceptedStaDate;
    }

    public String getAgreedStaDateOriginal() {
        return agreedStaDateOriginal;
    }

    public void setAgreedStaDateOriginal(String agreedStaDateOriginal) {
        this.agreedStaDateOriginal = agreedStaDateOriginal;
    }

    public Date getAgreedStaDate() {
        return agreedStaDate;
    }

    public void setAgreedStaDate(Date agreedStaDate) {
        this.agreedStaDate = agreedStaDate;
    }

    public List<OrderReceivedDTO> getOrderReceivedList() {
        return orderReceivedList;
    }

    public void setOrderReceivedList(List<OrderReceivedDTO> orderReceivedList) {
        this.orderReceivedList = orderReceivedList;
    }

    public boolean isExternal() {
        return isExternal;
    }

    public void setExternal(boolean isExternal) {
        this.isExternal = isExternal;
    }

    public boolean isSendPPO() {
        return isSendPPO;
    }

    public void setSendPPO(boolean isSendPPO) {
        this.isSendPPO = isSendPPO;
    }

    public boolean isSendGM() {
        return isSendGM;
    }

    public void setSendGM(boolean isSendGM) {
        this.isSendGM = isSendGM;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(returnEmptyStringForNull(itemId));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(lineType));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(orderNumber));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(orderDateOriginal));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(suffix));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(partQualifier));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(partNumber));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(description));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(partVersion));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(unitOfMeasure));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(priceOriginal));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(unitOfPrice));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(amount));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(currency));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(materialUseridOriginal));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(purchasingOrganization));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(buyerCode));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(buyerName));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(project));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(costCenter));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(glAccount));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(wbsElement));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(supplierCode));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(supplierName));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(inspection));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(internalFlow));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(issuerId));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(ordererId));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(note));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(requisitionNumber));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(shipArriveDateOriginal));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(acceptedStaDateOriginal));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(agreedStaDateOriginal));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(expectedStaDateOriginal));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(orderLineStatusOriginal));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(intExtOrder));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(orderFromGps));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(orderLinePaid));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(orderedQuantity));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(possibleToReceiveQuantityOriginal));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(testObjectsQtyOriginal));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(testObjectstotalQty));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(paidQuantityOriginal));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(ppaId));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(reference));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(sparePartQuantityOriginal));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(issuerName));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(issuerEmail));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(contactPersonName));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(contactPersonEmail));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(supplierPartNo));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(agreedSTALastUpdated));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(modularHarness));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(procureDateOriginal));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(orderSiteOriginal));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(warehouseSite));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(testObjectsPulledOriginal));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(orderState));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(orderTypeOriginal));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(migrated);
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(isSendPPO);
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(isSendGM);
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(reason));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(reasonIT));

        if (orderReceivedList != null) {
            for (OrderReceivedDTO orderReceived : orderReceivedList) {
                stringBuilder.append('\n');
                stringBuilder.append(orderReceived.toString());
            }
        }

        return stringBuilder.toString();
    }

    private String returnEmptyStringForNull(Object obj) {
        return obj == null ? "" : obj.toString().replace(SEMICOLON, "SEMICOLON").replace(COMMA, "COMMA");
    }

    public String getPartQualifier() {
        return partQualifier;
    }

    public void setPartQualifier(String partQualifier) {
        this.partQualifier = partQualifier;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getShipToId() {
        return shipToId;
    }

    public void setShipToId(String shipToId) {
        this.shipToId = shipToId;
    }

    public String getDeliveryControllerTeam() {
        return deliveryControllerTeam;
    }

    public void setDeliveryControllerTeam(String deliveryControllerTeam) {
        this.deliveryControllerTeam = deliveryControllerTeam;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getMaterialControllerTeam() {
        return materialControllerTeam;
    }

    public void setMaterialControllerTeam(String materialControllerTeam) {
        this.materialControllerTeam = materialControllerTeam;
    }

    public List<String> getTestObjectsQty() {
        return testObjectsQty;
    }

    public void setTestObjectsQty(List<String> testObjectsQty) {
        this.testObjectsQty = testObjectsQty;
    }

    public long getTotalTestobjectsQty() {
        return totalTestobjectsQty;
    }

    public void setTotalTestobjectsQty(long totalTestobjectsQty) {
        this.totalTestobjectsQty = totalTestobjectsQty;
    }

    public String getTestObjectstotalQty() {
        return testObjectstotalQty;
    }

    public void setTestObjectstotalQty(String testObjectstotalQty) {
        this.testObjectstotalQty = testObjectstotalQty;
    }

    public String getRequisitionNumberOriginal() {
        return requisitionNumberOriginal;
    }

    public void setRequisitionNumberOriginal(String requisitionNumberOriginal) {
        this.requisitionNumberOriginal = requisitionNumberOriginal;
    }

    public String getOrderedQtyoriginal() {
        return orderedQtyoriginal;
    }

    public void setOrderedQtyoriginal(String orderedQtyoriginal) {
        this.orderedQtyoriginal = orderedQtyoriginal;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getContactPersonEmail() {
        return contactPersonEmail;
    }

    public void setContactPersonEmail(String contactPersonEmail) {
        this.contactPersonEmail = contactPersonEmail;
    }

    public String getPaidQuantityOriginal() {
        return paidQuantityOriginal;
    }

    public void setPaidQuantityOriginal(String paidQuantityOriginal) {
        this.paidQuantityOriginal = paidQuantityOriginal;
    }

    public long getPaidQuantity() {
        return paidQuantity;
    }

    public void setPaidQuantity(long paidQuantity) {
        this.paidQuantity = paidQuantity;
    }

    public String getPpaId() {
        return ppaId;
    }

    public void setPpaId(String ppaId) {
        this.ppaId = ppaId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getSparePartQuantityOriginal() {
        return sparePartQuantityOriginal;
    }

    public void setSparePartQuantityOriginal(String sparePartQuantityOriginal) {
        this.sparePartQuantityOriginal = sparePartQuantityOriginal;
    }

    public long getSparePartQuantity() {
        return sparePartQuantity;
    }

    public void setSparePartQuantity(long sparePartQuantity) {
        this.sparePartQuantity = sparePartQuantity;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public String getIssuerEmail() {
        return issuerEmail;
    }

    public void setIssuerEmail(String issuerEmail) {
        this.issuerEmail = issuerEmail;
    }

    public String getOrderSiteId() {
        return orderSiteId;
    }

    public void setOrderSiteId(String orderSiteId) {
        this.orderSiteId = orderSiteId;
    }

    public String getOrderSiteOriginal() {
        return orderSiteOriginal;
    }

    public void setOrderSiteOriginal(String orderSiteOriginal) {
        this.orderSiteOriginal = orderSiteOriginal;
    }

    public String getReferenceIds() {
        return referenceIds;
    }

    public void setReferenceIds(String referenceIds) {
        this.referenceIds = referenceIds;
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

    public String getOrderIdGps() {
        return orderIdGps;
    }

    public void setOrderIdGps(String orderIdGps) {
        this.orderIdGps = orderIdGps;
    }

    public String getSupplierPartNo() {
        return supplierPartNo;
    }

    public void setSupplierPartNo(String supplierPartNo) {
        this.supplierPartNo = supplierPartNo;
    }

    public String getAgreedSTALastUpdatedOriginal() {
        return agreedSTALastUpdatedOriginal;
    }

    public void setAgreedSTALastUpdatedOriginal(String agreedSTALastUpdatedOriginal) {
        this.agreedSTALastUpdatedOriginal = agreedSTALastUpdatedOriginal;
    }

    public Date getAgreedSTALastUpdated() {
        return agreedSTALastUpdated;
    }

    public void setAgreedSTALastUpdated(Date agreedSTALastUpdated) {
        this.agreedSTALastUpdated = agreedSTALastUpdated;
    }

    public String getModularHarness() {
        return modularHarness;
    }

    public void setModularHarness(String modularHarness) {
        this.modularHarness = modularHarness;
    }

    public String getProcureDateOriginal() {
        return procureDateOriginal;
    }

    public void setProcureDateOriginal(String procureDateOriginal) {
        this.procureDateOriginal = procureDateOriginal;
    }

    public Date getProcureDate() {
        return procureDate;
    }

    public void setProcureDate(Date procureDate) {
        this.procureDate = procureDate;
    }

    public String getObjectInformation() {
        return objectInformation;
    }

    public void setObjectInformation(String objectInformation) {
        this.objectInformation = objectInformation;
    }

    public String getOrderInformation() {
        return orderInformation;
    }

    public void setOrderInformation(String orderInformation) {
        this.orderInformation = orderInformation;
    }

    public String getWarehouseSite() {
        return warehouseSite;
    }

    public void setWarehouseSite(String warehouseSite) {
        this.warehouseSite = warehouseSite;
    }

    public String getTestObjectsPulledOriginal() {
        return testObjectsPulledOriginal;
    }

    public void setTestObjectsPulledOriginal(String testObjectsPulledOriginal) {
        this.testObjectsPulledOriginal = testObjectsPulledOriginal;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getOrderTypeOriginal() {
        return orderTypeOriginal;
    }

    public void setOrderTypeOriginal(String orderTypeOriginal) {
        this.orderTypeOriginal = orderTypeOriginal;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public List<String> getTestObjectsPulled() {
        return testObjectsPulled;
    }

    public void setTestObjectsPulled(List<String> testObjectsPulled) {
        this.testObjectsPulled = testObjectsPulled;
    }

    public String getReason() {
        return reason.toString();
    }

    public void setReason(String reason) {
        this.reason.append(reason);
    }

    public StringBuilder getReasonIT() {
        return reasonIT;
    }

    public void setReasonIT(String reasonIT) {
        this.reasonIT.append(reasonIT);
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getPackingSlipNumbersForReceivals() {
        return StringUtils.join(packingSlipNumbersForReceivals, COMMA);
    }

    public void setPackingSlipNumbersForReceivals(String packingSlipNumber) {
        if (!StringUtils.isEmpty(packingSlipNumber)) {
            this.packingSlipNumbersForReceivals.add(packingSlipNumber);
        }
    }
}
