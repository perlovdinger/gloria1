package com.volvo.gloria.materialRequestProxy.c;

import java.io.Serializable;
import java.util.Date;

/**
 * MaterialRequestLineDTO.
 */
public class MaterialRequestLineTransformerDTO implements Serializable {
  
    private static final long serialVersionUID = 3009047105376446755L;

    private long procureLinkId;

    private String partAffiliation;

    private String partNumber;

    private String partVersion;

    private String partName;

    private String partModification;

    private long quantity;

    private String unitOfMeasure;

    private String productClass;

    private String partFunctionGroup;

    private String functionGroup;

    private String functionGroupSuffix;

    private double unitPrice;

    private String currency;

    private Date requiredStaDate;

    private boolean isRemoveType;
    
    private long materialRequestLineOid;

    public long getProcureLinkId() {
        return procureLinkId;
    }

    public void setProcureLinkId(long procureLinkId) {
        this.procureLinkId = procureLinkId;
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

    public String getPartModification() {
        return partModification;
    }

    public void setPartModification(String partModification) {
        this.partModification = partModification;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getProductClass() {
        return productClass;
    }

    public void setProductClass(String productClass) {
        this.productClass = productClass;
    }

    public String getPartFunctionGroup() {
        return partFunctionGroup;
    }

    public void setPartFunctionGroup(String partFunctionGroup) {
        this.partFunctionGroup = partFunctionGroup;
    }

    public String getFunctionGroup() {
        return functionGroup;
    }

    public void setFunctionGroup(String functionGroup) {
        this.functionGroup = functionGroup;
    }

    public String getFunctionGroupSuffix() {
        return functionGroupSuffix;
    }

    public void setFunctionGroupSuffix(String functionGroupSuffix) {
        this.functionGroupSuffix = functionGroupSuffix;
    }

   
    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getRequiredStaDate() {
        return requiredStaDate;
    }

    public void setRequiredStaDate(Date requiredStaDate) {
        this.requiredStaDate = requiredStaDate;
    }

    public boolean isRemoveType() {
        return isRemoveType;
    }

    public void setRemoveType(boolean isRemoveType) {
        this.isRemoveType = isRemoveType;
    }

    public long getMaterialRequestLineOid() {
        return materialRequestLineOid;
    }

    public void setMaterialRequestLineOid(long materialRequestLineOid) {
        this.materialRequestLineOid = materialRequestLineOid;
    }
}
