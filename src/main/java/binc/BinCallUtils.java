package binc;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import binc.lookup.BinLookupStrategy;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;


/**
 * 
 */
public class BinCallUtils
{ 
  private static String userHome = null;
  public static String userHomeFolder()
  {
    if (userHome != null) return userHome;
    userHome = System.getProperty( "user.home" );
    if (userHome == null)
      throw new RuntimeException("Could not resolve the user folder");
    if (!new File(userHome).exists())
      throw new RuntimeException("Incorrect home folder:" + userHome);
    return userHome;
  }
  public static File resolveUserHome(String path)
  {
    if (path.contains("~/"))
      path = path.replaceFirst("[~][/]", userHomeFolder() + "/");
    return new File(path);
  }
  
  public static String toString(File f)
  {
    try
    {
      return Files.toString(f, Charsets.UTF_8);
    } catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }
  
  public static String readFirstLine(File f)
  {
    try
    {
      return Files.readFirstLine(f, Charsets.UTF_8);
    } catch (IOException e)
    {
      throw new RuntimeException();
    }
  }
  public static void write(File f, CharSequence contents)
  {
    try
    {
      Files.write(contents, f, Charsets.UTF_8);
    } catch (IOException e)
    {
      throw new RuntimeException();
    }
  }

  

  
}
