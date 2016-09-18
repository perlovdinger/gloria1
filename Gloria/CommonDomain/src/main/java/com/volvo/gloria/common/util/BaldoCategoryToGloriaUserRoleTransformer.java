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
package com.volvo.gloria.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.volvo.gloria.baldo.userrole.BaldoCategory;
import com.volvo.gloria.baldo.userrole.GloriaRole;
import com.volvo.gloria.baldo.userrole.RoleMap;
import com.volvo.gloria.common.c.dto.RoleDTO;
import com.volvo.gloria.config.b.beans.ApplicationProperties;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Class to map/transform gloria user roles to baldo user categories.
 */
public final class BaldoCategoryToGloriaUserRoleTransformer extends XmlTransformer {
    private static final String USERROLECONFIG_DATA_CP = "userroleconfig/GloriaUserRolesMapWithBaldoCategories.xml";
    private static BaldoCategoryToGloriaUserRoleTransformer singleton = new BaldoCategoryToGloriaUserRoleTransformer();
    private static String message;
    private Map<String, List<RoleDTO>> gloriaUserRolesMap = new HashMap<String, List<RoleDTO>>();

    private BaldoCategoryToGloriaUserRoleTransformer() {
        super(XmlConstants.SchemaFullPath.CONFIG_GLORIA_USER_ROLE, XmlConstants.PackageName.CONFIG_GLORIA_USER_ROLE);
        try {
            message = IOUtil.getStringFromClasspath(USERROLECONFIG_DATA_CP);
            Object object = transformXmlToJaxb(message);
            String env = ServiceLocator.getService(ApplicationProperties.class).getEnvironment();

            RoleMap roleMap = (RoleMap) object;
            List<BaldoCategory> baldoCategories = roleMap.getBaldoCategory();
            for (BaldoCategory baldoCategory : baldoCategories) {

                // The Warehouse Manager role should currently not be able to run in production
                if (env.equals("prod-was") && baldoCategory.getCategory().equals("Warehouse_manager")) {
                } else {
                    List<RoleDTO> gloriaRoles = new ArrayList<RoleDTO>();

                    List<GloriaRole> mappedgloriaRoles = baldoCategory.getGloriaRole();
                    for (GloriaRole mappedGloriaRole : mappedgloriaRoles) {
                        RoleDTO roleDTO = new RoleDTO();
                        roleDTO.setId(mappedGloriaRole.getUserRole());
                        roleDTO.setRoleName(mappedGloriaRole.getUserRole());
                        gloriaRoles.add(roleDTO);
                    }
                    gloriaUserRolesMap.put(baldoCategory.getCategory(), gloriaRoles);
                }
            }
        } catch (IOException e) {
            throw new GloriaSystemException(e, "User role config file is can not be read.");
        } catch (JAXBException e) {
            throw new GloriaSystemException(e, "User role config file is not valid.");
        }
    }

    public static BaldoCategoryToGloriaUserRoleTransformer getInstance() {
        return singleton;
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        return null;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        return null;
    }

    public Map<String, List<RoleDTO>> getGloriaUserRolesMap() {
        return gloriaUserRolesMap;
    }
}
