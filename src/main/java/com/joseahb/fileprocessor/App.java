package com.joseahb.fileprocessor;

import picocli.CommandLine;

public class App {
  public static void main(String[] args) throws Exception {
    ProcessFileCommand pfcmd = new ProcessFileCommand();
    System.exit(new CommandLine(pfcmd).execute(args));
  }
}
