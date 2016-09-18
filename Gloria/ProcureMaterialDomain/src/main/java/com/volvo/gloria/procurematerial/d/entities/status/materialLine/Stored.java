package com.volvo.gloria.procurematerial.d.entities.status.materialLine;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.b.beans.MaterialServicesHelper;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptLine;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.b.WarehouseServices;



public class Stored extends MaterialLineDefaultOperation {
    
    @Override
    public boolean isBorrowable() {
        return true;
    }
    
    @Override
    public boolean isInStock() {
        return true;
    }
    
    @Override
    public void updateZoneToRequestGroup(MaterialLine materialLine) throws GloriaApplicationException {
        // do nothing here
    }
    
    @Override
    public boolean isRevertable() throws GloriaApplicationException {
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
        MaterialLineStatusHelper.revertMaterialLineToOrderPlaced(qtyCancelled, materialLine, deliveryNoteLine, goodsReceiptLine, userDTO, materialServices,
                                                                 warehouseServices, requestHeaderRepository, traceabilityRepository);
    }
    
    @Override
    public MaterialLine updateWithStatusTime(MaterialLine materialLine) {
        materialLine.getMaterialLineStatusTime().setStoredTime(DateUtil.getUTCTimeStamp());
        return materialLine;
    }
    
    @Override
    public void cancel(MaterialLine materialLine, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository,
            UserDTO userDTO) throws GloriaApplicationException {
        if (materialLine.getProcureType() != null && materialLine.getProcureType().isFromStock()) {
            materialLine.getProcureType().revertMaterialLine(materialLine, requestHeaderRepository, userDTO, traceabilityRepository);
        }
        super.cancel(materialLine, requestHeaderRepository, traceabilityRepository, userDTO);
    }
    
    @Override
    public MaterialLine mark(MaterialLine materialLineToMark, long quantity, MaterialServices materialServices,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, UserDTO user) throws GloriaApplicationException {
        return MaterialLineStatusHelper.markForQI(materialLineToMark, quantity, materialServices, requestHeaderRepository, traceabilityRepository, user);
    }
    
    @Override
    public MaterialLine setInspectionStatus(MaterialLine materialLine) {
        InspectionStatus currentInspectionStatus = materialLine.getInspectionStatus();
        if (currentInspectionStatus != null) {
            materialLine.setInspectionStatus(InspectionStatus.OK);
        }
        return materialLine;
    }
}
