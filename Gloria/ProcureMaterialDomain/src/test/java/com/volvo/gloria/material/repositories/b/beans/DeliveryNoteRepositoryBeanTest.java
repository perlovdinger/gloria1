package com.volvo.gloria.material.repositories.b.beans;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import com.volvo.gloria.common.c.Domain;
import com.volvo.gloria.common.c.dto.reports.ReportWarehouseTransactionDTO;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNote;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.MaterialLineStatusTime;
import com.volvo.gloria.procurematerial.d.entities.PartAlias;
import com.volvo.gloria.procurematerial.d.entities.PickList;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestlist.RequestListStatus;
import com.volvo.gloria.procurematerial.d.type.receive.ReceiveType;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class DeliveryNoteRepositoryBeanTest extends AbstractTransactionalTestCase {

    private static final String WH_SITE_ID = "whSiteId";
    @Inject
    private DeliveryNoteRepository deliveryNoteRepository;

    public DeliveryNoteRepositoryBeanTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }

    EntityManager em = null;

    @PersistenceContext(unitName = "ProcureMaterialDomainPU")
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
    
    /*
     * when a specic warehouseid is set then  a result is returned for that warehouse
     */
    @Test
    public void testGetTransactionReturnsReportData(){
        ReportWarehouseTransactionDTO reportWarehouseTransactionDTO = new  ReportWarehouseTransactionDTO(); 
        Date fromDate = new Date();
        Date toDate = DateUtils.addMinutes(new Date(),1);
        createDataForTransactionReturnsReport(WH_SITE_ID);
        createDataForTransactionReturnsReport("warehouseIdNotValid");
        reportWarehouseTransactionDTO.setWarehouse(new String[]{WH_SITE_ID});
        List<Tuple> tuples = deliveryNoteRepository.getTransactionReturnsReportData( reportWarehouseTransactionDTO,  fromDate,  toDate, null) ;
        Assert.assertEquals(1, tuples.size());
    }   
    
    /*
     * when a empty is set then  a result is returned for all warehouses
     */
    @Test
    public void testGetTransactionReturnsReportData1(){
        ReportWarehouseTransactionDTO reportWarehouseTransactionDTO = new  ReportWarehouseTransactionDTO(); 
        Date fromDate = new Date();
        Date toDate = DateUtils.addMinutes(new Date(),1);
        createDataForTransactionReturnsReport(WH_SITE_ID);
        createDataForTransactionReturnsReport("warehouseIdNotValid");
        reportWarehouseTransactionDTO.setWarehouse(new String[]{});
        List<Tuple> tuples = deliveryNoteRepository.getTransactionReturnsReportData( reportWarehouseTransactionDTO,  fromDate,  toDate, null) ;
        Assert.assertEquals(2, tuples.size());
    }
    
    /*
     * when a time range is before the time then an empty result is returned
     */
    @Test
    public void testGetTransactionReturnsReportData2(){
        ReportWarehouseTransactionDTO reportWarehouseTransactionDTO = new  ReportWarehouseTransactionDTO(); 
        Date fromDate = DateUtils.addMinutes(new Date(),-100);
        Date toDate = DateUtils.addMinutes(new Date(),-1);
        createDataForTransactionReturnsReport(WH_SITE_ID);
        createDataForTransactionReturnsReport("warehouseIdNotValid");
        reportWarehouseTransactionDTO.setWarehouse(new String[]{});
        List<Tuple> tuples = deliveryNoteRepository.getTransactionReturnsReportData( reportWarehouseTransactionDTO,  fromDate,  toDate, null) ;
        Assert.assertEquals(0, tuples.size());
    }
    
    /*
     * when a time range is after the time then an empty result is returned
     */
    @Test
    public void testGetTransactionReturnsReportData3(){
        ReportWarehouseTransactionDTO reportWarehouseTransactionDTO = new  ReportWarehouseTransactionDTO(); 
        Date fromDate = DateUtils.addMinutes(new Date(),+1);
        Date toDate = DateUtils.addMinutes(new Date(),+100);
        createDataForTransactionReturnsReport(WH_SITE_ID);
        createDataForTransactionReturnsReport("warehouseIdNotValid");
        reportWarehouseTransactionDTO.setWarehouse(new String[]{});
        List<Tuple> tuples = deliveryNoteRepository.getTransactionReturnsReportData( reportWarehouseTransactionDTO,  fromDate,  toDate, null) ;
        Assert.assertEquals(0, tuples.size());
    }
    
    private void createDataForTransactionReturnsReport(String whSiteId) {
        DeliveryNote deliveryNote = new DeliveryNote();
        deliveryNote.setWhSiteId(whSiteId);
        deliveryNote.setDeliveryNoteDate(new Date());
        deliveryNote.setReceiveType(ReceiveType.RETURN);
        em.persist(deliveryNote);
        DeliveryNoteLine deliveryNoteLine = new DeliveryNoteLine();
        deliveryNoteLine.setDeliveryNote(deliveryNote);
        em.persist(deliveryNoteLine);
        List<DeliveryNoteLine> deliveryNoteLines = new ArrayList<DeliveryNoteLine>();
        deliveryNote.setDeliveryNoteLine(deliveryNoteLines);
        em.persist(deliveryNote);
        
    }
    
    @Test
    public void testGetTransactionPicksReportData() throws GloriaApplicationException {
        createDataForPicksReportWithPartAlias();
        ReportWarehouseTransactionDTO reportWarehouseTransactionDTO = new ReportWarehouseTransactionDTO();
        reportWarehouseTransactionDTO.setAction("ALL_PICKS");
        List<Tuple> tuples = deliveryNoteRepository.getTransactionPicksReportData(reportWarehouseTransactionDTO, DateUtils.addMinutes(new Date(), -1),
                                                                                  DateUtils.addMinutes(new Date(), 1));
        Assert.assertEquals(1, tuples.size());
        Assert.assertEquals("orderNo", tuples.get(0).get(0).toString());
        Assert.assertEquals("projectId", tuples.get(0).get(1).toString());
        Assert.assertEquals("requestUserId", tuples.get(0).get(4).toString());
        Assert.assertEquals("code", tuples.get(0).get(12).toString());
        Assert.assertEquals("PARTMODIFICATION", tuples.get(0).get(17).toString());
        //Assert.assertEquals("MACK", tuples.get(0).get(18).toString());
    }
    private void createDataForPicksReportWithPartAlias() throws GloriaApplicationException {
        Material m= createDataForPicksReport();
        PartAlias partAlias = new PartAlias();
        partAlias.setDomain(Domain.MACK);
        em.persist(partAlias);
        m.getProcureLine().setPartAlias(partAlias);
        em.persist(m.getProcureLine());
    }
    /*
     * date range in past does not work
     */
    @Test
    public void testGetTransactionPicksReportData1() throws GloriaApplicationException {
        createDataForPicksReportWithPartAlias();
        ReportWarehouseTransactionDTO reportWarehouseTransactionDTO = new ReportWarehouseTransactionDTO();
        reportWarehouseTransactionDTO.setAction("ALL_PICKS");
        List<Tuple> tuples = deliveryNoteRepository.getTransactionPicksReportData(reportWarehouseTransactionDTO, DateUtils.addMinutes(new Date(), -100),
                                                                                  DateUtils.addMinutes(new Date(), -1));
        Assert.assertEquals(0, tuples.size());
    }
    
    /*
     * date range in future does not work
     */
    @Test
    public void testGetTransactionPicksReportData2() throws GloriaApplicationException {
        createDataForPicksReportWithPartAlias();
        ReportWarehouseTransactionDTO reportWarehouseTransactionDTO = new ReportWarehouseTransactionDTO();
        reportWarehouseTransactionDTO.setAction("ALL_PICKS");
        List<Tuple> tuples = deliveryNoteRepository.getTransactionPicksReportData(reportWarehouseTransactionDTO, DateUtils.addMinutes(new Date(), +1),
                                                                                  DateUtils.addMinutes(new Date(), +100));
        Assert.assertEquals(0, tuples.size());
    }
    /*
     * specific warehouse id should work
     */
    @Test
    public void testGetTransactionPicksReportData3() throws GloriaApplicationException {
        createDataForPicksReportWithPartAlias();

        ReportWarehouseTransactionDTO reportWarehouseTransactionDTO = new ReportWarehouseTransactionDTO();
        reportWarehouseTransactionDTO.setAction("ALL_PICKS");
        reportWarehouseTransactionDTO.setWarehouse(new String[]{WH_SITE_ID});
        List<Tuple> tuples = deliveryNoteRepository.getTransactionPicksReportData(reportWarehouseTransactionDTO, DateUtils.addMinutes(new Date(), -1),
                                                                                  DateUtils.addMinutes(new Date(), 1));
        Assert.assertEquals(1, tuples.size());
        Assert.assertEquals("orderNo", tuples.get(0).get(0).toString());

        Assert.assertEquals("projectId", tuples.get(0).get(1).toString());
        Assert.assertEquals("requestUserId", tuples.get(0).get(4).toString());
    }

    private Material createDataForPicksReport() throws GloriaApplicationException {
        MaterialLine materialLine = new MaterialLine();
        materialLine.setQuantity(1L);
        materialLine.setStatus(MaterialLineStatus.READY_TO_SHIP);
        materialLine.setWhSiteId(WH_SITE_ID);
        materialLine.setOrderNo("orderNo");
        em.persist(materialLine);
        RequestList requestList = new RequestList();
        requestList.setStatus(RequestListStatus.PICK_COMPLETED);
        requestList.setRequestUserId("requestUserId");
        em.persist(requestList);
        Assert.assertTrue(requestList.getRequestListOid() > 0);
        RequestGroup requestGroup = new RequestGroup();
        requestGroup.setRequestList(requestList);
        em.persist(requestGroup);
        List<RequestGroup> requestGroups = new ArrayList<RequestGroup>();
        requestGroups.add(requestGroup);
        requestList.setRequestGroups(requestGroups);
        em.persist(requestList);

        List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
        materialLines.add(materialLine);
        DeliveryNoteLine deliveryNoteLine = new DeliveryNoteLine();
        deliveryNoteLine.setDangerousGoodsName("rahul");
        deliveryNoteLine.setMaterialLine(materialLines);
        em.persist(deliveryNoteLine);
        materialLine.setDeliveryNoteLine(deliveryNoteLine);
        em.persist(materialLine);
        requestGroup.setMaterialLines(materialLines);
        em.persist(requestGroup);
        
        //Material materialOwner = new Material();
        FinanceHeader financeHeader = new FinanceHeader();
        financeHeader.setProjectId("projectId");
        em.persist(financeHeader);
        materialLine.setRequestGroup(requestGroup);

        MaterialLineStatusTime materialLineStatusTime = new MaterialLineStatusTime();
        materialLineStatusTime.setPickedTime(new Timestamp(System.currentTimeMillis()));
        materialLine.setMaterialLineStatusTime(materialLineStatusTime);
        Material material = new Material();
        materialLine.setMaterialOwner(material);
        material.setFinanceHeader(financeHeader);
        material.setPartModification("partModification");
        
        ProcureLine procureLine = new ProcureLine();
      procureLine.setBuyerCode("buyercode");
        /*
        PartAlias partAlias = new PartAlias();
        partAlias.setDomain(Domain.MACK);
        em.persist(partAlias);
        procureLine.setPartAlias(partAlias);*/
      em.persist(procureLine);
        MaterialHeader materialHeader = new MaterialHeader();
        material.setMaterialHeader(materialHeader);
        em.persist(materialHeader);
      material.setProcureLine(procureLine);
        em.persist(material);
        List<Material> materials = new ArrayList<Material>();
        materials.add(material);
        materialHeader.setMaterials(materials);
        materialLine.setMaterial(material);
        PickList pickList = new PickList();
        pickList.setCode("code");
        em.persist(pickList);
        materialLine.setPickList(pickList);
        em.persist(materialLine);
        material.setMaterialLine(materialLines);
        List<DeliveryNoteLine> deliveryNoteLines = this.getAllDeliveryNoteLines();
        Assert.assertEquals(1, deliveryNoteLines.size());
        Assert.assertTrue(deliveryNoteLines.get(0).getMaterialLine().size() > 0);
        Assert.assertTrue(deliveryNoteLines.get(0).getMaterialLine().get(0).getMaterialLineOID() > 0);
        return material;
        // Assert.assertTrue(deliveryNoteLines.get(0).getMaterialLine().get(0).getRequestGroup().getRequestGroupOid()>0);
    }

    private List<DeliveryNoteLine> getAllDeliveryNoteLines() {
        return em.createQuery("SELECT c FROM DeliveryNoteLine c").getResultList();
    }

    @Test
    public void testFindDeliveryNoteByTypeAndProject() {
        // create data
        createData(ReceiveType.REGULAR, DateUtil.getDate(2006, 06, 17), WH_SITE_ID, "projectID");
        createData(ReceiveType.RETURN, DateUtil.getDate(2006, 06, 17), WH_SITE_ID, "projectID");
        createData(ReceiveType.RETURN, DateUtil.getDate(2006, 06, 17), WH_SITE_ID, "projectID");
        createData(ReceiveType.RETURN_TRANSFER, DateUtil.getDate(2006, 06, 17), WH_SITE_ID, "projectID");
        // Invalid records not returned in result set
        createData(ReceiveType.RETURN_TRANSFER, DateUtil.getDate(20015, 06, 17), WH_SITE_ID, "projectID");
        createData(ReceiveType.RETURN_TRANSFER, DateUtil.getDate(2006, 06, 17), "whSiteIdInvalid", "projectID");
        createData(ReceiveType.RETURN_TRANSFER, DateUtil.getDate(2006, 06, 17), WH_SITE_ID, "projectIDInvalid");

        // Act
        Date fromDate = DateUtil.getDate(2006, 06, 15);
        Date toDate = DateUtil.getDate(2006, 06, 31);
        String[] projectIds = new String[] { "projectID" };
        String[] whSiteId = new String[] { WH_SITE_ID };
        List<Tuple> deliveryNotes = deliveryNoteRepository.findDeliveryNoteByTypeAndProject(fromDate, toDate, projectIds, whSiteId);
        // Assert
        Assert.assertEquals(3, deliveryNotes.size());
        Long regular = 0L;
        Long returnCount = 0L;
        Long returnTransferCount = 0L;
        for (Tuple deliveryNote : deliveryNotes) {
            if (ReceiveType.REGULAR.equals(deliveryNote.get(1))) {
                regular = (Long) deliveryNote.get(0);
            } else if (ReceiveType.RETURN.equals(deliveryNote.get(1))) {
                returnCount = (Long) deliveryNote.get(0);
            } else if (ReceiveType.RETURN_TRANSFER.equals(deliveryNote.get(1))) {
                returnTransferCount = (Long) deliveryNote.get(0);
            }
        }
        Assert.assertEquals(1L, regular.longValue());
        Assert.assertEquals(2L, returnCount.longValue());
        Assert.assertEquals(1L, returnTransferCount.longValue());
    }

    private void createData(ReceiveType receiveType, Date date, String whSiteId, String projectId) {
        DeliveryNote deliveryNote = new DeliveryNote();
        deliveryNote.setDeliveryNoteDate(date);
        deliveryNote.setWhSiteId(whSiteId);
        deliveryNote.setReceiveType(receiveType);
        deliveryNote.setDeliveryNoteNo("Del1");
        deliveryNoteRepository.save(deliveryNote);

        List<DeliveryNote> deliveryNoteTest = deliveryNoteRepository.findAll();
        Assert.assertNotNull(deliveryNoteTest);
        Assert.assertTrue(deliveryNoteTest.size() > 0);
        DeliveryNote deliveryNoteTo = null;
        for (DeliveryNote deliveryNote1 : deliveryNoteTest) {
            if (deliveryNote1.getReceiveType().equals(receiveType)) {
                deliveryNoteTo = deliveryNote1;
            }
        }

        DeliveryNoteLine deliveryNoteLine = new DeliveryNoteLine();
        deliveryNoteLine.setProjectId(projectId);
        deliveryNoteLine.setDeliveryNote(deliveryNoteTo);
        deliveryNoteRepository.save(deliveryNoteLine);
    }
    /*
     * private void insert(){ String query1 = "insert into users values('" + usr + "','" + pwd +"')"; Query q = em.createQuery(query1);
     * u=em.find(User.class,usr); return u; }
     */
}
