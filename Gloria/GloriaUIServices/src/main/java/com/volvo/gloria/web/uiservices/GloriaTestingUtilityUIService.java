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
package com.volvo.gloria.web.uiservices;

import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.springframework.util.StringUtils;

import com.volvo.gloria.common.util.InitDataCarryOver;
import com.volvo.gloria.common.util.InitDataCostCenter;
import com.volvo.gloria.common.util.InitDataWbsElement;
import com.volvo.gloria.config.b.beans.ApplicationProperties;
import com.volvo.gloria.config.b.beans.ApplicationUtils;
import com.volvo.gloria.procurematerial.util.migration.b.MigrationService;
import com.volvo.gloria.procurematerial.util.migration.c.dto.MigrationStatusDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.b.UtilServices;
import com.volvo.gloria.util.c.TestMessageDTO;
import com.volvo.gloria.warehouse.util.InitDataPrinter;
import com.volvo.gloria.warehouse.util.InitDataWarehouse;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Restful layer for managaing test data.
 * 
 */
@Path("/testingutility")
public class GloriaTestingUtilityUIService {

    private static final String NONOPERATIVE_MIGRATION_DATA = "config/MigrationData/";
    private static final String MIGRATION_DATA_PROPERTY_KEY = "MigrationData";

    public static final String[] COMPANY_CODES_TO_BE_MIGRATED = { "FR46" };

    private UtilServices utilServices = ServiceLocator.getService(UtilServices.class);
    private MigrationService migrationService = ServiceLocator.getService(MigrationService.class);

    @POST
    @Path("/v1/messages")
    public Response createMessageV1(TestMessageDTO testMessage) throws GloriaApplicationException, JAXBException, IOException {
        if ("xml".equals(testMessage.getFormat())) {
            utilServices.sendMessage(testMessage.getMessageContent(), testMessage.getJmsQueueId());
        }
        return null;
    }

    @POST
    @Path("/v1/testloginmsg")
    public String testLogin() {
        return "communication with UI Service is successful";
    }

    @POST
    @Path("/v1/cleandb")
    public Response cleanDBV1() throws GloriaApplicationException, JAXBException, IOException {
        return null;
    }

    @POST
    @Path("/v1/initcostcenters")
    public Response initCostCentersV1(TestMessageDTO testMessage) throws GloriaApplicationException, JAXBException, IOException {
        new InitDataCostCenter().initCostCenters("initDB/" + getEnvironment(),null);

        return null;
    }

    @POST
    @Path("/v1/initwbselements")
    public Response initWbsElementsV1(TestMessageDTO testMessage) throws GloriaApplicationException, JAXBException, IOException {
        new InitDataWbsElement().initWbsElement("initDB/" + getEnvironment(),null);

        return null;
    }

    @POST
    @Path("/v1/initcarryovers1")
    public Response initCarryOversV1(TestMessageDTO testMessage) throws GloriaApplicationException, JAXBException, IOException {
        String carryOverPath = "/data/nonOperative/CarryOver1/";
        new InitDataCarryOver().initDataCarryOver("initDB/" + getEnvironment(), IOUtil.FILE_TYPE_EXCEL_NEW, carryOverPath);
        return null;
    }
    @POST
    @Path("/v1/initcarryovers2")
    public Response initCarryOvers2V1(TestMessageDTO testMessage) throws GloriaApplicationException, JAXBException, IOException {
        String carryOverPath = "/data/nonOperative/CarryOver2/";
        new InitDataCarryOver().initDataCarryOver("initDB/" + getEnvironment(), IOUtil.FILE_TYPE_EXCEL_NEW, carryOverPath);
        return null;
    }
    @POST
    @Path("/v1/initcarryovers3")
    public Response initCarryOvers3V1(TestMessageDTO testMessage) throws GloriaApplicationException, JAXBException, IOException {
        String carryOverPath = "/data/nonOperative/CarryOver3/";
        new InitDataCarryOver().initDataCarryOver("initDB/" + getEnvironment(), IOUtil.FILE_TYPE_EXCEL_NEW, carryOverPath);
        return null;
    }
    @POST
    @Path("/v1/initcarryovers4")
    public Response initCarryOvers4V1(TestMessageDTO testMessage) throws GloriaApplicationException, JAXBException, IOException {
        String carryOverPath = "/data/nonOperative/CarryOver4/";
        new InitDataCarryOver().initDataCarryOver("initDB/" + getEnvironment(), IOUtil.FILE_TYPE_EXCEL_NEW, carryOverPath);
        return null;
    }
    @POST
    @Path("/v1/initcarryovers5")
    public Response initCarryOvers5V1(TestMessageDTO testMessage) throws GloriaApplicationException, JAXBException, IOException {
        String carryOverPath = "/data/nonOperative/CarryOver5/";
        new InitDataCarryOver().initDataCarryOver("initDB/" + getEnvironment(), IOUtil.FILE_TYPE_EXCEL_NEW, carryOverPath);
        return null;
    }
    
    @POST
    @Path("/v1/initwarehouses")
    public Response initwarehouses(TestMessageDTO testMessage) throws GloriaApplicationException, JAXBException, IOException {
        InitDataWarehouse.initWarehouses(getEnvironment(), null, false);       
        new InitDataPrinter(new Properties(), getEnvironment(), null).initPrinter();
        return null;
    }

    @POST
    @Path("/v1/warehousemigration")
    public Response initWarehouseMigrationV1(@QueryParam("ids") String siteIds) throws GloriaApplicationException {
        String[] sitesToBeMigrated = getSitesToBeMigrated(siteIds);

        Properties warehouseProperties = new Properties();
        warehouseProperties.setProperty(MIGRATION_DATA_PROPERTY_KEY, getInputFileLocation());
        migrationService.initiateWarehouseMigration(warehouseProperties, sitesToBeMigrated);
        return null;
    }

    @POST
    @Path("/v1/openordermigration")
    public Response initOpenOrderMigrationV1(@QueryParam("ids") String siteIds) throws GloriaApplicationException {
        String[] sitesToBeMigrated = getSitesToBeMigrated(siteIds);

        Properties openOrderProperties = new Properties();
        openOrderProperties.setProperty(MIGRATION_DATA_PROPERTY_KEY, getInputFileLocation());
        migrationService.initiateOrderMigration(openOrderProperties, sitesToBeMigrated);
        return null;
    }

    @POST
    @Path("/v1/migration")
    public Response initMigrationV1(@QueryParam("ids") String siteIds) throws GloriaApplicationException {
        String[] sitesToBeMigrated = getSitesToBeMigrated(siteIds);
        Properties warehouseProperties = new Properties();
        warehouseProperties.setProperty(MIGRATION_DATA_PROPERTY_KEY, getInputFileLocation());
        migrationService.initiateWarehouseMigration(warehouseProperties, sitesToBeMigrated);

        Properties openOrderProperties = new Properties();
        openOrderProperties.setProperty(MIGRATION_DATA_PROPERTY_KEY, getInputFileLocation());
        migrationService.initiateOrderMigration(openOrderProperties, sitesToBeMigrated);
        return null;
    }

    @POST
    @Path("/v1/sendfinancemsg")
    public Response initiateSendFinanceMessageForOrderMigration(@QueryParam("ids") String companyCodes) throws GloriaApplicationException {
        migrationService.initiateSendFinanceMessageForOrderMigration(COMPANY_CODES_TO_BE_MIGRATED);
        return null;
    }

    @GET
    @Path("/v1/migrationstatus")
    @Produces(MediaType.APPLICATION_JSON)
    public MigrationStatusDTO getMigrationStatusV1() throws GloriaApplicationException {
        return migrationService.getstatus();
    }

    private String getInputFileLocation() {
        String fileLocation = null;
        String env = ServiceLocator.getService(ApplicationProperties.class).getEnvironment();
        if ("dev-local-tomcat".equals(env)) {
            fileLocation = "C:\\Gloria\\migration\\input\\";
        } else {
            fileLocation = NONOPERATIVE_MIGRATION_DATA;
        }
        return fileLocation;
    }

    private String getEnvironment() {
        return ServiceLocator.getService(ApplicationProperties.class).getEnvironment();
    }

    private String[] getSitesToBeMigrated(String siteIds) {
        String[] sitesToBeMigrated;
        // We should only let user select sites in local dev env
        if (!StringUtils.isEmpty(siteIds) && ("dev-local-tomcat".equals(getEnvironment()))) {
            sitesToBeMigrated = siteIds.split(",");
        } else {
            sitesToBeMigrated = ApplicationUtils.getSitesToMigrate(getEnvironment());
        }       
        sitesToBeMigrated = ApplicationUtils.getSitesToMigrate(getEnvironment());
        return sitesToBeMigrated;
    }
}
