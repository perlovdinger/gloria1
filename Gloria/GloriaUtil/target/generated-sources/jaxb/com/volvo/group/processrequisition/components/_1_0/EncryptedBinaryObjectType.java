//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:29 em CEST 
//


package com.volvo.group.processrequisition.components._1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.openapplications.oagis._9.unqualifieddatatypes._1.BinaryObjectType;


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
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:DictionaryEntryName xmlns:ccts="urn:un:unece:uncefact:documentation:1.1" xmlns="http://www.openapplications.org/oagis/9" xmlns:oacl="http://www.openapplications.org/oagis/9/codelists" xmlns:qdt="http://www.openapplications.org/oagis/9/qualifieddatatypes/1.1" xmlns:udt="http://www.openapplications.org/oagis/9/unqualifieddatatypes/1.1" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;Encrypted_ Binary Object. Type&lt;/ccts:DictionaryEntryName&gt;
 * </pre>
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:VersionID xmlns:ccts="urn:un:unece:uncefact:documentation:1.1" xmlns="http://www.openapplications.org/oagis/9" xmlns:oacl="http://www.openapplications.org/oagis/9/codelists" xmlns:qdt="http://www.openapplications.org/oagis/9/qualifieddatatypes/1.1" xmlns:udt="http://www.openapplications.org/oagis/9/unqualifieddatatypes/1.1" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;1.0&lt;/ccts:VersionID&gt;
 * </pre>
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:DefinitionText xmlns:ccts="urn:un:unece:uncefact:documentation:1.1" xmlns="http://www.openapplications.org/oagis/9" xmlns:oacl="http://www.openapplications.org/oagis/9/codelists" xmlns:qdt="http://www.openapplications.org/oagis/9/qualifieddatatypes/1.1" xmlns:udt="http://www.openapplications.org/oagis/9/unqualifieddatatypes/1.1" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;Indicates that any element or attribute that makes use of this is to be or is recommended to be encrypted by the applications sharing this information.&lt;/ccts:DefinitionText&gt;
 * </pre>
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:RepresentationTermName xmlns:ccts="urn:un:unece:uncefact:documentation:1.1" xmlns="http://www.openapplications.org/oagis/9" xmlns:oacl="http://www.openapplications.org/oagis/9/codelists" xmlns:qdt="http://www.openapplications.org/oagis/9/qualifieddatatypes/1.1" xmlns:udt="http://www.openapplications.org/oagis/9/unqualifieddatatypes/1.1" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;Binary Object&lt;/ccts:RepresentationTermName&gt;
 * </pre>
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:QualifierTerm xmlns:ccts="urn:un:unece:uncefact:documentation:1.1" xmlns="http://www.openapplications.org/oagis/9" xmlns:oacl="http://www.openapplications.org/oagis/9/codelists" xmlns:qdt="http://www.openapplications.org/oagis/9/qualifieddatatypes/1.1" xmlns:udt="http://www.openapplications.org/oagis/9/unqualifieddatatypes/1.1" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;Encrypted&lt;/ccts:QualifierTerm&gt;
 * </pre>
 * 
 * 
 * <p>Java class for EncryptedBinaryObjectType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EncryptedBinaryObjectType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.openapplications.org/oagis/9/unqualifieddatatypes/1.1>BinaryObjectType">
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EncryptedBinaryObjectType")
public class EncryptedBinaryObjectType
    extends BinaryObjectType
{


}
