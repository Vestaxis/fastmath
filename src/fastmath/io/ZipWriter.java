package fastmath.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipWriter
{
  public final int BUF_SIZE = 16384;

  ZipOutputStream zipOut;

  public ZipWriter(File file) throws FileNotFoundException
  {
    zipOut = new ZipOutputStream( new FileOutputStream( file ) );
  }

  public void close() throws IOException
  {
    zipOut.close();
  }

  public void add( String name, InputStream inputStream ) throws IOException
  {
    zipOut.putNextEntry( new ZipEntry( name ) );
    BufferTools.fastChannelCopy( inputStream, zipOut );
  }

  /**
   * Adds every file in the specified directory, NOT recursive. Paths are NOT
   * stored in the zip entry
   * 
   * @param dirName
   * @throws IOException
   * @throws FileNotFoundException
   */
  public void addDir( String dirName ) throws FileNotFoundException, IOException
  {
    File dir = new File( dirName );

    File files[] = dir.listFiles( new FilenameFilter()
    {
      @Override
      public boolean accept( File dir, String name )
      {
        File file = new File( dir, name );
        return file.isFile() && file.canRead();
      }
    } );

    for ( File file : files )
    {
      FileInputStream fis = new FileInputStream( file );
      try
      {
        add( file.getName(), fis );
      }
      finally
      {
        fis.close();
      }
    }
  }
}
