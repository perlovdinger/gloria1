package com.volvo.gloria.procurematerial.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.util.Assert;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;

public class RequestGroupHelperTest {
   
   private List<MaterialLine> prepareMaterialLines() {
        MaterialLine firstMaterialLine = Mockito.mock(MaterialLine.class);
        Mockito.when(firstMaterialLine.getStatus()).thenReturn(MaterialLineStatus.STORED);
        
        Material firstMaterial = Mockito.mock(Material.class);
        
        FinanceHeader financeHeader = Mockito.mock(FinanceHeader.class);
        Mockito.when(firstMaterial.getFinanceHeader()).thenReturn(financeHeader);
        
        MaterialHeader materialHeader = Mockito.mock(MaterialHeader.class);
        Mockito.when(firstMaterial.getMaterialHeader()).thenReturn(materialHeader);
        
        MaterialHeaderVersion accepted = Mockito.mock(MaterialHeaderVersion.class);
        Mockito.when(materialHeader.getAccepted()).thenReturn(accepted);
        
        Mockito.when(firstMaterialLine.getMaterial()).thenReturn(firstMaterial);
        Mockito.when(firstMaterialLine.getGroupIdentifierKey()).thenReturn("projectId=W-123,referenceGroup=,referenceId=TO1,phase=,Zone=Z1");
        
        //Second Material
        Material secondMaterial = Mockito.mock(Material.class);
        Mockito.when(secondMaterial.getFinanceHeader()).thenReturn(financeHeader);
        Mockito.when(secondMaterial.getMaterialHeader()).thenReturn(materialHeader);
        
        MaterialLine secondMaterialLine = Mockito.mock(MaterialLine.class);
        Mockito.when(secondMaterialLine.getStatus()).thenReturn(MaterialLineStatus.STORED);
        Mockito.when(secondMaterialLine.getMaterial()).thenReturn(secondMaterial);
        
        Mockito.when(secondMaterialLine.getGroupIdentifierKey()).thenReturn("projectId=W-123,referenceGroup=,referenceId=TO1,phase=,Zone=Z1");
       
        //third material
        MaterialLine thirdMaterialLine = Mockito.mock(MaterialLine.class);
        Mockito.when(thirdMaterialLine.getStatus()).thenReturn(MaterialLineStatus.READY_TO_STORE);
        Mockito.when(thirdMaterialLine.getMaterial()).thenReturn(secondMaterial);
        Mockito.when(thirdMaterialLine.getGroupIdentifierKey()).thenReturn("projectId=W-123,referenceGroup=,referenceId=TO1,phase=,Zone=Z2");
        
        List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
        materialLines.add(firstMaterialLine);
        materialLines.add(secondMaterialLine);
        materialLines.add(thirdMaterialLine);
        return materialLines;
    }
   
    //@Comment : GLO-6270 states that MTRID should be removed from grouping criteria , hence doing so and adjusting the assertion of the test case to suit new requirement
    @Test
    public void testGLO5169_6270RequestGrouping() throws GloriaApplicationException {
        // Arrange
        UserDTO user = new UserDTO();
        CommonServices commonServices = Mockito.mock(CommonServices.class);
        MaterialHeaderRepository requestHeaderRepository = Mockito.mock(MaterialHeaderRepository.class);

        List<MaterialLine> materialLines = prepareMaterialLines();

        // Act
        ArrayList<RequestList> newRequestListsCreated = new ArrayList<RequestList>();
        RequestGroupHelper.processNonRequestExcludedMaterials(materialLines, newRequestListsCreated, user,
                                                                                               commonServices, false, requestHeaderRepository);

        // Assert
        Assert.notEmpty(newRequestListsCreated);
        Assert.isTrue(newRequestListsCreated.size() == 1);
        Assert.isTrue(newRequestListsCreated.get(0).getRequestGroups().size() == 2);
    }

}
