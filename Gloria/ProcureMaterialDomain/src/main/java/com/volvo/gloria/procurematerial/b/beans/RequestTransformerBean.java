package com.volvo.gloria.procurematerial.b.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.procurematerial.b.RequestTransformer;
import com.volvo.gloria.procurematerial.d.type.request.RequestType;
import com.volvo.gloria.procurerequest.c.dto.ChangeIdTransformerDTO;
import com.volvo.gloria.procurerequest.c.dto.FinanceHeaderTransformerDTO;
import com.volvo.gloria.procurerequest.c.dto.PartAliasTransformerDTO;
import com.volvo.gloria.procurerequest.c.dto.RequestGatewayDTO;
import com.volvo.gloria.procurerequest.c.dto.RequestHeaderTransformerDTO;
import com.volvo.gloria.procurerequest.c.dto.RequestHeaderVersionTransformerDTO;
import com.volvo.gloria.procurerequest.c.dto.RequestLineTransformerDTO;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.group.materialProcureRequest._1_0.AliasType;
import com.volvo.group.materialProcureRequest._1_0.ApprovalInfoType;
import com.volvo.group.materialProcureRequest._1_0.BomLinkType;
import com.volvo.group.materialProcureRequest._1_0.BuildSeriesAddType;
import com.volvo.group.materialProcureRequest._1_0.BuildSeriesRemoveType;
import com.volvo.group.materialProcureRequest._1_0.BuildSeriesType;
import com.volvo.group.materialProcureRequest._1_0.ComponentType;
import com.volvo.group.materialProcureRequest._1_0.ContactPersonType;
import com.volvo.group.materialProcureRequest._1_0.FinancialInfoType;
import com.volvo.group.materialProcureRequest._1_0.HeaderType;
import com.volvo.group.materialProcureRequest._1_0.ItemToVariantLinkType;
import com.volvo.group.materialProcureRequest._1_0.LineAddType;
import com.volvo.group.materialProcureRequest._1_0.LineRemoveType;
import com.volvo.group.materialProcureRequest._1_0.LineType;
import com.volvo.group.materialProcureRequest._1_0.MaterialControllerType;
import com.volvo.group.materialProcureRequest._1_0.MaterialRequestChangeType;
import com.volvo.group.materialProcureRequest._1_0.MaterialRequestChangeTypeAdd;
import com.volvo.group.materialProcureRequest._1_0.MaterialRequestChangeTypeRemove;
import com.volvo.group.materialProcureRequest._1_0.MaterialRequestHeaderType;
import com.volvo.group.materialProcureRequest._1_0.MaterialRequestMessageType;
import com.volvo.group.materialProcureRequest._1_0.PartType;
import com.volvo.group.materialProcureRequest._1_0.PhaseAddType;
import com.volvo.group.materialProcureRequest._1_0.PhaseRemoveType;
import com.volvo.group.materialProcureRequest._1_0.PhaseType;
import com.volvo.group.materialProcureRequest._1_0.ProcessMaterialProcureRequestType;
import com.volvo.group.materialProcureRequest._1_0.RequesterType;
import com.volvo.group.materialProcureRequest._1_0.TestObjectHeaderType;
import com.volvo.group.materialProcureRequest._1_0.TestObjectRemoveType;
import com.volvo.group.materialProcureRequest._1_0.TestObjectType;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * RequestTransformer service implementation.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class RequestTransformerBean extends XmlTransformer implements RequestTransformer {
    
    private static final String SPARE = "SPARE";

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestTransformerBean.class);

    private static final String TESTOBJECT_FIRST_BUILD = "TESTOBJECT_FIRST_BUILD";
    private static final String TESTOBJECT_MR = "TESTOBJECT_MR";
    private static final String FIRST_ASSEMBLY = "FIRST_ASSEMBLY";
    private static final String FA = "FA";
    private static final String LC = "LC";
    private static final String UNDERSCORE = "_";
    private static final String MR = "MR";

    public RequestTransformerBean() {
        super(XmlConstants.SchemaFullPath.PROCURE_TO_REQUEST_OBJECT, XmlConstants.PackageName.PROCURE_TO_REQUEST_OBJECT);
    }

    /**
     * Perform transformation from external information model to the application internal model.
     * 
     * @param requestMessage
     *            the Message to transform
     * @return a list of RequestGatewayDTO objects
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<RequestGatewayDTO> transformRequest(String requestMessage) {
        try {
            return (List<RequestGatewayDTO>) transform(requestMessage);
        } catch (JAXBException e) {
            // Throw an exception to roll-back the transaction.
            LOGGER.error("Unable to unmarshall the received message to a RequestGatewayDTO object, message will be discarded. Exception is:", e);
            throw new GloriaSystemException(e, "Unable to unmarshall the received message to a RequestGatewayDTO object, message will be discarded.");
        }
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        ProcessMaterialProcureRequestType requestMessageRoot = (ProcessMaterialProcureRequestType) jaxbOject;
        String senderLogicalId = requestMessageRoot.getApplicationArea().getSender().getLogicalID();
        List<MaterialRequestChangeType> procureMessageChanges = requestMessageRoot.getDataArea().getMaterialRequestChange();
        List<RequestGatewayDTO> requestGatewayDtos = new ArrayList<RequestGatewayDTO>();

        for (MaterialRequestChangeType procureMessageChangeType : procureMessageChanges) {
            MaterialRequestChangeTypeAdd materialRequestChangeAdd = procureMessageChangeType.getMaterialRequestChangeAdd();
            MaterialRequestChangeTypeRemove materialRequestChangeRemove = procureMessageChangeType.getMaterialRequestChangeRemove();
            if (materialRequestChangeAdd != null) {
                RequestGatewayDTO requestGatewayDto = new RequestGatewayDTO();
                requestGatewayDto.setSenderLogicalId(senderLogicalId);
                String requestType = procureMessageChangeType.getRequestType();
                String materialRequestType = procureMessageChangeType.getMaterialRequestChangeAdd().getType();
                if (materialRequestType.equalsIgnoreCase(LC) || materialRequestType.equalsIgnoreCase(MR)) {
                    requestType = TESTOBJECT_MR;
                }
                if (materialRequestType.equalsIgnoreCase(FIRST_ASSEMBLY)) {
                    requestType = TESTOBJECT_FIRST_BUILD;
                }

                ChangeIdTransformerDTO changeIdTransformerDTO = prepareChangeIdTransformerDTO(procureMessageChangeType, requestType);
                requestGatewayDto.setChangeIdTransformerDto(changeIdTransformerDTO);

                List<MaterialRequestMessageType> procureMessageTypes = materialRequestChangeAdd.getMaterialRequestMessage();
                ApprovalInfoType approvalInfoType = materialRequestChangeAdd.getApprovalInfo();
                List<RequestHeaderTransformerDTO> requestHeaderTransformerDtos = new ArrayList<RequestHeaderTransformerDTO>();

                for (MaterialRequestMessageType materialRequestMessageType : procureMessageTypes) {
                    HeaderType headerType = materialRequestMessageType.getHeader();

                    List<ComponentType> componentTypes = materialRequestMessageType.getComponent();

                    if (componentTypes != null && !componentTypes.isEmpty()) {
                        for (ComponentType componentType : componentTypes) {
                            prepareRequestDTOs(materialRequestChangeAdd, requestType, approvalInfoType, requestHeaderTransformerDtos, 
                                               headerType, componentType);
                        }
                    } else {
                        prepareRequestDTOs(materialRequestChangeAdd, requestType, approvalInfoType, requestHeaderTransformerDtos, headerType, null);
                    }

                }
                //sort to ensure that the headers with ADD/REMOVE change ends up first always.
                Collections.sort(requestHeaderTransformerDtos);
                requestGatewayDto.setRequestHeaderTransformerDtos(requestHeaderTransformerDtos);

                requestGatewayDtos.add(requestGatewayDto);
            } else if (materialRequestChangeRemove != null) {
                RequestGatewayDTO requestGatewayDto = new RequestGatewayDTO();
                requestGatewayDto.setCancelRequest(true);
                ChangeIdTransformerDTO changeIdDto = new ChangeIdTransformerDTO();
                changeIdDto.setChangeTechId(materialRequestChangeRemove.getChangeTechId());
                changeIdDto.setChangeId(materialRequestChangeRemove.getMtrlRequestVersion());
                requestGatewayDto.setChangeIdTransformerDto(changeIdDto);
                requestGatewayDto.setSenderLogicalId(senderLogicalId);
                requestGatewayDtos.add(requestGatewayDto);
            }
        }
        return requestGatewayDtos;
    }

    private void prepareRequestDTOs(MaterialRequestChangeTypeAdd materialRequestChangeAdd, String requestType, ApprovalInfoType approvalInfoType,
            List<RequestHeaderTransformerDTO> requestHeaderTransformerDtos, HeaderType headerType, ComponentType componentType) {
        RequestHeaderTransformerDTO requestHeaderTransformerDto = prepareRequestHeaderDTO(componentType, headerType, requestType, materialRequestChangeAdd);

        RequestHeaderVersionTransformerDTO requestHeaderVersionDto = prepareRequestHeaderVersion(headerType, requestType, componentType);
        requestHeaderTransformerDto.getRequestHeaderVersionTransformerDtos().add(requestHeaderVersionDto);

        List<RequestLineTransformerDTO> requestLineTransformerDtos = prepareRequestLineDTO(approvalInfoType, componentType);
        requestHeaderTransformerDto.setRequestLineTransformerDTOs(requestLineTransformerDtos);

        if (headerType != null && headerType.getAdditonalUsageHeader() != null) {
            requestHeaderTransformerDto.setAdditonalUsageHeader(true);
            String mtrlRequestId = materialRequestChangeAdd.getType() + materialRequestChangeAdd.getId();
            requestHeaderTransformerDto.setReferenceId("");
            requestHeaderTransformerDto.setMtrlRequestId(SPARE + UNDERSCORE + mtrlRequestId);
        }

        requestHeaderTransformerDtos.add(requestHeaderTransformerDto);
    }

    private List<RequestLineTransformerDTO> prepareRequestLineDTO(ApprovalInfoType approvalInfoType, ComponentType componentType) {
        List<RequestLineTransformerDTO> requestLines = new ArrayList<RequestLineTransformerDTO>();
        if (componentType != null) {
            List<LineType> lineTypes = componentType.getLine();

            for (LineType lineType : lineTypes) {
                LineAddType lineAddType = lineType.getLineAddType();

                if (lineAddType != null) {
                    RequestLineTransformerDTO requestLineTransformerDto = new RequestLineTransformerDTO();

                    transformBomLinkInfo(lineAddType, requestLineTransformerDto);

                    transformPartInformation(lineAddType, requestLineTransformerDto);

                    transformFinanceHeaderX(lineAddType, requestLineTransformerDto);

                    transformItemToVariantLink(lineAddType, requestLineTransformerDto);
                    
                    if (approvalInfoType != null) {
                        String mailFormId = approvalInfoType.getMailformId().trim();
                        if (!StringUtils.isEmpty(mailFormId)) {
                            requestLineTransformerDto.setMailFormId(mailFormId);
                        }
                    }

                    if (lineAddType.getRequiredShipToArrive() != null) {
                        requestLineTransformerDto.setRequiredStaDate(lineAddType.getRequiredShipToArrive().toGregorianCalendar().getTime());
                    }
                    requestLines.add(requestLineTransformerDto);
                }

                LineRemoveType lineRemoveType = lineType.getLineRemoveType();
                if (lineRemoveType != null) {
                    RequestLineTransformerDTO requestLineTransformerDto = new RequestLineTransformerDTO();
                    requestLineTransformerDto.setRemoveType(true);
                    requestLineTransformerDto.setProcureLinkId(lineRemoveType.getProcureLinkId());
                    requestLines.add(requestLineTransformerDto);
                }
            }
        }
        return requestLines;
    }

    private void transformItemToVariantLink(LineAddType lineAddType, RequestLineTransformerDTO requestLineTransformerDto) {
        ItemToVariantLinkType itemToVariantLink = lineAddType.getItemToVariantLink();
        if (itemToVariantLink != null) {
            requestLineTransformerDto.setItemToVariantLinkId(itemToVariantLink.getId());
            requestLineTransformerDto.setModularHarness(itemToVariantLink.getModularHarness());
        }
    }

    private void transformPartInformation(LineAddType lineAddType, RequestLineTransformerDTO requestLineTransformerDto) {
        PartType part = lineAddType.getPart();
        requestLineTransformerDto.setPartName(part.getName());
        requestLineTransformerDto.setUnitOfMeasure(part.getUnitOfMeasure().value());
        requestLineTransformerDto.setDesignResponsible(part.getDesignResponsible());
        requestLineTransformerDto.setObjectNumber(part.getObjectNumber());
        requestLineTransformerDto.setDemarcation(part.getDemarcation());
        requestLineTransformerDto.setCharacteristics(part.getCharacteristics());

        List<AliasType> aliases = part.getAlias();
        List<PartAliasTransformerDTO> partAliasTransformerDtos = new ArrayList<PartAliasTransformerDTO>();
        for (AliasType alias : aliases) {
            PartAliasTransformerDTO partAliasTransformerDTO = new PartAliasTransformerDTO();
            partAliasTransformerDTO.setCode(alias.getId());
            partAliasTransformerDTO.setDomain(alias.getDomain());
            partAliasTransformerDtos.add(partAliasTransformerDTO);
        }
        requestLineTransformerDto.setPartAliasTransformerDtos(partAliasTransformerDtos);
    }

    private void transformBomLinkInfo(LineAddType lineAddType, RequestLineTransformerDTO requestLineTransformerDto) {
        BomLinkType bomLink = lineAddType.getBomLink();
        requestLineTransformerDto.setProcureLinkId(bomLink.getProcureLinkId());
        requestLineTransformerDto.setPartAffiliation(bomLink.getPartAffiliation());
        requestLineTransformerDto.setPartNumber(bomLink.getPartNumber());
        requestLineTransformerDto.setPartVersion(bomLink.getPartVersion());
        requestLineTransformerDto.setPartModification(bomLink.getPartModification());
        requestLineTransformerDto.setQuantity(bomLink.getQuantity());
        requestLineTransformerDto.setProcureComment(bomLink.getProcureComment());
        requestLineTransformerDto.setProductClass(bomLink.getProductClassId());
        requestLineTransformerDto.setFunctionGroup(bomLink.getFunctionGroup());
        requestLineTransformerDto.setFunctionGroupSuffix(bomLink.getFunctionGroupSuffix());
        requestLineTransformerDto.setRefAssemblyPartNo(bomLink.getRefAssemblyPartNo());
        requestLineTransformerDto.setRefAssemblyPartVersion(bomLink.getRefAssemblyPartVersion());
    }

    private void transformFinanceHeaderX(LineAddType lineAddType, RequestLineTransformerDTO requestLineTransformerDto) {
        FinancialInfoType financeInfo = lineAddType.getFinancialInfo();
        FinanceHeaderTransformerDTO financeHeaderXTransformerDto = new FinanceHeaderTransformerDTO();
        financeHeaderXTransformerDto.setProjectId(financeInfo.getProjectId());
        financeHeaderXTransformerDto.setCompanyCode(financeInfo.getCompanyCode());
        financeHeaderXTransformerDto.setGlAccount(financeInfo.getGlAccount());
        financeHeaderXTransformerDto.setCostCenter(financeInfo.getCostCenter());
        financeHeaderXTransformerDto.setWbsCode(financeInfo.getWbsCode());
        financeHeaderXTransformerDto.setInternalorderNoSAP(financeInfo.getInternalOrderNoSAP());

        requestLineTransformerDto.setFinanceHeaderTransformerDTO(financeHeaderXTransformerDto);
    }

    private RequestHeaderVersionTransformerDTO prepareRequestHeaderVersion(HeaderType headerAddType, String requestType, ComponentType componentType) {
        RequestHeaderVersionTransformerDTO requestHeaderVersionTransformerDto = new RequestHeaderVersionTransformerDTO();
        if (headerAddType != null) {
            MaterialRequestHeaderType materialRequestHeaderType = headerAddType.getMaterialRequestHeader();
            ContactPersonType contactPerson = headerAddType.getContactPerson();

            requestHeaderVersionTransformerDto.setReceivedDateTime(DateUtil.getCurrentUTCDateTime());

            if (RequestType.valueOf(requestType).isProtomRequest()) {
                TestObjectHeaderType testObjectHeaderType = headerAddType.getTestObjectHeader();
                if (testObjectHeaderType != null) {
                    BuildSeriesType buildSeriesInfo = testObjectHeaderType.getBuildSeriesInfo();
                    if (buildSeriesInfo != null) {
                        BuildSeriesAddType buildSeries = buildSeriesInfo.getBuildSeries();
                        if (buildSeries != null) {
                            requestHeaderVersionTransformerDto.setReferenceGroup(buildSeries.getName());
                        }
                        BuildSeriesRemoveType buildSeriesRemove = buildSeriesInfo.getBuildSeriesRemove();
                        if (buildSeriesRemove != null) {
                            requestHeaderVersionTransformerDto.setReferenceGroup("");
                        }
                    }

                    PhaseType phaseInfo = testObjectHeaderType.getPhaseInfo();
                    if (phaseInfo != null) {
                        PhaseAddType phase = phaseInfo.getPhase();
                        if (phase != null) {
                            requestHeaderVersionTransformerDto.setOutboundStartDate(phase.getPlannedAssemblyStartDate().toGregorianCalendar().getTime());
                        }
                    }
                }

                if (componentType != null) {
                    String buildSiteId = componentType.getBuildSiteType().getId().trim();
                    if (!StringUtils.isEmpty(buildSiteId)) {
                        requestHeaderVersionTransformerDto.setOutboundLocationId(buildSiteId);
                    }
                }

            } else {
                XMLGregorianCalendar requiredShipToArrive = materialRequestHeaderType.getRequiredShipToArrive();
                if (requiredShipToArrive != null) {
                    requestHeaderVersionTransformerDto.setRequiredSTADate(requiredShipToArrive.toGregorianCalendar().getTime());
                }
                requestHeaderVersionTransformerDto.setReferenceGroup(materialRequestHeaderType.getReferenceGroup());
                String buildSiteId = materialRequestHeaderType.getOutboundLocationId().trim();
                if (!StringUtils.isEmpty(buildSiteId)) {
                    requestHeaderVersionTransformerDto.setOutboundLocationId(buildSiteId);
                }
                XMLGregorianCalendar xmlOutboundStartDate = materialRequestHeaderType.getOutboundStartDate();
                if (xmlOutboundStartDate != null) {
                    requestHeaderVersionTransformerDto.setOutboundStartDate(xmlOutboundStartDate.toGregorianCalendar().getTime());
                }
            }

            RequesterType requester = headerAddType.getRequester();
            if (requester != null) {
                requestHeaderVersionTransformerDto.setRequesterUserId(requester.getUserId());
                requestHeaderVersionTransformerDto.setRequesterName(requester.getName());
                requestHeaderVersionTransformerDto.setRequesterNotes(requester.getNotes());
            }
            
            MaterialControllerType materialController = headerAddType.getMaterialController();
            if (materialController != null) {
                requestHeaderVersionTransformerDto.setMaterialControllerUserId(materialController.getUserId());
                requestHeaderVersionTransformerDto.setMaterialControllerName(materialController.getName());
            }

            if (contactPerson != null) {
                requestHeaderVersionTransformerDto.setContactPersonId(contactPerson.getUserId());
                requestHeaderVersionTransformerDto.setContactPersonName(contactPerson.getName());
            }
            // sort to pick the earliest requiredSTADate from Lines.
            if (componentType != null) {
                List<LineType> lineTypes = componentType.getLine();
                if (lineTypes != null && !lineTypes.isEmpty()) {
                    requestHeaderVersionTransformerDto.setContainingLines(true);
                    Collections.sort(lineTypes, new Comparator<LineType>() {
                        public int compare(LineType lineOne, LineType lineTwo) {
                            LineAddType lineAddType1 = lineOne.getLineAddType();
                            LineAddType lineAddType2 = lineTwo.getLineAddType();
                            if (lineAddType1 == null || lineAddType2 == null || lineAddType1.getRequiredShipToArrive() == null
                                    || lineAddType2.getRequiredShipToArrive() == null) {
                                return 1;
                            }
                            Date reqdShipToArrive2 = lineAddType2.getRequiredShipToArrive().toGregorianCalendar().getTime();
                            Date reqdShipToArrive1 = lineAddType1.getRequiredShipToArrive().toGregorianCalendar().getTime();
                            return reqdShipToArrive2.compareTo(reqdShipToArrive1);
                        }
                    });
                    if (lineTypes.get(0).getLineAddType() != null) {
                        XMLGregorianCalendar requiredShipToArrive = lineTypes.get(0).getLineAddType().getRequiredShipToArrive();
                        requestHeaderVersionTransformerDto.setRequiredSTADate(requiredShipToArrive.toGregorianCalendar().getTime());
                    }
                }
            }
        }
        return requestHeaderVersionTransformerDto;
    }

    private RequestHeaderTransformerDTO prepareRequestHeaderDTO(ComponentType componentType, HeaderType headerType, String requestType,
            MaterialRequestChangeTypeAdd materialRequestChangeTypeAdd) {
        RequestHeaderTransformerDTO requestHeaderTransformerDto = new RequestHeaderTransformerDTO();
        requestHeaderTransformerDto.setRequestType(requestType);

        if (RequestType.valueOf(requestType).isProtomRequest()) {
            String changeTechId = materialRequestChangeTypeAdd.getChangeTechId();
            if (headerType != null) {
                TestObjectHeaderType testObjectHeader = headerType.getTestObjectHeader();
                if (testObjectHeader != null) {
                    TestObjectType testObjectType = testObjectHeader.getTestObjectInfo().getTestObject();

                    String testObjectId = "";
                    if (testObjectType != null) {
                        testObjectId = testObjectType.getId();
                    }
                    StringBuffer mtrlRequestIdBuffer = new StringBuffer();
                    mtrlRequestIdBuffer.append(testObjectId);

                    PhaseType phaseInfo = testObjectHeader.getPhaseInfo();
                    String phaseAddId = "";
                    String phaseAddName = "";
                    if (phaseInfo != null) {
                        PhaseAddType phaseAdd = phaseInfo.getPhase();
                        if (phaseAdd != null) {
                            requestHeaderTransformerDto.setExistsPhase(true);
                            phaseAddId = phaseAdd.getId() + "";
                            phaseAddName = phaseAdd.getName();
                            requestHeaderTransformerDto.setBuildType(phaseAdd.getType());
                            requestHeaderTransformerDto.setBuildId(phaseAddId);
                            requestHeaderTransformerDto.setBuildName(phaseAddName);
                            mtrlRequestIdBuffer.append(UNDERSCORE + phaseAddId);
                        }
                        PhaseRemoveType phaseRemove = phaseInfo.getPhaseRemove();
                        if (phaseRemove != null) {
                            requestHeaderTransformerDto.setExistsPhaseRemove(true);
                            requestHeaderTransformerDto.setBuildRemoveId(phaseRemove.getId());
                            if (changeTechId != null) {
                                mtrlRequestIdBuffer.append(UNDERSCORE + changeTechId);
                            }
                        }
                    } else {
                        if (changeTechId != null) {
                            mtrlRequestIdBuffer.append(UNDERSCORE + changeTechId);
                        }
                    }

                    if (componentType != null && componentType.getBuildSiteType() != null) {
                        mtrlRequestIdBuffer.append(UNDERSCORE + componentType.getBuildSiteType().getId());
                    }

                    requestHeaderTransformerDto.setMtrlRequestId(mtrlRequestIdBuffer.toString());

                    requestHeaderTransformerDto.setReferenceId(testObjectId);
                    
                    
                    TestObjectRemoveType testObjectRemoveType = testObjectHeader.getTestObjectInfo().getTestObjectRemove();
                    if (testObjectRemoveType != null) {
                        requestHeaderTransformerDto.setExistsTestObjectRemove(true);
                        requestHeaderTransformerDto.setRemoveReferenceId(testObjectRemoveType.getId());
                    }
                }
            }
        } else {
            MaterialRequestHeaderType materialRequestHeader = headerType.getMaterialRequestHeader();
            requestHeaderTransformerDto.setReferenceId(materialRequestHeader.getReferenceId());
            requestHeaderTransformerDto.setMtrlRequestId(materialRequestChangeTypeAdd.getId());
        }

        requestHeaderTransformerDto.setActive(true);
        return requestHeaderTransformerDto;
    }

    private ChangeIdTransformerDTO prepareChangeIdTransformerDTO(MaterialRequestChangeType materialRequestChangeType, String requestType) {
        ChangeIdTransformerDTO changeIdTransformerDto = new ChangeIdTransformerDTO();

        String materialRequestType = materialRequestChangeType.getMaterialRequestChangeAdd().getType();
        String materialRequestChangeAddId = materialRequestChangeType.getMaterialRequestChangeAdd().getId();

        if (RequestType.valueOf(requestType).isProtomRequest()) {
            if (materialRequestType.equalsIgnoreCase(MR) || materialRequestType.equalsIgnoreCase(LC)) {
                changeIdTransformerDto.setChangeId(materialRequestType + materialRequestChangeAddId);
            } else {
                List<MaterialRequestMessageType> procureMessage = materialRequestChangeType.getMaterialRequestChangeAdd().getMaterialRequestMessage();
                if (procureMessage != null) {
                    changeIdTransformerDto.setChangeId(FA);
                }
            }

            changeIdTransformerDto.setPriority(materialRequestChangeType.getMaterialRequestChangeAdd().getProcurePrioritity());
            changeIdTransformerDto.setProcureMessageId(materialRequestChangeType.getMaterialRequestChangeAdd().getProcureMessageId());
            changeIdTransformerDto.setMaterialRequestChangeAddId(materialRequestChangeAddId);
            changeIdTransformerDto.setProcureRequestId(materialRequestChangeType.getMaterialRequestChangeAdd().getChangeTechId());
            
            // For Protom procureMessageId should be used as changeTechid
            changeIdTransformerDto.setChangeTechId(materialRequestChangeType.getMaterialRequestChangeAdd().getProcureMessageId());

        } else {
            changeIdTransformerDto.setChangeTechId(materialRequestChangeType.getMaterialRequestChangeAdd().getChangeTechId());
            changeIdTransformerDto.setChangeId(materialRequestChangeType.getMaterialRequestChangeAdd().getMtrlRequestVersion());
        }

        changeIdTransformerDto.setType(materialRequestType);
        changeIdTransformerDto.setTitle(materialRequestChangeType.getMaterialRequestChangeAdd().getTitle());
        return changeIdTransformerDto;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        return null;
    }

}
