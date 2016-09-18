/*
 * Copyright 2009 Volvo Information Technology AB 
 * 
 * Licensed under the Volvo IT Corporate Source License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *      http://java.volvo.com/licenses/CORPORATE-SOURCE-LICENSE 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.volvo.gloria.common.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.b.CostCenterTransformer;
import com.volvo.gloria.common.costcenter.c.dto.SyncCostCenterDTO;
import com.volvo.gloria.util.GloriaExceptionLogger;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Initialises data for Cost Center, by loading an XML file on classpath or using a file URI defined by <code>costcenter.data</code> property.
 * 
 */
public final class InitDataCostCenter extends InitDataNonoperative {
    private static final String COST_CENTER_PATH = "/data/nonOperative/CostCenter/";
    private static final Logger LOGGER = LoggerFactory.getLogger(InitDataCostCenter.class);

    public void initCostCenters(String env,String costCenterPath) throws IOException {
        String path = COST_CENTER_PATH;
        if (costCenterPath != null) {
            path = costCenterPath;
        } 
        for (String aFileContent : getFiles(path, env)) {
            initCostCenter(aFileContent);
        }
    }

    private static void initCostCenter(String aFileContent) {
        CostCenterTransformer costCenterStorageTransformer = ServiceLocator.getService(CostCenterTransformer.class);
        CommonServices commonServices = ServiceLocator.getService(CommonServices.class);
        try {
            SyncCostCenterDTO syncCostCenterDTO = costCenterStorageTransformer.transformStoredCostCenter(aFileContent);
            LOGGER.info("Start Process CompanyCode=" + syncCostCenterDTO.getCostCenterItems().get(0).getCompanyCode());
            commonServices.syncCostCenter(syncCostCenterDTO);
            LOGGER.info("End Process CompanyCode=" + syncCostCenterDTO.getCostCenterItems().get(0).getCompanyCode());
        } catch (Exception e) {
            LOGGER.error("Error: " + e.getMessage() + " " + aFileContent);
            GloriaExceptionLogger.log(e, Exception.class);
            if (aFileContent != null) {
                LOGGER.info("Error in costcenter file =" + aFileContent.substring(0, Math.min(600, aFileContent.length())));
            }
        }
    }
}
