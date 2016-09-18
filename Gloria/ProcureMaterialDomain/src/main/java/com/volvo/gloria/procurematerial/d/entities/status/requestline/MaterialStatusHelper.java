package com.volvo.gloria.procurematerial.d.entities.status.requestline;

import java.util.ArrayList;
import java.util.List;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusHelper;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.tracing.c.GloriaTraceabilityConstants;

/**
 * This class is used for method implementations that are common for several statuses.
 */
public final class MaterialStatusHelper {

    private MaterialStatusHelper() {
    }

    public static void removeMaterial(String mtrRequestVersion, Material materialToRemove, UserDTO userDto, TraceabilityRepository traceabilityRepository) {
        materialToRemove.setStatus(MaterialStatus.REMOVED);
        materialToRemove.setMtrlRequestVersionAccepted(mtrRequestVersion);
        for (MaterialLine materialLine : materialToRemove.getMaterialLine()) {
            if (!materialLine.getStatus().isRemovedDb()) {
                MaterialLineStatusHelper.updateMaterialLineStatus(materialLine, MaterialLineStatus.REMOVED, "Removed",
                                                                  GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE, userDto, traceabilityRepository, true);
            }
        }

        MaterialHeader materialHeader = materialToRemove.getMaterialHeader();
        List<Material> materials = materialHeader.getMaterials();
        if (materials != null && !materials.isEmpty()) {
            boolean hasAllMaterialsRemoved = true;
            for (Material material : materials) {
                if (material.getStatus() != MaterialStatus.REMOVED) {
                    hasAllMaterialsRemoved = false;
                }
            }
            if (hasAllMaterialsRemoved) {
                materialHeader.setActive(false);
            }
        }
    }
        
    public static void regroupMaterials(MaterialHeaderRepository requestHeaderRepo, Material addMaterial, ProcurementServices procurementServices,
            UserServices userServices, String userId, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        List<Material> addMaterials = new ArrayList<Material>();
        addMaterials.add(addMaterial);
        List<Material> materialsToGroup = procurementServices.groupIfMaterialsExist(userId, addMaterial);
        procurementServices.groupMaterials(materialsToGroup);
        if (materialsToGroup != null && !materialsToGroup.isEmpty()) {
            Material materialToGroup = materialsToGroup.get(0);
            requestHeaderRepo.updateMaterial(materialToGroup);
        }

        UserDTO user = userServices.getUser(userId);

        boolean isFirstLine = true;
        for (MaterialLine materialLine : addMaterial.getMaterialLine()) {
            if (isFirstLine) {
                String actionDetail = "Accepted : " + addMaterial.getMtrlRequestVersionAccepted();
                MaterialLineStatusHelper.createTraceabilityLog(materialLine, traceabilityRepository, "Change", actionDetail, userId, user.getUserName(),
                                                               GloriaTraceabilityConstants.ORDER_STAACCEPTED_AGREEDSTA);
                isFirstLine = false;
            }
            MaterialLineStatusHelper.createTraceabilityLog(materialLine, traceabilityRepository, "Assigned", null, userId, user.getUserName(),
                                                           GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE);
        }
    }
}
