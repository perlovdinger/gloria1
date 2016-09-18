package com.volvo.gloria.procurematerial.util.migration.c.validation.migrationsite;

import java.util.Arrays;
import java.util.List;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;

/**
 * IN01 specific rules.
 * 
 */
public class RulesIN01 extends MigrationSiteCodeDefaultValidations {
    private static final String BLR_GARE_54 = "42102";
    private static final String BLR_DELIVERYFOLLOWUP_TEAM = "BLR";

    @Override
    public void validateApplySiteSpecificRulesForClosedOrder(OrderMigrationDTO orderMigrationDTO, String suffix, String shipToId,
            DeliveryFollowUpTeam deliveryFollowUpTeam, CommonServices commonServices) {
        updateDeliveryInformation(orderMigrationDTO, suffix, BLR_GARE_54, deliveryFollowUpTeam, commonServices);
        orderMigrationDTO.setDeliveryControllerTeam(BLR_DELIVERYFOLLOWUP_TEAM);
    }

    @Override
    public void validateApplySiteSpecificRulesForOpenOrder(OrderMigrationDTO orderMigrationDTO, String suffix, String shipToId,
            DeliveryFollowUpTeam deliveryFollowUpTeam, CommonServices commonServices) {
        if (orderMigrationDTO.isExternal()) {
            super.validateApplySiteSpecificRulesForOpenOrder(orderMigrationDTO, suffix, shipToId, deliveryFollowUpTeam, commonServices);
        } else {
            updateDeliveryInformation(orderMigrationDTO, suffix, BLR_GARE_54, deliveryFollowUpTeam, commonServices);
            orderMigrationDTO.setDeliveryControllerTeam(BLR_DELIVERYFOLLOWUP_TEAM);
        }
    }

    @Override
    public List<String> getTestObjectsToBeConsideredAsAdditional() {
        return Arrays.asList("SPARE", "SPAREPART");
    }
    
    @Override
    public List<String> getProjectsToBeReleased() {
        return Arrays.asList("460-9103", "65-00048", "9103", "9218", "NO_PROJ", "P9218", "QJ221046", "W-60", "W60-9103", "W60-9218", "W60 P9218", "W60-9106",
                             "W60-9208", "W60-9217", "W60-P910", "W60-P9103", "W60-P9223", "W6-9218");
    }
}
