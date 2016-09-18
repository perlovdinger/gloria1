package com.volvo.gloria.common.b.beans;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.TeamTransformer;
import com.volvo.gloria.common.c.dto.TeamTransformerDTO;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * TeamsTranformer Implementation.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TeamTransformerBean extends XmlTransformer implements TeamTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeamTransformerBean.class);

    public TeamTransformerBean() {
        super(XmlConstants.SchemaFullPath.INIT_TEAM, XmlConstants.PackageName.INIT_TEAM);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TeamTransformerDTO> transformTeams(String xmlContent) {
        try {
            return (List<TeamTransformerDTO>) transform(xmlContent);
        } catch (JAXBException e) {
            LOGGER.error("Unable to unmarshall the received message to a TeamDTO object, message will be discarded. Exception is:", e);
            throw new GloriaSystemException(e, "Unable to unmarshall the received message to a TeamDTO object, message will be discarded");
        }
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        com.volvo.group.init.team._1_0.Teams teams = (com.volvo.group.init.team._1_0.Teams) jaxbOject;
        List<TeamTransformerDTO> teamTransformerDTOs = new ArrayList<TeamTransformerDTO>();
        for (com.volvo.group.init.team._1_0.Team team : teams.getTeam()) {
            TeamTransformerDTO teamDTO = new TeamTransformerDTO();
            teamDTO.setName(team.getName());
            teamDTO.setType(team.getType()); 
            teamDTO.setCompanyCodeGroup(team.getCompanyCodeGroup());
            teamTransformerDTOs.add(teamDTO);
        }
        return teamTransformerDTOs;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        // TODO Auto-generated method stub
        return null;
    }

}
