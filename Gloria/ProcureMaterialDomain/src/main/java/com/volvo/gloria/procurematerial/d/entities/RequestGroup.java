package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Entity implementation class for Entity: RequestGroup.
 * 
 */
@Entity
@Table(name = "REQUEST_GROUP")
public class RequestGroup implements Serializable {

    private static final long serialVersionUID = -7343781532929324802L;

    private static final int _2048 = 2048;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUEST_GROUP_OID")
    private long requestGroupOid;

    @Version
    private long version;

    @ManyToOne
    @JoinColumn(name = "REQUEST_LIST_OID")
    private RequestList requestList;

    @ManyToOne
    @JoinColumn(name = "PICK_LIST_OID")
    private PickList pickList;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH}, mappedBy = "requestGroup")
    private List<MaterialLine> materialLines = new ArrayList<MaterialLine>();

    private String projectId;
    private String referenceGroup;
    private String referenceId;
    private String phase;
    @Column(length = _2048)
    private String changeRequestIds;
    private String zoneId;

    public long getRequestGroupOid() {
        return requestGroupOid;
    }

    public void setRequestGroupOid(long requestGroupOid) {
        this.requestGroupOid = requestGroupOid;
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

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getChangeRequestIds() {
        return changeRequestIds;
    }

    public void setChangeRequestIds(String changeRequestIds) {
        this.changeRequestIds = changeRequestIds;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public RequestList getRequestList() {
        return requestList;
    }

    public void setRequestList(RequestList requestList) {
        this.requestList = requestList;
    }

    public List<MaterialLine> getMaterialLines() {
        return materialLines;
    }

    public void setMaterialLines(List<MaterialLine> materialLines) {
        this.materialLines = materialLines;
    }

    public PickList getPickList() {
        return pickList;
    }

    public void setPickList(PickList pickList) {
        this.pickList = pickList;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
    
    public String getIdentifierKey() {
        return "projectId=" + this.projectId + ",referenceGroup=" + this.referenceGroup + ",referenceId=" + this.referenceId + ",phase=" + this.phase
                + ",Zone=" + this.zoneId;
    }
}
