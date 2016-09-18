package com.volvo.gloria.procurematerial.c.export;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.volvo.gloria.procurematerial.c.DeliveryStatusFlag;
import com.volvo.gloria.procurematerial.d.entities.DeliverySchedule;
import com.volvo.gloria.procurematerial.d.entities.EventLog;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLog;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.OrderLog;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.util.DeliveryHelper;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.excel.ExcelOperationUtil;

/**
 * to support export of 'Supplier' information from External Orders.
 * 
 */
public final class ExportSupplier extends ExcelExport {

    private List<OrderLine> orderLines = null;
    private boolean includeDeliveryController = false;
    private boolean includeOrderLineLogs = false;
    private boolean includeOrderLogs = false;
    private boolean includeProject = false;
    private boolean includePlannedDispatchDate = false;
    private boolean excludeAgreedSTA = false;

    public ExportSupplier(List<OrderLine> orderLines, boolean includeDeliveryController, boolean includeOrderLineLogs, boolean includeOrderLogs,
            boolean includeProject, boolean includePlannedDispatchDate, boolean excludeAgreedSTA) {
        this.orderLines = orderLines;
        this.includeDeliveryController = includeDeliveryController;
        this.includeOrderLineLogs = includeOrderLineLogs;
        this.includeOrderLogs = includeOrderLogs;
        this.includeProject = includeProject;
        this.includePlannedDispatchDate = includePlannedDispatchDate;
        this.excludeAgreedSTA = excludeAgreedSTA;
    }

    public FileToExportDTO export() {
        return super.export("Supplier");
    }

    @Override
    public void setUpHeader() {
        if (includeDeliveryController) {
            getHeaders().add("Delivery Controller");
        }
        getHeaders().add("Supplier Parma ID");
        getHeaders().add("Order No.");
        getHeaders().add("Part No.");
        getHeaders().add("Alias");
        getHeaders().add("Version");
        getHeaders().add("Part Name");
        getHeaders().add("Reference");
        getHeaders().add("Build Start");
        if (includeProject) {
            getHeaders().add("Project");
        }
        getHeaders().add("PO Date");
        getHeaders().add("Supplier Name");
        getHeaders().add("Possible To Receive Qty");
        getHeaders().add("Part Modification");

        getHeaders().add("Order Qty");
        getHeaders().add("Received Qty");
        getHeaders().add("Order STA");
        getHeaders().add("STA Accepted");
        if (!excludeAgreedSTA) {
            getHeaders().add("Agreed STA");
        }
        if (includePlannedDispatchDate) {
            getHeaders().add("Planned Dispatch date");
        }
        getHeaders().add("Expected quantity");
        getHeaders().add("Expected Arrival");
        getHeaders().add("Flag");
        getHeaders().add("Volvo Comments");
        getHeaders().add("Required STA");
        getHeaders().add("Event time");
        getHeaders().add("Supplier Comments");
        if (includeOrderLineLogs) {
            getHeaders().add("Orderline Log");
        }
        if (includeOrderLogs) {
            getHeaders().add("Order Log");
        }
    }

    @Override
    public void setUpData() {
        for (OrderLine orderLine : orderLines) {
            int cellIndex = 0;

            XSSFRow row = ExcelOperationUtil.createRow(getSheet(), getSheet().getPhysicalNumberOfRows());

            Order order = orderLine.getOrder();
            if (includeDeliveryController) {
                cellIndex = createCellForUpdateInExcelCells(getSheet(), orderLine.getDeliveryControllerUserName(), cellIndex, row, false);
            }

            cellIndex = createCellForUpdateInExcelCells(getSheet(), order.getSupplierId(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), order.getOrderNo(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), orderLine.getPartNumber(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), orderLine.getSupplierPartNo(), cellIndex, row, false);
            OrderLineVersion currentOrderLineVersion = orderLine.getCurrent();
            cellIndex = createCellForUpdateInExcelCells(getSheet(), currentOrderLineVersion.getPartVersion(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), orderLine.getPartName(), cellIndex, row, false);

            String outboundStartDateString = "";
            List<String> referenceIDs = new ArrayList<String>();
            String partModification = "";
            for (Material material : orderLine.getMaterials()) {
                partModification =material.getPartModification();
                MaterialHeader materialHeader = material.getMaterialHeader();
                if (materialHeader != null) {
                    String referenceId = materialHeader.getReferenceId();
                    Date outboundStartDate = materialHeader.getAccepted().getOutboundStartDate();
                    if (outboundStartDate != null) {
                        outboundStartDateString = DateUtil.getDateWithoutTimeAsString(outboundStartDate);                        
                    }
                    if (referenceId != null && !referenceIDs.contains(referenceId)) {
                        referenceIDs.add(referenceId);
                    }
                }
            }
            cellIndex = createCellForUpdateInExcelCells(getSheet(), StringUtils.join(referenceIDs, ","), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), outboundStartDateString, cellIndex, row, false);

            if (includeProject) {
                cellIndex = createCellForUpdateInExcelCells(getSheet(), orderLine.getProjectId(), cellIndex, row, false);
            }

            String orderDate = "";
            if (order.getOrderDateTime() != null) {
                orderDate = DateUtil.getDateWithoutTimeAsString(order.getOrderDateTime());
            }
            cellIndex = createCellForUpdateInExcelCells(getSheet(), orderDate, cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), order.getSupplierName(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), String.valueOf(orderLine.getPossibleToReceiveQuantity()), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), partModification, cellIndex, row, false);
                        
            cellIndex = createCellForUpdateInExcelCells(getSheet(), String.valueOf(currentOrderLineVersion.getQuantity()), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), String.valueOf(orderLine.getReceivedQuantity()), cellIndex, row, false);

            String orderStaDate = "";
            if (currentOrderLineVersion.getOrderStaDate() != null) {
                orderStaDate = DateUtil.getDateWithoutTimeAsString(currentOrderLineVersion.getOrderStaDate());
            }
            cellIndex = createCellForUpdateInExcelCells(getSheet(), orderStaDate, cellIndex, row, false);

            String staAcceptedDate = "";
            if (currentOrderLineVersion.getStaAcceptedDate() != null) {
                staAcceptedDate = DateUtil.getDateWithoutTimeAsString(currentOrderLineVersion.getStaAcceptedDate());
            }
            cellIndex = createCellForUpdateInExcelCells(getSheet(), staAcceptedDate, cellIndex, row, false);

            if (!excludeAgreedSTA) {
                String agreedSTA = "";
                if (currentOrderLineVersion.getStaAgreedDate() != null) {
                    agreedSTA = DateUtil.getDateWithoutTimeAsString(currentOrderLineVersion.getStaAgreedDate());
                }
                cellIndex = createCellForUpdateInExcelCells(getSheet(), agreedSTA, cellIndex, row, false);
            }

            cellIndex = setDeliverySchedulesDataToExcel(includePlannedDispatchDate, getSheet(), orderLine, cellIndex, row);

            String volvoComments = "";
            Date requiredStaDate = null;
            ProcureLine procureLine = orderLine.getProcureLine();
            if (procureLine != null) {
                volvoComments = procureLine.getWarehouseComment();
                requiredStaDate = procureLine.getRequiredStaDate();
            }
            cellIndex = createCellForUpdateInExcelCells(getSheet(), volvoComments, cellIndex, row, false);
            String requiredStaDateString = "";
            if (requiredStaDate != null) {
                requiredStaDateString = DateUtil.getDateWithoutTimeAsString(requiredStaDate);
            }
            cellIndex = createCellForUpdateInExcelCells(getSheet(), requiredStaDateString, cellIndex, row, false);

            String eventTimeString = "";
            Date eventTime = DeliveryHelper.getOrderLineLogEventTime(orderLine.getOrderLineLog());
            if (eventTime != null) {
                eventTimeString = DateUtil.getDateWithoutTimeAsString(eventTime);
            }
            cellIndex = createCellForUpdateInExcelCells(getSheet(), eventTimeString, cellIndex, row, false);

            
            
            cellIndex = createCellForUpdateInExcelCells(getSheet(), orderLine.getFreeText(), cellIndex, row, false);

            if (includeOrderLineLogs) {
                List<OrderLineLog> orderLineLogs = orderLine.getOrderLineLog();
                String logs = getLogs(orderLineLogs);
                cellIndex = createCellForUpdateInExcelCells(getSheet(), logs, cellIndex, row, true);
            }

            if (includeOrderLogs) {
                List<OrderLog> orderLogs = orderLine.getOrder().getOrderLog();
                String logs = getOrderLogs(orderLogs);
                cellIndex = createCellForUpdateInExcelCells(getSheet(), logs, cellIndex, row, true);
            }
        }
    }

    private String getOrderLogs(List<OrderLog> orderLogs) {
        String logs = "";
        if (orderLogs != null && !orderLogs.isEmpty()) {
            boolean isFirst = true;
            for (OrderLog orderLog : orderLogs) {
                String log = concatenateLog(orderLog);
                if (isFirst) {
                    isFirst = false;
                    logs = log;
                } else {
                    logs = logs.concat(" \n ").concat(log);
                }
            }
        }
        return logs;
    }

    private String getLogs(List<OrderLineLog> orderLineLogs) {
        String logs = "";
        if (orderLineLogs != null && !orderLineLogs.isEmpty()) {
            boolean isFirst = true;
            for (OrderLineLog orderLineLog : orderLineLogs) {
                String log = concatenateLog(orderLineLog);
                if (isFirst) {
                    isFirst = false;
                    logs = log;
                } else {
                    logs = logs.concat(" \n ").concat(log);
                }
            }
        }
        return logs;
    }

    private String concatenateLog(EventLog eventLog) {
        StringBuffer logBuffer = new StringBuffer();
        Date eventTime = eventLog.getEventTime();
        if (eventTime != null) {
            logBuffer.append(DateUtil.getDateWithoutTimeAsString(eventTime).concat(" \n "));
        }
        String eventValue = eventLog.getEventValue();
        if (eventValue != null) {
            logBuffer.append(eventValue.concat(" \n "));
        }
        String eventOriginatorId = eventLog.getEventOriginatorId();
        if (eventOriginatorId != null) {
            logBuffer.append(eventOriginatorId.concat(" \n "));
        }
        String eventOriginatorName = eventLog.getEventOriginatorName();
        if (eventOriginatorName != null) {
            logBuffer.append(eventOriginatorName.concat(" \n "));
        }
        return logBuffer.toString();
    }

    private int setDeliverySchedulesDataToExcel(boolean includePlannedDispatchDate, XSSFSheet sheet, OrderLine orderLine, int cellIndexParm, XSSFRow row) {
        int cellIndex = cellIndexParm;
        List<DeliverySchedule> deliverySchedules = orderLine.getDeliverySchedule();
        if (deliverySchedules != null && !deliverySchedules.isEmpty()) {
            int cellIndexForNextRow = cellIndex;
            boolean isFirst = true;
            for (DeliverySchedule deliverySchedule : deliverySchedules) {
                if (isFirst) {
                    isFirst = false;
                    cellIndex = createDeliveryScheduleCells(includePlannedDispatchDate, sheet, row, cellIndex, deliverySchedule);
                } else {
                    cellIndex = cellIndexForNextRow;
                    row = ExcelOperationUtil.createRow(sheet, sheet.getPhysicalNumberOfRows());
                    cellIndex = createDeliveryScheduleCells(includePlannedDispatchDate, sheet, row, cellIndex, deliverySchedule);
                }
            }

        } else {
            if (includePlannedDispatchDate) {
                cellIndex = createCellForUpdateInExcelCells(sheet, "", cellIndex, row, false);
            }
            cellIndex = createCellForUpdateInExcelCells(sheet, "", cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(sheet, "", cellIndex, row, false);
        }
        return cellIndex;
    }

    private int createDeliveryScheduleCells(boolean includePlannedDispatchDate, XSSFSheet sheet, XSSFRow row, int cellIndex, DeliverySchedule deliverySchedule) {
        if (includePlannedDispatchDate) {
            String plannedDispatchDate = "";
            if (deliverySchedule.getPlannedDispatchDate() != null) {
                plannedDispatchDate = DateUtil.getDateWithoutTimeAsString(deliverySchedule.getPlannedDispatchDate());
            }
            cellIndex = createCellForUpdateInExcelCells(sheet, plannedDispatchDate, cellIndex, row, false);
        }

        String expectedQty = "";
        if (deliverySchedule.getExpectedQuantity() != null) {
            expectedQty = String.valueOf(deliverySchedule.getExpectedQuantity());
        }
        cellIndex = createCellForUpdateInExcelCells(sheet, expectedQty, cellIndex, row, false);

        String expectedDate = "";
        if (deliverySchedule.getExpectedDate() != null) {
            expectedDate = DateUtil.getDateWithoutTimeAsString(deliverySchedule.getExpectedDate());
        }
        cellIndex = createCellForUpdateInExcelCells(sheet, expectedDate, cellIndex, row, false);
        DeliveryStatusFlag statusFlag = deliverySchedule.getStatusFlag();
        if (statusFlag != null) {
            cellIndex = createCellForUpdateInExcelCells(sheet, statusFlag.name(), cellIndex, row, false);
        } else {
            cellIndex = createCellForUpdateInExcelCells(sheet, "", cellIndex, row, false);
        }
        return cellIndex;
    }
}
