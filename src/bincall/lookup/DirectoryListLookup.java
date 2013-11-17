package bincall.lookup;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import bincall.BinCallUtils;
import bincall.Command;

import com.google.common.collect.Lists;




public class DirectoryListLookup implements BinLookupStrategy
{
  private final List<File> directories;
  private final boolean warnIfMultipleMatchesFound;
  
  public DirectoryListLookup(List<File> directories,
      boolean warnIfMultipleMatchesFound)
  {
    this.directories = directories;
    this.warnIfMultipleMatchesFound = warnIfMultipleMatchesFound;
  }
  
  public static DirectoryListLookup fromPathEnvironmentVariable(boolean warnIfMultipleMatchesFound)
  {
    String value = System.getenv("PATH");
    List<File> directories = Lists.newArrayList();
    for (String item : value.split("[:]"))
      directories.add(new File(item));
    return new DirectoryListLookup(directories, warnIfMultipleMatchesFound);
  }
  
  public static DirectoryListLookup fromListWithUserHomeToResolve(List<String> paths, boolean warnIfMultipleMatchesFound)
  {
    List<File> directories = Lists.newArrayList();
    for (String path : paths)
      directories.add(BinCallUtils.resolveUserHome(path));
    return new DirectoryListLookup(directories, warnIfMultipleMatchesFound);
  }
  
  public static final List<String> defaultUnixPaths = Arrays.asList(new String[]{
    "/bin/",
    "/sbin/",
    "/usr/bin/",
    "/usr/local/bin/",
    "/usr/sbin/",
    "~/bin/"
  });
  


  @Override
  public File lookup(Command bin)
  {
    boolean found = false;
    File result = null;
    for (File directory : directories)
      if (directory.isDirectory())
        for (File file : directory.listFiles())
          if (file.canExecute() && file.getName().equals(bin.getName()))
          {
            if (found) 
            {
              if (warnIfMultipleMatchesFound)
                System.err.println("Duplicate binaries found in path: " + 
                    bin.getName() + " is in both " + file.getAbsolutePath() + 
                    " and " + result.getAbsolutePath());
            }
            else
            {
              found = true;
              result = file;
            }
          }
    return result;
  }

  @Override
  public String toString()
  {
    return "DirectoryListLookup [directories=" + directories
        + ", raiseExceptionIfMultipleMatchesFound="
        + warnIfMultipleMatchesFound + "]";
  }
  
}
