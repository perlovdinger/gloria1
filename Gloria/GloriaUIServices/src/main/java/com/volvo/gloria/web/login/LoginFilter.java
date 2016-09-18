package com.volvo.gloria.web.login;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.b.SecurityServices;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * filter implementation for login requests.
 * 
 */
public class LoginFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginFilter.class);
    
    public static final int RESPONSE_STATUS_NUMBER = 401;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        SecurityContext context = (SecurityContext) httpServletRequest.getSession()
                                                                      .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        if (context == null) {
            String userId = null;
            if (httpServletRequest.getCookies() != null) {
                for (Cookie cookie : httpServletRequest.getCookies()) {
                    if ("PS-UID".equals(cookie.getName())) {
                        userId = cookie.getValue();
                        break;
                    }
                }
            }
            if (userId == null && httpServletRequest.getUserPrincipal() != null) {
                userId = httpServletRequest.getUserPrincipal().getName();
            }
            if (userId != null) {
                try {
                    performAuthorization(userId, httpServletRequest);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Managed to log in user '" + userId + "'");
                    }
                } catch (GloriaApplicationException e) {
                    httpServletResponse.setStatus(RESPONSE_STATUS_NUMBER);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Failed to log in user '" + userId + "'");
                    }
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    private void performAuthorization(String userId, HttpServletRequest httpServletRequest) throws GloriaApplicationException {
        UserServices userServices = ServiceLocator.getService(UserServices.class);
        SecurityServices securityServices = ServiceLocator.getService(SecurityServices.class);
        securityServices.setAuthentication(userId, userServices.getUserRoleIds(userId), httpServletRequest);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

}
