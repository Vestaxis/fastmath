package stochastic.processes.pointprocesses;

import math.Set;
import stochastic.SigmaField;
import stochastic.probability.ProbabilityDistributionFunction;

/**
 * 
 * TODO: dust this off
 * 
 * @see Karr, A. Point processes and their statistical inference 1991 Vol. 7
 *      #1.7
 * 
 * @author crow
 *
 * @param <Ω>
 * @param <ℱ>
 * @param <P>
 */
public interface PalmDistribution<Ω extends Set, ℱ extends SigmaField<? extends Ω>> extends ProbabilityDistributionFunction<Ω, ℱ>
{

}
