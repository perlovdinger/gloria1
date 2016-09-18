package com.volvo.gloria.common.b;

import java.util.List;

import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamTransformerDTO;

/**
 * Service interface for Delivery Folow Up Team message transformer.
 * 
 */
public interface DeliveryFollowUpTeamTransformer {

    List<DeliveryFollowUpTeamTransformerDTO> transformDeliveryFollowUpTeam(String xmlContent);

}
