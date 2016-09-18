package com.volvo.gloria.procurerequest.c.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class RequestLineTransformerDTO implements Serializable {

    private static final long serialVersionUID = 339664371104719244L;
    
    private long procureLinkId;

    private String status;

    private String partAffiliation;

    private String partNumber;

    private String partVersion;

    private String partName;

    private String partModification;

    private String refAssemblyPartNo;

    private String refAssemblyPartVersion;

    private long quantity;

    private int amount;

    private String unitOfMeasure;

    private String productClass;

    private String partFunctionGroup;

    private String functionGroup;

    private String functionGroupSuffix;

    private String assemblyDistribution;

    private String designResponsible;

    private long itemToVariantLinkId;

    private String modularHarness;

    private String objectNumber;

    private String demarcation;

    private String characteristics;

    private String procureComment;
    
    private double unitPrice;

    private String currency;

    private Date requiredStaDate;

    private List<PartAliasTransformerDTO> partAliasTransformerDtos;

    private FinanceHeaderTransformerDTO financeHeaderTransformerDTO;
    
    private boolean isRemoveType;
    
    private long materialRequestLineOid;
    
    private String mailFormId;

    public long getProcureLinkId() {
        return procureLinkId;
    }

    public void setProcureLinkId(long procureLinkId) {
        this.procureLinkId = procureLinkId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getRefAssemblyPartNo() {
        return refAssemblyPartNo;
    }

    public void setRefAssemblyPartNo(String refAssemblyPartNo) {
        this.refAssemblyPartNo = refAssemblyPartNo;
    }

    public String getRefAssemblyPartVersion() {
        return refAssemblyPartVersion;
    }

    public void setRefAssemblyPartVersion(String refAssemblyPartVersion) {
        this.refAssemblyPartVersion = refAssemblyPartVersion;
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

    public List<PartAliasTransformerDTO> getPartAliasTransformerDtos() {
        return partAliasTransformerDtos;
    }

    public void setPartAliasTransformerDtos(List<PartAliasTransformerDTO> partAliasTransformerDtos) {
        this.partAliasTransformerDtos = partAliasTransformerDtos;
    }

    public void setFunctionGroupSuffix(String functionGroupSuffix) {
        this.functionGroupSuffix = functionGroupSuffix;
    }

    public String getAssemblyDistribution() {
        return assemblyDistribution;
    }

    public void setAssemblyDistribution(String assemblyDistribution) {
        this.assemblyDistribution = assemblyDistribution;
    }

    public String getDesignResponsible() {
        return designResponsible;
    }

    public void setDesignResponsible(String designResponsible) {
        this.designResponsible = designResponsible;
    }

    public long getItemToVariantLinkId() {
        return itemToVariantLinkId;
    }

    public void setItemToVariantLinkId(long itemToVariantLinkId) {
        this.itemToVariantLinkId = itemToVariantLinkId;
    }

    public String getModularHarness() {
        return modularHarness;
    }

    public void setModularHarness(String modularHarness) {
        this.modularHarness = modularHarness;
    }

    public String getObjectNumber() {
        return objectNumber;
    }

    public void setObjectNumber(String objectNumber) {
        this.objectNumber = objectNumber;
    }

    public String getDemarcation() {
        return demarcation;
    }

    public void setDemarcation(String demarcation) {
        this.demarcation = demarcation;
    }

    public String getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(String characteristics) {
        this.characteristics = characteristics;
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
    
    public FinanceHeaderTransformerDTO getFinanceHeaderTransformerDTO() {
        return financeHeaderTransformerDTO;
    }

    public void setFinanceHeaderTransformerDTO(FinanceHeaderTransformerDTO financeHeaderTransformerDTO) {
        this.financeHeaderTransformerDTO = financeHeaderTransformerDTO;
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

    public String getMailFormId() {
        return mailFormId;
    }
    
    public void setMailFormId(String mailFormId) {
        this.mailFormId = mailFormId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getProcureComment() {
        return procureComment;
    }

    public void setProcureComment(String procureComment) {
        this.procureComment = procureComment;
    }

 
}
