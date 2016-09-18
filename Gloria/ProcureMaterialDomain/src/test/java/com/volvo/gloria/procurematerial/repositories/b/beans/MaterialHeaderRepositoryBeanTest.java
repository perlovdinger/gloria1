package com.volvo.gloria.procurematerial.repositories.b.beans;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.volvo.gloria.common.c.dto.reports.ReportWarehouseTransactionDTO;
import com.volvo.gloria.procurematerial.c.ShipmentType;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteSubLine;
import com.volvo.gloria.procurematerial.d.entities.DispatchNote;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.MaterialLineStatusTime;
import com.volvo.gloria.procurematerial.d.entities.MaterialPartAlias;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.PickList;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.entities.TransportLabel;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestlist.RequestListStatus;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.warehouse.c.ZoneType;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class MaterialHeaderRepositoryBeanTest extends AbstractTransactionalTestCase {

    public MaterialHeaderRepositoryBeanTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    EntityManager em = null;
    
    @PersistenceContext(unitName = "ProcureMaterialDomainPU")
    public void setEntityManager(EntityManager em) {
        this.em= em;
    }
    
    @Inject
    protected MaterialHeaderRepository requestHeaderRepository;
    
    @Inject
    protected DeliveryNoteRepository deliveryNoteRepository;
    
    /*
     * did this test to see if the only one sql is executed in the backend
     */
    @Test
    public void testXX(){
        ProcureLine procureLine = new ProcureLine();
        em.persist(procureLine);
        List<Long> procureLineIds = new ArrayList<Long>();
        procureLineIds.add(procureLine.getId());
        List<ProcureLine> materialsResult = requestHeaderRepository.findProcureLinesByIds(procureLineIds);
        Assert.assertEquals(1, materialsResult.size());
    }
    /*
     * did this test to see if the only one sql is executed in the backend
     */
    @Test
    public void testGetMaterialsForHeaderIds(){
        MaterialHeader materialHeader1 = createDataForGetMaterialsForHeaderIds();
        MaterialHeader materialHeader2 = createDataForGetMaterialsForHeaderIds();
        List<MaterialHeader> headerIds = new ArrayList<MaterialHeader>();
        headerIds.add(materialHeader1);
        headerIds.add(materialHeader2);
        List<Material> materialsResult = requestHeaderRepository.getMaterialsForHeaderIds(headerIds);
        materialsResult.get(0).getCharacteristics();
        Assert.assertEquals("costCenter", materialsResult.get(0).getFinanceHeader().getCostCenter());
        Assert.assertEquals(8, materialsResult.size());
        Assert.assertEquals(2, materialsResult.get(0).getPartAlias().size());
        MaterialHeader response =requestHeaderRepository.findById(materialHeader1.getId());
        List<Material> materialsTest = response.getMaterials();
        Assert.assertEquals(4, materialsTest.size());
        for (Material m: materialsTest) {
            Assert.assertNotNull(m.getFinanceHeader());
            List<MaterialPartAlias> materialPartAlias = m.getPartAlias();
            Assert.assertEquals(2, materialPartAlias.size());
            for (MaterialPartAlias m1: materialPartAlias) {
                Assert.assertNotNull(m1);
                Assert.assertEquals("partNumber", m1.getPartNumber());
            }
        }

        Assert.assertNotNull(materialsResult.get(0).getMaterialLine().get(0).getRequestGroup());
    }
    

    private MaterialHeader createDataForGetMaterialsForHeaderIds() {
        FinanceHeader financeHeader = new FinanceHeader();
        financeHeader.setCostCenter("costCenter");
        em.persist(financeHeader);
        
        Material material1 = createMaterial1(financeHeader);
        Material material2 = createMaterial1(financeHeader);
        Material material3 = createMaterial1(financeHeader);
        Material material4 = createMaterial1(financeHeader);
        
        List<Material> materials = new ArrayList<Material>();
        materials.add(material1);
        materials.add(material2);
        materials.add(material3);
        materials.add(material4);
        MaterialHeader materialHeader = new MaterialHeader();
        materialHeader.setMaterials(materials);
        em.persist(materialHeader);
        material1.setMaterialHeader(materialHeader);
        em.persist(material1);
        material2.setMaterialHeader(materialHeader);
        em.persist(materialHeader);
        material3.setMaterialHeader(materialHeader);
        em.persist(material3);
        material4.setMaterialHeader(materialHeader);
        em.persist(material4);
        /*
        Material material2 = new Material();
        material2.setFinanceHeader(financeHeader);
        em.persist(material2);
        


        
        MaterialPartAlias partAlia3 = new MaterialPartAlias();
        partAlia3.setPartNumber("partNumber");
        em.persist(partAlia3);
        MaterialPartAlias partAlia4 = new MaterialPartAlias();
        partAlia4.setPartNumber("partNumber");
        em.persist(partAlia4);
        List<MaterialPartAlias> partAlias1= new ArrayList<MaterialPartAlias>();
        partAlias1.add(partAlia3);
        partAlias1.add(partAlia4);
        material2.setPartAlias(partAlias1);
        em.persist(material2);*/
        return materialHeader;
    }
    private Material createMaterial1(FinanceHeader financeHeader) {
        Material material1 = new Material();        
        material1.setMailFormId("mailFormId");
        material1.setFinanceHeader(financeHeader);
        RequestGroup requestGroup = new RequestGroup();
        em.persist(requestGroup);
        MaterialLine materialLine = new MaterialLine();
        materialLine.setRequestGroup(requestGroup);
        em.persist(materialLine);
        List<MaterialLine> materialLinesRG = new ArrayList<MaterialLine>();
        materialLinesRG.add(materialLine);
        requestGroup.setMaterialLines(materialLinesRG);
        List<MaterialLine> materialLines= new ArrayList<MaterialLine>();
        materialLines.add(materialLine);
        material1.setMaterialLine(materialLines);
        MaterialPartAlias partAlia = new MaterialPartAlias();
        partAlia.setPartNumber("partNumber");
        em.persist(partAlia);
        MaterialPartAlias partAlia1 = new MaterialPartAlias();
        partAlia1.setPartNumber("partNumber");
        em.persist(partAlia1);
        List<MaterialPartAlias> partAlias= new ArrayList<MaterialPartAlias>();
        partAlias.add(partAlia);
        partAlias.add(partAlia1);
        material1.setPartAlias(partAlias);
        em.persist(material1);
        return material1;
    }
    
    @Test
    public void testUpdateProcureLineControllerDetails() {
        ProcureLine procureLine = new ProcureLine();
        procureLine.setMaterialControllerId("userid");
        em.persist(procureLine);
        ProcureLine procureLine1 = new ProcureLine();
        em.persist(procureLine1);
        ProcureLine procureLine2 = new ProcureLine();
        em.persist(procureLine2);
        List<Long> procureLinesOids = new ArrayList<Long>();
        procureLinesOids.add(procureLine.getProcureLineOid());
        procureLinesOids.add(procureLine1.getProcureLineOid());
        procureLinesOids.add(procureLine2.getProcureLineOid());
        em.detach(procureLine);        
        em.detach(procureLine1);
        em.detach(procureLine2);
        requestHeaderRepository.updateProcureLineControllerDetails("userid2","name","team",procureLinesOids); 
        List<ProcureLine> procureLines = this.getProcureLines();
        for(ProcureLine procureLine10 : procureLines){
            Assert.assertEquals("userid2", procureLine10.getMaterialControllerId());
            Assert.assertEquals("name", procureLine10.getMaterialControllerName());
            Assert.assertEquals("team", procureLine10.getMaterialControllerTeam());
        }        
    }
    
    @Test
    public void testFindProcureLineOidsAsList() {
        MaterialHeader materialHeader = new MaterialHeader();
        em.persist(materialHeader);
       
        Material material = new Material();
        material.setMaterialHeader(materialHeader);
        em.persist(material);

        Material material1 = new Material();
        material1.setMaterialHeader(materialHeader);
        em.persist(material1);
        
        List<Material> materials = new ArrayList<Material>();
        materials.add(material1);
        materials.add(material);
        
        materialHeader.setMaterials(materials);
        em.persist(materialHeader);
        
        ProcureLine procureLine = new ProcureLine();
        procureLine.setMaterials(materials);
        em.persist(procureLine);
        material1.setProcureLine(procureLine);
        material.setProcureLine(procureLine);
        em.persist(material1);
        em.persist(material);
        
        Map<Long,Long> resultList = requestHeaderRepository.findProcureLineOidsMap(materialHeader.getMaterialHeaderOid());
        Assert.assertEquals(1, resultList.size());
        
    }
    @Test
    public void testFindFinanceHeaderByMaterialHeaderOid(){
        FinanceHeader financeHeader = new FinanceHeader();
        financeHeader.setProjectId("projectI1d");
        em.persist(financeHeader);
        Material material = new Material();
        material.setFinanceHeader(financeHeader);
        em.persist(material);
        Material material2 = new Material();
        material2.setFinanceHeader(financeHeader);
        em.persist(material2);
        List<Material> materials = new ArrayList<Material>();
        materials.add(material);
        materials.add(material2);
        MaterialHeader materialHeader = new MaterialHeader();
        materialHeader.setMaterials(materials);
        em.persist(materialHeader);
        material.setMaterialHeader(materialHeader);
        em.persist(material);
        material2.setMaterialHeader(materialHeader);
        em.persist(material2);
        
        FinanceHeader financeHeaderResult = requestHeaderRepository.findFinanceHeaderByMaterialHeaderOid(materialHeader.getMaterialHeaderOid());
        Assert.assertThat(financeHeaderResult, is(equalTo(financeHeader)));
    }
    
    @Test
    public void testFindFinanceHeaderByMaterialHeaderOidWithNoResult(){
        FinanceHeader financeHeaderResult =requestHeaderRepository.findFinanceHeaderByMaterialHeaderOid(1L);
        Assert.assertNull(financeHeaderResult);
    }
    @Test
    public void testFindDispatchNote(){
        //insert Records
        //create RequestList
        createRequestList("rahul");
        RequestGroup requestGroup = createRequestGroup(null,"rahul", "projectId");
        //create DispatchNote
        Date dispatchnotedate = DateUtil.getDate(2006, 06, 17);
        createSingleRecord(1,dispatchnotedate, requestGroup.getRequestList());
        Date fromDate = DateUtil.getDate(2006, 06, 15);
        Date toDate =DateUtil.getDate(2006, 06, 31);
        //get ALL
        String[] projectIds = new String[]{"projectId"};
        //find ALL
        String[] whSiteId = new String[]{"whSiteId"};
        List<DispatchNote> dispatchNote = requestHeaderRepository.findDispatchNote(fromDate, toDate, projectIds, whSiteId);
        Assert.assertEquals(1, dispatchNote.size());
    }
    
    private void createSingleRecord(int oid,Date dispatchnotedate, RequestList requestList) {
        /*
        String queryString = "UPDATE DipatchNote SET dispatch_note_oid = "+ oid +", dispatchnotedate = ''";
        Query query = em.createQuery(queryString);
        int updateCount = query.executeUpdate();
        */
        DispatchNote dispatchNote = new DispatchNote();
        dispatchNote.setDispatchNoteDate(dispatchnotedate);
        dispatchNote.setRequestList(requestList);
        requestHeaderRepository.saveDispatchNote(dispatchNote);       
    }

    @Test
    @Ignore
    public void testGetMaterialLinesForWarehouse() throws GloriaApplicationException{
        
        List<Material> materialsBefore = requestHeaderRepository.findAllMaterials();
        Material material = new Material();
        material.setPartNumber("123");
        material.setPartAffiliation("Pa"); 
        material.setPartVersion("pv");
        //material.set.setQuantity(100);
        material.setPartModification("Pm"); 
        material.setPartName("Pn");
        requestHeaderRepository.addMaterial(material);
        List<Material> materials= requestHeaderRepository.findAllMaterials();
        Assert.assertEquals(materialsBefore.size() + 1, materials.size());
        Assert.assertEquals("PM", materials.get(0).getPartModification());
        
        MaterialLine materialLine = new MaterialLine();
        materialLine.setMaterial(materials.get(0));
        materialLine.setReservedUserId("rahul");
        materialLine.setZoneType(ZoneType.TO_STORE);
        materialLine.setWhSiteId("12");
        materials.get(0).getMaterialOID();
        materialLine.setQuantity(100L);
        materialLine.setStatus(MaterialLineStatus.READY_TO_STORE);
        materialLine.setStatusDate(DateUtil.getDateWithZeroTime(DateUtil.getSqlDate()));
        materialLine.setMaterial(material);
        material.getMaterialLine().add(materialLine);
        DeliveryNoteLine deliveryNoteLine = new DeliveryNoteLine();
        DeliveryNoteSubLine deliveryNoteSubLines = new DeliveryNoteSubLine();
        
        TransportLabel transportLabel = new TransportLabel();
        transportLabel.setCode("t");
        transportLabel.setWhSiteId("12");
        deliveryNoteRepository.save(transportLabel);
        List<TransportLabel> transportLabelsSaved = deliveryNoteRepository.findTransportLabelByWhSiteId("12");
        Assert.assertEquals(1, transportLabelsSaved.size());
        Assert.assertEquals("t", transportLabelsSaved.get(0).getCode());
        
        deliveryNoteSubLines.setTransportLabel(transportLabelsSaved.get(0));
        
        List<DeliveryNoteSubLine> deliveryNoteSubLinesList = new ArrayList<DeliveryNoteSubLine>();
        deliveryNoteSubLinesList.add(deliveryNoteSubLines);
        deliveryNoteLine.setDeliveryNoteSubLines(deliveryNoteSubLinesList);
        deliveryNoteLine.setPossibleToReceiveQty(1);
        deliveryNoteRepository.save(deliveryNoteLine);
        DeliveryNoteLine deliveryNoteLine1 = deliveryNoteRepository.findDeliveryNoteLineById(1L);
        Assert.assertEquals(1, deliveryNoteLine1.getPossibleToReceiveQty());
        materialLine.setDeliveryNoteLine(deliveryNoteLine1);
        requestHeaderRepository.addMaterialLine(materialLine);
        List<MaterialLine> materialLinesResults = requestHeaderRepository.findMaterialLinesByReservedUserId("rahul");
        Assert.assertEquals(1, materialLinesResults.size());
        Assert.assertEquals(MaterialLineStatus.READY_TO_STORE,materialLinesResults.get(0).getStatus());
        Assert.assertEquals("t",materialLinesResults.get(0).getDeliveryNoteLine().getDeliveryNoteSubLines().get(0).getTransportLabel().getCode());       

        
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);
        PageObject pageObjectResult = requestHeaderRepository.getMaterialLinesForWarehouse(pageObject,MaterialLineStatus.READY_TO_STORE.name(), false,"12",true,"123",null,"rahul"
                                                             ,"pa","pv", "pm",  "pn");
        Assert.assertEquals(1, pageObjectResult.getCount());
    }
    
    /*  One Request List and Two Request Groups and One Pick List  when queried returns back oneRequestList
     * 
     */
    @Test
    public void testFindRequestListByPicklist(){
        createRequestList("rahul");
        
        PickList pickList = createPickList();
        //verify creation
        List<PickList> pickLists = requestHeaderRepository.findPickListByReservedUserId("rahul");
        Assert.assertTrue(pickLists.size()>0);
        
        createRequestGroup(pickList,"rahul", null);
        createRequestGroup(pickList,"rahul", null);

        List<RequestList> requestLists = requestHeaderRepository.findRequestListByUserId("rahul", RequestListStatus.READY_TO_SHIP.toString());

        Assert.assertTrue(requestHeaderRepository.findAllRequestGroupsByRequestListId(requestHeaderRepository.findRequestListByUserId("rahul", RequestListStatus.READY_TO_SHIP.toString()).get(0).getRequestListOid()).size()>0);
        List<RequestGroup> requestGroups = requestHeaderRepository.findAllRequestGroupsByRequestListId(requestLists.get(0).getRequestListOid());
        Assert.assertEquals(2,requestGroups.size());
        
        //ACT
        List<RequestList> requestListResult = requestHeaderRepository.findRequestListByPicklist(pickList.getPickListOid());
        
        //Assert
        Assert.assertEquals(1,requestListResult.size());   
        Assert.assertEquals("rahul",requestListResult.get(0).getRequestUserId());   
    }
    
    /*  Two Request List and Two Request Groups and One Pick List  when queried returns back two Request List
     * 
     */
    @Test
    public void testFindRequestListByPicklistTwo(){
        createRequestList("rahul");
        createRequestList("rahul1");
        
        PickList pickList = createPickList();
        //verify creation
        List<PickList> pickLists = requestHeaderRepository.findPickListByReservedUserId("rahul");
        Assert.assertTrue(pickLists.size()>0);
        
        //create two different request groups and assign them to two different request Lists and the same pick list
        
        createRequestGroup(pickList,"rahul1", null);
        createRequestGroup(pickList,"rahul", null);

        List<RequestList> requestLists = requestHeaderRepository.findRequestListByUserId("rahul", RequestListStatus.READY_TO_SHIP.toString());

        Assert.assertTrue(requestHeaderRepository.findAllRequestGroupsByRequestListId(requestHeaderRepository.findRequestListByUserId("rahul", RequestListStatus.READY_TO_SHIP.toString()).get(0).getRequestListOid()).size()>0);
        List<RequestGroup> requestGroups = requestHeaderRepository.findAllRequestGroupsByRequestListId(requestLists.get(0).getRequestListOid());
        Assert.assertEquals(1,requestGroups.size()); 
        
        //ACT
        List<RequestList> requestListResult = requestHeaderRepository.findRequestListByPicklist(pickList.getPickListOid());
        
        //Assert now two are retuned
        Assert.assertEquals(2,requestListResult.size());   
        // cannot assert request id as we do not know which one it is rahul or rahul1
        //Assert.assertEquals("rahul",requestListResult.get(0).getRequestUserId());   
    }
    /*
     * what happens when things are null and empty
     */
    @Test
    public void testGetTransactionStoresReportData(){
        ReportWarehouseTransactionDTO reportWarehouseTransactionDTO = new ReportWarehouseTransactionDTO();
        Date fromDate = new Date();
        Date toDate = new Date();        
        requestHeaderRepository.getTransactionStoresReportData( reportWarehouseTransactionDTO,  fromDate,  toDate);
        //assert that this just works
    }
    /*
     * query returned for all warehouses
     */
    @Test
    public void testGetTransactionStoresReportData1() throws GloriaApplicationException{
        createTransactionStoresReportData( "warehouseId");
        createTransactionStoresReportData( "warehouseIdNotAvailable");
        ReportWarehouseTransactionDTO reportWarehouseTransactionDTO = new ReportWarehouseTransactionDTO();
        reportWarehouseTransactionDTO.setWarehouse(new String[]{});
        Date fromDate =  DateUtils.addMinutes(new Date(),-1);
        Date toDate = DateUtils.addMinutes(new Date(),+1);  
        List<Tuple> tuple =  requestHeaderRepository.getTransactionStoresReportData(reportWarehouseTransactionDTO, fromDate, toDate);
        Assert.assertEquals(2, tuple.size());}
    
    /*
     * query returned for specific warehouse
     */
    @Test
    public void testGetTransactionStoresReportData2() throws GloriaApplicationException{
        createTransactionStoresReportData( "warehouseId");
        createTransactionStoresReportData( "warehouseIdNotAvailable");
        ReportWarehouseTransactionDTO reportWarehouseTransactionDTO = new ReportWarehouseTransactionDTO();
        reportWarehouseTransactionDTO.setWarehouse(new String[]{"warehouseId"});
        Date fromDate =  DateUtils.addMinutes(new Date(),-1);
        Date toDate = DateUtils.addMinutes(new Date(), +1);  
        List<Tuple> tuple =  requestHeaderRepository.getTransactionStoresReportData(reportWarehouseTransactionDTO, fromDate, toDate);
        Assert.assertEquals(1, tuple.size());
    }
    
    /*
     * query returned for specific storeRoom
     */
    @Test
    public void testGetTransactionStoresReportData3() throws GloriaApplicationException{
        createTransactionStoresReportData( "warehouseId");
        createTransactionStoresReportData( "warehouseIdNotAvailable");
        ReportWarehouseTransactionDTO reportWarehouseTransactionDTO = new ReportWarehouseTransactionDTO();
        reportWarehouseTransactionDTO.setWarehouse(new String[]{"warehouseId"});
        reportWarehouseTransactionDTO.setStorageRoom(new String[]{"warehouseId"});
        Date fromDate =  DateUtils.addMinutes(new Date(),-1);
        Date toDate = DateUtils.addMinutes(new Date(), +1);  
        List<Tuple> tuple =  requestHeaderRepository.getTransactionStoresReportData(reportWarehouseTransactionDTO, fromDate, toDate);
        Assert.assertEquals(1, tuple.size());
    }


    private void createTransactionStoresReportData(String warehouseId) throws GloriaApplicationException {
        FinanceHeader financeHeader = new FinanceHeader();
        financeHeader.setProjectId("projectId");
        em.persist(financeHeader);
        
        MaterialPartAlias materialPartAlias = new MaterialPartAlias();
        materialPartAlias.setPartNumber("partNumber");
        em.persist(materialPartAlias);
        List<MaterialPartAlias> partAliass = new ArrayList<MaterialPartAlias>();
        partAliass.add(materialPartAlias);
        MaterialHeader materialHeader = new MaterialHeader();
        materialHeader.setMtrlRequestId("mtrlRequestId");
        em.persist(materialHeader);
        
        Material material = new Material();
        material.setFinanceHeader(financeHeader);
        material.setPartAlias(partAliass);
        material.setMaterialHeader(materialHeader);
        em.persist(material);
        
        List<Material> materials = new ArrayList<Material>();
        materials.add(material);
        
        materialPartAlias.setMaterial(material);
        materialHeader.setMaterials(materials);
        em.persist(materialHeader);
        MaterialLineStatusTime materialLineStatusTime = new MaterialLineStatusTime();
        materialLineStatusTime.setStoredStorageRoom(warehouseId);
        materialLineStatusTime.setReceivedTime(new Timestamp(System.currentTimeMillis()));
        materialLineStatusTime.setStoredTime(new Timestamp(System.currentTimeMillis()));
        materialLineStatusTime.setStoredQty(1L);
        MaterialLine materialLine = new MaterialLine();
        materialLine.setMaterial(material);
        materialLine.setMaterialLineStatusTime(materialLineStatusTime);
        materialLine.setOrderNo("orderNo");
        materialLine.setMaterialOwner(material);
        materialLine.setWhSiteId(warehouseId);
        materialLine.setQuantity(5L);
        em.persist(materialLine);
/*
        columnSelections.add(root.get("material").get("partAffiliation"));
        columnSelections.add(root.get("material").get("partNumber"));
        columnSelections.add(root.get("material").get("partVersion"));
        columnSelections.add(root.get("material").get("partName"));
        columnSelections.add(root.get("material").get("partModification"));
       /* columnSelections.add(root.get("material").get("partAlias").get("partNumber"));
        columnSelections.add(root.get("materialLineStatusTime").get("storedStorageRoom"));
        columnSelections.add(root.get("materialLineStatusTime").get("storedBinLocation"));
        columnSelections.add(root.get("material").get("mailFormId"));*/
    }

    private RequestGroup createRequestGroup(PickList pickList,String requestListUserId, String projectId) {
        RequestGroup group1 = new RequestGroup(); 
        group1.setPickList(pickList);
        group1.setProjectId(projectId);
        List<RequestList> requestLists = requestHeaderRepository.findRequestListByUserId(requestListUserId, RequestListStatus.READY_TO_SHIP.toString());
        Assert.assertEquals(1, requestLists.size());
        group1.setRequestList(requestLists.get(0));
        return requestHeaderRepository.saveRequestGroup(group1);
    }

    private void createRequestList(String requestUserId) {
        RequestList requestList = new RequestList();
        requestList.setRequestUserId(requestUserId);
        requestList.setStatus(RequestListStatus.READY_TO_SHIP);
        requestList.setWhSiteId("whSiteId");
        requestHeaderRepository.save(requestList);
    }

    private PickList createPickList() {
        PickList pickList = new PickList();
        pickList.setReservedUserId("rahul");
        requestHeaderRepository.savePickList(pickList);
        return pickList;
    }

    /*
     * 1 minute before and 1 minute after and there are results
    */
    @Test
    public void testGetTransactionShipmentReportData2() {
        Date dateForTestData = new Date(); 
        Date fromDate = DateUtils.addMinutes(new Date(), -1);
        Date toDate = DateUtils.addMinutes(new Date(), +1);
        List<Tuple> tuples = getTransactionReportShipmentReport(fromDate, toDate, new String[] { "warehouseID" }, dateForTestData);
        Assert.assertEquals(2, tuples.size());
        this.assertFields(tuples.get(0));
    } 

    private void assertFields(Tuple tuple) {
        for (int i = 0; i < 12; i++) {
            Assert.assertNotNull(tuple.get(i));
        }
    }

    /*
     * 1 minute before to 4 hours later  but with  invalid warehouseid and no results
     */
    @Test
    public void testGetTransactionShipmentReportData3() {
        Date dateForTestData = new Date(); 
        Date fromDate = DateUtils.addMinutes(dateForTestData, -1);
        Date toDate = DateUtils.addHours(dateForTestData, 4);
        List<Tuple> tuples = getTransactionReportShipmentReport(fromDate, toDate, new String[] { "warehouseIDINVALID"}, dateForTestData);
        Assert.assertEquals(0, tuples.size());
    }

    /*
     * 1 minute before and 1 minute after empty warehouseid and results
     */
    @Test
    public void testGetTransactionShipmentReportData4() {
        Date dateForTestData = new Date();        
        Date fromDate = DateUtils.addDays(new Date(), -1);
        Date toDate = DateUtils.addDays(new Date(), +1);
        List<Tuple> tuples = getTransactionReportShipmentReport(fromDate, toDate, new String[] {"warehouseID"},  dateForTestData);
        Assert.assertEquals(2, tuples.size());
        this.assertFields(tuples.get(0));
    }
    
    private List<Tuple> getTransactionReportShipmentReport(Date fromDate, Date toDate, String[] warehouseID, Date dateForTestData) {
        createTestData("rahul",DateUtils.addHours(dateForTestData, 1) , "warehouseID");
        createTestData("rahul1",DateUtils.addHours(dateForTestData, 2),"warehouseID");
        
        //Invalid Warehouse ID
        createTestData("rahul3", DateUtils.addHours(dateForTestData, 3), "INVALID");
        //createTestData("rahul1",DateUtils.addDays(new Date(), +4), "warehouseID");
        //
        //from yesterday to now
        ReportWarehouseTransactionDTO reportWarehouseTransactionDTO= new ReportWarehouseTransactionDTO();
        reportWarehouseTransactionDTO.setWarehouse(warehouseID);
        List<Tuple> tuples = requestHeaderRepository.getTransactionShipmentReportData(reportWarehouseTransactionDTO, fromDate , toDate, null);
        return tuples;
    }

    private void createTestData(String requestUserId, Date dispatchNoteDate, String warehouseId) {
        RequestList requestList = new RequestList();
        requestList.setWhSiteId(warehouseId);
        requestList.setRequestUserId(requestUserId);
        requestList.setRequiredDeliveryDate(new Date());
        requestList.setStatus(RequestListStatus.SHIPPED);
        requestList.setShipmentType(ShipmentType.SHIPMENT);
        requestList.setDeliveryAddressId("deliveryAddressID");
        requestList.setDeliveryAddressName("deliveryAddressName");
        
        requestHeaderRepository.save(requestList);
        List<RequestList> requestListResult = requestHeaderRepository.findRequestListByUserId(requestUserId, "SHIPPED");
        Assert.assertEquals(1, requestListResult.size());
        Assert.assertNotNull(requestListResult.get(0));
        Assert.assertEquals("deliveryAddressName", requestListResult.get(0).getDeliveryAddressName());
        //create DispatchNote
        
        DispatchNote dispatchNote = new DispatchNote();
        dispatchNote.setDispatchNoteDate(dispatchNoteDate);
        dispatchNote.setWeight("weight");
        dispatchNote.setHeight("height");
        dispatchNote.setCarrier("carrier");
        dispatchNote.setTrackingNo("trackingNo");
        dispatchNote.setDeliveryDate(new Date());
        dispatchNote.setDispatchNoteNo("dispatchNoteNo");
        requestHeaderRepository.saveDispatchNote(dispatchNote);
        List<DispatchNote> dispatchNoteResult = requestHeaderRepository.findDispatchNote(DateUtils.addMinutes(dispatchNoteDate, -1), DateUtils.addMinutes(dispatchNoteDate, +1), new String[]{}, new String[]{});
        Assert.assertEquals(1, dispatchNoteResult.size());
        
        // attach dispach note to RequestList
        requestListResult.get(0).setDispatchNote(dispatchNoteResult.get(0));
        requestHeaderRepository.save(requestListResult.get(0));
        //update DispatchNote with RequestList
        dispatchNote.setRequestList(requestListResult.get(0));
        requestHeaderRepository.saveDispatchNote(dispatchNote);
    }   
    
    @Test
    public void testUpdateMaterialLinesStatus() {
        MaterialHeader materialHeader = new MaterialHeader();
        em.persist(materialHeader);
        Material material = new Material();
        material.setMaterialHeader(materialHeader);
        em.persist(material);
        MaterialLine materialLine = new MaterialLine();
        materialLine.setStatus(MaterialLineStatus.BLOCKED);
        materialLine.setMaterial(material);
        em.persist(materialLine);
        List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
        materialLines.add(materialLine);
        material.setMaterialLine(materialLines);
        em.persist(material);
        List<Material> materials = new ArrayList<Material>();
        materials.add(material);
        materialHeader.setMaterials(materials);
        em.persist(materialHeader);
        Long materialHeaderOid = materialHeader.getMaterialHeaderOid();
        Long id = materialLine.getMaterialLineOID();
        em.detach(materialHeader);
        em.detach(material);
        em.detach(materialLine);
        em.flush();
        em.clear();
        
        requestHeaderRepository.updateMaterialLinesStatus(materialHeaderOid, MaterialLineStatus.BLOCKED, MaterialLineStatus.DEVIATED);
        //em.refresh(materialLine);
        //Assert.assertEquals(MaterialLineStatus.CREATED, materialLine.getStatus());

        //List<MaterialHeader> materialHeaders = new ArrayList<MaterialHeader>();
        //materialHeaders.add(materialHeader);
        List<MaterialLine> materialsResult = this.getMaterialLine(id);
        Assert.assertEquals(MaterialLineStatus.DEVIATED, materialsResult.get(0).getStatus());
        /*
        Assert.assertEquals(1, materialsResult.size());
        Assert.assertEquals(MaterialLineStatus.CREATED, materialsResult.get(0).getMaterialLine().get(0).getStatus());*/
        
    }
    
    @Test
    public void testUpdateMaterialLinesStatus1() {
        MaterialHeader materialHeader = new MaterialHeader();
        em.persist(materialHeader);
        Material material = new Material();
        material.setMaterialHeader(materialHeader);
        em.persist(material);
        MaterialLine materialLine = new MaterialLine();
        materialLine.setStatus(MaterialLineStatus.CREATED);
        materialLine.setMaterial(material);
        em.persist(materialLine);
        List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
        materialLines.add(materialLine);
        material.setMaterialLine(materialLines);
        em.persist(material);
        List<Material> materials = new ArrayList<Material>();
        materials.add(material);
        materialHeader.setMaterials(materials);
        em.persist(materialHeader);
        Long id = materialLine.getMaterialLineOID();
        em.detach(materialHeader);
        em.detach(material);
        em.detach(materialLine);
        em.flush();
        em.clear();
        List<Long> materialList = new ArrayList<Long>();
        materialList.add(material.getMaterialOID());
        
        requestHeaderRepository.updateMaterialLinesStatus(materialList, MaterialLineStatus.BLOCKED);
        //em.refresh(materialLine);
        //Assert.assertEquals(MaterialLineStatus.CREATED, materialLine.getStatus());

        //List<MaterialHeader> materialHeaders = new ArrayList<MaterialHeader>();
        //materialHeaders.add(materialHeader);
        List<MaterialLine> materialsResult = this.getMaterialLine(id);
        Assert.assertEquals(MaterialLineStatus.BLOCKED, materialsResult.get(0).getStatus());
        /*
        Assert.assertEquals(1, materialsResult.size());
        Assert.assertEquals(MaterialLineStatus.CREATED, materialsResult.get(0).getMaterialLine().get(0).getStatus());*/
        
    }    

    @Test
    public void testFindProcureLineSumQuantities() throws GloriaApplicationException{
        // create Material , MateiralLines and ProcureLine
        ProcureLine procureLine = new ProcureLine();
        em.persist(procureLine);
        Assert.assertTrue(procureLine.getProcureLineOid()>0);
        MaterialLine materialLine1 = new MaterialLine();
        materialLine1.setQuantity(5L);
        em.persist(materialLine1);
        Assert.assertTrue(materialLine1.getMaterialLineOID()>0);
        MaterialLine materialLine2 = new MaterialLine();
        materialLine2.setQuantity(5L);
        em.persist(materialLine2);
        Assert.assertTrue(materialLine2.getMaterialLineOID()>0);
        
        MaterialLine materialLine3 = new MaterialLine();
        materialLine3.setQuantity(5L);
        em.persist(materialLine3);
        Assert.assertTrue(materialLine3.getMaterialLineOID()>0);
        MaterialLine materialLine4 = new MaterialLine();
        materialLine4.setQuantity(5L);
        em.persist(materialLine4);
        
        Material material = new Material();
        material.setMaterialType(MaterialType.USAGE);
        material.setProcureLine(procureLine);
        List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
        materialLines.add(materialLine1);
        materialLines.add(materialLine2);
        material.setMaterialLine(materialLines);
        em.persist(material);
        
        Material material1 = new Material();
        material1.setMaterialType(MaterialType.ADDITIONAL);
        material1.setProcureLine(procureLine);
        List<MaterialLine> materialLines2 = new ArrayList<MaterialLine>();
        materialLines.add(materialLine3);
        materialLines.add(materialLine4);
        material1.setMaterialLine(materialLines2);
        em.persist(material1);
        List<Material> materials = new ArrayList<Material>();
        materials.add(material);
        materials.add(material1);
        procureLine.setMaterials(materials);
        em.persist(procureLine);
        
        materialLine1.setMaterial(material);
        em.persist(materialLine1);
        materialLine2.setMaterial(material);
        
        materialLine3.setMaterial(material1);
        em.persist(materialLine3);
        materialLine4.setMaterial(material1);
        em.persist(materialLine3);
        List<Long> materialLineOids = new ArrayList<Long>();
        materialLineOids.add(material.getMaterialOID());
        materialLineOids.add(material1.getMaterialOID());
        Map<MaterialType,Long> tuples = requestHeaderRepository.findMaterialLineQuantities(materialLineOids);
        Assert.assertEquals(2, tuples.size());
    }
    
    private List<ProcureLine> getProcureLines() {
        return em.createQuery("SELECT c FROM ProcureLine c").getResultList();
    }
    
    private List<MaterialLine> getMaterialLine(Long oid) {
        return em.createQuery("SELECT c FROM MaterialLine c where c.materialLineOID =" +oid.toString()).getResultList();
    }
    
    @Test
    public void testGetMaterialLinesForOrderLine(){
        Date outboundStartDate = new Date();

        OrderLine orderLine = new OrderLine();
        em.persist(orderLine);

        MaterialHeaderVersion materialHeaderVersion1 = new MaterialHeaderVersion();
        materialHeaderVersion1.setOutboundStartDate(outboundStartDate);
        em.persist(materialHeaderVersion1);        

        MaterialHeader materialHeader1 = new MaterialHeader();
        materialHeader1.setAccepted(materialHeaderVersion1);
        materialHeaderVersion1.setMaterialHeader(materialHeader1);
        em.persist(materialHeader1);
        
        MaterialHeaderVersion materialHeaderVersion2 = new MaterialHeaderVersion();
        materialHeaderVersion2.setOutboundStartDate(DateUtil.getNextDate(outboundStartDate));
        em.persist(materialHeaderVersion2);        

        MaterialHeader materialHeader2 = new MaterialHeader();
        materialHeader2.setAccepted(materialHeaderVersion2);
        materialHeaderVersion2.setMaterialHeader(materialHeader2);
        em.persist(materialHeader2);
        
        Material material1 = new Material();
        material1.setMaterialType(MaterialType.USAGE);
        material1.setMaterialHeader(materialHeader1);
        material1.setOrderLine(orderLine);
        em.persist(material1);
        
        Material material2 = new Material();
        material2.setMaterialType(MaterialType.ADDITIONAL);
        material2.setOrderLine(orderLine);
        em.persist(material2);
               
        Material material3 = new Material();
        material3.setMaterialType(MaterialType.USAGE);
        material3.setMaterialHeader(materialHeader2);
        material3.setOrderLine(orderLine);
        em.persist(material3);
        em.flush();
        
        List<Material> materialsForOrderLine=requestHeaderRepository.getMaterialsForOrderLine(orderLine.getOrderLineOID());
        Assert.assertEquals(3, materialsForOrderLine.size());
        Material firstMaterial=materialsForOrderLine.get(0);
        Assert.assertEquals(MaterialType.ADDITIONAL, firstMaterial.getMaterialType());
        Material secondMaterial=materialsForOrderLine.get(1);
        Assert.assertEquals(MaterialType.USAGE, secondMaterial.getMaterialType());
        Assert.assertEquals(DateUtil.getNextDate(outboundStartDate), secondMaterial.getMaterialHeader().getAccepted().getOutboundStartDate());        
    }
}
