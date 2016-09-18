package com.volvo.gloria.common.c.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Site Data Transfer Object for exchanging data from Jaxb.
 */
public class SiteDTOs {

    private List<SiteDTO> listOfSiteDTO = new ArrayList<SiteDTO>();

    public List<SiteDTO> getListOfSiteDTO() {
        return listOfSiteDTO;
    }

    public void setListOfSiteDTO(List<SiteDTO> listOfSiteDTO) {
        this.listOfSiteDTO = listOfSiteDTO;
    }

}
