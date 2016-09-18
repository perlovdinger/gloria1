package com.volvo.gloria.procurematerial.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.Site;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.beans.MaterialServicesHelper;
import com.volvo.gloria.procurematerial.c.DeliveryAddressType;
import com.volvo.gloria.procurematerial.c.ShipmentType;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestlist.RequestListStatus;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Helper class to group RequestGroup on creating RequestList.
 * 
 * A grouping key is evaluated on MaterialLine entity and the same will be used for grouping
 * 
 */
public final class RequestGroupHelper {

    protected RequestGroupHelper() {

    }

    public static List<RequestList> createRequestListAndGroup(List<MaterialLine> materialLines, UserDTO user, MaterialServices materialServices,
            CommonServices commonServices, boolean isDirectSend, MaterialHeaderRepository requestHeaderRepository) throws GloriaApplicationException {
        List<RequestList> newRequestListsCreated = new ArrayList<RequestList>();
        Set<RequestGroup> previousRequestGroups = new HashSet<RequestGroup>();
        if (materialLines != null && !materialLines.isEmpty()) {
            List<MaterialLine> requestExcludedMaterials = evaluateMaterialLines(materialLines, true);
            List<MaterialLine> nonRequestExcludedMaterials = evaluateMaterialLines(materialLines, false);
            processrReqExcludedMaterials(requestExcludedMaterials, previousRequestGroups, newRequestListsCreated, requestHeaderRepository);
            processNonRequestExcludedMaterials(nonRequestExcludedMaterials, newRequestListsCreated, user, commonServices, isDirectSend, 
                                               requestHeaderRepository);
        }
        removePreviousRequestGroups(materialServices, previousRequestGroups);
        return newRequestListsCreated;
    }
    
    private static List<MaterialLine> evaluateMaterialLines(List<MaterialLine> materialLines, boolean requestExcluded) {
        List<MaterialLine> requestedMaterials = new ArrayList<MaterialLine>();
        for (MaterialLine materialLine : materialLines) {
            if (materialLine.isRequestedExcluded() == requestExcluded && !materialLine.getStatus().equals(MaterialLineStatus.REMOVED_DB)) {
                requestedMaterials.add(materialLine);
            }
        }
        return requestedMaterials;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void processNonRequestExcludedMaterials(List<MaterialLine> materialLines, List<RequestList> newRequestListsCreated, UserDTO user,
            CommonServices commonServices, boolean isDirectSend, MaterialHeaderRepository requestHeaderRepository) throws GloriaApplicationException {
        if (materialLines != null && !materialLines.isEmpty()) {
            Map<String, List<MaterialLine>> finalWhSiteToMaterialLines = groupMaterialLinesByFinalWhSite(materialLines);
            Iterator finalWhSiteToMaterialLinesIterator = finalWhSiteToMaterialLines.entrySet().iterator();
            while (finalWhSiteToMaterialLinesIterator.hasNext()) {
                Map.Entry pair = (Map.Entry) finalWhSiteToMaterialLinesIterator.next();
                List<MaterialLine> materialLinesForCreateRequestList = (List<MaterialLine>) pair.getValue();
                RequestList requestList = createRequestList(materialLinesForCreateRequestList.get(0), user, commonServices, isDirectSend);
                requestList.setRequestGroups(createRequestGroups(materialLinesForCreateRequestList, requestList, null, null));
                if (isDirectSend) {
                    requestList.setStatus(RequestListStatus.PICK_COMPLETED);
                }
                requestHeaderRepository.save(requestList);
                
                //add newly created requestlist
                newRequestListsCreated.add(requestList);
            }
        }
    }

    public static Map<String, List<MaterialLine>> groupMaterialLinesByFinalWhSite(List<MaterialLine> materialLines) {
        Map<String, List<MaterialLine>> finalWhSiteToMaterialLines = new HashMap<String, List<MaterialLine>>();
        for (MaterialLine materialLine : materialLines) {
            String finalWhSiteId = materialLine.getFinalWhSiteId();
            if (!finalWhSiteToMaterialLines.containsKey(finalWhSiteId)) {
                List<MaterialLine> listOfMaterialLines = new ArrayList<MaterialLine>();
                listOfMaterialLines.add(materialLine);
                finalWhSiteToMaterialLines.put(finalWhSiteId, listOfMaterialLines);
            } else {
                finalWhSiteToMaterialLines.get(finalWhSiteId).add(materialLine);
            }
        }
        return finalWhSiteToMaterialLines;
    }

    public static List<RequestGroup> createRequestGroups(List<MaterialLine> materialLines, RequestList requestList, List<RequestGroup> requestGroups,
            Map<String, RequestGroup> groupMap) throws GloriaApplicationException {
        if (requestGroups == null) {
            requestGroups = new ArrayList<RequestGroup>();
        }
        if (groupMap == null) {
            groupMap = new HashMap<String, RequestGroup>();
        }
        for (MaterialLine materialLine : materialLines) {
            setRequestExcludedToMaterialLines(materialLine);
            String groupingKey = materialLine.getGroupIdentifierKey();
            if (!StringUtils.isEmpty(groupingKey)) {
                RequestGroup requestGroup = null;
                if (!groupMap.containsKey(groupingKey)) {
                    requestGroup = createRequestGroup(requestList, requestGroups, materialLine);
                    groupMap.put(groupingKey, requestGroup);
                } else {
                    requestGroup = groupMap.get(groupingKey);
                }
                requestGroup.getMaterialLines().add(materialLine);
                String mtrID = null;
                if (!StringUtils.isEmpty(requestGroup.getChangeRequestIds())) {
                    mtrID = requestGroup.getChangeRequestIds() + ", " + materialLine.getMaterial().getMtrlRequestVersionAccepted();
                } else {
                    mtrID = materialLine.getMaterial().getMtrlRequestVersionAccepted();
                }
                requestGroup.setChangeRequestIds(mtrID);
                materialLine.setRequestGroup(requestGroup);
            }
        }
        return requestGroups;
    }

    public static RequestList addMateriallinesToRequestList(RequestList requestList, List<RequestGroup> existingRequestGroups, List<MaterialLine> materialLines)
            throws GloriaApplicationException {
        Map<String, RequestGroup> groupMap = new HashMap<String, RequestGroup>();
        if (requestList != null && (existingRequestGroups != null && !existingRequestGroups.isEmpty()) && (materialLines != null && !materialLines.isEmpty())) {
            for (RequestGroup requestGroup : existingRequestGroups) {
                if (!groupMap.containsKey(requestGroup.getIdentifierKey())) {
                    groupMap.put(requestGroup.getIdentifierKey(), requestGroup);
                }
            }
            requestList.setRequestGroups(createRequestGroups(materialLines, requestList, existingRequestGroups, groupMap));
        }
        return requestList;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void processrReqExcludedMaterials(List<MaterialLine> requestExcludedMaterials, Set<RequestGroup> previousRequestGroups,
            List<RequestList> newRequestListsCreated, MaterialHeaderRepository requestHeaderRepository) throws GloriaApplicationException {
        if (requestExcludedMaterials != null && !requestExcludedMaterials.isEmpty()) {
            Map<RequestList, List<MaterialLine>> requestListToMaterialLines = prepareNewRequestLists(requestExcludedMaterials, previousRequestGroups);

            Iterator requestListToMaterialLinesIterator = requestListToMaterialLines.entrySet().iterator();
            while (requestListToMaterialLinesIterator.hasNext()) {
                Map.Entry pair = (Map.Entry) requestListToMaterialLinesIterator.next();
                RequestList requestList = (RequestList) pair.getKey();
                List<MaterialLine> materialLines = (List<MaterialLine>) pair.getValue();
                requestList.setRequestGroups(createRequestGroups(materialLines, requestList, null, null));
                requestList.setStatus(RequestListStatus.PICK_COMPLETED);
                requestHeaderRepository.save(requestList);

                // add newly created requestlist
                newRequestListsCreated.add(requestList);
            }
        }
    }

    public static Map<RequestList, List<MaterialLine>> prepareNewRequestLists(List<MaterialLine> requestExcludedMaterials,
            Set<RequestGroup> previousRequestGroups) {
        Map<RequestList, List<MaterialLine>> requestListToMaterialLines = new HashMap<RequestList, List<MaterialLine>>();
        Map<Long, RequestList> previousRequestLists = new HashMap<Long, RequestList>();
        for (MaterialLine requestExcludedMaterialLine : requestExcludedMaterials) {
            List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
            RequestGroup previousRequestGroup = requestExcludedMaterialLine.getRequestGroup();
            RequestList previousRequestList = previousRequestGroup.getRequestList();
            dereferenceTheOldRequestGroup(previousRequestGroups, requestExcludedMaterialLine, previousRequestGroup);
            RequestList requestList = null;
            if (previousRequestLists.containsKey(previousRequestList.getRequestListOid())) {
                requestList = previousRequestLists.get(previousRequestList.getRequestListOid());
                requestListToMaterialLines.get(requestList).add(requestExcludedMaterialLine);
            } else {
                requestList = cloneRequestList(previousRequestList);
                previousRequestLists.put(previousRequestList.getRequestListOid(), requestList);
                materialLines.add(requestExcludedMaterialLine);
                requestListToMaterialLines.put(requestList, materialLines);
            }
        }
        return requestListToMaterialLines;
    }

    public static void dereferenceTheOldRequestGroup(Set<RequestGroup> previousRequestGroups, MaterialLine requestExcludedMaterialLine,
            RequestGroup previousRequestGroup) {
        requestExcludedMaterialLine.setRequestGroup(null);
        requestExcludedMaterialLine.setRequestedExcluded(false);
        previousRequestGroup.getMaterialLines().remove(requestExcludedMaterialLine);
        if (!previousRequestGroups.contains(previousRequestGroup)) {
            previousRequestGroups.add(previousRequestGroup);
        }
    }

    public static RequestList cloneRequestList(RequestList previousRequestList) {
        RequestList requestList = new RequestList();
        requestList.setCreatedDate(DateUtil.getCurrentUTCDateTime());
        requestList.setDeliveryAddressId(previousRequestList.getDeliveryAddressId());
        requestList.setDeliveryAddressName(previousRequestList.getDeliveryAddressName());
        requestList.setDeliveryAddressType(previousRequestList.getDeliveryAddressType());
        requestList.setPriority(previousRequestList.getPriority());
        requestList.setRequesterName(previousRequestList.getRequesterName());
        requestList.setRequestUserId(previousRequestList.getRequestUserId());
        requestList.setRequiredDeliveryDate(previousRequestList.getRequiredDeliveryDate());
        requestList.setShipmentType(previousRequestList.getShipmentType());
        requestList.setShipVia(previousRequestList.getShipVia());
        requestList.setStatus(previousRequestList.getStatus());
        requestList.setWhSiteId(previousRequestList.getWhSiteId());
        requestList.setWhSiteName(previousRequestList.getWhSiteName());
        return requestList;
    }

    private static void removePreviousRequestGroups(MaterialServices materialServices, Set<RequestGroup> previousRequestGroups) {
        if (previousRequestGroups != null && !previousRequestGroups.isEmpty()) {
            for (RequestGroup requestGroup : previousRequestGroups) {
                RequestList previousRequestList = requestGroup.getRequestList();
                List<MaterialLine> mLines = requestGroup.getMaterialLines();
                if (mLines == null || mLines.isEmpty()) {
                    previousRequestList.getRequestGroups().remove(requestGroup);
                    materialServices.deleteRequestGroup(requestGroup);
                }
                materialServices.checkAndUpdateRequestListStatusAsPickCompleted(previousRequestList);
            }
        }
    }

    private static void setRequestExcludedToMaterialLines(MaterialLine materialLine)
            throws GloriaApplicationException {
        if (materialLine.getStatus().equals(MaterialLineStatus.ORDER_PLACED_INTERNAL)
                || materialLine.getStatus().equals(MaterialLineStatus.ORDER_PLACED_EXTERNAL)) {
            materialLine.setRequestedExcluded(true);
            materialLine.setDirectSend(DirectSendType.YES_REQUESTED);
        }
    }

    private static RequestGroup createRequestGroup(RequestList requestList, List<RequestGroup> requestGroups, MaterialLine materialLine) {
        Material material = materialLine.getMaterial();
        MaterialHeader materialHeader = material.getMaterialHeader();
        FinanceHeader financeHeader = material.getFinanceHeader();
        RequestGroup requestGroup = new RequestGroup();
        if (financeHeader != null) {
            requestGroup.setProjectId(financeHeader.getProjectId());
        }
        if (materialHeader != null) {            
            requestGroup.setReferenceGroup(materialHeader.getAccepted().getReferenceGroup());
            requestGroup.setReferenceId(materialHeader.getReferenceId());
            requestGroup.setPhase(materialHeader.getBuildId());
        }
        requestGroup.setRequestList(requestList);
        if (materialLine.getPlacementOID() != null && materialLine.getPlacementOID() > 0) {
            requestGroup.setZoneId(materialLine.getZoneCode());
        }
        requestGroups.add(requestGroup);
        return requestGroup;
    }

    private static RequestList createRequestList(MaterialLine materialLine, UserDTO user, CommonServices commonServices, boolean isDirectSend) {
        Material material = materialLine.getMaterial();
        RequestList requestList = new RequestList();
        String whSiteId = materialLine.getWhSiteId();
        requestList.setWhSiteId(whSiteId);
        requestList.setWhSiteName(getSiteName(commonServices, whSiteId));
        String deliveryAddressID = null;

        if (isDirectSend) {
            deliveryAddressID = materialLine.getFinalWhSiteId();
            requestList.setDeliveryAddressType(DeliveryAddressType.WH_SITE);
            requestList.setShipmentType(ShipmentType.TRANSFER);
            materialLine.setConfirmationText(MaterialServicesHelper.getDeliveryAddress(DeliveryAddressType.WH_SITE.name(), deliveryAddressID,
                                                                                       getSiteName(commonServices, deliveryAddressID), commonServices,
                                                                                       materialLine));
        } else {
            MaterialHeader materialHeader = material.getMaterialHeader();
            if (materialHeader != null) {
                deliveryAddressID = materialHeader.getAccepted().getOutboundLocationId();
            }
            requestList.setDeliveryAddressType(DeliveryAddressType.OUTBOUND_LOCATION);
            requestList.setShipmentType(ShipmentType.SHIPMENT);
        }
        requestList.setDeliveryAddressId(deliveryAddressID);
        requestList.setDeliveryAddressName(getSiteName(commonServices, deliveryAddressID));
        requestList.setCreatedDate(DateUtil.getCurrentUTCDateTime());
        requestList.setRequestUserId(user.getId());
        requestList.setRequesterName(user.getUserName());
        requestList.setStatus(RequestListStatus.CREATED);
        return requestList;
    }

    public static String getSiteName(CommonServices commonServices, String siteId) {
        if (siteId != null) {
            Site site = commonServices.getSiteBySiteId(siteId);
            if (site != null) {
                return site.getSiteName();
            }
        }
        return null;
    }
}
