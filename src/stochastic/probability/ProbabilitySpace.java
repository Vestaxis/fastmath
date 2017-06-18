package stochastic.probability;

import math.Set;
import math.space.CompleteSeparableMetricSpace;
import stochastic.SigmaField;
import stochastic.measures.MeasurableSpace;

/**
 *
 * 
 * @param <Ω>
 *          the sample space - what happened/happens
 * @param <ℱ>
 *          the event space - what can/does happen
 * @param <P>
 *          the probability measure of events in the sample space
 * 
 * @see https://oz.canaccord.com/trac/wiki/ProbabilitySpace
 */
public interface ProbabilitySpace<Ω extends Set, ℱ extends SigmaField<Ω>, P extends ProbabilityMeasure<Ω, ℱ>>
                                 extends
                                 MeasurableSpace<Ω, ℱ>,
                                 CompleteSeparableMetricSpace<Ω, ℱ>
{

}
