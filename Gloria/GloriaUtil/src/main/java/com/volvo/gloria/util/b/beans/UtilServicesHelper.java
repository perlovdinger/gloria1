package com.volvo.gloria.util.b.beans;

import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for security services.
 */
public abstract class UtilServicesHelper {
    private static final String SECURITY_CREDENTIAL = "J-97&%8ILed9Jb-";
    private static final String PROVIDER_URL = "ldap://vcn.ds.volvo.net:389";
    private static final String SECURITY_PRINCIPAL = "CN=cs-ws-s-d2_gloria,OU=Service Accounts,OU=WINSRV,OU=CS,DC=vcn,DC=ds,DC=volvo,DC=net";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilServicesHelper.class);
    
    private UtilServicesHelper() { 
        
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
        } catch (NamingException nex) {
            LOGGER.warn(nex.getMessage(), nex);
        }
        return ctx;
    }

}
