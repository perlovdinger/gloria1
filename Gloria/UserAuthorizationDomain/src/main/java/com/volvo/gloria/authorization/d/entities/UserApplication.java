/*
 * Copyright 2009 Volvo Information Technology AB 
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

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Entity class representing Organisation Application.
 */
@Entity
@Table(name = "USER_APPLICATION")
public class UserApplication implements Serializable {

    private static final long serialVersionUID = -7224124411525063567L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_APPLICATION_OID")
    private long userApplicationOid;

    private String applicationID;
    private String applicationDescription;

    @ManyToOne
    @JoinColumn(name = "GLORIA_USER_OID")
    private GloriaUser gloriaUser;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "userApplication")
    private List<ApplicationSetting> applicationSetting;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "userApplication")
    private List<UserCategory> userCategory;

    @Version
    private long version;

    public long getUserApplicationOid() {
        return userApplicationOid;
    }

    public void setUserApplicationOid(long userApplicationOid) {
        this.userApplicationOid = userApplicationOid;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public String getApplicationDescription() {
        return applicationDescription;
    }

    public void setApplicationDescription(String applicationDescription) {
        this.applicationDescription = applicationDescription;
    }

    public List<ApplicationSetting> getApplicationSetting() {
        return applicationSetting;
    }

    public void setApplicationSetting(List<ApplicationSetting> applicationSetting) {
        this.applicationSetting = applicationSetting;
    }

    public List<UserCategory> getUserCategory() {
        return userCategory;
    }

    public void setUserCategory(List<UserCategory> userCategory) {
        this.userCategory = userCategory;
    }

    public GloriaUser getGloriaUser() {
        return gloriaUser;
    }

    public void setGloriaUser(GloriaUser gloriaUser) {
        this.gloriaUser = gloriaUser;
    }

    public long getVersion() {
        return version;
    }
}
