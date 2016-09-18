package com.volvo.gloria.common.b;

import com.volvo.gloria.common.companycode.c.dto.SyncCompanyCodeDTO;

/**
 * Service interface for Company Code message transformer.
 */
public interface CompanyCodeTransformer {

    /**
     * Transforms the company code message.
     * 
     * @param message
     * 
     * @return CompanyCodeDTO
     */
    SyncCompanyCodeDTO transformCompanyCode(String message);

}
