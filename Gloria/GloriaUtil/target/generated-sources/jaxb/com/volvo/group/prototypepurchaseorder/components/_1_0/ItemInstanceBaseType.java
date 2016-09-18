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
 * Extends the ItemBaseType to identify a set of Items along with their SerialNumbers and/or Lot information.
 * 
 * <p>Java class for ItemInstanceBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ItemInstanceBaseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}ItemBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}UID" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}RFID" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}SerialNumber" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}LotSerial" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}SerializedLot" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}Quantity" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemInstanceBaseType", propOrder = {
    "uid",
    "rfid",
    "serialNumber",
    "lotSerial",
    "serializedLot",
    "quantity"
})
public class ItemInstanceBaseType
    extends ItemBaseType
{

    @XmlElement(name = "UID")
    protected List<IdentifierType> uid;
    @XmlElement(name = "RFID")
    protected List<IdentifierType> rfid;
    @XmlElement(name = "SerialNumber")
    protected List<IdentifierType> serialNumber;
    @XmlElement(name = "LotSerial")
    protected List<LotSerialType> lotSerial;
    @XmlElement(name = "SerializedLot")
    protected List<SerializedLotType> serializedLot;
    @XmlElement(name = "Quantity")
    protected QuantityType quantity;

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
     * Gets the value of the lotSerial property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lotSerial property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLotSerial().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LotSerialType }
     * 
     * 
     */
    public List<LotSerialType> getLotSerial() {
        if (lotSerial == null) {
            lotSerial = new ArrayList<LotSerialType>();
        }
        return this.lotSerial;
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

}
