package com.volvo.gloria.warehouse.b.beans;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.gloria.warehouse.b.PrinterTransformer;
import com.volvo.gloria.warehouse.c.dto.PrinterTransformerDTO;
import com.volvo.group.init.warehouse._1_0.Printer;
import com.volvo.group.init.warehouse._1_0.Printers;
import com.volvo.jvs.runtime.platform.ContainerManaged;

@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PrinterTransformerBean extends XmlTransformer implements PrinterTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrinterTransformerBean.class);

    public PrinterTransformerBean() {
        super(XmlConstants.SchemaFullPath.INIT_PRINTER, XmlConstants.PackageName.INIT_PRINTER);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PrinterTransformerDTO> transformPrinters(String xmlContent) {
        try {
            return (List<PrinterTransformerDTO>) transform(xmlContent);
        } catch (JAXBException e) {
            // Throw an exception to roll-back the transaction.
            LOGGER.error("Unable to unmarshall the received message to a PrinterTransformerDTO object, message will be discarded. Exception is:", e);
            throw new GloriaSystemException(e, "Unable to unmarshall the received message to a PrinterTransformerDTOs object, message will be discarded");
        }
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        Printers printers = (Printers) jaxbOject;
        List<PrinterTransformerDTO> printerTransformerDTOs = new ArrayList<PrinterTransformerDTO>();

        for (Printer printer : printers.getPrinter()) {
            PrinterTransformerDTO printerTransformerDTO = new PrinterTransformerDTO();

            printerTransformerDTO.setSiteId(printer.getSiteId());
            printerTransformerDTO.setName(printer.getName());
            printerTransformerDTO.setHostAddress(printer.getHostAddress());

            printerTransformerDTOs.add(printerTransformerDTO);
        }

        return printerTransformerDTOs;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        // TODO Auto-generated method stub
        return null;
    }

}
