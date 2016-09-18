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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entity class for Application Settings.
 */
@Entity
@Table(name = "APPLICATION_SETTING")
public class ApplicationSetting implements GenericEntity<Long> {
    private static final long serialVersionUID = 6313096714815964385L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APPLICATION_SETTING_OID")
    private long applicationSettingOid;

    private String applicationSettingID;
    private String applicationSettingValue;

    @ManyToOne
    @JoinColumn(name = "userApplication")
    private UserApplication userApplication;

    @Version
    private long version;

    public long getApplicationSettingOid() {
        return applicationSettingOid;
    }

    public void setApplicationSettingOid(long applicationSettingOid) {
        this.applicationSettingOid = applicationSettingOid;
    }

    public String getApplicationSettingID() {
        return applicationSettingID;
    }

    public void setApplicationSettingID(String applicationSettingID) {
        this.applicationSettingID = applicationSettingID;
    }

    public String getApplicationSettingValue() {
        return applicationSettingValue;
    }

    public void setApplicationSettingValue(String applicationSettingValue) {
        this.applicationSettingValue = applicationSettingValue;
    }

    public UserApplication getUserApplication() {
        return userApplication;
    }

    public void setUserApplication(UserApplication userApplication) {
        this.userApplication = userApplication;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public Long getId() {
        return applicationSettingOid;
    }
}
