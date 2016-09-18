package com.volvo.gloria.procurematerial.d.status.procureline;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.dto.ProcureLineDTO;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.PurchaseOrganisationRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class ProcureLineStatusTest extends AbstractTransactionalTestCase {
    public ProcureLineStatusTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    @Inject
    ProcureLineRepository procureLineRepository;
    
    @Inject
    PurchaseOrganisationRepository purchaseOrganisationRepo;
    
    @Inject
    CommonServices commonServices;
    
    @Inject
    ProcurementServices procurementServices;
    
    
    // one of the finance header from the underlying materails will be linked to Procureline 
    // on grouping. So updating material.financeHeader will reflect changes for both Procureline & material.
    @Test
    public void testUpdateProcureLine() throws GloriaApplicationException {
        
        ProcureLine procureLine = new ProcureLine();
        procureLine.setStatus(ProcureLineStatus.PROCURED);
        
        FinanceHeader financeHeader = new FinanceHeader();
        financeHeader.setGlAccount("test");
        
        Material materialOne = new Material(); 
        materialOne.setFinanceHeader(financeHeader);
        procureLine.getMaterials().add(materialOne);
        
        ProcureLineDTO procureLineDTO = new ProcureLineDTO();
        procureLineDTO.setGlAccount("Gl189");
        procureLineDTO.setCostCenter("23454");
        
        //Act
        procureLine.getStatus().updateProcureLine(procureLineDTO, procureLine, null, null, null, procureLineRepository, commonServices, procurementServices,
                                                  purchaseOrganisationRepo, null);
        
        //Assert
        FinanceHeader financeHeaderAfterUpdate = procureLine.getMaterials().get(0).getFinanceHeader();
        Assert.assertEquals("Gl189", financeHeaderAfterUpdate.getGlAccount());
        Assert.assertEquals("23454", financeHeaderAfterUpdate.getCostCenter());
    }
}
