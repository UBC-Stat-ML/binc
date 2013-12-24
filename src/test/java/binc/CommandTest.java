package binc;


import org.junit.Test;
import org.junit.runners.JUnit4;

import tutorialj.Tutorial;

import static binc.Command.*;

import static binc.Commands.*;

import static org.junit.Assert.*;

public class CommandTest
{
  /**
   * Usage
   * -------
   * 
   * Use ``call(cmd([name])`` to call a command synchronously:
   */
  @Tutorial(order = 1, showSource = true)
  @Test
  public void testLs()
  {
    String result = call(cmd("ls"));
    System.out.println(result);
  }
  
  /**
   * Add arguments and other options with method chaining:
   */
  @Tutorial(order = 2, showSource = true)
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
  @Tutorial(order = 3, showSource = true)
  @Test
  public void testSavedCmd()
  {
    Command simpleLs = cmd("ls");
    Command complexLs = simpleLs.withArgs("-a");
    String result = call(Command.cmd("ls"));
    assertTrue(result.charAt(0) != '.');
    assertTrue(call(complexLs).charAt(0) == '.');
  }
  
}
