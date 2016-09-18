//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:26 em CEST 
//


package com.volvo.group.purchaseorder.components._1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:UniqueID xmlns:ccts="urn:un:unece:uncefact:documentation:1.1" xmlns="http://www.openapplications.org/oagis/9" xmlns:oacl="http://www.openapplications.org/oagis/9/codelists" xmlns:qdt="http://www.openapplications.org/oagis/9/qualifieddatatypes/1.1" xmlns:udt="http://www.openapplications.org/oagis/9/unqualifieddatatypes/1.1" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;QDT000000&lt;/ccts:UniqueID&gt;
 * </pre>
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:CategoryCode xmlns:ccts="urn:un:unece:uncefact:documentation:1.1" xmlns="http://www.openapplications.org/oagis/9" xmlns:oacl="http://www.openapplications.org/oagis/9/codelists" xmlns:qdt="http://www.openapplications.org/oagis/9/qualifieddatatypes/1.1" xmlns:udt="http://www.openapplications.org/oagis/9/unqualifieddatatypes/1.1" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;QDT&lt;/ccts:CategoryCode&gt;
 * </pre>
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:DictionaryEntryName xmlns:ccts="urn:un:unece:uncefact:documentation:1.1" xmlns="http://www.openapplications.org/oagis/9" xmlns:oacl="http://www.openapplications.org/oagis/9/codelists" xmlns:qdt="http://www.openapplications.org/oagis/9/qualifieddatatypes/1.1" xmlns:udt="http://www.openapplications.org/oagis/9/unqualifieddatatypes/1.1" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;State_ Code. Type&lt;/ccts:DictionaryEntryName&gt;
 * </pre>
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:VersionID xmlns:ccts="urn:un:unece:uncefact:documentation:1.1" xmlns="http://www.openapplications.org/oagis/9" xmlns:oacl="http://www.openapplications.org/oagis/9/codelists" xmlns:qdt="http://www.openapplications.org/oagis/9/qualifieddatatypes/1.1" xmlns:udt="http://www.openapplications.org/oagis/9/unqualifieddatatypes/1.1" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;1.0&lt;/ccts:VersionID&gt;
 * </pre>
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:DefinitionText xmlns:ccts="urn:un:unece:uncefact:documentation:1.1" xmlns="http://www.openapplications.org/oagis/9" xmlns:oacl="http://www.openapplications.org/oagis/9/codelists" xmlns:qdt="http://www.openapplications.org/oagis/9/qualifieddatatypes/1.1" xmlns:udt="http://www.openapplications.org/oagis/9/unqualifieddatatypes/1.1" xmlns:xsd="http://www.w3.org/2001/XMLSchema"/&gt;
 * </pre>
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:RepresentationTermName xmlns:ccts="urn:un:unece:uncefact:documentation:1.1" xmlns="http://www.openapplications.org/oagis/9" xmlns:oacl="http://www.openapplications.org/oagis/9/codelists" xmlns:qdt="http://www.openapplications.org/oagis/9/qualifieddatatypes/1.1" xmlns:udt="http://www.openapplications.org/oagis/9/unqualifieddatatypes/1.1" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;Code&lt;/ccts:RepresentationTermName&gt;
 * </pre>
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:QualifierTerm xmlns:ccts="urn:un:unece:uncefact:documentation:1.1" xmlns="http://www.openapplications.org/oagis/9" xmlns:oacl="http://www.openapplications.org/oagis/9/codelists" xmlns:qdt="http://www.openapplications.org/oagis/9/qualifieddatatypes/1.1" xmlns:udt="http://www.openapplications.org/oagis/9/unqualifieddatatypes/1.1" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;State&lt;/ccts:QualifierTerm&gt;
 * </pre>
 * 
 * 
 * <p>Java class for StateCodeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StateCodeType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.openapplications.org/oagis/9>StateCodeContentType">
 *       &lt;attGroup ref="{http://www.openapplications.org/oagis/9}CodeListAttributeGroup"/>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StateCodeType", propOrder = {
    "value"
})
public class StateCodeType {

    @XmlValue
    protected String value;
    @XmlAttribute
    protected String languageID;
    @XmlAttribute
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String listAgencyID;
    @XmlAttribute
    protected String listAgencyName;
    @XmlAttribute
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String listID;
    @XmlAttribute
    protected String listName;
    @XmlAttribute
    protected String listSchemeURI;
    @XmlAttribute
    protected String listURI;
    @XmlAttribute
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String listVersionID;
    @XmlAttribute
    protected String name;

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
     * Gets the value of the languageID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguageID() {
        return languageID;
    }

    /**
     * Sets the value of the languageID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguageID(String value) {
        this.languageID = value;
    }

    /**
     * Gets the value of the listAgencyID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListAgencyID() {
        return listAgencyID;
    }

    /**
     * Sets the value of the listAgencyID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListAgencyID(String value) {
        this.listAgencyID = value;
    }

    /**
     * Gets the value of the listAgencyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListAgencyName() {
        return listAgencyName;
    }

    /**
     * Sets the value of the listAgencyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListAgencyName(String value) {
        this.listAgencyName = value;
    }

    /**
     * Gets the value of the listID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListID() {
        return listID;
    }

    /**
     * Sets the value of the listID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListID(String value) {
        this.listID = value;
    }

    /**
     * Gets the value of the listName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListName() {
        return listName;
    }

    /**
     * Sets the value of the listName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListName(String value) {
        this.listName = value;
    }

    /**
     * Gets the value of the listSchemeURI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListSchemeURI() {
        return listSchemeURI;
    }

    /**
     * Sets the value of the listSchemeURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListSchemeURI(String value) {
        this.listSchemeURI = value;
    }

    /**
     * Gets the value of the listURI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListURI() {
        return listURI;
    }

    /**
     * Sets the value of the listURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListURI(String value) {
        this.listURI = value;
    }

    /**
     * Gets the value of the listVersionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListVersionID() {
        return listVersionID;
    }

    /**
     * Sets the value of the listVersionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListVersionID(String value) {
        this.listVersionID = value;
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

}
