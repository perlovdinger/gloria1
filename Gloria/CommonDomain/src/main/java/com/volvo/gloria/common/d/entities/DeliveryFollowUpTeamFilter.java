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

import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.gloria.util.validators.GloriaStringSize;
/**
 * DeliveryFollowUpTeamFilter entity.
 */
@Entity
@Table(name = "DELIVERY_FOLLOW_UP_TEAM_FILTER")
public class DeliveryFollowUpTeamFilter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DELIVERY_FOLLOW_UP_TEAM_FILTER_OID")
    private long deliveryFollowUpTeamFilterOid;

    @Version
    private long version;

    @GloriaStringSize(max = GloriaParams.SUPPLIER_ID_LENGTH)
    private String supplierId;
    private String projectId;
    private String suffix;
    private String deliveryControllerUserId;

    @ManyToOne
    @JoinColumn(name = "DELIVERY_FOLLOW_UP_TEAM_OID")
    private DeliveryFollowUpTeam deliveryFollowUpTeam;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getDeliveryFollowUpTeamFilterOid() {
        return deliveryFollowUpTeamFilterOid;
    }

    public void setDeliveryFollowUpTeamFilterOid(long deliveryFollowUpTeamFilterOid) {
        this.deliveryFollowUpTeamFilterOid = deliveryFollowUpTeamFilterOid;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getDeliveryControllerUserId() {
        return deliveryControllerUserId;
    }

    public void setDeliveryControllerUserId(String deliveryControllerUserId) {
        this.deliveryControllerUserId = deliveryControllerUserId;
    }

    public DeliveryFollowUpTeam getDeliveryFollowUpTeam() {
        return deliveryFollowUpTeam;
    }

    public void setDeliveryFollowUpTeam(DeliveryFollowUpTeam deliveryFollowUpTeam) {
        this.deliveryFollowUpTeam = deliveryFollowUpTeam;
    }

    @Override
    public String toString() {
        return "DeliveryFollowUpTeamFilter [deliveryFollowUpTeamFilterOid=" + deliveryFollowUpTeamFilterOid + ", version=" + version + ", supplierId="
                + supplierId + ", projectId=" + projectId + ", suffix=" + suffix + ", deliveryControllerUserId=" + deliveryControllerUserId
                + ", deliveryFollowUpTeam id=" + deliveryFollowUpTeam.getDeliveryFollowUpTeamOid() + "]";
    }
}
