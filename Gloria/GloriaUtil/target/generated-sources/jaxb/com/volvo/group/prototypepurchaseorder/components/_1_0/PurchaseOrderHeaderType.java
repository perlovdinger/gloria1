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
 * <p>Java class for PurchaseOrderHeaderType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PurchaseOrderHeaderType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}OrderHeaderType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}TaxExemptCodes" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}Event" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}Classification" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}OrderDateTime" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}BuyerParty" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}AcceptByDateTime" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}ReleaseNumber" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}RequisitionReference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}QuoteReference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}SalesOrderReference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}UserArea" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PurchaseOrderHeaderType", propOrder = {
    "taxExemptCodes",
    "event",
    "classification",
    "orderDateTime",
    "buyerParty",
    "acceptByDateTime",
    "releaseNumber",
    "requisitionReference",
    "quoteReference",
    "salesOrderReference",
    "userArea"
})
public class PurchaseOrderHeaderType
    extends OrderHeaderType
{

    @XmlElement(name = "TaxExemptCodes")
    protected CodesType taxExemptCodes;
    @XmlElement(name = "Event")
    protected List<EventType> event;
    @XmlElement(name = "Classification")
    protected List<ClassificationType> classification;
    @XmlElement(name = "OrderDateTime")
    protected String orderDateTime;
    @XmlElement(name = "BuyerParty")
    protected CustomerPartyType buyerParty;
    @XmlElement(name = "AcceptByDateTime")
    protected String acceptByDateTime;
    @XmlElement(name = "ReleaseNumber")
    protected IdentifierType releaseNumber;
    @XmlElement(name = "RequisitionReference")
    protected List<OrderReferenceType> requisitionReference;
    @XmlElement(name = "QuoteReference")
    protected List<OrderReferenceType> quoteReference;
    @XmlElement(name = "SalesOrderReference")
    protected List<OrderReferenceType> salesOrderReference;
    @XmlElement(name = "UserArea")
    protected UserAreaType userArea;

    /**
     * Gets the value of the taxExemptCodes property.
     * 
     * @return
     *     possible object is
     *     {@link CodesType }
     *     
     */
    public CodesType getTaxExemptCodes() {
        return taxExemptCodes;
    }

    /**
     * Sets the value of the taxExemptCodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodesType }
     *     
     */
    public void setTaxExemptCodes(CodesType value) {
        this.taxExemptCodes = value;
    }

    /**
     * Gets the value of the event property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the event property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEvent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EventType }
     * 
     * 
     */
    public List<EventType> getEvent() {
        if (event == null) {
            event = new ArrayList<EventType>();
        }
        return this.event;
    }

    /**
     * Gets the value of the classification property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the classification property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClassification().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClassificationType }
     * 
     * 
     */
    public List<ClassificationType> getClassification() {
        if (classification == null) {
            classification = new ArrayList<ClassificationType>();
        }
        return this.classification;
    }

    /**
     * Gets the value of the orderDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderDateTime() {
        return orderDateTime;
    }

    /**
     * Sets the value of the orderDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderDateTime(String value) {
        this.orderDateTime = value;
    }

    /**
     * Gets the value of the buyerParty property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerPartyType }
     *     
     */
    public CustomerPartyType getBuyerParty() {
        return buyerParty;
    }

    /**
     * Sets the value of the buyerParty property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerPartyType }
     *     
     */
    public void setBuyerParty(CustomerPartyType value) {
        this.buyerParty = value;
    }

    /**
     * Gets the value of the acceptByDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAcceptByDateTime() {
        return acceptByDateTime;
    }

    /**
     * Sets the value of the acceptByDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAcceptByDateTime(String value) {
        this.acceptByDateTime = value;
    }

    /**
     * Gets the value of the releaseNumber property.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getReleaseNumber() {
        return releaseNumber;
    }

    /**
     * Sets the value of the releaseNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setReleaseNumber(IdentifierType value) {
        this.releaseNumber = value;
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
     * Gets the value of the quoteReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the quoteReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQuoteReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrderReferenceType }
     * 
     * 
     */
    public List<OrderReferenceType> getQuoteReference() {
        if (quoteReference == null) {
            quoteReference = new ArrayList<OrderReferenceType>();
        }
        return this.quoteReference;
    }

    /**
     * Gets the value of the salesOrderReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the salesOrderReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSalesOrderReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrderReferenceType }
     * 
     * 
     */
    public List<OrderReferenceType> getSalesOrderReference() {
        if (salesOrderReference == null) {
            salesOrderReference = new ArrayList<OrderReferenceType>();
        }
        return this.salesOrderReference;
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
