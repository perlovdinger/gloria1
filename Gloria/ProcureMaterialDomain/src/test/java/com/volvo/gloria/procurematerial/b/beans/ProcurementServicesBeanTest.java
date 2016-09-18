package com.volvo.gloria.procurematerial.b.beans;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.authorization.c.TeamType;
import com.volvo.gloria.common.b.CarryOverTransformer;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.b.CompanyCodeTransformer;
import com.volvo.gloria.common.b.CostCenterTransformer;
import com.volvo.gloria.common.b.GLAccountTransformer;
import com.volvo.gloria.common.b.WBSElementTransformer;
import com.volvo.gloria.common.c.dto.GlAccountDTO;
import com.volvo.gloria.common.c.dto.SiteDTO;
import com.volvo.gloria.common.c.dto.WbsElementDTO;
import com.volvo.gloria.common.d.entities.CarryOver;
import com.volvo.gloria.common.d.entities.CompanyCode;
import com.volvo.gloria.common.d.entities.GlAccount;
import com.volvo.gloria.common.d.entities.InternalOrderSap;
import com.volvo.gloria.common.d.entities.Site;
import com.volvo.gloria.common.d.entities.SupplierCounterPart;
import com.volvo.gloria.common.d.entities.WbsElement;
import com.volvo.gloria.common.repositories.b.CarryOverRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.ProcurementDtoTransformer;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.b.RequestTransformer;
import com.volvo.gloria.procurematerial.c.ChangeType;
import com.volvo.gloria.procurematerial.c.ProcureResponsibility;
import com.volvo.gloria.procurematerial.c.dto.MaterialHeaderDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialHeaderGroupingDTO;
import com.volvo.gloria.procurematerial.c.dto.ProcureLineDTO;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.PartAlias;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatus;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.request.RequestType;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.util.ProcurementHelper;
import com.volvo.gloria.procurematerial.util.ProcurementServicesTestHelper;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class ProcurementServicesBeanTest extends AbstractTransactionalTestCase {
    public ProcurementServicesBeanTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    @Inject
    private ProcurementServices procurementServices;

    @Inject
    private UserServices userServices;

    @Inject
    private CommonServices commonServices;

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
    private CarryOverRepository carryOverRepo;

    @Inject
    private MaterialServices materialServices;
    
    @Inject
    ProcurementDtoTransformer procurementDtoTransformer;
    
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

    private List<ProcureLine> procureInternalRequest() throws GloriaApplicationException {
        List<ProcureLine> procureLines = procureLineRepository.findAll();
        List<ProcureLineDTO> procureLineDTOs = ProcurementHelper.transformEntityToDTO(procureLines);
        List<SupplierCounterPart> supplierCounterParts = commonServices.getSupplierCounterPartsByCompanyCode("SE27");
       
        for (ProcureLineDTO procureLineDTO : procureLineDTOs) {
            procureLineDTO.setStatus(ProcureLineStatus.PROCURED.toString());
            procureLineDTO.setProcureResponsibility(ProcureResponsibility.PROCURER.name());
            procureLineDTO.setBuyerCode("XYZ");
            procureLineDTO.setAdditionalQuantity(5l);
            procureLineDTO.setUnitPrice(2);
            procureLineDTO.setCurrency("SEK");
            procureLineDTO.setProcureType("INTERNAL");

            if(supplierCounterParts != null && !supplierCounterParts.isEmpty()) {
                procureLineDTO.setSupplierCounterPartID(supplierCounterParts.get(0).getId());
            }
            procureLineDTO.setOrderNo("111");
            procureLineDTO.setOrderStaDate(DateUtil.getSqlDate());
            procureLineDTO.setSupplierName("very good supplier");
        }

        List<ProcureLine> updateProcureLines = procurementServices.updateProcureLines(procureLineDTOs, "procure", null, "all", "1722", null);

        Assert.assertEquals(1, updateProcureLines.size());
        return updateProcureLines;
    }

    @Test
    public void testGetUnassignedRequestHeaders() throws GloriaApplicationException {
        // Arrange
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);
        // Act
        pageObject = procurementServices.getRequestHeaders(pageObject, false, "ALL");
        // Assert
        Assert.assertNotNull(pageObject.getGridContents());
    }

    @Test
    public void testFindProcureRequestHeaderById() throws Exception {
        // Arrange
        List<MaterialHeader> procureRequestHeaders = procurementServices.findAllMaterialHeaders();
        MaterialHeader header = procureRequestHeaders.get(0);
        Long requestHeaderOID = header.getMaterialHeaderOid();

        // Act
        MaterialHeader requestHeader = procurementServices.findRequestHeaderById(requestHeaderOID);

        // Assert
        Assert.assertNotNull(requestHeader);
    }

    /**
     * scenario 1
     * 
     * same Part info,Finance info,referenceGroup
     * 
     * @throws GloriaApplicationException
     * 
     
    @Test
    public void testDefaultGroupingScenarioOne() throws GloriaApplicationException {
        // Arrange
        List<Material> allRequestLines = new ArrayList<Material>();

        FinanceHeader financeHeader1 = new FinanceHeader();
        financeHeader1.setProjectId("PROJ123");
        financeHeader1.setGlAccount("GL123");
        financeHeader1.setCostCenter("COS342");
        financeHeader1.setWbsCode("WBS345");
        financeHeader1.setCompanyCode("SE27");
        procurementServices.addFinanceHeader(financeHeader1);

        MaterialHeader requestHeader1 = new MaterialHeader();
        requestHeader1.setReferenceId("TO01");//
        requestHeader1.setBuildId("1");
        requestHeader1.setCompanyCode("SE27");
        requestHeader1.setRequestType(RequestType.TESTOBJECT_FIRST_BUILD);
        // Version
        // ChangeId
        ChangeId changeId1 = new ChangeId();
        changeId1.setChangeTechId("44");
        changeId1.setMtrlRequestVersion("SD1V1");
        changeId1.setType(ChangeType.FIRST_ASSEMBLY);
        changeId1.setProcureMessageId("900290");
        changeId1.setPriority("High");

        MaterialHeaderVersion headerVersion1 = new MaterialHeaderVersion();
        headerVersion1.setReferenceGroup("RGROUP1234");
        headerVersion1.setMaterialHeader(requestHeader1);
        requestHeader1.setAccepted(headerVersion1);
        headerVersion1.setChangeId(changeId1);
        requestHeader1 = requestHeaderRepository.save(requestHeader1);

        List<Material> materials = new ArrayList<Material>();
        Material material1 = new Material();
        material1.setMaterialHeader(requestHeader1);
        material1.setMaterialType(MaterialType.USAGE);
        material1.setAdd(changeId1);
        material1.setPartNumber("AsPart1");
        material1.setPartVersion("4");
        material1.setPartName("Part 2");
        material1.setFunctionGroup("functionGrp2");
        material1.setPartAffiliation("Test");
        material1.setPartModification("Testing");
        material1.setObjectNumber("DFU123");
        material1.setItemToVariantLinkId(123457);
        material1.setModularHarness("MH01");
        material1.setFunctionGroup("FG01");
        material1.setFinanceHeader(financeHeader1);
        material1.setStatus(MaterialStatus.ADDED);

        materials.add(material1);
        allRequestLines.addAll(materials);
        requestHeader1.setMaterials(materials);

        // 2nd
        FinanceHeader financeHeaderX2 = new FinanceHeader();
        financeHeaderX2.setProjectId("PROJ123");
        financeHeaderX2.setGlAccount("GL123");
        financeHeaderX2.setCostCenter("COS342");
        financeHeaderX2.setWbsCode("WBS345");
        financeHeaderX2.setCompanyCode("SE27");
        procurementServices.addFinanceHeader(financeHeaderX2);

        MaterialHeader requestHeader2 = new MaterialHeader();
        requestHeader2.setReferenceId("TO01");//
        requestHeader2.setBuildId("1");
        requestHeader2.setCompanyCode("SE27");
        requestHeader2.setRequestType(RequestType.TESTOBJECT_FIRST_BUILD);
        // ChangeId
        ChangeId changeId2 = new ChangeId();
        changeId2.setChangeTechId("44");
        changeId2.setMtrlRequestVersion("SD1V1");
        changeId2.setType(ChangeType.FIRST_ASSEMBLY);
        changeId2.setProcureMessageId("900290");
        changeId2.setPriority("High");
        // Version
        MaterialHeaderVersion headerVersion2 = new MaterialHeaderVersion();
        headerVersion2.setReferenceGroup("RGROUP1234");
        headerVersion2.setMaterialHeader(requestHeader2);
        headerVersion2.setChangeId(changeId2);
        requestHeader2.setAccepted(headerVersion2);

        List<Material> materials2 = new ArrayList<Material>();
        Material material2 = new Material();
        material2.setMaterialHeader(requestHeader2);
        material2.setAdd(changeId2);
        material2.setPartNumber("AsPart1");
        material2.setMaterialType(MaterialType.USAGE);
        material2.setPartVersion("4");
        material2.setPartName("Part 2");
        material2.setFunctionGroup("functionGrp2");
        material2.setPartAffiliation("Test");
        material2.setPartModification("Testing");
        material2.setObjectNumber("DFU123");
        material2.setItemToVariantLinkId(123457);
        material2.setModularHarness("MH01");
        material2.setFunctionGroup("FG01");
        material2.setFinanceHeader(financeHeaderX2);
        material2.setStatus(MaterialStatus.ADDED);
        materials2.add(material2);
        allRequestLines.addAll(materials2);
        requestHeader2.setMaterials(materials2);
        requestHeader2 = requestHeaderRepository.save(requestHeader2);

        List<MaterialGroupDTO> materialGroupDTOs = new ArrayList<MaterialGroupDTO>();
        MaterialGroupDTO materialGroupDTO = new MaterialGroupDTO();
        materialGroupDTO.setMaterials(allRequestLines);
        materialGroupDTOs.add(materialGroupDTO);

        // Act
        ProcureGroupHelper groupHelper = new ProcureGroupHelper(materialGroupDTOs, GloriaParams.GROUP_TYPE_DEFAULT);
        List<ProcureLine> groups = groupHelper.getProcureLines(commonServices, null);
        // Assert
        Assert.assertNotNull(groups);
        Assert.assertEquals(1, groups.size());
    }*/

    /**
     * scenario 2
     * 
     * difference in Finance info same Part info,referenceGroup,CR type,CR ID
     * 
     * @throws GloriaApplicationException
     * 
     
    @Test
    public void testDefaultGroupingScenarioTwo() throws GloriaApplicationException {
        // Arrange
        List<Material> allRequestLines = new ArrayList<Material>();

        FinanceHeader financeHeader1 = new FinanceHeader();
        financeHeader1.setProjectId("PROJ123");
        financeHeader1.setGlAccount("GL123");
        financeHeader1.setCostCenter("COS342");
        financeHeader1.setWbsCode("WBS345");
        financeHeader1.setCompanyCode("SE27");
        procurementServices.addFinanceHeader(financeHeader1);

        MaterialHeader requestHeader1 = new MaterialHeader();
        requestHeader1.setReferenceId("TO01");//
        requestHeader1.setBuildId("1");
        requestHeader1.setCompanyCode("SE27");
        requestHeader1.setRequestType(RequestType.TESTOBJECT_FIRST_BUILD);
        // ChangeId
        ChangeId changeId1 = new ChangeId();
        changeId1.setChangeTechId("44");
        changeId1.setMtrlRequestVersion("SD1V1");
        changeId1.setType(ChangeType.FIRST_ASSEMBLY);
        changeId1.setProcureMessageId("900290");
        changeId1.setPriority("High");

        // Version
        MaterialHeaderVersion headerVersion1 = new MaterialHeaderVersion();
        headerVersion1.setReferenceGroup("RGROUP1234");
        headerVersion1.setMaterialHeader(requestHeader1);
        headerVersion1.setChangeId(changeId1);
        requestHeader1.setAccepted(headerVersion1);
        requestHeader1 = requestHeaderRepository.save(requestHeader1);

        List<Material> requestLines = new ArrayList<Material>();
        Material material1 = new Material();
        material1.setMaterialHeader(requestHeader1);
        material1.setMaterialType(MaterialType.USAGE);
        material1.setAdd(changeId1);
        material1.setPartNumber("AsPart1");
        material1.setPartVersion("4");
        material1.setPartName("Part 2");
        material1.setFunctionGroup("functionGrp2");
        material1.setPartAffiliation("Test");
        material1.setPartModification("Testing");
        material1.setObjectNumber("DFU123");
        material1.setItemToVariantLinkId(123457);
        material1.setModularHarness("MH01");
        material1.setFunctionGroup("FG01");
        material1.setFinanceHeader(financeHeader1);
        material1.setStatus(MaterialStatus.ADDED);

        requestLines.add(material1);
        allRequestLines.addAll(requestLines);
        requestHeader1.setMaterials(requestLines);

        // 2nd
        FinanceHeader financeHeaderX2 = new FinanceHeader();
        financeHeaderX2.setProjectId("PROJ1234");
        financeHeaderX2.setGlAccount("GL1234");
        financeHeaderX2.setCostCenter("COS3424");
        financeHeaderX2.setWbsCode("WBS345");
        financeHeaderX2.setCompanyCode("SE27");
        procurementServices.addFinanceHeader(financeHeaderX2);

        MaterialHeader requestHeader2 = new MaterialHeader();
        requestHeader2.setReferenceId("TO01");//
        requestHeader2.setBuildId("1");
        requestHeader2.setCompanyCode("SE27");
        requestHeader2.setRequestType(RequestType.TESTOBJECT_FIRST_BUILD);
        // ChangeId
        ChangeId changeId2 = new ChangeId();
        changeId2.setChangeTechId("44");
        changeId2.setMtrlRequestVersion("SD1V1");
        changeId2.setType(ChangeType.FIRST_ASSEMBLY);
        changeId2.setProcureMessageId("900290");
        changeId2.setPriority("High");
        // Version
        MaterialHeaderVersion headerVersion2 = new MaterialHeaderVersion();
        headerVersion2.setReferenceGroup("RGROUP1234");
        headerVersion2.setMaterialHeader(requestHeader2);
        headerVersion2.setChangeId(changeId2);
        requestHeader2.setAccepted(headerVersion2);

        List<Material> materials2 = new ArrayList<Material>();
        Material material2 = new Material();
        material2.setMaterialHeader(requestHeader2);
        material2.setMaterialType(MaterialType.USAGE);
        material2.setAdd(changeId2);
        material2.setPartNumber("AsPart1");
        material2.setPartVersion("4");
        material2.setPartName("Part 2");
        material2.setFunctionGroup("functionGrp2");
        material2.setPartAffiliation("Test");
        material2.setPartModification("Testing");
        material2.setObjectNumber("DFU123");
        material2.setItemToVariantLinkId(123457);
        material2.setModularHarness("MH01");
        material2.setFunctionGroup("FG01");
        material2.setFinanceHeader(financeHeaderX2);
        material2.setStatus(MaterialStatus.ADDED);

        materials2.add(material2);
        allRequestLines.addAll(materials2);
        requestHeader2.setMaterials(materials2);
        requestHeader2 = requestHeaderRepository.save(requestHeader2);
        List<MaterialGroupDTO> materialGroupDTOs = new ArrayList<MaterialGroupDTO>();
        MaterialGroupDTO materialGroupDTO = new MaterialGroupDTO();
        materialGroupDTO.setMaterials(allRequestLines);
        materialGroupDTOs.add(materialGroupDTO);

        // Act
        ProcureGroupHelper groupHelper = new ProcureGroupHelper(materialGroupDTOs, GloriaParams.GROUP_TYPE_DEFAULT);
        List<ProcureLine> groups = groupHelper.getProcureLines(commonServices, null);
        // Assert
        Assert.assertNotNull(groups);
        Assert.assertEquals(2, groups.size());
    }*/

    /**
     * scenario 3
     * 
     * difference in part information same finance info
     * 
     * @throws GloriaApplicationException
     * 
     
    @Test
    public void testDefaultGroupingScenarioThree() throws GloriaApplicationException {
        // Arrange
        List<Material> allRequestLines = new ArrayList<Material>();

        FinanceHeader financeHeader1 = new FinanceHeader();
        financeHeader1.setProjectId("PROJ123");
        financeHeader1.setGlAccount("GL123");
        financeHeader1.setCostCenter("COS342");
        financeHeader1.setWbsCode("WBS345");
        financeHeader1.setCompanyCode("SE27");
        procurementServices.addFinanceHeader(financeHeader1);

        MaterialHeader requestHeader1 = new MaterialHeader();
        requestHeader1.setReferenceId("TO01");//
        requestHeader1.setBuildId("1");
        requestHeader1.setCompanyCode("SE27");
        requestHeader1.setRequestType(RequestType.TESTOBJECT_FIRST_BUILD);
        // ChangeId
        ChangeId changeId1 = new ChangeId();
        changeId1.setChangeTechId("44");
        changeId1.setMtrlRequestVersion("SD1V1");
        changeId1.setType(ChangeType.FIRST_ASSEMBLY);
        changeId1.setProcureMessageId("900290");
        changeId1.setPriority("High");

        // Version
        MaterialHeaderVersion headerVersion1 = new MaterialHeaderVersion();
        headerVersion1.setReferenceGroup("RGROUP1234");
        headerVersion1.setMaterialHeader(requestHeader1);
        requestHeader1.setAccepted(headerVersion1);
        headerVersion1.setChangeId(changeId1);
        requestHeader1 = requestHeaderRepository.save(requestHeader1);

        List<Material> materials = new ArrayList<Material>();
        Material material1 = new Material();
        material1.setMaterialHeader(requestHeader1);
        material1.setMaterialType(MaterialType.USAGE);
        material1.setAdd(changeId1);
        material1.setPartNumber("AsPart1");
        material1.setPartVersion("4");
        material1.setPartName("Part 2");
        material1.setFunctionGroup("functionGrp2");
        material1.setPartAffiliation("Test");
        material1.setPartModification("Testing");
        material1.setObjectNumber("DFU123");
        material1.setItemToVariantLinkId(123457);
        material1.setModularHarness("MH01");
        material1.setFunctionGroup("FG01");
        material1.setFinanceHeader(financeHeader1);
        material1.setStatus(MaterialStatus.ADDED);

        materials.add(material1);
        allRequestLines.addAll(materials);
        requestHeader1.setMaterials(materials);

        // 2nd
        FinanceHeader financeHeaderX2 = new FinanceHeader();
        financeHeaderX2.setProjectId("PROJ123");
        financeHeaderX2.setGlAccount("GL123");
        financeHeaderX2.setCostCenter("COS342");
        financeHeaderX2.setWbsCode("WBS345");
        financeHeaderX2.setCompanyCode("SE27");
        procurementServices.addFinanceHeader(financeHeaderX2);

        MaterialHeader requestHeader2 = new MaterialHeader();
        requestHeader2.setReferenceId("TO01");//
        requestHeader2.setBuildId("1");
        requestHeader2.setCompanyCode("SE27");
        requestHeader2.setRequestType(RequestType.TESTOBJECT_FIRST_BUILD);
        // ChangeId
        ChangeId changeId2 = new ChangeId();
        changeId2.setChangeTechId("44");
        changeId2.setMtrlRequestVersion("SD1V1");
        changeId2.setType(ChangeType.FIRST_ASSEMBLY);
        changeId2.setProcureMessageId("900290");
        changeId2.setPriority("High");

        // Version
        MaterialHeaderVersion headerVersion2 = new MaterialHeaderVersion();
        headerVersion2.setReferenceGroup("RGROUP1234");
        headerVersion2.setMaterialHeader(requestHeader2);
        headerVersion2.setChangeId(changeId2);
        requestHeader2.setAccepted(headerVersion2);

        List<Material> materials2 = new ArrayList<Material>();
        Material material2 = new Material();
        material2.setMaterialHeader(requestHeader2);
        material2.setMaterialType(MaterialType.USAGE);
        material2.setAdd(changeId2);
        material2.setPartNumber("AsPart2");
        material2.setPartVersion("5");
        material2.setPartName("Part 2");
        material2.setFunctionGroup("functionGrp2");
        material2.setPartAffiliation("Test2");
        material2.setPartModification("Testing_1");
        material2.setObjectNumber("DFU123");
        material2.setItemToVariantLinkId(123457);
        material2.setModularHarness("MH01");
        material2.setFunctionGroup("FG01");
        material2.setFinanceHeader(financeHeaderX2);
        material2.setStatus(MaterialStatus.ADDED);

        materials2.add(material2);
        allRequestLines.addAll(materials2);
        requestHeader2.setMaterials(materials2);
        requestHeader2 = requestHeaderRepository.save(requestHeader2);

        List<MaterialGroupDTO> materialGroupDTOs = new ArrayList<MaterialGroupDTO>();
        MaterialGroupDTO materialGroupDTO = new MaterialGroupDTO();
        materialGroupDTO.setMaterials(allRequestLines);
        materialGroupDTOs.add(materialGroupDTO);

        // Act
        ProcureGroupHelper groupHelper = new ProcureGroupHelper(materialGroupDTOs, GloriaParams.GROUP_TYPE_DEFAULT);
        List<ProcureLine> groups = groupHelper.getProcureLines(commonServices, null);
        // Assert
        Assert.assertNotNull(groups);
        Assert.assertEquals(2, groups.size());
    }*/

    @Test
    public void testFindProcureLineModified() throws GloriaApplicationException {
        // Arrange
        ProcureLine procureLine = new ProcureLine();
        procureLine.setMaterialControllerId("1234");
        procureLine.setpPartName("PN1");
        procureLine.setpPartAffiliation("testPart");
        procureLine.setResponsibility(ProcureResponsibility.PROCURER);
        procureLine.setCurrency("EUR");
        procurementServices.addProcureLine(procureLine);
        List<ProcureLine> procureLines = procurementServices.findAllProcureLines();
        for (ProcureLine line : procureLines) {
            if (line.getpPartAffiliation().equals("testPart")) {
                procureLine = line;
                break;
            }
        }
        // Act
        ProcureLineDTO updatedProcureLine = procurementServices.findProcureLineById(procureLine.getProcureLineOid(), null);

        // Assert
        Assert.assertNotNull(updatedProcureLine);
        Assert.assertEquals(procureLine.getProcureLineOid(), updatedProcureLine.getId());
        Assert.assertEquals("PN1", updatedProcureLine.getpPartName());
    }

    @Test
    public void testToProcureLaterWithMoreThanOneProcurements() throws Exception {
        // TODO Send a couple
        // Arrange
        List<Material> allRequestLines = new ArrayList<Material>();

        FinanceHeader financeHeader1 = new FinanceHeader();
        financeHeader1.setProjectId("PROJ123");
        financeHeader1.setGlAccount("GL123");
        financeHeader1.setCostCenter("COS342");
        financeHeader1.setWbsCode("WBS345");
        financeHeader1.setCompanyCode("SE27");
        procurementServices.addFinanceHeader(financeHeader1);

        MaterialHeader requestHeader1 = new MaterialHeader();
        requestHeader1.setReferenceId("TO01");//
        requestHeader1.setBuildId("1");
        requestHeader1.setCompanyCode("SE27");
        requestHeader1.setRequestType(RequestType.TESTOBJECT_FIRST_BUILD);
        // Version
        // ChangeId
        ChangeId changeId1 = new ChangeId();
        changeId1.setChangeTechId("44");
        changeId1.setMtrlRequestVersion("SD1V1");
        changeId1.setType(ChangeType.FIRST_ASSEMBLY);
        changeId1.setProcureMessageId("900290");
        changeId1.setPriority("High");

        MaterialHeaderVersion headerVersion1 = new MaterialHeaderVersion();
        headerVersion1.setReferenceGroup("RGROUP1234");
        headerVersion1.setMaterialHeader(requestHeader1);
        requestHeader1.setAccepted(headerVersion1);
        headerVersion1.setChangeId(changeId1);

        List<Material> materials = new ArrayList<Material>();
        Material material1 = new Material();
        material1.setMaterialHeader(requestHeader1);
        material1.setAdd(changeId1);
        material1.setPartNumber("AsPart1");
        material1.setPartVersion("4");
        material1.setPartName("Part 2");
        material1.setFunctionGroup("functionGrp2");
        material1.setPartAffiliation("Test");
        material1.setPartModification("Testing");
        material1.setObjectNumber("DFU123");
        material1.setItemToVariantLinkId(123457);
        material1.setModularHarness("MH01");
        material1.setFunctionGroup("FG01");
        material1.setFinanceHeader(financeHeader1);
        material1.setStatus(MaterialStatus.ADDED);
        material1.setMaterialType(MaterialType.USAGE);

        MaterialLine materialLine1 = new MaterialLine();
        material1.getMaterialLine().add(materialLine1);
        materialLine1.setQuantity(1l);
        materialLine1.setStatus(MaterialLineStatus.CREATED);
        materialLine1.setMaterial(material1);

        materials.add(material1);
        allRequestLines.addAll(materials);
        requestHeader1.setMaterials(materials);
        requestHeader1 = requestHeaderRepository.save(requestHeader1);
        // 2nd
        FinanceHeader financeHeaderX2 = new FinanceHeader();
        financeHeaderX2.setProjectId("PROJ123");
        financeHeaderX2.setGlAccount("GL123");
        financeHeaderX2.setCostCenter("COS342");
        financeHeaderX2.setWbsCode("WBS345");
        financeHeaderX2.setCompanyCode("SE27");
        procurementServices.addFinanceHeader(financeHeaderX2);

        MaterialHeader requestHeader2 = new MaterialHeader();
        requestHeader2.setReferenceId("TO01");//
        requestHeader2.setBuildId("1");
        requestHeader2.setCompanyCode("SE27");
        requestHeader2.setRequestType(RequestType.TESTOBJECT_FIRST_BUILD);
        // ChangeId
        ChangeId changeId2 = new ChangeId();
        changeId2.setChangeTechId("44");
        changeId2.setMtrlRequestVersion("SD1V1");
        changeId2.setType(ChangeType.FIRST_ASSEMBLY);
        changeId2.setProcureMessageId("900290");
        changeId2.setPriority("High");
        // Version
        MaterialHeaderVersion headerVersion2 = new MaterialHeaderVersion();
        headerVersion2.setReferenceGroup("RGROUP1234");
        headerVersion2.setMaterialHeader(requestHeader2);
        headerVersion2.setChangeId(changeId2);
        requestHeader2.setAccepted(headerVersion2);

        List<Material> materials2 = new ArrayList<Material>();
        Material material2 = new Material();
        material2.setMaterialHeader(requestHeader2);
        material2.setAdd(changeId2);
        material2.setPartNumber("AsPart1");
        material2.setPartVersion("4");
        material2.setPartName("Part 2");
        material2.setFunctionGroup("functionGrp2");
        material2.setPartAffiliation("Test");
        material2.setPartModification("Testing");
        material2.setObjectNumber("DFU123");
        material2.setItemToVariantLinkId(123457);
        material2.setModularHarness("MH01");
        material2.setFunctionGroup("FG01");
        material2.setFinanceHeader(financeHeaderX2);
        material2.setStatus(MaterialStatus.ADDED);
        material2.setMaterialType(MaterialType.USAGE);

        MaterialLine materialLine2 = new MaterialLine();
        material2.getMaterialLine().add(materialLine2);
        materialLine2.setQuantity(1l);
        materialLine2.setMaterial(material2);
        materialLine2.setStatus(MaterialLineStatus.CREATED);
        materials2.add(material2);
        allRequestLines.addAll(materials2);
        requestHeader2.setMaterials(materials2);
        requestHeader2 = requestHeaderRepository.save(requestHeader2);

        List<MaterialHeaderDTO> requestsToAssign = new ArrayList<MaterialHeaderDTO>();
        requestsToAssign.add(procurementDtoTransformer.transformAsDTO(requestHeader1));
        requestsToAssign.add(procurementDtoTransformer.transformAsDTO(requestHeader2));

        procurementServices.assignOrUnassignMateriaHeaders(requestsToAssign, "assign", "all", "GOT_PE", TeamType.MATERIAL_CONTROL.name(), null);

        List<ProcureLine> procureLines = procurementServices.findAllProcureLines();
        ProcureLine procureLine = procureLines.get(0);
        long procureLineId = procureLine.getProcureLineOid();
        List<Material> updatedMaterials = materialServices.findMaterialsByProcureLineIdForProcureDetails(procureLineId);
        Assert.assertNotNull(updatedMaterials);
        Assert.assertEquals(2, updatedMaterials.size());

        Material material = updatedMaterials.get(0);

        // Act
        List<Long> materialIds = new ArrayList<Long>();
        long materialOID = material.getMaterialOID();
        materialIds.add(materialOID);
        procurementServices.procureLater(materialIds);

        // Assert
        List<ProcureLine> updatedProcureLines = procurementServices.findAllProcureLines();
        Assert.assertEquals(2, updatedProcureLines.size());
        for (ProcureLine procureLine2 : updatedProcureLines) {
            if (materialOID == procureLine2.getMaterials().get(0).getMaterialOID()) {
                Assert.assertTrue(true);
            }

        }
    }

    /*
     * @Ignore
     * 
     * @Test public void testToProcureLaterAllProcurements() throws Exception{ //Send all procurement and should throw exception. // Arrange List<ProcureLine>
     * procureLines = procurementServices.findAllProcureLines(); ProcureLine procureLine = procureLines.get(0); long procureLineId =
     * procureLine.getProcureLineOid(); //List<Procurement> procurements = procurementServices.findProcurementsByProcureLineId(procureLineId); //Procurement
     * procurement = procurements.get(0);
     * 
     * procureLine.setStatus(ProcureLineStatus.WAIT_TO_PROCURE); procureLine.setProcureType(ProcureType.INTERNAL);
     * 
     * procureLine = procurementServices.updateProcureLine(ProcurementHelper.transformAsDTO(procureLine), "all");
     * 
     * // Act List<Long> procurementIds = new ArrayList<Long>(); try { for (Procurement procurement :
     * procurementServices.findMaterialsByProcureLineId(procureLineId)) { procurementIds.add(procurement.getProcurementOid()); }
     * procurementServices.procureLater(procurementIds); } catch (GloriaApplicationException e) { System.out.println(e.getErrorMessage());
     * Assert.assertTrue(e.getErrorMessage().contains("Procure line should have at least one Procurement and not yet Procured")); }
     * 
     * //this should be found for (Long procurementId : procurementIds) { procurementServices.findProcurementById(procurementId); } }
     */

    @Test
    public void testGetAllGlAccounts() {
        List<GlAccount> glAccounts = commonServices.getAllGlAccounts();
        Assert.assertNotNull(glAccounts);
        // Assert.assertEquals(23, glAccounts.size());
        List<GlAccountDTO> glAccountDTOs = ProcurementHelper.transformGlaccountEtyToDTO(glAccounts);
        GlAccountDTO glAccountDTO = glAccountDTOs.get(0);
        Assert.assertNotNull(glAccountDTO);
        Assert.assertEquals("549001", glAccountDTO.getAccountNumber());
    }

    @Test
    public void testGetAllWbsElements() {
        List<WbsElement> wbsElements = commonServices.getAllWbsElements();
        Assert.assertNotNull(wbsElements);
        Assert.assertEquals(1005, wbsElements.size());
        List<WbsElementDTO> wbsElementDTOs = ProcurementHelper.transformWbsElementEtyToDTO(wbsElements);
        WbsElementDTO wbsElementDTO = wbsElementDTOs.get(0);
        Assert.assertNotNull(wbsElementDTO);
        Assert.assertEquals("W60-7060", wbsElementDTO.getProjectId());
    }

    private MaterialHeader[] createDataForManualGrouping() throws GloriaApplicationException {
        MaterialHeader[] requestHeaders = new MaterialHeader[7];

        // first TO
        FinanceHeader financeHeader = new FinanceHeader();
        financeHeader.setProjectId("PROJ123");
        financeHeader.setGlAccount("GL123");
        financeHeader.setCostCenter("COS342");
        financeHeader.setWbsCode("WBS345");
        financeHeader.setInternalOrderNoSAP("INSAP123");
        financeHeader.setCompanyCode("SE27");
        procurementServices.addFinanceHeader(financeHeader);

        MaterialHeader requestHeader = new MaterialHeader();
        requestHeader.setReferenceId("REFID1");
        requestHeader.setBuildId("1");
        requestHeader.setCompanyCode("SE27");
        requestHeader.setRequestType(RequestType.TESTOBJECT_FIRST_BUILD);
        // ChangeId
        ChangeId changeIdOne = new ChangeId();
        changeIdOne.setChangeTechId("44");
        changeIdOne.setMtrlRequestVersion("SD1V1");
        changeIdOne.setType(ChangeType.FIRST_ASSEMBLY);
        changeIdOne.setProcureMessageId("900290");
        changeIdOne.setPriority("High");

        // Version
        MaterialHeaderVersion headerVersion = new MaterialHeaderVersion();
        headerVersion.setReferenceGroup("RGROUP1");
        headerVersion.setMaterialHeader(requestHeader);
        headerVersion.setOutboundLocationId("outBoundLocationId");
        headerVersion.setChangeId(changeIdOne);
        requestHeader.setAccepted(headerVersion);

        List<Material> materials = new ArrayList<Material>();
        Material materialOne = new Material();
        materialOne.setMaterialHeader(requestHeader);
        materialOne.setAdd(changeIdOne);
        materialOne.setPartAffiliation("PA1");
        materialOne.setPartNumber("AsPart1");
        materialOne.setPartVersion("4");
        materialOne.setObjectNumber("DFU123");
        materialOne.setPartModification("PM1");
        materialOne.setStatus(MaterialStatus.ADDED);
        materialOne.setMaterialType(MaterialType.USAGE);

        MaterialLine materialLine1 = new MaterialLine();
        materialLine1.setQuantity(1L);
        materialLine1.setMaterial(materialOne);
        materialLine1.setStatus(MaterialLineStatus.CREATED);
        materialOne.getMaterialLine().add(materialLine1);

        materialOne.setFinanceHeader(financeHeader);

        materials.add(materialOne);

        requestHeader.setMaterials(materials);
        requestHeader = requestHeaderRepository.save(requestHeader);
        requestHeaders[0] = requestHeader;

        // second TO
        FinanceHeader financeHeaderTwo = new FinanceHeader();
        financeHeaderTwo.setProjectId("PROJ123");
        financeHeaderTwo.setGlAccount("GL123");
        financeHeaderTwo.setCostCenter("COS342");
        financeHeaderTwo.setWbsCode("WBS345");
        financeHeaderTwo.setInternalOrderNoSAP("INSAP123");
        financeHeaderTwo.setCompanyCode("SE27");
        procurementServices.addFinanceHeader(financeHeaderTwo);

        MaterialHeader requestHeaderTwo = new MaterialHeader();
        requestHeaderTwo.setReferenceId("REFID12");
        requestHeaderTwo.setBuildId("1");
        requestHeaderTwo.setCompanyCode("SE27");
        requestHeaderTwo.setRequestType(RequestType.TESTOBJECT_FIRST_BUILD);
        // ChangeId
        ChangeId changeIdTwo1 = new ChangeId();
        changeIdTwo1.setChangeTechId("44");
        changeIdTwo1.setMtrlRequestVersion("SD1V1");
        changeIdTwo1.setType(ChangeType.FIRST_ASSEMBLY);
        changeIdTwo1.setProcureMessageId("900290");
        changeIdTwo1.setPriority("High");

        // Version
        MaterialHeaderVersion headerVersionTwo = new MaterialHeaderVersion();
        headerVersionTwo.setReferenceGroup("RGROUP12");
        headerVersionTwo.setMaterialHeader(requestHeaderTwo);
        headerVersionTwo.setChangeId(changeIdTwo1);
        requestHeaderTwo.setAccepted(headerVersionTwo);

        List<Material> materialsTwo = new ArrayList<Material>();
        Material materialTwo1 = new Material();
        materialTwo1.setMaterialHeader(requestHeaderTwo);
        materialTwo1.setAdd(changeIdTwo1);
        materialTwo1.setPartAffiliation("PA123");
        materialTwo1.setPartNumber("AsPart1232");
        materialTwo1.setPartVersion("4333");
        materialTwo1.setObjectNumber("DFU123456");
        materialTwo1.setPartModification("PM122");
        materialTwo1.setFinanceHeader(financeHeaderTwo);
        materialTwo1.setStatus(MaterialStatus.ADDED);
        materialTwo1.setMaterialType(MaterialType.USAGE);
        
        MaterialLine materialLine2 = new MaterialLine();
        materialLine2.setQuantity(1L);
        materialLine2.setStatus(MaterialLineStatus.CREATED);
        materialLine2.setMaterial(materialTwo1);
        materialTwo1.getMaterialLine().add(materialLine2);
        materialsTwo.add(materialTwo1);

        // ChangeId
        ChangeId changeIdTwo2 = new ChangeId();
        changeIdTwo2.setChangeTechId("44");
        changeIdTwo2.setMtrlRequestVersion("SD1V1");
        changeIdTwo2.setType(ChangeType.FIRST_ASSEMBLY);
        changeIdTwo2.setProcureMessageId("900290");
        changeIdTwo2.setPriority("High");

        Material materialTwo2 = new Material();
        materialTwo2.setMaterialHeader(requestHeaderTwo);
        materialTwo2.setAdd(changeIdTwo2);
        materialTwo2.setPartAffiliation("PA12");
        materialTwo2.setPartNumber("AsPart13");
        materialTwo2.setPartVersion("43");
        materialTwo2.setObjectNumber("DFU1233");
        materialTwo2.setPartModification("PM1");
        materialTwo2.setFinanceHeader(financeHeaderTwo);
        materialTwo2.setStatus(MaterialStatus.ADDED);
        materialTwo2.setMaterialType(MaterialType.USAGE);
        MaterialLine materialLine3 = new MaterialLine();
        materialLine3.setQuantity(1L);
        materialLine3.setStatus(MaterialLineStatus.CREATED);
        materialLine3.setMaterial(materialTwo2);
        materialTwo2.getMaterialLine().add(materialLine3);

        materialsTwo.add(materialTwo2);

        requestHeaderTwo.setMaterials(materialsTwo);
        requestHeaderTwo = requestHeaderRepository.save(requestHeaderTwo);
        requestHeaders[1] = requestHeaderTwo;

        // Third TO
        FinanceHeader financeHeaderThree = new FinanceHeader();
        financeHeaderThree.setProjectId("PROJ123");
        financeHeaderThree.setGlAccount("GL123");
        financeHeaderThree.setCostCenter("COS342");
        financeHeaderThree.setWbsCode("WBS345");
        financeHeaderThree.setInternalOrderNoSAP("INSAP123");
        financeHeaderThree.setCompanyCode("SE27");
        procurementServices.addFinanceHeader(financeHeaderThree);

        MaterialHeader requestHeaderThree = new MaterialHeader();
        requestHeaderThree.setReferenceId("REFID1234");
        requestHeaderThree.setBuildId("1");
        requestHeaderThree.setCompanyCode("SE27");
        requestHeaderThree.setRequestType(RequestType.TESTOBJECT_FIRST_BUILD);
        // ChangeId
        ChangeId changeIdThree = new ChangeId();
        changeIdThree.setChangeTechId("44");
        changeIdThree.setMtrlRequestVersion("SD1V1");
        changeIdThree.setType(ChangeType.FIRST_ASSEMBLY);
        changeIdThree.setProcureMessageId("900290");
        changeIdThree.setPriority("High");
        // Version
        MaterialHeaderVersion headerVersionThree = new MaterialHeaderVersion();
        headerVersionThree.setReferenceGroup("RGROUP1234");
        headerVersionThree.setChangeId(changeIdThree);
        headerVersionThree.setMaterialHeader(requestHeaderThree);
        requestHeaderThree.setAccepted(headerVersionThree);

        List<Material> naterialsThree = new ArrayList<Material>();
        Material materialThree = new Material();
        materialThree.setMaterialHeader(requestHeaderThree);
        materialThree.setAdd(changeIdThree);
        materialThree.setPartAffiliation("PA1");
        materialThree.setPartNumber("AsPart1");
        materialThree.setPartVersion("4");
        materialThree.setObjectNumber("DFU123");
        materialThree.setPartModification("PM1");
        materialThree.setStatus(MaterialStatus.ADDED);
        materialThree.setMaterialType(MaterialType.USAGE);
        materialThree.setFinanceHeader(financeHeaderThree);

        MaterialLine materialLine4 = new MaterialLine();
        materialLine4.setQuantity(1L);
        materialLine4.setStatus(MaterialLineStatus.CREATED);
        materialLine4.setMaterial(materialThree);
        materialThree.getMaterialLine().add(materialLine4);

        naterialsThree.add(materialThree);

        requestHeaderThree.setMaterials(naterialsThree);
        requestHeaderThree = requestHeaderRepository.save(requestHeaderThree);
        requestHeaders[2] = requestHeaderThree;

        // Fourth TO
        FinanceHeader financeHeaderFour = new FinanceHeader();
        financeHeaderFour.setProjectId("PROJ123456");
        financeHeaderFour.setGlAccount("GL123");
        financeHeaderFour.setCostCenter("COS342");
        financeHeaderFour.setWbsCode("WBS345");
        financeHeaderFour.setInternalOrderNoSAP("INSAP123");
        financeHeaderFour.setCompanyCode("SE27");
        procurementServices.addFinanceHeader(financeHeaderFour);

        MaterialHeader requestHeaderFour = new MaterialHeader();
        requestHeaderFour.setReferenceId("REFID123422");
        requestHeaderFour.setBuildId("1");
        requestHeaderFour.setCompanyCode("SE27");
        requestHeaderFour.setRequestType(RequestType.TESTOBJECT_FIRST_BUILD);
        // ChangeId
        ChangeId changeIdFour = new ChangeId();
        changeIdFour.setChangeTechId("44");
        changeIdFour.setMtrlRequestVersion("SD1V1");
        changeIdFour.setType(ChangeType.FIRST_ASSEMBLY);
        changeIdFour.setProcureMessageId("900290");
        changeIdFour.setPriority("High");

        // Version
        MaterialHeaderVersion headerVersionFour = new MaterialHeaderVersion();
        headerVersionFour.setReferenceGroup("RGROUP1234");
        headerVersionFour.setMaterialHeader(requestHeaderFour);
        headerVersionFour.setChangeId(changeIdFour);
        requestHeaderFour.setAccepted(headerVersionFour);

        List<Material> materialsFour = new ArrayList<Material>();
        Material materialFour = new Material();
        materialFour.setMaterialHeader(requestHeaderFour);
        materialFour.setAdd(changeIdFour);
        materialFour.setPartAffiliation("PA1");
        materialFour.setPartNumber("AsPart1");
        materialFour.setPartVersion("4");
        materialFour.setObjectNumber("DFU123");
        materialFour.setPartModification("PM1");
        materialFour.setStatus(MaterialStatus.ADDED);
        materialFour.setMaterialType(MaterialType.USAGE);

        MaterialLine materialLine5 = new MaterialLine();
        materialLine5.setQuantity(1L);
        materialLine5.setStatus(MaterialLineStatus.CREATED);
        materialLine5.setMaterial(materialFour);
        materialFour.getMaterialLine().add(materialLine5);
        materialFour.setFinanceHeader(financeHeaderFour);

        materialsFour.add(materialFour);

        requestHeaderFour.setMaterials(materialsFour);
        requestHeaderFour = requestHeaderRepository.save(requestHeaderFour);
        requestHeaders[3] = requestHeaderFour;

        // FIFTH TO
        FinanceHeader financeHeaderFive = new FinanceHeader();
        financeHeaderFive.setProjectId("PROJ123");
        financeHeaderFive.setGlAccount("GL123");
        financeHeaderFive.setCostCenter("COS342");
        financeHeaderFive.setWbsCode("WBS345");
        financeHeaderFive.setInternalOrderNoSAP("INSAP123");
        financeHeaderFive.setCompanyCode("SE27");
        procurementServices.addFinanceHeader(financeHeaderFive);

        MaterialHeader requestHeaderFive = new MaterialHeader();
        requestHeaderFive.setReferenceId("REFID1234");
        requestHeaderFive.setBuildId("1");
        requestHeaderFive.setCompanyCode("SE27");
        requestHeaderFive.setRequestType(RequestType.TESTOBJECT_FIRST_BUILD);
        // ChangeId
        ChangeId changeIdFive = new ChangeId();
        changeIdFive.setChangeTechId("44");
        changeIdFive.setMtrlRequestVersion("SD1V1");
        changeIdFive.setType(ChangeType.FIRST_ASSEMBLY);
        changeIdFive.setProcureMessageId("900290");
        changeIdFive.setPriority("High");
        // Version
        MaterialHeaderVersion headerVersionFive = new MaterialHeaderVersion();
        headerVersionFive.setReferenceGroup("RGROUP1");
        headerVersionFive.setChangeId(changeIdFive);
        headerVersionFive.setMaterialHeader(requestHeaderFive);
        requestHeaderFive.setAccepted(headerVersionFive);

        List<Material> materialsFive = new ArrayList<Material>();
        Material materialFive = new Material();
        materialFive.setMaterialHeader(requestHeaderFive);
        materialFive.setAdd(changeIdFive);
        materialFive.setPartAffiliation("PA1");
        materialFive.setPartNumber("AsPart1");
        materialFive.setPartVersion("4");
        materialFive.setObjectNumber("DFU123");
        materialFive.setPartModification("PM1");
        materialFive.setStatus(MaterialStatus.ADDED);
        materialFive.setMaterialType(MaterialType.USAGE);
        materialFive.setFinanceHeader(financeHeaderFive);

        MaterialLine materialLine6 = new MaterialLine();
        materialLine6.setQuantity(1L);
        materialLine6.setStatus(MaterialLineStatus.CREATED);
        materialLine6.setMaterial(materialFive);
        materialFive.getMaterialLine().add(materialLine6);

        materialsFive.add(materialFive);

        requestHeaderFive.setMaterials(materialsFive);
        requestHeaderFive = requestHeaderRepository.save(requestHeaderFive);
        requestHeaders[4] = requestHeaderFive;

        // SIXTH TO
        FinanceHeader financeHeaderSix = new FinanceHeader();
        financeHeaderSix.setProjectId("PROJ123");
        financeHeaderSix.setGlAccount("GL123");
        financeHeaderSix.setCostCenter("COS342");
        financeHeaderSix.setWbsCode("WBS345");
        financeHeaderSix.setInternalOrderNoSAP("INSAP123");
        financeHeaderSix.setCompanyCode("SE27");
        procurementServices.addFinanceHeader(financeHeaderSix);

        MaterialHeader requestHeaderSix = new MaterialHeader();
        requestHeaderSix.setReferenceId("REFID12");
        requestHeaderSix.setBuildId("1");
        requestHeaderSix.setCompanyCode("SE27");
        requestHeaderSix.setRequestType(RequestType.TESTOBJECT_FIRST_BUILD);
        // ChangeId
        ChangeId changeIdSix = new ChangeId();
        changeIdSix.setChangeTechId("44");
        changeIdSix.setMtrlRequestVersion("SD1V1");
        changeIdSix.setType(ChangeType.FIRST_ASSEMBLY);
        changeIdSix.setProcureMessageId("900290");
        changeIdSix.setPriority("High");

        // Version
        MaterialHeaderVersion headerVersionSix = new MaterialHeaderVersion();
        headerVersionSix.setReferenceGroup("RGROUP1");
        headerVersionSix.setMaterialHeader(requestHeaderSix);
        headerVersionSix.setChangeId(changeIdSix);
        requestHeaderSix.setAccepted(headerVersionSix);

        List<Material> materialsSix = new ArrayList<Material>();
        Material materialSix = new Material();
        materialSix.setMaterialHeader(requestHeaderSix);
        materialSix.setAdd(changeIdSix);
        materialSix.setPartAffiliation("PA123");
        materialSix.setPartNumber("AsPart1232");
        materialSix.setPartVersion("4333");
        materialSix.setObjectNumber("DFU123456");
        materialSix.setPartModification("PM122");
        materialSix.setFinanceHeader(financeHeaderSix);
        materialSix.setStatus(MaterialStatus.ADDED);
        materialSix.setMaterialType(MaterialType.USAGE);
        
        MaterialLine materialLine7 = new MaterialLine();
        materialLine7.setQuantity(1L);
        materialLine7.setStatus(MaterialLineStatus.CREATED);
        materialLine7.setMaterial(materialSix);
        materialSix.getMaterialLine().add(materialLine7);
        materialsSix.add(materialSix);
        requestHeaderSix.setMaterials(materialsSix);
        requestHeaderSix = requestHeaderRepository.save(requestHeaderSix);
        requestHeaders[5] = requestHeaderSix;

        // Seventh TO
        FinanceHeader financeHeaderSeven = new FinanceHeader();
        financeHeaderSeven.setProjectId("PROJ123");
        financeHeaderSeven.setGlAccount("GL123");
        financeHeaderSeven.setCostCenter("COS342");
        financeHeaderSeven.setWbsCode("WBS345");
        financeHeaderSeven.setInternalOrderNoSAP("INSAP123");
        financeHeaderSeven.setCompanyCode("SE27");
        procurementServices.addFinanceHeader(financeHeaderSeven);

        MaterialHeader requestHeaderSeven = new MaterialHeader();
        requestHeaderSeven.setBuildId("1");
        requestHeaderSeven.setCompanyCode("SE27");
        requestHeaderSeven.setRequestType(RequestType.TESTOBJECT_FIRST_BUILD);
        // ChangeId
        ChangeId changeIdSeven = new ChangeId();
        changeIdSeven.setChangeTechId("44");
        changeIdSeven.setMtrlRequestVersion("SD1V1");
        changeIdSeven.setType(ChangeType.FIRST_ASSEMBLY);
        changeIdSeven.setProcureMessageId("900290");
        changeIdSeven.setPriority("High");

        // Version
        MaterialHeaderVersion headerVersionSeven = new MaterialHeaderVersion();
        headerVersionSeven.setMaterialHeader(requestHeaderSeven);
        headerVersionSeven.setChangeId(changeIdSeven);
        requestHeaderSeven.setAccepted(headerVersionSeven);

        List<Material> materialsSeven = new ArrayList<Material>();
        Material materialSeven1 = new Material();
        materialSeven1.setMaterialHeader(requestHeaderSeven);
        materialSeven1.setAdd(changeIdSeven);
        materialSeven1.setPartAffiliation("PA123");
        materialSeven1.setPartNumber("AsPart1232");
        materialSeven1.setPartVersion("4333");
        materialSeven1.setObjectNumber("DFU123456");
        materialSeven1.setPartModification("PM122");
        materialSeven1.setFinanceHeader(financeHeaderSeven);
        materialSeven1.setStatus(MaterialStatus.ADDED);
        materialSeven1.setMaterialType(MaterialType.USAGE);
        
        MaterialLine materialLine8 = new MaterialLine();
        materialLine8.setQuantity(1L);
        materialLine8.setStatus(MaterialLineStatus.CREATED);
        materialLine8.setMaterial(materialSeven1);
        materialSeven1.getMaterialLine().add(materialLine8);
        materialsSeven.add(materialSeven1);

        Material materialSeven2 = new Material();
        materialSeven2.setMaterialHeader(requestHeaderSeven);
        materialSeven2.setAdd(changeIdSeven);
        materialSeven2.setPartAffiliation("PA1236");
        materialSeven2.setPartNumber("AsPart12326");
        materialSeven2.setPartVersion("V8");
        materialSeven2.setObjectNumber("DFU123456");
        materialSeven2.setPartModification("PM122");
        materialSeven2.setFinanceHeader(financeHeaderSeven);
        materialSeven2.setStatus(MaterialStatus.ADDED);
        materialSeven2.setMaterialType(MaterialType.USAGE);
        
        MaterialLine materialLine9 = new MaterialLine();
        materialLine9.setQuantity(1L);
        materialLine9.setStatus(MaterialLineStatus.CREATED);
        materialLine9.setMaterial(materialSeven2);
        materialSeven2.getMaterialLine().add(materialLine9);
        materialsSeven.add(materialSeven2);
        requestHeaderSeven.setMaterials(materialsSeven);
        requestHeaderSeven = requestHeaderRepository.save(requestHeaderSeven);
        requestHeaders[6] = requestHeaderSeven;
        return requestHeaders;
    }

    @Test
    public void testGetConsignorIdsForValidCarryOver() throws GloriaApplicationException {
        // Arrange
        Site site = new Site();
        site.setSiteCode("CUR BUS");
        site.setSiteName("Curitiba Bus");
        site.setSiteId("2801");        
        commonServices.addSite(site);

        Site sparePartSite = new Site();
        sparePartSite.setSiteCode("UD");
        sparePartSite.setSiteName("UDTrucksSpareParts");
        sparePartSite.setSiteId("UD");        
        commonServices.addSite(sparePartSite);

        ProcureLine procureLine = new ProcureLine();
        procureLine.setpPartAffiliation(GloriaParams.PART_AFFILIATION_VOLVO);
        procureLine.setpPartNumber("11299");

        procurementServices.addProcureLine(procureLine);

        CarryOver carryOver1 = new CarryOver();
        carryOver1.setPartNumber("11299");
        carryOver1.setStartDate(new Date(DateUtil.getDate(2010, 00, 30).getTime()));
        carryOver1.setCustomerId("2801");
        carryOver1.setCustomerName("BUS");
        carryOverRepo.save(carryOver1);

        // Act
        List<SiteDTO> siteDTOs = procurementServices.getConsignorIds(procureLine.getProcureLineOid());

        // Assert
        Assert.assertNull(siteDTOs);
        
    }

    @Test
    public void testNoGetConsignorIdsForInvalidCarryOver() throws GloriaApplicationException {
        // Arrange
        Site site = new Site();
        site.setSiteCode("CUR BUS");
        site.setSiteName("Curitiba Bus");
        site.setSiteId("2801");
       
        commonServices.addSite(site);

        Site sparePartSite = new Site();
        sparePartSite.setSiteCode("UD");
        sparePartSite.setSiteName("UDTrucksSpareParts");
        sparePartSite.setSiteId("UD");
        commonServices.addSite(sparePartSite);

        ProcureLine procureLine = new ProcureLine();
        procureLine.setpPartAffiliation(GloriaParams.PART_AFFILIATION_VOLVO);
        procureLine.setpPartNumber("11299");

        procurementServices.addProcureLine(procureLine);

        CarryOver carryOver1 = new CarryOver();
        carryOver1.setPartNumber("11299");
        carryOver1.setStartDate(new Date(DateUtil.getForeverDate().getTime()));
        carryOver1.setCustomerId("2801");
        carryOver1.setCustomerName("BUS");
        carryOverRepo.save(carryOver1);

        // Act
        List<SiteDTO> siteDTOs = procurementServices.getConsignorIds(procureLine.getProcureLineOid());

        // Assert
        Assert.assertNull(siteDTOs);
    }

    @Test
    public void testGetAllInternalOrderNoSAP() {
        // Arrange
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(15);
        // Act
        List<InternalOrderSap> internalOrderSaps = commonServices.getAllInternalOrderNoSAP(pageObject, null);
        // Assert
        Assert.assertNotNull(internalOrderSaps);
        Assert.assertTrue(internalOrderSaps.size() > 0);
    }

    @Test
    public void testManageRequest() throws IOException {
        // Arrange

        // Act(already loading in setUpTestData)

        // Assert
        MaterialHeader requestHeader = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(null, "FH-1245", "1", "4311", null).get(0);
        Assert.assertNotNull(requestHeader);
        Assert.assertEquals("FH-1245", requestHeader.getReferenceId());
        Assert.assertNotNull(requestHeader.getMaterialHeaderVersions());
        Assert.assertNotNull(requestHeader.getMaterials());
        Assert.assertNotNull(requestHeader.getMaterials().get(0).getAdd());
        Assert.assertEquals(2147483647, requestHeader.getMaterials().get(0).getProcureLinkId());
        Assert.assertNotNull(requestHeader.getAccepted().getChangeId());
        Assert.assertNotNull(requestHeader.getMaterials().get(0).getFinanceHeader());
    }

    @Test
    public void testFindAllCompanyCodes() {
        // Arrange

        // Act
        List<CompanyCode> companyCodes = commonServices.findAllCompanyCodes();

        // Assert
        Assert.assertNotNull(companyCodes);
        Assert.assertEquals(8, companyCodes.size());
    }

    @Test
    public void testFindRequestHeaderByMtrlRequestId() {
        // Arrange
        // Act
        MaterialHeader requestHeader = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(null, "FH-1245", "1", "4311", null).get(0);

        // Arrange
        Assert.assertNotNull(requestHeader);
        Assert.assertEquals("FH-1245", requestHeader.getReferenceId());
    }

    @Test
    public void testExportOnBuildSite() throws GloriaApplicationException {

        // Arrange
        ProcurementServicesTestHelper.assignProcureRequest(null, procurementServices, procurementDtoTransformer);
        List<ProcureLine> allProcurelines = procurementServices.findAllProcureLines();
        List<Long> procurelineIds = new ArrayList<Long>();
        for (ProcureLine procureline : allProcurelines) {
            procurelineIds.add(procureline.getProcureLineOid());
        }
        Assert.assertEquals(1, allProcurelines.size());

        // act
        FileToExportDTO fileToExportDTO = procurementServices.exportOnBuildSite(procurelineIds);

        // Assert
        Assert.assertNotNull(fileToExportDTO);
        Assert.assertNotNull(fileToExportDTO.getContent());
    }

    @Test
    public void testGetPartAliases() {
        // Arrange
        String volvoPartNumber = "11";

        // Act
        List<PartAlias> partAliases = procurementServices.getPartAliases(volvoPartNumber);

        // Assert
        Assert.assertNotNull(partAliases);

    }

    /**
     * Manual grouping
     * 
     * modifying a single material.
     * 
     * @throws GloriaApplicationException
     * 
     */
    @Test
    public void testManualGroupingScenarioOne() throws GloriaApplicationException {
        // Arrange
        MaterialHeader[] headers = createDataForManualGrouping();

        MaterialHeader header = headers[0];
        List<MaterialHeaderDTO> materialHeaders = new ArrayList<MaterialHeaderDTO>();
        materialHeaders.add(procurementDtoTransformer.transformAsDTO(header));

        List<Material> lines = headers[0].getMaterials();

        List<Long> materialIds = new ArrayList<Long>();
        materialIds.add(lines.get(0).getMaterialOID());

        procurementServices.assignOrUnassignMateriaHeaders(materialHeaders, "assign", "all", "GOT_PE", TeamType.MATERIAL_CONTROL.name(), null);
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

        // Act
        List<Material> groupedLines = procurementServices.groupMaterials(materialHeaderGroupingDTOs, "pPartNo", "pPartVersion", "pPartName", "pPartMod", null);
        // Assert
        List<ProcureLine> procureLines = procureLineRepository.findAll();
        Assert.assertNotNull(groupedLines);
        Assert.assertNotNull(procureLines);
        Assert.assertEquals(1, procureLines.size());
    }

    /**
     * Manual grouping
     * 
     * different test objects with same part information but different referenceGroup(requestlines)
     * 
     * @throws GloriaApplicationException
     * 
     */
    @Test
    public void testManualGroupingScenarioTwo() throws GloriaApplicationException {
        MaterialHeader[] headers = createDataForManualGrouping();

        MaterialHeader header1 = headers[0];
        MaterialHeader header2 = headers[2];
        List<MaterialHeaderDTO> materialHeaders = new ArrayList<MaterialHeaderDTO>();
        materialHeaders.add(procurementDtoTransformer.transformAsDTO(header1));
        materialHeaders.add(procurementDtoTransformer.transformAsDTO(header2));
        procurementServices.assignOrUnassignMateriaHeaders(materialHeaders, "assign", "all", "GOT_PE", TeamType.MATERIAL_CONTROL.name(), null);

        List<ProcureLine> procureLinesDefault = procureLineRepository.findAll();
        Assert.assertEquals(1, procureLinesDefault.size());

        List<Material> lines = new ArrayList<Material>();
        lines.add(headers[0].getMaterials().get(0));
        lines.add(headers[2].getMaterials().get(0));

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

        // Act
        List<Material> groupedLines = procurementServices.groupMaterials(materialHeaderGroupingDTOs, "pPartNo", "pPartVersion", "pPartName", "pPartMod", null);
        // Assert
        List<ProcureLine> procureLines = procureLineRepository.findAll();
        Assert.assertNotNull(groupedLines);
        Assert.assertNotNull(procureLines);
        Assert.assertEquals(1, procureLines.size());
    }

    /**
     * Manual grouping
     * 
     * different test objects with same part information and same referenceGroup(requestlines)
     * 
     * @throws GloriaApplicationException
     * 
     */
    @Test
    public void testManualGroupingScenarioThree() throws GloriaApplicationException {
        MaterialHeader[] headers = createDataForManualGrouping();

        MaterialHeader header1 = headers[0];
        MaterialHeader header2 = headers[4];
        List<MaterialHeaderDTO> materialHeaders = new ArrayList<MaterialHeaderDTO>();
        materialHeaders.add(procurementDtoTransformer.transformAsDTO(header1));
        procurementServices.assignOrUnassignMateriaHeaders(materialHeaders, "assign", "all", "GOT_PE", TeamType.MATERIAL_CONTROL.name(), null);

        List<MaterialHeaderDTO> materialHeaders2 = new ArrayList<MaterialHeaderDTO>();
        materialHeaders2.add(procurementDtoTransformer.transformAsDTO(header2));
        procurementServices.assignOrUnassignMateriaHeaders(materialHeaders2, "assign", "all", "GOT_PE", TeamType.MATERIAL_CONTROL.name(), null);

        List<ProcureLine> procureLinesDefault = procureLineRepository.findAll();
        Assert.assertEquals(1, procureLinesDefault.size());

        List<Material> lines = new ArrayList<Material>();
        lines.add(headers[0].getMaterials().get(0));
        lines.add(headers[4].getMaterials().get(0));

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

        // Act
        List<Material> groupedLines = procurementServices.groupMaterials(materialHeaderGroupingDTOs, "pPartNo", "pPartVersion", "pPartName", "pPartMod", null);
        // Assert
        List<ProcureLine> procureLines = procureLineRepository.findAll();
        Assert.assertNotNull(groupedLines);
        Assert.assertNotNull(procureLines);
        Assert.assertEquals(1, procureLines.size());
    }

    /**
     * Manual grouping
     * 
     * different test objects with different part information(requestlines) with different reference group - MOD2 type
     * 
     * @throws GloriaApplicationException
     * 
     */
    @Test
    public void testManualGroupingScenarioFour() throws GloriaApplicationException {
        MaterialHeader[] headers = createDataForManualGrouping();
        MaterialHeader header1 = headers[0];
        MaterialHeader header2 = headers[1];

        List<MaterialHeaderDTO> materialHeaders = new ArrayList<MaterialHeaderDTO>();
        materialHeaders.add(procurementDtoTransformer.transformAsDTO(header1));
        materialHeaders.add(procurementDtoTransformer.transformAsDTO(header2));
        procurementServices.assignOrUnassignMateriaHeaders(materialHeaders, "assign", "all", "GOT_PE", TeamType.MATERIAL_CONTROL.name(), null);

        List<ProcureLine> procureLinesDefault = procureLineRepository.findAll();
        Assert.assertEquals(3, procureLinesDefault.size());

        List<Material> lines = new ArrayList<Material>();
        lines.add(headers[0].getMaterials().get(0));
        lines.addAll(headers[1].getMaterials());

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

        // Act
        List<Material> groupedLines = procurementServices.groupMaterials(materialHeaderGroupingDTOs, "pPartNo", "pPartVersion", "pPartName", "pPartMod", null);
        // Assert
        List<ProcureLine> procureLines = procureLineRepository.findAll();
        Assert.assertNotNull(groupedLines);
        Assert.assertNotNull(procureLines);
        Assert.assertEquals(1, procureLines.size());
    }

    /**
     * Manual grouping
     * 
     * different test objects with different part information(requestlines) with same reference group - MOD2 type
     * 
     * @throws GloriaApplicationException
     * 
     */
    @Test
    public void testManualGroupingScenarioFive() throws GloriaApplicationException {
        MaterialHeader[] headers = createDataForManualGrouping();
        MaterialHeader header1 = headers[0];
        MaterialHeader header2 = headers[5];

        List<Material> lines = new ArrayList<Material>();
        lines.add(headers[0].getMaterials().get(0));
        lines.add(headers[5].getMaterials().get(0));

        List<MaterialHeaderDTO> materialHeaders = new ArrayList<MaterialHeaderDTO>();
        materialHeaders.add(procurementDtoTransformer.transformAsDTO(header1));
        materialHeaders.add(procurementDtoTransformer.transformAsDTO(header2));
        procurementServices.assignOrUnassignMateriaHeaders(materialHeaders, "assign", "all", "GOT_PE", TeamType.MATERIAL_CONTROL.name(), null);

        List<ProcureLine> procureLinesDefault = procureLineRepository.findAll();
        Assert.assertEquals(2, procureLinesDefault.size());

        List<MaterialHeaderGroupingDTO> materialHeaderGroupingDTOs = new ArrayList<MaterialHeaderGroupingDTO>();
        List<Long> materialIds = new ArrayList<Long>();
        for (Material material : lines) {
            materialIds.add(material.getMaterialOID());
            MaterialHeaderGroupingDTO materialHeaderGroupingDTO = new MaterialHeaderGroupingDTO();
            materialHeaderGroupingDTO.setProcurementQty(7);
            materialHeaderGroupingDTO.setMaterialIds(String.valueOf(material.getMaterialOID()));
            materialHeaderGroupingDTOs.add(materialHeaderGroupingDTO);
        }

        boolean isAllowed = procurementServices.isManualGroupingAllowed(materialIds, "material");
        Assert.assertTrue(isAllowed);

        // Act
        List<Material> groupedLines = procurementServices.groupMaterials(materialHeaderGroupingDTOs, "pPartNo", "pPartVersion", "pPartName", "pPartMod", null);
        // Assert
        List<ProcureLine> procureLines = procureLineRepository.findAll();
        Assert.assertNotNull(groupedLines);
        Assert.assertNotNull(procureLines);
        Assert.assertEquals(1, procureLines.size());
    }

    /**
     * Manual grouping
     * 
     * same test object with different part information(requestlines) - Mod2 type
     * 
     * @throws GloriaApplicationException
     * 
     */
    @Test
    public void testManualGroupingScenarioSix() throws GloriaApplicationException {
        MaterialHeader[] headers = createDataForManualGrouping();
        MaterialHeader header1 = headers[1];

        List<Material> lines = new ArrayList<Material>();
        lines.addAll(headers[1].getMaterials());

        List<MaterialHeaderDTO> materialHeaders = new ArrayList<MaterialHeaderDTO>();
        materialHeaders.add(procurementDtoTransformer.transformAsDTO(header1));
        procurementServices.assignOrUnassignMateriaHeaders(materialHeaders, "assign", "all", "GOT_PE", TeamType.MATERIAL_CONTROL.name(), null);

        List<ProcureLine> procureLinesDefault = procureLineRepository.findAll();
        Assert.assertEquals(2, procureLinesDefault.size());

        List<Long> materialIds = new ArrayList<Long>();
        for (Material material : lines) {
            materialIds.add(material.getMaterialOID());
        }

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

        boolean isAllowed = procurementServices.isManualGroupingAllowed(materialIds, "material");
        Assert.assertTrue(isAllowed);

        List<Material> groupedLines = procurementServices.groupMaterials(materialHeaderGroupingDTOs, "pPartNo", "pPartVersion", "pPartName", "pPartMod", null);
        // Assert
        List<ProcureLine> procureLines = procureLineRepository.findAll();
        Assert.assertNotNull(groupedLines);
        Assert.assertNotNull(procureLines);
        Assert.assertEquals(1, procureLines.size());
        Assert.assertNotNull(procureLines.get(0).getMaterials());
        List<Material> updatedMaterials = procureLines.get(0).getMaterials();
        Assert.assertEquals(3, updatedMaterials.size());
        for (Material material : updatedMaterials) {
            if (material.getMaterialType().equals(MaterialType.USAGE_REPLACED)) {
                Assert.assertNotNull(material.getReplacedByOid());
            }
        }
    }

    /**
     * Manual grouping
     * 
     * different test objects with different financial information(finance header)
     * 
     * invalid grouping criteria
     * 
     * @throws GloriaApplicationException
     * 
    
    @Test
    public void testManualGroupingScenarioSeven() throws GloriaApplicationException {
        MaterialHeader[] headers = createDataForManualGrouping();

        List<Material> lines = new ArrayList<Material>();
        lines.add(headers[0].getMaterials().get(0));
        lines.add(headers[3].getMaterials().get(0));

        List<MaterialGroupDTO> materialGroupingDTOs = new ArrayList<MaterialGroupDTO>();

        for (Material material : lines) {
            MaterialGroupDTO materialHeaderGroupingDTO = new MaterialGroupDTO();
            materialHeaderGroupingDTO.getMaterials().add(material);
            materialGroupingDTOs.add(materialHeaderGroupingDTO);
        }

        ProcureGroupHelper defaultGroupHelper = new ProcureGroupHelper(materialGroupingDTOs, GloriaParams.GROUP_TYPE_DEFAULT);
        List<ProcureLine> groups = defaultGroupHelper.getProcureLines(commonServices, null);

        List<Material> linesForGrouping = new ArrayList<Material>();
        linesForGrouping.add(groups.get(0).getMaterials().get(0));
        linesForGrouping.add(groups.get(1).getMaterials().get(0));

        // Act
        ProcureGroupHelper manualGroupHelper = new ProcureGroupHelper(materialGroupingDTOs, GloriaParams.GROUP_TYPE_DIFF_TOBJ_SAME_PART);

        // Assert
        Assert.assertFalse(manualGroupHelper.isValidForManualGrouping());
    }
 */
    /**
     * @throws GloriaApplicationException
     *             Manual Grouping - No test object , but different part information.
     */
    @Test
    public void testManualGroupingScenarioEight() throws GloriaApplicationException {
        MaterialHeader[] headers = createDataForManualGrouping();
        MaterialHeader header6 = headers[6];

        List<Material> lines = new ArrayList<Material>();
        lines.addAll(headers[6].getMaterials());

        List<MaterialHeaderDTO> materialHeaders = new ArrayList<MaterialHeaderDTO>();
        materialHeaders.add(procurementDtoTransformer.transformAsDTO(header6));
        procurementServices.assignOrUnassignMateriaHeaders(materialHeaders, "assign", "all", "GOT_PE", TeamType.MATERIAL_CONTROL.name(), null);

        List<ProcureLine> procureLinesDefault = procureLineRepository.findAll();
        Assert.assertEquals(2, procureLinesDefault.size());

        List<Long> materialIds = new ArrayList<Long>();
        for (Material material : lines) {
            materialIds.add(material.getMaterialOID());
        }

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

        boolean isAllowed = procurementServices.isManualGroupingAllowed(materialIds, "material");
        Assert.assertTrue(isAllowed);

        // Act
        List<Material> groupedLines = procurementServices.groupMaterials(materialHeaderGroupingDTOs, "pPartNo", "pPartVersion", "pPartName", "pPartMod", null);

        // Assert
        List<ProcureLine> procureLines = procureLineRepository.findAll();
        Assert.assertNotNull(groupedLines);
        Assert.assertNotNull(procureLines);
        Assert.assertEquals(1, procureLines.size());
        Assert.assertNotNull(procureLines.get(0).getMaterials());
        List<Material> updatedMaterials = procureLines.get(0).getMaterials();
        Assert.assertEquals(3, updatedMaterials.size());
        for (Material material : updatedMaterials) {
            if (material.getMaterialType().equals(MaterialType.USAGE_REPLACED)) {
                Assert.assertNotNull(material.getReplacedByOid());
            }
        }
    }

    @Test
    public void testUpdateProcureLineAsInternal() throws GloriaApplicationException {
        // Arrange
        ProcurementServicesTestHelper.assignProcureRequest(null, procurementServices, procurementDtoTransformer);
        List<ProcureLine> updateProcureLines = procureInternalRequest();

        // Assert
        Assert.assertEquals(1, updateProcureLines.size());
    }

    @Test
    @Ignore
    public void testReassignMaterialController() throws GloriaApplicationException {
        // Arrange
        PageObject pageObject = ProcurementServicesTestHelper.assignProcureRequest(null, procurementServices, procurementDtoTransformer);

        List<MaterialHeaderDTO> materialHeaderDTOs = ProcurementHelper.transformAsDTOs(pageObject.getGridContents());
        for (MaterialHeaderDTO materialHeaderDTO : materialHeaderDTOs) {
            materialHeaderDTO.setVersion(2);
        }
        String userId = "test";

        // Act
        procurementServices.assignOrUnassignMateriaHeaders(materialHeaderDTOs, "assign", userId, "xx", TeamType.MATERIAL_CONTROL.name(), null);

        // Assert
        List<MaterialHeaderDTO> reassignedMaterialHeaderDTOs = ProcurementHelper.transformAsDTOs(procurementServices.getRequestHeaders(pageObject, true, "ALL")
                                                                                                                    .getGridContents());
        Assert.assertNotNull(reassignedMaterialHeaderDTOs);
        MaterialHeaderDTO materialHeaderDTO = reassignedMaterialHeaderDTOs.get(0);
        Assert.assertEquals("test", materialHeaderDTO.getAssignedMaterialControllerId());
    }

    @Test
    public void testGetBorrowableMaterialLineswithReadyToShipStatus() throws GloriaApplicationException, IOException {

        // ARRANGE
        procurementServices.manageRequest(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(INITDATA_REQUEST_XML)));
        procurementServices.manageRequest(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(INITDATA_REQUEST_BORROWPARTS_XML)));

        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);

        List<Material> materials = procurementServices.findAllMaterials();

        for (Material individualMaterials : materials) {
            List<MaterialLine> listOfMaterialLines = individualMaterials.getMaterialLine();
            listOfMaterialLines.get(0).setStatus(MaterialLineStatus.READY_TO_SHIP);
        }
        procurementServices.groupMaterials(materials);

        // ACT
        PageObject barrowedPageObject = materialServices.getBorrowableMaterialLines(pageObject, materials.get(0).getMaterialLine().get(0).getMaterialLineOID());
        // ASSERT
        Assert.assertTrue(barrowedPageObject.getGridContents().size() == 0);
    }
 }
