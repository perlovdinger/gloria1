//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:26 em CEST 
//


package com.volvo.group.purchaseorder._1_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import com.volvo.group.purchaseorder.components._1_0.IdentifierType;


/**
 * <p>Java class for SemanticContactType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SemanticContactType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}SemanticContactType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.volvo.com/3P/Purchasing/2008/10}SecurityId" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.volvo.com/3P/Purchasing/2008/10}PlantBuyer" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SemanticContactType", propOrder = {
    "securityId",
    "plantBuyer"
})
public class SemanticContactType
    extends com.volvo.group.purchaseorder.components._1_0.SemanticContactType
{

    @XmlElement(name = "SecurityId")
    protected List<IdentifierType> securityId;
    @XmlElementRef(name = "PlantBuyer", namespace = "http://www.volvo.com/3P/Purchasing/2008/10", type = JAXBElement.class)
    protected List<JAXBElement<String>> plantBuyer;

    /**
     * Gets the value of the securityId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the securityId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSecurityId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IdentifierType }
     * 
     * 
     */
    public List<IdentifierType> getSecurityId() {
        if (securityId == null) {
            securityId = new ArrayList<IdentifierType>();
        }
        return this.securityId;
    }

    /**
     * Gets the value of the plantBuyer property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the plantBuyer property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPlantBuyer().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * 
     */
    public List<JAXBElement<String>> getPlantBuyer() {
        if (plantBuyer == null) {
            plantBuyer = new ArrayList<JAXBElement<String>>();
        }
        return this.plantBuyer;
    }

}
