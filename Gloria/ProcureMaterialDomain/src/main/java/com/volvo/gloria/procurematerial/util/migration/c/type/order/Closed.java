package com.volvo.gloria.procurematerial.util.migration.c.type.order;

import java.util.List;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;
import com.volvo.gloria.procurematerial.util.migration.c.validation.migrationsite.MigrationSiteCode;

/**
 * operations for - Closed.
 * 
 */
public class Closed extends MigrationOrderTypeDefaultOperations {

    @Override
    public void validateOrder(OrderMigrationDTO orderMigrationDTO, List<String> uniqueOrderLineKeys, MaterialHeaderRepository materialHeaderRepository) {
        super.validateOrder(orderMigrationDTO, uniqueOrderLineKeys, materialHeaderRepository);
     // Validation for failure cases on open orders
        if (!orderMigrationDTO.isMigrated()) {
            OrderMigrationHelper.validateRulesForClosedOrder(orderMigrationDTO, uniqueOrderLineKeys, materialHeaderRepository);
        }
    }
    
    @Override
    public void validateApplySiteSpecificRules(OrderMigrationDTO orderMigrationDTO, String suffix, String shipToId, DeliveryFollowUpTeam deliveryFollowUpTeam,
            CommonServices commonServices) {
        MigrationSiteCode migrationSiteCode = MigrationSiteCode.valueOf(orderMigrationDTO.getCompanyCode());
        migrationSiteCode.validateApplySiteSpecificRulesForClosedOrder(orderMigrationDTO, suffix, shipToId, deliveryFollowUpTeam, commonServices);
    }
}
