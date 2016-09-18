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
package com.volvo.gloria.authorization.c.dto;

import java.util.List;

/**
 * DTO class for Baldo Users.
 */
public class UserTransformerDTO {
    private String userID;
    private String userFirstName;
    private String userLastName;
    private String organisationID;
    private String organisationName;
    private List<AddressDTO> address;
    private String langCode;
    private String countryCode;
    private String ciAction;
    private List<UserCompanyCodeDTO> userCompanyCodes;
    private List<UserApplicationDTO> organisationApplication;
    
    public List<UserCompanyCodeDTO> getUserCompanyCodes() {
        return userCompanyCodes;
    }

    public void setUserCompanyCodes(List<UserCompanyCodeDTO> userCompanyCodes) {
        this.userCompanyCodes = userCompanyCodes;
    }



    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getOrganisationID() {
        return organisationID;
    }

    public void setOrganisationID(String organisationID) {
        this.organisationID = organisationID;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public List<UserApplicationDTO> getOrganisationApplication() {
        return organisationApplication;
    }

    public void setOrganisationApplication(List<UserApplicationDTO> organisationApplication) {
        this.organisationApplication = organisationApplication;
    }

    public List<AddressDTO> getAddress() {
        return address;
    }

    public void setAddress(List<AddressDTO> address) {
        this.address = address;
    }

    public String getCiAction() {
        return ciAction;
    }

    public void setCiAction(String ciAction) {
        this.ciAction = ciAction;
    }

    // Base equals on articleId and storageLocationId
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UserTransformerDTO other = (UserTransformerDTO) obj;
        if (!this.getUserID().equals(other.getUserID())) {
            return false;
        }
        return true;
    }

    // Base hashCode on articleId and storageLocationId
    @Override
    public int hashCode() {
        return this.getUserID().hashCode();
    }
}
