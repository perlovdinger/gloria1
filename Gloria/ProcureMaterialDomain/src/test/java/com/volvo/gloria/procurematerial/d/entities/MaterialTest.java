package com.volvo.gloria.procurematerial.d.entities;


import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.volvo.gloria.procurematerial.c.ProcureResponsibility;
import com.volvo.gloria.procurematerial.d.type.request.RequestType;

public class MaterialTest {

    @Test
    public void testKeySize() {
        Material material = new Material();
        MaterialHeader materialHeader = new MaterialHeader();
        materialHeader.setRequestType(RequestType.FOR_STOCK);
        material.setMaterialHeader(materialHeader);
        Map<String,String> map = material.getDefaultGroupIdentifierKey();
        Assert.assertEquals(15, map.size());
    }
    
    @Test
    public void testKeySizeWithTempProcureLine() {
        Material material = new Material();
        MaterialHeader materialHeader = new MaterialHeader();
        materialHeader.setRequestType(RequestType.FOR_STOCK);
        material.setMaterialHeader(materialHeader);
        ProcureLine procureLine = new ProcureLine();
        material.setTemporaryProcureLine(procureLine);
        Map<String,String> map = material.getDefaultGroupIdentifierKey();
        Assert.assertEquals(15, map.size());
    }
    
    @Test
    public void testKeySizeWithTempProcureLineAndFinanceHeader() {
        Material material = new Material();
        MaterialHeader materialHeader = new MaterialHeader();
        materialHeader.setRequestType(RequestType.FOR_STOCK);
        material.setMaterialHeader(materialHeader);
        ProcureLine procureLine = new ProcureLine();
        material.setTemporaryProcureLine(procureLine);
        FinanceHeader financeHeader = new FinanceHeader();
        material.setFinanceHeader(financeHeader);
        Map<String,String> map = material.getDefaultGroupIdentifierKey();
        Assert.assertEquals(15, map.size());
    }
    
    @Test
    public void testKeySizeWithProcureLine() {
        Material material = new Material();
        MaterialHeader materialHeader = new MaterialHeader();
        materialHeader.setRequestType(RequestType.FOR_STOCK);
        material.setMaterialHeader(materialHeader);
        ProcureLine procureLine = new ProcureLine();
        material.setProcureLine(procureLine);
        Map<String,String> map = material.getDefaultGroupIdentifierKey();
        Assert.assertEquals(15, map.size());
    }

    @Test
    public void testKey() {
        Material material = new Material();
        FinanceHeader financeHeader = setFinanceHeader();
        material.setFinanceHeader(financeHeader);
        material.setPartAffiliation("partAffiliation");
        material.setPartNumber("partNumber");
        material.setPartVersion("partVersion");
        material.setPartModification("partModification");
        material.setUnitOfMeasure("unitOfMeasure");
        ProcureLine procureLine = setProcureLine();
        material.setProcureLine(procureLine);
        MaterialHeader materialHeader = new MaterialHeader();
        materialHeader.setRequestType(RequestType.FOR_STOCK);
        MaterialHeaderVersion materialHeaderVersion = new MaterialHeaderVersion();
        materialHeaderVersion.setOutboundLocationId("outboundLocationId");
        materialHeader.setAccepted(materialHeaderVersion);
        material.setMaterialHeader(materialHeader);
        
        Map<String,String> map = material.getDefaultGroupIdentifierKey();
        Assert.assertEquals("projectId", map.get("projectId"));
        Assert.assertEquals("glAccount", map.get("glAccount"));
        Assert.assertEquals("costCenter", map.get("costCenter"));
        Assert.assertEquals("wbsCode", map.get("wbsCode"));
        Assert.assertEquals("internalOrderNoSAP", map.get("internalOrderNoSAP"));
        
        Assert.assertEquals( "MTREQ_FORSTOCK",map.get("requestType"));
        
        
        Assert.assertEquals("forwardedUserId", map.get("forwardedUserId"));
        Assert.assertEquals("forwardedTeam", map.get("forwardedTeam"));
        Assert.assertEquals(ProcureResponsibility.BUILDSITE.name(), map.get("responsibility"));
        Assert.assertEquals("outboundLocationId", map.get("buildSiteOutboundLocationId"));

    }
    private ProcureLine setProcureLine() {
        ProcureLine procureLine = new ProcureLine();
        procureLine.setResponsibility(ProcureResponsibility.BUILDSITE);
        procureLine.setForwardedUserId("forwardedUserId");
        procureLine.setForwardedTeam("forwardedTeam");
        return procureLine;
    }
    private FinanceHeader setFinanceHeader() {
        FinanceHeader financeHeader = new FinanceHeader();
        financeHeader.setProjectId("projectId");
        financeHeader.setGlAccount("glAccount");
        financeHeader.setCostCenter("costCenter");
        financeHeader.setWbsCode("wbsCode");
        financeHeader.setInternalOrderNoSAP("internalOrderNoSAP");
        return financeHeader;
    }

}
