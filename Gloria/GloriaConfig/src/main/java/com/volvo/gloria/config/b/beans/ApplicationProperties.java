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
package com.volvo.gloria.config.b.beans;


/**
 * This class keeps properties for a specific environment (local, DEV, TEST, QA).
 */
public class ApplicationProperties {
   
    private String environment;
    private String loginProcess;
    private String database;
    private boolean messageSend;
    private String mailHost;
    private String sitesToMigrate;
    private String companyCodes;
    
    public boolean isMessageSend() {
        return messageSend;
    }

    public void setMessageSend(boolean messageSend) {
        this.messageSend = messageSend;
    }

    
    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    
    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }    

    public String toString() {
        return "ApplicationProperties:environment=" + environment +  " database=" + database + " messageSend=" + messageSend;
    }

    public String getMailHost() {
        return mailHost;
    }

    public void setMailHost(String mailHost) {
        this.mailHost = mailHost;
    }

    public String getSitesToMigrate() {
        return sitesToMigrate;
    }

    public void setSitesToMigrate(String sitesToMigrate) {
        this.sitesToMigrate = sitesToMigrate;
    }

    public String getCompanyCodes() {
        return companyCodes;
    }

    public void setCompanyCodes(String companyCodes) {
        this.companyCodes = companyCodes;
    }

    public String getLoginProcess() {
        return loginProcess;
    }

    public void setLoginProcess(String loginProcess) {
        this.loginProcess = loginProcess;
    }
}
