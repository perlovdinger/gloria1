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
import com.volvo.gloria.materialRequestProxy.b.MaterialRequestCancelSender;
import com.volvo.gloria.materialRequestProxy.c.MaterialRequestToSendDTO;
import com.volvo.gloria.util.GloriaExceptionLogger;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.group.materialProcureRequest._1_0.ProcessMaterialProcureRequestType;
import com.volvo.jvs.integration.JmsFireNForget;
import com.volvo.jvs.integration.beans.JmsFireNForgetBean;
import com.volvo.jvs.integration.c.types.JmsSenderResult;
import com.volvo.jvs.integration.utils.MessageFactory;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Cancel material request sender bean.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MaterialRequestCancelSenderBean implements MaterialRequestCancelSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialRequestCancelSenderBean.class);
    private static final String GLORIA = "GLORIA";
    private static final String MATERIAL_REQUEST_PROXY = "MaterialRequestproxy";
    private ConnectionFactory connectionFactory = null;
    private Destination outgoingQueue = null;

    @Inject
    private MaterialRequestTransformerBean materialRequestTransformerBean;
    
    @Inject
    private CommonServices commonServices;

    @Inject
    @Named("connectionFactory")
    protected void setConnectionFactory(ConnectionFactory cf) {
        this.connectionFactory = cf;
    }

    @Inject
    @Named("procureRequestTOGatewayQueue")
    public void setDestination(Destination destination) {
        this.outgoingQueue = destination;
    }

    public void setCommonServices(CommonServices commonServices) {
        this.commonServices = commonServices;
    }
    
    @Override
    public String sendCancelMaterialRequest(MaterialRequestToSendDTO materialRequestToSendDTO) throws JAXBException {
        String marshalledXMLMessage = "";
        String messageId = "";
        try {
            JmsFireNForget fireNForget = new JmsFireNForgetBean(connectionFactory, outgoingQueue, true, Session.AUTO_ACKNOWLEDGE);
            marshalledXMLMessage = marshallXMLMessage(materialRequestToSendDTO);

            // validate XML to schema
            materialRequestTransformerBean.transform(marshalledXMLMessage);

            JmsSenderResult jmsSenderResult = fireNForget.sendMessage(MessageFactory.getTextMessageCreator(marshalledXMLMessage));
            Message message = jmsSenderResult.getOutboundMessage();
            if (message != null) {
                messageId = message.getJMSMessageID();
            }
        } catch (Exception e) {
            LOGGER.error("Error when sending cancel material procure request message:" + e.getMessage());
            GloriaExceptionLogger.log(e, MaterialRequestCancelSenderBean.class);
        } finally {
            LOGGER.info("MaterialRequestCancelSenderBean xml=" + marshalledXMLMessage);
            try {
                commonServices.logXMLMessage(new XmlLogEventDTO(GLORIA, GloriaParams.XML_MESSAGE_LOG_SENDING, MATERIAL_REQUEST_PROXY, messageId,
                                                            compress(marshalledXMLMessage)));
            } catch (IOException e) {
                LOGGER.error("An error occured while saving a cancel material procure request message [ID:" + messageId + "]: " + e.getMessage());
            }
        }

        return marshalledXMLMessage;
    }

    private String marshallXMLMessage(MaterialRequestToSendDTO materialRequestToSendDTO) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ProcessMaterialProcureRequestType.class.getPackage().getName());
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ProcessMaterialProcureRequestType processMaterialProcureRequestType = materialRequestTransformerBean.transformFromDTOToJAXB(materialRequestToSendDTO);
        JAXBElement<ProcessMaterialProcureRequestType> process = new JAXBElement<ProcessMaterialProcureRequestType>(new 
                QName("http://www.volvo.com/MaterialProcureRequest/1_0", "ProcessMaterialProcureRequest"), ProcessMaterialProcureRequestType.class,
                                                                                                        processMaterialProcureRequestType);
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(process, stringWriter);
        return stringWriter.toString();
    }

}
