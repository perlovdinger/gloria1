//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:32 em CEST 
//


package com.volvo.group.prototypepurchaseorder._1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.volvo.group.prototypepurchaseorder.components._1_0.PurchaseOrderHeaderType;


/**
 * <p>Java class for StandAloneOrderHeaderType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StandAloneOrderHeaderType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}PurchaseOrderHeaderType">
 *       &lt;sequence>
 *         &lt;element name="PaymentPlan" type="{http://www.volvo.com/3P/Purchasing/2008/10}PaymentPlanType" minOccurs="0"/>
 *         &lt;element name="OrderMode" type="{http://www.volvo.com/3P/Purchasing/2008/10}OrderModeType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StandAloneOrderHeaderType", propOrder = {
    "paymentPlan",
    "orderMode"
})
public class StandAloneOrderHeaderType
    extends PurchaseOrderHeaderType
{

    @XmlElement(name = "PaymentPlan")
    protected PaymentPlanType paymentPlan;
    @XmlElement(name = "OrderMode")
    protected OrderModeType orderMode;

    /**
     * Gets the value of the paymentPlan property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentPlanType }
     *     
     */
    public PaymentPlanType getPaymentPlan() {
        return paymentPlan;
    }

    /**
     * Sets the value of the paymentPlan property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentPlanType }
     *     
     */
    public void setPaymentPlan(PaymentPlanType value) {
        this.paymentPlan = value;
    }

    /**
     * Gets the value of the orderMode property.
     * 
     * @return
     *     possible object is
     *     {@link OrderModeType }
     *     
     */
    public OrderModeType getOrderMode() {
        return orderMode;
    }

    /**
     * Sets the value of the orderMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderModeType }
     *     
     */
    public void setOrderMode(OrderModeType value) {
        this.orderMode = value;
    }

}
