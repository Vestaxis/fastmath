package math.metrics;

import math.Product;
import math.Set;
import numbersystems.RealNumbers;

/**
 * supremum norm, or uniform norm, n. the norm placed on continuous or bounded
 * functions on a set S that assigns to each function the supremum of the moduli
 * of the values of the function on the set: <br>
 * <code>
         ||f||Â¥ = sup {|f(x)| : x Ã S}.
   </code><br>
 * So endowed, the real or complex continuous functions on a compact set S form
 * a Banach space that is denoted C(S).
 * 
 * @param <X>
 */
public class ChebyshevNorm<X extends Set> implements Metric<X>
{

  @Override
  public RealNumbers apply( Product<X, X> t )
  {
    // TODO Auto-generated method stub
    return null;
  }

}
