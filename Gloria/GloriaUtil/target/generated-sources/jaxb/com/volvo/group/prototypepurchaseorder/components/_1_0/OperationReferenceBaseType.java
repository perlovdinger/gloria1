//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:32 em CEST 
//


package com.volvo.group.prototypepurchaseorder.components._1_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OperationReferenceBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OperationReferenceBaseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}DocumentReferenceBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}GroupName" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}SequenceCode" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}StepID" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}StepType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OperationReferenceBaseType", propOrder = {
    "groupName",
    "sequenceCode",
    "stepID",
    "stepType"
})
public class OperationReferenceBaseType
    extends DocumentReferenceBaseType
{

    @XmlElement(name = "GroupName")
    protected List<NameType> groupName;
    @XmlElement(name = "SequenceCode")
    protected CodeType sequenceCode;
    @XmlElement(name = "StepID")
    protected IdentifierType stepID;
    @XmlElement(name = "StepType")
    protected CodeType stepType;

    /**
     * Gets the value of the groupName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groupName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroupName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NameType }
     * 
     * 
     */
    public List<NameType> getGroupName() {
        if (groupName == null) {
            groupName = new ArrayList<NameType>();
        }
        return this.groupName;
    }

    /**
     * Gets the value of the sequenceCode property.
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getSequenceCode() {
        return sequenceCode;
    }

    /**
     * Sets the value of the sequenceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setSequenceCode(CodeType value) {
        this.sequenceCode = value;
    }

    /**
     * Gets the value of the stepID property.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getStepID() {
        return stepID;
    }

    /**
     * Sets the value of the stepID property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setStepID(IdentifierType value) {
        this.stepID = value;
    }

    /**
     * Gets the value of the stepType property.
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getStepType() {
        return stepType;
    }

    /**
     * Sets the value of the stepType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setStepType(CodeType value) {
        this.stepType = value;
    }

}
