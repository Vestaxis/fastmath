package fastmath;

import junit.framework.TestCase;

public class ExpandableVectorTest extends TestCase
{

  public void testBasicFunctionality()
  {
    ExpandableVector vec = new ExpandableVector();
    vec.set( 5, 12.0 );
    assertTrue( vec.get( 5 ) == 12.0 );
  }
}
