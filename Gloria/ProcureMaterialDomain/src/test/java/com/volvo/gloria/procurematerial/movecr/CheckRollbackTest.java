package com.volvo.gloria.procurematerial.movecr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.Test;

import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.jvs.test.AbstractTransactionalTestCase;
/*
 * Check that Rollback is done between each test method.
 */
public class CheckRollbackTest extends AbstractTransactionalTestCase {
    public CheckRollbackTest() {
//        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }

    @Inject
    MaterialHeaderRepository requestHeaderRepository;

    @Test
    @Ignore
    public void findAllMaterials() {
        List<Material> materials = requestHeaderRepository.findAllMaterials();
        assertEquals(0, materials.size());
    }

    @Test
    @Ignore
    public void saveMaterial() {
        Material material = new Material();
        material.setPartName("partName1");
        requestHeaderRepository.saveMaterial(material);

        List<Material> materials = requestHeaderRepository.findAllMaterials();
        assertNotNull(materials);
        assertEquals(1, materials.size());
    }

    @Test
    @Ignore
    public void findMaterialCheckRollback() {
        List<Material> materials = requestHeaderRepository.findAllMaterials();
        // zero as previous tests inserts were rolled back
        assertEquals(0, materials.size());
    }

}
