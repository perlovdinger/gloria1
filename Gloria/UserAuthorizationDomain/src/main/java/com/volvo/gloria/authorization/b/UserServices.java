package com.volvo.gloria.authorization.b;

import java.util.List;

import com.volvo.gloria.authorization.c.TeamType;
import com.volvo.gloria.authorization.c.dto.UserTransformerDTO;
import com.volvo.gloria.authorization.d.entities.Team;
import com.volvo.gloria.common.c.dto.ProjectDTO;
import com.volvo.gloria.common.c.dto.RoleDTO;
import com.volvo.gloria.common.c.dto.SiteDTO;
import com.volvo.gloria.common.c.dto.TeamDTO;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.CompanyCode;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.paging.c.PageObject;

/**
 * Service class for User Operations and details.
 */
public interface UserServices {
    UserDTO getUser(String userId) throws GloriaApplicationException;

    List<RoleDTO> getUserRoles(String userId) throws GloriaApplicationException;

    List<String> getUserRoleIds(String userId) throws GloriaApplicationException;

    List<SiteDTO> getUserSites(String userId) throws GloriaApplicationException;

    List<String> getUserSiteIds(String userId);

    List<UserDTO> findUserByDeliveryFollowUpTeam(String loggedInUserIdFollowUpName) throws GloriaApplicationException;

    List<UserTransformerDTO> createUserData(String xmlContent);

    List<UserDTO> findUserByProcureTeam(String loggedInUserIdProcureTeam, String userId) throws GloriaApplicationException;

    List<RoleDTO> translateBaldoCategoriesToGloriaUserRole(List<String> baldoUserCategories) throws GloriaApplicationException;

    List<UserDTO> findAllUsersByTeamId(long teamOID) throws GloriaApplicationException;

    List<UserDTO> findUserByInternalProcureTeam(String loggedInUserIdInternalProcureTeam) throws GloriaApplicationException;
    
    List<Team> getTeams(String type);

    void createTeamData(String xmlContent);

    List<Team> findTeamsByName(String name, TeamType teamType);

    Team findTeamById(long teamOID);

    List<Team> updateUserTeams(String userId, String teamType, List<TeamDTO> teamDTOs) throws GloriaApplicationException;

    List<Team> getUserTeams(String userId, String teamType, String companyCode) throws GloriaApplicationException;

    PageObject getTeamUsers(PageObject pageObject, String teamType, String userCategory);
    
    /**
     * Validate if the user has access to the siven site.
     * @throws GloriaSystemException if the user does not have access to the site
     */
    void validateAccess(String userId, String siteId);

    List<String> getUserCompanyCodeCodes(String userId, String userTeam, String teamType) throws GloriaApplicationException;

    Team findTeamByName(String name, TeamType valueOf);

    List<UserDTO> findUserByTeam(String team, String type, String userCategory);

    List<CompanyCode> getUserCompanyCodes(String userId, String userTeam, String teamType, String filterStr) throws GloriaApplicationException;

    boolean authenticateUser(String username, String password) throws GloriaApplicationException;

    void initTeamUser(String aFileContent) throws GloriaApplicationException;

    List<UserDTO> findAllUsers();

    List<ProjectDTO> getTeamProjects(PageObject pageObject, String teamName, String teamType, String projectId);

    List<String> evaluateCompanyCodesForUser(String loggedInUser, String userTeam, String teamType) throws GloriaApplicationException;

}
