package com.volvo.gloria.procurematerial.b;

import java.util.List;

import com.volvo.gloria.procurematerial.c.dto.MaterialProcureTransformerDTO;


/**
 * Methods for MaterialTransformer.
 */
public interface MaterialTransformer {

    List<MaterialProcureTransformerDTO> transformMaterial(String xmlContent);


}
