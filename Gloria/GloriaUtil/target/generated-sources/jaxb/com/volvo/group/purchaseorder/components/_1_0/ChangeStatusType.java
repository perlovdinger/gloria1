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
 * <p>Java class for ChangeStatusType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ChangeStatusType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}StatusBasisType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}StateChange" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "ChangeStatusType", propOrder = {
    "stateChange",
    "userArea"
})
public class ChangeStatusType
    extends StatusBasisType
{

    @XmlElement(name = "StateChange")
    protected List<StateChangeType> stateChange;
    @XmlElement(name = "UserArea")
    protected UserAreaType userArea;

    /**
     * Gets the value of the stateChange property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stateChange property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStateChange().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StateChangeType }
     * 
     * 
     */
    public List<StateChangeType> getStateChange() {
        if (stateChange == null) {
            stateChange = new ArrayList<StateChangeType>();
        }
        return this.stateChange;
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
