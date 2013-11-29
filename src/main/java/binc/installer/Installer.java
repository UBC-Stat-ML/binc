package binc.installer;

import java.io.File;



public interface Installer
{
  public long getID();
  public File install(InstallerContext context);
}
