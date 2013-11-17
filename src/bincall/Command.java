package bincall;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import bincall.lookup.BinLookupStrategy;

import com.google.common.collect.Lists;



public class Command
{
  
  public static Command cmd(String name)
  {
    return new Command(
        name, 
        new ArrayList<Installer>(), 
        "", 
        null, 
        Long.MAX_VALUE, 
        null, 
        new DefaultResultCodeInterpreter(), 
        GlobalSettings.defaultLookupStrategies);
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
        strategies); 
  }
  
  public Command setInstaller(List<Installer> _installers)
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
        strategies); 
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
        strategies); 
  }
  
  public Command runIn(File _workingDirectory) 
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
        strategies); 
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
        strategies); 
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
        strategies); 
  }

  public Command(
      String name, 
      List<Installer> installers, 
      String args,
      File workingDirectory, 
      long maxDelay, 
      File outputFile, 
      ResultCodeInterpreter resultCodeInterpreter,
      List<? extends BinLookupStrategy> strategies)
  {
    this.name = name;
    this.installers = installers;
    this.args = args;
    this.workingDirectory = workingDirectory;
    this.maxDelay = maxDelay;
    this.outputFile = outputFile;
    this.resultCodeInterpreter = resultCodeInterpreter;
    this.strategies = strategies;
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
  
  public String call() { return call(""); }
  public String call(String inputStreamContents)
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
        stdoutBufferedReader = new BufferedReader(stdoutReader);//,
      String line = null;
      // send the input
      if (inputStreamContents.length() > 0)
      {
        OutputStream stdin = proc.getOutputStream();
        PrintWriter pw = new PrintWriter(stdin);
        pw.append(inputStreamContents);
        pw.close();
      }
      // read the output of the program
      while ( (line = stdoutBufferedReader.readLine()) != null)
      {
        result.append(line + "\n");
        if (writer != null)
          writer.append(line + "\n");
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
    
    System.out.println(cmd("sed").withArgs("s|x|y|").call("T-Rex"));
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
