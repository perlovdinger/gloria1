package com.volvo.gloria.procurerequest.c.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * RequestHeader DTO.
 */
public class RequestHeaderTransformerDTO implements Serializable, Comparable<RequestHeaderTransformerDTO> {

    private static final long serialVersionUID = -4635990243386273835L;

    private String mtrlRequestId;

    private String referenceId;

    private boolean active;
    

    private String buildId;
    
    private String buildName;
    
    private String  buildRemoveId;

    private String buildType;

    private String assignedMaterialControllerId;

    private String requestType;
    
    private boolean additonalUsageHeader;
    
    private String removeReferenceId;

    private List<RequestHeaderVersionTransformerDTO> requestHeaderVersionTransformerDtos = new ArrayList<RequestHeaderVersionTransformerDTO>();

    private List<RequestLineTransformerDTO> requestLineTransformerDTOs = new ArrayList<RequestLineTransformerDTO>();
    
    private boolean existsPhaseRemove;

    private boolean existsPhase;
    
    private boolean existsTestObjectRemove;

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

    public String getAssignedMaterialControllerId() {
        return assignedMaterialControllerId;
    }

    public void setAssignedMaterialControllerId(String assignedMaterialControllerId) {
        this.assignedMaterialControllerId = assignedMaterialControllerId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public List<RequestHeaderVersionTransformerDTO> getRequestHeaderVersionTransformerDtos() {
        return requestHeaderVersionTransformerDtos;
    }

    public void setRequestHeaderVersionTransformerDtos(List<RequestHeaderVersionTransformerDTO> requestHeaderVersionTransformerDtos) {
        this.requestHeaderVersionTransformerDtos = requestHeaderVersionTransformerDtos;
    }

    public List<RequestLineTransformerDTO> getRequestLineTransformerDTOs() {
        return requestLineTransformerDTOs;
    }

    public void setRequestLineTransformerDTOs(List<RequestLineTransformerDTO> requestLineTransformerDTOs) {
        this.requestLineTransformerDTOs = requestLineTransformerDTOs;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        if (this.getMtrlRequestId().equalsIgnoreCase(((RequestHeaderTransformerDTO) obj).getMtrlRequestId())) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getMtrlRequestId().hashCode();
    }

    public String getBuildType() {
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    public String getBuildRemoveId() {
        return buildRemoveId;
    }

    public void setBuildRemoveId(String buildRemoveId) {
        this.buildRemoveId = buildRemoveId;
    }

    public String getBuildName() {
        return buildName;
    }
    
    public void setBuildName(String buildName) {
        this.buildName = buildName;
    }
    
    @Override
    public int compareTo(RequestHeaderTransformerDTO requestHeaderTransformerDTO) {
        if (requestHeaderTransformerDTO.getRequestLineTransformerDTOs() != null && !requestHeaderTransformerDTO.getRequestLineTransformerDTOs().isEmpty()) {
            return 1;
        }
        return 0;
    }

    public boolean isAdditonalUsageHeader() {
        return additonalUsageHeader;
    }

    public void setAdditonalUsageHeader(boolean additonalUsageHeader) {
        this.additonalUsageHeader = additonalUsageHeader;
    }
    
    public String getRemoveReferenceId() {
        return removeReferenceId;
    }
    
    public void setRemoveReferenceId(String removeReferenceId) {
        this.removeReferenceId = removeReferenceId;
    }

    public boolean isExistsPhaseRemove() {
        return existsPhaseRemove;
    }

    public void setExistsPhaseRemove(boolean existsPhaseRemove) {
        this.existsPhaseRemove = existsPhaseRemove;
    }

    public boolean isExistsPhase() {
        return existsPhase;
    }

    public void setExistsPhase(boolean existsPhase) {
        this.existsPhase = existsPhase;
    }

    public boolean isExistsTestObjectRemove() {
        return existsTestObjectRemove;
    }

    public void setExistsTestObjectRemove(boolean existsTestObjectRemove) {
        this.existsTestObjectRemove = existsTestObjectRemove;
    }
}
