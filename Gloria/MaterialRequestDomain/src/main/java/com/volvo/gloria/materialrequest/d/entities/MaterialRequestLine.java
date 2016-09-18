package com.volvo.gloria.materialrequest.d.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.Utils;
import com.volvo.gloria.util.validators.GloriaLongValue;

/**
 * Entity implementation class for entity MaterialRequestLine.
 * 
 */
@Entity
@Table(name = "MATERIAL_REQUEST_LINE")
public class MaterialRequestLine implements Serializable {

    private static final long serialVersionUID = 8725214074453114886L;
    private static final int MAX_QUANTITY = 99999;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MATERIAL_REQUEST_LINE_OID")
    private long materialRequestLineOid;

    @Version
    private long version;

    private String changeTechId;
    private boolean removeMarked;
    private String partAffiliation;
    private String partNumber;
    private String partVersion;
    private String partName;
    private String partModification;
    
    @GloriaLongValue(max = MAX_QUANTITY)
    private long quantity;
    private String unitOfMeasure;
    private String productClass;
    private String functionGroup;
    private long requestLinkId;
    private String operation;
    private long updatedMaterialRequestlineOid;

    @ManyToOne
    @JoinColumn(name = "MATERIAL_REQUEST_VERSION_OID")
    private MaterialRequestVersion materialRequestVersion;

    @ManyToOne
    @JoinColumn(name = "MATERIAL_REQUEST_OBJECT_OID")
    private MaterialRequestObject materialRequestObject;

    public long getMaterialRequestLineOid() {
        return materialRequestLineOid;
    }

    public void setMaterialRequestLineOid(long materialRequestLineOid) {
        this.materialRequestLineOid = materialRequestLineOid;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getChangeTechId() {
        return changeTechId;
    }

    public void setChangeTechId(String changeTechId) {
        this.changeTechId = changeTechId;
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
        this.partAffiliation = partAffiliation.toUpperCase();
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber.toUpperCase();
    }

    public String getPartVersion() {
        return partVersion;
    }

    public void setPartVersion(String partVersion) {        
        this.partVersion = partVersion.toUpperCase();
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName.toUpperCase();
    }

    public String getPartModification() {
        return partModification;
    }

    public void setPartModification(String partModification) {
        if (partModification != null) {
            this.partModification = partModification.toUpperCase();
        } else {
            this.partModification = partModification;
        }       
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) throws GloriaApplicationException {
        Utils.validatePostiveNumberLong(quantity);
        this.quantity = quantity;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure.toUpperCase();
    }

    public String getProductClass() {
        return productClass;
    }

    public void setProductClass(String productClass) {
        this.productClass = productClass;
    }

    public String getFunctionGroup() {
        return functionGroup;
    }

    public void setFunctionGroup(String functionGroup) {
        this.functionGroup = functionGroup;
    } 

    public MaterialRequestVersion getMaterialRequestHeaderVersion() {
        return materialRequestVersion;
    }

    public void setMaterialRequestHeaderVersion(MaterialRequestVersion materialRequestVersion) {
        this.materialRequestVersion = materialRequestVersion;
    }

    public long getRequestLinkId() {
        return requestLinkId;
    }

    public void setRequestLinkId(long requestLinkId) {
        this.requestLinkId = requestLinkId;
    }

    public MaterialRequestObject getMaterialRequestObject() {
        return materialRequestObject;
    }

    public void setMaterialRequestObject(MaterialRequestObject materialRequestObject) {
        this.materialRequestObject = materialRequestObject;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public long getUpdatedMaterialRequestlineOid() {
        return updatedMaterialRequestlineOid;
    }

    public void setUpdatedMaterialRequestlineOid(long updatedMaterialRequestlineOid) {
        this.updatedMaterialRequestlineOid = updatedMaterialRequestlineOid;
    }
    
    public Boolean isNewMarked() {
        if (this.getChangeTechId().equals(this.getMaterialRequestHeaderVersion().getChangeTechId()) && !this.isRemoveMarked()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
