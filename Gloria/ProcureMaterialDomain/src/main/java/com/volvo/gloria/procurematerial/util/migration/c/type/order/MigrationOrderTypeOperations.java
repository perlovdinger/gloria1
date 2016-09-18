package com.volvo.gloria.procurematerial.util.migration.c.type.order;

import java.util.List;
import java.util.Set;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;

/**
 * operations supported on type : Closed/Open.
 * 
 */
public interface MigrationOrderTypeOperations {

    void validateOrder(OrderMigrationDTO orderMigrationDTO, List<String> uniqueOrderLineKeys, MaterialHeaderRepository materialHeaderRepository);

    void validateFinancialInfoForExternal(List<OrderMigrationDTO> allValidOrders, Set<OrderMigrationDTO> openOrderMigrationDTOs);

    void validateApplySiteSpecificRules(OrderMigrationDTO orderMigrationDTO, String suffix, String shipToId, DeliveryFollowUpTeam deliveryFollowUpTeam,
            CommonServices commonServices);

}
