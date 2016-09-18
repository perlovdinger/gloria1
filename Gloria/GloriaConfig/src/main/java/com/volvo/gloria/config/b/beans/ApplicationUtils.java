package com.volvo.gloria.config.b.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

public abstract class ApplicationUtils {

    private ApplicationUtils() {

    }
    
    public static PublicConfigurationDTO getPublicConfiguration() {
        ApplicationProperties applicationProperties = ServiceLocator.getService(ApplicationProperties.class);
        PublicConfigurationDTO publicConfigurationDTO = new PublicConfigurationDTO();
        publicConfigurationDTO.setLoginMethod(applicationProperties.getLoginProcess());
        return publicConfigurationDTO;
    }
    
    public static  List<String> getCompanyCodesForEnv(String env) {
        ApplicationProperties applicationProperties = ServiceLocator.getService(ApplicationProperties.class);
        String companyCodes = applicationProperties.getCompanyCodes();
        return new ArrayList<String>(Arrays.asList(companyCodes.split(",")));
    }

    public static String[] getSitesToMigrate(String env) {
        ApplicationProperties applicationProperties = ServiceLocator.getService(ApplicationProperties.class);
        String sitesToMigrate = applicationProperties.getSitesToMigrate();
        List<String> siteList = new ArrayList<String>(Arrays.asList(sitesToMigrate.split(",")));
        return siteList.toArray(new String[siteList.size()]);
    }
  
  
    public static String[] loadFileContentsFromClasspath(String directoryPath, String fileType) throws IOException {
        ApplicationProperties applicationProperties = ServiceLocator.getService(ApplicationProperties.class);
        String database = applicationProperties.getEnvironment();
        String location = "initDB/" + database + "/data";
        return ConfigUtils.loadFileContentsFromClasspath(directoryPath, fileType, location);
    }

    public static String[] getFileContents(Properties testDataProperties, String properyKey, String classPath, String fileType) throws IOException {
        if (testDataProperties.containsKey(properyKey)) {
            return ConfigUtils.loadFileContentsFromFileSystem(testDataProperties.getProperty(properyKey), fileType);
        } else {
            return loadFileContentsFromClasspath(classPath, fileType);
        }
    }

}
