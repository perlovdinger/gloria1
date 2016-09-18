package com.volvo.gloria.procurematerial.repositories.b;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Tuple;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.c.dto.reports.ReportFilterMaterialDTO;
import com.volvo.gloria.common.c.dto.reports.ReportWarehouseCostDTO;
import com.volvo.gloria.common.c.dto.reports.ReportWarehouseTransactionDTO;
import com.volvo.gloria.common.d.entities.CurrencyRate;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.c.ProcureResponsibility;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.DispatchNote;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.PickList;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.entities.status.changeid.ChangeIdStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.dto.reports.ReportRow;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.persistence.GenericRepository;
import com.volvo.gloria.warehouse.d.entities.BinLocation;
import com.volvo.gloria.warehouse.d.entities.BinlocationBalance;

/**
 * 
 */
public interface MaterialHeaderRepository extends GenericRepository<MaterialHeader, Long> {

    List<ChangeId> findChangeId(long procureLineOId, ChangeIdStatus... changeIdStatuses);

    ChangeId findChangeIdByOid(long changeIdOId);

    PageObject getRequestHeaders(PageObject pageObject, Boolean assignedMaterialController, List<String> companyCodes);

    List<Material> getMaterialsForHeaderIds(List<MaterialHeader> headers);

    void addFinanceHeader(FinanceHeader financeHeader);

    PageObject findMaterialsByMaterialHeaderId(long requestHeaderId, PageObject pageObject) throws GloriaApplicationException;

    PageObject findMaterialsByStatusAndResponsability(ProcureLineStatus procureLineStatus, ProcureResponsibility procureResponsibility,
            String assignedMaterialControllerId, String assignedMaterialControllerTeam, String procureForwardedId, PageObject pageObject, String materialType,
            List<String> companyCodes);

    FinanceHeader findFinanceHeaderById(long financeHeaderOid);

    ChangeId save(ChangeId changeId);

    PageObject findAllChangeId(PageObject pageObject, String assignedMaterialController, String assignedMaterialControllerTeam, List<String> companyCodes);

    void updateMaterial(Material material);

    Material findMaterialByProcureLinkId(long procureLinkId);

    Material findMaterialById(Long materialOid) throws GloriaApplicationException;

    List<Material> findAllMaterials();

    void addMaterial(Material material);

    List<Material> findMaterialsByProcureLineId(long procureLineOid);

    List<Material> getMaterialsByChangeId(long changeIdId);

    void deleteMaterial(Material material);

    Material getMaterialWithMaterialLinesById(long materialOid);

    List<Material> findMaterialByRequisitionId(String requisitionId);

    MaterialLine findMaterialLineById(Long materialLineId, boolean calculateStockBalance, List<String> whSiteIds);

    MaterialLine updateMaterialLine(MaterialLine materialLine);

    MaterialLine findMaterialLineByPartInfoAndStatus(String pPartNumber, String pPartVersion, 
            List<MaterialLineStatus> statuses, String whSiteId, String partModification, String partAffiliation);

    BinLocation suggestBinLocation(String pPartNumber, String pPartVersion, String whSiteId, String partModification, String partAffiliation);

    List<Material> findMaterialByOrderLineId(Long orderlineId);

    MaterialLine findMaterialLineById(Long materialLineId);

    PageObject getRequestGroup(PageObject pageObject, String whSiteId, String userId);

    List<RequestGroup> getAllRequestGroups();

    PickList findPickListById(Long pickListId);

    RequestList save(RequestList instanceToSave);

    PickList updatePickList(PickList pickList);

    PageObject findMaterialLineBypickListIdAndStatus(PageObject pageObject, long pickListId, String status, boolean suggestBinLocation)
            throws GloriaApplicationException;

    List<MaterialLine> findMaterialLinesByPickListId(long pickListId);

    PickList savePickList(PickList instanceToSave);

    RequestGroup findRequestGroupById(Long requestGroupOid);

    List<MaterialLine> findMaterialLines(List<Long> materialLineOIds);

    RequestGroup saveRequestGroup(RequestGroup requestGroup);

    RequestList findRequestListById(long requestListOid);

    List<RequestList> findRequestListByUserId(String requesterUserId, String status);

    void deleteRequestList(RequestList requestList);

    PageObject findPickListByStatus(PageObject pageObject, String whSiteId, String status, String userId);

    PickList findPickListByCode(String pickListCode);

    double calculateStockBalance(String partNumber, String partVersion, List<String> whSiteIds, String partModification, String partAffiliation);

    double calculateStockBalance(String partNumber, String partVersion, String whSiteId, String partModification, String partAffiliation);

    DispatchNote findDispatchNoteById(long dispatchNoteId);

    DispatchNote saveDispatchNote(DispatchNote dispatchNote);

    void deleteDispatchNote(long dispatchNoteId);

    PageObject getRequestLists(PageObject pageObject, String requestListStatus, String whSiteId, String outBoundLocationId, String requesterId)
            throws GloriaApplicationException;

    List<RequestGroup> findAllRequestGroupsByRequestListId(long requestListOid);

    void updateRequestList(RequestList requestList);

    List<MaterialLine> getMovingMaterialLines(String pPartNumber, String pPartVersion, String binLocationCode, Date scrapExpirationDate, String whSiteId);

    List<MaterialLine> getMaterialLinesWithPartNumberaAndBinlocation(String pPartNumber, String binLocationCode);

    List<RequestGroup> findRequestGroupsByDispatchNoteId(long dispatchNoteOID);

    List<MaterialLine> findMaterialLinesByRequestGroupId(long requestGroupOid);

    List<MaterialLine> findMaterialsByPartStatusSiteAndTransportLabel(String pPartNumber, String pPartVersion, String status, 
                                                                    String whSiteId, String pPartModification, String pPartAffiliation, String transportLabel);

    PageObject findMaterialLineByQi(PageObject pageObject, String status, String qiMarking, boolean calculateSuggestedBinLocation, String whSiteId);

    MaterialLine findMaterialLineByIdForQi(Long materialLineId);

    void addMaterialLine(MaterialLine materialLine);

    List<MaterialHeader> findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(String referenceId, String buildId,
            String outboundLocationId);

    List<MaterialHeader> findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(String requestType, String referenceId, String buildId,
            String outboundLocationId, String changeTechId);

    ChangeId findChangeIdByTechId(String changeTechId);

    List<ChangeId> findChangeIdByProcureRequestId(String procureRequestId);

    List<MaterialLine> getMaterialLinesInStock(String partNumber, String partVersion, String partAffiliation, String projectId, String partModification,
            String companyCode);

    PageObject getAccumulatedMaterialLinesInStock(PageObject pageObject, String partNumber, String partVersion, String partAffiliation, 
            String projectId, String partModification, String companyCode);

    List<MaterialLine> findMaterialLinesFromStock(String partNumber, String partVersion, String partAffiliation, String projectId, String materialLineKeys);

    PageObject getMaterialLinesForWarehouse(PageObject pageObject, String materialLineStatus, boolean calculateStockBalance, String whSiteId,
            Boolean suggestBinLocation, String partNo, String transportLabel, String userId, String  partAffiliation, String partVersion,
            String partModification, String partName);

    PageObject getMaterialLines(PageObject pageObject, String expirationDate, String expirationTo, String expirationFrom,
            Boolean allExpired) throws GloriaApplicationException;

    PageObject getBorrowableMaterialLines(PageObject pageObject, String pPartNumber, String pPartVersion, String pPartAffiliation, String referenceId,
            String mtrlRequestId);

    List<MaterialLine> findMaterialLinesByDispatchNoteInTransferOrReturn(String dispatchNoteNo, Date dispatchNoteDate);

    List<Material> findMaterialsByModificationId(long modificationId);

    DispatchNote findDispatchNote(String dispatchNoteNo, Date dispatchNoteDate);

    PageObject findMaterialLinesByDispatchNote(PageObject pageObject, String receiveType, String whSiteId, String deliveryAddressId, String shipmentType,
            String dispatchNoteNo, String partNo, String partVersion, String status);

    PageObject findMaterialsByProcureLineIds(List<Long> procureLineIds, PageObject pageObject);

    List<ProcureLine> findProcureLinesByModificationId(long modificationId);

    void deleteRequestGroup(RequestGroup requestGroup);

    List<MaterialHeader> findMaterialHeaderByMtrlRequestId(String mtrlRequestId, String referenceId);

    void deleteMaterialLine(MaterialLine materialLine);

    List<Material> findMaterialsForOrderNos(Set<String> receivedOrderNo);

    List<MaterialLine> findMaterialLinesByReservedUserId(String userId);

    String suggestNextlocation(String partNumber, String partVersion, String whSiteId, boolean directSend, String partModification, String partAffiliation);

    List<PickList> findPickListByReservedUserId(String userId);

    MaterialLine findAMaterialLineByAndStatusAndPlacement(MaterialLine materialLine, MaterialLineStatus materialLineStatus, String binLocationCode);

    Long getMaxFirstAssemblyIdSequence();

    MaterialLine merge(MaterialLine materialLineToMerge, MaterialLine existingMaterialLine) throws GloriaApplicationException;

    MaterialLine split(MaterialLine materialLineToSplit, MaterialLineStatus materialLineStatus, long quantity, MaterialLine existingMaterialLine,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, UserDTO user,
            List<MaterialLineStatus> avoidTraceForMLStatus) throws GloriaApplicationException;

    long getHeaderVersionsForFirstAssembly(String referenceId, String buildId, ChangeId changeId);

    List<MaterialLine> findMaterialByBinlocatioinBalance(BinlocationBalance binlocationBalance);

    PageObject getReferenceGroups(PageObject pageObject, List<String> companyCodes);

    PageObject getTestObjects(PageObject pageObject);

    PageObject getChangeIds(PageObject pageObject);

    List<ReportRow> fetchMaterialLinesForReport(ReportFilterMaterialDTO reportFilterMaterialDTO, Map<String, String> companyCodeToDefaultCurrencyMap,
            Map<String, CurrencyRate> currencyToCurrencyRateMap);

    void updateFinanceHeader(FinanceHeader financeHeader);

    List<MaterialLine> findMaterialLinesForOrderline(long orderLineOID);

    List<Material> findAllMaterialsByReplacedByOID(long replacedByOid);

    List<RequestList> findRequestListByPicklist(long picklistOid);

    Material saveMaterial(Material instanceToSave);
    
    List<DispatchNote> findDispatchNote(Date fromDate, Date toDate, String[] projectIds, String[] whSiteId);

    List<ReportRow> fetchWarehouseCostReport(ReportWarehouseCostDTO reportWarehouseCostDTO, Date fromDate, Date toDate,
            Map<String, String> companyCodeToDefaultCurrencyMap, Map<String, CurrencyRate> currencyToCurrencyRateMap);

    List<Tuple> getTransactionShipmentReportData(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO, Date fromDate, Date toDate, String[] projectIds);

    List<Tuple> getTransactionStoresReportData(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO, Date fromDate, Date toDate);

    FinanceHeader findFinanceHeaderByMaterialHeaderOid(long materialHeaderOid);

    /*
     * for a given material Header oid this returns back the procurelines id's with the number of materials associated 
     * with this procure line
     */
    public Map<Long, Long> findProcureLineOidsMap(long materialHeaderOid);

    void updateProcureLineControllerDetails(String materialControllerUserId, String materialControllerName, String materialControllerTeam,
            List<Long> procureLineOids);

    List<ProcureLine> findProcureLinesByIds(List<Long> procureLineIds);    

    void updateMaterialLinesStatus(long materialHeaderOid, MaterialLineStatus fromStatus, MaterialLineStatus toStatus);

    long findProcureLineSumQuantities(long procureLineOid, List<MaterialType> materialTypes);

    void updateMaterialLinesStatus(List<Long> materialOids, MaterialLineStatus toStatus);


    List<Material> findMaterialsByProcureLineIds(List<Long> procureLineOid);

    Map<MaterialType, Long> findMaterialLineQuantities(List<Long> materialOids);

    List<ChangeId> findChangeIdByCrId(String crId);

    /*
     * This interface generates sorted and distinct project ids from FinanceHeader
     */
    PageObject getProjects(PageObject pageObject, List<String> companyCodes, String projectId);
    
    // This interface generates sorted and distinct wbsElements from FinanceHeader 
    PageObject getWbsElements(PageObject pageObject, String wbsCode);
    List<Material> findMaterialsByMaterialHeaderId(Long materialHeaderOid);
    List<Material> findAllMaterials(long materialHeaderOid);

    List<String> getProjects(PageObject pageObject, String projectId);

    List<Material> getMaterialsForOrderLine(long orderLineOid);
    
    List<MaterialLine> findMaterialLinesByOrderLineList(List<Long> orderLineList);

    long countPicks(Date fromDate, Date toDate, String[] project, String[] warehouse);

    List<MaterialLine> getMaterialLinesByRequestListId(long requestListOid);

    void delete(PickList pickList);

    List<PickList> findPickListByRequestListId(long requestListOid);

    List<MaterialLine> findProcuredMaterialLinesByMaterialLineIds(List<Long> materialLineIds);
}
