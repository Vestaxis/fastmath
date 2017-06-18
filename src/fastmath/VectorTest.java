package fastmath;

import static java.lang.System.out;

import fastmath.Vector.Condition;
import junit.framework.TestCase;

public class VectorTest extends TestCase
{

  public void testAssign()
  {
    Vector x = new Vector( new double[]
    {
      0.1,
      0.2,
      0.3,
      0.4
    } );
    Vector y = new Vector( 4 );
    Vector z = new Vector( new double[]
    {
      0.1,
      0.2,
      0.3,
      0.4
    } );

    y.assign( x );
    assertEquals( z, y );
  }

  public void testFindAll()
  {
    Vector x = new Vector( new double[]
    {
      0.1,
      0.2,
      0.3,
      0.4,
      0.15,
      0.17,
      0.6,
      -0.3,
      0.0,
      -0.1,
      -0.2,
      3.4,
      0.2
    } );
    IntVector idx = x.findAll( 0, Condition.LT );
    out.println( idx );
    assertEquals( idx.elementAt( 0 ), 7 );
    assertEquals( idx.elementAt( 1 ), 9 );
    assertEquals( idx.elementAt( 2 ), 10 );
  }

  public void testSwap()
  {
    Vector a = new Vector( new double[]
    {
      1,
      2,
      3
    } );
    Vector b = new Vector( new double[]
    {
      4,
      5,
      6
    } );
    assertEquals( 6.0, a.sum() );
    assertEquals( 15.0, b.sum() );
    a.swap( b );
    assertEquals( 15.0, a.sum() );
    assertEquals( 6.0, b.sum() );
  }

  public void testSlice()
  {
    Vector a = new Vector( new double[]
    {
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      11,
      12,
      13,
      14,
      15,
      16,
      17,
      18,
      19,
      20
    } );
    Vector firstSlice = a.slice( 0, 10 );
    Vector secondSlice = a.slice( 10, a.size() );
    double firstSum = firstSlice.sum();
    double secondSum = secondSlice.sum();

  }

  public void testSliceReverse()
  {
    Vector a = new Vector( new double[]
    {
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      11,
      12,
      13,
      14,
      15,
      16,
      17,
      18,
      19,
      20
    } );
    Vector firstSlice = a.slice( 0, 10 ).reverse();
    Vector secondSlice = a.slice( 10, a.size() ).reverse();
    double firstSum = firstSlice.sum();
    double secondSum = secondSlice.sum();

  }

  public void testMultiply()
  {
    Vector a = new Vector( new double[]
    {
      5,
      10,
      15,
      20,
      30
    } );
    Vector b = new Vector( new double[]
    {
      1,
      5,
      3,
      10,
      6
    } );
    Vector c = new Vector( new double[]
    {
      5,
      50,
      45,
      200,
      180
    } );
    assertEquals( c, a.multiply( b ) );
  }

  public void testDivide()
  {
    Vector a = new Vector( new double[]
    {
      5,
      10,
      15,
      20,
      30
    } );
    Vector b = new Vector( new double[]
    {
      1,
      5,
      3,
      10,
      6
    } );
    Vector c = new Vector( new double[]
    {
      5,
      2,
      5,
      2,
      5
    } );
    assertEquals( c, a.divide( b ) );
  }
}
