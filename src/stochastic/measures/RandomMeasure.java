package stochastic.measures;

import stochastic.probability.ProbabilitySpace;

public interface RandomMeasure<P extends ProbabilitySpace<?, ?, ?>> extends MeasurableMapping<P, RadonMeasure<? extends P>>
{

}
