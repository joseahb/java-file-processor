package fileprocessor;

public class ProcessFile {
  public static void main(String[] args) {
    int exitCode = new CommandLine(new ProcessFileCommand()).execute(args)
    System.out.println(exitCode);
  }
}
