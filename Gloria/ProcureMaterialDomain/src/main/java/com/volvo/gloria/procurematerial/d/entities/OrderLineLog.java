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
 * Entity class for OrderLine Log.
 */
@Entity
@Table(name = "ORDER_LINE_LOG")
public class OrderLineLog extends EventLog {

    private static final long serialVersionUID = -7670878628701281277L;

    @ManyToOne
    @JoinColumn(name = "ORDER_LINE_OID")
    private OrderLine orderLine;

    public OrderLine getOrderLine() {
        return orderLine;
    }

    public void setOrderLine(OrderLine orderLine) {
        this.orderLine = orderLine;
    }
}
