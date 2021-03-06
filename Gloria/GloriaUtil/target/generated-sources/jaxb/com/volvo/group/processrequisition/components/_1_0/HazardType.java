//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:29 em CEST 
//


package com.volvo.group.processrequisition.components._1_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * Definition of a Hazard 
 * 
 * <p>Java class for HazardType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HazardType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}ID" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}Type" minOccurs="0"/>
 *         &lt;group ref="{http://www.openapplications.org/oagis/9}FreeFormTextGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}Precaution" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "HazardType", propOrder = {
    "id",
    "type",
    "description",
    "note",
    "precaution",
    "userArea"
})
public class HazardType {

    @XmlElementRef(name = "ID", namespace = "http://www.openapplications.org/oagis/9", type = JAXBElement.class)
    protected JAXBElement<? extends com.volvo.group.processrequisition.components._1_0.IdentifierType> id;
    @XmlElement(name = "Type")
    protected CodeType type;
    @XmlElement(name = "Description")
    protected List<DescriptionType> description;
    @XmlElement(name = "Note")
    protected List<NoteType> note;
    @XmlElement(name = "Precaution")
    protected List<PrecautionType> precaution;
    @XmlElement(name = "UserArea")
    protected UserAreaType userArea;

    /**
     * Is the identifier of the Hazard
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link com.volvo.group.processrequisition.components._1_0.IdentifierType }{@code >}
     *     {@link JAXBElement }{@code <}{@link com.volvo.group.processrequisition._1_0.IdentifierType }{@code >}
     *     
     */
    public JAXBElement<? extends com.volvo.group.processrequisition.components._1_0.IdentifierType> getID() {
        return id;
    }

    /**
     * Is the identifier of the Hazard
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link com.volvo.group.processrequisition.components._1_0.IdentifierType }{@code >}
     *     {@link JAXBElement }{@code <}{@link com.volvo.group.processrequisition._1_0.IdentifierType }{@code >}
     *     
     */
    public void setID(JAXBElement<? extends com.volvo.group.processrequisition.components._1_0.IdentifierType> value) {
        this.id = ((JAXBElement<? extends com.volvo.group.processrequisition.components._1_0.IdentifierType> ) value);
    }

    /**
     * This is a grouping of the Hazard Id into different types
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
     * This is a grouping of the Hazard Id into different types
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
     * Gets the value of the precaution property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the precaution property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrecaution().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PrecautionType }
     * 
     * 
     */
    public List<PrecautionType> getPrecaution() {
        if (precaution == null) {
            precaution = new ArrayList<PrecautionType>();
        }
        return this.precaution;
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
