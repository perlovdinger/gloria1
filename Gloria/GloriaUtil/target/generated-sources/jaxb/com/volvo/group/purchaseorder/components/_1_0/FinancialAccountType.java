//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:26 em CEST 
//


package com.volvo.group.purchaseorder.components._1_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FinancialAccountType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FinancialAccountType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.openapplications.org/oagis/9}FinancialAccountIDsGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}CurrencyCode" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}Name" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}Type" minOccurs="0"/>
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
@XmlType(name = "FinancialAccountType", propOrder = {
    "id",
    "bbanid",
    "ibanid",
    "upicid",
    "accountIDs",
    "currencyCode",
    "name",
    "type",
    "userArea"
})
public class FinancialAccountType {

    @XmlElementRef(name = "ID", namespace = "http://www.openapplications.org/oagis/9", type = JAXBElement.class)
    protected List<JAXBElement<? extends com.volvo.group.purchaseorder.components._1_0.IdentifierType>> id;
    @XmlElement(name = "BBANID")
    protected com.volvo.group.purchaseorder.components._1_0.IdentifierType bbanid;
    @XmlElement(name = "IBANID")
    protected com.volvo.group.purchaseorder.components._1_0.IdentifierType ibanid;
    @XmlElement(name = "UPICID")
    protected com.volvo.group.purchaseorder.components._1_0.IdentifierType upicid;
    @XmlElement(name = "AccountIDs")
    protected NamedIDsType accountIDs;
    @XmlElement(name = "CurrencyCode")
    protected CurrencyCodeType currencyCode;
    @XmlElement(name = "Name")
    protected NameType name;
    @XmlElement(name = "Type")
    protected CodeType type;
    @XmlElement(name = "UserArea")
    protected UserAreaType userArea;

    /**
     * Gets the value of the id property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the id property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link com.volvo.group.purchaseorder._1_0.IdentifierType }{@code >}
     * {@link JAXBElement }{@code <}{@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends com.volvo.group.purchaseorder.components._1_0.IdentifierType>> getID() {
        if (id == null) {
            id = new ArrayList<JAXBElement<? extends com.volvo.group.purchaseorder.components._1_0.IdentifierType>>();
        }
        return this.id;
    }

    /**
     * Gets the value of the bbanid property.
     * 
     * @return
     *     possible object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public com.volvo.group.purchaseorder.components._1_0.IdentifierType getBBANID() {
        return bbanid;
    }

    /**
     * Sets the value of the bbanid property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public void setBBANID(com.volvo.group.purchaseorder.components._1_0.IdentifierType value) {
        this.bbanid = value;
    }

    /**
     * Gets the value of the ibanid property.
     * 
     * @return
     *     possible object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public com.volvo.group.purchaseorder.components._1_0.IdentifierType getIBANID() {
        return ibanid;
    }

    /**
     * Sets the value of the ibanid property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public void setIBANID(com.volvo.group.purchaseorder.components._1_0.IdentifierType value) {
        this.ibanid = value;
    }

    /**
     * Gets the value of the upicid property.
     * 
     * @return
     *     possible object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public com.volvo.group.purchaseorder.components._1_0.IdentifierType getUPICID() {
        return upicid;
    }

    /**
     * Sets the value of the upicid property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public void setUPICID(com.volvo.group.purchaseorder.components._1_0.IdentifierType value) {
        this.upicid = value;
    }

    /**
     * Gets the value of the accountIDs property.
     * 
     * @return
     *     possible object is
     *     {@link NamedIDsType }
     *     
     */
    public NamedIDsType getAccountIDs() {
        return accountIDs;
    }

    /**
     * Sets the value of the accountIDs property.
     * 
     * @param value
     *     allowed object is
     *     {@link NamedIDsType }
     *     
     */
    public void setAccountIDs(NamedIDsType value) {
        this.accountIDs = value;
    }

    /**
     * Gets the value of the currencyCode property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyCodeType }
     *     
     */
    public CurrencyCodeType getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Sets the value of the currencyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyCodeType }
     *     
     */
    public void setCurrencyCode(CurrencyCodeType value) {
        this.currencyCode = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link NameType }
     *     
     */
    public NameType getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link NameType }
     *     
     */
    public void setName(NameType value) {
        this.name = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setType(CodeType value) {
        this.type = value;
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
