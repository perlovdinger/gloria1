package com.volvo.gloria.common.b.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.b.DeliveryFollowUpTeamTransformer;
import com.volvo.gloria.common.b.InternalOrderSapTransformer;
import com.volvo.gloria.common.b.PartAffiliationTransformer;
import com.volvo.gloria.common.b.SitesTransformer;
import com.volvo.gloria.common.b.UnitOfMeasureTransformer;
import com.volvo.gloria.common.c.CarryOverActionType;
import com.volvo.gloria.common.c.CostCenterActionType;
import com.volvo.gloria.common.c.FollowUpType;
import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamDTO;
import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamFilterDTO;
import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamTransformerDTO;
import com.volvo.gloria.common.c.dto.GlAccountDTO;
import com.volvo.gloria.common.c.dto.InternalOrderSapDTO;
import com.volvo.gloria.common.c.dto.PartAffiliationDTO;
import com.volvo.gloria.common.c.dto.ProjectDTO;
import com.volvo.gloria.common.c.dto.SiteDTO;
import com.volvo.gloria.common.c.dto.SiteDTOs;
import com.volvo.gloria.common.c.dto.SupplierCounterPartDTO;
import com.volvo.gloria.common.c.dto.UnitOfMeasureDTO;
import com.volvo.gloria.common.c.dto.WbsElementDTO;
import com.volvo.gloria.common.c.dto.XmlLogEventDTO;
import com.volvo.gloria.common.carryover.c.dto.SyncPurchaseOrderCarryOverDTO;
import com.volvo.gloria.common.companycode.c.dto.CompanyCodeDTO;
import com.volvo.gloria.common.companycode.c.dto.SyncCompanyCodeDTO;
import com.volvo.gloria.common.costcenter.c.dto.CostCenterItemDTO;
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
import com.volvo.gloria.common.d.entities.XmlMessageLog;
import com.volvo.gloria.common.repositories.b.CarryOverRepository;
import com.volvo.gloria.common.repositories.b.CompanyCodeRepository;
import com.volvo.gloria.common.repositories.b.ConversionUnitRepository;
import com.volvo.gloria.common.repositories.b.CostCenterRepository;
import com.volvo.gloria.common.repositories.b.CurrencyRateRepository;
import com.volvo.gloria.common.repositories.b.CurrencyRepository;
import com.volvo.gloria.common.repositories.b.DangerousGoodsRepository;
import com.volvo.gloria.common.repositories.b.DeliveryFollowUpTeamRepository;
import com.volvo.gloria.common.repositories.b.GlAccountRepository;
import com.volvo.gloria.common.repositories.b.InternalOrderSapRepository;
import com.volvo.gloria.common.repositories.b.PartAffiliationRepository;
import com.volvo.gloria.common.repositories.b.PartAliasMappingRepository;
import com.volvo.gloria.common.repositories.b.QualityDocumentRepository;
import com.volvo.gloria.common.repositories.b.SiteRepository;
import com.volvo.gloria.common.repositories.b.SupplierCounterPartRepository;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.common.repositories.b.UnitOfMeasureRepository;
import com.volvo.gloria.common.repositories.b.WbsElementRepository;
import com.volvo.gloria.common.repositories.b.XmlMessageLogRepository;
import com.volvo.gloria.common.util.CommonHelper;
import com.volvo.gloria.common.util.CostCenterHelper;
import com.volvo.gloria.common.wbs.c.dto.SyncWBSElementDTO;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Service implementations for CommonDomain.
 */

@ContainerManaged(name = "commonServices")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class CommonServicesBean implements CommonServices {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonServicesBean.class);
    @Inject
    private SiteRepository siteRepo;
    @Inject
    private SupplierCounterPartRepository suppCounterPartRepo;
    @Inject
    private PartAffiliationRepository partAffiliationRepo;
    @Inject
    private PartAliasMappingRepository partAliasMappingRepo;
    @Inject
    private UnitOfMeasureRepository unitOfMeasureRepo;
    @Inject
    private CurrencyRepository currencyRepo;
    @Inject
    private XmlMessageLogRepository xmlMessageLogRepo;
    @Inject
    private DeliveryFollowUpTeamRepository deliveryFollowUpTeamRepo;
    @Inject
    private SitesTransformer sitesTransformer;
    @Inject
    private PartAffiliationTransformer partAffiliationTransformer;
    @Inject
    private UnitOfMeasureTransformer unitOfMeasureTransformer;
    @Inject
    private DeliveryFollowUpTeamTransformer deliveryFollowUpTeamTransformer;
    @Inject
    private DangerousGoodsRepository dangerousGoodsRepo;
    @Inject
    private CarryOverRepository carryOverRepository;
    @Inject
    private InternalOrderSapRepository internalOrderSapRepo;
    @Inject
    private InternalOrderSapTransformer internalOrderSapTransformer;
    @Inject
    private CompanyCodeRepository companyCodeRepo;
    @Inject
    private CostCenterRepository costCenterRepo;
    @Inject
    private GlAccountRepository glAccountRepo;
    @Inject
    private WbsElementRepository wbsElementRepo;
    @Inject
    private QualityDocumentRepository qualityDocumentRepo;
    @Inject
    private CurrencyRateRepository currencyRateRepo;
    @Inject
    private TraceabilityRepository traceabilityRepository;
    @Inject
    private ConversionUnitRepository conversionUnitRepository;
    
    @Override
    public void addSite(Site site) {
        siteRepo.save(site);
    }

    @Override
    public void addSupplierCounterPart(SupplierCounterPart supplierCounterPart) {
        suppCounterPartRepo.save(supplierCounterPart);
    }

    @Override
    public void addSupplierCounterParts(List<SupplierCounterPart> supplierCounterPartList) {
        for (SupplierCounterPart supplierCounterPart : supplierCounterPartList) {
            addSupplierCounterPart(supplierCounterPart);
        }
    }

    @Override
    public List<Site> getSiteBySiteIds(List<String> siteIds) {
        return siteRepo.getSiteBySiteIds(siteIds);
    }

    @Override
    public List<SiteDTO> getAllsparePartSites() {
        List<Site> sites = siteRepo.getAllsparePartSites();
        return CommonHelper.transforToSiteDTOs(sites);
    }

    @Override
    public List<SiteDTO> getSite(List<String> siteIds) {
        List<Site> sites = getSiteBySiteIds(siteIds);
        return CommonHelper.transforToSiteDTOs(sites);
    }

    @Override
    public List<SupplierCounterPart> getAllSupplierCounterParts(String deliveryFollowUpTeamId) {
        return suppCounterPartRepo.getAllSupplierCounterParts(deliveryFollowUpTeamId);
    }

    @Override
    public List<SupplierCounterPart> getAllSupplierCounterParts() {
        return suppCounterPartRepo.findAll();
    }

    @Override
    public void addPartAffiliation(PartAffiliation partAffiliation) {
        partAffiliationRepo.save(partAffiliation);
    }
    @Override
    public void addPartAliasMapping(PartAliasMapping partAliasMapping) {
        partAliasMappingRepo.save(partAliasMapping);
    }

    @Override
    public void addUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        unitOfMeasureRepo.save(unitOfMeasure);
    }

    @Override
    public List<PartAffiliation> getAllPartAffiliations(boolean requestable) {
        return partAffiliationRepo.getAllPartAffiliations(requestable);
    }

    @Override
    public List<UnitOfMeasure> getAllUnitOfMeasures() {
        return unitOfMeasureRepo.findAllUnitOfMeasuresSupportedForGloria();
    }

    @Override
    public void addCurrency(Currency currency) {
        currencyRepo.save(currency);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void logXMLMessage(XmlLogEventDTO xmlLogEventDTO) {
        try {
            XmlMessageLog xmlMessageLog = new XmlMessageLog();
            BeanUtils.copyProperties(xmlLogEventDTO, xmlMessageLog);
            xmlMessageLogRepo.save(xmlMessageLog);
        } catch (Exception e) {
            LOGGER.error("Couldn't log message " + xmlLogEventDTO.getJmsMessageId(), e);
        }
    }

    @Override
    public DeliveryFollowUpTeam addDeliveryFollowUpTeam(DeliveryFollowUpTeam deliveryFollowUpTeam) {
        return deliveryFollowUpTeamRepo.save(deliveryFollowUpTeam);
    }

    @Override
    public DeliveryFollowUpTeamFilterDTO addDeliveryFollowUpTeamFilter(DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO, long deliveryFollowUpOid)
            throws GloriaApplicationException {
        DeliveryFollowUpTeam deliveryFollowUpteam = deliveryFollowUpTeamRepo.findDeliveryFollowUpTeamById(deliveryFollowUpOid);
        validateDeliveryFollowUpTeamFilter(deliveryFollowUpTeamFilterDTO, deliveryFollowUpteam);
        validateUniqueDeliverFollowUpTeamFilters(deliveryFollowUpTeamFilterDTO, deliveryFollowUpteam);
        return CommonHelper.transformAsDeliveryFollowUpTeamFilterDTO(deliveryFollowUpTeamRepo.addDeliveryFollowUpTeamFilter(deliveryFollowUpTeamFilterDTO,
                                                                                                                            deliveryFollowUpOid));
    }

    private void validateDeliveryFollowUpTeamFilter(DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO, DeliveryFollowUpTeam deliveryFollowUpteam)
            throws GloriaApplicationException {
        if (deliveryFollowUpteam.getFollowUpType() == FollowUpType.SUPPLIER) {
            if (StringUtils.isEmpty(deliveryFollowUpTeamFilterDTO.getSupplierId()) && StringUtils.isEmpty(deliveryFollowUpTeamFilterDTO.getSuffix())) {
                throw new GloriaApplicationException(GloriaExceptionConstants.NO_SUFFIX_AND_SUPPLIER_ID, "Either Suffix or Supplier ID is mandatory.", null);
            }
            if (StringUtils.isEmpty(deliveryFollowUpTeamFilterDTO.getDeliveryControllerUserId())) {
                throw new GloriaApplicationException(GloriaExceptionConstants.NO_DELIVERY_CONTROLLER_USER_ID, "Delivery Controller User ID is mandatory.",
                                                     null);
            }
        } else if (deliveryFollowUpteam.getFollowUpType() == FollowUpType.PROJECT
                && (StringUtils.isEmpty(deliveryFollowUpTeamFilterDTO.getProjectId()) 
                        || StringUtils.isEmpty(deliveryFollowUpTeamFilterDTO.getDeliveryControllerUserId()))) {
            throw new GloriaApplicationException(GloriaExceptionConstants.NO_PROJECT_ID_AND_DELIVERY_CTRL_USERID,
                                                 "Project Id and Delivery Controller User ID is mandatory.", null);
        }
    }

    private void validateUniqueDeliverFollowUpTeamFilters(DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO, 
            DeliveryFollowUpTeam deliveryFollowUpteam) throws GloriaApplicationException {
        List<DeliveryFollowUpTeamFilter> oldDeliveryFollowUpTeamFilters = deliveryFollowUpteam.getDeliveryFollowUpTeamFilters();
        for (DeliveryFollowUpTeamFilter deliveryFollowUpTeamFilter : oldDeliveryFollowUpTeamFilters) {
            if (deliveryFollowUpteam.getFollowUpType() == FollowUpType.PROJECT) {
                if (deliveryFollowUpTeamFilterDTO.getProjectId() != null
                        && deliveryFollowUpTeamFilterDTO.getProjectId().equals(deliveryFollowUpTeamFilter.getProjectId())) {
                    throw new GloriaApplicationException(GloriaExceptionConstants.UNIQUE_DELIVERY_FOLLOWUP_PROJECT_TEAM_FILTER, " Project Id must be unique.",
                                                         null);
                }
            } else {
                String existingSupplierInfo = getSupplierInfo(deliveryFollowUpTeamFilter.getSuffix(), deliveryFollowUpTeamFilter.getSupplierId());
                String newSupplierInfo = getSupplierInfo(deliveryFollowUpTeamFilterDTO.getSuffix(), deliveryFollowUpTeamFilterDTO.getSupplierId());
                if (existingSupplierInfo.equals(newSupplierInfo)) {
                    throw new GloriaApplicationException(GloriaExceptionConstants.UNIQUE_DELIVERY_FOLLOWUP_SUPPLIER_TEAM_FILTER,
                                                         " Supplier ID and Suffix combo must be unique.", null);
                }
            }
        }
    }

    private String getSupplierInfo(String suffix, String supplierId) {
        String supplierInfo = "";
        if (!StringUtils.isEmpty(suffix)) {
            supplierInfo = supplierInfo.concat(suffix);
        }
        if (!StringUtils.isEmpty(supplierId)) {
            supplierInfo = supplierInfo.concat(supplierId);
        }
        return supplierInfo;
    }

    @Override
    public DeliveryFollowUpTeamDTO getDeliveryFollowupTeam(String deliveryFollowUpTeamName) {
        return deliveryFollowUpTeamRepo.getDeliveryFollowupTeam(deliveryFollowUpTeamName);
    }

    @Override
    public List<DeliveryFollowUpTeamFilter> getDeliveryFollowUpTeamFilters(long deliveryFollowUpTeamOid) {
        return deliveryFollowUpTeamRepo.getDeliveryFollowUpTeamFilters(deliveryFollowUpTeamOid);
    }

    @Override
    public List<DeliveryFollowUpTeam> findAllDeliveryFollowUpTeam() {
        return deliveryFollowUpTeamRepo.findAllDeliveryFollowUpTeam();
    }

    @Override
    public List<DeliveryFollowUpTeamFilter> findAllDeliveryFollowUpTeamFilter() {
        return deliveryFollowUpTeamRepo.findAllDeliveryFollowUpTeamFilter();
    }

    @Override
    public DeliveryFollowUpTeamFilter updateDeliveryFollowupTeamFilter(DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO)
            throws GloriaApplicationException {

        DeliveryFollowUpTeamFilter deliveryFollowUpTeamFilter = 
                deliveryFollowUpTeamRepo.findDeliveryFollowUpTeamFiltersById(deliveryFollowUpTeamFilterDTO.getId());

        if (deliveryFollowUpTeamFilter.getDeliveryFollowUpTeam().getFollowUpType() == FollowUpType.SUPPLIER) {

            if (StringUtils.isEmpty(deliveryFollowUpTeamFilterDTO.getSupplierId()) && StringUtils.isEmpty(deliveryFollowUpTeamFilterDTO.getSuffix())) {
                throw new GloriaApplicationException(GloriaExceptionConstants.NO_SUFFIX_AND_SUPPLIER_ID, "Either Suffix or Supplier ID is mandatory.");
            }
            DeliveryFollowUpTeamFilter deliveryFollowupTeamFilter = 
                    deliveryFollowUpTeamRepo.checkSupplierDeliveryFollowupTeamFilter(deliveryFollowUpTeamFilterDTO.getSuffix(), 
                                                                                     deliveryFollowUpTeamFilterDTO.getSupplierId());

            if (deliveryFollowupTeamFilter != null && deliveryFollowupTeamFilter.getDeliveryFollowUpTeamFilterOid() != deliveryFollowUpTeamFilterDTO.getId()) {

                throw new GloriaApplicationException(GloriaExceptionConstants.UNIQUE_DELIVERY_FOLLOWUP_SUPPLIER_TEAM_FILTER,
                                                     " Supplier ID and Suffix combo must be unique.", null);
            }

        } else {
            if (StringUtils.isEmpty(deliveryFollowUpTeamFilterDTO.getProjectId())) {
                throw new GloriaApplicationException(GloriaExceptionConstants.NO_PROJECT_ID_AND_DELIVERY_CTRL_USERID,
                                                     "Project Id and DeliveryController is mandatory.");
            }
            DeliveryFollowUpTeamFilter deliveryFollowupTeamFilter = 
                    deliveryFollowUpTeamRepo.checkProjectDeliveryFollowupTeamFilter(deliveryFollowUpTeamFilterDTO.getProjectId());

            if (deliveryFollowupTeamFilter != null && deliveryFollowupTeamFilter.getDeliveryFollowUpTeamFilterOid() != deliveryFollowUpTeamFilterDTO.getId()) {
                throw new GloriaApplicationException(GloriaExceptionConstants.UNIQUE_DELIVERY_FOLLOWUP_PROJECT_TEAM_FILTER, " Project", null);
            }
        }
        return deliveryFollowUpTeamRepo.updateDeliveryFollowupTeamFilter(deliveryFollowUpTeamFilterDTO);
    }

    @Override
    public DeliveryFollowUpTeam updateDeliveryFollowupTeam(DeliveryFollowUpTeamDTO deliveryFollowUpTeamDTO) throws GloriaApplicationException {
        return deliveryFollowUpTeamRepo.updateDeliveryFollowupTeam(deliveryFollowUpTeamDTO);
    }

    @Override
    public Site getSiteBySiteId(String siteId) {
        return siteRepo.getSiteBySiteId(siteId);
    }

    @Override
    public DeliveryFollowUpTeamFilterDTO getDeliveryFollowUpTeamFilter(long deliveryFollowUpTeamFilterOId) throws GloriaApplicationException {
        return CommonHelper.transformAsDeliveryFollowUpTeamFilterDTO(deliveryFollowUpTeamRepo.getDeliveryFollowUpTeamFilter(deliveryFollowUpTeamFilterOId));
    }

    @Override
    public void deleteDeliveryFollowupTeamFilter(long deliveryFollowUpTeamFilterId) {
        deliveryFollowUpTeamRepo.deleteDeliveryFollowupTeamFilter(deliveryFollowUpTeamFilterId);
    }

    @Override
    public DeliveryFollowUpTeam findDeliveryFollowUpTeam(long deliveryFollowUpTeamOid) {
        return deliveryFollowUpTeamRepo.findDeliveryFollowUpTeamById(deliveryFollowUpTeamOid);
    }

    @Override
    public SupplierCounterPart findSupplierCounterPartByPPSuffix(String ppSuffix) {
        return suppCounterPartRepo.findSupplierCounterPartByPPSuffix(ppSuffix);
    }

    @Override
    public String matchDeliveryController(DeliveryFollowUpTeam deliveryFollowUpTeam, String ppSuffix, String supplierId, String projectId) {
        String deliveryControllerUserId = null;
        if (deliveryFollowUpTeam != null) {
            List<DeliveryFollowUpTeamFilter> delFollowUpTeamFilters = deliveryFollowUpTeamRepo.getDeliveryFollowUpTeamFilters(
                                                                      deliveryFollowUpTeam.getDeliveryFollowUpTeamOid());
            if (delFollowUpTeamFilters != null && !delFollowUpTeamFilters.isEmpty()) {

                if (deliveryFollowUpTeam.getFollowUpType().equals(FollowUpType.SUPPLIER)) {
                    return getDeliveryControllerUserIdIfSupplier(delFollowUpTeamFilters, ppSuffix, supplierId, deliveryFollowUpTeam);
                }
                if (deliveryFollowUpTeam.getFollowUpType().equals(FollowUpType.PROJECT)) {
                    for (DeliveryFollowUpTeamFilter deliveryFollowUpTeamFilter : delFollowUpTeamFilters) {
                        if (deliveryFollowUpTeamFilter.getProjectId().equalsIgnoreCase(projectId)) {
                            return deliveryFollowUpTeamFilter.getDeliveryControllerUserId();
                        }
                    }
                }
            }
            deliveryControllerUserId = deliveryFollowUpTeam.getDefaultDCUserId();
        }
        return deliveryControllerUserId;
    }

    private String getDeliveryControllerUserIdIfSupplier(List<DeliveryFollowUpTeamFilter> delFollowUpTeamFilters, String ppSuffix, String supplierId,
            DeliveryFollowUpTeam deliveryFollowUpTeam) {
        for (DeliveryFollowUpTeamFilter deliveryFollowUpTeamFilter : delFollowUpTeamFilters) {
            if (validate(ppSuffix, supplierId, deliveryFollowUpTeamFilter)) {
                return deliveryFollowUpTeamFilter.getDeliveryControllerUserId();
            }
        }
        return deliveryFollowUpTeam.getDefaultDCUserId();
    }

    private boolean validate(String ppSuffix, String supplierId, DeliveryFollowUpTeamFilter deliveryFollowUpTeamFilter) {
        String suppId = deliveryFollowUpTeamFilter.getSupplierId();
        String suff = deliveryFollowUpTeamFilter.getSuffix();

        boolean rule1 = (!StringUtils.isEmpty(suppId)) && (!StringUtils.isEmpty(suff)) && suppId.equalsIgnoreCase(supplierId)
                && suff.equalsIgnoreCase(ppSuffix);
        boolean rule2 = (!StringUtils.isEmpty(suppId)) && suppId.equalsIgnoreCase(supplierId) && StringUtils.isEmpty(suff);
        boolean rule3 = (!StringUtils.isEmpty(suff)) && suff.equalsIgnoreCase(ppSuffix) && StringUtils.isEmpty(suppId);

        if (rule1) {
            return rule1;
        } else {
            return rule2 || rule3;
        }
    }

    @Override
    public void createSitesData(String xmlContent) {
        SiteDTOs sitesDTOs = sitesTransformer.transformSites(xmlContent);

        for (SiteDTO siteDTO : sitesDTOs.getListOfSiteDTO()) {
            Site site = new Site();
            site.setSiteId(siteDTO.getSiteId());
            site.setSiteCode(siteDTO.getSiteCode());
            site.setSiteName(siteDTO.getSiteName());
            site.setAddress(siteDTO.getAddress());
            site.setPhone(siteDTO.getPhone());
            site.setCountryCode(siteDTO.getCompanyCode());
            site.setCompanyCode(siteDTO.getCompanyCode());
            site.setJointVenture(siteDTO.isJointVenture());
            site.setShipToSite(siteDTO.isShipToSite());
            site.setShipToType(siteDTO.getShipToType());          
            site.setBuildSite(siteDTO.isBuildSite());
            site.setBuildSiteType(siteDTO.getBuildSiteType());
            addSite(site);
        }
    }

    @Override
    public void createPartAffiliationData(String xmlContent) {

        List<PartAffiliationDTO> partaffiliationDTOs = partAffiliationTransformer.transformPartAffiliation(xmlContent);

        for (PartAffiliationDTO partAffiliationDTO : partaffiliationDTOs) {
            PartAffiliation partAffiliationEntity = new PartAffiliation();
            partAffiliationEntity.setCode(partAffiliationDTO.getCode());
            partAffiliationEntity.setName(partAffiliationDTO.getName());
            partAffiliationEntity.setDisplaySeq(Integer.valueOf(partAffiliationDTO.getDisplaySeq()));
            partAffiliationEntity.setRequestable(partAffiliationDTO.isRequestable());
            addPartAffiliation(partAffiliationEntity);
        }
    }

    @Override
    public void createUnitOfMeasureData(String xmlContent) {
        List<UnitOfMeasureDTO> unitOfMeasureDTOs = unitOfMeasureTransformer.transformUnitOfMeasure(xmlContent);

        for (UnitOfMeasureDTO unitOfMeasureDTO : unitOfMeasureDTOs) {
            UnitOfMeasure unitOfMeasureEty = new UnitOfMeasure();
            unitOfMeasureEty.setCode(unitOfMeasureDTO.getCode());
            unitOfMeasureEty.setName(unitOfMeasureDTO.getName());
            unitOfMeasureEty.setGloriaCode(unitOfMeasureDTO.isGloriaCode());
            unitOfMeasureEty.setDisplaySeq(Integer.valueOf(unitOfMeasureDTO.getDisplaySeq()));
            addUnitOfMeasure(unitOfMeasureEty);
        }
    }

    @Override
    public void createDeliveryFollowUpTeamData(String xmlContent) {
        List<DeliveryFollowUpTeamTransformerDTO> deliveryFollowUpTeamDTOs = deliveryFollowUpTeamTransformer.transformDeliveryFollowUpTeam(xmlContent);

        for (DeliveryFollowUpTeamTransformerDTO deliveryFollowUpTeamTransformerDTO : deliveryFollowUpTeamDTOs) {
            DeliveryFollowUpTeam deliveryFollowUpTeamEntity = new DeliveryFollowUpTeam();
            deliveryFollowUpTeamEntity.setName(deliveryFollowUpTeamTransformerDTO.getName());
            deliveryFollowUpTeamEntity.setFollowUpType(deliveryFollowUpTeamTransformerDTO.getFollowUpType());
            com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam deliveryFollowUpteam = addDeliveryFollowUpTeam(deliveryFollowUpTeamEntity);

            for (SupplierCounterPartDTO supplierCounterPartDTO : deliveryFollowUpTeamTransformerDTO.getSupplierCounterPartDTOs()) {
                SupplierCounterPart supplierPartEntity = new SupplierCounterPart();
                supplierPartEntity.setShipToId(supplierCounterPartDTO.getShipToId());
                supplierPartEntity.setPpSuffix(supplierCounterPartDTO.getPpSuffix());
                supplierPartEntity.setTransitAddressId(supplierCounterPartDTO.getTransitAddressId());
                supplierPartEntity.setDomesticInvoiceId(supplierCounterPartDTO.getDomesticInvoiceId());
                supplierPartEntity.setInternationalInvoiceId(supplierCounterPartDTO.getInternationalInvoiceId());
                supplierPartEntity.setComment(supplierCounterPartDTO.getComment());
                supplierPartEntity.setMaterialUserId(supplierCounterPartDTO.getMaterialUserId());
                supplierPartEntity.setDeliveryFollowUpTeam(findDeliveryFollowUpTeam(deliveryFollowUpteam.getDeliveryFollowUpTeamOid()));
                supplierPartEntity.setCompanyCode(supplierCounterPartDTO.getCompanyCode());
                addSupplierCounterPart(supplierPartEntity);
            }
        }

    }

    @Override
    public SupplierCounterPart getSupplierCounterPartById(long supplierCounterPartOId) {
        return suppCounterPartRepo.findById(supplierCounterPartOId);
    }

    @Override
    public void addDangerousGoods(DangerousGoods dangerousGoods) {
        dangerousGoodsRepo.save(dangerousGoods);

    }

    @Override
    public PageObject getDeliveryFollowUpTeamFilters(PageObject pageObject, long deliveryFollowUpTeamOid) {
        return deliveryFollowUpTeamRepo.getDeliveryFollowUpTeamFilters(pageObject, deliveryFollowUpTeamOid);
    }

    @Override
    public List<DangerousGoods> getAllDangerousGoods() {
        return dangerousGoodsRepo.findAll();
    }

    @Override
    public List<Site> getAllSites() {
        return siteRepo.findAll();
    }

    @Override
    public List<Site> getAllBuildSites(PageObject pageObject, String siteId) {
        return siteRepo.getAllBuildSites(pageObject, siteId);
    }

    @Override
    public List<String> syncCarryOver(SyncPurchaseOrderCarryOverDTO syncCarryOverDTO) {
        return CommonHelper.loadCarryOvers(syncCarryOverDTO, carryOverRepository);
    }


    @Override
    public void deleteCarryOverOnLoad(SyncPurchaseOrderCarryOverDTO syncCarryOverDTO) {
        if (CarryOverActionType.LOAD.toString().equals(syncCarryOverDTO.getAction())) {
            carryOverRepository.deleteAllCarryOver();
        }
    }

    @Override
    public List<InternalOrderSapDTO> createInternalOrderSapData(String xmlContent) {
        List<InternalOrderSapDTO> internalOrderSapDTOs = internalOrderSapTransformer.transformInterOrderSap(xmlContent);
        createInternalOrderSaps(internalOrderSapDTOs);
        return internalOrderSapDTOs;
    }

    private void createInternalOrderSaps(List<InternalOrderSapDTO> internalOrderSapDTOs) {
        if (internalOrderSapDTOs != null && !internalOrderSapDTOs.isEmpty()) {
            for (InternalOrderSapDTO internalOrderSapDTO : internalOrderSapDTOs) {
                InternalOrderSap internalOrderSap = new InternalOrderSap();
                internalOrderSap.setCode(internalOrderSapDTO.getCode());
                addInternalOrderSap(internalOrderSap);
            }
        }
    }

    @Override
    public void addInternalOrderSap(InternalOrderSap internalOrderSap) {
        internalOrderSapRepo.save(internalOrderSap);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','IT_SUPPORT')")
    public List<InternalOrderSap> getAllInternalOrderNoSAP(PageObject pageObject, String code) {
        return internalOrderSapRepo.findInternalOrderSap(pageObject, code);
    }

    public void addSyncCompanyCode(SyncCompanyCodeDTO syncCompanyCodeDTO) {
        List<CompanyCodeDTO> companyCodeDtos = syncCompanyCodeDTO.getCompanyCodeDTO();
        if (companyCodeDtos != null && !companyCodeDtos.isEmpty()) {
            for (CompanyCodeDTO companyCodeDto : companyCodeDtos) {
                createCompanyCode(companyCodeDto);
            }
        }
    }

    private void createCompanyCode(CompanyCodeDTO companyCodeDto) {
        CompanyCode companyCode = new CompanyCode();
        companyCode.setCode(companyCodeDto.getCode());
        companyCode.setName(companyCodeDto.getName());
        companyCode.setDefaultCurrency(companyCodeDto.getDefaultCurrency());
        companyCode.setSapPurchaseOrg(companyCodeDto.getSapPurchaseOrg());
        companyCode.setSapQuantityBlockReceiverId(companyCodeDto.getSapQuantityBlockReceiverId());
        companyCode.setReceivingGoods(companyCodeDto.isReceivingGoods());
        companyCode.setSendGRtoSAP(companyCodeDto.isSendGRtoSAP());
        companyCode.setSendPOtoSAP(companyCodeDto.isSendPOtoSAP());
        
        CompanyGroup companyGroup = getCompanyGroupByCode(companyCodeDto.getCompanyGroupCode());
        if (companyGroup != null) {
            companyCode.setCompanyGroup(companyGroup);
            companyGroup.getCompanyCodes().add(companyCode);
        } else {
            companyGroup = new CompanyGroup();
            companyGroup.setCode(companyCodeDto.getCompanyGroupCode());
            addCompanyGroup(companyGroup);
            companyCode.setCompanyGroup(companyGroup);
            companyGroup.getCompanyCodes().add(companyCode);
        }
        companyCodeRepo.save(companyCode);
    }

    @Override
    public void addCompanyCode(CompanyCode companyCode) {
        companyCodeRepo.save(companyCode);
    }

    @Override
    public List<CompanyCode> findAllCompanyCodes() {
        return companyCodeRepo.findAll();
    }

    @Override
    public void syncCostCenter(SyncCostCenterDTO syncCostCenterDTO) {
        if (syncCostCenterDTO != null) {
            List<CostCenterItemDTO> costCenterItems = syncCostCenterDTO.getCostCenterItems();
            CompanyCode companyCode = companyCodeRepo.findCompanyCodeByCode(costCenterItems.get(0).getCompanyCode());
            if (companyCode == null) {
                return;
            }
            if (CostCenterActionType.LOAD.equals(costCenterItems.get(0).getAction())) {
                costCenterRepo.deleteCostCentersByCompanyCode(companyCode.getCode());
                for (CostCenterItemDTO costCenterItemDTO : costCenterItems) {
                    if (!isCostCenterLocked(costCenterItemDTO)) {
                        CostCenter costCenter = CostCenterHelper.transformCostCenterItemDTOTpEty(costCenterItemDTO);
                        costCenter.setCompanyCode(companyCode);
                        costCenterRepo.save(costCenter);
                    }
                }
                return;
            }
            for (CostCenterItemDTO costCenterItemDTO : costCenterItems) {
                CostCenterActionType action = costCenterItemDTO.getAction();
                // Find highest validity date CC and less than incoming EffectiveStartDate.
                CostCenter existingCostCenter = costCenterRepo.findCostCenterByCompanyCodeAndStartDate(companyCode.getCode(),
                                                                                                       costCenterItemDTO.getCostCenter(),
                                                                                                       costCenterItemDTO.getEffectiveStartDate());
                boolean isAddNew = true;
                if (action.equals(CostCenterActionType.DELETE)) {
                    if (existingCostCenter != null) {
                        costCenterRepo.delete(existingCostCenter);
                    }
                } else if (action.equals(CostCenterActionType.CREATE) || action.equals(CostCenterActionType.UPDATE)) {
                    if (existingCostCenter != null) {
                        if (isCostCenterLocked(costCenterItemDTO)) {
                            costCenterRepo.delete(existingCostCenter);
                            continue;
                        }
                        if (existingCostCenter.getEffectiveStartDate().equals(costCenterItemDTO.getEffectiveStartDate())) {
                            isAddNew = false;
                            // Update exiting cost center
                            costCenterRepo.save(CostCenterHelper.copyCostCenterEty(existingCostCenter, costCenterItemDTO));
                        } else {
                            // Set existingCostCenter.endDate = incoming startdate-1
                            existingCostCenter.setEffectiveEndDate(DateUtil.getPreviousDate(costCenterItemDTO.getEffectiveStartDate()));
                            costCenterRepo.save(existingCostCenter);
                        }
                    }
                    if (!isCostCenterLocked(costCenterItemDTO) && isAddNew) {
                        CostCenter incomingCostCenter = CostCenterHelper.transformCostCenterItemDTOTpEty(costCenterItemDTO);
                        incomingCostCenter.setCompanyCode(companyCode);
                        costCenterRepo.save(incomingCostCenter);
                    }
                }
            }
        }
    }
    
    private boolean isCostCenterLocked(CostCenterItemDTO costCenterItemDTO) {
        if (costCenterItemDTO.isPrimaryPostings() && costCenterItemDTO.isPlanning() && costCenterItemDTO.isActualSecondaryCosts()
                && costCenterItemDTO.isPlanSecondaryCosts()) {
            return true;
        }
        return false;
    }

    @Override
    public void syncWBSElement(SyncWBSElementDTO syncWbsElementDTO) {
        Set<WbsElementDTO> incomingWbsElementDtos = new HashSet<WbsElementDTO>(syncWbsElementDTO.getWbsElementDTO());
        if (incomingWbsElementDtos != null && !incomingWbsElementDtos.isEmpty()) {
            CompanyCode companyCode = companyCodeRepo.findCompanyCodeByCode(incomingWbsElementDtos.iterator().next().getCompanyCode());
            if (companyCode != null) {
                LOGGER.info("Updating WBS codes for companyCode " + companyCode.getCode());
                List<WbsElement> existingWbsElements = wbsElementRepo.findWbsElementByCompanyCode(companyCode.getCode());
                if (existingWbsElements != null && !existingWbsElements.isEmpty()) {
                    deleteWbsElements(CommonHelper.transformWbsElementEtyToDTO(existingWbsElements));
                }
                addWbsElements(incomingWbsElementDtos, companyCode);
            }
        }
    }

    private void deleteWbsElements(List<WbsElementDTO> wbsElementDtos) {
        for (WbsElementDTO wbsElementDto : wbsElementDtos) {
            wbsElementRepo.delete(wbsElementDto.getId());
        }
    }

    private void addWbsElements(Set<WbsElementDTO> wbsElementDtos, CompanyCode companyCode) {
        for (WbsElementDTO wbsElementDto : wbsElementDtos) {
            String wbs = wbsElementDto.getWbs();
            if (wbs != null && wbs.length() >= 5) {
                if (!isWbsValid(wbs)) {
                    continue;
                }

            }
            
            WbsElement wbsElement = new WbsElement();
            wbsElement.setWbs(wbsElementDto.getWbs());
            wbsElement.setCompanyCode(companyCode);
            wbsElement.setDescription(wbsElementDto.getDescription());
            wbsElement.setProjectId(wbsElementDto.getProjectId());

            wbsElementRepo.save(wbsElement);
        }
    }

    /**
     * GTT - W10, G10, T10
     * Buses - 310 
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean isWbsValid(String wbs) {
        String toValidate = wbs.substring(2, 5);
        return (toValidate.equalsIgnoreCase("W10") || toValidate.equalsIgnoreCase("G10") 
                || toValidate.equalsIgnoreCase("T10") || toValidate.equalsIgnoreCase("310"));
    }

    @Override
    public void addGlAccount(GlAccount glAccount) {
        glAccountRepo.save(glAccount);
    }

    @Override
    public void updateGlAccount(GlAccount glAccount) {
        glAccountRepo.save(glAccount);
    }

    @Override
    public void addGlAccounts(List<GlAccount> glAccounts) {
        for (GlAccount glAccount : glAccounts) {
            addGlAccount(glAccount);
        }
    }

    @Override
    public List<GlAccount> createGlAccounts(List<GlAccountDTO> glAccountDTOs) {
        List<GlAccount> listOfGlAccounts = new ArrayList<GlAccount>();
        for (GlAccountDTO glAccountDto : glAccountDTOs) {
            CompanyCode companyCode = companyCodeRepo.findCompanyCodeByCode(glAccountDto.getCompanyCode());
            if (companyCode != null) {
                GlAccount glAccount = new GlAccount();
                glAccount.setAccountNumber(glAccountDto.getAccountNumber());
                glAccount.setCompanyCode(companyCode);
                glAccount.setAccountName(glAccountDto.getAccountName());
                glAccount.setAccountDescription(glAccountDto.getAccountDescription());
                listOfGlAccounts.add(glAccount);
            }
        }
        addGlAccounts(listOfGlAccounts);
        return listOfGlAccounts;
    }

    @Override
    public List<CostCenter> getAllCostCenters() {
        return costCenterRepo.findAll();
    }

    @Override
    public List<GlAccount> getAllGlAccounts() {
        return glAccountRepo.findAll();
    }

    @Override
    public List<WbsElement> getAllWbsElements() {
        return wbsElementRepo.findAll();
    }

    @Override
    public CompanyCode findCompanyCodeByCode(String companyCode) {
        return companyCodeRepo.findCompanyCodeByCode(companyCode);
    }

    @Override
    public List<ProjectDTO> getProjectsByCompanyCode(PageObject pageObject, String companyCode, String projectID) {
        return wbsElementRepo.getProjectsByCompanyCode(pageObject, Arrays.asList(companyCode), projectID);
    }

    @Override
    public PageObject findWbsElementsByCompanyCodeAndProjectId(String companyCode, String projectId, String wbs, PageObject pageObject) {
        return wbsElementRepo.findWbsElementsByCompanyCodeAndProjectId(companyCode, projectId, wbs, pageObject);
    }

    @Override
    public PageObject findCostCentersByCompanyCode(String companyCode, String searchString, PageObject pageObject) {
        return costCenterRepo.findCostCentersByCompanyCode(companyCode, searchString, pageObject);
    }

    @Override
    public PageObject findGlAccountsByCompanyCode(String companyCode, String glAccountStr, PageObject pageObject) {
        return glAccountRepo.findGlAccountsByCompanyCode(companyCode, glAccountStr, pageObject);
    }

    @Override
    public UnitOfMeasure findUnitOfMeasureByCode(String code) {
        return unitOfMeasureRepo.findUnitOfMeasureByCode(code);
    }

    @Override
    public SupplierCounterPart findSupplierCounterPartByMaterialUserId(String materialUserId) {
        return suppCounterPartRepo.findSupplierCounterPartByMaterialUserId(materialUserId);
    }

    @Override
    public String getCompanyCodeFromMaterialUserId(String materialUserId) {
        SupplierCounterPart supplierCounterPart = findSupplierCounterPartByMaterialUserId(materialUserId);
        if (supplierCounterPart != null) {
            return supplierCounterPart.getCompanyCode();
        }
        return "";
    }

    @Override
    public WbsElement findWBSElementByProjectIDandCompanyCode(String companyCode, String projectId, String wbsCode) {
        return wbsElementRepo.findWbsElementByCompanyCodeAndProjectId(companyCode, projectId, wbsCode);
    }

    @Override
    public void addQualityDocument(QualityDocument qualityDocumentEntity) {
        qualityDocumentRepo.save(qualityDocumentEntity);
    }

    @Override
    public List<QualityDocument> findAllQualityDocuments() {
        return qualityDocumentRepo.findAll();
    }

    @Override
    public void addCurrencyRate(CurrencyRate currencyRate) {
        currencyRateRepo.save(currencyRate);
    }

    @Override
    public List<Currency> findAllCurreny() {
        return currencyRepo.findAll();
    }
    
    @Override
    public Currency findCurrencyByCode(String code) {
        return currencyRepo.findByCode(code);
    }

    @Override
    public List<Traceability> getMaterialLineTraceabilitys(long materialOid) {
        return traceabilityRepository.getMaterialLineTraceabilitys(materialOid);
    }

    @Override
    public List<Traceability> getOrderLineTraceabilitys(long orderLineOid) {
        return traceabilityRepository.getOrderLineTraceabilitys(orderLineOid);
    }

    @Override
    public CompanyGroup getCompanyGroupByCode(String companyCodeGroup) {
        return companyCodeRepo.getCompanyGroupByCode(companyCodeGroup);
    }

    @Override
    public CompanyGroup addCompanyGroup(CompanyGroup companyGroup) {
        return companyCodeRepo.save(companyGroup);
    }

    @Override
    public List<String> getCompanyCodeCodes(List<String> companyGroupCodes) {
        List<String> companyCodes = new ArrayList<String>();
        for (String companyGroupCode : companyGroupCodes) {
            CompanyGroup companyGroup = companyCodeRepo.getCompanyGroupByCode(companyGroupCode);
            if (companyGroup != null) {
                for (CompanyCode companyCode : companyGroup.getCompanyCodes()) {
                    companyCodes.add(companyCode.getCode());
                }
            }
        }
        return companyCodes;
    }

    @Override
    public List<SupplierCounterPart> getSupplierCounterPartsByCompanyCode(String companyCode) {
        return suppCounterPartRepo.getSupplierCounterPartsByCompanyCode(companyCode);
    }
    
    @Override
    public void addConversionUnit(ConversionUnit conversionUnit) {
        conversionUnitRepository.save(conversionUnit);
    }
    
    @Override
    public ConversionUnit findConversionUnit(String applicationFrom, String applicationTo, String codeToConvert) {
        return conversionUnitRepository.findConversionUnit(applicationFrom, applicationTo, codeToConvert);
    }
    
    @Override
    public Site getSiteBySiteCode(String siteCode) {
        return siteRepo.getSiteBySiteCode(siteCode);
    }
    
    @Override
    public List<ProjectDTO> getProjectsByCompanyCodes(PageObject pageObject, List<String> companyCodes, String projectId) {
        return wbsElementRepo.getProjectsByCompanyCode(pageObject, companyCodes, projectId);
    }
    
    @Override
    public CurrencyRate getValidCurrencyRate(String currencyCode) {
        return currencyRateRepo.findValidCurrencyRate(currencyCode);
    }
    
    @Override
    public List<String> getSiteIdsByCompanyCodes(Collection<String> companyCodeCodes) {
        return siteRepo.getSiteIdsByCompanyCodes(companyCodeCodes);
    }

    @Override
    public boolean verifyApplicationStatus() {
        try {
            //Read Db
            List<CompanyCode> companyCodeList = companyCodeRepo.findAll();
            if (companyCodeList == null || companyCodeList.isEmpty()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Currency getCurrencyByCode(String code) {
        return currencyRepo.findByCode(code);
    }

}
