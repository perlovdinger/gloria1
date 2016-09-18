//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:32 em CEST 
//


package com.volvo.group.prototypepurchaseorder.components._1_0;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for TelephoneNumberType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TelephoneNumberType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.openapplications.org/oagis/9>StringType">
 *       &lt;attribute name="accessCode" type="{http://www.openapplications.org/oagis/9}PositiveIntegerNumericType" />
 *       &lt;attribute name="areaCode" type="{http://www.openapplications.org/oagis/9}PositiveIntegerNumericType" />
 *       &lt;attribute name="countryCode" type="{http://www.openapplications.org/oagis/9}PositiveIntegerNumericType" />
 *       &lt;attribute name="exchangeCode" type="{http://www.openapplications.org/oagis/9}PositiveIntegerNumericType" />
 *       &lt;attribute name="extension" type="{http://www.openapplications.org/oagis/9}PositiveIntegerNumericType" />
 *       &lt;attribute name="numberFormat" type="{http://www.openapplications.org/oagis/9}NormalizedStringType" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TelephoneNumberType", propOrder = {
    "value"
})
public class TelephoneNumberType {

    @XmlValue
    protected String value;
    @XmlAttribute
    protected BigInteger accessCode;
    @XmlAttribute
    protected BigInteger areaCode;
    @XmlAttribute
    protected BigInteger countryCode;
    @XmlAttribute
    protected BigInteger exchangeCode;
    @XmlAttribute
    protected BigInteger extension;
    @XmlAttribute
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String numberFormat;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the accessCode property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAccessCode() {
        return accessCode;
    }

    /**
     * Sets the value of the accessCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAccessCode(BigInteger value) {
        this.accessCode = value;
    }

    /**
     * Gets the value of the areaCode property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAreaCode() {
        return areaCode;
    }

    /**
     * Sets the value of the areaCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAreaCode(BigInteger value) {
        this.areaCode = value;
    }

    /**
     * Gets the value of the countryCode property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the value of the countryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCountryCode(BigInteger value) {
        this.countryCode = value;
    }

    /**
     * Gets the value of the exchangeCode property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getExchangeCode() {
        return exchangeCode;
    }

    /**
     * Sets the value of the exchangeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setExchangeCode(BigInteger value) {
        this.exchangeCode = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setExtension(BigInteger value) {
        this.extension = value;
    }

    /**
     * Gets the value of the numberFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumberFormat() {
        return numberFormat;
    }

    /**
     * Sets the value of the numberFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumberFormat(String value) {
        this.numberFormat = value;
    }

}
