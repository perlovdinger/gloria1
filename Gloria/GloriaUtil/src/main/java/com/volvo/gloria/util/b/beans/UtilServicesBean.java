package com.volvo.gloria.util.b.beans;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.springframework.security.access.prepost.PreAuthorize;

import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaExceptionLogger;
import com.volvo.gloria.util.b.UtilServices;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.c.dto.LDAPUserDTO;
import com.volvo.gloria.util.jms.JMSMessageSender;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Implements JMS related services.
 */
@ContainerManaged(name = "UtilServices")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UtilServicesBean implements UtilServices {
    private static String[] returningAttributes = { "cn", "sn", "givenname", "postalAddress", "postalCode", "l", "c", "telephoneNumber", "mail", "title",
            "preferredLanguage", "department", "volvoCostCentre" };

    @Override
    @PreAuthorize("hasAnyRole('IT_SUPPORT')")
    public void sendMessage(String message, String configurationURI) {
        JMSMessageSender.send(message, configurationURI);
    }

    @Override
    public LDAPUserDTO getLDAPUserData(String userId) throws GloriaApplicationException {
        LDAPUserDTO ldapUserDTO = new LDAPUserDTO();
        NamingEnumeration<SearchResult> searchResults = null;
        SearchControls constraints = new SearchControls();
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        constraints.setReturningAttributes(returningAttributes);
        try {
            searchResults = UtilServicesHelper.getLdapContext().search("DC=vcn,DC=ds,DC=volvo,DC=net", "cn=" + userId, constraints);
        } catch (NamingException e) {
            GloriaExceptionLogger.log(e, UtilServicesBean.class);
            throw new GloriaApplicationException(GloriaExceptionConstants.USER_NOT_FOUND_IN_AD, "Naming error.");
        }
        try {
            if (searchResults.hasMore()) {
                Attributes attributes = ((SearchResult) searchResults.next()).getAttributes();
                ldapUserDTO.setUserId(userId);
                ldapUserDTO.setEmail(attributes.get("mail").get().toString());
                ldapUserDTO.setUserName(attributes.get("givenName").get().toString() + " " + attributes.get("sn").get().toString());
                ldapUserDTO.setDepartment(attributes.get("department").get().toString());
                ldapUserDTO.setVolvoCostCentre(attributes.get("volvoCostCentre").get().toString());
            } else {
                throw new GloriaApplicationException(GloriaExceptionConstants.USER_NOT_FOUND_IN_AD, "User not found.");
            }
        } catch (NamingException e) {
            GloriaExceptionLogger.log(e, UtilServicesBean.class);
            throw new GloriaApplicationException(GloriaExceptionConstants.USER_NOT_FOUND_IN_AD, "Naming error.");
        }

        return ldapUserDTO;
    }

}
