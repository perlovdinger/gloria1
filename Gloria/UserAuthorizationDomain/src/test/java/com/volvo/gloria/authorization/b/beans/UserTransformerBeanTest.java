package com.volvo.gloria.authorization.b.beans;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.authorization.c.dto.ApplicationSettingDTO;
import com.volvo.gloria.authorization.c.dto.CategoryDTO;
import com.volvo.gloria.authorization.c.dto.UserApplicationDTO;
import com.volvo.gloria.authorization.c.dto.UserCompanyCodeDTO;
import com.volvo.gloria.authorization.c.dto.UserTransformerDTO;
import com.volvo.gloria.util.IOUtil;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class UserTransformerBeanTest  extends AbstractTransactionalTestCase {
    public UserTransformerBeanTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    private static final String INITDATA_USER_XML = "globaldataTest/UserOrganisationDetails.xml";
    
    
    @Inject
    private UserServices userServices;
    
    @Before
    public void setUpTestData() throws Exception {
        userServices.createUserData(IOUtil.getStringFromClasspath(INITDATA_USER_XML));

    }
    /*
     * No category of VIEWER etc so it should be zero and GLO-4787 
     */
    @Test
    public void testCreateUserCompanyCodeDTOs() throws Exception {
        String[] arr={"Viewer","Viewer_Price"};
        testType(arr[0]);
        testType(arr[1]);
    }
    private void testType(String type) {
        // Arrange
        UserTransformerBean bean = new UserTransformerBean();
        List<UserApplicationDTO> userCompanyCodeDTO = new ArrayList<UserApplicationDTO>();
        UserApplicationDTO userApplicationDTO = Mockito.mock(UserApplicationDTO.class);
        userCompanyCodeDTO.add(userApplicationDTO);        
        List<CategoryDTO> CategoryDTOs = new ArrayList<CategoryDTO>();
        CategoryDTO categoryDTO=Mockito.mock(CategoryDTO.class);
        CategoryDTOs.add(categoryDTO);
        Mockito.when(userApplicationDTO.getUserCategory()).thenReturn(CategoryDTOs);
        Mockito.when(categoryDTO.getId()).thenReturn(type);        
        List<ApplicationSettingDTO> applicationSettingDTOs = new ArrayList<ApplicationSettingDTO>();
        ApplicationSettingDTO applicationSettingDTO1=new ApplicationSettingDTO();
        applicationSettingDTO1.setApplicationSettingID("CCGRP");
        applicationSettingDTO1.setApplicationSettingValue("Value1");
        applicationSettingDTOs.add(applicationSettingDTO1);

        ApplicationSettingDTO applicationSettingDTO2=new ApplicationSettingDTO();
        applicationSettingDTO2.setApplicationSettingID("CCGRP");
        applicationSettingDTO2.setApplicationSettingValue("Value2");
        applicationSettingDTOs.add(applicationSettingDTO2);

        ApplicationSettingDTO applicationSettingDTO3=new ApplicationSettingDTO();
        applicationSettingDTO3.setApplicationSettingID("sfa");
        applicationSettingDTO3.setApplicationSettingValue("Value1");
        applicationSettingDTOs.add(applicationSettingDTO3);
        
        Mockito.when(userApplicationDTO.getApplicationSetting()).thenReturn(applicationSettingDTOs);
       
        // Act
        List<UserCompanyCodeDTO> userCompanyCodesDTO=bean.createUserCompanyCodeDTOs(userCompanyCodeDTO);
        
        // Assert
        Assert.assertEquals(2, userCompanyCodesDTO.size());
        Assert.assertEquals("Value1", userCompanyCodesDTO.get(0).getCode());
        Assert.assertEquals("Value2", userCompanyCodesDTO.get(1).getCode());
    }
    /*
     * No category of VIEWER etc so it should be zero refer task  GLO-4787  for requirement
     */
    @Test
    public void testCreateUserCompanyCodeDTOs1() throws Exception {
        List<UserTransformerDTO> userOrganisationTypeDTOs = userServices.createUserData(IOUtil.getStringFromClasspath(INITDATA_USER_XML));
        Assert.assertEquals(0, userOrganisationTypeDTOs.get(0).getUserCompanyCodes().size());    
    }
}
