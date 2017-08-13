package math.functions;

import org.apache.commons.math3.analysis.BivariateFunction;

import math.Set;

/**
 * TODO: bilinear, adj. of or relating to a function of two variables that is
 * linear with respect to each variable independently: f(a x + b y, c z + d w) =
 * a c f(x, z) + b c f(y, z) + a d f(x, w)
 * 
 * + b d f(y, w) for all scalars a, b, c, d and vectors x, y, z, w. The domain
 * of f is the Cartesian product of two vector spaces over the same field, and
 * the range of f is contained in a vector space, again of the same field.
 * 
 * @param <A>
 * @param <B>
 * @param <R>
 */
public interface BilinearFunction<A extends Set, B extends Set, R extends Set> extends BivariateFunction
{

}
