package binc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import binc.lookup.BinLookupStrategy;
import binc.lookup.DirectoryListLookup;
import binc.lookup.InstallLookup;



public class GlobalSettings
{
  private static String INSTALL_DIR = "~/.auto-installed/";
  
  public static File getInstallDir() 
  { 
    File installDir = BinCallUtils.resolveUserHome(INSTALL_DIR); 
    installDir.mkdirs();
    return installDir;
  }
  
  public static boolean warnIfMultipleMatchesFound = true;
  
//  public static boolean mergeErrorStreamWithOutputStream = true; 
  public static List<? extends BinLookupStrategy> 
    defaultLookupStrategies = Arrays.asList(
      DirectoryListLookup.fromPathEnvironmentVariable(),
      DirectoryListLookup.fromListWithUserHomeToResolve(DirectoryListLookup.defaultUnixPaths),
      InstallLookup.instance),
    forceInstallStrategy = Arrays.asList(
      InstallLookup.instance);
  
  
}
