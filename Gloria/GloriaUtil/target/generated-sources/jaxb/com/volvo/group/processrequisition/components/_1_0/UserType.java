//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:29 em CEST 
//


package com.volvo.group.processrequisition.components._1_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UserType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UserType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}ID" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}GroupID" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}UserAccount" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "UserType", propOrder = {
    "id",
    "groupID",
    "userAccount",
    "userArea"
})
public class UserType {

    @XmlElementRef(name = "ID", namespace = "http://www.openapplications.org/oagis/9", type = JAXBElement.class)
    protected JAXBElement<? extends com.volvo.group.processrequisition.components._1_0.IdentifierType> id;
    @XmlElement(name = "GroupID")
    protected List<com.volvo.group.processrequisition.components._1_0.IdentifierType> groupID;
    @XmlElement(name = "UserAccount")
    protected List<UserAccountType> userAccount;
    @XmlElement(name = "UserArea")
    protected UserAreaType userArea;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link com.volvo.group.processrequisition.components._1_0.IdentifierType }{@code >}
     *     {@link JAXBElement }{@code <}{@link com.volvo.group.processrequisition._1_0.IdentifierType }{@code >}
     *     
     */
    public JAXBElement<? extends com.volvo.group.processrequisition.components._1_0.IdentifierType> getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link com.volvo.group.processrequisition.components._1_0.IdentifierType }{@code >}
     *     {@link JAXBElement }{@code <}{@link com.volvo.group.processrequisition._1_0.IdentifierType }{@code >}
     *     
     */
    public void setID(JAXBElement<? extends com.volvo.group.processrequisition.components._1_0.IdentifierType> value) {
        this.id = ((JAXBElement<? extends com.volvo.group.processrequisition.components._1_0.IdentifierType> ) value);
    }

    /**
     * Gets the value of the groupID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groupID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroupID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link com.volvo.group.processrequisition.components._1_0.IdentifierType }
     * 
     * 
     */
    public List<com.volvo.group.processrequisition.components._1_0.IdentifierType> getGroupID() {
        if (groupID == null) {
            groupID = new ArrayList<com.volvo.group.processrequisition.components._1_0.IdentifierType>();
        }
        return this.groupID;
    }

    /**
     * Gets the value of the userAccount property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userAccount property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserAccount().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UserAccountType }
     * 
     * 
     */
    public List<UserAccountType> getUserAccount() {
        if (userAccount == null) {
            userAccount = new ArrayList<UserAccountType>();
        }
        return this.userAccount;
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
