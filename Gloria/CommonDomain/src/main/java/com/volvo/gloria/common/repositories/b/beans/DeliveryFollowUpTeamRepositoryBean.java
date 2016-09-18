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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.common.c.FollowUpType;
import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamDTO;
import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamFilterDTO;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeamFilter;
import com.volvo.gloria.common.repositories.b.DeliveryFollowUpTeamRepository;
import com.volvo.gloria.common.util.CommonHelper;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.paging.c.JpaAttribute;
import com.volvo.gloria.util.paging.c.JpaAttributeType;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Repository class DeliveryFollowUpTeam.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class DeliveryFollowUpTeamRepositoryBean extends GenericAbstractRepositoryBean<DeliveryFollowUpTeam, Long> implements DeliveryFollowUpTeamRepository {

    @PersistenceContext(unitName = "CommonDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    public DeliveryFollowUpTeamFilter addDeliveryFollowUpTeamFilter(DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO, long deliveryFollowUpOid) {

        DeliveryFollowUpTeamFilter deliveryFollowUpTeamFilter = new DeliveryFollowUpTeamFilter();
        deliveryFollowUpTeamFilter.setSupplierId(setNullIfEmpty(deliveryFollowUpTeamFilterDTO.getSupplierId()));
        deliveryFollowUpTeamFilter.setProjectId(deliveryFollowUpTeamFilterDTO.getProjectId());
        deliveryFollowUpTeamFilter.setSuffix(setNullIfEmpty(deliveryFollowUpTeamFilterDTO.getSuffix()));
        deliveryFollowUpTeamFilter.setDeliveryControllerUserId(deliveryFollowUpTeamFilterDTO.getDeliveryControllerUserId());

        DeliveryFollowUpTeam deliveryFollowUpTeam = findDeliveryFollowUpTeamById(deliveryFollowUpOid);
        deliveryFollowUpTeamFilter.setDeliveryFollowUpTeam(deliveryFollowUpTeam);

        getEntityManager().persist(deliveryFollowUpTeamFilter);

        deliveryFollowUpTeam.getDeliveryFollowUpTeamFilters().add(deliveryFollowUpTeamFilter);
        return deliveryFollowUpTeamFilter;
    }

    @Override
    public List<DeliveryFollowUpTeamFilter> findAllDeliveryFollowUpTeamFilter() {
        TypedQuery<DeliveryFollowUpTeamFilter> query = getEntityManager().createNamedQuery("findAllDeliveryFollowUpTeamFilter",
                                                                                           DeliveryFollowUpTeamFilter.class);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public DeliveryFollowUpTeamDTO getDeliveryFollowupTeam(String deliveryFollowUpTeamName) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(DeliveryFollowUpTeam.class);
        List<Expression<Boolean>> predicates = new ArrayList<Expression<Boolean>>();
        
        predicates.add(root.get("name").in((Object[]) deliveryFollowUpTeamName.split(",")));
        
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        List<DeliveryFollowUpTeam> resultList = resultSetQuery.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            DeliveryFollowUpTeam deliveryFollowUpTeam = resultList.get(0);
            return CommonHelper.transformAsDeliveryFollowUpTeamDTO(deliveryFollowUpTeam);
        }
        return null;
    }

    @Override
    public List<DeliveryFollowUpTeamFilter> getDeliveryFollowUpTeamFilters(long deliveryFollowUpTeamOid) {
        TypedQuery<DeliveryFollowUpTeamFilter> query = getEntityManager().createNamedQuery("getDeliveryFollowupTeamFilterByDeliveryFollowUpTeamOid",
                                                                                           DeliveryFollowUpTeamFilter.class);
        query.setParameter("deliveryFollowUpTeamOid", deliveryFollowUpTeamOid);
        List<DeliveryFollowUpTeamFilter> dFollowUpTeamFilter = query.getResultList();
        if (dFollowUpTeamFilter != null && !dFollowUpTeamFilter.isEmpty()) {
            return dFollowUpTeamFilter;
        }
        return new ArrayList<DeliveryFollowUpTeamFilter>();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PageObject getDeliveryFollowUpTeamFilters(PageObject pageObject, long deliveryFollowUpTeamOid) {
        Map<String, JpaAttribute> deliveryFollowUpTeamFilterFields = new HashMap<String, JpaAttribute>();
        deliveryFollowUpTeamFilterFields.put("projectId", new JpaAttribute("projectId", JpaAttributeType.STRINGTYPE));
        deliveryFollowUpTeamFilterFields.put("supplierId", new JpaAttribute("supplierId", JpaAttributeType.STRINGTYPE));
        deliveryFollowUpTeamFilterFields.put("suffix", new JpaAttribute("suffix", JpaAttributeType.STRINGTYPE));
        deliveryFollowUpTeamFilterFields.put("name", new JpaAttribute("deliveryFollowUpTeam.name", JpaAttributeType.STRINGTYPE));
        deliveryFollowUpTeamFilterFields.put("deliveryControllerUserId", new JpaAttribute("deliveryControllerUserId", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(DeliveryFollowUpTeamFilter.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        if (deliveryFollowUpTeamOid > 0) {
            predicatesRules.add(criteriaBuilder.equal(root.get("deliveryFollowUpTeam").get("deliveryFollowUpTeamOid"), deliveryFollowUpTeamOid));
        }

        CriteriaQuery criteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                            deliveryFollowUpTeamFilterFields, true);
        Query countQuery = entityManager.createQuery(criteriaQueryFromPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                                     deliveryFollowUpTeamFilterFields, false);
        Query resultSetQuery = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());

        List<DeliveryFollowUpTeamFilter> deliveryFollowUpTeamFilters = resultSetQuery.getResultList();

        List<DeliveryFollowUpTeamFilterDTO> listOfDeliveryFollowUpTeamFilterDto 
                                                    = CommonHelper.transformAsDeliveryFollowUpTeamFilterDTO(deliveryFollowUpTeamFilters);

        if (listOfDeliveryFollowUpTeamFilterDto != null) {
            pageObject.setGridContents(new ArrayList<PageResults>(listOfDeliveryFollowUpTeamFilterDto));
        } else {
            pageObject.setGridContents(null);
        }

        return pageObject;
    }

    @Override
    public DeliveryFollowUpTeamFilter findDeliveryFollowUpTeamFiltersById(long id) {
        DeliveryFollowUpTeamFilter deliveryFollowUpTeamFilter = getEntityManager().find(DeliveryFollowUpTeamFilter.class, id);
        if (deliveryFollowUpTeamFilter == null) {
            throw new GloriaSystemException("This operation cannot be performed. No delivery followUp team filter objects exists for id : " + id,
                                            GloriaExceptionConstants.INVALID_DATASET_OID);
        }

        return deliveryFollowUpTeamFilter;
    }

    @Override
    public DeliveryFollowUpTeamFilter updateDeliveryFollowupTeamFilter(DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO)
            throws GloriaApplicationException {
        DeliveryFollowUpTeamFilter deliveryFollowUpTeamFilter = findDeliveryFollowUpTeamFiltersById(deliveryFollowUpTeamFilterDTO.getId());

        if (deliveryFollowUpTeamFilter.getVersion() != deliveryFollowUpTeamFilterDTO.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                 "This operation cannot be performed since the information seen in the page has already been updated.");
        }

        deliveryFollowUpTeamFilter.setSupplierId(setNullIfEmpty(deliveryFollowUpTeamFilterDTO.getSupplierId()));
        deliveryFollowUpTeamFilter.setProjectId(deliveryFollowUpTeamFilterDTO.getProjectId());
        deliveryFollowUpTeamFilter.setSuffix(setNullIfEmpty(deliveryFollowUpTeamFilterDTO.getSuffix()));
        deliveryFollowUpTeamFilter.setDeliveryControllerUserId(deliveryFollowUpTeamFilterDTO.getDeliveryControllerUserId());

        getEntityManager().merge(deliveryFollowUpTeamFilter);
        return deliveryFollowUpTeamFilter;
    }

    @Override
    public DeliveryFollowUpTeam updateDeliveryFollowupTeam(DeliveryFollowUpTeamDTO deliveryFollowUpTeamDTO) throws GloriaApplicationException {
        DeliveryFollowUpTeam deliveryFollowUpTeam = findDeliveryFollowUpTeamById(deliveryFollowUpTeamDTO.getId());

        if (deliveryFollowUpTeam == null) {
            throw new GloriaSystemException("This operation cannot be performed. No delivery followup team objects exists for id : "
                    + deliveryFollowUpTeamDTO.getId(), GloriaExceptionConstants.INVALID_DATASET_OID);
        }

        if (deliveryFollowUpTeam.getVersion() != deliveryFollowUpTeamDTO.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                 "This operation cannot be performed since the information seen in the page has already been updated.");
        }

        deliveryFollowUpTeam.setName(deliveryFollowUpTeamDTO.getName());
        deliveryFollowUpTeam.setFollowUpType(FollowUpType.valueOf(deliveryFollowUpTeamDTO.getFollowUpType()));
        deliveryFollowUpTeam.setDefaultDCUserId(deliveryFollowUpTeamDTO.getDefaultDcUserid());

        return save(deliveryFollowUpTeam);
    }

    @Override
    public DeliveryFollowUpTeamFilter getDeliveryFollowUpTeamFilter(long deliveryFollowUpTeamFilterOId) {
        DeliveryFollowUpTeamFilter deliveryFollowUpTeamFilter = getEntityManager().find(DeliveryFollowUpTeamFilter.class, deliveryFollowUpTeamFilterOId);
        if (deliveryFollowUpTeamFilter == null) {
            throw new GloriaSystemException("This operation cannot be performed. No delivery followup team filter objects exists for id : "
                    + deliveryFollowUpTeamFilterOId, GloriaExceptionConstants.INVALID_DATASET_OID);
        }

        return deliveryFollowUpTeamFilter;
    }

    @Override
    public void deleteDeliveryFollowupTeamFilter(long deliveryFollowUpTeamFilterId) {
        DeliveryFollowUpTeamFilter deliverFollowUpTeamFilter = getDeliveryFollowUpTeamFilter(deliveryFollowUpTeamFilterId);
        getEntityManager().remove(deliverFollowUpTeamFilter);
    }

    @Override
    public DeliveryFollowUpTeamFilter checkSupplierDeliveryFollowupTeamFilter(String suffix, String supplierId) throws GloriaApplicationException {
        TypedQuery<DeliveryFollowUpTeamFilter> query = getEntityManager().createNamedQuery("findSupplierDeliveryFollowupTeamFilter",
                                                                                           DeliveryFollowUpTeamFilter.class);
        query.setParameter("suffix", suffix);
        query.setParameter("supplierId", supplierId);
        List<DeliveryFollowUpTeamFilter> dFollowUpTeamFilter = query.getResultList();
        if (dFollowUpTeamFilter != null && !dFollowUpTeamFilter.isEmpty()) {
            return dFollowUpTeamFilter.get(0);
        }
        return null;
    }

    @Override
    public DeliveryFollowUpTeamFilter checkProjectDeliveryFollowupTeamFilter(String projectId) throws GloriaApplicationException {
        TypedQuery<DeliveryFollowUpTeamFilter> query = getEntityManager().createNamedQuery("findProjectDeliveryFollowupTeamFilter",
                                                                                           DeliveryFollowUpTeamFilter.class);
        query.setParameter("projectId", projectId);
        List<DeliveryFollowUpTeamFilter> dFollowUpTeamFilter = query.getResultList();
        if (dFollowUpTeamFilter != null && !dFollowUpTeamFilter.isEmpty()) {
            return dFollowUpTeamFilter.get(0);
        }
        return null;
    }

    private String setNullIfEmpty(String value) {
        if (!StringUtils.isEmpty(value)) {
            return value;
        }
        return null;
    }

    @Override
    public DeliveryFollowUpTeamFilter getProjectFilterDetailsById(Long deliveryFollowUpTeamFilterOid) {
        Query query = getEntityManager().createNamedQuery("getProjectFilterDetailsById");
        query.setParameter("deliveryFollowUpTeamFilterOid", deliveryFollowUpTeamFilterOid);
        DeliveryFollowUpTeamFilter dFollowUpTeamFilter = (DeliveryFollowUpTeamFilter) query.getSingleResult();
        if (dFollowUpTeamFilter != null) {
            return dFollowUpTeamFilter;
        }
        return null;
    }

    @Override
    public DeliveryFollowUpTeam save(DeliveryFollowUpTeam instanceToSave) {
        DeliveryFollowUpTeam toSave = instanceToSave;
        if (instanceToSave.getDeliveryFollowUpTeamOid() < 1) {
            getEntityManager().persist(instanceToSave);
        } else {
            toSave = getEntityManager().merge(instanceToSave);
        }
        return toSave;
    }

    @Override
    public DeliveryFollowUpTeam findDeliveryFollowUpTeamById(long deliveryFollowUpTeamOid) {
        return getEntityManager().find(DeliveryFollowUpTeam.class, deliveryFollowUpTeamOid);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<DeliveryFollowUpTeam> findAllDeliveryFollowUpTeam() {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(DeliveryFollowUpTeam.class);
        Query resultSetQuery = entityManager.createQuery(criteriaQuery.select(root));
        return resultSetQuery.getResultList();
    }
}
