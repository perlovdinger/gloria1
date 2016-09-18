//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:29 em CEST 
//


package com.volvo.group.processrequisition._1_0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;


/**
 * <p>Java class for OrderModeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="OrderModeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *     &lt;enumeration value="Quantity"/>
 *     &lt;enumeration value="Production"/>
 *     &lt;enumeration value="Prototype"/>
 *     &lt;enumeration value="Rework"/>
 *     &lt;enumeration value="Sample"/>
 *     &lt;enumeration value="Tooling"/>
 *     &lt;enumeration value="OneTimePayment"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlEnum
public enum OrderModeType {

    @XmlEnumValue("Quantity")
    QUANTITY("Quantity"),
    @XmlEnumValue("Production")
    PRODUCTION("Production"),
    @XmlEnumValue("Prototype")
    PROTOTYPE("Prototype"),
    @XmlEnumValue("Rework")
    REWORK("Rework"),
    @XmlEnumValue("Sample")
    SAMPLE("Sample"),
    @XmlEnumValue("Tooling")
    TOOLING("Tooling"),
    @XmlEnumValue("OneTimePayment")
    ONE_TIME_PAYMENT("OneTimePayment");
    private final String value;

    OrderModeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static OrderModeType fromValue(String v) {
        for (OrderModeType c: OrderModeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

}
