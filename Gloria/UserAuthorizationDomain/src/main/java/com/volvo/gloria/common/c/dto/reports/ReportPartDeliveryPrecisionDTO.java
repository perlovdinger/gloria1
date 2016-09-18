package com.volvo.gloria.common.c.dto.reports;

import java.io.Serializable;

public class ReportPartDeliveryPrecisionDTO implements Serializable, Cloneable {

    private static final long serialVersionUID = -399099057778799259L;

    private String[] companyCode;
    private String[] suffix;
    private String[] project;
    private String[] buyerId;
    private String[] supplierParmaId;
    private String[] supplierParmaName;
    private String[] deliveryControllerId;
    private String[] source;
    private boolean numberOfDeliveries;
    private boolean numberOfOrderlines;
    private boolean deliveriesVSOrderSTA;
    private boolean deliveriesVSAgreedSTA;

    public String[] getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String[] companyCode) {
        this.companyCode = companyCode;
    }

    public String[] getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String[] buyerId) {
        this.buyerId = buyerId;
    }

    public String[] getSuffix() {
        return suffix;
    }

    public void setSuffix(String[] suffix) {
        this.suffix = suffix;
    }

    public String[] getProject() {
        return project;
    }

    public void setProject(String[] project) {
        this.project = project;
    }

    public String[] getDeliveryControllerId() {
        return deliveryControllerId;
    }

    public void setDeliveryControllerId(String[] deliveryControllerId) {
        this.deliveryControllerId = deliveryControllerId;
    }

    public String[] getSource() {
        return source;
    }

    public void setSource(String[] source) {
        this.source = source;
    }

    public String[] getSupplierParmaId() {
        return supplierParmaId;
    }

    public void setSupplierParmaId(String[] supplierParmaId) {
        this.supplierParmaId = supplierParmaId;
    }

    public String[] getSupplierParmaName() {
        return supplierParmaName;
    }

    public void setSupplierParmaName(String[] supplierParmaName) {
        this.supplierParmaName = supplierParmaName;
    }

    public boolean isDeliveriesVSOrderSTA() {
        return deliveriesVSOrderSTA;
    }

    public void setDeliveriesVSOrderSTA(boolean deliveriesVSOrderSTA) {
        this.deliveriesVSOrderSTA = deliveriesVSOrderSTA;
    }

    public boolean isDeliveriesVSAgreedSTA() {
        return deliveriesVSAgreedSTA;
    }

    public void setDeliveriesVSAgreedSTA(boolean deliveriesVSAgreedSTA) {
        this.deliveriesVSAgreedSTA = deliveriesVSAgreedSTA;
    }

    public boolean isNumberOfDeliveries() {
        return numberOfDeliveries;
    }

    public void setNumberOfDeliveries(boolean numberOfDeliveries) {
        this.numberOfDeliveries = numberOfDeliveries;
    }

    public boolean isNumberOfOrderlines() {
        return numberOfOrderlines;
    }

    public void setNumberOfOrderlines(boolean numberOfOrderlines) {
        this.numberOfOrderlines = numberOfOrderlines;
    }

    public ReportPartDeliveryPrecisionDTO clone() {
        try {
            return (ReportPartDeliveryPrecisionDTO) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }
}
