package com.volvo.gloria.procurematerial.d.entities.status.materialLine;

import java.util.ArrayList;
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
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptLine;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.entities.status.requestlist.RequestListStatus;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.d.entities.BinLocation;

public class ReadyToStore extends MaterialLineDefaultOperation {    
    @Override
    public boolean isBorrowable() {
        return true;
    }
    
    @Override
    public boolean isInStock() {
        return true;
    }

    @Override
    public void updateStatusToStored(MaterialLine materialLine) throws GloriaApplicationException {
       materialLine.setStatus(MaterialLineStatus.STORED);
       materialLine.setStatusDate(DateUtil.getCurrentUTCDateTime());
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
    public MaterialLine store(MaterialLineDTO materialLineDTO, UserDTO userDTO, MaterialHeaderRepository requestHeaderRepository,
            MaterialServices materialServices, TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices, String whSiteId)
            throws GloriaApplicationException {
        return MaterialServicesHelper.storeGoods(materialLineDTO, userDTO, requestHeaderRepository, materialServices, traceabilityRepository,
                                                 warehouseServices, whSiteId);
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
    public void storeReceiveAndQi(MaterialLine materialLine, MaterialServices materialServices, UserDTO user, TraceabilityRepository traceabilityRepository,
            DeliveryNoteSubLine deliveryNoteSubLine, WarehouseServices warehouseServices, MaterialHeaderRepository requestHeaderRepository)
            throws GloriaApplicationException {
        materialLine.setReservedUserId(null);
        materialLine.setReservedTimeStamp(null);

        BinLocation binLocation = warehouseServices.findBinLocationById(deliveryNoteSubLine.getBinLocation());
        MaterialServicesHelper.removePlacement(materialLine, warehouseServices);
        materialLine.setBinLocationCode(binLocation.getCode());

        materialLine = MaterialLineStatusHelper.merge(materialLine, MaterialLineStatus.STORED, materialLine.getQuantity(), requestHeaderRepository,
                                                      traceabilityRepository, user, null);
        // ensure directSend attributes to NO, if the item was requested, and on receive instead of SHIP it was STORED into a binlocation
        materialLine.setRequestedExcluded(false);
        materialLine.setDirectSend(DirectSendType.NO);
        RequestGroup requestGroup = materialLine.getRequestGroup();

        List<MaterialLine> materialLines = requestGroup != null ? requestGroup.getMaterialLines() : new ArrayList<MaterialLine>();
        if (materialLines.size() == 1) {
            RequestList requestList = requestGroup.getRequestList();
            List<RequestGroup> reqGroups = requestList.getRequestGroups();
            if (reqGroups.size() == 1) {
                requestHeaderRepository.deleteRequestList(materialLine.getRequestGroup().getRequestList());
            } else {
                requestHeaderRepository.deleteRequestGroup(requestGroup);
                requestList.setStatus(RequestListStatus.PICK_COMPLETED);
            }
        }

        materialLine.setRequestGroup(null);

        materialServices.createPlacement(binLocation, materialLine);
        MaterialServicesHelper.updateMaterialLineStatusTime(materialLine);
        
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
