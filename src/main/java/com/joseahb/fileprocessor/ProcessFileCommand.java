package com.joseahb.fileprocessor;

import java.io.File;
// import java.io.IOException;

// import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "procexcel", version = "Prcocess-excel 1.0", mixinStandardHelpOptions = true)
public class ProcessFileCommand implements Runnable { 

  @Parameters(paramLabel= "<xlsxfile>", defaultValue = "sample.xlsx",  // |4|
  description = "Spreadsheet file to be processed")

  private String xlsxfile = "sample.xlsx";
  
  @Override
  public void run(){
    FileReader r = new FileReader();
    try {
      r.processOneSheet(xlsxfile);
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      r.processAllSheets(xlsxfile);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

