package fastmath.fx;

import static fastmath.Console.println;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

import fastmath.Functions;
import fastmath.Pair;

/**
 * 
 * calculate numbers related to semilocal convergence / Lipschitz conditions.
 */
public class GramPoints
{

  public static Pair<Pair<Double, Double>, Pair<Double, Double>> range( int n )
  {
    double z[] = new double[2];
    double minz = Double.POSITIVE_INFINITY;
    double maxz = Double.NEGATIVE_INFINITY;
    double mint = Double.POSITIVE_INFINITY;
    double maxt = Double.NEGATIVE_INFINITY;

    double t0 = HardyZMap.z0( n );
    double t1 = HardyZMap.z0( n + 2 );
    for ( double t = t0; t < t1; t += 0.01 )
    {
      Functions.Zd( t, 0, z );
      if ( abs( z[0] ) > maxz )
      {
        maxz = abs( z[0] );
        maxt = t;
      }
      if ( abs( z[0] ) < minz )
      {
        minz = abs( z[0] );
        mint = t;
      }
    }

    return new Pair<Pair<Double, Double>, Pair<Double, Double>>( new Pair<Double, Double>( mint, minz ), new Pair<Double, Double>( maxt, maxz ) );
  }

}
