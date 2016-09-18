package com.volvo.gloria.common.c.dto;

import java.io.Serializable;

/**
 * DTO class for maintaining Delivery Controller details.
 */
public class DeliveryControllerDetailsDTO implements Serializable {

    private static final long serialVersionUID = -6879980366144105591L;

    private Long id;
    private Long deliveryControllerId;
    private String userId;
    private String supplierId;
    private String supplierName;
    private String projectName;
    private String projectId;
    private String supplirtContactInfo;
    private String deliveryControllerSite;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeliveryControllerId() {
        return deliveryControllerId;
    }

    public void setDeliveryControllerId(Long deliveryControllerId) {
        this.deliveryControllerId = deliveryControllerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getSupplirtContactInfo() {
        return supplirtContactInfo;
    }

    public void setSupplirtContactInfo(String supplirtContactInfo) {
        this.supplirtContactInfo = supplirtContactInfo;
    }

    public String getDeliveryControllerSite() {
        return deliveryControllerSite;
    }

    public void setDeliveryControllerSite(String deliveryControllerSite) {
        this.deliveryControllerSite = deliveryControllerSite;
    }
}
