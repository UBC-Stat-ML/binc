package bincall;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bincall.lookup.BinLookupStrategy;
import bincall.lookup.DirectLookup;
import bincall.lookup.DirectoryListLookup;
import bincall.lookup.InstallLookup;



public class GlobalSettings
{
  public static String INSTALL_DIR = "~/.auto-installed/";
  
  public static File getInstallDir() 
  { 
    return BinCallUtils.resolveUserHome(INSTALL_DIR); 
  }
  
  public static boolean warnIfMultipleMatchesFound = true;
  
//  public static boolean mergeErrorStreamWithOutputStream = true; 
  public static List<? extends BinLookupStrategy> defaultLookupStrategies = Arrays.asList(
      DirectLookup.instance,
      DirectoryListLookup.fromPathEnvironmentVariable(warnIfMultipleMatchesFound),
      DirectoryListLookup.fromListWithUserHomeToResolve(DirectoryListLookup.defaultUnixPaths, warnIfMultipleMatchesFound),
      InstallLookup.instance);
}
