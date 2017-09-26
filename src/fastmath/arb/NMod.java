package fastmath.arb;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.Structure.ByValue;

/**
 * 
 * <code>
 * 
 * typedef unsigned long int  mp_limb_t;
 * #define ulong mp_bitcnt_t
 * typedef struct
 * {
 *    mp_limb_t n;
 *    mp_limb_t ninv;
 *    mp_bitcnt_t norm;
 * }  nmod_t; 
 *</code>
 */
public class NMod extends Structure implements ByValue
{
  public long n;

  public long ninv;

  public long norm;

  private static final List<String> fieldOrder = Arrays.asList( new String[]
  {
    "n",
    "ninv",
    "norm"
  } );

  public NMod()
  {

  }

  public NMod(int q)
  {
    ArbLibrary.instance.nmod_init( this, q );
  }

  @Override
  protected List<String> getFieldOrder()
  {
    return fieldOrder;
  }

}
