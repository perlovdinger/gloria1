package com.volvo.gloria.common.b;

import java.util.Collection;
import java.util.List;

import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamDTO;
import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamFilterDTO;
import com.volvo.gloria.common.c.dto.GlAccountDTO;
import com.volvo.gloria.common.c.dto.InternalOrderSapDTO;
import com.volvo.gloria.common.c.dto.ProjectDTO;
import com.volvo.gloria.common.c.dto.SiteDTO;
import com.volvo.gloria.common.c.dto.XmlLogEventDTO;
import com.volvo.gloria.common.carryover.c.dto.SyncPurchaseOrderCarryOverDTO;
import com.volvo.gloria.common.companycode.c.dto.SyncCompanyCodeDTO;
import com.volvo.gloria.common.costcenter.c.dto.SyncCostCenterDTO;
import com.volvo.gloria.common.d.entities.CompanyCode;
import com.volvo.gloria.common.d.entities.CompanyGroup;
import com.volvo.gloria.common.d.entities.ConversionUnit;
import com.volvo.gloria.common.d.entities.CostCenter;
import com.volvo.gloria.common.d.entities.Currency;
import com.volvo.gloria.common.d.entities.CurrencyRate;
import com.volvo.gloria.common.d.entities.DangerousGoods;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeamFilter;
import com.volvo.gloria.common.d.entities.GlAccount;
import com.volvo.gloria.common.d.entities.InternalOrderSap;
import com.volvo.gloria.common.d.entities.PartAffiliation;
import com.volvo.gloria.common.d.entities.PartAliasMapping;
import com.volvo.gloria.common.d.entities.QualityDocument;
import com.volvo.gloria.common.d.entities.Site;
import com.volvo.gloria.common.d.entities.SupplierCounterPart;
import com.volvo.gloria.common.d.entities.Traceability;
import com.volvo.gloria.common.d.entities.UnitOfMeasure;
import com.volvo.gloria.common.d.entities.WbsElement;
import com.volvo.gloria.common.wbs.c.dto.SyncWBSElementDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.paging.c.PageObject;

/**
 * Service class for Common Domain.
 */
public interface CommonServices {

    void addSite(Site site);

    void addSupplierCounterPart(SupplierCounterPart supplierCounterPart);

    void addSupplierCounterParts(List<SupplierCounterPart> supplierCounterPartList);

    List<Site> getAllSites();

    List<Site> getSiteBySiteIds(List<String> siteIds);

    List<SiteDTO> getSite(List<String> siteIds);

    List<SupplierCounterPart> getAllSupplierCounterParts(String deliveryFollowUpTeamId);

    void addPartAffiliation(PartAffiliation partAffiliation);

    void addPartAliasMapping(PartAliasMapping partAliasMapping);

    void addUnitOfMeasure(UnitOfMeasure unitOfMeasure);

    List<PartAffiliation> getAllPartAffiliations(boolean requestable);

    List<UnitOfMeasure> getAllUnitOfMeasures();

    void addCurrency(Currency currency);

    void logXMLMessage(XmlLogEventDTO xmlLogEventDTO);

    DeliveryFollowUpTeam addDeliveryFollowUpTeam(DeliveryFollowUpTeam deliveryFollowUpTeam);

    DeliveryFollowUpTeamFilterDTO addDeliveryFollowUpTeamFilter(DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTOs, long deliveryFollowUpOid)
            throws GloriaApplicationException;

    DeliveryFollowUpTeamDTO getDeliveryFollowupTeam(String deliveryFollowUpTeamName);

    List<DeliveryFollowUpTeamFilter> getDeliveryFollowUpTeamFilters(long deliveryFollowUpTeamId);

    List<DeliveryFollowUpTeam> findAllDeliveryFollowUpTeam();

    List<DeliveryFollowUpTeamFilter> findAllDeliveryFollowUpTeamFilter();

    DeliveryFollowUpTeamFilter updateDeliveryFollowupTeamFilter(DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO) throws GloriaApplicationException;

    DeliveryFollowUpTeam updateDeliveryFollowupTeam(DeliveryFollowUpTeamDTO deliveryFollowUpTeamDTO) throws GloriaApplicationException;

    DeliveryFollowUpTeamFilterDTO getDeliveryFollowUpTeamFilter(long deliveryFollowUpTeamFilterOId) throws GloriaApplicationException;

    void deleteDeliveryFollowupTeamFilter(long deliveryFollowUpTeamFilterId);

    Site getSiteBySiteId(String siteId);

    DeliveryFollowUpTeam findDeliveryFollowUpTeam(long deliveryFollowUpTeamOid);

    SupplierCounterPart findSupplierCounterPartByPPSuffix(String ppSuffix);

    String matchDeliveryController(DeliveryFollowUpTeam deliveryFollowUpTeam, String ppSuffix, String supplierId, String projectId);

    void createSitesData(String xmlContent);

    void createPartAffiliationData(String xmlContent);

    void createUnitOfMeasureData(String xmlContent);

    void createDeliveryFollowUpTeamData(String xmlContent);

    SupplierCounterPart getSupplierCounterPartById(long supplierCounterPartOId);

    void addDangerousGoods(DangerousGoods dangerousGoods);

    PageObject getDeliveryFollowUpTeamFilters(PageObject pageObject, long deliveryFollowUpTeamOid);

    List<DangerousGoods> getAllDangerousGoods();

    List<Site> getAllBuildSites(PageObject pageObject, String siteId);

    void addInternalOrderSap(InternalOrderSap internalOrderSapEntity);

    List<InternalOrderSapDTO> createInternalOrderSapData(String stringFromClasspath);

    List<InternalOrderSap> getAllInternalOrderNoSAP(PageObject pageObject, String code);

    void addCompanyCode(CompanyCode companyCode);

    List<CompanyCode> findAllCompanyCodes();

    /**
     * Sync incoming cost center depending on action code.
     * 
     * @param syncCostCenterDTO
     */
    void syncCostCenter(SyncCostCenterDTO syncCostCenterDTO);

    /**
     * Sync with exiting WBS. Add new WBS and delete non exiting ones in incoming DTO.
     * 
     * @param syncWBSElementDTO
     */
    void syncWBSElement(SyncWBSElementDTO syncWBSElementDTO);

    void addGlAccount(GlAccount glAccount);

    void updateGlAccount(GlAccount glAccount);

    void addGlAccounts(List<GlAccount> glAccounts);

    List<GlAccount> createGlAccounts(List<GlAccountDTO> glAccountDtos);

    List<CostCenter> getAllCostCenters();

    List<GlAccount> getAllGlAccounts();

    List<WbsElement> getAllWbsElements();

    CompanyCode findCompanyCodeByCode(String companyCode);

    List<ProjectDTO> getProjectsByCompanyCode(PageObject pageObject, String companyCode, String projectID);

    PageObject findWbsElementsByCompanyCodeAndProjectId(String companyCode, String shortId, String code, PageObject pageObject);

    PageObject findCostCentersByCompanyCode(String companyCode, String costCenter, PageObject pageObject);

    PageObject findGlAccountsByCompanyCode(String companyCode, String glAccountStr, PageObject pageObject);

    UnitOfMeasure findUnitOfMeasureByCode(String code);

    SupplierCounterPart findSupplierCounterPartByMaterialUserId(String materialUserId);

    WbsElement findWBSElementByProjectIDandCompanyCode(String companyCode, String projectId, String wbsCode);

    void addQualityDocument(QualityDocument qualityDocumentEntity);

    List<QualityDocument> findAllQualityDocuments();

    void addSyncCompanyCode(SyncCompanyCodeDTO transformCompanyCode);

    List<SiteDTO> getAllsparePartSites();

    void addCurrencyRate(CurrencyRate currencyRate);

    Currency findCurrencyByCode(String code);

    List<String> syncCarryOver(SyncPurchaseOrderCarryOverDTO syncCarryOverDTO);

    List<Traceability> getMaterialLineTraceabilitys(long materialOid);

    List<Traceability> getOrderLineTraceabilitys(long orderLineOid);

    String getCompanyCodeFromMaterialUserId(String materialUserId);

    CompanyGroup getCompanyGroupByCode(String companyCodeGroup);

    CompanyGroup addCompanyGroup(CompanyGroup companyGroup);

    List<String> getCompanyCodeCodes(List<String> companyGroups);

    List<SupplierCounterPart> getSupplierCounterPartsByCompanyCode(String companyCode);

    List<SupplierCounterPart> getAllSupplierCounterParts();

    void addConversionUnit(ConversionUnit conversionUnit);

    ConversionUnit findConversionUnit(String applicationFrom, String applicationTo, String codeToConvert);

    Site getSiteBySiteCode(String siteCode);

    List<ProjectDTO> getProjectsByCompanyCodes(PageObject pageObject, List<String> companyCodes, String projectId);

    void deleteCarryOverOnLoad(SyncPurchaseOrderCarryOverDTO syncCarryOverDTO);

    CurrencyRate getValidCurrencyRate(String currencyCode);

    List<Currency> findAllCurreny();

    boolean isWbsValid(String wbs);

    List<String> getSiteIdsByCompanyCodes(Collection<String> companyCodeCodes);

    boolean verifyApplicationStatus();

    Currency getCurrencyByCode(String code);
}
