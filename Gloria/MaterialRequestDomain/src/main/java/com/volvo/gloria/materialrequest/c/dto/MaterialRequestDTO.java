package com.volvo.gloria.materialrequest.c.dto;

import java.io.Serializable;
import java.util.Date;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * DTO for the MaterialRequestHeader.
 */
public class MaterialRequestDTO implements Serializable, PageResults {

    private static final long serialVersionUID = 2036928120120819800L;

    private Long id;
    private long version;
    private long financeMaterialID;
    private long financeMaterialVersion;
    private String projectId;
    private String referenceGroup;
    private long materialRequestObjectID;
    private long materialRequestObjectVersion;
    private String name;
    private long materialRequestVersionId;
    private long materialRequestVersionVersion;
    private String title;
    private String mtrlRequestVersion;
    private String materialRequestVersionStatus;
    private Date materialRequestVersionStatusDate;
    private String contactPersonUserId;
    private String contactPersonName;
    private String requesterId;
    private String requesterName;
    private String type;
    private String companyCode;
    private String glAccount;
    private String wbsCode;
    private String costCenter;
    private String internalOrderNoSAP;
    private Date requiredStaDate;
    private Date outboundStartDate;
    private String outboundLocationId;
    private String mailFormId;
    private double approvalAmount;
    private String approvalCurrency;
    private String materialControllerUserId;
    private String materialControllerName;

    private String materialRequest;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getFinanceMaterialID() {
        return financeMaterialID;
    }

    public void setFinanceMaterialID(long financeMaterialID) {
        this.financeMaterialID = financeMaterialID;
    }

    public long getFinanceMaterialVersion() {
        return financeMaterialVersion;
    }

    public void setFinanceMaterialVersion(long financeMaterialVersion) {
        this.financeMaterialVersion = financeMaterialVersion;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getReferenceGroup() {
        return referenceGroup;
    }

    public void setReferenceGroup(String referenceGroup) {
        this.referenceGroup = referenceGroup;
    }

    public long getMaterialRequestObjectID() {
        return materialRequestObjectID;
    }

    public void setMaterialRequestObjectID(long materialRequestObjectID) {
        this.materialRequestObjectID = materialRequestObjectID;
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

    public long getMaterialRequestVersionId() {
        return materialRequestVersionId;
    }

    public void setMaterialRequestVersionId(long materialRequestVersionId) {
        this.materialRequestVersionId = materialRequestVersionId;
    }

    public long getMaterialRequestVersionVersion() {
        return materialRequestVersionVersion;
    }

    public void setMaterialRequestVersionVersion(long materialRequestVersionVersion) {
        this.materialRequestVersionVersion = materialRequestVersionVersion;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMtrlRequestVersion() {
        return mtrlRequestVersion;
    }

    public void setMtrlRequestVersion(String mtrlRequestVersion) {
        this.mtrlRequestVersion = mtrlRequestVersion;
    }

    public String getMaterialRequestVersionStatus() {
        return materialRequestVersionStatus;
    }

    public void setMaterialRequestVersionStatus(String materialRequestVersionStatus) {
        this.materialRequestVersionStatus = materialRequestVersionStatus;
    }

    public Date getMaterialRequestVersionStatusDate() {
        return materialRequestVersionStatusDate;
    }

    public void setMaterialRequestVersionStatusDate(Date materialRequestVersionStatusDate) {
        this.materialRequestVersionStatusDate = materialRequestVersionStatusDate;
    }

    public String getContactPersonUserId() {
        return contactPersonUserId;
    }

    public void setContactPersonUserId(String contactPersonUserId) {
        this.contactPersonUserId = contactPersonUserId;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getGlAccount() {
        return glAccount;
    }

    public void setGlAccount(String glAccount) {
        this.glAccount = glAccount;
    }

    public String getWbsCode() {
        return wbsCode;
    }

    public void setWbsCode(String wbsCode) {
        this.wbsCode = wbsCode;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getInternalOrderNoSAP() {
        return internalOrderNoSAP;
    }

    public void setInternalOrderNoSAP(String internalOrderNoSAP) {
        this.internalOrderNoSAP = internalOrderNoSAP;
    }

    public Date getRequiredStaDate() {
        return requiredStaDate;
    }

    public void setRequiredStaDate(Date requiredStaDate) {
        this.requiredStaDate = requiredStaDate;
    }

    public Date getOutboundStartDate() {
        return outboundStartDate;
    }

    public void setOutboundStartDate(Date outboundStartDate) {
        this.outboundStartDate = outboundStartDate;
    }

    public String getOutboundLocationId() {
        return outboundLocationId;
    }

    public void setOutboundLocationId(String outboundLocationId) {
        this.outboundLocationId = outboundLocationId;
    }

    public String getMailFormId() {
        return mailFormId;
    }

    public void setMailFormId(String mailFormId) {
        this.mailFormId = mailFormId;
    }

    public double getApprovalAmount() {
        return approvalAmount;
    }

    public void setApprovalAmount(double approvalAmount) {
        this.approvalAmount = approvalAmount;
    }

    public String getApprovalCurrency() {
        return approvalCurrency;
    }

    public void setApprovalCurrency(String approvalCurrency) {
        this.approvalCurrency = approvalCurrency;
    }

    public String getMaterialRequest() {
        return materialRequest;
    }

    public void setMaterialRequest(String materialRequest) {
        this.materialRequest = materialRequest;
    }

    public String getMaterialControllerUserId() {
        return materialControllerUserId;
    }

    public void setMaterialControllerUserId(String materialControllerUserId) {
        this.materialControllerUserId = materialControllerUserId;
    }

    public String getMaterialControllerName() {
        return materialControllerName;
    }

    public void setMaterialControllerName(String materialControllerName) {
        this.materialControllerName = materialControllerName;
    }

   
}
