package com.joseahb.fileprocessor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class FileReader {
    public void processAllSheets(String filename) throws Exception {
        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader( pkg );
        SharedStringsTable sst = r.getSharedStringsTable();
        XMLReader parser = fetchSheetParser(sst);
        Iterator<InputStream> sheets = r.getSheetsData();
        System.out.println("Processing All sheet:\n");
        while(sheets.hasNext()) {
            System.out.println("============Processing new sheet:=================\n");
            InputStream sheet = sheets.next();
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            sheet.close();
            System.out.println("===========Sheet Processing Done==================");
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
        // private boolean nextIsString;
        private String fieldName;
        private Boolean transactionInfo = false;
        private Boolean transactionId= false;
        private Boolean amount = false;
        ArrayList<String> data = new ArrayList<String>();

        private SheetHandler(SharedStringsTable sst) {
            this.sst = sst;
        }
        public void startElement(String uri, String localName, String name,
                                 Attributes attributes) throws SAXException {
            // String cellType = attributes.getValue("t"); // get the cell type
            // c => cell
            if(name.equals("c")) {
                // Print the cell reference
                fieldName = attributes.getValue("r");
                switch (fieldName.charAt(0)) {
                    case 'B':
                        transactionId = true;
                        break;
                    case 'D':
                        transactionInfo = true;
                        break;
                    case 'G':
                        amount = true;
                    default:
                        return;
                }
            }
            // Clear contents cache
            lastContents = "";
        }
        public void endElement(String uri, String localName, String name)
                throws SAXException {
            // v => contents of a cell
            // Output after we've seen the string contents
            if(name.equals("v")) {
                // Process the last contents as required.
                // Do now, as characters() may be called more than once
                String rowData = "";
                if(transactionId){
                    int idx = Integer.parseInt(lastContents);
                    lastContents = sst.getItemAt(idx).getString();
                    rowData += lastContents;
                    transactionId = false;
                }
                if(transactionInfo) {

                    int idx = Integer.parseInt(lastContents);
                    lastContents = sst.getItemAt(idx).getString();

                    String accountInfo = extractData(lastContents);
                    rowData += accountInfo;

                    transactionInfo = false;
                }
                if(amount){
                    rowData += " " + lastContents + "\n";
                    amount = false;
                }
                if (rowData != "") {
                    this.data.add(rowData);
                }
            }
        }
        public void characters(char[] ch, int start, int length) {
            lastContents += new String(ch, start, length);
        }
        public void endDocument() throws SAXException {
            System.out.println(this.data);
        }
    }
    public static String extractData(String cellContent){
        String[] splitContent = cellContent.split(" ");
        String account = "";
        String fullname = "";
        String extractedData = "";
        for (int i = 0; i < splitContent.length; i++) {
            if (splitContent[i].startsWith("0747")) {
                account = splitContent[i];
                String name = splitContent[i+1] + " " +splitContent[i+2].replaceAll("[0123456789]","");
                fullname = name;
            }
        }
        extractedData = account + " " +fullname;
        return extractedData; 
    }
}
