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
package com.volvo.gloria.common.b;

import com.volvo.gloria.common.costcenter.c.dto.SyncCostCenterDTO;

/**
 * Service interface for Cost Center message transformer.
 */
public interface CostCenterTransformer {

    /**
     * Transform a Cost Center message from Master Finance.
     * 
     * @param receivedCostCenterMessage
     *            the message
     * @return a SyncCostCenterTypeDTO
     */
    SyncCostCenterDTO transformStoredCostCenter(String receivedCostCenterMessage);

}
