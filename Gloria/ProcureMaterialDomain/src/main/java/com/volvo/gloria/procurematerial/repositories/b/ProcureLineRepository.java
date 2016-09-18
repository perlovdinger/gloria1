package com.volvo.gloria.procurematerial.repositories.b;

import java.util.List;
import java.util.Map;

import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.Requisition;
import com.volvo.gloria.procurematerial.d.entities.Supplier;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * Repository.
 * 
 * Specific to ProcureLine,Procurements,Requesitions
 * 
 */
public interface ProcureLineRepository extends GenericRepository<ProcureLine, Long> {

    ProcureLine findProcureLineById(long procurementOid) throws GloriaApplicationException;

    PageObject getProcurements(List<String> procureResponsibilitiyList, String procureLineStatus, String assignedMaterialController,
            String assignedMaterialControllerTeam, PageObject pageObject, List<String> companyCodes, String showFilter) throws GloriaApplicationException;

    List<ProcureLine> findAllProcureLines();

    void addRequisition(Requisition requisition);

    Requisition findRequisitionById(long requisitionOID);

    ProcureLine findProcureLineByRequisitionId(String requisitionId);

    void deleteRequisition(Requisition requisition);

    PageObject findProcureLinesByUserId(String procureForwardedId, String procureForwardedTeam, String procureLineStatus, PageObject pageObject,
            List<String> companyCodes) throws GloriaApplicationException;

    PageObject findProcureLinesByInternalProcurerTeam(
            String procureForwardedTeam, String procureLineStatus, PageObject pageObject) throws GloriaApplicationException;

    PageObject getReferences(PageObject pageObject);

    PageObject getBuildNames(PageObject pageObject);

    List<ProcureLine> findProcureLineByParameters(Map<String, Object> predicates1);

    Requisition findRequisitionByRequisitionId(String requisitionId);

    List<ProcureLine> findProcureLinesByPartInformation(String partNumber, String partVersion, String partAffiliation, String partModification,
                                                        ProcureLineStatus status);

    Requisition updateRequisition(Requisition requisition);

    Supplier findSupplierById(Long supplierOid);

    Supplier findSupplierBySupplierId(String supplierId);

    List<ProcureLine> findAllProcureLinesByIds(List<Long> procureLineIds) throws GloriaApplicationException;

}
