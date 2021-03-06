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
 * The basis for the ProcurementDocumet SubLine.
 * 
 * <p>Java class for RequestSubLineType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestSubLineType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}LineType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}Item" minOccurs="0"/>
 *         &lt;element name="Quantity" type="{http://www.openapplications.org/oagis/9}QuantityType" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}DrawingReference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}CatalogReference" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}ParentLineNumber" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestSubLineType", propOrder = {
    "item",
    "quantity",
    "drawingReference",
    "catalogReference",
    "parentLineNumber"
})
public class RequestSubLineType
    extends LineType
{

    @XmlElement(name = "Item")
    protected ItemType item;
    @XmlElement(name = "Quantity")
    protected QuantityType quantity;
    @XmlElement(name = "DrawingReference")
    protected List<SemanticDocumentReferenceType> drawingReference;
    @XmlElement(name = "CatalogReference")
    protected DocumentReferenceType catalogReference;
    @XmlElement(name = "ParentLineNumber")
    protected IdentifierType parentLineNumber;

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
     * Gets the value of the drawingReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the drawingReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDrawingReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SemanticDocumentReferenceType }
     * 
     * 
     */
    public List<SemanticDocumentReferenceType> getDrawingReference() {
        if (drawingReference == null) {
            drawingReference = new ArrayList<SemanticDocumentReferenceType>();
        }
        return this.drawingReference;
    }

    /**
     * Gets the value of the catalogReference property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentReferenceType }
     *     
     */
    public DocumentReferenceType getCatalogReference() {
        return catalogReference;
    }

    /**
     * Sets the value of the catalogReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentReferenceType }
     *     
     */
    public void setCatalogReference(DocumentReferenceType value) {
        this.catalogReference = value;
    }

    /**
     * Gets the value of the parentLineNumber property.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getParentLineNumber() {
        return parentLineNumber;
    }

    /**
     * Sets the value of the parentLineNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setParentLineNumber(IdentifierType value) {
        this.parentLineNumber = value;
    }

}
