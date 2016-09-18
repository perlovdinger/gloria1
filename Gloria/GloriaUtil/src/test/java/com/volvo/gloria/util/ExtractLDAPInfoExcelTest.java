package com.volvo.gloria.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * The purpose of the Class is to process an input excel csv with userids and write a new excel with info from LDAP.
 */
public class ExtractLDAPInfoExcelTest {
    @Ignore
    @Test
    public void extractLdapIntoExcel() throws IOException, NamingException {
        LdapContext ldapContext = ExtractLDAPInfo.getLdapContext();

        String csvIn = "ldap/GloriaUsers_2016_03_16.csv";
        String csvOut = "ldap/GloriaUsers_2016_03_16_out.csv";

        InputStream in = ExtractLDAPInfo.class.getClassLoader().getResourceAsStream(csvIn);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));

        URL csvUrl = ExtractLDAPInfo.class.getClassLoader().getResource(csvOut);
        BufferedWriter csvWriter = new BufferedWriter(new FileWriter(csvUrl.getFile()));

        URL errorsUrl = ExtractLDAPInfo.class.getClassLoader().getResource("ldap/errors.txt");
        BufferedWriter errorsWriter = new BufferedWriter(new FileWriter(errorsUrl.getFile()));
        String line = null;
        // Act
        while ((line = br.readLine()) != null) {
            String[] splittedLine = line.split(";");
            String userId = splittedLine[0];
            String csvRow = ExtractLDAPInfo.createCsvRow(ExtractLDAPInfo.getUserAttributes(userId, ldapContext), userId);
            if (StringUtils.isNotBlank(csvRow)) {
                csvWriter.write(csvRow);
                csvWriter.newLine();
            } else {
                errorsWriter.write(userId);
                errorsWriter.newLine();
            }
        }

        csvWriter.close();
        errorsWriter.close();

    }
}
