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
import com.volvo.gloria.warehouse.b.WarehouseServices;

/**
 * Methods for Material Line Operation.
 */
public interface MaterialLineOperation {

    MaterialLine receive(DeliveryNoteLine deliveryNoteLine, DeliveryNoteSubLine deliveryNoteSubLine, MaterialLine materialLine,
            MaterialHeaderRepository requestHeaderRepository, UserDTO user, TraceabilityRepository traceabilityRepository, MaterialServices materialServices)
            throws GloriaApplicationException;

    void borrow(MaterialLine borrowerMaterialLine, List<MaterialLine> borrowFromMaterialLines, boolean noReturn,
            MaterialHeaderRepository requestHeaderRepository, MaterialServices materialServices, TraceabilityRepository traceabilityRepository,
            WarehouseServices warehouseServices, OrderRepository orderRepository, UserDTO userDTO) throws GloriaApplicationException;

    boolean isRevertable() throws GloriaApplicationException;

    boolean isBorrowable() throws GloriaApplicationException;
    
    public void procure(MaterialLine materialLine, TraceabilityRepository traceabilityRepository, MaterialHeaderRepository requestHeaderRepository, UserDTO user)
            throws GloriaApplicationException;

    void qualityInspectMaterial(MaterialLine materialLine, MaterialServices materialServices,
            UserDTO user, TraceabilityRepository traceabilityRepository, DeliveryNoteSubLine deliveryNoteSubLine) throws GloriaApplicationException;
    
    MaterialLine transferReturn(DeliveryNoteLine deliveryNoteLine, DeliveryNoteSubLine deliveryNoteSubLine, MaterialLine materialLine,
            MaterialHeaderRepository requestHeaderRepository, UserDTO user, TraceabilityRepository traceabilityRepository, MaterialServices materialServices,
            Material transferMaterial, WarehouseServices warehouseServices) throws GloriaApplicationException;
    
    void setPickList(PickList pickList, MaterialLine materialLine, MaterialHeaderRepository requestHeaderRepository) throws GloriaApplicationException;
    
    public void place(MaterialLine materialLine, TraceabilityRepository traceabilityRepository, String userId, String userName)
            throws GloriaApplicationException;

    boolean isReceiveble();

    boolean isInStock();

    MaterialLine approveQI(long approvedQuantity, UserDTO user, MaterialLine materialLine, MaterialServices materialServices,
            TraceabilityRepository traceabilityRepository, MaterialHeaderRepository requestHeaderRepository, 
            CommonServices commonServices, boolean isStore, WarehouseServices warehouseServices)
            throws GloriaApplicationException;

    void quarantine(long blockedQuantity, UserDTO user, MaterialLine materialLine, MaterialServices materialServices,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices) throws GloriaApplicationException;

    boolean isShippable();

    boolean isPlaceable();
    
    boolean isPicked();

    void updateStatusToStored(MaterialLine materialLine) throws GloriaApplicationException;

    void updateZoneToRequestGroup(MaterialLine materialLine) throws GloriaApplicationException;

    boolean isReadyForQIApprove();

    void placeIntoZone(MaterialLine splittedMaterialLine, MaterialServices materialServices) throws GloriaApplicationException;

    MaterialLine request(MaterialLineDTO materialLineDTO, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository,
            MaterialServices materialServices, UserDTO userDTO, WarehouseServices warehouseServices, String deliveryAddressType, String deliveryAddressId,
            String deliveryAddressName, CommonServices commonServices) throws GloriaApplicationException;

    MaterialLine store(MaterialLineDTO materialLineDTO, UserDTO userDTO, MaterialHeaderRepository requestHeaderRepository, MaterialServices materialServices,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices, String whSiteId) throws GloriaApplicationException;

    MaterialLine pick(MaterialLineDTO materialLineDTO, UserDTO userDTO, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices, MaterialServices materialServices)
            throws GloriaApplicationException;

    MaterialLine release(MaterialLineDTO materialLineDTO, ProcurementServices procurementServices, MaterialHeaderRepository requestHeaderRepository)
            throws GloriaApplicationException;

    boolean isRemovedDb();

    MaterialLine scrap(MaterialLine materialLine, MaterialLineStatus scrapped2, long scrapQuantity, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, MaterialServices materialServices, UserDTO user) throws GloriaApplicationException;

    boolean isCancelGoodsReceiptAllowed(MaterialLine materialLine);

    void cancelReceivedMaterialLine(long qtyCancelled, DeliveryNoteLine deliveryNoteLine, GoodsReceiptLine goodsReceiptLine, MaterialLine materialLine,
            MaterialServices materialServices, WarehouseServices warehouseServices, DeliveryNoteRepository deliveryNoteRepository,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, UserDTO userDTO) throws GloriaApplicationException;

    void shipOrTransfer(MaterialLine materialLine, MaterialServices materialServices, String loggedInUserId) throws GloriaApplicationException;

    MaterialLine markAsDecreased(OrderLine orderLine, MaterialLine materialLine, long quantity, UserDTO userDTO, MaterialServices materialServices,
            WarehouseServices warehouseServices, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException;

    void cancel(MaterialLine materialLine, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, UserDTO userDTO)
            throws GloriaApplicationException;

    void storeReceiveAndQi(MaterialLine receivedMaterialLine, MaterialServices materialServices, UserDTO user, TraceabilityRepository traceabilityRepository,
            DeliveryNoteSubLine deliveryNoteSubLine, WarehouseServices warehouseServices, MaterialHeaderRepository requestHeaderRepository)
            throws GloriaApplicationException;

    MaterialLine updateWithStatusTime(MaterialLine materialLine);
    
    void noReturn(MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository,
            MaterialLine borrowedFromMaterialLine, Material borrowFromMaterial, UserDTO userDTO) throws GloriaApplicationException;

    MaterialLine unMark(MaterialLine materialLineToUnMark, MaterialLineStatus previousStatus, long quantity, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, MaterialServices materialServices, UserDTO user) throws GloriaApplicationException;

    void trace(MaterialLine materialLine, String action, String actionDetail, String il18MessageCode, UserDTO user,
            TraceabilityRepository traceabilityRepository);

    MaterialLine mark(MaterialLine materialLineToMark, long quantity, MaterialServices materialServices, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, UserDTO user) throws GloriaApplicationException;

    MaterialLine setInspectionStatus(MaterialLine materialLine);
}
