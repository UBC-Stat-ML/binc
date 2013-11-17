package bincall.lookup;

import java.io.File;

import bincall.Command;



public class DirectLookup implements BinLookupStrategy
{
  public static final DirectLookup instance = new DirectLookup();
  private DirectLookup() {}
  @Override
  public File lookup(Command bin)
  {
    File guess = new File(bin.getName());
    if (!guess.exists())
      return null;
    return guess;
  }

  @Override
  public String toString()
  {
    return "DirectLookup";
  }
}
