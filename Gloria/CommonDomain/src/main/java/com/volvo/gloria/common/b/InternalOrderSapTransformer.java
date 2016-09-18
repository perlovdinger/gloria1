package com.volvo.gloria.common.b;

import java.util.List;

import com.volvo.gloria.common.c.dto.InternalOrderSapDTO;

/**
 * Service interface for Internal Order SAP transformer.
 */
public interface InternalOrderSapTransformer {

    List<InternalOrderSapDTO> transformInterOrderSap(String receivedInternalOrderSapMessage);
}
