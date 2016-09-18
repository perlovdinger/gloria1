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
package com.volvo.gloria.authorization.util;

import java.io.IOException;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Initialises data for Team users, by loading an XML file on classpath or using a file URI defined by <code>domain.user</code> property.
 * 
 */
public final class InitDataTeamUser {

    private static final String XML = "xml";
    private static final String TEAM_USER_BUSINESS = "/data/base/user/BusinessTeamUser/";
    private static final String TEAM_USER_PROJECT = "/data/base/user/ProjectTeamUser/";
    private static final String TEAM_USER_HARDCODED = "/data/base/user/HardcodedTeamUser/";
    

    private InitDataTeamUser() throws IOException {
    }
    
    public static void initTeamUsers(String env) throws IOException, GloriaApplicationException {
        UserServices userServices = ServiceLocator.getService(UserServices.class);
        
        for (String aFileContent : getFiles(TEAM_USER_BUSINESS, env)) {
            userServices.initTeamUser(aFileContent);
        }
        for (String aFileContent : getFiles(TEAM_USER_PROJECT, env)) {
            userServices.initTeamUser(aFileContent);
        }
        for (String aFileContent : getFiles(TEAM_USER_HARDCODED, env)) {
            userServices.initTeamUser(aFileContent);
        }
    }
    
    
    private static String[] getFiles(String userPath, String env) throws IOException {
        String[] xmlFiles = null;
        try {
            xmlFiles = IOUtil.loadFileContentsFromClasspath(userPath, XML, env);
        } catch (IOException e) {
            xmlFiles = IOUtil.loadFileContentsFromClasspath(userPath, XML, "initDB/_global");           
        }
        return xmlFiles;

    }


}
