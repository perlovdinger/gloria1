package com.volvo.gloria.procurematerial.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamDTO;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.SupplierCounterPart;
import com.volvo.gloria.procure.c.dto.ProcureDTO;
import com.volvo.gloria.procurematerial.b.DeliveryServices;
import com.volvo.gloria.procurematerial.b.ProcureTransformer;
import com.volvo.gloria.procurematerial.b.ProcurementDtoTransformer;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.b.RequestTransformer;
import com.volvo.gloria.procurematerial.c.dto.MaterialHeaderDTO;
import com.volvo.gloria.procurematerial.c.dto.ProcureLineDTO;
import com.volvo.gloria.procurematerial.d.entities.DeliverySchedule;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.d.type.internalexternal.InternalExternal;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.procurerequest.c.dto.RequestGatewayDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.InitData;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Loads initial data for RequestToMessage for test object entities from XML file.
 */
public class TestDataRequest extends InitData {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestDataRequest.class);

    private static RequestTransformer requestTransformer = ServiceLocator.getService(RequestTransformer.class);

    private static ProcurementServices procurementServices = ServiceLocator.getService(ProcurementServices.class);

    private static ProcureTransformer procureTransformer = ServiceLocator.getService(ProcureTransformer.class);

    private static CommonServices commonServices = ServiceLocator.getService(CommonServices.class);

    private static UserServices userServices = ServiceLocator.getService(UserServices.class);

    private static DeliveryServices deliveryServices = ServiceLocator.getService(DeliveryServices.class);

    private static ProcurementDtoTransformer procurementDtoTransformer = ServiceLocator.getService(ProcurementDtoTransformer.class);
    

    private static final String TEST_DATA_PROPERTY_KEY = "requesttestobject.data";

    private static final String TEST_DATA_CLASSPATH = "testdata/request/request_FirstAssembly1.xml";

    private static final String TEST_DATA_PROCURE_PROPERTY_KEY = "procure.data";

    private static final String TEST_DATA_PROCURE_CLASSPATH = "testdata/procure/Procure.xml";

    public static void loadTestData(Properties testDataProperties) throws IOException, GloriaApplicationException {

        List<RequestGatewayDTO> requestDTOs = requestTransformer.transformRequest(getMessage(testDataProperties, TEST_DATA_PROPERTY_KEY, TEST_DATA_CLASSPATH));
        procurementServices.manageRequest(requestDTOs);

        String procureInput = getMessage(testDataProperties, TEST_DATA_PROCURE_PROPERTY_KEY, TEST_DATA_PROCURE_CLASSPATH);

        ProcureDTO procureDTO = procureTransformer.transformProcure(procureInput);

        LOGGER.info("Test data for Request test object created.");

        assignProcureRequestsToUser(procureDTO);
    }

    private static void assignProcureRequestsToUser(ProcureDTO procureDTO) {
        List<MaterialHeader> materialHeaders = procurementServices.findAllMaterialHeaders();
        List<MaterialHeaderDTO> procureMaterialHeaderDTOs = new ArrayList<MaterialHeaderDTO>();
        if (materialHeaders != null && !materialHeaders.isEmpty()) {
            MaterialHeader requestHeader = materialHeaders.get(0);
            MaterialHeaderDTO materialHeaderDTO = procurementDtoTransformer.transformAsDTO(requestHeader);
            materialHeaderDTO.setAssignedMaterialControllerId(procureDTO.getAssignedMaterialController());
            procureMaterialHeaderDTOs.add(materialHeaderDTO);
            try {
                procurementServices.assignOrUnassignMateriaHeaders(procureMaterialHeaderDTOs, "assign", procureDTO.getAssignedMaterialController(), "", null,
                                                                   null);
            } catch (GloriaApplicationException e) {
                LOGGER.error("Failed to assign procure requests for user " + procureDTO.getAssignedMaterialController());
            }
        }

        try {
            doProcure(procureDTO);
        } catch (GloriaApplicationException e) {
            LOGGER.error("Failed to procure requests");
        }
    }

    private static void doProcure(ProcureDTO procureDTO) throws GloriaApplicationException {
        UserDTO userDTO = userServices.getUser(procureDTO.getAssignedMaterialController());

        List<ProcureLine> procureLines = procurementServices.findAllProcureLines();
        try {
            if (procureLines != null && !procureLines.isEmpty()) {
                ProcureLine procureLine = procureLines.get(0);

                ProcureLineDTO procureLineDTO = ProcurementHelper.transformAsDTO(procureLine);
                procureLineDTO.setProcureType(selectSource());
                procureLineDTO.setSupplierCounterPartID(selectSupplierCounterPart(userDTO));
                procureLineDTO.setRequiredStaDate(procureDTO.getRequiredSTADate());
                procureLineDTO.setStatus(ProcureLineStatus.PROCURED.name());
                procureLineDTO.setCurrency(procureDTO.getCurrency());
                procureLineDTO.setUnitPrice(procureDTO.getUnitPrice());
                procureLineDTO.setMaxPrice(procureDTO.getMaxPrice());
                procureLineDTO.setBuyerCode(procureDTO.getBuyerCode());
                procureLineDTO.setAdditionalQuantity(procureDTO.getAdditionalQuantity());
                procureLineDTO.setQualityDocumentId(procureLine.getQualityDocumentOID());
                procurementServices.updateProcureLine(procureLineDTO, null, null, null, procureDTO.getAssignedMaterialController(), null, false, null);
                doCreateOrderEntities(procureLine, procureDTO, userDTO);
            }
        } catch (GloriaApplicationException e) {
            LOGGER.error(" error persisting procurement data and order data " + e);
        }
    }

    private static void doCreateOrderEntities(ProcureLine procureLine, ProcureDTO procureDTO, UserDTO userDTO) throws GloriaApplicationException {

        List<Material> materials = procureLine.getMaterials();

        if (materials != null && !materials.isEmpty()) {

            long procurementQty = 0;

            for (Material material : materials) {
                procurementQty += material.getQuantity();
            }

            SupplierCounterPart supplierCounterPart = commonServices.getSupplierCounterPartById(procureLine.getSupplierCounterPartOID());

            Order order = createOrder(procureDTO, userDTO, procureLine, supplierCounterPart);

            OrderLine orderLine = null; 
            createDeliverySchedules(procureLine, procurementQty, orderLine);
            deliveryServices.assignMaterialToOrderLine(procureLine.getRequisitionId(), orderLine);

            deliveryServices.addOrder(order);
        }


    }

    private static void createDeliverySchedules(ProcureLine procureLine, long procurementQty, OrderLine orderLine) {
        List<DeliverySchedule> deliverySchedules = new ArrayList<DeliverySchedule>();
        orderLine.setDeliverySchedule(deliverySchedules);
        DeliverySchedule deliverySchedule = new DeliverySchedule();
        deliverySchedule.setOrderLine(orderLine);
        deliverySchedule.setExpectedDate(procureLine.getRequiredStaDate());
        deliverySchedule.setExpectedQuantity(procureLine.getAdditionalQuantity() + procurementQty);
        deliverySchedules.add(deliverySchedule);
    }

    private static Order createOrder(ProcureDTO procureDTO, UserDTO userDTO, ProcureLine procureLine, SupplierCounterPart supplierCounterPart) {
        Order order = new Order();
        order.setInternalExternal(InternalExternal.EXTERNAL);
        order.setSupplierId(procureDTO.getSupplierId());
        order.setOrderNo("2049-900" + procureLine.getProcureLineOid() + "-");
        order.setOrderDateTime(procureLine.getProcureDate());
        order.setDeliveryControllerTeam(userDTO.getDelFollowUpTeam());
        order.setMaterialUserCategory(procureDTO.getMaterialUserCategory());
        order.setMaterialUserName(procureDTO.getMaterialUserName());
        order.setShipToId(procureDTO.getShipToId());
        order.setOrderDateTime(procureDTO.getRequiredSTADate());
        if (supplierCounterPart != null) {
            order.setOrderNo(order.getOrderNo() + supplierCounterPart.getPpSuffix());
            order.setMaterialUserId(supplierCounterPart.getMaterialUserId());
            order.setSuffix(supplierCounterPart.getPpSuffix());
            order.setShipToId(supplierCounterPart.getShipToId());
        }
        return order;
    }

    private static String selectSource() {
        return ProcureType.EXTERNAL.name();
    }

    private static long selectSupplierCounterPart(UserDTO userDTO) {
        DeliveryFollowUpTeamDTO followUpTeam = commonServices.getDeliveryFollowupTeam(userDTO.getDelFollowUpTeam());
        List<SupplierCounterPart> supplierCounterParts = commonServices.getAllSupplierCounterParts(String.valueOf(followUpTeam.getId()));
        if (supplierCounterParts != null && !supplierCounterParts.isEmpty()) {
            return supplierCounterParts.get(0).getId();
        }
        LOGGER.error("No supplier counter part information available");
        return 0;
    }
}
