/*
 * Copyright 2009 Volvo Information Technology AB 
 * 
 * Licensed under the Volvo IT Corporate Source License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *      http://java.volvo.com/licenses/CORPORATE-SOURCE-LICENSE 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.volvo.gloria.procurematerial.b.beans;

import static com.volvo.gloria.util.c.GloriaParams.DATA_OPTIMISTIC_LOCK_MESSAGE;
import static com.volvo.gloria.util.c.GloriaParams.GPS_PROTOTYPE_PO_ACTION_ADD;
import static com.volvo.gloria.util.c.GloriaParams.GPS_PROTOTYPE_PO_ACTION_DELETE;
import static com.volvo.gloria.util.c.GloriaParams.GROUP_TYPE_DEFAULT;
import static com.volvo.gloria.util.c.GloriaParams.GROUP_TYPE_DIFF_TOBJ_SAME_PART;
import static com.volvo.gloria.util.c.GloriaParams.GROUP_TYPE_SAME_TOBJ_DIFF_PART;
import static com.volvo.gloria.util.c.GloriaParams.UNITPRICE_PCE;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.authorization.c.TeamType;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.BuildSiteType;
import com.volvo.gloria.common.c.CurrencyUtil;
import com.volvo.gloria.common.c.Domain;
import com.volvo.gloria.common.c.dto.SiteDTO;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.CarryOver;
import com.volvo.gloria.common.d.entities.Currency;
import com.volvo.gloria.common.d.entities.PartAliasMapping;
import com.volvo.gloria.common.d.entities.QualityDocument;
import com.volvo.gloria.common.d.entities.Site;
import com.volvo.gloria.common.d.entities.SupplierCounterPart;
import com.volvo.gloria.common.purchaseorder.c.dto.StandAloneOrderHeaderDTO;
import com.volvo.gloria.common.purchaseorder.c.dto.StandAloneOrderLineDTO;
import com.volvo.gloria.common.purchaseorder.c.dto.SyncPurchaseOrderTypeDTO;
import com.volvo.gloria.common.repositories.b.CarryOverRepository;
import com.volvo.gloria.common.repositories.b.CompanyCodeRepository;
import com.volvo.gloria.common.repositories.b.PartAliasMappingRepository;
import com.volvo.gloria.common.repositories.b.QualityDocumentRepository;
import com.volvo.gloria.common.repositories.b.SiteRepository;
import com.volvo.gloria.common.repositories.b.TeamRepository;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.common.util.CommonHelper;
import com.volvo.gloria.financeProxy.b.ProcessPurchaseOrderSender;
import com.volvo.gloria.financeProxy.c.ProcessPurchaseOrderDTO;
import com.volvo.gloria.materialRequestProxy.b.MaterialProcureResponse;
import com.volvo.gloria.procurematerial.b.DeliveryServices;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.ProcurementDtoTransformer;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.CarryOverExist;
import com.volvo.gloria.procurematerial.c.ChangeType;
import com.volvo.gloria.procurematerial.c.PriceType;
import com.volvo.gloria.procurematerial.c.ProcureResponsibility;
import com.volvo.gloria.procurematerial.c.dto.MaterialDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialGroupDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialHeaderDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialHeaderGroupingDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.c.dto.PickListDTO;
import com.volvo.gloria.procurematerial.c.dto.ProcureLineDTO;
import com.volvo.gloria.procurematerial.c.dto.RequestGroupDTO;
import com.volvo.gloria.procurematerial.c.dto.RequestListDTO;
import com.volvo.gloria.procurematerial.c.dto.SupplierDTO;
import com.volvo.gloria.procurematerial.c.export.ExportBuildSite;
import com.volvo.gloria.procurematerial.c.export.ExportToProcure;
import com.volvo.gloria.procurematerial.d.entities.Buyer;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.DeliverySchedule;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.MaterialPartAlias;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLastModified;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.OrderSap;
import com.volvo.gloria.procurematerial.d.entities.PartAlias;
import com.volvo.gloria.procurematerial.d.entities.PartNumber;
import com.volvo.gloria.procurematerial.d.entities.PickList;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.PurchaseOrganisation;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.entities.Requisition;
import com.volvo.gloria.procurematerial.d.entities.Supplier;
import com.volvo.gloria.procurematerial.d.entities.status.changeid.ChangeIdStatus;
import com.volvo.gloria.procurematerial.d.entities.status.changeid.ChangeIdStatusHelper;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusHelper;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatusHelper;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineHelper;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatus;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.d.type.internalexternal.InternalExternal;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.modification.ModificationType;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureTypeHelper;
import com.volvo.gloria.procurematerial.d.type.request.RequestType;
import com.volvo.gloria.procurematerial.d.type.supplier.SupplierType;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderSapRepository;
import com.volvo.gloria.procurematerial.repositories.b.PartNumberRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.PurchaseOrganisationRepository;
import com.volvo.gloria.procurematerial.util.PreparedMaterialGroup;
import com.volvo.gloria.procurematerial.util.ProcureGroupHelper;
import com.volvo.gloria.procurematerial.util.ProcurementHelper;
import com.volvo.gloria.procurerequest.c.dto.ChangeIdTransformerDTO;
import com.volvo.gloria.procurerequest.c.dto.FinanceHeaderTransformerDTO;
import com.volvo.gloria.procurerequest.c.dto.PartAliasTransformerDTO;
import com.volvo.gloria.procurerequest.c.dto.RequestGatewayDTO;
import com.volvo.gloria.procurerequest.c.dto.RequestHeaderTransformerDTO;
import com.volvo.gloria.procurerequest.c.dto.RequestHeaderVersionTransformerDTO;
import com.volvo.gloria.procurerequest.c.dto.RequestLineTransformerDTO;
import com.volvo.gloria.purchaseProxy.b.RequisitionSender;
import com.volvo.gloria.util.ActiveDirectory;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaExceptionLogger;
import com.volvo.gloria.util.GloriaFormateUtil;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.Utils;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.gloria.util.c.SAPParam;
import com.volvo.gloria.util.c.dto.LDAPUserDTO;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.tracing.c.GloriaTraceabilityConstants;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * ProcurementServices implementation bean.
 */

@ContainerManaged(name = "ProcurementServices")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ProcurementServicesBean implements ProcurementServices {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcurementServicesBean.class);

    private static final String PLANT = "PLANT";
    private static final String GPS = "GPS";
    private static final String V = "V";
    private static final int DEPARTMENT_NAME_LENGTH = 6;
    private static final char DEPARTMENT_PADDING_CHAR = '0';
    private static final String NDM = "NDM";
    private static final String RVI = "RVI";
    private static final String MAC = "MAC";
    private static final String QUO = "QUO";
    private static final int RECEIVER_LENGTH = 60;
    public static final int INDEX_NUMBER = 10;
    private static final String REQUESTHEADERS_NOMATCHING_COMPANYCODES_MESSAGE = "The selected headers have different company codes.";
    @Inject
    private MaterialServices materialServices;

    @Inject
    private UserServices userServices;

    @Inject
    private RequisitionSender requisitionSender;

    @Inject
    private DeliveryServices deliveryServices;

    @Inject
    private CarryOverRepository carryOverRepo;

    @Inject
    private PurchaseOrganisationRepository purchaseOrganisationRepo;

    @Inject
    private ProcureLineRepository procureLineRepository;

    @Inject
    private CommonServices commonServices;

    @Inject
    private OrderRepository orderRepository;

    @Inject
    private MaterialHeaderRepository requestHeaderRepository;

    @Inject
    private MaterialProcureResponse materialProcureResponse;

    @Inject
    private PartNumberRepository partNumberRepo;

    @Inject
    private PartAliasMappingRepository partAliasMappingRepo;

    @Inject
    private QualityDocumentRepository qualityDocumentRepository;

    @Inject
    private TraceabilityRepository traceabilityRepository;

    @Inject
    private WarehouseServices warehouseServices;

    @Inject
    private ProcurementDtoTransformer procurementDtoTransformer;

    @Inject
    private CarryOverRepository carryOverRepository;

    @Inject
    private SiteRepository siteRepository;
    
    @Inject
    private TeamRepository teamRepository;
    
    @Inject
    private CompanyCodeRepository companyCodeRepository;
    
    @Inject
    private OrderSapRepository orderSapRepository;
    
    @Inject
    private ProcessPurchaseOrderSender processPurchaseOrderSender;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createOrUpdatePurchaseOrder(SyncPurchaseOrderTypeDTO syncPurchaseOrderTypeDTO) throws GloriaApplicationException {
        List<StandAloneOrderHeaderDTO> standAloneOrderHeaderDTOs = syncPurchaseOrderTypeDTO.getStandAloneOrderHeaderDTO();
        StandAloneOrderHeaderDTO standAloneOrderHeaderDTO = standAloneOrderHeaderDTOs.get(0);
        String requisitionId = standAloneOrderHeaderDTO.getStandAloneOrderLineDTO().get(0).getRequisitionIds();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(GPS);
        userDTO.setFirstName(GPS);
        if (syncPurchaseOrderTypeDTO.getAction() != null && (requisitionId != null && isValidRequisition(requisitionId))) {
            Order order = null;
            if (syncPurchaseOrderTypeDTO.getAction().equals(GPS_PROTOTYPE_PO_ACTION_ADD)) {
                order = new Order();
                orderRepository.save(order);
                ProcurementServicesHelper.createOrder(order, syncPurchaseOrderTypeDTO, standAloneOrderHeaderDTOs, commonServices, userServices,
                                                      deliveryServices, procureLineRepository, materialServices, warehouseServices, requestHeaderRepository,
                                                      traceabilityRepository, userDTO);
                updateProcurementWithOrderInfo(order);
                materialServices.createAndSendProcessPurchaseOrder(order, SAPParam.ACTION_CREATE);
            } else {
                List<OrderLine> requisitionOrderLines = deliveryServices.findOrderLineByRequisitionId(requisitionId);
                if (requisitionOrderLines != null && !requisitionOrderLines.isEmpty()) {
                    order = requisitionOrderLines.get(0).getOrder();
                    updateOrder(standAloneOrderHeaderDTOs, order.getOrderLines(), syncPurchaseOrderTypeDTO.getAction() != null
                            && syncPurchaseOrderTypeDTO.getAction().equals(GPS_PROTOTYPE_PO_ACTION_DELETE), userDTO);
                    materialServices.createAndSendProcessPurchaseOrder(order, SAPParam.ACTION_CHANGE);
                }
            }
            if (order != null) {
                ProcureTypeHelper.updateBuyerCodeInProcureLine(order, purchaseOrganisationRepo, procureLineRepository);
                updateAndAlertPartVersionChanges(order);
            }
        } else {
            LOGGER.error("Couldn't accept message. Invalid Requisition ID found for the incoming order " + standAloneOrderHeaderDTO.getOrderNo() + "from GPS.");
        }
    }

    private void updateAndAlertPartVersionChanges(Order order) throws GloriaApplicationException {
        for (OrderLine orderLine : order.getOrderLines()) {
            if (orderLine.getMaterials() != null && !orderLine.getMaterials().isEmpty()) {
                Material aMaterial = orderLine.getMaterials().get(0);
                String currentPartVersion = null;
                if (aMaterial != null) {
                    currentPartVersion = aMaterial.getPartVersion();

                }
                String newPartVersion = orderLine.getCurrent().getPartVersion();
                if (currentPartVersion != null && !currentPartVersion.equalsIgnoreCase(newPartVersion)) {
                    OrderLineLastModified orderLineLastModified = orderLine.getOrderLineLastModified();
                    orderLineLastModified.setAlertPartVersion(true);
                    for (Material material : orderLine.getMaterials()) {
                        UserDTO userDto = new UserDTO();
                        userDto.setId(ProcurementServicesHelper.GPS);
                        userDto.setFirstName(ProcurementServicesHelper.GPS);
                        MaterialLineStatusHelper.updateAndAlertPartversionChange(material, currentPartVersion, newPartVersion, userDto, materialServices,
                                                                                 traceabilityRepository);
                    }
                }
            }
        }
    }

    private boolean isValidRequisition(String requisitionId) {
        Requisition requisition = procureLineRepository.findRequisitionByRequisitionId(requisitionId);
        if (requisition != null && !requisition.isCancelled()) {
            return true;
        }
        return false;
    }

    private void placeMaterialLines(OrderLine orderLine) throws GloriaApplicationException {
        List<Material> materials = orderLine.getMaterials();
        if (materials != null) {
            for (Material material : materials) {
                for (MaterialLine materialLine : material.getMaterialLine()) {
                    if (materialLine.getStatus().isPlaceable()) {
                        materialLine.getStatus().place(materialLine, traceabilityRepository, ProcurementServicesHelper.GPS, ProcurementServicesHelper.GPS);
                    }
                }
            }
        }
    }

    private void updateProcurementWithOrderInfo(Order order) throws GloriaApplicationException {
        List<OrderLine> orderLines = order.getOrderLines();
        if (orderLines != null && !orderLines.isEmpty()) {
            // update order with CompanyCode
            order.setCompanyCode(orderLines.get(0).getMaterials().get(0).getFinanceHeader().getCompanyCode());

            for (OrderLine orderLine : orderLines) {
                String requisitionId = orderLine.getRequisitionId();
                ProcureLine procureLine = procureLineRepository.findProcureLineByRequisitionId(requisitionId);
                if (procureLine != null) {
                    procureLine.setOrderNo(order.getOrderNo());
                    if (!orderLine.getPartName().equals(procureLine.getpPartName())) {
                        procureLine.setpPartName(orderLine.getPartName());
                        procureLine.setpPartNameUpdated(true);
                    }
                    procureLine.setStatus(ProcureLineStatus.PLACED);
                    orderLine.setProcureLine(procureLine);
                    procureLineRepository.save(procureLine);
                }
                placeMaterialLines(orderLine);
            }
        }
    }

    private void updateOrder(List<StandAloneOrderHeaderDTO> standAloneOrderHeaderDTOs, List<OrderLine> fetchedOrderLines, boolean isOrderCancelled,
            UserDTO userDTO) throws GloriaApplicationException {
        for (StandAloneOrderHeaderDTO standAloneOrderHeaderDTO : standAloneOrderHeaderDTOs) {
            List<StandAloneOrderLineDTO> standAloneOrderLineDTOs = standAloneOrderHeaderDTO.getStandAloneOrderLineDTO();
            for (OrderLine orderLine : fetchedOrderLines) {
                if (!orderLine.getStatus().isCancelled(orderLine)) {
                    updateOrderLine(standAloneOrderHeaderDTO, orderLine, isOrderCancelled, identifyMatchingOrderLine(standAloneOrderLineDTOs, orderLine),
                                    userDTO);
                }
            }
        }
    }

    /**
     * identify if the incoming CHANGE from GPS has matching partNumber & partQualifier for orderline.
     * 
     * if match found update the orderline. If NO MATCH FOUND its a DELETE/CANCEL of orderline from GPS.
     * 
     * @param standAloneOrderLineDTOs
     * @param orderLine
     * @return
     */
    private StandAloneOrderLineDTO identifyMatchingOrderLine(List<StandAloneOrderLineDTO> standAloneOrderLineDTOs, OrderLine orderLine) {
        for (StandAloneOrderLineDTO standAloneOrderLineDTO : standAloneOrderLineDTOs) {
            if (orderLine.getPartNumber().equals(standAloneOrderLineDTO.getPartNumber())
                    && orderLine.getPartAffiliation().equals(standAloneOrderLineDTO.getPartQualifier())) {
                return standAloneOrderLineDTO;
            }
        }
        return null;
    }

    private void updateOrderLine(StandAloneOrderHeaderDTO standAloneOrderHeaderDTO, OrderLine orderLine, boolean isOrderCancelled,
            StandAloneOrderLineDTO standAloneOrderLineDTO, UserDTO userDTO) throws GloriaApplicationException {
        // if we have a DELETE of orderline from GPS.
        if (standAloneOrderLineDTO == null || isOrderCancelled) {
            orderLine.getStatus().cancel(orderLine, this, requestHeaderRepository, traceabilityRepository, userDTO);
        } else if (isOrderLineNotInCancelledStatus(orderLine)) {
            OrderLineVersion currentOrderLineVersion = orderLine.getCurrent();
            OrderLineLastModified orderLineLastModified = orderLine.getOrderLineLastModified();

            OrderLineVersion newOrderLineVersion = ProcurementServicesHelper.createOrderLineVersion(deliveryServices, standAloneOrderHeaderDTO,
                                                                                                    standAloneOrderLineDTO);
            // handle unitprice in EURO
            newOrderLineVersion.setUnitPriceInEuro(CurrencyUtil.convertUnitPriceToEUR(newOrderLineVersion.getUnitPrice(), newOrderLineVersion.getCurrency(),
                                                                                      commonServices));
            newOrderLineVersion.setVersionNo(orderLine.getOrderLineVersions().size() + 1);
            newOrderLineVersion.setOrderLine(orderLine);

            if (isQtyUpdatedFromGPS(orderLine, currentOrderLineVersion, newOrderLineVersion)) {
                ProcurementServicesHelper.handleOrderQuantityAmmendmentFromGPS(orderLine, currentOrderLineVersion.getQuantity(),
                                                                               newOrderLineVersion.getQuantity(), materialServices, warehouseServices,
                                                                               requestHeaderRepository, traceabilityRepository, userDTO);
            }

            if (!DateUtil.isSameDate(currentOrderLineVersion.getOrderStaDate(), newOrderLineVersion.getOrderStaDate())) {
                orderLineLastModified.setAlertOrderStaDate(true);
                OrderLineStatusHelper.createTracebilityForOrderLine(traceabilityRepository, orderLine, "STA Order Updated",
                                                                    DateUtil.getDateWithoutTimeAsString(newOrderLineVersion.getOrderStaDate()), GPS, GPS);
            }

            Date newStaAcceptedDate = newOrderLineVersion.getStaAcceptedDate();
            if (newStaAcceptedDate == null) {
                newOrderLineVersion.setStaAcceptedDate(currentOrderLineVersion.getStaAcceptedDate());
            }

            Date newStaAgreedDate = newOrderLineVersion.getStaAgreedDate();

            if (newStaAgreedDate == null) {
                newOrderLineVersion.setStaAgreedDate(currentOrderLineVersion.getStaAgreedDate());
                newOrderLineVersion.setStaAgreedLastUpdated(DateUtil.getCurrentUTCDateTime());
            } else {
                newOrderLineVersion.setStaAgreedLastUpdated(DateUtil.getCurrentUTCDateTime());
                // The existing staAcceptedDate shall be overwritten first time
                if (!orderLine.isStaAgreedDateUpdated()) {
                    orderLine.setStaAgreedDateUpdated(true);
                    newOrderLineVersion.setStaAcceptedDate(newOrderLineVersion.getStaAgreedDate());
                }
            }
            newOrderLineVersion.setOrderStaDateOnTime(currentOrderLineVersion.isOrderStaDateOnTime());
            newOrderLineVersion.setStaAgreedDateOnTime(currentOrderLineVersion.isStaAgreedDateOnTime());
            orderLine.getOrderLineVersions().add(newOrderLineVersion);
            orderLine.setCurrent(newOrderLineVersion);
            placeMaterialLines(orderLine);
        }
        deliveryServices.saveOrderLine(orderLine);
    }

    private boolean isQtyUpdatedFromGPS(OrderLine orderLine, OrderLineVersion currentOrderLineVersion, OrderLineVersion newOrderLineVersion) {
        // PTR will not be updated for migrated orders : when the order was full received when migrated
        if (orderLine.getOrder().isMigrated() && isFullyReceivedMigratedOrder(orderLine, currentOrderLineVersion)) {
            return false;
        }
        return currentOrderLineVersion.getQuantity() != newOrderLineVersion.getQuantity();
    }

    private boolean isFullyReceivedMigratedOrder(OrderLine orderLine, OrderLineVersion currentOrderLineVersion) {
        // case when the received QTY is exceeding the order qty when migrated
        return orderLine.getReceivedQuantity() > currentOrderLineVersion.getQuantity();
    }

    private boolean isOrderLineNotInCancelledStatus(OrderLine orderLine) {
        return orderLine.getCompleteType() == null || !orderLine.getStatus().isCancelled(orderLine);
    }

    /**
     * 
     * @param pageObject
     * @param assignedMaterialController
     *            If TRUE, only return entities where assignedMaterialController is not null, if FALSE, only return entitites where assignedMaterialController
     *            is null. If null, return rows where assignMeterialController is either null or not null.
     * @param userId
     *            , logged in user
     * @return
     * @throws GloriaApplicationException
     * 
     */
    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','IT_SUPPORT')")
    public PageObject getRequestHeaders(PageObject pageObject, Boolean assignedMaterialController, String userId) throws GloriaApplicationException {
        return requestHeaderRepository.getRequestHeaders(pageObject, assignedMaterialController,
                                                         userServices.getUserCompanyCodeCodes(userId, null, TeamType.MATERIAL_CONTROL.name()));
    }

    @Override
    public MaterialHeader findRequestHeaderById(Long requestHeaderOID) throws GloriaApplicationException {
        return requestHeaderRepository.findById(requestHeaderOID);
    }

    @Override
    public List<MaterialHeader> findAllMaterialHeaders() {
        return requestHeaderRepository.findAll();
    }

    /**
     * @throws GloriaApplicationException
     * 
     */
    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','IT_SUPPORT')")
    public PageObject getProcurements(List<String> procureResponsibilitiyList, String procureForwardedId, String procureForwardedTeam,
            String procureLineStatus, String assignedMaterialController, String assignedMaterialControllerTeam, String teamType, String showFilter,
            PageObject pageObject) throws GloriaApplicationException {
        if (!StringUtils.isEmpty(procureForwardedId)) {
            return procureLineRepository.findProcureLinesByUserId(procureForwardedId, procureForwardedTeam, procureLineStatus, pageObject,
                                                                  userServices.getUserCompanyCodeCodes(procureForwardedId, procureForwardedTeam, teamType));
        } else if (!StringUtils.isEmpty(procureForwardedTeam)) {
            return procureLineRepository.findProcureLinesByInternalProcurerTeam(procureForwardedTeam, procureLineStatus, pageObject);
        }
        return procureLineRepository.getProcurements(procureResponsibilitiyList,
                                                     procureLineStatus,
                                                     assignedMaterialController,
                                                     assignedMaterialControllerTeam,
                                                     pageObject,
                                                     userServices.getUserCompanyCodeCodes(assignedMaterialController, assignedMaterialControllerTeam, teamType),
                                                     showFilter);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL')")
    public PageObject getProcureLinesByInternalProcurerTeam(String procureForwardedTeam, String procureLineStatus, PageObject pageObject)
            throws GloriaApplicationException {
        return procureLineRepository.findProcureLinesByInternalProcurerTeam(procureForwardedTeam, procureLineStatus, pageObject);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL')")
    public List<MaterialHeader> assignOrUnassignMateriaHeaders(List<MaterialHeaderDTO> materialHeaderDTOs, String action, String materialControllerId,
            String userTeamForAssignedUser, String userTeamTypeForAssignedUser, String loggedInUserID) throws GloriaApplicationException {
        if ("assign".equalsIgnoreCase(action)) {
            if (materialHeaderDTOs != null && !materialHeaderDTOs.isEmpty()) {
                UserDTO userDTO = userServices.getUser(materialControllerId);

                validateRequests(materialHeaderDTOs, userTeamForAssignedUser, userTeamTypeForAssignedUser);

                if (StringUtils.isEmpty(materialControllerId)) {
                    throw new GloriaApplicationException(GloriaExceptionConstants.CANNOT_UNASSIGN_DIFFERENT_USER, "User Id should not be null.");
                }

                List<MaterialHeader> headers = new ArrayList<MaterialHeader>();
                for (MaterialHeaderDTO headerDTO : materialHeaderDTOs) {
                    MaterialHeader materialHeader = requestHeaderRepository.findById(headerDTO.getId());
                    if (materialHeader != null) {
                        materialHeader.getMaterialHeaderVersions();
                        if (headerDTO.getVersion() != materialHeader.getVersion()) {
                            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET, DATA_OPTIMISTIC_LOCK_MESSAGE);
                        }
                        
                        checkProcurementState(materialHeader);
                        
                        String materialControllerName = userDTO.getUserName();
                        if (StringUtils.isEmpty(materialHeader.getMaterialControllerUserId())) {
                            materialHeader.setMaterialControllerUserId(materialControllerId);
                            materialHeader.setMaterialControllerName(materialControllerName);
                            materialHeader.setMaterialControllerTeam(userTeamForAssignedUser);
                            requestHeaderRepository.save(materialHeader);
                            headers.add(materialHeader);
                        } else {
                            materialHeader.setMaterialControllerUserId(materialControllerId);
                            materialHeader.setMaterialControllerName(materialControllerName);
                            materialHeader.setMaterialControllerTeam(userTeamForAssignedUser);
                            requestHeaderRepository.save(materialHeader);
                            Map<Long, Long> procureLineOids = requestHeaderRepository.findProcureLineOidsMap(materialHeader.getMaterialHeaderOid());
                            List<Long> list = new ArrayList<Long>();
                            for (Map.Entry<Long, Long> entry : procureLineOids.entrySet()) {
                                list.add(entry.getKey());
                            }
                            requestHeaderRepository.updateProcureLineControllerDetails(materialHeader.getMaterialControllerUserId(),
                                                                                       materialHeader.getMaterialControllerName(),
                                                                                       materialHeader.getMaterialControllerTeam(), list);
                           
                            MaterialLineStatusHelper.traceAssignUnAssignReturn(userDTO, userServices.getUser(loggedInUserID), userTeamForAssignedUser,
                                                                               "Assigned", materialHeader.getMaterials(), traceabilityRepository);
                        }
                    }
                }
                // Default Grouping for only newly assigned materials
                return assignMaterialController(materialControllerId, loggedInUserID, userTeamForAssignedUser,
                                                headers);
            }
        } else if ("unassign".equalsIgnoreCase(action)) {
            return unassignMaterialController(materialHeaderDTOs, loggedInUserID, userTeamForAssignedUser);
        }
        return new ArrayList<MaterialHeader>();
    }

    private void checkProcurementState(MaterialHeader materialHeader) throws GloriaApplicationException {
        for (Material material : materialHeader.getMaterials()) {
            ProcureLine procureLine = material.getProcureLine();
            if (procureLine != null && procureLine.getStatus().isProcurementStarted()) {
                throw new GloriaApplicationException(GloriaExceptionConstants.PROCUREMENT_STARTED, "Procurement already started.");
            }
        }
    }

    @Override
    public List<MaterialHeader> assignMaterialController(String materialControllerId, String loggedInUserID,
            String userTeamForAssignedUser, List<MaterialHeader> headers) throws GloriaApplicationException {
        if (!headers.isEmpty()) {
            List<Material> materials = requestHeaderRepository.getMaterialsForHeaderIds(headers);
            createProcureLine(materialControllerId, loggedInUserID, userTeamForAssignedUser, materials);
        }
        return headers;
    }

    @Override
    public void createProcureLine(String materialControllerId, String loggedInUserID, String userTeamForAssignedUser,
            List<Material> materials) throws GloriaApplicationException {
        List<Material> materialsToGroup = new ArrayList<Material>();
        for (Material material : materials) {
            // need to set temporary pl as responsibility is needed as per new logic
            ProcureLine procureLine = new ProcureLine();
            List<Material> materials1 = new ArrayList<Material>();
            materials1.add(material);
            procureLine.setMaterials(materials1);
            procureLine.setResponsibility(defineProcureResponsibility(procureLine));
            material.setTemporaryProcureLine(procureLine);
        }
        materialsToGroup.addAll(groupIfMaterialsExist(materialControllerId, materials, null, null));
        groupMaterials(materialsToGroup);
        UserDTO assignToUserDTO = userServices.getUser(materialControllerId);
        UserDTO loggedInUserDTO = userServices.getUser(loggedInUserID);
        MaterialLineStatusHelper.traceAssignUnAssignReturn(assignToUserDTO, loggedInUserDTO, userTeamForAssignedUser,
                                                           "Assigned", materials, traceabilityRepository);
        
    }

    

    /*
     * This method tries to group materials if there is already a procure line existing in WAIT_to_PROCURE state It returns a list of materials that could not
     * be grouped.
     */
    @Override
    public List<Material> groupIfMaterialsExist(String materialControllerUserId, Material material) throws GloriaApplicationException {
        return this.groupIfMaterialsExist(materialControllerUserId, material, null, null);
    }

    /*
     * This method tries to group materials if there is already a procure line existing in WAIT_to_PROCURE state It returns a list of materials that could not
     * be grouped.
     * 
     * During an Assign only a Procure Line with a null value for ForwardeduserId is used Duuring an Forward a grouping is done based on the ForwardedUserId
     * this the parameter forwardedUserId
     */
    @Override
    public List<Material> groupIfMaterialsExist(String materialControllerUserId, Material material, String forwardedUserId, String forwardedTeam)
            throws GloriaApplicationException {

        UserDTO userDto = userServices.getUser(materialControllerUserId);
        List<Material> materialsToGroup = new ArrayList<Material>();
        if (material.getStatus().isAvailableForGrouping(material)) {
            Map<String, Object> map = new HashMap<String, Object>();
            try {
                map.put(ProcureLine.GROUPINGKEYMD5, material.getGroupingKeyMd5());
            } catch (NoSuchAlgorithmException e) {
                GloriaExceptionLogger.log(e, ProcurementServicesBean.class);
                throw new GloriaApplicationException(GloriaExceptionConstants.DEFAULT_ERROR_CODE, "NoSuchAlgorithm Exception.");
            }
            if (forwardedUserId != null) {
                map.put(ProcureLine.FORWARDEDUSERID, forwardedUserId);
            }
            if (forwardedTeam != null) {
                map.put(ProcureLine.FORWARDEDTEAM, forwardedTeam);
            }
            map.put(ProcureLine.STATUS, ProcureLineStatus.WAIT_TO_PROCURE);
            map.put(ProcureLine.MATERIALCONTROLLERID, materialControllerUserId);
            List<ProcureLine> procureLines = procureLineRepository.findProcureLineByParameters(map);

            ProcureLine procureLine = null;
            if (procureLines != null && !procureLines.isEmpty()) {
                procureLine = procureLines.get(0);
            }
            ProcureLine materialProcureLine = material.getProcureLine();
            if (procureLine != null && (materialProcureLine == null || materialProcureLine.getProcureLineOid() != procureLine.getProcureLineOid())) {
                material.setAddedAfter(true);
                ProcureLineHelper.associateMaterialWithProcureLine(material, procureLine);
                for (MaterialLine materialLine : material.getMaterialLine()) {
                    MaterialLineStatusHelper.updateMaterialLineStatus(materialLine, MaterialLineStatus.WAIT_TO_PROCURE, "Wait to procure",
                                                                      GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE, userDto, traceabilityRepository, false);
                    requestHeaderRepository.updateMaterialLine(materialLine);
                }
                procureLineRepository.save(procureLine);
            } else {
                materialsToGroup.add(material);
            }
        }
        return materialsToGroup;
    }

    @Override
    public List<Material> groupIfMaterialsExist(String materialControllerUserId, List<Material> materials, String forwardedUserId, String forwardedTeam)
            throws GloriaApplicationException {
        // this is the list of md5 keys and materials
        HashMap<String, List<Material>> mapOfMaterials = new HashMap<String, List<Material>>();
        for (Material m : materials) {
            if (m.getStatus().isAvailableForGrouping(m)) {
                String key = "";
                try {
                    key = m.getGroupingKeyMd5();
                } catch (NoSuchAlgorithmException e) {
                    GloriaExceptionLogger.log(e, ProcurementServicesBean.class);
                    throw new GloriaApplicationException(GloriaExceptionConstants.DEFAULT_ERROR_CODE, "NoSuchAlgorithm Exception.");
                }
                List<Material> materialsToAdd = mapOfMaterials.get(key);
                if (materialsToAdd == null) {
                    materialsToAdd = new ArrayList<Material>();
                }
                materialsToAdd.add(m);
                mapOfMaterials.put(key, materialsToAdd);
            }
        }

        // now I need to get the procure lines matching the above criteria
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ProcureLine.GROUPINGKEYMD5S, new ArrayList<String>(mapOfMaterials.keySet()));
        if (forwardedUserId != null) {
            map.put(ProcureLine.FORWARDEDUSERID, forwardedUserId);
        }
        if (forwardedTeam != null) {
            map.put(ProcureLine.FORWARDEDTEAM, forwardedTeam);
        }
        map.put(ProcureLine.STATUS, ProcureLineStatus.WAIT_TO_PROCURE);
        map.put(ProcureLine.MATERIALCONTROLLERID, materialControllerUserId);
        List<ProcureLine> procureLines = procureLineRepository.findProcureLineByParameters(map);

        List<Long> procureLineOids = new ArrayList<Long>();
        for (ProcureLine procureLine : procureLines) {
            procureLineOids.add(procureLine.getProcureLineOid());
        }
        List<Material> listOfProcureLineMaterials = requestHeaderRepository.findMaterialsByProcureLineIds(procureLineOids);
        Map<Long, List<Material>> mapOfExistingProcureLineMaterials = new HashMap<Long, List<Material>>();
        for (Material m : listOfProcureLineMaterials) {
            List<Material> materialsToAdd = mapOfMaterials.get(String.valueOf(m.getProcureLine().getProcureLineOid()));
            if (materialsToAdd == null) {
                materialsToAdd = mapOfExistingProcureLineMaterials.get(m.getProcureLine().getProcureLineOid());
            }
            if (materialsToAdd == null) {
                materialsToAdd = new ArrayList<Material>();
            }
            materialsToAdd.add(m);
            mapOfExistingProcureLineMaterials.put(m.getProcureLine().getProcureLineOid(), materialsToAdd);
        }
        if (procureLines != null && !procureLines.isEmpty()) {

            List<Long> allMaterialsAdded = new ArrayList<Long>();

            for (ProcureLine procureLine : procureLines) {
                List<Material> materialsAlreadyExisting = mapOfExistingProcureLineMaterials.get(procureLine.getProcureLineOid());
                List<Material> materialToBeAdded = mapOfMaterials.get(procureLine.getGroupingKeyMd5());
                allMaterialsAdded.addAll(ProcureLineHelper.associateMaterialWithProcureLinePerformance(materialsAlreadyExisting, materialToBeAdded,
                                                                                                       procureLine, requestHeaderRepository));
                procureLineRepository.save(procureLine);
                mapOfMaterials.remove(procureLine.getGroupingKeyMd5());
            }
            requestHeaderRepository.updateMaterialLinesStatus(allMaterialsAdded, MaterialLineStatus.WAIT_TO_PROCURE);
        }

        List<Material> itemsRemaininginMap = new ArrayList<Material>();
        for (String key : mapOfMaterials.keySet()) {
            itemsRemaininginMap.addAll(mapOfMaterials.get(key));
        }
        return itemsRemaininginMap;
    }

    private void validateRequests(List<MaterialHeaderDTO> materialHeaderDTOs, String userTeamToAssign, String userTeamTypeToAssign)
            throws GloriaApplicationException {
        final String companyCode = materialHeaderDTOs.get(0).getCompanyCode();
        boolean hasSameCompanyCodes = GloriaFormateUtil.hasSameItems(materialHeaderDTOs, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return StringUtils.defaultString(((MaterialHeaderDTO) object).getCompanyCode()).equals(StringUtils.defaultString(companyCode));
            }
        });
        if (!hasSameCompanyCodes) {
            throw new GloriaApplicationException(GloriaExceptionConstants.REQUESTHEAEDRS_NOMATCHING_COMPANYCODES,
                                                 REQUESTHEADERS_NOMATCHING_COMPANYCODES_MESSAGE);
        }
    }

    @Override
    public void groupMaterials(List<Material> materials) throws GloriaApplicationException {
        group(materials, MaterialLineStatus.WAIT_TO_PROCURE);
    }

    @Override
    public void regroupMaterialsOnOrderCancel(ProcureLine existingProcureLine, List<Material> materials) throws GloriaApplicationException {
        if (materials != null && !materials.isEmpty()) {
            // re-evaluate the grouping key and hold the new procureline created with the IP, if it was assigned to IP before
            for (Material material : materials) {
                material.setTemporaryProcureLine(existingProcureLine);
            }

            List<Material> materialsToGroup = new ArrayList<Material>();
            materialsToGroup.addAll(groupIfMaterialsExist(existingProcureLine.getMaterialControllerId(), materials, null, null));
            groupMaterials(materialsToGroup);

            // set the IP user information
            ProcureLine revertedProcureLine = materials.get(0).getProcureLine();
            if (revertedProcureLine != null) {
                revertedProcureLine.setForwardedUserId(existingProcureLine.getForwardedUserId());
                revertedProcureLine.setForwardedUserName(existingProcureLine.getForwardedUserName());
                revertedProcureLine.setForwardedTeam(existingProcureLine.getForwardedTeam());

                // retain the procure type as Internal as the regrouping default sets this to External
                revertedProcureLine.setProcureType(existingProcureLine.getProcureType());
            }
        }
    }

    private void group(List<Material> materials, MaterialLineStatus status) throws GloriaApplicationException {
        if (materials != null && !materials.isEmpty()) {
            Material matrl = materials.get(0);
            MaterialHeader materialHeader = matrl.getMaterialHeader();
            UserDTO userDTO = userServices.getUser(materialHeader.getMaterialControllerUserId());
            List<MaterialGroupDTO> materialGroupDTOs = new ArrayList<MaterialGroupDTO>();
            MaterialGroupDTO materialGroupDTO = new MaterialGroupDTO();
            materialGroupDTO.setMaterials(materials);
            materialGroupDTOs.add(materialGroupDTO);
            ProcureGroupHelper groupHelper = new ProcureGroupHelper(materialGroupDTOs, GROUP_TYPE_DEFAULT);
            long start1 = System.currentTimeMillis();
            groupHelper.getProcureLines(commonServices, userDTO, requestHeaderRepository);
            LOGGER.info("creating ProcureLines took " + (System.currentTimeMillis() - start1));
            long start = System.currentTimeMillis();
            List<Long> listForBulkUpdate = new ArrayList<Long>();
            Map<String, PreparedMaterialGroup> preparedGroups = groupHelper.getPreparedGroups();
            for (Object groupingKey1 : preparedGroups.keySet()) {
                PreparedMaterialGroup group = preparedGroups.get(groupingKey1);
                try {
                    ProcureLine procureLine = group.getCurrentProcureLine();
                    List<Material> materialsFORPL = group.getItems();
                    procureLine.setResponsibility(defineProcureResponsibility(procureLine, materialsFORPL));
                    if (ProcureResponsibility.BUILDSITE.equals(procureLine.getResponsibility())) {
                        procureLine.setProcureType(ProcureType.INTERNAL);
                    }
                    setProcureLineProcureType(procureLine, materialsFORPL);
                    String groupingKey = "";
                    String groupingKeyCompress = "";
                    for (Material material : materialsFORPL) {
                        material.setProcureLine(procureLine);
                        listForBulkUpdate.add(material.getMaterialOID());
                        requestHeaderRepository.updateMaterial(material);
                        groupingKey = material.getGroupingKeyMd5();
                        groupingKeyCompress = material.getGroupIdentifierKey(GloriaParams.GROUP_TYPE_DEFAULT);
                    }
                    procureLine.setGroupingKeyMd5(groupingKey);
                    procureLine.setGroupingKeyCompress(groupingKeyCompress);
                    procureLine = procureLineRepository.save(procureLine);
                } catch (NoSuchAlgorithmException e) {
                    GloriaExceptionLogger.log(e, ProcurementServicesBean.class);
                } catch (IOException e) {
                    GloriaExceptionLogger.log(e, ProcurementServicesBean.class);
                }
            }
            requestHeaderRepository.updateMaterialLinesStatus(listForBulkUpdate, MaterialLineStatus.WAIT_TO_PROCURE);
            LOGGER.info("looping through groups took " + (System.currentTimeMillis() - start));
        }
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE', 'PROCURE-INTERNAL','IT_SUPPORT')")
    public ProcureLineDTO findProcureLineById(Long id, String modificationType) throws GloriaApplicationException {
        ProcureLine procureLine = procureLineRepository.findProcureLineById(id);
        return ProcurementHelper.transformAsDTO(procureLine);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE')")
    public List<MaterialLine> getMaterialLinesInStock(String partNumber, String partVersion, String partAffiliation, String projectId, String partModification, String companyCode) {
        return requestHeaderRepository.getMaterialLinesInStock(partNumber, partVersion, partAffiliation, projectId, partModification, companyCode);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE', 'PROCURE-INTERNAL', 'IT_SUPPORT')")
    public PageObject getAccumulatedMaterialLinesInStock(PageObject pageObject, String partNumber, String partVersion, String partAffiliation,
            String projectId, String partModification, String companyCode) {
        return requestHeaderRepository.getAccumulatedMaterialLinesInStock(pageObject, partNumber, partVersion, partAffiliation, projectId, partModification, companyCode);
    }

    private ProcureResponsibility defineProcureResponsibility(ProcureLine procureLine) {
        List<Material> materials = procureLine.getMaterials();
        Material material = materials.get(0);
        if (material.isCarryOverExistAndMatched()) {
            return ProcureResponsibility.BUILDSITE;
        } else {
            return ProcureResponsibility.PROCURER;
        }
    }

    private ProcureResponsibility defineProcureResponsibility(ProcureLine procureLine, List<Material> materials) {
        Material material = materials.get(0);
        if (material.isCarryOverExistAndMatched()) {
            return ProcureResponsibility.BUILDSITE;
        } else {
            return ProcureResponsibility.PROCURER;
        }
    }

    @Override
    public void setProcureLineProcureType(ProcureLine procureLine, List<Material> materials) throws GloriaApplicationException {
        boolean existsCarryOver = existsCarryOver(materials);

        if (existsCarryOver) {
            procureLine.setProcureType(ProcureType.INTERNAL);
        } else {
            procureLine.setProcureType(ProcureType.EXTERNAL);
        }

        // set procureType considering the previous setting and material in stock
        suggestProcureType(procureLine, materials, existsCarryOver);
    }

    @Override
    public boolean existsCarryOver(List<Material> materials) {
        boolean existsCarryOver = false;
        if (materials != null && !materials.isEmpty()) {
            Material material = materials.get(0);
            existsCarryOver = material.isCarryOverExistAndMatched() || material.isCarryOverExist();
        }
        return existsCarryOver;
    }

    @Override
    public void suggestProcureType(ProcureLine procureLine, List<Material> materials, boolean existsCarryOver) throws GloriaApplicationException {
        MaterialHeader materialHeader = null;
        for (Material aMaterial : materials) {
            MaterialHeader aMaterialHeader = aMaterial.getMaterialHeader();
            if (aMaterialHeader != null) {
                materialHeader = aMaterialHeader;
                break;
            }
        }
        if (procureLine.getStatus().equals(ProcureLineStatus.WAIT_TO_PROCURE) && procureLine.getResponsibility() != ProcureResponsibility.BUILDSITE
                && materialHeader != null) {
            procureLine.setProcureType(materialHeader.getRequestType().suggestProcureType(procureLine, existsMaterialInStock(procureLine)));
        }
    }

    protected CarryOverExist checkCarryOverExistAndMatch(String partNumber, String partVersion, String partAffiliation, String outboundLocationType,
            String customerId, List<MaterialPartAlias> partAliasList, CarryOver existingCarryOver) {
        if (partAffiliation.equalsIgnoreCase("V") && partVersion != null && partVersion.startsWith("P") && customerId != null && outboundLocationType != null
                && outboundLocationType.equals(BuildSiteType.PLANT.name())) {
            return checkCarryOver(partNumber, partVersion, partAffiliation, customerId, partAliasList, existingCarryOver, CarryOverExist.EXIST_AND_MATCH);
        }
        return CarryOverExist.NO;
    }

    protected CarryOverExist checkCarryOverExist(String partNumber, String partVersion, String partAffiliation, String outboundLocationType,
            List<MaterialPartAlias> partAliasList, CarryOver existingCarryOver) {
        if (partAffiliation.equalsIgnoreCase("V") && partVersion != null && partVersion.startsWith("P")) {
            return checkCarryOver(partNumber, partVersion, partAffiliation, null, partAliasList, existingCarryOver, CarryOverExist.EXIST);
        }
        return CarryOverExist.NO;
    }

    private CarryOverExist checkCarryOver(String partNumber, String partVersion, String partAffiliation, String customerId,
            List<MaterialPartAlias> partAliasList, CarryOver existingCarryOver, CarryOverExist carryOverExistType) {

        List<CarryOver> carryOvers = null;

        // partNumber, partVersion, partAffiliation, customer
        carryOvers = carryOverRepo.findCarryOverByPartNumberPartversionAndCustomerId(partNumber, partVersion, partAffiliation, customerId, null);
        if (carryOvers != null && !carryOvers.isEmpty()) {
            existingCarryOver.setCarryOverOid(identifyValidCarryOver(carryOvers));
            return carryOverExistType;
        }

        // 1b: Does Alias exist for partNumber and customer
        for (MaterialPartAlias partAlias : partAliasList) {
            carryOvers = findCarryOverAlias(partAlias.getKolaDomain().toString(), partAlias.getPartNumber(), partAlias.getKolaDomain().toString(), customerId);
            if (carryOvers != null && !carryOvers.isEmpty()) {
                existingCarryOver.setCarryOverOid(identifyValidCarryOver(carryOvers));
                return carryOverExistType;
            }
        }

        return CarryOverExist.NO;
    }

    private List<CarryOver> findCarryOverAlias(String kolaDomain, String partNumber, String partAffiliation, String customerId) {
        String gpsQualifier = null;
        List<CarryOver> carryOvers = null;
        PartAliasMapping pm = partAliasMappingRepo.getGpsQualifier(kolaDomain);
        if (pm != null) {
            gpsQualifier = pm.getGpsQualifier();
            carryOvers = carryOverRepo.findCarryOverAlias(partNumber, gpsQualifier, customerId);
        }
        return carryOvers;
    }

    @SuppressWarnings("unchecked")
    private long identifyValidCarryOver(List<CarryOver> carryOvers) {
        List<Currency> currencies = commonServices.findAllCurreny();
        if (carryOvers != null && !carryOvers.isEmpty() && currencies != null && !currencies.isEmpty()) {

            // set the first item from the list as carry over
            CarryOver validCarryOver = carryOvers.iterator().next();

            // gather a list of all valid curreny codes in system
            Collection<String> currencyCodes = CollectionUtils.collect(currencies, TransformerUtils.invokerTransformer("getCode"));

            // filter out carryovers with valid curreny codes
            Collection<CarryOver> carryOversWithValidCurrency = CollectionUtils.select(carryOvers, buildCarryOverPredicate(currencyCodes));

            // if there are carry overs with valid currency pick one of the carryovers from filtered list
            if (carryOversWithValidCurrency != null && !carryOversWithValidCurrency.isEmpty()) {
                validCarryOver = carryOversWithValidCurrency.iterator().next();
            }

            return validCarryOver.getCarryOverOid();
        }
        return 0;
    }

    /**
     * predicate to filter out the carryovers with valid currency codes.
     * 
     * @param validCurrencies
     * @return
     */
    private org.apache.commons.collections.Predicate buildCarryOverPredicate(final Collection<String> validCurrencies) {
        return new org.apache.commons.collections.Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return validCurrencies.contains(((CarryOver) object).getCurrency());
            }
        };
    }

    private boolean existsMaterialInStock(ProcureLine procureLine) {
        List<Material> materials = procureLine.getMaterials();
        Material material = materials.get(0);
        List<MaterialLine> materialLinesInStock = requestHeaderRepository.getMaterialLinesInStock(material.getPartNumber(), material.getPartVersion(),
                                                                                                  material.getPartAffiliation(), material.getFinanceHeader()
                                                                                                                                         .getProjectId(),
                                                                                                  material.getPartModification(), material.getFinanceHeader()
                                                                                                                                          .getCompanyCode());

        return materialLinesInStock != null && !materialLinesInStock.isEmpty();
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE', 'PROCURE-INTERNAL')")
    public List<ProcureLine> updateProcureLines(List<ProcureLineDTO> procureLineDTOs, String action, String teamId, String userId, String finalWarehouseId,
            String forwardedUserId) throws GloriaApplicationException {
        List<ProcureLine> procureLines = new ArrayList<ProcureLine>();
        boolean multipleProcure = true;
        for (ProcureLineDTO procureLineDTO : procureLineDTOs) {
            ProcureLine procureLine = updateProcureLine(procureLineDTO, action, null, teamId, userId, finalWarehouseId, multipleProcure, forwardedUserId);
            if (procureLine != null) {
                procureLines.add(procureLine);
            }
        }
        return procureLines;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE', 'PROCURE-INTERNAL')")
    public ProcureLine updateProcureLine(ProcureLineDTO procureLineDTO, String action, String materialLineKeys, String team, String userId,
            String finalWarehouseId, boolean multipleProcure, String forwardedUserId) throws GloriaApplicationException {
        UserDTO userDto = userServices.getUser(userId);
        Long procureLineId = procureLineDTO.getId();
        boolean procured = true;
        ProcureLine procureLine = procureLineRepository.findProcureLineById(procureLineId);
        if (procureLine == null) {
            LOGGER.error("No procure line objects exists for id : " + procureLineId);
            throw new GloriaSystemException("This operation cannot be performed. No procure line objects exists for id : " + procureLineId,
                                            GloriaExceptionConstants.INVALID_DATASET_OID);
        }
        if (procureLineDTO.getVersion() != procureLine.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET, DATA_OPTIMISTIC_LOCK_MESSAGE);
        }
        // lazy loading
        procureLine.getMaterials();

        ProcureGroupHelper.updateProcureLineUsageQty(procureLine);
        ProcureGroupHelper.updateMaterialAddedAfter(procureLineDTO, procureLine);
        procureLine.getStatus().updateProcureLine(procureLineDTO, procureLine, action, team, userDto, procureLineRepository, commonServices, this,
                                                  purchaseOrganisationRepo, finalWarehouseId);

        updateSupplier(procureLineDTO, multipleProcure, procureLine);

        procureLine.getProcureType().validate(procureLineDTO, this);
        if (!StringUtils.isEmpty(action) && "procure".equalsIgnoreCase(action)) {
            procured = doProcure(procureLineDTO, materialLineKeys, userDto, procureLine, multipleProcure);
        } else if (!StringUtils.isEmpty(action) && "revert".equalsIgnoreCase(action)) {
            doRevertProcurement(procureLine, userDto);
        } else if (!StringUtils.isEmpty(action) && "forward".equalsIgnoreCase(action)) {
            UserDTO forwardedUserDto = userServices.getUser(forwardedUserId);
            doForwardProcurement(team, forwardedUserDto, userDto, procureLine);
        } else if (!StringUtils.isEmpty(action) && "assign".equalsIgnoreCase(action)) {
            UserDTO forwardedUserDto = userServices.getUser(forwardedUserId);
            doAssignInternalProcurement(userDto, team, forwardedUserDto, procureLine);
        } else if (!StringUtils.isEmpty(action) && "unassign".equalsIgnoreCase(action)) {
            doUnAssignInternalProcurement(procureLine);
        } else if (!StringUtils.isEmpty(action) && "return".equalsIgnoreCase(action)) {
            doReturnProcurement(procureLine, userDto);
        } else if (!StringUtils.isEmpty(action) && "ignoreUnsuccessful".equalsIgnoreCase(action)) {
            doUpdateProcuredSuccesful(procureLine);
        } else if (!StringUtils.isEmpty(action) && "toprocure".equalsIgnoreCase(action)) {
            doToprocure(procureLine, userDto);
        }
        if (procured) {
            return procureLine;
        }
        return null;
    }

    private void updateSupplier(ProcureLineDTO procureLineDTO, boolean multipleProcure, ProcureLine procureLine) {
        Supplier supplier = null;
        if (procureLineDTO.getSupplierId() != null) {
            if (multipleProcure) {
                supplier = getSupplierForMultipleUpdate(procureLineDTO, supplier);
            } else {
                supplier = procureLineRepository.findSupplierById(procureLineDTO.getSupplierId());
            }
        }
        if (supplier == null && procureLineDTO.getSupplierName() != null) {
            supplier = new Supplier();
            supplier.setSupplierName(procureLineDTO.getSupplierName());
            supplier.setSupplierType(SupplierType.INTERNAL_EDITED);
        }
        if (supplier != null) {
            procureLine.setSupplier(supplier);
            supplier.setProcureLine(procureLine);
        }
    }

    private Supplier getSupplierForMultipleUpdate(ProcureLineDTO procureLineDTO, Supplier supplier) {
        Site site = siteRepository.findById(procureLineDTO.getSupplierId());
        if (site != null) {
            supplier = procureLineRepository.findSupplierBySupplierId(site.getSiteId());
        }
        return supplier;
    }

    private void doToprocure(ProcureLine procureLine, UserDTO userDTO) throws GloriaApplicationException {
        String materialControllerUserId = userDTO.getId();
        List<Material> materials = procureLine.getMaterials();
        int size = materials.size();
        int numberOfGroupedMaterials = 0;
        for (int i = 0; i < size; i++) {
            Material material = materials.get(i);
            List<Material> materialsToBeGrouped = this.groupIfMaterialsExist(materialControllerUserId, material, null, null);
            numberOfGroupedMaterials = numberOfGroupedMaterials + materialsToBeGrouped.size();
        }
        // all materials were grouped then remove old procureLine
        if (numberOfGroupedMaterials == 0) {
            // remove a procureline if it has no materials attached to it
            procureLine.getStatus().remove(procureLine, procureLineRepository);
        } else {
            // when procureLine is send it back "to procure" then the key has changed and has to be updated
            Material material = procureLine.getMaterials().get(0);
            try {
                procureLine.setGroupingKeyCompress(material.getGroupIdentifierKey(GloriaParams.GROUP_TYPE_DEFAULT));
                procureLine.setGroupingKeyMd5(material.getGroupingKeyMd5());
            } catch (NoSuchAlgorithmException e) {
                GloriaExceptionLogger.log(e, ProcurementServicesBean.class);
            } catch (IOException e) {
                GloriaExceptionLogger.log(e, ProcurementServicesBean.class);
            }
        }
    }

    private void doUpdateProcuredSuccesful(ProcureLine procureLine) {
        procureLine.setProcureFailureDate(null);
        procureLineRepository.save(procureLine);
    }

    private void doReturnProcurement(ProcureLine procureLine, UserDTO userDTO) throws GloriaApplicationException {
        if (procureLine != null) {
            procureLine.getProcureType().returnProcureLine(procureLine, procureLineRepository, this);
            MaterialLineStatusHelper.traceAssignUnAssignReturn(null, userDTO, null, "Returned To MC", procureLine.getMaterials(),
                                                               traceabilityRepository);
        }
    }

    private void doUnAssignInternalProcurement(ProcureLine procureLine) throws GloriaApplicationException {
        if (procureLine != null) {
            procureLine.getProcureType().unAssignInternal(procureLine, procureLineRepository);
        }
    }

    private void doAssignInternalProcurement(UserDTO loggedInUserDTO, String team, UserDTO forwardedUserDto, ProcureLine procureLine)
            throws GloriaApplicationException {
        if (procureLine != null) {
            procureLine.getProcureType().assignInternal(procureLine, team, forwardedUserDto.getId(), forwardedUserDto.getUserName(), procureLineRepository);
            MaterialLineStatusHelper.traceAssignUnAssignReturn(forwardedUserDto, loggedInUserDTO, team, "Assigned IP", procureLine.getMaterials(),
                                                               traceabilityRepository);
        }
    }

    private void doForwardProcurement(String team, UserDTO forwardedUserDTO, UserDTO userDto, ProcureLine procureLine) throws GloriaApplicationException {
        if (procureLine != null) {
            procureLine.getProcureType().forward(procureLine, team, forwardedUserDTO.getId(), forwardedUserDTO.getUserName(), procureLineRepository, this,
                                                 userDto, traceabilityRepository);
        }
    }

    private boolean doProcure(ProcureLineDTO procureLineDTO, String materialLineKeys, UserDTO userDto, ProcureLine procureLine, boolean multipleProcure)
            throws GloriaApplicationException {

        List<Material> materials = procureLine.getMaterials();
        String projectId = materials.get(0).getFinanceHeader().getProjectId();
        boolean procured = true;
        long fromStockQty = 0L;
        long fromStockProjectQty = 0L;
        List<MaterialLine> fromStockMaterialLines = null;

        if (materialLineKeys != null) {
            fromStockMaterialLines = requestHeaderRepository.findMaterialLinesFromStock(procureLineDTO.getpPartNumber(), procureLineDTO.getpPartVersion(),
                                                                                        procureLineDTO.getpPartAffiliation(), procureLineDTO.getProjectId(),
                                                                                        materialLineKeys);

            if (fromStockMaterialLines != null && !fromStockMaterialLines.isEmpty()) {
                for (MaterialLine line : fromStockMaterialLines) {
                    fromStockQty += line.getQuantity();
                    Material aMaterial = line.getMaterial();
                    FinanceHeader financeHeader = aMaterial.getFinanceHeader();
                    if (financeHeader != null) {
                        String aProjectId = financeHeader.getProjectId();
                        if (!aMaterial.getMaterialType().equals(MaterialType.RELEASED)) {
                            if (aProjectId != null && projectId != null
                                    && (aMaterial.getMaterialType().equals(MaterialType.USAGE) || !projectId.equalsIgnoreCase(aProjectId))) {
                                fromStockProjectQty += line.getQuantity();
                            }
                        }
                    }
                }
            }
        }
        
        long totalProcureQuantity = procureLine.getUsageQuantity() + procureLineDTO.getAdditionalQuantity();
        procureLine.setFromStockQty(Math.min(fromStockQty - fromStockProjectQty, totalProcureQuantity));
        long remainingFromStockQuantity = totalProcureQuantity - (fromStockQty - fromStockProjectQty);
        if (remainingFromStockQuantity > 0) {
            procureLine.setFromStockProjectQty(Math.min(fromStockProjectQty, remainingFromStockQuantity));
        }
        procureLine.setProcureType(procureLine.getProcureType().setProcureType(procureLine.getUsageQuantity(), procureLineDTO.getAdditionalQuantity(),
                                                                               procureLine.getFromStockQty() + procureLine.getFromStockProjectQty()));

        procured = procureLine.getProcureType().procure(requestHeaderRepository, this, procureLine, fromStockMaterialLines, traceabilityRepository,
                                                        materialServices, warehouseServices, userDto, multipleProcure, procureLineDTO, materials);
        if (!procured) {
            procureLine.setProcureFailureDate(DateUtil.getCurrentUTCDateTime());
        }
        return procured;
    }

    @Override
    public void updateStatusAndProcureTeamInfo(ProcureLineDTO procureLineDTO, UserDTO userDto, ProcureLine procureLine, List<Material> materials)
            throws GloriaApplicationException {
        procureLine.setProcureTeam(userDto.getProcureTeam());
        procureLine.setProcureUserId(userDto.getId());
        procureLine.setProcureDate(DateUtil.getCurrentUTCDateTime());
        procureLine.getStatus().procured(procureLine);
        procureLine.setProcureFailureDate(DateUtil.getCurrentUTCDateTime());
        for (Material material : materials) {
            if (material.getMaterialType() != MaterialType.USAGE_REPLACED && material.getMaterialType() != MaterialType.ADDITIONAL) {
                for (MaterialLine materialLine : material.getMaterialLine()) {
                    materialLine.getStatus().procure(materialLine, traceabilityRepository, requestHeaderRepository, userDto);
                    materialLine.setProcureType(procureLine.getProcureType().getMaterialLineProcureType());
                }
                material.setOrderNo(procureLineDTO.getOrderNo());
            }
        }
    }

    @Override
    public void updateSupplierCounterPartInfo(ProcureLine procureLine) {
        List<Material> materials = procureLine.getMaterials();
        if (materials != null && !materials.isEmpty() && procureLine.getSupplierCounterPartOID() != null) {
            SupplierCounterPart supplierCounterPart = commonServices.getSupplierCounterPartById(procureLine.getSupplierCounterPartOID());
            for (Material material : materials) {
                material.getMaterialType().setSupplierCounterPart(material.getMaterialLine(), supplierCounterPart);
            }
        }
    }

    @Override
    public void createAdditionalMaterial(ProcureLine procureLine) throws GloriaApplicationException {
        if (procureLine.getAdditionalQuantity() > 0) {
            UserDTO userDTO = userServices.getUser(procureLine.getProcureUserId());
            ProcurementHelper.createAdditionalMaterial(getUsageOrModifiedMaterial(procureLine.getMaterials()), materialServices, traceabilityRepository,
                                                       userDTO);
            ProcurementHelper.setProcureLineChangeRequestIds(procureLine);
        }
    }

    private Material getUsageOrModifiedMaterial(List<Material> materials) {
        if (materials != null && !materials.isEmpty()) {
            sortMaterialsByMaterialType(materials);
            for (Material material : materials) {
                if (material.getMaterialType() == MaterialType.USAGE || material.getMaterialType() == MaterialType.MODIFIED
                        || material.getMaterialType() == MaterialType.ADDITIONAL_USAGE) {
                    return material;
                }
            }
        }
        return null;
    }

    private void sortMaterialsByMaterialType(List<Material> materialList) {
        Collections.sort(materialList, new Comparator<Material>() {
            public int compare(Material m1, Material m2) {
                if (m1.getMaterialType().ordinal() < (m2.getMaterialType().ordinal())) {
                    return -1;
                }
                return 1;
            }
        });
    }

    @Override
    public void createInternalOrder(ProcureLine procureLine, boolean traceMaterialLineLog) throws GloriaApplicationException {

        Order order = orderRepository.findOrderByOrderNo(procureLine.getOrderNo());

        SupplierCounterPart supplierCounterPart = commonServices.getSupplierCounterPartById(procureLine.getSupplierCounterPartOID());
        if (supplierCounterPart != null) {
            FinanceHeader financeHeader = procureLine.getFinanceHeader();
            UserDTO userDTO = null;
            if (procureLine.isForwardedForDC()) {
                userDTO = userServices.getUser(procureLine.getForwardedUserId());
            } else {
                userDTO = userServices.getUser(procureLine.getProcureUserId());
            }

            if (order == null) {
                order = new Order();
                order.setInternalExternal(InternalExternal.INTERNAL);
                Supplier supplier = procureLine.getSupplier();
                if (supplier != null) {
                    order.setSupplierId(supplier.getSupplierId());
                    order.setSupplierName(supplier.getSupplierName());
                }
                order.setOrderNo(procureLine.getOrderNo());
                order.setOrderDateTime(procureLine.getProcureDate());
                order.setCompanyCode(financeHeader.getCompanyCode());

                if (procureLine.isForwardedForDC()) {
                    if (userDTO != null) {
                        order.setDeliveryControllerTeam(userDTO.getDelFollowUpTeam());
                    }
                } else {

                    updateOrderWithSupplierCounterPartInfo(procureLine, supplierCounterPart, financeHeader, order);
                }

                order.setMaterialUserId(supplierCounterPart.getMaterialUserId());
                order.setSuffix(supplierCounterPart.getPpSuffix());
                order.setShipToId(supplierCounterPart.getShipToId());
            }

            List<OrderLine> orderLines = new ArrayList<OrderLine>();
            order.setOrderLines(orderLines);
            OrderLine orderLine = new OrderLine();
            orderLine.setOrder(order);
            orderLine.setPartAffiliation(procureLine.getpPartAffiliation());
            orderLine.setPartNumber(procureLine.getpPartNumber());
            orderLine.setPartName(procureLine.getpPartName());
            orderLine.setCurrency(procureLine.getCurrency());
            if (procureLine.isForwardedForDC()) {
                if (userDTO != null) {
                    orderLine.setDeliveryControllerUserId(userDTO.getId());
                    orderLine.setDeliveryControllerUserName(userDTO.getUserName());
                }
            }
            updateOrderLineWithCountryInfo(orderLine, procureLine);
            if (procureLine.getPartAlias() != null) {
                orderLine.setSupplierPartNo(procureLine.getPartAlias().getAliasPartNumber());
            }
            if (supplierCounterPart != null) {
                orderLine.setShipToPartyId(supplierCounterPart.getShipToId());
            }

            List<OrderLineVersion> listOfOrderLineVersions = new ArrayList<OrderLineVersion>();

            OrderLineVersion orderLineVersion = new OrderLineVersion();
            orderLineVersion.setPartVersion(procureLine.getpPartVersion());

            String unitOfMeasure = "";
            long procureLineQty = procureLine.getRequisition().getQuantity();
            List<Material> materials = procureLine.getMaterials();
            if (materials != null && !materials.isEmpty()) {
                unitOfMeasure = materials.get(0).getUnitOfMeasure();
            }
            orderLineVersion.setPartVersion(procureLine.getpPartVersion());
            orderLineVersion.setOrderStaDate(procureLine.getOrderStaDate());
            orderLineVersion.setQuantity(procureLineQty);
            orderLine.setPossibleToReceiveQuantity(procureLineQty);
            orderLineVersion.setUnitPrice(procureLine.getUnitPrice());
            orderLineVersion.setPriceType(PriceType.Negotiated);
            orderLineVersion.setCurrency(procureLine.getCurrency());
            orderLineVersion.setStaAgreedDate(procureLine.getRequiredStaDate());
            orderLineVersion.setStaAcceptedDate(DateUtil.getCurrentUTCDateTime());
            if (!StringUtils.isEmpty(procureLine.getForwardedUserId())) {
                orderLineVersion.setBuyerId(procureLine.getForwardedUserId());
                orderLineVersion.setBuyerName(procureLine.getForwardedUserName());
            } else {
                orderLineVersion.setBuyerId(procureLine.getMaterialControllerId());
                orderLineVersion.setBuyerName(procureLine.getMaterialControllerName());
            }
            orderLineVersion.setOrderLine(orderLine);
            orderLine.setCurrent(orderLineVersion);
            orderLineVersion.setUnitPriceInEuro(CurrencyUtil.convertUnitPriceToEUR(orderLineVersion.getUnitPrice(), orderLineVersion.getCurrency(),
                                                                                   commonServices));
            orderLineVersion.setPerQuantity(procureLine.getPerQuantity());
            listOfOrderLineVersions.add(orderLineVersion);
            orderLine.setOrderLineVersions(listOfOrderLineVersions);

            OrderLineLastModified orderLineLastModified = new OrderLineLastModified();
            orderLine.setOrderLineLastModified(orderLineLastModified);

            orderLine.setRequisitionId(procureLine.getRequisitionId());
            orderLine.setUnitOfMeasure(unitOfMeasure);
            orderLine.setStatus(OrderLineStatus.PLACED);

            updateOrderLineWithFinanceHeader(financeHeader, orderLine);

            orderLine.setProcureLine(procureLine);
            orderLine.setEarliestExpectedDate(procureLine.getRequiredStaDate());

            orderLines.add(orderLine);

            updateOrderLineWithDeliverySchedule(procureLine, orderLine, procureLineQty);

            // QI
            updateQIMarking(orderLine, procureLine);

            Order managedOrder = orderRepository.save(order);

            if (materials != null && !materials.isEmpty()) {
                for (Material material : materials) {
                    if (!material.getMaterialType().equals(MaterialType.USAGE_REPLACED)) {
                        material.setOrderLine(managedOrder.getOrderLines().get(0));
                        material.setOrderNo(managedOrder.getOrderNo());
                        MaterialLineStatusHelper.updateMaterialLinesWithOrderNo(material.getMaterialLine(), managedOrder.getOrderNo());
                        List<MaterialLine> materialLineList = material.getMaterialLine();
                        if (materialLineList != null && !materialLineList.isEmpty() && traceMaterialLineLog) {
                            for (MaterialLine materialLine : materialLineList) {
                                if (materialLine.getStatus().equals(MaterialLineStatus.ORDER_PLACED_INTERNAL)) {
                                    MaterialLineStatusHelper.createTraceabilityLogWithDeliverySchedule(material.getMaterialLine().get(0),
                                                                                                       traceabilityRepository, "Placed Internal",
                                                                                                       userDTO.getId(), userDTO.getUserName(),
                                                                                                       orderLine.getDeliverySchedule().get(0),
                                                                                                       GloriaTraceabilityConstants.ORDER_STAACCEPTED_AGREEDSTA);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void validateOrderInfo(ProcureLineDTO procureLineDTO) throws GloriaApplicationException {
        Order order = orderRepository.findOrderByOrderNo(procureLineDTO.getOrderNo());
        if (order != null && procureLineDTO.getSupplierCounterPartID() != null) {
            SupplierCounterPart supplierCounterPart = commonServices.getSupplierCounterPartById(procureLineDTO.getSupplierCounterPartID());
            boolean isShipToNotSame = !supplierCounterPart.getShipToId().equalsIgnoreCase(order.getShipToId());
            boolean isSuffixNotSame = !supplierCounterPart.getPpSuffix().equalsIgnoreCase(order.getSuffix());

            boolean isSupplierIdNotSame = order.getSupplierId() != null && procureLineDTO.getSupplierName() != null
                    && !procureLineDTO.getSupplierName().equalsIgnoreCase(order.getSupplierName());
            if (isShipToNotSame || isSuffixNotSame || isSupplierIdNotSame) {
                throw new GloriaApplicationException(GloriaExceptionConstants.DUPLICATE_ORDER_NO, "This operation is not allowed as "
                        + "the ShipTo&Suffix or SupplierId do not match the previous procurement with the same Order No. "
                        + "Please try to procure with new OrderNo");
            }
        }
    }

    private void updateOrderLineWithCountryInfo(OrderLine orderLine, ProcureLine procureLine) {
        Supplier supplier = procureLine.getSupplier();
        if (supplier != null) {
            Site site = commonServices.getSiteBySiteCode(supplier.getSupplierId());
            if (site != null) {
                if (!StringUtils.isEmpty(site.getCountryCode())) {
                    orderLine.setCountryOfOrigin(site.getCountryCode());
                }
            }
        }
    }

    private void updateQIMarking(OrderLine orderLine, ProcureLine procureLine) {
        if (orderLine != null) {
            List<Material> materials = procureLine.getMaterials();
            if (materials != null && !materials.isEmpty()) {
                MaterialLine materialLine = materials.get(0).getMaterialLine().get(0);
                orderLine.setQiMarking(deliveryServices.evaluateQIMarking(orderLine.getProjectId(), orderLine.getOrder().getSupplierId(),
                                                                          orderLine.getPartNumber(), orderLine.getPartName(), materialLine.getWhSiteId()));
            }
        }
    }

    private void updateOrderLineWithDeliverySchedule(ProcureLine procureLine, OrderLine orderLine, long procurementQty) {
        List<DeliverySchedule> deliverySchedules = new ArrayList<DeliverySchedule>();
        DeliverySchedule deliverySchedule = new DeliverySchedule();
        deliverySchedule.setOrderLine(orderLine);
        deliverySchedule.setExpectedDate(procureLine.getRequiredStaDate());
        deliverySchedule.setExpectedQuantity(procurementQty);
        deliverySchedules.add(deliverySchedule);
        orderLine.setDeliverySchedule(deliverySchedules);
    }

    private void updateOrderLineWithFinanceHeader(FinanceHeader financeHeader, OrderLine orderLine) {
        if (financeHeader != null) {
            orderLine.setProjectId(financeHeader.getProjectId());
        }
    }

    private void updateOrderWithSupplierCounterPartInfo(ProcureLine procureLine, SupplierCounterPart supplierCounterPart, FinanceHeader financeHeader,
            Order order) throws GloriaApplicationException {
        if (supplierCounterPart != null) {
            String projectId = "";
            if (financeHeader != null) {
                projectId = financeHeader.getProjectId();
            }
            Supplier supplier = procureLine.getSupplier();
            if (supplier != null) {
                commonServices.matchDeliveryController(supplierCounterPart.getDeliveryFollowUpTeam(), supplierCounterPart.getPpSuffix(),
                                                       supplier.getSupplierId(), projectId);
            }

            order.setDeliveryControllerTeam(supplierCounterPart.getDeliveryFollowUpTeam().getName());
        }
    }

    @Override
    public void sendProcureLineRequsitions(ProcureLine procureLine) {
        Requisition requisition = procureLine.getRequisition();
        if (requisition != null) {
            try {
                requisitionSender.sendRequsition(ProcurementHelper.transformToDTO(requisition));
                List<Material> materials = procureLine.getMaterials();
                UserDTO userDTO = null;
                try {
                    if (procureLine.isForwardedForDC()) {
                        userDTO = userServices.getUser(procureLine.getForwardedUserId());
                    } else {
                        userDTO = userServices.getUser(procureLine.getProcureUserId());
                    }
                } catch (GloriaApplicationException e) {
                    LOGGER.error(e.getErrorMessage());
                }
                if (userDTO != null) {
                    if (materials != null && !materials.isEmpty()) {
                        for (Material material : materials) {
                            List<MaterialLine> materialLineList = material.getMaterialLine();
                            if (materialLineList != null && !materialLineList.isEmpty()) {
                                for (MaterialLine materialLine : materialLineList) {
                                    if (materialLine.getStatus().equals(MaterialLineStatus.REQUISITION_SENT)) {
                                        MaterialLineStatusHelper.createTraceabilityLog(materialLine, traceabilityRepository, materialLine.getStatus().name(),
                                                                                       procureLine.getRequisitionId(), userDTO.getId(), userDTO.getUserName(),
                                                                                       GloriaTraceabilityConstants.ORDER_STAACCEPTED_AGREEDSTA);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (JAXBException e) {
                throw new GloriaSystemException(e, "Requisition couldn't be sent.");
            }
        }
    }

    @Override
    public void addFinanceHeader(FinanceHeader financeHeader) {
        requestHeaderRepository.addFinanceHeader(financeHeader);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL')")
    public void procureLater(List<Long> materialIds) throws GloriaApplicationException {
        List<Material> materialList = new ArrayList<Material>();
        Set<Long> procureLineIds = new HashSet<Long>();
        for (Long materialId : materialIds) {
            Material material = requestHeaderRepository.findMaterialById(materialId);
            ProcureLine procureLine = material.getProcureLine();
            procureLineIds.add(procureLine.getProcureLineOid());
            updateProcureTypeWhenProcuredLater(procureLine);
            if (material.getModificationId() > 0) {
                for (Material mat : procureLine.getMaterials()) {
                    if (material.getMaterialOID() == mat.getReplacedByOid()) {
                        materialList.add(mat);
                    }
                }
            }
            materialList.add(material);
        }

        // all Procurement list should be under same procureLine.
        ProcureLine procureLine = materialList.get(0).getProcureLine();

        // Validation
        // Check if all procurements is under same procure line.
        if (procureLineIds.size() != 1) {
            LOGGER.error("Materials could not be deleted, all procurements should be under same ProcureLine : " + materialIds);
            throw new GloriaSystemException("Please try again after some time. Can not perform procure later on Materials - " + materialIds,
                                            GloriaExceptionConstants.INVALID_PROCUREMENT_DELETION);
        }
        // Check Procure Line Status and if at least one more procurement left under current procure line.
        if (!procureLine.getStatus().equals(ProcureLineStatus.WAIT_TO_PROCURE) || !(procureLine.getMaterials().size() > materialList.size())) {
            LOGGER.error("Materials could not be deleted, Procure line should have at least one Material remaining and not yet Procured : "
                    + procureLine.getProcureLineOid());
            throw new GloriaApplicationException(procureLine.getProcureLineOid(), "procureLineOid", GloriaExceptionConstants.INVALID_PROCUREMENT_DELETION,
                                                 "Procure line should have at least one Material and not yet Procured", "ProcurementDTO");
        }

        try {

            procureLine.getMaterials().removeAll(materialList);

            // Copy existing ProcureLine Entity to a new ProcureLine
            ProcureLine newProcureLine = ProcurementHelper.cloneProcureLine(procureLine, materialList);
            newProcureLine = procureLineRepository.save(newProcureLine);

            // Move related Materials to the new ProcureLine
            for (Material material : materialList) {
                ProcureLineHelper.associateMaterialWithProcureLine(material, newProcureLine);
                requestHeaderRepository.updateMaterial(material);
            }
            // update needIsChange on newly created procureLine
            updateNeedIsChanged(newProcureLine, materialList);
            procureLineRepository.save(newProcureLine);

            ProcureGroupHelper.updateIdSetsOnProcureLine(procureLine, procureLine.getMaterials());
            ProcureGroupHelper.updateProcureLineUsageQty(procureLine);
            // update needIsChange on exsting procureLine
            updateNeedIsChanged(procureLine, procureLine.getMaterials());
            procureLineRepository.save(procureLine);

        } catch (Exception ex) {
            throw new GloriaSystemException(ex, "Please try again after some time. Can not perform procure later on Procurement - " + materialIds,
                                            GloriaExceptionConstants.INVALID_PROCUREMENT_DELETION);
        }
    }

    private void updateProcureTypeWhenProcuredLater(ProcureLine procureLine) throws GloriaApplicationException {
        // reset the procure type when its not with Internal Procurer(IP)
        if (isProcurementWithInteralProcurer(procureLine)) {
            if (existsMaterialInStock(procureLine)) {
                procureLine.setProcureType(ProcureType.INTERNAL_FROM_STOCK);
            }
        } else {
            setProcureLineProcureType(procureLine, procureLine.getMaterials());
        }
    }

    private boolean isProcurementWithInteralProcurer(ProcureLine procureLine) {
        return StringUtils.isNotEmpty(procureLine.getForwardedUserId()) && StringUtils.isNotEmpty(procureLine.getForwardedUserName())
                && StringUtils.isNotEmpty(procureLine.getForwardedTeam());
    }

    private void updateNeedIsChanged(ProcureLine procureLine, List<Material> materialList) {
        boolean needisChanged = false;
        for (Material material : materialList) {
            for (MaterialHeaderVersion materialHeaderVersion : material.getMaterialHeader().getMaterialHeaderVersions()) {
                if (materialHeaderVersion.getChangeId().getStatus().isChangeInWait()) {
                    needisChanged = true;
                    break;
                }
            }
        }
        procureLine.setNeedIsChanged(needisChanged);
    }

    @Override
    public Material updateMaterial(MaterialDTO materialDTO, String userId) throws GloriaApplicationException {
        Material material = requestHeaderRepository.findMaterialById(materialDTO.getId());
        if (material != null) {
            if (materialDTO.getVersion() != material.getVersion()) {
                throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET, DATA_OPTIMISTIC_LOCK_MESSAGE);
            }
            if (materialDTO.getReceiver() != null) {
                if (materialDTO.getReceiver().length() <= RECEIVER_LENGTH) {
                    material.setReceiver(materialDTO.getReceiver());
                } else {
                    throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_STRING_LENGTH,
                                                         "This operation cannot be performed since the length of the Receiver is exceeds the length 60.");
                }
            }
            String currentPartVersion = material.getPartVersion();
            String newPartVersion = materialDTO.getPartVersion();
            MaterialLineStatusHelper.updateAndAlertPartversionChange(material, currentPartVersion, newPartVersion, userServices.getUser(userId),
                                                                     materialServices, traceabilityRepository);

            // if its yet to be procured, update the finalWarehouse and directsend flag information on material lines
            ProcureLine procureLine = material.getProcureLine();
            if (procureLine != null && procureLine.getStatus() == ProcureLineStatus.WAIT_TO_PROCURE) {
                for (MaterialLine materialLine : material.getMaterialLine()) {
                    materialLine.setFinalWhSiteId(materialDTO.getFinalWhSiteId());
                    if (materialLine.getWhSiteId() != null && !materialLine.getWhSiteId().equals(materialLine.getFinalWhSiteId())) {
                        materialLine.setDirectSend(DirectSendType.YES_TRANSFER);
                    } else {
                        materialLine.setDirectSend(DirectSendType.NO);
                    }
                }
                requestHeaderRepository.updateMaterial(material);
            }

            // trigger lazy load
            material.getMaterialLine();
        }
        return material;
    }

    @Override
    public void addProcureLine(ProcureLine procureLine) {
        procureLineRepository.save(procureLine);
    }

    @Override
    public List<ProcureLine> findAllProcureLines() {
        return procureLineRepository.findAllProcureLines();
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE-INTERNAL', 'PROCURE','IT_SUPPORT')")
    public PageObject findMaterialsByStatusAndResponsability(ProcureLineStatus procureLineStatus, ProcureResponsibility procureResponsibility,
            String assignedMaterialControllerId, String assignedMaterialControllerTeam, String procureForwardedId, String procureForwardedTeam,
            PageObject pageObject, String materialType, String teamType) throws GloriaApplicationException {
        String userId = null;
        String userTeam = null;
        if (assignedMaterialControllerId != null) {
            userId = assignedMaterialControllerId;
            userTeam = assignedMaterialControllerTeam;
        } else {
            userId = procureForwardedId;
            userTeam = procureForwardedTeam;
        }
        return requestHeaderRepository.findMaterialsByStatusAndResponsability(procureLineStatus, procureResponsibility, assignedMaterialControllerId,
                                                                              assignedMaterialControllerTeam, procureForwardedId, pageObject, materialType,
                                                                              userServices.getUserCompanyCodeCodes(userId, userTeam, teamType));
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','DELIVERY','VIEWER_PRICE','VIEWER', 'WH_DEFAULT','WH_QI','IT_SUPPORT')")
    public MaterialDTO findMaterialById(long id) throws GloriaApplicationException {
        return procurementDtoTransformer.transformAsDTO(requestHeaderRepository.findMaterialById(id));
    }

    @Override
    public void createRequisition(ProcureLine procureLine) throws GloriaApplicationException {

        SupplierCounterPart supplierCounterPart = commonServices.getSupplierCounterPartById(procureLine.getSupplierCounterPartOID());

        Requisition requisition = createRequisition(procureLine, supplierCounterPart);
        String requisitionId = ProcurementServicesHelper.getUniqueRequisitionString(requisition.getRequisitionIdSequence());
        requisition.setRequisitionId(requisitionId);
        requisition = procureLineRepository.updateRequisition(requisition);

        procureLine.setRequisitionId(requisition.getRequisitionId());
        procureLine.setRequisition(requisition);

    }

    private Requisition createRequisition(ProcureLine procureLine, SupplierCounterPart supplierCounterPart) throws GloriaApplicationException {
        Requisition requisition = new Requisition();
        requisition.setIssuerOrganisation("");

        List<Material> materials = procureLine.getMaterials();
        String materialControllerUserId = procureLine.getMaterialControllerId();

        UserDTO userDTO = userServices.getUser(materialControllerUserId);

        requisition.setIssuerHandleId(materialControllerUserId);
        requisition.setIssuerName(userDTO.getUserName());
        requisition.setIssuerPhoneNo(userDTO.getPhoneNo());
        try {
            if (userDTO != null) {
                LDAPUserDTO ldapUserDTO = ActiveDirectory.getLDAPUserData(userDTO.getId());
                if (ldapUserDTO != null) {
                    requisition.setIssuerDepartment(StringUtils.leftPad(ldapUserDTO.getVolvoCostCentre(), DEPARTMENT_NAME_LENGTH, DEPARTMENT_PADDING_CHAR));
                }
            }
        } catch (Exception e) {
            GloriaExceptionLogger.log(e, ProcurementServicesBean.class);
        }
        requisition.setIssuerUserId(userDTO.getId());

        if (supplierCounterPart != null) {
            requisition.setMaterialUserId(supplierCounterPart.getMaterialUserId());
            requisition.setPpSuffix(supplierCounterPart.getPpSuffix());
        }

        Material usageMaterial = null;
        sortMaterialsByMaterialType(materials);
        for (Material material : materials) {
            if (!material.getMaterialType().equals(MaterialType.ADDITIONAL) && !material.getMaterialType().equals(MaterialType.USAGE_REPLACED)) {
                usageMaterial = material;
                break;
            }
        }

        if (usageMaterial != null) {
            MaterialHeaderVersion materialHeaderVersion = usageMaterial.getMaterialHeader().getAccepted();
            requisition.setContactPersonName(materialHeaderVersion.getContactPersonName());
            requisition.setOriginatorUserId(materialHeaderVersion.getContactPersonId());
            requisition.setOriginatorName(materialHeaderVersion.getContactPersonName());
        }
        calculateQuantityForRequisition(procureLine, requisition);

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

        requisition.setPurchaseInfo1(evaluatePurchaseInfo(procureLine));
        requisition.setPurchaseInfo2(procureLine.getProcureInfo());
        requisition.setPartNumber(procureLine.getpPartNumber());
        requisition.setPartQualifier(procureLine.getpPartAffiliation());
        requisition.setPartVersion(procureLine.getpPartVersion());
        requisition.setPartName(procureLine.getpPartName());
        requisition.setUnitOfMeasure(procureLine.getProcureType().translateUOMToGPSUnits(materials.get(0).getUnitOfMeasure(), commonServices));
        FinanceHeader financeHeader = procureLine.getFinanceHeader();
        requisition.setProjectId(financeHeader.getProjectId());
        requisition.setWbsCode(financeHeader.getWbsCode());
        requisition.setGlAccount(financeHeader.getGlAccount());
        requisition.setCostCenter(financeHeader.getCostCenter());

        requisition.setBuyerId(procureLine.getBuyerCode());
        requisition.setPurchaseOrganizationCode(procureLine.getPurchaseOrgCode());
        requisition.setReference(procureLine.getReferenceGps());

        procureLineRepository.addRequisition(requisition);

        return procureLineRepository.findRequisitionById(requisition.getRequisitionOid());
    }

    private String evaluatePurchaseInfo(ProcureLine procureLine) {
        String purchaseInfo1 = procureLine.getpPartModification();
        QualityDocument qualityDocument = qualityDocumentRepository.findById(procureLine.getQualityDocumentOID());
        if (qualityDocument != null) {
            if (!StringUtils.isEmpty(purchaseInfo1)) {
                purchaseInfo1 = purchaseInfo1.concat(":").concat(qualityDocument.getName());
            } else {
                purchaseInfo1 = qualityDocument.getName();
            }
        }
        return purchaseInfo1;
    }

    private void calculateQuantityForRequisition(ProcureLine procureLine, Requisition requisition) {
        List<Material> materials = procureLine.getMaterials();
        long quantity = 0;
        for (Material material : materials) {
            if (material.getMaterialType().isAllowedForRequisitionQty(procureLine)) {
                for (MaterialLine materialLine : material.getMaterialLine()) {
                    // consider REQUISITION_SENT for EXTERNAL and ORDER_PLACED for INTERNAL
                    if (materialLine.getStatus().isReceiveble() || materialLine.getStatus().isPlaceable()) {
                        quantity = sumMaterialLineQuantity(procureLine, quantity, materialLine);
                    }
                }
            }
        }
        requisition.setQuantity(procureLine.getProcureType().convertQuantityToGPSUnits(quantity, materials.get(0).getUnitOfMeasure(), commonServices));
    }

    private long sumMaterialLineQuantity(ProcureLine procureLine, long quantity, MaterialLine materialLine) {
        if (procureLine.getProcureType().isPartiallyProcuredFromStock()) {
            if (!materialLine.getStatus().isInStock() && !materialLine.getProcureType().equals(ProcureType.FROM_STOCK)) {
                quantity += materialLine.getQuantity();
            }
        } else {
            quantity += materialLine.getQuantity();
        }
        return quantity;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE', 'PROCURE-INTERNAL','IT_SUPPORT')")
    public boolean isManualGroupingAllowed(List<Long> ids, String type) throws GloriaApplicationException {
        List<Material> materials = new ArrayList<Material>();

        if ("material".equalsIgnoreCase(type)) {
            if (ids != null && !ids.isEmpty()) {
                for (Long id : ids) {
                    Material material = requestHeaderRepository.findMaterialById(id);
                    materials.add(material);
                }
            }
        } else if ("procureLine".equalsIgnoreCase(type)) {
            if (ids != null && !ids.isEmpty()) {
                for (Long id : ids) {
                    materials.addAll(findMaterialByProcureLineId(id));
                }
            }
        }

        boolean isGroupTypeMod2 = findGroupType(materials);
        ProcureGroupHelper groupHelper = null;
        List<MaterialGroupDTO> materialGroupDTOs = new ArrayList<MaterialGroupDTO>();
        MaterialGroupDTO materialGroupDTO = new MaterialGroupDTO();
        materialGroupDTO.setMaterials(materials);
        materialGroupDTOs.add(materialGroupDTO);
        if (isGroupTypeMod2) {
            groupHelper = new ProcureGroupHelper(materialGroupDTOs, GROUP_TYPE_SAME_TOBJ_DIFF_PART);
        } else {
            groupHelper = new ProcureGroupHelper(materialGroupDTOs, GROUP_TYPE_DIFF_TOBJ_SAME_PART);
        }
        return groupHelper.isValidForManualGrouping() && isNotModifiedEarlier(materials) && isAvailableForGrouping(materials);
    }

    private boolean isAvailableForGrouping(List<Material> materials) {
        boolean isAvailableForGrouping = true;
        for (Material material : materials) {
            if (!material.getStatus().isAvailableForGrouping(material)) {
                isAvailableForGrouping = false;
                break;
            }
        }
        return isAvailableForGrouping;
    }

    private boolean isNotModifiedEarlier(List<Material> materials) {
        boolean isNotModifiedEarlier = true;
        for (Material material : materials) {
            if (!material.getMaterialType().isModifiable()) {
                isNotModifiedEarlier = false;
                break;
            }
        }
        return isNotModifiedEarlier;
    }

    private boolean findGroupType(List<Material> materials) {
        Set<String> partInfo = new HashSet<String>();
        boolean isGroupTypeMod2 = false;
        for (Material material : materials) {
            String key = "partAffiliation=" + StringUtils.trimToEmpty(material.getPartAffiliation()) + ", partNumber="
                    + StringUtils.trimToEmpty(material.getPartNumber()) + ", partVersion=" + StringUtils.trimToEmpty(material.getPartVersion())
                    + ", partModification=" + StringUtils.trimToEmpty(material.getPartModification());
            if (!partInfo.isEmpty() && !partInfo.contains(key.toLowerCase())) {
                isGroupTypeMod2 = true;
            }
            partInfo.add(key.toLowerCase());
        }
        return isGroupTypeMod2;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','IT_SUPPORT')")
    public Buyer findByBuyerId(String buyerId) {
        return purchaseOrganisationRepo.findBuyerByCode(buyerId);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','IT_SUPPORT')")
    public PageObject findAllBuyerCodes(PageObject pageObject, String organisationCode) {
        return purchaseOrganisationRepo.findAllBuyers(pageObject, organisationCode);
    }

    @Override
    public Buyer addBuyerCode(Buyer buyerCode) {
        return purchaseOrganisationRepo.save(buyerCode);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE', 'PROCURE-INTERNAL')")
    public List<SiteDTO> getConsignorIds(long procureLineId) {
        ProcureLine procureLine = procureLineRepository.findById(procureLineId);
        Supplier supplier = procureLine.getSupplier();
        if (supplier != null) {
            return evaluateConsignerIds(supplier.getSupplierId(), procureLine.getMaterials());
        } else {
            return null;
        }

    }

    private List<SiteDTO> evaluateConsignerIds(String consignerId, List<Material> materials) {
        List<SiteDTO> siteDTOs = new ArrayList<SiteDTO>();
        if (!StringUtils.isEmpty(consignerId)) {
            Site site = commonServices.getSiteBySiteId(consignerId);
            if (site != null && site.isBuildSite() && site.getBuildSiteType().equals(PLANT)) {
                SiteDTO siteDto = CommonHelper.transforToSiteDTO(site);
                siteDTOs.add(siteDto);
            }
        }

        for (Material material : materials) {
            if (material.isCarryOverExist()) {
                String outboundLocationId = material.getMaterialHeader().getAccepted().getOutboundLocationId();
                Site site = commonServices.getSiteBySiteId(outboundLocationId);
                if (site != null && site.isBuildSite() && site.getBuildSiteType().equalsIgnoreCase(PLANT)) {
                    SiteDTO siteDto = CommonHelper.transforToSiteDTO(site);
                    siteDTOs.add(siteDto);
                }
            }
        }

        return siteDTOs;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ChangeId manageRequest(List<RequestGatewayDTO> requestGatewayDtos) throws GloriaApplicationException {
        return processRequest(requestGatewayDtos);
    }

    @Override
    public ChangeId manageRequestNoTransaction(List<RequestGatewayDTO> requestGatewayDtos) throws GloriaApplicationException {
        return processRequest(requestGatewayDtos);
    }

    private ChangeId processRequest(List<RequestGatewayDTO> requestGatewayDtos) throws GloriaApplicationException {
        List<MaterialHeader> materialHeaders = null;
        List<MaterialHeader> materialHeadersToAutoAssign = new ArrayList<MaterialHeader>();
        ChangeId cancelChangeId = null;
        for (RequestGatewayDTO requestGatewayDTO : requestGatewayDtos) {
            ChangeIdTransformerDTO changeIdTransformerDTO = requestGatewayDTO.getChangeIdTransformerDto();
            String changeIdCrId = changeIdTransformerDTO.getMaterialRequestChangeAddId();
            if (requestGatewayDTO.isCancelRequest()) {
                cancelChangeId = createCancelChangeId(cancelChangeId, requestGatewayDTO, changeIdTransformerDTO);
              //trace cancel Protom messages
                MaterialLineStatusHelper.traceProcureRequestMessage(requestGatewayDTO.getSenderLogicalId(), cancelChangeId, traceabilityRepository);
            } else {
                ChangeId changeId = createChangeId(requestGatewayDTO);
                for (RequestHeaderTransformerDTO requestHeaderTransformerDTO : requestGatewayDTO.getRequestHeaderTransformerDtos()) {
                    RequestType requestType = RequestType.valueOf(requestHeaderTransformerDTO.getRequestType());

                    // override requestType if additionalUsageHeaderExists
                    if (requestHeaderTransformerDTO.isAdditonalUsageHeader()) {
                        requestType = RequestType.ADDITIONAL_USAGE;
                        requestHeaderTransformerDTO.setRequestType(RequestType.ADDITIONAL_USAGE.name());
                    }

                    for (RequestHeaderVersionTransformerDTO requestHeaderVersionTransformerDTO 
                                                                                : requestHeaderTransformerDTO.getRequestHeaderVersionTransformerDtos()) {
                     materialHeaders = requestType.identifyHeaders(requestType.getReferenceId(requestHeaderTransformerDTO),
                                                                                           requestHeaderTransformerDTO.getMtrlRequestId(),
                                                                                           requestHeaderVersionTransformerDTO.getOutboundLocationId(),
                                                                                           changeIdTransformerDTO.getProcureRequestId(),
                                                                                           requestType.getBuildId(requestHeaderTransformerDTO), this);

                        materialHeaders = requestType.createOrUpdateHeaders(materialHeaders, requestHeaderTransformerDTO,
                                                                            requestHeaderVersionTransformerDTO.getOutboundLocationId(),
                                                                            requestHeaderRepository, this, changeIdCrId, requestHeaderVersionTransformerDTO);
                        
                       

                        for (MaterialHeader materialHeader : materialHeaders) {
                            manageMaterialHeader(requestType,
                                                 materialHeader,
                                                 changeId,
                                                 requestType.isAlreadyAssigned(requestGatewayDTO.getRequestHeaderTransformerDtos(),
                                                                               changeIdTransformerDTO.getProcureRequestId(), materialHeader, this),
                                                 requestHeaderTransformerDTO, changeIdCrId);
                            
                            if (hasToBeAssigned(materialHeader)) {
                                materialHeadersToAutoAssign.add(materialHeader);
                            }
                        }
                    }          
                }
                
                changeId.getStatus().init(hasChangeInMaterials(changeId), requestHeaderRepository, materialProcureResponse, changeId, "");
                changeId.getStatus().setAcceptedRelationToMaterialHeader(changeId, requestHeaderRepository);

                
                for (MaterialHeader materialHeader : materialHeadersToAutoAssign) {
                    materialHeader.getRequestType().autoAssignToMC(materialHeader, this, userServices, teamRepository, companyCodeRepository);
                }
             
                //trace Protom messages
                MaterialLineStatusHelper.traceProcureRequestMessage(requestGatewayDTO.getSenderLogicalId(), changeId, traceabilityRepository);
                return changeId;
            }
        }
        if (cancelChangeId != null) {
            cancelChangeId.getStatus().cancel(cancelChangeId, requestHeaderRepository, materialProcureResponse, "all", procureLineRepository,
                                              traceabilityRepository);
            return cancelChangeId;
        }
        return null;
    }

    private boolean hasToBeAssigned(MaterialHeader materialHeader) {
       return !StringUtils.isEmpty(materialHeader.getMcIdToBeAssigned());
    }

    private boolean hasChangeInMaterials(ChangeId changeId) {
        List<Material> addMaterials = changeId.getAddMaterials();
        List<Material> removeMaterials = changeId.getRemoveMaterials();
        boolean hasChange = false;
        if (addMaterials != null && !addMaterials.isEmpty()) {
            for (Material addMaterial : addMaterials) {
                if (!addMaterial.getMaterialType().equals(MaterialType.ADDITIONAL_USAGE) && addMaterial.getStatus() == MaterialStatus.ADD_NOT_ACCEPTED) {
                    hasChange = true;
                }
            }
        }
        if (removeMaterials != null && !removeMaterials.isEmpty()) {
            for (Material removeMaterial : removeMaterials) {
                if (!removeMaterial.getMaterialType().equals(MaterialType.ADDITIONAL_USAGE) && removeMaterial.getStatus() == MaterialStatus.REMOVE_MARKED) {
                    hasChange = true;
                }
            }
        }
        return hasChange;
    }

    private boolean isAssigned(Material material) {
        return material != null && material.getProcureLine() != null;
    }

    private ChangeId createCancelChangeId(ChangeId cancelChangeId, RequestGatewayDTO requestGatewayDTO, ChangeIdTransformerDTO changeIdTransformerDTO)
            throws GloriaApplicationException {
        if ("PROTOM".equals(requestGatewayDTO.getSenderLogicalId())) {
            return handleProtomCancel(cancelChangeId, requestGatewayDTO, changeIdTransformerDTO);
        }
        return handleMaterialRequestCancel(cancelChangeId, requestGatewayDTO, changeIdTransformerDTO);
    }

    private ChangeId handleMaterialRequestCancel(ChangeId cancelChangeId, RequestGatewayDTO requestGatewayDTO, ChangeIdTransformerDTO changeIdTransformerDTO)
            throws GloriaApplicationException {
        ChangeId change = requestHeaderRepository.findChangeIdByTechId(changeIdTransformerDTO.getChangeTechId());

        List<Material> materialsToCancel = new ArrayList<Material>();

        if (change.getStatus().isCancelRejected()) {
            cancelChangeId = change;
            materialsToCancel.addAll(cancelChangeId.getRemoveMaterials());
        } else {
            if (cancelChangeId == null) {
                cancelChangeId = createChangeId(requestGatewayDTO);
                cancelChangeId.setChangeTechId(Utils.createRandomString());
                cancelChangeId.setStatus(ChangeIdStatus.ACCEPTED);
                cancelChangeId.setType(ChangeType.CANCEL);
                cancelChangeId = requestHeaderRepository.save(cancelChangeId);

                List<MaterialHeaderVersion> materialHeaderVersions = change.getMaterialHeaderVersions();

                Map<Long, MaterialHeaderVersion> acceptedMaterialHeaderVersions = new HashMap<Long, MaterialHeaderVersion>();
                for (MaterialHeaderVersion materialHeaderVersion : materialHeaderVersions) {
                    MaterialHeaderVersion acceptedMaterialHeaderVersion = materialHeaderVersion.getMaterialHeader().getAccepted();
                    Long acceptedMaterialHeaderVersionOid = acceptedMaterialHeaderVersion.getMaterialHeaderVersionOid();
                    if (!acceptedMaterialHeaderVersions.containsKey(acceptedMaterialHeaderVersionOid)) {
                        acceptedMaterialHeaderVersions.put(acceptedMaterialHeaderVersionOid, acceptedMaterialHeaderVersion);
                    }
                }

                for (MaterialHeaderVersion anAcceptedMaterialHeaderVersion : acceptedMaterialHeaderVersions.values()) {
                    MaterialHeaderVersion clonedHeaderAcceptedVersion = cloneMaterialHeaderVersion(null, anAcceptedMaterialHeaderVersion);
                    cancelChangeId.getMaterialHeaderVersions().add(clonedHeaderAcceptedVersion);
                    clonedHeaderAcceptedVersion.setChangeId(cancelChangeId);
                }
            }

            boolean hasChangeIdMaterialHeadersAssignedToMaterialController = ChangeIdStatusHelper.hasChangeIdMaterialHeadersAssignedToMaterialController(change);

            if (hasChangeIdMaterialHeadersAssignedToMaterialController) {
                cancelChangeId.setVisibleUi(ChangeIdStatusHelper.hasChangeIdMaterialHeadersAssignedToMaterialController(change));
            }

            materialsToCancel.addAll(change.getAddMaterials());
        }

        // remove change.....
        if (!materialsToCancel.isEmpty()) {
            for (Material materialToremove : materialsToCancel) {
                if (!materialToremove.getMaterialType().equals(MaterialType.MODIFIED)) { // GLO-5968
                    MaterialHeader materialHeader = materialToremove.getMaterialHeader();
                    if (materialHeader != null) {
                        materialToremove.getStatus().remove(cancelChangeId, isAssigned(materialToremove), materialToremove, procureLineRepository,
                                                            traceabilityRepository);
                    }
                }
            }
        }

        return cancelChangeId;
    }

    private ChangeId handleProtomCancel(ChangeId cancelChangeId, RequestGatewayDTO requestGatewayDTO, ChangeIdTransformerDTO changeIdTransformerDTO)
            throws GloriaApplicationException {

        List<ChangeId> changeIds = requestHeaderRepository.findChangeIdByProcureRequestId(changeIdTransformerDTO.getChangeTechId());

        if (cancelChangeId == null) {
            cancelChangeId = createChangeId(requestGatewayDTO);
            cancelChangeId.setStatus(ChangeIdStatus.ACCEPTED);
            cancelChangeId.setType(ChangeType.CANCEL);
            cancelChangeId = requestHeaderRepository.save(cancelChangeId);
        }

        ChangeId change = changeIds.get(0);

        boolean hasChangeIdMaterialHeadersAssignedToMaterialController = ChangeIdStatusHelper.hasChangeIdMaterialHeadersAssignedToMaterialController(change);

        if (hasChangeIdMaterialHeadersAssignedToMaterialController) {
            cancelChangeId.setVisibleUi(true);
        }

        List<MaterialHeaderVersion> materialHeaderVersions = change.getMaterialHeaderVersions();
        if (!materialHeaderVersions.isEmpty()) {
            MaterialHeaderVersion currentAcceptedVersion = materialHeaderVersions.get(0).getMaterialHeader().getAccepted();
            for (MaterialHeaderVersion materialHeaderVersion : materialHeaderVersions) {
                MaterialHeaderVersion clonedHeaderVersion = cloneMaterialHeaderVersion(null, materialHeaderVersion);
                cancelChangeId.getMaterialHeaderVersions().add(clonedHeaderVersion);
                clonedHeaderVersion.setChangeId(cancelChangeId);
                if (change.getRequestType().isProtomRequest() && !change.getType().equals(ChangeType.MR) && !change.getType().equals(ChangeType.LC)) {
                    if (!StringUtils.contains(cancelChangeId.getMtrlRequestVersion(), V)) {
                        cancelChangeId.setMtrlRequestVersion(change.getMtrlRequestVersion().substring(0, change.getMtrlRequestVersion().indexOf(V)) + V
                                + clonedHeaderVersion.getHeaderVersion());
                    }
                } else {
                    cancelChangeId.setMtrlRequestVersion(change.getMtrlRequestVersion());
                }
                currentAcceptedVersion.getMaterialHeader().getMaterialHeaderVersions().add(clonedHeaderVersion);
            }
        }

        for (ChangeId changeId : changeIds) {
            List<Material> materialsToCancel = changeId.getAddMaterials();

            if (!materialsToCancel.isEmpty()) {
                for (Material materialToremove : materialsToCancel) {
                    materialToremove.getStatus().remove(cancelChangeId, hasChangeIdMaterialHeadersAssignedToMaterialController, materialToremove,
                                                        procureLineRepository, traceabilityRepository);
                }
            }
        }
        return cancelChangeId;
    }

    private MaterialHeaderVersion cloneMaterialHeaderVersion(ChangeId changeId, MaterialHeaderVersion materialHeaderVersion) {
        MaterialHeader materialHeader = materialHeaderVersion.getMaterialHeader();

        MaterialHeaderVersion clonedHeaderVersion = new MaterialHeaderVersion();
        clonedHeaderVersion.setContactPersonEmail(materialHeaderVersion.getContactPersonEmail());
        clonedHeaderVersion.setContactPersonId(materialHeaderVersion.getContactPersonId());
        clonedHeaderVersion.setContactPersonName(materialHeaderVersion.getContactPersonName());
        clonedHeaderVersion.setHeaderVersion(materialHeader.getRequestType().getNextHeaderVersion(materialHeader, changeId, requestHeaderRepository));

        clonedHeaderVersion.setMaterialHeader(materialHeader);
        clonedHeaderVersion.setOutboundLocationId(materialHeaderVersion.getOutboundLocationId());
        clonedHeaderVersion.setOutboundLocationName(materialHeaderVersion.getOutboundLocationName());
        clonedHeaderVersion.setOutboundLocationType(materialHeaderVersion.getOutboundLocationType());
        clonedHeaderVersion.setOutboundStartDate(materialHeaderVersion.getOutboundStartDate());
        clonedHeaderVersion.setReceivedDateTime(DateUtil.getCurrentUTCDateTime());
        clonedHeaderVersion.setReferenceGroup(materialHeaderVersion.getReferenceGroup());
        clonedHeaderVersion.setRequesterName(materialHeaderVersion.getRequesterName());
        clonedHeaderVersion.setRequesterNotes(materialHeaderVersion.getRequesterNotes());
        clonedHeaderVersion.setRequesterUserId(materialHeaderVersion.getRequesterUserId());

        return clonedHeaderVersion;
    }

    private MaterialHeader manageMaterialHeader(RequestType requestType, MaterialHeader materialHeader, ChangeId changeId,
            boolean isRequestHeaderAssignedToMaterialController, RequestHeaderTransformerDTO requestHeaderTransformerDTO, String changeIdCrId)
            throws GloriaApplicationException {
        String removeReferenceId = requestHeaderTransformerDTO.getRemoveReferenceId();
        String buildId = requestHeaderTransformerDTO.getBuildId();

        if (isNewChange(requestHeaderTransformerDTO) || !StringUtils.isEmpty(buildId) || !StringUtils.isEmpty(removeReferenceId)
                || materialHeader.getAccepted() == null) {
            // add MaterialHeaderVersion.headerVersion at the end of mtrlRequestVersion for testobjects
            if (isProtomRequest(requestHeaderTransformerDTO) && !changeId.getType().equals(ChangeType.MR) && !changeId.getType().equals(ChangeType.LC)) {
                if (!StringUtils.contains(changeId.getMtrlRequestVersion(), V)) {
                    if (changeId.getType().equals(ChangeType.FIRST_ASSEMBLY)) {
                        changeId.setMtrlRequestVersion(changeId.getMtrlRequestVersion() + materialHeader.getFirstAssemblyIdSequence() + V
                                + requestType.getNextHeaderVersion(materialHeader, changeId, requestHeaderRepository));
                    } else {
                        changeId.setMtrlRequestVersion(changeId.getMtrlRequestVersion() + V
                                + requestType.getNextHeaderVersion(materialHeader, null, requestHeaderRepository));
                    }
                }
            }
            changeId = requestHeaderRepository.save(changeId);
        } else {
            changeId = materialHeader.getAccepted().getChangeId();
        }

        // create or update accepted version
        MaterialHeaderVersion materialHeaderVersion = createRequestHeaderVersions(requestType, requestHeaderTransformerDTO, materialHeader, changeId);

        // link ChangeId to current version
        changeId.getMaterialHeaderVersions().add(materialHeaderVersion);

        // create/merge request lines and link to header & changeId
        handleRequestHeader(requestType, requestHeaderTransformerDTO, changeId, materialHeader, materialHeaderVersion,
                            isRequestHeaderAssignedToMaterialController, changeIdCrId);
        requestHeaderRepository.save(materialHeader);

        ChangeId previouesChangeId = materialHeaderVersion.getChangeId();
        if (previouesChangeId != null) {
            materialHeaderVersion.setPreviousChangeId(previouesChangeId);
        }
        materialHeaderVersion.setChangeId(changeId);

        materialHeader.getMaterials().addAll(changeId.getAddMaterials());

        if (isProtomRequest(materialHeader)) {
            createPartNumber(requestHeaderTransformerDTO);
        }
        return materialHeader;
    }

    private boolean isNewChange(RequestHeaderTransformerDTO requestHeaderTransformerDTO) {
        return requestHeaderTransformerDTO.getRequestHeaderVersionTransformerDtos().get(0).isContainingLines() || !isProtomRequest(requestHeaderTransformerDTO);

    }

    private boolean isProtomRequest(MaterialHeader materialHeader) {
        if (materialHeader != null) {
            return materialHeader.getRequestType().isProtomRequest();
        } else {
            return false;
        }
    }

    private boolean isProtomRequest(RequestHeaderTransformerDTO requestHeaderTransformerDTO) {
        return RequestType.valueOf(requestHeaderTransformerDTO.getRequestType()).isProtomRequest();
    }

    private void createPartNumber(RequestHeaderTransformerDTO requestHeader) {
        for (RequestLineTransformerDTO requestLineTransformerDTO : requestHeader.getRequestLineTransformerDTOs()) {
            PartNumber partNumber = null;
            if (requestLineTransformerDTO != null && !StringUtils.isEmpty(requestLineTransformerDTO.getPartNumber())) {
                partNumber = findVolvoPartWithAliasByPartNumber(requestLineTransformerDTO.getPartNumber());
                if (partNumber == null) {
                    partNumber = new PartNumber();
                    partNumber.setVolvoPartNumber(requestLineTransformerDTO.getPartNumber());
                }
                partNumber.setVolvoPartAffiliation(requestLineTransformerDTO.getPartAffiliation());
                createPartAlias(requestLineTransformerDTO, partNumber, partNumber.getPartAlias());
                partNumberRepo.save(partNumber);
            }
        }
    }

    private Domain createPartAlias(RequestLineTransformerDTO requestLineTransformerDTO, PartNumber partNumber, List<PartAlias> partAliases) {
        Domain gloriaDomain = null;
        if (requestLineTransformerDTO.getPartAliasTransformerDtos() != null) {
            for (PartAliasTransformerDTO alias : requestLineTransformerDTO.getPartAliasTransformerDtos()) {
                if (!isExistsPartAlias(alias, partAliases)) {
                    String kolaDomain = alias.getDomain();
                    gloriaDomain = translateToGloriaDomain(kolaDomain);
                    if (kolaDomain != null && gloriaDomain != null) {
                        PartAlias partAlias = new PartAlias();
                        partAlias.setPartNumber(partNumber);
                        partAlias.setDomain(gloriaDomain);
                        partAlias.setAliasPartNumber(alias.getCode());
                        partAliases.add(partAlias);
                    }
                }
            }
        }
        return gloriaDomain;
    }

    private boolean isExistsPartAlias(PartAliasTransformerDTO alias, List<PartAlias> partAliases) {
        if (!StringUtils.isEmpty(alias.getCode()) && !StringUtils.isEmpty(alias.getDomain())) {
            for (PartAlias partAlias : partAliases) {
                if (partAlias.getAliasPartNumber().equalsIgnoreCase(alias.getCode()) && partAlias.getDomain() == translateToGloriaDomain(alias.getDomain())) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private Domain translateToGloriaDomain(String kolaDomain) {
        if (kolaDomain.equalsIgnoreCase(MAC)) {
            return Domain.MACK;
        } else if (kolaDomain.equalsIgnoreCase(RVI)) {
            return Domain.RENAULT;
        } else if (kolaDomain.equalsIgnoreCase(NDM)) {
            return Domain.UD_TRUCKS;
        } else {
            return null;
        }

    }

    private void handleRequestHeader(RequestType requestType, RequestHeaderTransformerDTO requestHeaderTransformerDTO, ChangeId changeId,
            MaterialHeader materialHeader, MaterialHeaderVersion materialHeaderVersion, boolean procureLineExists, String changeIdCrId)
            throws GloriaApplicationException {

        requestType.planOrUnplan(requestHeaderTransformerDTO, materialHeader, changeIdCrId, requestHeaderRepository, this);
        requestType.swapTestObject(requestHeaderTransformerDTO, materialHeader, changeIdCrId, requestHeaderRepository, this);
        materialHeader.setMtrlRequestId(requestHeaderTransformerDTO.getMtrlRequestId());

        // create all the ADD types
        handleMaterialsAdded(requestHeaderTransformerDTO, changeId, materialHeader, materialHeaderVersion, procureLineExists, this);

        // remove all REMOVE types
        handleMaterialsRemoved(requestHeaderTransformerDTO, changeId, procureLineExists);

        // update when there is no change to an existing requestline.
        // update the header information when there is no "Change"
        RequestHeaderVersionTransformerDTO versionTransformerDTO = requestHeaderTransformerDTO.getRequestHeaderVersionTransformerDtos().get(0);
        String previousReferenceId = materialHeader.getReferenceId();

        String previousReferenceGroup = null;
        if (materialHeader.getAccepted() != null) {
            previousReferenceGroup = materialHeader.getAccepted().getReferenceGroup();
        }

        String referenceId = requestHeaderTransformerDTO.getReferenceId();
        String referenceGroup = versionTransformerDTO.getReferenceGroup();
        materialHeader.setReferenceId(referenceId);
        for (Material material : materialHeader.getMaterials()) {
            if (!materialHeader.getRequestType().isProtomRequest()) {
                material.setRequiredStaDate(versionTransformerDTO.getRequiredSTADate());
            }
            List<MaterialLine> materialLines = material.getMaterialLine();
            if (materialLines != null && !materialLines.isEmpty()) {
                for (MaterialLine materialLine : materialLines) {
                    if (materialLine.getMaterialOwner() == null) {
                        materialLine.setMaterialOwner(material);
                    }
                    RequestGroup requestGroup = materialLine.getRequestGroup();
                    if (requestGroup != null) {
                        requestGroup.setReferenceId(referenceId);
                    }
                }
            }
            // update procureline with requestGroup & referenceId
            ProcureLine procureLine = material.getProcureLine();
            if (procureLine != null) {
                procureLine.setReferenceGroups(versionTransformerDTO.getReferenceGroup());
                procureLine.setRequiredStaDate(versionTransformerDTO.getRequiredSTADate());
                String referenceGroups = procureLine.getReferenceGroups();
                if (StringUtils.isNotBlank(referenceGroups)) {
                    procureLine.setReferenceGroups(StringUtils.replace(referenceGroups, previousReferenceGroup, referenceGroup));
                } else {
                    procureLine.setReferenceGroups(referenceGroup);
                }
                ProcureGroupHelper.updateIdSetsOnProcureLine(procureLine, procureLine.getMaterials());
            }

            // update deliveryNoteLine with referenceId
            ProcurementServicesHelper.replaceDeliveryNoteLineReferenceIds(referenceId, material, previousReferenceId);
        }
    }

    private void handleMaterialsRemoved(RequestHeaderTransformerDTO requestHeaderTransformerDTO, ChangeId changeId, boolean procureLineExists)
            throws GloriaApplicationException {
        for (RequestLineTransformerDTO requestLineTransformerDto : requestHeaderTransformerDTO.getRequestLineTransformerDTOs()) {
            if (requestLineTransformerDto.isRemoveType()) {
                Material material = requestHeaderRepository.findMaterialByProcureLinkId(requestLineTransformerDto.getProcureLinkId());
                if (material != null) {
                    material.getStatus().remove(changeId, procureLineExists, material, procureLineRepository, traceabilityRepository);
                }
            }
        }
    }

    private void handleMaterialsAdded(RequestHeaderTransformerDTO requestHeaderTransformerDTO, ChangeId changeId, MaterialHeader materialHeader,
            MaterialHeaderVersion materialHeaderVersion, boolean procureLineExists, ProcurementServices procurementServices) throws GloriaApplicationException {
        for (RequestLineTransformerDTO requestLineTransformerDto : requestHeaderTransformerDTO.getRequestLineTransformerDTOs()) {
            if (!requestLineTransformerDto.isRemoveType()) {
                Material material = ProcurementHelper.createMaterial(changeId, materialHeader, procureLineExists, requestHeaderTransformerDTO,
                                                                     requestLineTransformerDto, materialServices, traceabilityRepository);
                procurementServices.updateMaterialWithCarryOverInfo(material, materialHeaderVersion.getOutboundLocationType(),
                                                                    materialHeaderVersion.getOutboundLocationId());
                material.getStatus().init(procureLineExists, material);
            }
        }
    }

    /**
     * MaterialHeaderVersion shall be created only when there are LineAdd elements.
     * 
     * @param changeId
     * @return
     */
    private MaterialHeaderVersion createRequestHeaderVersions(RequestType requestType, RequestHeaderTransformerDTO requestHeaderTransformerDTO,
            MaterialHeader materialHeader, ChangeId changeId) {
        RequestHeaderVersionTransformerDTO requestHeaderVersionTransformerDto = requestHeaderTransformerDTO.getRequestHeaderVersionTransformerDtos().get(0);
        MaterialHeaderVersion requestHeaderVersion = null;
        if (requestHeaderVersionTransformerDto.isContainingLines() || !isProtomRequest(materialHeader) || materialHeader.getAccepted() == null) {
            requestHeaderVersion = new MaterialHeaderVersion();
        } else {
            requestHeaderVersion = materialHeader.getAccepted();
        }
        requestHeaderVersion.setHeaderVersion(requestType.getNextHeaderVersion(materialHeader, changeId, requestHeaderRepository));
        requestHeaderVersion.setReceivedDateTime(requestHeaderVersionTransformerDto.getReceivedDateTime());
        requestHeaderVersion.setReferenceGroup(requestHeaderVersionTransformerDto.getReferenceGroup());

        requestType.setOutboundLocationInformation(requestHeaderVersionTransformerDto, requestHeaderVersion, commonServices);

        if (StringUtils.isEmpty(requestHeaderTransformerDTO.getBuildRemoveId())) {
            requestHeaderVersion.setOutboundStartDate(requestHeaderVersionTransformerDto.getOutboundStartDate());
        }

        requestHeaderVersion.setRequesterUserId(requestHeaderVersionTransformerDto.getRequesterUserId());
        requestHeaderVersion.setRequesterName(requestHeaderVersionTransformerDto.getRequesterName());
        requestHeaderVersion.setRequesterNotes(requestHeaderVersionTransformerDto.getRequesterNotes());
        String contactPersonId = requestHeaderVersionTransformerDto.getContactPersonId();
        requestHeaderVersion.setContactPersonId(contactPersonId);
        requestHeaderVersion.setContactPersonName(requestHeaderVersionTransformerDto.getContactPersonName());
        try {
            if (StringUtils.isNotBlank(contactPersonId)) {
                LDAPUserDTO ldapUserDTO = ActiveDirectory.getLDAPUserData(contactPersonId);
                if (ldapUserDTO != null) {
                    requestHeaderVersion.setContactPersonEmail(ldapUserDTO.getEmail());
                }
            }
        } catch (Exception e) {
            GloriaExceptionLogger.log(e, ProcurementServicesBean.class);
        }
        requestHeaderVersion.setMaterialHeader(materialHeader);
        requestHeaderRepository.save(materialHeader);
        return requestHeaderVersion;
    }

    @Override
    public MaterialHeader createRequestHeader(RequestHeaderTransformerDTO requestHeaderTransformerDTO, String outboundLocationId, Long currentFASequence,
            String changeIdCrId,RequestHeaderVersionTransformerDTO requestHeaderVersionTransformerDTO) throws GloriaApplicationException {
        MaterialHeader materialHeader = new MaterialHeader();
        materialHeader.setReferenceId(requestHeaderTransformerDTO.getReferenceId());
        materialHeader.setActive(requestHeaderTransformerDTO.isActive());
        materialHeader.setMtrlRequestId(requestHeaderTransformerDTO.getMtrlRequestId());
        materialHeader.setFirstAssemblyIdSequence(currentFASequence);
        if(requestHeaderVersionTransformerDTO.getMaterialControllerUserId()!=null) {
            materialHeader.setMcIdToBeAssigned(requestHeaderVersionTransformerDTO.getMaterialControllerUserId().toUpperCase());
        }
        
        materialHeader.setRequestType(RequestType.valueOf(requestHeaderTransformerDTO.getRequestType()));
        materialHeader.getRequestType().planOrUnplan(requestHeaderTransformerDTO, materialHeader, changeIdCrId, requestHeaderRepository, this);
        materialHeader.getRequestType().swapTestObject(requestHeaderTransformerDTO, materialHeader, changeIdCrId, requestHeaderRepository, this);
       
       // materialHeader.setMaterialControllerTeam("LYS");
        List<RequestLineTransformerDTO> requestLineTransformerDTOs = requestHeaderTransformerDTO.getRequestLineTransformerDTOs();
        if (requestLineTransformerDTOs != null && !requestLineTransformerDTOs.isEmpty()) {
            FinanceHeaderTransformerDTO financeHeaderTransformerDTO = requestLineTransformerDTOs.get(0).getFinanceHeaderTransformerDTO();
            if (financeHeaderTransformerDTO != null) {
                materialHeader.setCompanyCode(financeHeaderTransformerDTO.getCompanyCode());
            }
        }
        return materialHeader;
    }

    private ChangeId createChangeId(RequestGatewayDTO requestGatewayDTO) {

        ChangeIdTransformerDTO changeIdTransformerDTO = requestGatewayDTO.getChangeIdTransformerDto();

        ChangeId changeId = new ChangeId();
        changeId.setChangeTechId(changeIdTransformerDTO.getChangeTechId());
        changeId.setMtrlRequestVersion(changeIdTransformerDTO.getChangeId());
        if (StringUtils.isNotBlank(changeIdTransformerDTO.getType())) {
            changeId.setType(ChangeType.valueOf(changeIdTransformerDTO.getType()));
        }
        changeId.setTitle(changeIdTransformerDTO.getTitle());
        changeId.setProcureNotes(changeIdTransformerDTO.getProcureNotes());
        changeId.setPriority(changeIdTransformerDTO.getPriority());
        changeId.setCrId(changeIdTransformerDTO.getMaterialRequestChangeAddId());
        changeId.setProcureRequestId(changeIdTransformerDTO.getProcureRequestId());
        changeId.setProcureMessageId(changeIdTransformerDTO.getProcureMessageId());
        changeId.setReceivedDate(DateUtil.getCurrentUTCDateTime());
        changeId.setStatus(ChangeIdStatus.START);
        return changeId;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','IT_SUPPORT')")
    public PageObject findAllChangeIds(PageObject pageObject, String assignedMaterialController, String assignedMaterialControllerTeam)
            throws GloriaApplicationException {
        return requestHeaderRepository.findAllChangeId(pageObject, assignedMaterialController, assignedMaterialControllerTeam,
                                                       userServices.getUserCompanyCodeCodes(assignedMaterialController, assignedMaterialControllerTeam,
                                                                                            TeamType.MATERIAL_CONTROL.name()));
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','IT_SUPPORT')")
    public List<ChangeId> findChangeIdByStateAndProcureLineId(long procurelineId, ChangeIdStatus... changeIdStatus) {
        return requestHeaderRepository.findChangeId(procurelineId, changeIdStatus);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','IT_SUPPORT')")
    public ChangeId findChangeIdById(long changeIdId) {
        return requestHeaderRepository.findChangeIdByOid(changeIdId);
    }

    @Override
    public List<MaterialHeader> findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(String requestType, String referenceId, String buildId,
            String outboundLocationId, String changeTechId) {
        return requestHeaderRepository.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(requestType, referenceId, buildId, outboundLocationId,
                                                                                                      changeTechId);
    }

    @Override
    public List<MaterialHeader> findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(String referenceId, String buildId, String outboundLocationId) {
        return requestHeaderRepository.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(referenceId, buildId, outboundLocationId);
    }

    @Override
    public List<MaterialHeader> findMaterialHeaderByMtrlRequestId(String mtrlRequestId, String referenceId) {
        return requestHeaderRepository.findMaterialHeaderByMtrlRequestId(mtrlRequestId, referenceId);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','IT_SUPPORT')")
    public FileToExportDTO exportOnBuildSite(List<Long> procureLineIds) throws GloriaApplicationException {
        ExportBuildSite exportBuildSite = new ExportBuildSite(ProcurementHelper.transformEntityToDTO(procureLineRepository.findByIds(procureLineIds)));
        return exportBuildSite.export();
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL')")
    public void acceptOrRejectChangeId(String action, long changeIdId, String userId, List<MaterialDTO> materialDTOs) throws GloriaApplicationException {
        ChangeId changeId = requestHeaderRepository.findChangeIdByOid(changeIdId);
        ChangeIdStatus changeIdStatus = changeId.getStatus();
        if ("accept".equalsIgnoreCase(action)) {
            changeIdStatus.accept(changeId, requestHeaderRepository, this, procureLineRepository, materialProcureResponse, userId, materialDTOs,
                                  orderRepository, userServices, traceabilityRepository);
        } else if ("reject".equalsIgnoreCase(action)) {
            UserDTO userDTO = userServices.getUser(userId);
            changeIdStatus.reject(changeId, requestHeaderRepository, materialProcureResponse, userId, procureLineRepository, traceabilityRepository, userDTO);
        }
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','IT_SUPPORT')")
    public List<Material> getMaterialsByChangeId(long changeIdId) {
        List<Material> materialOwners = new ArrayList<Material>();
        List<Material> materials = requestHeaderRepository.getMaterialsByChangeId(changeIdId);
        if (!materials.isEmpty()) {
            for (Material material : materials) {
                ChangeId changeId = findChangeIdById(changeIdId);
                String addOrRemoveMarkOnMaterialForAChange = evaluateAddRemoveMarkOnMaterialForAChange(material, changeId);
                for (MaterialLine materialLine : material.getMaterialLine()) {
                    if (!materialLine.getStatus().equals(MaterialLineStatus.REMOVED_DB) && !materialOwners.contains(material)) {
                        material.setMark(addOrRemoveMarkOnMaterialForAChange);
                        material.setChangeAction(material.getStatus().getChangeAction(material));
                        materialOwners.add(material);
                    }
                }
            }
        }
        return materialOwners;
    }

    public String evaluateAddRemoveMarkOnMaterialForAChange(Material material, ChangeId changeId) {
        MaterialStatus materialStatus = evalMaterialStatus(material, changeId);
        if (materialStatus == MaterialStatus.REMOVE_MARKED || materialStatus == MaterialStatus.REMOVED) {
            return GloriaParams.MARK_REQUESTLINE_CHANGE_REMOVED;
        } else if (materialStatus == MaterialStatus.ADD_NOT_ACCEPTED || materialStatus == MaterialStatus.ADDED) {
            return GloriaParams.MARK_REQUESTLINE_CHANGE_ADDED;
        }
        return "";
    }

    private MaterialStatus evalMaterialStatus(Material material, ChangeId changeId) {
        if (changeId.getStatus() == ChangeIdStatus.REJECTED || changeId.getStatus() == ChangeIdStatus.CANCEL_REJECTED) {
            return material.getRejectChangeStatus();
        } else if (material.getAdd() != null && changeId.getChangeIdOid() == material.getAdd().getChangeIdOid()) {
            return MaterialStatus.ADDED;
        } else if (material.getRemove() != null && changeId.getChangeIdOid() == material.getRemove().getChangeIdOid()) {
            return MaterialStatus.REMOVED;
        }
        return material.getStatus();
    }

    public List<Material> findAllMaterials() {
        return requestHeaderRepository.findAllMaterials();
    }

    @Override
    public void addPartNumber(PartNumber partNumber) {
        partNumberRepo.addPartNumber(partNumber);
    }

    @Override
    public void addPartAlias(PartAlias partAlias) {
        partNumberRepo.addPartAlias(partAlias);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','IT_SUPPORT')")
    public List<PartAlias> getPartAliases(String volvoPartNo) {
        return partNumberRepo.getPartAliases(volvoPartNo);
    }

    @Override
    public PartNumber findVolvoPartWithAliasByPartNumber(String partNumber) {
        return partNumberRepo.findVolvoPartWithAliasByPartNumber(partNumber);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE')")
    public List<Material> findMaterialByProcureLineId(Long procureLineOid) throws GloriaApplicationException {
        List<Material> materials = new ArrayList<Material>();
        ProcureLine procureLine = procureLineRepository.findProcureLineById(procureLineOid);
        for (Material material : procureLine.getMaterials()) {
            if (material.getMaterialType() != MaterialType.ADDITIONAL && material.getMaterialType() != MaterialType.USAGE_REPLACED) {
                materials.add(material);
            }
        }
        return materials;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE', 'PROCURE-INTERNAL')")
    public List<Material> groupMaterials(List<MaterialHeaderGroupingDTO> materialHeaderGroupingDTOs, String pPartNo, String pPartVersion, String pPartName,
            String pPartModification, String userId) throws GloriaApplicationException {
        List<Material> materials = new ArrayList<Material>();
        boolean isGroupTypeMod2 = false;
        List<MaterialGroupDTO> materialGroupDTOs = new ArrayList<MaterialGroupDTO>();
        if (materialHeaderGroupingDTOs != null && !materialHeaderGroupingDTOs.isEmpty()) {
            for (MaterialHeaderGroupingDTO materialDto : materialHeaderGroupingDTOs) {
                List<Long> materialIds = GloriaFormateUtil.getValuesAsLong(materialDto.getMaterialIds());
                MaterialGroupDTO materialGroupDTO = new MaterialGroupDTO();
                materialGroupDTOs.add(materialGroupDTO);
                materialGroupDTO.setProcurementQty(materialDto.getProcurementQty());
                for (Long materialId : materialIds) {
                    Material material = requestHeaderRepository.findMaterialById(materialId);
                    material.setAddedAfter(false);
                    materials.add(material);
                    materialGroupDTO.getMaterials().add(material);
                }
            }
        }

        if (!isNotModifiedEarlier(materials)) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_GROUPING_CRITERIA,
                                                 "Manual Grouping not possible. The selected Procure Request Lines doesn't have a matching criteria");
        }

        isGroupTypeMod2 = findGroupType(materials);
        ProcureGroupHelper groupHelper = null;
        if (isGroupTypeMod2) {
            groupHelper = new ProcureGroupHelper(materialGroupDTOs, GROUP_TYPE_SAME_TOBJ_DIFF_PART);
        } else {
            groupHelper = new ProcureGroupHelper(materialGroupDTOs, GROUP_TYPE_DIFF_TOBJ_SAME_PART);
        }
        if (!groupHelper.isValidForManualGrouping()) {
            LOGGER.error("Manual Grouping not possible.Selected request lines doesnt have valid grouping criteria");
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_GROUPING_CRITERIA,
                                                 "Manual Grouping not possible. The selected Procure Request Lines doesn't have a matching criteria");
        }

        return groupHelper.groupLines(pPartNo, pPartVersion, pPartName, pPartModification, this, procureLineRepository, materialGroupDTOs, userId);
    }

    public List<Material> associateProcureLines(List<Material> materials, ProcureLine newProcureLineToAssociate, String groupingType,
            List<MaterialGroupDTO> materialGroupDTOs, long modificationId, String userId) throws GloriaApplicationException {

        List<Material> updatedMaterials = new ArrayList<Material>();
        UserDTO userDTO = null;
        if (!StringUtils.isEmpty(userId)) {
            userDTO = userServices.getUser(userId);
        }
        if (materials != null && !materials.isEmpty()) {
            Map<String, Long> materialHeaderIdMap = new HashMap<String, Long>();
            for (Material material : materials) {

                ProcureLine currentAssociatedProcureLine = procureLineRepository.findProcureLineById(material.getProcureLine().getProcureLineOid());
                currentAssociatedProcureLine.getMaterials().remove(material);

                procureLineRepository.save(currentAssociatedProcureLine);

                List<Material> currentAssociatedMaterials = currentAssociatedProcureLine.getMaterials();

                if (currentAssociatedMaterials == null || currentAssociatedMaterials.isEmpty()) {
                    procureLineRepository.delete(currentAssociatedProcureLine);
                } else {
                    ProcureGroupHelper.updateIdSetsOnProcureLine(currentAssociatedProcureLine, currentAssociatedProcureLine.getMaterials());
                }

                material.setProcureLine(newProcureLineToAssociate);
                Material newMaterial = null;
                if (groupingType.equals(GROUP_TYPE_SAME_TOBJ_DIFF_PART)) {
                    String referenceId = material.getMaterialHeader().getReferenceId();
                    long procurementQty = getProcurementQuantity(materialGroupDTOs, material);
                    material.setModificationType(ModificationType.ASSEMBLE);
                    if (!materialHeaderIdMap.containsKey(referenceId)) {
                        newMaterial = createNewModifiedMaterial(material, newProcureLineToAssociate, procurementQty, modificationId, ModificationType.ASSEMBLE,
                                                                userDTO);
                        materialHeaderIdMap.put(referenceId, newMaterial.getMaterialOID());
                        material.setReplacedByOid(newMaterial.getMaterialOID());
                        updatedMaterials.add(newMaterial);
                    } else {
                        material.setReplacedByOid(materialHeaderIdMap.get(referenceId));
                    }
                } else {
                    newMaterial = createNewModifiedMaterial(material, newProcureLineToAssociate, null, modificationId, ModificationType.REPLACE, userDTO);
                    material.setReplacedByOid(newMaterial.getMaterialOID());
                    material.setModificationType(ModificationType.REPLACE);
                    updatedMaterials.add(newMaterial);
                }

                material.setMaterialType(MaterialType.USAGE_REPLACED);
                material.setModificationId(modificationId);
                updatedMaterials.add(material);
            }

            newProcureLineToAssociate.getMaterials().addAll(updatedMaterials);
            ProcureGroupHelper.updateIdSetsOnProcureLine(newProcureLineToAssociate, materials);
            ProcureGroupHelper.updateProcureLineUsageQty(newProcureLineToAssociate);
            newProcureLineToAssociate.setFinanceHeader(materials.get(0).getFinanceHeader());
            newProcureLineToAssociate.setDfuObjectNumber(materials.get(0).getObjectNumber());
            newProcureLineToAssociate.setProcureType(ProcureType.INTERNAL);
            /*
             * @Comment: suggesting procureType based on any one of the materialHeader belonging to the modified procureLine as this can belong to different
             * materialHeaders.
             */
            setProcureLineProcureType(newProcureLineToAssociate, newProcureLineToAssociate.getMaterials());
            procureLineRepository.save(newProcureLineToAssociate);
        }
        return newProcureLineToAssociate.getMaterials();
    }

    private long getProcurementQuantity(List<MaterialGroupDTO> materialGroupDTOs, Material material) {
        long procurementQty = 0;
        for (MaterialGroupDTO materialGroupDTO : materialGroupDTOs) {
            for (Material mtrl : materialGroupDTO.getMaterials()) {
                if (material.getMaterialOID() == mtrl.getMaterialOID()) {
                    procurementQty = materialGroupDTO.getProcurementQty();
                }
            }
        }
        return procurementQty;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Material createNewModifiedMaterial(Material material, ProcureLine newProcureLineToAssociate, Long procurementQty, long modificationId,
            ModificationType modificationType, UserDTO user) throws GloriaApplicationException {
        Material newMaterial = new Material();
        newMaterial.setUnitOfMeasure(UNITPRICE_PCE);
        newMaterial.setPartAffiliation(newProcureLineToAssociate.getpPartAffiliation());
        newMaterial.setPartModification(newProcureLineToAssociate.getpPartModification());
        newMaterial.setPartName(newProcureLineToAssociate.getpPartName());
        newMaterial.setPartNumber(newProcureLineToAssociate.getpPartNumber());
        newMaterial.setPartVersion(newProcureLineToAssociate.getpPartVersion());
        newMaterial.setAdd(material.getAdd());
        newMaterial.setCharacteristics(material.getCharacteristics());
        newMaterial.setDemarcation(material.getDemarcation());
        newMaterial.setDesignResponsible(material.getDesignResponsible());
        newMaterial.setFinanceHeader(material.getFinanceHeader());
        newMaterial.setFunctionGroup(material.getFunctionGroup());
        newMaterial.setItemToVariantLinkId(material.getItemToVariantLinkId());
        newMaterial.setLinkFunctionGroup(material.getLinkFunctionGroup());
        newMaterial.setLinkFUnctionGroupSuffix(material.getLinkFUnctionGroupSuffix());
        newMaterial.setMailFormId(material.getMailFormId());
        newMaterial.setMaterialHeader(material.getMaterialHeader());
        newMaterial.setModificationId(modificationId);
        newMaterial.setModificationType(modificationType);
        newMaterial.setMtrlRequestVersionAccepted(material.getMtrlRequestVersionAccepted());
        newMaterial.setReceiver(material.getReceiver());

        MaterialLine newMaterialLine = new MaterialLine();
        if (procurementQty != null && procurementQty > 0) {
            newMaterialLine.setQuantity(procurementQty);
        } else {
            newMaterialLine.setQuantity(material.getQuantity());
        }

        newMaterialLine.setMaterialOwner(newMaterial);

        MaterialLineStatusHelper.updateMaterialLineStatus(newMaterialLine, MaterialLineStatus.WAIT_TO_PROCURE, "Wait to procure",
                                                          GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE, user, traceabilityRepository, false);
        newMaterialLine.setMaterial(newMaterial);
        newMaterial.getMaterialLine().add(newMaterialLine);
        newMaterial.setMaterialType(MaterialType.MODIFIED);
        newMaterial.setMigrated(material.isMigrated());
        newMaterial.setModularHarness(material.getModularHarness());
        newMaterial.setObjectNumber(material.getObjectNumber());
        newMaterial.setPartVersionOriginal(material.getPartVersionOriginal());
        newMaterial.setProcureLine(newProcureLineToAssociate);
        newMaterial.setProductClass(material.getProductClass());
        newMaterial.setRefAssemblyPartNo(material.getRefAssemblyPartNo());
        newMaterial.setRefAssemblyPartVersion(material.getRefAssemblyPartVersion());
        newMaterial.setRemove(material.getRemove());
        newMaterial.setRequiredStaDate(material.getRequiredStaDate());
        newMaterial.setStatus(material.getStatus());
        
        requestHeaderRepository.addMaterial(newMaterial);
        if (user != null) {
            MaterialLineStatusHelper.createTraceabilityLog(newMaterialLine, traceabilityRepository, "Modified", null, user.getId(), user.getUserName(), null);
        }
        
        return newMaterial;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE-INTERNAL', 'PROCURE','IT_SUPPORT')")
    public PageObject findMaterialsByProcureLineIds(String procureLineOids, PageObject pageObject) {
        List<Long> procureLineIds = GloriaFormateUtil.getValuesAsLong(procureLineOids);
        return requestHeaderRepository.findMaterialsByProcureLineIds(procureLineIds, pageObject);
    }

    private void doRevertProcurement(ProcureLine procureLine, UserDTO userDTO) throws GloriaApplicationException {
        if (procureLine != null && procureLine.getStatus().isRevertProcurementAllowed(procureLine)) {
            if (procureLine.isNeedIsChanged()) {
                throw new GloriaApplicationException(GloriaExceptionConstants.PROCURE_LINE_HAS_CHANGE,
                                                     "One of the procureLines selected has an unaccepted change.");
            }

            procureLine.getProcureType().revert(procureLine, requestHeaderRepository, procureLineRepository, orderRepository, traceabilityRepository, this,
                                                userDTO);
        }
    }

    public List<MaterialHeader> unassignMaterialController(List<MaterialHeaderDTO> materialHeaderDTOs, String userId, String userTeamForAssignedUser)
            throws GloriaApplicationException {
        List<MaterialHeader> materialHeaders = new ArrayList<MaterialHeader>();
        if (materialHeaderDTOs != null && !materialHeaderDTOs.isEmpty()) {
            for (MaterialHeaderDTO materialHeaderDTO : materialHeaderDTOs) {
                MaterialHeader materialHeader = requestHeaderRepository.findById(materialHeaderDTO.getId());
                if (materialHeaderDTO.getVersion() != materialHeader.getVersion()) {
                    throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET, DATA_OPTIMISTIC_LOCK_MESSAGE);
                }
                if (!materialHeader.getMaterialControllerUserId().equalsIgnoreCase(userId)) {
                    throw new GloriaApplicationException(GloriaExceptionConstants.CANNOT_UNASSIGN_DIFFERENT_USER,
                                                         "Logged in user does not match the assigned user for this Material.");
                }
                checkProcurementState(materialHeader);
                materialHeaders.add(unassignWithoutValidations(materialHeader));
                UserDTO loggedInUserDTO = userServices.getUser(userId);
                MaterialLineStatusHelper.traceAssignUnAssignReturn(null, loggedInUserDTO, userTeamForAssignedUser, "UnAssigned",
                                                                   materialHeader.getMaterials(), traceabilityRepository);
            }
        }
        return materialHeaders;
    }

    private MaterialHeader unassignWithoutValidations(MaterialHeader materialHeader) throws GloriaApplicationException {
        materialHeader.setMaterialControllerUserId(null);
        materialHeader.setMaterialControllerName(null);
        materialHeader.setMaterialControllerTeam(null);
        Map<Long, Long> procureLineIds = requestHeaderRepository.findProcureLineOidsMap(materialHeader.getMaterialHeaderOid());
        List<ProcureLine> procureLines = requestHeaderRepository.findProcureLinesByIds(new ArrayList<Long>(procureLineIds.keySet()));
        List<Material> listOfProcureLineMaterials = requestHeaderRepository.findMaterialsByProcureLineIds(new ArrayList<Long>(procureLineIds.keySet()));
        Map<Long, List<Material>> mapOfMaterials = new HashMap<Long, List<Material>>();
        for (Material m : listOfProcureLineMaterials) {
            List<Material> materialsToAdd = mapOfMaterials.get(m.getProcureLine().getProcureLineOid());
            if (materialsToAdd == null) {
                materialsToAdd = new ArrayList<Material>();
            }
            materialsToAdd.add(m);
            mapOfMaterials.put(m.getProcureLine().getProcureLineOid(), materialsToAdd);
        }
        // List<Material> materialsInProcureLines = requestHeaderRepository.findProcureLinesByIds(new ArrayList<Long>(procureLineIds.keySet()))
        for (ProcureLine procureLine : procureLines) {
            int numberOfMaterials = mapOfMaterials.get(procureLine.getProcureLineOid()).size();
            if (numberOfMaterials == 1) {
                // BULK REMOVE???
                procureLine.getStatus().remove(procureLine, procureLineRepository);
            } else {
                ProcureLineHelper.deAssociateMaterialFromProcureLinePerformance(procureLine, materialHeader.getMaterialHeaderOid(), requestHeaderRepository,
                                                                                mapOfMaterials.get(procureLine.getProcureLineOid()), procureLineRepository);
                // material is being removed so reset its AddedAfter status

            }
        }
        List<Material> materials = materialHeader.getMaterials();
        // the below leads to a single update for each material. If performance problem persists convert to
        // bulk update
        for (Material material : materials) {
            material.setAddedAfter(false);
            material.setProcureLine(null);
        }
        // bulk update of MaterialLines as this is better for performance
        requestHeaderRepository.updateMaterialLinesStatus(materialHeader.getMaterialHeaderOid(), MaterialLineStatus.WAIT_TO_PROCURE, MaterialLineStatus.CREATED);
        // lazy load this is needed in the transformer
        materialHeader.getMaterialHeaderVersions();
        return requestHeaderRepository.save(materialHeader);
    }

    @Override
    public void reassignMaterialController(List<MaterialHeaderDTO> materialHeaderDTOs, String loggedInUserId, String materialControllerToAssign,
            String userTeamForAssignedUser, String userTeamTypeForAssignedUser) throws GloriaApplicationException {
        if (materialHeaderDTOs != null && !materialHeaderDTOs.isEmpty()) {
            validateRequests(materialHeaderDTOs, userTeamForAssignedUser, userTeamTypeForAssignedUser);

            if (StringUtils.isEmpty(materialControllerToAssign)) {
                throw new GloriaApplicationException(GloriaExceptionConstants.CANNOT_UNASSIGN_DIFFERENT_USER, "User Id should not be null.");
            }
            UserDTO materialControllerToBeAssigned = userServices.getUser(materialControllerToAssign);
            UserDTO assigningMaterialController = userServices.getUser(loggedInUserId);

            for (MaterialHeaderDTO materialHeaderDTO : materialHeaderDTOs) {
                MaterialHeader materialHeader = requestHeaderRepository.findById(materialHeaderDTO.getId());
                reassignMaterialController(assigningMaterialController, materialControllerToBeAssigned, userTeamForAssignedUser, userTeamTypeForAssignedUser,
                                           materialHeader);
            }
        }
    }

    private void reassignMaterialController(UserDTO assigningMaterialController, UserDTO materialControllerToBeAssigned,
            String teamForMaterialControllerToBeAssigned, String userTeamTypeForAssignedUser, MaterialHeader materialHeader) throws GloriaApplicationException {

        String reassignProcureTeam = materialControllerToBeAssigned.getProcureTeam();
        String assigningProcureTeam = assigningMaterialController.getProcureTeam();
        if (StringUtils.isNotBlank(reassignProcureTeam) && StringUtils.isNotBlank(assigningProcureTeam)) {
            if (!Utils.hasSameItems(reassignProcureTeam, assigningProcureTeam)) {
                throw new GloriaApplicationException(GloriaExceptionConstants.CANNOT_REASSIGN_DIFFERENT_TEAM,
                                                     "Logged in user team does not match the assigned user team for this Material.");
            }
        }

        List<Material> materials = materialHeader.getMaterials();
        for (Material material : materials) {
            ProcureLine procureLine = material.getProcureLine();
            if (procureLine != null) {
                procureLine.getStatus().validateReassign();
                procureLine.setMaterialControllerId(materialControllerToBeAssigned.getId());
                procureLine.setMaterialControllerTeam(teamForMaterialControllerToBeAssigned);
                procureLine.setMaterialControllerName(materialControllerToBeAssigned.getUserName());
            }
        }

        materialHeader.setMaterialControllerUserId(materialControllerToBeAssigned.getId());
        materialHeader.setMaterialControllerTeam(teamForMaterialControllerToBeAssigned);
        materialHeader.setMaterialControllerName(materialControllerToBeAssigned.getUserName());
        requestHeaderRepository.save(materialHeader);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL')")
    public void cancelModification(long modificationId) throws GloriaApplicationException {
        List<ProcureLine> procureLines = requestHeaderRepository.findProcureLinesByModificationId(modificationId);
        for (ProcureLine procureLine : procureLines) {
            UserDTO userDTO = userServices.getUser(procureLine.getMaterialControllerId());
            procureLine.getStatus().cancelModification(procureLine.getMaterials(), requestHeaderRepository, this, procureLineRepository, procureLine, userDTO);
        }
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','IT_SUPPORT')")
    public List<MaterialDTO> findMaterialsByModificationId(long modificationId) {
        return procurementDtoTransformer.transformAsUsageReplacedMaterialDTOs(requestHeaderRepository.findMaterialsByModificationId(modificationId));
    }

    @Override
    public void addPurchaseOrganisation(PurchaseOrganisation purchaseOrganisation) {
        purchaseOrganisationRepo.save(purchaseOrganisation);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','IT_SUPPORT')")
    public List<PurchaseOrganisation> findAllPurchaseOrganizations() {
        return purchaseOrganisationRepo.findAll();
    }

    @Override
    public void validateOrderNoLength(ProcureLineDTO procureLineDTO) throws GloriaApplicationException {
        if (procureLineDTO.getOrderNo() != null && procureLineDTO.getOrderNo().length() > GloriaParams.MAX_INTERNAL_ORDER_NO_LENGTH) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_ORDERNO_LENGTH, "Order number exceceds 14 characters.");
        }
    }

    @Override
    public ChangeId findChangeIdByTechId(String changeTechId) {
        return requestHeaderRepository.findChangeIdByTechId(changeTechId);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE', 'PROCURE-INTERNAL','IT_SUPPORT')")
    public List<SiteDTO> getSparePartSites() {
        return commonServices.getAllsparePartSites();
    }

    @Override
    public List<ProcureLine> findProcureLinesByPartInformation(String partNumber, String partVersion, String partAffiliation, String partModification,
            ProcureLineStatus status) {
        return procureLineRepository.findProcureLinesByPartInformation(partNumber, partVersion, partAffiliation, partModification, status);
    }

    /**
     * 
     * set outboundLocationType == outboundLocationId == null when material is already part of accepted version.
     * 
     * {@inheritDoc}
     */
    @Override
    public Material updateMaterialWithCarryOverInfo(Material material, String outboundLocationType, String outboundLocationId) {
        if (material != null) {
            String outboundLocationTypeToMatch = null;
            String outboundLocationIdToMatch = null;

            CarryOver existingCarryOver = new CarryOver();

            String partNumber = material.getPartNumber();
            String partVersion = material.getPartVersion();
            String partAffiliation = material.getPartAffiliation();
            List<MaterialPartAlias> partAliasList = material.getPartAlias();
            outboundLocationTypeToMatch = evaluateOutboundLocationTypeForMaterial(outboundLocationType, material);
            outboundLocationIdToMatch = evaluateOutboundLocationIdForMaterial(outboundLocationId, material);

            if (CarryOverExist.EXIST == checkCarryOverExist(partNumber, partVersion, partAffiliation, outboundLocationTypeToMatch, partAliasList,
                                                            existingCarryOver)) {
                material.setCarryOverExist(true);
                material.setMatchedCarryOverOid(existingCarryOver.getCarryOverOid());
            }

            if (CarryOverExist.EXIST_AND_MATCH == checkCarryOverExistAndMatch(partNumber, partVersion, partAffiliation, outboundLocationTypeToMatch,
                                                                              outboundLocationIdToMatch, partAliasList, existingCarryOver)) {
                material.setCarryOverExistAndMatched(true);
                material.setMatchedCarryOverOid(existingCarryOver.getCarryOverOid());
            }
        }
        return material;
    }

    private String evaluateOutboundLocationIdForMaterial(String outboundLocationId, Material material) {
        if (!material.getMaterialType().isAdditional()) { // if not spare part
            if (!StringUtils.isEmpty(outboundLocationId)) {
                return outboundLocationId;
            }

            MaterialHeader materialHeader = material.getMaterialHeader();
            if (materialHeader.getAccepted() != null) {
                return materialHeader.getAccepted().getOutboundLocationId();
            }
        }
        return null;
    }

    private String evaluateOutboundLocationTypeForMaterial(String outboundLocationType, Material material) {
        if (!material.getMaterialType().isAdditional()) { // if not spare part
            if (!StringUtils.isEmpty(outboundLocationType)) {
                return outboundLocationType;
            }

            MaterialHeader materialHeader = material.getMaterialHeader();
            if (materialHeader.getAccepted() != null) {
                return materialHeader.getAccepted().getOutboundLocationType();
            }
        }
        return null;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','IT_SUPPORT')")
    public List<SupplierDTO> getProcureLineSuppliers(long procureLineId) throws GloriaApplicationException {
        ProcureLine procureLine = procureLineRepository.findById(procureLineId);
        return procurementDtoTransformer.transformAsSuppliersDTO(procureLine.getSuppliers());
    }

    @Override
    public void updateProcureLineSuppliers(long procureLineId) {
        ProcureLine procureLine = procureLineRepository.findById(procureLineId);
        List<Supplier> suppliers = procureLine.getSuppliers();
        if (procureLine != null && (suppliers == null || suppliers.isEmpty())) {
            String getpPartNumber = procureLine.getpPartNumber();
            String getpPartAffiliation = procureLine.getpPartAffiliation();
            List<Supplier> internalSuppliers = new ArrayList<Supplier>();
            internalSuppliers.addAll(SupplierType.INTERNAL_CARRY_OVER.getSuppliers(getpPartNumber, null, getpPartAffiliation, null, carryOverRepository,
                                                                                   commonServices));
            List<MaterialPartAlias> aliasList = procureLine.getMaterials().get(0).getPartAlias();
            for (MaterialPartAlias materialPartAlias : aliasList) {
                String gpsQualifier = null;
                PartAliasMapping pm = partAliasMappingRepo.getGpsQualifier(materialPartAlias.getKolaDomain().toString());
                if (pm != null) {
                    gpsQualifier = pm.getGpsQualifier();
                }
                internalSuppliers.addAll(SupplierType.INTERNAL_CARRY_OVER_ALIAS.getSuppliers(materialPartAlias.getPartNumber(), null, gpsQualifier, null,
                                                                                             carryOverRepository, commonServices));
            }
            for (Supplier internalSupplier : internalSuppliers) {
                internalSupplier.setProcureLine(procureLine);
            }
            procureLine.setSuppliers(internalSuppliers);
        }
        procureLineRepository.save(procureLine);
    }

    @Override
    public ProcureLine findProcureLineByRequisitionId(String requisitionId) {
        return procureLineRepository.findProcureLineByRequisitionId(requisitionId);
    }

    /**
     * the same handle changes for both TO and BuildStartDate.
     * 
     * @param materialLineDTO
     * @param loggedInUserId
     * @return
     * @throws GloriaApplicationException
     */
    @Override
    public MaterialLineDTO updateReferenceIdByMaterialLine(MaterialLineDTO materialLineDTO, String loggedInUserId) throws GloriaApplicationException {
        if (materialLineDTO != null && materialLineDTO.getId() > 0) {
            MaterialLine materialLine = requestHeaderRepository.findMaterialLineById(materialLineDTO.getId());
            if (materialLine != null) {
                UserDTO userDTO = userServices.getUser(loggedInUserId);
                Material material = materialLine.getMaterial();
                if (material != null && material.isMigrated()) {
                    MaterialHeader materialHeader = material.getMaterialHeader();
                    if (materialHeader != null) {
                        String existingReferenceId = materialHeader.getReferenceId();
                        String newReferenceId = materialLineDTO.getReferenceId();
                        if (!(handleEmpty(existingReferenceId).equals(handleEmpty(newReferenceId)))) {
                            materialHeader.setReferenceId(newReferenceId);
                            MaterialLineStatusHelper.createTraceabilityLog(materialLine,
                                                                           traceabilityRepository,
                                                                           "Updated Test Object",
                                                                           buildActionDetailOnChangesToMigratedMaterials(handleEmpty(existingReferenceId),
                                                                                                                         handleEmpty(newReferenceId)),
                                                                           userDTO.getId(), userDTO.getUserName(), null);
                        }

                        Date existingBuildStartDate = materialHeader.getAccepted().getOutboundStartDate();
                        Date newBuildStartDate = materialLineDTO.getOutboundStartDate();
                        if (!DateUtil.isSameDate(existingBuildStartDate, newBuildStartDate)) {
                            materialHeader.getAccepted().setOutboundStartDate(newBuildStartDate);
                            MaterialLineStatusHelper.createTraceabilityLog(materialLine,
                                                     traceabilityRepository, "Updated Build Start",
                                                     buildActionDetailOnChangesToMigratedMaterials(DateUtil.getDateWithoutTimeAsString(existingBuildStartDate),
                                                     DateUtil.getDateWithoutTimeAsString(newBuildStartDate)), userDTO.getId(), userDTO.getUserName(), null);
                        }
                        requestHeaderRepository.save(materialHeader);
                    }
                }
            }
        }
        return materialLineDTO;
    }

    private String buildActionDetailOnChangesToMigratedMaterials(String existingValue, String updatedValue) {
        StringBuffer actionDetailBuffer = new StringBuffer();
        if (StringUtils.isEmpty(updatedValue)) {
            actionDetailBuffer.append("Removed " + existingValue);
            return actionDetailBuffer.toString();
        }

        actionDetailBuffer.append("Updated");
        if (!StringUtils.isEmpty(existingValue)) {
            actionDetailBuffer.append(" from " + existingValue);
        }
        actionDetailBuffer.append(" to " + updatedValue);
        return actionDetailBuffer.toString();
    }
    
    private String handleEmpty(String value) {
        if (value != null && !value.isEmpty()) {
            return value.trim();
        }
        return "";
    }
   
    @Override
    @PreAuthorize("hasAnyRole('PROCURE-INTERNAL')")
    public FileToExportDTO exportToProcure(List<Long> procureLineIds) throws GloriaApplicationException {
        List<ProcureLine> procureLines = procureLineRepository.findAllProcureLinesByIds(procureLineIds);
        List<ProcureLineDTO> procureLineDTOs = ProcurementHelper.transformEntityToDTO(procureLines);
        ExportToProcure exportToProcure = new ExportToProcure(procureLineDTOs);
        return exportToProcure.export();
    }
    
    @Override
    public List<PurchaseOrganisation> getAllPurchaseOrganizations() {
        return purchaseOrganisationRepo.findAll();
    }   
    
    @Override
    public void cancelPickLists(List<RequestGroupDTO> requestGroupDTOs, String userId) throws GloriaApplicationException {
        if (requestGroupDTOs != null && !requestGroupDTOs.isEmpty()) {
            for (RequestGroupDTO requestGroupDTO : requestGroupDTOs) {
                cancelPickList(requestGroupDTO.getPickListId(), userId);
            }
        }   
    }
    
    @Override
    public void cancelPickList(PickListDTO pickListDTO, String userId) throws GloriaApplicationException {
        if (pickListDTO != null) {
            cancelPickList(pickListDTO.getId(), userId);
        }
    }

    @Override
    public void cancelRequestLists(List<RequestListDTO> requestListDTOs, String userId) throws GloriaApplicationException {
        if (requestListDTOs != null && !requestListDTOs.isEmpty()) {
            for (RequestListDTO requestListDTO : requestListDTOs) {
                cancelRequestList(requestListDTO.getId(), userId);
            }
        }
    }

    private void cancelPickList(long pickListId, String userId) throws GloriaApplicationException {
        PickList pickList = requestHeaderRepository.findPickListById(pickListId);
        if (pickList != null) {
            pickList.getStatus().cancel(pickList, userServices.getUser(userId), requestHeaderRepository, traceabilityRepository);
        }
    }

    private void cancelRequestList(long requestListId, String userId) throws GloriaApplicationException {
        RequestList requestList = requestHeaderRepository.findRequestListById(requestListId);
        if (requestList != null) {
            requestList.getStatus().cancel(requestList, userServices.getUser(userId), requestHeaderRepository, traceabilityRepository);
        }
    }
    
    @Override
    public void triggerPOMessageForSAP(String orderNo, String orderIdGps, String action) throws GloriaApplicationException {
        Order order = orderRepository.findOrderByOrderNo(orderNo);
        OrderSap orderSap = orderSapRepository.findOrderSapByUniqueExtOrder(orderIdGps);
        ProcessPurchaseOrderDTO processPurchaseOrderDTO = ProcureTypeHelper.createTransformProcessPurchaseOrderEntities(order, orderSap,
                                                                                                                        action, commonServices,
                                                                                                                        companyCodeRepository,
                                                                                                                        orderSapRepository);
        try {
            if (processPurchaseOrderDTO != null) {
                processPurchaseOrderSender.sendProcessPurchaseOrder(processPurchaseOrderDTO);
            }
        } catch (JAXBException e1) {
            throw new GloriaSystemException(e1, "process purchase order couldn't be sent.");
        }

    }
}
