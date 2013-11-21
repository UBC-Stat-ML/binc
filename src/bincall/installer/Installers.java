package bincall.installer;

import java.io.File;

import static bincall.installer.Downloader.*;
import static bincall.Command.*;
import static bincall.Commands.*;

public class Installers
{
  public static final Installer jagsInstaller = new Installer() {

    @Override public long getID() { return 0; }

    @Override
    public File install(InstallerContext context)
    {
      // download, gunzip, untar
      File installFolder = context.getInstallFolder();
      String downloadedFileName = "JAGS-3.4.0";
      File destination = new File(installFolder, downloadedFileName + ".tar.gz");
      downloader(destination, "http://3thfoundation.com/resources/JAGS-3.4.0.tar.gz")
        .addMirror("http://sourceforge.net/projects/mcmc-jags/files/JAGS/3.x/Source/JAGS-3.4.0.tar.gz/download")
        .download();
      call(gunzip.ranIn(installFolder).withArgs(downloadedFileName + ".tar.gz"));
      call(tar.ranIn(installFolder).withArgs("-xf " + downloadedFileName + ".tar"));
      File decompressed = new File(installFolder, downloadedFileName);
      
      // compile
      call(cmd(new File(decompressed, "configure")).ranIn(decompressed));
      call(make.ranIn(decompressed));
      
      return new File(decompressed, "src/terminal/jags");
    }
    
  };
}
