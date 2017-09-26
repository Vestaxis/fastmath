package fastmath.arb;

import static java.lang.Math.pow;

import junit.framework.TestCase;

public class RealTest extends TestCase
{

  public void testToString()
  {
    Real x = new Real( 3.7 );
    assertTrue( x.toString().startsWith( "3.70000000000" ) );
  }

  public void testNegate()
  {
    Real x = new Real( 2.5 );
    assertTrue( x.sign().sixtyFourBitValue() > 0 );
    x.negate();
    assertEquals( -2.5, x.sixtyFourBitValue() );
    assertTrue( x.sign().sixtyFourBitValue() < 0 );
  }
  
  public void testLambertW()
  {
    Real x = new Real( 16 ).W();
    assertEquals( 2.053192717, x.sixtyFourBitValue(), pow( 10,-8 ));
    
  }

}
