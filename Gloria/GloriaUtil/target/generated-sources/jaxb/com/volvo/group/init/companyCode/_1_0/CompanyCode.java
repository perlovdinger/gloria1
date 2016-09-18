//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:37 em CEST 
//


package com.volvo.group.init.companyCode._1_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element ref="{}Currency" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="code" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="companyCodeGroup" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="defaultCurrency" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="receivingGoods" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="sapPurchaseOrg" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sapQuantityBlockReceiverId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sendGRtoSAP" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="sendPOtoSAP" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "currency"
})
@XmlRootElement(name = "CompanyCode")
public class CompanyCode {

    @XmlElement(name = "Currency")
    protected List<Currency> currency;
    @XmlAttribute(required = true)
    protected String code;
    @XmlAttribute(required = true)
    protected String companyCodeGroup;
    @XmlAttribute(required = true)
    protected String defaultCurrency;
    @XmlAttribute(required = true)
    protected String name;
    @XmlAttribute(required = true)
    protected boolean receivingGoods;
    @XmlAttribute
    protected String sapPurchaseOrg;
    @XmlAttribute
    protected String sapQuantityBlockReceiverId;
    @XmlAttribute(required = true)
    protected boolean sendGRtoSAP;
    @XmlAttribute(required = true)
    protected boolean sendPOtoSAP;

    /**
     * Gets the value of the currency property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the currency property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCurrency().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Currency }
     * 
     * 
     */
    public List<Currency> getCurrency() {
        if (currency == null) {
            currency = new ArrayList<Currency>();
        }
        return this.currency;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the companyCodeGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompanyCodeGroup() {
        return companyCodeGroup;
    }

    /**
     * Sets the value of the companyCodeGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompanyCodeGroup(String value) {
        this.companyCodeGroup = value;
    }

    /**
     * Gets the value of the defaultCurrency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    /**
     * Sets the value of the defaultCurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultCurrency(String value) {
        this.defaultCurrency = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the receivingGoods property.
     * 
     */
    public boolean isReceivingGoods() {
        return receivingGoods;
    }

    /**
     * Sets the value of the receivingGoods property.
     * 
     */
    public void setReceivingGoods(boolean value) {
        this.receivingGoods = value;
    }

    /**
     * Gets the value of the sapPurchaseOrg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSapPurchaseOrg() {
        return sapPurchaseOrg;
    }

    /**
     * Sets the value of the sapPurchaseOrg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSapPurchaseOrg(String value) {
        this.sapPurchaseOrg = value;
    }

    /**
     * Gets the value of the sapQuantityBlockReceiverId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSapQuantityBlockReceiverId() {
        return sapQuantityBlockReceiverId;
    }

    /**
     * Sets the value of the sapQuantityBlockReceiverId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSapQuantityBlockReceiverId(String value) {
        this.sapQuantityBlockReceiverId = value;
    }

    /**
     * Gets the value of the sendGRtoSAP property.
     * 
     */
    public boolean isSendGRtoSAP() {
        return sendGRtoSAP;
    }

    /**
     * Sets the value of the sendGRtoSAP property.
     * 
     */
    public void setSendGRtoSAP(boolean value) {
        this.sendGRtoSAP = value;
    }

    /**
     * Gets the value of the sendPOtoSAP property.
     * 
     */
    public boolean isSendPOtoSAP() {
        return sendPOtoSAP;
    }

    /**
     * Sets the value of the sendPOtoSAP property.
     * 
     */
    public void setSendPOtoSAP(boolean value) {
        this.sendPOtoSAP = value;
    }

}