package com.volvo.gloria.procurematerial.d.entities;

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

/**
 * Entity Class for ORDER_SAP_SCHEDULE.
 */
@Entity
@Table(name = "ORDER_SAP_SCHEDULE")
public class OrderSapSchedule implements Serializable {

    private static final long serialVersionUID = 3569425571944381428L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_SAP_SCHEDULE_OID")
    private long orderSapScheduleOID;
    
    @Version
    private long version;
    
    @ManyToOne
    @JoinColumn(name = "ORDER_SAP_LINE_OID")
    private OrderSapLine orderSapLine;
    
    private String categoryOfDeliveryDate;
    private Date deliveryDate;
    
    public long getOrderSapScheduleOID() {
        return orderSapScheduleOID;
    }
    public void setOrderSapScheduleOID(long orderSapScheduleOID) {
        this.orderSapScheduleOID = orderSapScheduleOID;
    }
    public OrderSapLine getOrderSapLine() {
        return orderSapLine;
    }
    public void setOrderSapLine(OrderSapLine orderSapLine) {
        this.orderSapLine = orderSapLine;
    }
    public String getCategoryOfDeliveryDate() {
        return categoryOfDeliveryDate;
    }
    public void setCategoryOfDeliveryDate(String categoryOfDeliveryDate) {
        this.categoryOfDeliveryDate = categoryOfDeliveryDate;
    }
    public Date getDeliveryDate() {
        return deliveryDate;
    }
    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    public long getVersion() {
        return version;
    }
}
