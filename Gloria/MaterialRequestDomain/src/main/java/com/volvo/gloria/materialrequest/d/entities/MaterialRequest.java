package com.volvo.gloria.materialrequest.d.entities;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import com.volvo.gloria.materialrequest.d.type.materialrequest.MaterialRequestType;
import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entity implementation class for Entity: MaterialRequest.
 */
@Entity
@Table(name = "MATERIAL_REQUEST")
public class MaterialRequest implements GenericEntity<Long>, Serializable {

    private static final long serialVersionUID = -9051665244288509557L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MATERIAL_REQUEST_OID")
    private long materialRequestOid;

    @Version
    private long version;

    @Enumerated(EnumType.STRING)
    private MaterialRequestType type;
    private String materialRequestId;    
    private String contactPersonUserId;
    private String contactPersonName;
    private String requesterId;
    private String requesterName;
    private String materialControllerUserId;
    private String materialControllerName;
  
    @TableGenerator(name = "MATERIAL_REQ_SEQUENCE", table = "MATERIAL_REQ_SEQUENCE", pkColumnName = "MATERIAL_REQ_SEQUENCE_NAME", 
            valueColumnName = "MATERIAL_REQ_SEQUENCE_COUNT", pkColumnValue = "MATERIAL_REQ_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MATERIAL_REQ_SEQUENCE")
    private long materialRequestIdSequence;

    @OneToOne(cascade = CascadeType.ALL)
    private MaterialRequestVersion current = new MaterialRequestVersion();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "FINANCE_MATERIAL_OID")
    private FinanceMaterial financeMaterial;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "materialRequest")
    private List<MaterialRequestVersion> materialRequestVersions = new ArrayList<MaterialRequestVersion>();

    public long getMaterialRequestOid() {
        return materialRequestOid;
    }

    public void setMaterialRequestOid(long materialRequestOid) {
        this.materialRequestOid = materialRequestOid;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public MaterialRequestType getType() {
        return type;
    }

    public void setType(MaterialRequestType type) {
        this.type = type;
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

    public FinanceMaterial getFinanceMaterial() {
        return financeMaterial;
    }

    public void setFinanceMaterial(FinanceMaterial financeMaterial) {
        this.financeMaterial = financeMaterial;
    }

    public List<MaterialRequestVersion> getMaterialRequestVersions() {
        return materialRequestVersions;
    }

    public void setMaterialRequestVersions(List<MaterialRequestVersion> materialRequestVersions) {
        this.materialRequestVersions = materialRequestVersions;
    }

    public MaterialRequestVersion getCurrent() {
        return current;
    }

    public void setCurrent(MaterialRequestVersion current) {
        this.current = current;
    }

    public String getMaterialRequestId() {
        return materialRequestId;
    }

    public void setMaterialRequestId(String materialRequestId) {
        this.materialRequestId = materialRequestId;
    }
    
    
    public long getMaterialRequestIdSequence() {
        return materialRequestIdSequence;
    }

    public void setMaterialRequestIdSequence(long materialRequestIdSequence) {
        this.materialRequestIdSequence = materialRequestIdSequence;
    }


    @Override
    public Long getId() {
        return materialRequestOid;
    }

    public String getContactPersonUserId() {
        return contactPersonUserId;
    }

    public void setContactPersonUserId(String contactPersonUserId) {
        this.contactPersonUserId = contactPersonUserId;
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
