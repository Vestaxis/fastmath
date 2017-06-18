package stochastic.probability;

import math.Set;
import stochastic.SigmaField;
import stochastic.measures.Measure;

/**
 * A real non-negative function P on a class 𝒜 of subsets (events) of a
 * non-empty set Ω(the space of elementary events) forming a σ-field (i.e. a set
 * closed with respect to countable set-theoretic operations) such that P(Ω)=1
 * and P(cup_(i=1)^∞A_i)=sum_(i=1)^∞P(A_i) for A_i∪A_j=∅∀i≠j (σ-additivity)
 * 
 * @author sc
 *
 * @param <X>
 * @param <F>
 */
public interface ProbabilityMeasure<X extends Set, F extends SigmaField<? extends X>> extends Measure<F>
{

}
