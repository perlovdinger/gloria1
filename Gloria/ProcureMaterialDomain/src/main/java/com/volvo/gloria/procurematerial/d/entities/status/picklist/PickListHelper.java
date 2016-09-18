package com.volvo.gloria.procurematerial.d.entities.status.picklist;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.PickList;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusHelper;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.tracing.c.GloriaTraceabilityConstants;

public final class PickListHelper {

    private PickListHelper() {

    }

    public static void cancel(PickList pickList, UserDTO userDTO, MaterialHeaderRepository materialHeaderRepository,
            TraceabilityRepository traceabilityRepository) {
        if (pickList != null) {
            for (RequestGroup requestGroup : pickList.getRequestGroups()) {
                requestGroup.setPickList(null);
                for (MaterialLine materialLine : requestGroup.getMaterialLines()) {
                    materialLine.setPickList(null);
                    MaterialLineStatusHelper.createTraceabilityLog(materialLine, traceabilityRepository, "Pick List Cancelled",
                                                                   "Pick List Code : " + pickList.getCode(), userDTO.getId(), userDTO.getUserName(),
                                                                   GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE);
                }
            }
            pickList.setRequestGroups(null);
            pickList.setMaterialLines(null);
            materialHeaderRepository.delete(pickList);
        }
    }
}
