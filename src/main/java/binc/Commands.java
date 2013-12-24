package binc;

import static binc.Command.*;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import tutorialj.Tutorial;

import binc.installer.Installers;

/**
 * Immutability of method chaining can be used to create
 * libraries of commands. This makes call shorter, and makes
 * it easier to track down dependencies to external programs.
 */
@Tutorial(order = 4, showSource = true)
public class Commands
{

  @Test
  public void testLibraryCmd()
  {
    call(tar.withArgs("-help"));
  }
  
  // Commands assumed to be present in decent UNIX installs
  public static Command make = cmd("make");
  public static Command gunzip = cmd("gunzip");
  public static Command tar = cmd("tar");
  public static Command bash = cmd(new File("/bin/bash")); 
  public static Command rm = cmd(new File("/bin/rm"));
  
//  // Some examples of less standard commands
//  public static Command jags = cmd("jags").setInstaller(Installers.jagsInstaller);
//  
//  
//  public static void main(String [] args)
//  {
//    File jagsFolder = new File("/Users/bouchard/temp/jags-test");
//    
//    String response = call(jags.ranIn(jagsFolder).withArgs("script.jags").withStandardOutMirroring());
//    
//    System.out.println(response);
//    
////    System.out.println("Test:");
////    System.out.println((jags.setLookupStrategies(GlobalSettings.forceInstallStrategy).callWithInputStreamContents("")));
//  }
}
