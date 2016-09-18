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
package com.volvo.gloria.authorization.b;

import java.util.List;

import com.volvo.gloria.authorization.c.dto.UserTransformerDTO;
import com.volvo.gloria.authorization.d.entities.ApplicationSetting;
import com.volvo.gloria.authorization.d.entities.GloriaUser;
import com.volvo.gloria.authorization.d.entities.UserCategory;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * User service interface.
 */
public interface UserAuthorizationServices {   

    void addUser(UserTransformerDTO userOrganisationTypeDTO);
    
    void manageUsers(List<UserTransformerDTO> userOrganisationDTO);

    List<UserCategory> getUserCategoryByUsersOId(Long userOrganisationTypeOid);
    
    List<String> getBaldoUserCategories(Long userOrganisationTypeOid);
    
    List<ApplicationSetting> getApplicationSettings(String userId);

    GloriaUser findUserByUserId(String userId) throws GloriaApplicationException;

    List<GloriaUser> getUsersByApplicationSettingValue(String loggedInUserIdFollowUpName);

    List<GloriaUser> getUsersByTeam(long teamOID, String userCategory);
    
    void assignToInternalProcureTeam(String userId, long teamOID);
}
