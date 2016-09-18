package com.volvo.gloria.gateway.b.beans;

import static com.volvo.gloria.util.Utils.compress;

import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.XmlLogEventDTO;
import com.volvo.gloria.materialrequest.b.MaterialProcureGatewayTranformer;
import com.volvo.gloria.materialrequest.b.MaterialRequestServices;
import com.volvo.gloria.util.GloriaExceptionLogger;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.jvs.runtime.platform.ContainerManaged;

@ContainerManaged
public class MaterialProcureResponseGatewayBean implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialProcureResponseGatewayBean.class);

    private static final String APPLICATION = "GLORIA";
    private static final String INTEGRATION = "ProcureResponseGateway";
    
    @Inject
    private MaterialProcureGatewayTranformer materialProcureResponseTransformer;

    @Inject
    private MaterialRequestServices materialRequestServices;
    
    @Inject 
    private CommonServices commonServices;
    
    @Override
    public void onMessage(Message msg) {

        if (msg instanceof TextMessage) {
            TextMessage receivedMaterialProcureResponse = (TextMessage) msg;
            
            String jmsMessageID = "";
            String textMessage = "";
            try {
                jmsMessageID = msg.getJMSMessageID();
                textMessage = receivedMaterialProcureResponse.getText();
                materialRequestServices.changeMaterialRequestState(materialProcureResponseTransformer.transformMaterialProcureResponse(textMessage));
            } catch (Exception e) {
                LOGGER.error("An error occured while receiving a matrerial procure Response  [ID:" + jmsMessageID + "]: " + e.getMessage());
                GloriaExceptionLogger.log(e, MaterialProcureResponseGatewayBean.class);
            } finally {
                LOGGER.info("MaterialProcureResponseGatewayBean xml=" + textMessage);
                try {
                    commonServices.logXMLMessage(new XmlLogEventDTO(APPLICATION, GloriaParams.XML_MESSAGE_LOG_RECEIVING, INTEGRATION, jmsMessageID,
                                                                    compress(textMessage)));
                } catch (Exception e) {
                    LOGGER.error("An error occured while saving a incoming Request message [ID:" + jmsMessageID + "]: " + e.getMessage(), e);
                }
            }
        } else {
            LOGGER.error("A NON TextMessage was received! message received is of class: " + msg.getClass().getName());
        }

    }
}
