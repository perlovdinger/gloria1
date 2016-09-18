package com.volvo.gloria.common.repositories.b;

import java.util.List;

import com.volvo.gloria.authorization.c.TeamType;
import com.volvo.gloria.authorization.c.dto.UserTransformerDTO;
import com.volvo.gloria.authorization.d.entities.ApplicationSetting;
import com.volvo.gloria.authorization.d.entities.GloriaUser;
import com.volvo.gloria.authorization.d.entities.Team;
import com.volvo.gloria.authorization.d.entities.UserCategory;
import com.volvo.gloria.common.d.entities.CompanyGroup;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * Repository for Team.
 * 
 */
public interface TeamRepository extends GenericRepository<Team, Long> {

    List<Team> findAllTeams(TeamType type);

    List<Team> findTeamsByName(String team, TeamType teamType);

    GloriaUser findUserByUserId(String userId);

    GloriaUser getUserByUserId(String userId) throws GloriaApplicationException;

    void manageUsers(List<UserTransformerDTO> userOrganisationDTO);

    void addUser(UserTransformerDTO userOrganisationTypeDTO);

    List<UserCategory> getUserCategoryByUserOId(Long userOrganisationTypeOid);

    List<String> getBaldoUserCategories(Long userOrganisationTypeOid);

    List<ApplicationSetting> getApplicationSettings(String userId);

    List<GloriaUser> getUsersByApplicationSettingValue(String applicationSettingValue);

    void removeUser(GloriaUser userOrganisationType);

    List<GloriaUser> getUsersByTeam(long teamOID, String userCategory);

    GloriaUser save(GloriaUser user);

    PageObject getTeamUsers(PageObject pageObject, TeamType teamType, String userCategory);

    List<Team> findTeams(String userId, TeamType teamType);

    Team findTeamByName(String name, TeamType teamType);

    PageObject getDeliveryControllers(PageObject pageObject, List<String> teams, List<String> companyCodeGroups);

    PageObject getMaterialControllerTeams(PageObject pageObject);

    List<GloriaUser> findAllUsers();

    Team findTeamByCompanyCodeAndType(List<String> companyCodeGroups, TeamType teamType);
}
