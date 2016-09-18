//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:38 em CEST 
//


package com.volvo.group.init.material._1_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element ref="{}MaterialProcure"/>
 *         &lt;element ref="{}MaterialLine" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="buildStartDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="changeRequestId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="changeRequestType" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="contactDepartment" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="contactPersonId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="contactPersonName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="materialType" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="migrated" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="outBoundStartDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="outboundLocationId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="outboundLocationName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="phase" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="phasePlannedAssemblyEndDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="phasePlannedAssemblyStartDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="phasePlannedDeliveryStartDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="phaseType" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="quantity" use="required" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *       &lt;attribute name="receiver" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="referenceGroup" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="referenceId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="requesterName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="requesterUserId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="requiredStaDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="unitOfMeasure" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "materialProcure",
    "materialLine"
})
@XmlRootElement(name = "Material")
public class Material {

    @XmlElement(name = "MaterialProcure", required = true)
    protected MaterialProcure materialProcure;
    @XmlElement(name = "MaterialLine", required = true)
    protected List<MaterialLine> materialLine;
    @XmlAttribute(required = true)
    protected XMLGregorianCalendar buildStartDate;
    @XmlAttribute(required = true)
    protected String changeRequestId;
    @XmlAttribute(required = true)
    protected String changeRequestType;
    @XmlAttribute(required = true)
    protected String contactDepartment;
    @XmlAttribute(required = true)
    protected String contactPersonId;
    @XmlAttribute(required = true)
    protected String contactPersonName;
    @XmlAttribute(required = true)
    protected String materialType;
    @XmlAttribute(required = true)
    protected boolean migrated;
    @XmlAttribute(required = true)
    protected XMLGregorianCalendar outBoundStartDate;
    @XmlAttribute(required = true)
    protected String outboundLocationId;
    @XmlAttribute(required = true)
    protected String outboundLocationName;
    @XmlAttribute(required = true)
    protected String phase;
    @XmlAttribute(required = true)
    protected XMLGregorianCalendar phasePlannedAssemblyEndDate;
    @XmlAttribute(required = true)
    protected XMLGregorianCalendar phasePlannedAssemblyStartDate;
    @XmlAttribute(required = true)
    protected XMLGregorianCalendar phasePlannedDeliveryStartDate;
    @XmlAttribute(required = true)
    protected String phaseType;
    @XmlAttribute(required = true)
    protected byte quantity;
    @XmlAttribute(required = true)
    protected String receiver;
    @XmlAttribute(required = true)
    protected String referenceGroup;
    @XmlAttribute(required = true)
    protected String referenceId;
    @XmlAttribute(required = true)
    protected String requesterName;
    @XmlAttribute
    protected String requesterUserId;
    @XmlAttribute(required = true)
    protected XMLGregorianCalendar requiredStaDate;
    @XmlAttribute(required = true)
    protected String unitOfMeasure;

    /**
     * Gets the value of the materialProcure property.
     * 
     * @return
     *     possible object is
     *     {@link MaterialProcure }
     *     
     */
    public MaterialProcure getMaterialProcure() {
        return materialProcure;
    }

    /**
     * Sets the value of the materialProcure property.
     * 
     * @param value
     *     allowed object is
     *     {@link MaterialProcure }
     *     
     */
    public void setMaterialProcure(MaterialProcure value) {
        this.materialProcure = value;
    }

    /**
     * Gets the value of the materialLine property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the materialLine property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMaterialLine().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MaterialLine }
     * 
     * 
     */
    public List<MaterialLine> getMaterialLine() {
        if (materialLine == null) {
            materialLine = new ArrayList<MaterialLine>();
        }
        return this.materialLine;
    }

    /**
     * Gets the value of the buildStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBuildStartDate() {
        return buildStartDate;
    }

    /**
     * Sets the value of the buildStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBuildStartDate(XMLGregorianCalendar value) {
        this.buildStartDate = value;
    }

    /**
     * Gets the value of the changeRequestId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChangeRequestId() {
        return changeRequestId;
    }

    /**
     * Sets the value of the changeRequestId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChangeRequestId(String value) {
        this.changeRequestId = value;
    }

    /**
     * Gets the value of the changeRequestType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChangeRequestType() {
        return changeRequestType;
    }

    /**
     * Sets the value of the changeRequestType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChangeRequestType(String value) {
        this.changeRequestType = value;
    }

    /**
     * Gets the value of the contactDepartment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactDepartment() {
        return contactDepartment;
    }

    /**
     * Sets the value of the contactDepartment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactDepartment(String value) {
        this.contactDepartment = value;
    }

    /**
     * Gets the value of the contactPersonId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactPersonId() {
        return contactPersonId;
    }

    /**
     * Sets the value of the contactPersonId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactPersonId(String value) {
        this.contactPersonId = value;
    }

    /**
     * Gets the value of the contactPersonName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactPersonName() {
        return contactPersonName;
    }

    /**
     * Sets the value of the contactPersonName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactPersonName(String value) {
        this.contactPersonName = value;
    }

    /**
     * Gets the value of the materialType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaterialType() {
        return materialType;
    }

    /**
     * Sets the value of the materialType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaterialType(String value) {
        this.materialType = value;
    }

    /**
     * Gets the value of the migrated property.
     * 
     */
    public boolean isMigrated() {
        return migrated;
    }

    /**
     * Sets the value of the migrated property.
     * 
     */
    public void setMigrated(boolean value) {
        this.migrated = value;
    }

    /**
     * Gets the value of the outBoundStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOutBoundStartDate() {
        return outBoundStartDate;
    }

    /**
     * Sets the value of the outBoundStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOutBoundStartDate(XMLGregorianCalendar value) {
        this.outBoundStartDate = value;
    }

    /**
     * Gets the value of the outboundLocationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutboundLocationId() {
        return outboundLocationId;
    }

    /**
     * Sets the value of the outboundLocationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutboundLocationId(String value) {
        this.outboundLocationId = value;
    }

    /**
     * Gets the value of the outboundLocationName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutboundLocationName() {
        return outboundLocationName;
    }

    /**
     * Sets the value of the outboundLocationName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutboundLocationName(String value) {
        this.outboundLocationName = value;
    }

    /**
     * Gets the value of the phase property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhase() {
        return phase;
    }

    /**
     * Sets the value of the phase property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhase(String value) {
        this.phase = value;
    }

    /**
     * Gets the value of the phasePlannedAssemblyEndDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPhasePlannedAssemblyEndDate() {
        return phasePlannedAssemblyEndDate;
    }

    /**
     * Sets the value of the phasePlannedAssemblyEndDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPhasePlannedAssemblyEndDate(XMLGregorianCalendar value) {
        this.phasePlannedAssemblyEndDate = value;
    }

    /**
     * Gets the value of the phasePlannedAssemblyStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPhasePlannedAssemblyStartDate() {
        return phasePlannedAssemblyStartDate;
    }

    /**
     * Sets the value of the phasePlannedAssemblyStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPhasePlannedAssemblyStartDate(XMLGregorianCalendar value) {
        this.phasePlannedAssemblyStartDate = value;
    }

    /**
     * Gets the value of the phasePlannedDeliveryStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPhasePlannedDeliveryStartDate() {
        return phasePlannedDeliveryStartDate;
    }

    /**
     * Sets the value of the phasePlannedDeliveryStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPhasePlannedDeliveryStartDate(XMLGregorianCalendar value) {
        this.phasePlannedDeliveryStartDate = value;
    }

    /**
     * Gets the value of the phaseType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaseType() {
        return phaseType;
    }

    /**
     * Sets the value of the phaseType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaseType(String value) {
        this.phaseType = value;
    }

    /**
     * Gets the value of the quantity property.
     * 
     */
    public byte getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     * 
     */
    public void setQuantity(byte value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the receiver property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * Sets the value of the receiver property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceiver(String value) {
        this.receiver = value;
    }

    /**
     * Gets the value of the referenceGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferenceGroup() {
        return referenceGroup;
    }

    /**
     * Sets the value of the referenceGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferenceGroup(String value) {
        this.referenceGroup = value;
    }

    /**
     * Gets the value of the referenceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferenceId() {
        return referenceId;
    }

    /**
     * Sets the value of the referenceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferenceId(String value) {
        this.referenceId = value;
    }

    /**
     * Gets the value of the requesterName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequesterName() {
        return requesterName;
    }

    /**
     * Sets the value of the requesterName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequesterName(String value) {
        this.requesterName = value;
    }

    /**
     * Gets the value of the requesterUserId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequesterUserId() {
        return requesterUserId;
    }

    /**
     * Sets the value of the requesterUserId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequesterUserId(String value) {
        this.requesterUserId = value;
    }

    /**
     * Gets the value of the requiredStaDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRequiredStaDate() {
        return requiredStaDate;
    }

    /**
     * Sets the value of the requiredStaDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRequiredStaDate(XMLGregorianCalendar value) {
        this.requiredStaDate = value;
    }

    /**
     * Gets the value of the unitOfMeasure property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    /**
     * Sets the value of the unitOfMeasure property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnitOfMeasure(String value) {
        this.unitOfMeasure = value;
    }

}
