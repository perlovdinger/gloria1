package com.volvo.gloria.common.b;

import com.volvo.gloria.common.c.dto.SiteDTOs;

/**
 * Transformer class for Sites.
 */
public interface SitesTransformer {

    SiteDTOs transformSites(String xmlContent);

}
