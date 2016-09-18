package com.volvo.gloria.procurematerial.repositories.b.beans;

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
import org.junit.Ignore;
import org.junit.Test;

import com.volvo.gloria.common.c.dto.reports.ReportGeneralDTO;
import com.volvo.gloria.common.c.dto.reports.ReportPerformanceDTO;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNote;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliverySchedule;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.MaterialLineStatusTime;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.type.internalexternal.InternalExternal;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class OrderRepositoryBeanTest extends AbstractTransactionalTestCase {

    @Inject
    protected OrderRepository orderRepository;
    
    @Inject
    protected ProcureLineRepository procureLineRepository;
    
    @Inject
    protected MaterialHeaderRepository requestHeaderRepository;
    
    @Inject
    protected DeliveryNoteRepository deliveryNoteRepository;
    
    public OrderRepositoryBeanTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    /*
     * when you have two or more orderlines you get back grouped data based on quantity and part information
     */
    @Test
    public void testGeneralReportData() throws GloriaApplicationException {
        createGeneralReportData(5, "FIVEPART", true);   
        createGeneralReportData(1, "FIVEPART1", true);  
        createGeneralReportData(10, "FIVEPART2", true); 
        createGeneralReportData(10, "FIVEPART2", true); 
        ReportGeneralDTO reportGeneralDTO = new ReportGeneralDTO();   
        reportGeneralDTO.setWarehouse(new String[]{"warehouse1"});
        reportGeneralDTO.setProject(new String[]{"projectId"});
        List<Tuple> tuples =  orderRepository.getGeneralReportData(reportGeneralDTO);
        Assert.assertEquals(8, tuples.size());    
    }
    
    
    /*
     * when you have two or more orderlines you get back grouped data based on quantity and part information
     */
    @Test
    public void testGeneralReportDataWithOutOrderLines() throws GloriaApplicationException {
        createGeneralReportData(5, "FIVEPART", false);   
        createGeneralReportData(1, "FIVEPART1", false);  
        createGeneralReportData(10, "FIVEPART2", false); 
        createGeneralReportData(10, "FIVEPART2", false); 
        ReportGeneralDTO reportGeneralDTO = new ReportGeneralDTO();   
        reportGeneralDTO.setWarehouse(new String[]{"warehouse1"});
        reportGeneralDTO.setProject(new String[]{"projectId"});
       List<Tuple> tuples =  orderRepository.getGeneralReportDataWithNullOrderLines(reportGeneralDTO);
       // List<Tuple> tuples =  orderRepository.getGeneralReportData(reportGeneralDTO);

        Assert.assertEquals(1, tuples.size());    
    }

    private void createGeneralReportData(int price, String partName, boolean createOrderLines) throws GloriaApplicationException {
        Material material = new Material();
        material.setPartAffiliation("partAffiliation");
        material.setPartName(partName);
        material.setPartModification("partModification");
        material.setPartNumber("partNumber");
        material.setModificationId(price);
        FinanceHeader financeHeader = new FinanceHeader();
        financeHeader.setProjectId("projectId");
        em.persist(financeHeader);
        material.setFinanceHeader(financeHeader);
        Order order = createOrder();
        requestHeaderRepository.addMaterial(material);
        List<Material> materials = requestHeaderRepository.findMaterialsByModificationId(price);
        //Assert.assertEquals(1, materials.size());
        createMaterialLine(materials,2);
        createMaterialLine(materials, 3L);
        List<MaterialLine> materialLines = this.getMaterialLines();
        //Assert.assertEquals(2, materialLines.size());
        Assert.assertNotNull(materialLines.get(0).getMaterialLineStatusTime());
        material.setMaterialLine(materialLines);
        requestHeaderRepository.saveMaterial(material);
        //orderLineVersion is needed and set to current
        
        if(createOrderLines){
            OrderLineVersion orderLineVersion1 = createOrderLineVersion(1L, 5);
            Assert.assertTrue(orderLineVersion1.getOrderLineVersionOid()>0);;
            List<OrderLineVersion> orderLineVersions = new ArrayList<OrderLineVersion>();
            orderLineVersions.add(orderLineVersion1);orderRepository.saveOrderLineVersion(orderLineVersion1);
            OrderLine orderLineCreated = createOrderLine("buyerpartyId", "projectId","deliveryControllerId", order, null, null, orderLineVersions, orderLineVersion1, null, materials);   
            material.setOrderLine(orderLineCreated);
            orderLineVersion1.setOrderLine(orderLineCreated);
            orderRepository.saveOrderLineVersion(orderLineVersion1);
            requestHeaderRepository.saveMaterial(material);
        }    
    }

    private void createMaterialLine(List<Material> materials, long quantity) throws GloriaApplicationException {
        MaterialLine materialLine = new MaterialLine();
        materialLine.setWhSiteId("warehouse1");
        materialLine.setStatus(MaterialLineStatus.STORED);
        materialLine.setMaterial(materials.get(0));
        materialLine.setQuantity(quantity);
        requestHeaderRepository.updateMaterialLine(materialLine);
    }  
    @Test
    @Ignore
    public void testMaterialPerformanceReportData() throws GloriaApplicationException {
        //this test was failing in the build and hence needed to run this.
        this.removeAllMaterialLines();
        this.removeAllMaterials();
        MaterialLineStatusTime materialLineStatusTime = new MaterialLineStatusTime();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        materialLineStatusTime.setReceivedTime(timestamp);
        materialLineStatusTime.setStoredTime(timestamp);
        materialLineStatusTime.setRequestTime(timestamp);        
        materialLineStatusTime.setShippedTime(timestamp); 
        
        Material material = new Material();
        requestHeaderRepository.addMaterial(material);
        
        List<Material> materials = requestHeaderRepository.findAllMaterials();
        Assert.assertEquals(1, materials.size());
        MaterialLine materialLine = new MaterialLine();
        materialLine.setMaterial(materials.get(0));
        materialLine.setQuantity(2L);
        materialLine.setMaterialLineStatusTime(materialLineStatusTime);
        requestHeaderRepository.updateMaterialLine(materialLine);
        List<MaterialLine> materialLines = this.getMaterialLines();
        Assert.assertEquals(1, materialLines.size());
        Assert.assertNotNull(materialLines.get(0).getMaterialLineStatusTime());
        material.setMaterialLine(materialLines);
        requestHeaderRepository.saveMaterial(material);
        
        Date fromDate = new Date();
        Date toDate = DateUtils.addDays(new Date(), 1);
        ProcureLine procureLine = createProcureLine();
        Order order = createOrder();
        createDeliveryNote();
        createDeliveryNoteLines();
        createDeliveryNoteLines();
        createDeliveryNoteLines();
        List<DeliveryNoteLine> deliveryNoteLines = this.getDeliveryNoteLines();
        Assert.assertEquals(3, deliveryNoteLines.size());
        OrderLine orderLineCreated = createOrderLine("buyerpartyId", "projectId","deliveryControllerId", order, null, procureLine, null, null, deliveryNoteLines, materials);   

        Assert.assertTrue(orderLineCreated.getDeliveryNoteLines().size()>0);
        material.setOrderLine(orderLineCreated);
        requestHeaderRepository.saveMaterial(material);
        Assert.assertTrue(orderLineCreated.getOrderLineOID() > 0);
        
        for(DeliveryNoteLine deliveryNoteLine : deliveryNoteLines){
            deliveryNoteLine.setOrderLine(orderLineCreated);
            deliveryNoteRepository.save(deliveryNoteLine);
        }
        
        //QUERY
        ReportPerformanceDTO reportPerformanceDTOParam = new ReportPerformanceDTO();     
        reportPerformanceDTOParam.setBuyerId(new String[]{"buyerpartyId"});
        reportPerformanceDTOParam.setProject(new String[]{"projectId"});
        reportPerformanceDTOParam.setSuffix(new String[]{"suffix"});
        reportPerformanceDTOParam.setWarehouse(new String[]{"warehouse1"});
        reportPerformanceDTOParam.setBuildSeries(new String[]{"buildSeries"});
        reportPerformanceDTOParam.setPartNumber(new String[]{"partNo"});
        List<Tuple> tuple = orderRepository.materialPerformanceReportData(reportPerformanceDTOParam, fromDate, toDate) ;
        Assert.assertNotNull(tuple);
        // even though we have One Order Line with 3 versions we get back only one order line with version=1
        Assert.assertEquals(1, tuple.size());
        Assert.assertNotNull(tuple.get(0).get(0));
        Assert.assertNotNull(tuple.get(0).get(1));
        Assert.assertNotNull(tuple.get(0).get(2));
        Assert.assertNotNull(tuple.get(0).get(3));
        Assert.assertNotNull(tuple.get(0).get(4));
        Assert.assertNotNull(tuple.get(0).get(5));
        Assert.assertNotNull(tuple.get(0).get(6));
        Assert.assertNotNull(tuple.get(0).get(7));
        Assert.assertNotNull(tuple.get(0).get(8));
        Assert.assertNotNull(tuple.get(0).get(9));
        Assert.assertNotNull(tuple.get(0).get(10));
        Assert.assertNotNull(tuple.get(0).get(11));
        //Assert.assertNotNull(tuple.get(0).get(12));        
    }  

    @Test
    @Ignore
    public void testOrderSTAAndDeliverySTAReport() {
        Date fromDate = new Date();
        Date toDate = DateUtils.addDays(new Date(), 1);
        ProcureLine procureLine = createProcureLine();
        Order order = createOrder();
        OrderLineVersion orderLineVersion1 = createOrderLineVersion(1L, 0);
        Assert.assertTrue(orderLineVersion1.getOrderLineVersionOid()>0);;
        OrderLineVersion orderLineVersion2 = createOrderLineVersion(2L, 0);
        Assert.assertTrue(orderLineVersion2.getOrderLineVersionOid()>0);;
        OrderLineVersion orderLineVersion3 = createOrderLineVersion(3L, 0);
        Assert.assertTrue(orderLineVersion3.getOrderLineVersionOid()>0);;
        List<OrderLineVersion> orderLineVersions = new ArrayList<OrderLineVersion>();
        orderLineVersions.add(orderLineVersion1);orderRepository.saveOrderLineVersion(orderLineVersion1);
        orderLineVersions.add(orderLineVersion2);orderRepository.saveOrderLineVersion(orderLineVersion2);
        orderLineVersions.add(orderLineVersion3);orderRepository.saveOrderLineVersion(orderLineVersion3);
        createDeliveryNote();
        createDeliveryNoteLines();
        createDeliveryNoteLines();
        createDeliveryNoteLines();
        List<DeliveryNoteLine> deliveryNoteLines = this.getDeliveryNoteLines();
        Assert.assertEquals(3, deliveryNoteLines.size());

        OrderLine orderLineCreated = createOrderLine("buyerpartyId", "projectId","deliveryControllerId", order, null, procureLine, orderLineVersions, null, deliveryNoteLines, null);   
        Assert.assertTrue(orderLineCreated.getDeliveryNoteLines().size()>0);
        orderLineVersion1.setOrderLine(orderLineCreated);
        orderLineVersion2.setOrderLine(orderLineCreated);
        orderLineVersion3.setOrderLine(orderLineCreated);
        Assert.assertTrue(orderLineCreated.getOrderLineOID() > 0);
        Assert.assertTrue(orderLineCreated.getOrderLineVersions().size() > 0);
        
        for(DeliveryNoteLine deliveryNoteLine : deliveryNoteLines){
            deliveryNoteLine.setOrderLine(orderLineCreated);
            deliveryNoteRepository.save(deliveryNoteLine);
        }
        
        //QUERY
        ReportPerformanceDTO reportPerformanceDTOParam = new ReportPerformanceDTO();     
        reportPerformanceDTOParam.setBuyerId(new String[]{"buyerpartyId"});
        reportPerformanceDTOParam.setProject(new String[]{"projectId"});
        reportPerformanceDTOParam.setSuffix(new String[]{"suffix"});
        reportPerformanceDTOParam.setWarehouse(new String[]{"warehouse1"});
        reportPerformanceDTOParam.setBuildSeries(new String[]{"buildSeries"});
        reportPerformanceDTOParam.setPartNumber(new String[]{"partNo"});
        List<Tuple> tuple = orderRepository.orderSTAAndDeliverySTAReport(reportPerformanceDTOParam, fromDate, toDate) ;
        Assert.assertNotNull(tuple);
        // even though we have One Order Line with 3 versions we get back only one order line with version=1
        Assert.assertEquals(3, tuple.size());
        Assert.assertNotNull(tuple.get(0).get(0));
    }  
    
    private void createDeliveryNoteLines() {
        DeliveryNoteLine deliveryNoteLine = new DeliveryNoteLine();
        deliveryNoteLine.setPartAffiliation("partAffiliation");
        deliveryNoteLine.setReceivedDateTime(new Date());
        deliveryNoteLine.setDeliveryNote((DeliveryNote)this.getDeliveryNote().get(0));
        deliveryNoteRepository.save(deliveryNoteLine);
    }
    
    private void createDeliveryNote(){
        DeliveryNote deliveryNote = new DeliveryNote();  
        deliveryNote.setDeliveryNoteDate(new Date());
        deliveryNoteRepository.save(deliveryNote);
    }

    @Test
    @Ignore
    public void testPPReqAndOrderPlacedDateDifference() {
        Date fromDate = new Date();
        Date toDate = DateUtils.addDays(new Date(), 1);
        ProcureLine procureLine = createProcureLine();
        Order order = createOrder();
        OrderLineVersion orderLineVersion1 = createOrderLineVersion(1L, 0);
        Assert.assertTrue(orderLineVersion1.getOrderLineVersionOid()>0);;
        OrderLineVersion orderLineVersion2 = createOrderLineVersion(2L, 0);
        Assert.assertTrue(orderLineVersion2.getOrderLineVersionOid()>0);;
        OrderLineVersion orderLineVersion3 = createOrderLineVersion(3L, 0);
        Assert.assertTrue(orderLineVersion3.getOrderLineVersionOid()>0);;
        List<OrderLineVersion> orderLineVersions = new ArrayList<OrderLineVersion>();
        orderLineVersions.add(orderLineVersion1);orderRepository.saveOrderLineVersion(orderLineVersion1);
        orderLineVersions.add(orderLineVersion2);orderRepository.saveOrderLineVersion(orderLineVersion2);
        orderLineVersions.add(orderLineVersion3);orderRepository.saveOrderLineVersion(orderLineVersion3);
        
        OrderLine orderLineCreated = createOrderLine("buyerpartyId", "projectId","deliveryControllerId", order, null, procureLine, orderLineVersions, null, null, null);   
        
        orderLineVersion1.setOrderLine(orderLineCreated);
        orderLineVersion2.setOrderLine(orderLineCreated);
        orderLineVersion3.setOrderLine(orderLineCreated);
        Assert.assertTrue(orderLineCreated.getOrderLineOID() > 0);
        Assert.assertTrue(orderLineCreated.getOrderLineVersions().size() > 0);

        //QUERY
        ReportPerformanceDTO reportPerformanceDTOParam = new ReportPerformanceDTO();     
        reportPerformanceDTOParam.setBuyerId(new String[]{"buyerpartyId"});
        reportPerformanceDTOParam.setProject(new String[]{"projectId"});
        reportPerformanceDTOParam.setSuffix(new String[]{"suffix"});
        reportPerformanceDTOParam.setWarehouse(new String[]{"warehouse1"});
        reportPerformanceDTOParam.setBuildSeries(new String[]{"buildSeries"});
        reportPerformanceDTOParam.setPartNumber(new String[]{"partNo"});
        List<Tuple> tuple = orderRepository.pPReqAndOrderPlacedDateDifference(reportPerformanceDTOParam, fromDate, toDate) ;
        Assert.assertNotNull(tuple);
        // even though we have One Order Line with 3 versions we get back only one order line with version=1
        Assert.assertEquals(1, tuple.size());
        Assert.assertNotNull(tuple.get(0).get(0));
    }   


    private OrderLineVersion createOrderLineVersion(long versionNo, long unitPrice) {
        OrderLineVersion orderLineVersion = new OrderLineVersion();
        orderLineVersion.setOrderStaDateOnTime(true);
        orderLineVersion.setOrderStaDate(new Date());
        orderLineVersion.setStaAgreedDate(new Date());
        orderLineVersion.setVersionNo(versionNo);
        orderLineVersion.setUnitPrice(unitPrice);
        OrderLineVersion orderLineVersion2= orderRepository.saveOrderLineVersion(orderLineVersion);
        List orderLineVersions = getOrderLineVersionByVersionNo();
        for(Object orderLineVersionIter : orderLineVersions){
            OrderLineVersion OrderLineVersionIter1 = (OrderLineVersion)orderLineVersionIter;
            if(OrderLineVersionIter1.getVersionNo() == versionNo){
                return OrderLineVersionIter1;
            }
        }
        return null;
    }

    private ProcureLine createProcureLine() {
        ProcureLine procureLine = new ProcureLine();
        procureLine.setReferenceGroups("buildSeries");
        procureLine.setReferenceIds("referenceIds");
        procureLine.setRequisitionId("1234");
        procureLine.setProcureDate(DateUtils.addDays(new Date(), -1));
        procureLineRepository.save(procureLine);
        ProcureLine procureLineReturn = procureLineRepository.findProcureLineByRequisitionId("1234");
        Assert.assertTrue(procureLineReturn.getProcureLineOid()>0);
        return procureLineReturn;        
    }

    @Test
    public void testGetDistinctBuyerPartyIdsFromOrderLine() {
        OrderLine orderLine = new OrderLine();
        orderLine.setBuyerPartyId(null);
        orderRepository.saveOrderLine(orderLine);
        OrderLine orderLine1 = new OrderLine();
        orderLine1.setBuyerPartyId("rahul");
        orderRepository.saveOrderLine(orderLine1);
        OrderLine orderLine2 = new OrderLine();
        orderLine2.setBuyerPartyId("rahul1");
        orderRepository.saveOrderLine(orderLine2);
        List<Tuple> tuple = orderRepository.getDistinctBuyerPartyIdsFromOrderLine();
        // null is not returned back
        Assert.assertEquals(2, tuple.size());
        
    }
    /*
     * if null is the buyerid then it is not returned this failed during testing 
     */
    @Test
    public void testGetDistinctBuyerPartyIdsFromOrderLineWithNull() {
        OrderLine orderLine = new OrderLine();
        orderLine.setBuyerPartyId("rahul");
        orderRepository.saveOrderLine(orderLine);
        OrderLine orderLine1 = new OrderLine();
        orderLine1.setBuyerPartyId("rahul");
        orderRepository.saveOrderLine(orderLine1);
        OrderLine orderLine2 = new OrderLine();
        orderLine2.setBuyerPartyId("rahul1");
        orderRepository.saveOrderLine(orderLine2);
        List<Tuple> tuple = orderRepository.getDistinctBuyerPartyIdsFromOrderLine();
        Assert.assertEquals(2, tuple.size());
        
    }
    
    /*
     * if null is the buyerid then it is not returned this failed during testing 
     */
    @Test
    public void testGetDistinctPartsFromOrderLineWithNull() {
        OrderLine orderLine = new OrderLine();
        orderLine.setPartNumber("123");
        orderRepository.saveOrderLine(orderLine);
        OrderLine orderLine1 = new OrderLine();
        orderLine.setPartNumber(null);
        orderRepository.saveOrderLine(orderLine1);
        OrderLine orderLine2 = new OrderLine();
        orderLine.setPartNumber("1234");
        orderRepository.saveOrderLine(orderLine2);
        List<Tuple> tuple = orderRepository.getDistinctPartsFromOrderLine();
        Assert.assertEquals(1, tuple.size());
        
    }   
       
    private Order createOrder() {
        Order order = new Order();
        order.setOrderNo("orderNo");
        order.setInternalExternal(InternalExternal.EXTERNAL);
        order.setSuffix("suffix");
        order.setSupplierId("supplierId");
        order.setSupplierName("supplierName");
        order.setShipToId("warehouse1");
        order.setOrderDateTime(new Date());
        orderRepository.save(order);
        List<Order> orders = orderRepository.findAll();
        //Assert.assertEquals(1, orders.size());
        return orders.get(orders.size()-1);                
       
    }
    
    private OrderLine createOrderLine(String buyerpartyId, String projectId, String deliveryControllerUserId, Order order) {
        return this.createOrderLine( buyerpartyId,  projectId,  deliveryControllerUserId, order, null, null, null, this.createOrderLineVersion(1L, 0), null, null);
    }

    private OrderLine createOrderLine(String buyerpartyId, String projectId, String deliveryControllerUserId, Order order, List<DeliverySchedule> deliverySchedules, ProcureLine procureLine, List<OrderLineVersion> orderLineVersions, OrderLineVersion currentOrderLineVersion, List<DeliveryNoteLine> deliveryNoteLines, List<Material> materials) {
        OrderLine orderLine = new OrderLine();
        orderLine.setPartNumber("partNo");
        orderLine.setBuyerPartyId(buyerpartyId);
        orderLine.setProjectId(projectId);
        orderLine.setDeliveryControllerUserId(deliveryControllerUserId);
        orderLine.setOrder(order);
        orderLine.setProcureLine(procureLine);
        orderLine.setCurrent(currentOrderLineVersion);
        orderLine.setOrderLineVersions(orderLineVersions);
        orderLine.setRequisitionId("123");
        orderLine.setDeliverySchedule(deliverySchedules);
        orderLine.setDeliveryNoteLines(deliveryNoteLines);
        orderLine.setMaterials(materials);
        orderRepository.saveOrderLine(orderLine);
        return orderRepository.findOrderLineByRequisitionId("123").get(0);
    }
    
   
    
    @PersistenceContext(unitName = "ProcureMaterialDomainPU")
    public void setEntityManager(EntityManager em) {
        this.em=em;
    }

    EntityManager em;

    @SuppressWarnings("unchecked")
    private List<OrderLineVersion> getOrderLineVersionByVersionNo() {
        return em.createQuery("SELECT c FROM OrderLineVersion c").getResultList();
    }

    @SuppressWarnings("unchecked")
    private List<DeliveryNoteLine> getDeliveryNoteLines() {
        return em.createQuery("SELECT c FROM DeliveryNoteLine c").getResultList();
    }

    @SuppressWarnings("unchecked")
    private List<DeliveryNote> getDeliveryNote() {
        return em.createQuery("SELECT c FROM DeliveryNote c").getResultList();
    }
    
    @SuppressWarnings("unchecked")
    private List<MaterialLine> getMaterialLines() {
        return em.createQuery("SELECT c FROM MaterialLine c").getResultList();
    }
    private void removeAllMaterialLines() {
        em.createQuery("DELETE FROM MaterialLine").executeUpdate();        
    }
    private void removeAllMaterials() {
        em.createQuery("DELETE FROM Material").executeUpdate();        
    }
}
