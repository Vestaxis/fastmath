package stochastic.probability;

import math.Set;
import stochastic.measures.MeasurableSpace;

/**
 * note that random variables are defined without reference to probability
 * measures
 * 
 * @author crow
 *
 * @param <X>
 * @param <Y>
 */
public interface RandomVariable<P extends ProbabilitySpace<?, ?, ?>, X extends MeasurableSpace<?, ?>, Y extends MeasurableSpace<?, ?>> extends Set
{
  /**
   * @see schjb#1.9
   * 
   * @return the distribution of the random variable
   */
  public <D extends ProbabilityDistributionFunction<?, ?>> D getDistribution();

  /**
   * TODO: expectation, which does not necessarily exist
   */
}
