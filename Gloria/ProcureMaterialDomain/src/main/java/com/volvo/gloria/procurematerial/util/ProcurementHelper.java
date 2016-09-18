package com.volvo.gloria.procurematerial.util;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.KolaDomain;
import com.volvo.gloria.common.c.dto.CostCenterDTO;
import com.volvo.gloria.common.c.dto.GlAccountDTO;
import com.volvo.gloria.common.c.dto.InternalOrderSapDTO;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.c.dto.WbsElementDTO;
import com.volvo.gloria.common.d.entities.CostCenter;
import com.volvo.gloria.common.d.entities.GlAccount;
import com.volvo.gloria.common.d.entities.InternalOrderSap;
import com.volvo.gloria.common.d.entities.SupplierCounterPart;
import com.volvo.gloria.common.d.entities.WbsElement;
import com.volvo.gloria.common.purchaseorder.c.dto.StandAloneOrderHeaderDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.c.ProcureResponsibility;
import com.volvo.gloria.procurematerial.c.dto.ChangeIdDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialHeaderDTO;
import com.volvo.gloria.procurematerial.c.dto.PartAliasDTO;
import com.volvo.gloria.procurematerial.c.dto.ProcureLineDTO;
import com.volvo.gloria.procurematerial.c.dto.PurchaseOrganisationDTO;
import com.volvo.gloria.procurematerial.c.dto.ResponseDTO;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.MaterialPartAlias;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.PartAlias;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.PurchaseOrganisation;
import com.volvo.gloria.procurematerial.d.entities.Requisition;
import com.volvo.gloria.procurematerial.d.entities.Supplier;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusHelper;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineHelper;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.d.type.internalexternal.InternalExternal;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureTypeHelper;
import com.volvo.gloria.procurematerial.d.type.request.RequestType;
import com.volvo.gloria.procurerequest.c.dto.FinanceHeaderTransformerDTO;
import com.volvo.gloria.procurerequest.c.dto.PartAliasTransformerDTO;
import com.volvo.gloria.procurerequest.c.dto.RequestHeaderTransformerDTO;
import com.volvo.gloria.procurerequest.c.dto.RequestLineTransformerDTO;
import com.volvo.gloria.purchaseProxy.c.RequisitionDTO;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.tracing.c.GloriaTraceabilityConstants;

/**
 * Helper class for Procurement related entities.
 * 
 */
public class ProcurementHelper {
    public static final String COMMA = ",";
    public static final String NEWLINE = "\n";

    protected ProcurementHelper() {

    }

    public static List<MaterialHeaderDTO> transformAsDTOs(List<PageResults> pageResults) {
        List<MaterialHeaderDTO> materialHeaderDTOs = new ArrayList<MaterialHeaderDTO>();
        if (!pageResults.isEmpty()) {
            for (PageResults pageResult : pageResults) {
                materialHeaderDTOs.add((MaterialHeaderDTO) pageResult);
            }
        }
        return materialHeaderDTOs;
    }

    public static List<ProcureLineDTO> transformEntityToDTO(List<ProcureLine> procureLines) throws GloriaApplicationException {
        List<ProcureLineDTO> procureLineDTOs = new ArrayList<ProcureLineDTO>();
        if (!procureLines.isEmpty()) {
            for (ProcureLine procureLine : procureLines) {
                procureLineDTOs.add(transformAsDTO(procureLine));
            }
        }
        return procureLineDTOs;
    }

    public static ProcureLineDTO transformAsDTO(ProcureLine procureLine) throws GloriaApplicationException {
        if (procureLine != null) {
            ProcureLineDTO procureLineDTO = new ProcureLineDTO();

            procureLineDTO.setId(procureLine.getProcureLineOid());
            procureLineDTO.setVersion(procureLine.getVersion());

            setFinanceInformation(procureLine, procureLineDTO);

            procureLineDTO.setChangeRequestIds(procureLine.getChangeRequestIds());
            procureLineDTO.setDfuObjectNumber(procureLine.getDfuObjectNumber());
            setProcureResponsibilityInfo(procureLineDTO, procureLine);
            
            procureLineDTO.setDfuObjectNumber(procureLine.getDfuObjectNumber());
            if (procureLine.getStatus() != null) {
                procureLineDTO.setStatus(procureLine.getStatus().name());
            }
            procureLineDTO.setWarehouseComment(procureLine.getWarehouseComment());
            procureLineDTO.setMaxPrice(procureLine.getMaxPrice());

            setPartInformation(procureLine, procureLineDTO);

            procureLineDTO.setRequisitionId(procureLine.getRequisitionId());
            procureLineDTO.setOrderNo(procureLine.getOrderNo());
            procureLineDTO.setReferenceGroups(procureLine.getReferenceGroups());
            procureLineDTO.setReferenceIds(procureLine.getReferenceIds());
            procureLineDTO.setDangerousGoods(procureLine.getDangerousGoodsOID());
            if (procureLine.getProcureType() != null) {
                procureLineDTO.setProcureType(procureLine.getProcureType().name());
            }

            procureLineDTO.setBuyerCode(procureLine.getBuyerCode());
            procureLineDTO.setBuyerName(procureLine.getBuyerName());
            procureLineDTO.setPurchaseOrganisationCode(procureLine.getPurchaseOrgCode());
            procureLineDTO.setOrderStaDate(procureLine.getOrderStaDate());
            procureLineDTO.setSupplierCounterPartID(procureLine.getSupplierCounterPartOID());
            procureLineDTO.setRequiredStaDate(procureLine.getRequiredStaDate());
            Supplier supplier = procureLine.getSupplier();
            if (supplier != null) {
                procureLineDTO.setSupplierId(supplier.getSupplierOid());
                procureLineDTO.setSupplierName(supplier.getSupplierName());
            }
            procureLineDTO.setUnitPrice(procureLine.getUnitPrice());
            procureLineDTO.setCurrency(procureLine.getCurrency());
            procureLineDTO.setProcureInfo(procureLine.getProcureInfo());
            procureLineDTO.setNeedIsChanged(procureLine.isNeedIsChanged());
            procureLineDTO.setFunctionGroupId(procureLine.getFunctionGroupId());

            if (procureLine.getPartAlias() != null) {
                procureLineDTO.setPartAlias(procureLine.getPartAlias().getAliasPartNumber());
            }
            setMaterialRelatedInfo(procureLine, procureLineDTO);            
            procureLineDTO.setUsageQty(procureLine.getUsageQuantity());
            procureLineDTO.setAdditionalQuantity(procureLine.getAdditionalQuantity());
            procureLineDTO.setQualityDocumentId(procureLine.getQualityDocumentOID());
            procureLineDTO.setProcureForwardedId(procureLine.getForwardedUserId());
            procureLineDTO.setProcureForwardedName(procureLine.getForwardedUserName());
            procureLineDTO.setProcureForwardedTeam(procureLine.getForwardedTeam());
            procureLineDTO.setForwardedForDC(procureLine.isForwardedForDC());
            procureLineDTO.setReferenceGps(procureLine.getReferenceGps());
            procureLineDTO.setProcureQty(getProcureLineQuantity(procureLine));
            procureLineDTO.setProcureFailureDate(procureLine.getProcureFailureDate());
            procureLineDTO.setUnitOfPrice(procureLine.getPerQuantity());
            procureLineDTO.setFromStockQty(procureLine.getFromStockQty());
            procureLineDTO.setFromStockProjectQty(procureLine.getFromStockProjectQty());
            if (procureLine.getStatusFlag() != null) {
                procureLineDTO.setStatusFlag(procureLine.getStatusFlag().name());
            }
            procureLineDTO.setEdited(procureLine.isContentEdited());
            return procureLineDTO;
        }
        return null;
    }

    private static void setProcureResponsibilityInfo(ProcureLineDTO procureLineDTO, ProcureLine procureLine) {
        if (procureLine.getResponsibility() != null) {
            procureLineDTO.setProcureResponsibility(procureLine.getResponsibility().name());
            if (procureLine.getResponsibility() == ProcureResponsibility.BUILDSITE) {
                for (Material material : procureLine.getMaterials()) {
                    if (material.getMaterialHeader() != null) {
                        MaterialHeaderVersion accepted = material.getMaterialHeader().getAccepted();
                        procureLineDTO.setOutboundLocationId(accepted.getOutboundLocationId());
                        procureLineDTO.setOutboundLocationName(accepted.getOutboundLocationName());
                        break;
                    }
                }
            }
        }
    }

    private static void setMaterialRelatedInfo(ProcureLine procureLine, ProcureLineDTO procureLineDTO) {
        List<String> mailFormIds = new ArrayList<String>();
        List<Material> materials = procureLine.getMaterials();
        if (materials != null && !materials.isEmpty()) {
            boolean isForStock = false;
            boolean procureCommentExists = false;
            /*
             * @Comment: Materials of ForStock type cannot be combined with any other type to represent as one procureLine. Hence anyMaterial can be used to
             * identify ForStock type.
             */
            for (Material material : materials) {
                if (material.getMaterialHeader() != null && material.getMaterialHeader().getRequestType() == RequestType.FOR_STOCK) {
                    isForStock = true;
                }
                String mailFormId = material.getMailFormId();
                if (StringUtils.isNotEmpty(mailFormId) && !mailFormIds.contains(mailFormId)) {
                    mailFormIds.add(mailFormId);
                }
                procureLineDTO.setModularHarness(material.getModularHarness());
                if (material.isAddedAfter()) {
                    procureLineDTO.setHasUnread(true);
                }
                
                if (!StringUtils.isEmpty(material.getProcureComment())) {
                    procureCommentExists = true;
                }
                
            }
            procureLineDTO.setForStock(isForStock);
            procureLineDTO.setProcureCommmentExist(procureCommentExists);
        }
        procureLineDTO.setMailFormIds(StringUtils.join(mailFormIds, ','));
    }
    
    private static void setFinanceInformation(ProcureLine procureLine, ProcureLineDTO procureLineDTO) {
        FinanceHeader financeHeader = procureLine.getFinanceHeader();
        if (financeHeader != null) {
            procureLineDTO.setFinanceHeaderId(financeHeader.getFinanceHeaderXOid());
            procureLineDTO.setFinanceHeaderVersion(financeHeader.getVersion());
            procureLineDTO.setCostCenter(financeHeader.getCostCenter());
            procureLineDTO.setGlAccount(financeHeader.getGlAccount());
            procureLineDTO.setInternalOrderNoSAP(financeHeader.getInternalOrderNoSAP());
            procureLineDTO.setProjectId(financeHeader.getProjectId());
            procureLineDTO.setWbsCode(financeHeader.getWbsCode());
            procureLineDTO.setCompanyCode(financeHeader.getCompanyCode());
        }
    }

    private static void setPartInformation(ProcureLine procureLine, ProcureLineDTO procureLineDTO) {
        procureLineDTO.setpPartAffiliation(procureLine.getpPartAffiliation());
        procureLineDTO.setpPartModification(procureLine.getpPartModification());
        procureLineDTO.setpPartName(procureLine.getpPartName());
        procureLineDTO.setpPartNumber(procureLine.getpPartNumber());
        procureLineDTO.setpPartVersion(procureLine.getpPartVersion());
    }

    public static List<Long> getValues(List<MaterialHeader> list) {
        List<Long> ids = new ArrayList<Long>();
        if (!list.isEmpty()) {
            for (MaterialHeader procureRequestHeader : list) {
                ids.add(procureRequestHeader.getId());
            }
        }
        return ids;
    }



    public static List<CostCenterDTO> transformCCEtyToDTO(List<CostCenter> costCenters) {
        List<CostCenterDTO> costCenterDTOs = new ArrayList<CostCenterDTO>();
        if (!costCenters.isEmpty()) {
            for (CostCenter costCenter : costCenters) {
                costCenterDTOs.add(transformCCEtyAsDTO(costCenter));
            }
        }
        return costCenterDTOs;
    }

    private static CostCenterDTO transformCCEtyAsDTO(CostCenter costCenter) {
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
        if (!glAccounts.isEmpty()) {
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

    public static List<WbsElementDTO> transformWbsElementEtyToDTO(List<WbsElement> wbsElements) {
        List<WbsElementDTO> wbsElementDTOs = new ArrayList<WbsElementDTO>();
        if (!wbsElements.isEmpty()) {
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

    public static RequisitionDTO transformToDTO(Requisition requisition) {
        RequisitionDTO requisitionDTO = new RequisitionDTO();
        if (requisition.getRequisitionId() != null) {
            requisitionDTO.setRequisitionId(requisition.getRequisitionId());
        }
        requisitionDTO.setOriginatorUserId(requisition.getOriginatorUserId());
        requisitionDTO.setOriginatorName(requisition.getOriginatorName());
        requisitionDTO.setIssuerOrganisation(requisition.getIssuerOrganisation());
        requisitionDTO.setIssuerName(requisition.getIssuerName());
        requisitionDTO.setIssuerPhoneNo(requisition.getIssuerPhoneNo());
        requisitionDTO.setIssuerDepartment(requisition.getIssuerDepartment());
        requisitionDTO.setIssuerUserId(requisition.getIssuerUserId());
        requisitionDTO.setMaterialUserId(requisition.getMaterialUserId());
        requisitionDTO.setContactPersonName(requisition.getContactPersonName());
        requisitionDTO.setPpSuffix(requisition.getPpSuffix());
        requisitionDTO.setReference(requisition.getReference());
        requisitionDTO.setQuantity(requisition.getQuantity());
        requisitionDTO.setMaximumPrice(requisition.getMaximumPrice());
        requisitionDTO.setMaximumcurrency(requisition.getMaximumcurrency());
        requisitionDTO.setRequiredStaWeek(requisition.getRequiredStaWeek());
        requisitionDTO.setPriceType(requisition.getPriceType());
        requisitionDTO.setRequiredStaDate(requisition.getRequiredStaDate());
        requisitionDTO.setPurchaseInfo1(requisition.getPurchaseInfo1());
        requisitionDTO.setPurchaseInfo2(requisition.getPurchaseInfo2());
        requisitionDTO.setPartNumber(requisition.getPartNumber());
        requisitionDTO.setPartQualifier(requisition.getPartQualifier());
        requisitionDTO.setPartVersion(requisition.getPartVersion());
        requisitionDTO.setPartName(requisition.getPartName());
        requisitionDTO.setPartFunctionGroup(requisition.getPartFunctionGroup());
        requisitionDTO.setUnitOfMeasure(requisition.getUnitOfMeasure());
        requisitionDTO.setProjectId(requisition.getProjectId());
        requisitionDTO.setWbsCode(requisition.getWbsCode());
        requisitionDTO.setCostCenter(requisition.getCostCenter());
        requisitionDTO.setGlAccount(requisition.getGlAccount());
        requisitionDTO.setCancelled(requisition.isCancelled());
        requisitionDTO.setBuyerId(requisition.getBuyerId());
        requisitionDTO.setPurchaseOrganizationCode(requisition.getPurchaseOrganizationCode());
        return requisitionDTO;
    }

    public static ResponseDTO transformAsDTO(boolean success) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setSuccess(success);
        return responseDTO;
    }

    public static List<InternalOrderSapDTO> transformAsInternalSapDTOs(List<InternalOrderSap> internalOrderSaps) {
        List<InternalOrderSapDTO> internalOrderSapDTOs = new ArrayList<InternalOrderSapDTO>();
        if (!internalOrderSaps.isEmpty()) {
            for (InternalOrderSap internalOrderSap : internalOrderSaps) {
                internalOrderSapDTOs.add(transformAsDTO(internalOrderSap));
            }
        }
        return internalOrderSapDTOs;
    }

    private static InternalOrderSapDTO transformAsDTO(InternalOrderSap internalOrderSap) {
        if (internalOrderSap != null) {
            InternalOrderSapDTO internalOrderSapDTO = new InternalOrderSapDTO();
            internalOrderSapDTO.setId(internalOrderSap.getInternalOrderSapOid());
            internalOrderSapDTO.setVersion(internalOrderSap.getVersion());
            internalOrderSapDTO.setCode(internalOrderSap.getCode());
            return internalOrderSapDTO;
        }
        return null;
    }



    public static ProcureLine cloneProcureLine(ProcureLine procureLine, List<Material> materialList) throws GloriaApplicationException {
        ProcureLine newProcureLine = new ProcureLine();

        newProcureLine.setProcureType(procureLine.getProcureType());
        newProcureLine.setProcureDate(procureLine.getProcureDate());
        newProcureLine.setRequisitionId(procureLine.getRequisitionId());
        newProcureLine.setpPartAffiliation(procureLine.getpPartAffiliation());
        newProcureLine.setpPartNumber(procureLine.getpPartNumber());
        newProcureLine.setpPartVersion(procureLine.getpPartVersion());
        newProcureLine.setpPartName(procureLine.getpPartName());
        newProcureLine.setpPartModification(procureLine.getpPartModification());
        newProcureLine.setDfuObjectNumber(procureLine.getDfuObjectNumber());
        newProcureLine.setFunctionGroupId(procureLine.getFunctionGroupId());
        newProcureLine.setReferenceGroups(procureLine.getReferenceGroups());
        newProcureLine.setReferenceIds(procureLine.getReferenceIds());
        newProcureLine.setChangeRequestIds(procureLine.getChangeRequestIds());
        newProcureLine.setDangerousGoodsOID(procureLine.getDangerousGoodsOID());
        newProcureLine.setWarehouseComment(procureLine.getWarehouseComment());
        newProcureLine.setProcureInfo(procureLine.getProcureInfo());
        newProcureLine.setUnitPrice(procureLine.getUnitPrice());
        newProcureLine.setReferenceGps(procureLine.getReferenceGps());
        newProcureLine.setMaxPrice(procureLine.getMaxPrice());
        newProcureLine.setCurrency(procureLine.getCurrency());
        newProcureLine.setOrderNo(procureLine.getOrderNo());
        Supplier supplier = procureLine.getSupplier();
        if (supplier != null) {
            newProcureLine.setSupplier(supplier);
            supplier.setProcureLine(newProcureLine);
        }
        newProcureLine.setRequiredStaDate(procureLine.getRequiredStaDate());
        newProcureLine.setSupplierCounterPartOID(procureLine.getSupplierCounterPartOID());

        newProcureLine.setAdditionalQuantity(procureLine.getAdditionalQuantity());
        newProcureLine.setStatus(procureLine.getStatus());
        newProcureLine.setResponsibility(procureLine.getResponsibility());
        newProcureLine.setMaterialControllerId(procureLine.getMaterialControllerId());
        newProcureLine.setMaterialControllerName(procureLine.getMaterialControllerName());
        newProcureLine.setMaterialControllerTeam(procureLine.getMaterialControllerTeam());
        newProcureLine.setForwardedUserId(procureLine.getForwardedUserId());
        newProcureLine.setForwardedUserName(procureLine.getForwardedUserName());
        newProcureLine.setForwardedTeam(procureLine.getForwardedTeam());
        newProcureLine.setForwardedForDC(procureLine.isForwardedForDC());
        newProcureLine.setNeedIsChanged(procureLine.isNeedIsChanged());

        try {
            // Move relation of materials to the new ProcureLine
            for (Material material : materialList) {
                material.setProcureLine(newProcureLine);
                newProcureLine.setGroupingKeyMd5(material.getGroupingKeyMd5());
            }
        } catch (NoSuchAlgorithmException e) {
            throw new GloriaApplicationException(GloriaExceptionConstants.DEFAULT_ERROR_CODE, "NoSuchAlgorithm Exception");
        }
        newProcureLine.setFinanceHeader(procureLine.getFinanceHeader());
        newProcureLine.setRequisition(procureLine.getRequisition());
        return newProcureLine;
    }

    public static Material cloneProcurement(Material material) throws GloriaApplicationException {
        Material newMaterial = new Material();

        newMaterial.setMaterialType(material.getMaterialType());
        newMaterial.setUnitOfMeasure(material.getUnitOfMeasure());
        newMaterial.setRequiredStaDate(material.getRequiredStaDate());
        newMaterial.setAdd(material.getAdd());
        newMaterial.setRemove(material.getRemove());
        newMaterial.setReceiver(material.getReceiver());
        newMaterial.setMaterialHeader(material.getMaterialHeader());
        ProcureLineHelper.associateMaterialWithProcureLine(newMaterial, material.getProcureLine());
        return newMaterial;
    }

    public static List<ChangeIdDTO> transformChangeIdDTOs(List<ChangeId> listOfChangeId) {
        List<ChangeIdDTO> changeIdDTOs = new ArrayList<ChangeIdDTO>();
        for (ChangeId changeId : listOfChangeId) {
            changeIdDTOs.add(transformChangeIdDTO(changeId));
        }
        return changeIdDTOs;
    }

    public static ChangeIdDTO transformChangeIdDTO(ChangeId changeId) {
        ChangeIdDTO changeIdDto = new ChangeIdDTO();
        changeIdDto.setId(changeId.getChangeIdOid());
        changeIdDto.setVersion(changeId.getVersion());
        changeIdDto.setChangeId(changeId.getMtrlRequestVersion());
        changeIdDto.setProcureNotes(changeId.getProcureNotes());
        changeIdDto.setReceivedDate(changeId.getReceivedDate());
        if (changeId.getStatus() != null) {
            changeIdDto.setStatus(changeId.getStatus().name());
        }
        return changeIdDto;
    }

    public static List<PartAliasDTO> transformAsPartAliasDTOs(List<PartAlias> partAliases) {
        List<PartAliasDTO> partAliasDTOs = new ArrayList<PartAliasDTO>();
        if (!partAliases.isEmpty()) {
            for (PartAlias partAlias : partAliases) {
                partAliasDTOs.add(transformAsDTO(partAlias));
            }
        }
        return partAliasDTOs;
    }

    private static PartAliasDTO transformAsDTO(PartAlias partAlias) {
        if (partAlias != null) {
            PartAliasDTO partAliasDTO = new PartAliasDTO();
            partAliasDTO.setId(partAlias.getPartAliasOid());
            partAliasDTO.setPartNumber(partAlias.getAliasPartNumber());
            return partAliasDTO;
        }
        return null;
    }

    public static void setOrderAttributes(Order order, StandAloneOrderHeaderDTO standAloneOrderHeaderDTO, 
            CommonServices commonServices, UserServices userServices) throws GloriaApplicationException {
        order.setInternalExternal(InternalExternal.EXTERNAL);

        String[] orderNoTokens = InternalExternal.EXTERNAL.evaluateOrderNo(order.getOrderNo(), standAloneOrderHeaderDTO.getStandAloneOrderLineDTO().get(0)
                                                                                                                       .getBuyerId());

        order.setSuffix(orderNoTokens[1]);
        order.setOrderMode(standAloneOrderHeaderDTO.getOrderMode());
        order.setOrderIdGps(standAloneOrderHeaderDTO.getOrderIdGps());
        order.setMaterialUserId(standAloneOrderHeaderDTO.getMaterialUserId());
        order.setMaterialUserName(standAloneOrderHeaderDTO.getMaterialUserName());
        order.setMaterialUserCategory(standAloneOrderHeaderDTO.getMaterialUserCategory());

        String supplierId = standAloneOrderHeaderDTO.getSupplierId();
        SupplierCounterPart supplierCounterPart = commonServices.findSupplierCounterPartByPPSuffix(orderNoTokens[1]);
        if (supplierCounterPart != null && supplierCounterPart.getDeliveryFollowUpTeam() != null) {
            String delFollowUpTeam = supplierCounterPart.getDeliveryFollowUpTeam().getName();
            order.setDeliveryControllerTeam(delFollowUpTeam);
            for (OrderLine orderLine : order.getOrderLines()) {
                String deliverycontrollerUserId = commonServices.matchDeliveryController(supplierCounterPart.getDeliveryFollowUpTeam(), orderNoTokens[1],
                                                                                         supplierId, orderLine.getProjectId());
                UserDTO userDTO = userServices.getUser(deliverycontrollerUserId);
                orderLine.setDeliveryControllerUserId(deliverycontrollerUserId);
                orderLine.setDeliveryControllerUserName(userDTO.getUserName());
            }
            order.setShipToId(supplierCounterPart.getShipToId());
        }

        order.setSupplierName(standAloneOrderHeaderDTO.getSupplierName());
        order.setSupplierCategory(standAloneOrderHeaderDTO.getSupplierCategory());
        try {
            order.setOrderDateTime(DateUtil.getStringAsDate(standAloneOrderHeaderDTO.getOrderDateTime()));
        } catch (ParseException e1) {
            throw new GloriaSystemException(e1, e1.getLocalizedMessage());
        }
    }

    public static void setProcureLineChangeRequestIds(ProcureLine procureLine) {
        List<Material> materials = procureLine.getMaterials();
        List<String> changeRequestIds = new ArrayList<String>();
        for (Material material : materials) {
            if (!material.getMaterialType().equals(MaterialType.USAGE_REPLACED) && !material.getMaterialType().equals(MaterialType.RELEASED)
                    && !material.getMaterialType().equals(MaterialType.ADDITIONAL)) {
                ChangeId changeId = material.getAdd();
                MaterialHeader requestHeader = material.getMaterialHeader();
                MaterialHeaderVersion current = requestHeader.getAccepted();
                ChangeId currentChangeId = current.getChangeId();
                List<Material> addedRequestLines = currentChangeId.getAddMaterials();
                if (!addedRequestLines.isEmpty() && changeId != null
                        && !changeRequestIds.contains(changeId.getMtrlRequestVersion())) {
                    changeRequestIds.add(changeId.getMtrlRequestVersion());
                }
            }
        }
        procureLine.setChangeRequestIds(StringUtils.join(changeRequestIds, ProcurementHelper.COMMA));
    }

    public static void createAdditionalMaterial(Material material, MaterialServices materialServices, TraceabilityRepository traceabilityRepository,
            UserDTO userDTO) throws GloriaApplicationException {
        ProcureLine procureLine = material.getProcureLine();

        Material additionalMaterial = new Material();
        additionalMaterial.setMaterialType(MaterialType.ADDITIONAL);
        additionalMaterial.setUnitOfMeasure(material.getUnitOfMeasure());
        additionalMaterial.setMigrated(false);
        additionalMaterial.setRequiredStaDate(material.getRequiredStaDate());
        additionalMaterial.setMailFormId(material.getMailFormId());
        additionalMaterial.setPartNumber(material.getPartNumber());
        additionalMaterial.setPartName(material.getPartName());
        additionalMaterial.setPartAffiliation(material.getPartAffiliation());
        additionalMaterial.setPartModification(material.getPartModification());
        additionalMaterial.setPartVersion(material.getPartVersion());
        additionalMaterial.setOrderNo(procureLine.getOrderNo());
        additionalMaterial.setProcureLine(procureLine);
        
        FinanceHeader financeHeader = material.getFinanceHeader();
        if (financeHeader != null) {
            additionalMaterial.setFinanceHeader(ProcureTypeHelper.cloneFinanceHeader(financeHeader));
        }
        additionalMaterial.setRequiredStaDate(material.getRequiredStaDate());

        MaterialLine additionalMaterialLine = new MaterialLine();
        List<MaterialLine> currentMaterialLines = material.getMaterialLine();
        if (!currentMaterialLines.isEmpty()) {
            MaterialLine aCurrentMaterialLine = currentMaterialLines.get(0);
            additionalMaterialLine.setStatus(MaterialLineStatusHelper.getProcureState(aCurrentMaterialLine));
            additionalMaterialLine.setStatusDate(DateUtil.getCurrentUTCDateTime());
            additionalMaterialLine.setProcureType(procureLine.getProcureType().getMaterialLineProcureType());
            additionalMaterialLine.setWhSiteId(procureLine.getShipToId());
            additionalMaterialLine.setFinalWhSiteId(procureLine.getShipToId());
            additionalMaterialLine.setDirectSend(DirectSendType.NO);            
        }
        additionalMaterialLine.setQuantity(procureLine.getAdditionalQuantity());
                
        MaterialLineStatusHelper.assignMaterialLineToMaterial(additionalMaterial, additionalMaterialLine);
      
        procureLine.getMaterials().add(additionalMaterial);
        materialServices.addMaterial(additionalMaterial);
    }

    public static Material createMaterial(ChangeId changeId, MaterialHeader materialHeader, boolean procureLineExists,
            RequestHeaderTransformerDTO requestHeaderTransformerDTO, RequestLineTransformerDTO requestLineTransformerDto, MaterialServices materialServices,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        Material material = new Material();
        material.setMaterialType(RequestType.valueOf(requestHeaderTransformerDTO.getRequestType()).getMaterialType());
        material.setProcureLinkId(requestLineTransformerDto.getProcureLinkId());
        material.setPartAffiliation(requestLineTransformerDto.getPartAffiliation());
        material.setPartNumber(requestLineTransformerDto.getPartNumber());
        material.setPartVersion(requestLineTransformerDto.getPartVersion());
        String demarcation = requestLineTransformerDto.getDemarcation();
        material.setPartName(requestLineTransformerDto.getPartName());
        if (StringUtils.isNotBlank(demarcation)) {
            material.setPartName(material.getPartName() + "," + demarcation);
        }
        material.setPartModification(requestLineTransformerDto.getPartModification());
        material.setUnitOfMeasure(requestLineTransformerDto.getUnitOfMeasure());
        material.setProductClass(requestLineTransformerDto.getProductClass());
        material.setFunctionGroup(requestLineTransformerDto.getFunctionGroup());
        material.setRefAssemblyPartNo(requestLineTransformerDto.getRefAssemblyPartNo());
        material.setRefAssemblyPartVersion(requestLineTransformerDto.getRefAssemblyPartVersion());
        material.setDesignResponsible(requestLineTransformerDto.getDesignResponsible());
        material.setItemToVariantLinkId(requestLineTransformerDto.getItemToVariantLinkId());
        material.setModularHarness(requestLineTransformerDto.getModularHarness());
        material.setObjectNumber(requestLineTransformerDto.getObjectNumber());
        material.setDemarcation(demarcation);
        material.setCharacteristics(requestLineTransformerDto.getCharacteristics());
        material.setRequiredStaDate(requestLineTransformerDto.getRequiredStaDate());
        material.setProcureComment(requestLineTransformerDto.getProcureComment());
        material.setMailFormId(requestLineTransformerDto.getMailFormId());
        material.setFinanceHeader(createFinanceHeader(requestLineTransformerDto));
        material.setPartAlias(getPartAlias(material, requestLineTransformerDto));      
       
        MaterialLine materialLine = new MaterialLine();
        assignMaterialToMaterialHeader(materialHeader, material);
        MaterialLineStatusHelper.assignMaterialLineToMaterial(material, materialLine);

        materialLine.setQuantity(requestLineTransformerDto.getQuantity());
        materialLine.setStatus(MaterialLineStatus.CREATED);
        materialLine.setStatusDate(DateUtil.getCurrentUTCDateTime());
        MaterialLineStatusHelper.createTraceabilityLog(materialLine, traceabilityRepository, "Create", null, null, null,
                                                       GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE);
        material.setAdd(changeId);
        material.setMtrlRequestVersionAccepted(changeId.getMtrlRequestVersion());
        
        materialServices.addMaterial(material);
        
        changeId.getAddMaterials().add(material);
        
        return material;
    }

    private static void assignMaterialToMaterialHeader(MaterialHeader materialHeader, Material material) {
        material.setMaterialHeader(materialHeader);
        materialHeader.getMaterials().add(material);
    }

 
    private static List<MaterialPartAlias> getPartAlias(Material material, RequestLineTransformerDTO requestLineTransformerDto) {
        List<MaterialPartAlias> aliasEntityList = new ArrayList<MaterialPartAlias>();
        List<PartAliasTransformerDTO> aliasDtoList =  requestLineTransformerDto.getPartAliasTransformerDtos();
        for (PartAliasTransformerDTO partAliasTransformerDTO : aliasDtoList) {
            MaterialPartAlias entityAlias = new  MaterialPartAlias();
            entityAlias.setPartNumber(partAliasTransformerDTO.getCode());
           if ("RVI".equals(partAliasTransformerDTO.getDomain())) {
               entityAlias.setKolaDomain(KolaDomain.RVI);
               entityAlias.setMaterial(material);
            } else if ("MAC".equals(partAliasTransformerDTO.getDomain())) {
                entityAlias.setKolaDomain(KolaDomain.MAC);
                entityAlias.setMaterial(material);
            } else if ("NDM".equals(partAliasTransformerDTO.getDomain())) {
                entityAlias.setKolaDomain(KolaDomain.NDM);
                entityAlias.setMaterial(material);
            }
           if (entityAlias.getKolaDomain() != null) {
               aliasEntityList.add(entityAlias);               
           }            
        }
       
        return aliasEntityList;
    }

    private static FinanceHeader createFinanceHeader(RequestLineTransformerDTO requestLineTransformerDTO) {
        FinanceHeader financeHeader = new FinanceHeader();
        FinanceHeaderTransformerDTO financeHeaderTransformerDTo = requestLineTransformerDTO.getFinanceHeaderTransformerDTO();
        financeHeader.setProjectId(financeHeaderTransformerDTo.getProjectId());
        financeHeader.setCompanyCode(financeHeaderTransformerDTo.getCompanyCode());
        financeHeader.setGlAccount(financeHeaderTransformerDTo.getGlAccount());
        financeHeader.setCostCenter(financeHeaderTransformerDTo.getCostCenter());
        financeHeader.setWbsCode(financeHeaderTransformerDTo.getWbsCode());
        financeHeader.setInternalOrderNoSAP(financeHeaderTransformerDTo.getInternalorderNoSAP());
        return financeHeader;
    }

    public static List<PurchaseOrganisationDTO> transformAsPurchaseOrganisationsDTOs(List<PurchaseOrganisation> purchaseOrganisations) {
        List<PurchaseOrganisationDTO> purchaseOrganisationDTOs = new ArrayList<PurchaseOrganisationDTO>();
        if (!purchaseOrganisations.isEmpty()) {
            for (PurchaseOrganisation purchaseOrganisation : purchaseOrganisations) {
                purchaseOrganisationDTOs.add(transformAsPurchaseOrganisationDTO(purchaseOrganisation));
            }
        }
        return purchaseOrganisationDTOs;
    }

    private static PurchaseOrganisationDTO transformAsPurchaseOrganisationDTO(PurchaseOrganisation purchaseOrganisation) {
        PurchaseOrganisationDTO purchaseOrganisationDTO = new PurchaseOrganisationDTO();
        purchaseOrganisationDTO.setId(purchaseOrganisation.getPurchaseOrganisationOid());
        purchaseOrganisationDTO.setVersion(purchaseOrganisation.getVersion());
        purchaseOrganisationDTO.setOrganisationCode(purchaseOrganisation.getOrganisationCode());
        purchaseOrganisationDTO.setOrganisationName(purchaseOrganisation.getOrganisationName());
        return purchaseOrganisationDTO;
    }
    
    public static long getProcureLineQuantity(ProcureLine procureLine) {
        long procureLineQuantity = 0L;
        for (Material material : procureLine.getMaterials()) {
            if (material != null
                    && (material.getMaterialType().equals(MaterialType.USAGE) || material.getMaterialType().equals(MaterialType.MODIFIED)
                            || material.getMaterialType().equals(MaterialType.RELEASED) || material.getMaterialType().equals(MaterialType.ADDITIONAL)
                            || material.getMaterialType().equals(MaterialType.ADDITIONAL_USAGE))) {
                procureLineQuantity += material.getQuantity();
            }
        }
        return procureLineQuantity;
    }
}
