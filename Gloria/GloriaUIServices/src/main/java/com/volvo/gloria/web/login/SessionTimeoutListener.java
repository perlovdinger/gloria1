package com.volvo.gloria.web.login;

import java.util.List;

import javax.inject.Inject;

import org.springframework.context.ApplicationListener;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.c.GloriaExceptionConstants;

/**
 * This listener throws AccessDeniedException when timeout happens.
 * org.springframework.security.web.session.HttpSessionEventPublisher has
 * to be configured in web.xml. 
 */
@Service
public class SessionTimeoutListener implements ApplicationListener<SessionDestroyedEvent> {
    
    @Inject
    private MaterialServices materialServices;
    
    @Override
    public void onApplicationEvent(SessionDestroyedEvent event) {
        List<SecurityContext> lstSecurityContext = event.getSecurityContexts();
        if (lstSecurityContext != null && !lstSecurityContext.isEmpty()) {
            String userId = getUserId(lstSecurityContext);
            materialServices.unlockMaterialLine(userId);
            materialServices.releasePickList(userId);
            throw new AccessDeniedException("Session timeout.");
        }
    }

    public String getUserId(List<SecurityContext> lstSecurityContext) {
        String userId = null;
        Authentication authentication =  lstSecurityContext.get(0).getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal == null) {
            throw new GloriaSystemException("No LoggedIn user", GloriaExceptionConstants.NO_LOGGEDIN_USER);
        }
        if (User.class.isInstance(principal)) {
            User user = (User) principal;
            userId = user.getUsername();
        } else if (String.class.isInstance(principal)) {
            userId = (String) principal;
        }
        return userId;
    }

}
