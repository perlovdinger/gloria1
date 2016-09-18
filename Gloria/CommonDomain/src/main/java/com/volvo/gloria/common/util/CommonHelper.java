package com.volvo.gloria.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.common.c.CarryOverActionType;
import com.volvo.gloria.common.c.dto.CostCenterDTO;
import com.volvo.gloria.common.c.dto.DangerousGoodsDTO;
import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamDTO;
import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamFilterDTO;
import com.volvo.gloria.common.c.dto.GlAccountDTO;
import com.volvo.gloria.common.c.dto.MaterialLineTracebilityDTO;
import com.volvo.gloria.common.c.dto.PartAffiliationDTO;
import com.volvo.gloria.common.c.dto.QualityDocumentDTO;
import com.volvo.gloria.common.c.dto.SiteDTO;
import com.volvo.gloria.common.c.dto.SupplierCounterPartDTO;
import com.volvo.gloria.common.c.dto.UnitOfMeasureDTO;
import com.volvo.gloria.common.c.dto.WbsElementDTO;
import com.volvo.gloria.common.carryover.c.dto.CarryOverItemDTO;
import com.volvo.gloria.common.carryover.c.dto.SyncPurchaseOrderCarryOverDTO;
import com.volvo.gloria.common.companycode.c.dto.CompanyCodeDTO;
import com.volvo.gloria.common.d.entities.CarryOver;
import com.volvo.gloria.common.d.entities.CompanyCode;
import com.volvo.gloria.common.d.entities.CostCenter;
import com.volvo.gloria.common.d.entities.DangerousGoods;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeamFilter;
import com.volvo.gloria.common.d.entities.GlAccount;
import com.volvo.gloria.common.d.entities.PartAffiliation;
import com.volvo.gloria.common.d.entities.QualityDocument;
import com.volvo.gloria.common.d.entities.Site;
import com.volvo.gloria.common.d.entities.SupplierCounterPart;
import com.volvo.gloria.common.d.entities.Traceability;
import com.volvo.gloria.common.d.entities.UnitOfMeasure;
import com.volvo.gloria.common.d.entities.WbsElement;
import com.volvo.gloria.common.repositories.b.CarryOverRepository;
import com.volvo.group.purchaseorder._1_0.OrderModeType;

/**
 * Helper class for Common domain related entities.
 * 
 */
public class CommonHelper {
    protected CommonHelper() {
    }

    public static SupplierCounterPartDTO transformAsDTO(SupplierCounterPart supplierCounterPart) {
        if (supplierCounterPart != null) {
            SupplierCounterPartDTO supplierCounterPartDTO = new SupplierCounterPartDTO();
            supplierCounterPartDTO.setId(supplierCounterPart.getSupplierCounterPartOid());
            supplierCounterPartDTO.setVersion(supplierCounterPart.getVersion());
            supplierCounterPartDTO.setShipToId(supplierCounterPart.getShipToId());
            supplierCounterPartDTO.setPpSuffix(supplierCounterPart.getPpSuffix());
            supplierCounterPartDTO.setTransitAddressId(supplierCounterPart.getTransitAddressId());
            supplierCounterPartDTO.setDomesticInvoiceId(supplierCounterPart.getDomesticInvoiceId());
            supplierCounterPartDTO.setInternationalInvoiceId(supplierCounterPart.getInternationalInvoiceId());
            supplierCounterPartDTO.setComment(supplierCounterPart.getComment());
            supplierCounterPartDTO.setMaterialUserId(supplierCounterPart.getMaterialUserId());

            return supplierCounterPartDTO;
        }
        return null;
    }

    public static List<SupplierCounterPartDTO> transformAsDTO(List<SupplierCounterPart> supplierCounterParts) {
        List<SupplierCounterPartDTO> supplierCounterPartDTOs = new ArrayList<SupplierCounterPartDTO>();
        if (supplierCounterParts != null && !supplierCounterParts.isEmpty()) {
            for (SupplierCounterPart supplierCounterPart : supplierCounterParts) {
                supplierCounterPartDTOs.add(transformAsDTO(supplierCounterPart));
            }
        }
        return supplierCounterPartDTOs;
    }

    public static PartAffiliationDTO transformAsPartAffiliationDTO(PartAffiliation partAffiliation) {
        if (partAffiliation != null) {
            PartAffiliationDTO partAffiliationDTO = new PartAffiliationDTO();
            partAffiliationDTO.setId(partAffiliation.getPartAffiliationOID());
            partAffiliationDTO.setCode(partAffiliation.getCode());
            partAffiliationDTO.setName(partAffiliation.getName());
            partAffiliationDTO.setDisplaySeq(String.valueOf(partAffiliation.getDisplaySeq()));
            partAffiliationDTO.setRequestable(partAffiliation.isRequestable());
            return partAffiliationDTO;
        }
        return null;
    }

    public static List<PartAffiliationDTO> transformAsPartAffiliationDTO(List<PartAffiliation> partAffiliations) {
        List<PartAffiliationDTO> partAffiliationDTOs = new ArrayList<PartAffiliationDTO>();
        if (partAffiliations != null && !partAffiliations.isEmpty()) {
            for (PartAffiliation partAffiliation : partAffiliations) {
                partAffiliationDTOs.add(transformAsPartAffiliationDTO(partAffiliation));
            }
        }
        return partAffiliationDTOs;
    }

    public static UnitOfMeasureDTO transformAsUnitOfMeasureDTO(UnitOfMeasure unitOfMeasure) {
        if (unitOfMeasure != null && unitOfMeasure.isGloriaCode()) {
            UnitOfMeasureDTO unitOfMeasureDTO = new UnitOfMeasureDTO();
            unitOfMeasureDTO.setId(unitOfMeasure.getUnitOfMeasureOID());
            unitOfMeasureDTO.setCode(unitOfMeasure.getCode());
            unitOfMeasureDTO.setName(unitOfMeasure.getName());
            unitOfMeasureDTO.setDisplaySeq(String.valueOf(unitOfMeasure.getDisplaySeq()));
            return unitOfMeasureDTO;
        }
        return null;
    }

    public static List<UnitOfMeasureDTO> transformAsUnitOfMeasureDTO(List<UnitOfMeasure> unitOfMeasures) {
        List<UnitOfMeasureDTO> unitOfMeasureDTOs = new ArrayList<UnitOfMeasureDTO>();
        if (unitOfMeasures != null && !unitOfMeasures.isEmpty()) {
            for (UnitOfMeasure unitOfMeasure : unitOfMeasures) {
                unitOfMeasureDTOs.add(transformAsUnitOfMeasureDTO(unitOfMeasure));
            }
        }
        return unitOfMeasureDTOs;
    }

    public static DeliveryFollowUpTeamDTO transformAsDeliveryFollowUpTeamDTO(DeliveryFollowUpTeam deliveryFollowUpTeam) {
        if (deliveryFollowUpTeam != null) {
            DeliveryFollowUpTeamDTO deliveryFollowUpTeamDTO = new DeliveryFollowUpTeamDTO();
            deliveryFollowUpTeamDTO.setId(deliveryFollowUpTeam.getDeliveryFollowUpTeamOid());
            deliveryFollowUpTeamDTO.setVersion(deliveryFollowUpTeam.getVersion());
            deliveryFollowUpTeamDTO.setName(deliveryFollowUpTeam.getName());
            deliveryFollowUpTeamDTO.setDefaultDcUserid(deliveryFollowUpTeam.getDefaultDCUserId());
            deliveryFollowUpTeamDTO.setFollowUpType(deliveryFollowUpTeam.getFollowUpType().name());
            return deliveryFollowUpTeamDTO;
        }
        return null;
    }

    public static DeliveryFollowUpTeamFilterDTO transformAsDeliveryFollowUpTeamFilterDTO(DeliveryFollowUpTeamFilter deliveryFollowUpTeamFilter) {
        if (deliveryFollowUpTeamFilter != null) {
            DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO = new DeliveryFollowUpTeamFilterDTO();
            deliveryFollowUpTeamFilterDTO.setId(deliveryFollowUpTeamFilter.getDeliveryFollowUpTeamFilterOid());
            deliveryFollowUpTeamFilterDTO.setVersion(deliveryFollowUpTeamFilter.getVersion());
            deliveryFollowUpTeamFilterDTO.setSuffix(deliveryFollowUpTeamFilter.getSuffix());
            deliveryFollowUpTeamFilterDTO.setSupplierId(deliveryFollowUpTeamFilter.getSupplierId());
            deliveryFollowUpTeamFilterDTO.setProjectId(deliveryFollowUpTeamFilter.getProjectId());
            deliveryFollowUpTeamFilterDTO.setDeliveryControllerUserId(deliveryFollowUpTeamFilter.getDeliveryControllerUserId());
            return deliveryFollowUpTeamFilterDTO;
        }
        return null;
    }

    public static List<DeliveryFollowUpTeamFilterDTO> transformAsDeliveryFollowUpTeamFilterDTO(List<DeliveryFollowUpTeamFilter> deliveryFollowUpTeamFilters) {
        List<DeliveryFollowUpTeamFilterDTO> deliveryFollowUpTeamFilterDTOs = new ArrayList<DeliveryFollowUpTeamFilterDTO>();
        if (deliveryFollowUpTeamFilters != null && !deliveryFollowUpTeamFilters.isEmpty()) {
            for (DeliveryFollowUpTeamFilter deliveryFollowUpTeamFilter : deliveryFollowUpTeamFilters) {
                deliveryFollowUpTeamFilterDTOs.add(transformAsDeliveryFollowUpTeamFilterDTO(deliveryFollowUpTeamFilter));
            }
        }
        return deliveryFollowUpTeamFilterDTOs;
    }

    public static SiteDTO transforToSiteDTO(Site site) {
        if (site != null) {
            SiteDTO siteDTO = new SiteDTO();
            siteDTO.setId(site.getId());
            siteDTO.setSiteId(site.getSiteId());
            siteDTO.setSiteCode(site.getSiteCode());
            siteDTO.setSiteName(site.getSiteName());
            siteDTO.setAddress(site.getAddress());
            siteDTO.setPhone(site.getPhone());
            siteDTO.setCountryCode(site.getCountryCode());
            siteDTO.setCompanyCode(site.getCompanyCode());
            siteDTO.setJointVenture(site.isJointVenture());
            siteDTO.setShipToSite(site.isShipToSite());
            siteDTO.setShipToType(site.getShipToType());
           
            siteDTO.setBuildSite(site.isBuildSite());
            siteDTO.setBuildSiteType(site.getBuildSiteType());
            return siteDTO;
        }
        return null;
    }

    public static List<SiteDTO> transforToSiteDTOs(List<Site> sites) {
        List<SiteDTO> siteDTOs = new ArrayList<SiteDTO>();
        if (sites != null && !sites.isEmpty()) {
            for (Site site : sites) {
                SiteDTO siteDTO = transforToSiteDTO(site);
                siteDTOs.add(siteDTO);
            }
        }
        return siteDTOs;
    }

    public static DangerousGoodsDTO transformAsDTO(DangerousGoods dangerousGoods) {
        if (dangerousGoods != null) {
            DangerousGoodsDTO dangerousGoodsDTO = new DangerousGoodsDTO();
            dangerousGoodsDTO.setId(dangerousGoods.getId());
            dangerousGoodsDTO.setVersion(dangerousGoods.getVersion());
            dangerousGoodsDTO.setName(dangerousGoods.getName());
            return dangerousGoodsDTO;
        }
        return null;
    }

    public static List<DangerousGoodsDTO> transformToDangerousGoodsDTOs(List<DangerousGoods> dangerousGoods) {
        List<DangerousGoodsDTO> dangerousGoodsDTOs = new ArrayList<DangerousGoodsDTO>();
        if (dangerousGoods != null && !dangerousGoods.isEmpty()) {
            for (DangerousGoods dangerousGood : dangerousGoods) {
                dangerousGoodsDTOs.add(transformAsDTO(dangerousGood));
            }
        }
        return dangerousGoodsDTOs;
    }

    public static List<WbsElementDTO> transformWbsElementEtyToDTO(List<WbsElement> wbsElements) {
        List<WbsElementDTO> wbsElementDTOs = new ArrayList<WbsElementDTO>();
        if (wbsElements != null && !wbsElements.isEmpty()) {
            for (WbsElement wbsElement : wbsElements) {
                wbsElementDTOs.add(transformWbsElementEtyAsDTO(wbsElement));
            }
        }
        return wbsElementDTOs;
    }

    private static WbsElementDTO transformWbsElementEtyAsDTO(WbsElement wbsElement) {
        if (wbsElement != null) {
            WbsElementDTO wbsElementDTO = new WbsElementDTO();
            wbsElementDTO.setId(wbsElement.getWbsElementOid());
            wbsElementDTO.setWbs(wbsElement.getWbs());
            wbsElementDTO.setDescription(wbsElement.getDescription());
            wbsElementDTO.setProjectId(wbsElement.getProjectId());
            return wbsElementDTO;
        }
        return null;
    }

    public static List<CostCenterDTO> transformCCEtyToDTOs(List<CostCenter> costCenters) {
        List<CostCenterDTO> costCenterDTOs = new ArrayList<CostCenterDTO>();
        if (costCenters != null && !costCenters.isEmpty()) {
            for (CostCenter costCenter : costCenters) {
                costCenterDTOs.add(transformCCEtyToDTO(costCenter));
            }
        }
        return costCenterDTOs;
    }

    private static CostCenterDTO transformCCEtyToDTO(CostCenter costCenter) {
        if (costCenter != null) {
            CostCenterDTO costCenterDTO = new CostCenterDTO();
            costCenterDTO.setId(costCenter.getCostCenterOid());
            costCenterDTO.setCostCenter(costCenter.getCostCenter());
            costCenterDTO.setDescriptionShort(costCenter.getDescriptionShort());
            return costCenterDTO;
        }
        return null;
    }

    public static List<GlAccountDTO> transformGlaccountEtyToDTO(List<GlAccount> glAccounts) {
        List<GlAccountDTO> glAccountDTOs = new ArrayList<GlAccountDTO>();
        if (glAccounts != null && !glAccounts.isEmpty()) {
            for (GlAccount glAccount : glAccounts) {
                glAccountDTOs.add(transformGlAccountEtyAsDTO(glAccount));
            }
        }
        return glAccountDTOs;
    }

    private static GlAccountDTO transformGlAccountEtyAsDTO(GlAccount glAccount) {
        if (glAccount != null) {
            GlAccountDTO glAccountDTO = new GlAccountDTO();
            glAccountDTO.setId(glAccount.getglAccountOid());
            glAccountDTO.setAccountNumber(glAccount.getAccountNumber());
            glAccountDTO.setAccountName(glAccount.getAccountName());
            glAccountDTO.setAccountDescription(glAccount.getAccountDescription());

            return glAccountDTO;
        }
        return null;
    }

    public static List<CompanyCodeDTO> transformCompanyCodeToDTO(List<CompanyCode> companycodes) {
        List<CompanyCodeDTO> companyCodeDTOs = new ArrayList<CompanyCodeDTO>();
        if (companycodes != null && !companycodes.isEmpty()) {
            for (CompanyCode companyCode : companycodes) {
                companyCodeDTOs.add(transformAsDTO(companyCode));
            }

        }
        return companyCodeDTOs;
    }

    public static CompanyCodeDTO transformAsDTO(CompanyCode companyCode) {
        if (companyCode != null) {
            CompanyCodeDTO companyCodeDTO = new CompanyCodeDTO();
            companyCodeDTO.setCode(companyCode.getCode());
            companyCodeDTO.setId(companyCode.getCompanyCodeOid());
            companyCodeDTO.setName(companyCode.getName());
            companyCodeDTO.setVersion(companyCode.getVersion());
            companyCodeDTO.setDefaultCurrency(companyCode.getDefaultCurrency());
            companyCodeDTO.setSapPurchaseOrg(companyCode.getSapPurchaseOrg());
            companyCodeDTO.setSapQuantityBlockReceiverId(companyCode.getSapQuantityBlockReceiverId());
            companyCodeDTO.setReceivingGoods(companyCode.isReceivingGoods());
            companyCodeDTO.setSendGRtoSAP(companyCode.isSendGRtoSAP());
            companyCodeDTO.setSendPOtoSAP(companyCode.isSendPOtoSAP());
            return companyCodeDTO;
        }
        return null;
    }

    private static QualityDocumentDTO transformToQualityDocumentDTO(QualityDocument qualityDocument) {
        if (qualityDocument != null) {
            QualityDocumentDTO qualityDocumentDTO = new QualityDocumentDTO();
            qualityDocumentDTO.setCode(qualityDocument.getCode());
            qualityDocumentDTO.setDisplaySeq(qualityDocument.getDisplaySeq());
            qualityDocumentDTO.setId(qualityDocument.getId());
            qualityDocumentDTO.setName(qualityDocument.getName());
            return qualityDocumentDTO;
        }
        return null;
    }

    public static List<QualityDocumentDTO> transformToQualityDocumentDTOs(List<QualityDocument> qualityDocuments) {
        List<QualityDocumentDTO> qualityDocumentDTOs = new ArrayList<QualityDocumentDTO>();
        for (QualityDocument qualityDocument : qualityDocuments) {
            QualityDocumentDTO qualityDocumentDTO = transformToQualityDocumentDTO(qualityDocument);
            qualityDocumentDTOs.add(qualityDocumentDTO);
        }
        return qualityDocumentDTOs;
    }

    public static List<MaterialLineTracebilityDTO> transformAsMaterialLineTraceabilityDTOs(List<Traceability> traceabilitys) {
        List<MaterialLineTracebilityDTO> traceabilityDTOs = new ArrayList<MaterialLineTracebilityDTO>();
        if (traceabilitys != null && !traceabilitys.isEmpty()) {
            for (Traceability traceability : traceabilitys) {
                traceabilityDTOs.add(transformAsMaterialLineTraceabilityDTO(traceability));
            }
        }
        return traceabilityDTOs;
    }

    private static MaterialLineTracebilityDTO transformAsMaterialLineTraceabilityDTO(Traceability traceability) {
        MaterialLineTracebilityDTO traceabilityDTO = new MaterialLineTracebilityDTO();
        traceabilityDTO.setId(traceability.getTraceabilityOid());   
        traceabilityDTO.setMaterialOID(traceability.getMaterialOID());
        traceabilityDTO.setLoggedTime(traceability.getLoggedTime());
        traceabilityDTO.setAction(traceability.getAction());
        traceabilityDTO.setActionDetail(traceability.getActionDetail());
        traceabilityDTO.setMlQuantity(traceability.getMlQuantity());
        traceabilityDTO.setMlStatus(traceability.getMlStatus());
        traceabilityDTO.setOrderNo(traceability.getOrderNo());
        traceabilityDTO.setUserId(traceability.getUserId());
        traceabilityDTO.setUserName(traceability.getUserName());
        traceabilityDTO.setI18MessageCode(traceability.getI18MessageCode());
        return traceabilityDTO;
    }

    public static List<String> loadCarryOvers(SyncPurchaseOrderCarryOverDTO syncCarryOverDTO, CarryOverRepository carryOverRepository) {
        String action = syncCarryOverDTO.getAction();
        List<String> unprocessedMessageDocIds = new ArrayList<String>();
        List<CarryOverItemDTO> carryOverList = removeDuplicates(syncCarryOverDTO.getCarryOverItemDTOs());

        for (CarryOverItemDTO carryOverItem : carryOverList) {
           
            if (StringUtils.isBlank(carryOverItem.getPartVersion())) {
                continue;
            }
            if (!OrderModeType.STAND_ALONE.value().equals(carryOverItem.getOrderMode())) {
                if (CarryOverActionType.LOAD.toString().equals(action)) {

                    CarryOver existingCarryOver = carryOverRepository.findUniqueCarryOver(carryOverItem.getCustomerId(), carryOverItem.getSupplierId(),
                                                                                          carryOverItem.getPartAffliation(), carryOverItem.getPartNumber(),
                                                                                          carryOverItem.getPartVersion());
                    if (existingCarryOver == null) {
                        // Just Add
                        carryOverRepository.save(CarryOverHelper.transformCarryOverItemDTOTpEty(carryOverItem, null));
                    } 

                } else if (CarryOverActionType.CHANGE.toString().equals(action) || CarryOverActionType.ADD.toString().equals(action)) {
                    // Find if found update otherwise add
                    CarryOver existingCarryOver = carryOverRepository.findUniqueCarryOver(carryOverItem.getCustomerId(), carryOverItem.getSupplierId(),
                                                                                          carryOverItem.getPartAffliation(), carryOverItem.getPartNumber(),
                                                                                          carryOverItem.getPartVersion());
                    carryOverRepository.save(CarryOverHelper.transformCarryOverItemDTOTpEty(carryOverItem, existingCarryOver));
                } else if (CarryOverActionType.DELETE.toString().equals(action)) {
                    // Delete
                    CarryOver existingCarryOver = carryOverRepository.findUniqueCarryOver(carryOverItem.getCustomerId(), carryOverItem.getSupplierId(),
                                                                                          carryOverItem.getPartAffliation(), carryOverItem.getPartNumber(),
                                                                                          carryOverItem.getPartVersion());
                    if (existingCarryOver != null) {
                        carryOverRepository.delete(existingCarryOver.getCarryOverOid());
                    }
                }
            } else {
                unprocessedMessageDocIds.add(carryOverItem.getDocumentId());
            }

        }
        return unprocessedMessageDocIds;
    }

    private static List<CarryOverItemDTO> removeDuplicates(List<CarryOverItemDTO> list) {
        Map<String, CarryOverItemDTO> map = new HashMap<String, CarryOverItemDTO>();
        for (Iterator<CarryOverItemDTO> iterator = list.iterator(); iterator.hasNext();) {
            CarryOverItemDTO carryOverItemDTO = (CarryOverItemDTO) iterator.next();
            String key = carryOverItemDTO.getCustomerId() + ":" + carryOverItemDTO.getSupplierId() + ":" + carryOverItemDTO.getPartAffliation() + ":"
                    + carryOverItemDTO.getPartNumber() + ":" + carryOverItemDTO.getPartVersion();
            if (map.containsKey(key)) {
                // Keep newest
                CarryOverItemDTO storedCarryOver = (CarryOverItemDTO) map.get(key);
                if (storedCarryOver.getStartDate().before(carryOverItemDTO.getStartDate())) {
                    map.remove(key);
                    map.put(key, carryOverItemDTO);
                }
            } else {
                map.put(key, carryOverItemDTO);
            }

        }
        List<CarryOverItemDTO> carryOverList = new ArrayList<CarryOverItemDTO>();
        carryOverList.addAll(map.values());
        return carryOverList;

    }
}
