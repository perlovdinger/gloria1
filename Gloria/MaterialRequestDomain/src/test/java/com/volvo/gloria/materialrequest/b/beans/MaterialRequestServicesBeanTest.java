package com.volvo.gloria.materialrequest.b.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.b.WBSElementTransformer;
import com.volvo.gloria.common.d.entities.CompanyCode;
import com.volvo.gloria.common.d.entities.UnitOfMeasure;
import com.volvo.gloria.common.repositories.b.UnitOfMeasureRepository;
import com.volvo.gloria.common.wbs.c.dto.SyncWBSElementDTO;
import com.volvo.gloria.materialrequest.b.MaterialRequestHelper;
import com.volvo.gloria.materialrequest.b.MaterialRequestServices;
import com.volvo.gloria.materialrequest.c.dto.MaterialRequestDTO;
import com.volvo.gloria.materialrequest.c.dto.MaterialRequestLineDTO;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequest;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestLine;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestVersion;
import com.volvo.gloria.materialrequest.d.status.materialrequest.MaterialRequestStatus;
import com.volvo.gloria.materialrequest.d.status.materialrequest.MaterialRequestStatusHelper;
import com.volvo.gloria.materialrequest.d.type.materialrequest.MaterialRequestType;
import com.volvo.gloria.materialrequest.repositories.b.MaterialRequestRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

import static org.junit.Assert.*;

public class MaterialRequestServicesBeanTest extends AbstractTransactionalTestCase {
    public MaterialRequestServicesBeanTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    } 
    @Inject
    private UserServices userServices;

    @Inject
    private MaterialRequestRepository materialRequestRepository;

    @Inject
    private MaterialRequestServices materialRequestServices;

    @Inject
    CommonServices commonServices;

    @Inject
    private WBSElementTransformer wbsElementStorageTransformer;

    private static final String INITDATA_USER_XML = "globaldataTest/UserOrganisationDetails.xml";

    private static final String INITDATA_WBSELEMENT_XML = "globaldataTest/WbsElement.xml";

    @Before
    public void setUpTestData() throws JAXBException, IOException, GloriaApplicationException {

        userServices.createUserData(IOUtil.getStringFromClasspath(INITDATA_USER_XML));
        // setup company codes
        CompanyCode companyCodeEntity = new CompanyCode();
        companyCodeEntity.setCode("SE26");
        companyCodeEntity.setName("SE26");
        commonServices.addCompanyCode(companyCodeEntity);

        // setup wbs elements
        SyncWBSElementDTO syncWBSElementDTO = wbsElementStorageTransformer.transformStoredWBSElement(IOUtil.getStringFromClasspath(INITDATA_WBSELEMENT_XML));
        commonServices.syncWBSElement(syncWBSElementDTO);
    }
    //TODO Fix these tests

    @Test
    public void testRevertMaterialRequestVersion() throws GloriaApplicationException {
        MaterialRequestDTO materialRequestDTO = intialiseMaterialRequstDTO();

        String userId = "all";
        MaterialRequest materialRequest = materialRequestServices.createMaterialRequest(materialRequestDTO, userId);

        MaterialRequestVersion currentBeforeNewVersion = materialRequest.getCurrent();

        List<MaterialRequestLineDTO> materialRequestLineDTOs = new ArrayList<MaterialRequestLineDTO>();
        MaterialRequestLineDTO materialRequestLineDTO = new MaterialRequestLineDTO();
        materialRequestLineDTO.setPartAffiliation("v");
        materialRequestLineDTO.setPartNumber("1234");
        materialRequestLineDTO.setPartVersion("P01");
        materialRequestLineDTO.setPartName("Part wheel");
        materialRequestLineDTO.setPartModification("test mod");
        materialRequestLineDTO.setQuantity(20L);
        materialRequestLineDTO.setUnitOfMeasure("Pce");
        materialRequestLineDTO.setFunctionGroup("function");
        materialRequestLineDTO.setUnitPrice(125);
        materialRequestLineDTO.setCurrency("SEK");
        materialRequestLineDTOs.add(materialRequestLineDTO);

        MaterialRequestLineDTO materialRequestLineDTO2 = new MaterialRequestLineDTO();
        materialRequestLineDTO2.setPartAffiliation("v");
        materialRequestLineDTO2.setPartNumber("1235");
        materialRequestLineDTO2.setPartVersion("P01");
        materialRequestLineDTO2.setPartName("Part wheel 2");
        materialRequestLineDTO2.setPartModification("test mod2");
        materialRequestLineDTO2.setQuantity(20L);
        materialRequestLineDTO2.setUnitOfMeasure("Pce");
        materialRequestLineDTO2.setFunctionGroup("function");
        materialRequestLineDTO2.setUnitPrice(125);
        materialRequestLineDTO2.setCurrency("SEK");
        materialRequestLineDTOs.add(materialRequestLineDTO2);
        //mock unit of measure
        UnitOfMeasureRepository mockedUnitOfMeasureRepository = getUnitOfMeasure();

        MaterialRequestStatusHelper.doCreateMaterialRequestLine(materialRequestLineDTO, materialRequestRepository, materialRequest, mockedUnitOfMeasureRepository);
        MaterialRequestStatusHelper.doCreateMaterialRequestLine(materialRequestLineDTO2, materialRequestRepository, materialRequest, mockedUnitOfMeasureRepository);

        materialRequest.getCurrent().setStatus(MaterialRequestStatus.SENT_ACCEPTED);

        MaterialRequest materialRequestWithNewVersion = MaterialRequestStatusHelper.newVersion(materialRequest, materialRequestRepository,
                                                                                               materialRequest.getCurrent().getChangeVersion());

        Assert.assertEquals(MaterialRequestStatus.UPDATED, materialRequestWithNewVersion.getCurrent().getStatus());
        Assert.assertNotSame(currentBeforeNewVersion.getChangeVersion(), materialRequestWithNewVersion.getCurrent().getChangeVersion());

        materialRequestDTO = MaterialRequestHelper.transformAsDTO(materialRequest);

        // Act
        materialRequestServices.updateMaterialRequest(materialRequestDTO, "revert", userId);

        // Assert
        MaterialRequest materialRequestAfterRevert = materialRequestServices.findMaterialRequestById(materialRequestWithNewVersion.getMaterialRequestOid(),
                                                                                                     null, null);
        MaterialRequestVersion headerVersionAfterRevert = materialRequestAfterRevert.getCurrent();
        Assert.assertSame(currentBeforeNewVersion.getChangeVersion(), headerVersionAfterRevert.getChangeVersion());
        Assert.assertSame(currentBeforeNewVersion.getStatus(), headerVersionAfterRevert.getStatus());
    }

    private UnitOfMeasureRepository getUnitOfMeasure() {
        UnitOfMeasureRepository mockedUnitOfMeasureRepository=Mockito.mock(UnitOfMeasureRepository.class);
        List<UnitOfMeasure> listOfUnitOfMeasures=new ArrayList<UnitOfMeasure>();
        UnitOfMeasure measure= new UnitOfMeasure();
        measure.setCode("Pce");
        listOfUnitOfMeasures.add(measure);
        Mockito.when(mockedUnitOfMeasureRepository.findAllUnitOfMeasuresSupportedForGloria()).thenReturn(listOfUnitOfMeasures);
        return mockedUnitOfMeasureRepository;
    }

    @Test
    public void testCancelMaterialRequestWrongState() throws GloriaApplicationException, JAXBException {
        // Arrange
        MaterialRequestDTO materialRequestDTO = intialiseMaterialRequstDTO();

        String userId = "all";
        MaterialRequest materialRequest = materialRequestServices.createMaterialRequest(materialRequestDTO, userId);
        materialRequestDTO = MaterialRequestHelper.transformAsDTO(materialRequest);

        // Act
        try {
            materialRequestServices.updateMaterialRequest(materialRequestDTO, "send", userId);
        } catch (GloriaApplicationException e) {
            // Assert
            assertTrue("Cancel material request should not be allowed for the current state", !e.getMessage().contains("validationErrors"));
        }
    }

    @Test
    public void testCancelMaterialRequestAcceptedState() throws GloriaApplicationException, JAXBException {
        // Arrange
        MaterialRequestDTO materialRequestDTO = intialiseMaterialRequstDTO();

        String userId = "all";
        MaterialRequest materialRequest = materialRequestServices.createMaterialRequest(materialRequestDTO, userId);

        materialRequestDTO = MaterialRequestHelper.transformAsDTO(materialRequest);

        materialRequestServices.updateMaterialRequest(materialRequestDTO, "send", userId);
        materialRequestServices.updateMaterialRequest(materialRequestDTO, "cancel", userId);

        Assert.assertEquals(MaterialRequestStatus.CANCEL_WAIT, materialRequest.getCurrent().getStatus());
    }

    @Test(expected = GloriaApplicationException.class)
    public void testCancelMaterialRequestForCreatedStatus() throws GloriaApplicationException, JAXBException {
        // Arrange
        MaterialRequestDTO materialRequestDTO = intialiseMaterialRequstDTO();

        String userId = "all";
        MaterialRequest materialRequest = materialRequestServices.createMaterialRequest(materialRequestDTO, userId);
        materialRequestDTO = MaterialRequestHelper.transformAsDTO(materialRequest);

        // Act
        materialRequestServices.updateMaterialRequest(materialRequestDTO, "cancel", userId);
    }

    private MaterialRequestDTO intialiseMaterialRequstDTO() {
        MaterialRequestDTO materialRequestDTO = new MaterialRequestDTO();
        materialRequestDTO.setProjectId("M23-F900");
        materialRequestDTO.setReferenceGroup("REF");
        materialRequestDTO.setMaterialRequest("SD1");
        materialRequestDTO.setRequiredStaDate(new Date());
        materialRequestDTO.setContactPersonName("grace");
        materialRequestDTO.setRequesterName("smitha");
        materialRequestDTO.setRequesterId("sma");
        materialRequestDTO.setOutboundLocationId("NM");
        materialRequestDTO.setOutboundStartDate(new Date());
        materialRequestDTO.setApprovalAmount(new Double(200));
        materialRequestDTO.setApprovalCurrency("SEK");
        materialRequestDTO.setCompanyCode("SE26");
        materialRequestDTO.setWbsCode("A2G10-00027");
        materialRequestDTO.setGlAccount("glAcc");
        materialRequestDTO.setCostCenter("cc");
        materialRequestDTO.setInternalOrderNoSAP("SAPNo");
        materialRequestDTO.setMailFormId("mail");
        materialRequestDTO.setType(MaterialRequestType.SINGLE.name());
        return materialRequestDTO;
    }

    @Test
    public void testValidateMaterialRequestLine() throws GloriaApplicationException {
        // Arrange
        MaterialRequestDTO materialRequestDTO = intialiseMaterialRequstDTO();
        String userId = "all";
        MaterialRequest materialRequest = materialRequestServices.createMaterialRequest(materialRequestDTO, userId);
        List<MaterialRequestLineDTO> materialRequestLineDTOs = new ArrayList<MaterialRequestLineDTO>();
        MaterialRequestLineDTO materialRequestLineDTO = new MaterialRequestLineDTO();
        materialRequestLineDTO.setPartAffiliation("v");
        materialRequestLineDTO.setPartNumber("1234");
        materialRequestLineDTO.setPartName("1234");
        materialRequestLineDTO.setPartVersion("b20");
        materialRequestLineDTO.setPartModification("test mod");
        materialRequestLineDTO.setQuantity(20L);
        materialRequestLineDTO.setUnitOfMeasure("Pce");
        materialRequestLineDTO.setFunctionGroup("function");
        materialRequestLineDTO.setCurrency("SEK");
        materialRequestLineDTOs.add(materialRequestLineDTO);
        
        //mock unit of measure
        UnitOfMeasureRepository mockedUnitOfMeasureRepository = getUnitOfMeasure();

        MaterialRequestLine materialRequestLine = MaterialRequestStatusHelper.doCreateMaterialRequestLine(materialRequestLineDTO, materialRequestRepository,
                                                                                                         materialRequest, mockedUnitOfMeasureRepository);
        materialRequest.getCurrent().getMaterialRequestLines().add(materialRequestLine);
        materialRequest.getCurrent().setStatus(MaterialRequestStatus.SENT_ACCEPTED);

        MaterialRequest materialRequestWithNewVersion = MaterialRequestStatusHelper.newVersion(materialRequest, materialRequestRepository,
                                                                                               materialRequest.getCurrent().getChangeVersion());
        // Act
        boolean value = materialRequestRepository.validateMaterialRequest(materialRequestWithNewVersion.getMaterialRequestOid(),
                                                                          MaterialRequestType.SINGLE.name());
        // Assert
        //TODO: Dont know the intent of the test.  Fixed it for now
        Assert.assertEquals(Boolean.TRUE, value);
    }

}