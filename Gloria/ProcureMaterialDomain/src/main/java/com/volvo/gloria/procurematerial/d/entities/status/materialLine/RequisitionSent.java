package com.volvo.gloria.procurematerial.d.entities.status.materialLine;

import java.util.List;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.tracing.c.GloriaTraceabilityConstants;
import com.volvo.gloria.warehouse.b.WarehouseServices;


/**
 * Status after the external order requisition is sent to GPS.
 */
public class RequisitionSent extends MaterialLineDefaultOperation {

    @Override
    public boolean isRevertable() {
        return true;
    }

    @Override
    public void place(MaterialLine materialLine, TraceabilityRepository traceabilityRepository, String userID, String userName)
            throws GloriaApplicationException {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userID);
        userDTO.setFirstName(userName);
        MaterialLineStatusHelper.updateMaterialLineStatus(materialLine, MaterialLineStatus.ORDER_PLACED_EXTERNAL, "Placed External",
                                                          GloriaTraceabilityConstants.ORDER_STAACCEPTED, userDTO, traceabilityRepository, true);
    }    
    
    @Override
    public boolean isPlaceable() {
        return true;
    }
    
    @Override
    public MaterialLine markAsDecreased(OrderLine orderLine, MaterialLine materialLine, long quantity, UserDTO userDTO, MaterialServices materialServices,
            WarehouseServices warehouseServices, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        return MaterialLineStatusHelper.markAsDecreased(orderLine, materialLine, quantity, userDTO, materialServices, warehouseServices,
                                                        requestHeaderRepository, traceabilityRepository);
    }
    
    @Override
    public void borrow(MaterialLine borrowerMaterialLine, List<MaterialLine> borrowFromMaterialLines, boolean noReturn,
            MaterialHeaderRepository requestHeaderRepository, MaterialServices materialServices, TraceabilityRepository traceabilityRepository,
            WarehouseServices warehouseServices, OrderRepository orderRepository, UserDTO userDTO) throws GloriaApplicationException {
        MaterialLineStatusHelper.borrow(borrowerMaterialLine, borrowFromMaterialLines, requestHeaderRepository, noReturn, materialServices,
                                        traceabilityRepository, warehouseServices, orderRepository, userDTO);
    }
}
