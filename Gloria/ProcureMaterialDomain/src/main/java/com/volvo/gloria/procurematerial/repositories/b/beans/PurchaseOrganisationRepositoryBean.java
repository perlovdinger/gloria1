package com.volvo.gloria.procurematerial.repositories.b.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CompoundSelection;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.volvo.gloria.procurematerial.c.dto.BuyerCodeDTO;
import com.volvo.gloria.procurematerial.d.entities.Buyer;
import com.volvo.gloria.procurematerial.d.entities.PurchaseOrganisation;
import com.volvo.gloria.procurematerial.repositories.b.PurchaseOrganisationRepository;
import com.volvo.gloria.util.c.dto.reports.ReportFilterDTO;
import com.volvo.gloria.util.paging.c.JpaAttribute;
import com.volvo.gloria.util.paging.c.JpaAttributeType;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * repository for root Buyer Code.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PurchaseOrganisationRepositoryBean extends GenericAbstractRepositoryBean<PurchaseOrganisation, Long> implements PurchaseOrganisationRepository {

    @PersistenceContext(unitName = "ProcureMaterialDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Buyer findBuyerByCode(String code) {
        Query query = getEntityManager().createNamedQuery("findBuyerByCode");
        query.setParameter("code", code);
        List<Buyer> buyers = query.getResultList();
        if (buyers != null && !buyers.isEmpty()) {
            return buyers.get(0);
        }
        return null;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Buyer findBuyerByCodeAndOrganisationCode(String code, String organisationCode) {
        Query query = getEntityManager().createNamedQuery("findBuyerByCodeAndPurchaseOrganisation");
        query.setParameter("code", code);
        query.setParameter("organisationCode", organisationCode);
        List<Buyer> buyers = query.getResultList();
        if (buyers != null && !buyers.isEmpty()) {
            return buyers.get(0);
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public PageObject findAllBuyers(PageObject pageObject, String organisationCode) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Buyer.class);

        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("buyerId", new JpaAttribute("code", JpaAttributeType.STRINGTYPE));

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("purchaseOrganisation").get("organisationCode"), organisationCode));

        CompoundSelection<BuyerCodeDTO> selection = criteriaBuilder.construct(BuyerCodeDTO.class, criteriaBuilder.max(root.get("buyerOid")), root.get("code"),
                                                                              root.get("name"));
        
        CriteriaQuery resultCountCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, selection, predicatesRules,
                                                                                     pageObject, fieldMap, true);
        resultCountCriteriaQueryFromPageObject.groupBy(root.get("code"), root.get("name"));
        Query countQuery = entityManager.createQuery(resultCountCriteriaQueryFromPageObject);
        List<BuyerCodeDTO> buyerCodeDTOsForCount = countQuery.getResultList();
        if (buyerCodeDTOsForCount != null && !buyerCodeDTOsForCount.isEmpty()) {
            pageObject.setCount(buyerCodeDTOsForCount.size());
        }

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, selection, predicatesRules, pageObject,
                                                                                   fieldMap, false);
        resultSetCriteriaQueryFromPageObject.groupBy(root.get("code"), root.get("name"));
        Query queryForResultSets = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        queryForResultSets.setFirstResult(pageObject.getFirstResult());
        queryForResultSets.setMaxResults(pageObject.getMaxResult());
        List<BuyerCodeDTO> buyerCodeDTOs = queryForResultSets.getResultList();
        if (buyerCodeDTOs != null && !buyerCodeDTOs.isEmpty()) {
            pageObject.setGridContents(new ArrayList<PageResults>(buyerCodeDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }

    @Override
    public Buyer save(Buyer buyer) {
        Buyer toSave = buyer;
        if (buyer.getBuyerOid() < 1) {
            getEntityManager().persist(buyer);
        } else {
            toSave = getEntityManager().merge(buyer);
        }
        return toSave;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<PurchaseOrganisation> findAll() {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(PurchaseOrganisation.class);
        CriteriaQuery allPurchaseOrganization = criteriaQuery.select(root).distinct(true).orderBy(criteriaBuilder.asc(root.get("organisationCode")));
        Query resultSetQuery = entityManager.createQuery(allPurchaseOrganization);
        return resultSetQuery.getResultList();
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public PurchaseOrganisation findPurchaseOrgByCode(String purchaseOrganisationCode) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(PurchaseOrganisation.class);

        if (purchaseOrganisationCode != null) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("organisationCode"), purchaseOrganisationCode));
        }

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root).distinct(true);

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        List<PurchaseOrganisation> purchaseOrganisations = resultSetQuery.getResultList();
        if (purchaseOrganisations != null && !purchaseOrganisations.isEmpty()) {
            return purchaseOrganisations.get(0);
        }
        return null;
    }

    @Override
    public List<Buyer> findAllBuyers() {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Buyer> criteriaQuery = criteriaBuilder.createQuery(Buyer.class);
        Root<Buyer> rootEntry = criteriaQuery.from(Buyer.class);
        CriteriaQuery<Buyer> all = criteriaQuery.select(rootEntry);
        TypedQuery<Buyer> allQuery = entityManager.createQuery(all);
        List<Buyer> buyers = allQuery.getResultList();
        if (buyers != null && !buyers.isEmpty()) {
            return buyers;
        }
        return new ArrayList<Buyer>();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PageObject findAllBuyers(PageObject pageObject) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("id", new JpaAttribute("code", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Buyer.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        CriteriaQuery resultCountCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root.get("code"), predicatesRules,
                                                                                     pageObject, fieldMap, true);
        Query countQuery = entityManager.createQuery(resultCountCriteriaQueryFromPageObject);
        Long value = (Long) countQuery.getSingleResult();
        pageObject.setCount(value.intValue());

        Selection<ReportFilterDTO> selection = criteriaBuilder.construct(ReportFilterDTO.class, root.get("code"), root.get("code"));

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, selection, predicatesRules, pageObject,
                                                                                   fieldMap, false);
        Query queryForResultSets = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        queryForResultSets.setFirstResult(pageObject.getFirstResult());
        queryForResultSets.setMaxResults(pageObject.getMaxResult());

        List<ReportFilterDTO> reportBuyersDTOs = queryForResultSets.getResultList();
        if (reportBuyersDTOs != null && !reportBuyersDTOs.isEmpty()) {
            pageObject.setGridContents(new ArrayList<PageResults>(reportBuyersDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }
}
