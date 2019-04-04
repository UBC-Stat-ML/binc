package binc;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import binc.lookup.BinLookupStrategy;
import binc.lookup.DirectoryListLookup;

import com.google.common.collect.Lists;


public class Command
{
  
  public static Command cmd(File cmdLocation)
  {
    return cmd(cmdLocation.getName()).setLookupStrategy(
        DirectoryListLookup.fromListWithUserHomeToResolve(
            Collections.singletonList(cmdLocation.getParent())));
  }
  
  /**
   * Runs the command using the given name and the 
   * instance's look up strategy to locate the location
   * of the executable in the file system.
   * 
   * @param name
   * @return
   */
  public static Command byName(String name) 
  {
    return cmd(name);
  }
  
  /**
   * Runs the command at a specific location of
   * the file system.
   * 
   * This means that a single lookup strategy is used
   * to find the executable, i.e. the strategy that consists
   * in looking in a single directory. 
   * 
   * @param path
   * @return
   */
  public static Command byPath(File path)
  {
    return cmd(path);
  }
  
  public static Command byClass(Class<?> mainClass)
  {
    // Use the same classpath
    String classpath = Arrays.stream(((URLClassLoader) Thread.currentThread().getContextClassLoader()).getURLs())
        .map(URL::getFile)
        .collect(Collectors.joining(File.pathSeparator));
    
    Command javaCmd = byPath(Paths.get(System.getProperty("java.home"), "bin", "java").toFile());
    
    // get Xmx options such as -Xmx1g, etc
    for (String jvmArgument : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
      javaCmd = javaCmd.appendArg(jvmArgument);
    }
    
    return javaCmd
        .appendArg("-classpath").appendArg(classpath)
        .appendArg(mainClass.getCanonicalName());
  }
  
  public static Command cmd(String name)
  {
    if (name.contains("/") || name.contains("\\"))
      return cmd(new File(name));
    else
      return new Command(
        name, 
        new ArrayList<String>(), 
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


  public Command setLookupStrategies(List<? extends BinLookupStrategy> _strategies)
  {
    List<? extends BinLookupStrategy> strategies = _strategies;
    return new Command(
        name, 
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
        args, 
        workingDirectory, 
        maxDelay, 
        outputFile, 
        resultCodeInterpreter, 
        strategies,
        standardOutMirroring); 
  }
  
  private static List<String> split(String args)
  {
    String [] splitArgs = (args == null || args.matches("^\\s*$")) ? new String[]{} : args.split("\\s+");
    return Arrays.asList(splitArgs);
  }
  
  /**
   * Splits the provided string with the pattern \\s+ and set the
   * resulting list as the arguments. 
   * 
   * Note, if you want to NOT split by space (e.g. if one of the arguments
   * is a path which may contain spaces), use withArg() and appendArg().
   * 
   * E.g. call(cp.withArgs("-R -v").appendArg(file1).appendArg(file2));
   * 
   * @deprecated Use appendArgs or appendArg instead in 99% of situations. Otherwise object.withArgs(first).withArgs(second) will 
   *    silently override the first arg, which is easily overlooked and has introduced bugs in code a few times.
   * @param _args
   * @return
   */
  public Command withArgs(String _args)
  {
    List<String> args = split(_args);
    return new Command(
        name, 
        args, 
        workingDirectory, 
        maxDelay, 
        outputFile, 
        resultCodeInterpreter, 
        strategies,
        standardOutMirroring); 
  }
  
  /**
   * @deprecated Use appendArgs or appendArg instead in 99% of situations. Otherwise object.withArgs(first).withArgs(second) will 
   *    silently override the first arg, which is easily overlooked and has introduced bugs in code a few times.
   * @param _args
   * @return
   */
  public Command withSegmentedArguments(List<String> _args)
  {
    List<String> args = _args;
    return new Command(
        name, 
        args, 
        workingDirectory, 
        maxDelay, 
        outputFile, 
        resultCodeInterpreter, 
        strategies,
        standardOutMirroring); 
  }
  
  /**
   * Make sure you understand the difference with
   * withArgs (see withArgs).
   * 
   * @deprecated Use appendArgs or appendArg instead in 99% of situations. Otherwise object.withArgs(first).withArgs(second) will 
   *    silently override the first arg, which is easily overlooked and has introduced bugs in code a few times.
   * @param _arg
   * @return
   */
  public Command withArg(String _arg)
  {
    List<String> args = Lists.newArrayList(_arg);
    return new Command(
        name, 
        args, 
        workingDirectory, 
        maxDelay, 
        outputFile, 
        resultCodeInterpreter, 
        strategies,
        standardOutMirroring); 
  }
  
  /**
   * Make sure you understand the difference between appendArg
   * and appendArgs. This version will split the arguments using pattern \\s+.
   * @param _arg
   * @return
   */
  public Command appendArgs(String _args)
  {
    List<String> args = Lists.newArrayList(this.args);
    args.addAll(split(_args));
    return new Command(
        name, 
        args, 
        workingDirectory, 
        maxDelay, 
        outputFile, 
        resultCodeInterpreter, 
        strategies,
        standardOutMirroring); 
  }
  
  /**
   * Make sure you understand the difference between appendArg
   * and appendArgs. This version does not split.
   * @param _arg
   * @return
   */
  public Command appendArg(String _arg)
  {
    List<String> args = Lists.newArrayList(this.args);
    args.add(_arg);
    return new Command(
        name, 
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
        args, 
        workingDirectory, 
        maxDelay, 
        outputFile, 
        resultCodeInterpreter, 
        strategies,
        standardOutMirroring); 
  }
  
  public Command throwOnNonZeroReturnCode()
  {
    ResultCodeInterpreter resultCodeInterpreter = new ThrowOnNonZeroCode();
    return new Command(
        name, 
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
      List<String> args,
      File workingDirectory, 
      long maxDelay, 
      File outputFile, 
      ResultCodeInterpreter resultCodeInterpreter,
      List<? extends BinLookupStrategy> strategies,
      boolean standardOutMirroring)
  {
    this.name = name;
    this.args = args;
    this.workingDirectory = workingDirectory;
    this.maxDelay = maxDelay;
    this.outputFile = outputFile;
    this.resultCodeInterpreter = resultCodeInterpreter;
    this.strategies = strategies;
    this.standardOutMirroring = standardOutMirroring;
  }

  private final String name;
  
  private final List<String> args; // = "";
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
      resultCodeInterpreter.interpret(this, resultCode, result);

      timer.cancel();
    }
    catch (BinaryExecutionException bee) { throw bee; }
    catch (Throwable t) { throw new RuntimeException(t); }
    finally 
    {
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
    if (binaryLocation == null) 
      throw new RuntimeException("Binary not found: " + this.name + "\nLookup strategies: " + strategies + "\nCurrent directory: " + workingDirectory);
    List<String> command = Lists.newArrayList();
    command.add(binaryLocation.getAbsolutePath());
    command.addAll(args); //Arrays.asList(splitArgs));
    return command;
  }
  
  public File lookup()
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
    public void interpret(Command c, int code, CharSequence output);
  }
  
  public static class DefaultResultCodeInterpreter implements ResultCodeInterpreter
  {

    @Override
    public void interpret(Command c, int code, CharSequence output)
    {
      if (code != 0)
        System.err.println("Warning: command " + c.name + " returned non-zero code (" + code + "). Output so far:\n" + output);
    }
  }
  
  public static class ThrowOnNonZeroCode implements ResultCodeInterpreter
  {

    @Override
    public void interpret(Command c, int code, CharSequence output)
    {
      if (code != 0)
        throw new BinaryExecutionException(code, output, c);
    }
    
  }
  
  public static class BinaryExecutionException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;
    
    public final int code;
    public final CharSequence output;
    public final Command command;
    public BinaryExecutionException(int code, CharSequence output, Command command)
    {
      super();
      this.code = code;
      this.output = output;
      this.command = command;
    }
    @Override
    public String toString()
    {
      return "Warning: command " + command.name + " returned non-zero code (" + code + "). Output so far:\n" + output;
    }
  }

  public String getName()
  {
    return name;
  }

  public File getWorkingDirectory()
  {
    return workingDirectory;
  }
}
