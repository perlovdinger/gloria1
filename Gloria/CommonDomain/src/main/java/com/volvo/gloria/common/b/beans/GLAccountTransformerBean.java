package com.volvo.gloria.common.b.beans;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.GLAccountTransformer;
import com.volvo.gloria.common.c.dto.GlAccountDTO;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.group.init.glAccount._1_0.GlAccounts;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Service Implmentations for GLAccount. 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class GLAccountTransformerBean extends XmlTransformer implements GLAccountTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GLAccountTransformerBean.class);

    /**
     * 
     */
    public GLAccountTransformerBean() {
        super(XmlConstants.SchemaFullPath.INIT_GLACCOUNT, XmlConstants.PackageName.INIT_GLACCOUNT);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<GlAccountDTO> transformGLAccount(String receivedGLAccountMessage) {
        try {
            return (List<GlAccountDTO>) transform(receivedGLAccountMessage);
        } catch (JAXBException e) {
            // Throw an exception to roll-back the transaction.
            LOGGER.error("Unable to unmarshall the received message to a GlAccountDTOs object, message will be discarded. Exception is:", e);
            throw new GloriaSystemException(e, "Unable to unmarshall the received message to a GlAccountDTOs object, message will be discarded");
        }
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {

        GlAccounts glAccs = (GlAccounts) jaxbOject;

        List<GlAccountDTO> glAccountDTOs = new ArrayList<GlAccountDTO>();
        List<com.volvo.group.init.glAccount._1_0.GlAccount> glAccounts = glAccs.getGlAccount();
        for (com.volvo.group.init.glAccount._1_0.GlAccount glAccount : glAccounts) {
            GlAccountDTO glAccountDTO = new GlAccountDTO();
            glAccountDTO.setAccountNumber(glAccount.getAccountNumber());
            glAccountDTO.setCompanyCode(glAccount.getCompanyCode());
            glAccountDTO.setAccountName(glAccount.getAccountName());
            glAccountDTO.setAccountDescription(glAccount.getAcountDescription());
            glAccountDTOs.add(glAccountDTO);
        }
        return glAccountDTOs;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        // TODO Auto-generated method stub
        return null;
    }

}
