/*
 * Copyright 2009 Volvo Information Technology AB 
 * 
 * Licensed under the Volvo IT Corporate Source License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *      http://java.volvo.com/licenses/CORPORATE-SOURCE-LICENSE 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.volvo.gloria.procurematerial.b;

import java.util.List;

import com.volvo.gloria.common.c.dto.SiteDTO;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.purchaseorder.c.dto.SyncPurchaseOrderTypeDTO;
import com.volvo.gloria.procurematerial.c.ProcureResponsibility;
import com.volvo.gloria.procurematerial.c.dto.MaterialDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialHeaderDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialHeaderGroupingDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.c.dto.PickListDTO;
import com.volvo.gloria.procurematerial.c.dto.ProcureLineDTO;
import com.volvo.gloria.procurematerial.c.dto.RequestGroupDTO;
import com.volvo.gloria.procurematerial.c.dto.RequestListDTO;
import com.volvo.gloria.procurematerial.c.dto.SupplierDTO;
import com.volvo.gloria.procurematerial.d.entities.Buyer;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.PartAlias;
import com.volvo.gloria.procurematerial.d.entities.PartNumber;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.PurchaseOrganisation;
import com.volvo.gloria.procurematerial.d.entities.status.changeid.ChangeIdStatus;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.d.type.modification.ModificationType;
import com.volvo.gloria.procurerequest.c.dto.RequestGatewayDTO;
import com.volvo.gloria.procurerequest.c.dto.RequestHeaderTransformerDTO;
import com.volvo.gloria.procurerequest.c.dto.RequestHeaderVersionTransformerDTO;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.paging.c.PageObject;

/**
 * Procurement service interface.
 */
public interface ProcurementServices {

    void createOrUpdatePurchaseOrder(SyncPurchaseOrderTypeDTO syncPurchaseOrderTypeDTO) throws GloriaApplicationException;

    MaterialHeader findRequestHeaderById(Long requestHeaderOID) throws GloriaApplicationException;

    List<MaterialHeader> findAllMaterialHeaders();

    PageObject getProcurements(List<String> procureResponsibilitiyList, String procureForwardedId, String procureForwardedTeam, String procureLineStatus,
            String assignedMaterialController, String assignedMaterialControllerTeam, String teamType, String showFilter, PageObject pageObject)
            throws GloriaApplicationException;

    ProcureLineDTO findProcureLineById(Long id, String modificationType) throws GloriaApplicationException;

    PageObject getRequestHeaders(PageObject pageObject, Boolean assignedMaterialController, String userId) throws GloriaApplicationException;

    List<ProcureLine> updateProcureLines(List<ProcureLineDTO> procureLineDTOs, String action, String teamId, 
                                         String userId, String finalWarehouseId, String forwardedUserId) throws GloriaApplicationException;

    ProcureLine updateProcureLine(ProcureLineDTO procureLineDTO, String action, String materialLineKeys, String teamId, 
                                  String userId, String finalWarehouseId, boolean multipleProcure, String forwardedUserId)
            throws GloriaApplicationException;

    void addFinanceHeader(FinanceHeader financeHeader);

    void procureLater(List<Long> procurementIds) throws GloriaApplicationException;

    void addProcureLine(ProcureLine procureLine);

    List<ProcureLine> findAllProcureLines();

    PageObject findMaterialsByStatusAndResponsability(ProcureLineStatus procureLineStatus, ProcureResponsibility procureResponsibility,
            String assignedMaterialControllerId, String assignedMaterialControllerTeam, String procureForwardedId, String procureForwardedTeam,
            PageObject pageObject, String materialType, String teamType) throws GloriaApplicationException;

    void createRequisition(ProcureLine procureLine) throws GloriaApplicationException;

    boolean isManualGroupingAllowed(List<Long> requestLineIds, String type) throws GloriaApplicationException;

    Buyer findByBuyerId(String buyerId);

    PageObject findAllBuyerCodes(PageObject pageObject, String organisationCode);

    Buyer addBuyerCode(Buyer buyerCode);

    List<SiteDTO> getConsignorIds(long procureLineId);

    ChangeId manageRequest(List<RequestGatewayDTO> requestGatewayDtos) throws GloriaApplicationException;

    ChangeId manageRequestNoTransaction(List<RequestGatewayDTO> requestGatewayDtos) throws GloriaApplicationException;

    List<ChangeId> findChangeIdByStateAndProcureLineId(long procurelineId, ChangeIdStatus... changeIdStatuses);

    ChangeId findChangeIdById(long changeIdId);

    PageObject findAllChangeIds(PageObject pageObject, String assignedMaterialController, String assignedMaterialControllerTeam)
            throws GloriaApplicationException;

    FileToExportDTO exportOnBuildSite(List<Long> procureLineIds) throws GloriaApplicationException;

    void acceptOrRejectChangeId(String action, long changeIdId, String userId, List<MaterialDTO> materialDTOs) throws GloriaApplicationException;

    List<MaterialHeader> assignOrUnassignMateriaHeaders(List<MaterialHeaderDTO> materialHeaderDTOs, String action, String materialControllerId,
            String userTeamForAssignedUser, String userTeamTypeForAssignedUser, String loggedInUserID) throws GloriaApplicationException;

    void groupMaterials(List<Material> addMaterials) throws GloriaApplicationException;

    MaterialDTO findMaterialById(long id) throws GloriaApplicationException;

    List<Material> getMaterialsByChangeId(long changeIdId);

    List<Material> findAllMaterials();

    Material updateMaterial(MaterialDTO materialDTO, String userId) throws GloriaApplicationException;

    void addPartNumber(PartNumber partNumber);

    void addPartAlias(PartAlias partAlias);

    List<PartAlias> getPartAliases(String partNo);

    PartNumber findVolvoPartWithAliasByPartNumber(String partNumber);

    List<MaterialLine> getMaterialLinesInStock(String partNumber, String partVersion, String partAffiliation, String projectId, String partModification,
            String companyCode);

    List<Material> groupMaterials(List<MaterialHeaderGroupingDTO> materialHeaderGroupDTOs, String pPartNo, String pPartVersion, String pPartName,
            String pPartModification, String userId) throws GloriaApplicationException;

    List<Material> findMaterialByProcureLineId(Long procureLineOid) throws GloriaApplicationException;

    PageObject findMaterialsByProcureLineIds(String procureLineOids, PageObject pageObject);

    PageObject getAccumulatedMaterialLinesInStock(PageObject pageObject, String partNumber, String partVersion, String partAffiliation,
            String projectId, String partModification, String companyCode);

    Material createNewModifiedMaterial(Material material, ProcureLine newProcureLineToAssociate, Long procurementQty, long modificationId,
            ModificationType modificationType, UserDTO userDTO) throws GloriaApplicationException;

    void createAdditionalMaterial(ProcureLine procureLine) throws GloriaApplicationException;

    void sendProcureLineRequsitions(ProcureLine procureLine);

    void createInternalOrder(ProcureLine procureLine, boolean traceMaterialLineLog) throws GloriaApplicationException;

    void reassignMaterialController(List<MaterialHeaderDTO> materialHeaderDTOs, String loggedInUserId, String materialControllerToAssign,
            String userTeamForAssignedUser, String userTeamTypeForAssignedUser) throws GloriaApplicationException;

    void cancelModification(long modificationId) throws GloriaApplicationException;

    PageObject getProcureLinesByInternalProcurerTeam(String procureForwardedId, String procureLineStatus, PageObject pageObject)
            throws GloriaApplicationException;

    List<MaterialDTO> findMaterialsByModificationId(long modificationId);

    List<MaterialHeader> findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(String referenceId, String buildId,
            String outboundLocationId);

    List<MaterialHeader> findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(String requestType, String referenceId, String buildId,
            String outboundLocationId, String changeTechId);

    List<MaterialHeader> findMaterialHeaderByMtrlRequestId(String mtrlRequestId, String referenceId);

    void addPurchaseOrganisation(PurchaseOrganisation purchaseOrganisation);

    List<PurchaseOrganisation> findAllPurchaseOrganizations();

    void validateOrderNoLength(ProcureLineDTO procureLineDTO) throws GloriaApplicationException;

    void validateOrderInfo(ProcureLineDTO procureLineDTO) throws GloriaApplicationException;

    ChangeId findChangeIdByTechId(String changeTechId);

    MaterialHeader createRequestHeader(RequestHeaderTransformerDTO requestHeaderTransformerDTO, String outboundLocationId, Long currentFASequence,
            String changeIdCrId,RequestHeaderVersionTransformerDTO requestHeaderVersionTransformerDTO) throws GloriaApplicationException;

    List<SiteDTO> getSparePartSites();

    void updateSupplierCounterPartInfo(ProcureLine procureLine);

    void updateStatusAndProcureTeamInfo(ProcureLineDTO procureLineDTO, UserDTO userDto, ProcureLine procureLine, List<Material> materials)
            throws GloriaApplicationException;

    List<Material> groupIfMaterialsExist(String materialControllerUserId, Material material) throws GloriaApplicationException;

    List<Material> groupIfMaterialsExist(String materialControllerUserId, Material material, String forwardedUserId, String forwardedTeam)
            throws GloriaApplicationException;

    void regroupMaterialsOnOrderCancel(ProcureLine existingProcureLine, List<Material> materials) throws GloriaApplicationException;

    List<ProcureLine> findProcureLinesByPartInformation(String partNumber, String partVersion, String partAffiliation, String partModification,
            ProcureLineStatus status);


    Material updateMaterialWithCarryOverInfo(Material material, String outboundLocationType, String outboundLocationId);

    List<Material> groupIfMaterialsExist(String materialControllerToBeAssigned, List<Material> materials, String forwardedUserId, String forwardedTeam)
            throws GloriaApplicationException;

    void setProcureLineProcureType(ProcureLine procureLine, List<Material> materials) throws GloriaApplicationException;

    List<SupplierDTO> getProcureLineSuppliers(long procureLineId) throws GloriaApplicationException;

    void updateProcureLineSuppliers(long procureLineId);

    List<MaterialHeader> assignMaterialController(String materialControllerId, String loggedInUserID, String userTeamForAssignedUser,
            List<MaterialHeader> headers) throws GloriaApplicationException;

    void createProcureLine(String materialControllerId, String loggedInUserID, String userTeamForAssignedUser, List<Material> materials)
            throws GloriaApplicationException;

    void suggestProcureType(ProcureLine procureLine, List<Material> materials, boolean existsCarryOver) throws GloriaApplicationException;

    boolean existsCarryOver(List<Material> materials);

    ProcureLine findProcureLineByRequisitionId(String requisitionId);

    MaterialLineDTO updateReferenceIdByMaterialLine(MaterialLineDTO materialLineDTO, String loggedInUserId) throws GloriaApplicationException;

    FileToExportDTO exportToProcure(List<Long> procureLineIds) throws GloriaApplicationException;

    List<PurchaseOrganisation> getAllPurchaseOrganizations();

    void cancelRequestLists(List<RequestListDTO> requestListDTOs, String userId) throws GloriaApplicationException;

    void cancelPickList(PickListDTO pickListDTO, String userId) throws GloriaApplicationException;

    void cancelPickLists(List<RequestGroupDTO> requestGroupDTOs, String userId) throws GloriaApplicationException;

    void triggerPOMessageForSAP(String orderNo, String orderIdGps, String action) throws GloriaApplicationException;

}
