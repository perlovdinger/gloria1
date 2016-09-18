package com.volvo.gloria.gateway.b.beans;

import static com.volvo.gloria.util.Utils.compress;

import java.util.List;

import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.XmlLogEventDTO;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.b.RequestTransformer;
import com.volvo.gloria.procurerequest.c.dto.RequestGatewayDTO;
import com.volvo.gloria.util.GloriaExceptionLogger;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * GatewayBean for receive procure request from Protom.
 */
@ContainerManaged
public class RequestGatewayBean implements MessageListener {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestGatewayBean.class);

    private static final String INTEGRATION = "RequestGateway";
    
    @Inject
    private ProcurementServices procurementServices;
    
    @Inject
    private CommonServices commonServices;
    
    @Inject
    private RequestTransformer requestTransformer;
    
    public void setCommonServices(CommonServices commonServices) {
        this.commonServices = commonServices;
    }
    
    public void setProcurementServices(ProcurementServices procurementServices) {
        this.procurementServices = procurementServices;
    }

    @Override
    public void onMessage(Message msg) {
        if (msg instanceof TextMessage) {
            TextMessage requestMessage = (TextMessage) msg;
            String jmsMessageID = "";
            String textMessage = "";
            String senderLogicalId = "";
            try {
                jmsMessageID = msg.getJMSMessageID();
                textMessage = requestMessage.getText();
                List<RequestGatewayDTO> requestGatewayDtos = requestTransformer.transformRequest(textMessage);
                senderLogicalId = requestGatewayDtos.get(0).getSenderLogicalId();
                procurementServices.manageRequest(requestGatewayDtos);
            } catch (Exception e) {
                LOGGER.error("An error occured while receiving a Request message [ID:" + jmsMessageID + "]: " + e.getMessage());
                GloriaExceptionLogger.log(e, RequestGatewayBean.class);
            } finally {
                LOGGER.info("RequestGatewayBean xml=" + textMessage);
                try {
                    commonServices.logXMLMessage(new XmlLogEventDTO(senderLogicalId, GloriaParams.XML_MESSAGE_LOG_RECEIVING, INTEGRATION, jmsMessageID,
                                                                    compress(textMessage)));
                } catch (Exception e) {
                    LOGGER.error("An error occured while saving a incoming Request message [ID:" + jmsMessageID + "]: " + e.getMessage() , e);
                }
            }
        } else {
            LOGGER.error("A NON TextMessage was received! message received is of class: " + msg.getClass().getName());
        }
    }

}
