package com.volvo.gloria.procurematerial.d.entities;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.volvo.gloria.procurematerial.c.BuildType;
import com.volvo.gloria.procurematerial.d.type.request.RequestType;
import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * entity class for Request Header.
 */
@Entity
@Table(name = "MATERIAL_HEADER")
public class MaterialHeader implements GenericEntity<Long>, Serializable {

    private static final long serialVersionUID = -887328674251412783L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MATERIAL_HEADER_OID")
    private long materialHeaderOid;

    @Version
    private long version;

    @Enumerated(EnumType.STRING)
    private RequestType requestType;
    private String mtrlRequestId;
    private String referenceId;
    private String companyCode;    
    private String buildId;
    private String buildName;
    @Enumerated(EnumType.STRING)
    private BuildType buildType;    
    private String materialControllerUserId;
    private String materialControllerName;
    private String materialControllerTeam;
    private boolean active;

    @Transient
    private String mcIdToBeAssigned;
     
    @OneToOne(cascade = CascadeType.ALL)
    private MaterialHeaderVersion accepted;
    
    private Long firstAssemblyIdSequence;    

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "materialHeader")
    private List<MaterialHeaderVersion> materialHeaderVersions = new ArrayList<MaterialHeaderVersion>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "materialHeader")
    private List<Material> materials = new ArrayList<Material>();

    public long getMaterialHeaderOid() {
        return materialHeaderOid;
    }

    public void setMaterialHeaderOid(long requestHeaderOid) {
        this.materialHeaderOid = requestHeaderOid;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getMtrlRequestId() {
        return mtrlRequestId;
    }

    public void setMtrlRequestId(String mtrlRequestId) {
        this.mtrlRequestId = mtrlRequestId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
  
    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }
    
    public String getCompanyCode() {
        return companyCode;
    }
    
    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public List<MaterialHeaderVersion> getMaterialHeaderVersions() {
        return materialHeaderVersions;
    }

    public void setMaterialHeaderVersions(List<MaterialHeaderVersion> materialHeaderVersions) {
        this.materialHeaderVersions = materialHeaderVersions;
    }

    public MaterialHeaderVersion getAccepted() {
        return accepted;
    }

    public void setAccepted(MaterialHeaderVersion accepted) {
        this.accepted = accepted;
    }

    public BuildType getBuildType() {
        return buildType;
    }

    public void setBuildType(BuildType buildType) {
        this.buildType = buildType;
    }

    @Override
    public Long getId() {
        return materialHeaderOid;
    }
    /*
     * This is expensive to Use!
     */
    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
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

    public String getMaterialControllerTeam() {
        return materialControllerTeam;
    }

    public void setMaterialControllerTeam(String materialControllerTeam) {
        this.materialControllerTeam = materialControllerTeam;
    }
    
    public String getBuildName() {
        return buildName;
    }
    
    public void setBuildName(String buildName) {
        this.buildName = buildName;
    }
    
    public Long getFirstAssemblyIdSequence() {
        return firstAssemblyIdSequence;
    }
    
    public void setFirstAssemblyIdSequence(Long firstAssemblyIdSequence) {
        this.firstAssemblyIdSequence = firstAssemblyIdSequence;
    }
    
    public void setMcIdToBeAssigned(String mcIdToBeAssigned) {
        this.mcIdToBeAssigned = mcIdToBeAssigned;
    }
    
    public String getMcIdToBeAssigned() {
        return mcIdToBeAssigned;
    }
    
    @PrePersist
    protected void prePersist() {
        if (this.getBuildType() != BuildType.FIRSTASSEMBLY) {
            this.setFirstAssemblyIdSequence(null);
        }
    }
}
