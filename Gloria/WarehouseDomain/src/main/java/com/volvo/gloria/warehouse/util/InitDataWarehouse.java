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
package com.volvo.gloria.warehouse.util;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Loads initial data for Warehouse entities from XML file.
 */
public final class InitDataWarehouse {

    private static final String XML = "xml";
    private static final String WAREHOUSE_PATH = "/data/base/Warehouse/";

    private InitDataWarehouse() {
    }

    public static void initWarehouses(String env,String filePath,boolean rollout) throws JAXBException, IOException {
        String[] xmlFiles = null;
        if (filePath == null) {
            filePath = WAREHOUSE_PATH;
        }
        WarehouseServices warehouseService = ServiceLocator.getService(WarehouseServices.class);
        try {
            xmlFiles = IOUtil.loadFileContentsFromClasspath(filePath, XML, env);
            if (xmlFiles.length == 0) {
                xmlFiles = IOUtil.loadFileContentsFromClasspath(filePath, XML, "initDB/_global");
            }
        } catch (IOException e) {
            xmlFiles = IOUtil.loadFileContentsFromClasspath(filePath, XML, "initDB/_global");
        }
        //Ugly i know
        if (rollout) {
            env = null;
        }
        for (String aFileContent : xmlFiles) {
            warehouseService.createWarehouseData(aFileContent, env);
        }
    }
}
