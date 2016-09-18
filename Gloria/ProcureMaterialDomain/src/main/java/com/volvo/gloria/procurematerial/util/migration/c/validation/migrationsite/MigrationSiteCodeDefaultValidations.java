package com.volvo.gloria.procurematerial.util.migration.c.validation.migrationsite;

import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.findUserNameForUserId;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.markAsInvalid;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;

public class MigrationSiteCodeDefaultValidations implements MigrationSiteCodeValidations {

    @Override
    public void validateApplySiteSpecificRulesForOpenOrder(OrderMigrationDTO orderMigrationDTO, String suffix, String shipToId,
            DeliveryFollowUpTeam deliveryFollowUpTeam, CommonServices commonServices) {
        if (StringUtils.isEmpty(shipToId)) {
            markAsInvalid(orderMigrationDTO, "SUFFIX not supported/missing!");
        } else {
            updateDeliveryInformation(orderMigrationDTO, suffix, shipToId, deliveryFollowUpTeam, commonServices);
        }
    }

    @Override
    public void validateApplySiteSpecificRulesForClosedOrder(OrderMigrationDTO orderMigrationDTO, String suffix, String shipToId,
            DeliveryFollowUpTeam deliveryFollowUpTeam, CommonServices commonServices) {
        updateDeliveryInformation(orderMigrationDTO, suffix, shipToId, deliveryFollowUpTeam, commonServices);
    }

    protected void updateDeliveryInformation(OrderMigrationDTO orderMigrationDTO, String suffix, String shipToId, DeliveryFollowUpTeam deliveryFollowUpTeam,
            CommonServices commonServices) {
        orderMigrationDTO.setShipToId(shipToId);
        if (deliveryFollowUpTeam != null) {
            orderMigrationDTO.setDeliveryControllerTeam(deliveryFollowUpTeam.getName());
            String deliveryControllerUserId = commonServices.matchDeliveryController(deliveryFollowUpTeam, suffix, orderMigrationDTO.getSupplierCode(),
                                                                                     orderMigrationDTO.getProject());
            if (deliveryControllerUserId != null) {
                orderMigrationDTO.setDeliveryControllerUserId(deliveryControllerUserId);
                orderMigrationDTO.setDeliveryControllerUserName(findUserNameForUserId(deliveryControllerUserId));
            }
        }
    }
    
    @Override
    public List<String> getProjectsToBeReleased() {
        return new ArrayList<String>();
    }
    
    @Override
    public List<String> getTestObjectsToBeConsideredAsAdditional() {
        return new ArrayList<String>();
    }
}
