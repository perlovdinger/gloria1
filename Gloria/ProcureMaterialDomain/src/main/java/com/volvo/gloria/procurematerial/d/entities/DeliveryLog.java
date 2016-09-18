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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity Class for Delivery Log.
 */
@Entity
@Table(name = "DELIVERY_LOG")
public class DeliveryLog extends EventLog {

    private static final long serialVersionUID = -1887972575837951720L;

    @ManyToOne
    @JoinColumn(name = "DELIVERY_SCHEDULE_OID")
    private DeliverySchedule deliverySchedule;

    public DeliverySchedule getDeliverySchedule() {
        return deliverySchedule;
    }

    public void setDeliverySchedule(DeliverySchedule deliverySchedule) {
        this.deliverySchedule = deliverySchedule;
    }
}
