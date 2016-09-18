package com.volvo.gloria.procurematerial.integrations.materialrequest.multiple;

import java.io.IOException;
import java.util.ArrayList;
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

public class ProcurementMultipleTest extends AbstractTransactionalTestCase {
    public ProcurementMultipleTest() {
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

    private static final String TEST1_NEW_REQUEST = "testscenarios/integrations/materialrequest/multiple/test1/newrequest.xml";
    private static final String TEST2_UPDATE_REQUEST = "testscenarios/integrations/materialrequest/multiple/test2/updaterequest.xml";
    private static final String TEST3_CANCEL_REQUEST = "testscenarios/integrations/materialrequest/multiple/test3/cancelrequest.xml";

    EntityManager em = null;

    @PersistenceContext(unitName = "ProcureMaterialDomainPU")
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    
    @Before
    public void setUpTestData() throws JAXBException, IOException, GloriaApplicationException {
        userServices.createUserData(IOUtil.getStringFromClasspath(TEST_USER_XML));
        commonServices.addSyncCompanyCode(companyCodeTransformer.transformCompanyCode(IOUtil.getStringFromClasspath(INITDATA_COMPANY_CODE_XML)));
        userServices.createTeamData(IOUtil.getStringFromClasspath(INITDATA_TEAMS_XML));
    }

    @Test
    public void testFirstMultipleRequestAccepted() throws GloriaApplicationException, IOException {
        // Arrange
        ChangeId changeId = procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(TEST1_NEW_REQUEST)));

        // Act

        // Assert
        Assert.assertEquals(ChangeIdStatus.ACCEPTED, changeId.getStatus());
        Assert.assertEquals("M1V1", changeId.getMtrlRequestVersion());

        List<MaterialHeader> materialHeaders = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId("MULTIPLE", null, null, null,
                                                                                                                                  null);
        Assert.assertNotNull(materialHeaders);
        Assert.assertEquals(2, materialHeaders.size());

        MaterialHeader theMaterialHeader = materialHeaders.get(0);
        Assert.assertNotNull(theMaterialHeader);
        Assert.assertEquals("M1", theMaterialHeader.getMtrlRequestId());
        Assert.assertNotNull(theMaterialHeader.getAccepted());

        materialHeaders = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId("MULTIPLE", "TO1", null, null, null);
        Assert.assertNotNull(materialHeaders);
        Assert.assertEquals(1, materialHeaders.size());

        materialHeaders = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId("MULTIPLE", "TO2", null, null, null);
        Assert.assertNotNull(materialHeaders);
        Assert.assertEquals(1, materialHeaders.size());
    }

    @Test
    public void testSecondMultipleleRequestWaitConfirm() throws GloriaApplicationException, IOException {
        // Arrange
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(TEST1_NEW_REQUEST)));

        // Act
        List<MaterialHeader> materialHeadersTO1 = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId("MULTIPLE", "TO1", null,
                                                                                                                                     null, null);
        List<MaterialHeaderDTO> materialHeaderTO1DTOs = transformToDTO(materialHeadersTO1);

        procurementServices.assignOrUnassignMateriaHeaders(materialHeaderTO1DTOs, "assign", "V075345", "GOT_PE", TeamType.MATERIAL_CONTROL.name(),"V075345");

        List<MaterialHeader> materialHeadersTO2 = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId("MULTIPLE", "TO2", null,
                                                                                                                                     null, null);
        List<MaterialHeaderDTO> materialHeaderTO2DTOs = transformToDTO(materialHeadersTO2);

        procurementServices.assignOrUnassignMateriaHeaders(materialHeaderTO2DTOs, "assign", "V075345", "GOT_PE", TeamType.MATERIAL_CONTROL.name(), "V075345");

        ChangeId changeId = procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(TEST2_UPDATE_REQUEST)));

        // Assert
        Assert.assertEquals(ChangeIdStatus.WAIT_CONFIRM, changeId.getStatus());
        Assert.assertEquals("M1V2", changeId.getMtrlRequestVersion());

        materialHeadersTO1 = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId("MULTIPLE", "TO1", null, null, null);
        Assert.assertEquals(1, materialHeadersTO1.size());
        ChangeId acceptedChangeId = materialHeadersTO1.get(0).getAccepted().getChangeId();
        Assert.assertEquals(ChangeIdStatus.ACCEPTED, acceptedChangeId.getStatus());
        Assert.assertEquals("M1V1", acceptedChangeId.getMtrlRequestVersion());

    }

    @Test
    @Ignore
    public void testSecondMultipleleRequestAcceptWaitConfirm() throws GloriaApplicationException, IOException {
        // Arrange
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(TEST1_NEW_REQUEST)));

        List<MaterialHeader> materialHeadersTO1 = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId("MULTIPLE", "TO1", null,
                                                                                                                                     null, null);
        List<MaterialHeaderDTO> materialHeaderTO1DTOs = transformToDTO(materialHeadersTO1);

        procurementServices.assignOrUnassignMateriaHeaders(materialHeaderTO1DTOs, "assign", "V075345", "GOT_PE", TeamType.MATERIAL_CONTROL.name(), "V075345");

        List<MaterialHeader> materialHeadersTO2 = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId("MULTIPLE", "TO2", null,
                                                                                                                                     null, null);
        List<MaterialHeaderDTO> materialHeaderTO2DTOs = transformToDTO(materialHeadersTO2);

        procurementServices.assignOrUnassignMateriaHeaders(materialHeaderTO2DTOs, "assign", "V075345", "GOT_PE", TeamType.MATERIAL_CONTROL.name(), "V075345");

        ChangeId changeId = procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(TEST2_UPDATE_REQUEST)));

        // Act
        procurementServices.acceptOrRejectChangeId("accept", changeId.getChangeIdOid(), "V075345", new ArrayList<MaterialDTO>());

        // Assert
        Assert.assertEquals(ChangeIdStatus.ACCEPTED, changeId.getStatus());
        Assert.assertEquals("M1V2", changeId.getMtrlRequestVersion());

        materialHeadersTO1 = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId("MULTIPLE", "TO1", null, null, null);
        Assert.assertEquals(1, materialHeadersTO1.size());
        ChangeId acceptedChangeId = materialHeadersTO1.get(0).getAccepted().getChangeId();
        Assert.assertEquals(ChangeIdStatus.ACCEPTED, acceptedChangeId.getStatus());
        Assert.assertEquals("M1V2", acceptedChangeId.getMtrlRequestVersion());
    }

    @Test
    public void testThirdMultipleleRequestCancelWait() throws GloriaApplicationException, IOException {
        // Arrange
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(TEST1_NEW_REQUEST)));

        List<MaterialHeader> materialHeadersTO1 = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId("MULTIPLE", "TO1", null,
                                                                                                                                     null, null);
        List<MaterialHeaderDTO> materialHeaderTO1DTOs = transformToDTO(materialHeadersTO1);

        procurementServices.assignOrUnassignMateriaHeaders(materialHeaderTO1DTOs, "assign", "V075345", "GOT_PE", TeamType.MATERIAL_CONTROL.name(), "V075345");

        List<MaterialHeader> materialHeadersTO2 = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId("MULTIPLE", "TO2", null,
                                                                                                                                     null, null);
        List<MaterialHeaderDTO> materialHeaderTO2DTOs = transformToDTO(materialHeadersTO2);

        procurementServices.assignOrUnassignMateriaHeaders(materialHeaderTO2DTOs, "assign", "V075345", "GOT_PE", TeamType.MATERIAL_CONTROL.name(), "V075345");

        ChangeId changeId = procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(TEST2_UPDATE_REQUEST)));

        procurementServices.acceptOrRejectChangeId("accept", changeId.getChangeIdOid(), "V075345", new ArrayList<MaterialDTO>());

        //Act
        em.clear();
        ChangeId cancelChangeId = procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(TEST3_CANCEL_REQUEST)));

        // Assert
        Assert.assertEquals(ChangeIdStatus.CANCEL_WAIT, cancelChangeId.getStatus());
        Assert.assertEquals("M1V3", cancelChangeId.getMtrlRequestVersion());
    }
    @Test
    @Ignore
    public void testThirdMultipleleRequestAcceptCancelWait() throws GloriaApplicationException, IOException {
        // Arrange
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(TEST1_NEW_REQUEST)));

        List<MaterialHeader> materialHeadersTO1 = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId("MULTIPLE", "TO1", null,
                                                                                                                                     null, null);
        List<MaterialHeaderDTO> materialHeaderTO1DTOs = transformToDTO(materialHeadersTO1);

        procurementServices.assignOrUnassignMateriaHeaders(materialHeaderTO1DTOs, "assign", "V075345", "GOT_PE", TeamType.MATERIAL_CONTROL.name(), "V075345");

        List<MaterialHeader> materialHeadersTO2 = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId("MULTIPLE", "TO2", null,
                                                                                                                                     null, null);
        List<MaterialHeaderDTO> materialHeaderTO2DTOs = transformToDTO(materialHeadersTO2);

        procurementServices.assignOrUnassignMateriaHeaders(materialHeaderTO2DTOs, "assign", "V075345", "GOT_PE", TeamType.MATERIAL_CONTROL.name(), "V075345");

        ChangeId changeId = procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(TEST2_UPDATE_REQUEST)));

        procurementServices.acceptOrRejectChangeId("accept", changeId.getChangeIdOid(), "V075345", new ArrayList<MaterialDTO>());

        ChangeId cancelChangeId = procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(TEST3_CANCEL_REQUEST)));

        //Act
        procurementServices.acceptOrRejectChangeId("accept", cancelChangeId.getChangeIdOid(), "V075345", new ArrayList<MaterialDTO>());

        // Assert
        Assert.assertEquals(ChangeIdStatus.CANCELLED, cancelChangeId.getStatus());
        Assert.assertEquals("M1V3", cancelChangeId.getMtrlRequestVersion());
    }

    private List<MaterialHeaderDTO> transformToDTO(List<MaterialHeader> materialHeadersTO) {
        List<MaterialHeaderDTO> materialHeaderTO1DTOs = new ArrayList<MaterialHeaderDTO>();
        MaterialHeaderDTO materialHeaderTO1DTO = new MaterialHeaderDTO();
        materialHeaderTO1DTO.setId(materialHeadersTO.get(0).getMaterialHeaderOid());
        materialHeaderTO1DTO.setVersion(materialHeadersTO.get(0).getVersion());
        materialHeaderTO1DTO.setCompanyCode(materialHeadersTO.get(0).getCompanyCode());
        materialHeaderTO1DTOs.add(materialHeaderTO1DTO);
        return materialHeaderTO1DTOs;
    }

}
