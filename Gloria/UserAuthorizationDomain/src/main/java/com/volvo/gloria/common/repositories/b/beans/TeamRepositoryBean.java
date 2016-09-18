package com.volvo.gloria.common.repositories.b.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.authorization.c.ApplicationSettingIdType;
import com.volvo.gloria.authorization.c.TeamType;
import com.volvo.gloria.authorization.c.dto.AddressDTO;
import com.volvo.gloria.authorization.c.dto.ApplicationSettingDTO;
import com.volvo.gloria.authorization.c.dto.CategoryDTO;
import com.volvo.gloria.authorization.c.dto.UserApplicationDTO;
import com.volvo.gloria.authorization.c.dto.UserCompanyCodeDTO;
import com.volvo.gloria.authorization.c.dto.UserTransformerDTO;
import com.volvo.gloria.authorization.d.entities.Address;
import com.volvo.gloria.authorization.d.entities.ApplicationSetting;
import com.volvo.gloria.authorization.d.entities.GloriaUser;
import com.volvo.gloria.authorization.d.entities.Team;
import com.volvo.gloria.authorization.d.entities.UserApplication;
import com.volvo.gloria.authorization.d.entities.UserCategory;
import com.volvo.gloria.authorization.d.entities.UserCompanyCode;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.c.dto.reports.ReportDeliveryControllerIdDTO;
import com.volvo.gloria.common.repositories.b.TeamRepository;
import com.volvo.gloria.util.ActiveDirectory;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.gloria.util.c.dto.LDAPUserDTO;
import com.volvo.gloria.util.c.dto.reports.ReportFilterDTO;
import com.volvo.gloria.util.paging.c.JpaAttribute;
import com.volvo.gloria.util.paging.c.JpaAttributeType;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Repository class Team.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TeamRepositoryBean extends GenericAbstractRepositoryBean<Team, Long> implements TeamRepository {

    private static final String USER_CATEGORY_INTERNAL_PROCURER = "Internal_Procurer";

    private static final String USER_CATEGORY_DELIVERY_CONTROLLER = "Delivery_Controller";

    private static final String USER_CATEGORY_MATERIAL_CONTROLLER = "Material_Controller";

    private static final Logger LOGGER = LoggerFactory.getLogger(TeamRepositoryBean.class);

    private static final String HYPEN = "-";
    private static final String COMMA = ",";

    @PersistenceContext(unitName = "UserAuthorizationDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<Team> findAllTeams(TeamType type) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Team.class);
        Predicate predicate = criteriaBuilder.equal(root.get("type"), type);
        Query resultSetQuery = entityManager.createQuery(criteriaQuery.select(root).where(predicate));
        return resultSetQuery.getResultList();
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<Team> findTeamsByName(String name, TeamType teamType) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Team.class);
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(root.get("name").in((Object[]) name.split(",")));
        if (teamType != null) {
            predicatesRules.add(root.get("type").in(teamType));
        }
        Query resultSetQuery = entityManager.createQuery(criteriaQuery.select(root)
                                            .where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()]))));
        return resultSetQuery.getResultList();
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public GloriaUser findUserByUserId(String userId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(GloriaUser.class);
        Predicate predicate = criteriaBuilder.equal(root.get("userID"), userId);
        Query resultSetQuery = entityManager.createQuery(criteriaQuery.select(root).where(predicate));
        List<GloriaUser> users = resultSetQuery.getResultList();
        if (users != null && !users.isEmpty()) {
            return users.get(0);
        }
        return null;
    }

    @Override
    public GloriaUser getUserByUserId(String userId) throws GloriaApplicationException {
        GloriaUser result = findUserByUserId(userId);
        if (result == null) {
            LOGGER.error("No user exists for id : " + userId);
        }
        return result;
    }

    public void manageUsers(List<UserTransformerDTO> userTransformerDTOs) {
        if (userTransformerDTOs != null && !userTransformerDTOs.isEmpty()) {
            for (UserTransformerDTO userTransformerDTO : userTransformerDTOs) {

                if (userTransformerDTO.getCiAction().equals(GloriaParams.BALDO_USER_CIACTION_UPDATE)) {
                    updateUser(userTransformerDTO);
                } else if (userTransformerDTO.getCiAction().equals(GloriaParams.BALDO_USER_CIACTION_CREATE)) {
                    GloriaUser user = findUserByUserId(userTransformerDTO.getUserID().toUpperCase());
                    if (user == null) {
                        addUser(userTransformerDTO);
                    } else {
                        updateUser(userTransformerDTO);
                    }
                } else if (userTransformerDTO.getCiAction().equals(GloriaParams.BALDO_USER_CIACTION_DELETE)) {
                    inactiveUser(userTransformerDTO.getUserID());
                }
            }
        }
    }

    /**
     * to inActive/Soft removal of user from gloria.
     */
    private void inactiveUser(String userId) {
        GloriaUser user = findUserByUserId(userId.toUpperCase());
        user.setInactive(true);
        getEntityManager().merge(user);
    }

    /**
     * Update existing user configuration.
     */
    private void updateUser(UserTransformerDTO userTransformerDTO) {
        GloriaUser user = findUserByUserId(userTransformerDTO.getUserID().toUpperCase());
        if (user == null) {
            user = new GloriaUser();
        }        
        user = transformFromDTO(user, userTransformerDTO);
        getEntityManager().merge(user);
    }

    @SuppressWarnings("unchecked")
    private void updateTeamsOnUser(GloriaUser user, List<UserCategory> userCategories) {
        Set<Team> teams = user.getTeams();
        if (teams != null && !teams.isEmpty() && userCategories != null && !userCategories.isEmpty()) {
            updateTeams(user, CollectionUtils.select(userCategories, buildUserCategoryPredicate(USER_CATEGORY_MATERIAL_CONTROLLER)),
                        CollectionUtils.select(teams, buildTeamPredicate(TeamType.MATERIAL_CONTROL)));
            updateTeams(user, CollectionUtils.select(userCategories, buildUserCategoryPredicate(USER_CATEGORY_DELIVERY_CONTROLLER)),
                        CollectionUtils.select(teams, buildTeamPredicate(TeamType.DELIVERY_CONTROL)));
            updateTeams(user, CollectionUtils.select(userCategories, buildUserCategoryPredicate(USER_CATEGORY_INTERNAL_PROCURER)),
                        CollectionUtils.select(teams, buildTeamPredicate(TeamType.INTERNAL_PROCURE)));
        }
    }
    
    private void updateTeams(GloriaUser user, Collection<String> userCategory, Collection<Team> userTeams) {
        if ((userCategory == null || userCategory.isEmpty()) && (userTeams != null && !userTeams.isEmpty())) {
            user.getTeams().removeAll(userTeams);
            for (Team team : userTeams) {
                team.getUsers().remove(user);
            }
        }
    }

    private org.apache.commons.collections.Predicate buildTeamPredicate(final TeamType teamType) {
        return new org.apache.commons.collections.Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return ((Team) object).getType() == teamType;
            }
        };
    }

    private org.apache.commons.collections.Predicate buildUserCategoryPredicate(final String userCategoryId) {
        return new org.apache.commons.collections.Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return ((UserCategory) object).getUserCategoryID().equalsIgnoreCase(userCategoryId);
            }
        };
    }
    
    @Override
    public void removeUser(GloriaUser user) {
        getEntityManager().remove(user);
    }

    private GloriaUser transformFromDTO(GloriaUser user, UserTransformerDTO userTransformerDTO) {
        String userId = userTransformerDTO.getUserID().toUpperCase();
        user.setUserID(userId);
        user.setUserFirstName(userTransformerDTO.getUserFirstName());
        user.setUserLastName(userTransformerDTO.getUserLastName());
        user.setLangCode(userTransformerDTO.getLangCode());
        user.setCountryCode(userTransformerDTO.getCountryCode());
        user.setOrganisationID(userTransformerDTO.getOrganisationID());
        user.setOrganisationName(userTransformerDTO.getOrganisationName());

        try {
            LDAPUserDTO ldapUserDTO = ActiveDirectory.getLDAPUserData(userId);
            if (ldapUserDTO != null) {
                user.setDepartment(ldapUserDTO.getDepartment());
            }

        } catch (Exception e) {
            //Ignore not every user exist inte LDAP
        }

        List<AddressDTO> addressDTOs = userTransformerDTO.getAddress();
        List<Address> addresses = new ArrayList<Address>();
        for (AddressDTO addressDTO : addressDTOs) {
            Address address = new Address();
            addresses.add(address);

            address.setCity(addressDTO.getCity());
            address.setContactName(address.getContactName());
            address.setCountryCode(addressDTO.getCountryCode());
            address.setCountryName(addressDTO.getCountryName());
            address.setDepartment(addressDTO.getDepartment());
            address.setDistrictNo(addressDTO.getDistrictNo());
            address.setEmail(addressDTO.getEmail());
            address.setFaxNumber(addressDTO.getFaxNumber());
            address.setMobilePhoneNumber(addressDTO.getMobilePhoneNumber());
            address.setPhoneNo(addressDTO.getPhoneNo());
            address.setPoBox(addressDTO.getPoBox());
            address.setPostCode(addressDTO.getPostCode());
            address.setPostCodePOBox(addressDTO.getPostCodePOBox());
            address.setRegionID(addressDTO.getRegionID());
            address.setState(addressDTO.getState());
            address.setStreet1(addressDTO.getStreet1());
            address.setStreet2(addressDTO.getStreet2());
            address.setStreet3(addressDTO.getStreet3());
            address.setGloriaUser(user);
        }

        user.setAddress(addresses);
        setUserApplication(user, userTransformerDTO);

        List<UserCompanyCode> userCompanyCodes = new ArrayList<UserCompanyCode>();
        if (userTransformerDTO.getUserCompanyCodes() != null) {
            for (UserCompanyCodeDTO userCompanyCodeDTO : userTransformerDTO.getUserCompanyCodes()) {
                UserCompanyCode userCompanyCode = new UserCompanyCode();
                userCompanyCode.setCode(userCompanyCodeDTO.getCode());
                userCompanyCode.setGloriaUser(user);
                userCompanyCodes.add(userCompanyCode);
            }
            user.setUserCompanyCodes(userCompanyCodes);
        }
        return user;
    }

    private void setUserApplication(GloriaUser user, UserTransformerDTO userTransformerDTO) {
        List<UserApplication> userApplications = user.getUserApplication();
        if (userApplications != null) {
            for (UserApplication userApplication : userApplications) {
                getEntityManager().remove(userApplication);
            }
        }

        userApplications = new ArrayList<UserApplication>();

        List<UserApplicationDTO> organisationApplicationDTOs = userTransformerDTO.getOrganisationApplication();
        for (UserApplicationDTO organisationApplicationDTO : organisationApplicationDTOs) {
            UserApplication userApplication = new UserApplication();
            userApplications.add(userApplication);
            userApplication.setGloriaUser(user);

            userApplication.setApplicationDescription(organisationApplicationDTO.getApplicationDescription());
            userApplication.setApplicationID(organisationApplicationDTO.getApplicationID());

            List<ApplicationSettingDTO> applicationSettingDTOs = organisationApplicationDTO.getApplicationSetting();
            List<ApplicationSetting> applicationSettings = new ArrayList<ApplicationSetting>();
            for (ApplicationSettingDTO applicationSettingDTO : applicationSettingDTOs) {
                ApplicationSetting applicationSetting = new ApplicationSetting();
                applicationSettings.add(applicationSetting);

                applicationSetting.setApplicationSettingID(applicationSettingDTO.getApplicationSettingID());

                // Example WHSITE value: 1622 - PT SKO - Volvo Powertrain Corporation
                String baldoSettingValue = applicationSettingDTO.getApplicationSettingValue();
                String theValue = null;
                if (applicationSettingDTO.getApplicationSettingID().equals(ApplicationSettingIdType.WHSITE.name()) && baldoSettingValue.indexOf(HYPEN) > 0) {
                    String[] warehouseDescriptions = baldoSettingValue.split(HYPEN);
                    theValue = warehouseDescriptions[0].trim();
                } else {
                    theValue = baldoSettingValue;
                }

                applicationSetting.setApplicationSettingValue(theValue);
                applicationSetting.setUserApplication(userApplication);
            }
            userApplication.setApplicationSetting(applicationSettings);

            List<UserCategory> userCategories = new ArrayList<UserCategory>();
            List<CategoryDTO> userCategoryDTOs = organisationApplicationDTO.getUserCategory();
            for (CategoryDTO userCategoryDTO : userCategoryDTOs) {
                UserCategory userCategory = new UserCategory();
                userCategories.add(userCategory);

                userCategory.setUserCategoryID(userCategoryDTO.getId());
                userCategory.setUserCategoryDescription(userCategoryDTO.getUserCategoryDescription());
                userCategory.setEndTime(userCategoryDTO.getEndTime());
                userCategory.setUserApplication(userApplication);

            }
            userApplication.setUserCategory(userCategories);
            //update USER_TEAMS
            updateTeamsOnUser(user, userCategories);
        }
        user.setUserApplication(userApplications);

    }

    public void addUser(UserTransformerDTO userTransformerDTO) {
        try {
            GloriaUser user = new GloriaUser();
            user = transformFromDTO(user, userTransformerDTO);
            getEntityManager().persist(user);
        } catch (Exception e) {
            LOGGER.error("Could not create user " + userTransformerDTO.getUserID(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserCategory> getUserCategoryByUserOId(Long userOrganisationTypeOid) {
        Query query = getEntityManager().createNamedQuery("UserRolesQuery");
        query.setParameter("userOrganisationTypeOid", userOrganisationTypeOid);
        return query.getResultList();
    }

    @Override
    public List<String> getBaldoUserCategories(Long userOid) {
        List<UserCategory> categories = getUserCategoryByUserOId(userOid);
        List<String> baldoCategories = new ArrayList<String>();
        for (UserCategory userCategory : categories) {
            baldoCategories.add(userCategory.getUserCategoryDescription());
        }
        return baldoCategories;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ApplicationSetting> getApplicationSettings(String userId) {
        Query query = getEntityManager().createNamedQuery("getApplicationSettingByUserIdAndSettingType");
        query.setParameter("userId", userId);
        query.setParameter("applicationSettingId", ApplicationSettingIdType.WHSITE.name());
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<GloriaUser> getUsersByApplicationSettingValue(String applicationSettingValue) {
        Query query = getEntityManager().createNamedQuery("getUserByApplicationSettingValue");
        query.setParameter("applicationSettingValue", applicationSettingValue);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<GloriaUser> getUsersByTeam(long teamOID, String userCategory) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(GloriaUser.class);
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("teams").get("teamOid"), teamOID));
        
        if (!StringUtils.isEmpty(userCategory)) {
            predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower((Expression) root.get("userApplication").get("userCategory").get("userCategoryID")),
                                                      userCategory.toLowerCase()));
        }
        Query resultSetQuery = entityManager.createQuery(criteriaQuery.select(root)
                                                         .where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()]))));
        return resultSetQuery.getResultList();
    }

    @Override
    public GloriaUser save(GloriaUser user) {
        GloriaUser toSave = user;
        if (user.getId() == null || new Long(0).equals(user.getId())) {
            getEntityManager().persist(user);
        } else {
            toSave = getEntityManager().merge(user);
        }
        return toSave;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PageObject getTeamUsers(PageObject pageObject, TeamType teamType, String userCategory) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("id", new JpaAttribute("userID", JpaAttributeType.STRINGTYPE));
        fieldMap.put("userName", new JpaAttribute("userFirstName,userLastName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("teamNames", new JpaAttribute("teams.name", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(GloriaUser.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower((Expression) root.get("userApplication").get("userCategory").get("userCategoryID")),
                                                  userCategory.toLowerCase()));

        CriteriaQuery countCriteriaQueryFromPageObeject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap,
                                                                                true);
        Query countQuery = entityManager.createQuery(countCriteriaQueryFromPageObeject);
        Long value = (Long) countQuery.getSingleResult();
        pageObject.setCount(value.intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap,
                                                                                   false);
        Path<String> path = root.get("userID");
        resultSetCriteriaQueryFromPageObject.orderBy(criteriaBuilder.asc(path));

        Query queryForResultSets = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        queryForResultSets.setFirstResult(pageObject.getFirstResult());
        queryForResultSets.setMaxResults(pageObject.getMaxResult());

        List<GloriaUser> users = queryForResultSets.getResultList();

        List<UserDTO> userDTOs = new ArrayList<UserDTO>();
        if (users != null && !users.isEmpty()) {
            for (GloriaUser gloriaUser : users) {
                UserDTO userDTO = new UserDTO();
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
                Set<Team> teams = gloriaUser.getTeams();
                String teamInfo = getTeamsInfo(teams, teamType);
                if (teams != null && !teams.isEmpty() && !teamInfo.isEmpty()) {
                    userDTO.setTeamNames(getTeamsInfo(teams, teamType));
                }
                userDTOs.add(userDTO);
            }
        }
        pageObject.setGridContents(new ArrayList<PageResults>(userDTOs));
        return pageObject;
    }

    private String getTeamsInfo(Set<Team> teams, TeamType teamType) {
        Iterator<Team> setIterator = teams.iterator();
        StringBuilder sb = new StringBuilder();
        while (setIterator.hasNext()) {
            Team team = setIterator.next();
            if (team.getType() == teamType) {
                if (sb.length() > 0) {
                    sb.append(COMMA).append(" ");
                }
                sb.append(team.getName());
            }
        }
        return sb.toString();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Team> findTeams(String userId, TeamType type) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Team.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("users").get("userID"), userId));
        predicatesRules.add(root.get("type").in(type));
        Query resultSetQuery = entityManager.createQuery(criteriaQuery.select(root)
                                                                      .where(criteriaBuilder.and(predicatesRules.
                                                                                                 toArray(new Predicate[predicatesRules.size()]))));
        return resultSetQuery.getResultList();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Team findTeamByName(String name, TeamType teamType) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Team.class);
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("name"), name));
        predicatesRules.add(root.get("type").in(teamType));
        Query resultSetQuery = entityManager.createQuery(criteriaQuery.select(root)
                                                                      .where(criteriaBuilder.and(predicatesRules
                                                                                                 .toArray(new Predicate[predicatesRules.size()]))));
        List<Team> teams = resultSetQuery.getResultList();
        if (teams != null && !teams.isEmpty()) {
            return teams.get(0);
        }
        return null;
    }
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PageObject getDeliveryControllers(PageObject pageObject, List<String> teams, List<String> companyCodeGroups) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("id", new JpaAttribute("userID", JpaAttributeType.STRINGTYPE));
       
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(GloriaUser.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower((Expression) root.get("userApplication").get("userCategory").get("userCategoryID")),
                                                  USER_CATEGORY_DELIVERY_CONTROLLER.toLowerCase()));
        predicatesRules.add(criteriaBuilder.in(root.get("teams").get("companyCodeGroup")).value(companyCodeGroups));
        predicatesRules.add(criteriaBuilder.in(root.get("teams").get("name")).value(teams));
        
        CriteriaQuery countCriteriaQueryFromPageObeject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap,
                                                                                true);
        Query countQuery = entityManager.createQuery(countCriteriaQueryFromPageObeject);
        Long value = (Long) countQuery.getSingleResult();
        pageObject.setCount(value.intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap,
                                                                                   false);
        resultSetCriteriaQueryFromPageObject.orderBy(criteriaBuilder.asc(root.get("userID")));

        Query queryForResultSets = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        queryForResultSets.setFirstResult(pageObject.getFirstResult());
        queryForResultSets.setMaxResults(pageObject.getMaxResult());

        List<GloriaUser> users = queryForResultSets.getResultList();

        if (users != null && !users.isEmpty()) {
            List<ReportDeliveryControllerIdDTO> deliveryControllerIdDTOs = new ArrayList<ReportDeliveryControllerIdDTO>();
            for (GloriaUser gloriaUser : users) {
                ReportDeliveryControllerIdDTO reportDeliveryControllerIdDTO = new ReportDeliveryControllerIdDTO();
                reportDeliveryControllerIdDTO.setId(gloriaUser.getUserID());
                reportDeliveryControllerIdDTO.setText(gloriaUser.getUserID());
                reportDeliveryControllerIdDTO.setDeliveryControllerName(gloriaUser.getUserLastName().concat(" ").concat(gloriaUser.getUserFirstName()));
                deliveryControllerIdDTOs.add(reportDeliveryControllerIdDTO);
            }
            pageObject.setGridContents(new ArrayList<PageResults>(deliveryControllerIdDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PageObject getMaterialControllerTeams(PageObject pageObject) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("id", new JpaAttribute("name", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Team.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("type"), TeamType.MATERIAL_CONTROL));

        CriteriaQuery countCriteriaQueryFromPageObeject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap,
                                                                                true);
        Query countQuery = entityManager.createQuery(countCriteriaQueryFromPageObeject);
        Long value = (Long) countQuery.getSingleResult();
        pageObject.setCount(value.intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap,
                                                                                   false);
        Query queryForResultSets = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        queryForResultSets.setFirstResult(pageObject.getFirstResult());
        queryForResultSets.setMaxResults(pageObject.getMaxResult());

        List<Team> teams = queryForResultSets.getResultList();

        if (teams != null && !teams.isEmpty()) {
            List<ReportFilterDTO> reportMaterialControllerTeamDTOs = new ArrayList<ReportFilterDTO>();
            for (Team team : teams) {
                ReportFilterDTO materialControllerTeamDTO = new ReportFilterDTO();
                materialControllerTeamDTO.setId(team.getName());
                materialControllerTeamDTO.setText(team.getName());
                reportMaterialControllerTeamDTOs.add(materialControllerTeamDTO);
            }
            pageObject.setGridContents(new ArrayList<PageResults>(reportMaterialControllerTeamDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<GloriaUser> findAllUsers() {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(GloriaUser.class);
        Query resultSetQuery = entityManager.createQuery(criteriaQuery.select(root));
        return resultSetQuery.getResultList();
    }
    
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Team findTeamByCompanyCodeAndType(List<String> companyCodeGroups, TeamType teamType) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Team.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.in(root.get("companyCodeGroup")).value(companyCodeGroups));
        predicatesRules.add(criteriaBuilder.equal(root.get("type"), teamType));
        Query resultSetQuery = entityManager.createQuery(criteriaQuery.select(root)
                                            .where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()]))));
        List<Team> teams = resultSetQuery.getResultList();
        if (teams != null && !teams.isEmpty()) {
            return teams.get(0);
        }
        return null;
    }
}
