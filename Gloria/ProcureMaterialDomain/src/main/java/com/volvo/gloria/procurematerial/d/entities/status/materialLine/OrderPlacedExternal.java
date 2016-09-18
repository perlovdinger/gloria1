package com.volvo.gloria.procurematerial.d.entities.status.materialLine;

import java.util.List;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.b.beans.MaterialServicesHelper;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteSubLine;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.tracing.c.GloriaTraceabilityConstants;
import com.volvo.gloria.warehouse.b.WarehouseServices;

/**
 * Status after the external order is received.
 */
public class OrderPlacedExternal extends MaterialLineDefaultOperation {

    @Override
    public MaterialLine receive(DeliveryNoteLine deliveryNoteLine, DeliveryNoteSubLine deliveryNoteSubLine, MaterialLine materialLine,
            MaterialHeaderRepository requestHeaderRepository, UserDTO user, TraceabilityRepository traceabilityRepository, MaterialServices materialServices)
            throws GloriaApplicationException {
        return MaterialLineStatusHelper.receiveRegular(deliveryNoteLine, deliveryNoteSubLine, materialLine, requestHeaderRepository, user,
                                                       traceabilityRepository, materialServices);
    }

    @Override
    public void borrow(MaterialLine borrowerMaterialLine, List<MaterialLine> borrowFromMaterialLines, boolean noReturn,
            MaterialHeaderRepository requestHeaderRepository, MaterialServices materialServices, TraceabilityRepository traceabilityRepository,
            WarehouseServices warehouseServices, OrderRepository orderRepository, UserDTO userDTO) throws GloriaApplicationException {
        MaterialLineStatusHelper.borrow(borrowerMaterialLine, borrowFromMaterialLines, requestHeaderRepository, noReturn, materialServices,
                                        traceabilityRepository, warehouseServices, orderRepository, userDTO);
    }

    @Override
    public boolean isReceiveble() {
        return true;
    }
    
    @Override
    public void place(MaterialLine materialLine, TraceabilityRepository traceabilityRepository, String userID, String userName)
            throws GloriaApplicationException {
    }
    
    @Override
    public boolean isPlaceable() {
        return true;
    }
    
    @Override
    public MaterialLine request(MaterialLineDTO materialLineDTO, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, MaterialServices materialServices, UserDTO userDTO, WarehouseServices warehouseServices,
            String deliveryAddressType, String deliveryAddressId, String deliveryAddressName, CommonServices commonServices) throws GloriaApplicationException {
        return MaterialServicesHelper.requestGoods(materialLineDTO, requestHeaderRepository, traceabilityRepository, materialServices, userDTO,
                                                   warehouseServices, deliveryAddressType, deliveryAddressId, deliveryAddressName, commonServices);
    }
    
    @Override
    public MaterialLine release(MaterialLineDTO materialLineDTO, ProcurementServices procurementServices, MaterialHeaderRepository requestHeaderRepository)
            throws GloriaApplicationException {
        return MaterialServicesHelper.releaseGoods(materialLineDTO, procurementServices, requestHeaderRepository);
    }
    
    @Override
    public MaterialLine markAsDecreased(OrderLine orderLine, MaterialLine materialLine, long quantity, UserDTO userDTO, MaterialServices materialServices,
            WarehouseServices warehouseServices, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        return MaterialLineStatusHelper.markAsDecreased(orderLine, materialLine, quantity, userDTO, materialServices, warehouseServices,
                                                        requestHeaderRepository, traceabilityRepository);
    }

    @Override
    public void cancel(MaterialLine materialLine, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository,
            UserDTO userDTO) throws GloriaApplicationException {
        MaterialLineStatusHelper.cancelExternalOrderPlacedMaterialLines(materialLine, requestHeaderRepository, traceabilityRepository, userDTO);
    }

    @Override
    public void noReturn(MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, MaterialLine borrowedFromMaterialLine,
            Material borrowFromMaterial, UserDTO userDTO) throws GloriaApplicationException {
        MaterialLine clonedMaterialline = MaterialLineStatusHelper.cloneMaterialline(borrowedFromMaterialLine);
        if (clonedMaterialline != null) {
            clonedMaterialline.setOrderNo(null);
            borrowFromMaterial.getMaterialLine().add(clonedMaterialline);
            MaterialLineStatusHelper.merge(clonedMaterialline, MaterialLineStatus.REMOVED_BORROWED, requestHeaderRepository, traceabilityRepository, userDTO,
                                           null);
        }
    }
}
