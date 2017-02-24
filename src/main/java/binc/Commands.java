package binc;

import static binc.Command.cmd;

import java.io.File;



public class Commands
{
  // Commands assumed to be present in decent UNIX installs
  public static Command make = cmd("make");
  public static Command gunzip = cmd("gunzip");
  public static Command tar = cmd("tar");
  public static Command bash = cmd(new File("/bin/bash")); 
  public static Command rm = cmd(new File("/bin/rm"));

}
