package com.volvo.gloria.authorization.b.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.authorization.b.TeamUserTransformer;
import com.volvo.gloria.authorization.b.UserAuthorizationServices;
import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.authorization.b.UserTransformer;
import com.volvo.gloria.authorization.c.ApplicationSettingIdType;
import com.volvo.gloria.authorization.c.TeamType;
import com.volvo.gloria.authorization.c.dto.UserTransformerDTO;
import com.volvo.gloria.authorization.d.entities.Address;
import com.volvo.gloria.authorization.d.entities.ApplicationSetting;
import com.volvo.gloria.authorization.d.entities.GloriaUser;
import com.volvo.gloria.authorization.d.entities.Team;
import com.volvo.gloria.authorization.d.entities.UserApplication;
import com.volvo.gloria.authorization.d.entities.UserCategory;
import com.volvo.gloria.authorization.d.entities.UserCompanyCode;
import com.volvo.gloria.authorization.d.userrole.UserRoleHelper;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.b.TeamTransformer;
import com.volvo.gloria.common.c.dto.ProjectDTO;
import com.volvo.gloria.common.c.dto.RoleDTO;
import com.volvo.gloria.common.c.dto.SiteDTO;
import com.volvo.gloria.common.c.dto.TeamDTO;
import com.volvo.gloria.common.c.dto.TeamTransformerDTO;
import com.volvo.gloria.common.c.dto.TeamUserTransformerDTO;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.CompanyCode;
import com.volvo.gloria.common.d.entities.Site;
import com.volvo.gloria.common.repositories.b.CompanyCodeRepository;
import com.volvo.gloria.common.repositories.b.TeamRepository;
import com.volvo.gloria.common.util.BaldoCategoryToGloriaUserRoleTransformer;
import com.volvo.gloria.common.util.CommonHelper;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.gloria.util.c.UniqueItems;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Implementation class for UserServices.
 */
@ContainerManaged(name = "userServices")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UserServicesBean implements UserServices {
    private static final String COMMA = ",";

    private static final String TRUE = "true";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServicesBean.class);
    
    @Inject
    private UserAuthorizationServices userAuthorizationServices;

    @Inject
    private UserTransformer userTransformer;

    @Inject
    private CommonServices commonServices;

    @Inject
    private TeamRepository teamRepository;

    @Inject
    private TeamTransformer teamTransformer;
    
    @Inject
    private CompanyCodeRepository companyCodeRepository;
    
    @Inject
    private TeamUserTransformer teamUserTransformer;

    @Override
    public UserDTO getUser(String userId) throws GloriaApplicationException {
        GloriaUser gloriaUser = null;
        if (!StringUtils.isEmpty(userId)) {
            gloriaUser = userAuthorizationServices.findUserByUserId(userId.toUpperCase());
            //Only used for Go Live - can be removed after that
            if (gloriaUser != null && gloriaUser.hasDisabledLogin()) {
                gloriaUser = null;
            }
        }
        return transformToUserDTO(gloriaUser);
    }

    private UserDTO transformToUserDTO(GloriaUser gloriaUser) {
        UserDTO userDTO = new UserDTO();
        if (gloriaUser != null) {
            userDTO.setId(gloriaUser.getUserID());
            userDTO.setFirstName(gloriaUser.getUserFirstName());
            userDTO.setLastName(gloriaUser.getUserLastName());

            List<Address> addresses = gloriaUser.getAddress();

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                userDTO.setEmail(address.getEmail());
                userDTO.setPhoneNo(address.getPhoneNo());
                userDTO.setDepartament(address.getDepartment());
            }

            userDTO.setLanguageCode(gloriaUser.getLangCode());
            userDTO.setCountryCode(gloriaUser.getCountryCode());

            setOrganisationApplicationDetails(gloriaUser, userDTO);
            Set<Team> teams = gloriaUser.getTeams();
            userDTO.setDelFollowUpTeam(evaluateTeams(teams, TeamType.DELIVERY_CONTROL));
            userDTO.setProcureTeam(evaluateTeams(teams, TeamType.MATERIAL_CONTROL));
            userDTO.setInternalProcureTeam(evaluateTeams(teams, TeamType.INTERNAL_PROCURE)); 
            
            List<UserCompanyCode> userCompanyCodes = gloriaUser.getUserCompanyCodes();
            for (UserCompanyCode userCompanyCode : userCompanyCodes) {
                userDTO.getCompanyGroupCodes().add(userCompanyCode.getCode());
            }
        }
        return userDTO;
    }

    private void setOrganisationApplicationDetails(GloriaUser user, UserDTO userDTO) {
        List<UserApplication> userApplications = user.getUserApplication();

        for (UserApplication organisationApplication : nullSafe(userApplications)) {
            if (organisationApplication.getApplicationDescription().equals(GloriaParams.APPLICATION_ID)) {
                List<ApplicationSetting> applicationSettings = organisationApplication.getApplicationSetting();
                for (ApplicationSetting applicationSetting : applicationSettings) {
                    setApplicationSetting(userDTO, applicationSetting);
                }
            }
        }
    }

    private void setApplicationSetting(UserDTO userDTO, ApplicationSetting applicationSetting) {
        String applicationSettingID = applicationSetting.getApplicationSettingID();
        if (applicationSettingID.equals(ApplicationSettingIdType.LIMITED.name())) {
            boolean limitedAccess = false;
            if (applicationSetting.getApplicationSettingValue().equalsIgnoreCase(TRUE)) {
                limitedAccess = true;
            }
            userDTO.setLimitedAccess(limitedAccess);
        } else if (applicationSettingID.equals(ApplicationSettingIdType.WHSITE.name())) {
            if (userDTO.getWhSite() == null) {
                userDTO.setWhSite(applicationSetting.getApplicationSettingValue());
            } else {
                userDTO.setWhSite(userDTO.getWhSite() + COMMA + applicationSetting.getApplicationSettingValue());
            }
        }
    }

    /**
     * Returns the roles of a user.
     * 
     * @param userId
     *            - Check if this is the currently logged in user, otherwise do not return anything
     * @throws GloriaApplicationException
     * @return List<RoleDTO>
     */
    @Override
    public List<RoleDTO> getUserRoles(String userId) throws GloriaApplicationException {
        List<String> baldoCategories = new ArrayList<String>();
        GloriaUser user = userAuthorizationServices.findUserByUserId(userId.toUpperCase());
        if (user == null) {
            throw new GloriaApplicationException("User not found in Gloria " + userId, GloriaExceptionConstants.USER_NOT_FOUND);
        }
        List<UserApplication> userApplications = user.getUserApplication();
        if (userApplications != null && userApplications.size() == 1) {
            UserApplication organisationApplication = userApplications.get(0);

            List<UserCategory> userCategories = organisationApplication.getUserCategory();
            for (UserCategory userCategory : userCategories) {
                baldoCategories.add(userCategory.getUserCategoryID());
            }
        }

        return translateBaldoCategoriesToGloriaUserRole(baldoCategories);
    }

    /**
     * To get list of user roles of given VCN/User id. This method will be used in conjuction with setAuthentication method in SecurityServices. {@inheritDoc}
     */
    @Override
    public List<String> getUserRoleIds(String userId) throws GloriaApplicationException {
        List<RoleDTO> roleDTOs = getUserRoles(userId.toUpperCase());
        List<String> gloriaRoles = new ArrayList<String>();
        for (RoleDTO roleDTO : roleDTOs) {
            gloriaRoles.add(roleDTO.getId());
        }
        gloriaRoles.add("USER");
        return gloriaRoles;
    }

    /**
     * Returns the sites of a user.
     * 
     * @param userId
     *            - Check if this is the currently logged in user, otherwise do not return anything
     * @throws GloriaApplicationException
     * @return List<SiteDTO>
     */
    @Override
    public List<SiteDTO> getUserSites(String userId) throws GloriaApplicationException {
        if (UserRoleHelper.isItSupport(userAuthorizationServices.findUserByUserId(userId), commonServices)) {
            return CommonHelper.transforToSiteDTOs(commonServices.getAllSites());
        } else {
            List<String> siteIds = getUserSiteIds(userId);
            return commonServices.getSite(siteIds);
        }
    }

    /**
     * Returns the ID's of a user's sites.
     * 
     * @param userId
     *            - Check if this is the currently logged in user, otherwise do not return anything
     * @throws GloriaApplicationException
     * @return List<SiteDTO>
     */
    @Override
    public List<String> getUserSiteIds(String userId) {
        List<String> siteIds = new ArrayList<String>();
        List<ApplicationSetting> applicationSettings = userAuthorizationServices.getApplicationSettings(userId.toUpperCase());
        for (ApplicationSetting applicationSetting : applicationSettings) {
            siteIds.add(applicationSetting.getApplicationSettingValue());
        }

        return siteIds;
    }

    @Override
    public List<RoleDTO> translateBaldoCategoriesToGloriaUserRole(List<String> baldoUserCategories) throws GloriaApplicationException {
        Map<String, List<RoleDTO>> userRoleMap = BaldoCategoryToGloriaUserRoleTransformer.getInstance().getGloriaUserRolesMap();
        List<RoleDTO> assignedGloriaRolelist = new ArrayList<RoleDTO>();

        for (String baldoCategoryType : baldoUserCategories) {
            if (baldoCategoryType != null) {
                List<RoleDTO> userRoles = userRoleMap.get(baldoCategoryType.trim());
                if (userRoles != null) {
                    assignedGloriaRolelist.addAll(userRoles);
                }
            }
        }

        if (assignedGloriaRolelist == null || assignedGloriaRolelist.isEmpty()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.NO_GLORIA_ROLE_MAPPED_WITH_BALDO_CATEGORIES,
                                                 "No mapped Gloria User Role for given Baldo Categories.");
        }

        return assignedGloriaRolelist;
    }

    @Override
    public List<UserDTO> findUserByDeliveryFollowUpTeam(String loggedInUserIdFollowUpName) throws GloriaApplicationException {
        List<Team> teams = findTeamsByName(loggedInUserIdFollowUpName, TeamType.DELIVERY_CONTROL);
        List<UserDTO> userDTOs = new ArrayList<UserDTO>();
        for (Team team : nullSafe(teams)) {
            List<GloriaUser> users = userAuthorizationServices.getUsersByTeam(team.getTeamOid(), null);
            for (GloriaUser user : nullSafe(users)) {
                UserDTO userDTO = transformToUserDTO(user);
                userDTOs.add(userDTO);
            }
        }
        return userDTOs;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<UserTransformerDTO> createUserData(String xmlContent) {
        List<UserTransformerDTO> userOrganisationTypeDTOs = userTransformer.transformStoredUser(xmlContent);
        for (UserTransformerDTO userOrganisationTypeDTO : userOrganisationTypeDTOs) {
            userAuthorizationServices.addUser(userOrganisationTypeDTO);
        }
        return userOrganisationTypeDTOs;
    }

    @Override
    public List<UserDTO> findUserByProcureTeam(String loggedInUserIdProcureTeam, String userId) throws GloriaApplicationException {
        List<Team> teams = null;
        if (UserRoleHelper.isItSupport(userAuthorizationServices.findUserByUserId(userId), commonServices)) {
            teams = getTeams(TeamType.MATERIAL_CONTROL.name());
        } else {
            teams = findTeamsByName(loggedInUserIdProcureTeam, TeamType.MATERIAL_CONTROL);
        }
        List<UserDTO> userDTOs = new ArrayList<UserDTO>();
        for (Team team : nullSafe(teams)) {
            List<GloriaUser> users = userAuthorizationServices.getUsersByTeam(team.getTeamOid(), null);
            for (GloriaUser user : nullSafe(users)) {
                UserDTO userDTO = transformToUserDTO(user);
                userDTOs.add(userDTO);
            }
        }
        return userDTOs;
    }

    @Override
    public List<UserDTO> findAllUsersByTeamId(long teamOID) throws GloriaApplicationException {
        List<GloriaUser> users = userAuthorizationServices.getUsersByTeam(teamOID, null);
        List<UserDTO> userDTOs = new ArrayList<UserDTO>();
        for (GloriaUser user : nullSafe(users)) {
            UserDTO userDTO = transformToUserDTO(user);
            userDTOs.add(userDTO);
        }
        return userDTOs;
    }

    @Override
    public List<UserDTO> findUserByInternalProcureTeam(String loggedInUserIdInternalProcureTeam) throws GloriaApplicationException {
        List<Team> teams = findTeamsByName(loggedInUserIdInternalProcureTeam, TeamType.INTERNAL_PROCURE);
        List<UserDTO> userDTOs = new ArrayList<UserDTO>();
        for (Team team : nullSafe(teams)) {
            List<GloriaUser> users = userAuthorizationServices.getUsersByTeam(team.getTeamOid(), null);
            for (GloriaUser user : nullSafe(users)) {
                UserDTO userDTO = transformToUserDTO(user);
                userDTOs.add(userDTO);
            }
        }
        return userDTOs;
    }

    @Override
    public void createTeamData(String xmlContent) {
        List<TeamTransformerDTO> teamTransformerDTOs = teamTransformer.transformTeams(xmlContent);
        for (TeamTransformerDTO teamTransformerDTO : teamTransformerDTOs) {
            Team team = new Team();
            team.setName(teamTransformerDTO.getName());
            team.setType(TeamType.valueOf(teamTransformerDTO.getType()));
            team.setCompanyCodeGroup(teamTransformerDTO.getCompanyCodeGroup());
            teamRepository.save(team);
        }
    }

    @Override
    public List<Team> findTeamsByName(String name, TeamType teamType) {
        return teamRepository.findTeamsByName(name, teamType);
    }
    
    @Override
    public Team findTeamByName(String name, TeamType teamType) {
        return teamRepository.findTeamByName(name, teamType);
    }

    @Override
    public Team findTeamById(long teamOID) {
        return teamRepository.findById(teamOID);
    }

    @Override
    public PageObject getTeamUsers(PageObject pageObject, String teamType, String userCategory) {
        return teamRepository.getTeamUsers(pageObject, TeamType.valueOf(teamType), userCategory);
    }

    @Override
    public List<Team> getTeams(String teamType) {
        return teamRepository.findAllTeams(TeamType.valueOf(teamType));
    }

    @Override
    public List<Team> updateUserTeams(String userId, String teamType, List<TeamDTO> teamDTOs) throws GloriaApplicationException {
        List<Team> teams = new ArrayList<Team>();
        List<Team> existingUserTeams = teamRepository.findTeams(userId, TeamType.valueOf(teamType));
        GloriaUser gloriaUser = userAuthorizationServices.findUserByUserId(userId);
        // remove all existing teams of type TeamType for user
        gloriaUser.getTeams().removeAll(existingUserTeams);
        // remove the user from the existing teams of type TeamType
        for (Team team : existingUserTeams) {
            team.getUsers().remove(gloriaUser);
        }
        // update users with incoming teams
        for (TeamDTO teamDTO : nullSafe(teamDTOs)) {
            Team team = teamRepository.findById(teamDTO.getId());
            team.getUsers().add(gloriaUser);
            teams.add(team);
        }
        return teams;
    }

    @Override
    public List<Team> getUserTeams(String userId, String teamType, String companyCode) throws GloriaApplicationException {
        if (UserRoleHelper.isItSupport(userAuthorizationServices.findUserByUserId(userId), commonServices)) {
            if (!StringUtils.isEmpty(teamType)) {
                return teamRepository.findAllTeams(TeamType.valueOf(teamType));
            } else {
                return teamRepository.findAll();
            }
        } else {
            List<Team> userTeams = null;
            if (!StringUtils.isEmpty(teamType)) {
                userTeams = teamRepository.findTeams(userId, TeamType.valueOf(teamType));
                if (!StringUtils.isEmpty(companyCode)) {
                    return filterTeamsWithMatchingCompanyCode(companyCode, userTeams);
                }
            }
            return userTeams;
        }
    }

    private List<Team> filterTeamsWithMatchingCompanyCode(String companyCode, List<Team> userTeams) {
        List<Team> matchingUserTeams = new ArrayList<Team>();
        CompanyCode code = commonServices.findCompanyCodeByCode(companyCode);
        if (code != null) {
            for (Team team : nullSafe(userTeams)) {
                if (team.getCompanyCodeGroup().equals(code.getCompanyGroup().getCode())) {
                    matchingUserTeams.add(team);
                }
            }
        }
        return matchingUserTeams;
    }
    
    @Override
    public void validateAccess(String userId, String siteId) {
        List<String> siteIds = getUserSiteIds(userId);
        if (siteId != null && !siteIds.isEmpty() && !siteIds.contains(siteId)) {
            new GloriaSystemException("User " + userId + " is not authorized for site " + siteId, GloriaExceptionConstants.INVALID_WAREHOUSE_USER);
        }
    }
    
    @Override
    public List<String> getUserCompanyCodeCodes(String userId, String userTeam, String teamType) throws GloriaApplicationException {
        List<String> companyGroups = new ArrayList<String>();
        if (!StringUtils.isEmpty(userId)) {
            GloriaUser gloriaUser = userAuthorizationServices.findUserByUserId(userId);
            Set<Team> userTeams = null;
            if(gloriaUser!=null){
                userTeams = gloriaUser.getTeams();                
            }
            if (userTeams == null || userTeams.isEmpty()) {
                return new ArrayList<String>(UserRoleHelper.getAllUserCompanyCodeCodes(gloriaUser, commonServices));
            }
            for (Team team : userTeams) {
                if (!companyGroups.contains(team.getCompanyCodeGroup()) && (StringUtils.isEmpty(teamType) || team.getType() == TeamType.valueOf(teamType))
                        && (StringUtils.isEmpty(userTeam) || hasUserTeam(userTeam, team.getName()))) {
                    companyGroups.add(team.getCompanyCodeGroup());
                }
            }
        }
        return commonServices.getCompanyCodeCodes(companyGroups);
    }

    private boolean hasUserTeam(String userTeam, String teamToMatch) {
        if (!StringUtils.isEmpty(userTeam)) {
            List<String> teams = Arrays.asList(userTeam.split(","));
            if (teams != null && teams.contains(teamToMatch)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public List<CompanyCode> getUserCompanyCodes(String userId, String userTeam, String teamType, String filterStr) throws GloriaApplicationException {
        return evaluateUserCompanyCodes(userId, userTeam, teamType, filterStr);
    }
    
    private List<CompanyCode> evaluateUserCompanyCodes(String userId, String userTeam, String teamType, String filterStr) throws GloriaApplicationException {
        List<String> companyCodes = null;
        if (!StringUtils.isEmpty(userId)) {
            companyCodes = getUserCompanyCodeCodes(userId, userTeam, teamType);
        }
        return companyCodeRepository.getCompanyCodes(companyCodes, filterStr);
    }
    
    private String evaluateTeams(Set<Team> teams, TeamType teamType) {
        if (teams != null && !teams.isEmpty()) {
            Iterator<Team> setIterator = teams.iterator();
            UniqueItems userTeams = new UniqueItems();
            while (setIterator.hasNext()) {
                Team teamElement = setIterator.next();
                if (teamElement != null && teamElement.getType() == teamType) {
                    userTeams.add(teamElement.getName());
                }
            }
            return userTeams.createCommaSeparatedKey();
        }
        return "";
    }
    
    @Override
    public List<UserDTO> findUserByTeam(String teamName, String type, String userCategory) {
        TeamType teamType = null;
        if (!StringUtils.isEmpty(type)) {
            teamType = TeamType.valueOf(type);
        }
        List<Team> teams = findTeamsByName(teamName, teamType);
        List<UserDTO> userDTOs = new ArrayList<UserDTO>();
        for (Team team : nullSafe(teams)) {
            List<GloriaUser> users = userAuthorizationServices.getUsersByTeam(team.getTeamOid(), userCategory);
            for (GloriaUser user : nullSafe(users)) {
                UserDTO userDTO = transformToUserDTO(user);
                userDTOs.add(userDTO);
            }
        }
        return userDTOs;
    }
    
    @Override
    public boolean authenticateUser(String username, String password) throws GloriaApplicationException {
        Map<String, String> ldapEnvironment = new Hashtable<String, String>();
        ldapEnvironment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        ldapEnvironment.put(Context.SECURITY_AUTHENTICATION, "simple");
        ldapEnvironment.put(Context.PROVIDER_URL, "ldap://ldap.got.volvo.net:636");
        ldapEnvironment.put(Context.SECURITY_PROTOCOL, "ssl");
        LOGGER.info("authenticateUser:userId=" + username);
        InitialLdapContext ctx = null;

        try {
            ctx = new InitialLdapContext((Hashtable<String, String>) ldapEnvironment, null);
            if (username != null) {
                ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, "VCN\\" + username);
            }
            if (password != null) {
                ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            } else {
                return false;
            }

            ctx.reconnect(null);
        } catch (AuthenticationException e) {
            LOGGER.info("AuthenticationException:Unable to authenticate user in VCD. User ID: " + username);            
            return false;
        } catch (NamingException e) {
            LOGGER.error(e.getMessage(), e);
            LOGGER.info("authenticateUser:NamingException:userId=" + username);

            return false;
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        LOGGER.info("authenticateUser is ok" + username); 
        return true;
    }
    
    @Override
    public void initTeamUser(String aFileContent) throws GloriaApplicationException {
        List<TeamUserTransformerDTO> teamUserTransformerDTOs = teamUserTransformer.transformTeamUser(aFileContent);
        for (TeamUserTransformerDTO userOrganisationTypeDTO : teamUserTransformerDTOs) {
            String userId = userOrganisationTypeDTO.getUserId();
            List<TeamTransformerDTO> teamDTOs = userOrganisationTypeDTO.getTeamTransformerDTOs();
            GloriaUser gloriaUser = userAuthorizationServices.findUserByUserId(userId.toUpperCase());
            for (TeamTransformerDTO teamTransformerDTO : teamDTOs) {
                Team team = this.findTeamByName(teamTransformerDTO.getName(), TeamType.valueOf(teamTransformerDTO.getType()));
                if (gloriaUser != null && team != null) {
                    team.getUsers().add(gloriaUser);
                    gloriaUser.getTeams().add(team);
                    teamRepository.save(team);
                }
            }

        }
    }

    @Override
    public List<UserDTO> findAllUsers() {
        List<UserDTO> userDTOs = new ArrayList<UserDTO>();
        List<GloriaUser> users = teamRepository.findAllUsers();
        for (GloriaUser user : nullSafe(users)) {
            UserDTO userDTO = transformToUserDTO(user);
            userDTOs.add(userDTO);
        }
        return userDTOs;
    }

    @Override
    public List<ProjectDTO> getTeamProjects(PageObject pageObject, String teamName, String teamType, String projectId) {
        Team team = teamRepository.findTeamByName(teamName, TeamType.valueOf(teamType));
        if (team != null) {
            List<String> companyCodes = commonServices.getCompanyCodeCodes(Arrays.asList(team.getCompanyCodeGroup()));
            return commonServices.getProjectsByCompanyCodes(pageObject, companyCodes, projectId);
        }
        return new ArrayList<ProjectDTO>();
    }

    @Override
    public List<String> evaluateCompanyCodesForUser(String loggedInUser, String userTeam, String teamType) throws GloriaApplicationException {
        List<String> companyCodes = new ArrayList<String>();

        // if user has a team setup
        GloriaUser gloriaUser = teamRepository.findUserByUserId(loggedInUser);
        Set<Team> userTeams = gloriaUser.getTeams();
        if (userTeams != null && !userTeams.isEmpty()) {
            companyCodes.addAll(getUserCompanyCodeCodes(loggedInUser, null, null));
        } else if (userHasWarehouseSite(loggedInUser)) {
            // if user has warehouse set up
            companyCodes.addAll(evaluateCompanyCodesForWhSite(loggedInUser));
        } else {
            // if user has company code set up
            UserDTO user = getUser(loggedInUser);
            companyCodes.addAll(commonServices.getCompanyCodeCodes(user.getCompanyGroupCodes()));
        }
        return companyCodes;
    }

    private List<String> evaluateCompanyCodesForWhSite(String loggedInUser) throws GloriaApplicationException {
        List<String> companyCodesFromWhsite = new ArrayList<String>();
        List<SiteDTO> siteDTOs = getUserSites(loggedInUser);
        if (siteDTOs != null && !siteDTOs.isEmpty()) {
            List<String> siteIds = new ArrayList<String>();
            for (SiteDTO siteDTO : siteDTOs) {
                siteIds.add(siteDTO.getSiteId());
            }
            List<Site> sites = commonServices.getSiteBySiteIds(siteIds);
            for (Site site : nullSafe(sites)) {
                companyCodesFromWhsite.add(site.getCompanyCode());
            }
        }
        return companyCodesFromWhsite;
    }

    private boolean userHasWarehouseSite(String loggedInUser) throws GloriaApplicationException {
        List<SiteDTO> siteDTOs = getUserSites(loggedInUser);
        if (siteDTOs != null && !siteDTOs.isEmpty()) {
            return true;
        }
        return false;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> List<T> nullSafe(List<T> list) {
        return list == null ? Collections.EMPTY_LIST : list;
    }

}
