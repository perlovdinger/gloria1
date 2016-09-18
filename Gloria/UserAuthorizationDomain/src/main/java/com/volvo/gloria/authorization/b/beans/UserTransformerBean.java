/*
 * Copyright 2013 Volvo Information Technology AB
 * 
 * Licensed under the Volvo IT Corporate Source License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *      http://java.volvo.com/licenses/CORPORATE-SOURCE-LICENSE 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.volvo.gloria.authorization.b.beans;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.authorization.b.UserTransformer;
import com.volvo.gloria.authorization.c.dto.AddressDTO;
import com.volvo.gloria.authorization.c.dto.ApplicationSettingDTO;
import com.volvo.gloria.authorization.c.dto.CategoryDTO;
import com.volvo.gloria.authorization.c.dto.UserApplicationDTO;
import com.volvo.gloria.authorization.c.dto.UserCompanyCodeDTO;
import com.volvo.gloria.authorization.c.dto.UserTransformerDTO;
import com.volvo.gloria.common.c.baldo.UserAndOrganisation;
import com.volvo.gloria.common.c.baldo.UserOrganisationType;
import com.volvo.gloria.common.c.baldo.UserOrganisationType.Address;
import com.volvo.gloria.common.c.baldo.UserOrganisationType.OrganisationApplication;
import com.volvo.gloria.common.c.baldo.UserOrganisationType.OrganisationApplication.ApplicationSetting;
import com.volvo.gloria.common.c.baldo.UserOrganisationType.OrganisationApplication.UserCategory;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * User message transformer service implementation.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UserTransformerBean extends XmlTransformer implements UserTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTransformerBean.class);

    UserTransformerBean() {
        super(XmlConstants.SchemaFullPath.USER_AND_ORGANISATION, XmlConstants.PackageName.USER_AND_ORGANISATION);
    }

    /**
     * Perform transformation from external information model to the application internal model.
     * 
     * @param receivedUserUpdateMessage
     *            the Message to transform
     * @return a UserOrganisationTypeDTO object
     */
    @SuppressWarnings("unchecked")
    public List<UserTransformerDTO> transformStoredUser(String receivedUserUpdateMessage) {
        try {
            return (List<UserTransformerDTO>) transform(receivedUserUpdateMessage);
        } catch (JAXBException e) {
            // Throw an exception to roll-back the transaction.
            LOGGER.error("Unable to unmarshall the received message to a UserOrganisationType object," + " message will be discarded. Exception is:", e);
            throw new GloriaSystemException(e, "Unable to unmarshall the received message to a UserOrganisationType object," + " message will be discarded.");
        }
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        UserAndOrganisation userAndOrganisation = (UserAndOrganisation) jaxbOject;
        List<UserTransformerDTO> userOrganisationTypeDTOs = new ArrayList<UserTransformerDTO>();

        List<Object> gmfVerbAndUserOrganisationAndUserRoleDefs = userAndOrganisation.getGMFVerbAndUserOrganisationAndUserRoleDef();
        if (gmfVerbAndUserOrganisationAndUserRoleDefs != null && !gmfVerbAndUserOrganisationAndUserRoleDefs.isEmpty()) {
            for (Object object : gmfVerbAndUserOrganisationAndUserRoleDefs) {
                if (object instanceof UserOrganisationType) {
                    UserOrganisationType userOrganisationType = (UserOrganisationType) object;
                    UserTransformerDTO userOrganisationTypeDTO = convertFromObjectToUserOrDto(userOrganisationType);
                    List<OrganisationApplication> organisationApplications = userOrganisationType.getOrganisationApplication();
                    List<UserApplicationDTO> applicationDTOs = createOrganisationApplicationDTOs(organisationApplications);
                    List<UserCompanyCodeDTO> applicationSettingsValueList = createUserCompanyCodeDTOs(applicationDTOs);
                    userOrganisationTypeDTO.setUserCompanyCodes(applicationSettingsValueList);
                    userOrganisationTypeDTO.setOrganisationApplication(applicationDTOs);
                    userOrganisationTypeDTOs.add(userOrganisationTypeDTO);
                }
            }
        }
        return userOrganisationTypeDTOs;
    }

    protected List<UserCompanyCodeDTO> createUserCompanyCodeDTOs(List<UserApplicationDTO> applicationDTOs) {
        List<UserCompanyCodeDTO> applicationSettingsValueList = new ArrayList<UserCompanyCodeDTO>();
        boolean add = false;
        for (UserApplicationDTO applicationDTO : applicationDTOs) {
            for (CategoryDTO categoryDTO : applicationDTO.getUserCategory()) {
                if ("Viewer".equals(categoryDTO.getId()) || "Viewer_Price".equals(categoryDTO.getId())) {
                    add = true;
                    break;
                }
            }
            if (add) {
                for (ApplicationSettingDTO applicationSettingsDTO : applicationDTO.getApplicationSetting()) {
                    UserCompanyCodeDTO dto = new UserCompanyCodeDTO();
                    String applicationSettingsID = applicationSettingsDTO.getApplicationSettingID();
                    if ("CCGRP".equals(applicationSettingsID)) {
                        dto.setCode(applicationSettingsDTO.getApplicationSettingValue());
                        applicationSettingsValueList.add(dto);
                    }                                
                }
            }
        }
        return applicationSettingsValueList;
    }

    private List<UserApplicationDTO> createOrganisationApplicationDTOs(List<OrganisationApplication> organisationApplications) {
        List<UserApplicationDTO> applicationDTOs = new ArrayList<UserApplicationDTO>();
        for (OrganisationApplication organisationApplication : organisationApplications) {
            UserApplicationDTO organisationApplicationDTO = new UserApplicationDTO();
            if (organisationApplication.getApplicationDescription().equals(GloriaParams.APPLICATION_ID)) {
                organisationApplicationDTO.setApplicationDescription(organisationApplication.getApplicationDescription());
                organisationApplicationDTO.setApplicationID(organisationApplication.getApplicationID());
                List<ApplicationSetting> applicationSettings = organisationApplication.getApplicationSetting();
                List<ApplicationSettingDTO> applicationSettingDTOs = new ArrayList<ApplicationSettingDTO>();
                if (applicationSettings != null && !applicationSettings.isEmpty()) {
                    for (ApplicationSetting applicationSetting : applicationSettings) {
                        ApplicationSettingDTO applicationSettingDTO = new ApplicationSettingDTO();
                        applicationSettingDTOs.add(applicationSettingDTO);
                        applicationSettingDTO.setApplicationSettingID(applicationSetting.getApplicationSettingID());
                        applicationSettingDTO.setApplicationSettingValue(applicationSetting.getApplicationSettingValue());
                    }
                }
                organisationApplicationDTO.setApplicationSetting(applicationSettingDTOs);
            }
            
            List<UserCategory> userCategories = organisationApplication.getUserCategory();
            List<CategoryDTO> userCategoryDTOs = new ArrayList<CategoryDTO>();
            if (userCategories != null && !userCategories.isEmpty()) {
                for (UserCategory userCategory : userCategories) {
                    CategoryDTO userCategoryDTO = new CategoryDTO();
                    userCategoryDTO.setId(userCategory.getUserCategoryID());
                    userCategoryDTO.setUserCategoryDescription(userCategory.getUserCategoryDescription());
                    if (userCategory.getEndTime() != null) {
                        userCategoryDTO.setEndTime(userCategory.getEndTime().toGregorianCalendar().getTime());
                    }
                    userCategoryDTOs.add(userCategoryDTO);
                }
            }
            organisationApplicationDTO.setUserCategory(userCategoryDTOs);
            applicationDTOs.add(organisationApplicationDTO);
        }
        return applicationDTOs;
    }

    private UserTransformerDTO convertFromObjectToUserOrDto(UserOrganisationType userOrganisationType) {
        UserTransformerDTO userOrganisationTypeDTO = new UserTransformerDTO();
        userOrganisationTypeDTO.setCiAction(userOrganisationType.getCIAction());
        userOrganisationTypeDTO.setUserID(userOrganisationType.getUserID());
        userOrganisationTypeDTO.setUserFirstName(userOrganisationType.getUserFirstName());
        userOrganisationTypeDTO.setUserLastName(userOrganisationType.getUserLastName());
                
        List<Address> addressList = userOrganisationType.getAddress();
        
        if (addressList != null && !addressList.isEmpty()) {
            List<AddressDTO> addressDTOs = new ArrayList<AddressDTO>();
            
            for (Address address : addressList) {
                AddressDTO addressDTO = new AddressDTO();
                addressDTO.setDepartment(address.getDepartment());
                addressDTO.setStreet1(address.getStreet1());
                addressDTO.setStreet2(address.getStreet2());
                addressDTO.setStreet3(address.getStreet3());
                addressDTO.setPostCode(address.getPostCode());
                addressDTO.setCity(address.getCity());
                addressDTO.setState(address.getState());
                addressDTO.setRegionID(address.getRegionID());
                addressDTO.setCountryCode(address.getCountryCode());
                addressDTO.setCountryName(address.getCountryName());
                addressDTO.setPhoneNo(address.getPhoneNo());
                addressDTO.setMobilePhoneNumber(address.getMobilePhoneNumber());
                addressDTO.setFaxNumber(address.getFaxNumber());
                addressDTO.setContactName(address.getContactName());
                addressDTO.setEmail(address.getEmail());
                addressDTO.setDistrictNo(address.getDistrictNo());
                addressDTO.setPoBox(address.getPOBox());
                addressDTO.setPostCodePOBox(address.getPostCodePOBox());
                addressDTOs.add(addressDTO);
            }
            userOrganisationTypeDTO.setAddress(addressDTOs);
        }
        
        List<UserOrganisationType.UserLanguage> userLanguages = userOrganisationType.getUserLanguage();
        if (userLanguages != null && !userLanguages.isEmpty()) {
            UserOrganisationType.UserLanguage userLanguage = userLanguages.get(0);
            userOrganisationTypeDTO.setCountryCode(userLanguage.getCountryCode());
            userOrganisationTypeDTO.setLangCode(userLanguage.getLangCode());
        }
        
        return userOrganisationTypeDTO;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        return null;
    }
}
