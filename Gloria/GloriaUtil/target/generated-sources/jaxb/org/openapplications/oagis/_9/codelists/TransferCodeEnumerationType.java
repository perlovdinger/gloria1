//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:32 em CEST 
//


package org.openapplications.oagis._9.codelists;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;


/**
 * <p>Java class for TransferCodeEnumerationType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TransferCodeEnumerationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *     &lt;enumeration value="Complete"/>
 *     &lt;enumeration value="Return"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlEnum
public enum TransferCodeEnumerationType {

    @XmlEnumValue("Complete")
    COMPLETE("Complete"),
    @XmlEnumValue("Return")
    RETURN("Return");
    private final String value;

    TransferCodeEnumerationType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TransferCodeEnumerationType fromValue(String v) {
        for (TransferCodeEnumerationType c: TransferCodeEnumerationType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

}
