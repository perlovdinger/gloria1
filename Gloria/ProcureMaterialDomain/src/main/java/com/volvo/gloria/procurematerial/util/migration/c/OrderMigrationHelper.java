package com.volvo.gloria.procurematerial.util.migration.c;

import static com.volvo.gloria.util.c.SAPParam.ACCOUNT_ASSIGNMENT_CATEGORY;
import static com.volvo.gloria.util.c.SAPParam.CATEGORY_OF_DELIVERY_DATE;
import static com.volvo.gloria.util.c.SAPParam.GOODS_RECEIPT_ASSIGN_CODE_GM;
import static com.volvo.gloria.util.c.SAPParam.GOODS_RECEIPT_HEADER_TEXT;
import static com.volvo.gloria.util.c.SAPParam.GOODS_RECEIPT_MOVEMENT_TYPE;
import static com.volvo.gloria.util.c.SAPParam.GR_INDICATOR;
import static com.volvo.gloria.util.c.SAPParam.IR_INDICATOR;
import static com.volvo.gloria.util.c.SAPParam.MATERIAL_GROUP;
import static com.volvo.gloria.util.c.SAPParam.MOVEMENT_INDICATOR;
import static com.volvo.gloria.util.c.SAPParam.NON_VALUED_GR_INDICATOR;
import static com.volvo.gloria.util.c.SAPParam.ORDER_TYPE;
import static com.volvo.gloria.util.c.SAPParam.PURCHASE_GROUP;
import static com.volvo.gloria.util.c.SAPParam.PURCHASE_TYPE;
import static com.volvo.gloria.util.c.SAPParam.TAX_CODE;
import static com.volvo.gloria.util.c.SAPParam.UNLIMITED_DELIVERY_INDICATOR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.authorization.d.entities.Team;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.BuildSiteType;
import com.volvo.gloria.common.c.CurrencyUtil;
import com.volvo.gloria.common.c.PaginatedArrayList;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.CompanyCode;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam;
import com.volvo.gloria.common.d.entities.Site;
import com.volvo.gloria.common.d.entities.SupplierCounterPart;
import com.volvo.gloria.procurematerial.c.CompleteType;
import com.volvo.gloria.procurematerial.c.DeliveryNoteLineStatus;
import com.volvo.gloria.procurematerial.c.EventType;
import com.volvo.gloria.procurematerial.c.QiMarking;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNote;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteSubLine;
import com.volvo.gloria.procurematerial.d.entities.DeliverySchedule;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptHeader;
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptLine;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.MessageStatus;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLastModified;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLog;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.OrderLog;
import com.volvo.gloria.procurematerial.d.entities.OrderSap;
import com.volvo.gloria.procurematerial.d.entities.OrderSapAccounts;
import com.volvo.gloria.procurematerial.d.entities.OrderSapLine;
import com.volvo.gloria.procurematerial.d.entities.OrderSapSchedule;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.PurchaseOrganisation;
import com.volvo.gloria.procurematerial.d.entities.Requisition;
import com.volvo.gloria.procurematerial.d.entities.status.goodsReceiptLine.GoodsReceiptLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusHelper;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatus;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.d.type.internalexternal.InternalExternal;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.procurematerial.d.type.receive.ReceiveType;
import com.volvo.gloria.procurematerial.d.type.request.RequestType;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;
import com.volvo.gloria.procurematerial.util.migration.c.type.order.MigrationOrderType;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.Utils;
import com.volvo.gloria.util.c.SAPParam;
import com.volvo.gloria.util.c.UniqueItems;
import com.volvo.gloria.warehouse.d.entities.BinLocation;

/**
 * Common utility for Order Migration.
 */
public final class OrderMigrationHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderMigrationHelper.class);

    private static final long MILLI_SEC = 1000;
    private static final int UNITPRICE_SCALE_3 = 3;
    private static final int EVENT_VALUE_LENGTH = 2048;
    public static final int MAX_QUANTITY = 99999;
    private static final long RANDOM_999L = 999;
    public static final String PAID = "PAID";
    public static final String NOT_PAID = "NOT_PAID";
    private static final boolean MIGRATED = true;
    private static final boolean REQUESTED_EXCLUDED = false;
    private static final String ORDER_MODE = "Prototype";
    private static final long UOP_PCE = 1;
    private static final long UOP_PCE2 = 100;
    private static final long UOP_PCE3 = 1000;
    private static final String MISSING_PART_NAME = "undefined";
    private static long randomNumber = RANDOM_999L;
    private static final int LEFT_PAD_VENDOR = 10;
    private static final int LEFT_PAD_CURRENTBUYER = 4;
    private static final int LEFT_PAD_GENERALLEDGER = 10;
    private static final int LEFT_PAD_COSTCENTER = 10;

    private static final long RANDOM_L = 999;
    private static long randomNumberForDeliveryNote = RANDOM_L;
    private static final String QUO = "QUO";
    private static final int DEPARTMENT_NAME_LENGTH = 6;

    private static final int MAX_2048 = 2048;

    private static final String NOT_PAID_ORDER = "NO";

    private static Map<String, Boolean> isSendGRtoSAPCompanyCodeMap = new HashMap<String, Boolean>();
    private static Map<String, DeliveryFollowUpTeam> suffixDeliveryControllerTeamMap = new HashMap<String, DeliveryFollowUpTeam>();
    private static Map<String, String> ppSuffixShipToId = new HashMap<String, String>();
    private static Map<String, Boolean> isSendPOtoSAPCompanyCodeMap = new HashMap<String, Boolean>();
    private static Map<String, String> sapPurchaseOrgCompanyCodeMap = new HashMap<String, String>();
    private static Map<String, String> sapQuantityBlockReceiverIdCompanyCodeMap = new HashMap<String, String>();
    private static List<String> siteWithQIMandatoryList = new ArrayList<String>();
    private static Map<String, String> companyCodeToMaterialControllerTeamMap = new HashMap<String, String>();
    private static Map<String, String> companyGroupToMaterialControllerTeamMap = new HashMap<String, String>();
    private static Map<String, String> userIdToUserNameMap = new HashMap<String, String>();
    private static List<String> buildSites = new ArrayList<String>();
    private static Map<String, Site> sitesMap = new HashMap<String, Site>();
    private static Map<String, String> purchaseOrgMap = new HashMap<String, String>();

    static {
        siteWithQIMandatoryList.add("1722");
        siteWithQIMandatoryList.add("34331");
    }

    private OrderMigrationHelper() {
    }

    public static Order transformToOrderEntity(List<OrderMigrationDTO> sameOrderNoDtos, CommonServices commonServices) throws GloriaApplicationException {
        OrderMigrationDTO orderMigrationDTO = sameOrderNoDtos.iterator().next();
        Order order = new Order();
        order.setInternalExternal(orderMigrationDTO.isExternal() ? InternalExternal.EXTERNAL : InternalExternal.INTERNAL);
        order.setOrderNo(orderMigrationDTO.getOrderNumber());
        order.setOrderDateTime(orderMigrationDTO.getOrderDate());
        order.setOrderMode(ORDER_MODE);
        order.setMaterialUserId(orderMigrationDTO.getMaterialUserid());
        order.setSupplierId(orderMigrationDTO.getSupplierCode());
        order.setSupplierName(orderMigrationDTO.getSupplierName());
        order.setDeliveryControllerTeam(orderMigrationDTO.getDeliveryControllerTeam());
        order.setSuffix(orderMigrationDTO.getSuffix());
        order.setShipToId(orderMigrationDTO.getShipToId());
        order.setCompanyCode(orderMigrationDTO.getCompanyCode());
        order.setOrderIdGps(orderMigrationDTO.getOrderIdGps());

        List<OrderLine> orderLines = transformToOrderLineEntity(sameOrderNoDtos, commonServices);
        for (OrderLine orderLine : orderLines) {
            orderLine.setOrder(order);
        }
        order.setOrderLines(orderLines);

        return order;
    }

    public static OrderLog transformToOrderLogs(OrderMigrationDTO dto, Order order) {
        String logText = dto.getOrderInformation();
        OrderLog orderLog = new OrderLog();
        orderLog.setEventType(EventType.MIGRATION_ORDER);
        orderLog.setEventTime(DateUtil.getCurrentUTCDateTime());
        if (logText != null && logText.length() > EVENT_VALUE_LENGTH) {
            logText = logText.substring(0, EVENT_VALUE_LENGTH);
        }
        orderLog.setEventValue(logText);
        orderLog.setOrders(order);
        return orderLog;
    }

    @SuppressWarnings("unused")
    public static List<OrderLine> transformToOrderLineEntity(List<OrderMigrationDTO> sameOrderNoDtos, CommonServices commonServices)
            throws GloriaApplicationException {
        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        // Run only not migrated ones!
        for (OrderMigrationDTO dto : sameOrderNoDtos) {
            if (!dto.isMigrated()) {
                OrderLine orderLine = new OrderLine();
                orderLine.setPartAffiliation(dto.getPartQualifier());
                orderLine.setPartNumber(dto.getPartNumber());
                orderLine.setPartName(StringUtils.isEmpty(dto.getDescription()) ? MISSING_PART_NAME : dto.getDescription());
                orderLine.setCurrency(dto.getCurrency());
                orderLine.setUnitOfMeasure(dto.getUnitOfMeasure());
                orderLine.setShipToPartyId(dto.getShipToId());
                orderLine.setBuyerPartyId(dto.getPurchasingOrganization());
                Long requisitionId = getrequisitionNumber(dto.getRequisitionNumber());
                if (requisitionId != null) {
                    orderLine.setRequisitionId(requisitionId.toString());
                }
                orderLine.setProjectId(dto.getProject());
                orderLine.setPossibleToReceiveQuantity(dto.getPossibleToReceiveQuantity());
                orderLine.setReceivedQuantity(dto.getReceivedQuantity());
                orderLine.setStatus(dto.getOrderLineStatus());

                if (orderLine.getStatus().equals(OrderLineStatus.COMPLETED)) {
                    if (dto.getOrderedQuantity() > dto.getReceivedQuantity()) {
                        orderLine.setCompleteType(CompleteType.COMPLETE);
                    } else {
                        orderLine.setCompleteType(CompleteType.RECEIVED);
                    }
                    orderLine.setPossibleToReceiveQuantity(dto.getReceivedQuantity());
                }

                orderLine.setQiMarking(findQiMarkingForSite(dto.getInspection(), dto.getShipToId()));

                OrderLineLastModified orderLineLastModified = new OrderLineLastModified();
                orderLine.setOrderLineLastModified(orderLineLastModified);

                List<OrderLineVersion> orderLineVersions = transformToOrderLineVersionEntity(dto, orderLine, commonServices);

                List<DeliverySchedule> deliverySchedules = transformToDeliveryScheduleEntity(dto);
                for (DeliverySchedule deliverySchedule : deliverySchedules) {
                    deliverySchedule.setOrderLine(orderLine);
                }

                orderLine.setPaidQuantity(dto.getPaidQuantity());
                orderLine.setDeliverySchedule(deliverySchedules);
                orderLine.setDeliveryControllerUserId(dto.getDeliveryControllerUserId());
                orderLine.setDeliveryControllerUserName(dto.getDeliveryControllerUserName());
                orderLine.setSupplierPartNo(dto.getSupplierPartNo());
                orderLines.add(orderLine);
            }
        }

        return orderLines;
    }

    public static OrderLineLog transformToOrderLineLog(OrderMigrationDTO dto, OrderLine orderLine) {
        String logText = dto.getObjectInformation();
        OrderLineLog orderLineLog = new OrderLineLog();
        orderLineLog.setEventType(EventType.MIGRATION_ORDER);
        orderLineLog.setEventTime(DateUtil.getCurrentUTCDateTime());
        if (logText != null && logText.length() > EVENT_VALUE_LENGTH) {
            logText = logText.substring(0, EVENT_VALUE_LENGTH);
        }
        orderLineLog.setEventValue(logText);
        orderLineLog.setOrderLine(orderLine);
        return orderLineLog;
    }

    private static QiMarking findQiMarkingForSite(String inspection, String site) {
        if (!StringUtils.isEmpty(inspection) && "Y".equalsIgnoreCase(inspection)) {
            return siteWithQIMandatoryList.contains(site) ? QiMarking.MANDATORY : QiMarking.VISUAL;
        }

        return QiMarking.VISUAL;
    }

    public static List<DeliverySchedule> transformToDeliveryScheduleEntity(OrderMigrationDTO dto) {
        List<DeliverySchedule> deliverySchedules = new ArrayList<DeliverySchedule>();
        DeliverySchedule deliverySchedule = new DeliverySchedule();
        deliverySchedule.setExpectedQuantity(dto.getOrderedQuantity());
        if (dto.getExpectedStaDate() != null) {
            deliverySchedule.setExpectedDate(dto.getExpectedStaDate());
        } else {
            deliverySchedule.setExpectedDate(dto.getShipArriveDate());
        }

        deliverySchedules.add(deliverySchedule);
        return deliverySchedules;
    }

    public static List<OrderLineVersion> transformToOrderLineVersionEntity(OrderMigrationDTO dto, OrderLine orderLine, CommonServices commonServices) {
        List<OrderLineVersion> orderLineVersions = new ArrayList<OrderLineVersion>();
        OrderLineVersion orderLineVersion = new OrderLineVersion();
        orderLineVersion.setVersionDate(DateUtil.getCurrentUTCDateTime());
        orderLineVersion.setVersionNo(1);
        orderLineVersion.setPartVersion(dto.getPartVersion());
        orderLineVersion.setOrderStaDate(dto.getShipArriveDate());
        orderLineVersion.setQuantity(dto.getOrderedQuantity());
        orderLineVersion.setUnitPrice(CurrencyUtil.roundOff(dto.getPrice(), UNITPRICE_SCALE_3));
        orderLineVersion.setUnitPriceInEuro(CurrencyUtil.convertUnitPriceToEUR(CurrencyUtil.roundOff(dto.getPrice(), UNITPRICE_SCALE_3), dto.getCurrency(),
                                                                               commonServices));
        orderLineVersion.setStaAcceptedDate(dto.getAcceptedStaDate());
        if (dto.getAgreedStaDate() != null) {
            orderLineVersion.setStaAgreedDate(dto.getAgreedStaDate());
        }

        if (dto.getAgreedSTALastUpdated() != null) {
            orderLineVersion.setStaAgreedLastUpdated(dto.getAgreedSTALastUpdated());
        }

        long uop = 1;
        if ("PCE".equalsIgnoreCase(dto.getUnitOfPrice())) {
            uop = UOP_PCE;
        } else if ("PCE2".equalsIgnoreCase(dto.getUnitOfPrice())) {
            uop = UOP_PCE2;
        } else if ("PCE3".equalsIgnoreCase(dto.getUnitOfPrice())) {
            uop = UOP_PCE3;
        }
        orderLineVersion.setPerQuantity(uop);
        orderLineVersion.setBuyerId(dto.getBuyerCode());
        orderLineVersion.setBuyerName(dto.getBuyerName());
        orderLineVersion.setCurrency(dto.getCurrency());

        orderLineVersion.setOrderLine(orderLine);
        orderLine.setOrderLineVersions(orderLineVersions);
        orderLine.setCurrent(orderLineVersion);

        // add on additional attributes for delivery precision
        if (orderLineVersion.getOrderLine().getStatus() != OrderLineStatus.PLACED) {
            Date firstReceivalDateAsDeliveryNoteDate = dto.getMinReceivedDate();
            orderLineVersion.setOrderStaDateOnTime(isDeliveryOnTime(orderLineVersion.getOrderStaDate(), firstReceivalDateAsDeliveryNoteDate, null));
            orderLineVersion.setStaAgreedDateOnTime(isDeliveryOnTime(orderLineVersion.getStaAgreedDate(), firstReceivalDateAsDeliveryNoteDate, null));
        }
        orderLineVersions.add(orderLineVersion);
        return orderLineVersions;
    }
    
    private static boolean isDeliveryOnTime(Date anyStaDate, Date deliveryDate, Boolean existingOnTimeValue) {
        if (deliveryDate != null && anyStaDate != null && (existingOnTimeValue == null || !existingOnTimeValue)
                && (deliveryDate.before(anyStaDate) || DateUtil.areDatesFromSameWeek(deliveryDate, anyStaDate))) {
            return true;
        }
        return false;
    }

    public static Material transformToMaterialEntity(OrderMigrationDTO dto, Long quantity, MaterialType materialType, MaterialLineStatus materialLineStatus,
            MaterialHeader materialHeader, FinanceHeader financeHeader) throws GloriaApplicationException {
        Material material = new Material();
        material.setMaterialType(materialType);
        material.setStatus(MaterialStatus.ADDED);
        material.setPartAffiliation(dto.getPartQualifier());
        material.setPartNumber(dto.getPartNumber());
        String partVersion = dto.getPartVersion();
        material.setPartVersion(partVersion);
        material.setPartVersionOriginal(partVersion);
        material.setPartName(StringUtils.isEmpty(dto.getDescription()) ? MISSING_PART_NAME : dto.getDescription());
        material.setUnitOfMeasure(dto.getUnitOfMeasure());
        // Add if STA Date in future
        Date orderStaDate = dto.getShipArriveDate();
        if (orderStaDate != null && orderStaDate.compareTo(DateUtil.getDateWithZeroTime(DateUtil.getCurrentUTCDateTime())) >= 0) {
            material.setRequiredStaDate(orderStaDate);
        }
        material.setObjectNumber(dto.getProject());
        material.setOrderNo(dto.getOrderNumber());
        material.setModularHarness(StringUtils.defaultIfEmpty(dto.getModularHarness(), null));
        material.setMigrated(MIGRATED);
        material.setCreatedDate(DateUtil.getDateWithZeroTime(DateUtil.getCurrentUTCDateTime()));

        MaterialLine materialLine = new MaterialLine();
        materialLine.setStatus(materialLineStatus);
        materialLine.setDirectSend(DirectSendType.NO);
        materialLine.setWhSiteId(dto.getShipToId());
        materialLine.setFinalWhSiteId(dto.getShipToId());
        materialLine.setStatusDate(DateUtil.getDateWithZeroTime(DateUtil.getCurrentUTCDateTime()));
        materialLine.setQuantity(quantity);
        materialLine.setRequestedExcluded(REQUESTED_EXCLUDED);
        materialLine.setReceivedDate(dto.getMinReceivedDate());
        materialLine.setOrderNo(dto.getOrderNumber());

        if (dto.isExternal()) {
            materialLine.setProcureType(ProcureType.EXTERNAL);
        } else {
            materialLine.setProcureType(ProcureType.INTERNAL);
        }

        if (materialType != null && materialType == MaterialType.USAGE) {
            material.setMtrlRequestVersionAccepted("Migrated");
        }

        material.setMaterialHeader(materialHeader);
        material.setFinanceHeader(financeHeader);
        MaterialLineStatusHelper.assignMaterialLineToMaterial(material, materialLine);
        return material;
    }

    public static ProcureLine transformToProcureLine(OrderMigrationDTO dto, FinanceHeader financeHeader, ProcureLineStatus procureLineStatus)
            throws GloriaApplicationException {
        ProcureLine procureLine = new ProcureLine();
        if (dto.isExternal()) {
            procureLine.setProcureType(ProcureType.EXTERNAL);
        } else {
            procureLine.setProcureType(ProcureType.INTERNAL);
        }
        procureLine.setProcureDate(dto.getProcureDate());
        procureLine.setpPartAffiliation(dto.getPartQualifier());
        procureLine.setpPartNumber(dto.getPartNumber());
        procureLine.setpPartVersion(dto.getPartVersion());
        procureLine.setpPartName(StringUtils.isEmpty(dto.getDescription()) ? MISSING_PART_NAME : dto.getDescription());
        procureLine.setStatus(procureLineStatus);
        procureLine.setWhSiteId(dto.getShipToId());
        procureLine.setOrderNo(dto.getOrderNumber());
        procureLine.setFinanceHeader(financeHeader);
        procureLine.setMaterialControllerTeam(dto.getMaterialControllerTeam());
        procureLine.setMaterialControllerId(dto.getIssuerId());
        procureLine.setMaterialControllerName(dto.getIssuerName());
        procureLine.setUnitPrice(dto.getPrice());
        procureLine.setReferenceGps(dto.getReference());
        String purchaseOrgCode = dto.getPurchasingOrganization();
        if (!StringUtils.isEmpty(purchaseOrgCode)) {
            procureLine.setPurchaseOrgCode(purchaseOrgCode);
            procureLine.setPurchaseOrgName(findOrganizationByPurchaseOrgCode(purchaseOrgCode));
        }
        String noteText = dto.getNote();
        if (!StringUtils.isEmpty(noteText) && noteText.length() >= MAX_2048) {
            noteText = noteText.substring(0, MAX_2048);
        }
        procureLine.setProcureInfo(noteText);
        return procureLine;
    }

    public static FinanceHeader transformToFinanceHeader(OrderMigrationDTO dto, MaterialType materialType) {
        FinanceHeader financeHeader = new FinanceHeader();
        if (!materialType.equals(MaterialType.RELEASED)) {
            financeHeader.setProjectId(dto.getProject());
        }
        financeHeader.setCompanyCode(dto.getCompanyCode());
        financeHeader.setGlAccount(dto.getGlAccount());
        financeHeader.setWbsCode(dto.getWbsElement());
        financeHeader.setCostCenter(dto.getCostCenter());
        return financeHeader;
    }

    public static MaterialHeader transformToMaterialHeaderEntity(String testObject, MaterialType materialType) {
        // materialHeader
        MaterialHeader materialHeader = new MaterialHeader();

        if (!StringUtils.isEmpty(testObject) && !materialType.equals(MaterialType.ADDITIONAL_USAGE)) {
            testObject = testObject.replace("[", "");
            String referenceId = StringUtils.substringBefore(testObject, "@");
            materialHeader.setReferenceId(referenceId);
        }
        materialHeader.setActive(true);
        materialHeader.setMtrlRequestId("Migrated");
        materialHeader.setRequestType(RequestType.SINGLE);

        // materialHeaderVersion
        MaterialHeaderVersion materialHeaderVersion = new MaterialHeaderVersion();
        materialHeaderVersion.setHeaderVersion(1);
        materialHeaderVersion.setMaterialHeader(materialHeader);
        materialHeader.setAccepted(materialHeaderVersion);
        return materialHeader;
    }

    public static Map<String, List<OrderMigrationDTO>> sortAfterOrderNo(List<OrderMigrationDTO> orderMigrationDTOs) {
        Map<String, List<OrderMigrationDTO>> orderMap = new HashMap<String, List<OrderMigrationDTO>>();
        for (OrderMigrationDTO orderMigrationDTO : orderMigrationDTOs) {
            if (orderMap.containsKey(orderMigrationDTO.getOrderNumber())) {
                orderMap.get(orderMigrationDTO.getOrderNumber()).add(orderMigrationDTO);
            } else {
                List<OrderMigrationDTO> sameOrderNoList = new ArrayList<OrderMigrationDTO>();
                sameOrderNoList.add(orderMigrationDTO);
                orderMap.put(orderMigrationDTO.getOrderNumber(), sameOrderNoList);
            }
        }
        return orderMap;
    }

    public static OrderMigrationDTO getRelatedOrderMigrationDTO(List<OrderMigrationDTO> sameOrderNoDtos, OrderLine orderLine) {
        for (OrderMigrationDTO dto : sameOrderNoDtos) {
            if (dto.getPartNumber().equals(orderLine.getPartNumber()) 
                    && dto.getPartVersion().equals(orderLine.getOrderLineVersions().get(0).getPartVersion())) {
                return dto;
            }
        }
        return null;
    }

    public static String findPPSuffixShipToId(String suffix) {
        return ppSuffixShipToId.get(suffix);
    }

    public static String findCompanyCodeForOrderSite(String siteId) {
        Site site = sitesMap.get(siteId);
        if (site != null) {
            return site.getCompanyCode();
        }
        return null;
    }

    public static boolean isSendGRtoSAP(String companyCode) {
        return isSendGRtoSAPCompanyCodeMap.containsKey(companyCode);
    }

    public static boolean isSendPOtoSAP(String companyCode) {
        return isSendPOtoSAPCompanyCodeMap.containsKey(companyCode);
    }

    public static DeliveryFollowUpTeam findDeliveryControllerTeamForSuffix(String suffix) {
        return suffixDeliveryControllerTeamMap.get(suffix);
    }

    public static String findSapPurchaseOrgForCompanyCode(String companyCode) {
        return sapPurchaseOrgCompanyCodeMap.get(companyCode);
    }

    public static String findSapQuantityBlockReceiverIdForCompanyCode(String companyCode) {
        return sapQuantityBlockReceiverIdCompanyCodeMap.get(companyCode);
    }

    public static String findMaterialControllerTeamForCompanyCode(String companyCode) {
        return companyCodeToMaterialControllerTeamMap.get(companyCode);
    }

    public static String findUserNameForUserId(String userId) {
        return userIdToUserNameMap.get(userId);
    }

    public static void setAllCompanyCodes(Set<CompanyCode> allCompanyCodes) {
        for (CompanyCode companyCode : allCompanyCodes) {
            if (companyCode.isSendGRtoSAP()) { // add only if true
                isSendGRtoSAPCompanyCodeMap.put(companyCode.getCode(), companyCode.isSendGRtoSAP());
            }
            if (companyCode.isSendPOtoSAP()) { // add only if true
                isSendPOtoSAPCompanyCodeMap.put(companyCode.getCode(), companyCode.isSendPOtoSAP());
            }
            sapPurchaseOrgCompanyCodeMap.put(companyCode.getCode(), companyCode.getSapPurchaseOrg());
            sapQuantityBlockReceiverIdCompanyCodeMap.put(companyCode.getCode(), companyCode.getSapQuantityBlockReceiverId());
            companyCodeToMaterialControllerTeamMap.put(companyCode.getCode(),
                                                       companyGroupToMaterialControllerTeamMap.get(companyCode.getCompanyGroup().getCode()));
        }
    }

    public static void setAllPurchaseOrganizations(List<PurchaseOrganisation> purchaseOrganisations) {
        if (purchaseOrganisations != null && !purchaseOrganisations.isEmpty()) {
            for (PurchaseOrganisation purchaseOrganisation : purchaseOrganisations) {
                purchaseOrgMap.put(purchaseOrganisation.getOrganisationCode(), purchaseOrganisation.getOrganisationName());
            }
        }
    }
    
    public static void setAllSupplierCounterParts(Set<SupplierCounterPart> allSupplierCounterParts, String[] sitesToBeMigrated) {
        for (SupplierCounterPart supplierCounterPart : allSupplierCounterParts) {
            suffixDeliveryControllerTeamMap.put(supplierCounterPart.getPpSuffix(), supplierCounterPart.getDeliveryFollowUpTeam());
            for (String siteId : sitesToBeMigrated) {
                if (supplierCounterPart.getShipToId().equals(siteId)) {
                    ppSuffixShipToId.put(supplierCounterPart.getPpSuffix(), supplierCounterPart.getShipToId());
                }
            }
        }
    }

    public static void setAllMaterialControllerTeams(Set<Team> materialControllerTeams) {
        for (Team team : materialControllerTeams) {
            companyGroupToMaterialControllerTeamMap.put(team.getCompanyCodeGroup(), team.getName());
        }

    }

    public static void setAllUserIdToUserNames(Set<UserDTO> users) {
        if (users != null && !users.isEmpty()) {
            for (UserDTO userDTO : users) {
                userIdToUserNameMap.put(userDTO.getId(), userDTO.getUserName());
            }
        }
    }

    public static void setAllBuildSites(List<Site> sites) {
        if (sites != null && !sites.isEmpty()) {
            for (Site site : sites) {
                sitesMap.put(site.getSiteId(), site);
                if (site.getBuildSiteType().equals(BuildSiteType.PLANT.toString())) {
                    buildSites.add(site.getSiteId());
                }
            }
        }
    }

    public static void setAllSites(List<Site> sites) {
        if (sites != null && !sites.isEmpty()) {
            for (Site site : sites) {
                sitesMap.put(site.getSiteId(), site);
            }
        }
    }
    
    public static String findOrganizationByPurchaseOrgCode(String orgCode) {
        return purchaseOrgMap.get(orgCode);
    }

    public static void validateApplyGeneralRules(Set<OrderMigrationDTO> orderMigrationDTOs, List<String> uniqueOrderLineKeys,
            MaterialHeaderRepository materialHeaderRepository) {
        long time = System.currentTimeMillis();
        for (OrderMigrationDTO orderMigrationDTOToBeValidated : orderMigrationDTOs) {
            MigrationOrderType.valueOf(orderMigrationDTOToBeValidated.getOrderType()).validateOrder(orderMigrationDTOToBeValidated, uniqueOrderLineKeys,
                                                                                                    materialHeaderRepository);
        }
        log("Done validateApplyGeneralRules in - " + (System.currentTimeMillis() - time) / MILLI_SEC + " sec");
    }

    public static void validateApplySiteSpecificRules(List<OrderMigrationDTO> validOrders, CommonServices commonServices) {
        long time = System.currentTimeMillis();
        for (OrderMigrationDTO orderMigrationDTO : validOrders) {
            String suffix = orderMigrationDTO.getSuffix();
            String shipToId = findPPSuffixShipToId(orderMigrationDTO.getSuffix());
            DeliveryFollowUpTeam deliveryFollowUpTeam = findDeliveryControllerTeamForSuffix(suffix);

            MigrationOrderType.valueOf(orderMigrationDTO.getOrderType()).validateApplySiteSpecificRules(orderMigrationDTO, suffix, shipToId,
                                                                                                        deliveryFollowUpTeam, commonServices);
        }
        log("Done validateApplySiteSpecificRules in - " + (System.currentTimeMillis() - time) / MILLI_SEC + " sec");
    }

    private static long getrequisitionNumber(Long reqNumber) {
        if (reqNumber != null) {
            return reqNumber;
        }
        return ++randomNumber;
    }

    public static OrderSap transformToOrderSapEntity(List<OrderMigrationDTO> orderMigrationDTOs, Order order, List<OrderLine> orderLines, String companyCode,
            int totalOrderSapLinesPlaced, int totalOrderLinesRPSapLines, int totalOrderLinesReceivedSapLines) {
        OrderSap orderSap = new OrderSap();

        orderSap.setCompanyCode(companyCode);
        orderSap.setOrderType(ORDER_TYPE);
        orderSap.setVendor(StringUtils.leftPad(order.getSupplierId(), LEFT_PAD_VENDOR, '0'));
        orderSap.setPurchaseOrganization(findSapPurchaseOrgForCompanyCode(companyCode));
        orderSap.setPurchaseGroup(PURCHASE_GROUP);
        orderSap.setDocumentDate(order.getOrderDateTime());

        OrderLine firstOrderLine = orderLines.get(0);
        orderSap.setCurrency(firstOrderLine.getCurrency());
        orderSap.setPurchaseType(PURCHASE_TYPE);
        orderSap.setUniqueExtOrder(order.getOrderIdGps());
        orderSap.setMessageStatus(MessageStatus.SHOULD_BE_SEND);

        List<OrderSapLine> orderSapLines = createOrderSapLines(orderMigrationDTOs, orderLines, order, orderSap, companyCode, totalOrderSapLinesPlaced,
                                                               totalOrderLinesRPSapLines, totalOrderLinesReceivedSapLines);

        orderSap.getOrderSapLines().addAll(orderSapLines);
        return orderSap;
    }

    private static List<OrderSapLine> createOrderSapLines(List<OrderMigrationDTO> orderMigrationDTOs, List<OrderLine> orderLines, Order order,
            OrderSap orderSap, String companyCode, int totalOrderSapLinesPlaced, int totalOrderLinesRPSapLines, int totalOrderLinesReceivedSapLines) {
        List<OrderSapLine> orderSapLines = new ArrayList<OrderSapLine>();
        // reorder orderlines "asc" to maintain proper re-creating of "sequence[purchaseOrderItem]", during GPS ammendment.
        Collections.sort(orderLines, new Comparator<OrderLine>() {
            @Override
            public int compare(OrderLine o1, OrderLine o2) {
                return Utils.compare(o1.getOrderLineOID(), o2.getOrderLineOID());
            }
        });
        long sequence = 1;
        for (OrderLine orderLine : orderLines) {
            OrderMigrationDTO relatedOrderMigrationDTO = getRelatedOrderMigrationDTO(orderMigrationDTOs, orderLine);
            if (relatedOrderMigrationDTO != null && relatedOrderMigrationDTO.getOrderLinePaid().trim().equals(NOT_PAID_ORDER)) {
                OrderLineVersion orderLineVersion = orderLine.getOrderLineVersions().get(0);
                long processPurchaseOrderQty = 0;
                if (orderLine.getPossibleToReceiveQuantity() < orderLineVersion.getQuantity()) {
                    processPurchaseOrderQty = orderLine.getPossibleToReceiveQuantity() - orderLine.getPaidQuantity();
                } else {
                    processPurchaseOrderQty = orderLineVersion.getQuantity() - orderLine.getPaidQuantity();
                }
                if (processPurchaseOrderQty > 0) {
                    if (orderLine.getStatus() == OrderLineStatus.PLACED) {
                        totalOrderSapLinesPlaced++;
                    } else if (orderLine.getStatus() == OrderLineStatus.RECEIVED_PARTLY) {
                        totalOrderLinesRPSapLines++;
                    } else if (orderLine.getStatus() == OrderLineStatus.COMPLETED) {
                        totalOrderLinesReceivedSapLines++;
                    }
                    OrderSapLine orderSapLine = new OrderSapLine();
                    orderSapLine.setOrderSap(orderSap);
                    orderSapLine.setPurchaseOrderitem(sequence);
                    orderSapLine.setAction(SAPParam.ACTION_CREATE);
                    orderSapLine.setOrderReference(order.getOrderNo());
                    orderSapLine.setPartNumber(orderLine.getPartNumber());
                    orderSapLine.setShortText(orderLine.getPartName());
                    orderSapLine.setPlant(order.getMaterialUserId());
                    orderSapLine.setMaterialGroup(MATERIAL_GROUP);
                    orderSapLine.setIsoPurchaseOrderUnit(orderLine.getUnitOfMeasure());
                    orderSapLine.setIsoOrderPriceUnit(orderLine.getUnitOfMeasure());

                    orderSapLine.setOrderLineVersion(orderLineVersion);
                    orderSapLine.setQuantity(processPurchaseOrderQty);
                    orderSapLine.setNetPrice(orderLineVersion.getUnitPrice());
                    orderSapLine.setPriceUnit(String.valueOf(orderLineVersion.getPerQuantity()));
                    orderSapLine.setCurrentBuyer(StringUtils.leftPad(orderLineVersion.getBuyerId(), LEFT_PAD_CURRENTBUYER, '0'));
                    orderSapLine.setTaxCode(TAX_CODE);
                    orderSapLine.setAccountAssignmentCategory(ACCOUNT_ASSIGNMENT_CATEGORY);
                    orderSapLine.setUnlimitedDeliveryIndicator(UNLIMITED_DELIVERY_INDICATOR);
                    orderSapLine.setGrIndicator(GR_INDICATOR);
                    orderSapLine.setNonValuedGrIndicator(NON_VALUED_GR_INDICATOR);
                    orderSapLine.setIrIndicator(IR_INDICATOR);
                    orderSapLine.setAcknowledgementNumber(order.getOrderNo());
                    orderSapLine.setPurchaseRequisitionNumber(findSapQuantityBlockReceiverIdForCompanyCode(companyCode));

                    orderSapLine.setOrderSapAccounts(createOrderSapAccounts(orderLine.getProcureLine(), orderSapLine));
                    orderSapLine.setOrderSapSchedules(createOrderSapSchedules(order, orderSapLine));
                    orderSapLines.add(orderSapLine);
                    sequence++;

                    relatedOrderMigrationDTO.setSendPPO(true);
                }
            }
        }
        return orderSapLines;
    }

    private static List<OrderSapAccounts> createOrderSapAccounts(ProcureLine procureLine, OrderSapLine orderSapLine) {
        List<OrderSapAccounts> orderSapAccounts = new ArrayList<OrderSapAccounts>();
        OrderSapAccounts ordSapAccounts = new OrderSapAccounts();
        // set sequenceGenerator
        ordSapAccounts.setSequence(1);
        if (procureLine != null) {
            FinanceHeader financeHeader = procureLine.getFinanceHeader();
            ordSapAccounts.setGeneralLedgerAccount(StringUtils.leftPad(financeHeader.getGlAccount(), LEFT_PAD_GENERALLEDGER, '0'));
            ordSapAccounts.setCostCenter(StringUtils.leftPad(financeHeader.getCostCenter(), LEFT_PAD_COSTCENTER, '0'));
            ordSapAccounts.setWbsElement(financeHeader.getWbsCode());
        }
        ordSapAccounts.setOrderSapLine(orderSapLine);
        orderSapAccounts.add(ordSapAccounts);
        return orderSapAccounts;
    }

    private static List<OrderSapSchedule> createOrderSapSchedules(Order order, OrderSapLine orderSapLine) {
        List<OrderSapSchedule> orderSapSchedules = new ArrayList<OrderSapSchedule>();
        OrderSapSchedule orderSapSchedule = new OrderSapSchedule();
        orderSapSchedule.setOrderSapLine(orderSapLine);
        orderSapSchedule.setCategoryOfDeliveryDate(CATEGORY_OF_DELIVERY_DATE);
        orderSapSchedule.setDeliveryDate(order.getOrderDateTime());
        orderSapSchedules.add(orderSapSchedule);
        return orderSapSchedules;
    }

    public static GoodsReceiptHeader transformToGoodsReceiptHeaderEntity(OrderMigrationDTO dto, String companyCode, long goodsMovementQty, Order order,
            boolean isGrToBeSent) {
        GoodsReceiptHeader goodsReceiptHeader = new GoodsReceiptHeader();
        goodsReceiptHeader.setCompanyCode(companyCode);
        goodsReceiptHeader.setPostedDateTime(dto.getMinReceivedDate());
        goodsReceiptHeader.setHeaderText(GOODS_RECEIPT_HEADER_TEXT);
        goodsReceiptHeader.setAssignCodeGM(GOODS_RECEIPT_ASSIGN_CODE_GM);
        // TODO - This mapping could be wrong!
        goodsReceiptHeader.setDocumentDate(dto.getMinReceivedDate());

        GoodsReceiptLine goodsReceiptLine = new GoodsReceiptLine();
        goodsReceiptLine.setPlant(dto.getMaterialUserid());
        goodsReceiptLine.setMovementType(GOODS_RECEIPT_MOVEMENT_TYPE);
        goodsReceiptLine.setVendor(StringUtils.leftPad(dto.getSupplierCode(), LEFT_PAD_VENDOR, '0'));
        goodsReceiptLine.setVendorMaterialNumber(dto.getPartNumber());
        goodsReceiptLine.setOrderReference(dto.getOrderNumber());
        goodsReceiptLine.setQuantity(goodsMovementQty);
        goodsReceiptLine.setIsoUnitOfMeasure(dto.getUnitOfMeasure());

        goodsReceiptLine.setGoodsReceiptHeader(goodsReceiptHeader);
        goodsReceiptLine.setMovementIndicator(MOVEMENT_INDICATOR);
        goodsReceiptLine.setStatus(GoodsReceiptLineStatus.RECEIVED);
        goodsReceiptHeader.getGoodsReceiptLines().add(goodsReceiptLine);
        MessageStatus messageStatusToSet = MessageStatus.SENT;
        if (InternalExternal.EXTERNAL == order.getInternalExternal() && isGrToBeSent) {
            messageStatusToSet = MessageStatus.SHOULD_BE_SEND;
        }
        goodsReceiptHeader.setMessageStatus(messageStatusToSet);
        return goodsReceiptHeader;
    }

    public static DeliveryNote transformToDeliveryNote(OrderMigrationDTO dto, BinLocation binLocation, ProcureLine procureLine)
            throws GloriaApplicationException {

        Date firstReceivalDate = dto.getMinReceivedDate();

        DeliveryNote deliveryNote = new DeliveryNote();
        deliveryNote.setDeliveryNoteDate(firstReceivalDate);
        deliveryNote.setReceiveType(ReceiveType.REGULAR);
        String deliveryNoteNo = dto.getPackingSlipNumbersForReceivals();
        if (StringUtils.isEmpty(dto.getPackingSlipNumbersForReceivals())) {
            deliveryNoteNo = "MIGORDER-" + ++randomNumberForDeliveryNote;
        }
        deliveryNote.setDeliveryNoteNo(deliveryNoteNo);
        deliveryNote.setOrderNo(dto.getOrderNumber());
        deliveryNote.setWhSiteId(dto.getShipToId());
        deliveryNote.setSupplierId(dto.getSupplierCode());
        deliveryNote.setSupplierName(dto.getSupplierName());
        deliveryNote.setMaterialUserId(dto.getMaterialUserid());

        DeliveryNoteLine deliveryNoteLine = new DeliveryNoteLine();
        deliveryNoteLine.setPartAffiliation(dto.getPartQualifier());
        deliveryNoteLine.setPartName(StringUtils.isEmpty(dto.getDescription()) ? MISSING_PART_NAME : dto.getDescription());
        deliveryNoteLine.setPartNumber(dto.getPartNumber());
        deliveryNoteLine.setPartVersion(dto.getPartVersion());
        deliveryNoteLine.setProjectId(dto.getProject());
        deliveryNoteLine.setProcureType(procureLine.getProcureType());
        deliveryNoteLine.setPossibleToReceiveQty(dto.getPossibleToReceiveQuantity());
        deliveryNoteLine.setReceivedQuantity(dto.getReceivedQuantity());
        deliveryNoteLine.setDeliveryNoteQuantity(dto.getReceivedQuantity());
        deliveryNoteLine.setStatus(DeliveryNoteLineStatus.RECEIVED);
        deliveryNoteLine.setReceivedDateTime(firstReceivalDate);
        String referenceIds = dto.getReferenceIds();
        if (referenceIds != null && referenceIds.length() >= MAX_2048) {
            referenceIds = referenceIds.substring(0, MAX_2048);
        }
        deliveryNoteLine.setReferenceIds(referenceIds);

        DeliveryNoteSubLine deliveryNoteSubLine = new DeliveryNoteSubLine();
        deliveryNoteSubLine.setDirectSend(false);
        deliveryNoteSubLine.setToApproveQty(0L);
        deliveryNoteSubLine.setToReceiveQty(dto.getReceivedQuantity());
        if (binLocation != null) {
            deliveryNoteSubLine.setBinLocation(binLocation.getBinLocationOid());
            deliveryNoteSubLine.setBinLocationCode(binLocation.getCode());
        }

        deliveryNoteSubLine.setDeliveryNoteLine(deliveryNoteLine);
        deliveryNoteLine.getDeliveryNoteSubLines().add(deliveryNoteSubLine);
        deliveryNoteLine.setDeliveryNote(deliveryNote);
        deliveryNote.getDeliveryNoteLine().add(deliveryNoteLine);
        return deliveryNote;
    }

    public static Requisition transformToRequisition(OrderMigrationDTO dto, ProcureLine procureLine, CommonServices commonServices) {
        Requisition requisition = new Requisition();
        requisition.setIssuerOrganisation("");

        String materialControllerUserId = procureLine.getMaterialControllerId();
        requisition.setIssuerHandleId(materialControllerUserId);
        requisition.setIssuerName(dto.getIssuerName());
        String costCenter = procureLine.getFinanceHeader().getCostCenter();
        requisition.setIssuerDepartment(StringUtils.right(costCenter, DEPARTMENT_NAME_LENGTH));
        requisition.setIssuerUserId(dto.getIssuerId());
        requisition.setMaterialUserId(dto.getMaterialUserid());
        requisition.setPpSuffix(dto.getSuffix());

        Material usageMaterial = null;
        if (procureLine.getMaterials() != null && !procureLine.getMaterials().isEmpty()) {
            for (Material material : procureLine.getMaterials()) {
                if (!material.getMaterialType().equals(MaterialType.ADDITIONAL) && !material.getMaterialType().equals(MaterialType.RELEASED)) {
                    usageMaterial = material;
                    break;
                }
            }
        }

        if (usageMaterial != null) {
            MaterialHeaderVersion materialHeaderVersion = usageMaterial.getMaterialHeader().getAccepted();
            requisition.setContactPersonName(materialHeaderVersion.getContactPersonName());
            requisition.setOriginatorUserId(materialHeaderVersion.getRequesterUserId());
            requisition.setOriginatorName(materialHeaderVersion.getRequesterName());
        }
        requisition.setQuantity(procureLine.getProcureType().convertQuantityToGPSUnits(dto.getOrderedQuantity(), dto.getUnitOfMeasure(), commonServices));

        Double maxPrice = procureLine.getMaxPrice();
        if (ProcureType.INTERNAL.equals(procureLine.getProcureType()) && procureLine.getMaxPrice() == null) {
            maxPrice = new Double(0D);
        }
        if (maxPrice != null) {
            requisition.setMaximumPrice(maxPrice.doubleValue());
            requisition.setMaximumcurrency(procureLine.getCurrency());
        } else {
            requisition.setMaximumcurrency("");
        }
        requisition.setPriceType(QUO);
        if (procureLine.getRequiredStaDate() != null) {
            requisition.setRequiredStaWeek(DateUtil.calculateWeekForAGivenDate(procureLine.getRequiredStaDate()));
        }
        requisition.setRequiredStaDate(procureLine.getRequiredStaDate());

        requisition.setPurchaseInfo1(procureLine.getpPartModification());
        requisition.setPartNumber(procureLine.getpPartNumber());
        requisition.setPartQualifier(procureLine.getpPartAffiliation());
        requisition.setPartVersion(procureLine.getpPartVersion());
        requisition.setPartName(procureLine.getpPartName());
        requisition.setUnitOfMeasure(procureLine.getProcureType().translateUOMToGPSUnits(dto.getUnitOfMeasure(), commonServices));
        FinanceHeader financeHeader = procureLine.getFinanceHeader();
        requisition.setProjectId(financeHeader.getProjectId());
        requisition.setWbsCode(financeHeader.getWbsCode());
        requisition.setGlAccount(financeHeader.getGlAccount());
        requisition.setCostCenter(financeHeader.getCostCenter());

        requisition.setBuyerId(procureLine.getBuyerCode());
        requisition.setPurchaseOrganizationCode(procureLine.getPurchaseOrgCode());
        requisition.setReference(procureLine.getReferenceGps());
        return requisition;
    }

    public static void validateWithRulesCommonForOpenAndClosedOrder(OrderMigrationDTO orderMigrationDTO, List<String> uniqueOrderLineKeys,
            MaterialHeaderRepository materialHeaderRepository) {
        String key = orderMigrationDTO.getOrderNumber() + "_" + orderMigrationDTO.getPartNumber() + "_" + orderMigrationDTO.getPartVersion() + "_"
                + orderMigrationDTO.getPartQualifier();
        if (uniqueOrderLineKeys.contains(key)) {
            markAsInvalid(orderMigrationDTO, "Duplicate Order line!");
        } else {
            uniqueOrderLineKeys.add(key);
        }

        if (StringUtils.isEmpty(orderMigrationDTO.getLineType())) {
            markAsInvalid(orderMigrationDTO, "LINE_TYPE!");
        }
        if (StringUtils.isEmpty(orderMigrationDTO.getOrderNumber())) {
            markAsInvalid(orderMigrationDTO, "ORDER_NUMBER!");
        }
        if (StringUtils.isEmpty(orderMigrationDTO.getOrderDateOriginal())) {
            markAsInvalid(orderMigrationDTO, "ORDER_DATE!");
        }
        if (StringUtils.isEmpty(orderMigrationDTO.getPartNumber())) {
            markAsInvalid(orderMigrationDTO, "PART_NUMBER!");
        }
        if (StringUtils.isEmpty(orderMigrationDTO.getPartVersion())) {
            markAsInvalid(orderMigrationDTO, "STAGE+ISSUE!");
        }
        Long orderedQuantity = orderMigrationDTO.getOrderedQuantity();
        if (orderedQuantity == null || orderedQuantity == 0 || orderedQuantity > OrderMigrationHelper.MAX_QUANTITY) {
            markAsInvalid(orderMigrationDTO, "ORDERED_QUANTITY is missing or is zero or greater than 99999!");
        }
        Long receivedQuantity = orderMigrationDTO.getReceivedQuantity();
        if (receivedQuantity != null && receivedQuantity > OrderMigrationHelper.MAX_QUANTITY) {
            markAsInvalid(orderMigrationDTO, "RECEIVED_QTY greater than 99999!");
        }
        if (StringUtils.isEmpty(orderMigrationDTO.getOrderLineStatusOriginal())) {
            markAsInvalid(orderMigrationDTO, "ORDER_LINE_STATUS missing!");
        }
        if (StringUtils.isEmpty(orderMigrationDTO.getTestObjectsQtyOriginal()) && StringUtils.isEmpty(orderMigrationDTO.getSparePartQuantityOriginal())) {
            markAsInvalid(orderMigrationDTO, "Both TEST_OBJECT_QTY and SPARE_PART_QTY missing!");
        }
        if (StringUtils.isEmpty(orderMigrationDTO.getOrderSiteOriginal())) {
            markAsInvalid(orderMigrationDTO, "ORDER_SITE missing!");
        }
        orderMigrationDTO.setCompanyCode(findCompanyCodeForOrderSite(orderMigrationDTO.getOrderSiteId()));

        /*
         * do we need this ???? if (!buildSites.contains(orderMigrationDTO.getSupplierCode())) { orderMigrationDTO.setExternal(true); }
         */

        orderMigrationDTO.setExternal(("YES").equalsIgnoreCase(trim(orderMigrationDTO.getOrderFromGps())));

        if (StringUtils.isEmpty(orderMigrationDTO.getCompanyCode())) {
            markAsInvalid(orderMigrationDTO, "Company Code does not exists for this Suffix!");
        } else {
            orderMigrationDTO.setMaterialControllerTeam(findMaterialControllerTeamForCompanyCode(orderMigrationDTO.getCompanyCode()));
        }
    }

    public static void validateRulesForOpenOrder(OrderMigrationDTO orderMigrationDTO, List<String> uniqueOrderLineKeys,
            MaterialHeaderRepository materialHeaderRepository) {
        if (StringUtils.isEmpty(orderMigrationDTO.getSuffix())) {
            markAsInvalid(orderMigrationDTO, "SUFFIX!");
        }
        if (StringUtils.isEmpty(orderMigrationDTO.getProject())) {
            markAsInvalid(orderMigrationDTO, "PROJECT missing!");
        }
        if (StringUtils.isEmpty(orderMigrationDTO.getIssuerId())) {
            markAsInvalid(orderMigrationDTO, "ISSUER_ID missing!");
        }
        if (StringUtils.isEmpty(orderMigrationDTO.getOrdererId())) {
            markAsInvalid(orderMigrationDTO, "ORDERER_ID missing!");
        }
        if (StringUtils.isEmpty(orderMigrationDTO.getShipArriveDateOriginal())) {
            markAsInvalid(orderMigrationDTO, "SHIP_ARRIVE_DATE missing!");
        }

        validateOrderedQtyandTestObjectQty(orderMigrationDTO);
        validatePossibleToReceiveQuantity(orderMigrationDTO);
        validateOrdersIfNotInPlacedStatus(orderMigrationDTO);

        validateExternalOpenOrders(orderMigrationDTO);

        validateInternalOpenOrders(orderMigrationDTO);
    }

    public static void validateRulesForClosedOrder(OrderMigrationDTO orderMigrationDTO, List<String> uniqueOrderLineKeys,
            MaterialHeaderRepository materialHeaderRepository) {
        validateOrderedQtyandTestObjectQty(orderMigrationDTO);
        validatePossibleToReceiveQuantity(orderMigrationDTO);
        validateOrdersIfNotInPlacedStatus(orderMigrationDTO);
    }

    private static void validateInternalOpenOrders(OrderMigrationDTO orderMigrationDTO) {
        // do nothing
    }

    private static void validateExternalOpenOrders(OrderMigrationDTO orderMigrationDTO) {
        if (("YES").equalsIgnoreCase(orderMigrationDTO.getOrderFromGps().trim())) {
            if (StringUtils.isEmpty(orderMigrationDTO.getPriceOriginal())) {
                markAsInvalid(orderMigrationDTO, "PRICE missing!");
            }
            if (StringUtils.isEmpty(orderMigrationDTO.getUnitOfPrice())) {
                markAsInvalid(orderMigrationDTO, "UNIT_OF_PRICE missing!");
            }
            if (StringUtils.isEmpty(orderMigrationDTO.getAmountOriginal())) {
                markAsInvalid(orderMigrationDTO, "AMOUNT missing!");
            }
            if (StringUtils.isEmpty(orderMigrationDTO.getCurrency())) {
                markAsInvalid(orderMigrationDTO, "CURRENCY missing!");
            }
            if (StringUtils.isEmpty(orderMigrationDTO.getMaterialUserid())) {
                markAsInvalid(orderMigrationDTO, "MATERIAL_USERID  missing!");
            }
            if (StringUtils.isEmpty(orderMigrationDTO.getBuyerCode())) {
                markAsInvalid(orderMigrationDTO, "BUYER_CODE missing!");
            }
            if (StringUtils.isEmpty(orderMigrationDTO.getPurchasingOrganization())) {
                markAsInvalid(orderMigrationDTO, "PURCHASING_ORGANIZATION  missing!");
            }
            if (StringUtils.isEmpty(orderMigrationDTO.getSupplierCode())) {
                markAsInvalid(orderMigrationDTO, "SUPPLIER_CODE missing!");
            }
            if (StringUtils.isEmpty(orderMigrationDTO.getSupplierName())) {
                markAsInvalid(orderMigrationDTO, "SUPPLIER_NAME missing!");
            }
            if (StringUtils.isEmpty(orderMigrationDTO.getGlAccount())) {
                markAsInvalid(orderMigrationDTO, "GL_ACCOUNT missing!");
            }
            if (StringUtils.isEmpty(orderMigrationDTO.getWbsElement())) {
                markAsInvalid(orderMigrationDTO, "WBS_ELEMENT missing!");
            }
            if (StringUtils.isEmpty(orderMigrationDTO.getCostCenter())) {
                markAsInvalid(orderMigrationDTO, "COST_CENTER missing!");
            }
        }
    }

    private static void validatePossibleToReceiveQuantity(OrderMigrationDTO orderMigrationDTO) {
        Long orderedQuantity = orderMigrationDTO.getOrderedQuantity();
        Long receivedQuantity = orderMigrationDTO.getReceivedQuantity();
        Long possibleToReceiveQuantity = orderMigrationDTO.getPossibleToReceiveQuantity();
        if (possibleToReceiveQuantity != null) {
            long ptr = possibleToReceiveQuantity.longValue();
            if (ptr == 0) {
                markAsInvalid(orderMigrationDTO, "Possible to Receive Qty is zero!");
            } else if (orderMigrationDTO.getOrderLineStatus().equals(OrderLineStatus.COMPLETED) && ptr != orderedQuantity.longValue()
                    && ptr != receivedQuantity.longValue()) {
                markAsInvalid(orderMigrationDTO, "POSSIBLE TO RECEIVE QTY should either be equal to Ordered Qty or Received Qty!");

            } else if (orderMigrationDTO.getOrderLineStatus().equals(OrderLineStatus.RECEIVED_PARTLY) && (ptr <= receivedQuantity.longValue())) {
                markAsInvalid(orderMigrationDTO, "POSSIBLE TO RECEIVE QTY cannot be less than or equal to Received Qty when it is partly received!");

            }
        }
    }

    private static void validateOrderedQtyandTestObjectQty(OrderMigrationDTO orderMigrationDTO) {
        Long orderedQuantity = orderMigrationDTO.getOrderedQuantity();
        if (orderedQuantity != null && orderedQuantity > 0) {
            List<String> testobjects = orderMigrationDTO.getTestObjectsQty();
            long totalQuantity = 0;
            UniqueItems idSets = new UniqueItems();
            for (String testObject : testobjects) {
                if (!StringUtils.isEmpty(testObject)) {
                    testObject = testObject.replace("[", "");
                    idSets.add(StringUtils.substringBefore(testObject, "@"));
                    String qty = StringUtils.substringAfter(testObject, "@");
                    try {
                        long quantity = new Long(qty);
                        totalQuantity += quantity;
                    } catch (NumberFormatException nfe) {
                        markAsInvalid(orderMigrationDTO, "Test Object Quantity is not a number!");
                    }
                }
            }
            orderMigrationDTO.setReferenceIds(idSets.createCommaSeparatedKey());
            orderMigrationDTO.setTotalTestobjectsQty(totalQuantity);
        }
    }

    @Deprecated
    public static void validateMatchMaterialsForReceivedOrders2(List<OrderMigrationDTO> validOrders, MaterialHeaderRepository materialHeaderRepository) {
        long time = System.currentTimeMillis();
        Set<String> receivedOrderNumbers = receivedOrderNos(validOrders);
        List<Material> materials = materialHeaderRepository.findMaterialsForOrderNos(receivedOrderNumbers);
        if (materials != null && !materials.isEmpty()) {
            for (OrderMigrationDTO orderMigrationDTO : validOrders) {
                if (OrderLineStatus.COMPLETED == orderMigrationDTO.getOrderLineStatus()
                        || OrderLineStatus.RECEIVED_PARTLY == orderMigrationDTO.getOrderLineStatus()) {
                    for (Material material : materials) {
                        if (orderMigrationDTO.getOrderNumber().equals(material.getOrderNo())
                                && orderMigrationDTO.getPartNumber().equals(material.getPartNumber())
                                && orderMigrationDTO.getPartVersion().equals(material.getPartVersion())
                                && orderMigrationDTO.getPartQualifier().equals(material.getPartAffiliation())) {
                            orderMigrationDTO.setMaterial(material);
                            break;
                        }
                    }
                }
            }
        }
        log("Done validateMatchMaterialsForReceivedOrders in - " + (System.currentTimeMillis() - time) / MILLI_SEC + " sec");
    }

    public static void validateMatchMaterialsForReceivedOrders(List<OrderMigrationDTO> validOrders, MaterialHeaderRepository materialHeaderRepository) {
        long time = System.currentTimeMillis();
        long matchedOrders = 0;
        PaginatedArrayList<OrderMigrationDTO> orderNoPaginatedList = new PaginatedArrayList<OrderMigrationDTO>(validOrders);
        for (List<OrderMigrationDTO> subListOfOrders = null; (subListOfOrders = orderNoPaginatedList.nextPage()) != null;) {
            List<Material> materials = materialHeaderRepository.findMaterialsForOrderNos(receivedOrderNos(subListOfOrders));
            if (materials != null && !materials.isEmpty()) {
                for (OrderMigrationDTO orderMigrationDTO : subListOfOrders) {
                    if (OrderLineStatus.COMPLETED == orderMigrationDTO.getOrderLineStatus()
                            || OrderLineStatus.RECEIVED_PARTLY == orderMigrationDTO.getOrderLineStatus()) {
                        for (Material material : materials) {
                            if (orderMigrationDTO.getOrderNumber().equals(material.getOrderNo())
                                    && orderMigrationDTO.getPartNumber().equals(material.getPartNumber())
                                    && orderMigrationDTO.getPartVersion().equals(material.getPartVersion())
                                    && orderMigrationDTO.getPartQualifier().equals(material.getPartAffiliation())) {
                                orderMigrationDTO.setMaterial(material);
                                break;
                            }
                        }
                    }
                }
            }
            matchedOrders += subListOfOrders.size();
            log("Matching of materials processed for Orders - " + matchedOrders);
        }
        log("Done validateMatchMaterialsForReceivedOrders in - " + (System.currentTimeMillis() - time) / MILLI_SEC + " sec");
    }

    private static Set<String> receivedOrderNos(List<OrderMigrationDTO> validOrders) {
        Set<String> orderNos = new HashSet<String>();
        for (OrderMigrationDTO dto : validOrders) {
            OrderLineStatus orderLineStatus = dto.getOrderLineStatus();
            if (OrderLineStatus.COMPLETED == orderLineStatus || OrderLineStatus.RECEIVED_PARTLY == orderLineStatus) {
                orderNos.add(dto.getOrderNumber());
            }
        }
        return orderNos;
    }

    private static void validateOrdersIfNotInPlacedStatus(OrderMigrationDTO orderMigrationDTO) {
        OrderLineStatus orderLineStatus = orderMigrationDTO.getOrderLineStatus();
        if (OrderLineStatus.COMPLETED == orderLineStatus || OrderLineStatus.RECEIVED_PARTLY == orderLineStatus) {
            if (OrderLineStatus.COMPLETED == orderMigrationDTO.getOrderLineStatus()
                    && orderMigrationDTO.getReceivedQuantity() < orderMigrationDTO.getTotalTestobjectsQty()) {
                markAsInvalid(orderMigrationDTO, "The Received Order has Received Quantity less than to the Total of test objects Qty!");
            }
        }
    }

    public static void markAsInvalid(OrderMigrationDTO orderMigrationDTO, String reason) {
        orderMigrationDTO.setValid(false);
        orderMigrationDTO.setReason(reason);
    }

    public static void markAsInvalidIT(OrderMigrationDTO orderMigrationDTO, String reason) {
        orderMigrationDTO.setValid(false);
        orderMigrationDTO.setReasonIT(reason);
    }

    public static String trim(String value) {
        if (!StringUtils.isEmpty(value)) {
            return value.trim();
        }
        return value;
    }

    private static void log(String text) {
        LOGGER.info(text);
    }
}
