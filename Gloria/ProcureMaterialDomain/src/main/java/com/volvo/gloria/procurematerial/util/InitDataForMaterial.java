package com.volvo.gloria.procurematerial.util;

import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;

/**
 * Init data for Material.
 */
public final class InitDataForMaterial extends XmlTransformer {
    private static final String MATERIAL_PROPERTY_KEY = "material.data";
    private static final String MATERIAL_DATA_CP = "testdata/material/Material.xml";

    public InitDataForMaterial(String xmlFilePath) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_MATERIAL, XmlConstants.PackageName.INIT_MATERIAL);
        IOUtil.getStringFromClasspath(xmlFilePath);
    }

    public InitDataForMaterial(Properties testDataProperties) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_MATERIAL, XmlConstants.PackageName.INIT_MATERIAL);
        if (testDataProperties.containsKey(MATERIAL_PROPERTY_KEY)) {
            IOUtil.getStringFromFileSystem(testDataProperties.getProperty(MATERIAL_PROPERTY_KEY));
        } else {
            IOUtil.getStringFromClasspath(MATERIAL_DATA_CP);
        }
    }

    public void initMaterials() throws JAXBException, IOException, GloriaApplicationException {
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
