//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:32 em CEST 
//


package com.volvo.group.prototypepurchaseorder.components._1_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OrderLineType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrderLineType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}ProcurementLineType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}FreightClassification" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}SpecialPriceAuthorizationCode" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}SubstituteItemIndicator" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}RequisitionReference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}PromisedShipDateTime" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}PromisedDeliveryDateTime" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}ShippingInstructions" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderLineType", propOrder = {
    "freightClassification",
    "specialPriceAuthorizationCode",
    "substituteItemIndicator",
    "requisitionReference",
    "promisedShipDateTime",
    "promisedDeliveryDateTime",
    "shippingInstructions"
})
public class OrderLineType
    extends ProcurementLineType
{

    @XmlElement(name = "FreightClassification")
    protected SemanticClassificationType freightClassification;
    @XmlElement(name = "SpecialPriceAuthorizationCode")
    protected CodeType specialPriceAuthorizationCode;
    @XmlElement(name = "SubstituteItemIndicator")
    protected Boolean substituteItemIndicator;
    @XmlElement(name = "RequisitionReference")
    protected List<OrderReferenceType> requisitionReference;
    @XmlElement(name = "PromisedShipDateTime")
    protected String promisedShipDateTime;
    @XmlElement(name = "PromisedDeliveryDateTime")
    protected String promisedDeliveryDateTime;
    @XmlElement(name = "ShippingInstructions")
    protected List<TextType> shippingInstructions;

    /**
     * Gets the value of the freightClassification property.
     * 
     * @return
     *     possible object is
     *     {@link SemanticClassificationType }
     *     
     */
    public SemanticClassificationType getFreightClassification() {
        return freightClassification;
    }

    /**
     * Sets the value of the freightClassification property.
     * 
     * @param value
     *     allowed object is
     *     {@link SemanticClassificationType }
     *     
     */
    public void setFreightClassification(SemanticClassificationType value) {
        this.freightClassification = value;
    }

    /**
     * Gets the value of the specialPriceAuthorizationCode property.
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getSpecialPriceAuthorizationCode() {
        return specialPriceAuthorizationCode;
    }

    /**
     * Sets the value of the specialPriceAuthorizationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setSpecialPriceAuthorizationCode(CodeType value) {
        this.specialPriceAuthorizationCode = value;
    }

    /**
     * Gets the value of the substituteItemIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSubstituteItemIndicator() {
        return substituteItemIndicator;
    }

    /**
     * Sets the value of the substituteItemIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSubstituteItemIndicator(Boolean value) {
        this.substituteItemIndicator = value;
    }

    /**
     * Gets the value of the requisitionReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requisitionReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequisitionReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrderReferenceType }
     * 
     * 
     */
    public List<OrderReferenceType> getRequisitionReference() {
        if (requisitionReference == null) {
            requisitionReference = new ArrayList<OrderReferenceType>();
        }
        return this.requisitionReference;
    }

    /**
     * Gets the value of the promisedShipDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPromisedShipDateTime() {
        return promisedShipDateTime;
    }

    /**
     * Sets the value of the promisedShipDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPromisedShipDateTime(String value) {
        this.promisedShipDateTime = value;
    }

    /**
     * Gets the value of the promisedDeliveryDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPromisedDeliveryDateTime() {
        return promisedDeliveryDateTime;
    }

    /**
     * Sets the value of the promisedDeliveryDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPromisedDeliveryDateTime(String value) {
        this.promisedDeliveryDateTime = value;
    }

    /**
     * Gets the value of the shippingInstructions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the shippingInstructions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getShippingInstructions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TextType }
     * 
     * 
     */
    public List<TextType> getShippingInstructions() {
        if (shippingInstructions == null) {
            shippingInstructions = new ArrayList<TextType>();
        }
        return this.shippingInstructions;
    }

}