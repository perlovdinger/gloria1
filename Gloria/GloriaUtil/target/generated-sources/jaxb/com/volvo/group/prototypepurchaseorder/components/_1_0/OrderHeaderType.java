//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:32 em CEST 
//


package com.volvo.group.prototypepurchaseorder.components._1_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OrderHeaderType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrderHeaderType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}ProcurementHeaderType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}SpecialPriceAuthorizationCode" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}EarliestShipDateTime" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}RequestedShipDateTime" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}PromisedShipDateTime" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}PromisedDeliveryDateTime" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}PaymentMethodCode" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}Payment" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}ShippingInstructions" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderHeaderType", propOrder = {
    "specialPriceAuthorizationCode",
    "earliestShipDateTime",
    "requestedShipDateTime",
    "promisedShipDateTime",
    "promisedDeliveryDateTime",
    "paymentMethodCode",
    "payment",
    "shippingInstructions"
})
public class OrderHeaderType
    extends ProcurementHeaderType
{

    @XmlElement(name = "SpecialPriceAuthorizationCode")
    protected CodeType specialPriceAuthorizationCode;
    @XmlElement(name = "EarliestShipDateTime")
    protected String earliestShipDateTime;
    @XmlElement(name = "RequestedShipDateTime")
    protected String requestedShipDateTime;
    @XmlElement(name = "PromisedShipDateTime")
    protected String promisedShipDateTime;
    @XmlElement(name = "PromisedDeliveryDateTime")
    protected String promisedDeliveryDateTime;
    @XmlElement(name = "PaymentMethodCode")
    protected PaymentMethodCodeType paymentMethodCode;
    @XmlElement(name = "Payment")
    protected List<PaymentType> payment;
    @XmlElement(name = "ShippingInstructions")
    protected TextType shippingInstructions;

    /**
     * Gets the value of the specialPriceAuthorizationCode property.
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getSpecialPriceAuthorizationCode() {
        return specialPriceAuthorizationCode;
    }

    /**
     * Sets the value of the specialPriceAuthorizationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setSpecialPriceAuthorizationCode(CodeType value) {
        this.specialPriceAuthorizationCode = value;
    }

    /**
     * Gets the value of the earliestShipDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEarliestShipDateTime() {
        return earliestShipDateTime;
    }

    /**
     * Sets the value of the earliestShipDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEarliestShipDateTime(String value) {
        this.earliestShipDateTime = value;
    }

    /**
     * Gets the value of the requestedShipDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestedShipDateTime() {
        return requestedShipDateTime;
    }

    /**
     * Sets the value of the requestedShipDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestedShipDateTime(String value) {
        this.requestedShipDateTime = value;
    }

    /**
     * Gets the value of the promisedShipDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPromisedShipDateTime() {
        return promisedShipDateTime;
    }

    /**
     * Sets the value of the promisedShipDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPromisedShipDateTime(String value) {
        this.promisedShipDateTime = value;
    }

    /**
     * Gets the value of the promisedDeliveryDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPromisedDeliveryDateTime() {
        return promisedDeliveryDateTime;
    }

    /**
     * Sets the value of the promisedDeliveryDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPromisedDeliveryDateTime(String value) {
        this.promisedDeliveryDateTime = value;
    }

    /**
     * Gets the value of the paymentMethodCode property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentMethodCodeType }
     *     
     */
    public PaymentMethodCodeType getPaymentMethodCode() {
        return paymentMethodCode;
    }

    /**
     * Sets the value of the paymentMethodCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentMethodCodeType }
     *     
     */
    public void setPaymentMethodCode(PaymentMethodCodeType value) {
        this.paymentMethodCode = value;
    }

    /**
     * Gets the value of the payment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the payment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPayment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PaymentType }
     * 
     * 
     */
    public List<PaymentType> getPayment() {
        if (payment == null) {
            payment = new ArrayList<PaymentType>();
        }
        return this.payment;
    }

    /**
     * Gets the value of the shippingInstructions property.
     * 
     * @return
     *     possible object is
     *     {@link TextType }
     *     
     */
    public TextType getShippingInstructions() {
        return shippingInstructions;
    }

    /**
     * Sets the value of the shippingInstructions property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextType }
     *     
     */
    public void setShippingInstructions(TextType value) {
        this.shippingInstructions = value;
    }

}
