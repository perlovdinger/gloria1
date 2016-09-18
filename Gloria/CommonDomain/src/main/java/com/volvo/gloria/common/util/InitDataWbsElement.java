/*
 * Copyright 2013 Volvo Information Technology AB 
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
import com.volvo.gloria.common.b.WBSElementTransformer;
import com.volvo.gloria.common.wbs.c.dto.SyncWBSElementDTO;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Initialises data for WBS Element, by loading an XML file on classpath or using a file URI defined by <code>wbselement.data</code> property.
 * 
 */
public final class InitDataWbsElement extends InitDataNonoperative {
    private static final int CONTENT_LENGTH = 40;
    private static final String WBS_PATH = "/data/nonOperative/WbsElement/";
    private static final Logger LOGGER = LoggerFactory.getLogger(InitDataWbsElement.class);

    public void initWbsElement(String env,String wbsPath) throws IOException {
        String path = WBS_PATH;
        if (wbsPath != null) {
            path = wbsPath;
        } 
        for (String aFileContent : getFiles(path, env)) {
            initWbs(aFileContent);
        }
    }

    private void initWbs(String aFileContent) {
        WBSElementTransformer wbsElementStorageTransformer = ServiceLocator.getService(WBSElementTransformer.class);
        CommonServices commonServices = ServiceLocator.getService(CommonServices.class);

        try {
            LOGGER.info("Start Process WbsElement");
            SyncWBSElementDTO syncWBSElementDTO = wbsElementStorageTransformer.transformStoredWBSElement(aFileContent);
            commonServices.syncWBSElement(syncWBSElementDTO);
            LOGGER.info("End Process WbsElement");
        } catch (Exception e) {
            String content = null;
            if (aFileContent != null) {
                content = aFileContent.substring(0, CONTENT_LENGTH);
            }
            LOGGER.error("Couldn't initialize WBS : aFileContent=" + content);
        }
    }
}
