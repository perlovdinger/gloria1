package com.volvo.gloria.common.wbs.c.dto;

import java.util.List;

import com.volvo.gloria.common.c.dto.WbsElementDTO;

/**
 * DTO class for Sync Purchase Order.
 */
public class SyncWBSElementDTO {
    private String creationDateTime;
    private List<WbsElementDTO> wbsElementDTO;

    public String getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(String creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public List<WbsElementDTO> getWbsElementDTO() {
        return wbsElementDTO;
    }

    public void setWbsElementDTO(List<WbsElementDTO> wbsElementDTO) {
        this.wbsElementDTO = wbsElementDTO;
    }

    /**
     * Base equals on releaseID. {@inheritDoc}
     */
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
        SyncWBSElementDTO other = (SyncWBSElementDTO) obj;
        if (!this.getCreationDateTime().equals(other.getCreationDateTime())) {
            return false;
        }
        return true;
    }

    /**
     * Base hashCode on releaseID. {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.getCreationDateTime().hashCode();
    }

}
