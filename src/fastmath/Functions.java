package fastmath;

import static fastmath.Console.println;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.pow;
import static java.util.stream.IntStream.rangeClosed;

import java.util.function.Function;
import java.util.function.IntToDoubleFunction;

import fastmath.arb.Complex;
import fastmath.arb.Real;
import fastmath.fx.Iteration;

public class Functions
{
  static
  {
    NativeUtils.loadNativeFastmathLibrary();
  }

  public static final double π = Math.PI;

  // public static double nthZero( int n )
  // {
  // double z[] = new double[2];
  // Functions.iterate( Iteration.ZNewton, 1000, approximationZero( n ), 0, z,
  // 0.5 );
  // return z[0];
  // }
  //
  public static final double δ( double z )
  {
    return ( z == 0.0 ? 1.0 : 0.0 );
  };

  /**
   * Complex gamma function
   * 
   * @param x
   * @param y
   * @param z
   */
  public static void Γ( double x, double y, double[] z )
  {
    Complex p = new Complex( x, y );
    Complex g = p.Gamma();
    z[0] = g.getReal().sixtyFourBitValue();
    z[1] = g.getImaginary().sixtyFourBitValue();
  }

  /**
   * logarithm of the Complex gamma function
   * 
   * @param x
   * @param y
   * @param z
   */
  public static void lnΓ( double x, double y, double[] z )
  {
    Complex p = new Complex( x, y );
    Complex g = p.lnGamma();
    z[0] = g.getReal().sixtyFourBitValue();
    z[1] = g.getImaginary().sixtyFourBitValue();
  }

  /**
   * complex digamma function
   * 
   * @param x
   * @param y
   * @return
   */
  public static void Ψ( double x, double y, double[] z )
  {
    Complex p = new Complex( x, y );
    Complex g = p.DiGamma();
    z[0] = g.getReal().sixtyFourBitValue();
    z[1] = g.getImaginary().sixtyFourBitValue();
  }

  public static void ζ( double x, double y, double[] z )
  {
    Complex p = new Complex( x, y );
    Complex g = p.Zeta();
    z[0] = g.getReal().sixtyFourBitValue();
    z[1] = g.getImaginary().sixtyFourBitValue();
  }

  /**
   * the 1st derivative of this{@link #ζ(double, double, double[])}
   * 
   * @param x
   *          real part
   * @param y
   *          imaginary part
   * @param z
   *          result: [realPart,imagPart]
   */
  // public static void ζd( double x, double y, double[] z )
  // {
  // ComplexPolynomial poly = new ComplexPolynomial( 2 );
  // ComplexPolynomial sx = new ComplexPolynomial( 2 );
  // sx.setCoeff( 0, new Complex( x, y ) );
  // Complex one = new Complex( 1, 0 );
  // sx.setCoeff( 1, one );
  // Complex a = sx.getCoeff( 1 );
  // out.println( "a=" + a );
  // sx.zetaSeries( poly );
  //
  // Complex zd = poly.getCoeff( 1 );
  // out.println( "zd=" + zd );
  // z[0] = zd.getReal().sixtyFourBitValue();
  // z[1] = zd.getImaginary().sixtyFourBitValue();
  // }

  /**
   * The Hardy Z-function
   * 
   * @param x
   * @param y
   * @param z
   */
  public static void Z( double x, double y, double[] z )
  {
    // if ( y > 0 )
    // {
    // Z( x, -y, z );
    // z[1] = -z[1];
    // return;
    // }
    Complex p = new Complex( x, y );
    Complex g = p.HardyZ();
    z[0] = g.getReal().sixtyFourBitValue();
    z[1] = g.getImaginary().sixtyFourBitValue();
  }

  /**
   * The derivative of the Hardy Z-function
   * 
   * @param x
   * @param y
   * @param z
   */
  public static void Zd( double x, double y, double[] z )
  {
    // if ( y > 0 )
    // {
    // Zd( x, -y, z );
    // z[1] = -z[1];
    // return;
    // }
    Complex p = new Complex( x, y );
    Complex g = p.HardyZDerivative();
    z[0] = g.getReal().sixtyFourBitValue();
    z[1] = g.getImaginary().sixtyFourBitValue();
  }

  /**
   * The Newton derivative of the Hardy Z-function
   * 
   * @param x
   * @param y
   * @param z
   * @param h
   */
  public static void ZNewton( double x, double y, double[] z )
  {
    Complex p = new Complex( x, y );
    Complex g = p.HardyZNewton();
    z[0] = g.getReal().sixtyFourBitValue();
    z[1] = g.getImaginary().sixtyFourBitValue();
  }

  /**
   * A relaxed Newton-like iteration of the Hardy Z-function.
   * t-h*tanh(Z(t)/Z'(t))
   * 
   * @param x
   *          real part of complex coordinate
   * @param y
   *          imaginary part of complex coordinate
   * @param z
   *          2d-array that holds the real and imaginary parts of the value of
   *          the function evaluated at the point (x,y)
   * @param h
   *          relaxation constant
   */
  public static void ZHTNewton( double x, double y, double[] z, double h )
  {
    Complex p = new Complex( x, y );
    Complex g = p.HardyZRelaxedTanhNewton( new Real( h ) );
    z[0] = g.getReal().sixtyFourBitValue();
    z[1] = g.getImaginary().sixtyFourBitValue();

  }

  public static Real approximationZero( int n )
  {
    final Complex ndx = new Complex( ( ( 8.0 * n - 11.0 ) / ( 8.0 * exp( 1 ) ) ), 0 );
    final Complex w = ndx.W();
    final Complex top = new Complex( PI * ( 8 * n - 11 ), 0 );
    return top.divide( ( w.multiply( 4 ) ) ).getReal();
  }

  public static double W( double d )
  {
    return new Complex( d, 0 ).W().getReal().sixtyFourBitValue();
  }

  /**
   * The Riemann-Siegel ϑ function
   * 
   * @param x
   *          real part
   * @param y
   *          imaginary part
   * @param z
   *          upon return is set to [Re(ϑ(x+iy)),Im(ϑ(x+iy))]
   */
  public static void ϑ( double x, double y, double[] z )
  {
    Complex p = new Complex( x, y );
    Complex g = p.vartheta();
    z[0] = g.getReal().sixtyFourBitValue();
    z[1] = g.getImaginary().sixtyFourBitValue();
  }

  /**
   * Derivative of The Riemann-Siegel ϑ function
   * 
   * @param x
   *          real part
   * @param y
   *          imaginary part
   * @param z
   *          upon return is set to [Re(ϑ(x+iy)),Im(ϑ(x+iy))]
   */
  public static native void ϑd( double x, double y, double[] z );

  public static void main( String args[] )
  {
  }

  private static final String hex = "0123456789ABCDEF";

  private static final double ONE_HUNDRED = 100;

  /**
   * Trims milliseconds
   * 
   * @param t
   * @return
   */
  public static long trimMilli( long t )
  {
    return t - ( t % 1000 );
  }

  /**
   * Rounds x to the specified number of decimal places
   * 
   * @param x
   * @param decimals
   * @return
   */
  public static double round( double x, int decimals )
  {
    double y = Math.pow( 10, decimals );
    return Math.round( x * y ) / y;
  }

  /**
   * Returns true if this is a real number (non NaN or infinite)
   * 
   * @param x
   * @return
   */
  public static boolean isReal( double x )
  {
    return !Double.isInfinite( x ) && !Double.isNaN( x );
  }

  /**
   * Convers unix time to hours since midnight
   * 
   * @param x
   * @param timezoneOffset
   *          -6 for CST
   * @return
   */
  public static double unixTimeToHours( double x, double timezoneOffset )
  {
    return ( ( x % ( 1000 * 60 * 60 * 24 ) ) / 1000 / 60 / 60 ) + timezoneOffset;
  }

  /**
   * Convers unix time to hours since midnight
   * 
   * @param x
   * @param timezoneOffset
   *          -6 for CST
   * @return
   */
  public static double unixTimeToMinutes( double x, double timezoneOffset )
  {
    return ( ( x % ( 1000 * 60 * 60 * 24 ) ) / 1000 / 60 ) + ( timezoneOffset * 60 );
  }

  /**
   * Returns -1, 0 or 1 for the sign of x
   * 
   * @param x
   * @return
   */
  public static int sign( double x )
  {
    return ( x == 0 ) ? 0 : ( x > 0 ) ? 1 : -1;
  }

  /**
   * Rounds the number towards 0
   * 
   * @param x
   * @return
   */
  public static double roundToZero( double x )
  {
    return Math.signum( x ) >= 0 ? Math.floor( x ) : Math.ceil( x );
  }

  /**
   * x-floor(x)
   * 
   * @param x
   * @return
   */
  public static double frac( double x )
  {
    return x - Math.floor( x );
  }

  public static double Γ( double x )
  {
    double z[] = new double[2];
    Γ( x, 0, z );
    return z[0];
    // return exp( logGamma( x ) );
  }

  /**
   * @param range
   * @return random number uniformly distributed within range
   */
  public static double uniformRandom( Pair<Double, Double> range )
  {
    return Math.random() * ( range.right - range.left ) + range.left;
  }

  /**
   * The outper product of two vectors
   * 
   * @param a
   * @param b
   * @return
   */
  public static DoubleMatrix outerProduct( Vector a, Vector b )
  {
    DoubleMatrix amatrix = a.asMatrix().trans();
    DoubleMatrix bmatrix = b.asMatrix();
    // log.info(amatrix + " times " + bmatrix );
    return amatrix.prod( bmatrix );
  }

  public static Vector range( double xmin, double xmax, double step )
  {
    return range( xmin, xmax, step, (int) ( ( xmax - xmin ) / step ) + 1 );
  }

  public static Vector range( double xmin, double xmax, double step, int n )
  {
    assert step > 0;
    Vector data = new Vector( n );
    double t = xmin;
    for ( int row = 0; row < n; row++ )
    {
      data.set( row, t );
      t += step;
    }
    return data;

  }

  /**
   * Identity matrix
   * 
   * @param n
   * @return n*n identity matrix
   */
  public static DoubleColMatrix eye( int n )
  {
    DoubleColMatrix eye = new DoubleColMatrix( n, n );
    eye.diag().assign( 1.0 );
    return eye;
  }

  /**
   * Identity matrix
   * 
   * @param n
   * @return n*n identity matrix
   */
  public static DoubleColMatrix eye( int n, DoubleColMatrix eye )
  {
    eye.assign( 0.0 );
    eye.diag().assign( 1.0 );
    return eye;
  }

  /**
   * TODO: implement bits accumulation convergence criteria
   * 
   * @param map
   * @param n
   * @param initRe
   * @param initIm
   * @param z
   * @param h
   * @return
   */
  public static int iterate( Iteration map, int n, double initRe, double initIm, double z[], double h )
  {
    double lasterr = Double.MAX_VALUE;
    double[] pz = new double[]
    {
      Double.NaN,
      Double.NaN
    };

    z[0] = initRe;
    z[1] = initIm;
    for ( int i = 0; i < n; i++ )
    {
      pz[0] = z[0];
      pz[1] = z[1];
      map.value( z[0], z[1], z, h );
      final double err = abs( pz[0] - z[0] );
      lasterr = err;
      if ( err > lasterr )
      {
        throw new IllegalArgumentException( "diverged from " + lasterr + " to " + err );
      }

      if ( err < pow( 10, -17 ) )
      {
        return i;
      }
    }
    return n;
  }

  /**
   * TODO: implement bits accumulation convergence criteria
   * 
   * @param map
   * @param n
   * @param initRe
   * @param initIm
   * @param z
   * @param h
   * @return
   */
  public static Complex iterate( Function<Complex, Complex> map, Complex init, int n )
  {
    double lasterr = Double.MAX_VALUE;
    Complex pz;
    Complex z = init;
    for ( int i = 0; i < n; i++ )
    {
      pz = z;
      z = map.apply( z );
      // println( "wtf z[" + i + "]=" + z );
      if ( z.isInfinite() )
      {
        return pz;
      }
      final double err = pz.subtract( z ).abs().sixtyFourBitValue();
      lasterr = err;
      if ( err > lasterr )
      {
        throw new IllegalArgumentException( "diverged from " + lasterr + " to " + err );
      }

      // if ( err < pow( 10, -17 ) )
      // {
      // return z;
      // }
    }
    return z;
  }

  @Deprecated
  public static final double EPSILON = 1e-9;

  /**
   * Calculate iterates the mapping t-tanh(Z(t)/Z'(t)) where t=x+iy
   * 
   * @param x
   *          real part of initial point
   * @param y
   *          imaginary part of initial point
   * @param z
   *          where the accumultion point is stored, a 2d array, [real,imag]
   * @param h
   *          relaxation constant
   * @return iteration count
   */
  public static int convergeToZHTNewtonLimit( double initx, double inity, double[] z, double h )
  {
    return iterate( Iteration.ZHTNewton, 20, initx, inity, z, h );
  }

  public static DoubleRowMatrix iterateAlongPath( Iteration map, double initx, double inity, double h )
  {
    DoubleRowMatrix sequence = new DoubleRowMatrix( 0, 2 );

    double prevδnorm = Double.NEGATIVE_INFINITY;
    for ( int i = 0; i < 100; i++ )
    {
      Vector lastz = sequence.lastRow();
      if ( lastz == null )
      {
        lastz = new Vector( new double[]
        {
          initx,
          inity
        } );
        sequence.appendRow( lastz );
      }
      Vector z = sequence.appendRow();
      double to[] = new double[2];
      map.value( lastz.get( 0 ), lastz.get( 1 ), to, h );
      z.set( to );

      double δnorm = ( abs( z.get( 0 ) - lastz.get( 0 ) ) );
      double δ2norm = abs( δnorm - prevδnorm );

      if ( δnorm < pow( 10, -13 ) || Double.isNaN( δ2norm ) )
      {
        return sequence;
      }
      prevδnorm = δnorm;
    }
    return sequence;

  }

  /**
   * 
   * 
   * @param n
   * @return
   */
  public static double almostGramPoint( int n )
  {
    return ( -7 + 8 * n ) * PI / ( 4 * W( ( -7 + 8 * n ) / ( 8 * exp( 1 ) ) ) );
  }

  /**
   * 
   * @param initx
   * @param inity
   * @param z
   * @param h
   *          t-lambda*(Z(t)*(Z(t)/(Z(t+Z(t))-Z(t))))
   * 
   */
  public static void ZSteffensen( double x, double y, double[] z, double h )
  {
    Complex t = new Complex( x, y );
    Complex g = t.ZSteffensen( h );
    z[0] = g.getReal().sixtyFourBitValue();
    z[1] = g.getImaginary().sixtyFourBitValue();
  }

  public static void ZSteffensenTanh( double x, double y, double[] z, double h )
  {
    Complex t = new Complex( x, y );
    Complex g = t.ZSteffensenTanh( h );
    z[0] = g.getReal().sixtyFourBitValue();
    z[1] = g.getImaginary().sixtyFourBitValue();
  }

  public static double sum( IntToDoubleFunction summand, int lowerIndex, int upperIndex )
  {
    return rangeClosed( lowerIndex, upperIndex ).mapToDouble( summand ).sum();
  }

}
