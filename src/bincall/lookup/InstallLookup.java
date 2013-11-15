package bincall.lookup;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Random;

import javax.xml.bind.JAXBElement.GlobalScope;

import com.google.common.io.Files;

import bincall.BinCallUtils;
import bincall.Binary;
import bincall.GlobalSettings;
import bincall.InstallableBinary;
import bincall.Installer;
import bincall.InstallerContext;



public class InstallLookup implements BinLookupStrategy
{
  private static final String separator = "____";
  private static final String successStatusFileName = "auto-install-binary-location.txt";

  @Override
  public File lookup(Binary _binary)
  {
    if (!(_binary instanceof InstallableBinary))
      return null;
    InstallableBinary binary = (InstallableBinary) _binary;
    
    for (Installer installer : binary.getInstallers())
    {
      File result = attemptInstall(binary, installer);
      if (result != null)
        return result;
    }
    
    return null;
  }
  
  private static final Random rand = new Random();
  private static File attemptInstall(InstallableBinary binary, Installer installer)
  {
    final String installFolderPrefix = binary.getName() + separator + installer.getID();
    
    // check if it is already installed
    for (File directory : GlobalSettings.getInstallDir().listFiles())
      if (directory.getName().startsWith(installFolderPrefix) && getInstalledFile(directory) != null)
        return getInstalledFile(directory);
    
    // attempt to install
    File installFolder = new File(GlobalSettings.getInstallDir(), installFolderPrefix + separator + System.currentTimeMillis() + separator + rand.nextLong());
    InstallerContext context = new InstallerContext(installFolder); // might need to add info later
    File result = installer.install(context);
    if (result == null)
      return null;
    
    // create success file
    createInstalledFilePointer(installFolder, result);
    
    return result;
  }
  
  private static void createInstalledFilePointer(File installFolder, File result)
  {
    File successFile = new File(installFolder, successStatusFileName);
    String installedBinRelativePath = relativize(installFolder, result);
    BinCallUtils.write(successFile, installedBinRelativePath);
  }
  
  /**
   * Parent assumed to be a parent of children
   * @param parent
   * @param children
   * @return
   */
  private static String relativize(File parent, File children)
  {
    String path = children.getAbsolutePath();
    String base = parent.getAbsolutePath();
    return new File(base).toURI().relativize(new File(path).toURI()).getPath();
  }

  private static File getInstalledFile(File directory)
  {
    File successFile = new File(directory, successStatusFileName);
    if (!successFile.exists()) return null;
    String contents = BinCallUtils.readFirstLine(successFile);
    return new File(successFile, contents);
  }
  
  @Override
  public String toString()
  {
    return "InstallLookup";
  }

}
