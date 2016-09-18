//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:26 em CEST 
//


package com.volvo.group.purchaseorder.components._1_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for DocumentReferenceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocumentReferenceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}DocumentReferenceBaseType">
 *       &lt;sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}LineNumber" minOccurs="0"/>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}ItemIDs" minOccurs="0"/>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}Facility" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}SerializedLot" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}SalesOrderReference" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}PurchaseOrderReference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}GroupName" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}SequenceCode" minOccurs="0"/>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}StepID" minOccurs="0"/>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}StepType" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}IssuingParty" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}OperationReference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}ReleaseNumber" minOccurs="0"/>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}ScheduleLineNumber" minOccurs="0"/>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}SubLineNumber" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}ShipUnitReference" minOccurs="0"/>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}EffectiveTimePeriod" minOccurs="0"/>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}Item" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}ID" minOccurs="0"/>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}SealID" minOccurs="0"/>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}Type" minOccurs="0"/>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}FreightItemID" minOccurs="0"/>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}ShippingTrackingID" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}RequestedQuantity" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}UserArea" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" type="{http://www.openapplications.org/oagis/9}TokenType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentReferenceType", propOrder = {
    "lineNumber",
    "itemIDs",
    "facility",
    "serializedLot",
    "salesOrderReference",
    "purchaseOrderReference",
    "groupName",
    "sequenceCode",
    "stepID",
    "stepType",
    "issuingParty",
    "operationReference",
    "releaseNumber",
    "scheduleLineNumber",
    "subLineNumber",
    "shipUnitReference",
    "effectiveTimePeriod",
    "item",
    "id",
    "sealID",
    "type",
    "freightItemID",
    "shippingTrackingID",
    "requestedQuantity",
    "userArea"
})
public class DocumentReferenceType
    extends DocumentReferenceBaseType
{

    @XmlElement(name = "LineNumber")
    protected com.volvo.group.purchaseorder.components._1_0.IdentifierType lineNumber;
    @XmlElement(name = "ItemIDs")
    protected ItemIDsType itemIDs;
    @XmlElement(name = "Facility")
    protected List<FacilityType> facility;
    @XmlElement(name = "SerializedLot")
    protected List<SerializedLotType> serializedLot;
    @XmlElement(name = "SalesOrderReference")
    protected List<OrderReferenceType> salesOrderReference;
    @XmlElement(name = "PurchaseOrderReference")
    protected List<OrderReferenceType> purchaseOrderReference;
    @XmlElement(name = "GroupName")
    protected List<NameType> groupName;
    @XmlElement(name = "SequenceCode")
    protected CodeType sequenceCode;
    @XmlElement(name = "StepID")
    protected com.volvo.group.purchaseorder.components._1_0.IdentifierType stepID;
    @XmlElement(name = "StepType")
    protected CodeType stepType;
    @XmlElement(name = "IssuingParty")
    protected SemanticPartyType issuingParty;
    @XmlElement(name = "OperationReference")
    protected List<OperationReferenceType> operationReference;
    @XmlElement(name = "ReleaseNumber")
    protected com.volvo.group.purchaseorder.components._1_0.IdentifierType releaseNumber;
    @XmlElement(name = "ScheduleLineNumber")
    protected com.volvo.group.purchaseorder.components._1_0.IdentifierType scheduleLineNumber;
    @XmlElement(name = "SubLineNumber")
    protected com.volvo.group.purchaseorder.components._1_0.IdentifierType subLineNumber;
    @XmlElement(name = "ShipUnitReference")
    protected ShipUnitReferenceType shipUnitReference;
    @XmlElement(name = "EffectiveTimePeriod")
    protected TimePeriodType effectiveTimePeriod;
    @XmlElement(name = "Item")
    protected ItemType item;
    @XmlElementRef(name = "ID", namespace = "http://www.openapplications.org/oagis/9", type = JAXBElement.class)
    protected JAXBElement<? extends com.volvo.group.purchaseorder.components._1_0.IdentifierType> id;
    @XmlElement(name = "SealID")
    protected com.volvo.group.purchaseorder.components._1_0.IdentifierType sealID;
    @XmlElement(name = "Type")
    protected CodeType type;
    @XmlElement(name = "FreightItemID")
    protected com.volvo.group.purchaseorder.components._1_0.IdentifierType freightItemID;
    @XmlElement(name = "ShippingTrackingID")
    protected com.volvo.group.purchaseorder.components._1_0.IdentifierType shippingTrackingID;
    @XmlElement(name = "RequestedQuantity")
    protected QuantityType requestedQuantity;
    @XmlElement(name = "UserArea")
    protected UserAreaType userArea;
    @XmlAttribute(name = "type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String typeAttr;

    /**
     * Gets the value of the lineNumber property.
     * 
     * @return
     *     possible object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public com.volvo.group.purchaseorder.components._1_0.IdentifierType getLineNumber() {
        return lineNumber;
    }

    /**
     * Sets the value of the lineNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public void setLineNumber(com.volvo.group.purchaseorder.components._1_0.IdentifierType value) {
        this.lineNumber = value;
    }

    /**
     * Gets the value of the itemIDs property.
     * 
     * @return
     *     possible object is
     *     {@link ItemIDsType }
     *     
     */
    public ItemIDsType getItemIDs() {
        return itemIDs;
    }

    /**
     * Sets the value of the itemIDs property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemIDsType }
     *     
     */
    public void setItemIDs(ItemIDsType value) {
        this.itemIDs = value;
    }

    /**
     * Gets the value of the facility property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the facility property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFacility().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FacilityType }
     * 
     * 
     */
    public List<FacilityType> getFacility() {
        if (facility == null) {
            facility = new ArrayList<FacilityType>();
        }
        return this.facility;
    }

    /**
     * Gets the value of the serializedLot property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the serializedLot property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSerializedLot().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SerializedLotType }
     * 
     * 
     */
    public List<SerializedLotType> getSerializedLot() {
        if (serializedLot == null) {
            serializedLot = new ArrayList<SerializedLotType>();
        }
        return this.serializedLot;
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
     * Gets the value of the purchaseOrderReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the purchaseOrderReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPurchaseOrderReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrderReferenceType }
     * 
     * 
     */
    public List<OrderReferenceType> getPurchaseOrderReference() {
        if (purchaseOrderReference == null) {
            purchaseOrderReference = new ArrayList<OrderReferenceType>();
        }
        return this.purchaseOrderReference;
    }

    /**
     * Gets the value of the groupName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groupName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroupName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NameType }
     * 
     * 
     */
    public List<NameType> getGroupName() {
        if (groupName == null) {
            groupName = new ArrayList<NameType>();
        }
        return this.groupName;
    }

    /**
     * Gets the value of the sequenceCode property.
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getSequenceCode() {
        return sequenceCode;
    }

    /**
     * Sets the value of the sequenceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setSequenceCode(CodeType value) {
        this.sequenceCode = value;
    }

    /**
     * Gets the value of the stepID property.
     * 
     * @return
     *     possible object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public com.volvo.group.purchaseorder.components._1_0.IdentifierType getStepID() {
        return stepID;
    }

    /**
     * Sets the value of the stepID property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public void setStepID(com.volvo.group.purchaseorder.components._1_0.IdentifierType value) {
        this.stepID = value;
    }

    /**
     * Gets the value of the stepType property.
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getStepType() {
        return stepType;
    }

    /**
     * Sets the value of the stepType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setStepType(CodeType value) {
        this.stepType = value;
    }

    /**
     * Identifies the Party that issued the invoice
     * 
     * @return
     *     possible object is
     *     {@link SemanticPartyType }
     *     
     */
    public SemanticPartyType getIssuingParty() {
        return issuingParty;
    }

    /**
     * Identifies the Party that issued the invoice
     * 
     * @param value
     *     allowed object is
     *     {@link SemanticPartyType }
     *     
     */
    public void setIssuingParty(SemanticPartyType value) {
        this.issuingParty = value;
    }

    /**
     * Gets the value of the operationReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the operationReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOperationReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OperationReferenceType }
     * 
     * 
     */
    public List<OperationReferenceType> getOperationReference() {
        if (operationReference == null) {
            operationReference = new ArrayList<OperationReferenceType>();
        }
        return this.operationReference;
    }

    /**
     * Gets the value of the releaseNumber property.
     * 
     * @return
     *     possible object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public com.volvo.group.purchaseorder.components._1_0.IdentifierType getReleaseNumber() {
        return releaseNumber;
    }

    /**
     * Sets the value of the releaseNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public void setReleaseNumber(com.volvo.group.purchaseorder.components._1_0.IdentifierType value) {
        this.releaseNumber = value;
    }

    /**
     * Gets the value of the scheduleLineNumber property.
     * 
     * @return
     *     possible object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public com.volvo.group.purchaseorder.components._1_0.IdentifierType getScheduleLineNumber() {
        return scheduleLineNumber;
    }

    /**
     * Sets the value of the scheduleLineNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public void setScheduleLineNumber(com.volvo.group.purchaseorder.components._1_0.IdentifierType value) {
        this.scheduleLineNumber = value;
    }

    /**
     * Gets the value of the subLineNumber property.
     * 
     * @return
     *     possible object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public com.volvo.group.purchaseorder.components._1_0.IdentifierType getSubLineNumber() {
        return subLineNumber;
    }

    /**
     * Sets the value of the subLineNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public void setSubLineNumber(com.volvo.group.purchaseorder.components._1_0.IdentifierType value) {
        this.subLineNumber = value;
    }

    /**
     * Gets the value of the shipUnitReference property.
     * 
     * @return
     *     possible object is
     *     {@link ShipUnitReferenceType }
     *     
     */
    public ShipUnitReferenceType getShipUnitReference() {
        return shipUnitReference;
    }

    /**
     * Sets the value of the shipUnitReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShipUnitReferenceType }
     *     
     */
    public void setShipUnitReference(ShipUnitReferenceType value) {
        this.shipUnitReference = value;
    }

    /**
     * Gets the value of the effectiveTimePeriod property.
     * 
     * @return
     *     possible object is
     *     {@link TimePeriodType }
     *     
     */
    public TimePeriodType getEffectiveTimePeriod() {
        return effectiveTimePeriod;
    }

    /**
     * Sets the value of the effectiveTimePeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimePeriodType }
     *     
     */
    public void setEffectiveTimePeriod(TimePeriodType value) {
        this.effectiveTimePeriod = value;
    }

    /**
     * Gets the value of the item property.
     * 
     * @return
     *     possible object is
     *     {@link ItemType }
     *     
     */
    public ItemType getItem() {
        return item;
    }

    /**
     * Sets the value of the item property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemType }
     *     
     */
    public void setItem(ItemType value) {
        this.item = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link com.volvo.group.purchaseorder._1_0.IdentifierType }{@code >}
     *     {@link JAXBElement }{@code <}{@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }{@code >}
     *     
     */
    public JAXBElement<? extends com.volvo.group.purchaseorder.components._1_0.IdentifierType> getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link com.volvo.group.purchaseorder._1_0.IdentifierType }{@code >}
     *     {@link JAXBElement }{@code <}{@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }{@code >}
     *     
     */
    public void setID(JAXBElement<? extends com.volvo.group.purchaseorder.components._1_0.IdentifierType> value) {
        this.id = ((JAXBElement<? extends com.volvo.group.purchaseorder.components._1_0.IdentifierType> ) value);
    }

    /**
     * Gets the value of the sealID property.
     * 
     * @return
     *     possible object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public com.volvo.group.purchaseorder.components._1_0.IdentifierType getSealID() {
        return sealID;
    }

    /**
     * Sets the value of the sealID property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public void setSealID(com.volvo.group.purchaseorder.components._1_0.IdentifierType value) {
        this.sealID = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setType(CodeType value) {
        this.type = value;
    }

    /**
     * Gets the value of the freightItemID property.
     * 
     * @return
     *     possible object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public com.volvo.group.purchaseorder.components._1_0.IdentifierType getFreightItemID() {
        return freightItemID;
    }

    /**
     * Sets the value of the freightItemID property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public void setFreightItemID(com.volvo.group.purchaseorder.components._1_0.IdentifierType value) {
        this.freightItemID = value;
    }

    /**
     * Gets the value of the shippingTrackingID property.
     * 
     * @return
     *     possible object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public com.volvo.group.purchaseorder.components._1_0.IdentifierType getShippingTrackingID() {
        return shippingTrackingID;
    }

    /**
     * Sets the value of the shippingTrackingID property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.volvo.group.purchaseorder.components._1_0.IdentifierType }
     *     
     */
    public void setShippingTrackingID(com.volvo.group.purchaseorder.components._1_0.IdentifierType value) {
        this.shippingTrackingID = value;
    }

    /**
     * Gets the value of the requestedQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getRequestedQuantity() {
        return requestedQuantity;
    }

    /**
     * Sets the value of the requestedQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setRequestedQuantity(QuantityType value) {
        this.requestedQuantity = value;
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

    /**
     * Gets the value of the typeAttr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypeAttr() {
        return typeAttr;
    }

    /**
     * Sets the value of the typeAttr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypeAttr(String value) {
        this.typeAttr = value;
    }

}
