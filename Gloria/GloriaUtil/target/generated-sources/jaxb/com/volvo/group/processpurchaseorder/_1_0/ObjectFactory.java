//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:34 em CEST 
//


package com.volvo.group.processpurchaseorder._1_0;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.volvo.group.processpurchaseorder._1_0 package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.volvo.group.processpurchaseorder._1_0
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PurchaseOrderType }
     * 
     */
    public PurchaseOrderType createPurchaseOrderType() {
        return new PurchaseOrderType();
    }

    /**
     * Create an instance of {@link PurchaseOrderType.POHeaderData.PoItemData }
     * 
     */
    public PurchaseOrderType.POHeaderData.PoItemData createPurchaseOrderTypePOHeaderDataPoItemData() {
        return new PurchaseOrderType.POHeaderData.PoItemData();
    }

    /**
     * Create an instance of {@link PurchaseOrderType.POHeaderData.PoItemData.POScheduleLine }
     * 
     */
    public PurchaseOrderType.POHeaderData.PoItemData.POScheduleLine createPurchaseOrderTypePOHeaderDataPoItemDataPOScheduleLine() {
        return new PurchaseOrderType.POHeaderData.PoItemData.POScheduleLine();
    }

    /**
     * Create an instance of {@link PurchaseOrderType.POHeaderData }
     * 
     */
    public PurchaseOrderType.POHeaderData createPurchaseOrderTypePOHeaderData() {
        return new PurchaseOrderType.POHeaderData();
    }

    /**
     * Create an instance of {@link PurchaseOrderType.POHeaderData.PoItemData.POAccountAssignment }
     * 
     */
    public PurchaseOrderType.POHeaderData.PoItemData.POAccountAssignment createPurchaseOrderTypePOHeaderDataPoItemDataPOAccountAssignment() {
        return new PurchaseOrderType.POHeaderData.PoItemData.POAccountAssignment();
    }

    /**
     * Create an instance of {@link ProcessPurchaseOrder }
     * 
     */
    public ProcessPurchaseOrder createProcessPurchaseOrder() {
        return new ProcessPurchaseOrder();
    }

}
