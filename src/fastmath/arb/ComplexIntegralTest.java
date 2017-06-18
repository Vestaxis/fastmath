package fastmath.arb;

import static java.lang.Math.pow;
import static java.lang.System.out;

import com.sun.jna.Pointer;

import junit.framework.TestCase;

public class ComplexIntegralTest extends TestCase
{
  public void testIntegrate()
  {
    // ComplexReference out, Complex inp, Pointer param, long order, long prec
    ComplexIntegral integral = new ComplexIntegral( new ComplexFunctionEvaluator( x -> x.sinh() ) );
    Complex a = new Complex( 1, 1 );
    Complex b = new Complex( 2, 2 );
    ARF outerRadius = new ARF( 1.0 );
    ARF innerRadius = new ARF( 0.002 );

    Complex integralValue = integral.integrate( a, b, innerRadius, outerRadius, 64 );
    out.println( "integralValue=" + integralValue );
    assertEquals( -2.399355861, integralValue.getReal().sixtyFourBitValue(), pow( 10, -5 ));
    assertEquals( 2.308997130, integralValue.getImaginary().sixtyFourBitValue(), pow( 10, -5 ));
  }
}
