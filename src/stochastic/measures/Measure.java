package stochastic.measures;

import java.util.function.Function;

import math.Product;
import math.Set;
import stochastic.order.OrderedSet;

public interface Measure<X extends Set> extends OrderedSet, Function<Product<X, X>, Double>
{

}
