package com.volvo.gloria.util.b.beans;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.b.PrintLabelServices;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Implementation for label print.
 * 
 * The label is printed sending ZPL commands to Zebra printer.
 * 
 * Bar code type used here is 'Code 128'(BC).The Code 128 specification defines 128 characters, consisting of uppercase letters (A through Z), numeric digits (0
 * through 9) and a number of special characters (-, ., $, /, +, %, and space). An additional character (denoted '*') is used for both start and stop
 * delimiters.
 * 
 */
@ContainerManaged(name = "printLabelService")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PrintLabelServicesBean implements PrintLabelServices {

    @Override
    public void doPrint(String labelToPrint, String printer) throws GloriaApplicationException {
        String host = null;
        String port = null;
        try {
            String[] printerDetails = printer.split(":");
            if (printerDetails != null && printerDetails.length == 2) {
                host = printerDetails[0];
                port = printerDetails[1];
                Socket clientSocket = new Socket(host, Integer.parseInt(port));
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                outToServer.writeBytes(labelToPrint);
                clientSocket.close();
            } else {
                throw new GloriaApplicationException(GloriaExceptionConstants.PRINTER_CONNECT_ERROR, "Printer configuration error.");
            }
                
        } catch (IOException e) {
            throw new GloriaApplicationException(GloriaExceptionConstants.PRINTER_CONNECT_ERROR, "Network error.");
        }
    }

}
