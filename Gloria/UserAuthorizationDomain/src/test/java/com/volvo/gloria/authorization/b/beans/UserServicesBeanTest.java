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
package com.volvo.gloria.authorization.b.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.volvo.gloria.authorization.b.UserAuthorizationServices;
import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.authorization.c.dto.AddressDTO;
import com.volvo.gloria.authorization.c.dto.ApplicationSettingDTO;
import com.volvo.gloria.authorization.c.dto.CategoryDTO;
import com.volvo.gloria.authorization.c.dto.UserApplicationDTO;
import com.volvo.gloria.authorization.c.dto.UserTransformerDTO;
import com.volvo.gloria.common.d.entities.CompanyCode;
import com.volvo.gloria.common.repositories.b.CompanyCodeRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class UserServicesBeanTest extends AbstractTransactionalTestCase {
    public UserServicesBeanTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }  
    private static final String INITDATA_USER_XML = "globaldataTest/UserOrganisationDetails.xml";
    private static final String INITDATA_TEST_USER_XML = "globaldataTest/TestUserOrganisationDetails.xml";
   
    @Inject
    private UserServices userServices;
    @Inject
    private UserAuthorizationServices authorizationServices;
    
    @Inject
    private CompanyCodeRepository companyCodeRepository;

    @Before
    public void setUpTestData() throws Exception {
        userServices.createUserData(IOUtil.getStringFromClasspath(INITDATA_USER_XML));

    }

    /**
     * GLO-889BUG: Client diplays all modules
     * 
     * @throws Exception
     */
    @Test
    @Ignore
    public void testClientDisplaysAllModules() throws Exception {
        // Arrange
        // Act
        // Assert
        Assert.assertEquals(1, userServices.getUserRoles("wh1").size());
        Assert.assertEquals(7, userServices.getUserRoles("all").size());
    }

    
    public UserTransformerDTO getUsersListToManage(String ciAction) throws IOException {
        List<UserTransformerDTO> userOrganisationTypeDTOs = userServices.createUserData(IOUtil.getStringFromClasspath(INITDATA_TEST_USER_XML));
        for (UserTransformerDTO userOrganisationTypeDTO : userOrganisationTypeDTOs) {
            if (userOrganisationTypeDTO.getCiAction().equals(ciAction)) {
                return userOrganisationTypeDTO;
            }
        }
        return null;
    }
    
    /**
     * Scenario to test the unique user id constraint
     * Assertion : Console error is logged. Unique User Id error.
     */
    @Test
    public void testUserAdd() {
        //Arrange
        UserTransformerDTO organisationTypeDTO = new UserTransformerDTO();
        organisationTypeDTO.setUserID("all");
        organisationTypeDTO.setUserFirstName("Smitha");
        organisationTypeDTO.setUserLastName("Gowda");
        
        //Act
        authorizationServices.addUser(organisationTypeDTO);
    }
    
    /**
     * Scenario to test the unique user id constraint while update
     * Assertion :No Console error should be logged
     */
    @Test
    public void testUserUpdate() {
        //Arrange
        List<UserTransformerDTO> dtos = new ArrayList<UserTransformerDTO>();
        UserTransformerDTO organisationTypeDTO = new UserTransformerDTO();
        organisationTypeDTO.setUserID("all");
        organisationTypeDTO.setUserFirstName("Smitha");
        organisationTypeDTO.setUserLastName("Gowda");
        organisationTypeDTO.setCiAction("U");
    
        List<AddressDTO> addresses = new ArrayList<AddressDTO>();
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setDepartment("DVP");
        addresses.add(addressDTO);
        organisationTypeDTO.setAddress(addresses);
        List<UserApplicationDTO> organisationApplicationDTOs = new ArrayList<UserApplicationDTO>();
        UserApplicationDTO applicationDTO = new UserApplicationDTO();
        applicationDTO.setApplicationDescription("Gloria");
     
        List<ApplicationSettingDTO> applicationSettings = new ArrayList<ApplicationSettingDTO>();
        ApplicationSettingDTO applicationSettingDTO = new ApplicationSettingDTO();
        applicationSettingDTO.setApplicationSettingID("123");
        applicationSettings.add(applicationSettingDTO);
        applicationDTO.setApplicationSetting(applicationSettings);
        
        List<CategoryDTO> userCategorys = new ArrayList<CategoryDTO>();
        CategoryDTO categoryDTO =  new CategoryDTO();
        categoryDTO.setId("IT_support");
        userCategorys.add(categoryDTO);
        applicationDTO.setUserCategory(userCategorys);
        organisationApplicationDTOs.add(applicationDTO);
        
        
        organisationTypeDTO.setOrganisationApplication(organisationApplicationDTOs);
        
        dtos.add(organisationTypeDTO);
        
        //Act
        authorizationServices.manageUsers(dtos);
    }
    
    @Test
    public void testGetUserCompanyCodes() throws GloriaApplicationException{
        // Arrange
        String userId = "ALL";
        CompanyCode companycode = new CompanyCode();
        companycode.setCode("SE27");

        companyCodeRepository.save(companycode);
        
        // Act
        List<CompanyCode> companyCodes = userServices.getUserCompanyCodes(userId, null, null, null);
        
        //Assert
        Assert.assertNotNull(companyCodes);
        
    }
    @Ignore
    public void testAuthenticateUser() throws GloriaApplicationException{
        UserServicesBean bean = new UserServicesBean();
        try {
        Assert.assertTrue(bean.authenticateUser("userid1", "correct")); 
        Assert.assertFalse(bean.authenticateUser("userid1", "wrong")); 
        } catch (GloriaApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
