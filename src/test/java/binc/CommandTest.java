package binc;


import java.io.File;

import org.junit.Test;
import org.junit.runners.JUnit4;

import tutorialj.Tutorial;

import static binc.Command.*;

import static binc.Commands.*;

import static org.junit.Assert.*;


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
 *   compile group: 'com.3rdf', name: 'binc', version: '1.1'
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
 */
@Tutorial(startTutorial = "README.md")
public class CommandTest
{
  /**
   * Usage
   * -------
   * 
   * Use ``call(cmd([name])`` to call a command synchronously:
   */
  @Tutorial(showSource = true)
  @Test
  public void testLs()
  {
    String result = call(cmd("ls"));
    System.out.println(result);
  }
  
  /**
   * Add arguments and other options with method chaining:
   */
  @Tutorial(showSource = true)
  @Test
  public void testArgs()
  {
    String result = call(cmd("ls").withArgs("-a"));
    System.out.println(result);
  }
  
  /**
   * Method chaining is implemented using immutable creation,
   * so commands can be safely saved for later:
   */
  @Tutorial(showSource = true, nextStep = Commands.class)
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
