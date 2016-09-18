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
package com.volvo.gloria.warehouse.c.dto;

import java.io.Serializable;

/**
 * This is the Warehouse UI DTO.
 */

public class WarehouseDTO implements Serializable {

    private static final long serialVersionUID = 9036914082400143558L;
    
    private long id;
    private long version;
    private String siteId;
    private String siteCode;
    private String siteName;
    private String countryCode;
    private String companyCode;
    private String address;
    private String setUp;
    private boolean qiSupported;
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getVersion() {
        return version;
    }
    public void setVersion(long version) {
        this.version = version;
    }
    public String getSiteId() {
        return siteId;
    }
    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }
    public String getSiteCode() {
        return siteCode;
    }
    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }
    public String getSiteName() {
        return siteName;
    }
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
    public String getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    public String getCompanyCode() {
        return companyCode;
    }
    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getSetUp() {
        return setUp;
    }
    public void setSetUp(String setUp) {
        this.setUp = setUp;
    }
    public boolean isQiSupported() {
        return qiSupported;
    }
    public void setQiSupported(boolean qiSupported) {
        this.qiSupported = qiSupported;
    }
    
}
