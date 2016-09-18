package com.volvo.gloria.common.b;

import java.util.List;

import com.volvo.gloria.common.c.dto.TeamTransformerDTO;

/**
 * Transformer class for Teams.
 */
public interface TeamTransformer {
    List<TeamTransformerDTO> transformTeams(String xmlContent);
}
