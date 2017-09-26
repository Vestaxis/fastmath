package fastmath.arb;

import junit.framework.TestCase;

public class ComplexPolynomialTest extends TestCase
{

  public static void testPoly()
  {
    Complex p0 = new Complex( 1, 2 );
    Complex p1 = new Complex( 3, 4 );
    ComplexPolynomial poly = new ComplexPolynomial( 2 );

    poly.setCoeff( 0, p0 );    
    
    poly.setCoeff( 1, p1 );    
    
    Complex c0 = poly.getCoeff( 0 );
    assertEquals( p0, c0 );
    Complex c1 = poly.getCoeff( 1 );
    assertEquals( p1, c1 );

  }

}
