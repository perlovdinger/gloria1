package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;

public class PurchaseOrganisationDTO implements Serializable {

    private static final long serialVersionUID = -6067427281541721817L;
    
    private Long id;
    private Long version;
    private String organisationCode;
    private String organisationName;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getVersion() {
        return version;
    }
    public void setVersion(Long version) {
        this.version = version;
    }
    public String getOrganisationCode() {
        return organisationCode;
    }
    public void setOrganisationCode(String organisationCode) {
        this.organisationCode = organisationCode;
    }
    public String getOrganisationName() {
        return organisationName;
    }
    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }
}
