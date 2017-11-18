package fastmath;


import static java.lang.Math.pow;

import java.util.function.IntToDoubleFunction;

import junit.framework.TestCase;

public class FunctionsTest extends TestCase
{

  private static double ε18 = pow( 10, -18 );

  private static double ε15 = pow( 10, -15 );

  private static double ε10 = pow( 10, -10 );

  private static double ε7 = pow( 10, -7 );
  
  public void testProd()
  {
    double a[] = new double[] { 1,2,4};
    double x = Functions.product( (IntToDoubleFunction)i->a[i] , 0,2);
    assertEquals(8.0, x);
  }

  public void testProdExcluding()
  {
    double a[] = new double[] { 1,2,4,5};
    double x = Functions.productExcluding( i->a[i] , 0,3,2);
    assertEquals(10.0, x);
  }

  
  public void testMaxExp()
  {

  }

  public void testOuterProduct()
  {
    Vector a = new Vector( new double[]
    {
      1,
      2,
      3,
      4,
      5
    } );
    Vector b = new Vector( new double[]
    {
      6,
      7,
      8,
      9,
      10
    } );
    DoubleMatrix op = Functions.outerProduct( a, b );
    assertTrue( op.row( 0 ).equals( b ) );
    assertTrue( op.row( 1 ).equals( new Vector( new double[]
    {
      12,
      14,
      16,
      18,
      20
    } ) ) );
    assertTrue( op.row( 2 ).equals( new Vector( new double[]
    {
      18,
      21,
      24,
      27,
      30
    } ) ) );
    assertTrue( op.row( 3 ).equals( new Vector( new double[]
    {
      24,
      28,
      32,
      36,
      40
    } ) ) );
    assertTrue( op.row( 4 ).equals( new Vector( new double[]
    {
      30,
      35,
      40,
      45,
      50
    } ) ) );
    // log.info("op " + op);
  }

  public void testUniformRandom()
  {
    Pair<Double, Double> range = new Pair<>( 2.2, 6.7 );
    double minx = Double.MAX_VALUE;
    double maxx = Double.MIN_VALUE;
    for ( int i = 0; i < 1000; i++ )
    {
      double x = Functions.uniformRandom( range );
      maxx = x > maxx ? x : maxx;
      minx = x < minx ? x : minx;
      assertTrue( range.left <= x && x <= range.right );
    }
    // System.out.println(minx + " " + maxx );
  }

}
