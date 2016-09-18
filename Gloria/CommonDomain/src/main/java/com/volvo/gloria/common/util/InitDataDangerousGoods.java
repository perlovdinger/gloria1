package com.volvo.gloria.common.util;

import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.group.init.dangerousGoods._1_0.DangerousGood;
import com.volvo.group.init.dangerousGoods._1_0.DangerousGoods;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Init data for DangerousGoods.
 */
public class InitDataDangerousGoods extends InitDataBase {

    private static final String FILE_NAME = "DangerousGoods.xml";
    private static final String FILE_TYPE = "common";
    private static final String DANGEROUS_GOODS_DATA_PROPERTY_KEY = "dangerousGoodsData.data";
    private String message;

    public InitDataDangerousGoods(String xmlFilePath) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_DANGEROUS_GOODS, XmlConstants.PackageName.INIT_DANGEROUS_GOODS);
        message = IOUtil.getStringFromClasspath(xmlFilePath);
    }

    public InitDataDangerousGoods(Properties testDataProperties, String env) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_DANGEROUS_GOODS, XmlConstants.PackageName.INIT_DANGEROUS_GOODS);
        if (testDataProperties.containsKey(DANGEROUS_GOODS_DATA_PROPERTY_KEY)) {
            message = IOUtil.getStringFromFileSystem(testDataProperties.getProperty(DANGEROUS_GOODS_DATA_PROPERTY_KEY));
        } else {
            message = getXml(env, FILE_TYPE, FILE_NAME);

        }
    }

    public void initDangerousGoods() throws JAXBException, IOException {

        Object object = transformXmlToJaxb(message);
        DangerousGoods dangerousGoods = (DangerousGoods) object;

        CommonServices service = ServiceLocator.getService(CommonServices.class);
        for (DangerousGood dangerousGood : dangerousGoods.getDangerousGood()) {
            com.volvo.gloria.common.d.entities.DangerousGoods dangerousGoodsEntity = new com.volvo.gloria.common.d.entities.DangerousGoods();
            dangerousGoodsEntity.setName(dangerousGood.getName());
            dangerousGoodsEntity.setFlag(dangerousGood.isFlag());
            dangerousGoodsEntity.setDisplaySeq(dangerousGood.getDisplaySeq());
            service.addDangerousGoods(dangerousGoodsEntity);
        }

    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        return null;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        return null;
    }
}
