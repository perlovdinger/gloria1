package com.volvo.gloria.common.c.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for Team users transformation.
 * 
 */
public class TeamUserTransformerDTO {
    private String userId;
    private List<TeamTransformerDTO> teamTransformerDTOs = new ArrayList<TeamTransformerDTO>();
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public List<TeamTransformerDTO> getTeamTransformerDTOs() {
        return teamTransformerDTOs;
    }
    public void setTeamTransformerDTOs(List<TeamTransformerDTO> teamTransformerDTOs) {
        this.teamTransformerDTOs = teamTransformerDTOs;
    }

 }
