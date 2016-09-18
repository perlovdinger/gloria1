/*
 * Copyright 2009 Volvo Information Technology AB 
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
 * DTO class for User Application.
 */
public class UserApplicationDTO {
    private String applicationID;
    private String applicationDescription;
    
    private List<ApplicationSettingDTO> applicationSetting;
    private List<CategoryDTO> userCategory;

    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public String getApplicationDescription() {
        return applicationDescription;
    }

    public void setApplicationDescription(String applicationDescription) {
        this.applicationDescription = applicationDescription;
    }

    public List<ApplicationSettingDTO> getApplicationSetting() {
        return applicationSetting;
    }

    public void setApplicationSetting(List<ApplicationSettingDTO> applicationSetting) {
        this.applicationSetting = applicationSetting;
    }

    public List<CategoryDTO> getUserCategory() {
        return userCategory;
    }

    public void setUserCategory(List<CategoryDTO> userCategory) {
        this.userCategory = userCategory;
    }
}
