package com.volvo.gloria.procurematerial.util.migration.c.type.order;

import java.util.List;
import java.util.Set;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;

/**
 * default operations on - Closed/Open.
 * 
 */
public class MigrationOrderTypeDefaultOperations implements MigrationOrderTypeOperations {

    @Override
    public void validateOrder(OrderMigrationDTO orderMigrationDTO, List<String> uniqueOrderLineKeys, MaterialHeaderRepository materialHeaderRepository) {
        OrderMigrationHelper.validateWithRulesCommonForOpenAndClosedOrder(orderMigrationDTO, uniqueOrderLineKeys, materialHeaderRepository);
    }

    @Override
    public void validateFinancialInfoForExternal(List<OrderMigrationDTO> allValidOrders, Set<OrderMigrationDTO> openOrderMigrationDTOs) {
        // do nothing
    }

    @Override
    public void validateApplySiteSpecificRules(OrderMigrationDTO orderMigrationDTO, String suffix, String shipToId, DeliveryFollowUpTeam deliveryFollowUpTeam,
            CommonServices commonServices) {
        // do nothing
    }
}
