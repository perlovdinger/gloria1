//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:32 em CEST 
//


package com.volvo.group.prototypepurchaseorder._1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.volvo.group.prototypepurchaseorder.components._1_0.BusinessObjectDocumentType;


/**
 * <p>Java class for SyncStandAloneOrderType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SyncStandAloneOrderType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}BusinessObjectDocumentType">
 *       &lt;sequence>
 *         &lt;element name="DataArea" type="{http://www.volvo.com/3P/Purchasing/2008/10}SyncStandAloneOrderDataAreaType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SyncStandAloneOrderType", propOrder = {
    "dataArea"
})
public class SyncStandAloneOrderType
    extends BusinessObjectDocumentType
{

    @XmlElement(name = "DataArea", required = true)
    protected SyncStandAloneOrderDataAreaType dataArea;

    /**
     * Gets the value of the dataArea property.
     * 
     * @return
     *     possible object is
     *     {@link SyncStandAloneOrderDataAreaType }
     *     
     */
    public SyncStandAloneOrderDataAreaType getDataArea() {
        return dataArea;
    }

    /**
     * Sets the value of the dataArea property.
     * 
     * @param value
     *     allowed object is
     *     {@link SyncStandAloneOrderDataAreaType }
     *     
     */
    public void setDataArea(SyncStandAloneOrderDataAreaType value) {
        this.dataArea = value;
    }

}
