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
package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Min;

import com.volvo.gloria.procurematerial.c.DeliveryStatusFlag;
import com.volvo.gloria.util.validators.GloriaLongValue;

/**
 * Entity Class for Delivery Schedule.
 */
@Entity
@Table(name = "DELIVERY_SCHEDULE")
public class DeliverySchedule implements Serializable {

    private static final long serialVersionUID = -7303987803433779349L;
    private static final int MAX_QUANTITY = 99999;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DELIVERY_SCHEDULE_OID")
    private long deliveryScheduleOID;

    @Version
    private long version;

    @ManyToOne
    @JoinColumn(name = "ORDER_LINE_OID")
    private OrderLine orderLine;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "deliverySchedule")
    private List<DeliveryLog> deliveryLog;

    @Min(0)
    @GloriaLongValue(max = MAX_QUANTITY)
    private Long expectedQuantity;
    private Date expectedDate;
    private Date plannedDispatchDate;
    @Enumerated
    private DeliveryStatusFlag statusFlag;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "deliverySchedule")
    private List<AttachedDoc> attachedDocs = new ArrayList<AttachedDoc>();

    public long getDeliveryScheduleOID() {
        return deliveryScheduleOID;
    }

    public void setDeliveryScheduleOID(long deliveryScheduleOID) {
        this.deliveryScheduleOID = deliveryScheduleOID;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public OrderLine getOrderLine() {
        return orderLine;
    }

    public void setOrderLine(OrderLine orderLine) {
        this.orderLine = orderLine;
    }

    public List<DeliveryLog> getDeliveryLog() {
        return deliveryLog;
    }

    public void setDeliveryLog(List<DeliveryLog> deliveryLog) {
        this.deliveryLog = deliveryLog;
    }

    public Long getExpectedQuantity() {
        return expectedQuantity;
    }

    public void setExpectedQuantity(Long expectedQuantity) {
        this.expectedQuantity = expectedQuantity;
    }

    public Date getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(Date expectedDate) {
        this.expectedDate = expectedDate;
    }

    public DeliveryStatusFlag getStatusFlag() {
        return statusFlag;
    }

    public void setStatusFlag(DeliveryStatusFlag statusFlag) {
        this.statusFlag = statusFlag;
    }

    public List<AttachedDoc> getAttachedDocs() {
        return attachedDocs;
    }

    public void setAttachedDocs(List<AttachedDoc> attachedDocs) {
        this.attachedDocs = attachedDocs;
    }

    public Date getPlannedDispatchDate() {
        return plannedDispatchDate;
    }

    public void setPlannedDispatchDate(Date plannedDispatchDate) {
        this.plannedDispatchDate = plannedDispatchDate;
    }
}
