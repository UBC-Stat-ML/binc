package binc;

import static binc.Command.*;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import tutorialj.Tutorial;

import binc.installer.Installers;

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

}
