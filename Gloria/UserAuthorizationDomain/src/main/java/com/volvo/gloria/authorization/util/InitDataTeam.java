package com.volvo.gloria.authorization.util;

import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.util.InitDataBase;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Initialises data for Common Domain - Team, by loading an XML file on classpath.
 * 
 */
public class InitDataTeam extends InitDataBase {

    private static final String FILE_NAME = "Team.xml";
    private static final String FILE_TYPE = "common";
    private static final String TEAM_DATA_PROPERTY_KEY = "team.data";
    private String message;

    public InitDataTeam(String xmlFilePath) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_TEAM, XmlConstants.PackageName.INIT_TEAM);
        message = IOUtil.getStringFromClasspath(xmlFilePath);
    }

    public InitDataTeam(Properties testDataProperties, String env,String fileType) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_TEAM,  XmlConstants.PackageName.INIT_TEAM);
        if (fileType == null) {
            fileType = FILE_TYPE;
        }
        if (testDataProperties.containsKey(TEAM_DATA_PROPERTY_KEY)) {
            message = IOUtil.getStringFromFileSystem(testDataProperties.getProperty(TEAM_DATA_PROPERTY_KEY));
        } else {
            message = getXml(env, fileType, FILE_NAME);
        }
    }

    public void initTeam() throws JAXBException, IOException {
        transformXmlToJaxb(message);
        UserServices service = ServiceLocator.getService(UserServices.class);
        service.createTeamData(message);
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        // TODO Auto-generated method stub
        return null;
    }

}
