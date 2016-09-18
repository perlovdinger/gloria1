package com.volvo.gloria.procuretestcases;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.volvo.gloria.authorization.c.TeamType;
import com.volvo.gloria.procurematerial.b.ProcurementDtoTransformer;
import com.volvo.gloria.procurematerial.b.RequestTransformer;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.status.changeid.ChangeIdStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatus;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;

public class ProtomMRTest extends ProcureBaseTest {

    @Inject
    private RequestTransformer requestTransformer;
    
    @Inject
    ProcurementDtoTransformer procurementDtoTransformer;

    private static final String XML_1 = "procureTestCases/Protom_MR/baseflow/1_MR1_adds_no_phase.xml";
    private static final String XML_2 = "procureTestCases/Protom_MR/baseflow/2_MR1_set_Phase.xml";
    private static final String XML_3 = "procureTestCases/Protom_MR/baseflow/3_MR1_adds_remove.xml";
    private static final String XML_5 = "procureTestCases/Protom_MR/baseflow/5_MR2_adds_remove.xml";
    private static final String XML_6 = "procureTestCases/Protom_MR/baseflow/6_MR2_cancel.xml";

       
    @Test
    @Ignore
    public void testMRScenarios() throws GloriaApplicationException, IOException, InterruptedException {
        process_1_MR1_adds_no_phase();
        process_2_MR1_set_Phase();
        process_3_MR1_adds_remove();
        // process_4_MR1_cancel();
        process_5_MR2_adds_remove();
        process_6_MR2_cancel();
    }

    //
    // First MR
    // Auto accepted
    // Assign MaterialHeaders
    private void process_1_MR1_adds_no_phase() throws GloriaApplicationException, IOException, InterruptedException {

        // ------------------------------------------------------------------------------------------------------------------------
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_1)));
        // ------------------------------------------------------------------------------------------------------------------------
        String changeTechId = "100";

        ChangeId changeId = procurementServices.findChangeIdByTechId(changeTechId);
        validate(changeId, ChangeIdStatus.ACCEPTED, "MR101");

        MaterialStatus[] expectedStatus = { MaterialStatus.ADDED };
        validate("FH8732_1000_8410", "FH8732", null, "8410", "MR101", expectedStatus);
        validate("FH8732_1000_8411", "FH8732", null, "8411", "MR101", expectedStatus);

        // ------------------------------------------------------------------------------------------------------------------------
        // assign MaterialHeaders
        List<MaterialHeader> materialHeaders1 = procurementServices.findMaterialHeaderByMtrlRequestId("FH8732_1000_8410", "FH8732");
        procurementServices.assignOrUnassignMateriaHeaders(procurementDtoTransformer.transformAsDTO(materialHeaders1), "assign", "V016387", "GOT_PE", TeamType.MATERIAL_CONTROL.name(), "V016387");

        List<MaterialHeader> materialHeaders2 = procurementServices.findMaterialHeaderByMtrlRequestId("FH8732_1000_8411", "FH8732");
        procurementServices.assignOrUnassignMateriaHeaders(procurementDtoTransformer.transformAsDTO(materialHeaders2), "assign", "V016387", "GOT_PE", TeamType.MATERIAL_CONTROL.name(), "V016387");

        changeId = procurementServices.findChangeIdByTechId(changeTechId);
        validate(changeId, ChangeIdStatus.ACCEPTED, "MR101");
        Assert.assertEquals(2, changeId.getAddMaterials().size());
        Assert.assertEquals(0, changeId.getRemoveMaterials().size());

    }

    //
    // Set phase (Material Controller does not have to confirm)
    private void process_2_MR1_set_Phase() throws GloriaApplicationException, IOException, InterruptedException {
        // ------------------------------------------------------------------------------------------------------------------------
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_2)));

        MaterialStatus[] expectedStatus = { MaterialStatus.ADDED };
        validate("FH8732_3062_8410", "FH8732", "3062", "8410", "MR101", expectedStatus);
        validate("FH8732_3062_8411", "FH8732", "3062", "8411", "MR101", expectedStatus);
    }

    // updated MR add/remove Material
    // Will trigger Change to accept
    // Accept
    private void process_3_MR1_adds_remove() throws GloriaApplicationException, IOException, InterruptedException {
        String changeTechId = "102";
        // ------------------------------------------------------------------------------------------------------------------------
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_3)));
        ChangeId changeId = procurementServices.findChangeIdByTechId(changeTechId);
        validate(changeId, ChangeIdStatus.WAIT_CONFIRM, "MR101");
        Assert.assertEquals(2, changeId.getAddMaterials().size());
        Assert.assertEquals(2, changeId.getRemoveMaterials().size());

        MaterialStatus[] expectedStatus = { MaterialStatus.ADD_NOT_ACCEPTED, MaterialStatus.REMOVE_MARKED };
        validate("FH8732_3062_8410", "FH8732", "3062", "8410", "MR101", expectedStatus);
        validate("FH8732_3062_8411", "FH8732", "3062", "8411", "MR101", expectedStatus);

        // ------------------------------------------------------------------------------------------------------------------------
        // Accept change
        acceptRejectChange("accept", changeTechId, "V016387");
        validate(changeId, ChangeIdStatus.ACCEPTED, "MR101");

        MaterialStatus[] expectedStatus2 = { MaterialStatus.ADDED, MaterialStatus.REMOVED };
        validate("FH8732_3062_8410", "FH8732", "3062", "8410", "MR101", expectedStatus2);
        validate("FH8732_3062_8411", "FH8732", "3062", "8411", "MR101", expectedStatus2);

    }

    // updated MR add/remove Material
    // Will trigger Change to accept
    // Reject
    private void process_5_MR2_adds_remove() throws GloriaApplicationException, IOException, InterruptedException {
        String changeTechId = "103";
        // ------------------------------------------------------------------------------------------------------------------------
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_5)));
        //
        MaterialStatus[] expectedStatus = { MaterialStatus.ADD_NOT_ACCEPTED, MaterialStatus.REMOVED, MaterialStatus.REMOVE_MARKED };
        validate("FH8732_3062_8410", "FH8732", "3062", "8410", "MR101", expectedStatus);
        validate("FH8732_3062_8411", "FH8732", "3062", "8411", "MR101", expectedStatus);

        // ------------------------------------------------------------------------------------------------------------------------
        // accept change
        acceptRejectChange("accept", changeTechId, "V016387");
        ChangeId changeId2 = procurementServices.findChangeIdByTechId(changeTechId);
        validate(changeId2, ChangeIdStatus.ACCEPTED, "MR102");

        MaterialStatus[] expectedStatus2 = { MaterialStatus.ADDED, MaterialStatus.REMOVED };
        validate("FH8732_3062_8410", "FH8732", "3062", "8410", "MR102", expectedStatus2);
        validate("FH8732_3062_8411", "FH8732", "3062", "8411", "MR102", expectedStatus2);
    }

    private void process_6_MR2_cancel() throws GloriaApplicationException, IOException, InterruptedException {
        procurementServices.manageRequestNoTransaction(requestTransformer.transformRequest(IOUtil.getStringFromClasspath(XML_6)));

        ChangeId cancelChange = procurementServices.findChangeIdByTechId("1001");
        
        MaterialStatus[] expectedStatus = { MaterialStatus.REMOVED, MaterialStatus.REMOVE_MARKED };

        validate("FH8732_3062_8410", "FH8732", "3062", "8410", "MR102", expectedStatus);
        validate("FH8732_3062_8411", "FH8732", "3062", "8411", "MR102", expectedStatus);
        
        acceptRejectCancellation("accept", "1001", "V016387");
        
        validate(cancelChange, ChangeIdStatus.CANCELLED);
        MaterialStatus[] expectedStatus2 = { MaterialStatus.REMOVED };
        // -------------------------------------------------- materials removed
        validate("FH8732_3062_8410", "FH8732", "3062", "8410", "MR102", expectedStatus2);
        validate("FH8732_3062_8411", "FH8732", "3062", "8411", "MR102", expectedStatus2);
        

    }

}
