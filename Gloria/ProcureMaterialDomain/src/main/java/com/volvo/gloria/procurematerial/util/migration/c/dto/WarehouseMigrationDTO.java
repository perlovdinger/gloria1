package com.volvo.gloria.procurematerial.util.migration.c.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DTO for warehouse migration.
 */
public class WarehouseMigrationDTO {
    private static final String SEMICOLON = ";";
    private static final String COMMA = ",";
    private String siteId;
    private String warehouseSiteId;
    private String storageRoomCode;
    private String zoneCode;
    private String binLocationCode;
    private String staDate;
    private Date requiredStaDate;
    private String orderNo;
    private String orderDateOriginal;
    private Date orderDate;
    private String partAffiliationOriginal;
    private String partAffiliation;
    private String partNumber;
    private String stage;
    private String issue;
    private String partVersion;
    private String partNameOriginal;
    private String partName;
    private String projectId;
    private String reference;
    private String storageQuantity;
    private Long quantity;
    private String testObject;
    private boolean migrated = false;
    private String companyCode;
    private long binLocationOid;
    private String materialControllerTeam;
    private String unitOfMeasure;
    private String referenceIds;
    private List<String> testObjectsQty = new ArrayList<String>();
    private boolean splitted;
    private StringBuilder binlocationSplit = new StringBuilder();
    private StringBuffer reason = new StringBuffer();
    private StringBuffer reasonIT = new StringBuffer();

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getWarehouseSiteId() {
        return warehouseSiteId;
    }

    public void setWarehouseSiteId(String warehouseSiteId) {
        this.warehouseSiteId = warehouseSiteId;
    }

    public String getStorageRoomCode() {
        return storageRoomCode;
    }

    public void setStorageRoomCode(String storageRoomCode) {
        this.storageRoomCode = storageRoomCode;
    }

    public String getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode;
    }

    public String getBinLocationCode() {
        return binLocationCode;
    }

    public void setBinLocationCode(String binLocationCode) {
        this.binLocationCode = binLocationCode;
    }

    public String getStaDate() {
        return staDate;
    }

    public void setStaDate(String staDate) {
        this.staDate = staDate;
    }

    public Date getRequiredStaDate() {
        return requiredStaDate;
    }

    public void setRequiredStaDate(Date requiredStaDate) {
        this.requiredStaDate = requiredStaDate;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderDateOriginal() {
        return orderDateOriginal;
    }

    public void setOrderDateOriginal(String orderDateOriginal) {
        this.orderDateOriginal = orderDateOriginal;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
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

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getPartVersion() {
        return partVersion;
    }

    public void setPartVersion(String partVersion) {
        this.partVersion = partVersion;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getStorageQuantity() {
        return storageQuantity;
    }

    public void setStorageQuantity(String storageQuantity) {
        this.storageQuantity = storageQuantity;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getTestObject() {
        return testObject;
    }

    public void setTestObject(String testObject) {
        this.testObject = testObject;
    }

    public boolean isMigrated() {
        return migrated;
    }

    public void setMigrated(boolean migrated) {
        this.migrated = migrated;
    }

    public String getPartAffiliationOriginal() {
        return partAffiliationOriginal;
    }

    public void setPartAffiliationOriginal(String partAffiliationOriginal) {
        this.partAffiliationOriginal = partAffiliationOriginal;
    }

    public String getPartNameOriginal() {
        return partNameOriginal;
    }

    public void setPartNameOriginal(String partNameOriginal) {
        this.partNameOriginal = partNameOriginal;
    }

    public long getBinLocationOid() {
        return binLocationOid;
    }

    public void setBinLocationOid(long binLocationOid) {
        this.binLocationOid = binLocationOid;
    }

    public String getMaterialControllerTeam() {
        return materialControllerTeam;
    }

    public void setMaterialControllerTeam(String materialControllerTeam) {
        this.materialControllerTeam = materialControllerTeam;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(returnEmptyStringForNull(warehouseSiteId));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(binLocationCode));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(staDate));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(orderNo));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(orderDateOriginal));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(partAffiliationOriginal));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(partNumber));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(stage));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(issue));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(partNameOriginal));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(projectId));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(reference));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(storageQuantity));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(testObject));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(unitOfMeasure));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(splitted);
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(binlocationSplit.toString()));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(migrated);
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(reason));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(reasonIT));
        return stringBuilder.toString();
    }

    private String returnEmptyStringForNull(Object obj) {
        return obj == null ? "" : obj.toString().replace(SEMICOLON, "SEMICOLON").replace(COMMA, "COMMA");
    }

    public String getReferenceIds() {
        return referenceIds;
    }

    public void setReferenceIds(String referenceIds) {
        this.referenceIds = referenceIds;
    }

    public List<String> getTestObjectsQty() {
        return testObjectsQty;
    }

    public void setTestObjectsQty(List<String> testObjectsQty) {
        this.testObjectsQty = testObjectsQty;
    }

    public boolean isSplitted() {
        return splitted;
    }

    public void setSplitted(boolean splitted) {
        this.splitted = splitted;
    }

    public String getBinlocationSplit() {
        return binlocationSplit.toString();
    }

    public void setBinlocationSplit(String binlocation, long qty) {
        this.quantity += qty;
        this.storageQuantity = String.valueOf(this.quantity);
        this.binlocationSplit.append("[" + binlocation + "@" + qty + "]");
    }

    public StringBuffer getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason.append(reason);
    }

    public StringBuffer getReasonIT() {
        return reasonIT;
    }

    public void setReasonIT(String reasonIT) {
        this.reasonIT.append(reasonIT);
    }

    public String getOrderPartIdentifierKey() {
        return this.orderNo + "-" + this.partNumber + "-" + this.partVersion + "-" + this.partAffiliation;
    }
}
