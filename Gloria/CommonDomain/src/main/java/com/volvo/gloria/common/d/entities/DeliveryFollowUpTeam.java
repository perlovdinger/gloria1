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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.common.c.FollowUpType;
import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entity class for DeliveryFollowUpTeam.
 * 
 */
@Entity
@Table(name = "DELIVERY_FOLLOW_UP_TEAM")
public class DeliveryFollowUpTeam implements GenericEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DELIVERY_FOLLOW_UP_TEAM_OID")
    private long deliveryFollowUpTeamOid;

    @Version
    private long version;

    private String code;
    private String name;
    private String defaultDCUserId;

    @Enumerated(EnumType.STRING)
    private FollowUpType followUpType;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "deliveryFollowUpTeam")
    private List<DeliveryFollowUpTeamFilter> deliveryFollowUpTeamFilters = new ArrayList<DeliveryFollowUpTeamFilter>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "deliveryFollowUpTeam")
    private List<SupplierCounterPart> supplierCounterParts = new ArrayList<SupplierCounterPart>();

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getDeliveryFollowUpTeamOid() {
        return deliveryFollowUpTeamOid;
    }

    public void setDeliveryFollowUpTeamOid(long deliveryFollowUpTeamOid) {
        this.deliveryFollowUpTeamOid = deliveryFollowUpTeamOid;
    }

    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultDCUserId() {
        return defaultDCUserId;
    }

    public void setDefaultDCUserId(String defaultDCUserId) {
        this.defaultDCUserId = defaultDCUserId;
    }

    public List<DeliveryFollowUpTeamFilter> getDeliveryFollowUpTeamFilters() {
        return deliveryFollowUpTeamFilters;
    }

    public void setDeliveryFollowUpTeamFilters(List<DeliveryFollowUpTeamFilter> deliveryFollowUpTeamFilters) {
        this.deliveryFollowUpTeamFilters = deliveryFollowUpTeamFilters;
    }

    public FollowUpType getFollowUpType() {
        return followUpType;
    }

    public void setFollowUpType(FollowUpType followUpType) {
        this.followUpType = followUpType;
    }

    public List<SupplierCounterPart> getSupplierCounterParts() {
        return supplierCounterParts;
    }

    public void setSupplierCounterParts(List<SupplierCounterPart> supplierCounterParts) {
        this.supplierCounterParts = supplierCounterParts;
    }
    
    @Override
    public Long getId() {
        return deliveryFollowUpTeamOid;
    }
}
