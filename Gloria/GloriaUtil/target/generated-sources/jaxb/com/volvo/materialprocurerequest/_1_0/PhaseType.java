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
 * <p>Java class for PhaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PhaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PhaseRemove" type="{http://www.volvo.com/MaterialProcureRequest/1_0}PhaseRemoveType" minOccurs="0"/>
 *         &lt;element name="Phase" type="{http://www.volvo.com/MaterialProcureRequest/1_0}PhaseAddType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PhaseType", propOrder = {
    "phaseRemove",
    "phase"
})
public class PhaseType {

    @XmlElement(name = "PhaseRemove")
    protected PhaseRemoveType phaseRemove;
    @XmlElement(name = "Phase")
    protected PhaseAddType phase;

    /**
     * Gets the value of the phaseRemove property.
     * 
     * @return
     *     possible object is
     *     {@link PhaseRemoveType }
     *     
     */
    public PhaseRemoveType getPhaseRemove() {
        return phaseRemove;
    }

    /**
     * Sets the value of the phaseRemove property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhaseRemoveType }
     *     
     */
    public void setPhaseRemove(PhaseRemoveType value) {
        this.phaseRemove = value;
    }

    /**
     * Gets the value of the phase property.
     * 
     * @return
     *     possible object is
     *     {@link PhaseAddType }
     *     
     */
    public PhaseAddType getPhase() {
        return phase;
    }

    /**
     * Sets the value of the phase property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhaseAddType }
     *     
     */
    public void setPhase(PhaseAddType value) {
        this.phase = value;
    }

}
