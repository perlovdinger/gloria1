package com.volvo.gloria.util;

import org.junit.Test;
import org.springframework.util.Assert;

import com.volvo.gloria.util.c.dto.LDAPUserDTO;

public class ActiveDirectoryTest {

    @Test
    public void testRetreivalOfEmail() throws GloriaApplicationException {
        LDAPUserDTO ldapUserDTO = ActiveDirectory.getLDAPUserData("a028173");
        Assert.notNull(ldapUserDTO);
        if (ldapUserDTO != null) {
            Assert.notNull(ldapUserDTO.getEmail());
        }
    }
    @Test
    public void testRetreivalOfEmailWithUpperCase() throws GloriaApplicationException {
        LDAPUserDTO ldapUserDTO = ActiveDirectory.getLDAPUserData("A028173");
        Assert.notNull(ldapUserDTO);
        if (ldapUserDTO != null) {
            Assert.notNull(ldapUserDTO.getEmail());
        }
    }
}
