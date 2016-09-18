package com.volvo.gloria.common.repositories.b;

import java.util.List;

import com.volvo.gloria.common.d.entities.PartAffiliation;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * repository for root PartAffilication.
 * 
 */
public interface PartAffiliationRepository extends GenericRepository<PartAffiliation, Long> {

    List<PartAffiliation> getAllPartAffiliations(boolean requestable);
}
