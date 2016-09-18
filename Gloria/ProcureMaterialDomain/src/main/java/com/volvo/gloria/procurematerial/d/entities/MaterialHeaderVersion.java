package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * entity class for RequestHeaderVersion.
 */
@Entity
@Table(name = "MATERIAL_HEADER_VERSION")
public class MaterialHeaderVersion implements Serializable {

    private static final long serialVersionUID = -133652352325575237L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MATERIAL_HEADER_VERSION_OID")
    private long materialHeaderVersionOid;

    @ManyToOne
    @JoinColumn(name = "MATERIAL_HEADER_OID")
    private MaterialHeader materialHeader;

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "CHANGE_ID_OID")
    private ChangeId changeId;

    @Version
    private long version;

    private long headerVersion;
    private Date receivedDateTime;
    private String referenceGroup;    
    private String outboundLocationId;
    private String outboundLocationName;
    private String outboundLocationType;
    private Date outboundStartDate;
    private String requesterUserId;
    private String requesterName;
    private String requesterNotes;
    private String contactPersonId;
    private String contactPersonName;
    private String contactPersonEmail;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PREVIOUS_ACCEPTED_VERSION")
    private MaterialHeaderVersion previousAcceptedVersion;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PREVIOUS_CHANGE_ID")
    private ChangeId previousChangeId;

    private boolean swapped;
    
    public long getMaterialHeaderVersionOid() {
        return materialHeaderVersionOid;
    }

    public void setMaterialHeaderVersionOid(long materialHeaderVersionOid) {
        this.materialHeaderVersionOid = materialHeaderVersionOid;
    }

    public MaterialHeader getMaterialHeader() {
        return materialHeader;
    }

    public void setMaterialHeader(MaterialHeader materialHeader) {
        this.materialHeader = materialHeader;
    }

    public ChangeId getChangeId() {
        return changeId;
    }

    public void setChangeId(ChangeId changeId) {
        this.changeId = changeId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getHeaderVersion() {
        return headerVersion;
    }

    public void setHeaderVersion(long headerVersion) {
        this.headerVersion = headerVersion;
    }

    public Date getReceivedDateTime() {
        return receivedDateTime;
    }

    public void setReceivedDateTime(Date receivedDateTime) {
        this.receivedDateTime = receivedDateTime;
    }

    public String getReferenceGroup() {
        return referenceGroup;
    }

    public void setReferenceGroup(String referenceGroup) {
        this.referenceGroup = referenceGroup;
    }

    public String getOutboundLocationId() {
        return outboundLocationId;
    }

    public void setOutboundLocationId(String outboundLocationId) {
        this.outboundLocationId = outboundLocationId;
    }
    
    public String getOutboundLocationName() {
        return outboundLocationName;
    }
    
    public void setOutboundLocationName(String outboundLocationName) {
        this.outboundLocationName = outboundLocationName;
    }

    public Date getOutboundStartDate() {
        return outboundStartDate;
    }

    public void setOutboundStartDate(Date outboundStartDate) {
        this.outboundStartDate = outboundStartDate;
    }

    public String getRequesterUserId() {
        return requesterUserId;
    }

    public void setRequesterUserId(String requesterUserId) {
        this.requesterUserId = requesterUserId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterNotes() {
        return requesterNotes;
    }

    public void setRequesterNotes(String requesterNotes) {
        this.requesterNotes = requesterNotes;
    }

    public String getContactPersonId() {
        return contactPersonId;
    }

    public void setContactPersonId(String contactPersonId) {
        this.contactPersonId = contactPersonId;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getOutboundLocationType() {
        return outboundLocationType;
    }

    public void setOutboundLocationType(String outboundLocationType) {
        this.outboundLocationType = outboundLocationType;
    }

    public String getContactPersonEmail() {
        return contactPersonEmail;
    }

    public void setContactPersonEmail(String contactPersonEmail) {
        this.contactPersonEmail = contactPersonEmail;
    }

    public boolean isSwapped() {
        return swapped;
    }

    public void setSwapped(boolean swapped) {
        this.swapped = swapped;
    }

    public MaterialHeaderVersion getPreviousAcceptedVersion() {
        return previousAcceptedVersion;
    }

    public void setPreviousAcceptedVersion(MaterialHeaderVersion previousAcceptedVersion) {
        this.previousAcceptedVersion = previousAcceptedVersion;
    }
    
    public ChangeId getPreviousChangeId() {
        return previousChangeId;
    }

    public void setPreviousChangeId(ChangeId previousChangeId) {
        this.previousChangeId = previousChangeId;
    }

}
