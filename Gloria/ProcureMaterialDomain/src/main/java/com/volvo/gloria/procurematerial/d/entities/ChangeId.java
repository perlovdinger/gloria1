package com.volvo.gloria.procurematerial.d.entities;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.volvo.gloria.procurematerial.c.ChangeType;
import com.volvo.gloria.procurematerial.d.entities.status.changeid.ChangeIdStatus;
import com.volvo.gloria.procurematerial.d.type.request.RequestType;

/**
 * entity class for ChangeId.
 */
@Entity
@Table(name = "CHANGE_ID")
public class ChangeId implements Serializable {

    private static final long serialVersionUID = -716115256055854053L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHANGE_ID_OID")
    private long changeIdOid;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "changeId")
    private List<MaterialHeaderVersion> materialHeaderVersions = new ArrayList<MaterialHeaderVersion>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "add")
    private List<Material> addMaterials = new ArrayList<Material>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "remove")
    private List<Material> removeMaterials = new ArrayList<Material>();

    @Version
    private long version;

    private String changeTechId;

    @Enumerated(EnumType.STRING)
    private ChangeType type;
    private Date receivedDate;
    private String mtrlRequestVersion;
    @Enumerated(EnumType.STRING)
    private ChangeIdStatus status;
    private String acceptRejectByUserId;
    private String title;
    private String procureNotes;
    private boolean visibleUi;
    private String priority;
    private String procureMessageId;
    private String procureRequestId;

    private String crId;
    private boolean crHasBeenMoved;

    
    public String getProcureRequestId() {
        return procureRequestId;
    }

    public void setProcureRequestId(String procureRequestId) {
        this.procureRequestId = procureRequestId;
    }

    public long getChangeIdOid() {
        return changeIdOid;
    }

    public void setChangeIdOid(long changeIdOid) {
        this.changeIdOid = changeIdOid;
    }

    public List<MaterialHeaderVersion> getMaterialHeaderVersions() {
        return materialHeaderVersions;

    }

    public void setMaterialHeaderVersions(List<MaterialHeaderVersion> materialHeaderVersions) {
        this.materialHeaderVersions = materialHeaderVersions;
    }

    public List<Material> getAddMaterials() {
        return addMaterials;
    }

    public void setAddMaterials(List<Material> addMaterials) {
        this.addMaterials = addMaterials;
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

    public String getMtrlRequestVersion() {
        return mtrlRequestVersion;
    }

    public void setMtrlRequestVersion(String mtrlRequestVersion) {
        this.mtrlRequestVersion = mtrlRequestVersion;
    }

    public ChangeIdStatus getStatus() {
        return status;
    }

    public void setStatus(ChangeIdStatus status) {
        this.status = status;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public ChangeType getType() {
        return type;
    }

    public void setType(ChangeType type) {
        this.type = type;
    }

    public String getProcureNotes() {
        return procureNotes;
    }

    public void setProcureNotes(String procureNotes) {
        this.procureNotes = procureNotes;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public List<Material> getRemoveMaterials() {
        return removeMaterials;
    }

    public void setRemoveMaterials(List<Material> removeMaterials) {
        this.removeMaterials = removeMaterials;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAcceptRejectByUserId() {
        return acceptRejectByUserId;
    }

    public void setAcceptRejectByUserId(String acceptRejectByUserId) {
        this.acceptRejectByUserId = acceptRejectByUserId;
    }

    public String getProcureMessageId() {
        return procureMessageId;
    }

    public void setProcureMessageId(String procureMessageId) {
        this.procureMessageId = procureMessageId;
    }

    public boolean isVisibleUi() {
        return visibleUi;
    }

    public void setVisibleUi(boolean visibleUi) {
        this.visibleUi = visibleUi;
    }

    @Transient
    public RequestType getRequestType() {
        List<MaterialHeaderVersion> headerVersions = this.getMaterialHeaderVersions();
        if (headerVersions != null && !headerVersions.isEmpty()) {
            return headerVersions.get(0).getMaterialHeader().getRequestType();
        }
        return null;
    }

    public String getCrId() {
        return crId;
    }

    public void setCrId(String crId) {
        this.crId = crId;
    }

    public boolean isCrHasBeenMoved() {
        return crHasBeenMoved;
    }

    public void setCrHasBeenMoved(boolean crHasBeenMoved) {
        this.crHasBeenMoved = crHasBeenMoved;
    }
}
