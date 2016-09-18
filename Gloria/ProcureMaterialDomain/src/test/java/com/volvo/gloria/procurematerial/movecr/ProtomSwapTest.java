package com.volvo.gloria.procurematerial.movecr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.authorization.c.TeamType;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.b.CompanyCodeTransformer;
import com.volvo.gloria.procurematerial.b.ProcurementDtoTransformer;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.b.RequestTransformer;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.status.changeid.ChangeIdStatus;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class ProtomSwapTest extends AbstractTransactionalTestCase {
    public ProtomSwapTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }

    @Inject
    private RequestTransformer requestTransformer;
    private static final String INITDATA_COMPANY_CODE_XML = "globaldataTest/CompanyCode.xml";
    private static final String INITDATA_TEAMS_XML = "globaldataTest/Team.xml";

    @Inject
    protected ProcurementServices procurementServices;

    @Inject
    protected ProcurementServices procurementServices2;

    @Inject
    private CommonServices commonServices;

    @Inject
    private UserServices userServices;

    @Inject
    private CompanyCodeTransformer companyCodeTransformer;

    @Inject
    ProcurementDtoTransformer procurementDtoTransformer;

    @Inject
    MaterialHeaderRepository requestHeaderRepository;

    private static final String FOLDER = "unplan_plan_swap/";
    private static final String XML_1_SWAP_SINGLE_MR = FOLDER + "1_swap_single_MR/1_MR17816.xml";
    private static final String XML_2_SWAP_SINGLE_MR = FOLDER + "1_swap_single_MR/2_MR17816_UDL151Planned.xml";
    private static final String XML_3_SWAP_SINGLE_MR = FOLDER + "1_swap_single_MR/3_MR17816_UDL152swapto149.xml";

    private static final String XML_2_swapmulti_MR1000 = FOLDER + "2_swap_multi_MR/1_protom_MR1000.xml";
    private static final String XML_2_swapmulti_MR1001 = FOLDER + "2_swap_multi_MR/2_protom_MR1001.xml";
    private static final String XML_2_swapmulti_SWAP_MR1000 = FOLDER + "2_swap_multi_MR/3a_protom_MR1000_swap_to_new.xml";
    private static final String XML_2_swapmulti_SWAP_MR1001 = FOLDER + "2_swap_multi_MR/3b_protom_MR1001_swap_to_new.xml";

    private static final String XML_3_SWAP_MULTI2_1 = FOLDER + "3_swap_multi2/MR1.xml";
    private static final String XML_3_SWAP_MULTI2_2 = FOLDER + "3_swap_multi2/MR2.xml";
    private static final String XML_3_SWAP_MULTI2_3 = FOLDER + "3_swap_multi2/SwapMR2.xml";

    private static final String XML_ADVANCED_1 = FOLDER + "4_protom_advanced/1_ComponentMR.xml";
    private static final String XML_ADVANCED_2 = FOLDER + "4_protom_advanced/2_ComponentMRAddPart.xml";
    private static final String XML_ADVANCED_3 = FOLDER + "4_protom_advanced/3_ComponentSWAP.xml";

    private static final String XML_UNPLAN_PLAN_LC_1 = FOLDER + "5_unplan_plan_lc/1_FA1V1.xml";
    private static final String XML_UNPLAN_PLAN_LC_2 = FOLDER + "5_unplan_plan_lc/2_LC.xml";
    private static final String XML_UNPLAN_PLAN_LC_3 = FOLDER + "5_unplan_plan_lc/3_planLC.xml";

    private static final String XML_CANCEL_AFTER_SWAP_1 = FOLDER + "6_cancel_after_swap_GLO_6589/1_FAunassigned.xml";
    private static final String XML_CANCEL_AFTER_SWAP_2 = FOLDER + "6_cancel_after_swap_GLO_6589/2_LCunassigned.xml";
    private static final String XML_CANCEL_AFTER_SWAP_3 = FOLDER + "6_cancel_after_swap_GLO_6589/3_PlanLCunassigned.xml";
    private static final String XML_CANCEL_AFTER_SWAP_4 = FOLDER + "6_cancel_after_swap_GLO_6589/4_CancelLCunassigned.xml";
    private static final String XML_CANCEL_AFTER_SWAP_5 = FOLDER + "6_cancel_after_swap_GLO_6589/5_CancelFA.xml";

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtomSwapTest.class);

    @Before
    public void init() throws IOException {
        commonServices.addSyncCompanyCode(companyCodeTransformer.transformCompanyCode(IOUtil.getStringFromClasspath(INITDATA_COMPANY_CODE_XML)));
        userServices.createTeamData(IOUtil.getStringFromClasspath(INITDATA_TEAMS_XML));

    }

    @Test
    public void testSwap_single_MR() throws GloriaApplicationException, IOException, InterruptedException {

        processNoAssign(XML_1_SWAP_SINGLE_MR);
        processNoAssign(XML_2_SWAP_SINGLE_MR);
        processSwap(XML_3_SWAP_SINGLE_MR);
        checkSourceMaterialHeader("UDL-152", "UDL-152_263_31968", "17816", "31968");
        checkTargetMaterialHeader("UDL-149", "UDL-149_263_31968", "17816", "31968");
    }

    @Test
    public void testSwap_multi_MR() throws GloriaApplicationException, IOException, InterruptedException {
        processNoAssign(XML_2_swapmulti_MR1000);
        processNoAssign(XML_2_swapmulti_MR1001);
        processSwap(XML_2_swapmulti_SWAP_MR1000);
        checkSourceMaterialHeader("TO1_SW", "TO1_SW_1_31967", "1000", "31967");

        // ChangeTechId = 100
        checkTargetMaterialHeader("TO10_SW", "TO10_SW_100_31967", "1000", "31967");

        processSwap(XML_2_swapmulti_SWAP_MR1001);

        // checkSourceMaterialHeader("TO1_SW", "TO1_SW_1_31967", "1001","31967");
        checkTargetMaterialHeader("TO11_SW", "TO11_SW_101_31967", "1001", "31967");
    }

    @Test
    public void testGLO_6546() throws GloriaApplicationException, IOException, InterruptedException {
        processNoAssign(XML_3_SWAP_MULTI2_1);
        processNoAssign(XML_3_SWAP_MULTI2_2);
        processSwap(XML_3_SWAP_MULTI2_3);

        checkSourceMaterialHeader("ND:CAB-430", "ND:CAB-430_284_91828", "17854", "91828");
        checkTargetMaterialHeader("ND:CAB-435", "ND:CAB-435_284_91828", "17854", "91828");

    }

    @Test
    public void testProtomAdvanced() throws GloriaApplicationException, IOException, InterruptedException {
        processNoAssign(XML_ADVANCED_1);
        processNoAssign(XML_ADVANCED_2);
        processSwap(XML_ADVANCED_3);
        displayAllMaterialHeaders();

        String sourceReferenceId = "ND:CAB-428";
        String sourceMtrRequestId = "ND:CAB-428_2625_91828";
        String sourceCrId = "17877";
        String buildLocation = "91828";
        checkSourceMaterialHeader(sourceReferenceId, sourceMtrRequestId, sourceCrId, buildLocation);

        String targetReferenceId = "ND:CAB-429";
        String targetMtrRequestId = "ND:CAB-429_303_9006";
        String targetCrId = "17877";
        checkTargetMaterialHeader(targetReferenceId, targetMtrRequestId, targetCrId, buildLocation);
    }

    @Test
    public void testUnplanplanLC() throws GloriaApplicationException, IOException, InterruptedException {
        processNoAssign(XML_UNPLAN_PLAN_LC_1);
        processNoAssign(XML_UNPLAN_PLAN_LC_2);
        processSwap(XML_UNPLAN_PLAN_LC_3);
        displayAllMaterialHeaders();

        String plannedReferenceId = "FH-1972";
        String plannedMtrRequestId = "FH-1972_15670_35884";
        String plannedSourceCrId = "17877";
        String plannedBuildLocation = "35884";
        checkPlannedMaterialHeader(plannedReferenceId, plannedMtrRequestId, plannedSourceCrId, plannedBuildLocation);
    }

    @Test
    public void testCancelAfterSwap() throws GloriaApplicationException, IOException, InterruptedException {
        processNoAssign(XML_CANCEL_AFTER_SWAP_1);
        processNoAssign(XML_CANCEL_AFTER_SWAP_2);
        processNoAssign(XML_CANCEL_AFTER_SWAP_3);
        // displayAllMaterialHeaders();
        String plannedReferenceId = "ND:CAB-427";
        String plannedMtrRequestId = "ND:CAB-427_2524_31968";
        String plannedSourceCrId = "17897";
        String plannedBuildLocation = "31968";
        checkPlannedMaterialHeader(plannedReferenceId, plannedMtrRequestId, plannedSourceCrId, plannedBuildLocation);

        // process Cancel
        processNoAssign(XML_CANCEL_AFTER_SWAP_4);
    }

    private void processNoAssign(String xml) throws GloriaApplicationException, IOException, InterruptedException {
        // ------------------------------------------------------------------------------------------------------------------------
        procurementServices.manageRequest(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(xml)));
        // ------------------------------------------------------------------------------------------------------------------------

    }

    private void processAndAssign(String xml, String changeTechId, String userId, String team) throws GloriaApplicationException, IOException,
            InterruptedException {
        // ------------------------------------------------------------------------------------------------------------------------
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(xml)));
        // ------------------------------------------------------------------------------------------------------------------------

        ChangeId changeId = procurementServices.findChangeIdByTechId(changeTechId);
        List<MaterialHeaderVersion> versions = changeId.getMaterialHeaderVersions();
        List<MaterialHeader> materialHeaders = new ArrayList();

        for (MaterialHeaderVersion materialHeaderVersion : versions) {
            MaterialHeader mh = materialHeaderVersion.getMaterialHeader();
            if (mh.getMaterialControllerUserId() == null) {
                materialHeaders.add(mh);
            }
        }
        // // Assign all headers
        if (materialHeaders.size() > 0) {
            List<MaterialHeader> mh = procurementServices.assignOrUnassignMateriaHeaders(procurementDtoTransformer.transformAsDTO(materialHeaders), "assign",
                                                                                         userId, team, TeamType.MATERIAL_CONTROL.name(), userId);
        }
        if (changeId.getStatus() == ChangeIdStatus.WAIT_CONFIRM) {
            // acceptRejectChange("accept", changeTechId, userId);
        }
    }

    private void processSwap(String xml) throws GloriaApplicationException, IOException, InterruptedException {
        procurementServices2.manageRequest(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(xml)));

    }

    private void checkSourceMaterialHeader(String referenceId, String mtrRequestId, String crId, String buildLocation) {
        logger.info("referenceId=" + referenceId + " mtrRequestId=" + mtrRequestId + " crId=" + crId + " buildLocation" + buildLocation);
        List<MaterialHeader> materialHeaders = procurementServices.findMaterialHeaderByMtrlRequestId(mtrRequestId, referenceId);
        for (MaterialHeader materialHeader : materialHeaders) {
            String mhBuildlocation = materialHeader.getAccepted().getOutboundLocationId();
            if (mhBuildlocation.equals(buildLocation)) {
                List<Material> materialList = getMaterial(materialHeader);
                // Material matching crId - should not exist
                boolean materialExist = false;
                for (Material material : materialList) {
                    displayMaterial(material);
                    ChangeId chgAdded = material.getAdd();

                    if (chgAdded != null && chgAdded.getCrId().equals(crId)) {
                        materialExist = true;
                    }
                    ChangeId chgRemoved = material.getRemove();
                    if (chgRemoved != null && chgRemoved.getCrId().equals(crId)) {
                        materialExist = true;
                    }

                }
                Assert.assertFalse(materialExist);
                break;
            }
        }

    }

    private void checkTargetMaterialHeader(String referenceId, String mtrRequestId, String crId, String buildLocation) {

        List<MaterialHeader> materialHeaders = procurementServices.findMaterialHeaderByMtrlRequestId(mtrRequestId, referenceId);
        Assert.assertTrue("size = " + materialHeaders.size(), materialHeaders.size() == 1);

        for (MaterialHeader materialHeader : materialHeaders) {
            String mhBuildlocation = materialHeader.getAccepted().getOutboundLocationId();
            logger.info("mh referenceId=" + materialHeader.getReferenceId() + " " + materialHeader.getMtrlRequestId() + " mhBuildlocation=" + mhBuildlocation);

            if (mhBuildlocation.equals(buildLocation)) {
                List<Material> materialList = materialHeader.getMaterials();
                // List<Material> materialList =
                // requestHeaderRepository.findAllMaterials(materialHeader.getMaterialHeaderOid());
                //
                Assert.assertTrue(materialList.size() > 0);
                // Check that some Material has moved to new MaterialHeader
                boolean materialHasMoved = false;
                for (Material material : materialList) {
                    ChangeId chgAdded = material.getAdd();
                    if (chgAdded != null && chgAdded.getCrId().equals(crId)) {
                        materialHasMoved = true;
                    }
                    ChangeId chgRemoved = material.getRemove();
                    if (chgRemoved != null && chgRemoved.getCrId().equals(crId)) {
                        materialHasMoved = true;
                    }
                }
                if (!materialHasMoved) {
                    System.out.println("sdddsd");
                }
                Assert.assertTrue(materialHasMoved);
            }

        }

    }

    private void checkPlannedMaterialHeader(String referenceId, String mtrRequestId, String crId, String buildLocation) {
        logger.info("referenceId=" + referenceId + " mtrRequestId=" + mtrRequestId + " crId=" + crId + " buildLocation" + buildLocation);
        List<MaterialHeader> materialHeaders = procurementServices.findMaterialHeaderByMtrlRequestId(mtrRequestId, referenceId);
        for (MaterialHeader materialHeader : materialHeaders) {
            String mhBuildlocation = materialHeader.getAccepted().getOutboundLocationId();
            if (mhBuildlocation.equals(buildLocation)) {
                List<Material> materialList = materialHeader.getMaterials();
                Assert.assertTrue(materialList.size() > 0);
                // Check that some Material has moved to new MaterialHeader
            }
        }

    }

    private void displayMaterial(Material material) {
        logger.info("material oid=" + material.getMaterialOID() + " PartNumber=" + material.getPartNumber() + " ProcureLinkId=" + material.getProcureLinkId()
                + " ChangeIdOid=" + material.getAdd().getChangeIdOid() + " CRid=" + material.getAdd().getCrId() + " mh oid="
                + material.getMaterialHeader().getMaterialHeaderOid() + " mh active=" + material.getMaterialHeader().isActive() + " referenceId="
                + material.getMaterialHeader().getReferenceId());
    }

    private List<Material> getMaterial(MaterialHeader materialHeader) {
        List<Material> materials = materialHeader.getMaterials();
        // List<Material> materials = procurementServices.findAllMaterials();
        logger.info("after number of materials=" + materials.size());
        HashMap materialMap = new HashMap();
        for (Material material : materials) {
            // displayMaterial(material);
            materialMap.put(material.getMaterialOID(), material);
            // MaterialHeader mh = material.getMaterialHeader();
            // logger.info("mh referenceId=" + mh.getReferenceId() + " material PartNumber=" + material.getPartNumber());
        }

        List<Material> newMaterials = new ArrayList();
        for (Iterator iterator = materialMap.values().iterator(); iterator.hasNext();) {
            Material material = (Material) iterator.next();
            newMaterials.add(material);
        }
        return newMaterials;
    }

    private void displayAllMaterialHeaders() {
        List<MaterialHeader> allMaterialHeaders = procurementServices.findAllMaterialHeaders();
        for (MaterialHeader mh : allMaterialHeaders) {
            logger.info("mh referenceId=" + mh.getReferenceId() + " " + mh.getMtrlRequestId());

        }
    }

}
