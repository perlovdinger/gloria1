//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:26 em CEST 
//


package com.volvo.group.purchaseorder._1_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PackagingType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PackagingType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}PackagingType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.volvo.com/3P/Purchasing/2008/10}CommercialPackaging" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PackagingType", propOrder = {
    "commercialPackaging"
})
public class PackagingType
    extends com.volvo.group.purchaseorder.components._1_0.PackagingType
{

    @XmlElement(name = "CommercialPackaging")
    protected List<com.volvo.group.purchaseorder.components._1_0.PackagingType> commercialPackaging;

    /**
     * Gets the value of the commercialPackaging property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the commercialPackaging property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCommercialPackaging().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link com.volvo.group.purchaseorder.components._1_0.PackagingType }
     * 
     * 
     */
    public List<com.volvo.group.purchaseorder.components._1_0.PackagingType> getCommercialPackaging() {
        if (commercialPackaging == null) {
            commercialPackaging = new ArrayList<com.volvo.group.purchaseorder.components._1_0.PackagingType>();
        }
        return this.commercialPackaging;
    }

}
