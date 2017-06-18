package stochastic.probability;

import math.space.EuclideanSpace;
import stochastic.SigmaField;

/**
 * TODO
 * 
 * @param <P>
 *          a {@link ProbabilityMeasure} on a {@link EuclideanSpace}
 */
public abstract class EuclideanProbabilitySpace<P extends ProbabilityMeasure<EuclideanSpace, SigmaField<EuclideanSpace>>>
                                               implements
                                               ProbabilitySpace<EuclideanSpace, SigmaField<EuclideanSpace>, P>
{

}
