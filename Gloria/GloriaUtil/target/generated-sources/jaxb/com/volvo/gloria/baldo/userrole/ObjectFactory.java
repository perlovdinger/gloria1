//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:35 em CEST 
//


package com.volvo.gloria.baldo.userrole;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.volvo.gloria.baldo.userrole package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.volvo.gloria.baldo.userrole
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link BaldoCategory }
     * 
     */
    public BaldoCategory createBaldoCategory() {
        return new BaldoCategory();
    }

    /**
     * Create an instance of {@link RoleMap }
     * 
     */
    public RoleMap createRoleMap() {
        return new RoleMap();
    }

    /**
     * Create an instance of {@link GloriaRole }
     * 
     */
    public GloriaRole createGloriaRole() {
        return new GloriaRole();
    }

}
