package fastmath.fx;

import static java.lang.Math.abs;
import static java.lang.System.out;

import java.io.FileNotFoundException;
import java.io.IOException;

import fastmath.Fastmath;
import fastmath.Vector;
import static fastmath.fx.ZIterator.getZero;

public class Rocks
{
  public static void main( String args[] ) throws FileNotFoundException, IOException
  {
    Vector ozeros = ZIterator.readVectorFromCSV( "zeros6" );
    // ozeros = ozeros.slice( 0, 10000 );
    final Vector zeros = ozeros;
    out.println( "loaded " + zeros.size() + " zeros from file" );
    for ( int i = 3; i < zeros.size() - 1; i++ )
    {
      double x0 = ( Fastmath.instance.nthApproximationZeroD( i ) + getZero( zeros, i - 1 ) ) / 2;
      double da = abs( x0 - getZero(zeros,i-2) );
      double db = abs( x0 - getZero(zeros,i) );
      
//      if ( da < db )
//      {
//        out.println( "potential troublemaker at " + i );
//      }
      if ( x0 < getZero( zeros, i - 1 ) )
      {
        out.println( "fuckin " + i );
      }
//       if ( x0 > getZero( zeros, i  ) )
//       {
//       out.println( "dammit " + i );
//       }

    }
    System.exit( 1 );
    /**
     * non-convergent points up to n=200000
     */
  }
}
