//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:34 em CEST 
//


package com.volvo.group.finance.common._1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for MessageHeaderType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MessageHeaderType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MsgVersion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Sender">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="LogicalId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ComponentId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Receiver" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="LogicalId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ComponentId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="CreationDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MessageHeaderType", propOrder = {
    "msgVersion",
    "sender",
    "receiver",
    "creationDateTime"
})
public class MessageHeaderType {

    @XmlElement(name = "MsgVersion", required = true)
    protected String msgVersion;
    @XmlElement(name = "Sender", required = true)
    protected MessageHeaderType.Sender sender;
    @XmlElement(name = "Receiver")
    protected MessageHeaderType.Receiver receiver;
    @XmlElement(name = "CreationDateTime", required = true)
    protected XMLGregorianCalendar creationDateTime;

    /**
     * Gets the value of the msgVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsgVersion() {
        return msgVersion;
    }

    /**
     * Sets the value of the msgVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsgVersion(String value) {
        this.msgVersion = value;
    }

    /**
     * Gets the value of the sender property.
     * 
     * @return
     *     possible object is
     *     {@link MessageHeaderType.Sender }
     *     
     */
    public MessageHeaderType.Sender getSender() {
        return sender;
    }

    /**
     * Sets the value of the sender property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageHeaderType.Sender }
     *     
     */
    public void setSender(MessageHeaderType.Sender value) {
        this.sender = value;
    }

    /**
     * Gets the value of the receiver property.
     * 
     * @return
     *     possible object is
     *     {@link MessageHeaderType.Receiver }
     *     
     */
    public MessageHeaderType.Receiver getReceiver() {
        return receiver;
    }

    /**
     * Sets the value of the receiver property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageHeaderType.Receiver }
     *     
     */
    public void setReceiver(MessageHeaderType.Receiver value) {
        this.receiver = value;
    }

    /**
     * Gets the value of the creationDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreationDateTime() {
        return creationDateTime;
    }

    /**
     * Sets the value of the creationDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreationDateTime(XMLGregorianCalendar value) {
        this.creationDateTime = value;
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
     *         &lt;element name="LogicalId" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ComponentId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
        "logicalId",
        "componentId"
    })
    public static class Receiver {

        @XmlElement(name = "LogicalId", required = true)
        protected String logicalId;
        @XmlElement(name = "ComponentId")
        protected String componentId;

        /**
         * Gets the value of the logicalId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLogicalId() {
            return logicalId;
        }

        /**
         * Sets the value of the logicalId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLogicalId(String value) {
            this.logicalId = value;
        }

        /**
         * Gets the value of the componentId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getComponentId() {
            return componentId;
        }

        /**
         * Sets the value of the componentId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setComponentId(String value) {
            this.componentId = value;
        }

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
     *         &lt;element name="LogicalId" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ComponentId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
        "logicalId",
        "componentId"
    })
    public static class Sender {

        @XmlElement(name = "LogicalId", required = true)
        protected String logicalId;
        @XmlElement(name = "ComponentId")
        protected String componentId;

        /**
         * Gets the value of the logicalId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLogicalId() {
            return logicalId;
        }

        /**
         * Sets the value of the logicalId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLogicalId(String value) {
            this.logicalId = value;
        }

        /**
         * Gets the value of the componentId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getComponentId() {
            return componentId;
        }

        /**
         * Sets the value of the componentId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setComponentId(String value) {
            this.componentId = value;
        }

    }

}
