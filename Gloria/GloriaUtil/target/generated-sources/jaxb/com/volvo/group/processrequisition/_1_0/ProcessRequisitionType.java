//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:29 em CEST 
//


package com.volvo.group.processrequisition._1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.volvo.group.processrequisition.components._1_0.BusinessObjectDocumentType;


/**
 * <p>Java class for ProcessRequisitionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProcessRequisitionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}BusinessObjectDocumentType">
 *       &lt;sequence>
 *         &lt;element name="SendingApplication" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="DataArea" type="{http://www.volvo.com/3P/Purchasing/2008/10}ProcessRequisitionDataAreaType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessRequisitionType", propOrder = {
    "sendingApplication",
    "dataArea"
})
public class ProcessRequisitionType
    extends BusinessObjectDocumentType
{

    @XmlElement(name = "SendingApplication", required = true)
    protected Object sendingApplication;
    @XmlElement(name = "DataArea", required = true)
    protected ProcessRequisitionDataAreaType dataArea;

    /**
     * Gets the value of the sendingApplication property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getSendingApplication() {
        return sendingApplication;
    }

    /**
     * Sets the value of the sendingApplication property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setSendingApplication(Object value) {
        this.sendingApplication = value;
    }

    /**
     * Gets the value of the dataArea property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessRequisitionDataAreaType }
     *     
     */
    public ProcessRequisitionDataAreaType getDataArea() {
        return dataArea;
    }

    /**
     * Sets the value of the dataArea property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessRequisitionDataAreaType }
     *     
     */
    public void setDataArea(ProcessRequisitionDataAreaType value) {
        this.dataArea = value;
    }

}
