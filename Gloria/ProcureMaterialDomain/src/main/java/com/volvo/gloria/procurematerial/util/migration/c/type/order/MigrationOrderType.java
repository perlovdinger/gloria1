package com.volvo.gloria.procurematerial.util.migration.c.type.order;

import java.util.List;
import java.util.Set;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;

/**
 * Order type considered in Migration Scope - Closed/Open.
 * 
 */
public enum MigrationOrderType implements MigrationOrderTypeOperations {

    CLOSED(new Closed()), 
    OPEN(new Open());

    private final MigrationOrderTypeOperations migrationOrderTypeOperations;

    private MigrationOrderType(MigrationOrderTypeOperations migrationOrderTypeOperations) {
        this.migrationOrderTypeOperations = migrationOrderTypeOperations;
    }

    @Override
    public void validateOrder(OrderMigrationDTO orderMigrationDTO, List<String> uniqueOrderLineKeys, MaterialHeaderRepository materialHeaderRepository) {
        migrationOrderTypeOperations.validateOrder(orderMigrationDTO, uniqueOrderLineKeys, materialHeaderRepository);
    }
    
    @Override
    public void validateFinancialInfoForExternal(List<OrderMigrationDTO> allValidOrders, Set<OrderMigrationDTO> openOrderMigrationDTOs) {
        migrationOrderTypeOperations.validateFinancialInfoForExternal(allValidOrders, openOrderMigrationDTOs);
    }

    @Override
    public void validateApplySiteSpecificRules(OrderMigrationDTO orderMigrationDTO, String suffix, String shipToId, DeliveryFollowUpTeam deliveryFollowUpTeam,
            CommonServices commonServices) {
       migrationOrderTypeOperations.validateApplySiteSpecificRules(orderMigrationDTO, suffix, shipToId, deliveryFollowUpTeam, commonServices);
    }
}
