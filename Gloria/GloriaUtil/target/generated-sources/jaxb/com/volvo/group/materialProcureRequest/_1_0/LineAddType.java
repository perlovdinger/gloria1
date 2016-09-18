//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:36 em CEST 
//


package com.volvo.group.materialProcureRequest._1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for LineAddType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LineAddType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BomLink" type="{http://www.volvo.com/MaterialProcureRequest/1_0}BomLinkType"/>
 *         &lt;element name="ItemToVariantLink" type="{http://www.volvo.com/MaterialProcureRequest/1_0}ItemToVariantLinkType" minOccurs="0"/>
 *         &lt;element name="Part" type="{http://www.volvo.com/MaterialProcureRequest/1_0}PartType"/>
 *         &lt;element name="FinancialInfo" type="{http://www.volvo.com/MaterialProcureRequest/1_0}FinancialInfoType"/>
 *         &lt;element name="RequiredShipToArrive" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LineAddType", propOrder = {
    "bomLink",
    "itemToVariantLink",
    "part",
    "financialInfo",
    "requiredShipToArrive"
})
public class LineAddType {

    @XmlElement(name = "BomLink", required = true)
    protected BomLinkType bomLink;
    @XmlElement(name = "ItemToVariantLink")
    protected ItemToVariantLinkType itemToVariantLink;
    @XmlElement(name = "Part", required = true)
    protected PartType part;
    @XmlElement(name = "FinancialInfo", required = true)
    protected FinancialInfoType financialInfo;
    @XmlElement(name = "RequiredShipToArrive")
    protected XMLGregorianCalendar requiredShipToArrive;

    /**
     * Gets the value of the bomLink property.
     * 
     * @return
     *     possible object is
     *     {@link BomLinkType }
     *     
     */
    public BomLinkType getBomLink() {
        return bomLink;
    }

    /**
     * Sets the value of the bomLink property.
     * 
     * @param value
     *     allowed object is
     *     {@link BomLinkType }
     *     
     */
    public void setBomLink(BomLinkType value) {
        this.bomLink = value;
    }

    /**
     * Gets the value of the itemToVariantLink property.
     * 
     * @return
     *     possible object is
     *     {@link ItemToVariantLinkType }
     *     
     */
    public ItemToVariantLinkType getItemToVariantLink() {
        return itemToVariantLink;
    }

    /**
     * Sets the value of the itemToVariantLink property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemToVariantLinkType }
     *     
     */
    public void setItemToVariantLink(ItemToVariantLinkType value) {
        this.itemToVariantLink = value;
    }

    /**
     * Gets the value of the part property.
     * 
     * @return
     *     possible object is
     *     {@link PartType }
     *     
     */
    public PartType getPart() {
        return part;
    }

    /**
     * Sets the value of the part property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartType }
     *     
     */
    public void setPart(PartType value) {
        this.part = value;
    }

    /**
     * Gets the value of the financialInfo property.
     * 
     * @return
     *     possible object is
     *     {@link FinancialInfoType }
     *     
     */
    public FinancialInfoType getFinancialInfo() {
        return financialInfo;
    }

    /**
     * Sets the value of the financialInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link FinancialInfoType }
     *     
     */
    public void setFinancialInfo(FinancialInfoType value) {
        this.financialInfo = value;
    }

    /**
     * Gets the value of the requiredShipToArrive property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRequiredShipToArrive() {
        return requiredShipToArrive;
    }

    /**
     * Sets the value of the requiredShipToArrive property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRequiredShipToArrive(XMLGregorianCalendar value) {
        this.requiredShipToArrive = value;
    }

}
