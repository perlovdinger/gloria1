/**
 * 
 */
package com.volvo.gloria.procurematerial.util.migration.b.beans;

import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.isSendGRtoSAP;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.isSendPOtoSAP;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.setAllCompanyCodes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.PaginatedArrayList;
import com.volvo.gloria.common.d.entities.CompanyCode;
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptHeader;
import com.volvo.gloria.procurematerial.d.entities.OrderSap;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderSapRepository;
import com.volvo.gloria.procurematerial.util.migration.b.OrderMigrationsToFinanceService;
import com.volvo.gloria.procurematerial.util.migration.c.dto.MigrationStatusDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaExceptionLogger;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * 
 */
public class OrderMigrationsToFinanceServiceBean implements OrderMigrationsToFinanceService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderMigrationsToFinanceServiceBean.class);
    
    private static final int TEN = 10;
    private static final int HUNDRED = 100;
    private static final int MILLI = 1000;
    private static final int PAGE_SIZE = 100;
    private boolean isDone = true;
    private StringBuilder statusText = new StringBuilder();
    private int percentDone = 0;
    private String siteInProgress = "";
    private String siteDone = "";

    /**
     * {@inheritDoc}
     */

    @Override
    public void sendGoodsMovementsForMigratedOrders(String[] companyCodes) throws GloriaApplicationException {
        try {
            CommonServices commonServices = ServiceLocator.getService(CommonServices.class);
            DeliveryNoteRepository deliveryNoteRepo = ServiceLocator.getService(DeliveryNoteRepository.class);
            
            isDone = false;
            statusText = new StringBuilder();
            log("Sending GM for " + Arrays.asList(companyCodes).toString());
            
            setAllCompanyCodes(new HashSet<CompanyCode>(commonServices.findAllCompanyCodes()));
        
            for (String companyCode : companyCodes) {
                siteInProgress = companyCode;
                if (isSendGRtoSAP(companyCode)) {
                    long start = System.currentTimeMillis();
                    
                    List<GoodsReceiptHeader> goodsMovementHeaders = deliveryNoteRepo.findAllGoodsMovementsForCompanyCode(companyCode);
                    int totalDone = 0, divisor = TEN, total = goodsMovementHeaders.size();
                    log(companyCode + " - Total " + total);
                    
                    PaginatedArrayList<GoodsReceiptHeader> paginatedList = new PaginatedArrayList<GoodsReceiptHeader>(goodsMovementHeaders);
                    paginatedList.setPageSize(PAGE_SIZE);
                    for (List<GoodsReceiptHeader> subList = null; (subList = paginatedList.nextPage()) != null;) {
                        PerformSendGMForMigratedOrdersService performService = 
                                ServiceLocator.getService(PerformSendGMForMigratedOrdersService.class);
                        log(performService.performSend(subList).toString());
                        totalDone += subList.size();
                        percentDone = (int) (totalDone * HUNDRED / total);
                        if (percentDone >= divisor) {
                            divisor += TEN;
                            log(percentDone + "% - " + totalDone);
                        }
                    }
                    log(companyCode + " took - " + (System.currentTimeMillis() - start) / MILLI + " sec");
                }
                siteDone += (", " + companyCode);
            }
            
            isDone = true;
            statusText = new StringBuilder();
            siteInProgress = "";
        } catch (Exception e) {
            GloriaExceptionLogger.log(e, OrderMigrationsToFinanceService.class);
            throw new GloriaApplicationException(GloriaExceptionConstants.DEFAULT_ERROR_CODE, "Failed to run send Goods Movements For Migrated Orders!", e);
        } finally {
            statusText.append("Done");
            siteDone = Arrays.toString(companyCodes);
        }
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void sendPurcahseOrdersForMigratedOrders(String[] companyCodes) throws GloriaApplicationException {
        try {
            CommonServices commonServices = ServiceLocator.getService(CommonServices.class);
            OrderSapRepository orderSapRepository = ServiceLocator.getService(OrderSapRepository.class);
            isDone = false;
            statusText = new StringBuilder();
            log("Sending PPO for " + Arrays.asList(companyCodes).toString());
            
            setAllCompanyCodes(new HashSet<CompanyCode>(commonServices.findAllCompanyCodes()));
        
            for (String companyCode : companyCodes) {
                siteInProgress = companyCode;
                if (isSendPOtoSAP(companyCode)) {
                    long start = System.currentTimeMillis();
                    
                    List<OrderSap> orderSapList = orderSapRepository.findOrderSapForCompanyCode(companyCode);
                    int totalDone = 0, divisor = TEN, total = orderSapList.size();
                    log(companyCode + " - Total " + total);
                    
                    PaginatedArrayList<OrderSap> paginatedList = new PaginatedArrayList<OrderSap>(orderSapList);
                    paginatedList.setPageSize(PAGE_SIZE);
                    for (List<OrderSap> subList = null; (subList = paginatedList.nextPage()) != null;) {
                        PerformSendPOForMigratedOrdersService performService = 
                                ServiceLocator.getService(PerformSendPOForMigratedOrdersService.class);
                        log(performService.performSend(subList).toString());
                        totalDone += subList.size();
                        percentDone = (int) (totalDone * HUNDRED / total);
                        if (percentDone >= divisor) {
                            divisor += TEN;
                            log(percentDone + "% - " + totalDone);
                        }
                    }
                    log(companyCode + " took - " + (System.currentTimeMillis() - start) / MILLI + " sec");
                }
                siteDone += (", " + companyCode);
            }
            
            isDone = true;
            statusText = new StringBuilder();
            siteInProgress = "";
        } catch (Exception e) {
            GloriaExceptionLogger.log(e, OrderMigrationsToFinanceService.class);
            throw new GloriaApplicationException(GloriaExceptionConstants.DEFAULT_ERROR_CODE, "Failed to run send Purcahse Orders For Migrated Orders!", e);
        } finally {
            statusText.append("Done");
            siteDone = Arrays.toString(companyCodes);
        }

    }

    /**
     * {@inheritDoc}
     */

    @Override
    public MigrationStatusDTO getstatus() {
        MigrationStatusDTO migrationStatusDTO = new MigrationStatusDTO();
        migrationStatusDTO.setDone(isDone);
        migrationStatusDTO.setSiteCompleted(siteDone);
        migrationStatusDTO.setCompleted(percentDone);
        migrationStatusDTO.setSiteInProgress(siteInProgress);
        migrationStatusDTO.setStatus(statusText.toString());

        return migrationStatusDTO;
    }
    
    private void log(String text) {
        statusText.append(text + "\n");
        LOGGER.info(text);
    }

}
