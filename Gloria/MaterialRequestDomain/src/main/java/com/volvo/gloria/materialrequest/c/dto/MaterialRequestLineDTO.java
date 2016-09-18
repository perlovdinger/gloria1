package com.volvo.gloria.materialrequest.c.dto;

import java.io.Serializable;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * DTO for the MaterialRequestLine.
 */
public class MaterialRequestLineDTO implements Serializable, PageResults {

    private static final long serialVersionUID = -135144157084238669L;
    
    private long id;
    
    private long version;
    
    private boolean removeMarked;
    
    private Boolean isNew;
    
    private String partAffiliation;
    
    private String partNumber;
    
    private String partVersion;
    
    private String partName;
    
    private long quantity;
    
    private String unitOfMeasure;
    
    private String partModification;
    
    private String functionGroup;
      
    private double unitPrice;
    
    private String currency;
    
    private String changeTechId;

    private long materialRequestObjectId;
    
    private long materialRequestObjectVersion;
    
    private String name;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public boolean isRemoveMarked() {
        return removeMarked;
    }

    public void setRemoveMarked(boolean removeMarked) {
        this.removeMarked = removeMarked;
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

    public String getFunctionGroup() {
        return functionGroup;
    }

    public void setFunctionGroup(String functionGroup) {
        this.functionGroup = functionGroup;
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

    public String getChangeTechId() {
        return changeTechId;
    }
    
    public void setChangeTechId(String changeTechId) {
        this.changeTechId = changeTechId;
    }

    public long getMaterialRequestObjectId() {
        return materialRequestObjectId;
    }

    public void setMaterialRequestObjectId(long materialRequestObjectId) {
        this.materialRequestObjectId = materialRequestObjectId;
    }

    public long getMaterialRequestObjectVersion() {
        return materialRequestObjectVersion;
    }

    public void setMaterialRequestObjectVersion(long materialRequestObjectVersion) {
        this.materialRequestObjectVersion = materialRequestObjectVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }   
}
