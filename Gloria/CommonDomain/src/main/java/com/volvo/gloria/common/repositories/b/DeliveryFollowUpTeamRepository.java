package com.volvo.gloria.common.repositories.b;

import java.util.List;

import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamDTO;
import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamFilterDTO;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeamFilter;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * repository for root DeliveryFollowUpTeam.
 * 
 */
public interface DeliveryFollowUpTeamRepository extends GenericRepository<DeliveryFollowUpTeam, Long> {
    
    DeliveryFollowUpTeamFilter addDeliveryFollowUpTeamFilter(DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO, long deliveryFollowUpOid);

    List<DeliveryFollowUpTeamFilter> findAllDeliveryFollowUpTeamFilter();
    
    DeliveryFollowUpTeamDTO getDeliveryFollowupTeam(String deliveryFollowUpTeamName);
    
    List<DeliveryFollowUpTeamFilter> getDeliveryFollowUpTeamFilters(long deliveryFollowUpTeamOid);
    
    DeliveryFollowUpTeamFilter findDeliveryFollowUpTeamFiltersById(long id);
    
    DeliveryFollowUpTeamFilter updateDeliveryFollowupTeamFilter(DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO) throws GloriaApplicationException;
    
    DeliveryFollowUpTeam updateDeliveryFollowupTeam(DeliveryFollowUpTeamDTO deliveryFollowUpTeamDTO) throws GloriaApplicationException;
    
    DeliveryFollowUpTeamFilter getDeliveryFollowUpTeamFilter(long deliveryFollowUpTeamFilterOId) throws GloriaApplicationException;
    
    void deleteDeliveryFollowupTeamFilter(long deliveryFollowUpTeamFilterId);
    
    DeliveryFollowUpTeamFilter checkSupplierDeliveryFollowupTeamFilter(String suffix, String supplierId) throws GloriaApplicationException;
    
    PageObject getDeliveryFollowUpTeamFilters(PageObject pageObject, long deliveryFollowUpTeamOid);

    DeliveryFollowUpTeamFilter checkProjectDeliveryFollowupTeamFilter(String projectId)
            throws GloriaApplicationException;

    DeliveryFollowUpTeamFilter getProjectFilterDetailsById(Long deliveryFollowUpTeamFilterOid);

    DeliveryFollowUpTeam findDeliveryFollowUpTeamById(long deliveryFollowUpTeamOid);

    DeliveryFollowUpTeam save(DeliveryFollowUpTeam instanceToSave);

    List<DeliveryFollowUpTeam> findAllDeliveryFollowUpTeam();    
}
