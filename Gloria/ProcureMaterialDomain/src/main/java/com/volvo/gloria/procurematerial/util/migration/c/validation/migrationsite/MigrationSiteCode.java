package com.volvo.gloria.procurematerial.util.migration.c.validation.migrationsite;

import java.util.List;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;

/**
 * Sites in scope for Migration.
 * 
 */
public enum MigrationSiteCode implements MigrationSiteCodeValidations {
    FR46(new RulesFR46()), IN01(new RulesIN01()), BR03(new RulesBR03()), SE26(new RulesSE26()), SE27(new RulesSE27()), US10(new RulesUS10()), US45(
            new RulesUS45()), JP40(new RulesJP40());

    private final MigrationSiteCodeValidations migrationSiteValidations;

    private MigrationSiteCode(MigrationSiteCodeValidations migrationSiteValidations) {
        this.migrationSiteValidations = migrationSiteValidations;
    }

    @Override
    public void validateApplySiteSpecificRulesForOpenOrder(OrderMigrationDTO orderMigrationDTO, String suffix, String shipToId,
            DeliveryFollowUpTeam deliveryFollowUpTeam, CommonServices commonServices) {
        this.migrationSiteValidations.validateApplySiteSpecificRulesForOpenOrder(orderMigrationDTO, suffix, shipToId, deliveryFollowUpTeam, commonServices);
    }

    @Override
    public void validateApplySiteSpecificRulesForClosedOrder(OrderMigrationDTO orderMigrationDTO, String suffix, String shipToId,
            DeliveryFollowUpTeam deliveryFollowUpTeam, CommonServices commonServices) {
        this.migrationSiteValidations.validateApplySiteSpecificRulesForClosedOrder(orderMigrationDTO, suffix, shipToId, deliveryFollowUpTeam, commonServices);
    }

    @Override
    public List<String> getProjectsToBeReleased() {
        return this.migrationSiteValidations.getProjectsToBeReleased();
    }

    @Override
    public List<String> getTestObjectsToBeConsideredAsAdditional() {
        return this.migrationSiteValidations.getTestObjectsToBeConsideredAsAdditional();
    }
}
