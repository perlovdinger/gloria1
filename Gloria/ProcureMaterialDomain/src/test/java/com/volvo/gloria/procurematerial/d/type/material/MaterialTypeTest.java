package com.volvo.gloria.procurematerial.d.type.material;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class MaterialTypeTest extends AbstractTransactionalTestCase {
    public MaterialTypeTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    @Inject
    private ProcureLineRepository procureLineRepository;
    
    private MaterialHeaderRepository requestHeaderRepository;
    
    @Before
    public void setUp() {
        
        requestHeaderRepository = Mockito.mock(MaterialHeaderRepository.class);
    }

    @Test
    public void testGLO4586RevertUsage() throws GloriaApplicationException {
        // Arrange
        Material material = new Material();
        MaterialLine materialLine = new MaterialLine();
        materialLine.setProcureType(ProcureType.FROM_STOCK);
        materialLine.setStatus(MaterialLineStatus.ORDER_PLACED_INTERNAL);
        materialLine.setQuantity(10L);
        materialLine.setMaterial(material);
        material.getMaterialLine().add(materialLine);
        
        ProcureLine procureLine = new ProcureLine();
        procureLine.getMaterials().add(material);
        List<Material> materialsToBeRemoved = new ArrayList<Material>();
        
        // Act
        MaterialType.USAGE.revert(material, procureLine, requestHeaderRepository, procureLineRepository, materialsToBeRemoved, null, null);
        
        //Assert
    }
    
    @Test
    public void testGLO5115RevertReleased() throws GloriaApplicationException {
        // Arrange
        Material material = new Material();
        MaterialLine materialLine = new MaterialLine();
        materialLine.setStatus(MaterialLineStatus.ORDER_PLACED_INTERNAL);
        material.getMaterialLine().add(materialLine);
        List<Material> materialsToBeRemoved = new ArrayList<Material>();
        
        // Act
        MaterialType.RELEASED.revert(material, null, requestHeaderRepository, procureLineRepository, materialsToBeRemoved, null, null);
        
        //Assert
        Assert.assertEquals(material.getMaterialLine().get(0).getStatus(), MaterialLineStatus.WAIT_TO_PROCURE);
    }
    
    
    @Test
    public void testGLO4502RevertModified() throws GloriaApplicationException {
        //Arrange
        Material material = new Material();
        MaterialLine materialLine = new MaterialLine();
        materialLine.setProcureType(ProcureType.INTERNAL);
        materialLine.setStatus(MaterialLineStatus.ORDER_PLACED_INTERNAL);
        materialLine.setQuantity(10L);
        materialLine.setMaterial(material);
        material.getMaterialLine().add(materialLine);
        
        ProcureLine procureLine = new ProcureLine();
        procureLine.getMaterials().add(material);
        
        List<Material> materialsToBeRemoved = new ArrayList<Material>();
        //Act
        MaterialType.MODIFIED.revert(material, procureLine, requestHeaderRepository, procureLineRepository, materialsToBeRemoved, null, null);
        
        //Assert
  
        Assert.assertEquals(1, materialsToBeRemoved.size());
    }
    
    @Test
    public void testGLO4502RevertUsageReplaced() throws GloriaApplicationException {
        //Arrange
        Material material = new Material();
        MaterialLine materialLine = new MaterialLine();
        materialLine.setProcureType(ProcureType.INTERNAL);
        materialLine.setStatus(MaterialLineStatus.ORDER_PLACED_INTERNAL);
        materialLine.setQuantity(10L);
        materialLine.setMaterial(material);
        material.getMaterialLine().add(materialLine);
        
        ProcureLine procureLine = new ProcureLine();
        procureLine.getMaterials().add(material);
        List<Material> materialsToBeRemoved = new ArrayList<Material>();
        
        //Act
        MaterialType.USAGE_REPLACED.revert(material, procureLine, requestHeaderRepository, procureLineRepository, materialsToBeRemoved, null, null);
        
        //Assert
  
        Assert.assertEquals(MaterialType.USAGE, material.getMaterialType());
        Assert.assertEquals(0, material.getModificationId());
        Assert.assertEquals(null, material.getModificationType());
        Assert.assertEquals(0, material.getReplacedByOid());
    }
}
