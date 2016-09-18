//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:29 em CEST 
//


package com.volvo.group.processrequisition._1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for FinancialInformationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FinancialInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.volvo.com/3P/Purchasing/2008/10}ObjectID" minOccurs="0"/>
 *         &lt;element ref="{http://www.volvo.com/3P/Purchasing/2008/10}CostCollector" minOccurs="0"/>
 *         &lt;element ref="{http://www.volvo.com/3P/Purchasing/2008/10}FinancialAccount" minOccurs="0"/>
 *         &lt;element ref="{http://www.volvo.com/3P/Purchasing/2008/10}CostCenter" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FinancialInformationType", propOrder = {
    "objectID",
    "costCollector",
    "financialAccount",
    "costCenter"
})
public class FinancialInformationType {

    @XmlElement(name = "ObjectID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String objectID;
    @XmlElement(name = "CostCollector")
    protected CostCollectorType costCollector;
    @XmlElement(name = "FinancialAccount")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String financialAccount;
    @XmlElement(name = "CostCenter")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String costCenter;

    /**
     * Gets the value of the objectID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectID() {
        return objectID;
    }

    /**
     * Sets the value of the objectID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjectID(String value) {
        this.objectID = value;
    }

    /**
     * Gets the value of the costCollector property.
     * 
     * @return
     *     possible object is
     *     {@link CostCollectorType }
     *     
     */
    public CostCollectorType getCostCollector() {
        return costCollector;
    }

    /**
     * Sets the value of the costCollector property.
     * 
     * @param value
     *     allowed object is
     *     {@link CostCollectorType }
     *     
     */
    public void setCostCollector(CostCollectorType value) {
        this.costCollector = value;
    }

    /**
     * Gets the value of the financialAccount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFinancialAccount() {
        return financialAccount;
    }

    /**
     * Sets the value of the financialAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFinancialAccount(String value) {
        this.financialAccount = value;
    }

    /**
     * Gets the value of the costCenter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCostCenter() {
        return costCenter;
    }

    /**
     * Sets the value of the costCenter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCostCenter(String value) {
        this.costCenter = value;
    }

}
