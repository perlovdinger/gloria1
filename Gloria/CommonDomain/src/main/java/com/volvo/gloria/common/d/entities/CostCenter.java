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
package com.volvo.gloria.common.d.entities;

import java.io.Serializable;
import java.util.Date;

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
 * Entity class for Cost Center.
 * 
 */
@Entity
@Table(name = "COST_CENTER")
public class CostCenter implements GenericEntity<Long>, Serializable {
    private static final long serialVersionUID = -7264788336680052513L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COST_CENTER_OID")
    private long costCenterOid;

    @Version
    private long version;
    private String costCenter;
    private String descriptionShort;
    private String descriptionLong;
    private String personResponisbleId;
    private String personalResponsibleName;
    private Date effectiveStartDate;
    private Date effectiveEndDate;
    
    @ManyToOne
    @JoinColumn(name = "COMPANY_CODE_OID")
    private CompanyCode companyCode;
    

    public long getCostCenterOid() {
        return costCenterOid;
    }

    public void setCostCenterOid(long costCenterOid) {
        this.costCenterOid = costCenterOid;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getDescriptionShort() {
        return descriptionShort;
    }

    public void setDescriptionShort(String descriptionShort) {
        this.descriptionShort = descriptionShort;
    }

    public String getDescriptionLong() {
        return descriptionLong;
    }

    public void setDescriptionLong(String descriptionLong) {
        this.descriptionLong = descriptionLong;
    }

    public String getPersonResponisbleId() {
        return personResponisbleId;
    }

    public void setPersonResponisbleId(String personResponisbleId) {
        this.personResponisbleId = personResponisbleId;
    }

    public String getPersonalResponsibleName() {
        return personalResponsibleName;
    }

    public void setPersonalResponsibleName(String personalResponsibleName) {
        this.personalResponsibleName = personalResponsibleName;
    }

    public Date getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(Date effictiveStartDate) {
        this.effectiveStartDate = effictiveStartDate;
    }

    public Date getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(Date effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    @Override
    public Long getId() {
        return costCenterOid;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public CompanyCode getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(CompanyCode companyCode) {
        this.companyCode = companyCode;
    }
}
