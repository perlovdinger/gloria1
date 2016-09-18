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
package com.volvo.gloria.gateway.b.beans;

import static com.volvo.gloria.util.Utils.compress;

import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.authorization.b.UserAuthorizationServices;
import com.volvo.gloria.authorization.b.UserTransformer;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.XmlLogEventDTO;
import com.volvo.gloria.util.GloriaExceptionLogger;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * This is the service activator for the User Storage Interface, which receives updates on users.
 */
@ContainerManaged
public class UserGatewayBean implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserGatewayBean.class);
    
    private static final String APPLICATION = "BALDO";
    private static final String INTEGRATION = "UserGateway";

    @Inject
    private UserAuthorizationServices userAuthorizationServices;

    @Inject
    private UserTransformer userTransformer;

    @Inject
    private CommonServices commonServices;

    public void setUserAuthorizationServices(UserAuthorizationServices userAuthorizationServices) {
        this.userAuthorizationServices = userAuthorizationServices;
    }

    /**
     * {@inheritDoc}
     */
    public void onMessage(Message msg) {
        if (msg instanceof TextMessage) {
            TextMessage receivedUserMessage = (TextMessage) msg;
            String jmsMessageID = "";
            String textMessage = "";
            try {
                jmsMessageID = msg.getJMSMessageID();
                textMessage = receivedUserMessage.getText();
                userAuthorizationServices.manageUsers(userTransformer.transformStoredUser(textMessage));
            } catch (Exception e) {
                LOGGER.error("An error occured while receiving a User message [ID:" + jmsMessageID + "]: " + e.getMessage());
                GloriaExceptionLogger.log(e, UserGatewayBean.class);
            } finally {
                LOGGER.info("UserGatewayBean xml=" + textMessage);
                try {
                    commonServices.logXMLMessage(new XmlLogEventDTO(APPLICATION, GloriaParams.XML_MESSAGE_LOG_RECEIVING, INTEGRATION, jmsMessageID,
                                                                    compress(textMessage)));
                } catch (Exception exception) {
                    LOGGER.error("An error occured while saving a incoming User message [ID:" + jmsMessageID + "]: " + exception.getMessage(), exception);
                }
            }
        } else {
            LOGGER.error("A NON TextMessage was received! message received is of class: " + msg.getClass().getName());
        }
    }

    public void setCommonServices(CommonServices commonServices) {
        this.commonServices = commonServices;

    }

}
