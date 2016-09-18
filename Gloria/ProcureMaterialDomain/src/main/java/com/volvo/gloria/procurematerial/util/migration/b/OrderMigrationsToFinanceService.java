package com.volvo.gloria.procurematerial.util.migration.b;

import com.volvo.gloria.procurematerial.util.migration.c.dto.MigrationStatusDTO;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Service for sending goods movement and purchase orders to Master Finance system.
 */
public interface OrderMigrationsToFinanceService {
    
    void sendGoodsMovementsForMigratedOrders(String[] companyCodes) throws GloriaApplicationException;
    
    void sendPurcahseOrdersForMigratedOrders(String[] companyCodes) throws GloriaApplicationException;

    MigrationStatusDTO getstatus();
}
