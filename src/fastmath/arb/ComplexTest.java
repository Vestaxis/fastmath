package fastmath.arb;

import static fastmath.Console.println;
import static java.lang.Math.pow;
import static java.lang.System.out;

import fastmath.Fastmath;
import fastmath.Functions;
import junit.framework.TestCase;

public class ComplexTest extends TestCase
{

  public static final Real ZERO = new Real( 0 );

  public static final Complex z1 =  new Complex( new Real( "14.134725141734693790457251983562470270784257115699243175685567460149" ), new Real( 0 ) ) ;
  
  public void testW()
  {
    Complex w = new Complex( 3.4, 0 );
    assertEquals( 1.1149583666945559428278814887134033, w.W().getReal().sixtyFourBitValue() );
  }

  public void testHardyZTanh2ndDerivative()
  {
    // Complex y1 = new Complex( new Real(
    // "4589.3006433247738586039288518167696115070023361981" ), new Real( 0 ) );
    //out.println( "y1=" + y1 );
    Complex y1 = new Complex( 20, 0 );
    Complex y2 = y1.HardyZTanhReduced2ndDerivative( z1 );
    
    //out.println( "y2=" + y2 );
    assertEquals( -0.088275374826177938227, y2.getReal().sixtyFourBitValue(), pow( 10, -10 ) );
  }

  
  public void testArg()
  {
    Complex z1 = new Complex(  new Real( "0.5" ), new Real( "14.134725142") ).Zeta();
    Complex z2 = new Complex(  new Real( "0.5" ), new Real( "14.134725000") ).Zeta();
    Real a1 = z1.arg();
    Real a2 = z2.arg();
    Real a = a1.add( a2 ).divide( 2 );
    out.println( "arg=" + a1 + " a2=" + a2 + " a=" + a );
  }
  
  public void testZetaZeroArg()
  {
    Real t = new Complex( new Real( "14.132" ) , RealConstants.ZERO ).nthRiemannZeroArg( 1 );
    out.println( "HMMMMMMM " + t );
  }
  
  public void testHardyZSelfRelaxedNewton()
  {
    // Complex y1 = new Complex( new Real(
    // "4589.3006433247738586039288518167696115070023361981" ), new Real( 0 ) );
    Complex y1 = new Complex().approximationZero( 4087 );
    //out.println( "y1=" + y1 );
    Complex y2 = y1.HardyZAutoRelaxedTanhNewton();
    //out.println( "y2=" + y2 );
    assertEquals( 4589.4207764289976190, y2.getReal().sixtyFourBitValue(), pow( 10, -11 ) );
  }

  public void testHardyZReducedNewtonIteration()
  {
    Complex y1 = new Complex(Fastmath.instance.nthApproximationZeroD( 1 ), 0 );
    y1 = Functions.iterate( x -> x.HardyZNewton(), y1, 12 );
    Complex y2start = new Complex( Functions.approximationZero( 2 ), new Real() );
    Complex[] roots = new Complex[]
    {
      y1
    };

    Complex t = y2start;
    println( "y1=" + y1 );
    println( "y2start=" + y2start );
    Complex lastt = new Complex();
    final Complex[] roots1 = roots;

    Complex y2 = Functions.iterate( x -> x.HardyZReducedNewton( roots1 ), y2start, 40 );
    println( "refining " + y2 );
    y2 = Functions.iterate( x -> x.HardyZNewton(), y2, 10 );
    roots = new Complex[]
    {
      y1,
      y2
    };
    Complex y3start = new Complex( Functions.approximationZero( 3 ), new Real() );
    println( "y2=" + y2 );
    println( "y3start=" + y3start );
    final Complex[] roots2 = roots;
    Complex y3 = Functions.iterate( x -> x.HardyZReducedNewton( roots2 ), y3start, 100 );
    roots = new Complex[]
    {
      y1,
      y2,
      y3
    };
    println( "y3=" + y3 );
    for ( Complex root : roots )
    {
      println( "root " + root );
    }

  }

  public void testToString()
  {
    final Real re = new Real( 3.7 );
    out.println( re );
    final Real im = new Real( 0.2 );
    out.println( im );

    Complex x = new Complex( re, im );
    out.println( x );
  }

  public void testTanh()
  {
    final Complex x = new Complex( 1, 2 );
    final Complex ht = x.tanh();
    assertEquals( 1.166736257, ht.getReal().sixtyFourBitValue(), pow( 10, -9 ) );
    assertEquals( -0.2434582012, ht.getImaginary().sixtyFourBitValue(), pow( 10, -10 ) );
  }

  public void testGeneralizedSteffensen()
  {
    final Complex x = new Complex( Functions.approximationZero( 1 ), ZERO );
    Complex ht = x.HTZGeneralizedSteffensen();

    assertEquals( 14.0821124395352, ht.getReal().sixtyFourBitValue(), pow( 10, -9 ) );
    assertEquals( 0, ht.getImaginary().sixtyFourBitValue(), pow( 10, -10 ) );
  }

  public void testZHalley()
  {
    final Complex x = new Complex( Functions.approximationZero( 1 ), ZERO );
    Complex ht = x.ZHalley();

    assertEquals( 14.136595598593265, ht.getReal().sixtyFourBitValue(), pow( 10, -9 ) );
    assertEquals( 0, ht.getImaginary().sixtyFourBitValue(), pow( 10, -10 ) );
  }

  public void testGeneralizedRelaxedSteffensen()
  {
    Complex x = new Complex( Functions.approximationZero( 1 ), ZERO );
    Complex ht = x.HTZGeneralizedSteffensen( RealConstants.ONE );

    assertEquals( 14.0821124395352, ht.getReal().sixtyFourBitValue(), pow( 10, -9 ) );
    assertEquals( 0, ht.getImaginary().sixtyFourBitValue(), pow( 10, -10 ) );

    x = new Complex( Functions.approximationZero( 1 ), ZERO );
    ht = x.HTZGeneralizedSteffensen( new Real( 0.5 ) );

    assertEquals( 14.1024382345786, ht.getReal().sixtyFourBitValue(), pow( 10, -9 ) );
    assertEquals( 0, ht.getImaginary().sixtyFourBitValue(), pow( 10, -10 ) );

  }

  public void testHardyZReduced()
  {
    Complex t = new Complex( 17, 0 );
    Complex y = t.HardyZReduced( new Complex( new Real( "14.134725141734693790457251983562470270784257115699243175685567460149" ), new Real( 0 ) ) );
    assertEquals( 2.1566649204030922888, y.getReal().sixtyFourBitValue(), pow( 10.0, -10 ) );
  }

  public void testHardyZDerivativeReduced()
  {
    Complex t = new Complex( 17, 0 );
    Complex y = t.HardyZDerivativeReduced( new Complex( new Real( "14.134725141734693790457251983562470270784257115699243175685567460149" ), new Real( 0 ) ) );
    assertEquals( .40493431114146096739, y.getReal().sixtyFourBitValue(), pow( 10.0, -10 ) );
  }
  
  public void testHardyZ2ndDerivativeReduced()
  {
    Complex t = new Complex( 17, 0 );
    Complex y = t.HardyZ2ndDerivativeReduced( new Complex( new Real( "14.134725141734693790457251983562470270784257115699243175685567460149" ), new Real( 0 ) ) );
    assertEquals( -.37610749273066741049, y.getReal().sixtyFourBitValue(), pow( 10.0, -10 ) );
  }
  
  public void testHardyZSelfRelaxedReducedNewtonIteration()
  {
    Complex t = new Complex( 20, 0 );
    Complex y1 = new Complex( new Real( "14.134725141734693790457251983562470270784257115699243175685567460149" ), new Real( 0 ) );
    Complex y = t.HardyZAutoRelaxedReducedNewton( y1 );
    y = y.HardyZAutoRelaxedReducedNewton( y1 );
    y = y.HardyZAutoRelaxedReducedNewton( y1 );
    y = y.HardyZAutoRelaxedReducedNewton( y1 );
    assertEquals( 21.0220396387715549926284, y.getReal().sixtyFourBitValue(), pow( 10.0, -10 ) );
    
  }
}
