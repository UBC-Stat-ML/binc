package bincall;

import java.io.File;



public class GlobalSettings
{
  private static String INSTALL_DIR = "~/.auto-installed/";
  
  public static File getInstallDir() 
  { 
    return BinCallUtils.resolveUserHome(INSTALL_DIR); 
  }
}
