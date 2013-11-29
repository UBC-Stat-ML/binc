package binc.lookup;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import binc.BinCallUtils;
import binc.Command;
import binc.GlobalSettings;

import com.google.common.collect.Lists;

import static binc.Commands.*;


public class DirectoryListLookup implements BinLookupStrategy
{
  private final List<File> directories;
  
  public DirectoryListLookup(List<File> directories)
  {
    this.directories = directories;
  }
  
  public static String getPathFromBash()
  {
    return bash.withArgs("-l -s").callWithInputStreamContents("echo $PATH;");
  }
  
  public static DirectoryListLookup fromPathEnvironmentVariable()
  {
    String value = System.getenv("PATH");
    
//    if (value == null)  // LATER: fix missing ENV variable?
//    {
//      String fromBash = getPathFromBash();
//      if (fromBash != null)
//        System.g
//    }
    
    List<File> directories = Lists.newArrayList();
    for (String item : value.split("[:]"))
      directories.add(new File(item));
    return new DirectoryListLookup(directories);
  }
  
  public static DirectoryListLookup fromListWithUserHomeToResolve(List<String> paths)
  {
    List<File> directories = Lists.newArrayList();
    for (String path : paths)
      directories.add(BinCallUtils.resolveUserHome(path));
    return new DirectoryListLookup(directories);
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
              if (GlobalSettings.warnIfMultipleMatchesFound)
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
    return "DirectoryListLookup [directories=" + directories + "]";
  }
  
}
