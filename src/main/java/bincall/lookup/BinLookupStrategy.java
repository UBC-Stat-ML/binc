package bincall.lookup;

import java.io.File;

import bincall.Command;



public interface BinLookupStrategy
{
  public File lookup(Command binary);
}
