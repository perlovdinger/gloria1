package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;

/**
 * Data class for request part Details.
 */
public class RequestPartDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5837867754568162906L;
    
    private long id;
    private long version;
    private String referenceId;
    private String partAffiliation;
    private String partNumber;
    private String partVersion;
    private String partName;
    private String partModification;
    private String partFunctionGroup;
    private String dfuObjectNumber;
    private String assemblyDistribution;
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getVersion() {
        return version;
    }
    public void setVersion(long version) {
        this.version = version;
    }
    public String getReferenceId() {
        return referenceId;
    }
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
    public String getPartAffiliation() {
        return partAffiliation;
    }
    public void setPartAffiliation(String partAffiliation) {
        this.partAffiliation = partAffiliation;
    }
    public String getPartNumber() {
        return partNumber;
    }
    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }
    public String getPartVersion() {
        return partVersion;
    }
    public void setPartVersion(String partVersion) {
        this.partVersion = partVersion;
    }
    public String getPartName() {
        return partName;
    }
    public void setPartName(String partName) {
        this.partName = partName;
    }
    public String getPartModification() {
        return partModification;
    }
    public void setPartModification(String partModification) {
        this.partModification = partModification;
    }
    public String getPartFunctionGroup() {
        return partFunctionGroup;
    }
    public void setPartFunctionGroup(String partFunctionGroup) {
        this.partFunctionGroup = partFunctionGroup;
    }
    public String getDfuObjectNumber() {
        return dfuObjectNumber;
    }
    public void setDfuObjectNumber(String dfuObjectNumber) {
        this.dfuObjectNumber = dfuObjectNumber;
    }
    public String getAssemblyDistribution() {
        return assemblyDistribution;
    }
    public void setAssemblyDistribution(String assemblyDistribution) {
        this.assemblyDistribution = assemblyDistribution;
    }
   
}
