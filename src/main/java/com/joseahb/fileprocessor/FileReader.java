package com.joseahb.fileprocessor;

import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.util.SystemOutLogger;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.w3c.dom.TypeInfo;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.ParserConfigurationException;

public class FileReader {
    private ArrayList<String> data;
    public void processOneSheet(String filename) throws Exception {
        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader( pkg );
        SharedStringsTable sst = r.getSharedStringsTable();
        XMLReader parser = fetchSheetParser(sst);
        // To look up the Sheet Name / Sheet Order / rID,
        //  you need to process the core Workbook stream.
        // Normally it's of the form rId# or rSheet#
        InputStream sheet2 = r.getSheet("rId2");
        InputSource sheetSource = new InputSource(sheet2);
        parser.parse(sheetSource);
        sheet2.close();
    }
    public void processAllSheets(String filename) throws Exception {
        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader( pkg );
        SharedStringsTable sst = r.getSharedStringsTable();
        XMLReader parser = fetchSheetParser(sst);
        Iterator<InputStream> sheets = r.getSheetsData();
        System.out.println("Processing All sheet:\n");
        while(sheets.hasNext()) {
            System.out.println("Processing new sheet:\n");
            InputStream sheet = sheets.next();
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            sheet.close();
            System.out.println("Done");
        }
    }
    public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException, ParserConfigurationException {
        XMLReader parser = XMLHelper.newXMLReader();
        ContentHandler handler = new SheetHandler(sst);
        parser.setContentHandler(handler);
        return parser;
    }
    /**
     * See org.xml.sax.helpers.DefaultHandler javadocs
     */
    private static class SheetHandler extends DefaultHandler {
        private SharedStringsTable sst;
        private String lastContents;
        private boolean nextIsString;
        ArrayList<String> tmp;
        private SheetHandler(SharedStringsTable sst) {
            this.sst = sst;
        }
        public void startElement(String uri, String localName, String name,
                                 Attributes attributes) throws SAXException {
            
            String cellType = attributes.getValue("t"); // get the cell type
            // c => cell
            if(name.equals("c")) {
                // Print the cell reference
                // System.out.print(attributes.getValue("r") + " - ");
                // Figure out if the value is an index in the SST
                if(cellType != null && cellType.equals("s")) {
                    nextIsString = true;
                }
                 else {
                    nextIsString = false;
                }
            }
            // Clear contents cache
            lastContents = "";
            tmp = null;
        }
        public void endElement(String uri, String localName, String name)
                throws SAXException {
            // Process the last contents as required.
            // Do now, as characters() may be called more than once
            if(nextIsString) {
                int idx = Integer.parseInt(lastContents);
                lastContents = sst.getItemAt(idx).getString();
                if (isTransactionInfo(lastContents)){
                    String[] extracted = extractData(lastContents).toArray(new String[0]);
                    System.out.println(extracted);

                    // tmp.add(extracted.toString());
                }
                nextIsString = false;
            }
            // v => contents of a cell
            // Output after we've seen the string contents
            if(name.equals("v")) {
                // System.out.println("temp:" + tmp);
                System.out.println(lastContents);
            }
        }

       public void characters(char[] ch, int start, int length) {
            lastContents += new String(ch, start, length);
        }
    }
    public static Boolean isTransactionInfo (String cellContent) {
        if (cellContent.startsWith("Pesalink") && cellContent.length() > 22) {
            return true;
        } else {
            return false;
        }
    }
    public static ArrayList<String> extractData(String cellContent){
        String[] splitContent = cellContent.split(" ");
        String account = "";
        String fullname = "";
        ArrayList<String>  data = null;
        for (int i = 0; i < splitContent.length; i++) {
            if (splitContent[i].startsWith("0747")) {
                account = splitContent[i];
                String name = splitContent[i+1] + " " +splitContent[i+2].replaceAll("[0123456789]","");
                fullname = name;
            }
        }
        data.add(fullname);
        data.add(account);
        return data; 
    }
}
