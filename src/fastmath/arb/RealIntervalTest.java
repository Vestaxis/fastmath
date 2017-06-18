package fastmath.arb;

import junit.framework.TestCase;

public class RealIntervalTest extends TestCase
{
  public void testIntersection()
  {
    RealInterval a = new RealInterval( new Real( 30 ), new Real( 40 ) );
    RealInterval b = new RealInterval( new Real( 32 ), new Real( 38 ) );
    RealInterval c = a.intersection( b );
    assertEquals( b, c );
    RealInterval e = new RealInterval( new Real( 28 ), new Real( 42 ) );
    RealInterval f = a.intersection( e );
    assertEquals( f, a );
    RealInterval g = new RealInterval( 28, 32 );
    RealInterval h = a.intersection( g );
    assertEquals( new RealInterval( 30, 32 ), h );
    RealInterval i = new RealInterval( 38, 42 );
    RealInterval j = a.intersection( i );
    assertEquals( new RealInterval( 38, 40 ), j );
    RealInterval k = new RealInterval( 40, 42 );
    RealInterval l = a.intersection( k );
    assertEquals( new RealInterval( 40, 40 ), l );
    RealInterval m = new RealInterval( 42, 45 );
    RealInterval n = a.intersection( m );
    assertTrue( n.isEmpty() );
  }
}
