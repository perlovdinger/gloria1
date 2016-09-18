//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:26 em CEST 
//


package com.volvo.group.purchaseorder.components._1_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Identifies the agiven portion of an organization.
 * 
 * <p>Java class for EnterpriseUnitType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EnterpriseUnitType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}CostCenterID" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}ProfitCenterID" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}GLEntityID" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}GLElement" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}UserArea" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnterpriseUnitType", propOrder = {
    "costCenterID",
    "profitCenterID",
    "glEntityID",
    "glElement",
    "userArea"
})
public class EnterpriseUnitType {

    @XmlElement(name = "CostCenterID")
    protected IdentifierType costCenterID;
    @XmlElement(name = "ProfitCenterID")
    protected IdentifierType profitCenterID;
    @XmlElement(name = "GLEntityID")
    protected List<IdentifierType> glEntityID;
    @XmlElement(name = "GLElement")
    protected GLElementType glElement;
    @XmlElement(name = "UserArea")
    protected UserAreaType userArea;

    /**
     * Gets the value of the costCenterID property.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getCostCenterID() {
        return costCenterID;
    }

    /**
     * Sets the value of the costCenterID property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setCostCenterID(IdentifierType value) {
        this.costCenterID = value;
    }

    /**
     * Gets the value of the profitCenterID property.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getProfitCenterID() {
        return profitCenterID;
    }

    /**
     * Sets the value of the profitCenterID property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setProfitCenterID(IdentifierType value) {
        this.profitCenterID = value;
    }

    /**
     * Gets the value of the glEntityID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the glEntityID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGLEntityID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IdentifierType }
     * 
     * 
     */
    public List<IdentifierType> getGLEntityID() {
        if (glEntityID == null) {
            glEntityID = new ArrayList<IdentifierType>();
        }
        return this.glEntityID;
    }

    /**
     * Gets the value of the glElement property.
     * 
     * @return
     *     possible object is
     *     {@link GLElementType }
     *     
     */
    public GLElementType getGLElement() {
        return glElement;
    }

    /**
     * Sets the value of the glElement property.
     * 
     * @param value
     *     allowed object is
     *     {@link GLElementType }
     *     
     */
    public void setGLElement(GLElementType value) {
        this.glElement = value;
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
