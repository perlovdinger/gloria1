package com.volvo.gloria.materialrequest.repositories.b;

import java.util.List;

import com.volvo.gloria.materialrequest.d.entities.MaterialRequest;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestLine;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestObject;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestVersion;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * repository for root MaterialRequest.
 */
public interface MaterialRequestRepository extends GenericRepository<MaterialRequest, Long> {

    PageObject getMaterialRequests(PageObject pageObject, List<String> companyCodes);

    MaterialRequestLine saveOrUpdateMaterialRequestLine(MaterialRequestLine materialRequestLine);

    void deleteMaterialRequestLine(Long materialRequestLineOid);

    MaterialRequestLine findMaterialRequestLineById(Long materialRequestLineOid);

    void deleteMaterialRequestVersion(MaterialRequestVersion materialRequestVersion);

    MaterialRequestVersion findCurrentMaterialRequestVersionById(long materialRequestOid);

    MaterialRequestObject save(MaterialRequestObject materialRequestObject);

    void delete(MaterialRequestObject materialRequestObject);

    MaterialRequest findMaterialRequestByMtrlRequestId(String mtrlRequestId);

    List<MaterialRequestLine> saveOrUpdateMaterialRequestLines(List<MaterialRequestLine> materialRequestLinesToUpdate);

    PageObject findMaterialRequestLinesByHeaderId(PageObject pageObject, long materialRequestId);

    Long getMaxMaterialRequestIdSequence();

    boolean validateMaterialRequest(long materialRequestId, String materialRequestType);
}
