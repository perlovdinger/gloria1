package com.volvo.gloria.procurematerial.b;

import java.util.List;

import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteDTO;

/**
 * Interface for the Delivery Note Transformer.
 */
public interface DeliveryNoteTransformer {

    List<DeliveryNoteDTO> transformDeliveryNote(String xmlContent);
}
