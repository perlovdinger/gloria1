package com.volvo.gloria.web.w;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.config.b.beans.ApplicationProperties;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Utility class for displaying System properties.
 */
public class CheckEnvironment {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckEnvironment.class);

    public void checkAll() {
        String env = ServiceLocator.getService(ApplicationProperties.class).getEnvironment();
        writeLog("CheckEnvironment Start ********************************************************************************************************************");

        writeLog("env=" + env);
        displayTime();
        writeLog("********************************************************************************************************************************");
        displayApplicationProperties();
        writeLog("********************************************************************************************************************************");
        displaySysProperties();
        displaySeparators();
        writeLog("CheckEnvironment End  *******************************************************************************************************************");
        
    }

    public void displayTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String loginTime = sdf.format(cal.getTime());
        writeLog("Time=" + loginTime);
    }

    public void displayApplicationProperties() {
        ApplicationProperties applicationProperties = ServiceLocator.getService(ApplicationProperties.class);
        writeLog("CheckEnvironment: applicationProperties" + applicationProperties);
    }

    public void displaySysProperties() {
        LOGGER.info("CheckEnvironment:displaySysProperties");
        Properties prop = System.getProperties();
        Enumeration<?> propNames = prop.propertyNames();
        while (propNames.hasMoreElements()) {
            String propName = (String) propNames.nextElement();
            String propValue = System.getProperty(propName);
            writeLog(propName + " Value = " + propValue);
        }
    }

    public void displayLocales() {
        writeLog("CheckEnvironment:displayLocales");
        Locale locDefault = Locale.getDefault();
        writeLog("locale default country=" + locDefault.getCountry() + " language=" + locDefault.getLanguage());
        Locale[] locs = Locale.getAvailableLocales();
        for (int i = 0; i < locs.length; i++) {
            writeLog("loc=" + locs[i] + " country=" + locs[i].getCountry());
        }

    }

    public void displaySeparators() {
        writeLog("CheckEnvironment:displaySeparators");
        String prop = "";
        String propName = "path.separator";
        prop = System.getProperty(propName);
        writeLog("System:" + propName + " Value = " + prop);

        propName = "line.separator";
        prop = System.getProperty(propName);
        writeLog("System:" + propName + " Value = " + prop);

        propName = "file.separator";
        prop = System.getProperty(propName);
        writeLog("System:" + propName + " Value = " + prop);

        propName = "File.pathSeparator";
        prop = File.pathSeparator;
        writeLog("File:" + propName + " Value = " + prop);

        propName = "File.separator";
        prop = File.separator;
        writeLog("File:" + propName + " Value = " + prop);
        
    }

    private void writeLog(String string) {
        LOGGER.info("lg:" + string);

    }
    public static void main(String[] args) {
        CheckEnvironment test = new CheckEnvironment();
        test.checkAll();
    }
}
