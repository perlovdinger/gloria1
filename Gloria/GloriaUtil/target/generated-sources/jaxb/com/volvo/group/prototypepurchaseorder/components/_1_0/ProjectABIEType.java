//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:32 em CEST 
//


package com.volvo.group.prototypepurchaseorder.components._1_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * ProjectABIEType is logically derived from UN/CEFACT TBG17 ABIE ProjectType as defined in the Reusable Aggregate Business Information Entity (RUABIE) XML Schema file.
 * 
 * 
 * 
 * <p>Java class for ProjectABIEType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProjectABIEType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.openapplications.org/oagis/9}IDsGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}ResourceCodes" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}Description" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}Amount" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}Location" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}TimePeriod" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" type="{http://www.openapplications.org/oagis/9}NormalizedStringType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProjectABIEType", propOrder = {
    "id",
    "resourceCodes",
    "description",
    "amount",
    "location",
    "timePeriod"
})
public abstract class ProjectABIEType {

    @XmlElementRef(name = "ID", namespace = "http://www.openapplications.org/oagis/9", type = JAXBElement.class)
    protected List<JAXBElement<? extends com.volvo.group.prototypepurchaseorder.components._1_0.IdentifierType>> id;
    @XmlElement(name = "ResourceCodes")
    protected SequencedCodesType resourceCodes;
    @XmlElement(name = "Description")
    protected List<DescriptionType> description;
    @XmlElement(name = "Amount")
    protected List<AmountType> amount;
    @XmlElement(name = "Location")
    protected List<LocationType> location;
    @XmlElement(name = "TimePeriod")
    protected TimePeriodType timePeriod;
    @XmlAttribute
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String type;

    /**
     * Gets the value of the id property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the id property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link com.volvo.group.prototypepurchaseorder._1_0.IdentifierType }{@code >}
     * {@link JAXBElement }{@code <}{@link com.volvo.group.prototypepurchaseorder.components._1_0.IdentifierType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends com.volvo.group.prototypepurchaseorder.components._1_0.IdentifierType>> getID() {
        if (id == null) {
            id = new ArrayList<JAXBElement<? extends com.volvo.group.prototypepurchaseorder.components._1_0.IdentifierType>>();
        }
        return this.id;
    }

    /**
     * Gets the value of the resourceCodes property.
     * 
     * @return
     *     possible object is
     *     {@link SequencedCodesType }
     *     
     */
    public SequencedCodesType getResourceCodes() {
        return resourceCodes;
    }

    /**
     * Sets the value of the resourceCodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link SequencedCodesType }
     *     
     */
    public void setResourceCodes(SequencedCodesType value) {
        this.resourceCodes = value;
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
     * Gets the value of the amount property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the amount property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAmount().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AmountType }
     * 
     * 
     */
    public List<AmountType> getAmount() {
        if (amount == null) {
            amount = new ArrayList<AmountType>();
        }
        return this.amount;
    }

    /**
     * Gets the value of the location property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the location property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LocationType }
     * 
     * 
     */
    public List<LocationType> getLocation() {
        if (location == null) {
            location = new ArrayList<LocationType>();
        }
        return this.location;
    }

    /**
     * Gets the value of the timePeriod property.
     * 
     * @return
     *     possible object is
     *     {@link TimePeriodType }
     *     
     */
    public TimePeriodType getTimePeriod() {
        return timePeriod;
    }

    /**
     * Sets the value of the timePeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimePeriodType }
     *     
     */
    public void setTimePeriod(TimePeriodType value) {
        this.timePeriod = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

}
