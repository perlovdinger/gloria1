package com.volvo.gloria.util.c;

/**
 * Central place to hold Gloria params.
 */
public abstract class GloriaParams {
    public static final String DOCUMENT_BASE_URL = "/GloriaUIServices/api";
    public static final String APPLICATION_ID = "GLORIA";
    public static final Integer QUALITY_INSPECTION_LEVEL_ZERO = 0;
    public static final String BASE_URL_QUALITYDOCS = DOCUMENT_BASE_URL.concat("/documents/v1/receivedocs");
    public static final String BASE_URL_PROBLEMNOTEDOCS = DOCUMENT_BASE_URL.concat("/documents/v1/problemdocs");
    public static final String BASE_URL_ATTACHEDDOCS = DOCUMENT_BASE_URL.concat("/documents/v1/attacheddocs");
    public static final String BASE_URL_INSPECTIONDOCS = DOCUMENT_BASE_URL.concat("/documents/v1/qidocs");
    public static final int DOCUMENT_UPLOAD_LIMIT = 5;
    public static final int DOCUMENT_SIZE_LIMIT = 5242880;
    public static final String DOCUMENT_CATEGORY_QUALITYDOCS = "TYPE_QUALITY_DOCS";
    public static final String DOCUMENT_CATEGORY_PROBLEMNOTES = "TYPE_PROBLEMNOTE_DOCS";
    public static final String DOCUMENT_CATEGORY_ATTACHEDDOCS = "TYPE_ATTACHED_DOCS";
    public static final String DOCUMENT_CATEGORY_INSPECTIONDOCS = "TYPE_INSPECTION_DOCS";
    public static final String ASSIGNED = "assigned";
    public static final String UNASSIGNED = "unassigned";
    public static final String PROJECTS = "Projects";
    public static final String SUPPLIERS = "Suppliers";
    public static final String DOCUMENT_CATEGORY_DELIVERYDOCS = "TYPE_DELIVERY_DOCS";
    public static final int SUPPLIER_ID_LENGTH = 6;
    public static final int PARTAFFILIATION_CODE_LENGTH = 1;
    public static final int PARTAFFILIATION_NAME_LENGTH = 60;
    public static final int PROJECT_ID_LENGTH = 20;
    public static final String QUALITY_INSPECTION_LEVEL_ONE = "1";
    public static final String BALDO_USER_CIACTION_CREATE = "C";
    public static final String BALDO_USER_CIACTION_UPDATE = "U";
    public static final String BALDO_USER_CIACTION_DELETE = "D";
    public static final String XML_MESSAGE_LOG_SENT = "SENT";
    public static final String XML_MESSAGE_LOG_RECEIVING = "RECEIVING";
    public static final String XML_MESSAGE_LOG_SENDING = "SENDING";
    public static final String GROUP_TYPE_DEFAULT = "DEFAULT";
    public static final String GROUP_TYPE_DIFF_TOBJ_SAME_PART = "DIFFERENT_TEST_OBJECT_WITH_SAME_PART";
    public static final String GROUP_TYPE_SAME_TOBJ_DIFF_PART = "SAME_TEST_OBJECT_WITH_DIFF_PART";
    public static final String GPS_PROTOTYPE_PO_ACTION_ADD = "Add";
    public static final String GPS_PROTOTYPE_PO_ACTION_DELETE = "Delete";
    public static final String GPS_ORDER_NO_SPLITTER = "-";
    public static final String MARK_REQUESTLINE_CHANGE_REMOVED = "REMOVED";
    public static final String MARK_REQUESTLINE_CHANGE_ADDED = "ADDED";
    public static final String ENGLISH = "EN";
    public static final String SPARE_PARTS = "SPARE_PARTS";
    public static final String PART_AFFILIATION_NON_VOLVO = "X";
    public static final String PART_AFFILIATION_VOLVO = "V";
    public static final long MAX_INTERNAL_ORDER_NO_LENGTH = 14;
    public static final String APPLICATION_GLORIA = "Gloria";
    public static final String APPLICATION_GPS = "GPS";
    public static final String APPLICATION_KOLA = "Kola";
    public static final String APPLICATION_SAP = "SAP";
    public static final int TRANSPORTLABEL_ALPHANUMERIC_CODE_LENGTH = 8;
    public static final String CANCEL_PP_REQ = "CANCEL_PP_REQ";
    public static final String ADDITIONAL = "ADDITIONAL";
    public static final String CANCEL_MOD_WAITING = "CANCEL_MOD_WAITING";
    public static final String CANCEL_MOD_REMOVE = "CANCEL_MOD_REMOVE";
    public static final String REMOVE = "REMOVE";
    public static final String CANCEL_MOD_PP_REQ = "CANCEL_MOD_PP_REQ";
    public static final String CANCEL_MOD_IO = "CANCEL_MOD_IO";
    public static final String CANCEL_IO = "CANCEL_IO";
    public static final String DATA_OPTIMISTIC_LOCK_MESSAGE = "This operation cannot be performed since the information seen "
            + "in the page has already been updated.";
    public static final String UNITPRICE_PCE3 = "PCE3";
    public static final String UNITPRICE_PCE2 = "PCE2";
    public static final String UNITPRICE_PCE = "PCE";
    
    private GloriaParams() {
    }
}
