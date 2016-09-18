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
package com.volvo.gloria.authorization.d.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entity class for User Category.
 */
@Entity
@Table(name = "USER_CATEGORY")
public class UserCategory implements GenericEntity<Long> {
    private static final long serialVersionUID = -2798783567429587609L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_CATEGORY_OID")
    private long userCategoryOid;

    private String userCategoryID;
    private String userCategoryDescription;
    private Date endTime;

    @ManyToOne
    @JoinColumn(name = "userApplication")
    private UserApplication userApplication;

    public long getUserCategoryOid() {
        return userCategoryOid;
    }

    public void setUserCategoryOid(long userCategoryOid) {
        this.userCategoryOid = userCategoryOid;
    }

    public String getUserCategoryID() {
        return userCategoryID;
    }

    public void setUserCategoryID(String userCategoryID) {
        if (userCategoryID != null) {
            this.userCategoryID = userCategoryID.trim();
        }
    }

    public String getUserCategoryDescription() {
        return userCategoryDescription;
    }

    public void setUserCategoryDescription(String value) {
        if (value != null) {
            this.userCategoryDescription = value.trim();
        }
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date value) {
        this.endTime = value;
    }

    public UserApplication getUserApplication() {
        return userApplication;
    }

    public void setUserApplication(UserApplication userApplication) {
        this.userApplication = userApplication;
    }

    @Override
    public Long getId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getVersion() {
        // TODO Auto-generated method stub
        return 0;
    }
}
