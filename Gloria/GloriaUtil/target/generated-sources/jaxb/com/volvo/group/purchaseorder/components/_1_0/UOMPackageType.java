//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:26 em CEST 
//


package com.volvo.group.purchaseorder.components._1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Associates a unit of measure with a packaging unit.
 * 
 * <p>Java class for UOMPackageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UOMPackageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}UOMCode" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}UnitPackaging" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UOMPackageType", propOrder = {
    "uomCode",
    "unitPackaging"
})
public class UOMPackageType {

    @XmlElement(name = "UOMCode")
    protected UOMCodeType uomCode;
    @XmlElement(name = "UnitPackaging")
    protected PackagingType unitPackaging;

    /**
     * Gets the value of the uomCode property.
     * 
     * @return
     *     possible object is
     *     {@link UOMCodeType }
     *     
     */
    public UOMCodeType getUOMCode() {
        return uomCode;
    }

    /**
     * Sets the value of the uomCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link UOMCodeType }
     *     
     */
    public void setUOMCode(UOMCodeType value) {
        this.uomCode = value;
    }

    /**
     * Gets the value of the unitPackaging property.
     * 
     * @return
     *     possible object is
     *     {@link PackagingType }
     *     
     */
    public PackagingType getUnitPackaging() {
        return unitPackaging;
    }

    /**
     * Sets the value of the unitPackaging property.
     * 
     * @param value
     *     allowed object is
     *     {@link PackagingType }
     *     
     */
    public void setUnitPackaging(PackagingType value) {
        this.unitPackaging = value;
    }

}
