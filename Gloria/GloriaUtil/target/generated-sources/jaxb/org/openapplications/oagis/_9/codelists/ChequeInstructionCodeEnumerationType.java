//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:32 em CEST 
//


package org.openapplications.oagis._9.codelists;

import javax.xml.bind.annotation.XmlEnum;


/**
 * <p>Java class for ChequeInstructionCodeEnumerationType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ChequeInstructionCodeEnumerationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *     &lt;enumeration value="CCHQ"/>
 *     &lt;enumeration value="CCCH"/>
 *     &lt;enumeration value="BCHQ"/>
 *     &lt;enumeration value="DFFT"/>
 *     &lt;enumeration value="ELDR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlEnum
public enum ChequeInstructionCodeEnumerationType {

    CCHQ,
    CCCH,
    BCHQ,
    DFFT,
    ELDR;

    public String value() {
        return name();
    }

    public static ChequeInstructionCodeEnumerationType fromValue(String v) {
        return valueOf(v);
    }

}
