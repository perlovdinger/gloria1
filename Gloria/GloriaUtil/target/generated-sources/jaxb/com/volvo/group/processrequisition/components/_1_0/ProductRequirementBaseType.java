//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:29 em CEST 
//


package com.volvo.group.processrequisition.components._1_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProductRequirementBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProductRequirementBaseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}HeaderType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.openapplications.org/oagis/9}InventoryTransactionGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}ProductLineClassification" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}RequiredQuantity" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}RequiredDateTime" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}PurchaseOrderReference" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}SalesOrderReference" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}ProductionOrderReference" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductRequirementBaseType", propOrder = {
    "item",
    "serialNumber",
    "serializedLot",
    "uid",
    "rfid",
    "quantity",
    "glEntityID",
    "facility",
    "status",
    "reasonCode",
    "transactionDateTime",
    "productLineClassification",
    "requiredQuantity",
    "requiredDateTime",
    "purchaseOrderReference",
    "salesOrderReference",
    "productionOrderReference"
})
public class ProductRequirementBaseType
    extends HeaderType
{

    @XmlElement(name = "Item")
    protected ItemType item;
    @XmlElement(name = "SerialNumber")
    protected List<IdentifierType> serialNumber;
    @XmlElement(name = "SerializedLot")
    protected List<SerializedLotType> serializedLot;
    @XmlElement(name = "UID")
    protected List<IdentifierType> uid;
    @XmlElement(name = "RFID")
    protected List<IdentifierType> rfid;
    @XmlElement(name = "Quantity")
    protected QuantityType quantity;
    @XmlElement(name = "GLEntityID")
    protected IdentifierType glEntityID;
    @XmlElement(name = "Facility")
    protected List<FacilityType> facility;
    @XmlElement(name = "Status")
    protected StatusType status;
    @XmlElement(name = "ReasonCode")
    protected List<CodeType> reasonCode;
    @XmlElement(name = "TransactionDateTime")
    protected String transactionDateTime;
    @XmlElement(name = "ProductLineClassification")
    protected SemanticClassificationType productLineClassification;
    @XmlElement(name = "RequiredQuantity")
    protected QuantityType requiredQuantity;
    @XmlElement(name = "RequiredDateTime")
    protected String requiredDateTime;
    @XmlElement(name = "PurchaseOrderReference")
    protected OrderReferenceType purchaseOrderReference;
    @XmlElement(name = "SalesOrderReference")
    protected OrderReferenceType salesOrderReference;
    @XmlElement(name = "ProductionOrderReference")
    protected ProductionOrderReferenceType productionOrderReference;

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
     * Gets the value of the serialNumber property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the serialNumber property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSerialNumber().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IdentifierType }
     * 
     * 
     */
    public List<IdentifierType> getSerialNumber() {
        if (serialNumber == null) {
            serialNumber = new ArrayList<IdentifierType>();
        }
        return this.serialNumber;
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
     * Gets the value of the uid property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the uid property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IdentifierType }
     * 
     * 
     */
    public List<IdentifierType> getUID() {
        if (uid == null) {
            uid = new ArrayList<IdentifierType>();
        }
        return this.uid;
    }

    /**
     * Gets the value of the rfid property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rfid property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRFID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IdentifierType }
     * 
     * 
     */
    public List<IdentifierType> getRFID() {
        if (rfid == null) {
            rfid = new ArrayList<IdentifierType>();
        }
        return this.rfid;
    }

    /**
     * Gets the value of the quantity property.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setQuantity(QuantityType value) {
        this.quantity = value;
    }

    /**
     * Idenitifies GLEntity owns the item and takes the credit or debit for the item on the balance sheet.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getGLEntityID() {
        return glEntityID;
    }

    /**
     * Idenitifies GLEntity owns the item and takes the credit or debit for the item on the balance sheet.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setGLEntityID(IdentifierType value) {
        this.glEntityID = value;
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
     * This determines the receipt routing of an item or part.
     * 
     * Examples of values include:
     * Customer consignment, Inspection, Vendor consignment, Blocked
     * Bonded, User defined
     * 
     * Possible synonyms are 
     * Receipt Routing, Material Status, Material Condition Code
     * 
     * @return
     *     possible object is
     *     {@link StatusType }
     *     
     */
    public StatusType getStatus() {
        return status;
    }

    /**
     * This determines the receipt routing of an item or part.
     * 
     * Examples of values include:
     * Customer consignment, Inspection, Vendor consignment, Blocked
     * Bonded, User defined
     * 
     * Possible synonyms are 
     * Receipt Routing, Material Status, Material Condition Code
     * 
     * @param value
     *     allowed object is
     *     {@link StatusType }
     *     
     */
    public void setStatus(StatusType value) {
        this.status = value;
    }

    /**
     * Identifies the reason for the transaction.Gets the value of the reasonCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reasonCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReasonCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CodeType }
     * 
     * 
     */
    public List<CodeType> getReasonCode() {
        if (reasonCode == null) {
            reasonCode = new ArrayList<CodeType>();
        }
        return this.reasonCode;
    }

    /**
     * Identifies the DateTime  at which the inventory transaction was carried out
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionDateTime() {
        return transactionDateTime;
    }

    /**
     * Identifies the DateTime  at which the inventory transaction was carried out
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionDateTime(String value) {
        this.transactionDateTime = value;
    }

    /**
     * This is a grouping used to represent something that is sold either internally or externally.  It typically is a grouping of Items.
     * 
     * SYNONYMS:
     * Commodity
     * Product Family
     * Kit
     * 
     * @return
     *     possible object is
     *     {@link SemanticClassificationType }
     *     
     */
    public SemanticClassificationType getProductLineClassification() {
        return productLineClassification;
    }

    /**
     * This is a grouping used to represent something that is sold either internally or externally.  It typically is a grouping of Items.
     * 
     * SYNONYMS:
     * Commodity
     * Product Family
     * Kit
     * 
     * @param value
     *     allowed object is
     *     {@link SemanticClassificationType }
     *     
     */
    public void setProductLineClassification(SemanticClassificationType value) {
        this.productLineClassification = value;
    }

    /**
     * The required quantity of items
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getRequiredQuantity() {
        return requiredQuantity;
    }

    /**
     * The required quantity of items
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setRequiredQuantity(QuantityType value) {
        this.requiredQuantity = value;
    }

    /**
     * The timestamp that the item is required.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequiredDateTime() {
        return requiredDateTime;
    }

    /**
     * The timestamp that the item is required.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequiredDateTime(String value) {
        this.requiredDateTime = value;
    }

    /**
     * Gets the value of the purchaseOrderReference property.
     * 
     * @return
     *     possible object is
     *     {@link OrderReferenceType }
     *     
     */
    public OrderReferenceType getPurchaseOrderReference() {
        return purchaseOrderReference;
    }

    /**
     * Sets the value of the purchaseOrderReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderReferenceType }
     *     
     */
    public void setPurchaseOrderReference(OrderReferenceType value) {
        this.purchaseOrderReference = value;
    }

    /**
     * Gets the value of the salesOrderReference property.
     * 
     * @return
     *     possible object is
     *     {@link OrderReferenceType }
     *     
     */
    public OrderReferenceType getSalesOrderReference() {
        return salesOrderReference;
    }

    /**
     * Sets the value of the salesOrderReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderReferenceType }
     *     
     */
    public void setSalesOrderReference(OrderReferenceType value) {
        this.salesOrderReference = value;
    }

    /**
     * Gets the value of the productionOrderReference property.
     * 
     * @return
     *     possible object is
     *     {@link ProductionOrderReferenceType }
     *     
     */
    public ProductionOrderReferenceType getProductionOrderReference() {
        return productionOrderReference;
    }

    /**
     * Sets the value of the productionOrderReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductionOrderReferenceType }
     *     
     */
    public void setProductionOrderReference(ProductionOrderReferenceType value) {
        this.productionOrderReference = value;
    }

}
