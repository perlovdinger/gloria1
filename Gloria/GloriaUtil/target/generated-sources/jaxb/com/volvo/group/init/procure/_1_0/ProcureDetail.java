//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:39 em CEST 
//


package com.volvo.group.init.procure._1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="assignedMaterialController" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="shipToId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="finalWarehouseId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="requiredStaDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="unitPrice" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="currency" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="additionalQuantity" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "assignedMaterialController",
    "shipToId",
    "finalWarehouseId",
    "requiredStaDate",
    "unitPrice",
    "currency",
    "additionalQuantity"
})
@XmlRootElement(name = "ProcureDetail")
public class ProcureDetail {

    @XmlElement(required = true)
    protected String assignedMaterialController;
    @XmlElement(required = true)
    protected String shipToId;
    @XmlElement(required = true)
    protected String finalWarehouseId;
    @XmlElement(required = true)
    protected XMLGregorianCalendar requiredStaDate;
    protected double unitPrice;
    @XmlElement(required = true)
    protected String currency;
    protected long additionalQuantity;

    /**
     * Gets the value of the assignedMaterialController property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssignedMaterialController() {
        return assignedMaterialController;
    }

    /**
     * Sets the value of the assignedMaterialController property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssignedMaterialController(String value) {
        this.assignedMaterialController = value;
    }

    /**
     * Gets the value of the shipToId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShipToId() {
        return shipToId;
    }

    /**
     * Sets the value of the shipToId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShipToId(String value) {
        this.shipToId = value;
    }

    /**
     * Gets the value of the finalWarehouseId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFinalWarehouseId() {
        return finalWarehouseId;
    }

    /**
     * Sets the value of the finalWarehouseId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFinalWarehouseId(String value) {
        this.finalWarehouseId = value;
    }

    /**
     * Gets the value of the requiredStaDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRequiredStaDate() {
        return requiredStaDate;
    }

    /**
     * Sets the value of the requiredStaDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRequiredStaDate(XMLGregorianCalendar value) {
        this.requiredStaDate = value;
    }

    /**
     * Gets the value of the unitPrice property.
     * 
     */
    public double getUnitPrice() {
        return unitPrice;
    }

    /**
     * Sets the value of the unitPrice property.
     * 
     */
    public void setUnitPrice(double value) {
        this.unitPrice = value;
    }

    /**
     * Gets the value of the currency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the value of the currency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrency(String value) {
        this.currency = value;
    }

    /**
     * Gets the value of the additionalQuantity property.
     * 
     */
    public long getAdditionalQuantity() {
        return additionalQuantity;
    }

    /**
     * Sets the value of the additionalQuantity property.
     * 
     */
    public void setAdditionalQuantity(long value) {
        this.additionalQuantity = value;
    }

}