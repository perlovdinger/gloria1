package com.volvo.gloria.common.repositories.b.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.common.c.PaginatedArrayList;
import com.volvo.gloria.common.c.dto.ProjectDTO;
import com.volvo.gloria.common.c.dto.WbsElementDTO;
import com.volvo.gloria.common.d.entities.WbsElement;
import com.volvo.gloria.common.repositories.b.WbsElementRepository;
import com.volvo.gloria.common.util.CommonHelper;
import com.volvo.gloria.util.c.dto.reports.ReportFilterDTO;
import com.volvo.gloria.util.paging.c.JpaAttribute;
import com.volvo.gloria.util.paging.c.JpaAttributeType;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * repository for root WbsElement.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@SuppressWarnings({ "rawtypes", "unchecked" })
public class WbsElementRepositoryBean extends GenericAbstractRepositoryBean<WbsElement, Long> implements WbsElementRepository {

    private static final String WILDCARD = "%";

    @PersistenceContext(unitName = "CommonDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    public PageObject findWbsElementsByCompanyCodeAndProjectId(String companyCode, String projectId, String wbs, PageObject pageObject) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("wbs", new JpaAttribute("wbs", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();

        Root root = criteriaQuery.from(WbsElement.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("companyCode").get("code"), companyCode));

        if (!StringUtils.isEmpty(projectId)) {
            predicatesRules.add(criteriaBuilder.equal(root.get("projectId"), projectId));
        }
        CriteriaQuery countCriteriaQueryFromPageObeject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                                  fieldMap, true);
        Query countQuery = entityManager.createQuery(countCriteriaQueryFromPageObeject);
        Long value = (Long) countQuery.getSingleResult();
        pageObject.setCount(value.intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                                     fieldMap, false);
        Query queryForResultSets = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        Path<String> path = root.get("wbs");
        resultSetCriteriaQueryFromPageObject.orderBy(criteriaBuilder.asc(path));

        queryForResultSets.setFirstResult(pageObject.getFirstResult());
        queryForResultSets.setMaxResults(pageObject.getMaxResult());
        List<WbsElementDTO> wbsElementDTOs = CommonHelper.transformWbsElementEtyToDTO(queryForResultSets.getResultList());
        if (wbsElementDTOs != null) {
            pageObject.setGridContents(new ArrayList<PageResults>(wbsElementDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }

    @Override
    public WbsElement findWbsElementByCompanyCodeAndProjectId(String companyCode, String projectId, String wbs) {

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();

        Root root = criteriaQuery.from(WbsElement.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        if (!StringUtils.isEmpty(companyCode)) {
            predicatesRules.add(criteriaBuilder.equal(root.get("companyCode").get("code"), companyCode));
        }
        if (!StringUtils.isEmpty(projectId)) {
            predicatesRules.add(criteriaBuilder.equal(root.get("projectId"), projectId));
        }
        if (!StringUtils.isEmpty(wbs)) {
            predicatesRules.add(criteriaBuilder.equal(root.get("wbs"), wbs));
        }

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                                                               .where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        Query queryForResultSets = entityManager.createQuery(criteriaQueryForResultSet);
        List<WbsElement> wbsElements = queryForResultSets.getResultList();
        if (wbsElements != null && !wbsElements.isEmpty()) {
            return wbsElements.get(0);
        }
        return null;
    }

    @Override
    public List<ProjectDTO> getProjectsByCompanyCode(PageObject pageObject, List<String> companyCodes, String projectID) {
        Query query = getEntityManager().createNamedQuery("getProjectsByCompanyCode");
        query.setParameter("projectId", !StringUtils.isEmpty(projectID) ? projectID.toLowerCase().replaceAll("\\*", WILDCARD).concat(WILDCARD) : WILDCARD);
        query.setParameter("companyCodes", companyCodes);
        query.setMaxResults(pageObject.getResultsPerPage());
        List<String> result = query.getResultList();
        return transformToProjectDTO(result);
    }

    private List<ProjectDTO> transformToProjectDTO(List<String> result) {
        List<ProjectDTO> projectList = new ArrayList<ProjectDTO>();
        for (String projectID : result) {
            ProjectDTO projectDTO = new ProjectDTO();
            projectDTO.setProjectId(projectID);
            projectList.add(projectDTO);
        }
        return projectList;
    }

    @Override
    public List<WbsElement> findWbsElementByCompanyCode(String companyCode) {
        if (StringUtils.isEmpty(companyCode)) {
            return new ArrayList<WbsElement>();
        }
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();

        Root root = criteriaQuery.from(WbsElement.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("companyCode").get("code"), companyCode));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                                                               .where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        Query queryForResultSets = entityManager.createQuery(criteriaQueryForResultSet);
        return queryForResultSets.getResultList();
    }

    @Override
    public Set<String> filterInvalidWbsCodes(Set<String> wbsList, String companyCode) {
        List<String> result = new ArrayList<String>();
        PaginatedArrayList<String> wbsPaginatedList = new PaginatedArrayList<String>(wbsList);
        EntityManager entityManager = getEntityManager();
        for (List<String> subListWbs = null; (subListWbs = wbsPaginatedList.nextPage()) != null;) {
            Query query = entityManager.createNamedQuery("filterInvalidWbs");
            query.setParameter("wbs", subListWbs);
            query.setParameter("companyCode", companyCode);

            result.addAll(query.getResultList());
        }
        return new HashSet<String>(result);
    }
    
    @Override
    public PageObject getProjects(PageObject pageObject, List<String> companyCodes) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("id", new JpaAttribute("projectId", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(WbsElement.class);
        
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.in(root.get("companyCode").get("code")).value(companyCodes));

        CriteriaQuery resultCountCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root.get("projectId"), predicatesRules,
                                                                                     pageObject, fieldMap, true);
        Query countQuery = entityManager.createQuery(resultCountCriteriaQueryFromPageObject);
        Long value = (Long) countQuery.getSingleResult();
        pageObject.setCount(value.intValue());
        
        Selection<ReportFilterDTO> selection = criteriaBuilder.construct(ReportFilterDTO.class, root.get("projectId"), root.get("projectId"));
        
        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, selection, predicatesRules, pageObject,
                                                                                   fieldMap, false);
        Query queryForResultSets = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        queryForResultSets.setFirstResult(pageObject.getFirstResult());
        queryForResultSets.setMaxResults(pageObject.getMaxResult());
        List<ReportFilterDTO> reportProjectDTOs = queryForResultSets.getResultList();
        if (reportProjectDTOs != null && !reportProjectDTOs.isEmpty()) {
            pageObject.setCount(reportProjectDTOs.size());
            pageObject.setGridContents(new ArrayList<PageResults>(reportProjectDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }
    
    @Override
    public PageObject getWbsElements(PageObject pageObject, List<String> companyCodes) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("id", new JpaAttribute("wbs", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(WbsElement.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();

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
        List<WbsElement> wbsElements = queryForResultSets.getResultList();
        if (wbsElements != null && !wbsElements.isEmpty()) {
            List<ReportFilterDTO> reportWbsDTOs = new ArrayList<ReportFilterDTO>();
            for (WbsElement wbsElement : wbsElements) {
                ReportFilterDTO reportWBSDTO = new ReportFilterDTO();
                reportWBSDTO.setId(wbsElement.getWbs());
                reportWBSDTO.setText(wbsElement.getWbs());
                reportWbsDTOs.add(reportWBSDTO);
            }
            pageObject.setGridContents(new ArrayList<PageResults>(reportWbsDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }
}
