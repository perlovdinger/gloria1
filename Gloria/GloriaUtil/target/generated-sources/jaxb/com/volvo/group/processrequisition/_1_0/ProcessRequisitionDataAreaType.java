//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:29 em CEST 
//


package com.volvo.group.processrequisition._1_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.volvo.group.processrequisition.components._1_0.ProcessType;


/**
 * <p>Java class for ProcessRequisitionDataAreaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProcessRequisitionDataAreaType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}Process"/>
 *         &lt;element ref="{http://www.volvo.com/3P/Purchasing/2008/10}Requisition" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessRequisitionDataAreaType", propOrder = {
    "process",
    "requisition"
})
public class ProcessRequisitionDataAreaType {

    @XmlElement(name = "Process", namespace = "http://www.openapplications.org/oagis/9", required = true)
    protected ProcessType process;
    @XmlElement(name = "Requisition", required = true)
    protected List<RequisitionType> requisition;

    /**
     * Gets the value of the process property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessType }
     *     
     */
    public ProcessType getProcess() {
        return process;
    }

    /**
     * Sets the value of the process property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessType }
     *     
     */
    public void setProcess(ProcessType value) {
        this.process = value;
    }

    /**
     * Gets the value of the requisition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requisition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequisition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RequisitionType }
     * 
     * 
     */
    public List<RequisitionType> getRequisition() {
        if (requisition == null) {
            requisition = new ArrayList<RequisitionType>();
        }
        return this.requisition;
    }

}
