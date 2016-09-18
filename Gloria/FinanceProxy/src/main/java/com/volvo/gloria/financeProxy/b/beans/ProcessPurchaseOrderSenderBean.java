package com.volvo.gloria.financeProxy.b.beans;

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
import com.volvo.gloria.financeProxy.b.ProcessPurchaseOrderSender;
import com.volvo.gloria.financeProxy.c.ProcessPurchaseOrderDTO;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.group.processpurchaseorder._1_0.ProcessPurchaseOrder;
import com.volvo.group.processpurchaseorder._1_0.PurchaseOrderType;
import com.volvo.jvs.integration.JmsFireNForget;
import com.volvo.jvs.integration.beans.JmsFireNForgetBean;
import com.volvo.jvs.integration.c.types.JmsSenderResult;
import com.volvo.jvs.integration.utils.MessageFactory;
import com.volvo.jvs.runtime.platform.ContainerManaged;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ProcessPurchaseOrderSenderBean implements ProcessPurchaseOrderSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessPurchaseOrderSenderBean.class);

    private static final String SAP = "SAP";

    private static final String INTEGRATION = "ProcessPurchaseOrderProxy";

    private ConnectionFactory connectionFactory = null;

    private Destination outgoingQueue = null;

    @Inject
    private CommonServices commonServices;

    @Inject
    private ProcessPurchaseOrderTransformerBean processOrderTransformerBean;

    @Inject
    @Named("connectionFactory")
    protected void setConnectionFactory(ConnectionFactory cf) {
        this.connectionFactory = cf;
    }

    @Inject
    @Named("processPurchaseOrderQueue")
    public void setDestination(Destination destination) {
        this.outgoingQueue = destination;
    }

    @Override
    public String sendProcessPurchaseOrder(ProcessPurchaseOrderDTO processPurchaseOrderDTO) throws JAXBException {
        String marshalledXMLMessage = "";
        String messageId = "";
        try {
            JmsFireNForget fireNForget = new JmsFireNForgetBean(connectionFactory, outgoingQueue, true, Session.AUTO_ACKNOWLEDGE);
            marshalledXMLMessage = marshallXMLMessage(processPurchaseOrderDTO);

            // validate XML to schema
            if (marshalledXMLMessage == null) {
                throw new GloriaSystemException("Marshalled message is null", null);
            }
            processOrderTransformerBean.transform(marshalledXMLMessage);

            MessageFactory.getTextMessageCreator(marshalledXMLMessage);
            ApplicationProperties applicationProperties = ServiceLocator.getService(ApplicationProperties.class);
            boolean messageShouldBeSent = applicationProperties.isMessageSend();
            LOGGER.info("messageShouldBeSent=" + messageShouldBeSent);
            
            if (messageShouldBeSent) {
               JmsSenderResult jmsSenderResult = fireNForget.sendMessage(MessageFactory.getTextMessageCreator(marshalledXMLMessage));
                Message message = jmsSenderResult.getOutboundMessage();
                if (message != null) {
                    messageId = message.getJMSMessageID();
                    LOGGER.info("Message sent ok messageId=" + messageId);
                }
            } else {
                LOGGER.info("message NOT sent for this Environment");
            }
        } catch (Exception e) {
            LOGGER.error("Error when sending process purchase order message:" + e.getMessage(), e);
            LOGGER.error("xml=" + marshalledXMLMessage, e);
        } finally {
            LOGGER.info("xml=" + marshalledXMLMessage);
            try {
                commonServices.logXMLMessage(new XmlLogEventDTO(SAP, GloriaParams.XML_MESSAGE_LOG_SENDING, INTEGRATION, messageId,
                                                                compress(marshalledXMLMessage)));
            } catch (IOException e) {
                LOGGER.error("xml=" + marshalledXMLMessage);
                LOGGER.error("An error occured while saving a processPurchaseOrderTransformerBean message [ID:" + messageId + "]: " + e.getMessage());
            }
        }

        return marshalledXMLMessage;
    }

    private String marshallXMLMessage(ProcessPurchaseOrderDTO orderSapDTO) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(PurchaseOrderType.class.getPackage().getName());
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ProcessPurchaseOrder processPurchaseOrder = processOrderTransformerBean.transformFromDTOToJAXB(orderSapDTO);
        if (processPurchaseOrder != null) {
            JAXBElement<ProcessPurchaseOrder> process = new JAXBElement<ProcessPurchaseOrder>(new QName("http://www.volvo.com/3P/Purchasing/2008/10",
                                                                                                        "ProcessPurchaseOrder"), ProcessPurchaseOrder.class,
                                                                                              processPurchaseOrder);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(process, stringWriter);
            return stringWriter.toString();
        } else {
            return null;
        }
    }

}
