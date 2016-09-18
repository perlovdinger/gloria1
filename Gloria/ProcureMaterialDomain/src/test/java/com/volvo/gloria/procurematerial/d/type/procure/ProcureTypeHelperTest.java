package com.volvo.gloria.procurematerial.d.type.procure;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.dto.ProcureLineDTO;
import com.volvo.gloria.procurematerial.d.entities.Buyer;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.PurchaseOrganisationRepository;
import com.volvo.gloria.util.GloriaApplicationException;

public class ProcureTypeHelperTest {
    
    @Inject
    private TraceabilityRepository traceabilityRepository;

    /*
     * procureLine has one material that cannot be found in grouping then procueline properties 
     * must be reset
     */
    @Test
    public void testReturnProcureLine() throws GloriaApplicationException {
        //arrange
        List<Material> materials = new ArrayList<Material>();
        Material material = new Material();
        materials.add(material);
        ProcureLine procureLine = Mockito.mock(ProcureLine.class);
        ProcureLineRepository procureLineRepository = Mockito.mock(ProcureLineRepository.class);
        ProcurementServices procurementServices = Mockito.mock(ProcurementServices.class);
        Mockito.when(procureLine.getMaterials()).thenReturn(materials);
        List<Material> materials1 = new ArrayList<Material>();
        Material material1 = new Material();
        materials1.add(material1);
        Mockito.when(procurementServices.groupIfMaterialsExist(Mockito.any(String.class),Mockito.any(Material.class))).thenReturn(materials1);
        
        //act
        ProcureTypeHelper.returnProcureLine(procureLine,procureLineRepository,procurementServices);
        //assert
        Assert.assertNull(procureLine.getForwardedTeam());
        Assert.assertNull(procureLine.getForwardedUserId());
        Assert.assertNull(procureLine.getForwardedUserName());
        Mockito.verify(procureLineRepository, Mockito.times(1)).save(procureLine);
    }
    
    /*
     * procureLine has one material assigned to IP, 
     * 
     */
    @Test
    public void testRemoveOfProcurelineWithNoMoreMaterialsWhenIPReturnsProcureLine() throws GloriaApplicationException {
        // arrange
        List<Material> materials = new ArrayList<Material>();
        Material material = new Material();
        materials.add(material);
        ProcureLine procureLine = Mockito.mock(ProcureLine.class);
        ProcureLineRepository procureLineRepository = Mockito.mock(ProcureLineRepository.class);
        ProcurementServices procurementServices = Mockito.mock(ProcurementServices.class);
        Mockito.when(procureLine.getMaterials()).thenReturn(materials);
        Mockito.when(procurementServices.groupIfMaterialsExist(Mockito.any(String.class), Mockito.any(Material.class))).thenReturn(new ArrayList<Material>());
        Mockito.when(procureLine.getStatus()).thenReturn(ProcureLineStatus.WAIT_TO_PROCURE);

        // act
        ProcureTypeHelper.returnProcureLine(procureLine, procureLineRepository, procurementServices);
        // assert
        Mockito.verify(procureLineRepository, Mockito.times(1)).delete(procureLine);
    }
    
    /*
     * procureLine has one material that cannot be found in grouping then procueline properties 
     * must be reset
     */
    @Test
    public void testForwardProcureLine() throws GloriaApplicationException {
        //arrange
        List<Material> materials = new ArrayList<Material>();
        Material material = new Material();
        materials.add(material);
        ProcureLine procureLine = Mockito.mock(ProcureLine.class);
        ProcureLineRepository procureLineRepository = Mockito.mock(ProcureLineRepository.class);
        ProcurementServices procurementServices = Mockito.mock(ProcurementServices.class);
        Mockito.when(procureLine.getMaterials()).thenReturn(materials);
        List<Material> materials1 = new ArrayList<Material>();
        Material material1 = new Material();
        materials1.add(material1);
        Mockito.when(procurementServices.groupIfMaterialsExist(Mockito.any(String.class),Mockito.any(Material.class), Mockito.anyString(), Mockito.anyString())).thenReturn(materials1);
        UserDTO userDTO = new UserDTO();
        //act
        ProcureTypeHelper.forwardProcureLine(procureLine, "forwardedTeam", "userId", "userName",
                                              procureLineRepository,  procurementServices, userDTO, traceabilityRepository);
        //assert
        Mockito.verify(procureLine, Mockito.times(1)).setForwardedTeam(Mockito.anyString());
        Mockito.verify(procureLine, Mockito.times(1)).setForwardedUserId(Mockito.anyString());
        Mockito.verify(procureLine, Mockito.times(1)).setForwardedUserName(Mockito.anyString());
        Mockito.verify(procureLineRepository, Mockito.times(1)).save(procureLine);
    }
    
    @Test(expected = GloriaApplicationException.class)
    public void testGLO6651_MissingPurchaseOrganisation() throws GloriaApplicationException {
        // arrange
        ProcureLine procureLine = Mockito.mock(ProcureLine.class);
        Mockito.when(procureLine.getStatus()).thenReturn(ProcureLineStatus.WAIT_TO_PROCURE);
        Buyer buyer = Mockito.mock(Buyer.class);
        ProcureLineDTO procureLineDTO = Mockito.mock(ProcureLineDTO.class);
        PurchaseOrganisationRepository purchaseOrganisationRepository = Mockito.mock(PurchaseOrganisationRepository.class);
        Mockito.when(purchaseOrganisationRepository.findBuyerByCode(Mockito.anyString())).thenReturn(buyer);
        // act
        ProcureTypeHelper.updateProcureLineForExternal(procureLineDTO, procureLine, purchaseOrganisationRepository);
    }

}
