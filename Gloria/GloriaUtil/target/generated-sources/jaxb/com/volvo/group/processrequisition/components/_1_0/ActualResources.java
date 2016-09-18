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
 * <p>Java class for ActualResources complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ActualResources">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}Actual" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}InventoryActual" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}LabourActual" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}MachineActual" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}OperationActual" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}ToolActual" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActualResources", propOrder = {
    "actual",
    "inventoryActual",
    "labourActual",
    "machineActual",
    "operationActual",
    "toolActual"
})
public class ActualResources {

    @XmlElement(name = "Actual")
    protected List<ActualType> actual;
    @XmlElement(name = "InventoryActual")
    protected List<InventoryActualType> inventoryActual;
    @XmlElement(name = "LabourActual")
    protected List<LabourActualType> labourActual;
    @XmlElement(name = "MachineActual")
    protected List<MachineActualType> machineActual;
    @XmlElement(name = "OperationActual")
    protected List<OperationActualType> operationActual;
    @XmlElement(name = "ToolActual")
    protected List<ToolActualType> toolActual;

    /**
     * Gets the value of the actual property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the actual property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActual().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ActualType }
     * 
     * 
     */
    public List<ActualType> getActual() {
        if (actual == null) {
            actual = new ArrayList<ActualType>();
        }
        return this.actual;
    }

    /**
     * Gets the value of the inventoryActual property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inventoryActual property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInventoryActual().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InventoryActualType }
     * 
     * 
     */
    public List<InventoryActualType> getInventoryActual() {
        if (inventoryActual == null) {
            inventoryActual = new ArrayList<InventoryActualType>();
        }
        return this.inventoryActual;
    }

    /**
     * Gets the value of the labourActual property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the labourActual property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLabourActual().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LabourActualType }
     * 
     * 
     */
    public List<LabourActualType> getLabourActual() {
        if (labourActual == null) {
            labourActual = new ArrayList<LabourActualType>();
        }
        return this.labourActual;
    }

    /**
     * Gets the value of the machineActual property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the machineActual property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMachineActual().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MachineActualType }
     * 
     * 
     */
    public List<MachineActualType> getMachineActual() {
        if (machineActual == null) {
            machineActual = new ArrayList<MachineActualType>();
        }
        return this.machineActual;
    }

    /**
     * Gets the value of the operationActual property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the operationActual property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOperationActual().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OperationActualType }
     * 
     * 
     */
    public List<OperationActualType> getOperationActual() {
        if (operationActual == null) {
            operationActual = new ArrayList<OperationActualType>();
        }
        return this.operationActual;
    }

    /**
     * Gets the value of the toolActual property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the toolActual property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getToolActual().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ToolActualType }
     * 
     * 
     */
    public List<ToolActualType> getToolActual() {
        if (toolActual == null) {
            toolActual = new ArrayList<ToolActualType>();
        }
        return this.toolActual;
    }

}