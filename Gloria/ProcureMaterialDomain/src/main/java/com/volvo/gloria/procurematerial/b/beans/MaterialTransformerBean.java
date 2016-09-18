package com.volvo.gloria.procurematerial.b.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.procurematerial.b.MaterialTransformer;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDetailsDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialProcureTransformerDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialTransformerDTO;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.group.init.material._1_0.Material;
import com.volvo.group.init.material._1_0.MaterialLine;
import com.volvo.group.init.material._1_0.Materials;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * services Implementation for MaterilTransformer.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MaterialTransformerBean extends XmlTransformer implements MaterialTransformer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialTransformerBean.class);

    public MaterialTransformerBean() throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_MATERIAL, XmlConstants.PackageName.INIT_MATERIAL);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MaterialProcureTransformerDTO> transformMaterial(String xmlContent) {

        try {
            return (List<MaterialProcureTransformerDTO>) transform(xmlContent);
        } catch (JAXBException e) {
            // Throw an exception to roll-back the transaction.
            LOGGER.error("Unable to unmarshall the received message to a MaterialProcureTransformerDTOs object, message will be discarded. Exception is:", e);
            throw new GloriaSystemException(e,
                                            "Unable to unmarshall the received message to a MaterialProcureTransformerDTOs object, message will be discarded");
        }

    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        Materials materials = (Materials) jaxbOject;

        List<MaterialProcureTransformerDTO> materialProcureTransformerDTOs = new ArrayList<MaterialProcureTransformerDTO>();
        for (Material material : materials.getMaterial()) {
            List<MaterialTransformerDTO> materialList = new ArrayList<MaterialTransformerDTO>();

            MaterialTransformerDTO materialDTO = createMaterialDTO(material);

            MaterialProcureTransformerDTO materialProcureDTO = createMaterialProcureDTO(material.getMaterialProcure());

            List<MaterialLineDetailsDTO> materialLineList = new ArrayList<MaterialLineDetailsDTO>();
            for (MaterialLine materialLine : material.getMaterialLine()) {

                MaterialLineDetailsDTO materialLineDTO = createMaterialLineDTO(materialLine);
                materialLineList.add(materialLineDTO);
            }

            materialDTO.setMaterialLineDTO(materialLineList);
            materialList.add(materialDTO);
            materialProcureDTO.setMaterialDTOs(materialList);
            materialProcureTransformerDTOs.add(materialProcureDTO);
        }

        return materialProcureTransformerDTOs;
    }

    private MaterialLineDetailsDTO createMaterialLineDTO(MaterialLine materialLine) {
        MaterialLineDetailsDTO materialLineDTO = new MaterialLineDetailsDTO();
        materialLineDTO.setQuantity(materialLine.getQuantity());
        materialLineDTO.setStatus(MaterialLineStatus.valueOf(materialLine.getStatus()));
        XMLGregorianCalendar receivedDate = materialLine.getReceivedDate();
        if (receivedDate != null) {
            GregorianCalendar gregorianCalendar = receivedDate.toGregorianCalendar();
            materialLineDTO.setReceivedDate(gregorianCalendar.getTime());
        }
        return materialLineDTO;
    }

    private MaterialProcureTransformerDTO createMaterialProcureDTO(com.volvo.group.init.material._1_0.MaterialProcure materialProcure) {
        MaterialProcureTransformerDTO materialProcureDTO = new MaterialProcureTransformerDTO();
        materialProcureDTO.setProcureType(materialProcure.getProcureType());
        materialProcureDTO.setpPartAffiliation(materialProcure.getPPartAffiliation());
        materialProcureDTO.setpPartName(materialProcure.getPPartName());
        materialProcureDTO.setpPartNumber(String.valueOf(materialProcure.getPPartNumber()));
        materialProcureDTO.setpPartVersion(String.valueOf(materialProcure.getPPartVersion()));
        materialProcureDTO.setProjectId(materialProcure.getProjectId());
        materialProcureDTO.setCostCenter(materialProcure.getCostCenter());
        materialProcureDTO.setWbsCode(materialProcure.getWbsCode());
        materialProcureDTO.setGlAccount(materialProcure.getGlaAccount());
        materialProcureDTO.setInspectionLevel(materialProcure.getInspectionLevel());
        materialProcureDTO.setDfuObjectNumber(materialProcure.getDfuObjectNumber());
        materialProcureDTO.setRequisitionId(materialProcure.getRequisitionId());
        materialProcureDTO.setWhSiteId(materialProcure.getWhSiteId());
        return materialProcureDTO;
    }

    private MaterialTransformerDTO createMaterialDTO(Material material) {
        MaterialTransformerDTO materialDTO = new MaterialTransformerDTO();
        materialDTO.setUnitOfMeasure(material.getUnitOfMeasure());
        materialDTO.setQuantity((long) material.getQuantity());
        materialDTO.setMaterialType(material.getMaterialType());
        return materialDTO;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        // TODO Auto-generated method stub
        return null;
    }

}
