package com.volvo.gloria.procurematerial.util.migration.c.validation.migrationsite;

import java.util.Arrays;
import java.util.List;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;

/**
 * FR46 - specific rules.
 * 
 */
public class RulesFR46 extends MigrationSiteCodeDefaultValidations {

    private static final String LYON_GARE_54 = "34347";
    private static final String LYON_DELIVERYFOLLOWUP_TEAM = "LYS";

    @Override
    public void validateApplySiteSpecificRulesForOpenOrder(OrderMigrationDTO orderMigrationDTO, String suffix, String shipToId,
            DeliveryFollowUpTeam deliveryFollowUpTeam, CommonServices commonServices) {
        if (orderMigrationDTO.isExternal()) {
            String[] extractedOrderNo = orderMigrationDTO.getOrderNumber().split("-");
            try {
                orderMigrationDTO.setOrderIdGps("M".concat(extractedOrderNo[1]));
            } catch (ArrayIndexOutOfBoundsException e) {
                orderMigrationDTO.setReason("Order Number format incorrect!");
            }
        } else {
            orderMigrationDTO.setSuffix(null);
        }
        orderMigrationDTO.setShipToId(LYON_GARE_54);
        orderMigrationDTO.setDeliveryControllerTeam(LYON_DELIVERYFOLLOWUP_TEAM);
    }
    
    @Override
    public void validateApplySiteSpecificRulesForClosedOrder(OrderMigrationDTO orderMigrationDTO, String suffix, String shipToId,
            DeliveryFollowUpTeam deliveryFollowUpTeam, CommonServices commonServices) {
        orderMigrationDTO.setShipToId(LYON_GARE_54);
        orderMigrationDTO.setDeliveryControllerTeam(LYON_DELIVERYFOLLOWUP_TEAM);
    }
    
    @Override
    public List<String> getProjectsToBeReleased() {
        return Arrays.asList("FREE", "STOCK", "STOCK 3P", "STOCK3P", "STOCK-3P", "STOCKGTT");
    }
    
    @Override
    public List<String> getTestObjectsToBeConsideredAsAdditional() {
        return Arrays.asList("DUMMY", "DUMY");
    }
}
