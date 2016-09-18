package com.volvo.gloria.purchaseProxy.b.beans;

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
import com.volvo.gloria.config.b.beans.ApplicationProperties;
import com.volvo.gloria.purchaseProxy.b.RequisitionSender;
import com.volvo.gloria.purchaseProxy.c.RequisitionDTO;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.group.processrequisition._1_0.ProcessRequisitionType;
import com.volvo.jvs.integration.JmsFireNForget;
import com.volvo.jvs.integration.beans.JmsFireNForgetBean;
import com.volvo.jvs.integration.c.types.JmsSenderResult;
import com.volvo.jvs.integration.utils.MessageFactory;
import com.volvo.jvs.runtime.platform.ContainerManaged;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * RequisitionSender Implementation.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class RequisitionSenderBean implements RequisitionSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequisitionSenderBean.class);

    private static final String GPS = "GPS";

    private static final String INTEGRATION = "RequisitionProxy";

    private ConnectionFactory connectionFactory = null;

    private Destination outgoingQueue = null;

    @Inject
    private CommonServices commonServices;

    @Inject
    private RequisitionTransformerBean requisitionTransformerBean;

    @Inject
    @Named("connectionFactory")
    protected void setConnectionFactory(ConnectionFactory cf) {
        this.connectionFactory = cf;
    }

    @Inject
    @Named("requisitionSenderQueue")
    public void setDestination(Destination destination) {
        this.outgoingQueue = destination;
    }

    public String sendRequsition(RequisitionDTO requisitionDTO) throws JAXBException {
        String marshalledXMLMessage = "";
        String messageId = "";
        try {
            JmsFireNForget fireNForget = new JmsFireNForgetBean(connectionFactory, outgoingQueue, true, Session.AUTO_ACKNOWLEDGE);
            marshalledXMLMessage = marshallXMLMessage(requisitionDTO);

            // validate XML to schema
            requisitionTransformerBean.transform(marshalledXMLMessage);

            MessageFactory.getTextMessageCreator(marshalledXMLMessage);
            ApplicationProperties applicationProperties = ServiceLocator.getService(ApplicationProperties.class);
            boolean messageShouldBeSent = applicationProperties.isMessageSend();
            LOGGER.info("messageShouldBeSent=" + messageShouldBeSent);
            JmsSenderResult jmsSenderResult = null;
            if (messageShouldBeSent) {
                LOGGER.info("message sent for this Environment");
                jmsSenderResult = fireNForget.sendMessage(MessageFactory.getTextMessageCreator(marshalledXMLMessage));
                Message message = jmsSenderResult.getOutboundMessage();
                if (message != null) {
                    messageId = message.getJMSMessageID();
                    LOGGER.info("Message sent ok messageId=" + messageId);
                }
            } else {
                LOGGER.info("message NOT sent for this Environment");
                System.out.println("RequisitionSenderBean:" + " message NOT sent for this Environment");
            }
        } catch (Exception e) {
            LOGGER.error("Error when sending requisition message:" + e.getMessage());
            LOGGER.error("xml=" + marshalledXMLMessage);
        } finally {
            LOGGER.info("finally xml=" + marshalledXMLMessage);
            System.out.println("finally xml=" + marshalledXMLMessage);
            try {
                commonServices.logXMLMessage(new XmlLogEventDTO(GPS, GloriaParams.XML_MESSAGE_LOG_SENDING, INTEGRATION, messageId,
                                                                compress(marshalledXMLMessage)));
            } catch (IOException e) {
                LOGGER.error("xml=" + marshalledXMLMessage);
                LOGGER.error("An error occured while saving a requisition message [ID:" + messageId + "]: " + e.getMessage());
            }
        }

        return marshalledXMLMessage;
    }

    private String marshallXMLMessage(RequisitionDTO articleBalanceDTO) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ProcessRequisitionType.class.getPackage().getName());
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ProcessRequisitionType processRequisitionType = requisitionTransformerBean.transformFromDTOToJAXB(articleBalanceDTO);
        JAXBElement<ProcessRequisitionType> process = new JAXBElement<ProcessRequisitionType>(new QName("http://www.volvo.com/3P/Purchasing/2008/10",
                                                                                                        "ProcessRequisition"), ProcessRequisitionType.class,
                                                                                              processRequisitionType);
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(process, stringWriter);
        return stringWriter.toString();
    }

}
