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
 * <p>Java class for InventoryItemIDsBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InventoryItemIDsBaseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}ItemIDsType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.openapplications.org/oagis/9}ItemInstanceIDsGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}LotIDs" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InventoryItemIDsBaseType", propOrder = {
    "rfid",
    "serialNumber",
    "lotSerial",
    "lotIDs"
})
public class InventoryItemIDsBaseType
    extends ItemIDsType
{

    @XmlElement(name = "RFID")
    protected List<IdentifierType> rfid;
    @XmlElement(name = "SerialNumber")
    protected List<IdentifierType> serialNumber;
    @XmlElement(name = "LotSerial")
    protected List<LotSerialType> lotSerial;
    @XmlElement(name = "LotIDs")
    protected SequencedIDsType lotIDs;

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
     * Gets the value of the lotIDs property.
     * 
     * @return
     *     possible object is
     *     {@link SequencedIDsType }
     *     
     */
    public SequencedIDsType getLotIDs() {
        return lotIDs;
    }

    /**
     * Sets the value of the lotIDs property.
     * 
     * @param value
     *     allowed object is
     *     {@link SequencedIDsType }
     *     
     */
    public void setLotIDs(SequencedIDsType value) {
        this.lotIDs = value;
    }

}
