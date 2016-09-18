package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.procurematerial.c.ProcureResponsibility;
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatus;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.modification.ModificationType;
import com.volvo.gloria.procurematerial.d.type.request.RequestType;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.Utils;
import com.volvo.gloria.util.c.GloriaParams;

/**
 * Entity class for Material.
 */
@Entity
@Table(name = "MATERIAL")
public class Material implements Serializable {

    private static final long serialVersionUID = -4811853449420572126L;

    // this is used to compute the key while the grouping is still being decided
    @Transient
    private ProcureLine temporaryProcureLine;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MATERIAL_OID")
    private long materialOID;

    @Version
    private long version;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "material")
    private List<MaterialLine> materialLine = new ArrayList<MaterialLine>();

    @ManyToOne
    @JoinColumn(name = "ORDER_LINE_OID")
    private OrderLine orderLine;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "material")
    private List<MaterialPartAlias> partAlias = new ArrayList<MaterialPartAlias>();

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "FINANCE_HEADER_OID")
    private FinanceHeader financeHeader;

    @ManyToOne
    @JoinColumn(name = "MATERIAL_HEADER_OID")
    private MaterialHeader materialHeader;

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH })
    @JoinColumn(name = "PROCURE_LINE_OID")
    private ProcureLine procureLine;

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "REMOVE_CHANGE_ID_OID", nullable = true)
    private ChangeId remove;

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "ADD_CHANGE_ID_OID", nullable = true)
    private ChangeId add;

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH })
    @JoinColumn(name = "MATERIAL_LAST_MODIFIED_OID")
    private MaterialLastModified materialLastModified = new MaterialLastModified();

    @Enumerated(EnumType.STRING)
    private MaterialType materialType;
    private long procureLinkId;
    @Enumerated(EnumType.STRING)
    private MaterialStatus status = MaterialStatus.START;
    private String partAffiliation;
    private String partNumber;
    private String partVersion;
    private String partVersionOriginal;
    private String partName;
    private String partModification;

    private String unitOfMeasure;
    private Date requiredStaDate;
    private String characteristics;
    private String demarcation;
    private String productClass;
    private String designResponsible;
    private String functionGroup;
    private long itemToVariantLinkId;
    private String linkFUnctionGroupSuffix;
    private String linkFunctionGroup;
    private String refAssemblyPartNo;
    private String refAssemblyPartVersion;
    private String modularHarness;
    private String objectNumber;
    private String mailFormId;
    private String procureComment;
    private long replacedByOid;
    private String orderNo;
    private boolean migrated;
    private long modificationId;
    @Enumerated(EnumType.STRING)
    private ModificationType modificationType;
    @Enumerated(EnumType.STRING)
    private MaterialStatus rejectChangeStatus;

    private Date createdDate = DateUtil.getCurrentUTCDateTime();
    private boolean partNameUpdated;
    private String receiver;
    private String mtrlRequestVersionAccepted;

    private boolean carryOverExist;

    private boolean carryOverExistAndMatched;
    
    private long matchedCarryOverOid;

    @Transient
    private String mark;

    @Transient
    private String[] finalWhSiteNames;

    private boolean addedAfter;

    @Transient
    private String changeAction;

    @Transient
    private boolean isOrderCancelled;

    public boolean isOrderCancelled() {
        return isOrderCancelled;
    }

    public void setOrderCancelled(boolean isOrderCancelled) {
        this.isOrderCancelled = isOrderCancelled;
    }

    public long getMaterialOID() {
        return materialOID;
    }

    public void setMaterialOID(long materialOID) {
        this.materialOID = materialOID;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public List<MaterialLine> getMaterialLine() {
        return materialLine;
    }

    public void setMaterialLine(List<MaterialLine> materialLine) {
        this.materialLine = materialLine;
    }

    public void addMaterialLine(MaterialLine materialLine) {
        this.materialLine.add(materialLine);
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public MaterialType getMaterialType() {
        return materialType;
    }

    public void setMaterialType(MaterialType materialType) {
        this.materialType = materialType;
    }

    public boolean isMigrated() {
        return migrated;
    }

    public void setMigrated(boolean migrated) {
        this.migrated = migrated;
    }

    public OrderLine getOrderLine() {
        return orderLine;
    }

    public void setOrderLine(OrderLine orderLine) {
        this.orderLine = orderLine;
    }

    public long getProcureLinkId() {
        return procureLinkId;
    }

    public void setProcureLinkId(long procureLinkId) {
        this.procureLinkId = procureLinkId;
    }

    public MaterialStatus getStatus() {
        return status;
    }

    public void setStatus(MaterialStatus status) {
        this.status = status;
    }

    public String getPartAffiliation() {
        return partAffiliation;
    }

    public void setPartAffiliation(String partAffiliation) {
        this.partAffiliation = StringUtils.upperCase(partAffiliation);
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = StringUtils.upperCase(partNumber);
    }

    public String getPartVersion() {
        return partVersion;
    }

    public void setPartVersion(String partVersion) {
        this.partVersion = StringUtils.upperCase(partVersion);
    }

    public String getPartVersionOriginal() {
        return partVersionOriginal;
    }

    public void setPartVersionOriginal(String partVersionOriginal) {
        this.partVersionOriginal = StringUtils.upperCase(partVersionOriginal);
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = StringUtils.upperCase(partName);
    }

    public String getPartModification() {
        return partModification;
    }

    public void setPartModification(String partModification) {
        if (StringUtils.isBlank(partModification)) {
            this.partModification = null;
        } else {
            this.partModification = StringUtils.upperCase(partModification);
        }
    }

    public Date getRequiredStaDate() {
        return requiredStaDate;
    }

    public void setRequiredStaDate(Date requiredStaDate) {
        this.requiredStaDate = requiredStaDate;
    }

    public String getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(String characteristics) {
        this.characteristics = characteristics;
    }

    public String getDemarcation() {
        return demarcation;
    }

    public void setDemarcation(String demarcation) {
        this.demarcation = demarcation;
    }

    public String getProductClass() {
        return productClass;
    }

    public void setProductClass(String productClass) {
        this.productClass = productClass;
    }

    public String getDesignResponsible() {
        return designResponsible;
    }

    public void setDesignResponsible(String designResponsible) {
        this.designResponsible = designResponsible;
    }

    public String getFunctionGroup() {
        return functionGroup;
    }

    public void setFunctionGroup(String functionGroup) {
        this.functionGroup = functionGroup;
    }

    public long getItemToVariantLinkId() {
        return itemToVariantLinkId;
    }

    public void setItemToVariantLinkId(long itemToVariantLinkId) {
        this.itemToVariantLinkId = itemToVariantLinkId;
    }

    public String getLinkFUnctionGroupSuffix() {
        return linkFUnctionGroupSuffix;
    }

    public void setLinkFUnctionGroupSuffix(String linkFUnctionGroupSuffix) {
        this.linkFUnctionGroupSuffix = linkFUnctionGroupSuffix;
    }

    public String getLinkFunctionGroup() {
        return linkFunctionGroup;
    }

    public void setLinkFunctionGroup(String linkFunctionGroup) {
        this.linkFunctionGroup = linkFunctionGroup;
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

    public String getMailFormId() {
        return mailFormId;
    }

    public void setMailFormId(String mailFormId) {
        this.mailFormId = mailFormId;
    }

    public long getReplacedByOid() {
        return replacedByOid;
    }

    public void setReplacedByOid(long replacedByOid) {
        this.replacedByOid = replacedByOid;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public FinanceHeader getFinanceHeader() {
        return financeHeader;
    }

    public void setFinanceHeader(FinanceHeader financeHeader) {
        this.financeHeader = financeHeader;
    }

    public MaterialHeader getMaterialHeader() {
        return materialHeader;
    }

    public void setMaterialHeader(MaterialHeader materialHeader) {
        this.materialHeader = materialHeader;
    }

    public ProcureLine getProcureLine() {
        return procureLine;
    }

    public void setProcureLine(ProcureLine procureLine) {
        this.procureLine = procureLine;
    }

    public ChangeId getRemove() {
        return remove;
    }

    public void setRemove(ChangeId remove) {
        this.remove = remove;
    }

    public ChangeId getAdd() {
        return add;
    }

    public void setAdd(ChangeId add) {
        this.add = add;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public long getModificationId() {
        return modificationId;
    }

    public void setModificationId(long modificationId) {
        this.modificationId = modificationId;
    }

    public ModificationType getModificationType() {
        return modificationType;
    }

    public void setModificationType(ModificationType modificationType) {
        this.modificationType = modificationType;
    }

    public MaterialStatus getRejectChangeStatus() {
        return rejectChangeStatus;
    }

    public void setRejectChangeStatus(MaterialStatus rejectChangeStatus) {
        this.rejectChangeStatus = rejectChangeStatus;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getMtrlRequestVersionAccepted() {
        return mtrlRequestVersionAccepted;
    }

    public void setMtrlRequestVersionAccepted(String mtrlRequestVersionAccepted) {
        this.mtrlRequestVersionAccepted = mtrlRequestVersionAccepted;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getGroupingKeyMd5() throws NoSuchAlgorithmException {
        return Utils.md5Checksum(this.getGroupIdentifierKey(GloriaParams.GROUP_TYPE_DEFAULT));
    }

    protected Map<String, String> getDefaultGroupIdentifierKey() {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        if (financeHeader != null) {
            map.put("projectId", financeHeader.getProjectId());
            map.put("glAccount", financeHeader.getGlAccount());
            map.put("costCenter", financeHeader.getCostCenter());
            map.put("wbsCode", financeHeader.getWbsCode());
            map.put("internalOrderNoSAP", StringUtils.trimToEmpty(financeHeader.getInternalOrderNoSAP()));
        } else {
            map.put("projectId", "");
            map.put("glAccount", "");
            map.put("costCenter", "");
            map.put("wbsCode", "");
            map.put("internalOrderNoSAP", "");
        }
        if (materialHeader.getRequestType() == RequestType.FOR_STOCK) {
            map.put("requestType", "MTREQ_FORSTOCK");
        } else {
            map.put("requestType", "MTREQ_OTHERS");
        }
        map.put("partaffiliation", StringUtils.trimToEmpty(partAffiliation));
        map.put("partnumber", StringUtils.trimToEmpty(partNumber));
        map.put("partversion", StringUtils.trimToEmpty(partVersion));
        map.put("partmodification", StringUtils.trimToEmpty(partModification));
        map.put("unitofmeasure", StringUtils.trimToEmpty(unitOfMeasure));
        if (this.temporaryProcureLine != null) {
            map.putAll(addKeysFromProcureLine(temporaryProcureLine));
        } else if (this.getProcureLine() != null) {
            map.putAll(addKeysFromProcureLine(this.getProcureLine()));
        } else {
            map.put("forwardedUserId", "");
            map.put("forwardedTeam", "");
            map.put("responsibility", ProcureResponsibility.PROCURER.name());
            map.put("buildSiteOutboundLocationId", "");
        }
        return map;
    }

    private LinkedHashMap<String, String> addKeysFromProcureLine(ProcureLine procureLine) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        // calculate key from procureline if possible
        map.put("forwardedUserId", StringUtils.trimToEmpty(procureLine.getForwardedUserId()));
        map.put("forwardedTeam", StringUtils.trimToEmpty(procureLine.getForwardedTeam()));
        ProcureResponsibility responsibility = procureLine.getResponsibility();
        if (responsibility != null) {
            map.put("responsibility", responsibility.name());
            if (responsibility.equals(ProcureResponsibility.BUILDSITE)) {
                map.put("buildSiteOutboundLocationId", materialHeader.getAccepted().getOutboundLocationId());
            } else {
                map.put("buildSiteOutboundLocationId", "");
            }
        } else {
            map.put("responsibility", "");
            map.put("buildSiteOutboundLocationId", "");
        }
        return map;
    }

    public void setTemporaryProcureLine(ProcureLine temporaryProcureLine) {
        this.temporaryProcureLine = temporaryProcureLine;
    }

    /*
     * Do not change this after go live as this will affect all grouping functionality. If you do wish to change this then it should work both with old and new
     * logic. Nearly 8 use cases use this logic
     */
    public String getGroupIdentifierKey(String groupingType) {
        if (GloriaParams.GROUP_TYPE_DEFAULT.equals(groupingType)) {
            return "GROUPKEY [" + this.getDefaultGroupIdentifierKey().toString() + "]";
        }

        String key = "projectId=" + financeHeader.getProjectId() + ",glAccount=" + financeHeader.getGlAccount() + ",costCenter="
                + financeHeader.getCostCenter() + ",wbsCode=" + financeHeader.getWbsCode() + ",internalOrderNoSAP="
                + StringUtils.trimToEmpty(financeHeader.getInternalOrderNoSAP());

        if (materialHeader.getRequestType() == RequestType.FOR_STOCK) {
            key = key + ",requestType=MTREQ_FORSTOCK";
        } else {
            key = key + ",requestType=MTREQ_OTHERS";
        }

        if (!groupingType.equalsIgnoreCase(GloriaParams.GROUP_TYPE_SAME_TOBJ_DIFF_PART)) {
            key = key + ",partAffiliation=" + StringUtils.trimToEmpty(partAffiliation) + ", partNumber=" + StringUtils.trimToEmpty(partNumber)
                    + ", partVersion=" + StringUtils.trimToEmpty(partVersion) + ", partModification=" + StringUtils.trimToEmpty(partModification)
                    + ", unitOfMeasure=" + StringUtils.trimToEmpty(unitOfMeasure);
        }
        return "GROUPKEY [" + key.toLowerCase() + "]";
    }

    public String getFinanceCriteriaKey() {
        String key = "projectId=" + financeHeader.getProjectId() + ",glAccount=" + financeHeader.getGlAccount() + ",costCenter="
                + financeHeader.getCostCenter() + ",wbsCode=" + financeHeader.getWbsCode() + ",internalOrderNoSAP=" + financeHeader.getInternalOrderNoSAP();
        return key.toLowerCase();
    }

    public boolean isPartNameUpdated() {
        return partNameUpdated;
    }

    public void setPartNameUpdated(boolean partNameUpdated) {
        this.partNameUpdated = partNameUpdated;
    }

    public String getPartAndTypeIdentifierKey() {
        return ",partAffiliation=" + StringUtils.trimToEmpty(partAffiliation) + ", partNumber=" + StringUtils.trimToEmpty(partNumber) + ", partVersion="
                + StringUtils.trimToEmpty(partVersion) + ", partModification=" + StringUtils.trimToEmpty(partModification) + ", MaterialType="
                + materialType.toString();
    }

    public long getQuantity() {
        long quantity = 0;
        if (this.materialLine != null && !this.materialLine.isEmpty()) {
            for (MaterialLine line : this.materialLine) {
                if (!line.isOrderCancelled()) {
                    quantity += line.getQuantity();
                }
            }
        }
        return quantity;
    }
    
    
    public long getQuantityForMaterialLineList(List<MaterialLine> materialLineList) {
        long quantity = 0;
        if (materialLineList != null && !materialLineList.isEmpty()) {
            for (MaterialLine line : materialLineList) {
                if (!line.isOrderCancelled()) {
                    quantity += line.getQuantity();
                }
            }
        }
        return quantity;
    }

    public long getQuantityInclusiveCancelled() {
        long quantityInclusiveCancelled = 0;
        if (this.materialLine != null && !this.materialLine.isEmpty()) {
            for (MaterialLine line : this.materialLine) {
                quantityInclusiveCancelled += line.getQuantity();
            }
        }
        return quantityInclusiveCancelled;
    }

    public String[] getFinalWhSiteNames() {
        return finalWhSiteNames;
    }

    public void setFinalWhSiteNames(String[] finalWhSiteNames) {
        this.finalWhSiteNames = finalWhSiteNames;
    }

    public boolean isCarryOverExist() {
        return carryOverExist;
    }

    public void setCarryOverExist(boolean carryOverExist) {
        this.carryOverExist = carryOverExist;
    }

    public boolean isCarryOverExistAndMatched() {
        return carryOverExistAndMatched;
    }

    public void setCarryOverExistAndMatched(boolean carryOverExistAndMatched) {
        this.carryOverExistAndMatched = carryOverExistAndMatched;
    }

    public long getMatchedCarryOverOid() {
        return matchedCarryOverOid;
    }
    
    public void setMatchedCarryOverOid(long matchedCarryOverOid) {
        this.matchedCarryOverOid = matchedCarryOverOid;
    }
    
    public void setAddedAfter(boolean addedAfter) {
        this.addedAfter = addedAfter;
    }

    public boolean isAddedAfter() {
        return addedAfter;
    }

    public MaterialLastModified getMaterialLastModified() {
        return materialLastModified;
    }

    public void setMaterialLastModified(MaterialLastModified materialLastModified) {
        this.materialLastModified = materialLastModified;
    }

    public List<MaterialPartAlias> getPartAlias() {
        return partAlias;
    }

    public void setPartAlias(List<MaterialPartAlias> partAlias) {
        this.partAlias = partAlias;
    }

    public String getProcureComment() {
        return procureComment;
    }

    public void setProcureComment(String procureComment) {
        this.procureComment = procureComment;
    }

    public String getChangeAction() {
        return changeAction;
    }

    public void setChangeAction(String changeAction) {
        this.changeAction = changeAction;
    }

}
