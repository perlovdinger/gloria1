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


/**
 * <p>Java class for LineType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LineType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LineRemoveType" type="{http://www.volvo.com/MaterialProcureRequest/1_0}LineRemoveType" minOccurs="0"/>
 *         &lt;element name="LineAddType" type="{http://www.volvo.com/MaterialProcureRequest/1_0}LineAddType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LineType", propOrder = {
    "lineRemoveType",
    "lineAddType"
})
public class LineType {

    @XmlElement(name = "LineRemoveType")
    protected LineRemoveType lineRemoveType;
    @XmlElement(name = "LineAddType")
    protected LineAddType lineAddType;

    /**
     * Gets the value of the lineRemoveType property.
     * 
     * @return
     *     possible object is
     *     {@link LineRemoveType }
     *     
     */
    public LineRemoveType getLineRemoveType() {
        return lineRemoveType;
    }

    /**
     * Sets the value of the lineRemoveType property.
     * 
     * @param value
     *     allowed object is
     *     {@link LineRemoveType }
     *     
     */
    public void setLineRemoveType(LineRemoveType value) {
        this.lineRemoveType = value;
    }

    /**
     * Gets the value of the lineAddType property.
     * 
     * @return
     *     possible object is
     *     {@link LineAddType }
     *     
     */
    public LineAddType getLineAddType() {
        return lineAddType;
    }

    /**
     * Sets the value of the lineAddType property.
     * 
     * @param value
     *     allowed object is
     *     {@link LineAddType }
     *     
     */
    public void setLineAddType(LineAddType value) {
        this.lineAddType = value;
    }

}
