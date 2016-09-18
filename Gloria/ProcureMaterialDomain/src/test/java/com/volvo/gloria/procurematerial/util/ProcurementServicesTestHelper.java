package com.volvo.gloria.procurematerial.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;

import com.volvo.gloria.authorization.c.TeamType;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamDTO;
import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamFilterDTO;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.SupplierCounterPart;
import com.volvo.gloria.procurematerial.b.ProcurementDtoTransformer;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.b.RequestTransformer;
import com.volvo.gloria.procurematerial.c.ProcureResponsibility;
import com.volvo.gloria.procurematerial.c.dto.MaterialHeaderDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialHeaderGroupingDTO;
import com.volvo.gloria.procurematerial.c.dto.ProcureLineDTO;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.paging.c.PageObject;

public class ProcurementServicesTestHelper {
    private static final String INITDATA_REQUEST_XML_FOR_UPDATE = "globaldataTest/request_FirstAssembly2.xml";
    private static final String INITDATA_REQUEST_XML_1 = "globaldataTest/revert/material_request_1.xml";


    public static PageObject assignProcureRequest(MaterialHeader materialHeader, ProcurementServices procurementServices, ProcurementDtoTransformer procurementDtoTransformer) throws GloriaApplicationException {
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(10);
        MaterialHeaderDTO procureMaterialHeaderDTO = null;

        if (materialHeader == null) {
            pageObject = procurementServices.getRequestHeaders(pageObject, false, "ALL");

            procureMaterialHeaderDTO = (MaterialHeaderDTO) pageObject.getGridContents().get(0);
        } else {
            procureMaterialHeaderDTO = procurementDtoTransformer.transformAsDTO(materialHeader);
        }

        procureMaterialHeaderDTO.setAssignedMaterialControllerId("all");
        List<MaterialHeaderDTO> requestsToAssign = new ArrayList<MaterialHeaderDTO>();
        requestsToAssign.add(procureMaterialHeaderDTO);

        procurementServices.assignOrUnassignMateriaHeaders(requestsToAssign, "assign", "all", "GOT_PE", TeamType.MATERIAL_CONTROL.name(), null);

        pageObject.setPredicates(new HashMap<String, String>());
        pageObject.getPredicates().put("materialControllerUserId", "all");
        return pageObject;
    }

    public static List<ProcureLine> procureExternalRequest(ProcureLineRepository procureLineRepository, ProcurementServices procurementServices)
            throws GloriaApplicationException {
        List<ProcureLine> procureLines = procureLineRepository.findAll();
        List<ProcureLineDTO> procureLineDTOs = ProcurementHelper.transformEntityToDTO(procureLines);

        for (ProcureLineDTO procureLineDTO : procureLineDTOs) {
            procureLineDTO.setStatus(ProcureLineStatus.PROCURED.toString());
            procureLineDTO.setProcureResponsibility(ProcureResponsibility.PROCURER.name());
            procureLineDTO.setBuyerCode("XYZ");
            procureLineDTO.setAdditionalQuantity(5l);
            procureLineDTO.setUnitPrice(2);
            procureLineDTO.setCurrency("SEK");
            procureLineDTO.setMaxPrice(new Double(543D));
            procureLineDTO.setProcureType("EXTERNAL");
            procureLineDTO.setSupplierCounterPartID(0L);
        }

        List<ProcureLine> updateProcureLines = procurementServices.updateProcureLines(procureLineDTOs, "procure", null, "all", null, null);

        Assert.assertEquals(1, updateProcureLines.size());
        return updateProcureLines;
    }

    public static void updateMaterialRequest(ProcurementServices procurementServices, RequestTransformer requestTransformer) throws GloriaApplicationException,
            IOException {
        procurementServices.manageRequest(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(INITDATA_REQUEST_XML_FOR_UPDATE)));
    }
    
    public static DeliveryFollowUpTeamFilterDTO getDTOForSupplierTest(String ppSuffix, String supplierId, String projectId) {
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO = new DeliveryFollowUpTeamFilterDTO();
        deliveryFollowUpTeamFilterDTO.setDeliveryControllerUserId("kerstin");
        deliveryFollowUpTeamFilterDTO.setSupplierId("567");
        deliveryFollowUpTeamFilterDTO.setProjectId(null);
        deliveryFollowUpTeamFilterDTO.setSuffix(null);
        return deliveryFollowUpTeamFilterDTO;
    }
    
    public static DeliveryFollowUpTeamFilterDTO getDTOForSuffixTest(String ppSuffix, String supplierId, String projectId) {
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO = new DeliveryFollowUpTeamFilterDTO();
        deliveryFollowUpTeamFilterDTO.setDeliveryControllerUserId("gaynor");
        deliveryFollowUpTeamFilterDTO.setSuffix("196");
        deliveryFollowUpTeamFilterDTO.setSupplierId(null);
        deliveryFollowUpTeamFilterDTO.setProjectId(null);
        return deliveryFollowUpTeamFilterDTO;
    }
    
    public static long selectSupplierCounterPart(UserDTO userDTO, CommonServices commonServices) {
        DeliveryFollowUpTeamDTO followUpTeam = commonServices.getDeliveryFollowupTeam(userDTO.getDelFollowUpTeam());
        if (followUpTeam != null) {
            List<SupplierCounterPart> supplierCounterParts = commonServices.getAllSupplierCounterParts(String.valueOf(followUpTeam.getId()));
            if (supplierCounterParts != null && supplierCounterParts.size() > 0) {
                return supplierCounterParts.get(0).getId();
            }
        }
        return 0;
    }

    public static List<ProcureLine> procureInternalRequest(ProcureLineRepository procureLineRepository, ProcurementServices procurementServices) throws GloriaApplicationException {
        List<ProcureLine> procureLines = procureLineRepository.findAll();
        List<ProcureLineDTO> procureLineDTOs = ProcurementHelper.transformEntityToDTO(procureLines);

        for (ProcureLineDTO procureLineDTO : procureLineDTOs) {
            procureLineDTO.setStatus(ProcureLineStatus.PROCURED.toString());
            procureLineDTO.setProcureResponsibility(ProcureResponsibility.PROCURER.name());
            procureLineDTO.setBuyerCode("XYZ");
            procureLineDTO.setAdditionalQuantity(5l);
            procureLineDTO.setUnitPrice(2);
            procureLineDTO.setCurrency("SEK");
            procureLineDTO.setProcureType("INTERNAL");
            procureLineDTO.setSupplierCounterPartID(0L);
        }

        List<ProcureLine> updateProcureLines = procurementServices.updateProcureLines(procureLineDTOs, "procure", null, "all", null, null);

        Assert.assertEquals(1, updateProcureLines.size());
        return updateProcureLines;
    }

    public static void createMRWithSamePartInfoDiffTestObject(ProcurementServices procurementServices, RequestTransformer requestTransformer) throws GloriaApplicationException, IOException {
        procurementServices.manageRequest(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(INITDATA_REQUEST_XML_1)));
    }

    public static void modifyMaterials(List<Material> lines, ProcurementServices procurementServices) throws GloriaApplicationException {
        List<Long> materialIds = new ArrayList<Long>();
        for (Material material : lines) {
            materialIds.add(material.getMaterialOID());
        }

        boolean isAllowed = procurementServices.isManualGroupingAllowed(materialIds, "material");
        Assert.assertTrue(isAllowed);

        List<MaterialHeaderGroupingDTO> materialHeaderGroupingDTOs = new ArrayList<MaterialHeaderGroupingDTO>();
        MaterialHeaderGroupingDTO materialHeaderGroupingDTO = new MaterialHeaderGroupingDTO();
        StringBuffer materials = new StringBuffer();
        for (Long materialId : materialIds) {
            if (materials != null && materials.length() > 0) {
                materials.append(",");
            }
            materials.append(materialId);
        }

        materialHeaderGroupingDTO.setMaterialIds(materials.toString());
        materialHeaderGroupingDTOs.add(materialHeaderGroupingDTO);

        procurementServices.groupMaterials(materialHeaderGroupingDTOs, "pPartNo", "pPartVersion", "pPartName", "pPartMod", null);
    }
}
