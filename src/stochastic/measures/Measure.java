package stochastic.measures;

import math.Product;
import math.Set;
import math.functions.Function;
import numbersystems.RealNumbers;
import stochastic.order.OrderedSet;

/**
 * 
 * @author crow
 *
 */
public interface Measure<X extends Set> extends OrderedSet, Function<Product<X, X>, RealNumbers>
{

}
