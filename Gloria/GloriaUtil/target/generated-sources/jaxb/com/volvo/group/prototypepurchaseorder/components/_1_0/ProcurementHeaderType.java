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
 * <p>Java class for ProcurementHeaderType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProcurementHeaderType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}RequestHeaderType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}BillToParty" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}CarrierParty" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}PayFromParty" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}PartialShipmentAllowedIndicator" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}DropShipmentAllowedIndicator" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}EarlyShipmentAllowedIndicator" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}LicenseIndicator" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}TaxExemptIndicator" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}TransportationTerm" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}PaymentTerm" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}DistributedCharge" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}DistributedTax" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}Distribution" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcurementHeaderType", propOrder = {
    "billToParty",
    "carrierParty",
    "payFromParty",
    "partialShipmentAllowedIndicator",
    "dropShipmentAllowedIndicator",
    "earlyShipmentAllowedIndicator",
    "licenseIndicator",
    "taxExemptIndicator",
    "transportationTerm",
    "paymentTerm",
    "distributedCharge",
    "distributedTax",
    "distribution"
})
public class ProcurementHeaderType
    extends RequestHeaderType
{

    @XmlElement(name = "BillToParty")
    protected CustomerPartyType billToParty;
    @XmlElement(name = "CarrierParty")
    protected SemanticPartyType carrierParty;
    @XmlElement(name = "PayFromParty")
    protected SemanticPartyType payFromParty;
    @XmlElement(name = "PartialShipmentAllowedIndicator")
    protected Boolean partialShipmentAllowedIndicator;
    @XmlElement(name = "DropShipmentAllowedIndicator")
    protected Boolean dropShipmentAllowedIndicator;
    @XmlElement(name = "EarlyShipmentAllowedIndicator")
    protected Boolean earlyShipmentAllowedIndicator;
    @XmlElement(name = "LicenseIndicator")
    protected List<LicenseIndicatorType> licenseIndicator;
    @XmlElement(name = "TaxExemptIndicator")
    protected Boolean taxExemptIndicator;
    @XmlElement(name = "TransportationTerm")
    protected List<TransportationTermType> transportationTerm;
    @XmlElement(name = "PaymentTerm")
    protected List<PaymentTermType> paymentTerm;
    @XmlElement(name = "DistributedCharge")
    protected List<DistributedChargeType> distributedCharge;
    @XmlElement(name = "DistributedTax")
    protected List<DistributedTaxType> distributedTax;
    @XmlElement(name = "Distribution")
    protected List<DistributionType> distribution;

    /**
     * Gets the value of the billToParty property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerPartyType }
     *     
     */
    public CustomerPartyType getBillToParty() {
        return billToParty;
    }

    /**
     * Sets the value of the billToParty property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerPartyType }
     *     
     */
    public void setBillToParty(CustomerPartyType value) {
        this.billToParty = value;
    }

    /**
     * Gets the value of the carrierParty property.
     * 
     * @return
     *     possible object is
     *     {@link SemanticPartyType }
     *     
     */
    public SemanticPartyType getCarrierParty() {
        return carrierParty;
    }

    /**
     * Sets the value of the carrierParty property.
     * 
     * @param value
     *     allowed object is
     *     {@link SemanticPartyType }
     *     
     */
    public void setCarrierParty(SemanticPartyType value) {
        this.carrierParty = value;
    }

    /**
     * Gets the value of the payFromParty property.
     * 
     * @return
     *     possible object is
     *     {@link SemanticPartyType }
     *     
     */
    public SemanticPartyType getPayFromParty() {
        return payFromParty;
    }

    /**
     * Sets the value of the payFromParty property.
     * 
     * @param value
     *     allowed object is
     *     {@link SemanticPartyType }
     *     
     */
    public void setPayFromParty(SemanticPartyType value) {
        this.payFromParty = value;
    }

    /**
     * Gets the value of the partialShipmentAllowedIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPartialShipmentAllowedIndicator() {
        return partialShipmentAllowedIndicator;
    }

    /**
     * Sets the value of the partialShipmentAllowedIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPartialShipmentAllowedIndicator(Boolean value) {
        this.partialShipmentAllowedIndicator = value;
    }

    /**
     * Gets the value of the dropShipmentAllowedIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDropShipmentAllowedIndicator() {
        return dropShipmentAllowedIndicator;
    }

    /**
     * Sets the value of the dropShipmentAllowedIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDropShipmentAllowedIndicator(Boolean value) {
        this.dropShipmentAllowedIndicator = value;
    }

    /**
     * Gets the value of the earlyShipmentAllowedIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEarlyShipmentAllowedIndicator() {
        return earlyShipmentAllowedIndicator;
    }

    /**
     * Sets the value of the earlyShipmentAllowedIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEarlyShipmentAllowedIndicator(Boolean value) {
        this.earlyShipmentAllowedIndicator = value;
    }

    /**
     * Gets the value of the licenseIndicator property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the licenseIndicator property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLicenseIndicator().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LicenseIndicatorType }
     * 
     * 
     */
    public List<LicenseIndicatorType> getLicenseIndicator() {
        if (licenseIndicator == null) {
            licenseIndicator = new ArrayList<LicenseIndicatorType>();
        }
        return this.licenseIndicator;
    }

    /**
     * Specifies whether the associated element is exempt from withholding taxes.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTaxExemptIndicator() {
        return taxExemptIndicator;
    }

    /**
     * Specifies whether the associated element is exempt from withholding taxes.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTaxExemptIndicator(Boolean value) {
        this.taxExemptIndicator = value;
    }

    /**
     * Gets the value of the transportationTerm property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transportationTerm property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransportationTerm().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransportationTermType }
     * 
     * 
     */
    public List<TransportationTermType> getTransportationTerm() {
        if (transportationTerm == null) {
            transportationTerm = new ArrayList<TransportationTermType>();
        }
        return this.transportationTerm;
    }

    /**
     * Gets the value of the paymentTerm property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the paymentTerm property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPaymentTerm().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PaymentTermType }
     * 
     * 
     */
    public List<PaymentTermType> getPaymentTerm() {
        if (paymentTerm == null) {
            paymentTerm = new ArrayList<PaymentTermType>();
        }
        return this.paymentTerm;
    }

    /**
     * Gets the value of the distributedCharge property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the distributedCharge property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDistributedCharge().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DistributedChargeType }
     * 
     * 
     */
    public List<DistributedChargeType> getDistributedCharge() {
        if (distributedCharge == null) {
            distributedCharge = new ArrayList<DistributedChargeType>();
        }
        return this.distributedCharge;
    }

    /**
     * Gets the value of the distributedTax property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the distributedTax property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDistributedTax().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DistributedTaxType }
     * 
     * 
     */
    public List<DistributedTaxType> getDistributedTax() {
        if (distributedTax == null) {
            distributedTax = new ArrayList<DistributedTaxType>();
        }
        return this.distributedTax;
    }

    /**
     * Gets the value of the distribution property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the distribution property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDistribution().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DistributionType }
     * 
     * 
     */
    public List<DistributionType> getDistribution() {
        if (distribution == null) {
            distribution = new ArrayList<DistributionType>();
        }
        return this.distribution;
    }

}
