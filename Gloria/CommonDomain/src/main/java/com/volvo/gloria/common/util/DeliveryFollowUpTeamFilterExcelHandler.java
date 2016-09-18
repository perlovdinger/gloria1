package com.volvo.gloria.common.util;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamFilterDTO;
import com.volvo.gloria.util.excel.ExcelColumnHeader;
import com.volvo.gloria.util.excel.ExcelHandler;

/**
 * load the filters from excel.
 * 
 */
public class DeliveryFollowUpTeamFilterExcelHandler extends ExcelHandler<List<DeliveryFollowUpTeamFilterDTO>> {

    private List<DeliveryFollowUpTeamFilterDTO> results;

    public DeliveryFollowUpTeamFilterExcelHandler(InputStream ins) {
        super(ins, getColumnList());
    }

    @Override
    public void manageRow(List<String> row) throws ParseException {
        if (results == null || results.isEmpty()) {
            results = new ArrayList<DeliveryFollowUpTeamFilterDTO>();
        }
        String parmaCode = row.get(DeliveryFollowUpTeamFilterExcel.PARMA_CODE.sequenceNo());
        String dcUserId = row.get(DeliveryFollowUpTeamFilterExcel.DELIVERYCONTROLLER_ID.sequenceNo());
        if (!StringUtils.isEmpty(parmaCode) && !StringUtils.isEmpty(dcUserId)) {
            DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilter = new DeliveryFollowUpTeamFilterDTO();
            deliveryFollowUpTeamFilter.setSupplierId(parmaCode);
            deliveryFollowUpTeamFilter.setDeliveryControllerUserId(dcUserId);
            results.add(deliveryFollowUpTeamFilter);
        }
    }

    @Override
    public List<DeliveryFollowUpTeamFilterDTO> getResult() {
        return results;
    }

    private static List<String> getColumnList() {
        List<String> columnList = new ArrayList<String>();
        DeliveryFollowUpTeamFilterExcel[] values = DeliveryFollowUpTeamFilterExcel.values();
        for (DeliveryFollowUpTeamFilterExcel column : values) {
            columnList.add(column.columnName());
        }
        return columnList;
    }

    enum DeliveryFollowUpTeamFilterExcel implements ExcelColumnHeader {

        PARMA_CODE(0, "PARMA_CODE"), SUPPLIER_NAME(1, "SUPPLIER_NAME"), DELIVERYCONTROLLER_ID(2, "DELIVERYCONTROLLER_ID"), DELIVERYCONTROLLER_NAME(3,
                "DELIVERYCONTROLLER_NAME");

        private int sequenceNo;
        private String columnName;

        DeliveryFollowUpTeamFilterExcel(int sequenceNo, String columnName) {
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
}
