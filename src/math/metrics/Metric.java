package math.metrics;

import math.Set;
import stochastic.measures.Measure;

/**
 * metric, n. 1. a non-negative symmetric binary function defined for a given
 * set, often denoted d(x, y), δ(x, y) or r(x, y) and referred to as distance,
 * that satisfies the <i>triangle inequality</i>
 *
 * d(x, z) <= d(x, y) + d(y, z)
 * 
 * and is equal to zero only if x = y. For example, the ordinary distance
 * function in the plane is a metric, since |AB| = |BA|, the triangle inequality
 * follows from Pythagoras' theorem, and |AB| = 0 only if A and B coincide
 *
 * @auth crow
 */
public interface Metric<X extends Set> extends Measure<X>
{
}
