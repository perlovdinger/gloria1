package com.volvo.gloria.common.repositories.b;

import com.volvo.gloria.common.d.entities.PartAliasMapping;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * repository for root PartAffilication.
 * 
 */
public interface PartAliasMappingRepository extends GenericRepository<PartAliasMapping, Long> {

    PartAliasMapping getGpsQualifier(String kolaDomain);
}
