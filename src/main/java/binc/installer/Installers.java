package binc.installer;

import java.io.File;

import static binc.Command.*;
import static binc.Commands.*;
import static binc.installer.Downloader.*;

public class Installers
{
  public static final Installer jagsInstaller = new Installer() {

    @Override public long getID() { return 1; }

    @Override
    public File install(InstallerContext context)
    {
      // download, gunzip, untar
      File installFolder = context.getInstallFolder();
      String downloadedFileName = "JAGS-2.2.0";
      File destination = new File(installFolder, downloadedFileName + ".tar.gz");
      downloader(destination, "http://sourceforge.net/projects/mcmc-jags/files/JAGS/2.x/Source/JAGS-2.2.0.tar.gz/download")
//        .addMirror("http://3thfoundation.com/resources/JAGS-3.4.0.tar.gz")
        .download();
      call(gunzip.ranIn(installFolder).withArg(downloadedFileName + ".tar.gz"));
      call(tar.ranIn(installFolder).withArg("-xf").appendArg(downloadedFileName + ".tar"));
      File decompressed = new File(installFolder, downloadedFileName);
      
      // compile
      call(cmd(new File(decompressed, "configure")).ranIn(decompressed));
      call(make.ranIn(decompressed));
      
      return new File(decompressed, "src/terminal/jags");
    }
    
  };
}
