package com.volvo.gloria.common.b.beans;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.UserlogServices;
import com.volvo.gloria.common.c.UserlogActionType;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.UserLog;
import com.volvo.gloria.common.repositories.b.UserLogRepository;
import com.volvo.gloria.util.DateUtil;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * service implementations for User Log.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UserlogServiceBean implements UserlogServices {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserlogServiceBean.class);

    @Inject
    private UserLogRepository userLogRepository;

    @Override
    public void addUserLog(UserDTO userDTO, UserlogActionType action, String actionDetail, String confirmationText) {
        UserLog userLog = new UserLog();
        userLog.setUserId(userDTO.getId());
        userLog.setUserName(userDTO.getUserName());
        userLog.setLoggedTime(DateUtil.getCurrentUTCDateTime());
        userLog.setUserText(confirmationText);
        userLog.setAction(action.actionName());
        userLog.setActionDetail(actionDetail);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("userlog --> " + "Action:" + userLog.getAction() + " Action Detail:" + userLog.getActionDetail() + " User:" + userLog.getUserText()
                    + " Datetime" + userLog.getLoggedTime());
        }
        userLogRepository.save(userLog);
    }
}
