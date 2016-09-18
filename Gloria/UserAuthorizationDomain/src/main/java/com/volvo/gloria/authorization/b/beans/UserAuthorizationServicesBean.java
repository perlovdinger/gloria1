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

import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.volvo.gloria.authorization.b.UserAuthorizationServices;
import com.volvo.gloria.authorization.c.dto.UserTransformerDTO;
import com.volvo.gloria.authorization.d.entities.ApplicationSetting;
import com.volvo.gloria.authorization.d.entities.UserCategory;
import com.volvo.gloria.authorization.d.entities.GloriaUser;
import com.volvo.gloria.common.repositories.b.TeamRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * UserServices service implementation bean.
 */
@ContainerManaged(name = "userAuthorizationServices")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UserAuthorizationServicesBean implements UserAuthorizationServices {

    @Inject
    private TeamRepository teamRepository;

    /**
     * {@inheritDoc}
     */
    public void addUser(UserTransformerDTO userTransformerDTO) {
        teamRepository.addUser(userTransformerDTO);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void manageUsers(List<UserTransformerDTO> userTransformerDTOs) {
        teamRepository.manageUsers(userTransformerDTOs);
    }

    @Override
    public List<UserCategory> getUserCategoryByUsersOId(Long userOid) {
        return teamRepository.getUserCategoryByUserOId(userOid);
    }

    @Override
    public List<String> getBaldoUserCategories(Long userOrganisationTypeOid) {
        return teamRepository.getBaldoUserCategories(userOrganisationTypeOid);
    }

    @Override
    public List<ApplicationSetting> getApplicationSettings(String userId) {
        return teamRepository.getApplicationSettings(userId);
    }

    @Override
    public GloriaUser findUserByUserId(String userId) throws GloriaApplicationException {
        return teamRepository.findUserByUserId(userId);
    }

    @Override
    public List<GloriaUser> getUsersByApplicationSettingValue(String loggedInUserIdFollowUpName) {
        return teamRepository.getUsersByApplicationSettingValue(loggedInUserIdFollowUpName);
    }

    @Override
    public List<GloriaUser> getUsersByTeam(long teamOID, String userCategory) {
        return teamRepository.getUsersByTeam(teamOID, userCategory);
    }

    @Override
    public void assignToInternalProcureTeam(String userId, long teamOID) {
        GloriaUser user = teamRepository.findUserByUserId(userId);
        if (user != null) {
            user.setInternalProcureTeamOID(teamOID);
            teamRepository.save(user);
        }
    }
}
