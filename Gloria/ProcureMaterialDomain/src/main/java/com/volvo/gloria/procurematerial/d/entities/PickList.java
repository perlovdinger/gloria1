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
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.volvo.gloria.procurematerial.d.entities.status.picklist.PickListStatus;

/**
 * Entity implementation class for PickList.
 */
@Entity
@Table(name = "PICK_LIST")
public class PickList implements Serializable {

    private static final long serialVersionUID = -55216245350679497L;

    private static final int INITIAL_VALUE_1000 = 1000;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PICK_LIST_OID")
    private long pickListOid;

    @Version
    private long version;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH }, mappedBy = "pickList")
    private List<RequestGroup> requestGroups = new ArrayList<RequestGroup>();

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH }, mappedBy = "pickList")
    private List<MaterialLine> materialLines = new ArrayList<MaterialLine>();

    @TableGenerator(name = "SEQUENCE_PICKLISTCODE", table = "SEQUENCE", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", 
            pkColumnValue = "PICKLIST_SEQUENCE", allocationSize = 1, initialValue = INITIAL_VALUE_1000)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENCE_PICKLISTCODE")
    private Long pickListCodeSequence;
    
    private String code;
    private String pulledByUserId;

    @Enumerated(EnumType.STRING)
    private PickListStatus status;
    
    private String reservedUserId;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date reservedTimeStamp; 

    @Transient
    private boolean shipSkippable;
    
    public long getPickListOid() {
        return pickListOid;
    }

    public void setPickListOid(long pickListOid) {
        this.pickListOid = pickListOid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPulledByUserId() {
        return pulledByUserId;
    }

    public void setPulledByUserId(String pulledByUserId) {
        this.pulledByUserId = pulledByUserId;
    }

    public PickListStatus getStatus() {
        return status;
    }

    public void setStatus(PickListStatus status) {
        this.status = status;
    }

    public List<RequestGroup> getRequestGroups() {
        return requestGroups;
    }

    public void setRequestGroups(List<RequestGroup> requestGroups) {
        this.requestGroups = requestGroups;
    }

    public List<MaterialLine> getMaterialLines() {
        return materialLines;
    }

    public void setMaterialLines(List<MaterialLine> materialLines) {
        this.materialLines = materialLines;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getReservedUserId() {
        return reservedUserId;
    }

    public void setReservedUserId(String reservedUserId) {
        this.reservedUserId = reservedUserId;
    }

    public Date getReservedTimeStamp() {
        return reservedTimeStamp;
    }

    public void setReservedTimeStamp(Date reservedTimeStamp) {
        this.reservedTimeStamp = reservedTimeStamp;
    }

    public boolean isShipSkippable() {
        return shipSkippable;
    }
    
    public void setShipSkippable(boolean shipSkippable) {
        this.shipSkippable = shipSkippable;
    }
    
    public Long getPickListCodeSequence() {
        return pickListCodeSequence;
    }
    
    public void setPickListCodeSequence(Long pickListCodeSequence) {
        this.pickListCodeSequence = pickListCodeSequence;
    }
}
