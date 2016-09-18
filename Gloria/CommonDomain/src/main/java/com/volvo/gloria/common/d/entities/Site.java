package com.volvo.gloria.common.d.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entity class for Site.
 */
@Entity
@Table(name = "SITE")
public class Site implements GenericEntity<Long> {
    private static final long serialVersionUID = -4202407538153286631L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SITE_OID")
    private long siteOid;

    @Version
    private long version;

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

    public long getSiteOid() {
        return siteOid;
    }

    public void setSiteOid(long siteOid) {
        this.siteOid = siteOid;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
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

    @Override
    public Long getId() {
        return this.getSiteOid();
    }
}
