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
 * <p>Java class for SalesTaskCodeEnumerationType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SalesTaskCodeEnumerationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *     &lt;enumeration value="Meeting"/>
 *     &lt;enumeration value="ConferenceCall"/>
 *     &lt;enumeration value="FollowUp"/>
 *     &lt;enumeration value="EMail"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlEnum
public enum SalesTaskCodeEnumerationType {

    @XmlEnumValue("Meeting")
    MEETING("Meeting"),
    @XmlEnumValue("ConferenceCall")
    CONFERENCE_CALL("ConferenceCall"),
    @XmlEnumValue("FollowUp")
    FOLLOW_UP("FollowUp"),
    @XmlEnumValue("EMail")
    E_MAIL("EMail");
    private final String value;

    SalesTaskCodeEnumerationType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SalesTaskCodeEnumerationType fromValue(String v) {
        for (SalesTaskCodeEnumerationType c: SalesTaskCodeEnumerationType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

}
