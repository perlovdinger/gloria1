//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:38 em CEST 
//


package com.volvo.group.init.deliveryNote._1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *       &lt;attribute name="carrier" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="deliveryNoteDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="deliveryNoteNo" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="orderNo" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="receiveType" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="supplierId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="supplierName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="transportationNo" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "DeliveryNote")
public class DeliveryNote {

    @XmlAttribute(required = true)
    protected String carrier;
    @XmlAttribute(required = true)
    protected XMLGregorianCalendar deliveryNoteDate;
    @XmlAttribute(required = true)
    protected String deliveryNoteNo;
    @XmlAttribute(required = true)
    protected String orderNo;
    @XmlAttribute(required = true)
    protected String receiveType;
    @XmlAttribute(required = true)
    protected String supplierId;
    @XmlAttribute(required = true)
    protected String supplierName;
    @XmlAttribute(required = true)
    protected String transportationNo;

    /**
     * Gets the value of the carrier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCarrier() {
        return carrier;
    }

    /**
     * Sets the value of the carrier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCarrier(String value) {
        this.carrier = value;
    }

    /**
     * Gets the value of the deliveryNoteDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDeliveryNoteDate() {
        return deliveryNoteDate;
    }

    /**
     * Sets the value of the deliveryNoteDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDeliveryNoteDate(XMLGregorianCalendar value) {
        this.deliveryNoteDate = value;
    }

    /**
     * Gets the value of the deliveryNoteNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeliveryNoteNo() {
        return deliveryNoteNo;
    }

    /**
     * Sets the value of the deliveryNoteNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeliveryNoteNo(String value) {
        this.deliveryNoteNo = value;
    }

    /**
     * Gets the value of the orderNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * Sets the value of the orderNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderNo(String value) {
        this.orderNo = value;
    }

    /**
     * Gets the value of the receiveType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceiveType() {
        return receiveType;
    }

    /**
     * Sets the value of the receiveType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceiveType(String value) {
        this.receiveType = value;
    }

    /**
     * Gets the value of the supplierId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSupplierId() {
        return supplierId;
    }

    /**
     * Sets the value of the supplierId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSupplierId(String value) {
        this.supplierId = value;
    }

    /**
     * Gets the value of the supplierName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSupplierName() {
        return supplierName;
    }

    /**
     * Sets the value of the supplierName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSupplierName(String value) {
        this.supplierName = value;
    }

    /**
     * Gets the value of the transportationNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransportationNo() {
        return transportationNo;
    }

    /**
     * Sets the value of the transportationNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransportationNo(String value) {
        this.transportationNo = value;
    }

}
