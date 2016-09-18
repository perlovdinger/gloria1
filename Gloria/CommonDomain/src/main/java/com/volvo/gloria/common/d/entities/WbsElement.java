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
package com.volvo.gloria.common.d.entities;

import java.io.Serializable;

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
 * Entity class for WbsElement.
 * 
 */
@Entity
@Table(name = "WBS_ELEMENT")
public class WbsElement implements GenericEntity<Long>, Serializable {
    private static final long serialVersionUID = -178177099666568797L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WBS_ELEMENT_OID")
    private long wbsElementOid;

    @Version
    private long version;
    private String wbs;
    private String description;
    private String projectId;

    @ManyToOne
    @JoinColumn(name = "COMPANY_CODE_OID")
    private CompanyCode companyCode;

    @Override
    public Long getId() {
        return wbsElementOid;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public long getWbsElementOid() {
        return wbsElementOid;
    }

    public void setWbsElementOid(long wbsElementOid) {
        this.wbsElementOid = wbsElementOid;
    }

    public String getWbs() {
        return wbs;
    }

    public void setWbs(String wbs) {
        this.wbs = wbs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public CompanyCode getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(CompanyCode companyCode) {
        this.companyCode = companyCode;
    }
}
