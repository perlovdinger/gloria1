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
 * <p>Java class for SegmentResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SegmentResponseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}SegmentResponseBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}ProductionData" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}PersonnelActual" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}EquipmentActual" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}MaterialActual" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "SegmentResponseType", propOrder = {
    "productionData",
    "personnelActual",
    "equipmentActual",
    "materialActual",
    "userArea"
})
public class SegmentResponseType
    extends SegmentResponseBaseType
{

    @XmlElement(name = "ProductionData")
    protected List<ProductionDataType> productionData;
    @XmlElement(name = "PersonnelActual")
    protected List<ManufacturingPersonnelType> personnelActual;
    @XmlElement(name = "EquipmentActual")
    protected List<EquipmentType> equipmentActual;
    @XmlElement(name = "MaterialActual")
    protected List<ManufacturingMaterialType> materialActual;
    @XmlElement(name = "UserArea")
    protected UserAreaType userArea;

    /**
     * Gets the value of the productionData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the productionData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductionData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProductionDataType }
     * 
     * 
     */
    public List<ProductionDataType> getProductionData() {
        if (productionData == null) {
            productionData = new ArrayList<ProductionDataType>();
        }
        return this.productionData;
    }

    /**
     * Gets the value of the personnelActual property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the personnelActual property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPersonnelActual().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManufacturingPersonnelType }
     * 
     * 
     */
    public List<ManufacturingPersonnelType> getPersonnelActual() {
        if (personnelActual == null) {
            personnelActual = new ArrayList<ManufacturingPersonnelType>();
        }
        return this.personnelActual;
    }

    /**
     * Gets the value of the equipmentActual property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the equipmentActual property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEquipmentActual().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EquipmentType }
     * 
     * 
     */
    public List<EquipmentType> getEquipmentActual() {
        if (equipmentActual == null) {
            equipmentActual = new ArrayList<EquipmentType>();
        }
        return this.equipmentActual;
    }

    /**
     * Gets the value of the materialActual property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the materialActual property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMaterialActual().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManufacturingMaterialType }
     * 
     * 
     */
    public List<ManufacturingMaterialType> getMaterialActual() {
        if (materialActual == null) {
            materialActual = new ArrayList<ManufacturingMaterialType>();
        }
        return this.materialActual;
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