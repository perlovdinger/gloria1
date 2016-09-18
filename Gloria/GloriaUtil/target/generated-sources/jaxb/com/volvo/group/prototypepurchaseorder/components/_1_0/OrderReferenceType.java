//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:32 em CEST 
//


package com.volvo.group.prototypepurchaseorder.components._1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OrderReferenceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrderReferenceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}DocumentReferenceBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}ReleaseNumber" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}LineNumber" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}ScheduleLineNumber" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}SubLineNumber" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}UserArea" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderReferenceType", propOrder = {
    "releaseNumber",
    "lineNumber",
    "scheduleLineNumber",
    "subLineNumber",
    "userArea"
})
public class OrderReferenceType
    extends DocumentReferenceBaseType
{

    @XmlElement(name = "ReleaseNumber")
    protected IdentifierType releaseNumber;
    @XmlElement(name = "LineNumber")
    protected IdentifierType lineNumber;
    @XmlElement(name = "ScheduleLineNumber")
    protected IdentifierType scheduleLineNumber;
    @XmlElement(name = "SubLineNumber")
    protected IdentifierType subLineNumber;
    @XmlElement(name = "UserArea")
    protected UserAreaType userArea;

    /**
     * Gets the value of the releaseNumber property.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getReleaseNumber() {
        return releaseNumber;
    }

    /**
     * Sets the value of the releaseNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setReleaseNumber(IdentifierType value) {
        this.releaseNumber = value;
    }

    /**
     * Gets the value of the lineNumber property.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getLineNumber() {
        return lineNumber;
    }

    /**
     * Sets the value of the lineNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setLineNumber(IdentifierType value) {
        this.lineNumber = value;
    }

    /**
     * Gets the value of the scheduleLineNumber property.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getScheduleLineNumber() {
        return scheduleLineNumber;
    }

    /**
     * Sets the value of the scheduleLineNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setScheduleLineNumber(IdentifierType value) {
        this.scheduleLineNumber = value;
    }

    /**
     * Gets the value of the subLineNumber property.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getSubLineNumber() {
        return subLineNumber;
    }

    /**
     * Sets the value of the subLineNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setSubLineNumber(IdentifierType value) {
        this.subLineNumber = value;
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
