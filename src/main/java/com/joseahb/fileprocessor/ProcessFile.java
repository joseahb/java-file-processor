package com.joseahb.fileprocessor;

import picocli.CommandLine;

public class ProcessFile {
  public static void main(String[] args) throws Exception {
    int exitCode = new CommandLine(new ProcessFileCommand(args)).execute(args);
    System.out.println(exitCode);
  }
}
