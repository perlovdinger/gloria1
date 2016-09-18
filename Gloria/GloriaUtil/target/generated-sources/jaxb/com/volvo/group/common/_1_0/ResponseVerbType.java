//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:36 em CEST 
//


package com.volvo.group.common._1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResponseVerbType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResponseVerbType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OriginalApplicationArea" type="{http://www.volvo.com/group/common/1_0}ApplicationAreaType" minOccurs="0"/>
 *         &lt;element name="ResponseCriteria">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="Accepted"/>
 *               &lt;enumeration value="Rejected"/>
 *               &lt;enumeration value="Modified"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="StatusCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="StatusText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UserArea" type="{http://www.volvo.com/group/common/1_0}UserAreaType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponseVerbType", propOrder = {
    "originalApplicationArea",
    "responseCriteria",
    "statusCode",
    "statusText",
    "userArea"
})
public class ResponseVerbType {

    @XmlElement(name = "OriginalApplicationArea")
    protected ApplicationAreaType originalApplicationArea;
    @XmlElement(name = "ResponseCriteria", required = true)
    protected String responseCriteria;
    @XmlElement(name = "StatusCode")
    protected String statusCode;
    @XmlElement(name = "StatusText")
    protected String statusText;
    @XmlElement(name = "UserArea")
    protected UserAreaType userArea;

    /**
     * Gets the value of the originalApplicationArea property.
     * 
     * @return
     *     possible object is
     *     {@link ApplicationAreaType }
     *     
     */
    public ApplicationAreaType getOriginalApplicationArea() {
        return originalApplicationArea;
    }

    /**
     * Sets the value of the originalApplicationArea property.
     * 
     * @param value
     *     allowed object is
     *     {@link ApplicationAreaType }
     *     
     */
    public void setOriginalApplicationArea(ApplicationAreaType value) {
        this.originalApplicationArea = value;
    }

    /**
     * Gets the value of the responseCriteria property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponseCriteria() {
        return responseCriteria;
    }

    /**
     * Sets the value of the responseCriteria property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponseCriteria(String value) {
        this.responseCriteria = value;
    }

    /**
     * Gets the value of the statusCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the value of the statusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusCode(String value) {
        this.statusCode = value;
    }

    /**
     * Gets the value of the statusText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusText() {
        return statusText;
    }

    /**
     * Sets the value of the statusText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusText(String value) {
        this.statusText = value;
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