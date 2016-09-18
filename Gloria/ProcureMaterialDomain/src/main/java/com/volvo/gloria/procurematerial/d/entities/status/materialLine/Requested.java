package com.volvo.gloria.procurematerial.d.entities.status.materialLine;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.b.beans.MaterialServicesHelper;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.PickList;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.b.WarehouseServices;



public class Requested extends MaterialLineDefaultOperation {
    @Override
    public void setPickList(PickList pickList, MaterialLine materialLine, MaterialHeaderRepository requestHeaderRepository) throws GloriaApplicationException {
        if (!materialLine.isRequestedExcluded()) {
            materialLine.setPickList(pickList);
            requestHeaderRepository.updateMaterialLine(materialLine);
        }
    }
    
    @Override
    public void updateStatusToStored(MaterialLine materialLine) throws GloriaApplicationException {
       materialLine.setStatus(MaterialLineStatus.REQUESTED);
       materialLine.setStatusDate(DateUtil.getCurrentUTCDateTime());
    }
    
    @Override
    public void updateZoneToRequestGroup(MaterialLine materialLine) throws GloriaApplicationException {
        materialLine.getRequestGroup().setZoneId(materialLine.getZoneCode());
    }
    
    @Override
    public MaterialLine pick(MaterialLineDTO materialLineDTO, UserDTO userDTO, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices, MaterialServices materialServices)
            throws GloriaApplicationException {
        return MaterialServicesHelper.pickGoods(materialLineDTO, userDTO, requestHeaderRepository, traceabilityRepository, warehouseServices, materialServices);
    }
    
    @Override
    public MaterialLine store(MaterialLineDTO materialLineDTO, UserDTO userDTO, MaterialHeaderRepository requestHeaderRepository,
            MaterialServices materialServices, TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices, String whSiteId)
            throws GloriaApplicationException {
        return MaterialServicesHelper.storeGoods(materialLineDTO, userDTO, requestHeaderRepository, materialServices, traceabilityRepository,
                                                 warehouseServices, whSiteId);
    }
    
    @Override
    public MaterialLine updateWithStatusTime(MaterialLine materialLine) {
        materialLine.getMaterialLineStatusTime().setRequestTime(DateUtil.getUTCTimeStamp());
        return materialLine;
    }
}
