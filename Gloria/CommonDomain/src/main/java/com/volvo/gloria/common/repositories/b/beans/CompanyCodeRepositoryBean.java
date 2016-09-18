package com.volvo.gloria.common.repositories.b.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.common.d.entities.CompanyCode;
import com.volvo.gloria.common.d.entities.CompanyGroup;
import com.volvo.gloria.common.repositories.b.CompanyCodeRepository;
import com.volvo.gloria.util.c.dto.reports.ReportFilterDTO;
import com.volvo.gloria.util.paging.c.JpaAttribute;
import com.volvo.gloria.util.paging.c.JpaAttributeType;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * repository for root CompanyCode.
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class CompanyCodeRepositoryBean extends GenericAbstractRepositoryBean<CompanyCode, Long> implements CompanyCodeRepository {

    private static final String WILDCARD = "%";

    @PersistenceContext(unitName = "CommonDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    public CompanyCode findCompanyCodeByCode(String code) {
        CompanyCode companyCode = null;
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(CompanyCode.class);

        criteriaQuery.where(criteriaBuilder.equal(root.get("code"), code));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        List<CompanyCode> companyCodes = resultSetQuery.getResultList();
        if (companyCodes != null && !companyCodes.isEmpty()) {
            companyCode = companyCodes.get(0);
        }
        return companyCode;

    }

    @Override
    public List<CompanyCode> getCompanyCodes(List<String> companyCodes, String filterStr) {
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(CompanyCode.class);
        if (companyCodes != null) {
            predicatesRules.add(root.get("code").in(companyCodes));
        }

        if (!StringUtils.isEmpty(filterStr)) {
            String filterStrWildCard = filterStr.toLowerCase().trim().replaceFirst("\\*", WILDCARD).concat(WILDCARD);
            predicatesRules.add(criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.lower((Expression) root.get("name")), filterStrWildCard),
                                                   criteriaBuilder.like(criteriaBuilder.lower((Expression) root.get("code")), filterStrWildCard)));
        }
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);
        criteriaQueryForResultSet.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        Query queryForResultSets = entityManager.createQuery(criteriaQueryForResultSet);
        return queryForResultSets.getResultList();
    }

    @Override
    public CompanyGroup getCompanyGroupByCode(String companyCodeGroup) {
        CompanyGroup companyGroup = null;
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(CompanyGroup.class);

        criteriaQuery.where(criteriaBuilder.equal(root.get("code"), companyCodeGroup));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        List<CompanyGroup> companyGroups = resultSetQuery.getResultList();
        if (companyGroups != null && !companyGroups.isEmpty()) {
            companyGroup = companyGroups.get(0);
            // lazy load
            companyGroup.getCompanyCodes();
        }
        return companyGroup;
    }
    
    @Override
    public List<CompanyGroup> getCompanyGroupsByCompanyCodes(List<String> companyCodes) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(CompanyGroup.class);
        criteriaQuery.where(criteriaBuilder.in(root.get("companyCodes").get("code")).value(companyCodes));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        return resultSetQuery.getResultList();
    }

    @Override
    public CompanyGroup save(CompanyGroup companyGroup) {
        CompanyGroup toSave = companyGroup;
        if (companyGroup.getCompanyGroupOid() < 1) {
            getEntityManager().persist(companyGroup);
        } else {
            toSave = getEntityManager().merge(companyGroup);
        }
        return toSave;
    }

    @Override
    public PageObject getCompanyCodeFilters(PageObject pageObject, List<String> companyCodes) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("id", new JpaAttribute("code", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(CompanyCode.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.in(root.get("code")).value(companyCodes));

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
        List<CompanyCode> cCodes = queryForResultSets.getResultList();
        if (cCodes != null && !cCodes.isEmpty()) {
            List<ReportFilterDTO> reportCompanyCodeDTOs = new ArrayList<ReportFilterDTO>();
            for (CompanyCode code : cCodes) {
                ReportFilterDTO reportCompanyCodeDTO = new ReportFilterDTO();
                reportCompanyCodeDTO.setId(code.getCode());
                reportCompanyCodeDTO.setText(code.getName());
                reportCompanyCodeDTOs.add(reportCompanyCodeDTO);
            }
            pageObject.setGridContents(new ArrayList<PageResults>(reportCompanyCodeDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }
}
