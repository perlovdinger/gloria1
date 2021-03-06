//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:32 em CEST 
//


package com.volvo.group.prototypepurchaseorder.components._1_0;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PriceBreakType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PriceBreakType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}DiscountAmount" minOccurs="0"/>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}DiscountPercent" minOccurs="0"/>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}OverridePrice" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;choice>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}PriceBreakQuantity" minOccurs="0"/>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}PriceBreakAmount" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;group ref="{http://www.openapplications.org/oagis/9}FreeFormTextGroup" minOccurs="0"/>
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
@XmlType(name = "PriceBreakType", propOrder = {
    "discountAmount",
    "discountPercent",
    "overridePrice",
    "priceBreakQuantity",
    "priceBreakAmount",
    "description",
    "note",
    "userArea"
})
public class PriceBreakType {

    @XmlElement(name = "DiscountAmount")
    protected AmountType discountAmount;
    @XmlElement(name = "DiscountPercent")
    protected BigDecimal discountPercent;
    @XmlElement(name = "OverridePrice")
    protected AmountType overridePrice;
    @XmlElement(name = "PriceBreakQuantity")
    protected QuantityType priceBreakQuantity;
    @XmlElement(name = "PriceBreakAmount")
    protected AmountType priceBreakAmount;
    @XmlElement(name = "Description")
    protected List<DescriptionType> description;
    @XmlElement(name = "Note")
    protected List<NoteType> note;
    @XmlElement(name = "UserArea")
    protected UserAreaType userArea;

    /**
     * Allows a flat amount to be discounted per item.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getDiscountAmount() {
        return discountAmount;
    }

    /**
     * Allows a flat amount to be discounted per item.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setDiscountAmount(AmountType value) {
        this.discountAmount = value;
    }

    /**
     * Indicates the percentage of the PriceBreak.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    /**
     * Indicates the percentage of the PriceBreak.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDiscountPercent(BigDecimal value) {
        this.discountPercent = value;
    }

    /**
     * This is the price that overrides the list price when a given price break requirement is reached. For example, if a customer orders over 50,000 dollars worth of computer equipment, he may qualify for an overriding price.  The list price of $800 per will be modified to $700 per
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getOverridePrice() {
        return overridePrice;
    }

    /**
     * This is the price that overrides the list price when a given price break requirement is reached. For example, if a customer orders over 50,000 dollars worth of computer equipment, he may qualify for an overriding price.  The list price of $800 per will be modified to $700 per
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setOverridePrice(AmountType value) {
        this.overridePrice = value;
    }

    /**
     * The quantity that must be purchased of the item or commodity on a given price list line before the benefit, or until the detriment of a given price break line will apply.  For example, if a customer orders up to 5000 units of flour, they will get a price of $8:00 a case.  If the customer buys 5500 units of flour they will get a price of $7.50 a case
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getPriceBreakQuantity() {
        return priceBreakQuantity;
    }

    /**
     * The quantity that must be purchased of the item or commodity on a given price list line before the benefit, or until the detriment of a given price break line will apply.  For example, if a customer orders up to 5000 units of flour, they will get a price of $8:00 a case.  If the customer buys 5500 units of flour they will get a price of $7.50 a case
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setPriceBreakQuantity(QuantityType value) {
        this.priceBreakQuantity = value;
    }

    /**
     * The monetary amount that must be spent on a given price list line before the benefit or until the detriment of a given price break line will apply.  For example, if a customer orders up to 5000 dollars worth of flour, they will get a price of $8:00 a case.  If the customer buys 5500 dollars worth of flour they will get a price of $7.50 a case
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getPriceBreakAmount() {
        return priceBreakAmount;
    }

    /**
     * The monetary amount that must be spent on a given price list line before the benefit or until the detriment of a given price break line will apply.  For example, if a customer orders up to 5000 dollars worth of flour, they will get a price of $8:00 a case.  If the customer buys 5500 dollars worth of flour they will get a price of $7.50 a case
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setPriceBreakAmount(AmountType value) {
        this.priceBreakAmount = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the description property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDescription().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DescriptionType }
     * 
     * 
     */
    public List<DescriptionType> getDescription() {
        if (description == null) {
            description = new ArrayList<DescriptionType>();
        }
        return this.description;
    }

    /**
     * Gets the value of the note property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the note property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNote().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NoteType }
     * 
     * 
     */
    public List<NoteType> getNote() {
        if (note == null) {
            note = new ArrayList<NoteType>();
        }
        return this.note;
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
