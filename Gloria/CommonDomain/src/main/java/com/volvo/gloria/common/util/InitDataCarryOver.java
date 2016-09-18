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
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.carryover.c.dto.SyncPurchaseOrderCarryOverDTO;
import com.volvo.gloria.common.repositories.b.CarryOverRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaExceptionLogger;
import com.volvo.group.purchaseorder._1_0.OrderModeType;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Loads initial data for Carry over entities from XML file.
 */

public final class InitDataCarryOver extends InitDataNonoperative {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitDataCarryOver.class);

    public void initDataCarryOver(String env, String fileType, String carryOverPath) throws GloriaApplicationException, IOException {
        for (InputStream stream : getInputStreams(carryOverPath, env, fileType)) {
            processStream(stream);
        }
    }

    private void processStream(InputStream ins) throws GloriaApplicationException, IOException {
        CarryOverRepository carryOverRepository = ServiceLocator.getService(CarryOverRepository.class);
        try {
            CarryOverExcelHandler carryOverExcelHandler = new CarryOverExcelHandler(ins);
            SyncPurchaseOrderCarryOverDTO syncPurchaseOrderCarryOverDTO = carryOverExcelHandler.manageExcel();
            LOGGER.info("Start Process CustomerId=" + syncPurchaseOrderCarryOverDTO.getCarryOverItemDTOs().get(0).getCustomerId());
            load(carryOverRepository, syncPurchaseOrderCarryOverDTO);
            LOGGER.info("End Process CustomerId=" + syncPurchaseOrderCarryOverDTO.getCarryOverItemDTOs().get(0).getCustomerId());
        } catch (Exception e) {
            GloriaExceptionLogger.log(e, InitDataCarryOver.class);
        } finally {
            ins.close();
        }
    }

    private static void load(CarryOverRepository carryOverRepository, SyncPurchaseOrderCarryOverDTO syncPurchaseOrderCarryOverDTO) {
        List<String> unprocessedMessageDocIds;
        unprocessedMessageDocIds = CommonHelper.loadCarryOvers(syncPurchaseOrderCarryOverDTO, carryOverRepository);
        if (!unprocessedMessageDocIds.isEmpty()) {
            LOGGER.error("Carryover [Ordermode= " + OrderModeType.STAND_ALONE.value() + "] with following Document Ids: " + unprocessedMessageDocIds.toString()
                    + " , could not be processed.");
        }
    }
}
