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
 *         &lt;element ref="{}ProcureDetail"/>
 *         &lt;element ref="{}ProcureOrder"/>
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
    "procureDetail",
    "procureOrder"
})
@XmlRootElement(name = "Procure")
public class Procure {

    @XmlElement(name = "ProcureDetail", required = true)
    protected ProcureDetail procureDetail;
    @XmlElement(name = "ProcureOrder", required = true)
    protected ProcureOrder procureOrder;

    /**
     * Gets the value of the procureDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ProcureDetail }
     *     
     */
    public ProcureDetail getProcureDetail() {
        return procureDetail;
    }

    /**
     * Sets the value of the procureDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcureDetail }
     *     
     */
    public void setProcureDetail(ProcureDetail value) {
        this.procureDetail = value;
    }

    /**
     * Gets the value of the procureOrder property.
     * 
     * @return
     *     possible object is
     *     {@link ProcureOrder }
     *     
     */
    public ProcureOrder getProcureOrder() {
        return procureOrder;
    }

    /**
     * Sets the value of the procureOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcureOrder }
     *     
     */
    public void setProcureOrder(ProcureOrder value) {
        this.procureOrder = value;
    }

}
