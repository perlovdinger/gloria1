/*
 * Copyright 2014 Volvo Information Technology AB 
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.b.UserlogServices;
import com.volvo.gloria.common.c.UserlogActionType;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.CompanyCode;
import com.volvo.gloria.common.d.entities.Site;
import com.volvo.gloria.common.repositories.b.CompanyCodeRepository;
import com.volvo.gloria.common.repositories.b.SiteRepository;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.financeProxy.b.GoodsReceiptSender;
import com.volvo.gloria.financeProxy.b.ProcessPurchaseOrderSender;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.ProcurementDtoTransformer;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.DeliveryAddressType;
import com.volvo.gloria.procurematerial.c.MaterialLineActionType;
import com.volvo.gloria.procurematerial.c.ShipmentType;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteLineDTO;
import com.volvo.gloria.procurematerial.c.dto.DispatchNoteDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineQiDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderLineDTO;
import com.volvo.gloria.procurematerial.c.dto.PickListDTO;
import com.volvo.gloria.procurematerial.c.dto.RequestGroupDTO;
import com.volvo.gloria.procurematerial.c.dto.RequestListDTO;
import com.volvo.gloria.procurematerial.c.export.ExportProforma;
import com.volvo.gloria.procurematerial.c.export.ExportSupplier;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteSubLine;
import com.volvo.gloria.procurematerial.d.entities.DispatchNote;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.PickList;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.entities.action.requestlist.RequestListAction;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.InspectionStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusCounterType;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusHelper;
import com.volvo.gloria.procurematerial.d.entities.status.picklist.PickListStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestlist.RequestListStatus;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialLineStatusCounterRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderSapRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.util.MaterialTransformHelper;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaFormateUtil;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.ObjectJSON;
import com.volvo.gloria.util.b.PrintLabelServices;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.c.PrintLabelTemplate;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.tracing.c.GloriaTraceabilityConstants;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.c.Deviation;
import com.volvo.gloria.warehouse.c.ZoneType;
import com.volvo.gloria.warehouse.c.dto.BinlocationBalanceDTO;
import com.volvo.gloria.warehouse.d.entities.BinLocation;
import com.volvo.gloria.warehouse.d.entities.BinlocationBalance;
import com.volvo.gloria.warehouse.d.entities.Placement;
import com.volvo.gloria.warehouse.d.entities.Printer;
import com.volvo.gloria.warehouse.d.entities.StorageRoom;
import com.volvo.gloria.warehouse.d.entities.Warehouse;
import com.volvo.gloria.warehouse.d.entities.Zone;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Implementation class for MaterialService.
 */
@ContainerManaged(name = "materialService")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MaterialServicesBean implements MaterialServices {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialServicesBean.class);

    public static final int INDEX_NUMBER = 10;
    public static final int MAX_NUMBER = 9999;

    @Inject
    private MaterialHeaderRepository requestHeaderRepository;
    
    @Inject
    private MaterialLineStatusCounterRepository materialLineStatusCounterRepository;

    @Inject
    private DeliveryNoteRepository deliveryNoteRepo;

    @Inject
    private OrderSapRepository orderSapRepository;

    @Inject
    private ProcessPurchaseOrderSender processPurchaseOrderSender;

    @Inject
    private GoodsReceiptSender goodsReceiptSender;

    @Inject
    private UserServices userServices;

    @Inject
    private WarehouseServices warehouseServices;

    @Inject
    private PrintLabelServices printLabelServices;

    @Inject
    private CommonServices commonServices;

    @Inject
    private UserlogServices userlogServices;

    @Inject
    private CompanyCodeRepository companyCodeRepository;

    @Inject
    private OrderRepository orderRepository;

    @Inject
    private TraceabilityRepository traceabilityRepository;
    
    @Inject
    private ProcurementServices procurementServices;
    
    @Inject
    private ProcureLineRepository procureLineRepository;
    
    @Inject
    private ProcurementDtoTransformer procurementDtoTransformer;
    
    @Inject
    private SiteRepository siteRepo;
    
    @Override
    public MaterialLineStatusCounterRepository getMaterialLineStatusCounterRepository() {
        return this.materialLineStatusCounterRepository;
    }
    
    @Override
    public void addMaterialLine(MaterialLine materialLine) {
        requestHeaderRepository.addMaterialLine(materialLine);
    }

    @Override
    public void addMaterial(Material material) {
        requestHeaderRepository.addMaterial(material);
    }

    @Override
    public void addMaterials(List<Material> materialList) {
        for (Material material : materialList) {
            requestHeaderRepository.addMaterial(material);
        }
    }

    @Override
    public Material getMaterialWithMaterialLinesById(long materialOid) {
        return requestHeaderRepository.getMaterialWithMaterialLinesById(materialOid);
    }

    @Override
    public List<Material> getMaterialByRequisitionId(String requisitionId) {
        return requestHeaderRepository.findMaterialByRequisitionId(requisitionId);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL', 'WH_DEFAULT', 'VIEWER', 'REQUESTER_FOR_PULL', 'VIEWER_PRICE', 'DELIVERY', 'WH_QI','GR_ONLY','IT_SUPPORT')")
    public PageObject getMaterialLines(PageObject pageObject, String loggedInUser, String userId, String userTeam, String teamType, String expirationDate, 
            String expirationTo, String expirationFrom, Boolean allExpired)
            throws GloriaApplicationException {
        return requestHeaderRepository.getMaterialLines(pageObject, expirationDate, expirationTo, expirationFrom, allExpired);
    }
   
    @Override
    public PageObject getMaterialLinesForWarehouse(PageObject pageObject, String materialLineStatus, boolean calculateStockBalance, String userId,
            String whSiteId, Boolean suggestBinLocation, String partNo, String transportLabel, String partAffiliation, String partVersion,
            String partModification, String partName) throws GloriaApplicationException {
        userServices.validateAccess(userId, whSiteId);

        return requestHeaderRepository.getMaterialLinesForWarehouse(pageObject, materialLineStatus, calculateStockBalance, whSiteId, suggestBinLocation,
                                                                    partNo, transportLabel, userId, partAffiliation, partVersion, partModification, partName);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'WH_DEFAULT', 'PROCURE','PROCURE-INTERNAL')")
    public List<MaterialLine> updateMaterialLines(List<MaterialLineDTO> materialLineDTOs, String fromMaterialLineIds, String action, long scrapQty,
            String confirmationText, String userId, String whSiteId, String deliveryAddressType, String deliveryAddressId, String deliveryAddressName)
            throws GloriaApplicationException {
        if ("scrap".equalsIgnoreCase(action)) {
            return scrapMaterialLines(materialLineDTOs, scrapQty, confirmationText, userId);
        } else if ("release".equalsIgnoreCase(action)) {
            List<MaterialLineDTO> listofMateriallineDtos = new ArrayList<MaterialLineDTO>();
            List<Long> relesedMaterial = new ArrayList<Long>();
            for (MaterialLineDTO materialLineDto : materialLineDTOs) {
                if (!relesedMaterial.contains(materialLineDto.getMaterialId())) {
                    relesedMaterial.add(materialLineDto.getMaterialId());
                    listofMateriallineDtos.add(materialLineDto);
                }
            }
            return MaterialServicesHelper.releaseMaterialLines(listofMateriallineDtos, confirmationText, userId, this, userServices, userlogServices,
                                                               traceabilityRepository);
        } else if ("pick".equalsIgnoreCase(action)) {
            return MaterialServicesHelper.pickMaterialLines(materialLineDTOs, fromMaterialLineIds, action, userId, this);
        } else if ("pickShip".equalsIgnoreCase(action)) {
            List<MaterialLine> materialLines = MaterialServicesHelper.pickMaterialLines(materialLineDTOs, fromMaterialLineIds, "pick", userId, this);
            if (!materialLines.isEmpty()) {
                MaterialLine materialLine = materialLines.get(0);
                // this will actually update all the material lines in the request List
                materialLine.getStatus().shipOrTransfer(materialLine, this, userId);
            }
            return materialLines;
        } else {
            List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
            for (MaterialLineDTO materialLineDTO : materialLineDTOs) {
                materialLines.add(updateMaterialLine(materialLineDTO, fromMaterialLineIds, action, false, userId, whSiteId, deliveryAddressType,
                                                     deliveryAddressId, deliveryAddressName));
            }
            return materialLines;
        }
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','WH_DEFAULT')")
    public MaterialLine updateMaterialLine(MaterialLineDTO materialLineDTO, String fromMaterialLineIds, String action, boolean noReturn, String userId,
            String whSiteId, String deliveryAddressType, String deliveryAddressId, String deliveryAddressName) throws GloriaApplicationException {
        UserDTO userDTO = userServices.getUser(userId);

        warehouseServices.validatePart(materialLineDTO.getpPartName(), materialLineDTO.getpPartNumber());

        MaterialLineStatus materialLineStatus = MaterialLineStatus.valueOf(materialLineDTO.getStatus());
       
        if (StringUtils.isNotEmpty(action)) {
            switch (MaterialLineActionType.valueOf(StringUtils.upperCase(action))) {
            case REQUEST:
                return materialLineStatus.request(materialLineDTO, requestHeaderRepository, traceabilityRepository, this, userDTO, warehouseServices,
                                                  deliveryAddressType, deliveryAddressId, deliveryAddressName, commonServices);
            case STORE:
                return materialLineStatus.store(materialLineDTO, userDTO, requestHeaderRepository, this, traceabilityRepository, warehouseServices, whSiteId);
            case PICK:
                return materialLineStatus.pick(materialLineDTO, userDTO, requestHeaderRepository, traceabilityRepository, warehouseServices, this);
            case RELEASE:
                return materialLineStatus.release(materialLineDTO, procurementServices, requestHeaderRepository);
            case BORROW:
                return borrowMaterialLines(materialLineDTO.getId(), fromMaterialLineIds, noReturn, traceabilityRepository, userDTO);
            case PICKSHIP:
                MaterialLine materialLine = materialLineStatus.pick(materialLineDTO, userDTO, requestHeaderRepository, traceabilityRepository,
                                                                    warehouseServices, this);
                materialLine.getStatus().shipOrTransfer(materialLine, this, userId);
                return materialLine;
            default:
                break;
            }
        }
        return MaterialServicesHelper.updateMaterialLine(materialLineDTO, requestHeaderRepository, userDTO, traceabilityRepository);
    }


    @Override
    public void createPlacement(long binLocationOid, MaterialLine materialLine) throws GloriaApplicationException {
        BinLocation binLocation = warehouseServices.findBinLocationById(binLocationOid);
        if (binLocation == null) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_BIN_LOCATION, "The binlocation doesnt exist for id: " + binLocationOid);
        }

        createPlacement(binLocation, materialLine);
    }
    
    @Override
    public void createPlacement(BinLocation binLocation, MaterialLine materialLine) throws GloriaApplicationException {
        if (binLocation == null) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_BIN_LOCATION, "The binlocation doesnt exist");
        }
        Placement placement = new Placement();
        placement.setBinLocation(binLocation);
        Zone zone = binLocation.getZone();
        
        if (ZoneType.STORAGE.equals(zone.getType()) || ZoneType.TO_STORE.equals(zone.getType())) {
            BinlocationBalance binlocationBalance = MaterialServicesHelper.createOrUpdateBinlocationBalance(materialLine.getMaterial(),
                                                                                                            materialLine.getQuantity(), binLocation,
                                                                                                            warehouseServices);
            placement.setBinlocationBalance(binlocationBalance);
        }
        placement.setMaterialLineOID(materialLine.getMaterialLineOID());
        placement.setQuantity(materialLine.getQuantity());
        warehouseServices.addPlacement(placement);
        materialLine.setPlacementOID(placement.getPlacementOid());

        materialLine.setZoneType(zone.getType());
        materialLine.setZoneCode(zone.getCode());
        materialLine.setZoneName(zone.getName());
        
        StorageRoom storageRoom = zone.getStorageRoom();
        materialLine.setStorageRoomCode(storageRoom.getCode());
        materialLine.setStorageRoomName(storageRoom.getName());
        
        materialLine.setBinLocationCode(binLocation.getCode());
    }

    @Override
    public String getBinLocationCode(long binLocationOid) {
        BinLocation binLocation = warehouseServices.findBinLocationById(binLocationOid);
        if (binLocation == null) {
            return null;
        } else {
            return binLocation.getCode();
        }
    }

    @Override
    @PreAuthorize("hasAnyRole( 'WH_DEFAULT', 'VIEWER')")
    public MaterialLine findMaterialLineByIdWarehouse(Long materialLineId, boolean calculateStockBalance, String userId, String whSiteId)
            throws GloriaApplicationException {
        userServices.validateAccess(userId, whSiteId);

        List<String> whSiteIds = new ArrayList<String>();
        whSiteIds.add(whSiteId);
        return findMaterialLineByIdWarehouse(materialLineId, calculateStockBalance, userId, whSiteIds);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'WH_DEFAULT', 'VIEWER')")
    public MaterialLine findMaterialLineByIdWarehouse(Long materialLineId, boolean calculateStockBalance, String userId, List<String> whSiteIds)
            throws GloriaApplicationException {

        return requestHeaderRepository.findMaterialLineById(materialLineId, calculateStockBalance, whSiteIds);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE', 'REQUESTER_FOR_PULL', 'VIEWER_PRICE', 'DELIVERY', 'VIEWER', 'WH_DEFAULT', 'PROCURE-INTERNAL', 'WH_QI','IT_SUPPORT')")
    public MaterialLine findMaterialLineByIdProcurement(Long materialLineId, boolean calculateStockBalance, String userId) throws GloriaApplicationException {
        return requestHeaderRepository.findMaterialLineById(materialLineId, calculateStockBalance, null);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'DELIVERY', 'PROCURE','IT_SUPPORT')")
    public List<Material> getMaterials(Long orderlineId) {
        return updateMaterialsWithFinalWhSiteInfo(requestHeaderRepository.findMaterialByOrderLineId(orderlineId));
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','IT_SUPPORT')")
    public List<Material> findMaterialsByProcureLineIdForProcureDetails(long procureLineOID) throws GloriaApplicationException {
        return updateMaterialsWithFinalWhSiteInfo(requestHeaderRepository.findMaterialsByProcureLineId(procureLineOID));
    }

    private List<Material> updateMaterialsWithFinalWhSiteInfo(List<Material> materials) {
        List<Material> materialsWithoutUsageReplacedAndAdditional = new ArrayList<Material>();
        Map<String, String> warehouses = new HashMap<String, String>();
        if (materials != null && !materials.isEmpty()) {
            // fetch warehouse site-name information
            for (Warehouse warehouse : warehouseServices.getWarehouseList()) {
                Site site = commonServices.getSiteBySiteId(warehouse.getSiteId());
                if (site != null) {
                    warehouses.put(warehouse.getSiteId(), site.getSiteName());
                }
            }
            for (Material material : materials) {
                if (!material.getMaterialType().equals(MaterialType.USAGE_REPLACED)) {
                    List<String> fhSiteNames = new ArrayList<String>();
                    for (MaterialLine materialLine : material.getMaterialLine()) {
                        String siteName = warehouses.get(materialLine.getFinalWhSiteId());
                        if (!fhSiteNames.contains(siteName)) {
                            fhSiteNames.add(siteName);
                        }
                    }
                    material.setFinalWhSiteNames(fhSiteNames.toArray(new String[fhSiteNames.size()]));
                    materialsWithoutUsageReplacedAndAdditional.add(material);
                }
            }
        }
        return materialsWithoutUsageReplacedAndAdditional;
    }
    
    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','WH_SETUP')")
    public void print(long partId, int labelCopies) {

    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE', 'REQUESTER_FOR_PULL','WH_DEFAULT','IT_SUPPORT')")
    public PageObject getRequestGroup(PageObject pageObject, String userId, String whSiteId) throws GloriaApplicationException {
        userServices.validateAccess(userId, whSiteId);
        return requestHeaderRepository.getRequestGroup(pageObject, whSiteId, userId);
    }

    @Override
    public List<RequestGroup> getAllRequestGroups() {
        return requestHeaderRepository.getAllRequestGroups();
    }

    @Override
    public void saveRequestList(RequestList requestList) {
        requestHeaderRepository.save(requestList);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','WH_DEFAULT','IT_SUPPORT')")
    public PickList findPickListById(long pickListId) {
        PickList pickList = requestHeaderRepository.findPickListById(pickListId);
        if (pickList != null) {            
            pickList.setShipSkippable(hasAllItemsAreRequestestedAndPickedTogether(pickListId));
        }
        return pickList;
    }

    private boolean hasAllItemsAreRequestestedAndPickedTogether(long pickListId) {
        List<RequestList> requestLists = requestHeaderRepository.findRequestListByPicklist(pickListId);
        if (hasAllItemsRequestedTogether(requestLists)) {
            // when requested together, there will be only one request list
            List<RequestGroup> requestGroups = requestLists.get(0).getRequestGroups();
            final long anyPickListOID = pickListId;
            return GloriaFormateUtil.hasSameItems(requestGroups, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    RequestGroup requestGroup = (RequestGroup) object;
                    if (requestGroup.getPickList() != null && requestGroup.getPickList().getPickListOid() == anyPickListOID) {
                        return true;
                    }
                    return false;
                }
            });
        }
        return false;
    }

    private boolean hasAllItemsRequestedTogether(List<RequestList> requestLists) {
        return (requestLists.size() == 1);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','WH_DEFAULT','IT_SUPPORT')")
    public PageObject getMaterialLines(PageObject pageObject, long pickListId, String status, boolean suggestBinLocation) throws GloriaApplicationException {
        return requestHeaderRepository.findMaterialLineBypickListIdAndStatus(pageObject, pickListId, status, suggestBinLocation);
    }

    @Override
    public PickList savePickList(PickList instanceToSave) {

        return requestHeaderRepository.savePickList(instanceToSave);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','WH_DEFAULT')")
    public List<RequestGroup> updateRequestGroups(List<RequestGroupDTO> requestGroupDTOs, boolean createPickList, String userId) 
            throws GloriaApplicationException {
        List<RequestGroup> requestGroups = new ArrayList<RequestGroup>();
        UserDTO userDTO = userServices.getUser(userId);

        if (requestGroupDTOs != null && !requestGroupDTOs.isEmpty()) {
            for (RequestGroupDTO requestGroupDTO : requestGroupDTOs) {
                Long requestGroupId = requestGroupDTO.getId();
                RequestGroup requestGroup = requestHeaderRepository.findRequestGroupById(requestGroupId);

                if (requestGroup == null) {
                    LOGGER.error("No Request Group objects exists for id : " + requestGroupId);
                    throw new GloriaSystemException("This operation cannot be performed. No material line objects exists for id : " + requestGroupId,
                                                    GloriaExceptionConstants.INVALID_DATASET_OID);
                }

                if (requestGroupDTO.getVersion() != requestGroup.getVersion()) {
                    throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                         "This operation cannot be performed since the information seen in the page has already been updated.");
                }
                requestGroups.add(requestGroup);
            }
            if (createPickList) {
                PickList pickList = MaterialServicesHelper.createPickList(requestGroups, this, userDTO, traceabilityRepository);
                for (RequestGroup requestGroup : requestGroups) {
                    for (MaterialLine materialLine : requestGroup.getMaterialLines()) {
                        if (!materialLine.getStatus().equals(MaterialLineStatus.MISSING)) {
                            materialLine.getStatus().setPickList(pickList, materialLine, requestHeaderRepository);
                        }
                    }
                    requestGroup.setPickList(pickList);
                    requestHeaderRepository.saveRequestGroup(requestGroup);
                }
            }
        }
        return requestGroups;
    }

    @Override
    public List<MaterialLine> findMaterialLines(List<Long> materialLineOids) {
        return requestHeaderRepository.findMaterialLines(materialLineOids);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE', 'REQUESTER_FOR_PULL','WH_DEFAULT','IT_SUPPORT')")
    public RequestList findRequestListById(long requestListOid) {
        RequestList requestList = requestHeaderRepository.findRequestListById(requestListOid);
        Site deliverySite = commonServices.getSiteBySiteId(requestList.getDeliveryAddressId());
        if (deliverySite != null) {
            requestList.setDeliveryAddress(deliverySite.getAddress());
        }

        Site shipFromSite = commonServices.getSiteBySiteId(requestList.getWhSiteId());
        if (shipFromSite != null) {
            requestList.setWhSiteAddress(shipFromSite.getAddress());
        }

        List<PickList> pickListsForReqList = requestHeaderRepository.findPickListByRequestListId(requestListOid);
        if ((pickListsForReqList == null || pickListsForReqList.isEmpty()) 
                                                    && !hasDirectSendMaterialsReceived(requestList.getRequestGroups())) {
            requestList.setCancelAllowed(true);
        }
        return requestList;
    }
    
    // in case of direct send 1 Requestlist -- Request Group -- 1 Material line
    private boolean hasDirectSendMaterialsReceived(List<RequestGroup> requestGroups) {
        if (requestGroups != null && !requestGroups.isEmpty() && requestGroups.size() == 1) {
            List<MaterialLine> materialLines = requestGroups.get(0).getMaterialLines();
            if (materialLines != null && !materialLines.isEmpty() && materialLines.size() == 1) {
                MaterialLine materialLine = materialLines.get(0);
                return (!materialLine.getStatus().isReceiveble() && (materialLine.getDirectSend() == DirectSendType.YES_REQUESTED 
                        || materialLine.getDirectSend() == DirectSendType.YES_TRANSFER));
            }
        }
        return false;
    }

    @Override
    public List<RequestList> findRequestListByUserId(String requesterUserId, String status) {
        return requestHeaderRepository.findRequestListByUserId(requesterUserId, status);
    }

    @Override
    @PreAuthorize("hasAnyRole('PROCURE','WH_DEFAULT')")
    public RequestList updateRequestList(RequestListDTO requestListDTO, String action, String userId) throws GloriaApplicationException {
        Long requestListId = requestListDTO.getId();
        RequestList requestList = requestHeaderRepository.findRequestListById(requestListId);
        UserDTO user = userServices.getUser(userId);
        if (requestList == null) {
            LOGGER.error("No Request List objects exists for id : " + requestListId);
            throw new GloriaSystemException("This operation cannot be performed. No request line objects exists for id : " + requestListId,
                                            GloriaExceptionConstants.INVALID_DATASET_OID);
        }
        if (requestListDTO.getVersion() != requestList.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                 "This operation cannot be performed since the information seen in the page has already been updated.");
        }

        MaterialServicesHelper.shipRequestList(requestListDTO, requestList, action, user, traceabilityRepository, warehouseServices, requestHeaderRepository);
        DispatchNote updateDispatchNote = requestList.getDispatchNote();
        if (updateDispatchNote != null) {
            updateDispatchNote.setCarrier(requestListDTO.getCarrier());
            updateDispatchNote.setTrackingNo(requestListDTO.getTrackingNo());
            requestList.setDispatchNote(updateDispatchNote);
        }
        return requestHeaderRepository.save(requestList);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL')")
    public List<MaterialLine> getMaterialLinesForRequestList(long requestListOid) {
        return requestHeaderRepository.getMaterialLinesByRequestListId(requestListOid);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','WH_DEFAULT','IT_SUPPORT')")
    public PageObject findPickListByStatus(PageObject pageObject, String whSiteId, String materialLineStatus, String userId) {
        pageObject = requestHeaderRepository.findPickListByStatus(pageObject, whSiteId, materialLineStatus, userId);
        List<PageResults> pageResults = pageObject.getGridContents();
        if (pageResults != null && pageResults.size() > 0) {
            for (PageResults pageResult : pageResults) {
                PickListDTO pickListDTO = (PickListDTO) pageResult;
                pickListDTO.setShipSkippable(hasAllItemsAreRequestestedAndPickedTogether(pickListDTO.getId()));
            }
        }
        return pageObject;
    }

    @Deprecated
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL')")
    public PickList updatePickListByAction(PickListDTO pickListDTO, String action) throws GloriaApplicationException {
        Long pickListId = pickListDTO.getId();
        PickList pickList = requestHeaderRepository.findPickListById(pickListDTO.getId());

        if (pickList == null) {
            LOGGER.error("No Material line objects exists for id : " + pickListId);
            throw new GloriaSystemException("This operation cannot be performed. No material line objects exists for id : " + pickListId,
                                            GloriaExceptionConstants.INVALID_DATASET_OID);
        }

        if (pickList.getVersion() != pickList.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                 "This operation cannot be performed since the information seen in the page has already been updated.");
        }

        if ("pick".equalsIgnoreCase(action)) {
            for (MaterialLine materialLine : pickList.getMaterialLines()) {
                if (materialLine.getStatus() != MaterialLineStatus.READY_TO_SHIP && materialLine.getStatus() != MaterialLineStatus.MISSING) {
                    placeIntoZone(materialLine, ZoneType.SHIPPING);
                    MaterialLineStatusHelper.updateMaterialLineStatus(materialLine, MaterialLineStatus.READY_TO_SHIP, "Picked",
                                                                      GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE, null, traceabilityRepository, false);
                    requestHeaderRepository.updateMaterialLine(materialLine);
                    materialLineStatusCounterRepository.createAndSave(materialLine, MaterialLineStatusCounterType.PICKS); 
                    MaterialServicesHelper.reduceBinlocationBalance(materialLine, warehouseServices);
                }
            }
            pickList.setStatus(PickListStatus.PICKED);
            pickList = requestHeaderRepository.updatePickList(pickList);
            // lazy initialization
            pickList.getRequestGroups();
            
            List<RequestGroup> requestGroups = pickList.getRequestGroups();
            if (requestGroups != null && !requestGroups.isEmpty()) {
                checkAndUpdateRequestListStatusAsPickCompleted(requestGroups.get(0).getRequestList());
            }
        }
        return pickList;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE', 'REQUESTER_FOR_PULL','WH_DEFAULT','IT_SUPPORT')")
    public DispatchNote findDispatchNoteById(long dispatchNoteId) {
        return requestHeaderRepository.findDispatchNoteById(dispatchNoteId);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE', 'REQUESTER_FOR_PULL','WH_DEFAULT')")
    public void deleteDispatchNote(long dispatchNoteId) {
        DispatchNote dispatchNote = requestHeaderRepository.findDispatchNoteById(dispatchNoteId);
        if (dispatchNote != null) {
            RequestList requestList = dispatchNote.getRequestList();
            requestList.setStatus(RequestListStatus.PICK_COMPLETED);
            requestHeaderRepository.deleteDispatchNote(dispatchNoteId);
            requestHeaderRepository.save(requestList);
        }
    }

    @Override
    public DispatchNote createDispatchNote(long requestListId, DispatchNoteDTO dispatchNoteDTO) {
        RequestList requestList = findRequestListById(requestListId);
        DispatchNote dispatchNote = new DispatchNote();
        dispatchNote.setDispatchNoteNo(dispatchNoteDTO.getDispatchNoteNo());
        dispatchNote.setDispatchNoteDate(dispatchNoteDTO.getDispatchNoteDate());
        /*  */
        dispatchNote.setDeliveryDate(dispatchNoteDTO.getDeliveryDate());
        dispatchNote.setTransportMode(dispatchNoteDTO.getTransportMode());
        requestList.setStatus(RequestListStatus.valueOf(dispatchNoteDTO.getRequestListStatus()));
        dispatchNote.setRequestList(requestList);
        dispatchNote.setDispatchNoteNo(dispatchNoteDTO.getDispatchNoteNo());
        dispatchNote.setWeight(dispatchNoteDTO.getWeight());
        dispatchNote.setHeight(dispatchNoteDTO.getHeight());
        dispatchNote.setCarrier(dispatchNoteDTO.getCarrier());
        dispatchNote.setTrackingNo(dispatchNoteDTO.getTrackingNo());
        dispatchNote.setNote(dispatchNoteDTO.getNote());
        requestList.setShipVia(dispatchNoteDTO.getShipVia());
        return requestHeaderRepository.saveDispatchNote(dispatchNote);
    }

    @Override
    public DispatchNote updateDispatchNote(DispatchNoteDTO dispatchNoteDTO, String status, String userId) throws GloriaApplicationException {
        UserDTO user = userServices.getUser(userId);
        DispatchNote dispatchNote = requestHeaderRepository.findDispatchNoteById(dispatchNoteDTO.getId());
        dispatchNote.setDeliveryDate(dispatchNoteDTO.getDeliveryDate());
        // this assumes that the delivery date is the date on which the dispatch note is updated/created
        if (dispatchNoteDTO.getDeliveryDate() == null) {
            dispatchNote.setDeliveryDate(DateUtil.getCurrentUTCDateTime());
        }
        dispatchNote.setTransportMode(dispatchNoteDTO.getTransportMode());
        dispatchNote.setWeight(dispatchNoteDTO.getWeight());
        dispatchNote.setHeight(dispatchNoteDTO.getHeight());
        dispatchNote.setCarrier(dispatchNoteDTO.getCarrier());
        dispatchNote.setTrackingNo(dispatchNoteDTO.getTrackingNo());
        dispatchNote.setNote(dispatchNoteDTO.getNote());
        RequestList requestList = dispatchNote.getRequestList();
        if (status != null && "markAsShipped".equalsIgnoreCase(status)) {
            if (dispatchNoteDTO.getDeliveryDate() == null) {
                // if it is marked as shipped and deliverydate is null then set delivery date to what is set
                dispatchNote.setDeliveryDate(DateUtil.getCurrentUTCDateTime());
            }
            if (requestList != null) {
                requestList.setStatus(RequestListStatus.SHIPPED);
                MaterialServicesHelper.updateMaterialLineForRequestList(requestList, user, traceabilityRepository, warehouseServices, requestHeaderRepository);
                requestList.setShipVia(dispatchNoteDTO.getShipVia());
            }
            return requestHeaderRepository.saveDispatchNote(dispatchNote);
        } else {
            if (requestList != null) {
                requestList.setShipVia(dispatchNoteDTO.getShipVia());
            }
            return requestHeaderRepository.saveDispatchNote(dispatchNote);
        }
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','REQUESTER_FOR_PULL', 'DELIVERY', 'VIEWER', 'VIEWER_PRICE', 'WH_DEFAULT','IT_SUPPORT')")
    public PageObject getRequestLists(PageObject pageObject, String status, String whSiteId, String outBoundLocationId, String requesterId)
            throws GloriaApplicationException {
        return requestHeaderRepository.getRequestLists(pageObject, status, whSiteId, outBoundLocationId, requesterId);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE', 'REQUESTER_FOR_PULL','WH_DEFAULT')")
    public DispatchNote createDispatchNoteforRequestList(long requestListOid, DispatchNoteDTO dispatchNoteDTO, String action, String userId)
            throws GloriaApplicationException {
        UserDTO user = userServices.getUser(userId);
        DispatchNote dispatchNote = null;
        RequestList requestList = null;

        if (dispatchNoteDTO.getId() > 0) {
            dispatchNote = requestHeaderRepository.findDispatchNoteById(dispatchNoteDTO.getId());
            requestList = dispatchNote.getRequestList();
        } else {
            requestList = findRequestListById(requestListOid);
            dispatchNote = createNewDispatchNote();            
            dispatchNote.setDispatchNoteNo(ProcurementServicesHelper.getUniqueDispatchNoteNoString(dispatchNote.getDispatchNoteNoSequence()));

            if (requestList.getDeliveryAddressType() == DeliveryAddressType.WH_SITE) {
                requestList.setShipmentType(ShipmentType.TRANSFER);
            } else {
                requestList.setShipmentType(ShipmentType.SHIPMENT);
            }

            dispatchNote.setRequestList(requestList);            
            requestList.setDispatchNote(dispatchNote);
        }
        
        requestList.setShipVia(dispatchNoteDTO.getShipVia());
        dispatchNote.setDeliveryDate(dispatchNoteDTO.getDeliveryDate());
        // this assumes that the delivery date is the date on which the dispatch note is updated/created
        if (dispatchNoteDTO.getDeliveryDate() == null) {
            dispatchNote.setDeliveryDate(DateUtil.getCurrentUTCDateTime());
        }
        
        dispatchNote.setTransportMode(dispatchNoteDTO.getTransportMode());
        dispatchNote.setWeight(dispatchNoteDTO.getWeight());
        dispatchNote.setHeight(dispatchNoteDTO.getHeight());
        dispatchNote.setCarrier(dispatchNoteDTO.getCarrier());
        dispatchNote.setTrackingNo(dispatchNoteDTO.getTrackingNo());
        dispatchNote.setNote(dispatchNoteDTO.getNote());
        
        
        for (RequestGroup requestGroup : requestList.getRequestGroups()) {
            List<MaterialLine> materialLines = requestGroup.getMaterialLines();
            for (int idx = materialLines.size() - 1; idx >= 0; idx--) {
                MaterialLine materialLine = materialLines.get(idx);
                if (!materialLine.getStatus().equals(MaterialLineStatus.DEVIATED) && !materialLine.getStatus().equals(MaterialLineStatus.MISSING)) {
                    MaterialLineStatusHelper.createTraceabilityLog(materialLine, traceabilityRepository, "Dispatch Note Created", "Dispatch Note No : "
                            + dispatchNote.getDispatchNoteNo(), user.getId(), user.getUserName(), GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE);
                }
            }
        }
        if (action != null && "markAsShipped".equalsIgnoreCase(action)) {
            if (dispatchNoteDTO.getDeliveryDate() == null) {
                // if it is marked as shipped and deliverydate is null then set delivery date to what is set
                dispatchNote.setDeliveryDate(DateUtil.getCurrentUTCDateTime());
            }
            requestList.setStatus(RequestListStatus.SHIPPED);
            MaterialServicesHelper.updateMaterialLineForRequestList(requestList, user, traceabilityRepository, warehouseServices, requestHeaderRepository);
        } else {
            requestList.setStatus(RequestListStatus.READY_TO_SHIP);
        }
        requestHeaderRepository.saveDispatchNote(dispatchNote);
        requestHeaderRepository.updateRequestList(requestList);

        return dispatchNote;

    }

    private DispatchNote createNewDispatchNote() {
        DispatchNote dispatchNote;
        dispatchNote = new DispatchNote();
        dispatchNote.setDispatchNoteDate(DateUtil.getCurrentUTCDateTime());
        requestHeaderRepository.saveDispatchNote(dispatchNote);
        return requestHeaderRepository.findDispatchNoteById(dispatchNote.getDispatchNoteOID());
    }

    private List<MaterialLine> moveMaterialLines(List<MaterialLine> materialLines, long moveQuantity, String binLocationCode, UserDTO userDTO)
            throws GloriaApplicationException {
        List<MaterialLine> fetchedMaterialLines = new ArrayList<MaterialLine>();
        MaterialServicesHelper.doMoveMaterialLines(moveQuantity, binLocationCode, fetchedMaterialLines, materialLines, userDTO, warehouseServices,
                                                   requestHeaderRepository, traceabilityRepository, this);
        return fetchedMaterialLines;
    }

    @Override
    public List<MaterialLine> getMaterialLinesWithPartNumberaAndBinlocation(String partNumber, String binlocation, String userId)
            throws GloriaApplicationException {
        return requestHeaderRepository.getMaterialLinesWithPartNumberaAndBinlocation(partNumber, binlocation);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE', 'REQUESTER_FOR_PULL','WH_DEFAULT','IT_SUPPORT')")
    public List<RequestGroup> getRequestGroups(long requestlistId) {
        List<RequestGroup> requestGroups = requestHeaderRepository.findAllRequestGroupsByRequestListId(requestlistId);
        if (requestGroups != null && !requestGroups.isEmpty()) {
            for (RequestGroup requestGroup : requestGroups) {
                // lazy load
                requestGroup.getMaterialLines();
            }
        }
        return requestGroups;
    }

    @Override
    public RequestGroup findRequestGroupById(long requestGroupId) {
        return requestHeaderRepository.findRequestGroupById(requestGroupId);
    }

    @Override
    public PickList findPickListByCode(String picklistCode) {
        return requestHeaderRepository.findPickListByCode(picklistCode);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE', 'REQUESTER_FOR_PULL','WH_DEFAULT','IT_SUPPORT')")
    public List<RequestGroup> findRequestGroupsByDispatchNoteId(long dispatchNoteId) {
        List<RequestGroup> requestGroups = requestHeaderRepository.findRequestGroupsByDispatchNoteId(dispatchNoteId);
        if (requestGroups != null && !requestGroups.isEmpty()) {
            for (RequestGroup requestGroup : requestGroups) {
                // lazy load
                requestGroup.getMaterialLines();
            }
        }
        return requestGroups;
    }

    @Override
    public List<MaterialLine> findMaterialLinesByRequestGroupId(long requestGroupId) {
        return requestHeaderRepository.findMaterialLinesByRequestGroupId(requestGroupId);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'WH_DEFAULT')")
    public List<MaterialLine> scrapMaterialLines(List<MaterialLineDTO> materialLineDTOs, long scrapQuantity, String confirmationText, String userId)
            throws GloriaApplicationException {

        List<String> whSiteIds = userServices.getUserSiteIds(userId);
        List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
        UserDTO userDTO = userServices.getUser(userId);

        for (MaterialLineDTO materialLineDTO : materialLineDTOs) {
            materialLines.add(findMaterialLineByIdWarehouse(materialLineDTO.getId(), false, userId, whSiteIds));
            userlogServices.addUserLog(userDTO, UserlogActionType.SCRAPPART, MaterialServicesHelper.getActionDetailForScrap(materialLineDTO).toString()
                                       , confirmationText);
        }
        return MaterialServicesHelper.updateScrapMaterialLines(scrapQuantity, materialLines, requestHeaderRepository,
                                                               traceabilityRepository, this, warehouseServices, userDTO, confirmationText);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','WH_QI','WH_DEFAULT','IT_SUPPORT')")
    public PageObject getMaterialLineQi(PageObject pageObject, String status, String qiMarking, boolean suggestBinLocation, String whSiteId) {
        return requestHeaderRepository.findMaterialLineByQi(pageObject, status, qiMarking, suggestBinLocation, whSiteId);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL')")
    public MaterialLineQiDTO updateMaterialLineByQi(MaterialLineQiDTO materialLineQiDTO, String userId) throws GloriaApplicationException {
        String concatinatedMaterialIds = String.valueOf(materialLineQiDTO.getMaterialLineIds());
        UserDTO user = userServices.getUser(userId);

        warehouseServices.validatePart(materialLineQiDTO.getpPartName(), materialLineQiDTO.getpPartNumber());

        List<Long> materialLineIds = GloriaFormateUtil.getValuesAsLong(concatinatedMaterialIds);
        for (long materialineId : materialLineIds) {
            MaterialLine materialLine = requestHeaderRepository.findMaterialLineByIdForQi(materialineId);
            materialLine.getDeliveryNoteLine().setSendToQI(materialLineQiDTO.isMarkedForInspection());

            BinLocation existingBinLocation = warehouseServices.getPlacement(materialLine.getPlacementOID()).getBinLocation();

            if (materialLineQiDTO.isMarkedForInspection()) {
                materialLine.getStatus().mark(materialLine, materialLine.getQuantity(), this, requestHeaderRepository, traceabilityRepository, user);
            } else {
                MaterialLineStatusHelper.merge(materialLine, MaterialLineStatus.STORED, materialLine.getQuantity(), requestHeaderRepository,
                                               traceabilityRepository, user, null);
            }
            materialLine.setStatusDate(DateUtil.getCurrentUTCDateTime());

            MaterialServicesHelper.removePlacement(materialLine, warehouseServices);
            createPlacement(existingBinLocation, materialLine);
        }
        return materialLineQiDTO;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','WH_QI')")
    public MaterialLineQiDTO unmarkMaterialLineByQi(MaterialLineQiDTO materialLineQiDTO, long quantityToUnmark, String userId)
            throws GloriaApplicationException {
        long quantityRemainingToUnmark = quantityToUnmark;
        String concatinatedMaterialIds = String.valueOf(materialLineQiDTO.getMaterialLineIds());
        UserDTO user = userServices.getUser(userId);

        warehouseServices.validatePart(materialLineQiDTO.getpPartName(), materialLineQiDTO.getpPartNumber());
        List<MaterialLine> materialLinesMarked = requestHeaderRepository.findMaterialLines(GloriaFormateUtil.getValuesAsLong(concatinatedMaterialIds));
        DeliveryNoteLine deliveryNoteLine = materialLinesMarked.get(0).getDeliveryNoteLine();
        if (quantityToUnmark == calculateTotalMarkedForInspectionQty(deliveryNoteLine.getMaterialLine())) {
            materialLinesMarked.get(0).getDeliveryNoteLine().setSendToQI(false);
        }
        if (materialLinesMarked != null && !materialLinesMarked.isEmpty()) {
            for (MaterialLine materialLineMarked : materialLinesMarked) {
                if (quantityRemainingToUnmark < 0) {
                    break;
                }
                quantityRemainingToUnmark = doQIUnmark(quantityRemainingToUnmark, materialLineMarked, user);
            }
        }
        return materialLineQiDTO;
    }

    private long calculateTotalMarkedForInspectionQty(List<MaterialLine> materialLines) {
        long totalRceivedQtyMarkedForInspection = 0;
        if (materialLines != null && !materialLines.isEmpty()) {
            for (MaterialLine materialLine : materialLines) {
                if (materialLine.getStatus() == MaterialLineStatus.MARKED_INSPECTION) {
                    totalRceivedQtyMarkedForInspection += materialLine.getQuantity();
                }
            }
        }
        return totalRceivedQtyMarkedForInspection;
    }
    
    private long doQIUnmark(long unmarkQuantity, MaterialLine materialLine, UserDTO user) throws GloriaApplicationException {
        BinLocation existingBinLocation = warehouseServices.getPlacement(materialLine.getPlacementOID()).getBinLocation();
        MaterialLineStatus previousStatus = identifyPreviousStatusWhenInReceived(materialLine, materialLine.getPreviousStatus());
        if (materialLine.getQuantity() <= unmarkQuantity) {
            unmarkQuantity = unmarkQuantity - materialLine.getQuantity();
            materialLine.getStatus().unMark(materialLine, previousStatus, materialLine.getQuantity(), requestHeaderRepository, traceabilityRepository, this,
                                            user);
            MaterialServicesHelper.removePlacement(materialLine, warehouseServices);
        } else if (unmarkQuantity > 0) {
            MaterialServicesHelper.removePlacement(materialLine, warehouseServices);
            List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
            avoidTraceForMLStatus.add(materialLine.getPreviousStatus());
            MaterialLine materialLineToUnmark = MaterialLineStatusHelper.split(materialLine, materialLine.getPreviousStatus(), existingBinLocation.getCode(),
                                                                               unmarkQuantity, requestHeaderRepository, traceabilityRepository, user,
                                                                               avoidTraceForMLStatus);
            createPlacement(existingBinLocation, materialLineToUnmark);
            materialLine.getStatus().unMark(materialLineToUnmark, previousStatus, materialLineToUnmark.getQuantity(), requestHeaderRepository,
                                            traceabilityRepository, this, user);
            materialLine.setInspectionStatus(InspectionStatus.NOT_OK);
            unmarkQuantity = 0;
        } else if (unmarkQuantity == 0) {
            MaterialServicesHelper.removePlacement(materialLine, warehouseServices);
            materialLine.setInspectionStatus(InspectionStatus.NOT_OK);
            MaterialLineStatusHelper.createTraceabilityLog(materialLine, traceabilityRepository, MaterialLineStatus.MARKED_INSPECTION.toString(),
                                                           "UnMarked Qty: " + unmarkQuantity, user.getId(), user.getUserName(), "");
        }
        createPlacement(existingBinLocation, materialLine);
        return unmarkQuantity;
    }
    
    private MaterialLineStatus identifyPreviousStatusWhenInReceived(MaterialLine materialLine, MaterialLineStatus previousStatus) {
        if (materialLine != null && previousStatus != null && previousStatus.equals(MaterialLineStatus.RECEIVED)) {
            if (materialLine.getBinLocationCode().equalsIgnoreCase("TS-01-01-01")) {
                return MaterialLineStatus.READY_TO_STORE;
            }
            return MaterialLineStatus.STORED;
        }
        return previousStatus;
    }

    private void approveMaterialLines(long approvedQuantity, DeliveryNoteLine deliveryNoteLine, UserDTO user, List<MaterialLine> materialLines,
            DeliveryNoteSubLine deliveryNoteSubLine) throws GloriaApplicationException {
        List<MaterialLine> approvedMaterialLines = new ArrayList<MaterialLine>();
        for (MaterialLine materialLine : materialLines) {
            if (approvedQuantity < 1) {
                materialLine.getStatus().quarantine(materialLine.getQuantity(), user, materialLine, this, traceabilityRepository, warehouseServices);
                continue;
            }
            approvedQuantity = doQIApprove(approvedQuantity, user, materialLine, approvedMaterialLines, deliveryNoteSubLine);
        }
        if (deliveryNoteSubLine.isStore()) {
            MaterialServicesHelper.doStoreQi(approvedMaterialLines, this, user, traceabilityRepository,
                                                     deliveryNoteSubLine, warehouseServices, requestHeaderRepository);
        } else {
            MaterialServicesHelper.doDirectSend(requestHeaderRepository, deliveryNoteLine, approvedMaterialLines, user, this, commonServices);
        }
    }

    private long doQIApprove(long approvedQuantity, UserDTO user, MaterialLine materialLine, List<MaterialLine> approvedMaterialLines,
            DeliveryNoteSubLine deliveryNoteSubLine) throws GloriaApplicationException {
        if (materialLine.getQuantity() <= approvedQuantity) {
            approvedQuantity = approvedQuantity - materialLine.getQuantity();
            approvedMaterialLines.add(materialLine.getStatus().approveQI(materialLine.getQuantity(), user, materialLine, this, traceabilityRepository,
                                                                         requestHeaderRepository, commonServices, deliveryNoteSubLine.isStore(),
                                                                         warehouseServices));
        } else if (approvedQuantity > 0) {
            List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
            avoidTraceForMLStatus.add(materialLine.getStatus());
            MaterialLine materialLineToApprove = MaterialLineStatusHelper.split(materialLine, approvedQuantity, requestHeaderRepository,
                                                                                traceabilityRepository, this, user, avoidTraceForMLStatus);

            approvedMaterialLines.add(materialLineToApprove.getStatus().approveQI(approvedQuantity, user, materialLineToApprove, this, traceabilityRepository,
                                                                                  requestHeaderRepository, commonServices, deliveryNoteSubLine.isStore(),
                                                                                  warehouseServices));

            materialLine.getStatus().quarantine(materialLine.getQuantity(), user, materialLine, this, traceabilityRepository, warehouseServices);

            approvedQuantity = 0;
        }
        return approvedQuantity;
    }

    @Override
    public void sendGoodsReceipt(String companyCode, DeliveryNoteLine deliveryNoteLine, Long approvedQuantity, ProcureType procureType)
            throws GloriaApplicationException {
        if (procureType != null) {
            procureType.createSendGoodsReceipt(companyCode, deliveryNoteLine, approvedQuantity, goodsReceiptSender, deliveryNoteRepo, commonServices);
        }
    }

    @Override
    public void createAndSendProcessPurchaseOrder(Order order, String action) throws GloriaApplicationException {
        ProcureLine procureLine = order.getOrderLines().get(0).getProcureLine();
        if (procureLine != null) {
            procureLine.getProcureType().createSendProcessPurchaseOrder(procureLine, order, action, commonServices, companyCodeRepository, orderSapRepository,
                                                                        processPurchaseOrderSender);
        }
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL', 'WH_QI')")
    public List<MaterialLineQiDTO> updateMaterialLinesQi(List<MaterialLineQiDTO> materialLineQiDTOs, String userId) throws GloriaApplicationException {
        List<MaterialLineQiDTO> materialLineQis = new ArrayList<MaterialLineQiDTO>();
        for (MaterialLineQiDTO materialLineQiDTO : materialLineQiDTOs) {
            warehouseServices.validatePart(materialLineQiDTO.getpPartName(), materialLineQiDTO.getpPartNumber());
            materialLineQis.add(updateMaterialLineByQi(materialLineQiDTO, userId));
        }
        return materialLineQis;
    }
    
    
    @Override
    @PreAuthorize("hasAnyRole( 'DELIVERY', 'PROCURE','PROCURE-INTERNAL','IT_SUPPORT')")
    public PageObject getBorrowableMaterialLines(PageObject pageObject, long materialLineId) {
        MaterialLine materialLine = requestHeaderRepository.findMaterialLineById(materialLineId);
        MaterialHeader materialHeader = materialLine.getMaterial().getMaterialHeader();
        ProcureLine procureLine = materialLine.getMaterial().getProcureLine();
        return requestHeaderRepository.getBorrowableMaterialLines(pageObject, procureLine.getpPartNumber(), procureLine.getpPartVersion(),
                                                                  procureLine.getpPartAffiliation(), materialHeader.getReferenceId(),
                                                                  materialHeader.getMtrlRequestId());
    }

    @Override
    public MaterialLine borrowMaterialLines(long borrowerMaterialLineOid, String borrowFromMaterialLineOids, boolean noReturn,
            TraceabilityRepository traceabilityRepository, UserDTO userDTO) throws GloriaApplicationException {
        MaterialLine borrowerMaterialLine = requestHeaderRepository.findMaterialLineById(borrowerMaterialLineOid);
        List<MaterialLine> borrowFromMaterialLines = requestHeaderRepository.findMaterialLines(GloriaFormateUtil.getValuesAsLong(borrowFromMaterialLineOids));
        borrowerMaterialLine.getStatus().borrow(borrowerMaterialLine, borrowFromMaterialLines, noReturn, requestHeaderRepository, this, traceabilityRepository,
                                                warehouseServices, orderRepository, userDTO);
        if (noReturn && borrowerMaterialLine.getStatus().equals(MaterialLineStatus.WAIT_TO_PROCURE)) {
            ProcureLine borrowerProcureLine = borrowerMaterialLine.getMaterial().getProcureLine();
            if (borrowerProcureLine != null) {
                procureLineRepository.delete(borrowerProcureLine);
            }
        }
        return requestHeaderRepository.updateMaterialLine(borrowerMaterialLine);
    }

    @Override
    @PreAuthorize("hasAnyRole('PROCURE', 'WH_DEFAULT','IT_SUPPORT')")
    public FileToExportDTO exportProforma(long requestListId, String whSiteId) throws GloriaApplicationException {
        RequestList requestList = findRequestListById(requestListId);
        Site site = siteRepo.getSiteBySiteId(whSiteId);
        CompanyCode companyCode = companyCodeRepository.findCompanyCodeByCode(site.getCompanyCode());
        if (companyCode != null) {
            String defaultCurrency = companyCode.getDefaultCurrency();
            return ExportProforma.export(requestList, defaultCurrency, commonServices);
        }
        throw new GloriaApplicationException(GloriaExceptionConstants.CURRENCY_RATES_NOT_DEFINED,
                                             "No company code or currency rate defined for selected warehouse");
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','IT_SUPPORT')")
    public PageObject findMaterialLinesByDispatchNote(PageObject pageObject, String receiveType, String whSiteId, String deliveryAddressId,
            String shipmentType, String dispatchNoteNo, String partNo, String partVersion, String status) {
        return requestHeaderRepository.findMaterialLinesByDispatchNote(pageObject, receiveType, whSiteId, deliveryAddressId, shipmentType, dispatchNoteNo,
                                                                       partNo, partVersion, status);
    }

    @Override
    public void deleteRequestGroup(RequestGroup requestGroup) {
        requestHeaderRepository.deleteRequestGroup(requestGroup);
    }

    @Override
    public MaterialLine updateMaterialLine(MaterialLine materialLine) {
        return requestHeaderRepository.updateMaterialLine(materialLine);
    }

    @Override
    public FileToExportDTO exportSupplier(List<Long> orderLineIds, boolean includeDeliveryController, boolean includeOrderLineLogs, boolean includeOrderLogs,
            boolean includeProject, boolean includePlannedDispatchDate, boolean excludeAgreedSTA) {
        List<OrderLine> orderLines = orderRepository.findOrderLinesByIds(orderLineIds);
        ExportSupplier exportSupplier = new ExportSupplier(orderLines, includeDeliveryController, includeOrderLineLogs, includeOrderLogs, includeProject,
                                                           includePlannedDispatchDate, excludeAgreedSTA);
        return exportSupplier.export();
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_DEFAULT','WH_SETUP','WH_QI')")
    public BinlocationBalance updateBinlocationBalance(BinlocationBalanceDTO binlocationBalanceDto, String comment, String userId, String action, 
            long quantity, String binLocationCode)
            throws GloriaApplicationException {

        long binlocationBalanceOid = binlocationBalanceDto.getId();

        BinlocationBalance binlocationBalance = warehouseServices.findBinlocationBalanceById(binlocationBalanceOid);

        if (binlocationBalance == null) {
            LOGGER.error("No part balance objects exists for id : " + binlocationBalanceOid);
            throw new GloriaSystemException("This operation cannot be performed. No part balance objects exists for id : " + binlocationBalanceOid,
                                            GloriaExceptionConstants.INVALID_DATASET_OID);
        }

        if (binlocationBalanceDto.getVersion() != binlocationBalance.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                 "This operation cannot be performed since the information seen in the page has already been updated.");
        }
       
        UserDTO userDTO = userServices.getUser(userId);

        StringBuilder actionDetail = new StringBuilder();
        actionDetail.append("Part No=");
        actionDetail.append(binlocationBalance.getPartNumber());
        actionDetail.append("Warehouse=");
        actionDetail.append(binlocationBalance.getWarehouse().getSiteId());
        actionDetail.append("Bin Location=");
        actionDetail.append(binlocationBalance.getBinLocation().getCode());

        if (action != null && "move".equalsIgnoreCase(action)) {
            if (binlocationBalance.getDeviation() != null) {
                throw new GloriaApplicationException(GloriaExceptionConstants.MOVE_NOT_POSSIBLE_WHEN_DEVIATION_EXISTS,
                        "Parts with deviation cannot be moved.");
            }
            if (quantity > binlocationBalance.getQuantity()) {
                throw new GloriaApplicationException(GloriaExceptionConstants.MOVE_QTY_MORE_THAN_STOCK_QTY,
                                                     "This operation cannot be performed. Move quantity is more than stock balance: ");
            }
            List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
            for (Placement placement : binlocationBalance.getPlacements()) {
                MaterialLine materialLine = requestHeaderRepository.findMaterialLineById(placement.getMaterialLineOID());
                if (materialLine != null && !materialLine.getStatus().equals(MaterialLineStatus.MISSING)) {
                    materialLines.add(materialLine);
                }
            }
            moveMaterialLines(materialLines, quantity, binLocationCode, userDTO);
        } else {
            adjustStockBalance(binlocationBalanceDto, comment, binlocationBalance, userDTO, actionDetail);
        }

        return binlocationBalance;
    }

    private void adjustStockBalance(BinlocationBalanceDTO binlocationBalanceDto, String comment, BinlocationBalance binlocationBalance, UserDTO userDTO,
            StringBuilder actionDetail) throws GloriaApplicationException {
        userlogServices.addUserLog(userDTO, UserlogActionType.ADJUSTINVENTORYQUANTITY, actionDetail.toString(), comment);
        Deviation deviation = binlocationBalance.getDeviation();
        if (deviation != null && deviation.equals(Deviation.LOWER)) {
            MaterialServicesHelper.handleMaterialLineStatus(binlocationBalance.getBinLocation().getPlacements(), this, userDTO, traceabilityRepository);
        }

        if (binlocationBalanceDto.getQuantity() > binlocationBalance.getQuantity()) {
            releaseMaterials(binlocationBalanceDto, binlocationBalance, userDTO, comment);
        } else if (binlocationBalanceDto.getQuantity() < binlocationBalance.getQuantity()) {
            createMissingMaterials(binlocationBalanceDto, binlocationBalance, userDTO, deviation, comment);
        }

        binlocationBalance.setQuantity(binlocationBalanceDto.getQuantity());
        binlocationBalance.setDeviation(null);
        removeBinlocationBalance(binlocationBalance);
    }

    public void createMissingMaterials(BinlocationBalanceDTO binlocationBalanceDto, BinlocationBalance binlocationBalance, UserDTO userDTO, 
            Deviation deviation, String comment) throws GloriaApplicationException {
        long deviationQty = binlocationBalance.getQuantity() - binlocationBalanceDto.getQuantity();

        List<Long> existingPlacementOids = new ArrayList<Long>();
        for (Placement aPlacement : binlocationBalance.getPlacements()) {
            existingPlacementOids.add(aPlacement.getPlacementOid());
        }

        for (Long existingPlacementOid : existingPlacementOids) {
            Placement existingPlacement = warehouseServices.getPlacement(existingPlacementOid);
            MaterialLine existingLine = null;
            if (existingPlacement != null) {
                existingLine = findMaterialLineById(existingPlacement.getMaterialLineOID());
            }
            if (existingLine != null && (existingLine.getZoneType().equals(ZoneType.STORAGE) || existingLine.getZoneType().equals(ZoneType.TO_STORE))
                    && !existingLine.getStatus().isRemovedDb()) {
                if (deviationQty > 0) {
                    long deviatedMaterialLineQuantity = Math.min(existingLine.getQuantity(), deviationQty);
                    BinLocation materialLineBinLocation = null;
                    if (existingPlacement != null) {
                        materialLineBinLocation = existingPlacement.getBinLocation();
                    }
                    MaterialServicesHelper.removePlacement(existingLine, warehouseServices);
                    existingLine.setConfirmationText(comment);
                    List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
                    avoidTraceForMLStatus.add(existingLine.getStatus());
                    MaterialLine missingMaterial = MaterialLineStatusHelper.split(existingLine, MaterialLineStatus.MISSING, deviatedMaterialLineQuantity,
                                                                                  requestHeaderRepository, traceabilityRepository, this, userDTO,
                                                                                  avoidTraceForMLStatus); 
                    if (existingLine.getQuantity() > 0) {
                        createPlacement(materialLineBinLocation, existingLine);
                    }
                  
                    if (missingMaterial != null) {
                        missingMaterial.setRequestGroup(null);
                        missingMaterial.setPickList(null);
                    }
                    deviationQty = deviationQty - deviatedMaterialLineQuantity;
                }
            }
        }
    }

    public void releaseMaterials(BinlocationBalanceDTO binlocationBalanceDto, BinlocationBalance binlocationBalance, UserDTO userDTO, String comment)
            throws GloriaApplicationException {
        List<MaterialLine> materialLines = findMaterialByBinlocationBalance(binlocationBalance);
        if (materialLines != null && !materialLines.isEmpty()) {
            long releaseQty = binlocationBalanceDto.getQuantity() - binlocationBalance.getQuantity();
            Material material = materialLines.get(0).getMaterial();
            createReleasedMaterialAndPlace(material, releaseQty, binlocationBalance, userDTO, comment);
        }
    }

    private List<MaterialLine> findMaterialByBinlocationBalance(BinlocationBalance binlocationBalance) {
        return requestHeaderRepository.findMaterialByBinlocatioinBalance(binlocationBalance);
    }

    private void removeBinlocationBalance(BinlocationBalance binlocationBalance) {
        if (binlocationBalance != null && binlocationBalance.getQuantity() == 0 && binlocationBalance.getDeviation() == null) {
            warehouseServices.removeBinlocationBalance(binlocationBalance);
        }
    }

    private Material createReleasedMaterialAndPlace(Material material, long releaseQty, BinlocationBalance binlocationBalance, UserDTO userDTO, String comment)
            throws GloriaApplicationException {
        Zone zone = binlocationBalance.getBinLocation().getZone();
        String whSiteId = zone.getStorageRoom().getWarehouse().getSiteId();
      
        Material newMaterial = new Material();
        newMaterial.setCreatedDate(DateUtil.getCurrentUTCDateTime());        
        newMaterial.setMaterialType(MaterialType.RELEASED);
        newMaterial.setPartAffiliation(binlocationBalance.getPartAffiliation());
        newMaterial.setPartModification(binlocationBalance.getPartModification());
        newMaterial.setPartName(binlocationBalance.getPartName());
        newMaterial.setPartNumber(binlocationBalance.getPartNumber());
        newMaterial.setPartVersion(binlocationBalance.getPartVersion());
        newMaterial.setUnitOfMeasure(material.getUnitOfMeasure());
        newMaterial.setStatus(MaterialStatus.ADDED);
        
        Site site = commonServices.getSiteBySiteId(whSiteId);
        FinanceHeader financeHeader = new FinanceHeader();
        financeHeader.setCompanyCode(site.getCompanyCode());
        newMaterial.setFinanceHeader(financeHeader);
        
        MaterialLine newMaterialLine = new MaterialLine();
        newMaterialLine.setWhSiteId(whSiteId);
        newMaterialLine.setFinalWhSiteId(whSiteId);  
        newMaterialLine.setDirectSend(DirectSendType.NO);
        newMaterialLine.setQuantity(releaseQty);
        MaterialLineStatusHelper.assignMaterialLineToMaterial(newMaterial, newMaterialLine);
        requestHeaderRepository.saveMaterial(newMaterial);

        BinLocation binLocation = binlocationBalance.getBinLocation();

        MaterialLineStatus materialLineStatus = MaterialLineStatus.STORED;
        if (zone != null && zone.getType().equals(ZoneType.TO_STORE)) {
            materialLineStatus = MaterialLineStatus.READY_TO_STORE;
        } 
        List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
        avoidTraceForMLStatus.add(materialLineStatus);
        newMaterialLine = MaterialLineStatusHelper.merge(newMaterialLine, materialLineStatus, releaseQty, requestHeaderRepository,
                                                         traceabilityRepository, userDTO, avoidTraceForMLStatus);
        createPlacement(binLocation.getBinLocationOid(), newMaterialLine);
        
        MaterialLineStatusHelper.createTraceabilityLog(newMaterialLine, traceabilityRepository, "Qty Adjusted",
                                                       comment, userDTO.getId(), userDTO.getUserName(),
                                                       GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE);
        return newMaterial;
    }

    @Override
    public MaterialLine findMaterialLineById(Long materialLineOID) {
        return requestHeaderRepository.findMaterialLineById(materialLineOID);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE', 'PROCURE-INTERNAL', 'DELIVERY', 'REQUESTER_FOR_PULL', 'VIEWER', 'VIEWER_PRICE', 'WH_DEFAULT','WH_QI','IT_SUPPORT')")
    public List<OrderLineDTO> findOrderLineByMaterialId(long materialOid, String loggedInUserID) throws GloriaApplicationException {

        // lazy load
        List<OrderLine> orderLines = orderRepository.findOrderLinebyMaterialId(materialOid);
        if (orderLines != null && !orderLines.isEmpty()) {
            for (OrderLine orderLine : orderLines) {
                orderLine.getOrderLineVersions();

            }
        }
        return procurementDtoTransformer.transformAsOrderLineDTOs(orderLines, false);
    }

    @Override
    public void printPullLabels(long picklistId, long labelCopies, String whSiteId, long materialLineId) throws GloriaApplicationException {
        List<Printer> printers = warehouseServices.findListOfPrinters(whSiteId);
        if (printers != null && !printers.isEmpty()) {
            String printerInfo = printers.get(0).getHostAddress();
            PickList pickList = findPickListById(picklistId);
            List<MaterialLine> materialLines = requestHeaderRepository.findMaterialLinesByPickListId(picklistId);
            if (materialLines != null && materialLines.size() > 0) {
                for (MaterialLine materialLine : materialLines) {
                    if (materialLineId > 0 && materialLine.getMaterialLineOID() != materialLineId) {
                        continue;
                    }
                    long copies = labelCopies;
                    if (copies == 0) {
                        copies = materialLine.getQuantity();
                    }
                    ProcureLine procureLine = materialLine.getMaterial().getProcureLine();
                    MaterialHeader materialHeader = materialLine.getMaterial().getMaterialHeader();
                    FinanceHeader financeHeader = materialLine.getMaterial().getFinanceHeader();
                    RequestGroup requestGroup = materialLine.getRequestGroup();
                    RequestList requestList = requestGroup.getRequestList();

                    String truncatedDeliveryAddress = GloriaFormateUtil.truncate(requestList.getDeliveryAddressName(),
                                                                                 PrintLabelTemplate.LBL_MAX_DELIVERYADDRESS_LENGTH);
                    String truncatedReferenceId = "";
                    if (materialHeader != null) {
                        truncatedReferenceId = GloriaFormateUtil.truncate(materialHeader.getReferenceId(), PrintLabelTemplate.LBL_MAX_TESTOBJECT_LENGTH);
                    }
                    String truncatedProjectId = GloriaFormateUtil.truncate(financeHeader.getProjectId(), PrintLabelTemplate.LBL_MAX_PROJECT_LENGTH);
                    String truncatedPartMod = GloriaFormateUtil.truncate(procureLine.getpPartModification(), PrintLabelTemplate.LBL_MAX_PARTMOD_LENGTH);
                    String truncatedPartVersion = GloriaFormateUtil.truncate(procureLine.getpPartVersion(), PrintLabelTemplate.LBL_MAX_PARTVER_LENGTH);
                    String truncatedPartNumber = GloriaFormateUtil.truncate(procureLine.getpPartNumber(), PrintLabelTemplate.LBL_MAX_PARTNO_LENGTH);
                    String truncatedPartName = GloriaFormateUtil.truncate(procureLine.getpPartName(), PrintLabelTemplate.LBL_MAX_PARTNAME_LENGTH);

                    printLabelServices.doPrint(String.format(PrintLabelTemplate.ZPL_LABEL_TEMPLATE_PULL_LABEL, 
                                                             pickList.getCode(), 
                                                             GloriaFormateUtil.hexaDecimalValue(truncatedDeliveryAddress),
                                                             GloriaFormateUtil.hexaDecimalValue(truncatedReferenceId), 
                                                             materialLine.getQuantity(), 
                                                             GloriaFormateUtil.hexaDecimalValue(truncatedProjectId),
                                                             GloriaFormateUtil.hexaDecimalValue(truncatedPartMod),
                                                             GloriaFormateUtil.hexaDecimalValue(truncatedPartName), 
                                                             GloriaFormateUtil.hexaDecimalValue(truncatedPartVersion),
                                                             GloriaFormateUtil.hexaDecimalValue(truncatedPartNumber), copies), printerInfo);
                }
            }
        } else {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_PRINTER_CONFIGURATION, "Printers are not configured correctly");
        }
    }

    @Override
    public void printPartLabels(List<DeliveryNoteLineDTO> deliveryNoteLineDTOs, long labelCopies, String whSiteId) throws GloriaApplicationException {
        List<Printer> printers = warehouseServices.findListOfPrinters(whSiteId);
        if (printers != null && !printers.isEmpty()) {
            String printerInfo = printers.get(0).getHostAddress();
            for (DeliveryNoteLineDTO deliveryNoteLineDTO : deliveryNoteLineDTOs) {
                long copies = labelCopies;
                if (copies == 0) {
                    copies = deliveryNoteLineDTO.getDeliveryNoteQuantity();
                }

                ObjectJSON objectJSON = new ObjectJSON();

                String truncatedPartMod = GloriaFormateUtil.truncate(deliveryNoteLineDTO.getPartModification(), PrintLabelTemplate.LBL_MAX_PARTMOD_LENGTH);
                String truncatedPartVersion = GloriaFormateUtil.truncate(deliveryNoteLineDTO.getPartVersion(), PrintLabelTemplate.LBL_MAX_PARTVER_LENGTH);
                String truncatedPartNumber = GloriaFormateUtil.truncate(deliveryNoteLineDTO.getPartNumber(), PrintLabelTemplate.LBL_MAX_PARTNO_LENGTH);
                String truncatedPartName = GloriaFormateUtil.truncate(deliveryNoteLineDTO.getPartName(), PrintLabelTemplate.LBL_MAX_PARTNAME_LENGTH);

                if (!StringUtils.isEmpty(deliveryNoteLineDTO.getPartAffiliation())) {
                    objectJSON.addItem("a", deliveryNoteLineDTO.getPartAffiliation());
                }
                objectJSON.addItem("n", truncatedPartName);
                objectJSON.addItem("no", truncatedPartNumber);
                objectJSON.addItem("v", truncatedPartVersion);
                objectJSON.addItem("m", truncatedPartMod);

                // padding to maintain the 2D bar code size
                if ((PrintLabelTemplate.MAX2D_LABEL_DATA_LENGTH - objectJSON.jsonValue().length()) > 0) {
                    objectJSON.addItem("em", StringUtils.rightPad("", PrintLabelTemplate.MAX2D_LABEL_DATA_LENGTH - objectJSON.jsonValue().length()));
                }

                printLabelServices.doPrint(String.format(PrintLabelTemplate.ZPL_2D_LABEL_TEMPLATE_PART, objectJSON.jsonValueWithHexaDecimal(),
                                                         GloriaFormateUtil.hexaDecimalValue(truncatedPartMod),
                                                         GloriaFormateUtil.hexaDecimalValue(truncatedPartVersion),
                                                         GloriaFormateUtil.hexaDecimalValue(truncatedPartNumber),
                                                         GloriaFormateUtil.hexaDecimalValue(truncatedPartName), copies), printerInfo);
            }
        } else {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_PRINTER_CONFIGURATION, "Printers are not configured correctly");
        }
    }

    @Override
    public DispatchNote findDispatchNote(String dispatchNoteNo) {
        return requestHeaderRepository.findDispatchNote(dispatchNoteNo, null);
    }

    @Override
    public Long placeIntoZone(MaterialLine materialLine, ZoneType zoneType) throws GloriaApplicationException {
        Long id = null;
        if (materialLine != null) {
            Zone zone = warehouseServices.findZoneCodes(zoneType, materialLine.getWhSiteId());
            if (zone != null) {
                List<BinLocation> binLocations = zone.getBinLocations();
                if (binLocations != null && !binLocations.isEmpty()) {
                    id = binLocations.get(0).getId();
                    createPlacement(id, materialLine);
                }
            } else {
                Map<String, Object> msgParam = new HashMap<String, Object>();
                msgParam.put("zoneType", zoneType.name());
                throw new GloriaApplicationException("zoneType", GloriaExceptionConstants.BINLOCATION_MISSING, "No binlocations available for zone type "
                        + zoneType.name(), msgParam);
            }
        }
        return id;
    }

    @Override
    public void checkAndUpdateRequestListStatusAsPickCompleted(RequestList requestList) {
        List<RequestGroup> reqGroups = requestList.getRequestGroups();
        if (reqGroups != null && !reqGroups.isEmpty()) {
            boolean isPickCompleted = true;

            for (RequestGroup requestGroup : reqGroups) {
                PickList pickList = requestGroup.getPickList();
                if (pickList == null) {
                    isPickCompleted = false;
                    break;
                } else if (pickList.getMaterialLines() != null && !pickList.getMaterialLines().isEmpty() && pickList.getStatus() != PickListStatus.PICKED) {
                    isPickCompleted = false;
                    break;
                }
            }
            if (isPickCompleted) {
                Set<RequestList> requestListHashSet = new HashSet<RequestList>();
                Set<PickList> pickListSet = new HashSet<PickList>();
                for (RequestGroup requestGroup : reqGroups) {
                    pickListSet.add(requestGroup.getPickList());
                }
                for (PickList pickList : pickListSet) {
                    List<RequestGroup> requestGroupInner = pickList.getRequestGroups();
                    for (RequestGroup requestGroup1 :requestGroupInner) {
                       requestListHashSet.add(requestGroup1.getRequestList());
                    }
                }
                for (RequestList set : requestListHashSet) {
                    set.setStatus(RequestListStatus.PICK_COMPLETED);
                    requestHeaderRepository.save(set);
                }
            }
        } else {
            requestHeaderRepository.deleteRequestList(requestList);
        }
    }

    @Override
    public void reserveMaterialLines(String materiallineIds, String userId, String whSiteId) throws GloriaApplicationException {
        List<MaterialLine> materialLines = requestHeaderRepository.findMaterialLines(GloriaFormateUtil.getValuesAsLong(materiallineIds));
        if (materialLines != null && !materialLines.isEmpty()) {
            for (MaterialLine materialLine : materialLines) {
                String status = MaterialLineStatus.READY_TO_STORE.toString() + "," + MaterialLineStatus.REQUESTED.toString();
                Material material = materialLine.getMaterial();
                String transportLabel = MaterialServicesHelper.getTransportLabelForMaterialLine(materialLine);
                List<MaterialLine> materialLinesToReserve = requestHeaderRepository.findMaterialsByPartStatusSiteAndTransportLabel(material.getPartNumber(),
                                                                                                                                 material.getPartVersion(),
                                                                                                                                 status,
                                                                                                                                 whSiteId,
                                                                                                                                 material.getPartModification(),
                                                                                                                                 material.getPartAffiliation(),
                                                                                                                                 transportLabel);
                for (MaterialLine materialLineToReserve : materialLinesToReserve) {
                    if (!StringUtils.isEmpty(materialLineToReserve.getReservedUserId()) 
                            && !materialLineToReserve.getReservedUserId().equalsIgnoreCase(userId)) {
                        throw new GloriaApplicationException(GloriaExceptionConstants.LOCKED_BY_ANOTHER_USER, "Atleast one of the material line is "
                                + "locked by another user " + materialLineToReserve.getMaterialLineOID());
                    }
                    materialLineToReserve.setReservedUserId(userId);
                    materialLineToReserve.setReservedTimeStamp(DateUtil.getCurrentUTCDateTime());
                    requestHeaderRepository.updateMaterialLine(materialLineToReserve);
                }
            }
        }
    }

    @Override
    public void unlockMaterialLines(String materiallineIds, String userId, String whSiteId) {
        List<MaterialLine> materialLines = requestHeaderRepository.findMaterialLines(GloriaFormateUtil.getValuesAsLong(materiallineIds));
        if (materialLines != null && !materialLines.isEmpty()) {
            for (MaterialLine materialLine : materialLines) {
                String status = MaterialLineStatus.READY_TO_STORE.toString() + "," + MaterialLineStatus.REQUESTED.toString();
                Material material = materialLine.getMaterial();
                String transportLabel = MaterialServicesHelper.getTransportLabelForMaterialLine(materialLine);
                List<MaterialLine> materialLinesToUnlock = requestHeaderRepository.findMaterialsByPartStatusSiteAndTransportLabel(material.getPartNumber(),
                                                                                                                                material.getPartVersion(),
                                                                                                                                status, whSiteId, 
                                                                                                                                material.getPartModification(),
                                                                                                                                material.getPartAffiliation(),
                                                                                                                                transportLabel);
                for (MaterialLine materialLineToUnlock : materialLinesToUnlock) {
                    String reservedUserId = materialLineToUnlock.getReservedUserId();
                    if (reservedUserId != null && reservedUserId.equals(userId)) {
                        materialLineToUnlock.setReservedUserId(null);
                        materialLineToUnlock.setReservedTimeStamp(null);
                        requestHeaderRepository.updateMaterialLine(materialLineToUnlock);
                    }
                }
            }
        }
    }

    @Override
    public void unlockMaterialLine(String userId) {
        List<MaterialLine> materialLinesToUnlock = requestHeaderRepository.findMaterialLinesByReservedUserId(userId);
        if (materialLinesToUnlock != null && !materialLinesToUnlock.isEmpty()) {
            for (MaterialLine materialLine : materialLinesToUnlock) {
                materialLine.setReservedUserId(null);
                materialLine.setReservedTimeStamp(null);
                requestHeaderRepository.updateMaterialLine(materialLine);
            }
        }
    }

    @Override
    public void reservePicklist(long pickListId, String userId) throws GloriaApplicationException {
        PickList pickList = requestHeaderRepository.findPickListById(pickListId);
        if (!StringUtils.isEmpty(pickList.getReservedUserId()) && !pickList.getReservedUserId().equals(userId)) {
            throw new GloriaApplicationException(GloriaExceptionConstants.LOCKED_BY_ANOTHER_USER, "PickList is " + "locked by another user "
                    + pickList.getPickListOid());
        }
        pickList.setReservedUserId(userId);
        pickList.setReservedTimeStamp(DateUtil.getCurrentUTCDateTime());
        requestHeaderRepository.updatePickList(pickList);
    }

    @Override
    public void releasePickList(long pickListId, String userId) throws GloriaApplicationException {
        PickList pickList = requestHeaderRepository.findPickListById(pickListId);
        if (pickList != null) {
            String reservedUserId = pickList.getReservedUserId();
            boolean noMaterialsPicked = MaterialServicesHelper.noMaterialsPicked(pickList);
            boolean allMaterialsPicked = MaterialServicesHelper.isPicklistPicked(pickList);
            if (reservedUserId != null && reservedUserId.equals(userId) && (noMaterialsPicked || allMaterialsPicked)) {
                pickList.setReservedUserId(null);
                pickList.setReservedTimeStamp(null);
                requestHeaderRepository.updatePickList(pickList);
            } else {
                throw new GloriaApplicationException(GloriaExceptionConstants.PARTIALLY_LOCKED,
                                "PickList is still locked as atleast one of the material lines is not picked." + pickList.getPickListOid());
            }
        }
    }

    @Override
    public void releasePickList(String userId) {
        List<PickList> pickLists = requestHeaderRepository.findPickListByReservedUserId(userId);
        if (pickLists != null && !pickLists.isEmpty()) {
            for (PickList pickList : pickLists) {
                boolean noMaterialsPicked = MaterialServicesHelper.noMaterialsPicked(pickList);
                boolean allMaterialsPicked = MaterialServicesHelper.isPicklistPicked(pickList);
                if (noMaterialsPicked || allMaterialsPicked) {
                    pickList.setReservedUserId(null);
                    pickList.setReservedTimeStamp(null);
                    requestHeaderRepository.updatePickList(pickList);
                } 
            }
        }
    }

    @Override
    public List<DeliveryNoteLine> qiApproveDeliveryNoteLine(String deliveryNoteLineIds, String userId) throws GloriaApplicationException {
        List<Long> deliveryNoteLineIdsAslong = GloriaFormateUtil.getValuesAsLong(deliveryNoteLineIds);
        List<DeliveryNoteLine> deliveryNoteLines = new ArrayList<DeliveryNoteLine>();

        UserDTO user = userServices.getUser(userId);
        for (long deliveryNoteLineId : deliveryNoteLineIdsAslong) {
            DeliveryNoteLine deliveryNoteLine = deliveryNoteRepo.findDeliveryNoteLineById(deliveryNoteLineId);
            deliveryNoteLines.add(deliveryNoteLine);

            String companyCode = commonServices.getCompanyCodeFromMaterialUserId(deliveryNoteLine.getOrderLine().getOrder().getMaterialUserId());
            ProcureType procureType = null;
            long goodsReceiptQuantity = 0;
            for (DeliveryNoteSubLine deliveryNoteSubLine : deliveryNoteLine.getDeliveryNoteSubLines()) {
                Long approvedQuantity = deliveryNoteSubLine.getToApproveQty();
                if (approvedQuantity != null) {
                    List<MaterialLine> approveMaterialLines = new ArrayList<MaterialLine>();
                    for (MaterialLine materialLine : deliveryNoteLine.getMaterialLine()) {
                        boolean materialDirectSend = materialLine.getDirectSend().isDirectSend();
                        procureType = materialLine.getMaterial().getProcureLine().getProcureType();
                        if (materialLine.getStatus().isReadyForQIApprove() && materialDirectSend == deliveryNoteSubLine.isDirectSend()) {

                            if (approvedQuantity == 0) {
                                materialLine.getStatus().quarantine(materialLine.getQuantity(), user, materialLine, this, traceabilityRepository,
                                                                    warehouseServices);
                            } else {
                                approveMaterialLines.add(materialLine);
                            }
                        }
                    }

                    if (!approveMaterialLines.isEmpty()) {
                        approveMaterialLines(approvedQuantity, deliveryNoteLine, user, approveMaterialLines, deliveryNoteSubLine);
                    }
                    goodsReceiptQuantity += approvedQuantity;
                    if (approvedQuantity > 0) {
                        deliveryNoteSubLine.setTransportLabel(null);
                    }
                    deliveryNoteSubLine.setToApproveQty(null);
                    deliveryNoteSubLine.setBinLocation(0);
                    deliveryNoteSubLine.setBinLocationCode(null);
                    deliveryNoteRepo.save(deliveryNoteSubLine);
                }
            }
            if (goodsReceiptQuantity > 0) {
                sendGoodsReceipt(companyCode, deliveryNoteLine, goodsReceiptQuantity, procureType);
            }
        }

        return deliveryNoteLines;
    }
    
    @Override
    public RequestList manageRequestList(List<MaterialLineDTO> materialLineDTOs, String action, Date requiredDeliveryDate, Long priority,
            String deliveryAddressType, String deliveryAddessId, String deliveryAddressName, String loggedInUserId, Long requestListOid)
            throws GloriaApplicationException {
        RequestList requestList = null;
        UserDTO userDTO = userServices.getUser(loggedInUserId);
        if (requestListOid != null) {
            requestList = requestHeaderRepository.findRequestListById(requestListOid);
        }
        return RequestListAction.valueOf(action.toUpperCase()).performAction(requestList, userDTO, materialLineDTOs, requiredDeliveryDate, priority,
                                                                             deliveryAddressType, deliveryAddessId, deliveryAddressName, this);
    }

    @Override
    public RequestList createOrUpdateRequestList(RequestList requestList, Date requiredDeliveryDate, Long priority, String deliveryAddressType,
            String deliveryAddessId, String deliveryAddressName, UserDTO user, List<MaterialLineDTO> materialLineDTOs) throws GloriaApplicationException {
        if (requestList == null && materialLineDTOs != null && !materialLineDTOs.isEmpty()) {
            requestList = RequestListStatus.START.createRequestList(requiredDeliveryDate, priority, deliveryAddressType, deliveryAddessId, deliveryAddressName,
                                                                    user, materialLineDTOs, this, commonServices, requestHeaderRepository).get(0);
        } else {
            requestList = requestList.getStatus().updateRequestList(requestList, requiredDeliveryDate, priority, deliveryAddressType, deliveryAddessId,
                                                                    deliveryAddressName, user, materialLineDTOs, this, commonServices, requestHeaderRepository);
        }
        return requestList;
    }
    
    // send
    @Override
    public RequestList sendRequestList(RequestList requestList, List<MaterialLineDTO> materialLineDTOs, Date requiredDeliveryDate, Long priority,
            String deliveryAddressType, String deliveryAddessId, String deliveryAddressName, UserDTO user) throws GloriaApplicationException {
        if (requestList != null) {
            requestList.getStatus().sendRequestList(requestList, requiredDeliveryDate, priority, deliveryAddressType, deliveryAddessId, deliveryAddressName,
                                                    user, materialLineDTOs, this, commonServices, requestHeaderRepository);
        }
        return requestList;
    }
    
    // cancel
    @Override
    public void cancelRequestList(RequestList requestList, UserDTO userDTO) throws GloriaApplicationException {
        if (requestList != null) {
            requestList.getStatus().cancel(requestList, userDTO, requestHeaderRepository, traceabilityRepository);
        }
    }
    
    // add
    @Override
    public RequestList addToRequestList(RequestList requestList, List<MaterialLineDTO> materialLineDTOs, UserDTO user) throws GloriaApplicationException {
        if (requestList != null) {
            return requestList.getStatus().addToRequestList(requestList, materialLineDTOs, requestHeaderRepository);
        }
        return requestList;
    }
    
    // remove
    @Override
    public RequestList removeFromRequestList(RequestList requestList, List<MaterialLineDTO> materialLineDTOs, UserDTO user) throws GloriaApplicationException {
        if (requestList != null) {
            return requestList.getStatus().removeFromRequestList(requestList, materialLineDTOs, requestHeaderRepository);
        }
        return requestList;
    }
    
    @Override
    public List<MaterialLine> findMaterialLinesForOrderline(long orderLineOID) {
        return requestHeaderRepository.findMaterialLinesForOrderline(orderLineOID);
    }

    @Override
    public void sendQuickPull(MaterialLineDTO materialLineDTO, Date requiredDeliveryDate, String deliveryAddressType, String deliveryAddessId,
            String deliveryAddressName, String loggedInUserId, long pickedQuantity) throws GloriaApplicationException {
        List<MaterialLineDTO> materialLineDTOs = new ArrayList<MaterialLineDTO>();
        materialLineDTOs.add(materialLineDTO);
        materialLineDTO.setPossiblePickQuantity((long) pickedQuantity);
        UserDTO userDTO = userServices.getUser(loggedInUserId);
        RequestList requestList = RequestListAction.SEND.performAction(null, userDTO, materialLineDTOs,
                                                                       requiredDeliveryDate, new Long(0), deliveryAddressType, deliveryAddessId,
                                                                       deliveryAddressName, this);
        List<RequestGroup> requestGroup = updateRequestGroups(MaterialTransformHelper.transformAsRequestGroupDTOs(requestList.getRequestGroups()), true,
                                                              loggedInUserId);
        List<MaterialLine> materialLinesList = requestGroup.get(0).getMaterialLines();
        MaterialLineDTO materialLineDTO5 = MaterialTransformHelper.transformAsMaterialLineDTO(materialLinesList.get(0), null);

        //
        materialLineDTO5.setPickedQuantity((long) pickedQuantity);
        MaterialLine pickedMaterialLine = materialLinesList.get(0)
                                                           .getStatus()
                                                           .pick(materialLineDTO5, userDTO, requestHeaderRepository, traceabilityRepository, warehouseServices,
                                                                 this);
        pickedMaterialLine.setRequestGroup(requestGroup.get(0));
        MaterialLine materialLine2 = this.updateMaterialLine(pickedMaterialLine);
        materialLine2.getStatus().shipOrTransfer(materialLine2, this, loggedInUserId);
    }
    
    @Override
    public void reCreatePlacements(Material material) throws GloriaApplicationException {
        List<MaterialLine> materialLines = material.getMaterialLine();
        if (materialLines != null && !materialLines.isEmpty()) {
            for (MaterialLine materialLine : materialLines) {
                if (!materialLine.getStatus().equals(MaterialLineStatus.MISSING) && (materialLine.getPlacementOID() != null
                        && materialLine.getPlacementOID() > 0)) {
                    Placement placement = warehouseServices.getPlacement(materialLine.getPlacementOID());
                    if (placement != null) {
                        BinLocation binLocation = placement.getBinLocation();
                        if (binLocation != null) {
                            MaterialServicesHelper.removePlacement(materialLine, warehouseServices);
                            createPlacement(binLocation, materialLine);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public List<Material> getMaterialLinesForOrderLine(long orderLineOid) {
        return requestHeaderRepository.getMaterialsForOrderLine(orderLineOid);
    }
}
