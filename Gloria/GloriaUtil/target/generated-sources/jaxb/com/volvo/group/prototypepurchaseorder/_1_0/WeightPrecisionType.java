//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:32 em CEST 
//


package com.volvo.group.prototypepurchaseorder._1_0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;


/**
 * <p>Java class for WeightPrecisionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WeightPrecisionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *     &lt;enumeration value="SeveralPartsWeighed"/>
 *     &lt;enumeration value="PartWeighed"/>
 *     &lt;enumeration value="SamplePartWeighed"/>
 *     &lt;enumeration value="Calculated"/>
 *     &lt;enumeration value="Estimated"/>
 *     &lt;enumeration value="RoughlyEstimated"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlEnum
public enum WeightPrecisionType {

    @XmlEnumValue("SeveralPartsWeighed")
    SEVERAL_PARTS_WEIGHED("SeveralPartsWeighed"),
    @XmlEnumValue("PartWeighed")
    PART_WEIGHED("PartWeighed"),
    @XmlEnumValue("SamplePartWeighed")
    SAMPLE_PART_WEIGHED("SamplePartWeighed"),
    @XmlEnumValue("Calculated")
    CALCULATED("Calculated"),
    @XmlEnumValue("Estimated")
    ESTIMATED("Estimated"),
    @XmlEnumValue("RoughlyEstimated")
    ROUGHLY_ESTIMATED("RoughlyEstimated");
    private final String value;

    WeightPrecisionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static WeightPrecisionType fromValue(String v) {
        for (WeightPrecisionType c: WeightPrecisionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

}
