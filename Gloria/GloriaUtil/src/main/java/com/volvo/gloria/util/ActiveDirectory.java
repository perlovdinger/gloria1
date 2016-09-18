package com.volvo.gloria.util;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import com.volvo.gloria.util.b.beans.UtilServicesHelper;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.c.dto.LDAPUserDTO;

/**
 * Implements JMS related services.
 */
public abstract class ActiveDirectory  {
    
    private static String[] returningAttributes = { "cn", "sn", "givenname", "postalAddress", "postalCode", "l", "c", "telephoneNumber", "mail", "title",
            "preferredLanguage", "department", "volvoCostCentre" };
    
    private ActiveDirectory() { 
        
    } 

    public static LDAPUserDTO getLDAPUserData(String userId) throws GloriaApplicationException {
        LDAPUserDTO ldapUserDTO = new LDAPUserDTO();
        NamingEnumeration<SearchResult> searchResults = null;
        SearchControls constraints = new SearchControls();
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        constraints.setReturningAttributes(returningAttributes);
        LdapContext ldapContext = null;
        try {
            ldapContext = UtilServicesHelper.getLdapContext();
            searchResults = ldapContext.search("DC=vcn,DC=ds,DC=volvo,DC=net", "cn=" + userId, constraints);
            ldapContext.close();
        } catch (NamingException e) {            
            GloriaExceptionLogger.log(e, ActiveDirectory.class);
            throw new GloriaApplicationException(GloriaExceptionConstants.USER_NOT_FOUND_IN_AD, "Naming error.");
        }

        try {
            if (searchResults.hasMore()) {
                Attributes attributes = ((SearchResult) searchResults.next()).getAttributes();
                ldapUserDTO.setUserId(userId);
                Attribute mailAttribute = attributes.get("mail");
                if (mailAttribute != null) {
                    ldapUserDTO.setEmail(mailAttribute.get().toString());
                }
                Attribute givenNameAttribute = attributes.get("givenName");
                Attribute snAttribute = attributes.get("sn");
                if (givenNameAttribute != null && snAttribute != null) {
                    ldapUserDTO.setUserName(givenNameAttribute.get().toString() + " " + snAttribute.get().toString());
                }
                Attribute departmentAttribute = attributes.get("department");
                if (departmentAttribute != null) {
                    ldapUserDTO.setDepartment(departmentAttribute.get().toString());
                }
                Attribute volvoCostCentre = attributes.get("volvoCostCentre");
                if (volvoCostCentre != null) {
                    ldapUserDTO.setVolvoCostCentre(volvoCostCentre.get().toString());
                }
            } else {
                throw new GloriaApplicationException(GloriaExceptionConstants.USER_NOT_FOUND_IN_AD, "User not found.");
            }
        } catch (NamingException e) {
            GloriaExceptionLogger.log(e, ActiveDirectory.class);
            throw new GloriaApplicationException(GloriaExceptionConstants.USER_NOT_FOUND_IN_AD, "Naming error.");
        }

        return ldapUserDTO;
    }

}
