package com.volvo.gloria.procurematerial.util.migration.c.validation.migrationsite;

import java.util.List;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;

/**
 * Site specific validations.
 * 
 */
public interface MigrationSiteCodeValidations {

    void validateApplySiteSpecificRulesForOpenOrder(OrderMigrationDTO orderMigrationDTO, String suffix, String shipToId,
            DeliveryFollowUpTeam deliveryFollowUpTeam, CommonServices commonServices);

    void validateApplySiteSpecificRulesForClosedOrder(OrderMigrationDTO orderMigrationDTO, String suffix, String shipToId,
            DeliveryFollowUpTeam deliveryFollowUpTeam, CommonServices commonServices);

    List<String> getProjectsToBeReleased();

    List<String> getTestObjectsToBeConsideredAsAdditional();
}
