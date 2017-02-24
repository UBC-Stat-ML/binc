package binc;

import java.util.Arrays;
import java.util.List;

import binc.lookup.BinLookupStrategy;
import binc.lookup.DirectoryListLookup;



public class GlobalSettings
{
  public static boolean warnIfMultipleMatchesFound = false;
  
  public static List<? extends BinLookupStrategy> 
    defaultLookupStrategies = Arrays.asList(
      DirectoryListLookup.fromPathEnvironmentVariable(),
      DirectoryListLookup.fromListWithUserHomeToResolve(DirectoryListLookup.defaultUnixPaths));
}
