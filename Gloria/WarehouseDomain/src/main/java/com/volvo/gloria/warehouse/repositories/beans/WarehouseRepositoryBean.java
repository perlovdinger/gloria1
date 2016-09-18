package com.volvo.gloria.warehouse.repositories.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.OrderBy;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.c.PaginatedArrayList;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.dto.reports.ReportFilterDTO;
import com.volvo.gloria.util.paging.c.JpaAttribute;
import com.volvo.gloria.util.paging.c.JpaAttributeType;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.gloria.warehouse.b.WarehouseServicesHelper;
import com.volvo.gloria.warehouse.c.Deviation;
import com.volvo.gloria.warehouse.c.ZoneType;
import com.volvo.gloria.warehouse.c.dto.BinLocationDTO;
import com.volvo.gloria.warehouse.c.dto.BinlocationBalanceDTO;
import com.volvo.gloria.warehouse.c.dto.StorageRoomDTO;
import com.volvo.gloria.warehouse.c.dto.ZoneDTO;
import com.volvo.gloria.warehouse.d.entities.BaySetting;
import com.volvo.gloria.warehouse.d.entities.BinLocation;
import com.volvo.gloria.warehouse.d.entities.BinlocationBalance;
import com.volvo.gloria.warehouse.d.entities.Placement;
import com.volvo.gloria.warehouse.d.entities.Printer;
import com.volvo.gloria.warehouse.d.entities.QualityInspectionPart;
import com.volvo.gloria.warehouse.d.entities.QualityInspectionProject;
import com.volvo.gloria.warehouse.d.entities.QualityInspectionSupplier;
import com.volvo.gloria.warehouse.d.entities.StorageRoom;
import com.volvo.gloria.warehouse.d.entities.Warehouse;
import com.volvo.gloria.warehouse.d.entities.Zone;
import com.volvo.gloria.warehouse.repositories.b.WarehouseRepository;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * repository implementation for ROOT - Warehouse.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@SuppressWarnings({ "unchecked", "rawtypes" })
public class WarehouseRepositoryBean extends GenericAbstractRepositoryBean<Warehouse, Long> implements WarehouseRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(WarehouseRepositoryBean.class);

    @PersistenceContext(unitName = "WarehouseDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    public List<Warehouse> getWarehouseList() {
        Query query = getEntityManager().createNamedQuery("WarehouseListQuery");
        return query.getResultList();
    }

    @Override
    public void addStorageRoom(StorageRoom storageRoom) {
        getEntityManager().persist(storageRoom);
    }

    @Override
    public void addBinLocation(BinLocation binLocation) {
        getEntityManager().persist(binLocation);
    }

    @Override
    public void addPlacement(Placement placement) {
        getEntityManager().persist(placement);
    }

    @Override
    public Placement updatePlacement(Placement placement) {
        return getEntityManager().merge(placement);
    }

    @Override
    public BinLocation findBinLocationByID(Long binLocationId) {
        return getEntityManager().find(BinLocation.class, binLocationId);
    }
    
    @Override
    public Placement findPlacementByID(Long placementId) {
        return getEntityManager().find(Placement.class, placementId);
    }

    @Override
    public List<Placement> getPlacementByMaterialLine(Long id) {
        Query query;
        query = getEntityManager().createNamedQuery("getPlacementByMaterialLine");
        query.setParameter("materialLine", id);
        return query.getResultList();
    }

    @Override
    public List<StorageRoom> getStorageRooms() {
        Query query = getEntityManager().createNamedQuery("getStorageRoomsList");
        return query.getResultList();
    }

    @Override
    public PageObject getStorageRoomsByWarehouseId(PageObject pageObject, long warehouseOid) throws GloriaApplicationException {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("code", new JpaAttribute("code", JpaAttributeType.STRINGTYPE));
        fieldMap.put("name", new JpaAttribute("name", JpaAttributeType.STRINGTYPE));
        fieldMap.put("description", new JpaAttribute("description", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(StorageRoom.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("warehouse").get("warehouseOid"), warehouseOid));

        CriteriaQuery countCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap,
                                                                                 true);
        Query countQuery = entityManager.createQuery(countCriteriaQueryFromPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                                     fieldMap, false);
        Query resultSetQuery = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());
        List<StorageRoomDTO> storageRoomDTOs = WarehouseServicesHelper.transformAsStorageRoomDTOs(resultSetQuery.getResultList());

        if (!storageRoomDTOs.isEmpty()) {
            pageObject.setGridContents(new ArrayList<PageResults>(storageRoomDTOs));
        } else {
            pageObject.setGridContents(null);
        }

        return pageObject;
    }

    @Override
    public void deleteStorageRoomById(long storageRoomOid) throws GloriaApplicationException {
        StorageRoom storageRoom = findStorageRoomById(storageRoomOid);
        getEntityManager().remove(storageRoom);
    }

    @Override
    public PageObject getZones(PageObject pageObject, long storageRoomOid) throws GloriaApplicationException {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("code", new JpaAttribute("code", JpaAttributeType.STRINGTYPE));
        fieldMap.put("name", new JpaAttribute("name", JpaAttributeType.STRINGTYPE));
        fieldMap.put("description", new JpaAttribute("description", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Zone.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("storageRoom").get("storageRoomOid"), storageRoomOid));

        CriteriaQuery countCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap,
                                                                                 true);
        Query countQuery = entityManager.createQuery(countCriteriaQueryFromPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                                     fieldMap, false);
        Query resultSetQuery = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());
        List<ZoneDTO> zoneDTOs = WarehouseServicesHelper.transformAsZoneDTOs(resultSetQuery.getResultList());

        if (!zoneDTOs.isEmpty()) {
            pageObject.setGridContents(new ArrayList<PageResults>(zoneDTOs));
        } else {
            pageObject.setGridContents(null);
        }

        return pageObject;
    }

    @Override
    public StorageRoom findStorageRoomById(Long storageRoomOid) {
        StorageRoom storageRoom = null;

        storageRoom = getEntityManager().find(StorageRoom.class, storageRoomOid);
        return storageRoom;
    }

    @Override
    public BinLocation findBinLocationById(long binLocationOid) {
        BinLocation binLocation = null;
        binLocation = getEntityManager().find(BinLocation.class, binLocationOid);
        return binLocation;
    }

    @Override
    public void deleteBinLocation(long binLocationOid) {
        BinLocation binLocation = findBinLocationById(binLocationOid);
        getEntityManager().remove(binLocation);
    }

    @Override
    public StorageRoom updateStorageRoom(StorageRoom storageRoom) {
        return getEntityManager().merge(storageRoom);
    }

    @Override
    public Zone findZoneById(Long zoneOid) {
        Zone zone = null;
        zone = getEntityManager().find(Zone.class, zoneOid);
        return zone;
    }

    @Override
    public Zone updateZone(Zone zone) {
        return getEntityManager().merge(zone);
    }

    @Override
    public void deleteZone(Long zoneOid) {
        Zone zone = findZoneById(zoneOid);
        getEntityManager().remove(zone);
    }

    @Override
    public Zone findZoneWithAilesById(long zoneOid) {
        Query query = null;
        query = getEntityManager().createNamedQuery("findZoneWithAilesById");
        query.setParameter("zoneOid", zoneOid);
        return (Zone) query.getSingleResult();
    }

    @Override
    public Warehouse findWarehouseBySiteId(String siteId) {
        Query query = getEntityManager().createNamedQuery("getWarehouseBySiteId");
        query.setParameter("siteId", siteId);

        List<Warehouse> warehouses = query.getResultList();
        if (warehouses != null && !warehouses.isEmpty()) {
            return warehouses.get(0);
        }
        return null;
    }

    @Override
    public PageObject getAllBinLocationByWhSiteIdAndCode(String whSite, PageObject pageObject) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("zoneType", new JpaAttribute("zone.type", JpaAttributeType.STRINGTYPE));
        fieldMap.put("code", new JpaAttribute("code", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(BinLocation.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        Path<String> siteId = root.get("zone").get("storageRoom").get("warehouse").get("siteId");
        predicatesRules.add(criteriaBuilder.equal(siteId, whSite));

        CriteriaQuery countCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap,
                                                                               true);

        Query countQuery = entityManager.createQuery(countCriteriaQueryFromPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap,
                                                                                   false);
        TypedQuery<BinLocation> resultSetQuery = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        resultSetCriteriaQueryFromPageObject.orderBy(criteriaBuilder.asc(root.get("code")));

        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());
        List<BinLocationDTO> binLocationDTOs = WarehouseServicesHelper.transformAsBinlocationDTOs(resultSetQuery.getResultList());

        if (binLocationDTOs != null && !binLocationDTOs.isEmpty()) {
            pageObject.setGridContents(new ArrayList<PageResults>(binLocationDTOs));
        } else {
            pageObject.setGridContents(null);
        }

        return pageObject;
    }

    @Override
    public List<BinLocation> findPlacementBinLocations(long materialLineOID) {
        Query query = getEntityManager().createNamedQuery("findPlacementBinLocations");
        query.setParameter("materialLineOID", materialLineOID);

        return query.getResultList();
    }

    @Override
    public BinLocation findBinLocationByCode(String code, String whSiteId) {
        Query query = getEntityManager().createNamedQuery("findBinLocationByCode");
        query.setParameter("code", code);
        query.setParameter("whSiteId", whSiteId);
        List<BinLocation> binLocations = query.getResultList();
        if (binLocations != null && !binLocations.isEmpty()) {
            return binLocations.get(0);
        }
        return null;
    }
    
    @Override
    public List<Zone> findZonesByZoneTypeAndWhSiteId(String zoneType, String whSiteId) {
        Query query = getEntityManager().createNamedQuery("getZonesByZoneTypeAndWhSiteId");
        query.setParameter("siteId", whSiteId);
        query.setParameter("type", ZoneType.valueOf(zoneType.toUpperCase()));
        return query.getResultList();
    }

    @Override
    public List<Zone> getZones(long storageroomId, String zoneType) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Zone.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("storageRoom").get("storageRoomOid"), storageroomId));

        if (zoneType != null && !zoneType.isEmpty()) {
            predicatesRules.add(criteriaBuilder.equal(root.get("type"), ZoneType.valueOf(zoneType)));
        }

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                                                               .where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        return resultSetQuery.getResultList();
    }

    @Override
    public List<BaySetting> getBaySettings(long aislerackRowId) {
        Query query = getEntityManager().createNamedQuery("getBaySettingsForAisleRackRowId");
        query.setParameter("aisleRackRowOid", aislerackRowId);
        return query.getResultList();
    }

    @Override
    public Zone findZoneByWarehouseIdAndType(String type, Long warehouseOid) {
        Query query = getEntityManager().createNamedQuery("getZonesByWarehouseIdAndType");
        query.setParameter("warehouseId", warehouseOid);
        query.setParameter("type", ZoneType.valueOf(type));
        List<Zone> zones = query.getResultList();
        if (zones != null && !zones.isEmpty()) {
            return zones.get(0);
        }
        return null;
    }

    @Override
    public void addZone(Zone zone) {
        getEntityManager().persist(zone);
    }

    @Override
    public QualityInspectionPart save(QualityInspectionPart instanceToSave) {
        QualityInspectionPart toSave = instanceToSave;
        if (instanceToSave.getQualityInspectionPartOId() < 1) {
            LOGGER.debug("Creating new instance {} ...", instanceToSave);
            getEntityManager().persist(instanceToSave);
            LOGGER.debug("Creating new instance {} done.", instanceToSave);
        } else {
            LOGGER.debug("Updating {} ...", instanceToSave);
            toSave = getEntityManager().merge(instanceToSave);
            LOGGER.debug("Updating {} done.", instanceToSave);
        }
        return toSave;
    }

    @Override
    public QualityInspectionProject save(QualityInspectionProject instanceToSave) {
        QualityInspectionProject toSave = instanceToSave;
        if (instanceToSave.getQualityInspectionProjectOId() < 1) {
            LOGGER.debug("Creating new instance {} ...", instanceToSave);
            getEntityManager().persist(instanceToSave);
            LOGGER.debug("Creating new instance {} done.", instanceToSave);
        } else {
            LOGGER.debug("Updating {} ...", instanceToSave);
            toSave = getEntityManager().merge(instanceToSave);
            LOGGER.debug("Updating {} done.", instanceToSave);
        }
        return toSave;
    }

    @Override
    public QualityInspectionSupplier save(QualityInspectionSupplier instanceToSave) {
        QualityInspectionSupplier toSave = instanceToSave;
        if (instanceToSave.getQualityInspectionSupplierOId() < 1) {
            LOGGER.debug("Creating new instance {} ...", instanceToSave);
            getEntityManager().persist(instanceToSave);
            LOGGER.debug("Creating new instance {} done.", instanceToSave);
        } else {
            LOGGER.debug("Updating {} ...", instanceToSave);
            toSave = getEntityManager().merge(instanceToSave);
            LOGGER.debug("Updating {} done.", instanceToSave);
        }
        return toSave;
    }

    @Override
    public QualityInspectionPart findQualityInspectionPart(Long qualityInspectionPartOId) {
        QualityInspectionPart qualityInspectionPart = null;
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(QualityInspectionPart.class);

        List<Predicate> pridicateRules = new ArrayList<Predicate>();

        if (qualityInspectionPartOId != null) {
            pridicateRules.add(criteriaBuilder.equal(root.get("qualityInspectionPartOId"), qualityInspectionPartOId));
        }
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                                                               .where(criteriaBuilder.and(pridicateRules.toArray(new Predicate[pridicateRules.size()])));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        Object singleResult = resultSetQuery.getSingleResult();
        if (singleResult != null) {
            qualityInspectionPart = (QualityInspectionPart) singleResult;
        }
        return qualityInspectionPart;
    }

    @Override
    public List<QualityInspectionPart> findQualityInspectionPartByPartNameOrNumber(String partName, String partNumber, String whSiteId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(QualityInspectionPart.class);

        List<Predicate> predicateRules = new ArrayList<Predicate>();

        if (partName != null) {
            predicateRules.add(criteriaBuilder.equal(root.get("partName"), partName));
        }
        if (partNumber != null) {
            predicateRules.add(criteriaBuilder.equal(root.get("partNumber"), partNumber));
        }

        if (whSiteId != null) {
            predicateRules.add(criteriaBuilder.equal(root.get("warehouse").get("siteId"), whSiteId));
        }

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                                                               .where(criteriaBuilder.and(predicateRules.toArray(new Predicate[predicateRules.size()])));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();
    }

    @Override
    public QualityInspectionProject findQualityInspectionProject(long qualityInspectionProjectOId) {
        QualityInspectionProject qualityInspectionProject = null;
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(QualityInspectionProject.class);

        criteriaQuery.where(criteriaBuilder.equal(root.get("qualityInspectionProjectOId"), qualityInspectionProjectOId));
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        Object singleResult = resultSetQuery.getSingleResult();
        if (singleResult != null) {
            qualityInspectionProject = (QualityInspectionProject) singleResult;
        }
        return qualityInspectionProject;
    }

    @Override
    public QualityInspectionSupplier findQualityInspectionSupplier(long qualityInspectionSupplierOId) {
        QualityInspectionSupplier qualityInspectionSupplier = null;
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(QualityInspectionSupplier.class);

        criteriaQuery.where(criteriaBuilder.equal(root.get("qualityInspectionSupplierOId"), qualityInspectionSupplierOId));
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        Object singleResult = resultSetQuery.getSingleResult();
        if (singleResult != null) {
            qualityInspectionSupplier = (QualityInspectionSupplier) singleResult;
        }
        return qualityInspectionSupplier;
    }

    @Override
    public QualityInspectionProject findQualityInspectionProjectByProject(String projectName, String whSiteId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(QualityInspectionProject.class);

        List<Predicate> pridicateRules = new ArrayList<Predicate>();

        if (whSiteId != null) {
            pridicateRules.add(criteriaBuilder.equal(root.get("warehouse").get("siteId"), whSiteId));
        }

        pridicateRules.add(criteriaBuilder.equal(root.get("project"), projectName));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                                                               .where(criteriaBuilder.and(pridicateRules.toArray(new Predicate[pridicateRules.size()])));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        List resultList = resultSetQuery.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (QualityInspectionProject) resultList.get(0);
        }
        return null;
    }

    @Override
    public QualityInspectionSupplier findQualityInspectionSupplierBySupplier(String supplierName, String whSiteId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(QualityInspectionSupplier.class);

        List<Predicate> pridicateRules = new ArrayList<Predicate>();

        if (whSiteId != null) {
            pridicateRules.add(criteriaBuilder.equal(root.get("warehouse").get("siteId"), whSiteId));
        }
        pridicateRules.add(criteriaBuilder.equal(root.get("supplier"), supplierName));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                                                               .where(criteriaBuilder.and(pridicateRules.toArray(new Predicate[pridicateRules.size()])));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        List resultList = resultSetQuery.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (QualityInspectionSupplier) resultList.get(0);
        }

        return null;
    }

    @Override
    public List<QualityInspectionProject> findQualityInspectionProjects(String whSiteId) {

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(QualityInspectionProject.class);
        
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                .where(criteriaBuilder.equal(root.get("warehouse").get("siteId"), whSiteId));


        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();

    }

    @Override
    public List<QualityInspectionPart> findQualityInspectionParts(String whSiteId) {

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(QualityInspectionPart.class);
        
        List<Order> orderExpressions = new ArrayList<Order>();
        orderExpressions.add(criteriaBuilder.asc((Expression) root.get("partNumber")));
        orderExpressions.add(criteriaBuilder.asc((Expression) root.get("partName")));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                .where(criteriaBuilder.equal(root.get("warehouse").get("siteId"), whSiteId)).orderBy(orderExpressions);
        
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();

    }

    @Override
    public List<QualityInspectionSupplier> findQualityInspectionSuppliers(String whSiteId) {

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(QualityInspectionSupplier.class);
        
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                .where(criteriaBuilder.equal(root.get("warehouse").get("siteId"), whSiteId));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();

    }

    @Override
    public void deleteQualityInspectionPartById(long qiPartOid) throws GloriaApplicationException {
        QualityInspectionPart qiPart = findQualityInspectionPart(qiPartOid);
        getEntityManager().remove(qiPart);
    }

    @Override
    public void deleteQualityInspectionProjectById(long qiProjectOid) throws GloriaApplicationException {
        QualityInspectionProject qiProject = findQualityInspectionProject(qiProjectOid);
        getEntityManager().remove(qiProject);

    }

    @Override
    public void deleteQualityInspectionSupplierById(long qiSupplierOid) throws GloriaApplicationException {
        QualityInspectionSupplier qiSupplier = findQualityInspectionSupplier(qiSupplierOid);
        getEntityManager().remove(qiSupplier);
    }
    
    @Override
    public Printer save(Printer instanceToSave) {
        Printer toSave = instanceToSave;
        if (instanceToSave.getPrinterOid() < 1) {
            LOGGER.debug("Creating new instance {} ...", instanceToSave);
            getEntityManager().persist(instanceToSave);
            LOGGER.debug("Creating new instance {} done.", instanceToSave);
        } else {
            LOGGER.debug("Updating {} ...", instanceToSave);
            toSave = getEntityManager().merge(instanceToSave);
            LOGGER.debug("Updating {} done.", instanceToSave);
        }
        return toSave;
    }

    @Override
    public List<Printer> findListOfPrinters(String whSiteId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Printer.class);
        
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                .where(criteriaBuilder.equal(root.get("warehouse").get("siteId"), whSiteId));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
           List resultList = resultSetQuery.getResultList();
           if (resultList != null && !resultList.isEmpty()) {
               return  resultList;
           }
           return new ArrayList<Printer>();
    }
    
    @Override
    public Printer findPrinterBySiteIdAndName(String whSiteId, String name) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Printer.class);

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root).where(criteriaBuilder.and(criteriaBuilder.equal(root.get("warehouse")
                                                                                                                                 .get("siteId"), whSiteId),
                                                                                                       criteriaBuilder.equal(root.get("name"), name)));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        List resultList = resultSetQuery.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (Printer) resultList.get(0);
        }
        return null;
    }

    @Override
    public Zone findZoneCodes(ZoneType type, String whSiteId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Zone.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("storageRoom").get("warehouse").get("siteId"), whSiteId));
        predicatesRules.add(criteriaBuilder.equal(root.get("type"), type));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                                                               .where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        List resultList = resultSetQuery.getResultList();

        if (resultList != null && !resultList.isEmpty()) {
            return (Zone) resultList.get(0);
        }
        return null;
    }

    @Override
    public List<Warehouse> findWarehousesByWhsiteIds(List<String> whSiteIds) {
        Query query = getEntityManager().createNamedQuery("findWarehousesByWhsiteIds");
        query.setParameter("whsiteIds", whSiteIds);
        return query.getResultList();
    }
    
    @Override
    public BinlocationBalance getBinlocationBalance(String partAffiliation, String partNumber, String partVersion, String partModification,
            String binLocationCode, String siteId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(BinlocationBalance.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("partAffiliation"), partAffiliation));
        predicatesRules.add(criteriaBuilder.equal(root.get("partNumber"), partNumber));
        predicatesRules.add(criteriaBuilder.equal(root.get("partVersion"), partVersion));
        predicatesRules.add(criteriaBuilder.equal(root.get("partModification"), partModification));
        predicatesRules.add(criteriaBuilder.equal(root.get("binLocation").get("code"), binLocationCode));
        predicatesRules.add(criteriaBuilder.equal(root.get("warehouse").get("siteId"), siteId));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                                                               .where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        List resultList = resultSetQuery.getResultList();

        if (resultList != null && !resultList.isEmpty()) {
            return (BinlocationBalance) resultList.get(0);
        }
        return null;
    }
    
    @Override
    public BinlocationBalance save(BinlocationBalance instanceToSave) {
        BinlocationBalance toSave = instanceToSave;
        if (instanceToSave.getBinlocationBalanceOid() < 1) {
            LOGGER.debug("Creating new instance {} ...", instanceToSave);
            getEntityManager().persist(instanceToSave);
            LOGGER.debug("Creating new instance {} done.", instanceToSave);
        } else {
            LOGGER.debug("Updating {} ...", instanceToSave);
            toSave = getEntityManager().merge(instanceToSave);
            LOGGER.debug("Updating {} done.", instanceToSave);
        }
        return toSave;
    }
    
    @Override
    public void removeBinlocationBalance(BinlocationBalance binlocationBalance) {
        getEntityManager().remove(binlocationBalance);
    }
    
    @Override
    public PageObject getBinlocationBalancesByWhSite(PageObject pageObject, String whSite, String pPartNumber, String binLocationCode) {
        
        Map<String, JpaAttribute> binlocationBalanceFields = new HashMap<String, JpaAttribute>();
        
        binlocationBalanceFields.put("partNumber", new JpaAttribute("partNumber", JpaAttributeType.STRINGTYPE));
        binlocationBalanceFields.put("code", new JpaAttribute("binLocation.code", JpaAttributeType.STRINGTYPE));
        binlocationBalanceFields.put("deviation", new JpaAttribute("deviation", JpaAttributeType.ENUMTYPE, Deviation.class));

        binlocationBalanceFields.put("partAffiliation", new JpaAttribute("partAffiliation", JpaAttributeType.STRINGTYPE));
        binlocationBalanceFields.put("partVersion", new JpaAttribute("partVersion", JpaAttributeType.STRINGTYPE));
        binlocationBalanceFields.put("partModification", new JpaAttribute("partModification", JpaAttributeType.STRINGTYPE));
        binlocationBalanceFields.put("partName", new JpaAttribute("partName", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(BinlocationBalance.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("warehouse").get("siteId"), whSite));
        predicatesRules.add(criteriaBuilder.or(criteriaBuilder.notEqual(root.get("quantity"), 0), criteriaBuilder.isNotNull(root.get("deviation"))));
        
        if (!StringUtils.isEmpty(pPartNumber) && !StringUtils.isEmpty(binLocationCode)) {
            predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("partNumber")), pPartNumber.toLowerCase()));
            predicatesRules.add(criteriaBuilder.equal(root.get("binLocation").get("code"), binLocationCode));
        }
        
        if (!StringUtils.isEmpty(pageObject.getPredicate("storageRoom"))) {
            predicatesRules.add(criteriaBuilder.equal(root.get("binLocation").get("zone").get("storageRoom").get("code"),
                                                      pageObject.getPredicate("storageRoom")));
        }

        if (!StringUtils.isEmpty(pageObject.getPredicate("zone"))) {
            predicatesRules.add(criteriaBuilder.equal(root.get("binLocation").get("zone").get("code"), pageObject.getPredicate("zone")));
        }
        
        String binlocationCode = evaluateBinlocation(pageObject.getPredicate("aisle"),
                                                     pageObject.getPredicate("bay"), pageObject.getPredicate("level"), pageObject.getPredicate("position"));
        if (!StringUtils.isEmpty(binlocationCode)) {
            predicatesRules.add(criteriaBuilder.like(root.get("binLocation").get("code"), "%" + binlocationCode + "%"));           
        }
        
        CriteriaQuery criteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                            binlocationBalanceFields, true);
        Query countQuery = entityManager.createQuery(criteriaQueryFromPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());
        
        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                                     binlocationBalanceFields, false);
        Query resultSetQuery = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());

        List<BinlocationBalance> binlocationBalances = resultSetQuery.getResultList();
        if (binlocationBalances != null && !binlocationBalances.isEmpty()) {
            List<BinlocationBalanceDTO> binlocationBalanceDTOs = WarehouseServicesHelper.transforAsBinlocationBalanceDTOs(binlocationBalances);
            pageObject.setGridContents(new ArrayList<PageResults>(binlocationBalanceDTOs));
        }
        return pageObject;
    }

    @Override
    public List<BinlocationBalance> getBinLocationBalances(String whSite) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(BinlocationBalance.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("warehouse").get("siteId"), whSite));
        predicatesRules.add(criteriaBuilder.or(criteriaBuilder.notEqual(root.get("quantity"), 0), criteriaBuilder.isNotNull(root.get("deviation"))));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                                                               .where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();
    }

    @Override
    public PageObject getBinlocationBalancesByBinlocation(PageObject pageObject, String whSite, String binlocationCode, String partNumber, String partName,
            String partVersion, String partAffiliation, String partModification) {

        Map<String, JpaAttribute> binlocationBalanceFields = new HashMap<String, JpaAttribute>();

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(BinlocationBalance.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("warehouse").get("siteId"), whSite));
        predicatesRules.add(criteriaBuilder.or(criteriaBuilder.notEqual(root.get("quantity"), 0), criteriaBuilder.isNotNull(root.get("deviation"))));

        if (!StringUtils.isEmpty(partNumber)) {
            predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("partNumber")), partNumber.toLowerCase()));
        }

        if (!StringUtils.isEmpty(partName)) {
            predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("partName")), partName.toLowerCase()));
        }

        if (!StringUtils.isEmpty(partVersion)) {
            predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("partVersion")), partVersion.toLowerCase()));
        }

        if (!StringUtils.isEmpty(partModification)) {
            predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("partModification")), partModification.toLowerCase()));
        }

        if (!StringUtils.isEmpty(partAffiliation)) {
            predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("partAffiliation")), partAffiliation.toLowerCase()));
        }

        if (!StringUtils.isEmpty(binlocationCode)) {
            predicatesRules.add(criteriaBuilder.equal(root.get("binLocation").get("code"), binlocationCode));
        }

        CriteriaQuery criteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                          binlocationBalanceFields, true);
        Query countQuery = entityManager.createQuery(criteriaQueryFromPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                                   binlocationBalanceFields, false);
        Query resultSetQuery = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());

        List<BinlocationBalance> binlocationBalances = resultSetQuery.getResultList();
        if (binlocationBalances != null && !binlocationBalances.isEmpty()) {
            List<BinlocationBalanceDTO> binlocationBalanceDTOs = WarehouseServicesHelper.transforAsBinlocationBalanceDTOs(binlocationBalances);
            pageObject.setGridContents(new ArrayList<PageResults>(binlocationBalanceDTOs));
        }
        return pageObject;
    }

    @Override
    public BinlocationBalance findBinlocationBalanceById(long binlocationBalanceOid) {
        return getEntityManager().find(BinlocationBalance.class, binlocationBalanceOid);
    }
    
    @Override
    public List<Zone> findZones(String whSiteId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Zone.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("storageRoom").get("warehouse").get("siteId"), whSiteId));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                                                               .where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();
    }
    
    @Override
    public List<BinLocation> findBinlocations(String zone, String aisle, String bay, String level, String whSiteId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(BinLocation.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("zone").get("code"), zone));
        predicatesRules.add(criteriaBuilder.equal(root.get("zone").get("storageRoom").get("warehouse").get("siteId"), whSiteId));

        String binlocationCode = evaluateBinlocation(aisle, bay, level);
        if (!StringUtils.isEmpty(binlocationCode)) {
            Path<String> pathBinlocationCode = root.get("code");
            predicatesRules.add(criteriaBuilder.like(pathBinlocationCode, "%" + binlocationCode + "%"));
        }
        List<Order> orderExpressions = new ArrayList<Order>();
        orderExpressions.add(criteriaBuilder.asc((Expression) root.get("code")));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                                                               .where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])))
                                                               .orderBy(orderExpressions);

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        return resultSetQuery.getResultList();
    }
    
    private String evaluateBinlocation(String aisle, String bay, String level, String position) {
        List<String> binlocationCode = new ArrayList<String>();
        if (!StringUtils.isEmpty(aisle)) {
            binlocationCode.add(aisle);
        }
        evaluateCodes(aisle, bay, binlocationCode);
        if (!StringUtils.isEmpty(level)) {
            evaluateCodes(bay, String.format("%02d", Long.parseLong(level)), binlocationCode);
        }
        if (!StringUtils.isEmpty(position)) {
            evaluateCodes(level, String.format("%02d", Long.parseLong(position)), binlocationCode);
        }
        return StringUtils.join(binlocationCode.iterator(), "-");
    }
    
    private String evaluateBinlocation(String aisle, String bay, String level) {
        List<String> binlocationCode = new ArrayList<String>();
        if (!StringUtils.isEmpty(aisle)) {
            binlocationCode.add(aisle);
        }
        evaluateCodes(aisle, bay, binlocationCode);
        if (!StringUtils.isEmpty(level)) {
            evaluateCodes(bay, String.format("%02d", Long.parseLong(level)), binlocationCode);
        }
        return StringUtils.join(binlocationCode.iterator(), "-");
    }

    private void evaluateCodes(String parent, String current, List<String> binlocationCode) {
        if (!StringUtils.isEmpty(parent) && !StringUtils.isEmpty(current)) {
            binlocationCode.add(current);
        }
    }
    
    @Override
    public void deletePlacement(Long placementOid) {
        Placement placement = findPlacementByID(placementOid);
        getEntityManager().remove(placement);
    }
    
    @Override
    public List<BinLocation> findBinLocations(String whSiteId, Set<String> binLocationCodes) {

        List<BinLocation> result = new ArrayList<BinLocation>();
        PaginatedArrayList<String> binLocationPaginatedList = new PaginatedArrayList<String>(binLocationCodes);
        for (List<String> subListBinLocation = null; (subListBinLocation = binLocationPaginatedList.nextPage()) != null;) {
            Query query = getEntityManager().createNamedQuery("findBinLocationByCodeAndWhSiteId");
            query.setParameter("siteId", whSiteId);
            query.setParameter("codes", subListBinLocation);

            result.addAll(query.getResultList());
        }
        return result;
    }
    
    @Override
    public PageObject getWarehouses(PageObject pageObject) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("id", new JpaAttribute("siteId", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Warehouse.class);

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

        List<Warehouse> warehouses = queryForResultSets.getResultList();

        if (warehouses != null && !warehouses.isEmpty()) {
            List<ReportFilterDTO> reportWarehouseDTOs = new ArrayList<ReportFilterDTO>();
            for (Warehouse warehouse : warehouses) {
                ReportFilterDTO warehouseDTO = new ReportFilterDTO();
                warehouseDTO.setId(warehouse.getSiteId());
                warehouseDTO.setText(warehouse.getSiteId());
                reportWarehouseDTOs.add(warehouseDTO);
            }
            pageObject.setGridContents(new ArrayList<PageResults>(reportWarehouseDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }
}
