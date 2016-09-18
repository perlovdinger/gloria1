package com.volvo.gloria.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamDTO;
import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamFilterDTO;
import com.volvo.gloria.common.repositories.b.DeliveryFollowUpTeamRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaExceptionLogger;
import com.volvo.gloria.util.IOUtil;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

public class InitDataDeliveryFollowUpSupplierFilter extends InitDataNonoperative {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitDataDeliveryFollowUpSupplierFilter.class);

    private static final String DC_SUPPLIER_FILTERS_FILE_PREFIX = "DC_SUPPLIER_FILTERS_";

    private static Map<String, String> siteCodeDCToTeamName = new HashMap<String, String>();

    public void initDataDeliveryFollowUpSupplierFilter(String env, String supplierFiltersPath) throws GloriaApplicationException, IOException {
        String envPath = env;
        setAllSites();
        String[] files = null;
        try {
            files = IOUtil.loadFileContentsFromClasspath(supplierFiltersPath, IOUtil.FILE_TYPE_EXCEL_NEW, envPath);
        } catch (Exception e) {
            envPath = "initDB/_global";
            files = IOUtil.loadFileContentsFromClasspath(supplierFiltersPath, IOUtil.FILE_TYPE_EXCEL_NEW, envPath);
        }

        if (files != null && files.length > 0) {
            for (String fileName : files) {
                if (fileName.contains(DC_SUPPLIER_FILTERS_FILE_PREFIX)) {
                    String siteCode = fileName.split(DC_SUPPLIER_FILTERS_FILE_PREFIX)[1];
                    siteCode = siteCode.substring(0, siteCode.indexOf("."));
                    processStream(IOUtil.loadInputStreamFromClasspath(supplierFiltersPath, IOUtil.FILE_TYPE_EXCEL_NEW, envPath)[0], siteCode);
                }
            }
        }
    }

    private void processStream(InputStream ins, String siteCode) throws GloriaApplicationException, IOException {
        DeliveryFollowUpTeamRepository deliveryFollowUpTeamRepository = ServiceLocator.getService(DeliveryFollowUpTeamRepository.class);
        DeliveryFollowUpTeamDTO deliveryFollowUpTeamDTO = deliveryFollowUpTeamRepository.getDeliveryFollowupTeam(siteCodeDCToTeamName.get(siteCode));
        try {
            if (deliveryFollowUpTeamDTO != null) {
                DeliveryFollowUpTeamFilterExcelHandler filterExcelHandler = new DeliveryFollowUpTeamFilterExcelHandler(ins);
                List<DeliveryFollowUpTeamFilterDTO> deliveryFollowUpTeamFilterDTOs = filterExcelHandler.manageExcel();
                if (deliveryFollowUpTeamFilterDTOs != null && !deliveryFollowUpTeamFilterDTOs.isEmpty()) {
                    LOGGER.info("Start Process DC TEAM - SUPPLIER FILTERS: " + deliveryFollowUpTeamFilterDTOs.size());
                    for (DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO : deliveryFollowUpTeamFilterDTOs) {
                        deliveryFollowUpTeamRepository.addDeliveryFollowUpTeamFilter(deliveryFollowUpTeamFilterDTO, deliveryFollowUpTeamDTO.getId());
                    }
                    LOGGER.info("Done Process DC TEAM - SUPPLIER FILTERS");
                }
            }
        } catch (Exception e) {
            GloriaExceptionLogger.log(e, InitDataDeliveryFollowUpSupplierFilter.class);
        } finally {
            ins.close();
        }
    }

    private void setAllSites() {
        siteCodeDCToTeamName.put("42102", "BLR");
        siteCodeDCToTeamName.put("47670", "CUR");
    }
}
