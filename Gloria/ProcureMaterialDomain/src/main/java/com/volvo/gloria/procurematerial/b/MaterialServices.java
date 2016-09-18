/*
 * Copyright 2014 Volvo Information Technology AB 
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

import java.util.Date;
import java.util.List;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteLineDTO;
import com.volvo.gloria.procurematerial.c.dto.DispatchNoteDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineQiDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderLineDTO;
import com.volvo.gloria.procurematerial.c.dto.PickListDTO;
import com.volvo.gloria.procurematerial.c.dto.RequestGroupDTO;
import com.volvo.gloria.procurematerial.c.dto.RequestListDTO;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DispatchNote;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.PickList;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.procurematerial.repositories.b.MaterialLineStatusCounterRepository;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.warehouse.c.ZoneType;
import com.volvo.gloria.warehouse.c.dto.BinlocationBalanceDTO;
import com.volvo.gloria.warehouse.d.entities.BinLocation;
import com.volvo.gloria.warehouse.d.entities.BinlocationBalance;

/**
 * Service interface to MaterialDomain material components.
 */
public interface MaterialServices {

    void addMaterials(List<Material> materialList);

    void addMaterialLine(MaterialLine materialLine);

    void addMaterial(Material material);

    Material getMaterialWithMaterialLinesById(long materialOid);

    List<Material> getMaterialByRequisitionId(String requisitionId);

    List<MaterialLine> updateMaterialLines(List<MaterialLineDTO> materialLineDTOs, String fromMaterialLineIds, String action, long scrapQty,
            String confirmationText, String userId, String whSiteId, String deliveryAddressType, String deliveryAddressId, String deliveryAddressName)
            throws GloriaApplicationException;

    List<RequestGroup> getAllRequestGroups();

    MaterialLine updateMaterialLine(MaterialLineDTO materialLineDTO, String fromMaterialLineIds, String action, boolean noReturn, String userId,
            String whSiteId, String deliveryAddressType, String deliveryAddressId, String deliveryAddressName) throws GloriaApplicationException;

    List<Material> getMaterials(Long orderlineId);

    MaterialLine findMaterialLineByIdWarehouse(Long materialLineId, boolean calculateStockBalance, String userId, String whSiteId)
            throws GloriaApplicationException;

    MaterialLine findMaterialLineByIdWarehouse(Long materialLineId, boolean calculateStockBalance, String userId, List<String> whSiteIds)
            throws GloriaApplicationException;

    MaterialLine findMaterialLineByIdProcurement(Long materialLineId, boolean calculateStockBalance, String userId) throws GloriaApplicationException;

    void print(long partId, int labelCopies);

    PageObject getRequestGroup(PageObject pageObject, String userId, String whSiteId) throws GloriaApplicationException;

    void saveRequestList(RequestList requestList);

    PickList findPickListById(long pickListId);

    PageObject findPickListByStatus(PageObject pageObject, String whSiteId, String materialLineStatus, String userId);

    PageObject getMaterialLines(PageObject pageObject, long pickListId, String requested, boolean suggestBinLocation) throws GloriaApplicationException;

    PickList savePickList(PickList instanceToSave);

    List<RequestGroup> updateRequestGroups(List<RequestGroupDTO> requestGroupDTOs, boolean createPickList, String userId) throws GloriaApplicationException;

    List<MaterialLine> findMaterialLines(List<Long> materialLineOids);

    RequestList findRequestListById(long requestListOid);

    List<RequestList> findRequestListByUserId(String requesterUserId, String status);
    
    RequestList updateRequestList(RequestListDTO requestListDTO, String action, String userId) throws GloriaApplicationException;

    List<MaterialLine> getMaterialLinesForRequestList(long requestListOid);

    PickList updatePickListByAction(PickListDTO pickListDTO, String action) throws GloriaApplicationException;

    DispatchNote findDispatchNoteById(long dispatchNoteId);

    void deleteDispatchNote(long dispatchNoteId);

    DispatchNote updateDispatchNote(DispatchNoteDTO dispachNoteDTO, String status, String userId) throws GloriaApplicationException;

    PageObject getRequestLists(PageObject pageObject, String status, String whSiteId, String outBoundLocationId, String requesterId)
            throws GloriaApplicationException;

    DispatchNote createDispatchNote(long requestListId, DispatchNoteDTO dispatchNoteDTO);

    List<MaterialLine> getMaterialLinesWithPartNumberaAndBinlocation(String partNumber, String binlocation, String userId) throws GloriaApplicationException;

    List<RequestGroup> getRequestGroups(long requestlistId);

    RequestGroup findRequestGroupById(long requestGroupId);

    PickList findPickListByCode(String picklistsCode);

    List<RequestGroup> findRequestGroupsByDispatchNoteId(long dispatchNoteId);

    List<MaterialLine> findMaterialLinesByRequestGroupId(long requestGroupId);

    List<MaterialLine> scrapMaterialLines(List<MaterialLineDTO> materialLineDTOs, long scrapQuantity, String confirmationText, String userId)
            throws GloriaApplicationException;

    PageObject getMaterialLineQi(PageObject pageObject, String status, String qiMarking, boolean calculateSuggestedBinLocation, String whSiteId);

    MaterialLineQiDTO updateMaterialLineByQi(MaterialLineQiDTO materialLineQiDTO, String userId) throws GloriaApplicationException;

    List<MaterialLineQiDTO> updateMaterialLinesQi(List<MaterialLineQiDTO> materialLineQiDTOs, String userId) throws GloriaApplicationException;

    DispatchNote createDispatchNoteforRequestList(long requestListOid, DispatchNoteDTO dispatchNoteDTO, String action, String userId)
            throws GloriaApplicationException;

    void sendGoodsReceipt(String companyCode, DeliveryNoteLine deliveryNoteLine, Long approvedQuantity, ProcureType procureType)
            throws GloriaApplicationException;

    void createAndSendProcessPurchaseOrder(Order order, String action) throws GloriaApplicationException;

    PageObject getBorrowableMaterialLines(PageObject pageObject, long materialLineId);

    MaterialLine borrowMaterialLines(long borrowerMaterialLineOid, String borrowFromMaterialLineOids, boolean noReturn,
            TraceabilityRepository traceabilityRepository, UserDTO userDTO) throws GloriaApplicationException;

    PageObject getMaterialLinesForWarehouse(PageObject pageObject, String materialLineStatus, boolean calculateStockBalance, String userId, String whSiteId,
            Boolean suggestBinLocation, String partNo, String transportLabel, String partAffiliation, String partVersion, String partModification,
            String partName) throws GloriaApplicationException;

    FileToExportDTO exportProforma(long requestListId, String whSiteId) throws GloriaApplicationException;

    PageObject findMaterialLinesByDispatchNote(PageObject pageObject, String receiveType, String whSiteId, String deliveryAddressId, String shipmentType,
            String dispatchNoteNo, String partNo, String partVersion, String status);

    PageObject getMaterialLines(PageObject pageObject, String loggedInUser, String userId, String userTeam, String teamType, String expirationDate,
            String expirationTo, String expirationFrom, Boolean allExpired) throws GloriaApplicationException;

    void deleteRequestGroup(RequestGroup requestGroup);

    MaterialLine updateMaterialLine(MaterialLine materialLine);

    void createPlacement(long binLocationOid, MaterialLine materialLine) throws GloriaApplicationException;

    FileToExportDTO exportSupplier(List<Long> orderLineIds, boolean includeDeliveryController, boolean includeOrderLineLogs, boolean includeOrderLogs,
            boolean includeProject, boolean includePlannedDispatchDate, boolean excludeAgreedSTA);

    BinlocationBalance updateBinlocationBalance(BinlocationBalanceDTO binlocationBalanceDto, String comment, String userId, String action, long quantity,
            String binLocationCode) throws GloriaApplicationException;

    List<OrderLineDTO> findOrderLineByMaterialId(long materialOid, String loggedInUserID) throws GloriaApplicationException;

    void printPullLabels(long quantity, long labelCopies, String whSiteId, long materialLineId) throws GloriaApplicationException;

    void printPartLabels(List<DeliveryNoteLineDTO> deliveryNoteLineDTOs, long labelCopies, String whSiteId) throws GloriaApplicationException;

    public DispatchNote findDispatchNote(String dispatchNoteNo);

    Long placeIntoZone(MaterialLine materialLine, ZoneType zoneType) throws GloriaApplicationException;

    void checkAndUpdateRequestListStatusAsPickCompleted(RequestList requestList);

    void reserveMaterialLines(String materiallineIds, String userId, String whSiteId) throws GloriaApplicationException;

    void unlockMaterialLines(String materiallineIds, String userId, String whSiteId);

    void unlockMaterialLine(String userId);

    void reservePicklist(long pickListId, String userId) throws GloriaApplicationException;

    void releasePickList(long pickListId, String userId) throws GloriaApplicationException;

    void releasePickList(String userId);

    List<DeliveryNoteLine> qiApproveDeliveryNoteLine(String deliveryNoteLineIds, String userId) throws GloriaApplicationException;

    String getBinLocationCode(long binLocationOid);

    RequestList manageRequestList(List<MaterialLineDTO> materialLineDTOs, String action, Date requiredDeliveryDate, Long priority,
            String deliveryAddressType, String deliveryAddessId, String deliveryAddressName, String loggedInUserId, Long requestListOid)
            throws GloriaApplicationException;

    MaterialLine findMaterialLineById(Long materialLineOID);

    void createPlacement(BinLocation binLocation, MaterialLine materialLine) throws GloriaApplicationException;

    List<Material> findMaterialsByProcureLineIdForProcureDetails(long procureLineOID) throws GloriaApplicationException;

    void sendQuickPull(MaterialLineDTO materialLineDTOs, Date requiredDeliveryDate, String deliveryAddressType, 
            String deliveryAddessId, String deliveryAddressName, String loggedInUserId, long quantity) throws GloriaApplicationException;

    List<MaterialLine> findMaterialLinesForOrderline(long orderLineOID);

    MaterialLineStatusCounterRepository getMaterialLineStatusCounterRepository();

    void reCreatePlacements(Material material) throws GloriaApplicationException;

    MaterialLineQiDTO unmarkMaterialLineByQi(MaterialLineQiDTO materialLineQiDTO, long quantityToUnmark, String userId) throws GloriaApplicationException;

    List<Material> getMaterialLinesForOrderLine(long orderLineOid);

    RequestList createOrUpdateRequestList(RequestList requestList, Date requiredDeliveryDate, Long priority, String deliveryAddressType,
            String deliveryAddessId, String deliveryAddressName, UserDTO user, List<MaterialLineDTO> materialLineDTOs) throws GloriaApplicationException;

    void cancelRequestList(RequestList requestList, UserDTO userDTO) throws GloriaApplicationException;

    RequestList sendRequestList(RequestList requestList, List<MaterialLineDTO> materialLineDTOs, Date requiredDeliveryDate, Long priority,
            String deliveryAddressType, String deliveryAddessId, String deliveryAddressName, UserDTO user)
            throws GloriaApplicationException;

    RequestList addToRequestList(RequestList requestList, List<MaterialLineDTO> materialLineDTOs, UserDTO user) throws GloriaApplicationException;

    RequestList removeFromRequestList(RequestList requestList, List<MaterialLineDTO> materialLineDTOs, UserDTO user) throws GloriaApplicationException;
}
