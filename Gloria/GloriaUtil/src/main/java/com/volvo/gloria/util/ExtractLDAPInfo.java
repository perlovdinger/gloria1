package com.volvo.gloria.util;

import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extracts some information from Active Directory.
 */
public final class ExtractLDAPInfo {
    private static final String SECURITY_CREDENTIAL = "J-97&%8ILed9Jb-";
    private static final String PROVIDER_URL = "ldap://vcn.ds.volvo.net:389";
    private static final String SECURITY_PRINCIPAL = "CN=cs-ws-s-d2_gloria,OU=Service Accounts,OU=WINSRV,OU=CS,DC=vcn,DC=ds,DC=volvo,DC=net";
    private static String[] returningAttributes = { "cn", "sn", "givenname", "postalAddress", "postalCode", "l", "c", "telephoneNumber", "mail", "title",
            "preferredLanguage", "department", "volvoCostCentre" };
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractLDAPInfo.class);

    private ExtractLDAPInfo() {         
    }
    
    public static void main(String[] args) {
        listUser("V072974");
    }

    public static void listUser(String userId) {
        LdapContext ldapContext = getLdapContext();
        try {
            LOGGER.info(createCsvRow(getUserAttributes(userId, ldapContext), userId));
        } catch (NamingException e) {
            LOGGER.error("LDAP error" + e.getMessage(), e);
        }

    }    

    public static LdapContext getLdapContext() {
        LdapContext ctx = null;
        try {
            Map<String, String> env = new Hashtable<String, String>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.SECURITY_AUTHENTICATION, "Simple");
            env.put(Context.SECURITY_PRINCIPAL, SECURITY_PRINCIPAL);
            env.put(Context.SECURITY_CREDENTIALS, SECURITY_CREDENTIAL);
            env.put(Context.PROVIDER_URL, PROVIDER_URL);
            env.put(Context.REFERRAL, "follow");
            ctx = new InitialLdapContext((Hashtable<String, String>) env, null);
            LOGGER.info("Connection successful.");
        } catch (NamingException nex) {
            LOGGER.error("LDAP connection failed.", nex);
        }
        LOGGER.info("Connected to AD: " + ctx.toString());
        return ctx;
    }

    public static NamingEnumeration<SearchResult> getUserAttributes(String username, LdapContext ctx) {
        LOGGER.info("Fetching user attributes for user: " + username);
        if (username != null) {
            username.trim();
        }
        NamingEnumeration<SearchResult> searchResults = null;
        SearchControls constraints = new SearchControls();
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        constraints.setReturningAttributes(returningAttributes);
        try {
            searchResults = ctx.search("DC=vcn,DC=ds,DC=volvo,DC=net", "cn=" + username, constraints);
        } catch (NamingException e) {
            LOGGER.error("Failed to search user attributes for user:" + username, e);
        }
        return searchResults;
    }

    public static String createCsvRow(NamingEnumeration<SearchResult> searchResult, String userId) throws NamingException {
        StringBuffer csvRow = new StringBuffer();
        if (searchResult.hasMore()) {
            Attributes attributes = ((SearchResult) searchResult.next()).getAttributes();
            for (String attribute : returningAttributes) {
                Attribute theAttribute = attributes.get(attribute);
                if (theAttribute != null) {
                    Object object = theAttribute.get();
                    String attributeValue = object.toString();
                    csvRow.append(attributeValue);

                } else {
                    csvRow.append(" ");
                }
                csvRow.append(";");
            }

        } else {
            LOGGER.info("Invalid user: " + userId);
        }
        String csvRowString = csvRow.toString();
        LOGGER.info(csvRowString);
        return csvRowString;
    }
}
