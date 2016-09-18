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
 * <p>Java class for AllocatedResourcesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AllocatedResourcesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}Allocation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}InventoryAllocation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}LabourAllocation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}MachineAllocation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}OperationAllocation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}ToolAllocation" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "AllocatedResourcesType", propOrder = {
    "allocation",
    "inventoryAllocation",
    "labourAllocation",
    "machineAllocation",
    "operationAllocation",
    "toolAllocation",
    "userArea"
})
public class AllocatedResourcesType {

    @XmlElement(name = "Allocation")
    protected List<AllocationType> allocation;
    @XmlElement(name = "InventoryAllocation")
    protected List<InventoryAllocationType> inventoryAllocation;
    @XmlElement(name = "LabourAllocation")
    protected List<LabourAllocationType> labourAllocation;
    @XmlElement(name = "MachineAllocation")
    protected List<MachineAllocationType> machineAllocation;
    @XmlElement(name = "OperationAllocation")
    protected List<OperationAllocationType> operationAllocation;
    @XmlElement(name = "ToolAllocation")
    protected List<ToolAllocationType> toolAllocation;
    @XmlElement(name = "UserArea")
    protected UserAreaType userArea;

    /**
     * Gets the value of the allocation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the allocation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAllocation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AllocationType }
     * 
     * 
     */
    public List<AllocationType> getAllocation() {
        if (allocation == null) {
            allocation = new ArrayList<AllocationType>();
        }
        return this.allocation;
    }

    /**
     * Gets the value of the inventoryAllocation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inventoryAllocation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInventoryAllocation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InventoryAllocationType }
     * 
     * 
     */
    public List<InventoryAllocationType> getInventoryAllocation() {
        if (inventoryAllocation == null) {
            inventoryAllocation = new ArrayList<InventoryAllocationType>();
        }
        return this.inventoryAllocation;
    }

    /**
     * Gets the value of the labourAllocation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the labourAllocation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLabourAllocation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LabourAllocationType }
     * 
     * 
     */
    public List<LabourAllocationType> getLabourAllocation() {
        if (labourAllocation == null) {
            labourAllocation = new ArrayList<LabourAllocationType>();
        }
        return this.labourAllocation;
    }

    /**
     * Gets the value of the machineAllocation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the machineAllocation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMachineAllocation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MachineAllocationType }
     * 
     * 
     */
    public List<MachineAllocationType> getMachineAllocation() {
        if (machineAllocation == null) {
            machineAllocation = new ArrayList<MachineAllocationType>();
        }
        return this.machineAllocation;
    }

    /**
     * Gets the value of the operationAllocation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the operationAllocation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOperationAllocation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OperationAllocationType }
     * 
     * 
     */
    public List<OperationAllocationType> getOperationAllocation() {
        if (operationAllocation == null) {
            operationAllocation = new ArrayList<OperationAllocationType>();
        }
        return this.operationAllocation;
    }

    /**
     * Gets the value of the toolAllocation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the toolAllocation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getToolAllocation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ToolAllocationType }
     * 
     * 
     */
    public List<ToolAllocationType> getToolAllocation() {
        if (toolAllocation == null) {
            toolAllocation = new ArrayList<ToolAllocationType>();
        }
        return this.toolAllocation;
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
