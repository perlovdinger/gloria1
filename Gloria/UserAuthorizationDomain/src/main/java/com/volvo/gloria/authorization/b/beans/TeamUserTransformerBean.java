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
package com.volvo.gloria.authorization.b.beans;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.authorization.b.TeamUserTransformer;
import com.volvo.gloria.common.c.dto.TeamTransformerDTO;
import com.volvo.gloria.common.c.dto.TeamUserTransformerDTO;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.group.init.teamUser._1_0.Team;
import com.volvo.group.init.teamUser._1_0.User;
import com.volvo.group.init.teamUser._1_0.Users;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * User message transformer service implementation.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TeamUserTransformerBean extends XmlTransformer implements TeamUserTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeamUserTransformerBean.class);

    TeamUserTransformerBean() {
        super(XmlConstants.SchemaFullPath.USER_TEAM, XmlConstants.PackageName.USER_TEAM);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TeamUserTransformerDTO> transformTeamUser(String xmlContent) {
        try {
            return (List<TeamUserTransformerDTO>) transform(xmlContent);
        } catch (JAXBException e) {
            // Throw an exception to roll-back the transaction.
            LOGGER.error("Unable to unmarshall TeamUser object.Exception is:", e);
            throw new GloriaSystemException(e, "Unable to unmarshall TeamUser");
        }
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        Users usersObject = (Users) jaxbOject;
        List<TeamUserTransformerDTO> userTeamTransformerDTOs = new ArrayList<TeamUserTransformerDTO>();
        for (User user : usersObject.getUser()) {
            TeamUserTransformerDTO teamUserTransformerDTO = new TeamUserTransformerDTO();
            teamUserTransformerDTO.setUserId(user.getUserId());
            for (Team team : user.getTeam()) {
                TeamTransformerDTO teamTransformerDTO = new TeamTransformerDTO();
                teamTransformerDTO.setName(team.getName());
                teamTransformerDTO.setType(team.getType());
                teamUserTransformerDTO.getTeamTransformerDTOs().add(teamTransformerDTO);
            }
            userTeamTransformerDTOs.add(teamUserTransformerDTO);
        }
        return userTeamTransformerDTOs;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        return null;
    }
}
