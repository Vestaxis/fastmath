package fastmath.arb;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class MPFR extends Structure
{
  public long _mpfr_prec;
  
  public int _mpfr_sign;
  
  public long _mpfr_exp;
  
  public Pointer _mpfr_d;
  
  /**
   * typedef struct { <br>
   * mpfr_prec_t _mpfr_prec; <br>
   * mpfr_sign_t _mpfr_sign; <br>
   * mpfr_exp_t _mpfr_exp; <br>
   * mp_limb_t *_mpfr_d; <br>
   * } __mpfr_struct; <br>
   */
  @Override
  protected List<String> getFieldOrder()
  {
    return Arrays.asList( new String[]
    {
      "_mpfr_prec",
      "_mpfr_sign",
      "_mpfr_exp",
      "_mpfr_d"
    } );
  }

}
