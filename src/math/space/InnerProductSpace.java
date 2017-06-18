package math.space;

import math.Field;
import math.InnerProduct;

/**
 * An inner product space is a vector space together with an inner product on
 * it. If the inner product defines a complete metric, then the inner product
 * space is called a Hilbert space.
 * 
 * @author crow
 *
 */
public interface InnerProductSpace<F extends Field<?>> extends VectorSpace<F>
{
  public InnerProduct getInnerProduct();
}
