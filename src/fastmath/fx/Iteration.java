package fastmath.fx;

import static fastmath.arb.Constants.HALF;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.System.out;

import java.util.ArrayList;
import java.util.List;

import fastmath.Functions;
import fastmath.arb.Complex;
import fastmath.arb.Constants;
import fastmath.arb.Real;
import fastmath.arb.RealConstants;

public enum Iteration
{
 ZNewton,
 ZHTNewton,
 ZHTNewtonSelfRelax,
 ZHTNewtonAutoSelfRelax,
 ZSteffensen,
 ZSteffensenTanh,
 ZHTOdd,
 ZHTEven;

  public Complex value( Complex x, double h, Complex y, List<Real> zeros, int n )
  {
    // Complex norm = x.pow( RealConstants.QUARTER );

    // Complex curvature = x.HardyZ2ndDerivative().tanh();
    // out.println( "curvature " + curvature );
    // if ( curvature.sixtyFourBitValue() > 1 )
    // {
    // throw new IllegalArgumentException( "SD " + curvature );
    // }
    // Real norm = x.HardyZDerivative().tanh().abs();

    Complex hz = zeros == null ? x.HardyZTanh() : new Complex( x.HardyZReducedTanh( zeros ), RealConstants.ZERO );
    // Complex hz = x.HardyZ();

    // Complex hz = y.getReal().sixtyFourBitValue() ==
    // y.getImaginary().sixtyFourBitValue() ? x.HardyZ() : x.HardyZTanhReduced(
    // y );
    // out.println( "hz=" + hz + " at " + x.getReal().sixtyFourBitValue() + "
    // lastZero=" + y );
    switch( this )
    {
    case ZHTNewtonSelfRelax:
      return x.HardyZAutoRelaxedTanhNewton();
    case ZHTNewtonAutoSelfRelax:
      return x.HardyZTanhAutoRelaxedReducedNewton( y );
    // return x.HardyZAutoRelaxedReducedNewton( y );
    case ZNewton:
      return x.HardyZRelaxedNewton( new Real( h ) );
    case ZHTNewton:
      return x.HardyZRelaxedTanhNewton( new Real( h ) );
    // case RiemannSiegelNewton:
    // //Functions.ϑNewton(x, z, h );
    // return;
    case ZSteffensen:
      return x.ZSteffensen( h );
    case ZSteffensenTanh:
      return x.ZSteffensenTanh( h );
    case ZHTOdd:
      return x.subtract( ZHT( x, h, hz ) );
    case ZHTEven:
      return x.add( ZHT( x, h, hz ) );
    // return x.add( hz.divide( norm ).tanh().multiply( h ) );

    // return x.add( x.HardyZ().divide( x.sqrt() ).tanh() );
    default:
      throw new IllegalArgumentException( "unsupported iteration function " + this );
    }
  }

  public Complex ZHT( Complex x, double h, Complex hz )
  {
    return hz.divide( x.getReal().Omega() ).multiply( h );
  }

  public void value( double initx, double inity, double z[], double h )
  {
    // public Complex value( Complex x, double h, Complex y, double start,
    // List<Real> zeros, int n )
    Complex x = new Complex( initx, inity );
    Complex y = value( x, h, null, null, -1 );
    z[0] = y.getReal().sixtyFourBitValue();
    z[1] = y.getImaginary().sixtyFourBitValue();
  }

  public int convergeToLimit( double initx, double inity, double[] z )
  {
    switch( this )
    {

    case ZHTNewton:
      return Functions.convergeToZHTNewtonLimit( initx, inity, z, 1 );
    default:
      return Functions.iterate( this, 100, initx, inity, z, 1 );
    }
  }
}