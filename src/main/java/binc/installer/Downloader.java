package binc.installer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collections;
import java.util.List;

import binc.BinCallUtils;

import com.google.common.collect.Lists;

import static binc.Command.cmd;
import static binc.Commands.*;

public class Downloader
{
  private final File destination;
  private final List<URL> urls;
  
  public static Downloader downloader(File destination, URL url)
  {
    List<URL> urls = Collections.singletonList(url);
    return new Downloader(destination, urls);
  }
  
  public static Downloader downloader(File destination, String url)
  {
    try
    {
      return downloader(destination, new URL(url));
    } 
    catch (Exception e)
    {
      throw new RuntimeException();
    }
  }
  
  public Downloader addMirror(URL mirror)
  {
    List<URL> urls = Lists.newArrayList(this.urls);
    urls.add(mirror);
    return new Downloader(destination, urls);
  }
  
  public Downloader addMirror(String mirror)
  {
    try
    {
      return addMirror(new URL(mirror));
    } 
    catch (MalformedURLException e)
    {
      throw new RuntimeException(e);
    }
  }
  
  private Downloader(File destination, List<URL> urls)
  {
    this.destination = destination;
    this.urls = urls;
  }

  public void download()
  {
    StringBuilder messages = new StringBuilder();
    for (URL url : urls)
    {
      try 
      {
        download(url, destination);
        return;
      }
      catch (Exception e)
      {
        messages.append(e.getMessage() + "\n");
      }
    }
    throw new RuntimeException("All download URLs failed (" + urls + "). Details:\n" + messages);
  }
  
  public static void download(URL website, File target)
  {
    try
    {
      ReadableByteChannel rbc = Channels.newChannel(website.openStream());
      FileOutputStream fos = new FileOutputStream(target);
      fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    } 
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }
  

  /**
   * @param args
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException
  {
    Installer jagsInstaller = new Installer() {
      @Override public File install(InstallerContext context)
      {
//        File destination = new File(context.getInstallFolder(), "JAGS-3.4.0.tar.gz");
//        downloader(destination, "http://3thfoundation.com/resources/JAGS-3.4.0.tar.gz")
//          .addMirror("http://sourceforge.net/projects/mcmc-jags/files/JAGS/3.x/Source/JAGS-3.4.0.tar.gz/download")
//          .download();
//        gunzip.runIn(context.getInstallFolder()).withArgs("JAGS-3.4.0.tar.gz").call();
//        tar.runIn(context.getInstallFolder()).withArgs("-xf JAGS-3.4.0.tar").call();
        System.out.println(
            cmd(new File(context.getInstallFolder(), "JAGS-3.4.0/configure"))
            .ranIn(new File(context.getInstallFolder(), "JAGS-3.4.0")).call());
        return null;
      }
      @Override public long getID() { return 0; }
    };
    
    File testCtx = BinCallUtils.resolveUserHome("~/temp/installfolder");
    
    InstallerContext context = new InstallerContext(testCtx);
    jagsInstaller.install(context);
    
//    File downloaded = BinCallUtils.resolveUserHome("~/temp/test-new.tar.gz");
//    
//    Downloader d = 
//      downloader(downloaded, "http://sourceforge.net/projects/mcmc-jags/files/JAGS/3.x/Source/bad")
//        .addMirror("http://sourceforge.net/projects/mcmc-jags/files/JAGS/3.x/Source/JAGS-3.4.0.tar.gz/download");
//    
//    d.download();
    
    
//    download(
//        "http://sourceforge.net/projects/mcmc-jags/files/JAGS/3.x/Source/JAGS-3.4.0.tar.gz/download", 
//        downloaded);
    
//    File decompressed = BinCallUtils.resolveUserHome("~/temp/JAGS-3.4.0-decompressed");
//    decompress(downloaded, decompressed);
    
  }
  
//  public static void decompress(File zipFile, File destinationFolder)
//  {
//    destinationFolder.mkdirs();
//    if (!destinationFolder.exists())
//      throw new RuntimeException("Could not create zip destination folder: " + destinationFolder.getAbsolutePath());
//    
//    try 
//    {
//      BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(zipFile));
//      ArchiveInputStream input = new ArchiveStreamFactory().createArchiveInputStream(inputStream);
//      ArchiveEntry entry = null;
//      while ((entry = input.getNextEntry()) != null)
//      {
//        File entryDestination = new File(destinationFolder  ,  entry.getName());
//        entryDestination.getParentFile().mkdirs();
//        OutputStream out = new FileOutputStream(new File(destinationFolder, entry.getName()));
//        IOUtils.copy(input, out); 
//        out.close(); 
//      }
//      input.close();
//    }
//    catch (Exception e)
//    {
//      throw new RuntimeException(e);
//    }
//  }

}
