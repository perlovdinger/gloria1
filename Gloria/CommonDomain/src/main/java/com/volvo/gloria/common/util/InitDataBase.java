package com.volvo.gloria.common.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.xml.XmlTransformer;

public abstract class InitDataBase extends XmlTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitDataBase.class);
    
    public InitDataBase() {
        super();
    }

    public InitDataBase(String xmlSchemaFullPath, String packageName) {
        super(xmlSchemaFullPath, packageName);
    }

    protected String getXml(String environment, String type, String fileName) {
        String envPathName = environment + "/data/base/" + type + "/" + fileName;
        try {
            String xml = IOUtil.getStringFromClasspath(envPathName);
            if (xml != null) {
                return xml;
            } else {
                return IOUtil.getStringFromClasspath("initDB/_global/data/base/" + type + "/" + fileName);
            }
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e); 
        }
        return null;
    }

}
