//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:36 em CEST 
//


package com.volvo.group.materialProcureRequest._1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MaterialRequestChangeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MaterialRequestChangeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RequestType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MaterialRequestChangeAdd" type="{http://www.volvo.com/MaterialProcureRequest/1_0}MaterialRequestChangeTypeAdd" minOccurs="0"/>
 *         &lt;element name="MaterialRequestChangeRemove" type="{http://www.volvo.com/MaterialProcureRequest/1_0}MaterialRequestChangeTypeRemove" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MaterialRequestChangeType", propOrder = {
    "requestType",
    "materialRequestChangeAdd",
    "materialRequestChangeRemove"
})
public class MaterialRequestChangeType {

    @XmlElement(name = "RequestType", required = true)
    protected String requestType;
    @XmlElement(name = "MaterialRequestChangeAdd")
    protected MaterialRequestChangeTypeAdd materialRequestChangeAdd;
    @XmlElement(name = "MaterialRequestChangeRemove")
    protected MaterialRequestChangeTypeRemove materialRequestChangeRemove;

    /**
     * Gets the value of the requestType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestType() {
        return requestType;
    }

    /**
     * Sets the value of the requestType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestType(String value) {
        this.requestType = value;
    }

    /**
     * Gets the value of the materialRequestChangeAdd property.
     * 
     * @return
     *     possible object is
     *     {@link MaterialRequestChangeTypeAdd }
     *     
     */
    public MaterialRequestChangeTypeAdd getMaterialRequestChangeAdd() {
        return materialRequestChangeAdd;
    }

    /**
     * Sets the value of the materialRequestChangeAdd property.
     * 
     * @param value
     *     allowed object is
     *     {@link MaterialRequestChangeTypeAdd }
     *     
     */
    public void setMaterialRequestChangeAdd(MaterialRequestChangeTypeAdd value) {
        this.materialRequestChangeAdd = value;
    }

    /**
     * Gets the value of the materialRequestChangeRemove property.
     * 
     * @return
     *     possible object is
     *     {@link MaterialRequestChangeTypeRemove }
     *     
     */
    public MaterialRequestChangeTypeRemove getMaterialRequestChangeRemove() {
        return materialRequestChangeRemove;
    }

    /**
     * Sets the value of the materialRequestChangeRemove property.
     * 
     * @param value
     *     allowed object is
     *     {@link MaterialRequestChangeTypeRemove }
     *     
     */
    public void setMaterialRequestChangeRemove(MaterialRequestChangeTypeRemove value) {
        this.materialRequestChangeRemove = value;
    }

}