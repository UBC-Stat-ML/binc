package bincall.lookup;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import bincall.BinCallUtils;
import bincall.Binary;

import com.google.common.collect.Lists;




public class DirectoryListLookup implements BinLookupStrategy
{
  private final List<File> directories;
  private final boolean raiseExceptionIfMultipleMatchesFound;
  
  public DirectoryListLookup(List<File> directories,
      boolean raiseExceptionIfMultipleMatchesFound)
  {
    this.directories = directories;
    this.raiseExceptionIfMultipleMatchesFound = raiseExceptionIfMultipleMatchesFound;
  }
  
  public static DirectoryListLookup fromPathEnvironmentVariable(boolean raiseExceptionIfMultipleMatchesFound)
  {
    String value = System.getenv("PATH");
    List<File> directories = Lists.newArrayList();
    for (String item : value.split("[:]"))
      directories.add(new File(item));
    return new DirectoryListLookup(directories, raiseExceptionIfMultipleMatchesFound);
  }
  
  public static DirectoryListLookup fromListWithUserHomeToResolve(List<String> paths, boolean raiseExceptionIfMultipleMatchesFound)
  {
    List<File> directories = Lists.newArrayList();
    for (String path : paths)
      directories.add(BinCallUtils.resolveUserHome(path));
    return new DirectoryListLookup(directories, raiseExceptionIfMultipleMatchesFound);
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
  public File lookup(Binary bin)
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
              if (raiseExceptionIfMultipleMatchesFound)
                throw new RuntimeException("Duplicate binaries found in path: " + 
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
        + raiseExceptionIfMultipleMatchesFound + "]";
  }
  
}
