package com.volvo.gloria.common.b;

import com.volvo.gloria.common.c.UserlogActionType;
import com.volvo.gloria.common.c.dto.UserDTO;

/**
 * interface for User Log.
 * 
 */
public interface UserlogServices {

    void addUserLog(UserDTO userDTO, UserlogActionType action, String actionDetail, String confirmationText);
}
