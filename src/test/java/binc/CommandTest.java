package binc;


import static binc.Command.call;
import static binc.Command.cmd;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import tutorialj.Tutorial;



public class CommandTest
{
  /**
   * Summary
   * -------
   * 
   * binc makes it easier to call binary program from java applications.
   * 
   * Installation
   * ------------
   * 
   * - Compile using ``gradle installApp``
   * - Add the jars in  ``build/install/binc/lib/`` to your classpath, OR, add
   * the following to your project gradle script 
   * 
   * ```groovy
   * dependencies {
   *   compile group: 'ca.ubc.stat', name: 'binc', version: '1.2.0'
   * }
   * repositories {
   *   mavenCentral()
   *   jcenter()
   *   maven {
   *     url "http://www.stat.ubc.ca/~bouchard/maven/"
   *   }
   * }
   * ```
   * 
   * Usage
   * -------
   * 
   * Use ``call(cmd([name])`` to call a command synchronously:
   */
  @Tutorial(showSource = true, startTutorial = "README.md")
  @Test
  public void testLs()
  {
    String result = call(Command.byName("ls"));
    System.out.println(result);
    System.out.println(call(Command.byPath(new File("/bin/ls"))));
  }
  
  /**
   * Add arguments and other options with method chaining:
   */
  @Tutorial
  @Test
  public void testArgs()
  {
    String result = call(cmd("ls").withArgs("-a"));
    System.out.println(result);
    
    File file1 = new File("test 1");
    File file2 = new File("test 2");
    // note: using appendArg (not plural) to avoid breaking the spaces in the path names
    call(cmd("cp").withArgs("-R -v").appendArg(file1.getAbsolutePath()).appendArg(file2.getAbsolutePath()));
  }
  
  /**
   * Method chaining is implemented using immutable creation,
   * so commands can be safely saved for later:
   * 
   * Immutability of method chaining can be used to create
   * libraries of commands. This makes call shorter, and makes
   * it easier to track down dependencies to external programs.
   */
  @Tutorial(showSource = true)
  @Test
  public void testSavedCmd()
  {
    Command simpleLs = cmd("ls");
    Command complexLs = simpleLs.withArgs("-a");
    String result = call(Command.cmd("ls"));
    assertTrue(result.charAt(0) != '.');
    assertTrue(call(complexLs).charAt(0) == '.');
  }
  
  /**
   * A more complex example showing other chaining
   * capabilities:
   */
  @Tutorial(showSource = true)
  @Test
  public void testComple()
  {
    String result = Command
      .cmd("ls")
      .setMaxDelay(1000)
      .ranIn(new File("/"))
      .callWithInputStreamContents("to input stream");
  }

}
