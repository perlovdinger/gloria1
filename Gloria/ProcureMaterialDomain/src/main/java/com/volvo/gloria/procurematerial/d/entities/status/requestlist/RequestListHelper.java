package com.volvo.gloria.procurematerial.d.entities.status.requestlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.beans.MaterialServicesHelper;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.PickList;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusHelper;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.util.RequestGroupHelper;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaFormateUtil;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.tracing.c.GloriaTraceabilityConstants;

public final class RequestListHelper {
    
    private static final String DELIVERY_ADDRESS_REQUIRED_MESSAGE = "New Delivery Address or Transfer To Warehouse "
            + "is required as there is no Build Location available for this request.";
    
    private static final String REQUESTLIST_NOMATCHING_BUILDLOCATION_MESSAGE = "Selected Materiallines doesn't have matching build location.";
    
    private static final String REQUESTLIST_NOMATCHING_WHSITE_MESSAGE = "Selected Materiallines doesn't have matching whsite.";

    private RequestListHelper() {

    }

    @SuppressWarnings("unchecked")
    public static RequestList addToRequestList(RequestList requestList, List<MaterialLineDTO> materialLineDTOs,
            MaterialHeaderRepository materialHeaderRepository) throws GloriaApplicationException {
        if (materialLineDTOs != null && !materialLineDTOs.isEmpty()) {
            List<Long> materialLineOIds = new ArrayList<Long>(CollectionUtils.collect(materialLineDTOs, TransformerUtils.invokerTransformer("getId")));
            List<MaterialLine> materialLines = materialHeaderRepository.findMaterialLines(materialLineOIds);
            if (materialLines != null && !materialLines.isEmpty()) {
                RequestGroupHelper.addMateriallinesToRequestList(requestList, requestList.getRequestGroups(), materialLines);
                for (RequestGroup requestGroup : requestList.getRequestGroups()) { // persist/merge the request groups which were created / updated
                    materialHeaderRepository.saveRequestGroup(requestGroup);
                }
            }
            return materialHeaderRepository.save(requestList);
        }
        return requestList;
    }

    @SuppressWarnings("unchecked")
    public static RequestList removeFromRequestList(RequestList requestList, List<MaterialLineDTO> materialLineDTOs, 
            MaterialHeaderRepository materialHeaderRepository) {
        if (materialLineDTOs != null && !materialLineDTOs.isEmpty()) {
            List<Long> materialLineOIds = new ArrayList<Long>(CollectionUtils.collect(materialLineDTOs, TransformerUtils.invokerTransformer("getId")));
            List<MaterialLine> materialLines = materialHeaderRepository.findMaterialLines(materialLineOIds);
            if (materialLines != null && !materialLines.isEmpty()) {
                for (MaterialLine materialLine : materialLines) {
                    RequestGroup requestGroup = materialLine.getRequestGroup();
                    requestGroup.getMaterialLines().remove(materialLine);
                    materialLine.setRequestGroup(null);
                    materialLine.setDirectSend(DirectSendType.NO);
                    materialLine.setRequestedExcluded(false);
                    if (requestGroup.getMaterialLines().isEmpty()) {
                        requestList.getRequestGroups().remove(requestGroup);
                        materialHeaderRepository.deleteRequestGroup(requestGroup);
                    }
                }
            }
            return materialHeaderRepository.save(requestList);
        }
        return requestList;
    }

    public static void cancel(RequestList requestList, UserDTO userDTO, MaterialHeaderRepository materialHeaderRepository,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        long requestListOid = requestList.getRequestListOid();
        List<PickList> pickListForRequestList = materialHeaderRepository.findPickListByRequestListId(requestListOid);
        if (pickListForRequestList != null && !pickListForRequestList.isEmpty()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.REQUESTLIST_WITH_PICKLIST,
                                                 "Request List cannot be cancelled when pick list is associated with it");
        }

        List<MaterialLine> materialLinesForRequestList = materialHeaderRepository.getMaterialLinesByRequestListId(requestListOid);
        if (materialLinesForRequestList != null && !materialLinesForRequestList.isEmpty()) {
            for (MaterialLine materialLine : materialLinesForRequestList) {
                materialLine.setRequestGroup(null);
                if (requestList.getStatus() == RequestListStatus.SENT) {
                    if (!materialLine.isRequestedExcluded()) {
                        MaterialLineStatusHelper.merge(materialLine, materialLine.getPreviousStatus(), materialHeaderRepository, traceabilityRepository,
                                                       userDTO, Arrays.asList(materialLine.getPreviousStatus()));
                    }                   
                }
                materialLine.setDirectSend(DirectSendType.NO);
                materialLine.setRequestedExcluded(false);
                MaterialLineStatusHelper.createTraceabilityLog(materialLine, traceabilityRepository, "RequestList Cancelled", "RequestListId: "
                        + requestListOid, userDTO.getId(), userDTO.getUserName(), GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE);
            }
            materialHeaderRepository.deleteRequestList(requestList);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static List<RequestList> createRequestList(Date requiredDeliveryDate, Long priority, String deliveryAddressType, String deliveryAddessId,
            String deliveryAddressName, UserDTO user, List<MaterialLineDTO> materialLineDTOs, MaterialServices materialServices, CommonServices commonServices,
            MaterialHeaderRepository materialHeaderRepository) throws GloriaApplicationException {
        validateRequestlistOnCreate(materialLineDTOs, deliveryAddessId, deliveryAddressName);
        List<Long> materialLineOIds = new ArrayList<Long>(CollectionUtils.collect(materialLineDTOs, TransformerUtils.invokerTransformer("getId")));
        List<MaterialLine> materialLines = materialHeaderRepository.findMaterialLines(materialLineOIds);
        List<RequestList> requestLists = RequestGroupHelper.createRequestListAndGroup(materialLines, user, materialServices, commonServices, false,
                                                                                      materialHeaderRepository);
        if (requestLists != null && !requestLists.isEmpty()) {
            for (RequestList requestList : requestLists) {
                requestList.setRequiredDeliveryDate(requiredDeliveryDate);
                requestList.setPriority(priority);
                MaterialServicesHelper.setRequestListAddress(deliveryAddessId, deliveryAddressName, deliveryAddressType, requestList, commonServices);
                materialHeaderRepository.save(requestList);
            }
        }
        return requestLists;
    }
    
    public static RequestList updateRequestList(RequestList requestList, List<MaterialLineDTO> materialLineDTOs, Date requiredDeliveryDate, Long priority,
            String deliveryAddressType, String deliveryAddessId, String deliveryAddressName, CommonServices commonServices,
            MaterialHeaderRepository materialHeaderRepository) throws GloriaApplicationException {
        validateRequestlistOnCreate(materialLineDTOs, deliveryAddessId, deliveryAddressName);
        if (requestList != null) {
            requestList.setRequiredDeliveryDate(requiredDeliveryDate);
            requestList.setPriority(priority);
            materialHeaderRepository.save(requestList);
        }
        return requestList;
    }
    
    public static void sendRequestList(RequestList requestList, Date requiredDeliveryDate, Long priority, String deliveryAddressType, String deliveryAddessId,
            String deliveryAddressName, UserDTO user, List<MaterialLineDTO> materialLineDTOs, MaterialServices materialServices, CommonServices commonServices,
            MaterialHeaderRepository materialHeaderRepository)
            throws GloriaApplicationException {
        validateRequestlistOnCreate(materialLineDTOs, deliveryAddessId, deliveryAddressName);
        requestList.setRequiredDeliveryDate(requiredDeliveryDate);
        requestList.setPriority(priority);
        MaterialServicesHelper.setRequestListAddress(deliveryAddessId, deliveryAddressName, deliveryAddressType, requestList, commonServices);
        materialServices.updateMaterialLines(materialLineDTOs, null, "request", 0, null, user.getId(), null, deliveryAddressType, deliveryAddessId,
                                             deliveryAddressName);
        requestList.setStatus(RequestListStatus.SENT);
        materialHeaderRepository.save(requestList);
    }
    
    public static void validateRequestlistOnCreate(List<MaterialLineDTO> materialLineDTOs, String deliveryAddessId, String deliveryAddressName)
            throws GloriaApplicationException {

        if (StringUtils.isEmpty(deliveryAddessId) && StringUtils.isEmpty(deliveryAddressName)) {
            throw new GloriaApplicationException(GloriaExceptionConstants.DELIVERY_ADDRESS_REQUIRED, DELIVERY_ADDRESS_REQUIRED_MESSAGE);
        }
        
        String warehouseSite = materialLineDTOs.get(0).getWhSiteId();
        if (warehouseSite != null && warehouseSite.equalsIgnoreCase(deliveryAddessId)) {
            throw new GloriaApplicationException(GloriaExceptionConstants.TRANSFER_ADDRESS_SAME_AS_SHIP_TO, null);
        }

        final String buildLocation = materialLineDTOs.get(0).getOutBoundLocationId();
        boolean hasSameBuildLocation = GloriaFormateUtil.hasSameItems(materialLineDTOs, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return StringUtils.defaultString(((MaterialLineDTO) object).getOutBoundLocationId()).equals(StringUtils.defaultString(buildLocation));
            }
        });
        if (!hasSameBuildLocation) {
            throw new GloriaApplicationException(GloriaExceptionConstants.REQUESTLIST_NOMATCHING_BUILDLOCATION, REQUESTLIST_NOMATCHING_BUILDLOCATION_MESSAGE);
        }

        final String whSite = materialLineDTOs.get(0).getWhSiteId();
        boolean hasSameWhSite = GloriaFormateUtil.hasSameItems(materialLineDTOs, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return StringUtils.defaultString(((MaterialLineDTO) object).getWhSiteId()).equals(StringUtils.defaultString(whSite));
            }
        });
        if (!hasSameWhSite) {
            throw new GloriaApplicationException(GloriaExceptionConstants.REQUESTLIST_NOMATCHING_WHSITE, REQUESTLIST_NOMATCHING_WHSITE_MESSAGE);
        }
    }
}
