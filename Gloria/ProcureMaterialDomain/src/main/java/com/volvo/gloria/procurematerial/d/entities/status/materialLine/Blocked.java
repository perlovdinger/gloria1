package com.volvo.gloria.procurematerial.d.entities.status.materialLine;

import java.util.List;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptLine;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.tracing.c.GloriaTraceabilityConstants;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.c.ZoneType;
import com.volvo.gloria.warehouse.d.entities.Placement;

public class Blocked extends MaterialLineDefaultOperation {        
    @Override
    public MaterialLine approveQI(long approvedQuantity, UserDTO user, MaterialLine materialLine, MaterialServices materialServices,
            TraceabilityRepository traceabilityRepository, MaterialHeaderRepository requestHeaderRepository, 
            CommonServices commonServices, boolean isStore, WarehouseServices warehouseServices)
            throws GloriaApplicationException {
        MaterialLineStatusHelper.unBlock(approvedQuantity, user, materialLine, traceabilityRepository);
        MaterialLine approvedMaterialLine = MaterialLineStatusHelper.doShipOrStoreMaterial(user, materialLine, materialServices, traceabilityRepository,
                                                                                           requestHeaderRepository, commonServices, isStore, warehouseServices);
        MaterialLineStatusHelper.createTraceabilityLog(approvedMaterialLine, traceabilityRepository, MaterialLineStatus.QI_OK.name(), null, user.getId(),
                                                       user.getUserName(), GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE);
        return approvedMaterialLine;
    }

    @Override
    public void quarantine(long blockedQuantity, UserDTO user, MaterialLine materialLine, MaterialServices materialServices,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices) throws GloriaApplicationException {
        if (blockedQuantity > 0) {
            Placement placement = warehouseServices.getPlacementByMaterialLine(materialLine.getMaterialLineOID());
            if (placement != null) {
                placement.setQuantity(blockedQuantity);
                warehouseServices.updatePlacement(placement);
            }
        }
    }
    
    @Override
    public boolean isRevertable() throws GloriaApplicationException {
        return true;
    }
    
    @Override
    public boolean isReadyForQIApprove() {
        return true;
    }
    
    @Override
    public void placeIntoZone(MaterialLine materialLine, MaterialServices materialServices) throws GloriaApplicationException {
        materialServices.placeIntoZone(materialLine, ZoneType.QUARANTINE);
    }
    
    @Override
    public MaterialLine scrap(MaterialLine materialLine, MaterialLineStatus scrapped2, long scrapQuantity,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, MaterialServices materialServices, UserDTO user)
            throws GloriaApplicationException {
        return MaterialLineStatusHelper.split(materialLine, scrapped2, scrapQuantity, requestHeaderRepository,
                                              traceabilityRepository, materialServices, user, null);
    }
    
    @Override
    public boolean isCancelGoodsReceiptAllowed(MaterialLine materialLine) {
        return true;
    }
    
    @Override
    public void cancelReceivedMaterialLine(long qtyCancelled, DeliveryNoteLine deliveryNoteLine, GoodsReceiptLine goodsReceiptLine, MaterialLine materialLine,
            MaterialServices materialServices, WarehouseServices warehouseServices, DeliveryNoteRepository deliveryNoteRepository,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, UserDTO userDTO) throws GloriaApplicationException {
        // do nothing
    }
    
    @Override
    public void borrow(MaterialLine borrowerMaterialLine, List<MaterialLine> borrowFromMaterialLines, boolean noReturn,
            MaterialHeaderRepository requestHeaderRepository, MaterialServices materialServices, TraceabilityRepository traceabilityRepository,
            WarehouseServices warehouseServices, OrderRepository orderRepository, UserDTO userDTO) throws GloriaApplicationException {
        MaterialLineStatusHelper.borrow(borrowerMaterialLine, borrowFromMaterialLines, requestHeaderRepository, noReturn, materialServices,
                                        traceabilityRepository, warehouseServices, orderRepository, userDTO);
    }
    
    @Override
    public MaterialLine setInspectionStatus(MaterialLine materialLine) {
        materialLine.setInspectionStatus(InspectionStatus.NOT_OK);
        return materialLine;
    }
}
