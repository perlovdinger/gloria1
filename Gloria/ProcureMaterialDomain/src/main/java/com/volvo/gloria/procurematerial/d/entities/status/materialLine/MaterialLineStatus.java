package com.volvo.gloria.procurematerial.d.entities.status.materialLine;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteSubLine;
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptLine;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.PickList;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.b.WarehouseServices;

public enum MaterialLineStatus implements MaterialLineOperation {

    CREATED(new Created()),
    WAIT_TO_PROCURE(new WaitToProcure()),
    ORDER_PLACED_INTERNAL(new OrderPlacedInternal()),
    REQUISITION_SENT(new RequisitionSent()),
    ORDER_PLACED_EXTERNAL(new OrderPlacedExternal()),
    RECEIVED(new Received()),
    QI_READY(new QiReady()),
    BLOCKED(new Blocked()),
    QI_OK(new QiOk()),
    READY_TO_STORE(new ReadyToStore()),
    STORED(new Stored()),
    REQUESTED(new Requested()),
    READY_TO_SHIP(new ReadyToShip()),
    SHIPPED(new Shipped()),
    SCRAPPED(new Scrapped()),
    MISSING(new Missing()),
    DEVIATED(new Deviated()),
    REMOVED(new Removed()),
    REMOVED_BORROWED(new RemovedBorrowed()),
    REMOVED_DB(new RemovedDb()),
    IN_TRANSFER(new InTransfer()),
    MARKED_INSPECTION(new MarkedInspection()),
    QTY_DECREASED(new QtyDecreased());
    
    private final MaterialLineOperation materialLineOperations;

    MaterialLineStatus(MaterialLineOperation materialLineOperation) {
        this.materialLineOperations = materialLineOperation;
    }

    @Override
    public MaterialLine receive(DeliveryNoteLine deliveryNoteLine, DeliveryNoteSubLine deliveryNoteSubLine, MaterialLine materialLine,
            MaterialHeaderRepository requestHeaderRepository, UserDTO user, TraceabilityRepository traceabilityRepository, MaterialServices materialServices)
            throws GloriaApplicationException {
       return materialLineOperations.receive(deliveryNoteLine, deliveryNoteSubLine, materialLine, requestHeaderRepository, user, traceabilityRepository,
                                       materialServices);
    }
    
    @Override
    public void borrow(MaterialLine borrowerMaterialLine, java.util.List<MaterialLine> borrowFromMaterialLines, boolean noReturn,
            MaterialHeaderRepository requestHeaderRepository, MaterialServices materialServices, TraceabilityRepository traceabilityRepository,
            WarehouseServices warehouseServices, OrderRepository orderRepository, UserDTO userDTO) throws GloriaApplicationException {
        materialLineOperations.borrow(borrowerMaterialLine, borrowFromMaterialLines, noReturn, requestHeaderRepository, materialServices,
                                      traceabilityRepository, warehouseServices, orderRepository, userDTO);
    }

    @Override
    public boolean isRevertable() throws GloriaApplicationException {
        return materialLineOperations.isRevertable();
    }
    
    @Override
    public boolean isBorrowable() throws GloriaApplicationException {
        return materialLineOperations.isBorrowable();
    }

    @Override
    public void procure(MaterialLine materialLine, TraceabilityRepository traceabilityRepository, MaterialHeaderRepository requestHeaderRepository, UserDTO user)
            throws GloriaApplicationException {
        materialLineOperations.procure(materialLine, traceabilityRepository, requestHeaderRepository, user);
    }

    @Override
    public void qualityInspectMaterial(MaterialLine materialLine, MaterialServices materialServices, 
            UserDTO user, TraceabilityRepository traceabilityRepository, DeliveryNoteSubLine deliveryNoteSubLine) throws GloriaApplicationException {
        materialLineOperations.qualityInspectMaterial(materialLine, materialServices, user, traceabilityRepository, deliveryNoteSubLine);
    }
    
    @Override
    public MaterialLine transferReturn(DeliveryNoteLine deliveryNoteLine, DeliveryNoteSubLine deliveryNoteSubLine, MaterialLine materialLine,
            MaterialHeaderRepository requestHeaderRepository, UserDTO user, TraceabilityRepository traceabilityRepository, MaterialServices materialServices,
            Material transferMaterial, WarehouseServices warehouseServices) throws GloriaApplicationException {
        return materialLineOperations.transferReturn(deliveryNoteLine, deliveryNoteSubLine, materialLine, requestHeaderRepository, user,
                                                     traceabilityRepository, materialServices, transferMaterial, warehouseServices);
    }

    @Override
    public void setPickList(PickList pickList, MaterialLine materialLine, MaterialHeaderRepository requestHeaderRepository) throws GloriaApplicationException {
        materialLineOperations.setPickList(pickList, materialLine, requestHeaderRepository);
    }

    @Override
    public void place(MaterialLine materialLine, TraceabilityRepository traceabilityRepository, String userId, String userName)
            throws GloriaApplicationException {
        materialLineOperations.place(materialLine, traceabilityRepository, userId, userName);
    }
    
    @Override
    public boolean isReceiveble() {
        return materialLineOperations.isReceiveble();
    }

    @Override
    public boolean isInStock() {
        return materialLineOperations.isInStock();
    }

    @Override
    public MaterialLine approveQI(long approvedQuantity, UserDTO user, MaterialLine materialLine, MaterialServices materialServices,
            TraceabilityRepository traceabilityRepository, MaterialHeaderRepository requestHeaderRepository, 
            CommonServices commonServices, boolean isStore, WarehouseServices warehouseServices)
            throws GloriaApplicationException {
       return materialLineOperations.approveQI(approvedQuantity, user, materialLine, materialServices, traceabilityRepository, requestHeaderRepository,
                                         commonServices, isStore, warehouseServices);
    }

    @Override
    public void quarantine(long blockedQuantity, UserDTO user, MaterialLine materialLine, MaterialServices materialServices,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices) throws GloriaApplicationException {
        materialLineOperations.quarantine(blockedQuantity, user, materialLine, materialServices, traceabilityRepository, warehouseServices);
    }

    @Override
    public boolean isShippable() {
        return materialLineOperations.isShippable();
    }

    @Override
    public boolean isPlaceable() {
        return materialLineOperations.isPlaceable();
    }
    
    @Override
    public void updateStatusToStored(MaterialLine materialLine) throws GloriaApplicationException {
       materialLineOperations.updateStatusToStored(materialLine);
    }

    @Override
    public void updateZoneToRequestGroup(MaterialLine materialLine) throws GloriaApplicationException {
        materialLineOperations.updateZoneToRequestGroup(materialLine);
    }

    @Override
    public boolean isReadyForQIApprove() {
        return materialLineOperations.isReadyForQIApprove();
    }

    @Override
    public void placeIntoZone(MaterialLine materialLine, MaterialServices materialServices) throws GloriaApplicationException {
        materialLineOperations.placeIntoZone(materialLine, materialServices);
    }

    @Override
    public MaterialLine request(MaterialLineDTO materialLineDTO, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, MaterialServices materialServices, UserDTO userDTO, WarehouseServices warehouseServices,
            String deliveryAddressType, String deliveryAddressId, String deliveryAddressName, CommonServices commonServices) throws GloriaApplicationException {
        return materialLineOperations.request(materialLineDTO, requestHeaderRepository, traceabilityRepository, materialServices, userDTO, warehouseServices,
                                              deliveryAddressType, deliveryAddressId, deliveryAddressName, commonServices);
    }

    @Override
    public MaterialLine store(MaterialLineDTO materialLineDTO, UserDTO userDTO, MaterialHeaderRepository requestHeaderRepository,
            MaterialServices materialServices, TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices, String whSiteId)
            throws GloriaApplicationException {
        return materialLineOperations.store(materialLineDTO, userDTO, requestHeaderRepository, materialServices, traceabilityRepository, warehouseServices,
                                            whSiteId);
    }

    @Override
    public MaterialLine pick(MaterialLineDTO materialLineDTO, UserDTO userDTO, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices, MaterialServices materialServices)
            throws GloriaApplicationException {
        return materialLineOperations.pick(materialLineDTO, userDTO, requestHeaderRepository, traceabilityRepository, warehouseServices, materialServices);
    }

    @Override
    public MaterialLine release(MaterialLineDTO materialLineDTO, ProcurementServices procurementServices, MaterialHeaderRepository requestHeaderRepository)
            throws GloriaApplicationException {
        return materialLineOperations.release(materialLineDTO, procurementServices, requestHeaderRepository);
    }

    @Override
    public boolean isRemovedDb() {
        return materialLineOperations.isRemovedDb();
    }

    @Override
    public MaterialLine scrap(MaterialLine materialLine, MaterialLineStatus scrapped2, long scrapQuantity, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, MaterialServices materialServices, UserDTO user) throws GloriaApplicationException {
        return materialLineOperations.scrap(materialLine, scrapped2, scrapQuantity, requestHeaderRepository, traceabilityRepository, materialServices, user);
    }

    @Override
    public boolean isCancelGoodsReceiptAllowed(MaterialLine materialLine) {
        return materialLineOperations.isCancelGoodsReceiptAllowed(materialLine);
    }

    @Override
    public void cancelReceivedMaterialLine(long qtyCancelled, DeliveryNoteLine deliveryNoteLine, GoodsReceiptLine goodsReceiptLine, MaterialLine materialLine,
            MaterialServices materialServices, WarehouseServices warehouseServices, DeliveryNoteRepository deliveryNoteRepository,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, UserDTO userDTO) throws GloriaApplicationException {
        materialLineOperations.cancelReceivedMaterialLine(qtyCancelled, deliveryNoteLine, goodsReceiptLine, materialLine, materialServices, warehouseServices,
                                                          deliveryNoteRepository, requestHeaderRepository, traceabilityRepository, userDTO);
    }
    
    @Override
    public void shipOrTransfer(MaterialLine materialLine, MaterialServices materialServices, String loggedInUserId) throws GloriaApplicationException {
        materialLineOperations.shipOrTransfer(materialLine, materialServices, loggedInUserId);
    }
    
    @Override
    public MaterialLine markAsDecreased(OrderLine orderLine, MaterialLine materialLine, long quantity, UserDTO userDTO, MaterialServices materialServices,
            WarehouseServices warehouseServices, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        return materialLineOperations.markAsDecreased(orderLine, materialLine, quantity, userDTO, materialServices, warehouseServices, requestHeaderRepository,
                                                      traceabilityRepository);
    }
    
    @Override
    public boolean isPicked() {
        return materialLineOperations.isPicked();
    }

    @Override
    public void cancel(MaterialLine materialLine, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, UserDTO userDTO)
            throws GloriaApplicationException {
        materialLineOperations.cancel(materialLine, requestHeaderRepository, traceabilityRepository, userDTO);
    }

    @Override
    public void storeReceiveAndQi(MaterialLine receivedMaterialLine, MaterialServices materialServices, UserDTO user,
            TraceabilityRepository traceabilityRepository, DeliveryNoteSubLine deliveryNoteSubLine, WarehouseServices warehouseServices,
            MaterialHeaderRepository requestHeaderRepository) throws GloriaApplicationException {
        materialLineOperations.storeReceiveAndQi(receivedMaterialLine, materialServices, user, traceabilityRepository, deliveryNoteSubLine, warehouseServices,
                                                 requestHeaderRepository);
    }
    
    @Override
    public MaterialLine updateWithStatusTime(MaterialLine materialLine) {
        return materialLineOperations.updateWithStatusTime(materialLine);
    }

    @Override
    public void noReturn(MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, MaterialLine borrowedFromMaterialLine,
            Material borrowFromMaterial, UserDTO userDTO) throws GloriaApplicationException {
        materialLineOperations.noReturn(requestHeaderRepository, traceabilityRepository, borrowedFromMaterialLine, borrowFromMaterial, userDTO);
    }
    
    @Override
    public MaterialLine unMark(MaterialLine materialLineToUnMark, MaterialLineStatus previousStatus, long quantity,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, MaterialServices materialServices, UserDTO user)
            throws GloriaApplicationException {
        return materialLineOperations.unMark(materialLineToUnMark, previousStatus, quantity, requestHeaderRepository, traceabilityRepository, materialServices,
                                             user);
    }

    @Override
    public void trace(MaterialLine materialLine, String action, String actionDetail, String il18MessageCode, UserDTO user, TraceabilityRepository traceabilityRepository) {
        materialLineOperations.trace(materialLine, action, actionDetail, il18MessageCode, user, traceabilityRepository);
    }
    
    @Override
    public MaterialLine mark(MaterialLine materialLineToMark, long quantity, MaterialServices materialServices, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, UserDTO user) throws GloriaApplicationException {
        return materialLineOperations.mark(materialLineToMark, quantity, materialServices, requestHeaderRepository, traceabilityRepository, user);
    }
    
    @Override
    public MaterialLine setInspectionStatus(MaterialLine materialLine) {
        return materialLineOperations.setInspectionStatus(materialLine);
    }
}
