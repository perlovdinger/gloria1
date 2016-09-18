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
package com.volvo.gloria.common.b.beans;

import static com.volvo.gloria.util.c.GloriaParams.ENGLISH;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.CostCenterTransformer;
import com.volvo.gloria.common.costcenter.c.dto.CostCenterItemDTO;
import com.volvo.gloria.common.costcenter.c.dto.SyncCostCenterDTO;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.group.costcenter._1_0.CostCenterType;
import com.volvo.group.costcenter._1_0.CostCenterType.CostCenterItem;
import com.volvo.group.costcenter._1_0.CostCenterType.CostCenterItem.CostCenterDescription;
import com.volvo.group.costcenter._1_0.CostCenterType.CostCenterItem.LockIndicators;
import com.volvo.group.costcenter._1_0.SyncCostCenter;
import com.volvo.group.finance.common._1_0.EffectiveDatePeriodType;
import com.volvo.group.finance.common._1_0.MessageHeaderType;
import com.volvo.group.finance.common._1_0.SyncType;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Cost Center message transformer service implementation.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class CostCenterTransformerBean extends XmlTransformer implements CostCenterTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CostCenterTransformerBean.class);

    public CostCenterTransformerBean() {
        super(XmlConstants.SchemaFullPath.COST_CENTER, XmlConstants.PackageName.COST_CENTER);
    }

    /**
     * Perform transformation from external information model to the application internal model.
     * 
     * @param receivedCostCenterMessage
     *            the Message to transform
     * @return a SyncCostCenterDTO object
     */
    @Override
    public SyncCostCenterDTO transformStoredCostCenter(String receivedCostCenterMessage) {
        try {
            return (SyncCostCenterDTO) transform(receivedCostCenterMessage);
        } catch (JAXBException e) {
            // Throw an exception to roll-back the transaction.
            LOGGER.error("Unable to unmarshall the received message to a SyncCostCenterTypeDTO object, message will be discarded. Exception is:", e);
        }
        return null;
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        SyncCostCenter syncCostCenter = (SyncCostCenter) jaxbOject;
        SyncCostCenterDTO syncCostCenterDTO = new SyncCostCenterDTO();

        SyncType sync = syncCostCenter.getSync();
        MessageHeaderType messageHeader = sync.getMessageHeader();
        XMLGregorianCalendar creationDateTime = messageHeader.getCreationDateTime();
        syncCostCenterDTO.setCreationDateTime(creationDateTime.toString());

        List<CostCenterItemDTO> costCenterItemDTOs = new ArrayList<CostCenterItemDTO>();
        List<CostCenterType> synCostCenters = syncCostCenter.getCostCenter();
         if (synCostCenters != null && !synCostCenters.isEmpty()) {
            for (CostCenterType costCenterType : synCostCenters) {

                CostCenterItem costCenterItem = costCenterType.getCostCenterItem();
                CostCenterItemDTO costCenterItemDTO = new CostCenterItemDTO();

                costCenterItemDTO.setAction(costCenterType.getBusinessObjectData().getAction());
                costCenterItemDTO.setCostCenter(costCenterItem.getCostCenter());
                costCenterItemDTO.setCompanyCode(costCenterItem.getCompanyCode());
                
                LockIndicators lockIndicators = costCenterItem.getLockIndicators();
                if (lockIndicators != null) {
                    costCenterItemDTO.setPrimaryPostings(lockIndicators.isPrimaryPostings());
                    costCenterItemDTO.setPlanning(lockIndicators.isPlanning());
                    costCenterItemDTO.setActualSecondaryCosts(lockIndicators.isActualSecondaryCosts());
                    costCenterItemDTO.setPlanSecondaryCosts(lockIndicators.isPlanSecondaryCosts());
                    costCenterItemDTO.setActualRevenuePostings(lockIndicators.isActualRevenuePostings());
                    costCenterItemDTO.setCommitmentUpdate(lockIndicators.isCommitmentUpdate());
                    costCenterItemDTO.setPlanningRevenues(lockIndicators.isPlanningRevenues());
                }
                
                List<CostCenterDescription> costCenterDescriptions = costCenterItem.getCostCenterDescription();
                if (costCenterDescriptions != null && !costCenterDescriptions.isEmpty()) {
                    for (CostCenterDescription costCenterDescription : costCenterItem.getCostCenterDescription()) {
                        if (costCenterDescription.getLanguage().equalsIgnoreCase(ENGLISH)) {
                            costCenterItemDTO.setDescriptionLong(costCenterDescription.getLongText());
                            costCenterItemDTO.setDescriptionShort(costCenterDescription.getShortText());
                            break;
                        }
                    }
                }
                costCenterItemDTO.setPersonResponsibleName(costCenterItem.getPersonResponsibleName());
                costCenterItemDTO.setPersonResponsibleUserId(costCenterItem.getPersonResponsibleUserId());

                EffectiveDatePeriodType effectiveDatePeriodType = costCenterItem.getEffectiveDatePeriod();
                XMLGregorianCalendar startDate = effectiveDatePeriodType.getStartDate();
                XMLGregorianCalendar endDate = effectiveDatePeriodType.getEndDate();
                costCenterItemDTO.setEffectiveStartDate(new Date(startDate.toGregorianCalendar().getTime().getTime()));
                costCenterItemDTO.setEffectiveEndDate(new Date(endDate.toGregorianCalendar().getTime().getTime()));
                

                costCenterItemDTOs.add(costCenterItemDTO);
            }
        }
        syncCostCenterDTO.setCostCenterItems(costCenterItemDTOs);
        return syncCostCenterDTO;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        return null;
    }

}
