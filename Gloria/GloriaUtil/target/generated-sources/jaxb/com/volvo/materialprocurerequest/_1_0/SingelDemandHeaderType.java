//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:36 em CEST 
//


package com.volvo.materialprocurerequest._1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for SingelDemandHeaderType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SingelDemandHeaderType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReferenceId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BuiIdId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BuildStartDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="RequiredShipToArrive" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="ReferenceGroup" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OutboundDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="OutboundLocationId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SingelDemandHeaderType", propOrder = {
    "referenceId",
    "buiIdId",
    "buildStartDate",
    "requiredShipToArrive",
    "referenceGroup",
    "outboundDate",
    "outboundLocationId"
})
public class SingelDemandHeaderType {

    @XmlElement(name = "ReferenceId", required = true)
    protected String referenceId;
    @XmlElement(name = "BuiIdId")
    protected String buiIdId;
    @XmlElement(name = "BuildStartDate")
    protected XMLGregorianCalendar buildStartDate;
    @XmlElement(name = "RequiredShipToArrive")
    protected XMLGregorianCalendar requiredShipToArrive;
    @XmlElement(name = "ReferenceGroup")
    protected String referenceGroup;
    @XmlElement(name = "OutboundDate")
    protected XMLGregorianCalendar outboundDate;
    @XmlElement(name = "OutboundLocationId")
    protected String outboundLocationId;

    /**
     * Gets the value of the referenceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferenceId() {
        return referenceId;
    }

    /**
     * Sets the value of the referenceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferenceId(String value) {
        this.referenceId = value;
    }

    /**
     * Gets the value of the buiIdId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBuiIdId() {
        return buiIdId;
    }

    /**
     * Sets the value of the buiIdId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBuiIdId(String value) {
        this.buiIdId = value;
    }

    /**
     * Gets the value of the buildStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBuildStartDate() {
        return buildStartDate;
    }

    /**
     * Sets the value of the buildStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBuildStartDate(XMLGregorianCalendar value) {
        this.buildStartDate = value;
    }

    /**
     * Gets the value of the requiredShipToArrive property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRequiredShipToArrive() {
        return requiredShipToArrive;
    }

    /**
     * Sets the value of the requiredShipToArrive property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRequiredShipToArrive(XMLGregorianCalendar value) {
        this.requiredShipToArrive = value;
    }

    /**
     * Gets the value of the referenceGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferenceGroup() {
        return referenceGroup;
    }

    /**
     * Sets the value of the referenceGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferenceGroup(String value) {
        this.referenceGroup = value;
    }

    /**
     * Gets the value of the outboundDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOutboundDate() {
        return outboundDate;
    }

    /**
     * Sets the value of the outboundDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOutboundDate(XMLGregorianCalendar value) {
        this.outboundDate = value;
    }

    /**
     * Gets the value of the outboundLocationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutboundLocationId() {
        return outboundLocationId;
    }

    /**
     * Sets the value of the outboundLocationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutboundLocationId(String value) {
        this.outboundLocationId = value;
    }

}