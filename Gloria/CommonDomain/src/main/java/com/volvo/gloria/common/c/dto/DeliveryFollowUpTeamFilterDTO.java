package com.volvo.gloria.common.c.dto;

import java.io.Serializable;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * data class for DeliveryFollowUpTeamFilter.
 * 
 */
public class DeliveryFollowUpTeamFilterDTO implements Serializable, PageResults {

    private static final long serialVersionUID = 1L;

    private long version;
    private Long id;
    private String supplierId;
    private String projectId;
    private String suffix;
    private String deliveryControllerUserId;

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getDeliveryControllerUserId() {
        return deliveryControllerUserId;
    }

    public void setDeliveryControllerUserId(String deliveryControllerUserId) {
        this.deliveryControllerUserId = deliveryControllerUserId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
