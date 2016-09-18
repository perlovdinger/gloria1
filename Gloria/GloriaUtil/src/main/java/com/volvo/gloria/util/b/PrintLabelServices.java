package com.volvo.gloria.util.b;

import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Service for label print.
 * 
 * The label is printed sending ZPL commands to Zebra printer.
 * 
 * Bar code type used here is 'Code 39'(B3).The Code 39 specification defines 43 characters, consisting of uppercase letters (A through Z), numeric digits (0
 * through 9) and a number of special characters (-, ., $, /, +, %, and space). An additional character (denoted '*') is used for both start and stop
 * delimiters.Code 39 can be decoded with virtually any barcode reader.
 * 
 */
public interface PrintLabelServices {

    void doPrint(String labelToPrint, String printer) throws GloriaApplicationException;

}
