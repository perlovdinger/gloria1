/*
 * Copyright 2013 Volvo Information Technology AB 
 * 
 * Licensed under the Volvo IT Corporate Source License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *      http://java.volvo.com/licenses/CORPORATE-SOURCE-LICENSE 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.volvo.gloria.procurematerial.d.entities;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.procurematerial.c.ProcureLineProgressStatusFlag;
import com.volvo.gloria.procurematerial.c.ProcureResponsibility;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.Utils;
import com.volvo.gloria.util.persistence.GenericEntity;
import com.volvo.gloria.util.validators.GloriaLongValue;

/**
 * Entity class for ProcureLine.
 */
@Entity
@Table(name = "PROCURE_LINE")
public class ProcureLine implements GenericEntity<Long>, Serializable {

    private static final long serialVersionUID = 7036230039824784655L;
    private static final int _2048 = 2048;
    private static final int MAX_QUANTITY = 99999;
    public static final String GROUPINGKEYMD5 = "groupingKeyMd5";
    public static final String GROUPINGKEYMD5S = "groupingKeyMd5s";
    public static final String STATUS = "status";
    public static final String MATERIALCONTROLLERID = "materialControllerId";
    public static final String FORWARDEDUSERID = "forwardedUserId";
    public static final String FORWARDEDTEAM = "forwardedTeam";
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROCURE_LINE_OID")
    private long procureLineOid;

    @Version
    private long version;

    @Enumerated(EnumType.STRING)
    private ProcureType procureType;
    
    private String pPartAffiliation;
    private String pPartNumber;
    private String pPartVersion;
    private String pPartName;
    private String pPartModification;
    private String dfuObjectNumber;
    private String functionGroupId;
    
    @Enumerated(EnumType.STRING)
    private ProcureLineStatus status;
    
    @Enumerated(EnumType.STRING)
    private ProcureLineProgressStatusFlag statusFlag;
        
    @Enumerated(EnumType.STRING)
    private ProcureResponsibility responsibility;
    private String materialControllerId;
    private String materialControllerName;
    private String materialControllerTeam;
    private String forwardedUserId;
    private String forwardedUserName;
    private String forwardedTeam;
    private boolean forwardedForDC;    
    private String procureUserId;
    private Date procureDate;    
    private String procureTeam;
    private String requisitionId;
    private String orderNo;
    @GloriaLongValue(max = MAX_QUANTITY)
    private long usageQuantity;
    @GloriaLongValue(max = MAX_QUANTITY)
    private long additionalQuantity;
    private Date requiredStaDate;
    private Long supplierCounterPartOID;
    private String shipToId;
    private String whSiteId;
    private String warehouseComment;
    @Column(length = _2048)
    private String procureInfo;
    private Double maxPrice;    
    private double unitPrice;    
    private String supplierCountryCode;
    private String currency;
    private String priceType;
    private long dangerousGoodsOID;
    private boolean needIsChanged;
    @Column(length = _2048)
    private String changeRequestIds;
    private String referenceIds;
    private String referenceGroups;
    private boolean fromStockAllowed;
    private Long qualityDocumentOID;
    private boolean pPartNameUpdated;
    private String referenceGps;
    private Date orderStaDate;
    private String groupingKeyMd5; 
    private boolean contentEdited;
    @Lob
    private byte[] groupingKeyCompress;
    private Date procureFailureDate = new Date(0);
    private long perQuantity;
    private boolean crHasBeenMoved;

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "PART_ALIAS_OID")    
    private PartAlias partAlias;    
    
    
    @OneToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "FINANCE_HEADER_OID")
    private FinanceHeader financeHeader;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "REQUISITION_OID")
    private Requisition requisition;
  
    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH }, mappedBy = "procureLine")
    private List<Material> materials = new ArrayList<Material>();
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "SUPPLIER_OID")
    private Supplier supplier;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH }, mappedBy = "procureLine")
    private List<Supplier> suppliers = new ArrayList<Supplier>();

    private String buyerCode;
    private String buyerName;
    private String purchaseOrgName;
    private String purchaseOrgCode;
    private long fromStockQty;
    private long fromStockProjectQty;
    
    public String getReferenceGps() {
        return referenceGps;
    }

    public void setReferenceGps(String referenceGps) {
        this.referenceGps = referenceGps;
    }

    @Override
    public Long getId() {
        return procureLineOid;
    }

    public long getProcureLineOid() {
        return procureLineOid;
    }

    public void setProcureLineOid(long procureLineOid) {
        this.procureLineOid = procureLineOid;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public ProcureType getProcureType() {
        return procureType;
    }

    public void setProcureType(ProcureType procureType) {
        this.procureType = procureType;
    }

    public Date getProcureDate() {
        return procureDate;
    }

    public void setProcureDate(Date procureDate) {
        this.procureDate = procureDate;
    }

    public String getRequisitionId() {
        return requisitionId;
    }

    public void setRequisitionId(String requisitionId) {
        this.requisitionId = requisitionId;
    }

    public String getpPartAffiliation() {
        return pPartAffiliation;
    }

    public void setpPartAffiliation(String pPartAffiliation) {
        this.pPartAffiliation = pPartAffiliation;
    }

    public String getpPartNumber() {
        return pPartNumber;
    }

    public void setpPartNumber(String pPartNumber) {
        this.pPartNumber = pPartNumber;
    }

    public String getpPartVersion() {
        return pPartVersion;
    }

    public void setpPartVersion(String pPartVersion) {
        this.pPartVersion = pPartVersion;
    }

    public String getpPartName() {
        return pPartName;
    }

    public void setpPartName(String pPartName) {
        this.pPartName = pPartName;
    }

    public String getpPartModification() {
        return pPartModification;
    }

    public void setpPartModification(String pPartModification) {
        this.pPartModification = pPartModification;
    }

    public String getDfuObjectNumber() {
        return dfuObjectNumber;
    }

    public void setDfuObjectNumber(String dfuObjectNumber) {
        this.dfuObjectNumber = dfuObjectNumber;
    }

    public String getFunctionGroupId() {
        return functionGroupId;
    }

    public void setFunctionGroupId(String functionGroupId) {
        this.functionGroupId = functionGroupId;
    }

    public String getReferenceGroups() {
        return referenceGroups;
    }

    public void setReferenceGroups(String referenceGroups) {
        this.referenceGroups = referenceGroups;
    }

    public String getReferenceIds() {
        return referenceIds;
    }

    public void setReferenceIds(String referenceIds) {
        this.referenceIds = referenceIds;
    }

    public String getChangeRequestIds() {
        return changeRequestIds;
    }

    public void setChangeRequestIds(String changeRequestIds) {
        this.changeRequestIds = changeRequestIds;
    }

    public long getDangerousGoodsOID() {
        return dangerousGoodsOID;
    }

    public void setDangerousGoodsOID(long dangerousGoodsOID) {
        this.dangerousGoodsOID = dangerousGoodsOID;
    }

    public String getWarehouseComment() {
        return warehouseComment;
    }

    public void setWarehouseComment(String warehouseComment) {
        this.warehouseComment = warehouseComment;
    }

    public String getProcureInfo() {
        return procureInfo;
    }

    public void setProcureInfo(String procureInfo) {
        this.procureInfo = procureInfo;
    }

    
    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) throws GloriaApplicationException {
        Utils.validatePostiveNumberDouble(unitPrice);
        this.unitPrice = unitPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) throws GloriaApplicationException {
        Utils.validatePostiveNumberDouble(maxPrice);
        this.maxPrice = maxPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Date getRequiredStaDate() {
        return requiredStaDate;
    }

    public void setRequiredStaDate(Date requiredStaDate) {
        this.requiredStaDate = requiredStaDate;
    }

    public Long getSupplierCounterPartOID() {
        return supplierCounterPartOID;
    }

    public void setSupplierCounterPartOID(Long supplierCounterPartOID) {
        this.supplierCounterPartOID = supplierCounterPartOID;
    }
    
    public long getUsageQuantity() {
        return usageQuantity;
    }
    
    public void setUsageQuantity(long usageQuantity) {
        this.usageQuantity = usageQuantity;
    }

    public long getAdditionalQuantity() {
        return additionalQuantity;
    }

    public void setAdditionalQuantity(long additionalQuantity) {
        this.additionalQuantity = additionalQuantity;
    }

    public ProcureLineStatus getStatus() {
        return status;
    }

    public void setStatus(ProcureLineStatus status) {
        this.status = status;
    }
    
    public ProcureLineProgressStatusFlag getStatusFlag() {
        return statusFlag;
    }

    public void setStatusFlag(ProcureLineProgressStatusFlag statusFlag) {
        this.statusFlag = statusFlag;
    }

    public ProcureResponsibility getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(ProcureResponsibility responsibility) {
        this.responsibility = responsibility;
    }
    
    public FinanceHeader getFinanceHeader() {
        return financeHeader;
    }

    public void setFinanceHeader(FinanceHeader financeHeader) {
        this.financeHeader = financeHeader;
    }

    public Requisition getRequisition() {
        return requisition;
    }

    public void setRequisition(Requisition requisition) {
        this.requisition = requisition;
    }

    public boolean isNeedIsChanged() {
        return needIsChanged;
    }

    public void setNeedIsChanged(boolean needIsChanged) {
        this.needIsChanged = needIsChanged;
    }

    public List<Material> getMaterials() {
        return materials;
    }
    
    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public PartAlias getPartAlias() {
        return partAlias;
    }
    
    public void setPartAlias(PartAlias partAlias) {
        this.partAlias = partAlias;
    }

    public String getProcureUserId() {
        return procureUserId;
    }

    public void setProcureUserId(String procureUserId) {
        this.procureUserId = procureUserId;
    }
    
    public String getShipToId() {
        return shipToId;
    }

    public void setShipToId(String shipToId) {
        this.shipToId = shipToId;
    }

    public String getWhSiteId() {
        return whSiteId;
    }

    public void setWhSiteId(String whSiteId) {
        this.whSiteId = whSiteId;
    }

    public boolean isFromStockAllowed() {
        return fromStockAllowed;
    }

    public void setFromStockAllowed(boolean fromStockAllowed) {
        this.fromStockAllowed = fromStockAllowed;
    }
    
    public Long getQualityDocumentOID() {
        return qualityDocumentOID;
    }
    
    public void setQualityDocumentOID(Long qualityDocumentOID) {
        this.qualityDocumentOID = qualityDocumentOID;
    }

    public String getSupplierCountryCode() {
        return supplierCountryCode;
    }

    public void setSupplierCountryCode(String supplierCountryCode) {
        this.supplierCountryCode = supplierCountryCode;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public String getMaterialControllerId() {
        return materialControllerId;
    }

    public void setMaterialControllerId(String materialControllerId) {
        this.materialControllerId = materialControllerId;
    }

    public String getMaterialControllerName() {
        return materialControllerName;
    }

    public void setMaterialControllerName(String materialControllerName) {
        this.materialControllerName = materialControllerName;
    }

    public String getMaterialControllerTeam() {
        return materialControllerTeam;
    }

    public void setMaterialControllerTeam(String materialControllerTeam) {
        this.materialControllerTeam = materialControllerTeam;
    }

    public String getForwardedUserId() {
        return forwardedUserId;
    }
    
    public void setForwardedUserId(String forwardedUserId) {
        this.forwardedUserId = forwardedUserId;
    }
    
    public String getForwardedUserName() {
        return forwardedUserName;
    }
    
    public void setForwardedUserName(String forwardedUserName) {
        this.forwardedUserName = forwardedUserName;
    }

    public String getForwardedTeam() {
        return forwardedTeam;
    }

    public void setForwardedTeam(String forwardedTeam) {
        this.forwardedTeam = forwardedTeam;
    }

    public boolean isForwardedForDC() {
        return forwardedForDC;
    }

    public void setForwardedForDC(boolean forwardedForDC) {
        this.forwardedForDC = forwardedForDC;
    }
    
    public String getProcureTeam() {
        return procureTeam;
    }
    
    public void setProcureTeam(String procureTeam) {
        this.procureTeam = procureTeam;
    }

    public boolean ispPartNameUpdated() {
        return pPartNameUpdated;
    }

    public void setpPartNameUpdated(boolean pPartNameUpdated) {
        this.pPartNameUpdated = pPartNameUpdated;
    }

    public String getBuyerCode() {
        return buyerCode;
    }

    public void setBuyerCode(String buyerCode) {
        this.buyerCode = buyerCode;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getPurchaseOrgName() {
        return purchaseOrgName;
    }

    public void setPurchaseOrgName(String purchaseOrgName) {
        this.purchaseOrgName = purchaseOrgName;
    }

    public String getPurchaseOrgCode() {
        return purchaseOrgCode;
    }

    public void setPurchaseOrgCode(String purchaseOrgCode) {
        this.purchaseOrgCode = purchaseOrgCode;
    }
    
    public Date getOrderStaDate() {
        return orderStaDate;
    }
    
    public void setOrderStaDate(Date orderStaDate) {
        this.orderStaDate = orderStaDate;
    }

    public Date getProcureFailureDate() {
        return procureFailureDate;
    }

    public void setProcureFailureDate(Date procureFailureDate) {
        this.procureFailureDate = procureFailureDate;
    }

    public long getPerQuantity() {
        return perQuantity;
    }

    public void setPerQuantity(long perQuantity) {
        this.perQuantity = perQuantity;
    }

    public String getGroupingKeyCompress() throws IOException {
        return Utils.decompress(groupingKeyCompress);
    }

    public void setGroupingKeyCompress(String groupingKeyCompress) throws IOException {
        this.groupingKeyCompress = Utils.compress(groupingKeyCompress);
    }
    
    public String getGroupingKeyMd5() {
        return groupingKeyMd5;
    }

    public void setGroupingKeyMd5(String groupingKeyMd5) throws NoSuchAlgorithmException {
        this.groupingKeyMd5 = groupingKeyMd5;
    }
    
    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<Supplier> suppliers) {
        this.suppliers = suppliers;
    }

    public boolean isCrHasBeenMoved() {
        return crHasBeenMoved;
    }

    public void setCrHasBeenMoved(boolean crHasBeenMoved) {
        this.crHasBeenMoved = crHasBeenMoved;
    }

    public long getFromStockQty() {
        return fromStockQty;
    }

    public void setFromStockQty(long fromStockQty) {
        this.fromStockQty = fromStockQty;
    }

    public long getFromStockProjectQty() {
        return fromStockProjectQty;
    }

    public void setFromStockProjectQty(long fromStockProjectQty) {
        this.fromStockProjectQty = fromStockProjectQty;
    }
    
    public boolean isContentEdited() {
        return contentEdited;
    }

    public void setContentEdited(boolean contentEdited) {
        this.contentEdited = contentEdited;
    }
}
