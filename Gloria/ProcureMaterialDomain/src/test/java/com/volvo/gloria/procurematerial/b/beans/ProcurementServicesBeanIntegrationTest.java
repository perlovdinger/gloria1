package com.volvo.gloria.procurematerial.b.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.b.CarryOverTransformer;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.b.CompanyCodeTransformer;
import com.volvo.gloria.common.b.CostCenterTransformer;
import com.volvo.gloria.common.b.GLAccountTransformer;
import com.volvo.gloria.common.b.WBSElementTransformer;
import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamFilterDTO;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam;
import com.volvo.gloria.common.d.entities.SupplierCounterPart;
import com.volvo.gloria.common.purchaseorder.c.dto.PurchaseOrderScheduleDTO;
import com.volvo.gloria.common.purchaseorder.c.dto.StandAloneOrderHeaderDTO;
import com.volvo.gloria.common.purchaseorder.c.dto.StandAloneOrderLineDTO;
import com.volvo.gloria.common.purchaseorder.c.dto.SyncPurchaseOrderTypeDTO;
import com.volvo.gloria.procurematerial.b.DeliveryServices;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.ProcurementDtoTransformer;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.b.RequestTransformer;
import com.volvo.gloria.procurematerial.c.ProcureResponsibility;
import com.volvo.gloria.procurematerial.c.dto.ChangeIdDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialHeaderDTO;
import com.volvo.gloria.procurematerial.c.dto.ProcureLineDTO;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.PartAlias;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.PurchaseOrganisation;
import com.volvo.gloria.procurematerial.d.entities.status.changeid.ChangeIdStatus;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatus;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.procurematerial.repositories.b.PartNumberRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.util.ProcurementHelper;
import com.volvo.gloria.procurematerial.util.ProcurementServicesTestHelper;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class ProcurementServicesBeanIntegrationTest extends AbstractTransactionalTestCase {
    public ProcurementServicesBeanIntegrationTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    @Inject
    private ProcurementServices procurementServices;

    @Inject
    private UserServices userServices;

    @Inject
    private CommonServices commonServices;

    @Inject
    private DeliveryServices deliveryServices;

    @Inject
    private CarryOverTransformer carryOverTransformer;

    @Inject
    private RequestTransformer requestTransformer;

    @Inject
    private MaterialHeaderRepository requestHeaderRepository;

    @Inject
    private ProcureLineRepository procureLineRepository;

    @Inject
    private WBSElementTransformer wbsElementStorageTransformer;

    @Inject
    private CostCenterTransformer costCenterStorageTransformer;

    @Inject
    private GLAccountTransformer gLAccountTransformer;

    @Inject
    private CompanyCodeTransformer companyCodeTransformer;

    @Inject
    private PartNumberRepository partNumberRepository;

    @Inject
    private MaterialServices materialServices;
    
    @Inject
    private ProcurementDtoTransformer procurementDtoTransformer;

    EntityManager em = null;
    
    @PersistenceContext(unitName = "ProcureMaterialDomainPU")
    public void setEntityManager(EntityManager em) {
        this.em= em;
    }


    private static final String INITDATA_CARRYOVER_XML = "globaldataTest/SyncPurchaseOrder_1.00.xml";
    private static final String INITDATA_GLACCOUNT_XML = "globaldataTest/GlAccount.xml";
    private static final String INITDATA_WBSELEMENT_XML = "globaldataTest/WbsElement.xml";
    private static final String INITDATA_COMPANY_CODE_XML = "globaldataTest/CompanyCode.xml";
    private static final String INITDATA_COSTCENTER_XML = "globaldataTest/SyncCostCenter_1_0_01.xml";
    private static final String INITDATA_USER_XML = "globaldataTest/UserOrganisationDetails.xml";
    private static final String INITDATA_DELIVERY_FOLLOW_UP_TEAM_XML = "globaldataTest/DeliveryFollowUpTeam.xml";
    private static final String INITDATA_INTERNALORDERSAP_DATA_CP_XML = "globaldataTest/InternalOrderSap.xml";
    private static final String INITDATA_REQUEST_XML = "globaldataTest/request_FirstAssembly1.xml";
    private static final String INITDATA_REQUEST_BORROWPARTS_XML = "globaldataTest/request_FirstAssemblyBorrowParts.xml";
    private static final String INITDATA_TEAMS_XML = "globaldataTest/Team.xml";
    private static final String INITDATA_TEAM_USERS_XML = "globaldataTest/TeamUsers.xml";

    @Before
    public void setUpTestData() throws JAXBException, IOException, GloriaApplicationException {

        commonServices.syncCarryOver(carryOverTransformer.transformStoredCarryOver(IOUtil.getStringFromClasspath(INITDATA_CARRYOVER_XML)));

        commonServices.addSyncCompanyCode(companyCodeTransformer.transformCompanyCode(IOUtil.getStringFromClasspath(INITDATA_COMPANY_CODE_XML)));

        commonServices.createGlAccounts(gLAccountTransformer.transformGLAccount(IOUtil.getStringFromClasspath(INITDATA_GLACCOUNT_XML)));

        commonServices.syncCostCenter(costCenterStorageTransformer.transformStoredCostCenter(IOUtil.getStringFromClasspath(INITDATA_COSTCENTER_XML)));

        commonServices.syncWBSElement(wbsElementStorageTransformer.transformStoredWBSElement(IOUtil.getStringFromClasspath(INITDATA_WBSELEMENT_XML)));

        userServices.createUserData(IOUtil.getStringFromClasspath(INITDATA_USER_XML));

        commonServices.createDeliveryFollowUpTeamData(IOUtil.getStringFromClasspath(INITDATA_DELIVERY_FOLLOW_UP_TEAM_XML));

        commonServices.createInternalOrderSapData(IOUtil.getStringFromClasspath(INITDATA_INTERNALORDERSAP_DATA_CP_XML));

        procurementServices.manageRequest(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(INITDATA_REQUEST_XML)));
        
        userServices.createTeamData(IOUtil.getStringFromClasspath(INITDATA_TEAMS_XML));

        userServices.initTeamUser(IOUtil.getStringFromClasspath(INITDATA_TEAM_USERS_XML));
    }

    @Test
    @Ignore
    public void testRevertProcurementForExternal() throws GloriaApplicationException {
        // Arrange
        ProcurementServicesTestHelper.assignProcureRequest(null, procurementServices, procurementDtoTransformer);
        List<ProcureLine> procureLines = ProcurementServicesTestHelper.procureExternalRequest(procureLineRepository, procurementServices);
        ProcureLine procureLine = procureLineRepository.findProcureLineById(procureLines.get(0).getId());
        Assert.assertEquals(3, procureLine.getVersion());
        ProcureLineDTO procureLineDTO = ProcurementHelper.transformAsDTO(procureLine);

        // Act
        procurementServices.updateProcureLine(procureLineDTO, "revert", null, null, null, null, false, null);

        // Assert
        ProcureLine revertedProcureLine = procureLineRepository.findProcureLineById(procureLineDTO.getId());
        Assert.assertEquals(ProcureLineStatus.WAIT_TO_PROCURE, revertedProcureLine.getStatus());
        Assert.assertEquals(1, revertedProcureLine.getMaterials().size());
        Assert.assertNull(revertedProcureLine.getRequisition());
    }

    @Test
    @Ignore
    public void testFindMaterialsByProcureLineIdForProcureDetailss() throws GloriaApplicationException {
        // Arrange
        PageObject pageObject = ProcurementServicesTestHelper.assignProcureRequest(null, procurementServices, procurementDtoTransformer);

        List<ProcureLine> procureLines = procureLineRepository.findAll();

        String procureLineOids = "";
        boolean first = true;
        for (ProcureLine procureLine : procureLines) {
            String oid = String.valueOf(procureLine.getProcureLineOid());
            if (first) {
                first = false;
                procureLineOids = procureLineOids + oid;
            } else {
                procureLineOids = procureLineOids + "," + oid;
            }
        }

        // Act
        PageObject paObject = procurementServices.findMaterialsByProcureLineIds(procureLineOids, pageObject);

        // Assert
        Assert.assertNotNull(paObject);
        Assert.assertEquals(1, paObject.getGridContents().size());
    }

    /**
     * when ProcureLineStatus = WAIT_TO_PROCURE
     * 
     */
    @Test
    @Ignore
    public void testAcceptChangeId() throws GloriaApplicationException, IOException {
        // Arrange
        ProcurementServicesTestHelper.assignProcureRequest(null, procurementServices, procurementDtoTransformer);
        ProcurementServicesTestHelper.updateMaterialRequest(procurementServices, requestTransformer);
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(10);
        pageObject = procurementServices.findAllChangeIds(pageObject, "all", "all");

        List<PageResults> pageResults = pageObject.getGridContents();
        List<ChangeIdDTO> changeIdDTOs = new ArrayList<ChangeIdDTO>();

        for (PageResults pageResult : pageResults) {
            changeIdDTOs.add((ChangeIdDTO) pageResult);
        }

        Assert.assertEquals(1, changeIdDTOs.size());
        // Act
        long changeIdOid = changeIdDTOs.get(0).getId();
        procurementServices.acceptOrRejectChangeId("accept", changeIdOid, "all", null);

        // Assert
        ChangeId changeId = requestHeaderRepository.findChangeIdByOid(changeIdOid);
        Assert.assertEquals(ChangeIdStatus.ACCEPTED, changeId.getStatus());

        for (Material addMaterial : changeId.getAddMaterials()) {
            Assert.assertEquals(MaterialStatus.ADDED, addMaterial.getStatus());
        }

        for (Material removeMaterial : changeId.getRemoveMaterials()) {
            Assert.assertEquals(MaterialStatus.REMOVED, removeMaterial.getStatus());
            Assert.assertNull(removeMaterial.getProcureLine());
        }

        List<ProcureLine> procureLines = procureLineRepository.findAll();
        Assert.assertEquals(1, procureLines.size());
    }

    @Test
    @Ignore
    public void testAddOrder() throws GloriaApplicationException {
        // Arrange
        ProcurementServicesTestHelper.assignProcureRequest(null, procurementServices, procurementDtoTransformer);
        List<ProcureLine> procureLines = ProcurementServicesTestHelper.procureExternalRequest(procureLineRepository, procurementServices);
        Assert.assertNotNull(procureLines);
        Assert.assertEquals(1, procureLines.size());
        ProcureLine procureLine = procureLines.get(0);

        SyncPurchaseOrderTypeDTO syncPurchaseOrderTypeDTO = new SyncPurchaseOrderTypeDTO();

        StandAloneOrderHeaderDTO standAloneOrderHeaderDTO = new StandAloneOrderHeaderDTO();
        standAloneOrderHeaderDTO.setOrderNo("2049-900019S123-196");
        standAloneOrderHeaderDTO.setOrderMode("Prototype");
        standAloneOrderHeaderDTO.setOrderIdGps("S489692");
        standAloneOrderHeaderDTO.setMaterialUserId("1001");
        standAloneOrderHeaderDTO.setMaterialUserName("VTC Tuve");
        standAloneOrderHeaderDTO.setSupplierId("567");
        standAloneOrderHeaderDTO.setMaterialUserCategory("Organization");
        standAloneOrderHeaderDTO.setOrderDateTime("2011-07-05");

        StandAloneOrderLineDTO aloneOrderLineDTO = new StandAloneOrderLineDTO();
        aloneOrderLineDTO.setPartNumber("1590744");
        aloneOrderLineDTO.setPartQualifier("V");
        aloneOrderLineDTO.setPerQuantity(new Long(1));
        aloneOrderLineDTO.setUnitOfMeasure("PCE");
        aloneOrderLineDTO.setUnitPriceTimePeriod("2011-07-05");
        aloneOrderLineDTO.setRequisitionIds(procureLine.getRequisitionId());

        PurchaseOrderScheduleDTO aloneOrderScheduleDTO = new PurchaseOrderScheduleDTO();
        aloneOrderScheduleDTO.setQuantity(new Long(10));
        aloneOrderScheduleDTO.setShipToArrive("2011-07-05");
        List<PurchaseOrderScheduleDTO> orderScheduleDTOs = new ArrayList<PurchaseOrderScheduleDTO>();
        orderScheduleDTOs.add(aloneOrderScheduleDTO);
        aloneOrderLineDTO.setPurchaseOrderSchedule(orderScheduleDTOs);

        List<StandAloneOrderLineDTO> standAloneOrderLineDtos = new ArrayList<StandAloneOrderLineDTO>();
        standAloneOrderLineDtos.add(aloneOrderLineDTO);
        standAloneOrderHeaderDTO.setStandAloneOrderLineDTO(standAloneOrderLineDtos);

        List<StandAloneOrderHeaderDTO> standAloneDtos = new ArrayList<StandAloneOrderHeaderDTO>();
        standAloneDtos.add(standAloneOrderHeaderDTO);
        syncPurchaseOrderTypeDTO.setStandAloneOrderHeaderDTO(standAloneDtos);

        List<DeliveryFollowUpTeam> teams = commonServices.findAllDeliveryFollowUpTeam();
        DeliveryFollowUpTeam deliveryFollowUpTeam = null;
        for (DeliveryFollowUpTeam delFollowUpTeam : teams) {
            if (delFollowUpTeam.getName().equalsIgnoreCase("DEL-FOLLOW-UP-GOT-PT")) {
                deliveryFollowUpTeam = delFollowUpTeam;
                break;
            }
        }

        String ppSuffix = "";
        String supplierId = "";
        String projectId = "";
        List<DeliveryFollowUpTeamFilterDTO> teamFilters = new ArrayList<DeliveryFollowUpTeamFilterDTO>();

        // Arrange data for testing with just suffix matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO1 = ProcurementServicesTestHelper.getDTOForSuffixTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO1);
        // Arrange data for testing with just supplier id matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO2 = ProcurementServicesTestHelper.getDTOForSupplierTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO2);

        for (DeliveryFollowUpTeamFilterDTO dftf : teamFilters) {
            commonServices.addDeliveryFollowUpTeamFilter(dftf, deliveryFollowUpTeam.getDeliveryFollowUpTeamOid());
        }

        // Act
        procurementServices.createOrUpdatePurchaseOrder(syncPurchaseOrderTypeDTO);

        // Assert
        List<Order> orders = deliveryServices.findOrderBySuffix("196");
        Assert.assertNotNull(orders);
        Assert.assertEquals(1, orders.size());
        Order order = orders.get(0);
        Assert.assertNotNull(order.getOrderLines());
        Assert.assertEquals(1, order.getOrderLines().size());
        Assert.assertEquals("DEL-FOLLOW-UP-GOT-PT", order.getDeliveryControllerTeam());
    }

    @Test
    @Ignore
    public void testUpdateOrder() throws GloriaApplicationException {
        // Arrange
        testAddOrder();
        List<Order> orders = deliveryServices.findOrderBySuffix("196");
        OrderLine orderLine = orders.get(0).getOrderLines().get(0);
        SyncPurchaseOrderTypeDTO syncPurchaseOrderTypeDTO = new SyncPurchaseOrderTypeDTO();

        StandAloneOrderHeaderDTO standAloneOrderHeaderDTO = new StandAloneOrderHeaderDTO();
        standAloneOrderHeaderDTO.setOrderNo("2049-900019S123-196");
        standAloneOrderHeaderDTO.setOrderMode("Prototype");
        standAloneOrderHeaderDTO.setOrderIdGps("S489692");
        standAloneOrderHeaderDTO.setMaterialUserId("1001");
        standAloneOrderHeaderDTO.setMaterialUserName("VTC Tuve");
        standAloneOrderHeaderDTO.setSupplierId("567");
        standAloneOrderHeaderDTO.setMaterialUserCategory("Organization");
        standAloneOrderHeaderDTO.setOrderDateTime("2011-07-05");

        StandAloneOrderLineDTO aloneOrderLineDTO = new StandAloneOrderLineDTO();
        aloneOrderLineDTO.setPartNumber("1590744");
        aloneOrderLineDTO.setPartQualifier("V");
        aloneOrderLineDTO.setPerQuantity(new Long(1));
        aloneOrderLineDTO.setUnitOfMeasure("PCE");
        aloneOrderLineDTO.setUnitPriceTimePeriod("2011-07-05");
        aloneOrderLineDTO.setRequisitionIds(orderLine.getRequisitionId());

        PurchaseOrderScheduleDTO aloneOrderScheduleDTO = new PurchaseOrderScheduleDTO();
        aloneOrderScheduleDTO.setQuantity(new Long(20));
        aloneOrderScheduleDTO.setShipToArrive("2011-07-10");
        List<PurchaseOrderScheduleDTO> orderScheduleDTOs = new ArrayList<PurchaseOrderScheduleDTO>();
        orderScheduleDTOs.add(aloneOrderScheduleDTO);
        aloneOrderLineDTO.setPurchaseOrderSchedule(orderScheduleDTOs);

        List<StandAloneOrderLineDTO> standAloneOrderLineDtos = new ArrayList<StandAloneOrderLineDTO>();
        standAloneOrderLineDtos.add(aloneOrderLineDTO);
        standAloneOrderHeaderDTO.setStandAloneOrderLineDTO(standAloneOrderLineDtos);

        List<StandAloneOrderHeaderDTO> standAloneDtos = new ArrayList<StandAloneOrderHeaderDTO>();
        standAloneDtos.add(standAloneOrderHeaderDTO);
        syncPurchaseOrderTypeDTO.setStandAloneOrderHeaderDTO(standAloneDtos);

        // Act
        procurementServices.createOrUpdatePurchaseOrder(syncPurchaseOrderTypeDTO);

        // Assert
        orders = deliveryServices.findOrderBySuffix("196");
        Assert.assertNotNull(orders);
        Assert.assertEquals(1, orders.size());
        Order order = orders.get(0);
        List<OrderLineVersion> orderLineVersions = order.getOrderLines().get(0).getOrderLineVersions();
        Collections.sort(orderLineVersions, new Comparator<OrderLineVersion>() {
            public int compare(OrderLineVersion orderLineVersionOne, OrderLineVersion orderLineVersionTwo) {
                if (orderLineVersionOne.getOrderLineVersionOid() > (orderLineVersionTwo.getOrderLineVersionOid())) {
                    return -1;
                }
                return 1;
            }
        });

        OrderLineVersion orderLineVersion = orderLineVersions.get(0);
        Assert.assertEquals(20, orderLineVersion.getQuantity());
        Assert.assertEquals("2011-07-10", DateUtil.getDateWithoutTimeAsString(orderLineVersion.getOrderStaDate()));
    }

    @Test
    public void testAssignProcureRequests() throws GloriaApplicationException {
        PageObject pageObject = ProcurementServicesTestHelper.assignProcureRequest(null, procurementServices, procurementDtoTransformer);

        // Assert
        List<MaterialHeaderDTO> assignedRequests = ProcurementHelper.transformAsDTOs(procurementServices.getRequestHeaders(pageObject, true, "ALL").getGridContents());
        Assert.assertNotNull(assignedRequests);
        Assert.assertEquals(1, assignedRequests.size());
        Assert.assertEquals("all", assignedRequests.get(0).getAssignedMaterialControllerId());
        MaterialHeader materialHeader = requestHeaderRepository.findById(assignedRequests.get(0).getId());
        Assert.assertNotNull(materialHeader.getMaterials());
        List<Material> materials = materialHeader.getMaterials();
        for (Material material : materials) {
            Assert.assertNotNull(material.getProcureLine());
        }
    }

    @Test
    @Ignore
    public void testUpdateProcurelineWithNonExistingPartAlias() throws GloriaApplicationException {
        // Arrange
        testAssignProcureRequests();
        ProcureLine procureLine = procureLineRepository.findAll().get(0);
        ProcureLineDTO procureLineDTO = ProcurementHelper.transformAsDTO(procureLine);

        String alias = "alias-1";

        PartAlias partAliasBeforeUpdate = partNumberRepository.findPartAliasByAliasName(alias);

        procureLineDTO.setStatus(ProcureLineStatus.PROCURED.toString());
        procureLineDTO.setProcureResponsibility(ProcureResponsibility.PROCURER.name());
        procureLineDTO.setBuyerCode("XYZ");
        procureLineDTO.setAdditionalQuantity(5l);
        procureLineDTO.setUnitPrice(2);
        procureLineDTO.setCurrency("SEK");
        procureLineDTO.setProcureType(ProcureType.INTERNAL.name());
        procureLineDTO.setPartAlias(alias);

        List<ProcureLineDTO> procureLineDTOs = new ArrayList<ProcureLineDTO>();
        procureLineDTOs.add(procureLineDTO);

        // Act
        List<ProcureLine> updateProcureLines = procurementServices.updateProcureLines(procureLineDTOs, null, null, "all", null, null);

        // Assert
        Assert.assertNull(partAliasBeforeUpdate);
        PartAlias partAliasAfterUpdate = partNumberRepository.findPartAliasByAliasName(procureLineDTO.getPartAlias());
        Assert.assertNotNull(partAliasAfterUpdate);

        Assert.assertEquals(1, updateProcureLines.size());
        Assert.assertNotNull(updateProcureLines.get(0).getPartAlias());
        Assert.assertEquals(partAliasAfterUpdate.getAliasPartNumber(), updateProcureLines.get(0).getPartAlias().getAliasPartNumber());
    }

    @Test
    public void testAssignGTTAndUpdateProcureLines() throws GloriaApplicationException {
        // Arrange

        testAssignProcureRequests();
        List<ProcureLine> procureLines = procureLineRepository.findAll();
        List<ProcureLineDTO> procureLineDTOs = ProcurementHelper.transformEntityToDTO(procureLines);

        PurchaseOrganisation purchaseOrganisation = new PurchaseOrganisation();
        purchaseOrganisation.setOrganisationCode("675");
        em.persist(purchaseOrganisation);
        

        for (ProcureLineDTO procureLineDTO : procureLineDTOs) {
            procureLineDTO.setStatus(ProcureLineStatus.WAIT_TO_PROCURE.toString());
            procureLineDTO.setProcureResponsibility(ProcureResponsibility.PROCURER.name());
            procureLineDTO.setPurchaseOrganisationCode("675");   
        }

        // Act
        List<ProcureLine> updateProcureLines = procurementServices.updateProcureLines(procureLineDTOs, null, null, "all", null, null);

        // Assert
        Assert.assertEquals(ProcureResponsibility.PROCURER.name(), updateProcureLines.get(0).getResponsibility().name());
    }

    /**
     * unitPrice or Price is greater than 0 and currency is empty
     * 
     * @throws GloriaApplicationException
     */
    @Ignore
    @Test(expected = GloriaApplicationException.class)
    public void testValidatePriceAndCurrencyScenario2() throws GloriaApplicationException {
        // Arrange
        testAssignProcureRequests();
        List<ProcureLine> procureLines = procureLineRepository.findAll();
        List<ProcureLineDTO> procureLineDTOs = ProcurementHelper.transformEntityToDTO(procureLines);

        for (ProcureLineDTO procureLineDTO : procureLineDTOs) {
            procureLineDTO.setStatus(ProcureLineStatus.PROCURED.toString());
            procureLineDTO.setProcureType(ProcureType.INTERNAL.name());
            procureLineDTO.setProcureResponsibility(ProcureResponsibility.PROCURER.name());
            procureLineDTO.setBuyerCode("XYZ");
            procureLineDTO.setAdditionalQuantity(5l);
            procureLineDTO.setUnitPrice(2);
        }
        // Act
        procurementServices.updateProcureLines(procureLineDTOs, "procure", null, "all", null, null);
    }

    @Test
    @Ignore
    public void testAssignGTTWithStatusProcuredAndUpdateProcureLines() throws GloriaApplicationException {
        // Arrange

        testAssignProcureRequests();
        List<ProcureLine> procureLines = procureLineRepository.findAll();
        List<ProcureLineDTO> procureLineDTOs = ProcurementHelper.transformEntityToDTO(procureLines);

        for (ProcureLineDTO procureLineDTO : procureLineDTOs) {
            procureLineDTO.setStatus(ProcureLineStatus.PROCURED.toString());
            procureLineDTO.setProcureResponsibility(ProcureResponsibility.PROCURER.name());
            procureLineDTO.setBuyerCode("XYZ");
            procureLineDTO.setAdditionalQuantity(5l);
        }

        // Act
        List<ProcureLine> updateProcureLines = procurementServices.updateProcureLines(procureLineDTOs, null, null, "all", null, null);

        // Assert
        Assert.assertEquals(ProcureResponsibility.PROCURER.name(), updateProcureLines.get(0).getResponsibility().name());
    }

    @Test
    public void testDoValidate() throws GloriaApplicationException {
        // Arrange
        testAssignProcureRequests();
        List<ProcureLine> procureLines = procureLineRepository.findAll();
        List<ProcureLineDTO> procureLineDTOs = ProcurementHelper.transformEntityToDTO(procureLines);

        PurchaseOrganisation purchaseOrganisation = new PurchaseOrganisation();
        purchaseOrganisation.setOrganisationCode("675");
        em.persist(purchaseOrganisation);
        
        for (ProcureLineDTO procureLineDTO : procureLineDTOs) {
            procureLineDTO.setStatus(ProcureLineStatus.PROCURED.toString());
            procureLineDTO.setProcureResponsibility(ProcureResponsibility.PROCURER.name());
            procureLineDTO.setBuyerCode("");
            procureLineDTO.setAdditionalQuantity(5l);
            procureLineDTO.setUnitPrice(2);
            procureLineDTO.setCurrency("");
            procureLineDTO.setPurchaseOrganisationCode("675");
        }

        // Act
        List<ProcureLine> procureLinesResult = procurementServices.updateProcureLines(procureLineDTOs, "procure", null, "all", null, null);
        Assert.assertEquals(0, procureLinesResult.size());
    }

    @Test
    public void testGetProcureLinesByInternalProcurerTeam() throws GloriaApplicationException {
        // Arrange
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(10);
        testAssignProcureRequests();
        List<ProcureLine> procureLines = procureLineRepository.findAll();
        ProcureLine procureLine = procureLines.get(0);
        procureLine.setForwardedUserId("INT_PROC_1");
        procureLine.setForwardedUserName("INT_PROC_1_NAME");
        procureLine.setForwardedTeam("TEM_INT_PROC");
        procureLineRepository.save(procureLine);
        // Act
        pageObject = procurementServices.getProcureLinesByInternalProcurerTeam("TEM_INT_PROC", ProcureLineStatus.WAIT_TO_PROCURE.name(), pageObject);

        // Assert
        Assert.assertNotNull(pageObject.getGridContents());
        ProcureLineDTO procureLineDTO = (ProcureLineDTO) pageObject.getGridContents().get(0);
        Assert.assertEquals(procureLine.getForwardedUserId(), procureLineDTO.getProcureForwardedId());
    }

    @Test
    @Ignore
    public void testUpdateMaterialRequestAfterAssigningMC() throws GloriaApplicationException, IOException {
        // Act
        ProcurementServicesTestHelper.assignProcureRequest(null, procurementServices, procurementDtoTransformer);
        ProcurementServicesTestHelper.updateMaterialRequest(procurementServices, requestTransformer);

        // Assert
        MaterialHeader materialHeader = requestHeaderRepository.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(null, null, "FH-1245-4311","", null).get(0);
        Assert.assertEquals(2, materialHeader.getMaterials().size());
        Material newMaterial = requestHeaderRepository.findMaterialByProcureLinkId(2147483648L);
        Assert.assertEquals(MaterialStatus.ADD_NOT_ACCEPTED, newMaterial.getStatus());

        Material removedMaterial = requestHeaderRepository.findMaterialByProcureLinkId(2147483647L);
        Assert.assertEquals(MaterialStatus.REMOVE_MARKED, removedMaterial.getStatus());

        Assert.assertEquals(ChangeIdStatus.WAIT_CONFIRM, materialHeader.getAccepted().getChangeId().getStatus());
    }

    @Test
    @Ignore
    public void testGetBorrowableMaterialLinesWithProcuredStatus() throws GloriaApplicationException, IOException {

        // ARRANGE
        procurementServices.manageRequest(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(INITDATA_REQUEST_XML)));
        procurementServices.manageRequest(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(INITDATA_REQUEST_BORROWPARTS_XML)));

        UserDTO userDTO = userServices.getUser("all");

        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);

        List<Material> materials = procurementServices.findAllMaterials();

        for (Material material : materials) {
            for(MaterialLine materialLine : material.getMaterialLine()){
                materialLine.setFinalWhSiteId("1722");
            }
        }

        procurementServices.groupMaterials(materials);

        List<SupplierCounterPart> supplierCounterParts = commonServices.getSupplierCounterPartsByCompanyCode("SE27");
        List<ProcureLine> procureLines = procurementServices.findAllProcureLines();

        List<ProcureLineDTO> procureLineDTOs = new ArrayList<ProcureLineDTO>();

        for (ProcureLine procureLine : procureLines) {

            ProcureLineDTO procureLineDTO = ProcurementHelper.transformAsDTO(procureLine);
            procureLineDTO.setSupplierCounterPartID(ProcurementServicesTestHelper.selectSupplierCounterPart(userDTO, commonServices));

            procureLineDTO.setProcureType(ProcureType.INTERNAL.name());
            procureLineDTO.setUnitPrice(2);
            procureLineDTO.setCurrency("EURO");
            procureLineDTO.setUsageQty(50);
            procureLineDTO.setOrderNo("900000");
            procureLineDTO.setOrderStaDate(DateUtil.getSqlDate());
            procureLineDTO.setSupplierName("apple inc");
            
            if(supplierCounterParts != null && !supplierCounterParts.isEmpty()) {
                procureLineDTO.setSupplierCounterPartID(supplierCounterParts.get(0).getId());
            }
            procureLineDTOs.add(procureLineDTO);
        }
        procurementServices.updateProcureLines(procureLineDTOs, "procure", null, "all", null, null);

        // ACT
        PageObject barrowedPageObject = materialServices.getBorrowableMaterialLines(pageObject, materials.get(0).getMaterialLine().get(0).getMaterialLineOID());

        // ASSERT
        Assert.assertNotNull(barrowedPageObject.getGridContents());
        Assert.assertEquals(1, barrowedPageObject.getGridContents().size());

    }

    /**
     * when ProcureLineStatus = PROCURED and ProcureType = Internal
     * 
     * @throws GloriaApplicationException
     * @throws IOException
     */
    @Test
    @Ignore
    public void testAcceptChangeIdScenario2() throws GloriaApplicationException, IOException {
        // Arrange
        ProcurementServicesTestHelper.assignProcureRequest(null, procurementServices, procurementDtoTransformer);
        List<ProcureLine> procureLines = ProcurementServicesTestHelper.procureInternalRequest(procureLineRepository, procurementServices);
        Assert.assertEquals(1, procureLines.size());
        ProcureLine procureLine = procureLines.get(0);
        ProcurementServicesTestHelper.updateMaterialRequest(procurementServices, requestTransformer);
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(10);
        pageObject = procurementServices.findAllChangeIds(pageObject, "all", "all");

        List<PageResults> pageResults = pageObject.getGridContents();
        List<ChangeIdDTO> changeIdDTOs = new ArrayList<ChangeIdDTO>();

        for (PageResults pageResult : pageResults) {
            changeIdDTOs.add((ChangeIdDTO) pageResult);
        }

        Assert.assertEquals(1, changeIdDTOs.size());

        ChangeId removeChangeId = requestHeaderRepository.findChangeIdByOid(changeIdDTOs.get(0).getId());
        List<MaterialDTO> materialDTOs = procurementDtoTransformer.transformAsMaterialDTOs(removeChangeId.getRemoveMaterials());
        for (MaterialDTO materialDTO : materialDTOs) {
            materialDTO.setChangeAction(GloriaParams.CANCEL_IO);
        }

        // Act
        long changeIdOid = changeIdDTOs.get(0).getId();
        procurementServices.acceptOrRejectChangeId("accept", changeIdOid, "all", materialDTOs);

        // Assert
        ChangeId changeId = requestHeaderRepository.findChangeIdByOid(changeIdOid);
        Assert.assertEquals(ChangeIdStatus.ACCEPTED, changeId.getStatus());

        for (Material addMaterial : changeId.getAddMaterials()) {
            Assert.assertEquals(MaterialStatus.ADDED, addMaterial.getStatus());
        }

        for (Material removeMaterial : changeId.getRemoveMaterials()) {
            Assert.assertEquals(MaterialStatus.REMOVED, removeMaterial.getStatus());
        }

        Assert.assertEquals(2, procureLine.getMaterials().size());
    }

    /**
     * when ProcureLineStatus = PROCURED and ProcureType = External
     * 
     * @throws GloriaApplicationException
     * @throws IOException
     */
    @Test
    @Ignore
    public void testAcceptChangeIdScenario3() throws GloriaApplicationException, IOException {
        // Arrange
        ProcurementServicesTestHelper.assignProcureRequest(null, procurementServices, procurementDtoTransformer);
        List<ProcureLine> procureLines = ProcurementServicesTestHelper.procureInternalRequest(procureLineRepository, procurementServices);
        Assert.assertEquals(1, procureLines.size());
        ProcureLine procureLine = procureLines.get(0);
        ProcurementServicesTestHelper.updateMaterialRequest(procurementServices, requestTransformer);
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(10);
        pageObject = procurementServices.findAllChangeIds(pageObject, "all", "all");

        List<PageResults> pageResults = pageObject.getGridContents();
        List<ChangeIdDTO> changeIdDTOs = new ArrayList<ChangeIdDTO>();

        for (PageResults pageResult : pageResults) {
            changeIdDTOs.add((ChangeIdDTO) pageResult);
        }

        Assert.assertEquals(1, changeIdDTOs.size());

        ChangeId removeChangeId = requestHeaderRepository.findChangeIdByOid(changeIdDTOs.get(0).getId());
        List<MaterialDTO> materialDTOs = procurementDtoTransformer.transformAsMaterialDTOs(removeChangeId.getRemoveMaterials());
        for (MaterialDTO materialDTO : materialDTOs) {
            materialDTO.setChangeAction(GloriaParams.CANCEL_IO);
        }

        // Act
        long changeIdOid = changeIdDTOs.get(0).getId();
        procurementServices.acceptOrRejectChangeId("accept", changeIdOid, "all", materialDTOs);

        // Assert
        ChangeId changeId = requestHeaderRepository.findChangeIdByOid(changeIdOid);
        Assert.assertEquals(ChangeIdStatus.ACCEPTED, changeId.getStatus());

        for (Material addMaterial : changeId.getAddMaterials()) {
            Assert.assertEquals(MaterialStatus.ADDED, addMaterial.getStatus());
        }

        for (Material removeMaterial : changeId.getRemoveMaterials()) {
            Assert.assertEquals(MaterialStatus.REMOVED, removeMaterial.getStatus());
        }

        Assert.assertEquals(2, procureLine.getMaterials().size());
    }

    @Test
    @Ignore
    public void testUnassignMaterialController() throws GloriaApplicationException {
        // Arrange
        PageObject pageObject = ProcurementServicesTestHelper.assignProcureRequest(null, procurementServices, procurementDtoTransformer);
        String userId = "all";
        List<MaterialHeaderDTO> materialHeaderDTOs = ProcurementHelper.transformAsDTOs(pageObject.getGridContents());

        // Act

        procurementServices.assignOrUnassignMateriaHeaders(materialHeaderDTOs, "unassign", null, null, null, userId);

        // Assert
        List<MaterialHeaderDTO> unassignedMaterialHeaderDTOs = ProcurementHelper.transformAsDTOs(procurementServices.getRequestHeaders(pageObject, false, "ALL")
                                                                                                                    .getGridContents());
        Assert.assertNotNull(unassignedMaterialHeaderDTOs);
        MaterialHeaderDTO materialHeaderDTO = unassignedMaterialHeaderDTOs.get(0);
        Assert.assertEquals(null, materialHeaderDTO.getAssignedMaterialControllerId());
    }

    @Test
    @Ignore
    public void testRejectChangeId() throws GloriaApplicationException, IOException {
        // Arrange
        ProcurementServicesTestHelper.assignProcureRequest(null, procurementServices, procurementDtoTransformer);
        ProcurementServicesTestHelper.updateMaterialRequest(procurementServices, requestTransformer);
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(10);
        pageObject = procurementServices.findAllChangeIds(pageObject, "all", "all");

        List<PageResults> pageResults = pageObject.getGridContents();
        List<ChangeIdDTO> changeIdDTOs = new ArrayList<ChangeIdDTO>();

        for (PageResults pageResult : pageResults) {
            changeIdDTOs.add((ChangeIdDTO) pageResult);
        }

        Assert.assertEquals(1, changeIdDTOs.size());
        // Act
        long changeIdOid = changeIdDTOs.get(0).getId();
        procurementServices.acceptOrRejectChangeId("reject", changeIdOid, "all", null);

        // Assert
        ChangeId changeId = requestHeaderRepository.findChangeIdByOid(changeIdOid);
        Assert.assertEquals(ChangeIdStatus.REJECTED, changeId.getStatus());

        for (Material addMaterial : changeId.getAddMaterials()) {
            Assert.assertEquals(MaterialStatus.REMOVED, addMaterial.getStatus());
        }

        for (Material removeMaterial : changeId.getRemoveMaterials()) {
            Assert.assertEquals(MaterialStatus.ADDED, removeMaterial.getStatus());
        }
    }

    @Test
    @Ignore
    public void testFindMaterialsByProcureLineIdForProcureDetails() throws GloriaApplicationException {
        // Arrange
        ProcurementServicesTestHelper.assignProcureRequest(null, procurementServices, procurementDtoTransformer);

        List<ProcureLine> procureLines = procureLineRepository.findAll();

        Long procureLineOid = procureLines.get(0).getProcureLineOid();

        // Act
        List<Material> materials = materialServices.findMaterialsByProcureLineIdForProcureDetails(procureLineOid);

        // Assert
        Assert.assertNotNull(materials);
        Assert.assertEquals(1, materials.size());
    }

    @Test
    @Ignore
    public void testFindAllChangeIds() throws GloriaApplicationException {
        // Arrange
        String assignedMaterialController = "";
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(10);

        // Act
        pageObject = procurementServices.findAllChangeIds(pageObject, assignedMaterialController, "all");

        // Assert
        Assert.assertNotNull(pageObject.getGridContents());
        Assert.assertEquals(1, pageObject.getGridContents().size());
    }

    @Test
    @Ignore
    public void testUpdateMaterialRequest() throws GloriaApplicationException, IOException {
        // Act
        ProcurementServicesTestHelper.updateMaterialRequest(procurementServices, requestTransformer);

        // Assert
        MaterialHeader materialHeader = requestHeaderRepository.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(null, null, "FH-1245-4311","", null).get(0);
        Assert.assertEquals(2, materialHeader.getMaterials().size());
        Material newMaterial = requestHeaderRepository.findMaterialByProcureLinkId(2147483648L);
        Assert.assertEquals(MaterialStatus.ADDED, newMaterial.getStatus());

        Material removedMaterial = requestHeaderRepository.findMaterialByProcureLinkId(2147483647L);
        Assert.assertEquals(MaterialStatus.REMOVED, removedMaterial.getStatus());
    }

    @Test
    @Ignore
    public void testGetNotProcuredRequestHeaders() throws GloriaApplicationException, IOException {
        // Arrange
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(10);
        ProcurementServicesTestHelper.assignProcureRequest(null, procurementServices, procurementDtoTransformer);
        List<ProcureLine> procureLines = ProcurementServicesTestHelper.procureInternalRequest(procureLineRepository, procurementServices);
        Assert.assertEquals(1, procureLines.size());
        procureLines.get(0);
        ProcurementServicesTestHelper.updateMaterialRequest(procurementServices, requestTransformer);

        List<MaterialHeaderDTO> materialRequests = ProcurementHelper.transformAsDTOs(procurementServices.getRequestHeaders(pageObject, true, "ALL").getGridContents());
        Assert.assertEquals(0, materialRequests.size());
    }

    @Test
    @Ignore
    public void testToProcureLaterWithInvalidStatus() throws Exception {
        // Arrange
        ProcurementServicesTestHelper.assignProcureRequest(null, procurementServices, procurementDtoTransformer);
        List<ProcureLine> procureLines = ProcurementServicesTestHelper.procureExternalRequest(procureLineRepository, procurementServices);
        ProcureLine procureLine = procureLines.get(0);
        long procureLineId = procureLine.getProcureLineOid();
        List<Material> materials = materialServices.findMaterialsByProcureLineIdForProcureDetails(procureLineId);
        Material material = materials.get(0);
        // Act
        try {
            List<Long> procurementIds = new ArrayList<Long>();
            procurementIds.add(material.getMaterialOID());
            procurementServices.procureLater(procurementIds);
        } catch (GloriaApplicationException e) {
            System.out.println(e.getErrorMessage());
            Assert.assertTrue(e.getErrorMessage().contains("Procure line should have at least one Material and not yet Procured"));
        }

    }

    @Test
    @Ignore
    public void testRevertProcurementForInternalWithModifiedMaterial() throws GloriaApplicationException, IOException {
        // Arrange
        ProcurementServicesTestHelper.assignProcureRequest(null, procurementServices, procurementDtoTransformer);

        ProcurementServicesTestHelper.createMRWithSamePartInfoDiffTestObject(procurementServices, requestTransformer);
        MaterialHeader materialHeader = requestHeaderRepository.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(null, null, "FH-1246-4311","", null).get(0);
        ProcurementServicesTestHelper.assignProcureRequest(materialHeader, procurementServices, procurementDtoTransformer);

        // modification for these two materials performed below
        List<Material> materials = requestHeaderRepository.findAllMaterials();
        Assert.assertEquals(2, materials.size());
        ProcurementServicesTestHelper.modifyMaterials(materials, procurementServices);

        List<ProcureLine> procureLines = procureLineRepository.findAll();
        Assert.assertEquals(1, procureLines.size());
        Assert.assertEquals(4, procureLines.get(0).getMaterials().size());

        procureLines = ProcurementServicesTestHelper.procureInternalRequest(procureLineRepository, procurementServices);

        ProcureLine procureLine = procureLineRepository.findProcureLineById(procureLines.get(0).getId());
        ProcureLineDTO procureLineDTO = ProcurementHelper.transformAsDTO(procureLine);

        // Act
        procurementServices.updateProcureLine(procureLineDTO, "revert", null, null, null, null, false, null);

        // Assert
        ProcureLine revertedProcureLine = procureLineRepository.findById(procureLineDTO.getId());
        Assert.assertEquals(ProcureLineStatus.WAIT_TO_PROCURE, revertedProcureLine.getStatus());
        Assert.assertEquals(2, revertedProcureLine.getMaterials().size());
        Assert.assertNull(revertedProcureLine.getRequisition());
        for (Material material : revertedProcureLine.getMaterials()) {
            if (material.getMaterialType().equals(MaterialType.USAGE)) {
                Assert.assertTrue(true);
            }
        }
    }

    @Test
    @Ignore
    public void testCancelModification() throws GloriaApplicationException, IOException {
        // Arrange
        ProcurementServicesTestHelper.assignProcureRequest(null, procurementServices, procurementDtoTransformer);
        ProcurementServicesTestHelper.createMRWithSamePartInfoDiffTestObject(procurementServices, requestTransformer);
        MaterialHeader materialHeader = requestHeaderRepository.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(null, null, "FH-1246-4311","", null).get(0);
        ProcurementServicesTestHelper.assignProcureRequest(materialHeader, procurementServices, procurementDtoTransformer);

        // modification for these two materials performed below
        List<Material> materials = requestHeaderRepository.findAllMaterials();
        Assert.assertEquals(2, materials.size());
        ProcurementServicesTestHelper.modifyMaterials(materials, procurementServices);

        List<ProcureLine> procureLines = procureLineRepository.findAll();
        Assert.assertEquals(1, procureLines.size());
        Assert.assertEquals(4, procureLines.get(0).getMaterials().size());
        long modificationId = materials.get(0).getModificationId();

        // Act
        procurementServices.cancelModification(modificationId);

        // Assert
        List<ProcureLine> procureLinesAfterCancel = procureLineRepository.findAll();
        Assert.assertEquals(1, procureLinesAfterCancel.size());
        List<Material> materialsAfterCancel = procureLinesAfterCancel.get(0).getMaterials();
        Assert.assertEquals(2, materialsAfterCancel.size());
        for (Material material : materialsAfterCancel) {
            Assert.assertEquals(MaterialType.USAGE, material.getMaterialType());
        }
    }

}
