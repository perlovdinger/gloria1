package com.volvo.gloria.procurematerial.b.beans;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.b.UserlogServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.Site;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.DeliveryAddressType;
import com.volvo.gloria.procurematerial.c.ShipmentType;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.c.dto.RequestListDTO;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteSubLine;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.MaterialLineStatusTime;
import com.volvo.gloria.procurematerial.d.entities.PickList;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusCounterType;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusHelper;
import com.volvo.gloria.procurematerial.d.entities.status.picklist.PickListStatus;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineHelper;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestlist.RequestListStatus;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialLineStatusCounterRepository;
import com.volvo.gloria.procurematerial.util.MaterialTransformHelper;
import com.volvo.gloria.procurematerial.util.RequestGroupHelper;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.tracing.c.GloriaTraceabilityConstants;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.c.Deviation;
import com.volvo.gloria.warehouse.c.ZoneType;
import com.volvo.gloria.warehouse.d.entities.BinLocation;
import com.volvo.gloria.warehouse.d.entities.BinlocationBalance;
import com.volvo.gloria.warehouse.d.entities.Placement;
import com.volvo.gloria.warehouse.d.entities.StorageRoom;
import com.volvo.gloria.warehouse.d.entities.Warehouse;
import com.volvo.gloria.warehouse.d.entities.Zone;

/**
 * Helper class for MaterialServices.
 */
public abstract class MaterialServicesHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialServicesHelper.class);

    private MaterialServicesHelper() {

    }

    public static List<MaterialLine> pickMaterialLines(List<MaterialLineDTO> materialLineDTOs, String fromMaterialLineIds, String action, String userId,
            MaterialServices materialServices) throws GloriaApplicationException {
        List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
        for (MaterialLineDTO materialLineDTO : materialLineDTOs) {
            materialLines.add(materialServices.updateMaterialLine(materialLineDTO, fromMaterialLineIds, action, false, userId, null, null, null, null));
        }
        return materialLines;
    }

    public static List<MaterialLine> releaseMaterialLines(List<MaterialLineDTO> materialLineDTOs, String confirmationText, String userId,
            MaterialServices materialServices, UserServices userServices, UserlogServices userlogServices, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        UserDTO userDTO = userServices.getUser(userId);
        List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
        
        for (MaterialLineDTO materialLineDTO : materialLineDTOs) {
            
            MaterialLine materialLine = materialServices.updateMaterialLine(materialLineDTO, null, "release", false, userId, null, null, null, null);
            materialLine.setConfirmationText(confirmationText);
            if (userDTO != null) {
                MaterialLineStatusHelper.createTraceabilityLog(materialLine, traceabilityRepository, "Released", null, userId, userDTO.getUserName(), null);
            }
            materialLines.add(materialLine);
        }
        //trace all the released lines for material
        if (userDTO != null) {
            for (MaterialLine materialLine : materialLines) {
                for (MaterialLine matMerialLine : materialLine.getMaterial().getMaterialLine()) {
                    String actionDetail = materialLine.getConfirmationText();
                    if (!(matMerialLine.getStatus().equals(MaterialLineStatus.REMOVED) || matMerialLine.getStatus().equals(MaterialLineStatus.REMOVED_DB))) {
                        if (matMerialLine.getMaterialLineOID() != materialLine.getMaterialLineOID()) {
                            MaterialLineStatusHelper.createTraceabilityLog(matMerialLine, traceabilityRepository, "Released", actionDetail, userId,
                                                                           userDTO.getUserName(), null);
                        }
                    }
                }
            }
        }
        return materialLines;
    }

    public static MaterialLine releaseGoods(MaterialLineDTO materialLineDTO, ProcurementServices procurementServices,
            MaterialHeaderRepository requestHeaderRepository) throws GloriaApplicationException {
        Long materialLineId = materialLineDTO.getId();
        MaterialLine materialLine = requestHeaderRepository.findMaterialLineById(materialLineId);

        if (materialLine == null) {
            LOGGER.error("No Material line objects exists for id : " + materialLineId);
            throw new GloriaSystemException("This operation cannot be performed. No material line objects exists for id : " + materialLineId,
                                            GloriaExceptionConstants.INVALID_DATASET_OID);
        }

        if (materialLineDTO.getVersion() != materialLine.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                 "This operation cannot be performed since the information has been updated.");
        }

        Material material = materialLine.getMaterial();
        
        material.getMaterialType().release(material, material.getProcureLine());
        reEvaluateProcureTypeBasedOnMaterialReleased(material, procurementServices, requestHeaderRepository);
        return requestHeaderRepository.updateMaterialLine(materialLine);
    }

    public static void reEvaluateProcureTypeBasedOnMaterialReleased(Material material, ProcurementServices procurementServices,
            MaterialHeaderRepository requestHeaderRepository) throws GloriaApplicationException {

        List<ProcureLine> procureLines = procurementServices.findProcureLinesByPartInformation(material.getPartNumber(), material.getPartVersion(),
                                                                                     material.getPartAffiliation(), material.getPartModification(),
                                                                                     ProcureLineStatus.WAIT_TO_PROCURE);

        if (procureLines != null && !procureLines.isEmpty()) {
            for (ProcureLine procureLine : procureLines) {
                procurementServices.setProcureLineProcureType(procureLine, procureLine.getMaterials());
            }
        }
    }

    public static MaterialLine updateMaterialLine(MaterialLineDTO materialLineDTO, MaterialHeaderRepository requestHeaderRepository, UserDTO user,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        Long materialLineId = materialLineDTO.getId();
        MaterialLine materialLine = requestHeaderRepository.findMaterialLineById(materialLineId);

        if (materialLine == null) {
            LOGGER.error("No Material line objects exists for id : " + materialLineId);
            throw new GloriaSystemException("This operation cannot be performed. No material line objects exists for id : " + materialLineId,
                                            GloriaExceptionConstants.INVALID_DATASET_OID);
        }

        if (materialLineDTO.getVersion() != materialLine.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                 "This operation cannot be performed since the information has been updated.");
        }

        materialLine.setQuantity(materialLineDTO.getQuantity());
        materialLine.setExpirationDate(materialLineDTO.getExpirationDate());
        MaterialLineStatusHelper.updateMaterialLineStatus(materialLine, MaterialLineStatus.valueOf(materialLineDTO.getStatus()), "Status updated",
                                                          GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE, user, traceabilityRepository, false);
        return requestHeaderRepository.updateMaterialLine(materialLine);
    }

    public static void updatePlacement(MaterialLine materialLine, BinLocation binLocation, long quantity, WarehouseServices warehouseServices,
            MaterialServices materialServices) throws GloriaApplicationException {

        BinlocationBalance binlocationBalance = createOrUpdateBinlocationBalance(materialLine.getMaterial(), quantity, binLocation, warehouseServices);
        Placement placement = warehouseServices.getPlacementByMaterialLine(materialLine.getMaterialLineOID());
        if (placement == null) {
            materialServices.createPlacement(binLocation.getBinLocationOid(), materialLine);
            placement = warehouseServices.getPlacementByMaterialLine(materialLine.getMaterialLineOID());
        }
        placement.setBinLocation(binLocation);
        placement.setBinlocationBalance(binlocationBalance);

        warehouseServices.updatePlacement(placement);

        Zone zone = binLocation.getZone();
        materialLine.setZoneType(zone.getType());
        materialLine.setZoneCode(zone.getCode());
        materialLine.setZoneName(zone.getName());

        StorageRoom storageRoom = zone.getStorageRoom();
        materialLine.setStorageRoomCode(storageRoom.getCode());
        materialLine.setStorageRoomName(storageRoom.getName());

        materialLine.setBinLocationCode(binLocation.getCode());
    }

    public static BinlocationBalance createOrUpdateBinlocationBalance(Material material, long quantity, BinLocation binLocation,
            WarehouseServices warehouseServices) {
        Warehouse warehouse = binLocation.getZone().getStorageRoom().getWarehouse();
        BinlocationBalance binlocationBalance = warehouseServices.getBinlocationBalance(material.getPartAffiliation(), material.getPartNumber(),
                                                                                        material.getPartVersion(), material.getPartModification(),
                                                                                        binLocation.getCode(), warehouse.getSiteId());

        if (binlocationBalance != null) {
            binlocationBalance.setQuantity(binlocationBalance.getQuantity() + quantity);
        } else {
            binlocationBalance = new BinlocationBalance();
            binlocationBalance.setPartAffiliation(material.getPartAffiliation());
            binlocationBalance.setPartModification(material.getPartModification());
            binlocationBalance.setPartNumber(material.getPartNumber());
            binlocationBalance.setPartName(material.getPartName());
            binlocationBalance.setPartVersion(material.getPartVersion());
            binlocationBalance.setQuantity(quantity);
            binlocationBalance.setBinLocation(binLocation);
            binlocationBalance.setWarehouse(warehouse);
        }
        return warehouseServices.saveOrUpdateBinlocationBalance(binlocationBalance);
    }

    public static PickList createPickList(List<RequestGroup> requestGroups, MaterialServices requestHeaderRepository, UserDTO user,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        PickList pickList = new PickList();
        pickList.setStatus(PickListStatus.CREATED);
        // generate next sequence
        requestHeaderRepository.savePickList(pickList);
        pickList = requestHeaderRepository.findPickListById(pickList.getPickListOid());
        
        pickList.setCode(ProcurementServicesHelper.getUniquePickListCodeString(pickList.getPickListCodeSequence()));
        for (RequestGroup requestGroup : requestGroups) {
            List<MaterialLine> pickMaterialLines = new ArrayList<MaterialLine>();
            for (MaterialLine materialLine : requestGroup.getMaterialLines()) {
                if (!materialLine.isRequestedExcluded() && !materialLine.getStatus().equals(MaterialLineStatus.MISSING)) {
                    pickMaterialLines.add(materialLine);
                    MaterialLineStatusHelper.updateMaterialLineStatus(materialLine, MaterialLineStatus.REQUESTED, null,
                                                                      GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE, user, traceabilityRepository,
                                                                      false);
                    MaterialLineStatusHelper.createTraceabilityLog(materialLine, traceabilityRepository, "Pick List Created",
                                                                   "Pick List Code : " + pickList.getCode(), user.getId(), user.getUserName(),
                                                                   GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE);
                }
            }
            pickList.getMaterialLines().addAll(pickMaterialLines);
            pickList.getRequestGroups().add(requestGroup);
        }
        return requestHeaderRepository.savePickList(pickList);
    }

    public static void shipRequestList(RequestListDTO requestListDTO, RequestList requestList, String action, UserDTO user,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices, MaterialHeaderRepository requestHeaderRepository)
            throws GloriaApplicationException {
        if ("ship".equalsIgnoreCase(action)) {
            if (requestList.getStatus() != RequestListStatus.READY_TO_SHIP) {
                throw new GloriaApplicationException(requestList.getRequestListOid(), null, GloriaExceptionConstants.REQUEST_LIST_NOT_IN_READYTOSHIP_STATE,
                                                     "Request list is not in Ready_TO_Ship state", null);
            }
            if (StringUtils.isEmpty(requestListDTO.getDispatchNoteNumber())) {
                throw new GloriaApplicationException(requestList.getRequestListOid(), null, GloriaExceptionConstants.DISPATCH_NO_MISSING_FOR_REQUEST_LIST,
                                                     "Dispatch Note Number missing for requestlist", null);
            }
            requestList.setStatus(RequestListStatus.SHIPPED);

            updateMaterialLineForRequestList(requestList, user, traceabilityRepository, warehouseServices, requestHeaderRepository);
        }
    }

    static void updateMaterialLineForRequestList(RequestList requestList, UserDTO user, TraceabilityRepository traceabilityRepository,
            WarehouseServices warehouseServices, MaterialHeaderRepository requestHeaderRepository) throws GloriaApplicationException {
        for (RequestGroup requestGroup : requestList.getRequestGroups()) {
            List<MaterialLine> materialLines = requestGroup.getMaterialLines();
            shipOrTransferAllNonDeviatedAndNotMissingMaterialLines(requestList, user, traceabilityRepository, warehouseServices, requestHeaderRepository,
                                                                   materialLines);
        }
    }

    private static void shipOrTransferAllNonDeviatedAndNotMissingMaterialLines(RequestList requestList, UserDTO user,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices, MaterialHeaderRepository requestHeaderRepository,
            List<MaterialLine> materialLines) throws GloriaApplicationException {
        for (int idx = materialLines.size() - 1; idx >= 0; idx--) {
            MaterialLine materialLine = materialLines.get(idx);
            if (!materialLine.getStatus().equals(MaterialLineStatus.DEVIATED) && !materialLine.getStatus().equals(MaterialLineStatus.MISSING)) {
                removePlacement(materialLine, warehouseServices);
                if (requestList.getShipmentType().equals(ShipmentType.TRANSFER)) {
                    MaterialLineStatusHelper.merge(materialLine, MaterialLineStatus.IN_TRANSFER, requestHeaderRepository, traceabilityRepository, user, null);
                } else {
                    MaterialLineStatusHelper.merge(materialLine, MaterialLineStatus.SHIPPED, requestHeaderRepository, traceabilityRepository, user, null);
                }
            }
        }
    }

    public static void removePlacement(MaterialLine materialLine, WarehouseServices warehouseServices) throws GloriaApplicationException {
        Placement placement = warehouseServices.getPlacementByMaterialLine(materialLine.getMaterialLineOID());

        // the MaterialLine oid is not set, shall be fixed in an another way
        if (placement == null) {
            placement = warehouseServices.getPlacementByMaterialLine(0L);
        }

        if (placement != null) {
            BinlocationBalance binlocationBalance = placement.getBinlocationBalance();
            if (binlocationBalance != null) {
                binlocationBalance.setQuantity(Math.max(binlocationBalance.getQuantity() - materialLine.getQuantity(), 0L));
                warehouseServices.saveOrUpdateBinlocationBalance(binlocationBalance);
                binlocationBalance.getPlacements().remove(placement);
            }
            placement.setBinlocationBalance(null);
            warehouseServices.deletePlacement(placement.getPlacementOid());
        }

        materialLine.setZoneType(null);
        materialLine.setZoneCode(null);
        materialLine.setZoneName(null);
        materialLine.setStorageRoomCode(null);
        materialLine.setStorageRoomName(null);
        materialLine.setBinLocationCode(null);
        materialLine.setPlacementOID(null);
    }

    public static MaterialLine requestGoods(MaterialLineDTO materialLineDTO, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, MaterialServices materialServices, UserDTO user, WarehouseServices warehouseServices,
            String deliveryAddressType, String deliveryAddressId, String deliveryAddressName, CommonServices commonServices) throws GloriaApplicationException {
        MaterialLine materialLine = requestHeaderRepository.findMaterialLineById(materialLineDTO.getId());

        if (materialLine == null) {
            LOGGER.error("No Material line objects exists for id : " + materialLineDTO.getId());
            throw new GloriaSystemException("This operation cannot be performed. No material line objects exists for id : " + materialLineDTO.getId(),
                                            GloriaExceptionConstants.INVALID_DATASET_OID);
        }

        /*if (materialLineDTO.getVersion() != materialLine.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                 "This operation cannot be performed since the information has been updated.");
        }*/

        if (materialLineDTO.getPossiblePickQuantity() > materialLineDTO.getQuantity().longValue()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_POSSIBLE_PICK_QUANTITY,
                                                 "Pick quantity cannot be greater than actual quantity.");
        }

        
        String deliveryFullAddress = getDeliveryAddress(deliveryAddressType, deliveryAddressId, deliveryAddressName, commonServices, materialLine);
        
        materialLine.setConfirmationText(deliveryFullAddress);
            
        if (materialLine.getStatus() != MaterialLineStatus.REQUISITION_SENT && materialLine.getStatus() != MaterialLineStatus.ORDER_PLACED_EXTERNAL
                && materialLine.getStatus() != MaterialLineStatus.ORDER_PLACED_INTERNAL) {
            long partialPullQuantity = materialLine.getQuantity() - materialLineDTO.getPossiblePickQuantity();
            BinLocation materialLineBinLocation = null;
            // @Comment : Remove the placement for materialLine before Split and after split createPlacements for right quantity.
            if (partialPullQuantity > 0) {
                Placement placement = warehouseServices.getPlacement(materialLine.getPlacementOID());
                if (placement != null) {
                    materialLineBinLocation = placement.getBinLocation();
                }
                MaterialServicesHelper.removePlacement(materialLine, warehouseServices);
            }
 
            List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
            avoidTraceForMLStatus.add(materialLine.getStatus());
            
            // derefernce the request group before split-- clone method will copy thus to splitted line otherwise
            RequestGroup requestGroup = materialLine.getRequestGroup();
            requestGroup.getMaterialLines().remove(materialLine);
            materialLine.setRequestGroup(null);
            
            MaterialLine requestedMaterialLine = MaterialLineStatusHelper.split(materialLine, MaterialLineStatus.REQUESTED,
                                                                                materialLineDTO.getPossiblePickQuantity(), requestHeaderRepository,
                                                                                traceabilityRepository, materialServices, user, avoidTraceForMLStatus);

            // associate the splitted line back to request group
            requestedMaterialLine.setRequestGroup(requestGroup);
            requestGroup.getMaterialLines().add(requestedMaterialLine);
            
            // @Comment: If partialPullQuantity = 0 then the placements are handled inside the split method.
            if (partialPullQuantity > 0) {
                materialServices.createPlacement(materialLineBinLocation, materialLine);
                materialServices.createPlacement(materialLineBinLocation, requestedMaterialLine);
            }
            
            MaterialLineStatusHelper.createTraceabilityLog(materialLine, traceabilityRepository, materialLine.getStatus().name(), null, user.getId(),
                                                           user.getUserName(), null);
            requestedMaterialLine.getMaterialLineStatusTime().setRequestedQty(materialLineDTO.getPossiblePickQuantity());
            return requestedMaterialLine;
        }
        List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
        avoidTraceForMLStatus.add(materialLine.getStatus());
        MaterialLine materialLineReturn = MaterialLineStatusHelper.split(materialLine, materialLineDTO.getPossiblePickQuantity(),
                  requestHeaderRepository, traceabilityRepository, materialServices, user, avoidTraceForMLStatus);
        
        materialLineReturn.getMaterialLineStatusTime().setRequestedQty(materialLineDTO.getPossiblePickQuantity());

        if (!materialLineReturn.getStatus().equals(MaterialLineStatus.REMOVED_DB)) {
            MaterialLineStatusHelper.createTraceabilityLog(materialLineReturn, traceabilityRepository, "Requested", materialLineReturn.getConfirmationText(),
                                                           user.getId(), user.getUserName(), GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE);
        }
        if (!materialLine.getStatus().equals(MaterialLineStatus.REMOVED_DB)) {
            String action = materialLine.getStatus() == MaterialLineStatus.ORDER_PLACED_INTERNAL ? "Placed Internal" : materialLine.getStatus().name();
            MaterialLineStatusHelper.createTraceabilityLog(materialLine, traceabilityRepository, action, null, user.getId(), user.getUserName(),
                                                           GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE);
        }
        return materialLineReturn;
    }

    public static String getDeliveryAddress(String deliveryAddressType, String deliveryAddressId, String deliveryAddressName, CommonServices commonServices,
            MaterialLine materialLine) {
        String updatedDeliveryAddressID = null;
        String updatedDeliveryAddressName = null;
        String deliveryAddress = null;
        MaterialHeader materialHeader = materialLine.getMaterial().getMaterialHeader();
        if (materialHeader != null) {
            updatedDeliveryAddressID = materialHeader.getAccepted().getOutboundLocationId();
        }
        updatedDeliveryAddressName = RequestGroupHelper.getSiteName(commonServices, updatedDeliveryAddressID);

        if (!StringUtils.isEmpty(deliveryAddressType) && deliveryAddressType.equals(DeliveryAddressType.NEW_DELIVERY_ADDRESS.name())) {
            updatedDeliveryAddressID = "";
            updatedDeliveryAddressName = deliveryAddressName;
        } else if (!StringUtils.isEmpty(deliveryAddressType) && deliveryAddressType.equals(DeliveryAddressType.WH_SITE.name())) {
            updatedDeliveryAddressID = deliveryAddressId;
            updatedDeliveryAddressName = deliveryAddressName;
        }

        Site deliverySite = commonServices.getSiteBySiteId(updatedDeliveryAddressID);
        if (deliverySite != null) {
            deliveryAddress = deliverySite.getAddress();
        }

        return MaterialTransformHelper.getSiteAddress(updatedDeliveryAddressID, updatedDeliveryAddressName, deliveryAddress);
    }

    public static MaterialLine pickGoods(MaterialLineDTO materialLineDTO, UserDTO user, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices, MaterialServices materialServices)
            throws GloriaApplicationException {
        Long materialLineId = materialLineDTO.getId();
        MaterialLine materialLine = requestHeaderRepository.findMaterialLineById(materialLineId);

        if (materialLine == null) {
            LOGGER.error("No Material line objects exists for id : " + materialLineId);
            throw new GloriaSystemException("This operation cannot be performed. No material line objects exists for id : " + materialLineId,
                                            GloriaExceptionConstants.INVALID_DATASET_OID);
        }

        if (materialLineDTO.getVersion() != materialLine.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                 "This operation cannot be performed since the information has been updated.");
        }

        long pickedQuantity = materialLineDTO.getPickedQuantity();
        if (pickedQuantity < 0) {
            throw new GloriaApplicationException(GloriaExceptionConstants.NEGATIVE_NUMBER,
                                                 "This operation cannot be performed. Actual Picked quantity is a negative number");
        }

        Long materialLineQuantity = materialLine.getQuantity();
        if (pickedQuantity > materialLineQuantity) {
            throw new GloriaApplicationException("pickedQuantity", GloriaExceptionConstants.INVALID_ACTUAL_PICK_QUANTITY,
                                                 "The actual Pick qty is more than Pull qty.", null);
        }
        String binLocationCode = materialLine.getBinLocationCode();
        long deviationQty = materialLineQuantity - pickedQuantity;
        handleDeviationOnPick(materialLineDTO, materialLine, deviationQty, warehouseServices, requestHeaderRepository, traceabilityRepository,
                              materialServices, user);

        moveMaterialsToShipment(user, traceabilityRepository, materialServices, materialLine, pickedQuantity, 
                                materialServices.getMaterialLineStatusCounterRepository());

        MaterialLineStatusHelper.createTraceabilityLog(materialLine, traceabilityRepository, "Picked", "Bin Location : " + binLocationCode, user.getId(),
                                                       user.getUserName(), null);
        MaterialLineStatusTime materialLineStatusTime = materialLine.getMaterialLineStatusTime();
        if (materialLineStatusTime != null) {
            materialLineStatusTime.setPickedTime(new Timestamp(System.currentTimeMillis()));
            materialLineStatusTime.setPickedQty(pickedQuantity);

            materialLineStatusTime.setPickedStorageRoom(materialLine.getStorageRoomName());
            materialLineStatusTime.setPickedBinLocation(materialLine.getBinLocationCode());
        }
        return requestHeaderRepository.updateMaterialLine(materialLine);
    }

    public static void moveMaterialsToShipment(UserDTO user, TraceabilityRepository traceabilityRepository, MaterialServices materialServices,
            MaterialLine materialLine, long pickedQuantity, MaterialLineStatusCounterRepository materialLineStatusCounterRepository) 
                    throws GloriaApplicationException {
        if (pickedQuantity > 0) {

            materialServices.placeIntoZone(materialLine, ZoneType.SHIPPING);
            MaterialLineStatusHelper.updateMaterialLineStatus(materialLine, MaterialLineStatus.READY_TO_SHIP, "Ready to ship",
                                                              GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE, user, traceabilityRepository, false);
            materialLineStatusCounterRepository.createAndSave(materialLine, MaterialLineStatusCounterType.PICKS); 
            PickList pickList = materialLine.getPickList();
            if (pickList != null && isPicklistPicked(pickList)) {
                pickList.setStatus(PickListStatus.PICKED);
                pickList.setPulledByUserId(user.getId());
            }

            RequestGroup requestGroup = materialLine.getRequestGroup();
            if (requestGroup != null) {
                materialServices.checkAndUpdateRequestListStatusAsPickCompleted(requestGroup.getRequestList());
            }
        }
    }

    public static boolean isPicklistPicked(PickList pickList) {
        for (MaterialLine materialLine : pickList.getMaterialLines()) {
            if (!materialLine.getStatus().isPicked()) {
                return false;
            }
        }
        return true;
    }

    public static long updateDeviation(long deviationQty, MaterialLine materialLine, PickList pickList, RequestGroup requestGroup, Deviation deviation,
            String binlocation, WarehouseServices warehouseServices, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, UserDTO userDTO, MaterialServices materialServices) throws GloriaApplicationException {
        if (materialLine != null) {
            deviationQty = handleDeviatedMaterialLines(deviationQty, materialLine, pickList, requestGroup, deviation, requestHeaderRepository,
                                                       traceabilityRepository, userDTO, materialServices, warehouseServices);
            setDeviationToBinlocationBalance(materialLine, deviation, binlocation, warehouseServices);
        }
        return deviationQty;
    }

    public static void setDeviationToBinlocationBalance(MaterialLine materialLine, Deviation deviation, String binlocation, 
            WarehouseServices warehouseServices) {
        Material material = materialLine.getMaterial();
        BinlocationBalance binlocationBalance = warehouseServices.getBinlocationBalance(material.getPartAffiliation(), material.getPartNumber(),
                                                                                        material.getPartVersion(), material.getPartModification(), binlocation,
                                                                                        materialLine.getWhSiteId());
        if (binlocationBalance != null) {
            binlocationBalance.setDeviation(deviation);
        }
    }

    private static long handleDeviatedMaterialLines(long deviationQty, MaterialLine materialLine, PickList pickList, RequestGroup requestGroup,
            Deviation deviation, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, UserDTO userDTO,
            MaterialServices materialServices, WarehouseServices warehouseServices) throws GloriaApplicationException {
        Placement placement = warehouseServices.getPlacement(materialLine.getPlacementOID());
        BinLocation materialLineBinLocation = null;
        if (placement != null) {
            materialLineBinLocation = placement.getBinLocation();
        }

        MaterialServicesHelper.removePlacement(materialLine, warehouseServices);
        if (deviation != null && deviationQty > 0) {
            long deviatedMaterialLineQuantity = Math.min(materialLine.getQuantity(), deviationQty);

            List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
            avoidTraceForMLStatus.add(MaterialLineStatus.REQUESTED);
            MaterialLine deviatedMaterialLine = MaterialLineStatusHelper.split(materialLine, MaterialLineStatus.DEVIATED, deviatedMaterialLineQuantity,
                                                                               requestHeaderRepository, traceabilityRepository, materialServices, userDTO,
                                                                               avoidTraceForMLStatus);

            materialServices.createPlacement(materialLineBinLocation, deviatedMaterialLine);

            deviatedMaterialLine.setRequestGroup(null);
            deviatedMaterialLine.setPickList(null);
            if (pickList != null) {
                pickList.getMaterialLines().remove(deviatedMaterialLine);
            }
            return deviationQty - deviatedMaterialLineQuantity;
        }
        return deviationQty;
    }

    public static MaterialLine storeGoods(MaterialLineDTO materialLineDTO, UserDTO user, MaterialHeaderRepository requestHeaderRepository,
            MaterialServices materialServices, TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices, String whSiteId)
            throws GloriaApplicationException {
        String status = MaterialLineStatus.READY_TO_STORE.toString() + "," + MaterialLineStatus.REQUESTED.toString();
        List<MaterialLine> readyToStoreParts = requestHeaderRepository.findMaterialsByPartStatusSiteAndTransportLabel(materialLineDTO.getpPartNumber(),
                                                                                                                      materialLineDTO.getpPartVersion(),
                                                                                                                      status, whSiteId,
                                                                                                                      materialLineDTO.getpPartModification(),
                                                                                                                      materialLineDTO.getpPartAffiliation(),
                                                                                                                      materialLineDTO.getTransportLabel());
        long storedQty = materialLineDTO.getStoredQuantity();
        if (storedQty < 0) {
            throw new GloriaApplicationException(GloriaExceptionConstants.NEGATIVE_NUMBER,
                                                 "This operation cannot be performed. Additional quantity is a negative number");
        }

        String tLabel = checkValues(materialLineDTO.getTransportLabel());
        BinLocation binLocation = warehouseServices.findBinLocationById(materialLineDTO.getBinlocation());

        for (MaterialLine materialLine : readyToStoreParts) {
            if (!tLabel.equals(checkValues(materialLine.getTransportLabelCode()))) {
                continue;
            }
            if (storedQty < 1) {
                break;
            }
            Placement placement = warehouseServices.getPlacement(materialLine.getPlacementOID());
            BinLocation materialLineBinLocation = null;
            if (placement != null) {
                materialLineBinLocation = placement.getBinLocation();
            }

            MaterialServicesHelper.removePlacement(materialLine, warehouseServices);

            MaterialLineStatus storedStatus = MaterialLineStatus.STORED;
            if (materialLine.getStatus().equals(MaterialLineStatus.REQUESTED)) {
                storedStatus = MaterialLineStatus.REQUESTED;
            }

            materialLine.setReservedUserId(null);
            materialLine.setReservedTimeStamp(null);

            if (materialLine.getQuantity() <= storedQty) {
                storedQty = storedQty - materialLine.getQuantity();
                materialLine.setBinLocationCode(binLocation.getCode());
                materialLine = MaterialLineStatusHelper.merge(materialLine, storedStatus, materialLine.getQuantity(), requestHeaderRepository,
                                                              traceabilityRepository, user, null);
                materialServices.createPlacement(binLocation, materialLine);
                materialLine.getStatus().updateZoneToRequestGroup(materialLine);
                updateMaterialLineStatusTime(materialLine);

            } else {
                List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
                avoidTraceForMLStatus.add(storedStatus);
                MaterialLine splittedLine = MaterialLineStatusHelper.split(materialLine, storedStatus,
                                                                           materialServices.getBinLocationCode(materialLineDTO.getBinlocation()), storedQty,
                                                                           requestHeaderRepository, traceabilityRepository, user, avoidTraceForMLStatus);
                materialServices.createPlacement(materialLineBinLocation, materialLine);
                materialServices.createPlacement(binLocation, splittedLine);
                splittedLine.getStatus().updateZoneToRequestGroup(splittedLine);
                MaterialLineStatusHelper.merge(splittedLine, storedStatus, requestHeaderRepository, traceabilityRepository, user, null);
                storedQty = 0;
                updateMaterialLineStatusTime(splittedLine);
            }
        }
        return null;
    }

    public static void updateMaterialLineStatusTime(MaterialLine materialLine) {
        materialLine.getMaterialLineStatusTime().setStoredTime(new Timestamp(System.currentTimeMillis()));
        materialLine.getMaterialLineStatusTime().setStoredQty(materialLine.getQuantity());
        materialLine.getMaterialLineStatusTime().setStoredStorageRoom(materialLine.getStorageRoomName());
        materialLine.getMaterialLineStatusTime().setStoredBinLocation(materialLine.getBinLocationCode());        
    }

    static void doMoveMaterialLines(long moveQuantity, String binLocationCode, List<MaterialLine> fetchedMaterialLines, List<MaterialLine> materialLines,
            UserDTO user, WarehouseServices warehouseServices, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository,
            MaterialServices materialServices) throws GloriaApplicationException {
        BinLocation binLocation = warehouseServices.findBinLocationByCode(binLocationCode, null);

        long currentMovedQty = moveQuantity;

        for (MaterialLine materialLine : materialLines) {
            if (currentMovedQty > 0) {
                Placement placement = warehouseServices.getPlacement(materialLine.getPlacementOID());
                BinLocation materialLineBinLocation = null;
                if (placement != null) {
                    materialLineBinLocation = placement.getBinLocation();
                }

                Long materialLineQty = materialLine.getQuantity();
                long toMoveQuantity = Math.min(currentMovedQty, materialLineQty);
                currentMovedQty = currentMovedQty - toMoveQuantity;
                MaterialServicesHelper.removePlacement(materialLine, warehouseServices);
                List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
                avoidTraceForMLStatus.add(materialLine.getStatus());
                MaterialLine splittedMaterialLine = MaterialLineStatusHelper.split(materialLine, toMoveQuantity, requestHeaderRepository,
                                                                                   traceabilityRepository, user, avoidTraceForMLStatus);
                materialServices.createPlacement(binLocation, splittedMaterialLine);
                if (materialLine.getQuantity() > 0) {
                    materialServices.createPlacement(materialLineBinLocation, materialLine);
                }
                
                MaterialLineStatusHelper.createTraceabilityLog(materialLine, traceabilityRepository, materialLine.getStatus().name(), null, user.getId(),
                                                               user.getUserName(), null);
                MaterialLineStatusHelper.createTraceabilityLog(splittedMaterialLine, traceabilityRepository, splittedMaterialLine.getStatus().name(), null,
                                                               user.getId(), user.getUserName(), null);
                fetchedMaterialLines.add(splittedMaterialLine);
            } else {
                break;
            }
        }
    }

    static List<MaterialLine> updateScrapMaterialLines(long scrapQuantity, List<MaterialLine> materialLines, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, MaterialServices materialServices, 
            WarehouseServices warehouseServices, UserDTO user, String confirmationText)
            throws GloriaApplicationException {
        List<MaterialLine> scrappedMaterialLines = new ArrayList<MaterialLine>();
        if (materialLines != null) {

            for (MaterialLine materialLine : materialLines) {
                materialLine.setConfirmationText(confirmationText);
                Material material = materialLine.getMaterial();
                long materialLineOID = materialLine.getMaterialLineOID();
                if (!material.getMaterialType().isScrappable()) {
                    LOGGER.error(" Material line objects is not of Common Type : " + materialLineOID);
                    throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_MATERIAL_TYPE,
                                                         "Scrap operation cannot be performed. Material line is not of Common Type : " + materialLineOID);
                }

                BinLocation materialLineBinLocation = MaterialLineStatusHelper.removePlacement(warehouseServices, materialLine);

                // scrap all lines irrespective of Quantity
                if (materialLines.size() > 1) {
                    scrapQuantity = materialLine.getQuantity();
                }
                // case 1 : When a part of a single line is scrapped
                // case 2 : When single line is scrapped
                // case 3 : Multiple lines are scrapped
                MaterialLine scrappedMaterialLine = materialLine.getStatus().scrap(materialLine, MaterialLineStatus.SCRAPPED, scrapQuantity,
                                                                                   requestHeaderRepository, traceabilityRepository, materialServices, user);
                scrappedMaterialLine.setExpirationDate(DateUtil.getCurrentUTCDateTime());
                scrappedMaterialLines.add(scrappedMaterialLine);
                materialServices.createPlacement(materialLineBinLocation, materialLine);
            }
        }
        return scrappedMaterialLines;
    }

    private static String checkValues(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        return value.trim();
    }

    private static void handleDeviationOnPick(MaterialLineDTO materialLineDTO, MaterialLine materialLine, long deviationQty,
            WarehouseServices warehouseServices, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository,
            MaterialServices materialServices, UserDTO user) throws GloriaApplicationException {
        Deviation deviation = null;
        if (materialLineDTO.isBalanceExceeded()) {
            deviation = Deviation.HIGHER;
        } else if (deviationQty > 0) {
            deviation = Deviation.LOWER;
        }

        updateDeviation(deviationQty, materialLine, materialLine.getPickList(), materialLine.getRequestGroup(), deviation, materialLine.getBinLocationCode(),
                        warehouseServices, requestHeaderRepository, traceabilityRepository, user, materialServices);

    }

    static long calculateAvailableStock(List<MaterialLine> materialLines) {
        long stock = 0;
        if (materialLines != null && !materialLines.isEmpty()) {
            for (MaterialLine materialLine : materialLines) {
                if (materialLine.getStatus() == MaterialLineStatus.STORED) {
                    stock += materialLine.getQuantity();
                }
            }
        }
        return stock;
    }

    static void reduceBinlocationBalance(MaterialLine materialLine, WarehouseServices warehouseServices) {
        // @TODO: as part of state pattern
        // to be called when the state changes from "STORED" to any other state
        Material material = materialLine.getMaterial();
        BinlocationBalance binlocationBalance = warehouseServices.getBinlocationBalance(material.getPartAffiliation(), material.getPartNumber(),
                                                                                        material.getPartVersion(), material.getPartModification(),
                                                                                        materialLine.getBinLocationCode(), materialLine.getWhSiteId());
        if (binlocationBalance != null && (materialLine.getZoneType().equals(ZoneType.STORAGE) || materialLine.getZoneType().equals(ZoneType.TO_STORE))) {
            long qty = binlocationBalance.getQuantity() - materialLine.getQuantity();
            if (qty >= 0) {
                binlocationBalance.setQuantity(qty);
                warehouseServices.saveOrUpdateBinlocationBalance(binlocationBalance);
            } else if (binlocationBalance.getDeviation() == null) {
                warehouseServices.removeBinlocationBalance(binlocationBalance);
            }
        }
    }

    static StringBuilder getActionDetailForScrap(MaterialLineDTO mlDTO) {
        StringBuilder actionDetail = new StringBuilder();
        actionDetail.append("Part No = ");
        actionDetail.append(mlDTO.getpPartNumber());
        actionDetail.append(" Part Version = ");
        actionDetail.append(mlDTO.getpPartVersion());
        actionDetail.append(" Part Name = ");
        actionDetail.append(mlDTO.getpPartName());
        actionDetail.append(" Scrap Date = ");
        actionDetail.append(" List of MaterialLines Scrapped = ");
        return actionDetail;
    }

    public static void doDirectSend(MaterialHeaderRepository requestHeaderRepository, DeliveryNoteLine deliveryNoteLine, List<MaterialLine> materialLines,
            UserDTO user, MaterialServices materialServices, CommonServices commonServices) throws GloriaApplicationException {
        List<MaterialLine> shippableMaterials = new ArrayList<MaterialLine>();
        for (MaterialLine materialLine : materialLines) {
            if (materialLine.getDirectSend().isDirectSend() && materialLine.getStatus().isShippable()) {
                shippableMaterials.add(materialLine);
            }
        }
        if (shippableMaterials != null && !shippableMaterials.isEmpty()) {
            RequestGroupHelper.createRequestListAndGroup(shippableMaterials, user, materialServices, commonServices, true, requestHeaderRepository);
        }
    }

    public static void setRequestListAddress(String deliveryAddressId, String deliveryAddessName, String deliveryAddressType, RequestList requestList,
            CommonServices commonServices) {
        if (!StringUtils.isEmpty(deliveryAddressType)) {
            requestList.setDeliveryAddressType(DeliveryAddressType.valueOf(deliveryAddressType));
            if (requestList.getDeliveryAddressType().equals(DeliveryAddressType.NEW_DELIVERY_ADDRESS)) {
                requestList.setDeliveryAddressId("");
                requestList.setDeliveryAddressName(deliveryAddessName);
            } else if (requestList.getDeliveryAddressType().equals(DeliveryAddressType.WH_SITE)) {
                requestList.setDeliveryAddressId(deliveryAddressId);
                requestList.setDeliveryAddressName(deliveryAddessName);
                for (RequestGroup requestGroup : requestList.getRequestGroups()) {
                    for (MaterialLine materialLine : requestGroup.getMaterialLines()) {
                        materialLine.setFinalWhSiteId(deliveryAddressId);
                    }
                }
            }
            
            Site deliverySite = commonServices.getSiteBySiteId(requestList.getDeliveryAddressId());
            if (deliverySite != null) {
                requestList.setDeliveryAddress(deliverySite.getAddress());
            }
        }
    }

    public static void handleMaterialLineStatus(List<Placement> placements, MaterialServices materialServices, UserDTO user,
            TraceabilityRepository traceabilityRepository) {
        if (placements != null && !placements.isEmpty()) {
            for (Placement placement : placements) {
                MaterialLine materialLine = materialServices.findMaterialLineById(placement.getMaterialLineOID());
                if (materialLine != null && materialLine.getStatus().equals(MaterialLineStatus.DEVIATED)) {
                    if (materialLine.getZoneType().equals(ZoneType.TO_STORE)) {
                        MaterialLineStatusHelper.updateMaterialLineStatus(materialLine, MaterialLineStatus.READY_TO_STORE, "Ready To Store",
                                                                          GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE, user, traceabilityRepository,
                                                                          false);
              } else {
                        MaterialLineStatusHelper.updateMaterialLineStatus(materialLine, MaterialLineStatus.STORED, "Stored",
                                                                          GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE, user, traceabilityRepository,
                                                                          true);
                    }
                }
            }
        }
    }

    public static Material cloneMaterial(Material material, MaterialHeaderRepository requestHeaderRepository, MaterialType materialType, long quantity)
            throws GloriaApplicationException {
        Material newMaterial = new Material();
        newMaterial.setAdd(material.getAdd());
        newMaterial.setCharacteristics(material.getCharacteristics());
        newMaterial.setCreatedDate(DateUtil.getCurrentUTCDateTime());
        newMaterial.setDemarcation(material.getDemarcation());
        newMaterial.setDesignResponsible(material.getDesignResponsible());
        newMaterial.setFinanceHeader(material.getFinanceHeader());
        newMaterial.setFunctionGroup(material.getFunctionGroup());
        newMaterial.setItemToVariantLinkId(material.getItemToVariantLinkId());
        newMaterial.setLinkFunctionGroup(material.getLinkFunctionGroup());
        newMaterial.setLinkFUnctionGroupSuffix(material.getLinkFUnctionGroupSuffix());
        newMaterial.setMailFormId(material.getMailFormId());
        newMaterial.setMtrlRequestVersionAccepted(material.getMtrlRequestVersionAccepted());
        newMaterial.setReceiver(material.getReceiver());
        newMaterial.setMaterialType(materialType);
        newMaterial.setMigrated(material.isMigrated());
        newMaterial.setModularHarness(material.getModularHarness());
        newMaterial.setObjectNumber(material.getObjectNumber());
        newMaterial.setOrderLine(material.getOrderLine());
        newMaterial.setOrderNo(material.getOrderNo());
        newMaterial.setPartAffiliation(material.getPartAffiliation());
        newMaterial.setPartModification(material.getPartModification());
        newMaterial.setPartName(material.getPartName());
        newMaterial.setPartNumber(material.getPartNumber());
        newMaterial.setPartVersion(material.getPartVersion());
        newMaterial.setPartVersionOriginal(material.getPartVersionOriginal());        
        newMaterial.setProcureLinkId(material.getProcureLinkId());
        newMaterial.setProductClass(material.getProductClass());
        newMaterial.setRefAssemblyPartNo(material.getRefAssemblyPartNo());
        newMaterial.setRefAssemblyPartVersion(material.getRefAssemblyPartVersion());
        newMaterial.setRemove(material.getRemove());
        newMaterial.setRequiredStaDate(material.getRequiredStaDate());
        newMaterial.setStatus(material.getStatus());
        newMaterial.setUnitOfMeasure(material.getUnitOfMeasure());        
        newMaterial.setRejectChangeStatus(material.getRejectChangeStatus());
        newMaterial.setMaterialHeader(material.getMaterialHeader());
        ProcureLineHelper.associateMaterialWithProcureLine(newMaterial, material.getProcureLine());
        requestHeaderRepository.addMaterial(newMaterial);
        return newMaterial;
    }

    public static String getSiteAddress(String siteId, String siteName, CommonServices commonServices) {
        String siteAddress = null;
        if (!StringUtils.isEmpty(siteId)) {
            siteAddress = siteId + "-" + siteName;
        } else {
            siteAddress = siteName;
        }
        Site deliverySite = commonServices.getSiteBySiteId(siteId);
        if (deliverySite != null) {
            siteAddress = siteAddress + ", " + deliverySite.getAddress();
        }
        return siteAddress;
    }

    public static String getTransportLabelForMaterialLine(MaterialLine materialLine) {
        String transportLabel = null;
        if (materialLine.getDeliveryNoteLine() != null) {
            DeliveryNoteSubLine deliveryNoteSubLine = materialLine.getDeliveryNoteLine().getSubLine(materialLine.getDirectSend() != DirectSendType.NO);
            if (deliveryNoteSubLine != null && deliveryNoteSubLine.getTransportLabel() != null) {
                transportLabel = deliveryNoteSubLine.getTransportLabel().getCode();
            }
        }
        return transportLabel;
    }

    public static void doStoreQi(List<MaterialLine> approvedMaterialLines, MaterialServices materialServices, UserDTO user,
            TraceabilityRepository traceabilityRepository, DeliveryNoteSubLine deliveryNoteSubLine, WarehouseServices warehouseServices,
            MaterialHeaderRepository requestHeaderRepository) throws GloriaApplicationException {
        for (MaterialLine materialLine : approvedMaterialLines) {
            materialLine.getStatus().storeReceiveAndQi(materialLine, materialServices, user, traceabilityRepository, deliveryNoteSubLine, warehouseServices,
                                                       requestHeaderRepository);
        }

    }

    public static boolean noMaterialsPicked(PickList pickList) {
        for (MaterialLine materialLine : pickList.getMaterialLines()) {
            if (materialLine.getStatus().isPicked()) {
                return false;
            }
        }
        return true;
    }
}
