package fastmath.fx;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import fastmath.Vector;
import fastmath.arb.Real;

public class Ziterator2 extends ZIterator
{


  public static void readZerosFromFile( String filename ) throws FileNotFoundException, IOException
  {
    FileInputStream fis = new FileInputStream( new File( filename ) );
    BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
    String line;
    int k = 0;
    while (( line = reader.readLine() ) != null)
    {
      String[] tokens = line.split( "=" );
      Integer idxz = Integer.valueOf( tokens[0] );
      if ( idxz > 6 )
      {
        break;
      }
      foundRoots.ensureCapacity( idxz );
      foundRoots.add( idxz - 1, new Real( tokens[1] ) );

    }
    reader.close();
  }

  public static void main( String[] args ) throws FileNotFoundException, IOException
  {
    Vector roots = readVectorFromCSV( "zeros6" );
    roots = roots.slice( 0, 1000000 );
    //roots = roots.slice( 0, 580500 );
    //roots = roots.slice( 0, 500 );
    
    foundRoots.ensureCapacity( roots.size() );
    int cnt = 0;
    for ( Double zero : roots )
    {
      foundRoots.add( new Real( zero ) );     
    }
    
    openOutputFile();
    
    out.println( "Starting " );
    
   // verifyRange( roots, 100, roots.size()  );
    verifyRange( roots, 580485, roots.size(), foundRoots  );
    
    outputStream.close();
    
  }

}
