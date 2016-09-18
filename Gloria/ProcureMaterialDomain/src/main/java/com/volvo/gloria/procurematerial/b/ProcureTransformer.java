package com.volvo.gloria.procurematerial.b;

import com.volvo.gloria.procure.c.dto.ProcureDTO;

/**
 * Services for ProcureTransformer.
 */
public interface ProcureTransformer {

    ProcureDTO transformProcure(String receivedProcureMessage);

}
