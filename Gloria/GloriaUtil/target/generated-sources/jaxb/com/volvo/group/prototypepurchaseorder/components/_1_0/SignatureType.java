//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:32 em CEST 
//


package com.volvo.group.prototypepurchaseorder.components._1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * Allows any digital Signatures to be added to the Instance of the BOD Instance in order to sign for any portion of the BOD content.
 * 
 * <p>Java class for SignatureType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SignatureType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any/>
 *       &lt;/sequence>
 *       &lt;attribute name="qualifyingAgencyId" type="{http://www.openapplications.org/oagis/9}NormalizedStringType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignatureType", propOrder = {
    "any"
})
public class SignatureType {

    @XmlAnyElement(lax = true)
    protected Object any;
    @XmlAttribute
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String qualifyingAgencyId;

    /**
     * Gets the value of the any property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getAny() {
        return any;
    }

    /**
     * Sets the value of the any property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setAny(Object value) {
        this.any = value;
    }

    /**
     * Gets the value of the qualifyingAgencyId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQualifyingAgencyId() {
        return qualifyingAgencyId;
    }

    /**
     * Sets the value of the qualifyingAgencyId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQualifyingAgencyId(String value) {
        this.qualifyingAgencyId = value;
    }

}
