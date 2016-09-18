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
package com.volvo.gloria.common.b.beans;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.WBSElementTransformer;
import com.volvo.gloria.common.c.dto.WbsElementDTO;
import com.volvo.gloria.common.wbs.c.dto.SyncWBSElementDTO;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.group.finance.common._1_0.MessageHeaderType;
import com.volvo.group.finance.common._1_0.SyncType;
import com.volvo.group.wbs._1_0.SyncWBSElementShort;
import com.volvo.group.wbs._1_0.WBSLoadType;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * WBS Element message transformer service implementation.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class WBSElementTransformerBean extends XmlTransformer implements WBSElementTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WBSElementTransformerBean.class);

    public WBSElementTransformerBean() {
        super(XmlConstants.SchemaFullPath.WBS, XmlConstants.PackageName.WBS);
    }

    /**
     * Perform transformation from external information model to the application internal model.
     * 
     * @param receivedWBSElementMessage
     *            the Message to transform
     * @return a SyncWBSElementDTO object
     */
    @Override
    public SyncWBSElementDTO transformStoredWBSElement(String receivedWBSElementMessage) {
        try {
            return (SyncWBSElementDTO) transform(receivedWBSElementMessage);
        } catch (JAXBException e) {
            // Throw an exception to roll-back the transaction.
            LOGGER.error("Unable to unmarshall the received message to a SyncWBSElementTypeDTO object, message will be discarded. Exception is:", e);
            throw new GloriaSystemException(e, "Unable to unmarshall the received message to a SyncWBSElementTypeDTO object, message will be discarded");
        }
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        SyncWBSElementShort syncWBSElement = (SyncWBSElementShort) jaxbOject;
        SyncWBSElementDTO syncWBSElementDTO = new SyncWBSElementDTO();
        
        SyncType sync = syncWBSElement.getSync();
        MessageHeaderType messageHeader = sync.getMessageHeader();
        XMLGregorianCalendar creationDateTime = messageHeader.getCreationDateTime();
        syncWBSElementDTO.setCreationDateTime(creationDateTime.toString());
        List<WbsElementDTO> wbsElementDTOs = new ArrayList<WbsElementDTO>();
        syncWBSElementDTO.setWbsElementDTO(wbsElementDTOs);
        List<WBSLoadType> wbsRec = syncWBSElement.getWBSRec();
        
        if (wbsRec != null && !wbsRec.isEmpty()) {
            for (WBSLoadType wbsLoadType : wbsRec) {
                WbsElementDTO wbsElementDTO = new WbsElementDTO();
                wbsElementDTOs.add(wbsElementDTO);
                wbsElementDTO.setWbs(wbsLoadType.getWBS());
                wbsElementDTO.setCompanyCode(wbsLoadType.getCC());
                wbsElementDTO.setDescription(wbsLoadType.getDesc());
                wbsElementDTO.setProjectId(wbsLoadType.getShortID());
            }
        }
        return syncWBSElementDTO;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        return null;
    }

}
