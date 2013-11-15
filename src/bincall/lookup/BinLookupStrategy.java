package bincall.lookup;

import java.io.File;

import bincall.Binary;



public interface BinLookupStrategy
{
  public File lookup(Binary binary);
}
