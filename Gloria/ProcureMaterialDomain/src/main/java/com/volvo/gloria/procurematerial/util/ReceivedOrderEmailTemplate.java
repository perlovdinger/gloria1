package com.volvo.gloria.procurematerial.util;

import org.springframework.util.StringUtils;


public final class  ReceivedOrderEmailTemplate {

    private static final String ENVIRONMENT_PROD_WAS = "prod-was";
    
    protected static final String SUBJECT = "Parts ordered by you in GlORIA have been received  ORDER_NUMBER";
    
    protected static final String GENERICBODY = ""
    + "The Parts ordered by you in GLORIA have been received \n\n";
  
    protected static final String BODY = ""
            + "Order Number : ORDER_NUMBER \n"
            + "Part Number : PART_NUMBER \n"
            + "Part Name   : PART_NAME \n"
            + "Part Modification : PART_MODIFICATION \n"
            + "Part Version: PART_VERSION\n"
            + "Material Controller Name: MATERIAL_CONTROLLER_NAME \n"
            + "Material Controller Id: MATERIAL_CONTROLLER_ID\n"
            + "Project Id: PROJECT_NUMBER\n"
            + "Date of Receival(UTC) : DATE_OF_RECEIVAL \n"
            + "Received Quantity : RECEIVED_QUANTITY\n"
            + "Current Balance : CURRENT_BALANCE \n"
            + "Supplier Name : SUPPLIER_NAME\n"
            + "Supplier Id : SUPPLIER_ID \n"
            + "Reference : REFERENCE \n"
            + "Damaged Quantity : DAMAGED_QUANTITY";

    protected static final String SUBJECT_TEST = "TEST MESSAGE FROM GLORIA :Parts ordered by you in GlORIA have been received  ORDER_NUMBER";
    
    protected static final String GENERICBODY_TEST = ""
            + "This is only a test for the future application GLORIA. Do not consider this information\n"
            + "The Parts ordered by you in GLORIA have been received \n\n";
    
    protected static final String BODY_TEST = ""
            + "Order Number : ORDER_NUMBER \n"
            + "Part Number : PART_NUMBER \n"
            + "Part Name   : PART_NAME \n"
            + "Part Modification : PART_MODIFICATION \n"
            + "Part Version: PART_VERSION\n"
            + "Material Controller Name: MATERIAL_CONTROLLER_NAME \n"
            + "Material Controller Id: MATERIAL_CONTROLLER_ID\n"
            + "Project Id: PROJECT_NUMBER\n"
            + "Date of Receival(UTC) : DATE_OF_RECEIVAL \n"
            + "Received Quantity : RECEIVED_QUANTITY\n"
            + "Current Balance : CURRENT_BALANCE \n"
            + "Supplier Name : SUPPLIER_NAME\n"
            + "Supplier Id : SUPPLIER_ID \n"
            + "Reference : REFERENCE \n"
            + "Damaged Quantity : DAMAGED_QUANTITY";
    
    private ReceivedOrderEmailTemplate() {
        
    }
    
    public static String getSubject(String env) {
        if (StringUtils.isEmpty(env) || !env.equalsIgnoreCase(ENVIRONMENT_PROD_WAS)) {
            return SUBJECT_TEST;
        }
        return SUBJECT;
    }

    public static String getBody(String env) {
        if (StringUtils.isEmpty(env) || !env.equalsIgnoreCase(ENVIRONMENT_PROD_WAS)) {
            return BODY_TEST;
        }
        return BODY;
    }
    
    public static String getGenericBody(String env) {
        if (StringUtils.isEmpty(env) || !env.equalsIgnoreCase(ENVIRONMENT_PROD_WAS)) {
            return GENERICBODY_TEST;
        }
        return GENERICBODY;
    }
}
