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
package com.volvo.gloria.util.persistence;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.EntityManager;

import com.volvo.gloria.util.DateUtil;

/**
 * Responsible for all updating of entities that implements BuildChangeble.
 * 
 */
public class BuildChangeMgr {
    private EntityManager em;

    /**
     * Instansiating with EntityManager.
     * 
     * @param em
     *            EntityManager.
     */
    public BuildChangeMgr(EntityManager em) {
        this.em = em;
    }

    public void addRow(BuildChangeble buildChangeble, BuildChangeDateTime buildChangeDateTime) {
        // validFrom = eventDateTime
        // ValidTo = forever

        buildChangeble.setAddedBuildChange(buildChangeDateTime.getBuildChange());
        buildChangeble.setAddedDateTime(new Timestamp(System.currentTimeMillis()));

        buildChangeble.setValidFrom(buildChangeDateTime.getEventDateTime());
        buildChangeble.setValidTo(DateUtil.getForeverSqlDate());
        em.persist(buildChangeble);
    }

    public void removeRow(BuildChangeble buildChangeble, BuildChangeDateTime buildChangeDateTime) {
        // validFrom = no change
        // ValidTo = eventDateTime

        BuildChangeble readEntity = em.find(buildChangeble.getClass(), buildChangeble.getOID());
        if (readEntity != null) {
            readEntity.setRemovedBuildChange(buildChangeDateTime.getBuildChange());
            readEntity.setRemovedDateTime(new Timestamp(System.currentTimeMillis()));
            readEntity.setValidTo(buildChangeDateTime.getEventDateTime());
            em.persist(readEntity);
        }
    }

    public void updateRow(BuildChangeble buildChangeble) {
        // is update allowed ?
        // only allowed if currentDate < validFrom
        Date validFromDate = buildChangeble.getValidFrom();
        Date now = new Date(System.currentTimeMillis());
        if (now.before(validFromDate)) {
            // ok to update
            em.merge(buildChangeble);
        }
    }
}
