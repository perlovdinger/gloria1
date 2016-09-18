package com.volvo.gloria.procurematerial.util.migration.c;

import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.markAsInvalid;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.trim;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderReceivedDTO;
import com.volvo.gloria.procurematerial.util.migration.c.type.order.MigrationOrderType;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.gloria.util.excel.ExcelColumnHeader;
import com.volvo.gloria.util.excel.ExcelHandler;

/**
 * Helper class for to convert open order migration excel to dto.
 */
public class OrderNewTemplateExcelHandler extends ExcelHandler<Set<OrderMigrationDTO>> {

    private static final String DATE_TIME_PATTERN = "MM-dd-yyyy HH:mm:ss";

    private static final int VALID_DAY_OPENORDER = 1;
    private static final int VALID_MONTH_OPENORDER = 0;
    private static final int VALID_YEAR_OPENORDER = 2013;

    private static final int VALID_DAY_CLOSEDORDER = 1;
    private static final int VALID_MONTH_CLOSEDORDER = 0;
    private static final int VALID_YEAR_CLOSEDORDER = 2004;

    private static final int LEFT_PAD_COSTCENTER = 10;

    private static int id;

    public static final String HEADER = "ITEM_ID;LINE_TYPE;ORDER_NUMBER;ORDER_DATE;SUFFIX;PART_QUALIFIER;PART_NUMBER;DESCRIPTION;"
            + "STAGE+ISSUE;UNIT_OF_MEASURE;PRICE;UNIT_OF_PRICE;AMOUNT;CURRENCY;MATERIAL_USERID;"
            + "PURCHASING_ORGANIZATION;BUYER_CODE;BUYER_NAME;PROJECT;COST_CENTER;GL_ACCOUNT;WBS_ELEMENT;SUPPLIER_CODE;"
            + "SUPPLIER_NAME;INSPECTION;INTERNAL_FLOW;ISSUER_ID;ORDERER_ID;NOTE;REQUISITION_NUMBER;"
            + "SHIP_ARRIVE_DATE;ACCEPTED_STA_DATE;AGREED_STA_DATE;EXPECTED_STA_DATE;ORDER_LINE_STATUS;INT_EXT_ORDER;ORDER_FROM_GPS;ORDER_LINE_PAID;"
            + "ORDERED_QUANTITY;POSSIBLE_TO_RECEIVE_QUANTITY;RECEIVED_QUANTITY;RECEIVED_DATE;TEST_OBJECTS_QTY;TEST_OBJECTS_TOTAL_QTY;PAID_QUANTITY;"
            + "PPA_ID;REFERENCE;SPARE_PART_QTY;ISSUER_NAME;ISSUER_EMAIL;ORDERER_NAME;ORDERER_EMAIL;SUPPLIER_PART_NO;"
            + "AGREED_STA_LAST_UPDATED;MODULAR_HARNESS;PROCUREDATE;OBJECT_INFORMATION;ORDER_INFORMATION;PACKING_SLIP_NUMBER;ORDER_SITE;"
            + "WAREHOUSE_SITE;TEST_OBJECTS_PULLED;ORDER_STATE;ORDER_TYPE;" 
            + "Migrated;isSendPPO;isSendGM;Reason;Reason_IT";

    private Set<OrderMigrationDTO> resultDTO;

    private Map<String, OrderMigrationDTO> itemRowMap = new HashMap<String, OrderMigrationDTO>();
    private static Map<String, String> siteCodeToSiteId = new HashMap<String, String>();

    private static Iterator<String> headerArray = Arrays.asList(HEADER.split(";")).iterator();

    public OrderNewTemplateExcelHandler(InputStream is) {
        super(is, getColumnList());
        setAllSites();
    }

    public OrderNewTemplateExcelHandler(String filename) {
        super(filename, getColumnList());
        setAllSites();
    }

    public static List<String> getColumnList() {
        List<String> columnList = new ArrayList<String>();
        OrderMigrationExcel[] values = OrderMigrationExcel.values();
        for (OrderMigrationExcel column : values) {
            columnList.add(column.columnName());
        }
        return columnList;
    }

    public void manageRow(List<String> row) throws ParseException {
        if (resultDTO == null) {
            resultDTO = new HashSet<OrderMigrationDTO>();
        }

        String itemId = trim(row.get(OrderMigrationExcel.ITEMID.sequenceNo())).replaceAll("\\W|\\s+", "");
        String lineType = trim(row.get(OrderMigrationExcel.LINETYPE.sequenceNo()));
        String orderTypeOriginal = trim(row.get(OrderMigrationExcel.ORDER_TYPE.sequenceNo()));
        String orderLinePaid = trim(row.get(OrderMigrationExcel.ORDERLINEPAID.sequenceNo()));
        if (itemRowMap.containsKey(itemId) && "RECEIVE_LINE".equalsIgnoreCase(lineType)) {
            readReceivedLineItemForAnOrderLine(row, itemId, itemRowMap.get(itemId));
        } else {
            OrderMigrationDTO orderMigrationDTO = new OrderMigrationDTO();
            orderMigrationDTO.setItemId(itemId);
            orderMigrationDTO.setLineType(lineType);
            orderMigrationDTO.setOrderLinePaid(orderLinePaid);
            orderMigrationDTO.setOrderTypeOriginal(orderTypeOriginal);
            orderMigrationDTO.setOrderType(identifyOrderType(orderLinePaid));

            if ("ORDER_LINE".equalsIgnoreCase(lineType)) {
                readOrderLineItem(row, itemId, lineType, orderMigrationDTO);
                itemRowMap.put(itemId, orderMigrationDTO);
            } else {
                System.out.println(lineType + "   " + itemId);
                markAsInvalid(orderMigrationDTO, "RECEIVE_LINE without ORDER_LINE");
            }
            resultDTO.add(orderMigrationDTO);
        }
    }

    private String identifyOrderType(String orderLinePaid) {
        if (orderLinePaid.equalsIgnoreCase("NO")) {
            return MigrationOrderType.OPEN.name();
        }
        return MigrationOrderType.CLOSED.name();
    }

    private void readOrderLineItem(List<String> row, String itemId, String lineType, OrderMigrationDTO orderMigrationDTO) {
        validateOrderDate(trim(row.get(OrderMigrationExcel.ORDERDATE.sequenceNo())), orderMigrationDTO);

        String materialUserid = trim(row.get(OrderMigrationExcel.MATERIALUSERID.sequenceNo()));
        if (!StringUtils.isEmpty(materialUserid)) {
            orderMigrationDTO.setMaterialUseridOriginal(materialUserid);
            orderMigrationDTO.setMaterialUserid(materialUserid.charAt(0) == '0' ? materialUserid.substring(1) : materialUserid);
        }
        orderMigrationDTO.setInspection(trim(row.get(OrderMigrationExcel.INSPECTION.sequenceNo())));
        orderMigrationDTO.setInternalFlow(trim(row.get(OrderMigrationExcel.INTERNALFLOW.sequenceNo())));
        orderMigrationDTO.setIssuerId(trim(row.get(OrderMigrationExcel.ISSUERID.sequenceNo())));
        orderMigrationDTO.setNote(trim(row.get(OrderMigrationExcel.NOTE.sequenceNo())));
        orderMigrationDTO.setPpaId(trim(row.get(OrderMigrationExcel.PPA_ID.sequenceNo())));

        updateWithOrderInformation(row, orderMigrationDTO);

        updateWithPartInformation(row, orderMigrationDTO);

        updateWithFinanceInformation(row, orderMigrationDTO);
    }

    private void updateWithFinanceInformation(List<String> row, OrderMigrationDTO orderMigrationDTO) {
        orderMigrationDTO.setProject(trim(row.get(OrderMigrationExcel.PROJECT.sequenceNo())));
        String costCenter = trim(row.get(OrderMigrationExcel.COSTCENTER.sequenceNo()));
        orderMigrationDTO.setCostCenter(costCenter == null ? null : StringUtils.leftPad(costCenter, LEFT_PAD_COSTCENTER, '0'));
        orderMigrationDTO.setGlAccount(trim(row.get(OrderMigrationExcel.GLACCOUNT.sequenceNo())));
        orderMigrationDTO.setWbsElement(trim(row.get(OrderMigrationExcel.WBSELEMENT.sequenceNo())));
    }

    private void updateWithPartInformation(List<String> row, OrderMigrationDTO orderMigrationDTO) {
        String partQualifier = row.get(OrderMigrationExcel.PARTQUALIFER.sequenceNo());
        if (StringUtils.isEmpty(partQualifier)) {
            partQualifier = GloriaParams.PART_AFFILIATION_NON_VOLVO;
        }
        orderMigrationDTO.setPartQualifier(trim(partQualifier));

        String partNumber = row.get(OrderMigrationExcel.PARTNUMBER.sequenceNo());
        if (!StringUtils.isEmpty(partNumber)) {
            orderMigrationDTO.setPartNumber(trim(partNumber));
        }

        String description = row.get(OrderMigrationExcel.DESCRIPTION.sequenceNo());
        orderMigrationDTO.setDescription(trim(description).replace("\"", ""));

        String partVersion = row.get(OrderMigrationExcel.PARTVERSION.sequenceNo());
        if (!StringUtils.isEmpty(partVersion)) {
            orderMigrationDTO.setPartVersion(trim(partVersion));
        }

        String unitOfMeasure = row.get(OrderMigrationExcel.UNITOFMEASURE.sequenceNo());
        if (StringUtils.isEmpty(unitOfMeasure)) {
            unitOfMeasure = "PCE";
        }
        orderMigrationDTO.setUnitOfMeasure(trim(unitOfMeasure));

        String priceOriginal = row.get(OrderMigrationExcel.PRICE.sequenceNo());
        orderMigrationDTO.setPriceOriginal(trim(priceOriginal));
        if (!StringUtils.isEmpty(priceOriginal)) {
            orderMigrationDTO.setPrice(Double.valueOf(priceOriginal));
        } else {
            orderMigrationDTO.setPrice(0);
        }

        orderMigrationDTO.setUnitOfPrice(trim(row.get(OrderMigrationExcel.UNITOFPRICE.sequenceNo())));
        String amount = trim(row.get(OrderMigrationExcel.AMOUNT.sequenceNo()));
        orderMigrationDTO.setAmountOriginal(amount);
        if (amount != null) {
            try {
                orderMigrationDTO.setAmount(Integer.valueOf(amount.replaceAll("[^0-9.]|\\s+", "")).intValue());
            } catch (Exception e) {
                orderMigrationDTO.setAmount(0);
            }
        }

        orderMigrationDTO.setTestObjectsQtyOriginal(trim(row.get(OrderMigrationExcel.TESTOBJECTSQTY.sequenceNo())));
        if (orderMigrationDTO.getTestObjectsQtyOriginal() != null) {
            List<String> testObjectsList = Arrays.asList(StringUtils.splitByWholeSeparator(orderMigrationDTO.getTestObjectsQtyOriginal(), "]"));
            List<String> testObjects = new ArrayList<String>(testObjectsList);
            List<String> empties = new ArrayList<String>();
            empties.add("");
            empties.add(null);
            testObjects.removeAll(empties);
            orderMigrationDTO.setTestObjectsQty(testObjects);
        }
        orderMigrationDTO.setTestObjectstotalQty(trim(row.get(OrderMigrationExcel.TEST_OBJECTS_TOTAL_QTY.sequenceNo())));

        String sparePartQtyOriginal = trim(row.get(OrderMigrationExcel.SPARE_PART_QTY.sequenceNo()));
        orderMigrationDTO.setSparePartQuantityOriginal(sparePartQtyOriginal);
        if (!StringUtils.isEmpty(sparePartQtyOriginal)) {
            try {
                orderMigrationDTO.setSparePartQuantity(Long.valueOf(sparePartQtyOriginal));
            } catch (NumberFormatException e) {
                markAsInvalid(orderMigrationDTO, "SPARE_PART_QTY is not a number!");
            }
        }
        orderMigrationDTO.setModularHarness(trim(row.get(OrderMigrationExcel.MODULAR_HARNESS.sequenceNo())));
        orderMigrationDTO.setReference(trim(row.get(OrderMigrationExcel.REFERENCE.sequenceNo())));
        orderMigrationDTO.setWarehouseSite(trim(row.get(OrderMigrationExcel.WAREHOUSE_SITE.sequenceNo())));

        orderMigrationDTO.setTestObjectsPulledOriginal(trim(row.get(OrderMigrationExcel.TEST_OBJECTS_PULLED.sequenceNo())));
        if (orderMigrationDTO.getTestObjectsPulledOriginal() != null) {
            List<String> testObjectsList = Arrays.asList(StringUtils.splitByWholeSeparator(orderMigrationDTO.getTestObjectsPulledOriginal(), "]"));
            List<String> testObjects = new ArrayList<String>(testObjectsList);
            List<String> empties = new ArrayList<String>();
            empties.add("");
            empties.add(null);
            testObjects.removeAll(empties);
            orderMigrationDTO.setTestObjectsPulled(testObjects);
        }

    }

    private void updateWithOrderInformation(List<String> row, OrderMigrationDTO orderMigrationDTO) {
        orderMigrationDTO.setOrdererId(trim(row.get(OrderMigrationExcel.ORDERID.sequenceNo())));
        orderMigrationDTO.setOrderNumber(trim(row.get(OrderMigrationExcel.ORDERNUMBER.sequenceNo())));
        orderMigrationDTO.setSuffix(trim(row.get(OrderMigrationExcel.SUFFIX.sequenceNo())));
        orderMigrationDTO.setBuyerCode(trim(row.get(OrderMigrationExcel.BUYERCODE.sequenceNo())));
        orderMigrationDTO.setBuyerName(trim(row.get(OrderMigrationExcel.BUYERNAME.sequenceNo())));
        String supplierCode = trim(row.get(OrderMigrationExcel.SUPPLIERCODE.sequenceNo()));
        orderMigrationDTO.setSupplierCode(supplierCode == null ? null : supplierCode.replaceAll("[^0-9.]|\\s+", ""));
        orderMigrationDTO.setSupplierName(trim(row.get(OrderMigrationExcel.SUPPLIERNAME.sequenceNo())));
        orderMigrationDTO.setPurchasingOrganization(trim(row.get(OrderMigrationExcel.PURCHASINGORGANISATION.sequenceNo())));
        orderMigrationDTO.setCurrency(trim(row.get(OrderMigrationExcel.CURRENCY.sequenceNo())));
        String shipToArrivalDateOriginal = trim(row.get(OrderMigrationExcel.SHIPARRIVEDATE.sequenceNo()));
        orderMigrationDTO.setShipArriveDateOriginal(shipToArrivalDateOriginal);
        if (!StringUtils.isEmpty(shipToArrivalDateOriginal) && !StringUtils.isEmpty(shipToArrivalDateOriginal.trim())) {
            orderMigrationDTO.setShipArriveDate(getDate(shipToArrivalDateOriginal, orderMigrationDTO));
        }
        String acceptedStaDateOriginal = trim(row.get(OrderMigrationExcel.ACCEPTEDSTADATE.sequenceNo()));
        orderMigrationDTO.setAcceptedStaDateOriginal(acceptedStaDateOriginal);
        setDate(orderMigrationDTO, acceptedStaDateOriginal, "ACCEPTED STA");

        String agreedStaDateOriginal = trim(row.get(OrderMigrationExcel.AGREEDSTADATE.sequenceNo()));
        orderMigrationDTO.setAgreedStaDateOriginal(agreedStaDateOriginal);
        setDate(orderMigrationDTO, agreedStaDateOriginal, "AGREED STA");

        String expectedStaDateOriginal = trim(row.get(OrderMigrationExcel.EXPECTEDSTADATE.sequenceNo()));
        orderMigrationDTO.setExpectedStaDateOriginal(expectedStaDateOriginal);
        setDate(orderMigrationDTO, expectedStaDateOriginal, "EXPECTED STA");

        orderMigrationDTO.setOrderLineStatusOriginal(trim(row.get(OrderMigrationExcel.ORDERLINESTATUS.sequenceNo())));
        orderMigrationDTO.setIntExtOrder(trim(row.get(OrderMigrationExcel.INTEXTORDER.sequenceNo())));
        orderMigrationDTO.setOrderFromGps(trim(row.get(OrderMigrationExcel.ORDERFROMGPS.sequenceNo())));
        String orderedQty = trim(row.get(OrderMigrationExcel.ORDEREDQUANTITY.sequenceNo()));
        orderMigrationDTO.setOrderedQtyoriginal(orderedQty);
        if (!StringUtils.isEmpty(orderedQty)) {
            try {
                orderMigrationDTO.setOrderedQuantity(Long.valueOf(orderedQty));
            } catch (NumberFormatException e) {
                markAsInvalid(orderMigrationDTO, "ORDERED_QTY is not a number!");
            }
        }
        String possibleToReceiveQty = trim(row.get(OrderMigrationExcel.POSSIBLETORECEIVEQUANTITY.sequenceNo()));
        orderMigrationDTO.setPossibleToReceiveQuantityOriginal(possibleToReceiveQty);
        if (!StringUtils.isEmpty(possibleToReceiveQty)) {
            try {
                orderMigrationDTO.setPossibleToReceiveQuantity(Long.valueOf(possibleToReceiveQty));
            } catch (NumberFormatException e) {
                markAsInvalid(orderMigrationDTO, "POSSIBLE_TO_RECEIVE_QTY is not a number!");
            }
        }
        String requisitionNumber = trim(row.get(OrderMigrationExcel.REQUISTIONNUMBER.sequenceNo()));
        orderMigrationDTO.setRequisitionNumberOriginal(requisitionNumber);
        if (!StringUtils.isEmpty(requisitionNumber)) {
            try {
                orderMigrationDTO.setRequisitionNumber(Long.valueOf(requisitionNumber));
            } catch (NumberFormatException e) {
                markAsInvalid(orderMigrationDTO, "REQUISTION_NUMBER is not a number!");
            }
        }
        String paidQuantityOriginal = trim(row.get(OrderMigrationExcel.PAID_QUANTIY.sequenceNo()));
        orderMigrationDTO.setPaidQuantityOriginal(paidQuantityOriginal);
        if (!StringUtils.isEmpty(paidQuantityOriginal)) {
            try {
                orderMigrationDTO.setPaidQuantity(Long.valueOf(paidQuantityOriginal));
            } catch (NumberFormatException e) {
                markAsInvalid(orderMigrationDTO, "PAID_QTY is not a number!");
            }
        }
        orderMigrationDTO.setIssuerName(trim(row.get(OrderMigrationExcel.ISSUER_NAME.sequenceNo())));
        orderMigrationDTO.setIssuerEmail(trim(row.get(OrderMigrationExcel.ISSUER_EMAIL.sequenceNo())));
        orderMigrationDTO.setContactPersonName(trim(row.get(OrderMigrationExcel.ORDERER_NAME.sequenceNo())));
        orderMigrationDTO.setContactPersonEmail(trim(row.get(OrderMigrationExcel.ORDERER_EMAIL.sequenceNo())));
        orderMigrationDTO.setObjectInformation(trim(row.get(OrderMigrationExcel.OBJECT_INFORMATION.sequenceNo())));
        orderMigrationDTO.setOrderInformation(trim(row.get(OrderMigrationExcel.ORDER_INFORMATION.sequenceNo())));

        String orderSiteOriginal = trim(row.get(OrderMigrationExcel.ORDER_SITE.sequenceNo()));
        orderMigrationDTO.setOrderSiteOriginal(orderSiteOriginal);
        String siteId = siteCodeToSiteId.get(orderSiteOriginal);

        orderMigrationDTO.setOrderSiteId(siteId);
        orderMigrationDTO.setId(id++);

        orderMigrationDTO.setSupplierPartNo(trim(row.get(OrderMigrationExcel.SUPPLIER_PART_NO.sequenceNo())));

        String agreedStaDateLastUpdatedOriginal = trim(row.get(OrderMigrationExcel.AGREED_STA_LAST_UPDATED.sequenceNo()));
        orderMigrationDTO.setAgreedSTALastUpdatedOriginal(agreedStaDateLastUpdatedOriginal);
        setDate(orderMigrationDTO, agreedStaDateLastUpdatedOriginal, "AGREED STA LAST UPDATED");

        String procuredDateOriginal = trim(row.get(OrderMigrationExcel.PROCUREDATE.sequenceNo()));
        orderMigrationDTO.setProcureDateOriginal(procuredDateOriginal);
        setDate(orderMigrationDTO, procuredDateOriginal, "PROCURE DATE");

        orderMigrationDTO.setOrderState(trim(row.get(OrderMigrationExcel.ORDER_STATE.sequenceNo())));
    }

    private void readReceivedLineItemForAnOrderLine(List<String> row, String itemId, OrderMigrationDTO orderMigrationDTO) throws ParseException {
        OrderReceivedDTO orderReceivedDTO = new OrderReceivedDTO();
        orderReceivedDTO.setItemId(itemId);
        orderReceivedDTO.setLineType(row.get(OrderMigrationExcel.LINETYPE.sequenceNo()).trim());
        String receivedQuantity = trim(row.get(OrderMigrationExcel.RECEIVEDQUANTITY.sequenceNo()));
        orderReceivedDTO.setReceivedQuantityorginal(receivedQuantity);
        if (!StringUtils.isEmpty(receivedQuantity)) {
            try {
                orderReceivedDTO.setReceivedQuantity(Long.valueOf(receivedQuantity));
            } catch (NumberFormatException e) {
                markAsInvalid(orderMigrationDTO, "RECEIVED_QTY is not a number!");
            }
        }
        String receivalDateOriginal = trim(row.get(OrderMigrationExcel.RECEIVEDDATE.sequenceNo()));
        if (!StringUtils.isEmpty(receivalDateOriginal) && !StringUtils.isEmpty(receivalDateOriginal.trim())) {
            orderReceivedDTO.setReceivalDateOriginal(receivalDateOriginal);
            receivalDateOriginal = receivalDateOriginal.replace("/", "-");
            orderReceivedDTO.setReceivalDate(DateUtil.getStringAsDate(receivalDateOriginal, DATE_TIME_PATTERN));
        }
        orderReceivedDTO.setPackingSlipNumber(trim(row.get(OrderMigrationExcel.PACKING_SLIP_NUMBER.sequenceNo)));
        orderMigrationDTO.setPackingSlipNumbersForReceivals(orderReceivedDTO.getPackingSlipNumber());
        orderReceivedDTO.setOrderLinePaid(trim(row.get(OrderMigrationExcel.ORDERLINEPAID.sequenceNo())));
        orderMigrationDTO.getOrderReceivedList().add(orderReceivedDTO);
    }

    private void validateOrderDate(String dateValue, OrderMigrationDTO orderMigrationDTO) {
        orderMigrationDTO.setOrderDateOriginal(dateValue);

        // check dateValue
        if (StringUtils.isEmpty(dateValue)) {
            markAsInvalid(orderMigrationDTO, "ORDER DATE MISSING");
        }

        if(dateValue == null) {
            System.out.println("");
        }
        
        // check date format
        if (!isValidDateFormat(dateValue, DATE_TIME_PATTERN)) {
            markAsInvalid(orderMigrationDTO, "INVALID ORDER DATE");
        }

        setDate(orderMigrationDTO, dateValue, "ORDER DATE");

        // check range for both Closed & Open orders
        if (!isValidOrderDateRange(orderMigrationDTO.getOrderType().trim().equalsIgnoreCase("CLOSED"), orderMigrationDTO.getOrderDate())) {
            markAsInvalid(orderMigrationDTO, "ORDER DATE QUITE OLDER");
        }
    }

    private boolean isValidOrderDateRange(boolean aClosedOrder, Date orderDate) {
        if (aClosedOrder) {
            return DateUtil.isDateAfterPreviousYear(orderDate, VALID_YEAR_CLOSEDORDER, VALID_MONTH_CLOSEDORDER, VALID_DAY_CLOSEDORDER);
        }
        return DateUtil.isDateAfterPreviousYear(orderDate, VALID_YEAR_OPENORDER, VALID_MONTH_OPENORDER, VALID_DAY_OPENORDER);
    }

    private boolean isValidDateFormat(String dateValue, String dateTimePattern) {
        String formattedDateValue = dateValue.replace("/", "-");
        try {
            DateUtil.getStringAsDate(formattedDateValue, DATE_TIME_PATTERN);
        } catch (ParseException parseException) {
            return false;
        }
        return true;
    }

    private void setDate(OrderMigrationDTO orderMigrationDTO, String date, String dateName) {
        if (!StringUtils.isEmpty(date) && !StringUtils.isEmpty(date.trim())) {
            date = date.replace("/", "-");
            try {
                if ("ACCEPTED STA".equalsIgnoreCase(dateName)) {
                    orderMigrationDTO.setAcceptedStaDate(DateUtil.getStringAsDate(date, DATE_TIME_PATTERN));
                } else if ("AGREED STA".equalsIgnoreCase(dateName)) {
                    orderMigrationDTO.setAgreedStaDate(DateUtil.getStringAsDate(date, DATE_TIME_PATTERN));
                } else if ("EXPECTED STA".equalsIgnoreCase(dateName)) {
                    orderMigrationDTO.setExpectedStaDate(DateUtil.getStringAsDate(date, DATE_TIME_PATTERN));
                } else if ("AGREED STA LAST UPDATED".equalsIgnoreCase(dateName)) {
                    orderMigrationDTO.setAgreedSTALastUpdated(DateUtil.getStringAsDate(date, DATE_TIME_PATTERN));
                } else if ("PROCURE DATE".equalsIgnoreCase(dateName)) {
                    orderMigrationDTO.setProcureDate(DateUtil.getStringAsDate(date, DATE_TIME_PATTERN));
                } else if ("ORDER DATE".equalsIgnoreCase(dateName)) {
                    orderMigrationDTO.setOrderDate(DateUtil.getStringAsDate(date, DATE_TIME_PATTERN));
                }
            } catch (ParseException parseException) {
                markAsInvalid(orderMigrationDTO, "INVALID " + dateName + " DATE!");
            }
        }
    }

    private static Date getDate(String date, OrderMigrationDTO openOrderMigrationDTO) {
        if (date == null) {
            return null;
        }
        try {
            final int dayInWeek = 7;
            date = date.replaceAll("'", "");
            String[] split = date.split("-");
            int year = Integer.valueOf(split[0]);
            int week = Integer.valueOf(split[1]);
            int dayOfweek = (Integer.valueOf(split[2]) + 1) % dayInWeek;

            return DateUtil.getDateFromWeekNo(year, week, dayOfweek);
        } catch (Exception e) {
            openOrderMigrationDTO.setReason("INVALID SHIP TO ARRIVE DATE FORMAT!");
        }
        return null;
    }

    @Override
    public Set<OrderMigrationDTO> getResult() {
        return resultDTO;
    }

    enum OrderMigrationExcel implements ExcelColumnHeader {
        ITEMID(0, headerArray.next()), 
        LINETYPE(1, headerArray.next()), 
        ORDERNUMBER(2, headerArray.next()), 
        ORDERDATE(3, headerArray.next()), 
        SUFFIX(4, headerArray.next()),                
        PARTQUALIFER(5, headerArray.next()), 
        PARTNUMBER(6, headerArray.next()), 
        DESCRIPTION(7, headerArray.next()), 
        PARTVERSION(8, headerArray.next()), 
        UNITOFMEASURE(9, headerArray.next()), 
        PRICE(10, headerArray.next()), 
        UNITOFPRICE(11, headerArray.next()), 
        AMOUNT(12, headerArray.next()), 
        CURRENCY(13, headerArray.next()), 
        MATERIALUSERID(14, headerArray.next()), 
        PURCHASINGORGANISATION(15, headerArray.next()), 
        BUYERCODE(16, headerArray.next()), 
        BUYERNAME(17, headerArray.next()), 
        PROJECT(18, headerArray.next()), 
        COSTCENTER(19, headerArray.next()), 
        GLACCOUNT(20, headerArray.next()), 
        WBSELEMENT(21, headerArray.next()), 
        SUPPLIERCODE(22, headerArray.next()), 
        SUPPLIERNAME(23, headerArray.next()), 
        INSPECTION(24, headerArray.next()), 
        INTERNALFLOW(25, headerArray.next()), 
        ISSUERID(26, headerArray.next()), 
        ORDERID(27, headerArray.next()), 
        NOTE(28, headerArray.next()), 
        REQUISTIONNUMBER(29, headerArray.next()), 
        SHIPARRIVEDATE(30, headerArray.next()), 
        ACCEPTEDSTADATE(31, headerArray.next()), 
        AGREEDSTADATE(32, headerArray.next()), 
        EXPECTEDSTADATE(33, headerArray.next()), 
        ORDERLINESTATUS(34, headerArray.next()), 
        INTEXTORDER(35, headerArray.next()), 
        ORDERFROMGPS(36, headerArray.next()), 
        ORDERLINEPAID(37, headerArray.next()), 
        ORDEREDQUANTITY(38, headerArray.next()), 
        POSSIBLETORECEIVEQUANTITY(39, headerArray.next()), 
        RECEIVEDQUANTITY(40, headerArray.next()), 
        RECEIVEDDATE(41, headerArray.next()), 
        TESTOBJECTSQTY(42, headerArray.next()), 
        TEST_OBJECTS_TOTAL_QTY(43, headerArray.next()), 
        PAID_QUANTIY(44, headerArray.next()), 
        PPA_ID(45, headerArray.next()), 
        REFERENCE(46, headerArray.next()), 
        SPARE_PART_QTY(47, headerArray.next()), 
        ISSUER_NAME(48, headerArray.next()), 
        ISSUER_EMAIL(49, headerArray.next()), 
        ORDERER_NAME(50, headerArray.next()), 
        ORDERER_EMAIL(51, headerArray.next()), 
        SUPPLIER_PART_NO(52, headerArray.next()), 
        AGREED_STA_LAST_UPDATED(53, headerArray.next()), 
        MODULAR_HARNESS(54, headerArray.next()), 
        PROCUREDATE(55, headerArray.next()), 
        OBJECT_INFORMATION(56, headerArray.next()), 
        ORDER_INFORMATION(57, headerArray.next()), 
        PACKING_SLIP_NUMBER(58, headerArray.next()),
        ORDER_SITE(59, headerArray.next()), 
        WAREHOUSE_SITE(60, headerArray.next()), 
        TEST_OBJECTS_PULLED(61, headerArray.next()), 
        ORDER_STATE(62, headerArray.next()), 
        ORDER_TYPE(63, headerArray.next()), 
        MIGRATED(64, headerArray.next()), 
        ISSENDPPO(65, headerArray.next()), 
        ISSENDGM(66, headerArray.next()), 
        REASON(67, headerArray.next()), 
        REASON_IT(68, headerArray.next());

  private int sequenceNo;
        private String columnName;

        OrderMigrationExcel(int sequenceNo, String columnName) {
            this.sequenceNo = sequenceNo;
            this.columnName = columnName;
        }

        public int sequenceNo() {
            return sequenceNo;
        }

        public String columnName() {
            return columnName;
        }
    }

    private void setAllSites() {
        siteCodeToSiteId.put("GOT_3P", "664");
        siteCodeToSiteId.put("SKO_XX", "1620");
        siteCodeToSiteId.put("GOT_PT", "1722");
        siteCodeToSiteId.put("CUR_XX", "47670");
        siteCodeToSiteId.put("MAL_XX", "2919");
        siteCodeToSiteId.put("GSO_3P", "4042");
        siteCodeToSiteId.put("AGE_PT", "8374");
        siteCodeToSiteId.put("VIP_XX", "8410");
        siteCodeToSiteId.put("VEC_PT", "8418");
        siteCodeToSiteId.put("LYO_3P", "34347");
        siteCodeToSiteId.put("BLR_3P", "42102");
        siteCodeToSiteId.put("HAG_XX", "45907");
        siteCodeToSiteId.put("DND_XX", "8411");
        siteCodeToSiteId.put("TMB_XX", "8417");
        siteCodeToSiteId.put("TSA_XX", "7876");
    }
}
