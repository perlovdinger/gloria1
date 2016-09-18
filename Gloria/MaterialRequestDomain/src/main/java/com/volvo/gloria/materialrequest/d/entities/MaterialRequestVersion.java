package com.volvo.gloria.materialrequest.d.entities;

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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.materialrequest.d.status.materialrequest.MaterialRequestStatus;

/**
 * Entity implementation class for Entity: MaterialRequestVersion.
 */
@Entity
@Table(name = "MATERIAL_REQ_VERSION")
public class MaterialRequestVersion implements Serializable {

    private static final long serialVersionUID = 2552651134800734320L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MATERIAL_REQ_VERSION_OID")
    private long materialRequestVersionOid;

    @Version
    private long version;

    private String mtrlRequestVersion;
    private long changeVersion;
    private String changeTechId;
    private String title;
    private Date requiredStaDate;
    private String outboundLocationId;
    private Date outboundStartDate;
    private String mailFormId;
    private double approvalAmount;
    private String approvalCurrency;
    @Enumerated(EnumType.STRING)
    private MaterialRequestStatus status;
    private Date statusDate;

    @ManyToOne
    @JoinColumn(name = "MATERIAL_REQUEST_OID")
    private MaterialRequest materialRequest;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "materialRequestVersion")
    private List<MaterialRequestLine> materialRequestLines = new ArrayList<MaterialRequestLine>();

    @OneToOne(cascade = CascadeType.ALL)
    private MaterialRequestObject materialRequestObject;

    public long getMaterialRequestVersionOid() {
        return materialRequestVersionOid;
    }

    public void setMaterialRequestVersionOid(long materialRequestVersionOid) {
        this.materialRequestVersionOid = materialRequestVersionOid;
    }

    public long getVersion() {
        return version;
    }

    public String getMtrlRequestVersion() {
        return mtrlRequestVersion;
    }

    public void setMtrlRequestVersion(String mtrlRequestVersion) {
        this.mtrlRequestVersion = mtrlRequestVersion;
    }

    public long getChangeVersion() {
        return changeVersion;
    }

    public void setChangeVersion(long changeVersion) {
        this.changeVersion = changeVersion;
    }

    public MaterialRequest getMaterialRequest() {
        return materialRequest;
    }

    public void setMaterialRequest(MaterialRequest materialRequest) {
        this.materialRequest = materialRequest;
    }

    public List<MaterialRequestLine> getMaterialRequestLines() {
        return materialRequestLines;
    }

    public void setMaterialRequestLines(List<MaterialRequestLine> materialRequestLines) {
        this.materialRequestLines = materialRequestLines;
    }   

    public String getChangeTechId() {
        return changeTechId;
    }

    public void setChangeTechId(String changeTechId) {
        this.changeTechId = changeTechId;
    }

    public MaterialRequestStatus getStatus() {
        return status;
    }

    public void setStatus(MaterialRequestStatus status) {
        this.status = status;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public Date getRequiredStaDate() {
        return requiredStaDate;
    }

    public void setRequiredStaDate(Date requiredStaDate) {
        this.requiredStaDate = requiredStaDate;
    }

    public MaterialRequestObject getMaterialRequestObject() {
        return materialRequestObject;
    }

    public void setMaterialRequestObject(MaterialRequestObject materialRequestObject) {
        this.materialRequestObject = materialRequestObject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOutboundLocationId() {
        return outboundLocationId;
    }

    public void setOutboundLocationId(String outboundLocationId) {
        this.outboundLocationId = outboundLocationId;
    }

    public Date getOutboundStartDate() {
        return outboundStartDate;
    }

    public void setOutboundStartDate(Date outboundStartDate) {
        this.outboundStartDate = outboundStartDate;
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

    public void setVersion(long version) {
        this.version = version;
    }
    
    @PrePersist
    public void setChange() {
        this.setChangeVersion(getChangeVersion() + 1);
        this.setMtrlRequestVersion(this.getMaterialRequest().getMaterialRequestId() + "V" + getChangeVersion());
    }
}
