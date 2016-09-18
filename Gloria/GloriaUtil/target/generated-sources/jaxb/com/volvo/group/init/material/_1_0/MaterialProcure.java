//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:38 em CEST 
//


package com.volvo.group.init.material._1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *       &lt;attribute name="consignorId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="costCenter" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="currency" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="dangerousGoodsFlag" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="dangerousGoodsName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="dfuObjectNumber" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="directSend" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="finalWarehouseId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="glaAccount" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="inspectionLevel" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="internalOrderNo" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="mailFormId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="pPartAffiliation" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="pPartModification" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="pPartName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="pPartNumber" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="pPartVersion" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="procureDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="procureInfo" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="procureType" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="projectId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="referenceGroups" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="requiredStaDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="requisitionId" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="scrappingAlert" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="shipToId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="warehouseComment" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="wbsCode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="whSiteId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "MaterialProcure")
public class MaterialProcure {

    @XmlAttribute
    protected String consignorId;
    @XmlAttribute
    protected String costCenter;
    @XmlAttribute
    protected String currency;
    @XmlAttribute
    protected Boolean dangerousGoodsFlag;
    @XmlAttribute
    protected String dangerousGoodsName;
    @XmlAttribute(required = true)
    protected String dfuObjectNumber;
    @XmlAttribute(required = true)
    protected boolean directSend;
    @XmlAttribute
    protected String finalWarehouseId;
    @XmlAttribute
    protected String glaAccount;
    @XmlAttribute(required = true)
    protected String inspectionLevel;
    @XmlAttribute
    protected String internalOrderNo;
    @XmlAttribute
    protected String mailFormId;
    @XmlAttribute(required = true)
    protected String pPartAffiliation;
    @XmlAttribute
    protected String pPartModification;
    @XmlAttribute(required = true)
    protected String pPartName;
    @XmlAttribute(required = true)
    protected String pPartNumber;
    @XmlAttribute(required = true)
    protected String pPartVersion;
    @XmlAttribute(required = true)
    protected XMLGregorianCalendar procureDate;
    @XmlAttribute
    protected String procureInfo;
    @XmlAttribute(required = true)
    protected String procureType;
    @XmlAttribute
    protected String projectId;
    @XmlAttribute
    protected String referenceGroups;
    @XmlAttribute
    protected XMLGregorianCalendar requiredStaDate;
    @XmlAttribute(required = true)
    protected long requisitionId;
    @XmlAttribute
    protected String scrappingAlert;
    @XmlAttribute
    protected String shipToId;
    @XmlAttribute
    protected String warehouseComment;
    @XmlAttribute
    protected String wbsCode;
    @XmlAttribute
    protected String whSiteId;

    /**
     * Gets the value of the consignorId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConsignorId() {
        return consignorId;
    }

    /**
     * Sets the value of the consignorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConsignorId(String value) {
        this.consignorId = value;
    }

    /**
     * Gets the value of the costCenter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCostCenter() {
        return costCenter;
    }

    /**
     * Sets the value of the costCenter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCostCenter(String value) {
        this.costCenter = value;
    }

    /**
     * Gets the value of the currency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the value of the currency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrency(String value) {
        this.currency = value;
    }

    /**
     * Gets the value of the dangerousGoodsFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDangerousGoodsFlag() {
        return dangerousGoodsFlag;
    }

    /**
     * Sets the value of the dangerousGoodsFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDangerousGoodsFlag(Boolean value) {
        this.dangerousGoodsFlag = value;
    }

    /**
     * Gets the value of the dangerousGoodsName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDangerousGoodsName() {
        return dangerousGoodsName;
    }

    /**
     * Sets the value of the dangerousGoodsName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDangerousGoodsName(String value) {
        this.dangerousGoodsName = value;
    }

    /**
     * Gets the value of the dfuObjectNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDfuObjectNumber() {
        return dfuObjectNumber;
    }

    /**
     * Sets the value of the dfuObjectNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDfuObjectNumber(String value) {
        this.dfuObjectNumber = value;
    }

    /**
     * Gets the value of the directSend property.
     * 
     */
    public boolean isDirectSend() {
        return directSend;
    }

    /**
     * Sets the value of the directSend property.
     * 
     */
    public void setDirectSend(boolean value) {
        this.directSend = value;
    }

    /**
     * Gets the value of the finalWarehouseId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFinalWarehouseId() {
        return finalWarehouseId;
    }

    /**
     * Sets the value of the finalWarehouseId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFinalWarehouseId(String value) {
        this.finalWarehouseId = value;
    }

    /**
     * Gets the value of the glaAccount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGlaAccount() {
        return glaAccount;
    }

    /**
     * Sets the value of the glaAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGlaAccount(String value) {
        this.glaAccount = value;
    }

    /**
     * Gets the value of the inspectionLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInspectionLevel() {
        return inspectionLevel;
    }

    /**
     * Sets the value of the inspectionLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInspectionLevel(String value) {
        this.inspectionLevel = value;
    }

    /**
     * Gets the value of the internalOrderNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInternalOrderNo() {
        return internalOrderNo;
    }

    /**
     * Sets the value of the internalOrderNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInternalOrderNo(String value) {
        this.internalOrderNo = value;
    }

    /**
     * Gets the value of the mailFormId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMailFormId() {
        return mailFormId;
    }

    /**
     * Sets the value of the mailFormId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMailFormId(String value) {
        this.mailFormId = value;
    }

    /**
     * Gets the value of the pPartAffiliation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPPartAffiliation() {
        return pPartAffiliation;
    }

    /**
     * Sets the value of the pPartAffiliation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPPartAffiliation(String value) {
        this.pPartAffiliation = value;
    }

    /**
     * Gets the value of the pPartModification property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPPartModification() {
        return pPartModification;
    }

    /**
     * Sets the value of the pPartModification property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPPartModification(String value) {
        this.pPartModification = value;
    }

    /**
     * Gets the value of the pPartName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPPartName() {
        return pPartName;
    }

    /**
     * Sets the value of the pPartName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPPartName(String value) {
        this.pPartName = value;
    }

    /**
     * Gets the value of the pPartNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPPartNumber() {
        return pPartNumber;
    }

    /**
     * Sets the value of the pPartNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPPartNumber(String value) {
        this.pPartNumber = value;
    }

    /**
     * Gets the value of the pPartVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPPartVersion() {
        return pPartVersion;
    }

    /**
     * Sets the value of the pPartVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPPartVersion(String value) {
        this.pPartVersion = value;
    }

    /**
     * Gets the value of the procureDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getProcureDate() {
        return procureDate;
    }

    /**
     * Sets the value of the procureDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setProcureDate(XMLGregorianCalendar value) {
        this.procureDate = value;
    }

    /**
     * Gets the value of the procureInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcureInfo() {
        return procureInfo;
    }

    /**
     * Sets the value of the procureInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcureInfo(String value) {
        this.procureInfo = value;
    }

    /**
     * Gets the value of the procureType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcureType() {
        return procureType;
    }

    /**
     * Sets the value of the procureType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcureType(String value) {
        this.procureType = value;
    }

    /**
     * Gets the value of the projectId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * Sets the value of the projectId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProjectId(String value) {
        this.projectId = value;
    }

    /**
     * Gets the value of the referenceGroups property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferenceGroups() {
        return referenceGroups;
    }

    /**
     * Sets the value of the referenceGroups property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferenceGroups(String value) {
        this.referenceGroups = value;
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
     * Gets the value of the requisitionId property.
     * 
     */
    public long getRequisitionId() {
        return requisitionId;
    }

    /**
     * Sets the value of the requisitionId property.
     * 
     */
    public void setRequisitionId(long value) {
        this.requisitionId = value;
    }

    /**
     * Gets the value of the scrappingAlert property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScrappingAlert() {
        return scrappingAlert;
    }

    /**
     * Sets the value of the scrappingAlert property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScrappingAlert(String value) {
        this.scrappingAlert = value;
    }

    /**
     * Gets the value of the shipToId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShipToId() {
        return shipToId;
    }

    /**
     * Sets the value of the shipToId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShipToId(String value) {
        this.shipToId = value;
    }

    /**
     * Gets the value of the warehouseComment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWarehouseComment() {
        return warehouseComment;
    }

    /**
     * Sets the value of the warehouseComment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWarehouseComment(String value) {
        this.warehouseComment = value;
    }

    /**
     * Gets the value of the wbsCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWbsCode() {
        return wbsCode;
    }

    /**
     * Sets the value of the wbsCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWbsCode(String value) {
        this.wbsCode = value;
    }

    /**
     * Gets the value of the whSiteId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWhSiteId() {
        return whSiteId;
    }

    /**
     * Sets the value of the whSiteId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWhSiteId(String value) {
        this.whSiteId = value;
    }

}
