package fastmath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * library for working with JNI (Java Native Interface)
 * 
 * @see http://frommyplayground.com/how-to-load-native-jni-library-from-jar
 */
public class NativeUtils
{

  private static Boolean cachedTerribleExcuseForAnOperatingSystemIndicator;

  /**
   * Private constructor - this class will never be instanced
   */
  private NativeUtils()
  {
  }

  /**
   * 
   * @return true if the "os.name" Java sytem property contains the string
   *         "Windows"
   */
  public static boolean isTerribleExcuseForAnOperatingSystem()
  {
    if (cachedTerribleExcuseForAnOperatingSystemIndicator != null) { return cachedTerribleExcuseForAnOperatingSystemIndicator; }
    cachedTerribleExcuseForAnOperatingSystemIndicator = System.getProperty("os.name").contains("Windows");
    return cachedTerribleExcuseForAnOperatingSystemIndicator;
  }

  /**
   * Loads library from current JAR archive
   * 
   * The file from JAR is copied into system temporary directory and then loaded.
   * The temporary file is deleted after exiting. Method uses String as filename
   * because the pathname is "abstract", not system-dependent.
   * 
   * @param filename
   *          The filename inside JAR as absolute path (beginning with '/'), e.g.
   *          /package/File.ext
   * @throws IOException
   *           If temporary file creation or read/write operation fails
   * @throws IllegalArgumentException
   *           If source file (param path) does not exist
   * @throws IllegalArgumentException
   *           If the path is not absolute or if the filename is shorter than
   *           three characters (restriction of
   *           {@see File#createTempFile(java.lang.String, java.lang.String)}).
   */
  public static void loadLibraryFromClasspath(String libraryPathExcludingFilenameExtension) throws IOException
  {
    String filenameExtension = isTerribleExcuseForAnOperatingSystem() ? "dll" : "so";
    File temp = File.createTempFile("library", filenameExtension);
    temp.deleteOnExit();

    if (!temp.exists()) { throw new FileNotFoundException("Failed to open temporary file at " + temp.getAbsolutePath()); }

    byte[] buffer = new byte[1024];
    int readBytes;
    try
    {
      System.load("/usr/lib/" + libraryPathExcludingFilenameExtension + "." + filenameExtension);
      return;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    InputStream is = ClassLoader.getSystemResourceAsStream(libraryPathExcludingFilenameExtension + "." + filenameExtension);
    if (is == null) { throw new FileNotFoundException("File " + libraryPathExcludingFilenameExtension + " was not found by the class loader'."); }

    OutputStream os = new FileOutputStream(temp);
    try
    {
      while ((readBytes = is.read(buffer)) != -1)
      {
        os.write(buffer, 0, readBytes);
      }
    }
    finally
    {
      os.close();
      is.close();
    }

    System.load(temp.getAbsolutePath());
  }

  private static boolean nativeLibraryDisabled = true;

  protected static void loadNativeFastmathLibrary()
  {
    if (nativeLibraryDisabled) { return; }
    try
    {
      loadLibraryFromClasspath("libfastmath");
    }
    catch (IOException e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}