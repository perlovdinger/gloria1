package com.volvo.gloria.procurematerial.d.entities.status.materialLine;

import java.util.List;

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
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.tracing.c.GloriaTraceabilityConstants;
import com.volvo.gloria.warehouse.b.WarehouseServices;

public class MaterialLineDefaultOperation implements MaterialLineOperation {

    public static final String EXCEPTION_MESSAGE = " operation is not supported in current state.";

    @Override
    public MaterialLine receive(DeliveryNoteLine deliveryNoteLine, DeliveryNoteSubLine deliveryNoteSubLine, MaterialLine materialLine,
            MaterialHeaderRepository requestHeaderRepository, UserDTO user, TraceabilityRepository traceabilityRepository, MaterialServices materialServices)
            throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "receive()" + EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public void borrow(MaterialLine borrowerMaterialLine, List<MaterialLine> borrowFromMaterialLines, boolean noReturn,
            MaterialHeaderRepository requestHeaderRepository, MaterialServices materialServices, TraceabilityRepository traceabilityRepository,
            WarehouseServices warehouseServices, OrderRepository orderRepository, UserDTO userDTO) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "borrow()" + EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public boolean isRevertable() throws GloriaApplicationException {
        return false;
    }

    @Override
    public boolean isBorrowable() throws GloriaApplicationException {
        throw new GloriaApplicationException(GloriaExceptionConstants.CANNOT_BORROW, "Cannot borrow in this state.");
    }

    @Override
    public void procure(MaterialLine materialLine, TraceabilityRepository traceabilityRepository, MaterialHeaderRepository requestHeaderRepository, UserDTO user)
            throws GloriaApplicationException {
        // do nothing
    }

    /**
     * Do nothing as default.
     */
    @Override
    public void qualityInspectMaterial(MaterialLine materialLine, MaterialServices materialServices, UserDTO user,
            TraceabilityRepository traceabilityRepository, DeliveryNoteSubLine deliveryNoteSubLine) throws GloriaApplicationException {
    }

    @Override
    public MaterialLine transferReturn(DeliveryNoteLine deliveryNoteLine, DeliveryNoteSubLine deliveryNoteSubLine, MaterialLine materialLine,
            MaterialHeaderRepository requestHeaderRepository, UserDTO user, TraceabilityRepository traceabilityRepository, MaterialServices materialServices,
            Material transferMaterial, WarehouseServices warehouseServices) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "transferReturn()" + EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public void setPickList(PickList pickList, MaterialLine materialLine, MaterialHeaderRepository requestHeaderRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "setPickList()" + EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public void place(MaterialLine materialLine, TraceabilityRepository traceabilityRepository, String userId, String userName)
            throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "place()" + EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public boolean isReceiveble() {
        return false;
    }

    @Override
    public boolean isInStock() {
        return false;
    }

    @Override
    public MaterialLine approveQI(long approvedQuantity, UserDTO user, MaterialLine materialLine, MaterialServices materialServices,
            TraceabilityRepository traceabilityRepository, MaterialHeaderRepository requestHeaderRepository, CommonServices commonServices, boolean isStore,
            WarehouseServices warehouseServices) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "approveQI()" + EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public void quarantine(long blockedQuantity, UserDTO user, MaterialLine materialLine, MaterialServices materialServices,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "quarantine()" + EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public boolean isShippable() {
        return false;
    }

    @Override
    public boolean isPlaceable() {
        return false;
    }

    @Override
    public void updateStatusToStored(MaterialLine materialLine) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "updateStatusToStored()" + EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public void updateZoneToRequestGroup(MaterialLine materialLine) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "updateZoneToRequestGroup()" + EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public boolean isReadyForQIApprove() {
        return false;
    }

    @Override
    public void placeIntoZone(MaterialLine splittedMaterialLine, MaterialServices materialServices) throws GloriaApplicationException {
    }

    @Override
    public MaterialLine request(MaterialLineDTO materialLineDTO, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, MaterialServices materialServices, UserDTO userDTO, WarehouseServices warehouseServices,
            String deliveryAddressType, String deliveryAddressId, String deliveryAddressName, CommonServices commonServices) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "request()" + EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public MaterialLine store(MaterialLineDTO materialLineDTO, UserDTO userDTO, MaterialHeaderRepository requestHeaderRepository,
            MaterialServices materialServices, TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices, String whSiteId)
            throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "store()" + EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public MaterialLine pick(MaterialLineDTO materialLineDTO, UserDTO userDTO, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices, MaterialServices materialServices)
            throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "pick()" + EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public MaterialLine release(MaterialLineDTO materialLineDTO, ProcurementServices procurementServices, MaterialHeaderRepository requestHeaderRepository)
            throws GloriaApplicationException {
        throw new GloriaApplicationException(GloriaExceptionConstants.RELEASE_OPERATION_NOT_SUPPORTED, "Release operation is not supported in current state.");
    }

    @Override
    public boolean isRemovedDb() {
        return false;
    }

    @Override
    public MaterialLine scrap(MaterialLine materialLine, MaterialLineStatus scrapped2, long scrapQuantity, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, MaterialServices materialServices, UserDTO user) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "scrap()" + EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public boolean isCancelGoodsReceiptAllowed(MaterialLine materialLine) {
        return false;
    }

    @Override
    public void cancelReceivedMaterialLine(long qtyCancelled, DeliveryNoteLine deliveryNoteLine, GoodsReceiptLine goodsReceiptLine, MaterialLine materialLine,
            MaterialServices materialServices, WarehouseServices warehouseServices, DeliveryNoteRepository deliveryNoteRepository,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, UserDTO userDTO) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "cancelReceivedMaterialLine()" + EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public MaterialLine markAsDecreased(OrderLine orderLine, MaterialLine materialLine, long quantity, UserDTO userDTO, MaterialServices materialServices,
            WarehouseServices warehouseServices, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        return null;
    }

    @Override
    public void shipOrTransfer(MaterialLine materialLine, MaterialServices materialServices, String loggedInUserId) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "shipOrTransfer()" + EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public boolean isPicked() {
        return false;
    }

    @Override
    public void cancel(MaterialLine materialLine, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository,
            UserDTO userDTO) throws GloriaApplicationException {
        materialLine.setOrderCancelled(true);
        MaterialLineStatusHelper.createTraceabilityLog(materialLine, traceabilityRepository, "Order Cancelled", null, "GPS", "GPS",
                                                       GloriaTraceabilityConstants.ORDER_CANCELLED);
    }

    @Override
    public void storeReceiveAndQi(MaterialLine receivedMaterialLine, MaterialServices materialServices, UserDTO user,
            TraceabilityRepository traceabilityRepository, DeliveryNoteSubLine deliveryNoteSubLine, WarehouseServices warehouseServices,
            MaterialHeaderRepository requestHeaderRepository) throws GloriaApplicationException {
    }

    @Override
    public MaterialLine updateWithStatusTime(MaterialLine materialLine) {
        return materialLine;
    }

    @Override
    public void noReturn(MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, MaterialLine borrowedFromMaterialLine,
            Material borrowFromMaterial, UserDTO userDTO) throws GloriaApplicationException {
        MaterialLineStatusHelper.merge(borrowedFromMaterialLine, MaterialLineStatus.REMOVED_BORROWED, requestHeaderRepository, traceabilityRepository, userDTO,
                                       null);
    }
    
    @Override
    public MaterialLine mark(MaterialLine materialLineToMark, long quantity, MaterialServices materialServices,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, UserDTO user) throws GloriaApplicationException {
        return materialLineToMark;
    }
    
    @Override
    public MaterialLine unMark(MaterialLine materialLineToUnMark, MaterialLineStatus previousStatus, long quantity,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, MaterialServices materialServices, UserDTO user)
            throws GloriaApplicationException {
        return materialLineToUnMark;
    }

    @Override
    public void trace(MaterialLine materialLine, String action, String actionDetail, String il18MessageCode, UserDTO user,
            TraceabilityRepository traceabilityRepository) {
        if (user != null) {
            MaterialLineStatusHelper.createTraceabilityLog(materialLine, traceabilityRepository, action, actionDetail, user.getId(), user.getUserName(),
                                                           il18MessageCode);
        }
    }
    
    @Override
    public MaterialLine setInspectionStatus(MaterialLine materialLine) {
        return materialLine;
    }   
}
