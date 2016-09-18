/*
 * Copyright 2013 Volvo Information Technology AB 
 * 
 * Licensed under the Volvo IT Corporate Source License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *      http://java.volvo.com/licenses/CORPORATE-SOURCE-LICENSE 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.volvo.gloria.common.purchaseorder.c.dto;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

/**
 * DTO class for Sync Carry over part information from GPS system.
 */
public class PurchaseOrderHeaderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderId;
    private String documentId;
    private String customerId;
    private String customerName;
    private String supplierId;
    private String supplierName;
    private String orderMode;
    private String supplierCountryCode;
    private Date requestedShipdate;
    private List<PurchaseOrderLineDTO> purchaseOrderLineDTOs;

    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public List<PurchaseOrderLineDTO> getPurchaseOrderLineDTOs() {
        return purchaseOrderLineDTOs;
    }

    public void setPurchaseOrderLineDTOs(List<PurchaseOrderLineDTO> purchaseOrderLineDTOs) {
        this.purchaseOrderLineDTOs = purchaseOrderLineDTOs;
    }

    public String getOrderMode() {
        return orderMode;
    }
    
    public void setOrderMode(String orderMode) {
        this.orderMode = orderMode;
    }
    
    public String getSupplierCountryCode() {
        return supplierCountryCode;
    }
    
    public void setSupplierCountryCode(String supplierCountryCode) {
        this.supplierCountryCode = supplierCountryCode;
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
        PurchaseOrderHeaderDTO other = (PurchaseOrderHeaderDTO) obj;
        if (!this.getCustomerId().equals(other.getCustomerId())) {
            return false;
        }
        return true;
    }

    /**
     * Base hashCode on releaseID. {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.getCustomerId().hashCode();
    }

    @Override
    public String toString() {
        return "PurchaseOrderHeaderDTO [orderId=" + orderId + ", customerId=" + customerId + ", customerName=" + customerName + ", supplierId=" + supplierId
                + ", supplierName=" + supplierName + ", supplierCountryCode=" + supplierCountryCode + "]";
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Date getRequestedShipdate() {
        return requestedShipdate;
    }

    public void setRequestedShipdate(Date requestedShipdate) {
        this.requestedShipdate = requestedShipdate;
    }

}
