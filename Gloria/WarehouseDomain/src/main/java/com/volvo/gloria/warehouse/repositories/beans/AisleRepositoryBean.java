package com.volvo.gloria.warehouse.repositories.beans;

import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.gloria.warehouse.d.entities.AisleRackRow;
import com.volvo.gloria.warehouse.d.entities.BaySetting;
import com.volvo.gloria.warehouse.d.entities.Zone;
import com.volvo.gloria.warehouse.repositories.b.AisleRepository;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * repository class for AisleRackRow.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AisleRepositoryBean extends GenericAbstractRepositoryBean<AisleRackRow, Long> implements AisleRepository {

    @PersistenceContext(unitName = "WarehouseDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AisleRackRow> getAisleRackRowByZoneID(long zoneId) {
        Query query = getEntityManager().createNamedQuery("getAisleRackRowByZoneId");
        query.setParameter("zoneOid", zoneId);
        return query.getResultList();
    }

    @Override
    public AisleRackRow addAisleRackRow(long zoneId, AisleRackRow aisleRackRow) {
        Zone zone = findZone(zoneId);
        aisleRackRow.setSetUp(zone.getStorageRoom().getWarehouse().getSetUp());
        aisleRackRow.setZone(zone);

        getEntityManager().persist(aisleRackRow);
        return aisleRackRow;
    }

    public Zone findZone(long zoneId) {
        Zone zone = null;
        zone = getEntityManager().find(Zone.class, zoneId);
        return zone;
    }

    @Override
    public AisleRackRow updateAisleRackRow(AisleRackRow aisleRackRow) {
        getEntityManager().merge(aisleRackRow);
        return aisleRackRow;
    }

    @Override
    public void deleteAisleRackRow(long aisleRackRowOid) {
        AisleRackRow aisleRackRow = findAisleRackRow(aisleRackRowOid);

        getEntityManager().remove(aisleRackRow);
    }

    public AisleRackRow findAisleRackRow(long aisleRackRowOid) {
        AisleRackRow aisleRackRow = null;
        aisleRackRow = getEntityManager().find(AisleRackRow.class, aisleRackRowOid);
        return aisleRackRow;
    }

    @Override
    public BaySetting addBaySetting(BaySetting baySetting) {
        getEntityManager().persist(baySetting);
        return baySetting;
    }

    @Override
    public BaySetting updateBaySetting(BaySetting baySetting) {
        getEntityManager().merge(baySetting);
        return baySetting;
    }

    @Override
    public BaySetting findBaySetting(Long baySettingOid) {
        return getEntityManager().find(BaySetting.class, baySettingOid);
    }

    @Override
    public void deleteBaySetting(Long baySettingOid) {
        BaySetting baySetting = findBaySetting(baySettingOid);
        AisleRackRow aisleRackRow = findById(baySetting.getAisleRackRow().getAisleRackRowOid());
        aisleRackRow.getBaySettings().remove(baySetting);
        getEntityManager().remove(baySetting);
        aisleRackRow.setNumberOfBay(aisleRackRow.getNumberOfBay() - 1);
        updateAisleRackRow(aisleRackRow);
    }
}
