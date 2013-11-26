package bincall;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import bincall.installer.Installer;
import bincall.lookup.BinLookupStrategy;
import bincall.lookup.DirectoryListLookup;

import com.google.common.collect.Lists;



public class Command
{
  
  public static Command cmd(File cmdLocation)
  {
    return cmd(cmdLocation.getName()).setLookupStrategy(
        DirectoryListLookup.fromListWithUserHomeToResolve(
            Collections.singletonList(cmdLocation.getParent())));
  }
  
  public static Command cmd(String name)
  {
    if (name.contains("/"))
      return cmd(new File(name));
    else
      return new Command(
        name, 
        new ArrayList<Installer>(), 
        "", 
        null, 
        Long.MAX_VALUE, 
        null, 
        new DefaultResultCodeInterpreter(), 
        GlobalSettings.defaultLookupStrategies,
        false);
  }
  
  public File which()
  {
    return lookup();
  }
  
  public Command setInstaller(Installer _installer)
  {
    List<Installer> installers = Lists.newArrayList();
    installers.add(_installer);
    return new Command(
        name, 
        installers, 
        args, 
        workingDirectory, 
        maxDelay, 
        outputFile, 
        resultCodeInterpreter, 
        strategies,
        standardOutMirroring); 
  }
  
  public Command setInstallers(List<Installer> _installers)
  {
    List<Installer> installers = _installers;
    return new Command(
        name, 
        installers, 
        args, 
        workingDirectory, 
        maxDelay, 
        outputFile, 
        resultCodeInterpreter, 
        strategies,
        standardOutMirroring); 
  }
  
  public Command setLookupStrategies(List<? extends BinLookupStrategy> _strategies)
  {
    List<? extends BinLookupStrategy> strategies = _strategies;
    return new Command(
        name, 
        installers, 
        args, 
        workingDirectory, 
        maxDelay, 
        outputFile, 
        resultCodeInterpreter, 
        strategies,
        standardOutMirroring); 
  }
  
  public Command setLookupStrategy(BinLookupStrategy _strategy)
  {
    List<BinLookupStrategy> strategies = new ArrayList<BinLookupStrategy>();
    strategies.add(_strategy);
    return new Command(
        name, 
        installers, 
        args, 
        workingDirectory, 
        maxDelay, 
        outputFile, 
        resultCodeInterpreter, 
        strategies,
        standardOutMirroring); 
  }
  
  public Command withArgs(String _args)
  {
    String args = _args;
    return new Command(
        name, 
        installers, 
        args, 
        workingDirectory, 
        maxDelay, 
        outputFile, 
        resultCodeInterpreter, 
        strategies,
        standardOutMirroring); 
  }
  
  public Command ranIn(File _workingDirectory) 
  {
    File workingDirectory = _workingDirectory;
    return new Command(
        name, 
        installers, 
        args, 
        workingDirectory, 
        maxDelay, 
        outputFile, 
        resultCodeInterpreter, 
        strategies,
        standardOutMirroring); 
  }
  
  public Command setMaxDelay(long _maxDelay)
  {
    long maxDelay = _maxDelay;
    return new Command(
        name, 
        installers, 
        args, 
        workingDirectory, 
        maxDelay, 
        outputFile, 
        resultCodeInterpreter, 
        strategies,
        standardOutMirroring); 
  }

  public Command saveOutputTo(File _outputFile)
  {
    File outputFile = _outputFile;
    return new Command(
        name, 
        installers, 
        args, 
        workingDirectory, 
        maxDelay, 
        outputFile, 
        resultCodeInterpreter, 
        strategies,
        standardOutMirroring); 
  }
  
  public Command withStandardOutMirroring()
  {
    boolean standardOutMirroring = true;
    return new Command(
        name, 
        installers, 
        args, 
        workingDirectory, 
        maxDelay, 
        outputFile, 
        resultCodeInterpreter, 
        strategies,
        standardOutMirroring); 
  }

  public Command(
      String name, 
      List<Installer> installers, 
      String args,
      File workingDirectory, 
      long maxDelay, 
      File outputFile, 
      ResultCodeInterpreter resultCodeInterpreter,
      List<? extends BinLookupStrategy> strategies,
      boolean standardOutMirroring)
  {
    this.name = name;
    this.installers = installers;
    this.args = args;
    this.workingDirectory = workingDirectory;
    this.maxDelay = maxDelay;
    this.outputFile = outputFile;
    this.resultCodeInterpreter = resultCodeInterpreter;
    this.strategies = strategies;
    this.standardOutMirroring = standardOutMirroring;
  }

  private final String name;
  private final List<Installer> installers;
  
  private final String args; // = "";
  private final File workingDirectory; // = null;
  private final long maxDelay; // = Long.MAX_VALUE;
//  private final String inputStreamContents; // = ""; passed by call() instead (in prevision for pipes)
  private final File outputFile; // = null;
  private final ResultCodeInterpreter resultCodeInterpreter; // = new DefaultResultCodeInterpreter();
  private final List<? extends BinLookupStrategy> strategies;
  private final boolean standardOutMirroring;
  
  public ProcessBuilder newProcessBuilder()
  {
    // find the bin
    List<String> command = buildCommand();
    // prepare the process builder
    ProcessBuilder pb = new ProcessBuilder(command);
    if (workingDirectory != null)
      pb.directory(workingDirectory);
    pb.redirectErrorStream(true);
    return pb;
  }
  
  public static String call(Command cmd)
  {
    return cmd.call();
  }
  
  public String call() { return callWithInputStreamContents(""); }
  public String callWithInputStreamContents(String inputStreamContents)
  {
    ProcessBuilder pb = newProcessBuilder();
    Process _proc = null;
    StringBuilder result = new StringBuilder();
    PrintWriter writer = null;
    try
    {     
      writer = outputFile == null ? null : new PrintWriter(outputFile, "UTF-8");
      final Process proc = pb.start();
      _proc = proc;
      Timer timer = new Timer();
      if (maxDelay != Long.MAX_VALUE)
        timer.schedule(new TimerTask() {
          @Override  public void run() {
            System.err.println("Command " + name + " timed out (max time was " + maxDelay + "ms)");
            proc.destroy(); }
        }, maxDelay);
      InputStream 
        stdout = proc.getInputStream();
      InputStreamReader 
        stdoutReader = new InputStreamReader(stdout);
      BufferedReader 
        stdoutBufferedReader = new BufferedReader(stdoutReader);
      String line = null;
      // send the input
      OutputStream stdin = proc.getOutputStream();
      PrintWriter pw = new PrintWriter(stdin);
      pw.append(inputStreamContents);
      pw.close();
      // read the output of the program
      while ( (line = stdoutBufferedReader.readLine()) != null)
      {
        result.append(line + "\n");
        if (writer != null)
          writer.append(line + "\n");
        if (standardOutMirroring)
          System.out.println(line);
      }

      int resultCode = proc.waitFor();
      resultCodeInterpreter.interpret(this, resultCode);

      timer.cancel();
    }
    catch (Throwable t) { throw new RuntimeException(t); }
    finally {
      if (_proc != null)
      {
        try
        {
          _proc.getErrorStream().close();
          _proc.getInputStream().close();
          _proc.getOutputStream().close();
          if (writer != null)
            writer.close();
        } catch (Exception e) { throw new RuntimeException(e); }
      }
    }
    return result.toString();
  }
  
  private List<String> buildCommand()
  {
    File binaryLocation = lookup();
    if (binaryLocation == null) throw new RuntimeException("Binary not found: " + this.name);
    String [] splitArgs = (args == null || args.matches("^\\s*$")) ? new String[]{} : this.args.split("\\s+");
    List<String> command = Lists.newArrayList();
    command.add(binaryLocation.getAbsolutePath());
    command.addAll(Arrays.asList(splitArgs));
    return command;
  }
  
  private File lookup()
  {
    for (BinLookupStrategy strategy : strategies)
    {
      File result = strategy.lookup(this);
      if (result != null)
        return result;
    }
    return null;
  }
  
  public static interface ResultCodeInterpreter
  {
    public void interpret(Command c, int code);
  }
  
  public static class DefaultResultCodeInterpreter implements ResultCodeInterpreter
  {

    @Override
    public void interpret(Command c, int code)
    {
      if (code != 0)
        System.err.println("Warning: command " + c.name + " returned non-zero code (" + code + ")");
    }
  }
  
  public static void main(String[] args)
  {
//    Command cmd = cmd("ls").withArgs("-al");
//    System.out.println(cmd.call());
    
    System.out.println(cmd("sed").withArgs("s|x|y|").callWithInputStreamContents("T-Rex"));
    
//    cmd("jags").setInstaller(_installer)
  }

  public String getName()
  {
    return name;
  }

  public List<Installer> getInstallers()
  {
    return installers;
  }
}
