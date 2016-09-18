package com.volvo.gloria.warehouse.b;

import java.util.List;

import com.volvo.gloria.warehouse.c.dto.PrinterTransformerDTO;

/**
 * Printer Tranformer interface to transform the Xml content.
 */
public interface PrinterTransformer {
    
    /**
     * Transform Printers transforms xml content info to the list Of Printer Transformer DTO. 
     * 
     * @param xmlContent
     * @return List of Printer Transformer DTO.
     */
    List<PrinterTransformerDTO> transformPrinters(String xmlContent);

}
