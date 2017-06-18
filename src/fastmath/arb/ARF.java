package fastmath.arb;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Structure;

import fastmath.arb.ArbLibrary.MPFRRoundingMode;

/**
 * typedef struct { fmpz exp; mp_size_t size; mantissa_struct d; } arf_struct;
 */
public class ARF extends Structure
{

  public long exp;

  public long size;

  public Mantissa d;

  public ARF(double e)
  {
    setValue( e );
  }

  public ARF()
  {
    // TODO Auto-generated constructor stub
  }

  @Override
  protected List<String> getFieldOrder()
  {
    return Arrays.asList( new String[]
    {
      "exp",
      "size",
      "d"
    } );
  }

  public double getValue()
  {
    return ArbLibrary.instance.arf_get_d( this, RoundingMode.Near.ordinal() );
  }

  public void setValue( double v )
  {
    ArbLibrary.instance.arf_set_d( this, v );
  }

  enum RoundingMode
  {
   Down,
   Up,
   Floor,
   Ceil,
   Near
  };

  public ARF add( ARF x )
  {
    ARF result = new ARF();
    ArbLibrary.instance.arf_add( result, this, x, Real.bits, RoundingMode.Near.ordinal() );
    return result;
  }

  public ARF divide( int i )
  {
    ARF result = new ARF();
    ArbLibrary.instance.arf_div_si( result, this, i, Real.bits, RoundingMode.Near.ordinal() );
    return result;
  }

  public ARF divide( ARF i )
  {
    ARF result = new ARF();
    ArbLibrary.instance.arf_div( result, this, i, Real.bits, RoundingMode.Near.ordinal() );
    return result;
  }

}
