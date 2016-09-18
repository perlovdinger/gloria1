package com.volvo.gloria.procurematerial.repositories.b;

import java.util.List;

import com.volvo.gloria.procurematerial.d.entities.PartAlias;
import com.volvo.gloria.procurematerial.d.entities.PartNumber;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * 
 */
public interface PartNumberRepository extends GenericRepository<PartNumber, Long> {

    void addPartNumber(PartNumber partNumber);

    PartNumber findPartNumberById(long partNumberOid) throws GloriaApplicationException;

    void updatePartNumber(PartNumber partNumber);

    void deletePartNumber(PartNumber partNumber) throws GloriaApplicationException;
    
    List<PartAlias> getPartAliases(String volvoPartNo);
    
    void addPartAlias(PartAlias partAlias);

    PartAlias findPartAliasById(long partAliasOid) throws GloriaApplicationException;

    void updatePartAlias(PartAlias partAlias);

    void deletePartAlias(PartAlias partAlias) throws GloriaApplicationException;

    PartNumber findVolvoPartWithAliasByPartNumber(String partNumber);

    PartAlias findPartAliasByAliasName(String partAlias);
}

