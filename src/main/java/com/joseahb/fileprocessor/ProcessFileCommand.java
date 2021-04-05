package com.joseahb.fileprocessor;

// import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "procexcel", version = "Prcocess-excel 1.0", mixinStandardHelpOptions = true)
public class ProcessFileCommand implements Runnable { 

  @Option(names = { "-f", "--file" }, paramLabel = "excelFile", description = "the Excel file{.xlsx}")
  String excelFile;
  
  // @Parameters(index="0", paramLabel= "<xlsxfile>", 
  // description = "Spreadsheet file to be processed") String xlsxfile;
  @Override
  public void run(){
    FileReader r = new FileReader();    
    try {
      r.processAllSheets(excelFile);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

