package fastmath;

import static java.lang.System.out;

import junit.framework.TestCase;

public class IntVectorTest extends TestCase
{
  public void testReverse()
  {
    IntVector x = new IntVector( 2, 4, 6, 8, 10 );
    IntVector y = x.reverse();
    out.println( x );
    out.println( y );
  }

}
