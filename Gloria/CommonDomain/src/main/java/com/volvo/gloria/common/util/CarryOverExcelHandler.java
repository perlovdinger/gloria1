package com.volvo.gloria.common.util;

import static com.volvo.gloria.util.DateUtil.getStringAsDate;

import java.io.InputStream;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.volvo.gloria.common.c.CarryOverActionType;
import com.volvo.gloria.common.carryover.c.dto.CarryOverItemDTO;
import com.volvo.gloria.common.carryover.c.dto.SyncPurchaseOrderCarryOverDTO;
import com.volvo.gloria.util.excel.ExcelColumnHeader;
import com.volvo.gloria.util.excel.ExcelHandler;

public class CarryOverExcelHandler extends ExcelHandler<SyncPurchaseOrderCarryOverDTO> {
    private static final String STARTDATE_PATTERN = "yyyyMMdd";
    private SyncPurchaseOrderCarryOverDTO resultDTO;
    private List<CarryOverItemDTO> carryOverItemDTOs;

    public CarryOverExcelHandler(String filename) {
        super(filename, getColumnList());

    }
    public CarryOverExcelHandler(InputStream is) {
        super(is, getColumnList());

    }

    private static List<String> getColumnList() {
        List<String> columnList = new ArrayList<String>();
        CarryOverExcel[] values = CarryOverExcel.values();
        for (CarryOverExcel column : values) {
            columnList.add(column.columnName());
        }
        return columnList;
    }

    public void manageRow(List<String> row) throws ParseException {
        if (resultDTO == null) {
            resultDTO = new SyncPurchaseOrderCarryOverDTO();
            resultDTO.setAction(CarryOverActionType.LOAD.toString());
            carryOverItemDTOs = new ArrayList<CarryOverItemDTO>();
            resultDTO.setCarryOverItemDTOs(carryOverItemDTOs);
        }
        CarryOverItemDTO carryOverItemDTO = new CarryOverItemDTO();
        carryOverItemDTOs.add(carryOverItemDTO);
        readPurchaseOrderHeader(carryOverItemDTO, row);
        readPurchaseOrderLine(carryOverItemDTO, row);
    }

    private void readPurchaseOrderLine(CarryOverItemDTO carryOverItemDTO, List<String> row) throws ParseException {
        carryOverItemDTO.setCustomerId(row.get(CarryOverExcel.customerId.sequenceNo()));
        carryOverItemDTO.setCustomerName((String) row.get(CarryOverExcel.customerName.sequenceNo()));
        carryOverItemDTO.setSupplierId(row.get(CarryOverExcel.supplierId.sequenceNo()));
        carryOverItemDTO.setSupplierName((String) row.get(CarryOverExcel.supplierName.sequenceNo()));
        carryOverItemDTO.setSupplierCountryCode((String) row.get(CarryOverExcel.supplierCountryCode.sequenceNo()));
        String startDate = row.get(CarryOverExcel.startDate.sequenceNo());
        if (startDate != null) {
            carryOverItemDTO.setStartDate(new Date(getStringAsDate(startDate, STARTDATE_PATTERN).getTime()));
        }
    }

    private void readPurchaseOrderHeader(CarryOverItemDTO carryOverItemDTO, List<String> row) {
        carryOverItemDTO.setPartAffliation((String) row.get(CarryOverExcel.partAffiliation.sequenceNo()));
        carryOverItemDTO.setPartNumber(row.get(CarryOverExcel.partNumber.sequenceNo()).trim());
        carryOverItemDTO.setPartVersion((String) row.get(CarryOverExcel.partVersion.sequenceNo()));
        carryOverItemDTO.setUnitCode((String) row.get(CarryOverExcel.unitCode.sequenceNo()));
        carryOverItemDTO.setCurrency((String) row.get(CarryOverExcel.currency.sequenceNo()));
        String amount = row.get(CarryOverExcel.amount.sequenceNo());
        if (!StringUtils.isEmpty(amount)) {
            carryOverItemDTO.setAmount(Double.valueOf(amount.replace(",", ".")));
        }
        carryOverItemDTO.setPriceUnit((String) row.get(CarryOverExcel.priceUnit.sequenceNo()));

        carryOverItemDTOs.add(carryOverItemDTO);
    }

    @Override
    public SyncPurchaseOrderCarryOverDTO getResult() {
        return resultDTO;
    }

    enum CarryOverExcel implements ExcelColumnHeader {
        customerId(0, "IDUSER"), customerName(1, "NMUSER"), supplierId(2, "IDSUPPL"), supplierName(3, "NMSUPPL_OFFICIAL"), supplierCountryCode(4, "CDCTRY2"), 
        partNumber(5, "Part Number"), partAffiliation(6, "Part Qualifier"), partVersion(7, "IDISSUE"), unitCode(8, "CDUOM"), currency(9, "CDCURR"), amount(10,
        "PRORDER"), priceUnit(11, "CDUOP"), startDate(12, "TIPRICE_VALID"), orderId(13, "IDORDER");

        private int sequenceNo;
        private String columnName;

        CarryOverExcel(int sequenceNo, String columnName) {
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
