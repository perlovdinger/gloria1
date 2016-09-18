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
package com.volvo.gloria.common.b;

import com.volvo.gloria.common.carryover.c.dto.SyncPurchaseOrderCarryOverDTO;


/**
 * Service interface for carry over parts message transformer.
 */
public interface CarryOverTransformer {

    /**
     * Transform a carry over part information message from GPS.
     * 
     * @param receivedCarryOverMessage
     *            the message
     * @return a SyncPurchaseOrderCarryOverDTO
     */
    SyncPurchaseOrderCarryOverDTO transformStoredCarryOver(String receivedCarryOverMessage);
}
