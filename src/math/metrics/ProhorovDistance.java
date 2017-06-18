package math.metrics;

import math.Product;
import math.Set;
import numbersystems.RealNumbers;

/**
 * ρ(N_1,N_2)=inf{ε>0:P{N_1∈Γ}-P{N_2∈Γ^ε}+ε,Γ∈ℳ_p}
 * 
 * @see karr p.27
 * 
 * @author whatsittoya
 *
 */
public class ProhorovDistance<X extends Set> implements Metric<X>
{

  @Override
  public RealNumbers apply( Product<X, X> t )
  {
    // TODO Auto-generated method stub
    return null;
  }

}
