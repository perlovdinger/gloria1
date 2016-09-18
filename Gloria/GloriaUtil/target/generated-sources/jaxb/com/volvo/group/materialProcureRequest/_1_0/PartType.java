//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:36 em CEST 
//


package com.volvo.group.materialProcureRequest._1_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * KOLA Part attributes
 * 
 * <p>Java class for PartType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PartType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="UnitOfMeasure" type="{http://www.volvo.com/MaterialProcureRequest/1_0}UnitOfMeasureType"/>
 *         &lt;element name="DesignResponsible" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FunctionGroup" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ObjectNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Demarcation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Characteristics" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Alias" type="{http://www.volvo.com/MaterialProcureRequest/1_0}AliasType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartType", propOrder = {
    "name",
    "unitOfMeasure",
    "designResponsible",
    "functionGroup",
    "objectNumber",
    "demarcation",
    "characteristics",
    "alias"
})
public class PartType {

    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "UnitOfMeasure", required = true)
    protected UnitOfMeasureType unitOfMeasure;
    @XmlElement(name = "DesignResponsible")
    protected String designResponsible;
    @XmlElement(name = "FunctionGroup")
    protected String functionGroup;
    @XmlElement(name = "ObjectNumber")
    protected String objectNumber;
    @XmlElement(name = "Demarcation")
    protected String demarcation;
    @XmlElement(name = "Characteristics")
    protected String characteristics;
    @XmlElement(name = "Alias")
    protected List<AliasType> alias;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the unitOfMeasure property.
     * 
     * @return
     *     possible object is
     *     {@link UnitOfMeasureType }
     *     
     */
    public UnitOfMeasureType getUnitOfMeasure() {
        return unitOfMeasure;
    }

    /**
     * Sets the value of the unitOfMeasure property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnitOfMeasureType }
     *     
     */
    public void setUnitOfMeasure(UnitOfMeasureType value) {
        this.unitOfMeasure = value;
    }

    /**
     * Gets the value of the designResponsible property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDesignResponsible() {
        return designResponsible;
    }

    /**
     * Sets the value of the designResponsible property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDesignResponsible(String value) {
        this.designResponsible = value;
    }

    /**
     * Gets the value of the functionGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFunctionGroup() {
        return functionGroup;
    }

    /**
     * Sets the value of the functionGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFunctionGroup(String value) {
        this.functionGroup = value;
    }

    /**
     * Gets the value of the objectNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectNumber() {
        return objectNumber;
    }

    /**
     * Sets the value of the objectNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjectNumber(String value) {
        this.objectNumber = value;
    }

    /**
     * Gets the value of the demarcation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDemarcation() {
        return demarcation;
    }

    /**
     * Sets the value of the demarcation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDemarcation(String value) {
        this.demarcation = value;
    }

    /**
     * Gets the value of the characteristics property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCharacteristics() {
        return characteristics;
    }

    /**
     * Sets the value of the characteristics property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCharacteristics(String value) {
        this.characteristics = value;
    }

    /**
     * Gets the value of the alias property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the alias property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlias().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AliasType }
     * 
     * 
     */
    public List<AliasType> getAlias() {
        if (alias == null) {
            alias = new ArrayList<AliasType>();
        }
        return this.alias;
    }

}
