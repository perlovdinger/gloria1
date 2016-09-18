package com.volvo.gloria.financeProxy.c;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProcessPurchaseOrderDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4579502252834514690L;
    private String action;
    private String companyCode;
    private String orderType;
    private String vendor;
    private String purchaseOrganisation;
    private String purhchaseGroup;
    private Date documentDate;
    private String currency;
    private String purchaseType;
    private String uniqueExtOrder;
    private boolean suppressDecimal;
    
    private List<OrderSapLineDTO> orderSapLines = new ArrayList<OrderSapLineDTO>();

    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getPurchaseOrganisation() {
        return purchaseOrganisation;
    }

    public void setPurchaseOrganisation(String purchaseOrganisation) {
        this.purchaseOrganisation = purchaseOrganisation;
    }

    public String getPurhchaseGroup() {
        return purhchaseGroup;
    }

    public void setPurhchaseGroup(String purhchaseGroup) {
        this.purhchaseGroup = purhchaseGroup;
    }

    public Date getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(Date documentDate) {
        this.documentDate = documentDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

    public String getUniqueExtOrder() {
        return uniqueExtOrder;
    }

    public void setUniqueExtOrder(String uniqueExtOrder) {
        this.uniqueExtOrder = uniqueExtOrder;
    }

    public List<OrderSapLineDTO> getOrderSapLines() {
        return orderSapLines;
    }

    public void setOrderSapLines(List<OrderSapLineDTO> orderSapLines) {
        this.orderSapLines = orderSapLines;
    }

    public boolean isSuppressDecimal() {
        return suppressDecimal;
    }

    public void setSuppressDecimal(boolean suppressDecimal) {
        this.suppressDecimal = suppressDecimal;
    }
}
