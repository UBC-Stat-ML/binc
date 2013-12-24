Summary
-------

binc makes it easier to call binary program from java applications.

Installation
------------

- Compile using ``gradle installApp``
- Add the jars in  ``build/install/binc/lib/`` to your classpath, OR, add
the following to your project gradle script 

```groovy
dependencies {
  compile group: 'com.3rdf', name: 'binc', version: '1.0'
}
repositories {
  mavenCentral()
  jcenter()
  maven {
    url "http://www.stat.ubc.ca/~bouchard/maven/"
  }
  maven {
    url "http://spoon.gforge.inria.fr/repositories/releases/"
  }
}
```




Usage
-------

Use ``call(cmd([name])`` to call a command synchronously:


```java

@org.junit.Test
public void testLs() {
    java.lang.String result = binc.Command.call(binc.Command.cmd("ls"));
    java.lang.System.out.println(result);
}
```

Add arguments and other options with method chaining:


```java

@org.junit.Test
public void testArgs() {
    java.lang.String result = binc.Command.call(binc.Command.cmd("ls").withArgs("-a"));
    java.lang.System.out.println(result);
}
```

Method chaining is implemented using immutable creation,
so commands can be safely saved for later:


```java

@org.junit.Test
public void testSavedCmd() {
    binc.Command simpleLs = binc.Command.cmd("ls");
    binc.Command complexLs = simpleLs.withArgs("-a");
    java.lang.String result = binc.Command.call(binc.Command.cmd("ls"));
    org.junit.Assert.assertTrue(((result.charAt(0)) != '.'));
    org.junit.Assert.assertTrue(((binc.Command.call(complexLs).charAt(0)) == '.'));
}
```

Immutability of method chaining can be used to create
libraries of commands. This makes call shorter, and makes
it easier to track down dependencies to external programs.


```java

public class Commands {
    @org.junit.Test
    public void testLibraryCmd() {
        binc.Command.call(binc.Commands.tar.withArgs("-help"));
    }
    
    public static binc.Command make = binc.Command.cmd("make");
    
    public static binc.Command gunzip = binc.Command.cmd("gunzip");
    
    public static binc.Command tar = binc.Command.cmd("tar");
    
    public static binc.Command bash = binc.Command.cmd(new java.io.File("/bin/bash"));
    
    public static binc.Command rm = binc.Command.cmd(new java.io.File("/bin/rm"));
    
}
```

