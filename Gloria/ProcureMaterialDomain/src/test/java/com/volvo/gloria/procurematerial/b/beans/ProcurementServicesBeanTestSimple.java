package com.volvo.gloria.procurematerial.b.beans;



import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.common.repositories.b.beans.TraceabilityRepositoryBean;
import com.volvo.gloria.procurematerial.c.dto.MaterialHeaderDTO;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.beans.MaterialHeaderRepositoryBean;
import com.volvo.gloria.util.GloriaApplicationException;

public class ProcurementServicesBeanTestSimple {
    
   // @Inject
   // private UserServices userServices;
    
    public ProcurementServicesBeanTestSimple() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    
    /*
     * when there is a single material then this is removed.  This is the basic flow
     */
    @Test
    public void testWithSingleMaterial() throws GloriaApplicationException {
        List<MaterialHeaderDTO> MaterialHeadersDTOs = new ArrayList<MaterialHeaderDTO>();
        MaterialHeaderDTO MaterialHeaderDTO = new MaterialHeaderDTO();
        MaterialHeadersDTOs.add(MaterialHeaderDTO);
        ProcurementServicesBean procurementServicesBean = new ProcurementServicesBean();
        
        // create an instance of the controller we want to test
        ProcureLineRepository procureLineRepository = Mockito.mock(ProcureLineRepository.class);  
        MaterialHeaderRepository requestHeaderRepository = Mockito.mock(MaterialHeaderRepositoryBean.class);  
        TraceabilityRepository traceabilityRepository = Mockito.mock(TraceabilityRepositoryBean.class);
        
        UserServices userServices = Mockito.mock(UserServices.class);
        
        UserDTO userDTO = Mockito.mock(UserDTO.class);
        userDTO.setId("UserId");
        userDTO.setUserName("Name");
        
        // since we aren't using spring, these values won't be injected, so set them manually
        ReflectionTestUtils.setField(procurementServicesBean , "requestHeaderRepository", requestHeaderRepository );
        ReflectionTestUtils.setField(procurementServicesBean , "procureLineRepository", procureLineRepository );
        ReflectionTestUtils.setField(procurementServicesBean , "userServices", userServices );
        ReflectionTestUtils.setField(procurementServicesBean , "traceabilityRepository", traceabilityRepository );

        MaterialHeader materialHeader = new MaterialHeader();
        materialHeader.setMaterialControllerUserId("userId");
        Material material = new Material();
        ProcureLine procureLine = new ProcureLine();
        procureLine.setStatus(ProcureLineStatus.WAIT_TO_PROCURE);
        List<Material> procureLineMaterials = new ArrayList<Material>();
        Material procureLineMaterial = new Material();
        procureLineMaterial.setMaterialType(MaterialType.USAGE);
        procureLineMaterials.add(procureLineMaterial);
        procureLine.setMaterials(procureLineMaterials);
        material.setProcureLine(procureLine);
        material.setMaterialType(MaterialType.USAGE);
        List<Material> materials = new ArrayList<Material>();
        materials.add(material);
        materialHeader.setMaterials(materials);
        Mockito.when(requestHeaderRepository.findById(Mockito.anyLong())).thenReturn(materialHeader);        
        List<MaterialHeader> materialHeaders = procurementServicesBean.unassignMaterialController(MaterialHeadersDTOs, "userId", "LYS");
        Mockito.when(userServices.getUser(Mockito.anyString())).thenReturn(userDTO);
        Assert.assertEquals(materialHeaders.size(), 1);
        //procureLine.getStatus().remove(procureLine, procureLineRepository); is called!
        
    }
    
    /*
     * when there is a multiple materialHeaders then procureline is notremoved.  This is the secondary flow
     */
    @Test
    public void testWithMultipleMaterial() throws GloriaApplicationException {
        List<MaterialHeaderDTO> MaterialHeadersDTOs = new ArrayList<MaterialHeaderDTO>();
        MaterialHeaderDTO MaterialHeaderDTO = new MaterialHeaderDTO();
        MaterialHeadersDTOs.add(MaterialHeaderDTO);
        ProcurementServicesBean procurementServicesBean = new ProcurementServicesBean();
        
        // create an instance of the controller we want to test
        ProcureLineRepository procureLineRepository = Mockito.mock(ProcureLineRepository.class);  
        MaterialHeaderRepository requestHeaderRepository = Mockito.mock(MaterialHeaderRepositoryBean.class);
        TraceabilityRepository traceabilityRepository = Mockito.mock(TraceabilityRepositoryBean.class);
        
        UserServices userServices = Mockito.mock(UserServices.class);
        
        UserDTO userDTO = Mockito.mock(UserDTO.class);
        userDTO.setId("UserId");
        userDTO.setUserName("Name");
        
        // since we aren't using spring, these values won't be injected, so set them manually
        ReflectionTestUtils.setField(procurementServicesBean , "requestHeaderRepository", requestHeaderRepository );
        ReflectionTestUtils.setField(procurementServicesBean , "procureLineRepository", procureLineRepository );
        ReflectionTestUtils.setField(procurementServicesBean , "userServices", userServices );
        ReflectionTestUtils.setField(procurementServicesBean , "traceabilityRepository", traceabilityRepository );
        
        MaterialHeader materialHeader = new MaterialHeader();
        materialHeader.setMaterialControllerUserId("userId");
        Material material = new Material();
        MaterialLine materialLine = new MaterialLine();
        materialLine.setStatus(MaterialLineStatus.BLOCKED);
        List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
        materialLines.add(materialLine);
        material.setMaterialLine(materialLines);
        material.setMaterialType(MaterialType.USAGE);
        ProcureLine procureLine = new ProcureLine();
        procureLine.setStatus(ProcureLineStatus.WAIT_TO_PROCURE);
        List<Material> procureLineMaterials = new ArrayList<Material>();
        Material procureLineMaterial = new Material();
        procureLineMaterial.setMaterialType(MaterialType.USAGE);
        Material procureLineMaterial1 = new Material();
        procureLineMaterial1.setMaterialType(MaterialType.USAGE);
        procureLineMaterial1.setAddedAfter(true);
        procureLineMaterials.add(procureLineMaterial);
        procureLineMaterials.add(procureLineMaterial1);
        procureLineMaterials.add(material);
        procureLine.setMaterials(procureLineMaterials);
        material.setProcureLine(procureLine);
        List<Material> materials = new ArrayList<Material>();
        materials.add(material);
        materialHeader.setMaterials(materials);
        Mockito.when(requestHeaderRepository.findById(Mockito.anyLong())).thenReturn(materialHeader);        
        Mockito.when(userServices.getUser(Mockito.anyString())).thenReturn(userDTO);
        
        List<MaterialHeader> materialHeaders = procurementServicesBean.unassignMaterialController(MaterialHeadersDTOs, "userId", "LYS");
        
        Assert.assertEquals(materialHeaders.size(), 1);
        //procureLine.getStatus().remove(procureLine, procureLineRepository);
        
        // item has been removed
        Assert.assertEquals(procureLineMaterials.size(), 3);
        Assert.assertEquals(material.isAddedAfter(),false);
        Assert.assertNull( material.getProcureLine());
        Assert.assertEquals(MaterialLineStatus.BLOCKED, material.getMaterialLine().get(0).getStatus());
        //isAddedAfter continues to be true since the list size is reduced to 2 not 1
        Assert.assertEquals(procureLineMaterial1.isAddedAfter(), true);
        //
        
        
    }
}
