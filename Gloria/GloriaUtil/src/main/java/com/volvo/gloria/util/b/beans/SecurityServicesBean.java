package com.volvo.gloria.util.b.beans;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.b.SecurityServices;
import com.volvo.gloria.util.c.GloriaExceptionConstants;

/**
 * This class supports Spring Security.
 */
@Component
public class SecurityServicesBean implements SecurityServices {
    @Autowired
    private UserDetailsManager userDetailsManager;

    @Override
    public void authenticateUser(String name) {
        loadUserByUsername(name);
    }

    public UserDetails loadUserByUsername(String username) {
        UserDetails userDetails = userDetailsManager.loadUserByUsername(username);
        Authentication userAuthentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(),
                                                                                    userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(userAuthentication);
        return userDetails;
    }

    @Override
    public void setAuthentication(String userId, List<String> gloriaRoles, HttpServletRequest request) {
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<SimpleGrantedAuthority>();
        for (String gloriaRole : gloriaRoles) {
            simpleGrantedAuthorities.add(new SimpleGrantedAuthority(gloriaRole));
        }

        Authentication userAuthentication = new UsernamePasswordAuthenticationToken(userId.toUpperCase(), "", simpleGrantedAuthorities);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(userAuthentication);
        if (request != null) {
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
        }
    }

    @Override
    public String getLoggedInUserId(HttpServletRequest request) throws GloriaApplicationException {
        String loggedInUserId = null;
        SecurityContext context = null;
        if (request == null) {
            context = SecurityContextHolder.getContext();
        } else {
            context = (SecurityContext) request.getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        }
        if (context != null) {
            Authentication authentication = context.getAuthentication();
            if (authentication == null) {
                return null;
            }
            Object principal = authentication.getPrincipal();
            if (principal == null) {
                throw new GloriaSystemException("No LoggedIn user", GloriaExceptionConstants.NO_LOGGEDIN_USER);
            }
            if (User.class.isInstance(principal)) {
                User user = (User) principal;
                loggedInUserId = user.getUsername();
            } else if (String.class.isInstance(principal)) {
                loggedInUserId = (String) principal;
            }
        }
        return loggedInUserId;
    }
    
    @Override
    public void logout(HttpServletRequest request) {
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, null);
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
    }
}
