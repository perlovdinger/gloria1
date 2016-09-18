package com.volvo.gloria.authorization.util;

import java.util.ArrayList;
import java.util.List;

import com.volvo.gloria.authorization.d.entities.Team;
import com.volvo.gloria.common.c.dto.TeamDTO;

/**
 * Helper class for UserAuthorization domain related entities.
 * 
 */
public final class UserHelper {

    private UserHelper() {
    }

    public static List<TeamDTO> transformToTeamDTOs(List<Team> teams) {
        List<TeamDTO> teamDTOs = new ArrayList<TeamDTO>();
        if (teams != null && !teams.isEmpty()) {
            for (Team team : teams) {
                TeamDTO teamDTO = transformToTeamDTO(team);
                teamDTOs.add(teamDTO);
            }
        }
        return teamDTOs;
    }

    public static TeamDTO transformToTeamDTO(Team team) {
        if (team != null) {
            TeamDTO teamDTO = new TeamDTO();
            teamDTO.setId(team.getId());
            teamDTO.setVersion(team.getVersion());
            teamDTO.setName(team.getName());
            // It support does not belong to any team
            if (team.getType() == null) {
                teamDTO.setType("");
            } else {
                teamDTO.setType(team.getType().name());
            }
            return teamDTO;
        }
        return null;
    }
}
