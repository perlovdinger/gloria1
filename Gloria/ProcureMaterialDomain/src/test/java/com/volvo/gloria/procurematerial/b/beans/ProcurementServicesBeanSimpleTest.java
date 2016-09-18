package com.volvo.gloria.procurematerial.b.beans;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class ProcurementServicesBeanSimpleTest extends AbstractTransactionalTestCase{

    public ProcurementServicesBeanSimpleTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    
    @Inject
    private ProcurementServices procurementServices;
    @Inject
    private ProcureLineRepository procureLineRepository;
    
    /*
     * This test tries to insert a ProcureLine and then retreive it using its grouping key
     * The result should be retreivable since the forwardedUser id is blank
     */
    @Test
    public void testFindProcureLineByGroupingKey() throws GloriaApplicationException, IOException, NoSuchAlgorithmException {

        // Arrange
        ProcureLine procureLine = new ProcureLine();
        procureLine.setCurrency("EUR");
        procureLine.setGroupingKeyMd5("abc");
        procureLine.setStatus(ProcureLineStatus.RECEIVED);
        procureLine.setMaterialControllerId("Rahul");
        procurementServices.addProcureLine(procureLine);
        HashMap<String ,Object> map = new HashMap<String ,Object>();
        map.put(ProcureLine.GROUPINGKEYMD5, "abc");
        map.put(ProcureLine.STATUS, ProcureLineStatus.RECEIVED);
        map.put(ProcureLine.MATERIALCONTROLLERID, "Rahul");
        map.put(ProcureLine.FORWARDEDUSERID, "");

        // ACT
        List<ProcureLine> procureLineList = procureLineRepository.findProcureLineByParameters( map);
        ProcureLine procureLineResult = procureLineList.get(0);
        // ASSERT
        Assert.assertNotNull(procureLineResult);
        Assert.assertEquals("EUR", procureLineResult.getCurrency());      
        Assert.assertEquals(ProcureLineStatus.RECEIVED, procureLineResult.getStatus());      
        Assert.assertEquals(procureLineResult.getMaterialControllerId(), "Rahul");   
    }
    
    @Test
    public void testFindProcureLineByGroupingKey111() throws GloriaApplicationException, IOException, NoSuchAlgorithmException {

        // Arrange
        ProcureLine procureLine = new ProcureLine();
        procureLine.setCurrency("EUR");
        procureLine.setGroupingKeyMd5("abc");
        procureLine.setStatus(ProcureLineStatus.RECEIVED);
        procureLine.setMaterialControllerId("Rahul");
        procurementServices.addProcureLine(procureLine);
        HashMap<String ,Object> map = new HashMap<String ,Object>();
        map.put(ProcureLine.GROUPINGKEYMD5, "abc");
        map.put(ProcureLine.STATUS, ProcureLineStatus.RECEIVED);
        map.put(ProcureLine.MATERIALCONTROLLERID, "Rahul");
        map.put(ProcureLine.FORWARDEDUSERID, "");

        // ACT
        List<ProcureLine> procureLineList = procureLineRepository.findProcureLineByParameters( map);
        ProcureLine procureLineResult = procureLineList.get(0);
        // ASSERT
        Assert.assertNotNull(procureLineResult);
        Assert.assertEquals("EUR", procureLineResult.getCurrency());      
        Assert.assertEquals(ProcureLineStatus.RECEIVED, procureLineResult.getStatus());      
        Assert.assertEquals(procureLineResult.getMaterialControllerId(), "Rahul");   
    }
    
    /*
     * This test tries to insert a ProcureLine and then retreive it using its grouping key
     * The result should NOT BE retreivable since the forwardedUser id is blank
     */
    @Test
    public void testFindProcureLineByGroupingKey1() throws GloriaApplicationException, IOException, NoSuchAlgorithmException {

        // Arrange
        ProcureLine procureLine = new ProcureLine();
        procureLine.setCurrency("EUR");
        procureLine.setGroupingKeyMd5("abc");
        procureLine.setStatus(ProcureLineStatus.RECEIVED);
        procureLine.setMaterialControllerId("Rahul");
        procureLine.setForwardedUserId( "abcde");
        procurementServices.addProcureLine(procureLine);
        HashMap<String ,Object> map = new HashMap<String ,Object>();
        map.put(ProcureLine.GROUPINGKEYMD5, "abc");
        map.put(ProcureLine.STATUS, ProcureLineStatus.RECEIVED);
        map.put(ProcureLine.MATERIALCONTROLLERID, "Rahul");
        map.put(ProcureLine.FORWARDEDUSERID, "abcde");

        // ACT
        List<ProcureLine> procureLineList = procureLineRepository.findProcureLineByParameters( map);
        //ProcureLine procureLineResult = procureLineList.get(0);
        // ASSERT
        Assert.assertNotNull(procureLineList);
        Assert.assertEquals(1, procureLineList.size());
    }
    
    /*
     * This test tries to insert a ProcureLine and then retreive it using its grouping key
     * The result should BE retreivable since the forwardedUser id is blank
     */
    @Test
    public void testFindProcureLineByGroupingKey2() throws GloriaApplicationException, IOException, NoSuchAlgorithmException {

        // Arrange
        ProcureLine procureLine = new ProcureLine();
        procureLine.setCurrency("EUR");
        procureLine.setGroupingKeyMd5("abc");
        procureLine.setStatus(ProcureLineStatus.RECEIVED);
        procureLine.setMaterialControllerId("Rahul");
        //procureLine.setForwardedUserId( "abcde");
        procurementServices.addProcureLine(procureLine);
        HashMap<String ,Object> map = new HashMap<String ,Object>();
        map.put(ProcureLine.GROUPINGKEYMD5, "abc");
        map.put(ProcureLine.STATUS, ProcureLineStatus.RECEIVED);
        map.put(ProcureLine.MATERIALCONTROLLERID, "Rahul");
        map.put(ProcureLine.FORWARDEDUSERID, null);

        // ACT
        List<ProcureLine> procureLineList = procureLineRepository.findProcureLineByParameters( map);
        ProcureLine procureLineResult = procureLineList.get(0);
        // ASSERT
        Assert.assertNotNull(procureLineResult);
        Assert.assertEquals("EUR", procureLineResult.getCurrency());      
        Assert.assertEquals(ProcureLineStatus.RECEIVED, procureLineResult.getStatus());      
        Assert.assertEquals(procureLineResult.getMaterialControllerId(), "Rahul");   
    }


}
