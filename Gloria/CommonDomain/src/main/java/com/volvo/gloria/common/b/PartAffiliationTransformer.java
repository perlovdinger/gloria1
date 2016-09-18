package com.volvo.gloria.common.b;

import java.util.List;

import com.volvo.gloria.common.c.dto.PartAffiliationDTO;

/**
 * Service interface for Part affiliation message transformer.
 * 
 */
public interface PartAffiliationTransformer {

    List<PartAffiliationDTO> transformPartAffiliation(String xmlContent);

}
