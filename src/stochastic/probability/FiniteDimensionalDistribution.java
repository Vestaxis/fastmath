package stochastic.probability;

import math.Set;
import math.space.FiniteDimensionalSpace;
import stochastic.SigmaField;

/**
 * TODO
 * 
 * @author sc
 *
 * @param <Ω>
 * @param <ℱ>
 */
public interface FiniteDimensionalDistribution<Ω extends Set, ℱ extends SigmaField<? extends Ω>>
                                              extends
                                              ProbabilityDistributionFunction<Ω, ℱ>,
                                              FiniteDimensionalSpace
{

}
