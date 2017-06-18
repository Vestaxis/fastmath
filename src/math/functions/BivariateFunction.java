package math.functions;

import math.Set;
import stochastic.order.OrderedPair;

/**
 * A function of two variables. f(a,b)=r
 * 
 * @param <A>
 *          domain of a in f(a,b)
 * @param <B>
 *          domain of b in f(a,b)
 * @param <R>
 *          range of f(a,b)
 */
public interface BivariateFunction<A extends Set, B extends Set, R extends Set> extends Function<OrderedPair<A, B>, R>
{

}
