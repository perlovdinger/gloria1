/**
 * 
 */
package com.volvo.gloria.common.util;

import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.group.init.qualitydocument._1_0.QualityDocument;
import com.volvo.group.init.qualitydocument._1_0.QualityDocuments;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

public class InitDataQualityDocument extends InitDataBase {

    private static final String FILE_NAME = "QualityDocument.xml";
    private static final String FILE_TYPE = "common";
    private String message;

    public InitDataQualityDocument(Properties properties, String env) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_QUALITY_DOCUMENT, XmlConstants.PackageName.INIT_QUALITY_DOCUMENT);
        message = getXml(env, FILE_TYPE, FILE_NAME);
    }

    public void initQualityDocument() throws JAXBException, IOException {
        Object object = transformXmlToJaxb(message);
        QualityDocuments qualityDocuments = (QualityDocuments) object;
        CommonServices service = ServiceLocator.getService(CommonServices.class);
        for (QualityDocument qualityDocument : qualityDocuments.getQualityDocument()) {
            com.volvo.gloria.common.d.entities.QualityDocument qualityDocumentEntity = new com.volvo.gloria.common.d.entities.QualityDocument();
            qualityDocumentEntity.setCode(qualityDocument.getCode());
            qualityDocumentEntity.setName(qualityDocument.getName());
            qualityDocumentEntity.setDisplaySeq(qualityDocument.getDisplaySeq());
            service.addQualityDocument(qualityDocumentEntity);
        }
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
