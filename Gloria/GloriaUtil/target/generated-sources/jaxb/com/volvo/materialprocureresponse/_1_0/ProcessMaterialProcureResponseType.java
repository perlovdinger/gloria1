//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:36 em CEST 
//


package com.volvo.materialprocureresponse._1_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.volvo.group.common._1_0.BODType;


/**
 * <p>Java class for ProcessMaterialProcureResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProcessMaterialProcureResponseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.volvo.com/group/common/1_0}BODType">
 *       &lt;sequence>
 *         &lt;element name="DataArea">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="MaterialProcureResponse" type="{http://www.volvo.com/MaterialProcureResponse/1_0}MaterialProcureResponseType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessMaterialProcureResponseType", propOrder = {
    "dataArea"
})
public class ProcessMaterialProcureResponseType
    extends BODType
{

    @XmlElement(name = "DataArea", required = true)
    protected ProcessMaterialProcureResponseType.DataArea dataArea;

    /**
     * Gets the value of the dataArea property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessMaterialProcureResponseType.DataArea }
     *     
     */
    public ProcessMaterialProcureResponseType.DataArea getDataArea() {
        return dataArea;
    }

    /**
     * Sets the value of the dataArea property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessMaterialProcureResponseType.DataArea }
     *     
     */
    public void setDataArea(ProcessMaterialProcureResponseType.DataArea value) {
        this.dataArea = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="MaterialProcureResponse" type="{http://www.volvo.com/MaterialProcureResponse/1_0}MaterialProcureResponseType" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "materialProcureResponse"
    })
    public static class DataArea {

        @XmlElement(name = "MaterialProcureResponse", required = true)
        protected List<MaterialProcureResponseType> materialProcureResponse;

        /**
         * Gets the value of the materialProcureResponse property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the materialProcureResponse property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getMaterialProcureResponse().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link MaterialProcureResponseType }
         * 
         * 
         */
        public List<MaterialProcureResponseType> getMaterialProcureResponse() {
            if (materialProcureResponse == null) {
                materialProcureResponse = new ArrayList<MaterialProcureResponseType>();
            }
            return this.materialProcureResponse;
        }

    }

}
