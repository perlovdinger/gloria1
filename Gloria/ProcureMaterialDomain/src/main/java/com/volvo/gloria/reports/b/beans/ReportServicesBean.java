package com.volvo.gloria.reports.b.beans;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.Tuple;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.reports.ReportFilterMaterialDTO;
import com.volvo.gloria.common.c.dto.reports.ReportFilterOrderDTO;
import com.volvo.gloria.common.c.dto.reports.ReportGeneralDTO;
import com.volvo.gloria.common.c.dto.reports.ReportPartDeliveryPrecisionDTO;
import com.volvo.gloria.common.c.dto.reports.ReportPerformanceDTO;
import com.volvo.gloria.common.c.dto.reports.ReportWarehouseActionDTO;
import com.volvo.gloria.common.c.dto.reports.ReportWarehouseCostDTO;
import com.volvo.gloria.common.c.dto.reports.ReportWarehouseTransactionDTO;
import com.volvo.gloria.common.d.entities.CompanyGroup;
import com.volvo.gloria.common.d.entities.CurrencyRate;
import com.volvo.gloria.common.d.entities.Site;
import com.volvo.gloria.common.d.entities.SupplierCounterPart;
import com.volvo.gloria.common.repositories.b.CompanyCodeRepository;
import com.volvo.gloria.common.repositories.b.CurrencyRateRepository;
import com.volvo.gloria.common.repositories.b.SupplierCounterPartRepository;
import com.volvo.gloria.common.repositories.b.TeamRepository;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialLineStatusCounterRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.PurchaseOrganisationRepository;
import com.volvo.gloria.reports.ReportHelper;
import com.volvo.gloria.reports.b.ReportServices;
import com.volvo.gloria.reports.c.ExportReport;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaFormateUtil;
import com.volvo.gloria.util.c.dto.reports.ReportColumn;
import com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers;
import com.volvo.gloria.util.c.dto.reports.ReportFilterDTO;
import com.volvo.gloria.util.c.dto.reports.ReportRow;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.warehouse.d.entities.StorageRoom;
import com.volvo.gloria.warehouse.repositories.b.WarehouseRepository;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * 
 * services for Report support.
 * 
 */
@ContainerManaged(name = "reportServices")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ReportServicesBean implements ReportServices {
    
    private static final int NUMBER_11 = 11;
    private static final int NUMBER_10 = 10;
    private static final int NUMBER_9 = 9;
    private static final int NUMBER_8 = 8;
    private static final int NUMBER_7 = 7;
    private static final int NUMBER_6 = 6;
    private static final int NUMBER_5 = 5;
    private static final int NUMBER_4 = 4;
    private static final int NUMBER_3 = 3;
    private static final int NUMBER_2 = 2;
    private static final int NUMBER_1 = 1;
    private static final int NUMBER_0 = 0;


    private static final String REPORT_FILTER_OPTION_ALL = "ALL";

    private String[] receivalsHeader = new String[] { "Delivery Note No", "Delivery Note Date", "Order No", "Order Date", "Order Qty", "Received Qty",
            "Cancelled Qty", "Blocked Qty", "GR Status", "Supplier ID", "Supplier Name", "Order STA", "Actual Receival Date", "Part Affiliation", "Part No",
            "Part Version", "Part Name", "Part Modification", "Part Alias", "Project ID", "Contact Person ID", "Delivery Controller ID",
            "Delivery Controller Name", "Price", "Currency" };

    private String[] storesHeader = new String[] { "Order No", "Project Id", "MTR ID", "Received Date", "Stored Date", "Stored qty", "Part Affiliation",
            "Part No", "Part Version", "Part Name", "Part Modification", "Part Alias", "Storage Room", "Bin Location", "Mail form Id" };

    private String[] picksHeader = new String[] { "Order No", "Project Id", "MTR ID", "Request Delivery Date", "Requested by ID", "Request Send Date",
            "Picked Date", "Picked by ID", "Requested Quantity", "Picked Qty", "Shipped Date", "Request List ID", "Pick List ID", "Part Affiliation",
            "Part No", "Part Version", "Part Name", "Part Modification", "Storage Room", "Bin Location", "Part Alias" };

    private String[] shipmentsHeader = new String[] { "Dispatch Note No", "Dispatch Note Date", "Required Delivery Date", "Actual Delivery Date", "Weight",
            "Height", "Carrier", "Requester ID", "Shipment Type", "Delivery Adress ID", "Delivery Adress Name", "Tracking Number" };

    private String[] returnsHeader = new String[] { "Delivery Note No", "Delivery Note Date", "Return Date", "Possible Return Qty", "Actual Return qty",
            "Delivery Adress ID (shipped to)", "Delivery Adress Name (shipped to)", "Part Affiliation", "Part No", "Part Version", "Part Name",
            "Part Modification", "Part Alias", "Project ID", "TestObject ID" };
    
    private String[] performanceReportHeader = new String[]{ "From Date (Actual (real)Delivery date for L & M, PP-req date for K)", 
            "To-Date (Delivery date for L & M, pp-req date for K)",
            "Warehouse",  "Suffix", "Project", "Build series", "Test Object", "Buyer ID", "Part Number", "Order number"};

    @Inject
    private DeliveryNoteRepository deliveryNoteRepository;
    
    @Inject
    private UserServices userServices;

    @Inject
    private CommonServices commonServices;

    @Inject
    private CompanyCodeRepository companyCodeRepository;

    @Inject
    private SupplierCounterPartRepository supplierCounterPartRepository;

    @Inject
    private MaterialHeaderRepository requestHeaderRepository;

    @Inject
    private OrderRepository orderRepository;

    @Inject
    private TeamRepository teamRepository;
    
    @Inject
    private ProcureLineRepository procureLineRepository;
    
    @Inject
    private MaterialLineStatusCounterRepository materialLineStatusCounterRepository;

    @Inject
    private PurchaseOrganisationRepository purchaseOrganisationRepo;

    @Inject
    private WarehouseRepository warehouseRepository;
    
    @Inject
    private CurrencyRateRepository currencyRateRepository;
    
    @Override
    public PageObject getCompanyCodeFilters(PageObject pageObject, String userId) throws GloriaApplicationException {
        List<String> companyCodes = userServices.evaluateCompanyCodesForUser(userId, null, null);
        return companyCodeRepository.getCompanyCodeFilters(pageObject, companyCodes);
    }

    @Override
    public PageObject getSuffixFilters(PageObject pageObject) {
        return supplierCounterPartRepository.getSupplierCounterParts(pageObject);
    }

    @Override
    public PageObject getProjectFilters(PageObject pageObject, String companyCode, String userId, String projectId) throws GloriaApplicationException {
        return requestHeaderRepository.getProjects(pageObject, evaluateCompanyCodes(companyCode, userId), projectId);
    }

    @Override
    public PageObject getBuildSerieFilters(PageObject pageObject, String companyCode, String userId) throws GloriaApplicationException {
        return requestHeaderRepository.getReferenceGroups(pageObject, evaluateCompanyCodes(companyCode, userId));
    }

    @Override
    public PageObject getTestObjectFilters(PageObject pageObject) {
        return requestHeaderRepository.getTestObjects(pageObject);
    }

    @Override
    public PageObject getSupplierParmaIdFilters(PageObject pageObject, String companyCode, String suffix, String userId) throws GloriaApplicationException {
        return orderRepository.getSupplierParmaIds(pageObject, evaluateSuffixes(suffix), evaluateCompanyCodes(companyCode, userId));
    }

    @Override
    public PageObject getSupplierParmaNameFilters(PageObject pageObject, String companyCode, String suffix, String userId) {
        return null;
    }

    @Override
    public PageObject getReferenceFilters(PageObject pageObject) {
        return procureLineRepository.getReferences(pageObject);
    }
    
    @Override
    public PageObject getBuildNameFilters(PageObject pageObject) {
        return procureLineRepository.getBuildNames(pageObject);
    }

    @Override
    public PageObject getMtrIdFilters(PageObject pageObject) {
        return requestHeaderRepository.getChangeIds(pageObject);
    }

    @Override
    public PageObject getDeliveryControllerIdFilters(PageObject pageObject, String companyCode, String suffix, String userId) 
            throws GloriaApplicationException {
        List<CompanyGroup> companyGroups = companyCodeRepository.getCompanyGroupsByCompanyCodes(evaluateCompanyCodes(companyCode, userId));
        List<String> companyGroupCodes = new ArrayList<String>();
        if (companyGroups != null && !companyGroups.isEmpty()) {
            for (CompanyGroup companyGroup : companyGroups) {
                companyGroupCodes.add(companyGroup.getCode());
            }
        }

        List<SupplierCounterPart> supplierCounterParts = supplierCounterPartRepository.getSupplierCounterPartsBySuffix(evaluateSuffixes(suffix));
        List<String> deliveryTeamsForSuffix = new ArrayList<String>();
        if (supplierCounterParts != null && !supplierCounterParts.isEmpty()) {
            for (SupplierCounterPart supplierCounterPart : supplierCounterParts) {
                deliveryTeamsForSuffix.add(supplierCounterPart.getDeliveryFollowUpTeam().getName());
            }
        }
        return teamRepository.getDeliveryControllers(pageObject, deliveryTeamsForSuffix, companyGroupCodes);
    }

    @Override
    public PageObject getDeliveryControllerNameFilters(PageObject pageObject, String companyCode, String suffix, String userId) {
        return null;
    }

    @Override
    public PageObject getOrderStatusFilters(PageObject pageObject) {
        return null;
    }

    @Override
    public PageObject getWbsFilters(PageObject pageObject, String wbsCode) {
        return requestHeaderRepository.getWbsElements(pageObject, wbsCode);
    }

    @Override
    public PageObject getMaterialStatusFilters(PageObject pageObject) {
        return null;
    }

    @Override
    public PageObject getMaterialControllerTeamFilters(PageObject pageObject) {
        return teamRepository.getMaterialControllerTeams(pageObject);
    }

    @Override
    public FileToExportDTO generateOrderReport(ReportFilterOrderDTO reportFilterOrderDTO, Date fromDate, Date toDate, String userId) 
            throws GloriaApplicationException {
        
        // manage 'all' in filters

        if (isSelectedFilterAll(reportFilterOrderDTO.getCompanyCode())) {            
            reportFilterOrderDTO.setCompanyCode(evaluateCompanyCodes(reportFilterOrderDTO.getCompanyCode()[0], userId).toArray(new String[]{}));
        }

        if (isSelectedFilterAll(reportFilterOrderDTO.getSuffix())) {
            reportFilterOrderDTO.setSuffix(null);
        }

        if (isSelectedFilterAll(reportFilterOrderDTO.getProject())) {
            reportFilterOrderDTO.setProject(null);
        }
        
        if (isSelectedFilterAll(reportFilterOrderDTO.getBuildSeries())) {
            reportFilterOrderDTO.setBuildSeries(null);
        }
        

        if (isSelectedFilterAll(reportFilterOrderDTO.getTestObject())) {
            reportFilterOrderDTO.setTestObject(null);
        }
        
        if (isSelectedFilterAll(reportFilterOrderDTO.getSupplierParmaId())) {
            reportFilterOrderDTO.setSupplierParmaId(null);
        }
        
        if (isSelectedFilterAll(reportFilterOrderDTO.getReference())) {
            reportFilterOrderDTO.setReference(null);
        }
        
        if (isSelectedFilterAll(reportFilterOrderDTO.getMtrId())) {
            reportFilterOrderDTO.setMtrId(null);
        }
        
        if (isSelectedFilterAll(reportFilterOrderDTO.getDeliveryControllerId())) {
            reportFilterOrderDTO.setDeliveryControllerId(null);
        }
        
        if (isSelectedFilterAll(reportFilterOrderDTO.getOrderStatus())) {
            reportFilterOrderDTO.setOrderStatus(null);
        }
        Map<String, String> companyCodeToDefaultCurrencyMap = ReportHelper.getCompanyCodeToDefaultCurrencyMap(companyCodeRepository);
        Map<String, CurrencyRate> currencyToCurrencyRateMap = ReportHelper.getCurrencyToCurrencyRateMap(currencyRateRepository);
        return ExportReport.export(orderRepository.fetchOrderLinesForReport(reportFilterOrderDTO, fromDate, toDate, companyCodeToDefaultCurrencyMap,
                                                                            currencyToCurrencyRateMap), "Order_Report_" + DateUtil.getUTCTimeStamp());
    }
 
    @Override
    public FileToExportDTO generateWarehouseActionReport(Date fromDate, Date toDate, ReportWarehouseActionDTO reportWarehouseActionDTOParam) {
        //get data from DeliveryNote
        //get data from New Table  only for picked items
        // select count(*) from material_line_status_counter where projectid in () and whsite in () group by type
        ReportWarehouseActionDTO reportWarehouseActionDTO = reportWarehouseActionDTOParam.clone();
        //if porjectid is "ALL" then projectID is empty in the ReportWarehouseActionDTO that way the query does not have this as filter

        if (isSelectedFilterAll(reportWarehouseActionDTO.getProject())) {
            //always initialise to a empty array instead of null
            reportWarehouseActionDTO.setProject(new String[] {});
        }
        //if whsiteid is "ALL" then projectID is empty in the ReportWarehouseActionDTO that way the query does not have this as filter
        if (isSelectedFilterAll(reportWarehouseActionDTO.getWarehouse())) {
            reportWarehouseActionDTO.setWarehouse(new String[] {});
        }
        //Join Delivvery Note and DeliveryNotelines and count Regular and return
        /*ReceiveType.REGULAR;  ReceiveType.RETURN; DeliveryNote.whsiteID; DeliveryNoteline.projectId*/
        
        ReportWarehouseTransactionDTO reportWarehouseTransactionDTO = new ReportWarehouseTransactionDTO();
        reportWarehouseTransactionDTO.setWarehouse(reportWarehouseActionDTO.getWarehouse());
        List<Tuple> receiveTuples = deliveryNoteRepository.getTransactionReceivalsReportData(reportWarehouseTransactionDTO, fromDate, toDate,
                                                                                             reportWarehouseActionDTO.getProject());
        List<Tuple> returnReceivalTuples = deliveryNoteRepository.getTransactionReturnsReportData(reportWarehouseTransactionDTO, fromDate, toDate,
                                                                                                  reportWarehouseActionDTO.getProject());
        // Shipments :  number of dispatch Notes for a given wharehouse list adn the project id coming form project ID  from dispatchNoteDate 
        
        List<Tuple> disptachNoteTuples = requestHeaderRepository.getTransactionShipmentReportData(reportWarehouseTransactionDTO, fromDate, toDate,
                                                                                                  reportWarehouseActionDTO.getProject());
        
        long noOfPicks = requestHeaderRepository.countPicks(fromDate, toDate, reportWarehouseActionDTO.getProject(), reportWarehouseActionDTO.getWarehouse());
     
        List<ReportRow> reportRows = new ArrayList<ReportRow>();
        ReportRow reportRow = new ReportRow();
        createAndAddReportColumn(reportRow, "From Date", fromDate);
        createAndAddReportColumn(reportRow, "To Date", toDate);
        createAndAddReportColumn(reportRow, "Warehouse", Arrays.toString(reportWarehouseActionDTOParam.getWarehouse()));
        createAndAddReportColumn(reportRow, "Project", Arrays.toString(reportWarehouseActionDTOParam.getProject()));
        createAndAddReportColumn(reportRow, "Number of Receivals", receiveTuples != null ? receiveTuples.size() : 0);
        createAndAddReportColumn(reportRow, "Number of Pulls", noOfPicks);
        createAndAddReportColumn(reportRow, "Number of Returns", returnReceivalTuples != null ? returnReceivalTuples.size() : 0);
        createAndAddReportColumn(reportRow, "Number of Shipments", disptachNoteTuples != null ? disptachNoteTuples.size() : 0);
        reportRows.add(reportRow);
        return ExportReport.export(reportRows, "Warehouse_Ac_Report_" + DateUtil.getUTCTimeStamp());  
    }

    private void createAndAddReportColumn(ReportRow reportRow, String name, Object value) {
        ReportColumn reportColumn = new ReportColumn();
        reportColumn.setName(name);
        if (value != null) {
            reportColumn.setValue(value);
        }
        reportRow.getReportColumns().add(reportColumn);
    }
    
    
    @Override
    public FileToExportDTO generateMaterialReport(ReportFilterMaterialDTO reportFilterMaterialDTO, String userId) 
            throws GloriaApplicationException {
        // manage 'all' in filters

        if (isSelectedFilterAll(reportFilterMaterialDTO.getCompanyCode())) {            
            reportFilterMaterialDTO.setCompanyCode(evaluateCompanyCodes(reportFilterMaterialDTO.getCompanyCode()[0], userId).toArray(new String[]{}));
        }

        if (isSelectedFilterAll(reportFilterMaterialDTO.getSuffix())) {
            reportFilterMaterialDTO.setSuffix(null);
        }

        if (isSelectedFilterAll(reportFilterMaterialDTO.getProject())) {
            reportFilterMaterialDTO.setProject(null);
        }
        
        if (isSelectedFilterAll(reportFilterMaterialDTO.getBuildSeries())) {
            reportFilterMaterialDTO.setBuildSeries(null);
        }
        

        if (isSelectedFilterAll(reportFilterMaterialDTO.getTestObject())) {
            reportFilterMaterialDTO.setTestObject(null);
        }
        
        if (isSelectedFilterAll(reportFilterMaterialDTO.getPhaseName())) {
            reportFilterMaterialDTO.setPhaseName(null);
        }
        
        if (isSelectedFilterAll(reportFilterMaterialDTO.getMtrId())) {
            reportFilterMaterialDTO.setMtrId(null);
        }
        
        if (isSelectedFilterAll(reportFilterMaterialDTO.getWbs())) {
            reportFilterMaterialDTO.setWbs(null);
        }
        
        if (isSelectedFilterAll(reportFilterMaterialDTO.getStatus())) {
            reportFilterMaterialDTO.setStatus(null);
        }
        
        if (isSelectedFilterAll(reportFilterMaterialDTO.getMaterialType())) {
            reportFilterMaterialDTO.setMaterialType(null);
        }
        
        if (isSelectedFilterAll(reportFilterMaterialDTO.getMaterialControllerTeam())) {
            reportFilterMaterialDTO.setMaterialControllerTeam(null);
        }
        Map<String, String> companyCodeToDefaultCurrencyMap = ReportHelper.getCompanyCodeToDefaultCurrencyMap(companyCodeRepository);
        Map<String, CurrencyRate> currencyToCurrencyRateMap = ReportHelper.getCurrencyToCurrencyRateMap(currencyRateRepository);
        return ExportReport.export(requestHeaderRepository.fetchMaterialLinesForReport(reportFilterMaterialDTO, companyCodeToDefaultCurrencyMap,
                                                                                       currencyToCurrencyRateMap), "Material_Report_" + DateUtil.getSqlDate());
    }
    
    private boolean isSelectedFilterAll(String[] selections) {
        if (selections != null && selections.length == 1) {
            if (selections[0].equalsIgnoreCase(REPORT_FILTER_OPTION_ALL)) {
                return true;
            }
        }
        return false;
    }

    private List<String> evaluateCompanyCodes(String companyCode, String userId) throws GloriaApplicationException {
        if (StringUtils.isEmpty(companyCode) || REPORT_FILTER_OPTION_ALL.equalsIgnoreCase(companyCode)) {
            return userServices.evaluateCompanyCodesForUser(userId, null, null);
        }
        return GloriaFormateUtil.getValuesAsString(companyCode);
    }

    private List<String> evaluateSuffixes(String suffix) {
        if (StringUtils.isEmpty(suffix) || REPORT_FILTER_OPTION_ALL.equalsIgnoreCase(suffix)) {
            List<SupplierCounterPart> supplierCounterParts = commonServices.getAllSupplierCounterParts();
            List<String> suffixes = new ArrayList<String>();
            if (supplierCounterParts != null && !supplierCounterParts.isEmpty()) {
                for (SupplierCounterPart supplierCounterPart : supplierCounterParts) {
                    suffixes.add(supplierCounterPart.getPpSuffix());
                }
            }
            return suffixes;
        }
        return GloriaFormateUtil.getValuesAsString(suffix);
    }
    
    public PageObject getBuyerFilters(PageObject pageObject) {
        return purchaseOrganisationRepo.findAllBuyers(pageObject);
    }

    @Override
    public FileToExportDTO generatePartDeliveryPrecisionReport(Date fromDate, Date toDate, String userId,
            ReportPartDeliveryPrecisionDTO reportPartDeliveryPrecisionDTOParam) throws GloriaApplicationException {
        ReportPartDeliveryPrecisionDTO reportPartDeliveryPrecisionDTO = reportPartDeliveryPrecisionDTOParam.clone();
        List<ReportRow> reportRows = new ArrayList<ReportRow>();
        resetArraysToEmptyIfAllFound(reportPartDeliveryPrecisionDTO, userId);
        List<Tuple> summaryTupleValues = orderRepository.fetchDeliveryPrecisionIdentifiersForOrders(reportPartDeliveryPrecisionDTO, fromDate, toDate, true);
        if ((summaryTupleValues != null && summaryTupleValues.size() > 0)
                && (Double.valueOf(summaryTupleValues.get(0)
                                   .get(ReportColumnIdentifiers.REPORT_COLUMN_ID_PRECISION_NO_OF_ORDERLINES).toString()).longValue() > 0)) {
            // summary
            handleTuplesForPreciscionReport(fromDate, toDate, reportRows, summaryTupleValues, reportPartDeliveryPrecisionDTO, true);
            
            // details
            List<Tuple> detailTupleValues = orderRepository.fetchDeliveryPrecisionIdentifiersForOrders(reportPartDeliveryPrecisionDTO, fromDate, toDate, false);
            handleTuplesForPreciscionReport(fromDate, toDate, reportRows, detailTupleValues, reportPartDeliveryPrecisionDTO, false);
        }
        return ExportReport.export(reportRows, "DP_Report_" + DateUtil.getUTCTimeStamp());
    }

    private void handleTuplesForPreciscionReport(Date fromDate, Date toDate, List<ReportRow> reportRows, List<Tuple> tupleValues,
            ReportPartDeliveryPrecisionDTO reportPartDeliveryPrecisionDTOParam, boolean isSummary) {
        DecimalFormat decimalFormat = new DecimalFormat("###.##");
        for (Tuple tuple : tupleValues) {
            ReportRow reportDetailRow = new ReportRow();
            createAndAddReportColumn(reportDetailRow, ReportColumnIdentifiers.REPORT_COLUMN_ID_COMPANY_CODE,
                                     tuple.get(ReportColumnIdentifiers.REPORT_COLUMN_ID_COMPANY_CODE));
            createAndAddReportColumn(reportDetailRow, ReportColumnIdentifiers.REPORT_COLUMN_ID_SUFFIX, REPORT_FILTER_OPTION_ALL);
            createAndAddReportColumn(reportDetailRow, ReportColumnIdentifiers.REPORT_COLUMN_ID_PROJECT, REPORT_FILTER_OPTION_ALL);
            createAndAddReportColumn(reportDetailRow, ReportColumnIdentifiers.REPORT_COLUMN_ID_PRECISION_FROMDATE, fromDate);
            createAndAddReportColumn(reportDetailRow, ReportColumnIdentifiers.REPORT_COLUMN_ID_PRECISION_TODATE, toDate);
            createAndAddReportColumn(reportDetailRow, ReportColumnIdentifiers.REPORT_COLUMN_ID_BUYER_ID, REPORT_FILTER_OPTION_ALL);
            if (isSummary) {
                createAndAddReportColumn(reportDetailRow, ReportColumnIdentifiers.REPORT_COLUMN_ID_SUPPLIER_PARMA_ID, REPORT_FILTER_OPTION_ALL);
                createAndAddReportColumn(reportDetailRow, ReportColumnIdentifiers.REPORT_COLUMN_ID_SUPPLIER_NAME, REPORT_FILTER_OPTION_ALL);
            } else {
                createAndAddReportColumn(reportDetailRow, ReportColumnIdentifiers.REPORT_COLUMN_ID_SUPPLIER_PARMA_ID,
                                         tuple.get(ReportColumnIdentifiers.REPORT_COLUMN_ID_SUPPLIER_PARMA_ID));
                createAndAddReportColumn(reportDetailRow, ReportColumnIdentifiers.REPORT_COLUMN_ID_SUPPLIER_NAME,
                                         tuple.get(ReportColumnIdentifiers.REPORT_COLUMN_ID_SUPPLIER_NAME));
            }
            createAndAddReportColumn(reportDetailRow, ReportColumnIdentifiers.REPORT_COLUMN_ID_DELIVERY_CONTROLLER_ID, REPORT_FILTER_OPTION_ALL);
            createAndAddReportColumn(reportDetailRow, ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDER_TYPE, REPORT_FILTER_OPTION_ALL);
            createAndAddReportColumn(reportDetailRow, ReportColumnIdentifiers.REPORT_COLUMN_ID_PRECISION_NO_OF_ORDERLINES,
                                     Double.valueOf(tuple.get(ReportColumnIdentifiers.REPORT_COLUMN_ID_PRECISION_NO_OF_ORDERLINES).toString()).longValue());
            createAndAddReportColumn(reportDetailRow, ReportColumnIdentifiers.REPORT_COLUMN_ID_PRECISION_TOTAL_DELIVERIES,
                                     Double.valueOf(tuple.get(ReportColumnIdentifiers.REPORT_COLUMN_ID_PRECISION_NO_OF_ORDERLINES).toString()).longValue());
            if (reportPartDeliveryPrecisionDTOParam.isDeliveriesVSOrderSTA()) {
                createAndAddReportColumn(reportDetailRow, ReportColumnIdentifiers.REPORT_COLUMN_ID_PRECISION_ONTIME_ORDERSTA,
                                         decimalFormat.format(tuple.get(ReportColumnIdentifiers.REPORT_COLUMN_ID_PRECISION_ONTIME_ORDERSTA)));
            }

            if (reportPartDeliveryPrecisionDTOParam.isDeliveriesVSAgreedSTA()) {
                createAndAddReportColumn(reportDetailRow, ReportColumnIdentifiers.REPORT_COLUMN_ID_PRECISION_ONTIME_AGREEDSTA,
                                         decimalFormat.format(tuple.get(ReportColumnIdentifiers.REPORT_COLUMN_ID_PRECISION_ONTIME_AGREEDSTA)));
            }
            //createAndAddReportColumn(reportDetailRow, "No xx order STA", decimalFormat.format(tuple.get("orderSTAOnTimexx")));
            //createAndAddReportColumn(reportDetailRow, "No xx agreed STA", decimalFormat.format(tuple.get("agreedSTAOnTimexx")));
            reportRows.add(reportDetailRow);
        }
    }
    
    private void resetArraysToEmptyIfAllFound(ReportPartDeliveryPrecisionDTO reportPartDeliveryPrecisionDTO, String userId) throws GloriaApplicationException {
        if (isSelectedFilterAll(reportPartDeliveryPrecisionDTO.getCompanyCode())) {
            // always initialise to a empty array instead of null
            reportPartDeliveryPrecisionDTO.setCompanyCode(evaluateCompanyCodes(reportPartDeliveryPrecisionDTO.getCompanyCode()[0], userId)
                                                          .toArray(new String[] {}));
        }
        if (isSelectedFilterAll(reportPartDeliveryPrecisionDTO.getSuffix())) {
            // always initialise to a empty array instead of null
            reportPartDeliveryPrecisionDTO.setSuffix(new String[] {});
        }
        if (isSelectedFilterAll(reportPartDeliveryPrecisionDTO.getProject())) {
            // always initialise to a empty array instead of null
            reportPartDeliveryPrecisionDTO.setProject(new String[] {});
        }
        if (isSelectedFilterAll(reportPartDeliveryPrecisionDTO.getBuyerId())) {
            // always initialise to a empty array instead of null
            reportPartDeliveryPrecisionDTO.setBuyerId(new String[] {});
        }
        if (isSelectedFilterAll(reportPartDeliveryPrecisionDTO.getSupplierParmaId())) {
            // always initialise to a empty array instead of null
            reportPartDeliveryPrecisionDTO.setSupplierParmaId(new String[] {});
        }
        if (isSelectedFilterAll(reportPartDeliveryPrecisionDTO.getSupplierParmaName())) {
            // always initialise to a empty array instead of null
            reportPartDeliveryPrecisionDTO.setSupplierParmaName(new String[] {});
        }
        if (isSelectedFilterAll(reportPartDeliveryPrecisionDTO.getDeliveryControllerId())) {
            // always initialise to a empty array instead of null
            reportPartDeliveryPrecisionDTO.setDeliveryControllerId(new String[] {});
        }
        if (isSelectedFilterAll(reportPartDeliveryPrecisionDTO.getSource())) {
            // always initialise to a empty array instead of null
            reportPartDeliveryPrecisionDTO.setSource(new String[] {});
        }
    }

    @Override
    public PageObject getBuyersFromOrderLine(PageObject pageObject) {
        List<Tuple> tuple = orderRepository.getDistinctBuyerPartyIdsFromOrderLine();
        List<ReportFilterDTO> reportBuyerIdDTOs = new ArrayList<ReportFilterDTO>();
        int i = 0;
        for (Tuple warehouse : tuple) {
            i = i + 1;
            ReportFilterDTO reportBuyerDTO = new ReportFilterDTO();
            reportBuyerDTO.setId(warehouse.get(0).toString());
            reportBuyerDTO.setText(warehouse.get(0).toString());
            reportBuyerIdDTOs.add(reportBuyerDTO);
        }
        if (reportBuyerIdDTOs != null && !reportBuyerIdDTOs.isEmpty()) {
            pageObject.setCount(reportBuyerIdDTOs.size());
            pageObject.setGridContents(new ArrayList<PageResults>(reportBuyerIdDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }   
    
    private void resetArraysToEmptyIfAllFound(ReportPerformanceDTO reportPerformanceDTO) {
        if (isSelectedFilterAll(reportPerformanceDTO.getWarehouse())) {
            reportPerformanceDTO.setWarehouse(new String[]{});
        }
        if (isSelectedFilterAll(reportPerformanceDTO.getSuffix())) {
            reportPerformanceDTO.setSuffix(new String[]{});
        }
        if (isSelectedFilterAll(reportPerformanceDTO.getWarehouse())) {
            reportPerformanceDTO.setWarehouse(new String[]{});
        }
        if (isSelectedFilterAll(reportPerformanceDTO.getProject())) {
            reportPerformanceDTO.setProject(new String[]{});
        }
        if (isSelectedFilterAll(reportPerformanceDTO.getBuildSeries())) {
            reportPerformanceDTO.setBuildSeries(new String[]{});
        }
        if (isSelectedFilterAll(reportPerformanceDTO.getBuyerId())) {
            reportPerformanceDTO.setBuyerId(new String[]{});
        }      
        if (isSelectedFilterAll(reportPerformanceDTO.getPartNumber())) {
            reportPerformanceDTO.setPartNumber(new String[]{});
        } 
    }

    @Override
    public FileToExportDTO generatePeformanceReport(Date fromDate, Date toDate, String dateType, ReportPerformanceDTO reportPerformanceDTOParam) {     
        ReportPerformanceDTO reportPerformanceDTO = reportPerformanceDTOParam.clone();
        resetArraysToEmptyIfAllFound(reportPerformanceDTO);
        if (dateType == null) {
            return ExportReport.export(new ArrayList<ReportRow>(), "Perform_DC_" + DateUtil.getUTCTimeStamp());
        }
        if (dateType.contains("PPREQ")) {
            List<ReportRow> reportRows = new ArrayList<ReportRow>();
            List<Tuple> tupleResult = orderRepository.pPReqAndOrderPlacedDateDifference(reportPerformanceDTO, fromDate, toDate);
            for (Tuple tuple : tupleResult) {
                ReportRow reportRow = new ReportRow();
                createAndAddReportColumn(reportRow, "From Date (PP Requisition)", fromDate);
                createAndAddReportColumn(reportRow, "To Date(PP Requisition)", toDate);
                createAndAddReportColumn(reportRow, performanceReportHeader[NUMBER_2], tuple.get(NUMBER_0));
                createAndAddReportColumn(reportRow, performanceReportHeader[NUMBER_3], tuple.get(NUMBER_1));
                createAndAddReportColumn(reportRow, performanceReportHeader[NUMBER_4], tuple.get(NUMBER_2));
                createAndAddReportColumn(reportRow, performanceReportHeader[NUMBER_5], tuple.get(NUMBER_3)); 
                createAndAddReportColumn(reportRow, performanceReportHeader[NUMBER_6], tuple.get(NUMBER_4)); 
                createAndAddReportColumn(reportRow, performanceReportHeader[NUMBER_7], tuple.get(NUMBER_5)); 
                createAndAddReportColumn(reportRow, performanceReportHeader[NUMBER_8], tuple.get(NUMBER_6)); 
                createAndAddReportColumn(reportRow, performanceReportHeader[NUMBER_9], tuple.get(NUMBER_7)); 
                Date orderDate = (Date) tuple.get(NUMBER_8);
                Date procureDate = (Date) tuple.get(NUMBER_9);
                if (orderDate != null && procureDate != null) {
                    int timeBetweenPPDateAndOrderDate = DateUtil.calculateWorkingDays(procureDate, orderDate);
                    createAndAddReportColumn(reportRow, "Requisition date", procureDate);
                    createAndAddReportColumn(reportRow, "Order date", orderDate);
                    createAndAddReportColumn(reportRow, "Time between PP -Req and Order placed", timeBetweenPPDateAndOrderDate + "");
                } else {
                    createAndAddReportColumn(reportRow, "Requisition date", procureDate);
                    createAndAddReportColumn(reportRow, "Order date", orderDate);
                    createAndAddReportColumn(reportRow, "Time between PP -Req and Order placed", "");
                }
                reportRows.add(reportRow);
            }
            return ExportReport.export(reportRows, "Perform_DC_" + DateUtil.getUTCTimeStamp());  
        } else if (dateType.contains("ORDERSTA") || dateType.contains("AGREEDSTA")) {
            List<Tuple> tupleResult = orderRepository.orderSTAAndDeliverySTAReport(reportPerformanceDTO, fromDate, toDate);
            List<ReportRow> reportRows = new ArrayList<ReportRow>();
            String column10 = "Time between order STA and 1st delivery date";
            String column11 = "Time between agreed STA 1st delivery date";
            String column12 = "Order STA";
            String column13 = "First Delivery Note Date";
            String column14 = "Agreed STA";
            for (Tuple tuple : tupleResult) {
                ReportRow reportRow = new ReportRow();
                createAndAddReportColumn(reportRow, "From Date (Actual Delivery)", fromDate);
                createAndAddReportColumn(reportRow, "To Date(Actual Delivery)", toDate);
                createAndAddReportColumn(reportRow, performanceReportHeader[NUMBER_2], tuple.get(NUMBER_0));
                createAndAddReportColumn(reportRow, performanceReportHeader[NUMBER_3], tuple.get(NUMBER_1));
                createAndAddReportColumn(reportRow, performanceReportHeader[NUMBER_4], tuple.get(NUMBER_2));
                createAndAddReportColumn(reportRow, performanceReportHeader[NUMBER_5], tuple.get(NUMBER_3)); 
                createAndAddReportColumn(reportRow, performanceReportHeader[NUMBER_6], tuple.get(NUMBER_4)); 
                createAndAddReportColumn(reportRow, performanceReportHeader[NUMBER_7], tuple.get(NUMBER_5)); 
                createAndAddReportColumn(reportRow, performanceReportHeader[NUMBER_8], tuple.get(NUMBER_6)); 
                createAndAddReportColumn(reportRow, performanceReportHeader[NUMBER_9], tuple.get(NUMBER_7)); 
                Date deliveryNoteDate = (Date) tuple.get(NUMBER_8);
                Date orderStaDate = (Date) tuple.get(NUMBER_9);
                Date staAgreedDate = (Date) tuple.get(NUMBER_10);
                if (deliveryNoteDate != null && orderStaDate != null) {
                    int timeBetweenOrderStaAndFirstReceived = DateUtil.calculateWorkingDays(orderStaDate, deliveryNoteDate);
                    createAndAddReportColumn(reportRow, column12, orderStaDate);
                    createAndAddReportColumn(reportRow, column13, deliveryNoteDate);
                    createAndAddReportColumn(reportRow, column10, timeBetweenOrderStaAndFirstReceived + "");
                } else {
                    createAndAddReportColumn(reportRow, column12, orderStaDate);
                    createAndAddReportColumn(reportRow, column13, deliveryNoteDate);
                    createAndAddReportColumn(reportRow, column10, "");
                }
                if (deliveryNoteDate != null && staAgreedDate != null) {
                    int timeBetweenAgreedStaAndFirstReceived = DateUtil.calculateWorkingDays(staAgreedDate, deliveryNoteDate);
                    createAndAddReportColumn(reportRow, column14, staAgreedDate);
                    createAndAddReportColumn(reportRow, column13, deliveryNoteDate);
                    createAndAddReportColumn(reportRow, column11, timeBetweenAgreedStaAndFirstReceived + "");
                } else {
                    createAndAddReportColumn(reportRow, column14, staAgreedDate);
                    createAndAddReportColumn(reportRow, column13, deliveryNoteDate);
                    createAndAddReportColumn(reportRow, column11, "");
                }
                reportRows.add(reportRow);
            }
            return ExportReport.export(reportRows, "Perform_WH_" + DateUtil.getUTCTimeStamp());  
            
        } else {
            List<Tuple> tupleResult = orderRepository.materialPerformanceReportData(reportPerformanceDTO, fromDate, toDate);
            List<ReportRow> reportRows = new ArrayList<ReportRow>();
            String column10 =  "Time between Received status and part storage";
            String column11 = "Time between request for pull and actual pull (ship)";
            String column12 = "Received Date";
            String column13 = "Stored Date";
            String column14 = "Requested Date";
            String column15 = "Picked Date";
            for (Tuple tuple : tupleResult) {
                ReportRow reportRow1 = new ReportRow();
                if (dateType.contains("RECEIVED")) {
                    createAndAddReportColumn(reportRow1, "From Date (Receival)", fromDate);
                    createAndAddReportColumn(reportRow1, "To Date(Receival)", toDate);
                }
                if (dateType.contains("REQUEST")) {
                    createAndAddReportColumn(reportRow1, "From Date (Requested)", fromDate);
                    createAndAddReportColumn(reportRow1, "To Date(Requested)", toDate);
                }
                createAndAddReportColumn(reportRow1, performanceReportHeader[NUMBER_2], tuple.get(NUMBER_0));
                createAndAddReportColumn(reportRow1, performanceReportHeader[NUMBER_3], tuple.get(NUMBER_1));
                createAndAddReportColumn(reportRow1, performanceReportHeader[NUMBER_4], tuple.get(NUMBER_2));
                createAndAddReportColumn(reportRow1, performanceReportHeader[NUMBER_5], tuple.get(NUMBER_3)); 
                createAndAddReportColumn(reportRow1, performanceReportHeader[NUMBER_6], tuple.get(NUMBER_4)); 
                createAndAddReportColumn(reportRow1, performanceReportHeader[NUMBER_7], tuple.get(NUMBER_5)); 
                createAndAddReportColumn(reportRow1, performanceReportHeader[NUMBER_8], tuple.get(NUMBER_6)); 
                createAndAddReportColumn(reportRow1, performanceReportHeader[NUMBER_9], tuple.get(NUMBER_7)); 
                Date receivedTime = (Date) tuple.get(NUMBER_8);
                Date storedTime = (Date) tuple.get(NUMBER_9);
                Date requestTime = (Date) tuple.get(NUMBER_10);
                Date shippedTime = (Date) tuple.get(NUMBER_11);
                if (receivedTime != null && storedTime != null) {
                    int timeBetweenReceivedAndStored = DateUtil.calculateWorkingDays(receivedTime, storedTime);
                    createAndAddReportColumn(reportRow1, column12, receivedTime);
                    createAndAddReportColumn(reportRow1, column13, storedTime);
                    createAndAddReportColumn(reportRow1, column10, timeBetweenReceivedAndStored + "");
                } else {
                    createAndAddReportColumn(reportRow1, column12, receivedTime);
                    createAndAddReportColumn(reportRow1, column13, storedTime);
                    createAndAddReportColumn(reportRow1, column10, "");
                }
                if (requestTime != null && shippedTime != null) {
                    int timeBetweenRequestAndShipped = DateUtil.calculateWorkingDays(requestTime, shippedTime);
                    createAndAddReportColumn(reportRow1, column14, requestTime);
                    createAndAddReportColumn(reportRow1, column15, shippedTime);
                    createAndAddReportColumn(reportRow1, column11, timeBetweenRequestAndShipped + "");
                } else {
                    createAndAddReportColumn(reportRow1, column14, requestTime);
                    createAndAddReportColumn(reportRow1, column15, shippedTime);
                    createAndAddReportColumn(reportRow1, column11, "");
                }
                reportRows.add(reportRow1);
            }
            return ExportReport.export(reportRows, "Perform_WH_" + DateUtil.getUTCTimeStamp());  
        }  
    }
    
    @Override
    public PageObject getPartsFromOrderLine(PageObject pageObject) {
        List<Tuple> tuple = orderRepository.getDistinctPartsFromOrderLine();
        List<ReportFilterDTO> reportBuyerIdDTOs = new ArrayList<ReportFilterDTO>();

        for (Tuple warehouse : tuple) {
            ReportFilterDTO reportBuyerDTO = new ReportFilterDTO();
            reportBuyerDTO.setId(warehouse.get(0).toString());
            reportBuyerDTO.setText(warehouse.get(0).toString());
            reportBuyerIdDTOs.add(reportBuyerDTO);
        }
        if (reportBuyerIdDTOs != null && !reportBuyerIdDTOs.isEmpty()) {
            pageObject.setCount(reportBuyerIdDTOs.size());
            pageObject.setGridContents(new ArrayList<PageResults>(reportBuyerIdDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }

    @Override
    public FileToExportDTO generateGeneralWarehouseReport(ReportGeneralDTO reportGeneralDTOParam) {
        ReportGeneralDTO reportGeneralDTO = reportGeneralDTOParam.clone();
        resetGeneralWareHouseReportToEmpty(reportGeneralDTO);
        List<Tuple> tuples = orderRepository.getGeneralReportData(reportGeneralDTO);
        List<Tuple> tuplesWithNullOrderLines = orderRepository.getGeneralReportDataWithNullOrderLines(reportGeneralDTO);
        double totalStockValue = 0;
        double stockValue = 0.0;
        Set<String> setDistinctPartsWithCostZero = new HashSet<String>();
        Set<String> setDistinctPartsWithCostNonZero = new HashSet<String>();
        for (Tuple tuple : tuples) {
            String key = "";
            if (tuple.get(NUMBER_0) != null) {
                key = key + tuple.get(NUMBER_0).toString();
            }
            if (tuple.get(NUMBER_1) != null) {
                key = key + tuple.get(NUMBER_1).toString();
            }
            if (tuple.get(NUMBER_2) != null) {
                key = key + tuple.get(NUMBER_2).toString();
            }
            long quantity = tuple.get(NUMBER_3) != null ? ((Long) tuple.get(NUMBER_3)) : 0;
            double costOfPart = tuple.get(NUMBER_4) != null ? ((Double) tuple.get(NUMBER_4)) : 0;
            long perQuantity = tuple.get(NUMBER_5) != null ? ((Long) tuple.get(NUMBER_5)) : 0;
            if (0 == costOfPart) {
                setDistinctPartsWithCostZero.add(key);
            }  else {
                setDistinctPartsWithCostNonZero.add(key);
                if (perQuantity > 0) {
                    stockValue = ((costOfPart / perQuantity) * (quantity));
                }
                totalStockValue = totalStockValue + stockValue;
            }
        }   
        for (Tuple tuple : tuplesWithNullOrderLines) {
            String key = "";
            if (tuple.get(NUMBER_0) != null) {
                key = key + tuple.get(NUMBER_0).toString();
            }
            if (tuple.get(NUMBER_1) != null) {
                key = key + tuple.get(NUMBER_1).toString();
            }
            if (tuple.get(NUMBER_2) != null) {
                key = key + tuple.get(NUMBER_2).toString();
            }
            setDistinctPartsWithCostZero.add(key);
        }
        List<String> common = new ArrayList<String>(setDistinctPartsWithCostZero);
        common.retainAll(setDistinctPartsWithCostNonZero);        
        int numberOfDistinctParts = setDistinctPartsWithCostZero.size() + setDistinctPartsWithCostNonZero.size() - common.size(); 
        List<ReportRow> reportRows = new ArrayList<ReportRow>();
        ReportRow reportRow = new ReportRow();
        createAndAddReportColumn(reportRow, "Reporting Date", DateUtil.getCurrentUTCDateTime());
        createAndAddReportColumn(reportRow, "Warehouse", Arrays.toString(reportGeneralDTOParam.getWarehouse()));
        createAndAddReportColumn(reportRow, "Project", Arrays.toString(reportGeneralDTOParam.getProject()));
        createAndAddReportColumn(reportRow, "Number of unique stored Parts ",  numberOfDistinctParts + "");
        createAndAddReportColumn(reportRow, "Total Stock  Value", new BigDecimal(totalStockValue).setScale(2, RoundingMode.CEILING).toString());
        createAndAddReportColumn(reportRow, "Number of unique stored Parts with a value = 0", 
                                 setDistinctPartsWithCostZero.size() - common.size()+ "");
        reportRows.add(reportRow);
        return ExportReport.export(reportRows, "Report_" + DateUtil.getUTCTimeStamp());   
    }
    
    @Override
    public FileToExportDTO generateWarehouseCostReport(ReportWarehouseCostDTO reportWarehouseCostDTO, Date fromDate, Date toDate, String userId)
            throws GloriaApplicationException {
        // manage 'all' in filters

        if (isSelectedFilterAll(reportWarehouseCostDTO.getWarehouse())) {
            reportWarehouseCostDTO.setWarehouse(null);
        }

        if (isSelectedFilterAll(reportWarehouseCostDTO.getProject())) {
            reportWarehouseCostDTO.setProject(null);
        }
        Map<String, String> companyCodeToDefaultCurrencyMap = ReportHelper.getCompanyCodeToDefaultCurrencyMap(companyCodeRepository);
        Map<String, CurrencyRate> currencyToCurrencyRateMap = ReportHelper.getCurrencyToCurrencyRateMap(currencyRateRepository);
        return ExportReport.export(requestHeaderRepository.fetchWarehouseCostReport(reportWarehouseCostDTO, fromDate, toDate, companyCodeToDefaultCurrencyMap,
                                                                                    currencyToCurrencyRateMap),
                                   "Warehouse_Cost_Report_" + DateUtil.getSqlDate());
    }
    
    /*
     * WIPS
     */
    public FileToExportDTO generateWarehouseTransactionReportShipments(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO, 
            Date fromDate, Date toDate) {
        //dont send  the received original param to calculate
        List<Tuple> tuples = requestHeaderRepository.getTransactionShipmentReportData(reportWarehouseTransactionDTO, fromDate, toDate, null);
        List<ReportRow> reportRows = createReportRows(tuples, shipmentsHeader);   
        return ExportReport.export(reportRows, "SHIP_WAREHOUSE_TR_" + DateUtil.getUTCTimeStamp());   
    }


    @Override
    public PageObject getWarehouseFilters(PageObject pageObject) {
        pageObject = warehouseRepository.getWarehouses(pageObject);
        List<PageResults> pageResults = pageObject.getGridContents();
        if (pageResults != null && !pageResults.isEmpty()) {
            // update ReportFilterDTO with siteName(text attribute), based on siteId(id attribute)
            for (PageResults pageResult : pageResults) {
                ReportFilterDTO warehouseFilterDTO = (ReportFilterDTO) pageResult;
                Site warehouseSite = commonServices.getSiteBySiteId(warehouseFilterDTO.getId());
                warehouseFilterDTO.setText(warehouseSite.getSiteName());
            }
        }
        return pageObject;
    }

    @Override
    public FileToExportDTO generateWarehouseTransactionReport(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO, 
            Date fromDate, Date toDate) throws GloriaApplicationException {
        resetWarehouseTransactionReportToEmpty(reportWarehouseTransactionDTO);
        String action = reportWarehouseTransactionDTO.getAction();
        if ("ALL_RECEIVALS".equals(action)) {
            return this.generateWarehouseTransactionReportReceivals(reportWarehouseTransactionDTO, fromDate, toDate);
        } else if ("ALL_STORES".equals(action)) {
            return this.generateWarehouseTransactionReportStores(reportWarehouseTransactionDTO, fromDate, toDate);
        } else if ("ALL_PICKS".equals(action)) {
            return this.generateWarehouseTransactionReportPicks(reportWarehouseTransactionDTO, fromDate, toDate);
        } else if ("ALL_SHIPMENTS".equals(action)) {
            return this.generateWarehouseTransactionReportShipments(reportWarehouseTransactionDTO, fromDate, toDate);
        } else if ("ALL_RETURNS".equals(action)) {
            return this.generateWarehouseTransactionReportReturns(reportWarehouseTransactionDTO, fromDate, toDate);
        } else {
            throw new GloriaApplicationException("", "Received invalid action parameter");
        }
    }



    @Override
    public PageObject getStorageRooms(PageObject pageObject) {
        List<StorageRoom> storageRooms = warehouseRepository.getStorageRooms();
        List<ReportFilterDTO> reportFilterDTOs = new ArrayList<ReportFilterDTO>();
        for (StorageRoom storageRoom : storageRooms) {
            ReportFilterDTO reportBuyerDTO = new ReportFilterDTO();
            reportBuyerDTO.setId(storageRoom.getId().toString());
            reportBuyerDTO.setText(storageRoom.getName());
            reportFilterDTOs.add(reportBuyerDTO);
        }
        if (reportFilterDTOs != null && !reportFilterDTOs.isEmpty()) {
            pageObject.setCount(reportFilterDTOs.size());
            pageObject.setGridContents(new ArrayList<PageResults>(reportFilterDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }
    
    private void resetGeneralWareHouseReportToEmpty(ReportGeneralDTO reportGeneralDTOParam) {
        if (isSelectedFilterAll(reportGeneralDTOParam.getWarehouse())) {
            reportGeneralDTOParam.setWarehouse(new String[]{});
        }
        if (isSelectedFilterAll(reportGeneralDTOParam.getProject())) {
            reportGeneralDTOParam.setProject(new String[]{});
        }        
    }
    
    private FileToExportDTO generateWarehouseTransactionReportReturns(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO, Date fromDate, Date toDate) {
        List<Tuple> tuples = deliveryNoteRepository.getTransactionReturnsReportData(reportWarehouseTransactionDTO, fromDate, toDate, null);
        List<ReportRow> reportRows = createReportRows(tuples, returnsHeader);        
        return ExportReport.export(reportRows, "RET_WAREHOUSE_TR_" + DateUtil.getUTCTimeStamp());   
    }

    private List<ReportRow> createReportRows(List<Tuple> tuples, String[] headers) {
        List<ReportRow> reportRows = new ArrayList<ReportRow>();
        for (Tuple tuple : tuples) {
            ReportRow reportRow = new ReportRow();
            int noOfElements = tuple.getElements().size();
            for (int i = 0; i < noOfElements; i++) {
                if (tuple.get(i) != null) {
                    createAndAddReportColumn(reportRow, headers[i], tuple.get(i));
                } else {
                    createAndAddReportColumn(reportRow, headers[i], "");
                }
            }
            reportRows.add(reportRow);
        }
        return reportRows;
    }

 
    private FileToExportDTO generateWarehouseTransactionReportPicks(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO, Date fromDate, Date toDate) {
        List<Tuple> tuples = deliveryNoteRepository.getTransactionPicksReportData(reportWarehouseTransactionDTO, fromDate, toDate);
        List<Tuple> result = new ArrayList<Tuple>();
        result.addAll(tuples);
        List<ReportRow> reportRows = createReportRows(result, picksHeader);       
        return ExportReport.export(reportRows, "PICKS_WAREHOUSE_TR_" + DateUtil.getUTCTimeStamp());   
    }    

    private FileToExportDTO generateWarehouseTransactionReportStores(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO, Date fromDate, Date toDate) {
        List<Tuple> tuples = requestHeaderRepository.getTransactionStoresReportData(reportWarehouseTransactionDTO, fromDate, toDate);
        List<ReportRow> reportRows = createReportRows(tuples, storesHeader);  
        return ExportReport.export(reportRows, "STORES_WAREHOUSE_TR_" + DateUtil.getUTCTimeStamp());   
    }
    
    private FileToExportDTO generateWarehouseTransactionReportReceivals(ReportWarehouseTransactionDTO 
            reportWarehouseTransactionDTO, Date fromDate, Date toDate) {
        List<Tuple> tuples = deliveryNoteRepository.getTransactionReceivalsReportData(reportWarehouseTransactionDTO, fromDate, toDate, null);
        List<ReportRow> reportRows = createReportRows(tuples, receivalsHeader);  
        return ExportReport.export(reportRows, "RECEIVALS_WAREHOUSE_TR_" + DateUtil.getUTCTimeStamp());   
    }

    private void resetWarehouseTransactionReportToEmpty(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO) {
        if (isSelectedFilterAll(reportWarehouseTransactionDTO.getWarehouse())) {
            reportWarehouseTransactionDTO.setWarehouse(new String[]{});
        }
        if (isSelectedFilterAll(reportWarehouseTransactionDTO.getStorageRoom())) {
            reportWarehouseTransactionDTO.setStorageRoom(new String[]{});
        }       
    }

}
