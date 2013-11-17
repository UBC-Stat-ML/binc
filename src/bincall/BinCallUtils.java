package bincall;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import bincall.lookup.BinLookupStrategy;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;



public class BinCallUtils
{
  private static String userHome = null;
  public static String userHomeFolder()
  {
    if (userHome != null) return userHome;
    return userHome = System.getProperty( "user.home" );
  }
  public static File resolveUserHome(String path)
  {
    return new File(path.replaceFirst("[~][/]", userHomeFolder() + "/"));
  }
  
  public static String toString(File f)
  {
    try
    {
      return Files.toString(f, Charsets.UTF_8);
    } catch (IOException e)
    {
      throw new RuntimeException();
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
