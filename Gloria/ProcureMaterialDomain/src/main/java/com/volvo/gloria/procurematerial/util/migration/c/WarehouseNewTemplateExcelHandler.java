package com.volvo.gloria.procurematerial.util.migration.c;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.volvo.gloria.procurematerial.util.migration.c.dto.WarehouseMigrationDTO;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.gloria.util.excel.ExcelColumnHeader;
import com.volvo.gloria.util.excel.ExcelHandler;

/**
 * Helper class for to convert warehouse new template migration excel to dto.
 */
public class WarehouseNewTemplateExcelHandler extends ExcelHandler<List<WarehouseMigrationDTO>> {

    public static final String HEADER = "WAREHOUSE_SITE;BIN_LOCATION_CODE;"
            + "STA_DATE;ORDER_NUMBER;ORDER_DATE;PART_AFFILIATION;PART_NUMBER;STAGE;ISSUE;DESCRIPTION;"
            + "PROJECT;REFERENCE;STORAGE_QTY;TEST_OBJECTS_QTY;UNIT_OF_MEASURE;SPLITTED;BINLOCATION_SPLIT;Migrated;Reason;Reason_IT";

    /** Do not try this at home! **/
    private static Iterator<String> headerArray = Arrays.asList(HEADER.split(";")).iterator();

    private List<WarehouseMigrationDTO> resultDTO;
    private String siteId;
    private static final String MISSING_PART_NAME = "Missing";
    private static final String DATE_TIME_PATTERN = "MM-dd-yyyy HH:mm:ss";
    private static Map<String, String> siteCodeToSiteId = new HashMap<String, String>();
    private Map<String, WarehouseMigrationDTO> splittedPartsDTO = null;
    private boolean mergeSplittedParts = true;

    public WarehouseNewTemplateExcelHandler(String filename, boolean mergeSplittedParts) {
        super(filename, getColumnList());
        this.mergeSplittedParts = mergeSplittedParts;
        setAllSites();
    }

    public WarehouseNewTemplateExcelHandler(InputStream ins) {
        super(ins, getColumnList());
        setAllSites();
    }

    public static List<String> getColumnList() {
        List<String> columnList = new ArrayList<String>();
        WarehouseMigrationExcel[] values = WarehouseMigrationExcel.values();
        for (WarehouseMigrationExcel column : values) {
            columnList.add(column.columnName());
        }
        return columnList;
    }

    public void manageRow(List<String> row) throws ParseException {
        if (resultDTO == null) {
            resultDTO = new ArrayList<WarehouseMigrationDTO>();
        }

        if (mergeSplittedParts && splittedPartsDTO == null) {
            splittedPartsDTO = new HashMap<String, WarehouseMigrationDTO>();
        }

        WarehouseMigrationDTO warehouseMigrationDTO = new WarehouseMigrationDTO();
        siteId = siteCodeToSiteId.get(row.get(WarehouseMigrationExcel.warehouseSiteId.sequenceNo()));
        warehouseMigrationDTO.setWarehouseSiteId(row.get(WarehouseMigrationExcel.warehouseSiteId.sequenceNo()));
        warehouseMigrationDTO.setSiteId(siteId);
        warehouseMigrationDTO.setBinLocationCode(row.get(WarehouseMigrationExcel.binLocationCode.sequenceNo()).trim());
        warehouseMigrationDTO.setStaDate(row.get(WarehouseMigrationExcel.requiredStaDate.sequenceNo()));
        warehouseMigrationDTO.setRequiredStaDate(getDate(row.get(WarehouseMigrationExcel.requiredStaDate.sequenceNo())));
        warehouseMigrationDTO.setOrderNo(row.get(WarehouseMigrationExcel.orderNo.sequenceNo()));
        Date orderDate = null;
        String orderDateOriginal = row.get(WarehouseMigrationExcel.orderDate.sequenceNo());
        if (!StringUtils.isEmpty(orderDateOriginal) && !StringUtils.isEmpty(orderDateOriginal.trim())) {
            warehouseMigrationDTO.setOrderDateOriginal(orderDateOriginal);
            orderDateOriginal = orderDateOriginal.replace("/", "-");
            try {
                orderDate = DateUtil.getStringAsDate(orderDateOriginal, DATE_TIME_PATTERN);
                warehouseMigrationDTO.setOrderDate(orderDate);
            } catch (Exception e) {
                warehouseMigrationDTO.setReason("INVALID ORDER DATE!");
            }
        }

        String partNumber = row.get(WarehouseMigrationExcel.partNumber.sequenceNo());
        if (!StringUtils.isEmpty(partNumber)) {
            warehouseMigrationDTO.setPartNumber(partNumber.trim());
        }
        String partAffiliation = row.get(WarehouseMigrationExcel.partAffiliation.sequenceNo());
        warehouseMigrationDTO.setPartAffiliationOriginal(partAffiliation);
        if (StringUtils.isEmpty(partAffiliation)) {
            partAffiliation = GloriaParams.PART_AFFILIATION_NON_VOLVO;
        }
        warehouseMigrationDTO.setPartAffiliation(partAffiliation.trim());
        warehouseMigrationDTO.setStage(row.get(WarehouseMigrationExcel.stage.sequenceNo()));
        warehouseMigrationDTO.setIssue(row.get(WarehouseMigrationExcel.issue.sequenceNo()));
        warehouseMigrationDTO.setPartVersion(row.get(WarehouseMigrationExcel.stage.sequenceNo()).trim()
                + row.get(WarehouseMigrationExcel.issue.sequenceNo()).trim());
        String partName = row.get(WarehouseMigrationExcel.partName.sequenceNo());
        warehouseMigrationDTO.setPartNameOriginal(partName);
        if (partName == null || ("").equals(partName)) {
            partName = MISSING_PART_NAME;
        }
        warehouseMigrationDTO.setPartName(partName.trim());
        warehouseMigrationDTO.setProjectId(row.get(WarehouseMigrationExcel.projectId.sequenceNo()));
        warehouseMigrationDTO.setReference(row.get(WarehouseMigrationExcel.reference.sequenceNo()));
        String quantity = row.get(WarehouseMigrationExcel.quantity.sequenceNo());
        warehouseMigrationDTO.setStorageQuantity(quantity);
        if (!StringUtils.isEmpty(quantity)) {
            try {
                warehouseMigrationDTO.setQuantity(Long.valueOf(quantity.replaceAll("[^0-9.]|\\s+", "")));
            } catch (Exception e) {
                warehouseMigrationDTO.setReason("INVALID QUANTITY!");
            }
        }
        warehouseMigrationDTO.setTestObject(row.get(WarehouseMigrationExcel.testObject.sequenceNo()));
        if (warehouseMigrationDTO.getTestObject() != null) {
            List<String> testObjectsList = Arrays.asList(org.apache.commons.lang.StringUtils.splitByWholeSeparator(warehouseMigrationDTO.getTestObject(), "]"));
            List<String> testObjects = new ArrayList<String>(testObjectsList);
            List<String> empties = new ArrayList<String>();
            empties.add("");
            empties.add(null);
            testObjects.removeAll(empties);
            warehouseMigrationDTO.setTestObjectsQty(testObjects);
        }
        warehouseMigrationDTO.setUnitOfMeasure(row.get(WarehouseMigrationExcel.unitOfMeasure.sequenceNo()));
        warehouseMigrationDTO.setSplitted(Boolean.valueOf(row.get(WarehouseMigrationExcel.splitted.sequenceNo())));

        if (warehouseMigrationDTO.isSplitted() && mergeSplittedParts) {
            String orderPartIdentifierKey = warehouseMigrationDTO.getOrderPartIdentifierKey();
            if (splittedPartsDTO.containsKey(orderPartIdentifierKey)) {
                WarehouseMigrationDTO existingDTO = splittedPartsDTO.get(orderPartIdentifierKey);
                existingDTO.setBinlocationSplit(warehouseMigrationDTO.getBinLocationCode(), warehouseMigrationDTO.getQuantity());
            } else {
                splittedPartsDTO.put(orderPartIdentifierKey, warehouseMigrationDTO);
                resultDTO.add(warehouseMigrationDTO);
            }
        } else {
            resultDTO.add(warehouseMigrationDTO);
        }
    }

    private static Date getDate(String date) {
        if (date == null) {
            return null;
        }
        final int dayInWeek = 7;
        String[] split = date.split("-");
        int year = Integer.valueOf(split[0]);
        int week = Integer.valueOf(split[1]);
        int dayOfweek = (Integer.valueOf(split[2]) + 1) % dayInWeek;

        return DateUtil.getDateFromWeekNo(year, week, dayOfweek);
    }

    @Override
    public List<WarehouseMigrationDTO> getResult() {
        return resultDTO;
    }

    enum WarehouseMigrationExcel implements ExcelColumnHeader {
        warehouseSiteId(0, headerArray.next()), binLocationCode(1, headerArray.next()), requiredStaDate(2, headerArray.next()), 
        orderNo(3, headerArray.next()), orderDate(4, headerArray.next()), partAffiliation(5, headerArray.next()), partNumber(6, headerArray.next()), 
        stage(7, headerArray.next()), issue(8, headerArray.next()), partName(9, headerArray.next()), projectId(10, headerArray.next()), 
        reference(11, headerArray.next()), quantity(12, headerArray.next()), testObject(13, headerArray.next()), unitOfMeasure(14, headerArray.next()), 
        splitted(15, headerArray.next()), binlocationsplitted(16, headerArray.next()), migrated(17, headerArray.next()), reason(18, headerArray.next()), 
        reasonIT(19, headerArray.next());

        private int sequenceNo;
        private String columnName;

        WarehouseMigrationExcel(int sequenceNo, String columnName) {
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
