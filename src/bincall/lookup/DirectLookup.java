package bincall.lookup;

import java.io.File;

import bincall.Binary;



public class DirectLookup implements BinLookupStrategy
{
  @Override
  public File lookup(Binary bin)
  {
    return new File(bin.getName());
  }

  @Override
  public String toString()
  {
    return "DirectLookup";
  }
}
