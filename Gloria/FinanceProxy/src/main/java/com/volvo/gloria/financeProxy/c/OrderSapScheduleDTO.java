package com.volvo.gloria.financeProxy.c;

import java.io.Serializable;
import java.util.Date;

public class OrderSapScheduleDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8441920900267354960L;

    private String categoryOfDeliveryDate;
    private Date deliveryDate;

   
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

}
