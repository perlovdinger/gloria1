package com.volvo.gloria.procurematerial.repositories.b;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import com.volvo.gloria.common.c.dto.reports.ReportWarehouseTransactionDTO;
import com.volvo.gloria.procurematerial.c.DeliveryNoteLineStatus;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNote;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteSubLine;
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptHeader;
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptLine;
import com.volvo.gloria.procurematerial.d.entities.ReceiveDoc;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.ProblemDoc;
import com.volvo.gloria.procurematerial.d.entities.QiDoc;
import com.volvo.gloria.procurematerial.d.entities.TransportLabel;
import com.volvo.gloria.procurematerial.d.type.receive.ReceiveType;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * repository for root DeliveryNote.
 * 
 */
public interface DeliveryNoteRepository extends GenericRepository<DeliveryNote, Long> {

    List<DeliveryNoteLine> findDeliveryNoteLinesByDeliveryNoteId(Long deliveryNoteOid, String whSiteId, DeliveryNoteLineStatus status, ReceiveType receiveType) 
            throws GloriaApplicationException;

    DeliveryNoteLine findDeliveryNoteLineById(long deliveryNoteLineId);

    DeliveryNoteLine save(DeliveryNoteLine instanceToSave);

    DeliveryNote findDeliveryNote(String orderNo, String deliveryNoteNo, ReceiveType receiveType, Date date);

    void delete(DeliveryNoteLine deliveryNoteLine);

    QiDoc save(QiDoc qualityDoc);

    ProblemDoc save(ProblemDoc problemDoc);

    QiDoc findQualityDocById(long qualityDocId);

    void delete(QiDoc qualityDoc);

    ProblemDoc findProblemDocById(long problemNoteDocId);

    void delete(ProblemDoc problemDoc);

    List<QiDoc> findQiDocs(long deliveryNoteLineId);

    List<ProblemDoc> findProblemDocs(long deliveryNoteLineId);

    List<DeliveryNoteLine> findDeliveryNoteLinesByOrderLineId(long orderLineId, DeliveryNoteLineStatus status);

    List<TransportLabel> findTransportLabelByWhSiteId(String whSite);

    TransportLabel save(TransportLabel instanceToSave);

    TransportLabel findTransportLabelById(long transportLabelId);

    TransportLabel findTransportLabelByIdAndWhSiteId(long transportlabelID, String whSite);
    
    GoodsReceiptLine save(GoodsReceiptLine goodsReceiptLine);

    GoodsReceiptLine findGoodsReceiptLineById(long goodsReceiptLineOId);

    void deleteGoodsReceiptLine(GoodsReceiptLine goodsReceiptLine);

    GoodsReceiptHeader save(GoodsReceiptHeader goodsReceiptHeader);

    GoodsReceiptHeader findGoodsReceiptHeaderById(long goodsReceiptHeaderOId);

    void deleteGoodsReceiptHeader(GoodsReceiptHeader goodsReceiptHeader);

    List<GoodsReceiptLine> findAllGoodsReceiptLine();
    
    List<GoodsReceiptHeader> findAllGoodsReceiptHeaders();

    ReceiveDoc save(ReceiveDoc inspectionDoc);

    ReceiveDoc findReceiveDocById(long inspectionDocId);

    List<ReceiveDoc> findInspectionDocs(long deliveryNoteLineOid);

    void delete(ReceiveDoc inspectionDoc);

    List<OrderLineVersion> findAllOrderLineVersions(long orderLineOid);
    
    List<GoodsReceiptHeader> findAllGoodsMovementsForCompanyCode(String companyCode);

    DeliveryNoteSubLine findDeliveryNoteSubLineById(long deliveryNoteSubLineid);

    DeliveryNoteSubLine save(DeliveryNoteSubLine deliveryNoteSubLine);

    List<DeliveryNoteSubLine> findDeliveryNoteSubLinesLineById(long deliveryNoteLineOid);

    void delete(DeliveryNoteSubLine deliveryNoteSubLine);

    PageObject findDeliveryNoteLinesForQI(PageObject pageObject, String materialLineStatus, String qiMarking, String whSiteId, boolean needSublines) 
            throws GloriaApplicationException;

    PageObject findGoodsReceiptLinesByPlant(PageObject pageObject, String plant, String status);

    List<Tuple> findDeliveryNoteByTypeAndProject(Date fromDate, Date toDate, String[] projectIds, String[] whSiteId);

    List<Tuple> getTransactionReceivalsReportData(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO, Date fromDate, Date toDate, String[] projectIds);

    List<Tuple> getTransactionPicksReportData(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO, Date fromDate, Date toDate);

    List<Tuple> getTransactionReturnsReportData(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO, Date fromDate, Date toDate, String[] projectIds);

}
