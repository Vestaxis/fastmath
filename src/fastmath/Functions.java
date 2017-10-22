package fastmath;

import static java.util.stream.IntStream.rangeClosed;

import java.util.function.IntToDoubleFunction;

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
  public static final double δ(double z)
  {
    return (z == 0.0 ? 1.0 : 0.0);
  };

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
  public static native void ϑd(double x, double y, double[] z);

  public static void main(String args[])
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
  public static long trimMilli(long t)
  {
    return t - (t % 1000);
  }

  /**
   * Rounds x to the specified number of decimal places
   * 
   * @param x
   * @param decimals
   * @return
   */
  public static double round(double x, int decimals)
  {
    double y = Math.pow(10, decimals);
    return Math.round(x * y) / y;
  }

  /**
   * Returns true if this is a real number (non NaN or infinite)
   * 
   * @param x
   * @return
   */
  public static boolean isReal(double x)
  {
    return !Double.isInfinite(x) && !Double.isNaN(x);
  }

  /**
   * Convers unix time to hours since midnight
   * 
   * @param x
   * @param timezoneOffset
   *          -6 for CST
   * @return
   */
  public static double unixTimeToHours(double x, double timezoneOffset)
  {
    return ((x % (1000 * 60 * 60 * 24)) / 1000 / 60 / 60) + timezoneOffset;
  }

  /**
   * Convers unix time to hours since midnight
   * 
   * @param x
   * @param timezoneOffset
   *          -6 for CST
   * @return
   */
  public static double unixTimeToMinutes(double x, double timezoneOffset)
  {
    return ((x % (1000 * 60 * 60 * 24)) / 1000 / 60) + (timezoneOffset * 60);
  }

  /**
   * Returns -1, 0 or 1 for the sign of x
   * 
   * @param x
   * @return
   */
  public static int sign(double x)
  {
    return (x == 0) ? 0 : (x > 0) ? 1 : -1;
  }

  /**
   * Rounds the number towards 0
   * 
   * @param x
   * @return
   */
  public static double roundToZero(double x)
  {
    return Math.signum(x) >= 0 ? Math.floor(x) : Math.ceil(x);
  }

  /**
   * x-floor(x)
   * 
   * @param x
   * @return
   */
  public static double frac(double x)
  {
    return x - Math.floor(x);
  }

  /**
   * @param range
   * @return random number uniformly distributed within range
   */
  public static double uniformRandom(Pair<Double, Double> range)
  {
    return Math.random() * (range.right - range.left) + range.left;
  }

  /**
   * The outper product of two vectors
   * 
   * @param a
   * @param b
   * @return
   */
  public static DoubleMatrix outerProduct(Vector a, Vector b)
  {
    DoubleMatrix amatrix = a.asMatrix().trans();
    DoubleMatrix bmatrix = b.asMatrix();
    // log.info(amatrix + " times " + bmatrix );
    return amatrix.prod(bmatrix);
  }

  public static Vector range(double xmin, double xmax, double step)
  {
    return range(xmin, xmax, step, (int) ((xmax - xmin) / step) + 1);
  }

  public static Vector range(double xmin, double xmax, double step, int n)
  {
    assert step > 0;
    Vector data = new Vector(n);
    double t = xmin;
    for (int row = 0; row < n; row++)
    {
      data.set(row, t);
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
  public static DoubleColMatrix eye(int n)
  {
    DoubleColMatrix eye = new DoubleColMatrix(n, n);
    eye.diag().assign(1.0);
    return eye;
  }

  /**
   * Identity matrix
   * 
   * @param n
   * @return n*n identity matrix
   */
  public static DoubleColMatrix eye(int n, DoubleColMatrix eye)
  {
    eye.assign(0.0);
    eye.diag().assign(1.0);
    return eye;
  }

  @Deprecated
  public static final double EPSILON = 1e-9;

  public static double sum(IntToDoubleFunction elements, int lowerIndex, int upperIndex)
  {
    return rangeClosed(lowerIndex, upperIndex).mapToDouble(elements).sum();
  }

  public static double sumExcluding(IntToDoubleFunction elements, int lowerIndex, int upperIndex, int excluding)
  {
    return rangeClosed(lowerIndex, upperIndex).filter(i -> i != excluding).mapToDouble(elements).sum();
  }

  public static double prod(IntToDoubleFunction elements, int lowerIndex, int upperIndex)
  {
    return rangeClosed(lowerIndex, upperIndex).mapToDouble(elements).reduce(1, (a, b) -> a * b);
  }

  public static double prodExcluding(IntToDoubleFunction elements, int lowerIndex, int upperIndex, int excluding)
  {
    return rangeClosed(lowerIndex, upperIndex).filter(i -> i != excluding).mapToDouble(elements).reduce(1, (a, b) -> a * b);
  }

}
