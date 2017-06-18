package stochastic.measures;

import math.geometry.differential.TopologicalSpace;
import stochastic.SigmaField;

/**
 * Given a set X, a set function mu^*:2^X->[0,infty] is said to be an outer
 * measure provided that mu^*(emptyset)=0 and that mu^* is countably monotone,
 * where emptyset is the empty set.
 * 
 * Given a collection S of subsets of X and an arbitrary set function
 * mu:S->[0infty], one can define a new set function mu^* by setting
 * mu^*(emptyset)=0 and defining, for each non-empty subset E subset X,
 * mu^*(E)=infsum_(k=1)^inftymu(E_k)
 * 
 * where the infimum is taken over all countable collections {E_k}_(k=1)^infty
 * of sets in S which cover E. The resulting function mu^*:2^X->[0infty] is an
 * outer measure and is called the outer measure induced by mu.
 * 
 * @author crow
 *
 */
public interface OuterRegularMeasure<X extends TopologicalSpace, Σ extends SigmaField<X>> extends Measure<Σ>
{

}
