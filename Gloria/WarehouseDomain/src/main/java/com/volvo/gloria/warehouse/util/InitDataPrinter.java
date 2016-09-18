package com.volvo.gloria.warehouse.util;

import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import com.volvo.gloria.common.util.InitDataBase;
import com.volvo.gloria.config.b.beans.ApplicationUtils;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * 
 */
public class InitDataPrinter extends InitDataBase {
    private static final String FILE_NAME = "Printer/Printer.xml";
    private static final String FILE_TYPE = "Warehouse";
    private String message;

    private static final String NONOPERATIVE_PRINTER = "/nonOperative/Printer/";
    private static final String PRINTER_DATA_PROPERTY_KEY = "nonOperative.printer";

    public InitDataPrinter(String xmlFilePath) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_PRINTER, XmlConstants.PackageName.INIT_PRINTER);
        message = IOUtil.getStringFromClasspath(xmlFilePath);
    }

    public InitDataPrinter(Properties testDataProperties, String env, String fileName) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_PRINTER, XmlConstants.PackageName.INIT_PRINTER);
        if (fileName == null) {
            fileName = FILE_NAME;
        }
        if (testDataProperties.containsKey(PRINTER_DATA_PROPERTY_KEY)) {
            message = IOUtil.getStringFromFileSystem(testDataProperties.getProperty(PRINTER_DATA_PROPERTY_KEY));
        } else {
            message = getXml(env, FILE_TYPE, fileName);
        }
    }

    public void initPrinter() throws JAXBException, IOException {
        WarehouseServices warehouseServices = ServiceLocator.getService(WarehouseServices.class);

        warehouseServices.createPrinterData(message);
      
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

    public static void loadPrinter(Properties testDataProperties) throws JAXBException, IOException {
        WarehouseServices warehouseServices = ServiceLocator.getService(WarehouseServices.class);
        for (String aFileContent : ApplicationUtils.getFileContents(testDataProperties, PRINTER_DATA_PROPERTY_KEY, 
                                                                    NONOPERATIVE_PRINTER, IOUtil.FILE_TYPE_XML)) {
            warehouseServices.createPrinterData(aFileContent);
        }
    }
}
