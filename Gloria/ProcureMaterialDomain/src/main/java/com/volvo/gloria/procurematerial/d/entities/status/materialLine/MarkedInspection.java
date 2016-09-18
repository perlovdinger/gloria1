package com.volvo.gloria.procurematerial.d.entities.status.materialLine;

import java.util.ArrayList;
import java.util.List;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.beans.MaterialServicesHelper;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteSubLine;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.d.entities.BinLocation;

public class MarkedInspection extends MaterialLineDefaultOperation {
    @Override
    public boolean isBorrowable() {
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
    public MaterialLine scrap(MaterialLine materialLine, MaterialLineStatus scrapped2, long scrapQuantity,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, MaterialServices materialServices, UserDTO user)
            throws GloriaApplicationException {
        return MaterialLineStatusHelper.split(materialLine, scrapped2, scrapQuantity, requestHeaderRepository,
                                              traceabilityRepository, materialServices, user, null);
    }
    
    @Override
    public void borrow(MaterialLine borrowerMaterialLine, List<MaterialLine> borrowFromMaterialLines, boolean noReturn,
            MaterialHeaderRepository requestHeaderRepository, MaterialServices materialServices, TraceabilityRepository traceabilityRepository,
            WarehouseServices warehouseServices, OrderRepository orderRepository, UserDTO userDTO) throws GloriaApplicationException {
        MaterialLineStatusHelper.borrow(borrowerMaterialLine, borrowFromMaterialLines, requestHeaderRepository, noReturn, materialServices,
                                        traceabilityRepository, warehouseServices, orderRepository, userDTO);
    }

    @Override
    public MaterialLine unMark(MaterialLine materialLineToUnMark, MaterialLineStatus previousStatus, long quantity,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, MaterialServices materialServices, UserDTO user)
            throws GloriaApplicationException {
        MaterialLine materialLineUnmarked = MaterialLineStatusHelper.unmarkFromQI(materialLineToUnMark, previousStatus, quantity, requestHeaderRepository,
                                                                                  traceabilityRepository, user);
        if (previousStatus != null && previousStatus == MaterialLineStatus.READY_TO_STORE) {
            MaterialLineStatusHelper.createTraceabilityLog(materialLineToUnMark, traceabilityRepository, "Ready To Store", null, user.getId(),
                                                           user.getUserName(), "");
        }
        return materialLineUnmarked;
    }
    
    @Override
    public MaterialLine setInspectionStatus(MaterialLine materialLine) {
        materialLine.setInspectionStatus(InspectionStatus.INSPECTING);
        return materialLine;
    }
    
    @Override
    public void storeReceiveAndQi(MaterialLine materialLine, MaterialServices materialServices, UserDTO user,
            TraceabilityRepository traceabilityRepository, DeliveryNoteSubLine deliveryNoteSubLine, WarehouseServices warehouseServices,
            MaterialHeaderRepository requestHeaderRepository) throws GloriaApplicationException {
        materialLine.setReservedUserId(null);
        materialLine.setReservedTimeStamp(null);
        
        BinLocation binLocation = warehouseServices.findBinLocationById(deliveryNoteSubLine.getBinLocation());
        MaterialServicesHelper.removePlacement(materialLine, warehouseServices);
        materialLine.setBinLocationCode(binLocation.getCode());
        
        materialLine.setRequestGroup(null);
        
        materialServices.createPlacement(binLocation, materialLine);
        MaterialServicesHelper.updateMaterialLineStatusTime(materialLine);
    }
}
