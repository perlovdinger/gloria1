package com.volvo.gloria.materialRequestProxy.b.beans;

import static com.volvo.gloria.util.Utils.compress;

import java.io.IOException;
import java.io.StringWriter;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.Session;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.XmlLogEventDTO;
import com.volvo.gloria.materialRequestProxy.b.MaterialProcureResponse;
import com.volvo.gloria.materialRequestProxy.c.MaterialProcureResponseDTO;
import com.volvo.gloria.util.GloriaExceptionLogger;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.jvs.integration.JmsFireNForget;
import com.volvo.jvs.integration.beans.JmsFireNForgetBean;
import com.volvo.jvs.integration.c.types.JmsSenderResult;
import com.volvo.jvs.integration.utils.MessageFactory;
import com.volvo.jvs.runtime.platform.ContainerManaged;
import com.volvo.materialprocureresponse._1_0.ProcessMaterialProcureResponseType;

@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MaterialProcureResponseBean implements MaterialProcureResponse {
    private static final String PROCESS_MATERIAL_PROCURE_RESPONSE_TYPE = "ProcessMaterialProcureResponse";

    private static final String MATERIALPROCURERESPONSE_NAME_SPACE = "http://www.volvo.com/MaterialProcureResponse/1_0";

    private static final String MATERIAL_PROCURE_RESPONSE = "MATERIAL-PROCURE-RESPONSE";

    private static final String MATERIAL_REQUEST_PROXY = "MaterialRequestProxy";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialProcureResponseBean.class);

    private ConnectionFactory connectionFactory = null;

    private Destination outgoingQueue = null;
    
    @Inject
    private MaterialProcureProxyTransformerBean materialProcureResponseTransformerBean;
    
    @Inject
    private CommonServices commonServices;

    @Inject
    @Named("connectionFactoryBus")
    protected void setConnectionFactory(ConnectionFactory cf) {
        this.connectionFactory = cf;
    }

    @Inject
    @Named("materialProcureResponseSenderQueue")
    public void setDestination(Destination destination) {
        this.outgoingQueue = destination;
    }


    @Override
    public String sendMaterialProcureResponse(String mtrlRequestVersion, String userId, String response, String changeTechId, String mtrlRequestId)  
            throws JAXBException {
        String marshalledXMLMessage = "";
        String messageId = "";
        try {
            MaterialProcureResponseDTO materialProcureResponseDTO = new MaterialProcureResponseDTO();
            
            materialProcureResponseDTO.setUserId(userId);
            materialProcureResponseDTO.setResponse(response);
            materialProcureResponseDTO.setChangeId(mtrlRequestVersion);
            materialProcureResponseDTO.setChangeTechId(changeTechId);
            materialProcureResponseDTO.setMaterialRequestId(mtrlRequestId);
            
            
            JmsFireNForget fireNForget = new JmsFireNForgetBean(connectionFactory, outgoingQueue, true, Session.AUTO_ACKNOWLEDGE);
            marshalledXMLMessage = marshallXMLMessage(materialProcureResponseDTO);
            
            // validate XML to schema
            materialProcureResponseTransformerBean.transform(marshalledXMLMessage);

            MessageFactory.getTextMessageCreator(marshalledXMLMessage);
            JmsSenderResult jmsSenderResult = fireNForget.sendMessage(MessageFactory.getTextMessageCreator(marshalledXMLMessage));
            Message message = jmsSenderResult.getOutboundMessage();
            if (message != null) {
                messageId = message.getJMSMessageID();
            }
        } catch (Exception e) {
            LOGGER.error("Error when material procure response message:" + e.getMessage());
            GloriaExceptionLogger.log(e, MaterialProcureResponseBean.class);
        } finally {
            LOGGER.info("MaterialProcureResponseBean xml=" + marshalledXMLMessage);
            try {
                commonServices.logXMLMessage(new XmlLogEventDTO(MATERIAL_PROCURE_RESPONSE,
                              GloriaParams.XML_MESSAGE_LOG_SENDING, MATERIAL_REQUEST_PROXY, messageId, compress(marshalledXMLMessage)));
            } catch (IOException e) {
                LOGGER.error("An error occured while saving a material procure response message [ID:" + messageId + "]: " + e.getMessage());
            }
        }

        return marshalledXMLMessage;
    }

    private String marshallXMLMessage(MaterialProcureResponseDTO materialProcureResponseDTO) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ProcessMaterialProcureResponseType.class.getPackage().getName());
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ProcessMaterialProcureResponseType processMaterialProcureResponseType = materialProcureResponseTransformerBean.transformFromDTOToJAXB(
                                                                                materialProcureResponseDTO);
        JAXBElement<ProcessMaterialProcureResponseType> process = 
                new JAXBElement<ProcessMaterialProcureResponseType>(new QName(MATERIALPROCURERESPONSE_NAME_SPACE,
                   PROCESS_MATERIAL_PROCURE_RESPONSE_TYPE), ProcessMaterialProcureResponseType.class, processMaterialProcureResponseType);
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(process, stringWriter);
        return stringWriter.toString();
    }

}
