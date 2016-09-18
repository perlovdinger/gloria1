//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:26 em CEST 
//


package com.volvo.group.purchaseorder.components._1_0;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Contains the relevant information about the salesperson and the associated commission information. 
 * 
 * <p>Java class for OrderCommissionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrderCommissionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}SalesOrganizationIDs" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}SalesPerson" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;sequence>
 *             &lt;element ref="{http://www.openapplications.org/oagis/9}Amount" minOccurs="0"/>
 *             &lt;element ref="{http://www.openapplications.org/oagis/9}Quantity" minOccurs="0"/>
 *           &lt;/sequence>
 *           &lt;sequence>
 *             &lt;element ref="{http://www.openapplications.org/oagis/9}OrderAmount" minOccurs="0"/>
 *             &lt;element ref="{http://www.openapplications.org/oagis/9}OrderQuantity" minOccurs="0"/>
 *             &lt;element ref="{http://www.openapplications.org/oagis/9}Percentage" minOccurs="0"/>
 *           &lt;/sequence>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}UserArea" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderCommissionType", propOrder = {
    "salesOrganizationIDs",
    "salesPerson",
    "amount",
    "quantity",
    "orderAmount",
    "orderQuantity",
    "percentage",
    "userArea"
})
public class OrderCommissionType {

    @XmlElement(name = "SalesOrganizationIDs")
    protected NamedIDsType salesOrganizationIDs;
    @XmlElement(name = "SalesPerson")
    protected ContactType salesPerson;
    @XmlElement(name = "Amount")
    protected AmountType amount;
    @XmlElement(name = "Quantity")
    protected QuantityType quantity;
    @XmlElement(name = "OrderAmount")
    protected AmountType orderAmount;
    @XmlElement(name = "OrderQuantity")
    protected QuantityType orderQuantity;
    @XmlElement(name = "Percentage")
    protected BigDecimal percentage;
    @XmlElement(name = "UserArea")
    protected UserAreaType userArea;

    /**
     * Gets the value of the salesOrganizationIDs property.
     * 
     * @return
     *     possible object is
     *     {@link NamedIDsType }
     *     
     */
    public NamedIDsType getSalesOrganizationIDs() {
        return salesOrganizationIDs;
    }

    /**
     * Sets the value of the salesOrganizationIDs property.
     * 
     * @param value
     *     allowed object is
     *     {@link NamedIDsType }
     *     
     */
    public void setSalesOrganizationIDs(NamedIDsType value) {
        this.salesOrganizationIDs = value;
    }

    /**
     * Gets the value of the salesPerson property.
     * 
     * @return
     *     possible object is
     *     {@link ContactType }
     *     
     */
    public ContactType getSalesPerson() {
        return salesPerson;
    }

    /**
     * Sets the value of the salesPerson property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactType }
     *     
     */
    public void setSalesPerson(ContactType value) {
        this.salesPerson = value;
    }

    /**
     * Gets the value of the amount property.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setAmount(AmountType value) {
        this.amount = value;
    }

    /**
     * Gets the value of the quantity property.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setQuantity(QuantityType value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the orderAmount property.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getOrderAmount() {
        return orderAmount;
    }

    /**
     * Sets the value of the orderAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setOrderAmount(AmountType value) {
        this.orderAmount = value;
    }

    /**
     * Gets the value of the orderQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getOrderQuantity() {
        return orderQuantity;
    }

    /**
     * Sets the value of the orderQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setOrderQuantity(QuantityType value) {
        this.orderQuantity = value;
    }

    /**
     * Gets the value of the percentage property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPercentage() {
        return percentage;
    }

    /**
     * Sets the value of the percentage property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPercentage(BigDecimal value) {
        this.percentage = value;
    }

    /**
     * Gets the value of the userArea property.
     * 
     * @return
     *     possible object is
     *     {@link UserAreaType }
     *     
     */
    public UserAreaType getUserArea() {
        return userArea;
    }

    /**
     * Sets the value of the userArea property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserAreaType }
     *     
     */
    public void setUserArea(UserAreaType value) {
        this.userArea = value;
    }

}
