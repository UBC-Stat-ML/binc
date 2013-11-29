package binc.lookup;

import java.io.File;

import binc.Command;



public interface BinLookupStrategy
{
  public File lookup(Command binary);
}
