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
import javax.jms.JMSException;
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
import com.volvo.gloria.financeProxy.b.GoodsReceiptSender;
import com.volvo.gloria.financeProxy.c.GoodsReceiptHeaderTransformerDTO;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.group.finance.goodsmovement._1_0.ProcessGoodsMovement;
import com.volvo.jvs.integration.JmsFireNForget;
import com.volvo.jvs.integration.beans.JmsFireNForgetBean;
import com.volvo.jvs.integration.c.types.JmsSenderResult;
import com.volvo.jvs.integration.utils.MessageFactory;
import com.volvo.jvs.runtime.platform.ContainerManaged;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * implentation for Goods Receipt Send on QI_OK.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class GoodsReceiptSenderBean implements GoodsReceiptSender {

    private static final String PROCESS_GOODS_MOVEMENT = "ProcessGoodsMovement";

    private static final String GOODSMOVEMENT_NAME_SPACE = "http://finance.group.volvo.com/goodsmovement/1_0";

    private static final String SAP_MIV = "SAP-MIV";

    private static final String INTEGRATION = "GoodsReceiptProxy";

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsReceiptSenderBean.class);

    private ConnectionFactory connectionFactory = null;

    private Destination outgoingQueue = null;

    @Inject
    private CommonServices commonServices;

    @Inject
    private GoodsReceiptTransformerBean goodReceiptTransformerBean;

    @Inject
    @Named("connectionFactory")
    protected void setConnectionFactory(ConnectionFactory cf) {
        this.connectionFactory = cf;
    }

    @Inject
    @Named("goodsReceiptSenderQueue")
    public void setDestination(Destination destination) {
        this.outgoingQueue = destination;
    }

    @Override
    public String sendGoodsReceipt(GoodsReceiptHeaderTransformerDTO goodsReceiptHeaderTransformerDTO) throws JAXBException {
        String marshalledXMLMessage = "";
        String messageId = "";
        try {
            JmsFireNForget fireNForget = new JmsFireNForgetBean(connectionFactory, outgoingQueue, true, Session.AUTO_ACKNOWLEDGE);
            marshalledXMLMessage = marshallXMLMessage(goodsReceiptHeaderTransformerDTO);

            // validate XML to schema
            goodReceiptTransformerBean.transform(marshalledXMLMessage);

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
                    commonServices.logXMLMessage(new XmlLogEventDTO(SAP_MIV, GloriaParams.XML_MESSAGE_LOG_SENDING, INTEGRATION, messageId,
                                                                    compress(marshalledXMLMessage)));
                }
            } else {
                LOGGER.info("message NOT sent for this Environment");
            }

        } catch (JMSException jMSException) {
            LOGGER.error("Error when sending goods receipt message:" + jMSException.getMessage(), jMSException);
        } catch (IOException iOException) {
            LOGGER.error("An error occured while saving a good receipt message [ID:" + messageId + "]: " + iOException.getMessage(), iOException);
        } finally {
            LOGGER.info("GoodsReceiptSenderBean xml=" + marshalledXMLMessage);
        }

        return marshalledXMLMessage;
    }

    private String marshallXMLMessage(GoodsReceiptHeaderTransformerDTO goodsReceiptHeaderTransformerDTO) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ProcessGoodsMovement.class.getPackage().getName());
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ProcessGoodsMovement processGoodsMovement = goodReceiptTransformerBean.transformFromDTOToJAXB(goodsReceiptHeaderTransformerDTO);
        JAXBElement<ProcessGoodsMovement> process = new JAXBElement<ProcessGoodsMovement>(new QName(GOODSMOVEMENT_NAME_SPACE, PROCESS_GOODS_MOVEMENT),
                                                                                          ProcessGoodsMovement.class, processGoodsMovement);
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(process, stringWriter);
        return stringWriter.toString();
    }
}
