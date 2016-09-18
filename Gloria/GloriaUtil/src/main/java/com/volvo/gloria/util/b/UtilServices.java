package com.volvo.gloria.util.b;

import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.dto.LDAPUserDTO;

/**
 * Utility services JMS, LDAP.
 */
public interface UtilServices {
    void sendMessage(final String message, String configurationURI);

    LDAPUserDTO getLDAPUserData(String userId) throws GloriaApplicationException;
}
