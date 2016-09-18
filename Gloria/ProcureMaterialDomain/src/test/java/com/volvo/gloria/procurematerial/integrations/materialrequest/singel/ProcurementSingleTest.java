package com.volvo.gloria.procurematerial.integrations.materialrequest.singel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.authorization.c.TeamType;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.b.CompanyCodeTransformer;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.b.RequestTransformer;
import com.volvo.gloria.procurematerial.c.dto.MaterialDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialHeaderDTO;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.status.changeid.ChangeIdStatus;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class ProcurementSingleTest extends AbstractTransactionalTestCase {
    public ProcurementSingleTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    @Inject
    private ProcurementServices procurementServices;

    @Inject
    private UserServices userServices;
    
    @Inject
    private CommonServices commonServices;
    
    @Inject
    private CompanyCodeTransformer companyCodeTransformer;

    @Inject
    private RequestTransformer requestTransformer;
    
    private static final String INITDATA_COMPANY_CODE_XML = "globaldataTest/CompanyCode.xml";
    private static final String INITDATA_TEAMS_XML = "globaldataTest/Team.xml";

    private static final String TEST_USER_XML = "testscenarios/integrations/V075345.xml";

    private static final String TEST1_TIRE_REQUST = "testscenarios/integrations/materialrequest/singel/test1/tirerequest.xml";
    private static final String TEST2_RIM_REQUST = "testscenarios/integrations/materialrequest/singel/test2/rimrequest.xml";

    @Before
    public void setUpTestData() throws JAXBException, IOException, GloriaApplicationException {
        userServices.createUserData(IOUtil.getStringFromClasspath(TEST_USER_XML));
        commonServices.addSyncCompanyCode(companyCodeTransformer.transformCompanyCode(IOUtil.getStringFromClasspath(INITDATA_COMPANY_CODE_XML)));
        userServices.createTeamData(IOUtil.getStringFromClasspath(INITDATA_TEAMS_XML));
    }

    @Test
    public void test1FirstSingleRequestAccepted() throws GloriaApplicationException, IOException {
        // Arrange
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(TEST1_TIRE_REQUST)));

        // Act

        // Assert
        ChangeId changeId = procurementServices.findChangeIdByTechId("6893016698917156782");
        Assert.assertEquals(ChangeIdStatus.ACCEPTED, changeId.getStatus());
        Assert.assertEquals("S1V1", changeId.getMtrlRequestVersion());

        List<MaterialHeader> materialHeaders = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(null, "FH", null, null, null);
        Assert.assertNotNull(materialHeaders);
        Assert.assertEquals(1, materialHeaders.size());

        MaterialHeader theMaterialHeader = materialHeaders.get(0);
        Assert.assertNotNull(theMaterialHeader);
        Assert.assertEquals("S1", theMaterialHeader.getMtrlRequestId());
        Assert.assertNotNull(theMaterialHeader.getAccepted());
    }

    @Test
    public void test2SecondSingleRequestWaitConfirm() throws GloriaApplicationException, IOException {
        // Arrange
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(TEST1_TIRE_REQUST)));

        // Act
        List<MaterialHeader> materialHeaders = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(null, "FH", null, null, null);
        List<MaterialHeaderDTO> materialHeaderDTOs = new ArrayList<MaterialHeaderDTO>();
        MaterialHeaderDTO materialHeaderDTO = new MaterialHeaderDTO();
        materialHeaderDTO.setId(materialHeaders.get(0).getMaterialHeaderOid());
        materialHeaderDTO.setVersion(materialHeaders.get(0).getVersion());
        materialHeaderDTO.setCompanyCode(materialHeaders.get(0).getCompanyCode());
        materialHeaderDTOs.add(materialHeaderDTO);

        procurementServices.assignOrUnassignMateriaHeaders(materialHeaderDTOs, "assign", "V075345", "GOT_VE", TeamType.MATERIAL_CONTROL.name(), "V075345");
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(TEST2_RIM_REQUST)));

        // Assert
        ChangeId changeId = procurementServices.findChangeIdByTechId("696216461243227909");
        Assert.assertEquals(ChangeIdStatus.WAIT_CONFIRM, changeId.getStatus());
        Assert.assertEquals("S1V2", changeId.getMtrlRequestVersion());

        materialHeaders = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(null, "FH", null, null, null);
        Assert.assertEquals(1, materialHeaders.size());
        ChangeId acceptedChangeId = materialHeaders.get(0).getAccepted().getChangeId();
        Assert.assertEquals(ChangeIdStatus.ACCEPTED, acceptedChangeId.getStatus());
        Assert.assertEquals("S1V1", acceptedChangeId.getMtrlRequestVersion());
    }

    @Test
    public void test2SecondSingleRequestAcceptWaitConfirm() throws GloriaApplicationException, IOException {
        // Arrange
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(TEST1_TIRE_REQUST)));

        List<MaterialHeader> materialHeaders = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(null, "FH", null, null, null);
        List<MaterialHeaderDTO> materialHeaderDTOs = new ArrayList<MaterialHeaderDTO>();
        MaterialHeaderDTO materialHeaderDTO = new MaterialHeaderDTO();
        materialHeaderDTO.setId(materialHeaders.get(0).getMaterialHeaderOid());
        materialHeaderDTO.setVersion(materialHeaders.get(0).getVersion());
        materialHeaderDTO.setCompanyCode(materialHeaders.get(0).getCompanyCode());
        materialHeaderDTOs.add(materialHeaderDTO);

        procurementServices.assignOrUnassignMateriaHeaders(materialHeaderDTOs, "assign", "V075345", "GOT_VE", TeamType.MATERIAL_CONTROL.name(), "V075345");
        procurementServices.manageRequest(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(TEST2_RIM_REQUST)));
        List<MaterialDTO> materialDTOs = new ArrayList<MaterialDTO>();
        // Act
        ChangeId changeId = procurementServices.findChangeIdByTechId("696216461243227909");
        procurementServices.acceptOrRejectChangeId("accept", changeId.getChangeIdOid(), "V075345", materialDTOs);

        // Assert
        materialHeaders = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(null, "FH", null, null, null);
        Assert.assertEquals(2, materialHeaders.size());
        ChangeId acceptedChangeId = materialHeaders.get(1).getAccepted().getChangeId();
        Assert.assertEquals(ChangeIdStatus.ACCEPTED, acceptedChangeId.getStatus());
        Assert.assertEquals("S1V2", acceptedChangeId.getMtrlRequestVersion());
    }

    @Test
    public void test3SecondSingleRequestRejectWaitConfirm() throws GloriaApplicationException, IOException {
        // Arrange
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(TEST1_TIRE_REQUST)));

        List<MaterialHeader> materialHeaders = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(null, "FH", null, null, null);
        List<MaterialHeaderDTO> materialHeaderDTOs = new ArrayList<MaterialHeaderDTO>();
        MaterialHeaderDTO materialHeaderDTO = new MaterialHeaderDTO();
        materialHeaderDTO.setId(materialHeaders.get(0).getMaterialHeaderOid());
        materialHeaderDTO.setVersion(materialHeaders.get(0).getVersion());
        materialHeaderDTO.setCompanyCode(materialHeaders.get(0).getCompanyCode());
        materialHeaderDTOs.add(materialHeaderDTO);

        procurementServices.assignOrUnassignMateriaHeaders(materialHeaderDTOs, "assign", "V075345", "GOT_VE", TeamType.MATERIAL_CONTROL.name(), "V075345");
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(TEST2_RIM_REQUST)));
        List<MaterialDTO> materialDTOs = new ArrayList<MaterialDTO>();
        // Act
        ChangeId changeId = procurementServices.findChangeIdByTechId("696216461243227909");
        procurementServices.acceptOrRejectChangeId("reject", changeId.getChangeIdOid(), "V075345", materialDTOs);

        // Assert
        materialHeaders = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(null, "FH", null, null, null);
        Assert.assertEquals(1, materialHeaders.size());
        ChangeId acceptedChangeId = materialHeaders.get(0).getAccepted().getChangeId();
        Assert.assertEquals(ChangeIdStatus.ACCEPTED, acceptedChangeId.getStatus());
        Assert.assertEquals("S1V1", acceptedChangeId.getMtrlRequestVersion());

        changeId = procurementServices.findChangeIdByTechId("696216461243227909");
        Assert.assertEquals(ChangeIdStatus.REJECTED, changeId.getStatus());
        Assert.assertEquals("S1V2", changeId.getMtrlRequestVersion());

    }
}
