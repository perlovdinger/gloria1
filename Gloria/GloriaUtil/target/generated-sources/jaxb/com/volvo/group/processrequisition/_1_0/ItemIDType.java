//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:29 em CEST 
//


package com.volvo.group.processrequisition._1_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ItemIDType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ItemIDType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}ItemIDType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.volvo.com/3P/Purchasing/2008/10}ID" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemIDType", propOrder = {
    "rest"
})
public class ItemIDType
    extends com.volvo.group.processrequisition.components._1_0.ItemIDType
{

    @XmlElementRef(name = "ID", namespace = "http://www.volvo.com/3P/Purchasing/2008/10", type = JAXBElement.class)
    protected List<JAXBElement<IdentifierType>> rest;

    /**
     * Gets the rest of the content model. 
     * 
     * <p>
     * You are getting this "catch-all" property because of the following reason: 
     * The field name "ID" is used by two different parts of a schema. See: 
     * line 123 of file:/C:/svn/gloria_trunc/4_implementationSet/Gloria/GloriaUtil/src/main/resources/xsd/purchaseorder/org_volvo_purchasing/3P/Common/Components/Components.xsd
     * line 3131 of file:/C:/svn/gloria_trunc/4_implementationSet/Gloria/GloriaUtil/src/main/resources/xsd/purchaseorder/org_openapplications_platform/1_0/Common/OAGi/Components/Components.xsd
     * <p>
     * To get rid of this property, apply a property customization to one 
     * of both of the following declarations to change their names: 
     * Gets the value of the rest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link IdentifierType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<IdentifierType>> getRest() {
        if (rest == null) {
            rest = new ArrayList<JAXBElement<IdentifierType>>();
        }
        return this.rest;
    }

}
