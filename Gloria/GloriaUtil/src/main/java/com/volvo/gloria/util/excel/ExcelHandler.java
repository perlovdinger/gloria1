package com.volvo.gloria.util.excel;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaSystemException;

/**
 * Base class for Excel file reading.
 * 
 * @param <T>
 */
public abstract class ExcelHandler<T> {
    private static final int TOTAL_ALPHA = 26;

    private List<String> rowValueList = new ArrayList<String>();
    private int totalNoOfRow;
    private List<String> actualColumnNames = new ArrayList<String>();
    private String filename;
    private InputStream is;
    private List<String> columns;

    private StylesTable styles;


    public ExcelHandler(String filename, List<String> columns) {
        this.filename = filename;
        this.columns = columns;
    }

    public ExcelHandler(InputStream is, List<String> columns) {
        this.is = is;
        this.columns = columns;
    }

    /**
     * Manage each row.
     * 
     * @param row
     * @throws ParseException
     */
    public abstract void manageRow(List<String> row) throws ParseException;

    /**
     * Handle request.
     * 
     * @return
     */
    public abstract T getResult();

    private boolean validateColumnHeaders(List<String> actualColumns) {
        if (columns.size() == actualColumns.size()) {
            return true;
            //return columns.equals(actualColumns);
        }
        return true;
    }

    /**
     * Handling of actual excel file.
     * @return 
     * @throws GloriaApplicationException 
     */
    public T manageExcel() throws GloriaApplicationException  {
        try {
            OPCPackage pkg = null;
            if (filename != null) {
                pkg = OPCPackage.open(filename);
            } else if (is != null) {
                pkg = OPCPackage.open(is);
            } 
            XSSFReader r = new XSSFReader(pkg);
            styles = r.getStylesTable(); 
            SharedStringsTable sst = r.getSharedStringsTable();
    
            XMLReader parser = fetchSheetParser(sst);
    
            Iterator<InputStream> sheets = r.getSheetsData();
            while (sheets.hasNext()) {
                InputStream sheet = sheets.next();
                InputSource sheetSource = new InputSource(sheet);
                parser.parse(sheetSource);
                // Handle null column at end for last row
                if (!rowValueList.isEmpty() && rowValueList.size() < totalNoOfRow) { 
                    for (int i = rowValueList.size(); i < totalNoOfRow; i++) {
                        rowValueList.add(null);
                    }
                    manageRow(rowValueList);
                }
                sheet.close();
            }
        } catch (Exception e) {
            throw new GloriaApplicationException(null, "Invalid Excel :" + e.getMessage(), e);
        }
        
        return getResult();
    }

    public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
        XMLReader parser = XMLReaderFactory.createXMLReader();
        ContentHandler handler = new CustomHandler(sst);
        parser.setContentHandler(handler);
        return parser;
    }

    /**
     * Custom SAX handler for Excel XML.
     */
    private final class CustomHandler extends DefaultHandler {
        private SharedStringsTable sst;
        private String lastContents;
        private boolean nextIsString, nextIsNumber;
        private int rowNo;
        private boolean isValidated = false;
        private int currentRow;
        private int columnCount = 0;
        private short formatIndex;
        private String formatString;

        private CustomHandler(SharedStringsTable sst) {
            this.sst = sst;
        }
        // Used to format numeric cell values.


        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            if ("c".equals(name)) {
                rowNo = getRowNo(attributes.getValue("r"));
                columnCount = getColumnNo(attributes.getValue("r"));
                if (rowNo > 1 && currentRow != rowNo) {
                    if (rowValueList.size() > columnCount && rowValueList.size() < totalNoOfRow) {
                        // Handle null column at end
                        for (int i = rowValueList.size(); i < totalNoOfRow; i++) { 
                            rowValueList.add(null);
                        }
                        try {
                            manageRow(rowValueList);
                        } catch (ParseException e) {
                            throw new GloriaSystemException(e, "Invalid Excel");
                        }
                    }
                    rowValueList = new ArrayList<String>();
                }
                String cellType = attributes.getValue("t");
                if ("s".equals(cellType)) {
                    nextIsString = true;
                    nextIsNumber = false;
                } else {
                    nextIsString = false;
                    String cellStyleStr = attributes.getValue("s");
                    if (cellStyleStr != null) {
                        nextIsNumber = true;
                        int styleIndex = Integer.parseInt(cellStyleStr);
                        XSSFCellStyle style = styles.getStyleAt(styleIndex);
                        this.formatIndex = style.getDataFormat();
                        this.formatString = style.getDataFormatString();
                        if (this.formatString == null) {
                            this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
                        }
                    }
                }
            }
            // Clear contents cache
            lastContents = "";
        }

        public void endElement(String uri, String localName, String name) throws SAXException {
            if (nextIsString) {
                int idx = Integer.parseInt(lastContents);
                lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
                nextIsString = false;
            }
            
            if (nextIsNumber && this.formatString != null && !StringUtils.isEmpty(lastContents)) {
                lastContents = new DataFormatter().formatRawCellContents(Double.parseDouble(lastContents), this.formatIndex, this.formatString);
                this.formatString = null;
            }

            if ("v".equals(name)) {
                if (rowNo == 1) {
                    actualColumnNames.add(lastContents);
                } else {
                    if (!isValidated) {
                        totalNoOfRow = actualColumnNames.size();
                        if (!validateColumnHeaders(actualColumnNames)) {
                            throw new GloriaSystemException("Invalid Excel Format", null);
                        }
                        isValidated = true;
                        currentRow = rowNo;
                    }
                    // Handle null column
                    for (int i = rowValueList.size(); i < (columnCount - 1); i++) { 
                        rowValueList.add(null);
                    }
                    if (rowNo == currentRow) {

                        rowValueList.add(trim(lastContents));
                        if (rowValueList.size() == totalNoOfRow) {
                            try {
                                manageRow(rowValueList);
                            } catch (ParseException e) {
                                throw new GloriaSystemException(e, "Invalid Excel");
                            }
                        }
                    } else {
                        currentRow = rowNo;
                        rowValueList.add(trim(lastContents));
                    }
                }
            }
        }
        
        public void characters(char[] ch, int start, int length) throws SAXException {
            lastContents += new String(Arrays.copyOf(ch, ch.length), start, length);
        }
        
        private String trim(String value) {
            return StringUtils.trim(value);
        }
    }

    /**
     * Get column number (e.g. D8 --> 4)
     * 
     * @param str
     * @return
     */
    public static int getColumnNo(String str) {
        int number = 0;

        char letter;
        for (int i = 0; i < str.length(); i++) {
            letter = str.charAt(i);
            if (letter >= 'A' && letter <= 'Z') {
                if (i == 0) {
                    number = (int) letter - 'A' + 1;
                } else {
                    number = number * TOTAL_ALPHA + (int) letter - 'A' + 1;
                }
            }

        }
        return number;
    }

    /**
     * Get row number (e.g. D8 --> 8)
     * 
     * @param value
     * @return
     */
    public static int getRowNo(String value) {
        return Integer.valueOf(value.replaceAll("\\D+", ""));
    }

    /**
     * Get java date from excel date (e.g. 20140401).
     * 
     * @param dateNumber
     * @return
     */
    public Date getJavaDate(double dateNumber) {
        return DateUtil.getJavaDate(dateNumber);
    }
}
