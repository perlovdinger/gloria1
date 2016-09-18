package com.volvo.gloria.common.repositories.b;

import java.util.List;

import com.volvo.gloria.common.d.entities.Traceability;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * Repository for Traceability.
 */
public interface TraceabilityRepository extends GenericRepository<Traceability, Long> {

    List<Traceability> getMaterialLineTraceabilitys(long materialLineOid);

    List<Traceability> getOrderLineTraceabilitys(long orderLineOid);

}
