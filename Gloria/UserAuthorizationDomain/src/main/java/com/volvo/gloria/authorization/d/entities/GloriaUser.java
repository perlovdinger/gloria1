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
package com.volvo.gloria.authorization.d.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entity class for User Organistation data.
 */
@Entity
@Table(name = "GLORIA_USER")
public class GloriaUser implements GenericEntity<Long> {
    private static final long serialVersionUID = -6960211772857771502L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GLORIA_USER_OID")
    private long gloriaUserOid;

    private String userID;
    private String userFirstName;
    private String userLastName;
    private String organisationID;
    private String organisationName;
    private String langCode;
    private String countryCode;
    private boolean inactive = false;
    private long internalProcureTeamOID;
    private String department;
    
    //Only used for Go Live - can be removed after that
    private boolean disabledLogin = false;
    
    @ManyToMany(mappedBy = "users")
    private Set<Team> teams = new HashSet<Team>();

    @Version
    private long version;
    
    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "gloriaUser", orphanRemoval = true)
    private List<Address> address;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "gloriaUser", orphanRemoval = true)
    private List<UserApplication> userApplication;
    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "gloriaUser", orphanRemoval = true)
    private List<UserCompanyCode> userCompanyCodes;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "gloriaUser", orphanRemoval = true)
    private List<ReportFilter> reportFilters;

    public List<UserCompanyCode> getUserCompanyCodes() {
        return userCompanyCodes;
    }

    public void setUserCompanyCodes(List<UserCompanyCode> userCompanyCodes) {
        this.userCompanyCodes = userCompanyCodes;
    }

    public long getGloriaUserOid() {
        return gloriaUserOid;
    }

    public void setGloriaUserOid(long gloriaUserOid) {
        this.gloriaUserOid = gloriaUserOid;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getOrganisationID() {
        return organisationID;
    }

    public void setOrganisationID(String organisationID) {
        this.organisationID = organisationID;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public List<UserApplication> getUserApplication() {
        return userApplication;
    }

    public void setUserApplication(List<UserApplication> userApplication) {
        this.userApplication = userApplication;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public long getInternalProcureTeamOID() {
        return internalProcureTeamOID;
    }

    public void setInternalProcureTeamOID(long internalProcureTeamOID) {
        this.internalProcureTeamOID = internalProcureTeamOID;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }

    public Set<Team> getTeams() {
        return teams;
    }

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public Long getId() {
        return gloriaUserOid;
    }
    
    public String getDepartment() {
        return department;
    }
    

    public boolean hasDisabledLogin() {
        return disabledLogin;
    }

    public void setDisabledLogin(boolean disabledLogin) {
        this.disabledLogin = disabledLogin;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
    
    public List<ReportFilter> getReportFilters() {
        return reportFilters;
    }
    
    public void setReportFilters(List<ReportFilter> reportFilters) {
        this.reportFilters = reportFilters;
    }

    @Override
    public String toString() {
        return "GloriaUser [userID=" + userID + ", userFirstName=" + userFirstName + ", userLastName=" + userLastName + ", department=" + department
                + ", organisationID=" + organisationID + ", organisationName=" + organisationName + ", langCode=" + langCode + ", countryCode=" + countryCode
                + ", inactive=" + inactive + ",Team=" + teams.toString() + "]";
    }    
}
