package com.volvo.gloria.authorization.b;

import java.util.List;

import com.volvo.gloria.common.c.dto.TeamUserTransformerDTO;
/**
 * TeamUser transformer interface.
 */
public interface TeamUserTransformer {
    List<TeamUserTransformerDTO> transformTeamUser(String xmlContent);
}
