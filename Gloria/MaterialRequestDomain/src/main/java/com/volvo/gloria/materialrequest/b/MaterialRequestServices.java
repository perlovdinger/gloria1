package com.volvo.gloria.materialrequest.b;

import java.util.List;

import com.volvo.gloria.materialRequestProxy.c.MaterialProcureResponseDTO;
import com.volvo.gloria.materialrequest.c.dto.MaterialRequestDTO;
import com.volvo.gloria.materialrequest.c.dto.MaterialRequestLineDTO;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequest;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestLine;
import com.volvo.gloria.util.DocumentDTO;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.paging.c.PageObject;

/**
 * MaterialRequest services interface.
 */
public interface MaterialRequestServices {

    MaterialRequest createMaterialRequest(MaterialRequestDTO materialRequestDTO, String userId) throws GloriaApplicationException;

    MaterialRequest updateMaterialRequest(MaterialRequestDTO materialRequestDTO) throws GloriaApplicationException;

    public MaterialRequest updateMaterialRequest(MaterialRequestDTO materialRequestDTO, String action, String userId) 
            throws GloriaApplicationException;
            
    void deleteMaterialRequest(long materialRequestId) throws GloriaApplicationException;

    PageObject getMaterialRequestlines(long materialRequestId, PageObject pageObject);

    MaterialRequest findMaterialRequestById(long materialRequestOid, String action, String userId) throws GloriaApplicationException;

    PageObject getMaterialRequests(PageObject pageObject, String userId) throws GloriaApplicationException;

    List<MaterialRequestLine> updateMaterialRequestLines(List<MaterialRequestLineDTO> materialRequestLineDTOs, long materialRequestOid)
            throws GloriaApplicationException;

    FileToExportDTO exportMaterialRequestParts(long materialRequestId);

    void uploadMaterialRequestDocuments(Long materialRequestId, DocumentDTO document) throws GloriaApplicationException;

    void changeMaterialRequestState(MaterialProcureResponseDTO transformStoredMaterialProcureResponse) throws GloriaApplicationException;

    MaterialRequestLine createMaterialRequestLine(MaterialRequestLineDTO materialRequestLineDTO, long materialRequestOid) throws GloriaApplicationException;

    MaterialRequestLine updateMaterialRequestLine(MaterialRequestLineDTO materialRequestLineDTO, long materialRequestOid) throws GloriaApplicationException;

    void deleteMaterialRequestLine(long materialRequestOid, String materialRequestLineOids) throws GloriaApplicationException;

    MaterialRequestLine undoRemoveMaterialRequestLine(MaterialRequestLineDTO materialRequestLineDTO, long materialRequestOid, long materialRequestlineOid) throws GloriaApplicationException;
}
