package com.volvo.gloria.procuretestcases;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.volvo.gloria.authorization.c.TeamType;
import com.volvo.gloria.procurematerial.b.ProcurementDtoTransformer;
import com.volvo.gloria.procurematerial.b.RequestTransformer;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.status.changeid.ChangeIdStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatus;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;

public class ProtomFALCTest extends ProcureBaseTest {
    
    @Inject
    private RequestTransformer requestTransformer;
    
    @Inject
    ProcurementDtoTransformer procurementDtoTransformer;

    private static final String XML_1 = "procureTestCases/Protom_FALC/baseflow/1_fa1_adds.xml";
    private static final String XML_2 = "procureTestCases/Protom_FALC/baseflow/2_fa1_adds_remove.xml";
    private static final String XML_3 = "procureTestCases/Protom_FALC/baseflow/3_auto_buildSerie.xml";
    private static final String XML_4 = "procureTestCases/Protom_FALC/baseflow/4_fa2_add_remove.xml";
    private static final String XML_5 = "procureTestCases/Protom_FALC/baseflow/5_lc_add_remove.xml";
    
    
    private static final String XML_6 = "procureTestCases/Protom_FALC_Cancel/FA1_pr1.xml";
    private static final String XML_7 = "procureTestCases/Protom_FALC_Cancel/FA2_pr1.xml";
    private static final String XML_8 = "procureTestCases/Protom_FALC_Cancel/FA5_pr1_Cancel.xml";
    private static final String XML_9 = "procureTestCases/Protom_FALC_Cancel/FA3_pr2.xml";
    private static final String XML_10 = "procureTestCases/Protom_FALC_Cancel/FA4_pr2.xml";

    private static final String XML_11 = "procureTestCases/Protom_FA_Sequence/FA1V1_pr_outboundlocation_1001.xml";
    private static final String XML_12 = "procureTestCases/Protom_FA_Sequence/FA1V2_pr_outboundlocation_1001_91828.xml";
    private static final String XML_13 = "procureTestCases/Protom_FA_Sequence/FA1V3_cancel_outboundlocation_1001_91828.xml";
    private static final String XML_14 = "procureTestCases/Protom_FA_Sequence/FA1V2_pr_outboundlocation_91828.xml";
    
    private static final String XML_15 = "procureTestCases/Protom_SWAP/1_protom_mr_swap_1.xml";
    private static final String XML_16 = "procureTestCases/Protom_SWAP/1_protom_mr_swap_2.xml";
    
    private static final String XML_17 = "procureTestCases/Protom_ADDITIONAL/1_protom_additional_usage_ex1.xml";
    private static final String XML_18 = "procureTestCases/Protom_ADDITIONAL/2_protom_additional_usage_auto_accept_change_ex2.xml";
    
    EntityManager em = null;

    @PersistenceContext(unitName = "ProcureMaterialDomainPU")
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    
    @Before
    public void init() throws IOException {
        load();
    }
    
    @Test
    public void testFirstAssemblyScenarios() throws GloriaApplicationException, IOException, InterruptedException {
      
       
        process_1_FA1_adds();
        process_2_FA1_adds_remove();
        process_3_FA_auto_buildSerie();
        //process_4_FA2_adds_remove();
//        process_5_lc_adds_remove();    
    }

    // Auto accept - FA1V1
    private void process_1_FA1_adds() throws GloriaApplicationException, IOException {
        // ------------------------------------------------------------------------------------------------------------------------
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_1)));
        // ------------------------------------------------------------------------------------------------------------------------
        String changeTechId = "200";
        ChangeId changeId = procurementServices.findChangeIdByTechId(changeTechId);
        validate(changeId, ChangeIdStatus.ACCEPTED, "FA1V1");

        MaterialStatus[] expectedStatus = { MaterialStatus.ADDED };
        validate("FH8732_3333_8410", "FH8732", "3333", "8410", "FA1V1", expectedStatus);
        validate("FH8732_3333_8411", "FH8732", "3333", "8411", "FA1V1", expectedStatus);
        // ------------------------------------------------------------------------------------------------------------------------
        // assign MaterialHeaders
        List<MaterialHeader> materialHeaders1 = procurementServices.findMaterialHeaderByMtrlRequestId("FH8732_3333_8410", "FH8732");
        procurementServices.assignOrUnassignMateriaHeaders(procurementDtoTransformer.transformAsDTO(materialHeaders1), "assign", "V016387", "GOT_PE", TeamType.MATERIAL_CONTROL.name(), "V016387");

        List<MaterialHeader> materialHeaders2 = procurementServices.findMaterialHeaderByMtrlRequestId("FH8732_3333_8411", "FH8732");
        procurementServices.assignOrUnassignMateriaHeaders(procurementDtoTransformer.transformAsDTO(materialHeaders2), "assign", "V016387", "GOT_PE", TeamType.MATERIAL_CONTROL.name(), "V016387");

        changeId = procurementServices.findChangeIdByTechId(changeTechId);
        validate(changeId, ChangeIdStatus.ACCEPTED, "FA1V1");
        Assert.assertEquals(2, changeId.getAddMaterials().size());

    }

    // updated FA add/remove Material
    // Will trigger Change to accept - FA2000V2
    // Accept
    private void process_2_FA1_adds_remove() throws GloriaApplicationException, IOException, InterruptedException {
        String changeTechId = "201";
        // ------------------------------------------------------------------------------------------------------------------------
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_2)));
        ChangeId changeId = procurementServices.findChangeIdByTechId(changeTechId);
        validate(changeId, ChangeIdStatus.WAIT_CONFIRM);
        MaterialStatus[] expectedStatus = { MaterialStatus.ADD_NOT_ACCEPTED, MaterialStatus.REMOVE_MARKED };
        List<Material> materialList1 = validateReturnList("FH8732_3333_8410", "FH8732", "3333", "8410", "FA1V1", expectedStatus);
        List<Material> materialList2 = validateReturnList("FH8732_3333_8411", "FH8732", "3333", "8411", "FA1V1", expectedStatus);
        
        // ------------------------------------------------------------------------------------------------------------------------
        // Accept change
        acceptRejectChange("accept", changeTechId, "V016387");

//
        MaterialStatus[] expectedStatus2 = { MaterialStatus.ADDED, MaterialStatus.REMOVED };
         validate(materialList1,expectedStatus2);
         validate(materialList2,expectedStatus2);
         
        
    }

    private void process_3_FA_auto_buildSerie() throws GloriaApplicationException, IOException, InterruptedException {
        // ------------------------------------------------------------------------------------------------------------------------
       
        //procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_3)));

        //MaterialStatus[] expectedStatus = { MaterialStatus.ADDED, MaterialStatus.REMOVED };
        //List<Material> materialList1 = validateReturnList("FH8732_3333_8410", "FH8732", "3333", "8410", expectedStatus);
        //List<Material> materialList2 = validateReturnList("FH8732_3333_8411", "FH8732", "3333", "8411", expectedStatus);

    }

    private void process_4_FA2_adds_remove() throws GloriaApplicationException, IOException, InterruptedException {
        String changeTechId = "203";
        // ------------------------------------------------------------------------------------------------------------------------
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_4)));
        ChangeId changeId = procurementServices.findChangeIdByTechId(changeTechId);
        validate(changeId, ChangeIdStatus.WAIT_CONFIRM);

        MaterialStatus[] expectedStatus = { MaterialStatus.ADD_NOT_ACCEPTED, MaterialStatus.REMOVE_MARKED, MaterialStatus.REMOVED };
        validate("FH8732_3333_8410", "FH8732", "3333", "8410", "FA1V2", expectedStatus);
        validate("FH8732_3333_8411", "FH8732", "3333", "8411", "FA1V2", expectedStatus);
        // ------------------------------------------------------------------------------------------------------------------------
        // Accept change
        acceptRejectChange("accept", changeTechId, "V016387");
        ChangeId changeId2 = procurementServices.findChangeIdByTechId(changeTechId);
        validate(changeId2, ChangeIdStatus.ACCEPTED);

        MaterialStatus[] expectedStatus2 = { MaterialStatus.ADDED, MaterialStatus.REMOVED };
        validate("FH8732_3333_8410", "FH8732", "3333", "8410", expectedStatus2);
        validate("FH8732_3333_8411", "FH8732", "3333", "8411", expectedStatus2);

    }

    private void process_5_lc_adds_remove() throws GloriaApplicationException, IOException, InterruptedException {
        String changeTechId = "204";
        // ------------------------------------------------------------------------------------------------------------------------
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_5)));

        ChangeId changeId = procurementServices.findChangeIdByTechId(changeTechId);
        validate(changeId, ChangeIdStatus.WAIT_CONFIRM, "LC133");

        MaterialStatus[] expectedStatus = { MaterialStatus.ADD_NOT_ACCEPTED, MaterialStatus.REMOVE_MARKED, MaterialStatus.REMOVED };
        validate("FH8732_3333_8410", "FH8732", "3333", "8410", expectedStatus);
        validate("FH8732_3333_8411", "FH8732", "3333", "8411", expectedStatus);

        // ------------------------------------------------------------------------------------------------------------------------
        // Accept change
        acceptRejectChange("accept", changeTechId, "V016387");
        ChangeId changeId2 = procurementServices.findChangeIdByTechId(changeTechId);
        validate(changeId2, ChangeIdStatus.ACCEPTED, "LC133");
        MaterialStatus[] expectedStatus2 = { MaterialStatus.ADDED, MaterialStatus.REMOVED };
        validate("FH8732_3333_8410", "FH8732", "3333", "8410", "LC133", expectedStatus2);
        validate("FH8732_3333_8411", "FH8732", "3333", "8411", "LC133", expectedStatus2);

    }
    
    
    @Test
    //GLO-4964
    public void testCancelFA_NOASSIGN_SameChangeTechId() throws GloriaApplicationException, IOException, InterruptedException {
        // ######### arrange
        // -------------------------------------------------- FA1_Pr1 - changeTechId : 1049
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_6)));
        // --------------------------------------------------- FA2_Pr2 - changeTechId : 1049
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_7)));
       
        // ######### act
        // --------------------------------------------------- cancel - changeTechId : 1049
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_8)));

        // ######### assert
        ChangeId cancelChange = procurementServices.findChangeIdByTechId("1049");
        validate(cancelChange, ChangeIdStatus.CANCELLED);
        MaterialStatus[] expectedStatus2 = { MaterialStatus.REMOVED };
        // -------------------------------------------------- materials removed from FA1_Pr1,FA2_Pr1
        validate("FH-5158_4089_1001", "FH-5158", "4089", "1001", expectedStatus2);
    }
    
    @Test
    //GLO-4964
    @Ignore
    public void testCancelFA_ASSIGN_SameChangeTechId() throws GloriaApplicationException, IOException, InterruptedException {
        // ######### arrange
        // -------------------------------------------------- FA1_Pr1 - changeTechId : 1049
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_6)));

        // -------------------------------------------------- assign FA2_Pr1
        List<MaterialHeader> materialHeaders1 = procurementServices.findMaterialHeaderByMtrlRequestId("FH-5158_4089_1001", "FH-5158");
        procurementServices.assignOrUnassignMateriaHeaders(procurementDtoTransformer.transformAsDTO(materialHeaders1), "assign", "V016387", "GOT_PE",
                                                           TeamType.MATERIAL_CONTROL.name(), "V016387");

        // --------------------------------------------------- FA2_Pr1 - changeTechId : 1049
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_7)));

        // --------------------------------------------------- accept change from FA2_Pr1
        acceptRejectChange("accept", "52897", "V016387");

        // ######### act
        // --------------------------------------------------- cancel - changeTechId : 1049
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_8)));

        // ######### assert
        ChangeId cancelChange = procurementServices.findChangeIdByTechId("1049");
        validate(cancelChange, ChangeIdStatus.CANCEL_WAIT);
        MaterialStatus[] expectedStatus = { MaterialStatus.REMOVE_MARKED };
        validate("FH-5158_4089_1001", "FH-5158", "4089", "1001", expectedStatus);

        // -------------------------------------------------- accept cancel
        acceptRejectCancellation("accept", "1049", "V016387");

        validate(cancelChange, ChangeIdStatus.CANCELLED);
        MaterialStatus[] expectedStatus2 = { MaterialStatus.REMOVED };
        // -------------------------------------------------- materials removed from FA1_Pr1,FA2_Pr1
        validate("FH-5158_4089_1001", "FH-5158", "4089", "1001", expectedStatus2);
    }
    
    @Test
    //GLO-4964
    public void testCancelFA_OneOfChangeTechIds() throws GloriaApplicationException, IOException, InterruptedException {
        // ######### arrange
        // -------------------------------------------------- FA1_Pr1 - changeTechId : 1049
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_6)));       
        // --------------------------------------------------- FA2_Pr1 - changeTechId : 1049
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_7)));
        // --------------------------------------------------- FA3_Pr2 - changeTechId : 1056
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_9)));
        // --------------------------------------------------- FA4_Pr2 - changeTechId : 1056
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_10)));

        // ######### act
        // --------------------------------------------------- cancel - changeTechId : 1049
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_8)));

        // ######### assert
        ChangeId cancelChange = procurementServices.findChangeIdByTechId("1049");
  
        validate(cancelChange, ChangeIdStatus.CANCELLED);
        MaterialStatus[] expectedStatus2 = { MaterialStatus.REMOVED , MaterialStatus.ADDED };
        // -------------------------------------------------- materials removed from FA1_Pr1,FA2_Pr1 & materials added from FA3_Pr2,FA4Pr2
        validate("FH-5158_4089_1001", "FH-5158", "4089", "1001", expectedStatus2);
    }
    
    @Test
    //GLO-5009
    @Ignore
    public void testFA_Sequence_MultipleOutboundlocations_WithAChange() throws GloriaApplicationException, IOException, InterruptedException {
        // ######### arrange
        //-------------------------------------------------- FA1V1_Pr_1001 - changeTechId : 1116, Outboundlocation : 1001
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_11)));

        ChangeId changeF1V1 = procurementServices.findChangeIdByTechId("52953");
        validate(changeF1V1, ChangeIdStatus.ACCEPTED);
        
        // -------------------------------------------------- assign FA1V1_Pr_1001
        List<MaterialHeader> materialHeaders1 = procurementServices.findMaterialHeaderByMtrlRequestId("Comp-5008_5490_1001", "Comp-5008");
        procurementServices.assignOrUnassignMateriaHeaders(procurementDtoTransformer.transformAsDTO(materialHeaders1), "assign", "V016387", "GOT_PE",
                                                           TeamType.MATERIAL_CONTROL.name(), "V016387");

        //-------------------------------------------------- FA1V2_Pr_1001_91828 - changeTechId : 1131, Outboundlocation : 1001,91828
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_12)));
        
        ChangeId changeF1V2 = procurementServices.findChangeIdByTechId("52968");        
        validate(changeF1V2, ChangeIdStatus.WAIT_CONFIRM);
        
        // --------------------------------------------------- accept change from FA1V2_Pr_1001_91828
        acceptRejectChange("accept", "52968", "V016387");
        
        MaterialStatus[] expectedStatus = { MaterialStatus.ADDED };
        // -------------------------------------------------- materials Added FA1V2_Pr_1001, FA1V2_Pr_91828
        validate("Comp-5008_5490_1001", "Comp-5008", "5490", "1001", expectedStatus);
        validate("Comp-5008_5490_91828", "Comp-5008", "5490", "91828", expectedStatus);

        // --------------------------------------------------- cancel - changeTechId : 1131
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_13)));
        
        //
        ChangeId cancelChangeF1V2 = procurementServices.findChangeIdByTechId("1131");        
        validate(cancelChangeF1V2, ChangeIdStatus.CANCEL_WAIT);
        
        // -------------------------------------------------- accept cancel
        acceptRejectCancellation("accept", "1131", "V016387");
        
        validate(cancelChangeF1V2, ChangeIdStatus.CANCELLED);
        MaterialStatus[] expectedStatus2 = { MaterialStatus.REMOVED , MaterialStatus.ADDED};
        // -------------------------------------------------- materials removed from FA1V2_Pr_1001
        validate("Comp-5008_5490_1001", "Comp-5008", "5490", "1001", expectedStatus2);      
        
        MaterialStatus[] expectedStatus3 = { MaterialStatus.REMOVED };
        // -------------------------------------------------- materials removed from FA1V2_Pr_91828
        validate("Comp-5008_5490_91828", "Comp-5008", "5490", "91828", expectedStatus3);
    }
    
    @Test
    //GLO-5250
    public void testFA_Sequence_MultipleOutboundlocations_WithoutAChange() throws GloriaApplicationException, IOException, InterruptedException {
        // ######### arrange
        //-------------------------------------------------- FA1V1_Pr_1001 - changeTechId : 1116, Outboundlocation : 1001
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_11)));

        ChangeId changeF1V1 = procurementServices.findChangeIdByTechId("52953");
        validate(changeF1V1, ChangeIdStatus.ACCEPTED);
        
        // -------------------------------------------------- assign FA1V1_Pr_1001
        List<MaterialHeader> materialHeaders1 = procurementServices.findMaterialHeaderByMtrlRequestId("Comp-5008_5490_1001", "Comp-5008");
        procurementServices.assignOrUnassignMateriaHeaders(procurementDtoTransformer.transformAsDTO(materialHeaders1), "assign", "V016387", "GOT_PE",
                                                           TeamType.MATERIAL_CONTROL.name(), "V016387");
        
        //-------------------------------------------------- FA1V2_Pr_91828 - changeTechId : 1131, Outboundlocation : 91828
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_14)));
        
        ChangeId changeF1V2 = procurementServices.findChangeIdByTechId("52968");        
        validate(changeF1V2, ChangeIdStatus.ACCEPTED);
        
        MaterialStatus[] expectedStatus = { MaterialStatus.ADDED };
        // -------------------------------------------------- materials Added FA1V2_Pr_91828
        validate("Comp-5008_5490_91828", "Comp-5008", "5490", "91828", expectedStatus);     
    }
    
    @Test
    @Ignore
    public void test_SWAP_referenceId() throws GloriaApplicationException, IOException {
        // ######### arrange
        // --------------------------------------------------
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_15)));

        // -------------------------------------------------- assign
        List<MaterialHeader> materialHeaders1 = procurementServices.findMaterialHeaderByMtrlRequestId("FM-8890_3062_2800", "FM-8890");
        procurementServices.assignOrUnassignMateriaHeaders(procurementDtoTransformer.transformAsDTO(materialHeaders1), "assign", "all", "GOT_PE",
                                                           TeamType.MATERIAL_CONTROL.name(), "all");

        // dummy deliveryNoteLines and RequestGroups for updation
        DeliveryNoteLine someDeliveryNoteLine = new DeliveryNoteLine();
        someDeliveryNoteLine.setReferenceIds("FM-8890");

        Order someOrder = new Order();
        OrderLine someOrderLine = new OrderLine();
        someOrderLine.setOrder(someOrder);
        someOrderLine.getDeliveryNoteLines().add(someDeliveryNoteLine);
        someOrder.getOrderLines().add(someOrderLine);
        someOrder = orderRepository.save(someOrder);

        List<Material> materials = materialHeaders1.get(0).getMaterials();
        Material anyMaterial = materials.get(0);
        anyMaterial.setOrderLine(someOrder.getOrderLines().get(0));

        RequestGroup someRequestGroup = new RequestGroup();
        someRequestGroup.setReferenceId("FM-8890");
        someRequestGroup = requestHeaderRepository.saveRequestGroup(someRequestGroup);
        
        MaterialLine anyMaterialLine = anyMaterial.getMaterialLine().get(0);
        anyMaterialLine.setRequestGroup(someRequestGroup);
        // ######### act
        // --------------------------------------------------
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_16)));

        List<MaterialHeader> headersWithUpdatedReferenceIds = procurementServices.findMaterialHeaderByMtrlRequestId("FM-2222_2232", "FM-2222");

        // ######### assert
        Assert.assertTrue(headersWithUpdatedReferenceIds != null && !headersWithUpdatedReferenceIds.isEmpty());

        // assert change in header
        MaterialHeader materialHeaderAfterSwap = headersWithUpdatedReferenceIds.get(0);
        Assert.assertTrue(materialHeaderAfterSwap.getReferenceId().contains("FM-2222"));
        // assert change in procureline
        Material materialAfterSwap = materialHeaderAfterSwap.getMaterials().get(0);
        Assert.assertTrue(materialAfterSwap.getProcureLine().getReferenceIds().contains("FM-2222"));
        // assert change in deliveryNoteLine
        Assert.assertTrue(materialAfterSwap.getOrderLine().getDeliveryNoteLines().get(0).getReferenceIds().contains("FM-2222"));
        // assert change in requestGroup
        Assert.assertTrue(materialAfterSwap.getMaterialLine().get(0).getRequestGroup().getReferenceId().contains("FM-2222"));
    }

    @Test
    @Ignore
    public void test_add_AdditionalUsage_request() throws GloriaApplicationException, IOException, InterruptedException {
        // ######### arrange
        // -------------------------------------------------- SPARE_MR17512 - with 2 addtional_usage headers
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_17)));

        ChangeId change = procurementServices.findChangeIdByTechId("41");
        validate(change, ChangeIdStatus.ACCEPTED);

        // -------------------------------------------------- assign FA1V1_Pr_1001
        List<MaterialHeader> materialHeaders1 = procurementServices.findMaterialHeaderByMtrlRequestId("SPARE_MR17512", null);
        procurementServices.assignOrUnassignMateriaHeaders(procurementDtoTransformer.transformAsDTO(materialHeaders1), "assign", "V016387", "LYS",
                                                           TeamType.MATERIAL_CONTROL.name(), "V016387");

        MaterialStatus[] expectedStatus = { MaterialStatus.ADDED };
        // -------------------------------------------------- materials Added FA1V2_Pr_91828
        validate("SPARE_MR17512", "MR17512", null, null, expectedStatus);
    }
    
    @Test
    @Ignore
    public void test_add_AdditionalUsage_request_autoAcceptChange() throws GloriaApplicationException, IOException, InterruptedException {
        // ######### arrange
        // -------------------------------------------------- SPARE_MR17512 - with 2 addtional_usage headers
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_17)));

        ChangeId change = procurementServices.findChangeIdByTechId("41");
        validate(change, ChangeIdStatus.ACCEPTED);
        em.clear();

        // -------------------------------------------------- assign FA1V1_Pr_1001
        List<MaterialHeader> materialHeaders1 = procurementServices.findMaterialHeaderByMtrlRequestId("SPARE_MR17512", null);
        procurementServices.assignOrUnassignMateriaHeaders(procurementDtoTransformer.transformAsDTO(materialHeaders1), "assign", "V016387", "LYS",
                                                           TeamType.MATERIAL_CONTROL.name(), "V016387");

        em.clear();
        MaterialStatus[] expectedStatus = { MaterialStatus.ADDED };
        // -------------------------------------------------- materials Added FA1V2_Pr_91828
        validate("SPARE_MR17512", "MR17512", null, null, expectedStatus);
        
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_18)));
        
        change = procurementServices.findChangeIdByTechId("42");
        validate(change, ChangeIdStatus.ACCEPTED);
        MaterialStatus[] expectedStatusAfterChange = { MaterialStatus.ADDED };
        // -------------------------------------------------- materials Added FA1V2_Pr_91828
        validate("SPARE_MR17512", "MR17512", null, null, expectedStatusAfterChange);
    }
}
