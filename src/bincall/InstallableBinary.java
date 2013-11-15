package bincall;

import java.util.List;



public interface InstallableBinary extends Binary
{
  public List<Installer> getInstallers();
}
