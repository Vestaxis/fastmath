package fastmath.arb;

import static fastmath.Console.println;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Structure;

/**
 * 
 * http://arblib.org/arb.html
 *
 */
public class Real extends Structure implements Comparable<Real>
{

  public static int bits = 256;

  public boolean overlaps( Real o )
  {
    if ( !( o instanceof Real ) )
    {
      return false;
    }
    Real x = (Real) o;
    return ArbLibrary.instance.arb_overlaps( this, x ) != 0;
  }

  public boolean contains( Real o )
  {
    if ( !( o instanceof Real ) )
    {
      return false;
    }
    Real x = (Real) o;
    return ArbLibrary.instance.arb_contains( this, x ) != 0;
  }

  @Override
  public boolean equals( Object o )
  {
    if ( !( o instanceof Real ) )
    {
      return false;
    }
    Real x = (Real) o;
    return ArbLibrary.instance.arb_equal( this, x ) != 0;
  }



  @Override
  public String toString()
  {
    return ArbLibrary.instance.arb_get_str( this, bits, 0 );
  }

  public Real()
  {
  }

  public Real(double d)
  {
    ArbLibrary.instance.arb_set_d( this, d );
  }

  public Real(String str)
  {
    ArbLibrary.instance.arb_set_str( this, str, bits );
  }

  public Real(ARF x)
  {
    ArbLibrary.instance.arb_set_arf( this, x );
  }

  public boolean containsZero()
  {
    return ArbLibrary.instance.arb_contains_zero( this ) != 0;
  }

  public Real W()
  {
    throw new UnsupportedOperationException();
    //Real res = new Real();
    //ArbLibrary.instance.arb_lambertw( res, this, 0, Real.bits );
    //return res;
  }

  public Real pi()
  {
    ArbLibrary.instance.arb_const_pi( this, bits );
    return this;
  }

  public Real floor()
  {
    Real r = new Real();
    ArbLibrary.instance.arb_floor( r, this, bits );
    return r;
  }

  public Real ceil()
  {
    Real r = new Real();
    ArbLibrary.instance.arb_ceil( r, this, bits );
    return r;
  }

  /**
   * @return -this
   */
  public Real negate()
  {
    ArbLibrary.instance.arb_neg( this, this );
    return this;
  }

  /**
   * the effective relative accuracy of x measured in bits, equal to the
   * negative of the return value from arb_rel_error_bits()
   * 
   * @return
   */
  public long getRelativeAccuracyBits()
  {
    return ArbLibrary.instance.arb_rel_accuracy_bits( this );
  }

  public Real sign()
  {
    Real y = new Real();
    ArbLibrary.instance.arb_sgn( y, this );
    return y;
  }

  public Real tanh()
  {
    Real result = new Real();
    ArbLibrary.instance.arb_tanh( result, this, bits );
    return result;
  }

  public Real subtract( Real subtrahend )
  {
    Real result = new Real();
    ArbLibrary.instance.arb_sub( result, this, subtrahend, bits );
    return result;
  }

  public Real add( Real a )
  {
    Real result = new Real();
    ArbLibrary.instance.arb_add( result, this, a, bits );
    return result;
  }

  public Real divide( int i )
  {
    Real result = new Real();
    ArbLibrary.instance.arb_div_si( result, this, i, bits );
    return result;
  }

  public Real log()
  {
    Real result = new Real();
    ArbLibrary.instance.arb_log( result, this, bits );
    return result;
  }

  public Real sqrt()
  {
    Real result = new Real();
    ArbLibrary.instance.arb_sqrt( result, this, bits );
    return result;
  }

  public Real Omega()
  {
    Real logx = log();
    return logx.divide( logx.log() ).sqrt().multiply( 3 ).divide( 4 ).exp();
  }

  public Real divide( Real divisor )
  {
    Real result = new Real();
    ArbLibrary.instance.arb_div( result, this, divisor, bits );
    return result;
  }

  public Real multiply( long a )
  {
    Real result = new Real();
    ArbLibrary.instance.arb_mul_si( result, this, a, bits );
    return result;
  }

  public Real multiply( Real a )
  {
    Real result = new Real();
    ArbLibrary.instance.arb_mul( result, this, a, bits );
    return result;
  }

  public Complex multiply( Complex a )
  {
    Complex result = new Complex();
    ArbLibrary.instance.acb_mul_arb( result, a, this, bits );
    return result;
  }

  public Real abs()
  {
    Real y = new Real();
    ArbLibrary.instance.arb_abs( y, this );
    return y;
  }

  public static void main( String args[] )
  {
    println( "sizeof Real is " + Native.getNativeSize( Real.class ) );
    println( "sizeof Complex is " + Native.getNativeSize( Complex.class ) );
    // Real x = new Real( 31.337 );
    // println( x.toString() );
    //
    // Real y = new Real( "31.337" );
    // println( y.toString() );
  }

  public double sixtyFourBitValue()
  {
    return mid.getValue();
  }

  public ARF getUpperBound()
  {
    ARF ub = new ARF();
    ArbLibrary.instance.arb_get_ubound_arf( ub, this, bits );
    return ub;
  }

  public ARF getLowerBound()
  {
    ARF lb = new ARF();
    ArbLibrary.instance.arb_get_lbound_arf( lb, this, bits );
    return lb;
  }

  private static final List<String> fields = Arrays.asList( new String[]
  {
    "mid",
    "rad"
  } );

  public ARF mid;// = new ARF();

  public MagStruct rad; // = new MagStruct();

  public ARF getMidpoint()
  {
    return mid;
  }

  @Override
  protected List<String> getFieldOrder()
  {
    return fields;
  }

  @Override
  public int compareTo( Real o )
  {
    return lessThan( o ) ? -1 : greaterThan( o ) ? 1 : 0;
  }

  public boolean lessThan( Real y )
  {
    return ArbLibrary.instance.arb_lt( this, y ) > 0;
  }

  public boolean greaterThan( Real y )
  {
    return ArbLibrary.instance.arb_gt( this, y ) > 0;
  }

  public boolean greaterThanOrEquals( Real y )
  {
    return greaterThan( y ) || equals( y );
  }

  public boolean lessThanOrEquals( Real y )
  {
    return lessThan( y ) || equals( y );
  }

  public Real exp()
  {
    Real result = new Real();
    ArbLibrary.instance.arb_exp( result, this, bits );
    return result;
  }

  public Complex subtract( Complex b )
  {
    Complex thisc = new Complex( this, RealConstants.ZERO );
    Complex result = new Complex();
    ArbLibrary.instance.acb_sub( result, thisc, b, bits );
    return result;
  }

}
