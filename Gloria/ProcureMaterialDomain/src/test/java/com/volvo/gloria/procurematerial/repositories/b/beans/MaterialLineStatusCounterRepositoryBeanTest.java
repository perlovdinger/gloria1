package com.volvo.gloria.procurematerial.repositories.b.beans;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import com.volvo.gloria.procurematerial.d.entities.MaterialLineStatusCounter;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusCounterType;
import com.volvo.gloria.procurematerial.repositories.b.MaterialLineStatusCounterRepository;
import com.volvo.gloria.util.DateUtil;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class MaterialLineStatusCounterRepositoryBeanTest extends AbstractTransactionalTestCase{

    public MaterialLineStatusCounterRepositoryBeanTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    
    @Inject
    protected MaterialLineStatusCounterRepository materialLineStatusCounterRepository;
    /*
     * 
     */
    @Test
    public void testCount(){
        //create data
        
        Date withinRange = DateUtil.getDate(2015, 7, 1);
        Date withinRange1 = DateUtil.getDate(2015, 7, 30);
        Date outsideRange = DateUtil.getDate(2015, 8, 1);
        Date outsideRange2 = DateUtil.getDate(2015, 6, 31);
        // invalid due to RETURNS
        setUpSingleRecord(withinRange ,"rahul1","whSite",MaterialLineStatusCounterType.RETURNS);
        //valid
        setUpSingleRecord(withinRange,"rahul1","whSite4",MaterialLineStatusCounterType.PICKS);
        // Invalid type
        setUpSingleRecord(withinRange1,"rahul1","whSite4",MaterialLineStatusCounterType.RECEIVES);
        // outside date range1 and 2
        setUpSingleRecord(outsideRange,"rahul2","whSite4",MaterialLineStatusCounterType.PICKS);
        setUpSingleRecord(outsideRange2,"rahul2","whSite3",MaterialLineStatusCounterType.PICKS);
        
        setUpSingleRecord(withinRange,"rahul2","whSite3",MaterialLineStatusCounterType.PICKS);
        setUpSingleRecord(withinRange1,"rahul3","whSite2",MaterialLineStatusCounterType.PICKS);
        setUpSingleRecord(withinRange,"rahul3","whSite2",MaterialLineStatusCounterType.PICKS);
        setUpSingleRecord(withinRange1,"rahul3","whSite2",MaterialLineStatusCounterType.PICKS);
        setUpSingleRecord(withinRange1,"rahul4","whSite1",MaterialLineStatusCounterType.PICKS);
        setUpSingleRecord(withinRange,"rahul4","whSite1",MaterialLineStatusCounterType.PICKS);
        setUpSingleRecord(withinRange,"rahul4","whSite1",MaterialLineStatusCounterType.PICKS);
        //Invalid project
        setUpSingleRecord(withinRange,"rahul5","whSite1",MaterialLineStatusCounterType.PICKS);
        // Invalid whsite
        setUpSingleRecord(withinRange1,"rahul4","whSite6",MaterialLineStatusCounterType.PICKS);
        
        Date fromDate = DateUtil.getDate(2015, 7, 1);
        Date toDate = DateUtil.getDate(2015, 7, 31);
        String[] project = {"rahul1","rahul2","rahul3","rahul4"};
        String[] warehouse = {"whSite1","whSite2","whSite3","whSite4"};
        List<MaterialLineStatusCounterType> materialLineStatusCounterTypes = new ArrayList<MaterialLineStatusCounterType>();
        materialLineStatusCounterTypes.add(MaterialLineStatusCounterType.PICKS);
        List<MaterialLineStatusCounter> materialLineStatusCounters = materialLineStatusCounterRepository.getCount(fromDate, toDate, project , warehouse, materialLineStatusCounterTypes);
        Assert.assertEquals(8, materialLineStatusCounters.size());      
        
    }
    /*
     * this test tests saving of a MaterialStatusCounter and retriving it and asserting the values
     */
    @Test
    public void testSave() {        
        MaterialLineStatusCounter materialLineStatusCounter = setUpSingleRecord();        
        // Assert all fields are populated
        assertForFields(materialLineStatusCounter);      
    }
    
    @Test
    public void testFindById(){
        MaterialLineStatusCounter materialLineStatusCounter = setUpSingleRecord();
        MaterialLineStatusCounter materialLineStatusCounterResult = materialLineStatusCounterRepository.findById(materialLineStatusCounter.getId());
        assertForFields(materialLineStatusCounterResult);   
    }
    
    @Test
    public void testFindAll(){
        setUpSingleRecord();
        MaterialLineStatusCounter materialLineStatusCounterResult = getRecordFromDB();
        assertForFields(materialLineStatusCounterResult);   
    }

    private void assertForFields(MaterialLineStatusCounter materialLineStatusCounter) {
        Assert.assertNotNull(materialLineStatusCounter);
        Assert.assertNotNull(materialLineStatusCounter.getId());  
        Assert.assertNotNull(materialLineStatusCounter.getDate());
        Assert.assertEquals("rahul",materialLineStatusCounter.getProjectId());
        Assert.assertEquals(MaterialLineStatusCounterType.RETURNS, materialLineStatusCounter.getType());
        Assert.assertEquals("whSite", materialLineStatusCounter.getWhSite());
        Assert.assertNotNull(materialLineStatusCounter.getVersion());
    }

    private MaterialLineStatusCounter getRecordFromDB() {
        List<MaterialLineStatusCounter> materialLineStatusCounterResults =  materialLineStatusCounterRepository.findAll();
        Assert.assertEquals(1, materialLineStatusCounterResults.size());
        MaterialLineStatusCounter materialLineStatusCounterAssert = materialLineStatusCounterResults.get(0);
        return materialLineStatusCounterAssert;
    }
    
    private MaterialLineStatusCounter setUpSingleRecord() {
        return setUpSingleRecord(new Timestamp(System.currentTimeMillis()),"rahul","whSite",MaterialLineStatusCounterType.RETURNS);
    }
    private MaterialLineStatusCounter setUpSingleRecord(Date date, String projectId, String whsiteId, MaterialLineStatusCounterType type) {
        MaterialLineStatusCounter materialLineStatusCounter = new MaterialLineStatusCounter();
        materialLineStatusCounter.setDate(new Timestamp(date.getTime()));
        materialLineStatusCounter.setProjectId(projectId);
        materialLineStatusCounter.setWhSite(whsiteId);
        materialLineStatusCounter.setType(type);
        MaterialLineStatusCounter materialLineStatusCounterResult = materialLineStatusCounterRepository.save(materialLineStatusCounter);
        return materialLineStatusCounterResult;
    }
}
