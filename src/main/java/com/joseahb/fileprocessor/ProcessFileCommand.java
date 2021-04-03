package com.joseahb.fileprocessor;

import java.io.File;
// import java.io.IOException;

// import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "procexcel", version = "Prcocess-excel 1.0", mixinStandardHelpOptions = true)
public class ProcessFileCommand implements Runnable {
  private String fileName = "";
  private File file = null;
  // Constructor
  ProcessFileCommand (String[] args)  {
    if ( args.length > 0 ) {
      this.file = new File(args[0]);
      this.fileName = this.file.getName();
    }
  }

  @Parameters(paramLabel= "<xlsx-file>")
  @Override
  public void run(){
      FileReader r = new FileReader();
      try {
        r.processOneSheet(this.fileName);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      try {
        r.processAllSheets(this.fileName);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
  }
}
