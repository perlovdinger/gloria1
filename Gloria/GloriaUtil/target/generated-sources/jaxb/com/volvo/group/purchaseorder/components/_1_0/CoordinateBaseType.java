//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:26 em CEST 
//


package com.volvo.group.purchaseorder.components._1_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The OAGIS CoordinateType allows the use of another coordinate system for the use within a warehousing and inventory environment and the geographic coordinat system.
 * 
 * <p>Java class for CoordinateBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CoordinateBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}Latitude" minOccurs="0"/>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}Longitude" minOccurs="0"/>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}AltitudeMeasure" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}SystemID" minOccurs="0"/>
 *           &lt;element ref="{http://www.openapplications.org/oagis/9}CoordinateReference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/sequence>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoordinateBaseType", propOrder = {
    "latitude",
    "longitude",
    "altitudeMeasure",
    "systemID",
    "coordinateReference"
})
public class CoordinateBaseType {

    @XmlElement(name = "Latitude")
    protected PositionType latitude;
    @XmlElement(name = "Longitude")
    protected PositionType longitude;
    @XmlElement(name = "AltitudeMeasure")
    protected MeasureType altitudeMeasure;
    @XmlElement(name = "SystemID")
    protected IdentifierType systemID;
    @XmlElement(name = "CoordinateReference")
    protected List<SequencedIDsType> coordinateReference;

    /**
     * Gets the value of the latitude property.
     * 
     * @return
     *     possible object is
     *     {@link PositionType }
     *     
     */
    public PositionType getLatitude() {
        return latitude;
    }

    /**
     * Sets the value of the latitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link PositionType }
     *     
     */
    public void setLatitude(PositionType value) {
        this.latitude = value;
    }

    /**
     * Gets the value of the longitude property.
     * 
     * @return
     *     possible object is
     *     {@link PositionType }
     *     
     */
    public PositionType getLongitude() {
        return longitude;
    }

    /**
     * Sets the value of the longitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link PositionType }
     *     
     */
    public void setLongitude(PositionType value) {
        this.longitude = value;
    }

    /**
     * Gets the value of the altitudeMeasure property.
     * 
     * @return
     *     possible object is
     *     {@link MeasureType }
     *     
     */
    public MeasureType getAltitudeMeasure() {
        return altitudeMeasure;
    }

    /**
     * Sets the value of the altitudeMeasure property.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasureType }
     *     
     */
    public void setAltitudeMeasure(MeasureType value) {
        this.altitudeMeasure = value;
    }

    /**
     * Gets the value of the systemID property.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getSystemID() {
        return systemID;
    }

    /**
     * Sets the value of the systemID property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setSystemID(IdentifierType value) {
        this.systemID = value;
    }

    /**
     * Gets the value of the coordinateReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the coordinateReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCoordinateReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SequencedIDsType }
     * 
     * 
     */
    public List<SequencedIDsType> getCoordinateReference() {
        if (coordinateReference == null) {
            coordinateReference = new ArrayList<SequencedIDsType>();
        }
        return this.coordinateReference;
    }

}