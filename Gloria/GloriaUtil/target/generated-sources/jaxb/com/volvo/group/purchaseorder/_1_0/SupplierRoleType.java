//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:26 em CEST 
//


package com.volvo.group.purchaseorder._1_0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;


/**
 * <p>Java class for SupplierRoleType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SupplierRoleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *     &lt;enumeration value="DealerParent"/>
 *     &lt;enumeration value="Dealer"/>
 *     &lt;enumeration value="SubDealer"/>
 *     &lt;enumeration value="Branch"/>
 *     &lt;enumeration value="Factory"/>
 *     &lt;enumeration value="Fleet"/>
 *     &lt;enumeration value="SalesCompany"/>
 *     &lt;enumeration value="ParentCompany"/>
 *     &lt;enumeration value="SalesOffice"/>
 *     &lt;enumeration value="ManufacturingSite"/>
 *     &lt;enumeration value="ShipFromAddress"/>
 *     &lt;enumeration value="EngineeringPartner"/>
 *     &lt;enumeration value="Consignee"/>
 *     &lt;enumeration value="AccountsPayable"/>
 *     &lt;enumeration value="Agency"/>
 *     &lt;enumeration value="AccountsReceivable"/>
 *     &lt;enumeration value="RemitToAddress"/>
 *     &lt;enumeration value="UnCategorized"/>
 *     &lt;enumeration value="DirectCustomer"/>
 *     &lt;enumeration value="ServicePartner"/>
 *     &lt;enumeration value="Warehouse"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlEnum
public enum SupplierRoleType {

    @XmlEnumValue("DealerParent")
    DEALER_PARENT("DealerParent"),
    @XmlEnumValue("Dealer")
    DEALER("Dealer"),
    @XmlEnumValue("SubDealer")
    SUB_DEALER("SubDealer"),
    @XmlEnumValue("Branch")
    BRANCH("Branch"),
    @XmlEnumValue("Factory")
    FACTORY("Factory"),
    @XmlEnumValue("Fleet")
    FLEET("Fleet"),
    @XmlEnumValue("SalesCompany")
    SALES_COMPANY("SalesCompany"),
    @XmlEnumValue("ParentCompany")
    PARENT_COMPANY("ParentCompany"),
    @XmlEnumValue("SalesOffice")
    SALES_OFFICE("SalesOffice"),
    @XmlEnumValue("ManufacturingSite")
    MANUFACTURING_SITE("ManufacturingSite"),
    @XmlEnumValue("ShipFromAddress")
    SHIP_FROM_ADDRESS("ShipFromAddress"),
    @XmlEnumValue("EngineeringPartner")
    ENGINEERING_PARTNER("EngineeringPartner"),
    @XmlEnumValue("Consignee")
    CONSIGNEE("Consignee"),
    @XmlEnumValue("AccountsPayable")
    ACCOUNTS_PAYABLE("AccountsPayable"),
    @XmlEnumValue("Agency")
    AGENCY("Agency"),
    @XmlEnumValue("AccountsReceivable")
    ACCOUNTS_RECEIVABLE("AccountsReceivable"),
    @XmlEnumValue("RemitToAddress")
    REMIT_TO_ADDRESS("RemitToAddress"),
    @XmlEnumValue("UnCategorized")
    UN_CATEGORIZED("UnCategorized"),
    @XmlEnumValue("DirectCustomer")
    DIRECT_CUSTOMER("DirectCustomer"),
    @XmlEnumValue("ServicePartner")
    SERVICE_PARTNER("ServicePartner"),
    @XmlEnumValue("Warehouse")
    WAREHOUSE("Warehouse");
    private final String value;

    SupplierRoleType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SupplierRoleType fromValue(String v) {
        for (SupplierRoleType c: SupplierRoleType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

}
