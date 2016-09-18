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
import java.util.List;

import com.volvo.gloria.authorization.b.UserAuthorizationServices;
import com.volvo.gloria.authorization.b.UserTransformer;
import com.volvo.gloria.authorization.c.dto.UserTransformerDTO;
import com.volvo.gloria.util.IOUtil;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Initialises data for User Domain, by loading an XML file on classpath or using a file URI defined by <code>domain.user</code> property.
 * 
 */
public final class InitDataUser {

    private static final String XML = "xml";
    private static final String USER_PATH_BUSINESS = "/data/base/user/Business/";
    private static final String USER_PATH_PROJECT = "/data/base/user/Project/";
    private static final String USER_PATH_HARDCODED = "/data/base/user/Hardcoded/";

    private InitDataUser() {
    }

    public static void initUsers(String env) throws IOException {
        String[] files = null; 
        files =  getFiles(USER_PATH_BUSINESS, env);
        processUser(files);
        
        files =  getFiles(USER_PATH_PROJECT, env);
        processUser(files);
        
        files =  getFiles(USER_PATH_HARDCODED, env);
        processUser(files);        
        
    }
    private static void processUser(String[] files){
        if (files != null) {
            for (String aFileContent : files) {
                if (aFileContent != null) {
                    initUser(aFileContent);
                }
            }
        }
    }
    private static String[] getFiles(String userPath, String env) throws IOException {
        String[] xmlFiles = null;
        if (env.equals("initDB/prod-was")) {
            // In prod users have to exist in configuration
            try {
                xmlFiles = IOUtil.loadFileContentsFromClasspath(userPath, XML, env);
            } catch (Exception e) {
                // ignore
            }
        } else {
            try {
                xmlFiles = IOUtil.loadFileContentsFromClasspath(userPath, XML, env);
            } catch (IOException e) {
                xmlFiles = IOUtil.loadFileContentsFromClasspath(userPath, XML, "initDB/_global");
            }
        }

        return xmlFiles;

    }

    private static void initUser(String aFileContent) {
        UserTransformer userTransformer = ServiceLocator.getService(UserTransformer.class);
        UserAuthorizationServices userAuthorizationServices = ServiceLocator.getService(UserAuthorizationServices.class);
        List<UserTransformerDTO> userOrganisationTypeDTOs = userTransformer.transformStoredUser(aFileContent);
        for (UserTransformerDTO userOrganisationTypeDTO : userOrganisationTypeDTOs) {
            userAuthorizationServices.addUser(userOrganisationTypeDTO);
        }
    }
}
