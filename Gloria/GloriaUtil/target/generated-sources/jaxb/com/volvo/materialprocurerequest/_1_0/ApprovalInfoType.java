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
 * <p>Java class for ApprovalInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ApprovalInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MailformId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ApprovalAmount" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="ApprovalCurrency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ApprovalInfoType", propOrder = {
    "mailformId",
    "approvalAmount",
    "approvalCurrency"
})
public class ApprovalInfoType {

    @XmlElement(name = "MailformId", required = true)
    protected String mailformId;
    @XmlElement(name = "ApprovalAmount")
    protected Double approvalAmount;
    @XmlElement(name = "ApprovalCurrency")
    protected String approvalCurrency;

    /**
     * Gets the value of the mailformId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMailformId() {
        return mailformId;
    }

    /**
     * Sets the value of the mailformId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMailformId(String value) {
        this.mailformId = value;
    }

    /**
     * Gets the value of the approvalAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getApprovalAmount() {
        return approvalAmount;
    }

    /**
     * Sets the value of the approvalAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setApprovalAmount(Double value) {
        this.approvalAmount = value;
    }

    /**
     * Gets the value of the approvalCurrency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApprovalCurrency() {
        return approvalCurrency;
    }

    /**
     * Sets the value of the approvalCurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApprovalCurrency(String value) {
        this.approvalCurrency = value;
    }

}
