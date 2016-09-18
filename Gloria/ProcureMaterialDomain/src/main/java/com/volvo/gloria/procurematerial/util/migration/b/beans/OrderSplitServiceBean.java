package com.volvo.gloria.procurematerial.util.migration.b.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.procurematerial.util.migration.b.OrderSplitService;
import com.volvo.gloria.procurematerial.util.migration.c.OrderNewTemplateExcelHandler;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.c.GloriaExceptionConstants;

/**
 * An implementation class for Open Order Split.
 */
public class OrderSplitServiceBean implements OrderSplitService {
    private static final String OPEN_ORDER_DATA_PROPERTY_KEY = "MigrationData";
    private static final String ALL_ORDER = "data_orders";
    private static final int MILLI = 1000;
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSplitServiceBean.class);
   
    @Override
    public void initiateOrderSplitData(Properties openOrderProperties) throws GloriaApplicationException {
        try {
            long time = System.currentTimeMillis();
            for (String fullFilename : getInFilenames()) {
                Set<OrderMigrationDTO> openOrderMigrationDTOs = new HashSet<OrderMigrationDTO>();
                if (fullFilename != null && (fullFilename.contains(ALL_ORDER))
                        && fullFilename.endsWith(IOUtil.FILE_TYPE_EXCEL_NEW)) {
                    System.out.println(fullFilename);
                    OrderNewTemplateExcelHandler openOrderNewTemplateExcelHandler = new OrderNewTemplateExcelHandler(fullFilename);
                    openOrderMigrationDTOs.addAll(openOrderNewTemplateExcelHandler.manageExcel());
                    
                    if (!openOrderMigrationDTOs.isEmpty()) {
                        generateExcels(openOrderMigrationDTOs, openOrderProperties);
                    }
                }               
            }
            LOGGER.info("Done Reading in - " + (System.currentTimeMillis() - time) / MILLI + " sec");            
        } catch (Exception e) {
            LOGGER.error("Failed to run OpenOrder Split!");
            e.printStackTrace();
            throw new GloriaApplicationException(GloriaExceptionConstants.DEFAULT_ERROR_CODE, "Failed to run OpenOrder Split!", e);
        } 
    }

 
    private void generateExcels(Set<OrderMigrationDTO> openOrderMigrationDTOs, Properties openOrderProperties) {
        Map<String, List<OrderMigrationDTO>> siteIdOpenOrdersMap = new HashMap<String, List<OrderMigrationDTO>>();
        int total = openOrderMigrationDTOs.size();
        LOGGER.info("Total Open Orders :" + total);

        for (OrderMigrationDTO openOrderMigrationDTO : openOrderMigrationDTOs) {
            String siteId = openOrderMigrationDTO.getOrderSiteId();

            if (siteIdOpenOrdersMap.containsKey(siteId)) {
                siteIdOpenOrdersMap.get(siteId).add(openOrderMigrationDTO);
            } else {
                siteIdOpenOrdersMap.put(siteId, new ArrayList<OrderMigrationDTO>(Arrays.asList(openOrderMigrationDTO)));
            }
        }

        ExportSplitOrdersExcels.export(siteIdOpenOrdersMap, openOrderProperties);
    }

    private String[] getInFilenames() throws IOException {
       
        return IOUtil.loadFileContentsFromClasspath(OPEN_ORDER_DATA_PROPERTY_KEY + "/", IOUtil.FILE_TYPE_EXCEL_NEW, "initDB/_global/data/");
    
    }
}
