//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:29 em CEST 
//


package com.volvo.group.processrequisition.components._1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PayerPartyBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PayerPartyBaseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}PartyBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}CustomerAccountID" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PayerPartyBaseType", propOrder = {
    "customerAccountID"
})
public class PayerPartyBaseType
    extends PartyBaseType
{

    @XmlElement(name = "CustomerAccountID")
    protected IdentifierType customerAccountID;

    /**
     * Gets the value of the customerAccountID property.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getCustomerAccountID() {
        return customerAccountID;
    }

    /**
     * Sets the value of the customerAccountID property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setCustomerAccountID(IdentifierType value) {
        this.customerAccountID = value;
    }

}
