package bincall;

import static bincall.Command.cmd;

import java.io.File;

import bincall.installer.Installers;
import static bincall.Command.*;

public class Commands
{
  // Commands assumed to be present in decent UNIX installs
  public static Command make = cmd("make");
  public static Command gunzip = cmd("gunzip");
  public static Command tar = cmd("tar");
  public static Command bash = cmd(new File("/bin/bash")); 
  
  // Some examples of less standard commands
  public static Command jags = cmd("jags").setInstaller(Installers.jagsInstaller);
  
  
  public static void main(String [] args)
  {
    File jagsFolder = new File("/Users/bouchard/temp/jags-test");
    
    String response = call(jags.ranIn(jagsFolder).withArgs("script.jags").withStandardOutMirroring());
    
    System.out.println(response);
    
//    System.out.println("Test:");
//    System.out.println((jags.setLookupStrategies(GlobalSettings.forceInstallStrategy).callWithInputStreamContents("")));
  }
}
