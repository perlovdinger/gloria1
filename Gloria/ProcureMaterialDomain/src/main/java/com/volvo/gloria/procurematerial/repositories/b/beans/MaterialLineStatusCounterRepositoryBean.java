package com.volvo.gloria.procurematerial.repositories.b.beans;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.MaterialLineStatusCounter;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusCounterType;
import com.volvo.gloria.procurematerial.repositories.b.MaterialLineStatusCounterRepository;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MaterialLineStatusCounterRepositoryBean extends GenericAbstractRepositoryBean<MaterialLineStatusCounter, Long> 
    implements MaterialLineStatusCounterRepository {
    
    @PersistenceContext(unitName = "ProcureMaterialDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    public MaterialLineStatusCounter findById(long id) {
        return super.findById(id);
    }    

    @Override
    public List<MaterialLineStatusCounter> getCount(Date fromDate, Date toDate, String[] project, String[] warehouse, 
                                                    List<MaterialLineStatusCounterType> materialLineStatusCounterTypes) {        
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<MaterialLineStatusCounter> criteriaQuery = criteriaBuilder.createQuery(MaterialLineStatusCounter.class);
        Root<MaterialLineStatusCounter> root = criteriaQuery.from(MaterialLineStatusCounter.class);
        criteriaQuery.select(root);
        //add date
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        Path<Timestamp> path = root.get("date");
        if (fromDate != null) {
            predicatesRules.add(criteriaBuilder.greaterThanOrEqualTo(path, new Timestamp(fromDate.getTime())));
        }
        if (toDate != null) {
            predicatesRules.add(criteriaBuilder.lessThan(path, new Timestamp(toDate.getTime())));
        }
        if (project != null && project.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("projectId")).value(Arrays.asList(project)));
        }
        if (warehouse != null && warehouse.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("whSite")).value(Arrays.asList(warehouse)));
        }
        if (materialLineStatusCounterTypes != null && !materialLineStatusCounterTypes.isEmpty()) {
            predicatesRules.add(criteriaBuilder.in(root.get("type")).value(materialLineStatusCounterTypes));
        }        
        if (!predicatesRules.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        }
        TypedQuery<MaterialLineStatusCounter> query = 
                entityManager.createQuery(criteriaQuery.select(root).where(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        return query.getResultList();
    }
    
    @Override
    public MaterialLineStatusCounter createAndSave(MaterialLine materialLine, MaterialLineStatusCounterType materialLineStatusCounterType) {
        MaterialLineStatusCounter materialLineStatusCounter = new MaterialLineStatusCounter();
        materialLineStatusCounter.setDate(new Timestamp(System.currentTimeMillis()));
        if (materialLine.getDeliveryNoteLine() != null) {
            materialLineStatusCounter.setProjectId(materialLine.getDeliveryNoteLine().getProjectId());
        }
        materialLineStatusCounter.setWhSite(materialLine.getWhSiteId());
        materialLineStatusCounter.setType(materialLineStatusCounterType);
        return this.save(materialLineStatusCounter); 
    }
}
