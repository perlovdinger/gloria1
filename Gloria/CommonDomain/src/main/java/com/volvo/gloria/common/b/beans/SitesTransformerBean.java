package com.volvo.gloria.common.b.beans;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.SitesTransformer;
import com.volvo.gloria.common.c.dto.SiteDTO;
import com.volvo.gloria.common.c.dto.SiteDTOs;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.group.init.site._1_0.Sites;
import com.volvo.group.init.site._1_0.Sites.Site;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * SitesTranformer Implementation.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SitesTransformerBean extends XmlTransformer implements SitesTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SitesTransformerBean.class);

    public SitesTransformerBean() {
        super(XmlConstants.SchemaFullPath.INIT_SITE, XmlConstants.PackageName.INIT_SITE);
    }

    @Override
    public SiteDTOs transformSites(String receivedSitesMessage) {
        try {
            return (SiteDTOs) transform(receivedSitesMessage);
        } catch (JAXBException e) {
            // Throw an exception to roll-back the transaction.
            LOGGER.error("Unable to unmarshall the received message to a SitesDTO object, message will be discarded. Exception is:", e);
            throw new GloriaSystemException(e, "Unable to unmarshall the received message to a SitesDTO object, message will be discarded");
        }
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {

        SiteDTOs siteDTOs = new SiteDTOs();

        Sites sites = (Sites) jaxbOject;
        for (Site site : sites.getSite()) {
            SiteDTO siteDTO = new SiteDTO();
            siteDTO.setSiteId(site.getSiteId());
            siteDTO.setSiteCode(site.getSiteCode());
            siteDTO.setSiteName(site.getSiteName());
            siteDTO.setAddress(site.getAddress());
            siteDTO.setPhone(site.getPhone());
            siteDTO.setCountryCode(site.getCountryCode());
            siteDTO.setCompanyCode(site.getCompanyCode());
            siteDTO.setJointVenture(site.isJoinVenture());
            siteDTO.setShipToSite(site.isShipToSite());
            siteDTO.setShipToType(site.getShipToType());
            siteDTO.setBuildSite(site.isBuildSite());
            siteDTO.setBuildSiteType(site.getBuildSiteType());            
            siteDTOs.getListOfSiteDTO().add(siteDTO);
        }

        return siteDTOs;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        return null;
    }

}
