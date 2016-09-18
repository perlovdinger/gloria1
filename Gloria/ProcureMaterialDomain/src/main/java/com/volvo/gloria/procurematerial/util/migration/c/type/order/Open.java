package com.volvo.gloria.procurematerial.util.migration.c.type.order;

import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.isSendPOtoSAP;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.markAsInvalid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam;
import com.volvo.gloria.common.repositories.b.CostCenterRepository;
import com.volvo.gloria.common.repositories.b.GlAccountRepository;
import com.volvo.gloria.common.repositories.b.WbsElementRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;
import com.volvo.gloria.procurematerial.util.migration.c.validation.migrationsite.MigrationSiteCode;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Operations for - Open.
 * 
 */
public class Open extends MigrationOrderTypeDefaultOperations {

    @Override
    public void validateOrder(OrderMigrationDTO orderMigrationDTO, List<String> uniqueOrderLineKeys, MaterialHeaderRepository materialHeaderRepository) {
        
        super.validateOrder(orderMigrationDTO, uniqueOrderLineKeys, materialHeaderRepository);        
        
        // Validation for failure cases on open orders
        if (!orderMigrationDTO.isMigrated()) {
            OrderMigrationHelper.validateRulesForOpenOrder(orderMigrationDTO, uniqueOrderLineKeys, materialHeaderRepository);
        }
    }  
    
    @Override
    public void validateFinancialInfoForExternal(List<OrderMigrationDTO> allValidOrders, Set<OrderMigrationDTO> openOrderMigrationDTOs) {
        Map<String, Set<String>> wbsListPerCompanyMap = new HashMap<String, Set<String>>();
        Map<String, Set<String>> ccListPerCompanyMap = new HashMap<String, Set<String>>();
        Map<String, Set<String>> glAccountListPerCompanyMap = new HashMap<String, Set<String>>();
       
        CostCenterRepository costCenterRepo = ServiceLocator.getService(CostCenterRepository.class);
        GlAccountRepository glAccountRepo = ServiceLocator.getService(GlAccountRepository.class);
        WbsElementRepository wbsElementRepo = ServiceLocator.getService(WbsElementRepository.class);

        for (OrderMigrationDTO dto : allValidOrders) {
            if (isSendPOtoSAP(dto.getCompanyCode())) {
                if (!wbsListPerCompanyMap.containsKey(dto.getCompanyCode())) {
                    wbsListPerCompanyMap.put(dto.getCompanyCode(), new HashSet<String>());
                    ccListPerCompanyMap.put(dto.getCompanyCode(), new HashSet<String>());
                    glAccountListPerCompanyMap.put(dto.getCompanyCode(), new HashSet<String>());
                }
                wbsListPerCompanyMap.get(dto.getCompanyCode()).add(dto.getWbsElement());
                ccListPerCompanyMap.get(dto.getCompanyCode()).add(dto.getCostCenter());
                glAccountListPerCompanyMap.get(dto.getCompanyCode()).add(dto.getGlAccount());
            }
        }

        Set<String> companyCodes = wbsListPerCompanyMap.keySet();
        for (String companyCode : companyCodes) {
            Set<String> glAccountList = glAccountRepo.filterInvalidGlAccounts(glAccountListPerCompanyMap.get(companyCode), companyCode);
            Set<String> wbsList = wbsElementRepo.filterInvalidWbsCodes(wbsListPerCompanyMap.get(companyCode), companyCode);
            Set<String> costCenterList = costCenterRepo.filterInvalidCostCenters(ccListPerCompanyMap.get(companyCode), companyCode);

            for (OrderMigrationDTO dto : allValidOrders) {
                if (companyCode.equals(dto.getCompanyCode()) && dto.isExternal()) {
                    if (!wbsList.contains(dto.getWbsElement())) {
                        markAsInvalid(dto, "WBS_ELEMENT invalid!");
                    }
                    if (!glAccountList.contains(dto.getGlAccount())) {
                        markAsInvalid(dto, "GL_ACCOUNT invalid!");
                    }
                    if (!costCenterList.contains(dto.getCostCenter())) {
                        markAsInvalid(dto, "COST_CENTER invalid!");
                    }
                }
            }
        }
    }
    
    @Override
    public void validateApplySiteSpecificRules(OrderMigrationDTO orderMigrationDTO, String suffix, String shipToId, DeliveryFollowUpTeam deliveryFollowUpTeam,
            CommonServices commonServices) {
        MigrationSiteCode migrationSiteCode = MigrationSiteCode.valueOf(orderMigrationDTO.getCompanyCode());
        migrationSiteCode.validateApplySiteSpecificRulesForOpenOrder(orderMigrationDTO, suffix, shipToId, deliveryFollowUpTeam, commonServices);
    }
}
