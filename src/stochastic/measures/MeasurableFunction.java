package stochastic.measures;

import math.Set;
import numbersystems.PositiveRealNumbers;
import stochastic.SigmaField;

/**
 * A measurable function is a measurable mapping to the real line and its
 * (Borel) sigma algebra
 * 
 * @param <X>
 */
public interface MeasurableFunction<X extends Set, Σ extends SigmaField<? extends X>>
                                   extends
                                   MeasurableMapping<MeasurableSpace<? extends X, Σ>, PositiveRealNumbers>
{

}
