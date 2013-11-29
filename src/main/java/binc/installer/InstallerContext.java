package binc.installer;

import java.io.File;



public class InstallerContext
{ 
  private final File installFolder;

  public InstallerContext(File installFolder)
  {
    installFolder.mkdirs();
    this.installFolder = installFolder;
  }

  public File getInstallFolder()
  {
    return installFolder;
  }

}
