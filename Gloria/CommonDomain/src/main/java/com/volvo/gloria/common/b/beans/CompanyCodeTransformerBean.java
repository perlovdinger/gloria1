package com.volvo.gloria.common.b.beans;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.CompanyCodeTransformer;
import com.volvo.gloria.common.companycode.c.dto.CompanyCodeDTO;
import com.volvo.gloria.common.companycode.c.dto.SyncCompanyCodeDTO;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.group.init.companyCode._1_0.CompanyCodes;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * CompanyCode message transformer service implementation.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class CompanyCodeTransformerBean extends XmlTransformer implements CompanyCodeTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyCodeTransformerBean.class);

    public CompanyCodeTransformerBean() {
        super(XmlConstants.SchemaFullPath.INIT_COMPANYCODE, XmlConstants.PackageName.INIT_COMPANYCODE);
    }

    /**
     * Perform transformation from external information model to the application internal model.
     * 
     * @param message
     *            the Message to transform
     * @return a CompanyCodeDTO object
     */
    @Override
    public SyncCompanyCodeDTO transformCompanyCode(String message) {
        try {
            return (SyncCompanyCodeDTO) transform(message);
        } catch (JAXBException e) {
            LOGGER.error("Unable to unmarshall the received message to a CompanyCodeDTO object, message will be discarded. Exception is:", e);
            throw new GloriaSystemException(e, "Unable to unmarshall the received message to a CompanyCodeDTO object, message will be discarded");
        }
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        CompanyCodes companyCodes = (CompanyCodes) jaxbOject;
        SyncCompanyCodeDTO syncCompanyCodeDTO = new SyncCompanyCodeDTO();
        List<CompanyCodeDTO> companyCodeDTOs = new ArrayList<CompanyCodeDTO>();

        for (com.volvo.group.init.companyCode._1_0.CompanyCode companyCode : companyCodes.getCompanyCode()) {
            CompanyCodeDTO companyCodeDto = new CompanyCodeDTO();
            companyCodeDto.setCode(companyCode.getCode());
            companyCodeDto.setName(companyCode.getName());
            companyCodeDto.setDefaultCurrency(companyCode.getDefaultCurrency());
            companyCodeDto.setSapPurchaseOrg(companyCode.getSapPurchaseOrg());
            companyCodeDto.setSapQuantityBlockReceiverId(companyCode.getSapQuantityBlockReceiverId());
            companyCodeDto.setReceivingGoods(companyCode.isReceivingGoods());
            companyCodeDto.setSendGRtoSAP(companyCode.isSendGRtoSAP());
            companyCodeDto.setSendPOtoSAP(companyCode.isSendPOtoSAP());
            companyCodeDto.setCompanyGroupCode(companyCode.getCompanyCodeGroup());            
            companyCodeDTOs.add(companyCodeDto);
        }
        syncCompanyCodeDTO.setCompanyCodeDTO(companyCodeDTOs);
        return syncCompanyCodeDTO;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        // TODO Auto-generated method stub
        return null;
    }

}
