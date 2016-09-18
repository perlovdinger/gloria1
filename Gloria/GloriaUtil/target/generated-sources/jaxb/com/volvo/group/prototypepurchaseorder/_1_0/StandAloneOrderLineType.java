//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:32 em CEST 
//


package com.volvo.group.prototypepurchaseorder._1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.volvo.group.prototypepurchaseorder.components._1_0.PurchaseOrderLineType;
import com.volvo.group.prototypepurchaseorder.components._1_0.UOMCodeType;


/**
 * <p>Java class for StandAloneOrderLineType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StandAloneOrderLineType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}PurchaseOrderLineType">
 *       &lt;sequence>
 *         &lt;element name="ToolNumber" type="{http://www.volvo.com/3P/Purchasing/2008/10}ToolNumberType" minOccurs="0"/>
 *         &lt;element name="UOMCode" type="{http://www.openapplications.org/oagis/9}UOMCodeType" minOccurs="0"/>
 *         &lt;element name="FinancialInformation" type="{http://www.volvo.com/3P/Purchasing/2008/10}FinancialInformationType" minOccurs="0"/>
 *         &lt;element name="PriceStatus" type="{http://www.volvo.com/3P/Purchasing/2008/10}PriceStatusType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StandAloneOrderLineType", propOrder = {
    "toolNumber",
    "uomCode",
    "financialInformation",
    "priceStatus"
})
public class StandAloneOrderLineType
    extends PurchaseOrderLineType
{

    @XmlElement(name = "ToolNumber")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String toolNumber;
    @XmlElement(name = "UOMCode")
    protected UOMCodeType uomCode;
    @XmlElement(name = "FinancialInformation")
    protected FinancialInformationType financialInformation;
    @XmlElement(name = "PriceStatus")
    protected PriceStatusType priceStatus;

    /**
     * Gets the value of the toolNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToolNumber() {
        return toolNumber;
    }

    /**
     * Sets the value of the toolNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToolNumber(String value) {
        this.toolNumber = value;
    }

    /**
     * Gets the value of the uomCode property.
     * 
     * @return
     *     possible object is
     *     {@link UOMCodeType }
     *     
     */
    public UOMCodeType getUOMCode() {
        return uomCode;
    }

    /**
     * Sets the value of the uomCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link UOMCodeType }
     *     
     */
    public void setUOMCode(UOMCodeType value) {
        this.uomCode = value;
    }

    /**
     * Gets the value of the financialInformation property.
     * 
     * @return
     *     possible object is
     *     {@link FinancialInformationType }
     *     
     */
    public FinancialInformationType getFinancialInformation() {
        return financialInformation;
    }

    /**
     * Sets the value of the financialInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link FinancialInformationType }
     *     
     */
    public void setFinancialInformation(FinancialInformationType value) {
        this.financialInformation = value;
    }

    /**
     * Gets the value of the priceStatus property.
     * 
     * @return
     *     possible object is
     *     {@link PriceStatusType }
     *     
     */
    public PriceStatusType getPriceStatus() {
        return priceStatus;
    }

    /**
     * Sets the value of the priceStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link PriceStatusType }
     *     
     */
    public void setPriceStatus(PriceStatusType value) {
        this.priceStatus = value;
    }

}
