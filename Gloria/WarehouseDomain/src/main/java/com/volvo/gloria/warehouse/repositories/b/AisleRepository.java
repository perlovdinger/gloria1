package com.volvo.gloria.warehouse.repositories.b;

import java.util.List;

import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.persistence.GenericRepository;
import com.volvo.gloria.warehouse.d.entities.AisleRackRow;
import com.volvo.gloria.warehouse.d.entities.BaySetting;

/**
 * repository for root Aisle.
 * 
 */
public interface AisleRepository extends GenericRepository<AisleRackRow, Long> {
    
    List<AisleRackRow> getAisleRackRowByZoneID(long zoneId) throws GloriaApplicationException;

    AisleRackRow addAisleRackRow(long zoneId, AisleRackRow aisleRackRow);
    
    AisleRackRow updateAisleRackRow(AisleRackRow aisleRackRow);
    
    void deleteAisleRackRow(long aisleRackRowOid);

    BaySetting addBaySetting(BaySetting baySetting);

    BaySetting updateBaySetting(BaySetting baySetting);

    BaySetting findBaySetting(Long baySettingOid);

    void deleteBaySetting(Long baySettingOid);
}
