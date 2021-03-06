//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:38 em CEST 
//


package com.volvo.group.init.deliveryFollowUpTeam._1_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


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
 *         &lt;element name="DeliveryFollowUpTeam" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="SupplierCounterPart" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                           &lt;attribute name="comment" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="companyCode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="disabledProcure" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                           &lt;attribute name="displayName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="domesticInvoiceId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="internationalInvoiceId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="materialUserId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="ppSuffix" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="shipToId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="transitAddressId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="followUpType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "deliveryFollowUpTeam"
})
@XmlRootElement(name = "DeliveryFollowUpTeams")
public class DeliveryFollowUpTeams {

    @XmlElement(name = "DeliveryFollowUpTeam")
    protected List<DeliveryFollowUpTeams.DeliveryFollowUpTeam> deliveryFollowUpTeam;

    /**
     * Gets the value of the deliveryFollowUpTeam property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the deliveryFollowUpTeam property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDeliveryFollowUpTeam().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DeliveryFollowUpTeams.DeliveryFollowUpTeam }
     * 
     * 
     */
    public List<DeliveryFollowUpTeams.DeliveryFollowUpTeam> getDeliveryFollowUpTeam() {
        if (deliveryFollowUpTeam == null) {
            deliveryFollowUpTeam = new ArrayList<DeliveryFollowUpTeams.DeliveryFollowUpTeam>();
        }
        return this.deliveryFollowUpTeam;
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
     *         &lt;element name="SupplierCounterPart" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *                 &lt;attribute name="comment" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="companyCode" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="disabledProcure" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *                 &lt;attribute name="displayName" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="domesticInvoiceId" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="internationalInvoiceId" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="materialUserId" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="ppSuffix" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="shipToId" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="transitAddressId" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/extension>
     *             &lt;/simpleContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="followUpType" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "supplierCounterPart"
    })
    public static class DeliveryFollowUpTeam {

        @XmlElement(name = "SupplierCounterPart")
        protected List<DeliveryFollowUpTeams.DeliveryFollowUpTeam.SupplierCounterPart> supplierCounterPart;
        @XmlAttribute
        protected String followUpType;
        @XmlAttribute
        protected String name;

        /**
         * Gets the value of the supplierCounterPart property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the supplierCounterPart property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSupplierCounterPart().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link DeliveryFollowUpTeams.DeliveryFollowUpTeam.SupplierCounterPart }
         * 
         * 
         */
        public List<DeliveryFollowUpTeams.DeliveryFollowUpTeam.SupplierCounterPart> getSupplierCounterPart() {
            if (supplierCounterPart == null) {
                supplierCounterPart = new ArrayList<DeliveryFollowUpTeams.DeliveryFollowUpTeam.SupplierCounterPart>();
            }
            return this.supplierCounterPart;
        }

        /**
         * Gets the value of the followUpType property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFollowUpType() {
            return followUpType;
        }

        /**
         * Sets the value of the followUpType property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFollowUpType(String value) {
            this.followUpType = value;
        }

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;simpleContent>
         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
         *       &lt;attribute name="comment" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="companyCode" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="disabledProcure" type="{http://www.w3.org/2001/XMLSchema}boolean" />
         *       &lt;attribute name="displayName" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="domesticInvoiceId" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="internationalInvoiceId" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="materialUserId" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="ppSuffix" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="shipToId" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="transitAddressId" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/extension>
         *   &lt;/simpleContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class SupplierCounterPart {

            @XmlValue
            protected String value;
            @XmlAttribute
            protected String comment;
            @XmlAttribute
            protected String companyCode;
            @XmlAttribute
            protected Boolean disabledProcure;
            @XmlAttribute
            protected String displayName;
            @XmlAttribute
            protected String domesticInvoiceId;
            @XmlAttribute
            protected String internationalInvoiceId;
            @XmlAttribute
            protected String materialUserId;
            @XmlAttribute
            protected String ppSuffix;
            @XmlAttribute
            protected String shipToId;
            @XmlAttribute
            protected String transitAddressId;

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValue(String value) {
                this.value = value;
            }

            /**
             * Gets the value of the comment property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getComment() {
                return comment;
            }

            /**
             * Sets the value of the comment property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setComment(String value) {
                this.comment = value;
            }

            /**
             * Gets the value of the companyCode property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCompanyCode() {
                return companyCode;
            }

            /**
             * Sets the value of the companyCode property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCompanyCode(String value) {
                this.companyCode = value;
            }

            /**
             * Gets the value of the disabledProcure property.
             * 
             * @return
             *     possible object is
             *     {@link Boolean }
             *     
             */
            public Boolean isDisabledProcure() {
                return disabledProcure;
            }

            /**
             * Sets the value of the disabledProcure property.
             * 
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *     
             */
            public void setDisabledProcure(Boolean value) {
                this.disabledProcure = value;
            }

            /**
             * Gets the value of the displayName property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDisplayName() {
                return displayName;
            }

            /**
             * Sets the value of the displayName property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDisplayName(String value) {
                this.displayName = value;
            }

            /**
             * Gets the value of the domesticInvoiceId property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDomesticInvoiceId() {
                return domesticInvoiceId;
            }

            /**
             * Sets the value of the domesticInvoiceId property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDomesticInvoiceId(String value) {
                this.domesticInvoiceId = value;
            }

            /**
             * Gets the value of the internationalInvoiceId property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getInternationalInvoiceId() {
                return internationalInvoiceId;
            }

            /**
             * Sets the value of the internationalInvoiceId property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setInternationalInvoiceId(String value) {
                this.internationalInvoiceId = value;
            }

            /**
             * Gets the value of the materialUserId property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getMaterialUserId() {
                return materialUserId;
            }

            /**
             * Sets the value of the materialUserId property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setMaterialUserId(String value) {
                this.materialUserId = value;
            }

            /**
             * Gets the value of the ppSuffix property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPpSuffix() {
                return ppSuffix;
            }

            /**
             * Sets the value of the ppSuffix property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPpSuffix(String value) {
                this.ppSuffix = value;
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
             * Gets the value of the transitAddressId property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTransitAddressId() {
                return transitAddressId;
            }

            /**
             * Sets the value of the transitAddressId property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTransitAddressId(String value) {
                this.transitAddressId = value;
            }

        }

    }

}
