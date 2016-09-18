package com.volvo.gloria.util.b;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Interface exposing security services.
 */
public interface SecurityServices {
    void authenticateUser(String name);

    void setAuthentication(String userId, List<String> gloriaRoles, HttpServletRequest request);

    String getLoggedInUserId(HttpServletRequest request) throws GloriaApplicationException;

    void logout(HttpServletRequest request);
}
