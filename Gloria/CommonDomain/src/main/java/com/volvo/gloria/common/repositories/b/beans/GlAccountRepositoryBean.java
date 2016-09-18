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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.volvo.gloria.common.c.PaginatedArrayList;
import com.volvo.gloria.common.c.dto.GlAccountDTO;
import com.volvo.gloria.common.d.entities.GlAccount;
import com.volvo.gloria.common.repositories.b.GlAccountRepository;
import com.volvo.gloria.common.util.CommonHelper;
import com.volvo.gloria.util.paging.c.JpaAttribute;
import com.volvo.gloria.util.paging.c.JpaAttributeType;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;


/**
 * Service Implementations for GLAccountRepository.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class GlAccountRepositoryBean extends GenericAbstractRepositoryBean<GlAccount, Long> implements GlAccountRepository {

    @PersistenceContext(unitName = "CommonDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PageObject findGlAccountsByCompanyCode(String companyCode, String glAccountStr, PageObject pageObject) {

        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("accountName", new JpaAttribute("accountNumber,accountName", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();

        Root root = criteriaQuery.from(GlAccount.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("companyCode").get("code"), companyCode));

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
        List<GlAccountDTO> glAccountDTOs = CommonHelper.transformGlaccountEtyToDTO(queryForResultSets.getResultList());
        if (glAccountDTOs != null) {
            pageObject.setGridContents(new ArrayList<PageResults>(glAccountDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Set<String> filterInvalidGlAccounts(Set<String> glAccountList, String companyCode) {
        List<String> result = new ArrayList<String>();
        PaginatedArrayList<String> glAccountPaginatedList = new PaginatedArrayList<String>(glAccountList);
        EntityManager entityManager = getEntityManager();
        for (List<String> subListGlAccounts = null; (subListGlAccounts = glAccountPaginatedList.nextPage()) != null;) {
            Query query = entityManager.createNamedQuery("filterInvalidGlAccounts");
            query.setParameter("accountNos", subListGlAccounts);
            query.setParameter("companyCode", companyCode);
            
            result.addAll(query.getResultList());
        }
        return new HashSet<String>(result);
    }
}
