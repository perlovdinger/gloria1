package com.volvo.gloria.procurematerial.util.migration.c.validation.migrationsite;

import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.findUserNameForUserId;

import java.util.Arrays;
import java.util.List;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;

/**
 * BR03 specific rules.
 * 
 */
public class RulesBR03 extends MigrationSiteCodeDefaultValidations {
    private static final String CUR_GARE_54 = "47670";
    private static final String CUR_DELIVERYFOLLOWUP_TEAM = "CUR";
    private static final String CUR_DELIVERYCONTROLLER_ID = "TBC7151";

    @Override
    public void validateApplySiteSpecificRulesForClosedOrder(OrderMigrationDTO orderMigrationDTO, String suffix, String shipToId,
            DeliveryFollowUpTeam deliveryFollowUpTeam, CommonServices commonServices) {
        orderMigrationDTO.setShipToId(CUR_GARE_54);
        orderMigrationDTO.setDeliveryControllerTeam(CUR_DELIVERYFOLLOWUP_TEAM);
        orderMigrationDTO.setDeliveryControllerUserId(CUR_DELIVERYCONTROLLER_ID);
        orderMigrationDTO.setDeliveryControllerUserName(findUserNameForUserId(CUR_DELIVERYCONTROLLER_ID));
    }
    
    @Override
    public void validateApplySiteSpecificRulesForOpenOrder(OrderMigrationDTO orderMigrationDTO, String suffix, String shipToId,
            DeliveryFollowUpTeam deliveryFollowUpTeam, CommonServices commonServices) {
        orderMigrationDTO.setShipToId(CUR_GARE_54);
        orderMigrationDTO.setDeliveryControllerTeam(CUR_DELIVERYFOLLOWUP_TEAM);
        orderMigrationDTO.setDeliveryControllerUserId(CUR_DELIVERYCONTROLLER_ID);
        orderMigrationDTO.setDeliveryControllerUserName(findUserNameForUserId(CUR_DELIVERYCONTROLLER_ID));
    }
    
    @Override
    public List<String> getTestObjectsToBeConsideredAsAdditional() {
        return Arrays.asList("SPARE", "SPAREPART", "SPAREPARTS");
    }
}
