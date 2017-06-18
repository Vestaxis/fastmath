package stochastic;

import math.Set;
import math.Topology;
import math.theory.ring.Ring;
import stochastic.measures.MeasurableSpace;

/**
 * a σ-algebra (also sigma-algebra, σ-field, sigma-field) on a set X is a
 * collection of subsets of X that is closed under countably many set operations
 * (complement, union and intersection). On the other hand, an algebra is only
 * required to be closed under finitely many set operations. <b>That is, a
 * σ-algebra is an algebra of sets, completed to include countably infinite
 * operations.<b> The pair (X, Σ) is also a field of sets, called a
 * {@link MeasurableSpace}
 * 
 * A useful example is the set of subsets of the real line formed by starting
 * with all open intervals and adding in all countable unions, countable
 * intersections, and relative complements and continuing this process (by
 * transfinite iteration through all countable ordinals) until the relevant
 * closure properties are achieved (a construction known as the Borel
 * hierarchy).
 * 
 * σ-algebra is synonymous with σ-ring
 * 
 */
public interface SigmaField<X extends Set> extends Topology<X>, LambdaSystem<X>, ProductSystem<X>, Ring<X>
{

}
