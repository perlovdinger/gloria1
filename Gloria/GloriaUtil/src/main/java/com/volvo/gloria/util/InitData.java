package com.volvo.gloria.util;

import java.io.IOException;
import java.util.Properties;

/**
 * This base class extracts test data XML from either the file system or some classpath.
 */
public abstract class InitData {
    
    protected InitData() { 
        
    }

    protected static String getMessage(Properties testDataProperties, String testDataProperyKey, String testDataClassPath) throws IOException {
        String message = null;
        if (testDataProperties.containsKey(testDataProperyKey)) {
            message = IOUtil.getStringFromFileSystem(testDataProperties.getProperty(testDataProperyKey));
        } else {
            message = IOUtil.getStringFromClasspath(testDataClassPath);
        }
        return message;
    }
}
