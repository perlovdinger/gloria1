package com.volvo.gloria.common.c.dto;

import java.io.Serializable;

/**
 * DTO class for site.
 */
public class SiteDTO implements Serializable {

    private static final long serialVersionUID = 8512022038438270147L;

    private long id;
    private String siteId;
    private String siteCode;
    private String siteName;
    private String address;
    private String phone;
    private String countryCode;
    private String companyCode;
    private boolean jointVenture;
    private boolean shipToSite;
    private String shipToType;
    
    private boolean buildSite;
    private String buildSiteType;

    // ?? any use in UI
    private boolean qiSupport;
    private boolean whSite;
    private boolean whVirtual;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public boolean isWhSite() {
        return whSite;
    }

    public void setWhSite(boolean whSite) {
        this.whSite = whSite;
    }

    public boolean isWhVirtual() {
        return whVirtual;
    }

    public void setWhVirtual(boolean whVirtual) {
        this.whVirtual = whVirtual;
    }

    public boolean isBuildSite() {
        return buildSite;
    }

    public void setBuildSite(boolean buildSite) {
        this.buildSite = buildSite;
    }

    public String getBuildSiteType() {
        return buildSiteType;
    }

    public void setBuildSiteType(String buildSiteType) {
        this.buildSiteType = buildSiteType;
    }

   
    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public boolean isJointVenture() {
        return jointVenture;
    }

    public void setJointVenture(boolean jointVenture) {
        this.jointVenture = jointVenture;
    }

    public boolean isShipToSite() {
        return shipToSite;
    }

    public void setShipToSite(boolean shipToSite) {
        this.shipToSite = shipToSite;
    }

    public String getShipToType() {
        return shipToType;
    }

    public void setShipToType(String shipToType) {
        this.shipToType = shipToType;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isQiSupport() {
        return qiSupport;
    }

    public void setQiSupport(boolean qiSupport) {
        this.qiSupport = qiSupport;
    }

}
