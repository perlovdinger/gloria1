package com.volvo.gloria.procuretestcases;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.b.CompanyCodeTransformer;
import com.volvo.gloria.procurematerial.b.ProcurementDtoTransformer;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.status.changeid.ChangeIdStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatus;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public abstract class ProcureBaseTest extends AbstractTransactionalTestCase {
    public ProcureBaseTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    private static final String INITDATA_COMPANY_CODE_XML = "globaldataTest/CompanyCode.xml";
    private static final String INITDATA_TEAMS_XML = "globaldataTest/Team.xml";

    @Inject
    protected ProcurementServices procurementServices;

    @Inject
    private CommonServices commonServices;
    
    @Inject
    private UserServices userServices;
    
    @Inject
    private CompanyCodeTransformer companyCodeTransformer;
    
    @Inject
    protected ProcureLineRepository procureLineRepository;
    
    @Inject
    protected OrderRepository orderRepository;
    
    @Inject
    protected MaterialHeaderRepository requestHeaderRepository;
    
    @Inject
    private ProcurementDtoTransformer procurementDtoTransformer;
    
    protected void load() throws IOException{
        commonServices.addSyncCompanyCode(companyCodeTransformer.transformCompanyCode(IOUtil.getStringFromClasspath(INITDATA_COMPANY_CODE_XML)));
        userServices.createTeamData(IOUtil.getStringFromClasspath(INITDATA_TEAMS_XML));
    }

    // Check that all status in MaterialList matches expectedStatus
    protected boolean containsAllMaterialStatus(List<Material> materialList, MaterialStatus[] expectedStatus) {
        List<MaterialStatus> statusList = Arrays.asList(expectedStatus);
        HashMap<MaterialStatus, MaterialStatus> map = new HashMap<MaterialStatus, MaterialStatus>();
        for (MaterialStatus materialStatus : statusList) {
            map.put(materialStatus, materialStatus);
            // Does all expectedStatus exist in MaterialList
            if (!containsMaterialStatus(materialList, materialStatus)) {
                Assert.fail("MaterialStatus not found in List " + materialStatus);
            }
        }
        // Is all MaterialList.status valid
        for (Material material : materialList) {
            if (!map.containsValue(material.getStatus())) {
                Assert.fail("MaterialStatus not expected " + material.getStatus());
            }
        }
        return true;
    }

    protected boolean containsMaterialStatus(List<Material> materialList, MaterialStatus materialStatus) {
        boolean containStatus = false;
        for (Material material : materialList) {
            if (material.getStatus().equals(materialStatus)) {
                containStatus = true;
            }
        }
        return containStatus;
    }

    protected void validate(MaterialHeader materialHeader, String referenceId, String buildId, String outboundlocationId, String mtrlRequestId,
            String mtrlRequestVersion) {
        Assert.assertNotNull(materialHeader);
        Assert.assertEquals(referenceId, materialHeader.getReferenceId());
        Assert.assertEquals(buildId, materialHeader.getBuildId());
        Assert.assertEquals(outboundlocationId, materialHeader.getAccepted().getOutboundLocationId());
        Assert.assertEquals(mtrlRequestId, materialHeader.getMtrlRequestId());
//        Assert.assertEquals(mtrlRequestVersion, materialHeader.getAccepted().getChangeId().getMtrlRequestVersion());
    }

    protected void validate(MaterialHeader materialHeader, String referenceId, String buildId, String outboundlocationId, String mtrlRequestId) {
        Assert.assertNotNull(materialHeader);
        Assert.assertEquals(referenceId, materialHeader.getReferenceId());
        Assert.assertEquals(buildId, materialHeader.getBuildId());
        Assert.assertEquals(outboundlocationId, materialHeader.getAccepted().getOutboundLocationId());
        Assert.assertEquals(mtrlRequestId, materialHeader.getMtrlRequestId());
    }

    protected void validate(List<Material> materialList, MaterialStatus[] expectedStatus) {
        Assert.assertNotNull(materialList);
        Assert.assertTrue(containsAllMaterialStatus(materialList, expectedStatus));
    }

    protected void validate(ChangeId changeId, ChangeIdStatus expectedStatus) {
        Assert.assertEquals(expectedStatus, changeId.getStatus());

    }

    protected void validate(ChangeId changeId, ChangeIdStatus expectedStatus, String mtrlRequestVersion) {
        Assert.assertEquals(expectedStatus, changeId.getStatus());
//        Assert.assertEquals(mtrlRequestVersion, changeId.getMtrlRequestVersion());
    }

   
    protected void acceptRejectChange(String acceptReject, String changeTechId, String userId) throws GloriaApplicationException, IOException {
        List<Material> materialList = new ArrayList<Material>();
        ChangeId changeId = procurementServices.findChangeIdByTechId(changeTechId);
        materialList.addAll(changeId.getAddMaterials());
        materialList.addAll(changeId.getRemoveMaterials());
        for (Material material : materialList) {
            material.setChangeAction(GloriaParams.ADDITIONAL);
        }
        procurementServices.acceptOrRejectChangeId(acceptReject, changeId.getChangeIdOid(), userId, procurementDtoTransformer.transformAsMaterialDTOs(materialList));
        //changeId = procurementServices.findChangeIdByTechId(changeTechId);
        if (acceptReject.equals("accept")) {
            validate(changeId, ChangeIdStatus.ACCEPTED);
        } else if (acceptReject.equals("reject")) {
            validate(changeId, ChangeIdStatus.REJECTED);
        }
    }
    
    protected void acceptRejectCancellation(String acceptReject, String changeTechId, String userId) throws GloriaApplicationException, IOException {
        List<Material> materialList = new ArrayList<Material>();
        ChangeId changeId = procurementServices.findChangeIdByTechId(changeTechId);
        materialList.addAll(changeId.getAddMaterials());
        procurementServices.acceptOrRejectChangeId(acceptReject, changeId.getChangeIdOid(), userId, procurementDtoTransformer.transformAsMaterialDTOs(materialList));
        changeId = procurementServices.findChangeIdByTechId(changeTechId);
        if (acceptReject.equals("accept")) {
            validate(changeId, ChangeIdStatus.CANCELLED);
        } else if (acceptReject.equals("reject")) {
            validate(changeId, ChangeIdStatus.CANCEL_REJECTED);
        }
    }

    protected void validate(String mtrlRequestId, String referenceId, String buildId, String outboundlocationId, String mtrlRequestVersion,
            MaterialStatus[] expectedStatus) {
        List<MaterialHeader> materialHeaders = procurementServices.findMaterialHeaderByMtrlRequestId(mtrlRequestId, referenceId);
        Assert.assertEquals(1, materialHeaders.size());
        MaterialHeader materialHeader = materialHeaders.get(0);
        validate(materialHeader, referenceId, buildId, outboundlocationId, mtrlRequestId, mtrlRequestVersion);
        List<Material> materialList = materialHeader.getMaterials();
        validate(materialList, expectedStatus);
    }
    protected List<Material> validateReturnList(String mtrlRequestId, String referenceId, String buildId, String outboundlocationId, String mtrlRequestVersion,
            MaterialStatus[] expectedStatus) {
        List<MaterialHeader> materialHeaders = procurementServices.findMaterialHeaderByMtrlRequestId(mtrlRequestId, referenceId);
        Assert.assertEquals(1, materialHeaders.size());
        MaterialHeader materialHeader = materialHeaders.get(0);
        validate(materialHeader, referenceId, buildId, outboundlocationId, mtrlRequestId, mtrlRequestVersion);
        List<Material> materialList = materialHeader.getMaterials();
        validate(materialList, expectedStatus);
        return materialList;
    }

    protected void validate(String mtrlRequestId, String referenceId, String buildId, String outboundlocationId, MaterialStatus[] expectedStatus) {
        List<MaterialHeader> materialHeaders = procurementServices.findMaterialHeaderByMtrlRequestId(mtrlRequestId, referenceId);
        Assert.assertEquals(1, materialHeaders.size());
        MaterialHeader materialHeader = materialHeaders.get(0);
        validate(materialHeader, referenceId, buildId, outboundlocationId, mtrlRequestId);
        List<Material> materialList = materialHeader.getMaterials();
        validate(materialList, expectedStatus);
    }
    protected List<Material> validateReturnList(String mtrlRequestId, String referenceId, String buildId, String outboundlocationId, MaterialStatus[] expectedStatus) {
        List<MaterialHeader> materialHeaders = procurementServices.findMaterialHeaderByMtrlRequestId(mtrlRequestId, referenceId);
        Assert.assertEquals(1, materialHeaders.size());
        MaterialHeader materialHeader = materialHeaders.get(0);
        validate(materialHeader, referenceId, buildId, outboundlocationId, mtrlRequestId);
        List<Material> materialList = materialHeader.getMaterials();
        validate(materialList, expectedStatus);
        return materialList;
    }
}
