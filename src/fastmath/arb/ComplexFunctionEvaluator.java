package fastmath.arb;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

/**
 * int func(acb_ptr out, const acb_t inp, void * param, slong order, slong prec)
 * 
 *
 */
public class ComplexFunctionEvaluator implements Callback
{
  private ComplexFunction function;

  public ComplexFunctionEvaluator(ComplexFunction function)
  {
    this.function = function;
  }

  public int value( ComplexReference out, Complex inp, Pointer param, long order, long prec )
  {
    out.assign( function.apply( inp ) );
    return 0;
  }

}
